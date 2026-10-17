package com.mailvor.modules.tk.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/** Splits the CMS address stored in AppDataConfig.kuCid into domain and CID. */
public final class KuCmsAddress {
    private final String domain;
    private final String cid;

    private KuCmsAddress(String domain, String cid) {
        this.domain = domain;
        this.cid = cid;
    }

    public String getDomain() { return domain; }
    public String getCid() { return cid; }

    public static KuCmsAddress parse(String address) {
        try {
            if (address == null || address.trim().isEmpty()) throw invalid();
            URI uri = new URI(address.trim());
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme())) ||
                    uri.getHost() == null || uri.getUserInfo() != null || uri.getRawQuery() == null) {
                throw invalid();
            }
            String cid = null;
            for (String part : uri.getRawQuery().split("&")) {
                String[] pair = part.split("=", 2);
                if ("cid".equals(decode(pair[0]))) {
                    if (cid != null || pair.length != 2) throw invalid();
                    cid = decode(pair[1]);
                }
            }
            if (cid == null || !cid.matches("[A-Za-z0-9_-]+")) throw invalid();
            String domain = uri.getHost().toLowerCase(Locale.ROOT);
            if (uri.getPort() != -1) domain += ":" + uri.getPort();
            return new KuCmsAddress(domain, cid);
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw invalid();
        }
    }

    private static String decode(String value) throws UnsupportedEncodingException {
        return URLDecoder.decode(value, "UTF-8");
    }

    private static IllegalArgumentException invalid() {
        return new IllegalArgumentException("请填写包含 cid 参数的完整好单库 CMS 地址，例如 http://jkj.mailvor.com/?cid=FazG6352");
    }
}
