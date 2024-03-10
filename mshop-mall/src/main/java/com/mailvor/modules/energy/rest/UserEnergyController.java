/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.rest;

import com.mailvor.api.MshopException;
import com.mailvor.modules.energy.domain.UserEnergyOrder;
import com.mailvor.modules.energy.domain.UserEnergyOrderLog;
import com.mailvor.modules.energy.dto.*;
import com.mailvor.modules.energy.param.UserEnergyParam;
import com.mailvor.modules.energy.param.UserEnergyScaleParam;
import com.mailvor.modules.energy.service.UserEnergyLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderLogService;
import com.mailvor.modules.energy.service.UserEnergyOrderService;
import com.mailvor.modules.energy.service.UserEnergyService;
import com.mailvor.modules.logging.aop.log.Log;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserHbScale;
import com.mailvor.modules.user.service.MwUserHbScaleService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

/**
* @author huangyu
* @date 2019-10-10
*/
@Api(tags = "商城:热度管理")
@RestController
@RequestMapping("api/energy")
public class UserEnergyController {

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private UserEnergyOrderService energyOrderService;


    @Resource
    private UserEnergyOrderLogService orderLogService;

    @Resource
    private UserEnergyLogService energyLogService;

    @Resource
    private UserEnergyService userEnergyService;

    @Resource
    private MwUserHbScaleService scaleService;
    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MwUserService userService;

    @Log("查询热度配置")
    @ApiOperation(value = "查询热度配置")
    @GetMapping(value = "/config")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_CONFIG_SELECT')")
    public ResponseEntity getEnergyConfig(){
        return new ResponseEntity<>(systemConfigService.getEnergyConfig(),HttpStatus.OK);
    }

    @Log("修改热度配置")
    @ApiOperation(value = "修改热度配置")
    @PostMapping(value = "/config")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_CONFIG_CREATE')")
    public ResponseEntity create(@RequestBody EnergyConfigDto param){

        systemConfigService.setEnergyConfig(param);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Log("查询热度订单")
    @ApiOperation(value = "查询热度订单")
    @GetMapping(value = "/order")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_SELECT')")
    public ResponseEntity getEnergyList(UserEnergyOrderQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(energyOrderService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PutMapping(value = "/order")
    @Log("修改热度订单")
    @ApiOperation("修改热度订单")
    @PreAuthorize("@el.check('admin','ENERGY_ALL','ENERGY_EDIT')")
    public ResponseEntity<Object> update(@Validated @RequestBody UserEnergyParam param){
        checkOpePwd2(param.getOpePwd());
        if(param.getReleaseMoney().compareTo(BigDecimal.valueOf(100)) == 1) {
            throw new MshopException("金额太大");
        }
        UserEnergyOrder energyOrder = energyOrderService.getById(param.getId());
        if(energyOrder == null) {
            throw new MshopException("热度订单不存在");
        }
        UserEnergyOrderLog orderLog = orderLogService.getById(energyOrder.getLogId());
        if(orderLog != null && orderLog.getType() != 2) {
            throw new MshopException("无法修改");
        }
        energyOrder.setReleaseMoney(param.getReleaseMoney().setScale(2, RoundingMode.HALF_UP));
        energyOrderService.updateById(energyOrder);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除热度订单")
    @ApiOperation("删除热度订单")
    @PreAuthorize("@el.check('admin','ENERGY_ALL','ENERGY_DELETE')")
    @DeleteMapping(value = "/order")
    public ResponseEntity<Object> deleteAll(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            energyOrderService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Log("查询热度订单记录")
    @ApiOperation(value = "查询热度订单记录")
    @GetMapping(value = "/order/log")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_SELECT')")
    public ResponseEntity getEnergyOrderLog(UserEnergyOrderLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(orderLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }
    @Log("查询热度增加记录")
    @ApiOperation(value = "查询热度增加记录")
    @GetMapping(value = "/log")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_SELECT')")
    public ResponseEntity getEnergyLog(UserEnergyLogQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(energyLogService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @Log("查询用户热度记录")
    @ApiOperation(value = "查询用户热度记录")
    @GetMapping(value = "/user")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_SELECT')")
    public ResponseEntity getUserEnergy(UserEnergyQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(userEnergyService.queryAll(criteria,pageable),HttpStatus.OK);
    }


    protected void checkOpePwd2(String pwd) {
        String adminPwd = redisUtils.getY("auth:code:ope222");
        if(StringUtils.isBlank(adminPwd)) {
            throw new MshopException("操作密码未设置，无法更改");
        }
        if(!pwd.toUpperCase().equals((adminPwd).toUpperCase())) {
            throw new MshopException("操作密码不正确");
        }
    }




    @Log("查询热度订单翻倍记录")
    @ApiOperation(value = "查询热度订单翻倍记录")
    @GetMapping(value = "/orderScale")
    @PreAuthorize("hasAnyRole('admin','ENERGY_ALL','ENERGY_SELECT')")
    public ResponseEntity getEnergyScaleList(UserEnergyOrderScaleQueryCriteria criteria, Pageable pageable){
        return new ResponseEntity<>(scaleService.queryAll(criteria,pageable),HttpStatus.OK);
    }

    @PostMapping(value = "/orderScale")
    @Log("修改热度订单翻倍记录")
    @ApiOperation("修改热度订单翻倍记录")
    @PreAuthorize("@el.check('admin','ENERGY_ALL','ENERGY_EDIT')")
    public ResponseEntity<Object> addScale(@Validated @RequestBody UserEnergyScaleParam param){
        checkOpePwd2(param.getOpePwd());
        MwUser user = userService.getById(param.getUid());
        if(user == null) {
            throw new MshopException("用户不存在");
        }

        MwUserHbScale scale = scaleService.getById(param.getUid());
        if(scale != null) {
            throw new MshopException("记录已存在");
        }
        scale = new MwUserHbScale();
        scale.setUid(param.getUid());
        scale.setMonthScale(param.getMonthScale());
        scale.setMonthInvalidDay(param.getMonthInvalidDay());

        scaleService.save(scale);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping(value = "/orderScale")
    @Log("修改热度订单翻倍记录")
    @ApiOperation("修改热度订单翻倍记录")
    @PreAuthorize("@el.check('admin','ENERGY_ALL','ENERGY_EDIT')")
    public ResponseEntity<Object> updateScale(@Validated @RequestBody UserEnergyScaleParam param){
        checkOpePwd2(param.getOpePwd());

        MwUserHbScale scale = scaleService.getById(param.getUid());
        if(scale == null) {
            throw new MshopException("记录不存在");
        }
        scale.setMonthScale(param.getMonthScale());
        scale.setMonthInvalidDay(param.getMonthInvalidDay());

        scaleService.updateById(scale);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Log("删除热度订单翻倍记录")
    @ApiOperation("删除热度订单翻倍记录")
    @PreAuthorize("@el.check('admin','ENERGY_ALL','ENERGY_DELETE')")
    @DeleteMapping(value = "/orderScale")
    public ResponseEntity<Object> deleteScales(@RequestBody Long[] ids) {
        Arrays.asList(ids).forEach(id->{
            scaleService.removeById(id);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
