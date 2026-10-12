package com.hzsca.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzsca.config.AlipayProperties;
import com.hzsca.entity.PaymentOrder;
import com.hzsca.mapper.PaymentOrderMapper;
import com.hzsca.service.PaymentService;
import com.hzsca.utils.AlipaySignUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 支付服务实现。
 *
 * <p>当 {@link AlipayProperties#isConfigured()} 为 false 时自动降级为 <b>Mock 演示模式</b>：
 * 下单只创建本地订单，前端可调用 mock 支付接口直接把订单置为已支付，从而完整演示
 * 「下单 → 支付 → 状态回写 → 结果页」链路。</p>
 *
 * <p>真实模式下通过 JDK 内置 RSA2 签名调用支付宝沙箱的
 * {@code alipay.trade.page.pay}（收银台跳转）与 {@code alipay.trade.query}（主动查单），
 * 并对 {@code notify_url} 异步通知做验签，全程不依赖第三方 SDK。</p>
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    /** 订单状态常量 */
    private static final String ST_CREATED = "CREATED";
    private static final String ST_PAYING = "PAYING";
    private static final String ST_PAID = "PAID";
    private static final String ST_CLOSED = "CLOSED";
    private static final String ST_FAILED = "FAILED";

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PaymentOrderMapper orderMapper;
    private final AlipayProperties props;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public PaymentServiceImpl(PaymentOrderMapper orderMapper, AlipayProperties props, ObjectMapper objectMapper) {
        this.orderMapper = orderMapper;
        this.props = props;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().build();
    }

    @Override
    public boolean isMockMode() {
        return !props.isConfigured();
    }

    @Override
    public OrderView createOrder(String subjectType, Long subjectId, long amountFen, String purpose, String remark) {
        if (amountFen < props.getMinAmount() || amountFen > props.getMaxAmount()) {
            throw new IllegalArgumentException("捐助金额需在 " + (props.getMinAmount() / 100) + " ~ "
                    + (props.getMaxAmount() / 100) + " 元之间");
        }
        if (subjectId == null || subjectType == null) {
            throw new IllegalStateException("无法识别当前登录用户");
        }

        String outTradeNo = generateOutTradeNo();
        PaymentOrder order = new PaymentOrder();
        order.setOutTradeNo(outTradeNo);
        order.setSubject("HZSCA 爱心捐助");
        order.setAmount(amountFen);
        order.setCurrency("CNY");
        order.setChannel(isMockMode() ? "MOCK" : "ALIPAY");
        order.setPurpose(purpose == null || purpose.isBlank() ? "donate" : purpose);
        order.setStatus(ST_CREATED);
        order.setSubjectType(subjectType);
        order.setSubjectId(subjectId);
        order.setRemark(remark);
        orderMapper.insert(order);

        if (isMockMode()) {
            logger.info("创建捐助订单(Mock 模式) outTradeNo={}, amount={}分", outTradeNo, amountFen);
            return new OrderView(outTradeNo, order.getSubject(), amountFen, ST_CREATED, null, null, true);
        }

        try {
            String payUrl = buildPagePayUrl(order);
            orderMapper.updatePayInfo(outTradeNo, payUrl, null);
            logger.info("创建捐助订单(支付宝沙箱) outTradeNo={}, amount={}分", outTradeNo, amountFen);
            return new OrderView(outTradeNo, order.getSubject(), amountFen, ST_PAYING, null, payUrl, false);
        } catch (Exception e) {
            logger.error("构造支付宝收银台失败 outTradeNo={}", outTradeNo, e);
            orderMapper.updateStatusIfCurrent(outTradeNo, ST_FAILED, List.of(ST_CREATED), null, null, null);
            throw new IllegalStateException("构造支付宝支付请求失败：" + e.getClass().getSimpleName());
        }
    }

    @Override
    public PaymentOrder getOwnOrder(String subjectType, Long subjectId, String outTradeNo) {
        PaymentOrder order = orderMapper.selectByOutTradeNo(outTradeNo);
        if (order == null) {
            return null;
        }
        // 越权校验：订单必须归属于当前登录主体
        if (!order.getSubjectType().equals(subjectType) || !order.getSubjectId().equals(subjectId)) {
            return null;
        }
        return order;
    }

    @Override
    public PaymentOrder syncOrder(PaymentOrder order) {
        if (order == null || isMockMode() || ST_PAID.equals(order.getStatus())) {
            return order;
        }
        try {
            Map<String, String> biz = new LinkedHashMap<>();
            biz.put("out_trade_no", order.getOutTradeNo());

            Map<String, String> params = baseParams("alipay.trade.query");
            params.put("biz_content", objectMapper.writeValueAsString(biz));
            params.put("sign", AlipaySignUtils.sign(AlipaySignUtils.buildSignContent(params), props.getMerchantPrivateKey()));

            String body = restClient.post()
                    .uri(props.getGateway())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(toForm(params))
                    .retrieve()
                    .body(String.class);

            JsonNode resp = objectMapper.readTree(body).path("alipay_trade_query_response");
            String code = resp.path("code").asText("");
            if (!"10000".equals(code)) {
                logger.warn("支付宝查单未成功 outTradeNo={}, code={}, subMsg={}",
                        order.getOutTradeNo(), code, resp.path("sub_msg").asText(""));
                return order;
            }
            String tradeStatus = resp.path("trade_status").asText("");
            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                markPaid(order.getOutTradeNo(), resp.path("trade_no").asText(null),
                        resp.path("buyer_user_id").asText(null), "{\"source\":\"sync\"}");
                return orderMapper.selectByOutTradeNo(order.getOutTradeNo());
            }
        } catch (Exception e) {
            logger.warn("主动查单失败 outTradeNo={}, error={}", order.getOutTradeNo(), e.getClass().getSimpleName());
        }
        return order;
    }

    @Override
    public PaymentOrder mockPay(PaymentOrder order) {
        if (!isMockMode() || order == null) {
            throw new IllegalStateException("当前非 Mock 模式，无法使用模拟支付");
        }
        markPaid(order.getOutTradeNo(), "MOCK" + order.getOutTradeNo(), "mock-buyer", "{\"source\":\"mock\"}");
        return orderMapper.selectByOutTradeNo(order.getOutTradeNo());
    }

    @Override
    public String handleNotify(Map<String, String> params) {
        try {
            if (params == null || params.isEmpty()) {
                return "failure";
            }
            String sign = params.get("sign");
            if (sign == null || sign.isBlank()) {
                logger.warn("支付宝通知缺少 sign");
                return "failure";
            }
            // 验签：用支付宝公钥验证，防止伪造通知
            boolean ok = AlipaySignUtils.verify(
                    AlipaySignUtils.buildSignContent(params), sign, props.getAlipayPublicKey());
            if (!ok) {
                logger.warn("支付宝通知验签失败 outTradeNo={}", params.get("out_trade_no"));
                return "failure";
            }

            String outTradeNo = params.get("out_trade_no");
            String tradeStatus = params.get("trade_status");
            PaymentOrder order = orderMapper.selectByOutTradeNo(outTradeNo);
            if (order == null) {
                logger.warn("支付宝通知对应的订单不存在 outTradeNo={}", outTradeNo);
                return "failure";
            }

            // 金额校验：防止金额被篡改
            String totalAmount = params.get("total_amount");
            if (totalAmount != null && !totalAmount.isBlank()) {
                long notifiedFen = Math.round(Double.parseDouble(totalAmount) * 100);
                if (notifiedFen != order.getAmount()) {
                    logger.warn("支付宝通知金额不一致 outTradeNo={}, order={}分, notify={}分",
                            outTradeNo, order.getAmount(), notifiedFen);
                    return "failure";
                }
            }

            if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                markPaid(outTradeNo, params.get("trade_no"), params.get("buyer_id"),
                        objectMapper.writeValueAsString(params));
            } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                orderMapper.updateStatusIfCurrent(outTradeNo, ST_CLOSED,
                        List.of(ST_CREATED, ST_PAYING), null, null, null);
            }
            return "success";
        } catch (Exception e) {
            logger.error("处理支付宝异步通知异常", e);
            return "failure";
        }
    }

    @Override
    public long totalDonatedFen() {
        try {
            return orderMapper.sumPaidAmount();
        } catch (Exception e) {
            return 0L;
        }
    }

    /** 幂等地把订单置为已支付（重复通知不会重复扣减/重复置位） */
    private void markPaid(String outTradeNo, String tradeNo, String buyerId, String payload) {
        int rows = orderMapper.updateStatusIfCurrent(outTradeNo, ST_PAID,
                List.of(ST_CREATED, ST_PAYING, ST_PAID), tradeNo, buyerId, payload);
        if (rows > 0) {
            logger.info("订单已支付 outTradeNo={}, tradeNo={}", outTradeNo, tradeNo);
        }
    }

    /** 构造 alipay.trade.page.pay 的 GET 收银台地址 */
    private String buildPagePayUrl(PaymentOrder order) throws Exception {
        Map<String, Object> biz = new LinkedHashMap<>();
        biz.put("out_trade_no", order.getOutTradeNo());
        // 支付宝金额单位为元，保留两位小数
        biz.put("total_amount", String.format("%.2f", order.getAmount() / 100.0));
        biz.put("subject", order.getSubject());
        biz.put("product_code", "FAST_INSTANT_TRADE_PAY");

        Map<String, String> params = baseParams("alipay.trade.page.pay");
        params.put("biz_content", objectMapper.writeValueAsString(biz));
        params.put("sign", AlipaySignUtils.sign(AlipaySignUtils.buildSignContent(params), props.getMerchantPrivateKey()));
        return props.getGateway() + "?" + toQueryString(params);
    }

    private Map<String, String> baseParams(String method) {
        Map<String, String> params = new TreeMap<>();
        params.put("app_id", props.getAppId());
        params.put("method", method);
        params.put("format", "JSON");
        params.put("charset", props.getCharset());
        params.put("sign_type", props.getSignType());
        params.put("timestamp", LocalDateTime.now().format(TS));
        params.put("version", "1.0");
        params.put("notify_url", props.getNotifyUrl());
        params.put("return_url", props.getReturnUrl());
        return params;
    }

    private String toQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            if (e.getValue() == null || e.getValue().isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(e.getKey()).append('=')
                    .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    private MultiValueMap<String, String> toForm(Map<String, String> params) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        params.forEach(form::add);
        return form;
    }

    private String generateOutTradeNo() {
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rand = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "HZSCA" + ts + rand;
    }
}
