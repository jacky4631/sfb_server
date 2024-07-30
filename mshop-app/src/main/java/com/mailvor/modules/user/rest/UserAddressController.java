/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.rest;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.common.util.CityTreeUtil;
import com.mailvor.common.web.param.IdParam;
import com.mailvor.constant.ShopConstants;
import com.mailvor.modules.template.domain.MwSystemCity;
import com.mailvor.modules.template.service.MwSystemCityService;
import com.mailvor.modules.user.domain.MwUserAddress;
import com.mailvor.modules.user.param.AddressParam;
import com.mailvor.modules.user.service.MwUserAddressService;
import com.mailvor.modules.user.vo.CityVo;
import com.mailvor.modules.user.vo.MwUserAddressQueryVo;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户地前端控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-28
 */
@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(value = "用户地址", tags = "用户:用户地址")
public class UserAddressController {

    private final MwUserAddressService userAddressService;
    private final MwSystemCityService systemCityService;


    @Cacheable(cacheNames = ShopConstants.MSHOP_REDIS_CITY_KEY)
    @GetMapping("/city_list")
    @ApiOperation(value = "城市列表",notes = "城市列表")
    public ApiResult<List<CityVo>> getTest() {
        List<MwSystemCity> mwSystemCities = systemCityService.list();

        List<CityVo> cityVOS = Lists.newArrayList();

        for (MwSystemCity systemCity : mwSystemCities){
            CityVo cityVO = new CityVo();

            cityVO.setV(systemCity.getCityId());
            cityVO.setN(systemCity.getName());
            cityVO.setPid(systemCity.getParentId());

            cityVOS.add(cityVO);
        }


        return ApiResult.ok(CityTreeUtil.list2TreeConverter(cityVOS, 0));

    }

    /**
    * 添加或修改地址
    */
    @AppLog(value = "添加或修改地址", type = 1)
    @AuthCheck
    @PostMapping("/address/edit")
    @ApiOperation(value = "添加或修改地址",notes = "添加或修改地址")
    public ApiResult<Map<String,Object>> addMwUserAddress(@Valid @RequestBody AddressParam param){
        Long uid = LocalUser.getUser().getUid();
        Long id = userAddressService.addAndEdit(uid,param);
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("id",id);
        return ApiResult.ok(map);
    }

    /**
     * 设置默认地址
     */
    @AppLog(value = "设置默认地址", type = 1)
    @AuthCheck
    @PostMapping("/address/default/set")
    @ApiOperation(value = "设置默认地址",notes = "设置默认地址")
    public ApiResult<Boolean> setDefault(@Valid @RequestBody IdParam idParam){
        Long uid = LocalUser.getUser().getUid();
        userAddressService.setDefault(uid,Long.valueOf(idParam.getId()));
        return ApiResult.ok();
    }



    /**
    * 删除用户地址
    */
    @AuthCheck
    @PostMapping("/address/del")
    @ApiOperation(value = "删除用户地址",notes = "删除用户地址")
    public ApiResult<Boolean> deleteMwUserAddress(@Valid @RequestBody IdParam idParam){
        userAddressService.removeById(Long.valueOf(idParam.getId()));
        return ApiResult.ok();
    }


    /**
     * 用户地址列表
     */
    @AuthCheck
    @GetMapping("/address/list")
    @ApiOperation(value = "用户地址列表",notes = "用户地址列表")
    public ApiResult<List<MwUserAddressQueryVo>> getMwUserAddressPageList(@RequestParam(value = "page",defaultValue = "1") int page,
                                                                          @RequestParam(value = "limit",defaultValue = "10") int limit){
        Long uid = LocalUser.getUser().getUid();
        List<MwUserAddressQueryVo> addressQueryVos = userAddressService.getList(uid,page,limit);
        return ApiResult.ok(addressQueryVos);
    }

    /**
     * 地址详情
     */
    @AuthCheck
    @GetMapping("/address/detail/{id}")
    @ApiOperation(value = "地址详情",notes = "地址详情")
    public ApiResult<MwUserAddressQueryVo> addressDetail(@PathVariable String id){
        if(StrUtil.isBlank(id) || !NumberUtil.isNumber(id)){
            throw new MshopException("参数非法");
        }
        return ApiResult.ok(userAddressService.getDetail(Long.valueOf(id)));
    }

    /**
     * 获取用户默认地址
     */
    @AuthCheck
    @GetMapping("/address/default")
    @ApiOperation(value = "默认地址",notes = "默认地址")
    public ApiResult<MwUserAddress> addressDefault(){
        Long uid = LocalUser.getUser().getUid();
        return ApiResult.ok(userAddressService.getUserDefaultAddress(uid));
    }

}

