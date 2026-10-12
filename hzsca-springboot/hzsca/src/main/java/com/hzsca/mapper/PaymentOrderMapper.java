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

    /**
     * 查询「长时间未支付」的订单，用于对账任务主动查单与关单。
     *
     * @param before 创建时间早于该时刻的订单才会被查出
     * @param limit  单次处理上限
     */
    List<PaymentOrder> selectStalePending(@Param("before") java.time.LocalDateTime before,
                                          @Param("limit") int limit);

    /** 统计指定状态的订单数量 */
    int countByStatus(@Param("status") String status);
}
