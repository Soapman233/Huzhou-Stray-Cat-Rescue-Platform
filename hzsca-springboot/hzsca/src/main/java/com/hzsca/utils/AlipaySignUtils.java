package com.hzsca.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 支付宝 RSA2 签名工具（零第三方依赖，仅使用 JDK 内置 {@code java.security}）。
 *
 * <p>支付宝开放平台要求：
 * <ul>
 *   <li>签名算法 RSA2 = {@code SHA256withRSA}；</li>
 *   <li>待签名串为「除 sign、sign_type 外」的所有非空参数，按参数名 ASCII 升序，
 *       以 {@code k=v} 形式用 {@code &} 拼接（值不做 URL 编码）；</li>
 *   <li>签名结果为 Base64 字符串。</li>
 * </ul>
 * </p>
 *
 * <p>商户私钥为 PKCS#8 格式，支付宝公钥为 X.509 格式，均为 Base64 编码（不含 PEM 头尾标记）。</p>
 */
public final class AlipaySignUtils {

    private AlipaySignUtils() {
    }

    /**
     * 构造待签名串：过滤空值、sign、sign_type，按键名 ASCII 升序拼接。
     */
    public static String buildSignContent(Map<String, String> params) {
        TreeMap<String, String> sorted = new TreeMap<>(params);
        List<String> pairs = new ArrayList<>();
        for (Map.Entry<String, String> e : sorted.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if (key == null || value == null || value.isEmpty()) {
                continue;
            }
            if ("sign".equals(key) || "sign_type".equals(key)) {
                continue;
            }
            pairs.add(key + "=" + value);
        }
        return String.join("&", pairs);
    }

    /**
     * 使用商户私钥对内容做 RSA2 签名。
     *
     * @param content          待签名串
     * @param privateKeyBase64 PKCS#8 私钥（Base64）
     * @return Base64 签名串
     */
    public static String sign(String content, String privateKeyBase64) throws Exception {
        PrivateKey privateKey = parsePrivateKey(privateKeyBase64);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    /**
     * 使用支付宝公钥验证签名。
     *
     * @param content         待验签串（由原始参数重新拼接得到，不含 sign/sign_type）
     * @param signBase64      支付宝返回的 sign
     * @param publicKeyBase64 X.509 公钥（Base64）
     */
    public static boolean verify(String content, String signBase64, String publicKeyBase64) throws Exception {
        PublicKey publicKey = parsePublicKey(publicKeyBase64);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.getDecoder().decode(signBase64));
    }

    private static PrivateKey parsePrivateKey(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(stripPem(base64));
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    private static PublicKey parsePublicKey(String base64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(stripPem(base64));
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /** 容忍用户直接粘贴带 PEM 头尾或换行的密钥 */
    private static String stripPem(String key) {
        if (key == null) {
            return "";
        }
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
    }
}
