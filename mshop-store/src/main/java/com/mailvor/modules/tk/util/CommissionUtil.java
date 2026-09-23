package com.mailvor.modules.tk.util;

import cn.hutool.core.util.NumberUtil;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 佣金计算工具类
 * 统一处理自购返利和分享返利的佣金计算
 * 默认比例：自购返80%，分享返80%，平台留20%
 */
@Slf4j
public class CommissionUtil {

    // 默认佣金比例（80%归用户）
    private static final BigDecimal DEFAULT_COMMISSION_RATIO = new BigDecimal("80");

    // 配置key：自购佣金比例
    public static final String CONFIG_KEY_SELF_RATIO = "commission_ratio_self";
    // 配置key：分享佣金比例
    public static final String CONFIG_KEY_SHARE_RATIO = "commission_ratio_share";

    /**
     * 获取自购佣金比例（从系统配置读取，默认80%）
     */
    public static BigDecimal getSelfCommissionRatio() {
        try {
            MwSystemConfigService configService = SpringContextHolder.getBean(MwSystemConfigService.class);
            String ratioStr = configService.getData(CONFIG_KEY_SELF_RATIO);
            if (ratioStr != null && !ratioStr.isEmpty()) {
                return new BigDecimal(ratioStr);
            }
        } catch (Exception e) {
            log.warn("获取自购佣金比例配置失败，使用默认值80%", e);
        }
        return DEFAULT_COMMISSION_RATIO;
    }

    /**
     * 获取分享佣金比例（从系统配置读取，默认80%）
     */
    public static BigDecimal getShareCommissionRatio() {
        try {
            MwSystemConfigService configService = SpringContextHolder.getBean(MwSystemConfigService.class);
            String ratioStr = configService.getData(CONFIG_KEY_SHARE_RATIO);
            if (ratioStr != null && !ratioStr.isEmpty()) {
                return new BigDecimal(ratioStr);
            }
        } catch (Exception e) {
            log.warn("获取分享佣金比例配置失败，使用默认值80%", e);
        }
        return DEFAULT_COMMISSION_RATIO;
    }

    /**
     * 计算自购佣金（用户获得的返利金额）
     * @param totalCommission 平台总佣金（来自电商平台的佣金）
     * @return 用户获得的佣金
     */
    public static BigDecimal calculateSelfCommission(BigDecimal totalCommission) {
        if (totalCommission == null || totalCommission.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ratio = getSelfCommissionRatio();
        return totalCommission.multiply(ratio)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算分享佣金（分享者获得的返利金额）
     * @param totalCommission 平台总佣金
     * @return 分享者获得的佣金
     */
    public static BigDecimal calculateShareCommission(BigDecimal totalCommission) {
        if (totalCommission == null || totalCommission.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal ratio = getShareCommissionRatio();
        return totalCommission.multiply(ratio)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * 计算平台留存佣金
     * @param totalCommission 平台总佣金
     * @param userCommission 用户获得的佣金
     * @return 平台留存的佣金
     */
    public static BigDecimal calculatePlatformCommission(BigDecimal totalCommission, BigDecimal userCommission) {
        if (totalCommission == null || userCommission == null) {
            return BigDecimal.ZERO;
        }
        return totalCommission.subtract(userCommission);
    }

    /**
     * 计算自购佣金（double版本）
     */
    public static double calculateSelfCommission(double totalCommission) {
        return calculateSelfCommission(BigDecimal.valueOf(totalCommission)).doubleValue();
    }

    /**
     * 计算分享佣金（double版本）
     */
    public static double calculateShareCommission(double totalCommission) {
        return calculateShareCommission(BigDecimal.valueOf(totalCommission)).doubleValue();
    }

    /**
     * 预览佣金分配结果（用于后台配置页面预览）
     * @param totalCommission 示例佣金金额
     * @return 佣金分配结果
     */
    public static CommissionPreview previewCommissionDistribution(BigDecimal totalCommission) {
        BigDecimal selfRatio = getSelfCommissionRatio();
        BigDecimal shareRatio = getShareCommissionRatio();

        BigDecimal selfCommission = calculateSelfCommission(totalCommission);
        BigDecimal shareCommission = calculateShareCommission(totalCommission);
        BigDecimal platformCommission = calculatePlatformCommission(totalCommission, selfCommission);

        return new CommissionPreview(
                totalCommission,
                selfRatio,
                shareRatio,
                selfCommission,
                shareCommission,
                platformCommission
        );
    }

    /**
     * 佣金预览结果
     */
    public static class CommissionPreview {
        private final BigDecimal totalCommission;
        private final BigDecimal selfRatio;
        private final BigDecimal shareRatio;
        private final BigDecimal selfCommission;
        private final BigDecimal shareCommission;
        private final BigDecimal platformCommission;

        public CommissionPreview(BigDecimal totalCommission, BigDecimal selfRatio,
                                 BigDecimal shareRatio, BigDecimal selfCommission,
                                 BigDecimal shareCommission, BigDecimal platformCommission) {
            this.totalCommission = totalCommission;
            this.selfRatio = selfRatio;
            this.shareRatio = shareRatio;
            this.selfCommission = selfCommission;
            this.shareCommission = shareCommission;
            this.platformCommission = platformCommission;
        }

        public BigDecimal getTotalCommission() { return totalCommission; }
        public BigDecimal getSelfRatio() { return selfRatio; }
        public BigDecimal getShareRatio() { return shareRatio; }
        public BigDecimal getSelfCommission() { return selfCommission; }
        public BigDecimal getShareCommission() { return shareCommission; }
        public BigDecimal getPlatformCommission() { return platformCommission; }

        @Override
        public String toString() {
            return String.format("佣金分配预览: 总佣金=%.2f, 自购比例=%s%%, 分享比例=%s%%, " +
                            "自购佣金=%.2f, 分享佣金=%.2f, 平台留存=%.2f",
                    totalCommission, selfRatio, shareRatio,
                    selfCommission, shareCommission, platformCommission);
        }
    }
}
