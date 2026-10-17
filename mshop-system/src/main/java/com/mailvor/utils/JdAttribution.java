package com.mailvor.utils;

/** 京东多 App 订单归属。SFB 使用号段 1，UID 必须小于号段大小。 */
public final class JdAttribution {
    public static final long POSITION_BASE = 1_000_000_000_000L;
    public static final String SUB_UNION_PREFIX = "sfb_";

    private JdAttribution() {
    }

    /** UID 0 保留给未登录转链，回传订单不会绑定或入库。 */
    public static long positionId(long uid) {
        if (uid < 0 || uid >= POSITION_BASE) {
            throw new IllegalArgumentException("京东归属 UID 必须大于等于 0 且小于 " + POSITION_BASE);
        }
        return POSITION_BASE + uid;
    }

    public static String subUnionId(long uid) {
        return SUB_UNION_PREFIX + uid;
    }

    /** 优先解析 SFB 子渠道，无法获取有效 UID 时回退到 SFB 推广位号段。 */
    public static long userId(String subUnionId, Long positionId) {
        if (subUnionId != null && subUnionId.startsWith(SUB_UNION_PREFIX)) {
            try {
                long uid = Long.parseLong(subUnionId.substring(SUB_UNION_PREFIX.length()));
                if (uid > 0 && subUnionId.equals(subUnionId(uid))) {
                    return uid;
                }
            } catch (NumberFormatException ignored) {
                // 子渠道缺失、格式错误或溢出时，继续使用推广位号段。
            }
        }
        if (positionId == null || positionId <= POSITION_BASE
                || positionId >= 2 * POSITION_BASE) {
            return 0L;
        }
        return positionId - POSITION_BASE;
    }

}
