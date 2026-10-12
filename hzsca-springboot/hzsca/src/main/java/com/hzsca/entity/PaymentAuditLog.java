package com.hzsca.entity;

import java.time.LocalDateTime;

/**
 * 支付审计日志。记录订单状态变更与对账过程，便于问题排查与资金对账追溯。
 */
public class PaymentAuditLog {
    private Long id;
    /** 关联商户订单号（对账任务级日志可为空） */
    private String outTradeNo;
    /** 动作：CREATE / PAY / NOTIFY / SYNC / RECONCILE / CLOSE / FAIL */
    private String action;
    private String fromStatus;
    private String toStatus;
    private Boolean success;
    private String detail;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFromStatus() {
        return fromStatus;
    }

    public void setFromStatus(String fromStatus) {
        this.fromStatus = fromStatus;
    }

    public String getToStatus() {
        return toStatus;
    }

    public void setToStatus(String toStatus) {
        this.toStatus = toStatus;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
