package com.hzsca.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付宝（沙箱）配置。
 *
 * <p>当 {@link #enabled} 为 {@code false} 或密钥未配置时，支付模块自动降级为
 * <b>Mock 演示模式</b>：下单后可直接调用 mock 支付接口把订单置为已支付，
 * 便于在没有支付宝账号的环境下演示完整流程。</p>
 *
 * <p>配置示例（application.yml）：
 * <pre>
 * alipay:
 *   enabled: true
 *   app-id: ${ALIPAY_APP_ID:}
 *   merchant-private-key: ${ALIPAY_PRIVATE_KEY:}
 *   alipay-public-key: ${ALIPAY_PUBLIC_KEY:}
 * </pre>
 * </p>
 */
@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {

    /** 是否启用真实支付宝沙箱（false 时走 Mock 演示模式） */
    private boolean enabled = false;

    /** 沙箱网关地址 */
    private String gateway = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";

    /** 应用 AppID（沙箱应用） */
    private String appId = "";

    /** 商户应用私钥（PKCS8，Base64，不含头尾标记） */
    private String merchantPrivateKey = "";

    /** 支付宝公钥（用于验签异步通知） */
    private String alipayPublicKey = "";

    /** 异步通知地址（需公网可达，本地可用内网穿透） */
    private String notifyUrl = "http://localhost:8080/api/payment/alipay/notify";

    /** 同步跳转地址（支付完成后浏览器回跳） */
    private String returnUrl = "http://localhost:5173/hzsca/donate/result";

    /** 签名算法 */
    private String signType = "RSA2";

    /** 字符集 */
    private String charset = "UTF-8";

    /** 单笔最小金额（分） */
    private long minAmount = 100L;

    /** 单笔最大金额（分） */
    private long maxAmount = 5_000_000L;

    /** 是否已具备真实调用条件 */
    public boolean isConfigured() {
        return enabled
                && appId != null && !appId.isBlank()
                && merchantPrivateKey != null && !merchantPrivateKey.isBlank()
                && alipayPublicKey != null && !alipayPublicKey.isBlank();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMerchantPrivateKey() {
        return merchantPrivateKey;
    }

    public void setMerchantPrivateKey(String merchantPrivateKey) {
        this.merchantPrivateKey = merchantPrivateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(long minAmount) {
        this.minAmount = minAmount;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(long maxAmount) {
        this.maxAmount = maxAmount;
    }
}
