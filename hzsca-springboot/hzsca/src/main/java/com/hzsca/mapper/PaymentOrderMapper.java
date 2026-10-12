package com.hzsca.mapper;

import com.hzsca.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PaymentOrderMapper {

    int insert(PaymentOrder order);

    PaymentOrder selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    List<PaymentOrder> selectBySubject(@Param("subjectType") String subjectType,
                                       @Param("subjectId") Long subjectId);

    /**
     * 仅当订单当前状态在 allowedStatuses 中时才更新，用于并发下的状态幂等迁移。
     *
     * @return 受影响行数（0 表示状态已被其他请求改变）
     */
    int updateStatusIfCurrent(@Param("outTradeNo") String outTradeNo,
                              @Param("status") String status,
                              @Param("allowedStatuses") List<String> allowedStatuses,
                              @Param("tradeNo") String tradeNo,
                              @Param("buyerId") String buyerId,
                              @Param("notifyPayload") String notifyPayload);

    /** 记录收银台信息（跳转地址/表单） */
    int updatePayInfo(@Param("outTradeNo") String outTradeNo,
                      @Param("payUrl") String payUrl,
                      @Param("payForm") String payForm);

    /** 汇总已支付金额（分），用于捐助统计 */
    long sumPaidAmount();
}
