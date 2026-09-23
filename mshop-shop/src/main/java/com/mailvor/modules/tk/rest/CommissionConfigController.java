/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tk.rest;

import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.util.CommissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 佣金配置管理
 */
@AllArgsConstructor
@Api(tags = "商城：佣金配置管理")
@RestController
@RequestMapping("/api/commission")
public class CommissionConfigController {

    private final MwSystemConfigService mwSystemConfigService;

    @Log("查询佣金配置")
    @ApiOperation("查询佣金配置")
    @GetMapping(value = "/config")
    @PreAuthorize("hasAnyRole('admin','COMMISSION_CONFIG')")
    public ResponseEntity getConfig() {
        Map<String, Object> result = new HashMap<>();
        String selfRatio = mwSystemConfigService.getData(CommissionUtil.CONFIG_KEY_SELF_RATIO);
        String shareRatio = mwSystemConfigService.getData(CommissionUtil.CONFIG_KEY_SHARE_RATIO);

        result.put("ratioSelf", selfRatio != null ? Integer.parseInt(selfRatio) : 80);
        result.put("ratioShare", shareRatio != null ? Integer.parseInt(shareRatio) : 80);
        result.put("ratioPlatform", 100 - (selfRatio != null ? Integer.parseInt(selfRatio) : 80));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("更新佣金配置")
    @ApiOperation("更新佣金配置")
    @PostMapping(value = "/config")
    @PreAuthorize("hasAnyRole('admin','COMMISSION_CONFIG')")
    public ResponseEntity updateConfig(@RequestBody Map<String, Object> config) {
        Object ratioSelfObj = config.get("ratioSelf");
        Object ratioShareObj = config.get("ratioShare");

        if (ratioSelfObj == null || ratioShareObj == null) {
            return new ResponseEntity<>("参数不完整", HttpStatus.BAD_REQUEST);
        }

        int ratioSelf = Integer.parseInt(ratioSelfObj.toString());
        int ratioShare = Integer.parseInt(ratioShareObj.toString());

        if (ratioSelf < 0 || ratioSelf > 100 || ratioShare < 0 || ratioShare > 100) {
            return new ResponseEntity<>("比例必须在0-100之间", HttpStatus.BAD_REQUEST);
        }

        saveConfigValue(CommissionUtil.CONFIG_KEY_SELF_RATIO, String.valueOf(ratioSelf));
        saveConfigValue(CommissionUtil.CONFIG_KEY_SHARE_RATIO, String.valueOf(ratioShare));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("预览佣金分配")
    @ApiOperation("预览佣金分配")
    @GetMapping(value = "/preview")
    @PreAuthorize("hasAnyRole('admin','COMMISSION_CONFIG')")
    public ResponseEntity preview(@RequestParam BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseEntity<>("金额必须大于0", HttpStatus.BAD_REQUEST);
        }

        CommissionUtil.CommissionPreview preview = CommissionUtil.previewCommissionDistribution(amount);

        Map<String, Object> result = new HashMap<>();
        result.put("inputAmount", preview.getTotalCommission());
        result.put("selfRatio", preview.getSelfRatio());
        result.put("shareRatio", preview.getShareRatio());
        result.put("selfCommission", preview.getSelfCommission());
        result.put("shareCommission", preview.getShareCommission());
        result.put("platformCommission", preview.getPlatformCommission());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void saveConfigValue(String key, String value) {
        MwSystemConfig existing = mwSystemConfigService.getOne(
                new LambdaQueryWrapper<MwSystemConfig>().eq(MwSystemConfig::getMenuName, key));
        MwSystemConfig config = new MwSystemConfig();
        config.setMenuName(key);
        config.setValue(value);
        if (existing == null) {
            mwSystemConfigService.save(config);
        } else {
            config.setId(existing.getId());
            mwSystemConfigService.saveOrUpdate(config);
        }
    }
}
