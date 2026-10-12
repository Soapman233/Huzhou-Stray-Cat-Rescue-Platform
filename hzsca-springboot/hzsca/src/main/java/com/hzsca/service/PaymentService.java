package com.hzsca.service;

import com.hzsca.entity.PaymentOrder;

import java.util.Map;

/**
 * 支付服务。支持「支付宝沙箱」与「Mock 演示」两种模式。
 */
public interface PaymentService {

    /** 当前是否处于 Mock 演示模式（未配置真实支付宝密钥时） */
    boolean isMockMode();

    /**
     * 创建捐助订单并返回收银台信息。
     *
     * @param subjectType 下单主体类型：web / wx
     * @param subjectId   下单主体ID
     * @param amountFen   金额（分）
     * @param purpose     业务用途，默认 donate
     * @param remark      用户留言/昵称
     */
    OrderView createOrder(String subjectType, Long subjectId, long amountFen, String purpose, String remark);

    /** 查询归属于当前主体的订单（越权返回 null） */
    PaymentOrder getOwnOrder(String subjectType, Long subjectId, String outTradeNo);

    /** 主动向支付宝查单并同步状态（Mock 模式下直接返回当前订单） */
    PaymentOrder syncOrder(PaymentOrder order);

    /** Mock 模式下把订单标记为已支付 */
    PaymentOrder mockPay(PaymentOrder order);

    /**
     * 处理支付宝异步通知。
     *
     * @return 需要返回给支付宝的纯文本（success / failure）
     */
    String handleNotify(Map<String, String> params);

    /** 累计已捐助金额（分） */
    long totalDonatedFen();

    /** 下单结果视图（对外暴露的字段，不含敏感信息） */
    record OrderView(String outTradeNo, String subject, Long amount, String status,
                     String payForm, String payUrl, boolean mock) {
    }
}
