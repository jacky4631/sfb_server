package com.mailvor.modules.tk.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommissionUtil 单元测试
 * 验证80/20佣金模型的核心计算逻辑
 * 注意：CommissionUtil的getSelfCommissionRatio/getShareCommissionRatio依赖Spring上下文，
 * 在无Spring环境的单元测试中会fallback到默认值80%，正好验证默认行为。
 */
class CommissionUtilTest {

    @Test
    @DisplayName("自购佣金计算 — 100元佣金按80%应得80元")
    void calculateSelfCommission_normalCase() {
        BigDecimal result = CommissionUtil.calculateSelfCommission(new BigDecimal("100"));
        assertEquals(new BigDecimal("80.00"), result);
    }

    @Test
    @DisplayName("自购佣金计算 — 小额佣金10.50元按80%应得8.40元")
    void calculateSelfCommission_smallAmount() {
        BigDecimal result = CommissionUtil.calculateSelfCommission(new BigDecimal("10.50"));
        assertEquals(new BigDecimal("8.40"), result);
    }

    @Test
    @DisplayName("自购佣金计算 — 零值返回0")
    void calculateSelfCommission_zero() {
        BigDecimal result = CommissionUtil.calculateSelfCommission(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("自购佣金计算 — null返回0")
    void calculateSelfCommission_null() {
        BigDecimal result = CommissionUtil.calculateSelfCommission((BigDecimal) null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("自购佣金计算 — 负值返回0")
    void calculateSelfCommission_negative() {
        BigDecimal result = CommissionUtil.calculateSelfCommission(new BigDecimal("-50"));
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("自购佣金计算 — 精度测试：99.99 * 80% = 80.00（四舍五入）")
    void calculateSelfCommission_rounding() {
        // 99.99 * 0.80 = 79.992 → 79.99
        BigDecimal result = CommissionUtil.calculateSelfCommission(new BigDecimal("99.99"));
        assertEquals(new BigDecimal("79.99"), result);
    }

    @Test
    @DisplayName("自购佣金计算 — 精度测试：0.01 * 80% = 0.01（四舍五入）")
    void calculateSelfCommission_penny() {
        // 0.01 * 0.80 = 0.008 → 0.01 (HALF_UP)
        BigDecimal result = CommissionUtil.calculateSelfCommission(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01"), result);
    }

    @Test
    @DisplayName("分享佣金计算 — 100元佣金按80%应得80元")
    void calculateShareCommission_normalCase() {
        BigDecimal result = CommissionUtil.calculateShareCommission(new BigDecimal("100"));
        assertEquals(new BigDecimal("80.00"), result);
    }

    @Test
    @DisplayName("分享佣金计算 — 50.55元按80%应得40.44元")
    void calculateShareCommission_specificAmount() {
        // 50.55 * 0.80 = 40.44
        BigDecimal result = CommissionUtil.calculateShareCommission(new BigDecimal("50.55"));
        assertEquals(new BigDecimal("40.44"), result);
    }

    @Test
    @DisplayName("分享佣金计算 — null返回0")
    void calculateShareCommission_null() {
        BigDecimal result = CommissionUtil.calculateShareCommission((BigDecimal) null);
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("平台留存计算 — 100元总佣金，用户得80元，平台留20元")
    void calculatePlatformCommission() {
        BigDecimal total = new BigDecimal("100");
        BigDecimal userCommission = new BigDecimal("80.00");
        BigDecimal result = CommissionUtil.calculatePlatformCommission(total, userCommission);
        assertEquals(new BigDecimal("20.00"), result);
    }

    @Test
    @DisplayName("double版本 — 自购佣金计算")
    void calculateSelfCommission_double() {
        double result = CommissionUtil.calculateSelfCommission(100.0);
        assertEquals(80.0, result, 0.01);
    }

    @Test
    @DisplayName("double版本 — 分享佣金计算")
    void calculateShareCommission_double() {
        double result = CommissionUtil.calculateShareCommission(100.0);
        assertEquals(80.0, result, 0.01);
    }

    @Test
    @DisplayName("淘宝服务费场景 — 佣金先扣服务费再算80%")
    void tbServiceFeeScenario() {
        // 模拟淘宝订单：平台佣金10元，服务费比例90%
        // 扣服务费后：10 * 90% = 9.00
        // 自购佣金：9.00 * 80% = 7.20
        BigDecimal commission = new BigDecimal("10");
        BigDecimal afterServiceFee = commission.multiply(new BigDecimal("90"))
                .divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal selfCommission = CommissionUtil.calculateSelfCommission(afterServiceFee);
        assertEquals(new BigDecimal("7.20"), selfCommission);
    }

    @Test
    @DisplayName("退款惩罚场景 — 佣金>=3次退款除以8")
    void refundPenaltyScenario() {
        // 佣金100元，80% = 80元，退款惩罚÷8 = 10元
        BigDecimal commission = new BigDecimal("100");
        BigDecimal selfCommission = CommissionUtil.calculateSelfCommission(commission);
        assertEquals(new BigDecimal("80.00"), selfCommission);

        // 模拟退款惩罚
        BigDecimal afterPenalty = selfCommission.divide(new BigDecimal("8"), 2, java.math.RoundingMode.HALF_UP);
        assertEquals(new BigDecimal("10.00"), afterPenalty);
    }

    @Test
    @DisplayName("完整链路场景 — 自购+一级分享+二级分享")
    void fullChainScenario() {
        // 平台佣金100元
        BigDecimal platformCommission = new BigDecimal("100");

        // 自购用户获得80%
        BigDecimal selfCommission = CommissionUtil.calculateSelfCommission(platformCommission);
        assertEquals(new BigDecimal("80.00"), selfCommission);

        // 一级分享者获得80%（基于自购佣金金额）
        BigDecimal shareOne = CommissionUtil.calculateShareCommission(selfCommission);
        assertEquals(new BigDecimal("64.00"), shareOne);

        // 二级分享者也获得80%（基于同一佣金金额）
        BigDecimal shareTwo = CommissionUtil.calculateShareCommission(selfCommission);
        assertEquals(new BigDecimal("64.00"), shareTwo);

        // 平台留存 = 100 - 80 = 20
        BigDecimal platformKeep = CommissionUtil.calculatePlatformCommission(platformCommission, selfCommission);
        assertEquals(new BigDecimal("20.00"), platformKeep);
    }

    @Test
    @DisplayName("默认比例验证 — 无Spring环境应fallback到80%")
    void defaultRatio() {
        BigDecimal selfRatio = CommissionUtil.getSelfCommissionRatio();
        BigDecimal shareRatio = CommissionUtil.getShareCommissionRatio();
        assertEquals(new BigDecimal("80"), selfRatio);
        assertEquals(new BigDecimal("80"), shareRatio);
    }
}
