package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.config.AlipayProperties;
import com.hzsca.entity.PaymentOrder;
import com.hzsca.security.SecurityUtils;
import com.hzsca.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付接口。
 *
 * <p>下单/查单/同步/模拟支付需要登录；支付宝的同步回跳与异步通知必须匿名可访问，
 * 已在 {@code SecurityConfig} 中放行。</p>
 */
@Tag(name = "支付管理", description = "爱心捐助下单、查单与支付宝回调")
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final AlipayProperties alipayProperties;

    public PaymentController(PaymentService paymentService, AlipayProperties alipayProperties) {
        this.paymentService = paymentService;
        this.alipayProperties = alipayProperties;
    }

    @Operation(summary = "支付配置", description = "返回当前是否 Mock 模式、金额上下限与累计捐助金额")
    @GetMapping("/config")
    public Result config() {
        Map<String, Object> data = new HashMap<>();
        data.put("mock", paymentService.isMockMode());
        data.put("minAmount", alipayProperties.getMinAmount());
        data.put("maxAmount", alipayProperties.getMaxAmount());
        data.put("totalDonated", paymentService.totalDonatedFen());
        return Result.success(data);
    }

    @Operation(summary = "创建捐助订单", description = "创建订单并返回收银台跳转地址；Mock 模式下 payUrl 为空")
    @PostMapping("/orders")
    public Result createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        if (userId == null || type == null) {
            return Result.error("401", "请先登录后再捐助");
        }
        if (request == null || request.amount == null) {
            return Result.error("400", "缺少捐助金额");
        }
        try {
            PaymentService.OrderView view = paymentService.createOrder(
                    type, userId, request.amount, request.purpose, request.remark);
            Map<String, Object> data = new HashMap<>();
            data.put("outTradeNo", view.outTradeNo());
            data.put("subject", view.subject());
            data.put("amount", view.amount());
            data.put("status", view.status());
            data.put("payUrl", view.payUrl());
            data.put("payForm", view.payForm());
            data.put("mock", view.mock());
            return Result.success(data);
        } catch (IllegalArgumentException e) {
            return Result.error("400", e.getMessage());
        } catch (IllegalStateException e) {
            return Result.error("500", e.getMessage());
        }
    }

    @Operation(summary = "查询订单", description = "查询归属于当前登录用户的订单状态")
    @GetMapping("/orders/{outTradeNo}")
    public Result getOrder(@PathVariable("outTradeNo") String outTradeNo) {
        PaymentOrder order = ownOrder(outTradeNo);
        if (order == null) {
            return Result.error("404", "订单不存在");
        }
        return Result.success(toView(order));
    }

    @Operation(summary = "同步订单状态", description = "主动向支付宝查单并回写状态")
    @PostMapping("/orders/{outTradeNo}/sync")
    public Result syncOrder(@PathVariable("outTradeNo") String outTradeNo) {
        PaymentOrder order = ownOrder(outTradeNo);
        if (order == null) {
            return Result.error("404", "订单不存在");
        }
        PaymentOrder synced = paymentService.syncOrder(order);
        return Result.success(toView(synced));
    }

    @Operation(summary = "模拟支付", description = "仅 Mock 演示模式下可用，直接把订单置为已支付")
    @PostMapping("/orders/{outTradeNo}/mock/pay")
    public Result mockPay(@PathVariable("outTradeNo") String outTradeNo) {
        PaymentOrder order = ownOrder(outTradeNo);
        if (order == null) {
            return Result.error("404", "订单不存在");
        }
        if (!paymentService.isMockMode()) {
            return Result.error("400", "当前为真实支付模式，不支持模拟支付");
        }
        try {
            return Result.success(toView(paymentService.mockPay(order)));
        } catch (IllegalStateException e) {
            return Result.error("400", e.getMessage());
        }
    }

    @Operation(summary = "支付宝同步回跳", description = "支付完成后浏览器回跳，仅作提示，真实状态以异步通知为准")
    @GetMapping("/alipay/return")
    public Map<String, Object> alipayReturn(@RequestParam Map<String, String> params) {
        Map<String, Object> data = new HashMap<>();
        data.put("outTradeNo", params.get("out_trade_no"));
        data.put("success", true);
        return data;
    }

    @Operation(summary = "支付宝异步通知", description = "接收支付结果通知，必须返回纯文本 success")
    @PostMapping("/alipay/notify")
    public String alipayNotify(@RequestParam Map<String, String> params) {
        logger.info("收到支付宝异步通知 outTradeNo={}", params.get("out_trade_no"));
        return paymentService.handleNotify(params);
    }

    private PaymentOrder ownOrder(String outTradeNo) {
        Long userId = SecurityUtils.getCurrentUserId();
        String type = SecurityUtils.getCurrentPrincipalType();
        if (userId == null || type == null) {
            return null;
        }
        return paymentService.getOwnOrder(type, userId, outTradeNo);
    }

    private Map<String, Object> toView(PaymentOrder order) {
        Map<String, Object> data = new HashMap<>();
        data.put("outTradeNo", order.getOutTradeNo());
        data.put("subject", order.getSubject());
        data.put("amount", order.getAmount());
        data.put("status", order.getStatus());
        data.put("channel", order.getChannel());
        data.put("payUrl", order.getPayUrl());
        data.put("remark", order.getRemark());
        data.put("createTime", order.getCreateTime());
        data.put("payTime", order.getPayTime());
        return data;
    }

    public static class CreateOrderRequest {
        /** 金额，单位：分 */
        public Long amount;
        public String purpose;
        public String remark;
    }
}
