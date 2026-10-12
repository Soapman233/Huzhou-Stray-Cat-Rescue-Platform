package com.hzsca.entity;

import java.time.LocalDateTime;

/**
 * 支付订单。
 *
 * <p>金额统一以「分」为单位存储，避免浮点误差。支持支付宝沙箱与 Mock 演示两种渠道。</p>
 */
public class PaymentOrder {
    private Long id;
    /** 商户订单号（对外唯一标识） */
    private String outTradeNo;
    /** 订单标题 */
    private String subject;
    /** 金额，单位：分 */
    private Long amount;
    /** 币种 */
    private String currency;
    /** 支付渠道：ALIPAY / MOCK */
    private String channel;
    /** 业务用途：donate=爱心捐助 */
    private String purpose;
    /** 订单状态：CREATED/PAYING/PAID/FINISHED/CLOSED/FAILED/REFUNDED */
    private String status;
    /** 支付渠道交易号 */
    private String tradeNo;
    /** 付款方在支付渠道的标识 */
    private String buyerId;
    /** 下单主体类型：web / wx */
    private String subjectType;
    /** 下单主体ID */
    private Long subjectId;
    /** 收银台跳转地址 */
    private String payUrl;
    /** 收银台自动提交表单 HTML */
    private String payForm;
    /** 异步通知原始报文 */
    private String notifyPayload;
    /** 用户留言/昵称 */
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime finishTime;
    private LocalDateTime updateTime;

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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getPayUrl() {
        return payUrl;
    }

    public void setPayUrl(String payUrl) {
        this.payUrl = payUrl;
    }

    public String getPayForm() {
        return payForm;
    }

    public void setPayForm(String payForm) {
        this.payForm = payForm;
    }

    public String getNotifyPayload() {
        return notifyPayload;
    }

    public void setNotifyPayload(String notifyPayload) {
        this.notifyPayload = notifyPayload;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
