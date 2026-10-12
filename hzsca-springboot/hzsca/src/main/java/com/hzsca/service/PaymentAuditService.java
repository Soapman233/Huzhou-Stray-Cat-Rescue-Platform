package com.hzsca.service;

import com.hzsca.entity.PaymentAuditLog;
import com.hzsca.mapper.PaymentAuditLogMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付审计服务：统一写入支付订单的状态变更与对账日志。
 *
 * <p>设计原则：<b>审计失败绝不影响主流程</b>。所有写日志操作都吞掉异常并降级为告警日志，
 * 避免因日志表异常导致支付流程中断。</p>
 */
@Service
public class PaymentAuditService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentAuditService.class);

    /** 动作常量 */
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_PAY = "PAY";
    public static final String ACTION_NOTIFY = "NOTIFY";
    public static final String ACTION_SYNC = "SYNC";
    public static final String ACTION_RECONCILE = "RECONCILE";
    public static final String ACTION_CLOSE = "CLOSE";
    public static final String ACTION_FAIL = "FAIL";

    private final PaymentAuditLogMapper auditLogMapper;

    public PaymentAuditService(PaymentAuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    /** 记录一条审计日志（失败仅告警，不抛出） */
    public void record(String outTradeNo, String action, String fromStatus, String toStatus,
                       boolean success, String detail) {
        try {
            PaymentAuditLog log = new PaymentAuditLog();
            log.setOutTradeNo(outTradeNo);
            log.setAction(action);
            log.setFromStatus(fromStatus);
            log.setToStatus(toStatus);
            log.setSuccess(success);
            log.setDetail(truncate(detail, 2000));
            auditLogMapper.insert(log);
        } catch (Exception e) {
            logger.warn("写入支付审计日志失败 action={}, outTradeNo={}, error={}",
                    action, outTradeNo, e.getClass().getSimpleName());
        }
    }

    /** 查询某订单的审计轨迹 */
    public List<PaymentAuditLog> history(String outTradeNo) {
        try {
            return auditLogMapper.selectByOutTradeNo(outTradeNo);
        } catch (Exception e) {
            return List.of();
        }
    }

    /** 统计某动作在指定时间之后的次数 */
    public int countSince(String action, LocalDateTime since) {
        try {
            return auditLogMapper.countByActionSince(action, since);
        } catch (Exception e) {
            return 0;
        }
    }

    /** 找出「已支付但缺少 PAY/NOTIFY 审计记录」的订单号（对账异常项） */
    public List<String> findPaidOrdersWithoutAudit() {
        try {
            return auditLogMapper.selectPaidOrdersWithoutAudit();
        } catch (Exception e) {
            return List.of();
        }
    }

    private String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max) + "...(truncated)";
    }
}
