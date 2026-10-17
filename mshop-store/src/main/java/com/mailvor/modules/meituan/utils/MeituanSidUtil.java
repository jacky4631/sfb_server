package com.mailvor.modules.meituan.utils;

import static com.mailvor.modules.meituan.constants.MeituanConstants.MT_MEDIUM_PREFIX;

/** SFB 保留独立 sid 前缀，避免将共享凭证的 TSQ 或其他 App 订单绑定到本地。 */
public final class MeituanSidUtil {
    private MeituanSidUtil() {
    }

    public static String build(Long uid) {
        if (uid != null && uid < 0) {
            throw new IllegalArgumentException("美团 UID 不能为负数");
        }
        return MT_MEDIUM_PREFIX + (uid == null ? 0L : uid);
    }

    /** 非 SFB 或非法 sid 返回 null，未登录的有效 sid 返回 0。 */
    public static Long parse(String sid) {
        if (sid == null || !sid.startsWith(MT_MEDIUM_PREFIX)) {
            return null;
        }
        try {
            long uid = Long.parseLong(sid.substring(MT_MEDIUM_PREFIX.length()));
            return uid >= 0 && sid.equals(build(uid)) ? uid : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
