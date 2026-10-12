-- ============================================================
-- HZSCA 支付模块 —— 支付订单表
-- ------------------------------------------------------------
-- 说明：本表用于记录捐助/支付订单，支持「支付宝沙箱」与「Mock 演示」两种模式。
--       金额统一以「分」为单位（BIGINT）存储，避免浮点误差。
--       由 DbBootstrap 在检测到表缺失时自动执行，也可手动导入：
--         mysql -uroot -p hzsca < payment.sql
-- ============================================================

USE hzsca;

CREATE TABLE IF NOT EXISTS `payment_order` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `out_trade_no`   VARCHAR(64)  NOT NULL COMMENT '商户订单号（对外唯一标识）',
    `subject`        VARCHAR(128) NOT NULL DEFAULT '爱心捐助' COMMENT '订单标题',
    `amount`         BIGINT       NOT NULL COMMENT '金额，单位：分',
    `currency`       VARCHAR(8)   NOT NULL DEFAULT 'CNY' COMMENT '币种',
    `channel`        VARCHAR(20)  NOT NULL DEFAULT 'ALIPAY' COMMENT '支付渠道：ALIPAY/MOCK',
    `purpose`        VARCHAR(32)  NOT NULL DEFAULT 'donate' COMMENT '业务用途：donate=爱心捐助',
    `status`         VARCHAR(20)  NOT NULL DEFAULT 'CREATED' COMMENT '订单状态：CREATED/PAYING/PAID/FINISHED/CLOSED/FAILED/REFUNDED',
    `trade_no`       VARCHAR(64)  NULL COMMENT '支付渠道交易号（支付宝 trade_no）',
    `buyer_id`       VARCHAR(64)  NULL COMMENT '付款方在支付渠道的标识（支付宝 buyer_id）',
    `subject_type`   VARCHAR(10)  NOT NULL COMMENT '下单主体类型：web=网页用户 / wx=小程序用户',
    `subject_id`     BIGINT       NOT NULL COMMENT '下单主体ID（对应 users.UserID 或 wx_user.id）',
    `pay_url`        TEXT         NULL COMMENT '收银台跳转地址（支付宝返回的 payUrl 或自建表单地址）',
    `pay_form`       TEXT         NULL COMMENT '收银台自动提交表单 HTML（支付宝 page.pay 返回）',
    `notify_payload` TEXT         NULL COMMENT '异步通知原始报文（便于对账与排查）',
    `remark`         VARCHAR(255) NULL COMMENT '用户留言/昵称等附加信息',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `pay_time`       DATETIME     NULL COMMENT '支付成功时间',
    `finish_time`    DATETIME     NULL COMMENT '订单完结时间',
    `update_time`    DATETIME     NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_payment_out_trade_no` (`out_trade_no`),
    KEY `idx_payment_status` (`status`),
    KEY `idx_payment_subject` (`subject_type`, `subject_id`),
    KEY `idx_payment_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付订单表';
