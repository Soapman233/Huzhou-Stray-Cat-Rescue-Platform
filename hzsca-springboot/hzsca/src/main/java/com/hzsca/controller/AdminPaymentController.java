package com.hzsca.controller;

import com.hzsca.common.Result;
import com.hzsca.entity.PaymentAuditLog;
import com.hzsca.service.PaymentAuditService;
import com.hzsca.service.PaymentReconcileTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付管理（管理端）：手动触发对账、查看订单审计轨迹。
 *
 * <p>路径位于 {@code /api/admin/**}，由 {@code SecurityConfig} 统一要求 admin 角色。</p>
 */
@Tag(name = "支付管理（后台）", description = "支付对账与审计日志查询")
@RestController
@RequestMapping("/api/admin/payment")
public class AdminPaymentController {

    private final PaymentReconcileTask reconcileTask;
    private final PaymentAuditService auditService;

    public AdminPaymentController(PaymentReconcileTask reconcileTask, PaymentAuditService auditService) {
        this.reconcileTask = reconcileTask;
        this.auditService = auditService;
    }

    @Operation(summary = "手动触发支付对账", description = "立即执行一次对账：主动查单补记、超时关单、审计一致性检查")
    @PostMapping("/reconcile")
    public Result reconcile() {
        try {
            Map<String, Object> summary = reconcileTask.reconcile();
            return Result.success(summary);
        } catch (Exception e) {
            return Result.error("500", "对账执行失败：" + e.getMessage());
        }
    }

    @Operation(summary = "查询订单审计轨迹", description = "返回某笔订单的状态变更与回调处理记录")
    @GetMapping("/orders/{outTradeNo}/audit")
    public Result auditTrail(@PathVariable("outTradeNo") String outTradeNo) {
        List<PaymentAuditLog> logs = auditService.history(outTradeNo);
        Map<String, Object> data = new HashMap<>();
        data.put("outTradeNo", outTradeNo);
        data.put("total", logs.size());
        data.put("records", logs);
        return Result.success(data);
    }
}
