package com.hzsca.service;

import com.hzsca.config.AlipayProperties;
import com.hzsca.entity.PaymentOrder;
import com.hzsca.mapper.PaymentOrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付对账定时任务。
 *
 * <p>资金类业务必须假设「异步通知会丢失」，因此除前端轮询外，还需服务端兜底对账：</p>
 * <ol>
 *   <li><b>主动查单</b>：把长时间停留在 CREATED / PAYING 的订单逐笔向支付宝查单，
 *       若已支付则补记状态（解决「通知丢失导致订单状态不更新」）；</li>
 *   <li><b>超时关单</b>：超过 {@code closeHours} 仍未支付的订单置为 CLOSED，避免脏数据长期堆积；</li>
 *   <li><b>审计一致性检查</b>：找出「已支付但缺少 PAY/NOTIFY 审计记录」的订单并告警。</li>
 * </ol>
 *
 * <p>可通过 {@code payment.reconcile.enabled=false} 关闭；Mock 演示模式下不会真正调用支付宝。</p>
 */
@Component
@ConditionalOnProperty(name = "payment.reconcile.enabled", havingValue = "true", matchIfMissing = true)
public class PaymentReconcileTask {

    private static final Logger logger = LoggerFactory.getLogger(PaymentReconcileTask.class);

    private final PaymentOrderMapper orderMapper;
    private final PaymentService paymentService;
    private final PaymentAuditService auditService;
    private final AlipayProperties alipayProperties;

    /** 超过该分钟数仍未支付的订单才会被对账（避免与用户正在进行的支付冲突） */
    @Value("${payment.reconcile.pending-minutes:30}")
    private int pendingMinutes;

    /** 超过该小时数仍未支付的订单将被关闭 */
    @Value("${payment.reconcile.close-hours:24}")
    private int closeHours;

    /** 单次对账处理的最大订单数 */
    @Value("${payment.reconcile.batch-size:200}")
    private int batchSize;

    public PaymentReconcileTask(PaymentOrderMapper orderMapper,
                                PaymentService paymentService,
                                PaymentAuditService auditService,
                                AlipayProperties alipayProperties) {
        this.orderMapper = orderMapper;
        this.paymentService = paymentService;
        this.auditService = auditService;
        this.alipayProperties = alipayProperties;
    }

    /** 默认每天凌晨 02:30 执行一次 */
    @Scheduled(cron = "${payment.reconcile.cron:0 30 2 * * ?}")
    public void scheduledReconcile() {
        try {
            Map<String, Object> summary = reconcile();
            logger.info("支付对账任务完成：{}", summary);
        } catch (Exception e) {
            logger.error("支付对账任务异常", e);
        }
    }

    /**
     * 执行一次对账。
     *
     * @return 对账结果摘要（也供管理端手动触发时返回）
     */
    public Map<String, Object> reconcile() {
        Map<String, Object> summary = new HashMap<>();
        LocalDateTime pendingBefore = LocalDateTime.now().minusMinutes(Math.max(pendingMinutes, 1));
        LocalDateTime closeBefore = LocalDateTime.now().minusHours(Math.max(closeHours, 1));

        List<PaymentOrder> candidates = orderMapper.selectStalePending(pendingBefore, batchSize);
        int checked = 0;
        int repaired = 0;
        int closed = 0;

        for (PaymentOrder order : candidates) {
            checked++;
            // 1) 主动查单兜底（Mock 模式下 syncOrder 会直接返回原订单）
            PaymentOrder synced = paymentService.syncOrder(order);
            if (synced != null && ("PAID".equals(synced.getStatus()) || "FINISHED".equals(synced.getStatus()))) {
                repaired++;
                auditService.record(order.getOutTradeNo(), PaymentAuditService.ACTION_RECONCILE,
                        order.getStatus(), synced.getStatus(), true, "对账查单：已补记为已支付");
                continue;
            }
            // 2) 超时关单
            if (order.getCreateTime() != null && order.getCreateTime().isBefore(closeBefore)) {
                int rows = orderMapper.updateStatusIfCurrent(order.getOutTradeNo(), "CLOSED",
                        List.of("CREATED", "PAYING"), null, null, null);
                if (rows > 0) {
                    closed++;
                    auditService.record(order.getOutTradeNo(), PaymentAuditService.ACTION_CLOSE,
                            order.getStatus(), "CLOSED", true, "对账任务：超过 " + closeHours + " 小时未支付，自动关单");
                }
            }
        }

        // 3) 审计一致性检查
        List<String> missingAudit = auditService.findPaidOrdersWithoutAudit();
        if (!missingAudit.isEmpty()) {
            logger.warn("发现 {} 笔已支付订单缺少审计记录：{}", missingAudit.size(), missingAudit);
            auditService.record(null, PaymentAuditService.ACTION_RECONCILE, null, null, false,
                    "对账异常：以下已支付订单缺少 PAY/NOTIFY 审计记录 -> " + missingAudit);
        }

        int paidCount = orderMapper.countByStatus("PAID") + orderMapper.countByStatus("FINISHED");
        summary.put("mode", paymentService.isMockMode() ? "MOCK" : "ALIPAY");
        summary.put("checked", checked);
        summary.put("repaired", repaired);
        summary.put("closed", closed);
        summary.put("paidTotal", paidCount);
        summary.put("missingAudit", missingAudit.size());
        summary.put("finishedAt", LocalDateTime.now().toString());

        auditService.record(null, PaymentAuditService.ACTION_RECONCILE, null, null, true,
                "对账完成：" + summary);
        return summary;
    }
}
