package com.hzsca.mapper;

import com.hzsca.entity.PaymentAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PaymentAuditLogMapper {

    int insert(PaymentAuditLog log);

    List<PaymentAuditLog> selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    /** 查询指定动作、指定时间之后的日志条数（用于对账统计） */
    int countByActionSince(@Param("action") String action, @Param("since") LocalDateTime since);

    /**
     * 查询「已支付但缺少审计记录」的订单号，用于对账时发现异常。
     */
    List<String> selectPaidOrdersWithoutAudit();
}
