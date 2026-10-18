package com.mailvor.modules.pay.config;

import cn.hutool.crypto.asymmetric.RSA;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mailvor.api.MshopException;
import com.mailvor.modules.utils.RsaUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.*;
import static com.mailvor.modules.config.RSAConfig.KEY_RSA_PRIVATE;

/** 通道配置只在服务端解密合并；管理接口仅返回非敏感字段与配置状态。 */
@Component
public class PayCertificateProfiles {
    private static final Set<String> PUBLIC_FIELDS = new HashSet<>(Arrays.asList(
            "appId", "gatewayUrl", "format", "charset", "signType", "sysServiceProviderId", "returnUrl", "mchId", "mode"));
    private static final Set<String> SECRET_FIELDS = new HashSet<>(Arrays.asList(
            "privateKey", "publicKey", "appCertContent", "alipayPublicCertContent", "rootCertContent", "mchKey", "keyContent"));
    private static final Map<String, String> LEGACY = new LinkedHashMap<>();
    static {
        LEGACY.put("appCertContent", "appPublicCert");
        LEGACY.put("alipayPublicCertContent", "aliPublicCert");
        LEGACY.put("rootCertContent", "aliRootCert");
        LEGACY.put("keyContent", "keyPath");
    }
    /** 旧版后台按数据库列名保存支付宝配置，与驼峰字段指向同一个属性。 */
    private static final Map<String, String> SNAKE_CASE = new LinkedHashMap<>();
    static {
        SNAKE_CASE.put("app_id", "appId");
        SNAKE_CASE.put("private_key", "privateKey");
        SNAKE_CASE.put("public_key", "publicKey");
        SNAKE_CASE.put("gateway_url", "gatewayUrl");
        SNAKE_CASE.put("sign_type", "signType");
        SNAKE_CASE.put("sys_service_provider_id", "sysServiceProviderId");
        SNAKE_CASE.put("return_url", "returnUrl");
        SNAKE_CASE.put("notify_url", "notifyUrl");
        SNAKE_CASE.put("app_public_cert", "appPublicCert");
        SNAKE_CASE.put("ali_public_cert", "aliPublicCert");
        SNAKE_CASE.put("ali_root_cert", "aliRootCert");
    }

    /**
     * fastjson 反序列化时同义字段先出现者生效，历史配置里残留的下划线旧字段会盖掉后台新保存的驼峰字段，
     * 曾导致用旧私钥签名却上报新证书的 app_cert_sn，支付宝返回 isv.invalid-signature。
     * 这里统一成驼峰字段：驼峰值优先，旧值只在驼峰缺失或空白时回填，随后删除旧键。
     */
    public JSONObject normalize(JSONObject profile) {
        JSONObject canonical = new JSONObject(true);
        for (Map.Entry<String, Object> entry : profile.entrySet()) {
            canonical.put(entry.getKey().trim(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : SNAKE_CASE.entrySet()) {
            if (!canonical.containsKey(entry.getKey())) continue;
            Object legacy = canonical.remove(entry.getKey());
            if (blank(canonical.get(entry.getValue())) && !blank(legacy)) canonical.put(entry.getValue(), legacy);
        }
        return canonical;
    }

    /** 运行时解密通道配置后调用，历史数据无需迁移即可读到正确字段；非对象结构原样返回。 */
    public String normalizeProfile(String profile) {
        if (StringUtils.isBlank(profile)) return profile;
        try {
            return normalize(JSON.parseObject(profile)).toJSONString();
        } catch (Exception e) {
            return profile;
        }
    }

    private boolean blank(Object value) {
        return value == null || (value instanceof String && StringUtils.isBlank((String) value));
    }

    public boolean nativeChannel(String key) {
        return "alipay".equals(key) || "alipayweb".equals(key) || "wechatpay".equals(key);
    }

    private JSONObject decrypt(byte[] encrypted) {
        if (encrypted == null || encrypted.length == 0) return new JSONObject();
        try {
            JSONObject profile = JSON.parseObject(RsaUtil.decrypt(new String(encrypted, StandardCharsets.UTF_8), KEY_RSA_PRIVATE));
            if (profile == null) throw new IllegalArgumentException();
            return normalize(profile);
        } catch (Exception e) {
            throw new MshopException("支付证书配置无法解析，请检查加密配置");
        }
    }

    public JSONObject metadata(String channelKey, byte[] encrypted) {
        JSONObject profile = decrypt(encrypted);
        JSONObject result = new JSONObject();
        JSONObject fields = new JSONObject();
        for (String field : PUBLIC_FIELDS) if (profile.containsKey(field)) fields.put(field, profile.get(field));
        if (!fields.containsKey("mode") && channelKey != null && channelKey.startsWith("alipay")) {
            fields.put("mode", !present(profile, "appCertContent") && !present(profile, "appPublicCert") && present(profile, "publicKey") ? 1 : 2);
        }
        List<String> configured = new ArrayList<>();
        for (String field : SECRET_FIELDS) {
            if (present(profile, field) || (LEGACY.containsKey(field) && present(profile, LEGACY.get(field)))) configured.add(field);
        }
        result.put("config", fields);
        result.put("configured", configured);
        return result;
    }

    /** 空白字段表示不替换；每次使用数据库最新内容合并，避免覆盖未编辑的私钥。 */
    public byte[] prepare(String channelKey, byte[] existing, String encryptedPatch, boolean merge) {
        if (StringUtils.isBlank(encryptedPatch)) return existing;
        JSONObject patch = decrypt(encryptedPatch.getBytes(StandardCharsets.UTF_8));
        JSONObject profile = merge ? decrypt(existing) : new JSONObject();
        if (merge && !nativeChannel(channelKey)) throw new MshopException("该支付通道请使用高级 JSON 配置");
        for (String field : patch.keySet()) {
            if (merge && !PUBLIC_FIELDS.contains(field) && !SECRET_FIELDS.contains(field)) {
                throw new MshopException("不支持的支付配置字段：" + field);
            }
            Object value = patch.get(field);
            if (!merge) {
                profile.put(field, value);
            } else if (value != null && (PUBLIC_FIELDS.contains(field)
                    || !(value instanceof String) || StringUtils.isNotBlank((String) value))) {
                profile.put(field, value instanceof String ? ((String) value).trim() : value);
            }
        }
        if (nativeChannel(channelKey)) validate(channelKey, profile, patch);
        return encrypt(profile);
    }

    private byte[] encrypt(JSONObject profile) {
        try {
            RSAPrivateCrtKey key = (RSAPrivateCrtKey) new RSA(KEY_RSA_PRIVATE, null).getPrivateKey();
            byte[] publicKey = KeyFactory.getInstance("RSA")
                    .generatePublic(new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent())).getEncoded();
            byte[] encrypted = RsaUtil.encrypt(profile.toJSONString(), Base64.getEncoder().encodeToString(publicKey))
                    .getBytes(StandardCharsets.UTF_8);
            if (encrypted.length > 65535) throw new MshopException("支付证书配置超过 64KB，请检查上传的证书文件");
            return encrypted;
        } catch (MshopException e) {
            throw e;
        } catch (Exception e) {
            throw new MshopException("支付证书配置加密失败");
        }
    }

    private boolean present(JSONObject profile, String field) { return StringUtils.isNotBlank(profile.getString(field)); }
    private void require(JSONObject profile, String field, String label) {
        if (!present(profile, field)) throw new MshopException("请配置" + label);
    }
    private void requireCertificate(JSONObject profile, String field, String label) {
        if (!present(profile, field) && !present(profile, LEGACY.get(field))) throw new MshopException("请上传" + label);
    }

    private void validate(String channelKey, JSONObject profile, JSONObject patch) {
        require(profile, "appId", "应用 APPID");
        if (channelKey.startsWith("alipay")) {
            if (profile.getInteger("mode") == null) {
                profile.put("mode", !present(profile, "appCertContent") && !present(profile, "appPublicCert")
                        && present(profile, "publicKey") ? 1 : 2);
            }
            if (!present(profile, "gatewayUrl")) profile.put("gatewayUrl", "https://openapi.alipay.com/gateway.do");
            if (!present(profile, "format")) profile.put("format", "json");
            if (!present(profile, "charset")) profile.put("charset", "UTF-8");
            if (!present(profile, "signType")) profile.put("signType", "RSA2");
            require(profile, "privateKey", "支付宝应用私钥");
            if (Integer.valueOf(1).equals(profile.getInteger("mode"))) {
                require(profile, "publicKey", "支付宝公钥");
            } else if (Integer.valueOf(2).equals(profile.getInteger("mode")) || present(profile, "appCertContent") || present(profile, "appPublicCert")) {
                requireCertificate(profile, "appCertContent", "应用公钥证书");
                requireCertificate(profile, "alipayPublicCertContent", "支付宝公钥证书");
                requireCertificate(profile, "rootCertContent", "支付宝根证书");
            } else {
                require(profile, "publicKey", "支付宝公钥或证书");
            }
            for (String field : Arrays.asList("appCertContent", "alipayPublicCertContent", "rootCertContent")) {
                if (present(patch, field)) validateCertificate(patch.getString(field), field);
            }
            if (present(patch, "privateKey")) {
                try {
                    String key = patch.getString("privateKey").replace("-----BEGIN PRIVATE KEY-----", "")
                            .replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
                    KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
                    profile.put("privateKey", key);
                } catch (Exception e) {
                    throw new MshopException("应用私钥格式无效，请使用 PKCS8 格式 RSA 私钥");
                }
            }
            if (Integer.valueOf(2).equals(profile.getInteger("mode")) && present(profile, "appCertContent")) {
                try {
                    java.security.interfaces.RSAPrivateKey privateKey = (java.security.interfaces.RSAPrivateKey)
                            KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(
                                    Base64.getMimeDecoder().decode(profile.getString("privateKey")
                                            .replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", ""))));
                    java.security.interfaces.RSAPublicKey publicKey = (java.security.interfaces.RSAPublicKey)
                            CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(
                                    profile.getString("appCertContent").getBytes(StandardCharsets.UTF_8))).getPublicKey();
                    if (!privateKey.getModulus().equals(publicKey.getModulus())) throw new IllegalArgumentException();
                } catch (Exception e) {
                    throw new MshopException("应用私钥与应用公钥证书不匹配，请上传同一应用的证书和私钥");
                }
            }
        } else {
            require(profile, "mchId", "微信商户号");
            require(profile, "mchKey", "微信 API v2 密钥");
            if (profile.getString("mchKey").length() != 32) throw new MshopException("微信 API v2 密钥必须为 32 位");
            if (present(profile, "keyContent")) {
                try {
                    KeyStore store = KeyStore.getInstance("PKCS12");
                    store.load(new ByteArrayInputStream(Base64.getDecoder().decode(profile.getString("keyContent"))),
                            profile.getString("mchId").toCharArray());
                    boolean hasPrivateKey = false;
                    Enumeration<String> aliases = store.aliases();
                    while (aliases.hasMoreElements()) hasPrivateKey |= store.isKeyEntry(aliases.nextElement());
                    if (!hasPrivateKey) throw new IllegalArgumentException();
                } catch (Exception e) {
                    throw new MshopException("微信证书无效或与商户号不匹配，请上传对应商户的 apiclient_cert.p12");
                }
            } else if (!present(profile, "keyPath")) {
                throw new MshopException("请上传微信商户证书 apiclient_cert.p12");
            }
        }
    }

    private void validateCertificate(String content, String field) {
        try {
            if (CertificateFactory.getInstance("X.509", new org.bouncycastle.jce.provider.BouncyCastleProvider()).generateCertificates(
                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8))).isEmpty()) throw new IllegalArgumentException();
        } catch (Exception e) {
            throw new MshopException("证书内容无效：" + field + "，请上传支付宝下载的 .crt 或 PEM 证书文件");
        }
    }
}
