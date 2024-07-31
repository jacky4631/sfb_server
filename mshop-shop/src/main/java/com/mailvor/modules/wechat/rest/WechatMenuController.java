/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.wechat.rest;


import com.mailvor.constant.ShopConstants;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.aop.ForbidSubmit;
import com.mailvor.modules.mp.domain.MwWechatMenu;
import com.mailvor.modules.mp.service.MwWechatMenuService;
import com.mailvor.modules.mp.config.WxMpConfiguration;
import com.mailvor.utils.OrderUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.bean.menu.WxMenu;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
* @author huangyu
* @date 2019-10-06
*/
@Api(tags = "微信：微信菜单")
@RestController
@RequestMapping("api")
@SuppressWarnings("unchecked")
public class WechatMenuController {

    private final MwWechatMenuService mwWechatMenuService;

    public WechatMenuController(MwWechatMenuService mwWechatMenuService) {
        this.mwWechatMenuService = mwWechatMenuService;
    }

    @ApiOperation(value = "查询菜单")
    @GetMapping(value = "/mwWechatMenu")
    @PreAuthorize("hasAnyRole('admin','MwWechatMenu_ALL','MwWechatMenu_SELECT')")
    public ResponseEntity getMwWechatMenus(){
        return new ResponseEntity(mwWechatMenuService.getOne(new LambdaQueryWrapper<MwWechatMenu>()
                .eq(MwWechatMenu::getKey,ShopConstants.WECHAT_MENUS)),HttpStatus.OK);
    }

    @ForbidSubmit
    @ApiOperation(value = "创建菜单")
    @PostMapping(value = "/mwWechatMenu")
    @PreAuthorize("hasAnyRole('admin','MwWechatMenu_ALL','MwWechatMenu_CREATE')")
    public ResponseEntity create( @RequestBody String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        String jsonButton = jsonObject.get("buttons").toString();
        MwWechatMenu mwWechatMenu = new MwWechatMenu();
        Boolean isExist = mwWechatMenuService.isExist(ShopConstants.WECHAT_MENUS);
        WxMenu menu = JSONObject.parseObject(jsonStr,WxMenu.class);

        WxMpService wxService = WxMpConfiguration.getWxMpService();
        if(isExist){
            mwWechatMenu.setKey(ShopConstants.WECHAT_MENUS);
            mwWechatMenu.setResult(jsonButton);
            mwWechatMenuService.saveOrUpdate(mwWechatMenu);
        }else {
            mwWechatMenu.setKey(ShopConstants.WECHAT_MENUS);
            mwWechatMenu.setResult(jsonButton);
            mwWechatMenu.setAddTime(OrderUtil.getSecondTimestampTwo());
            mwWechatMenuService.save(mwWechatMenu);
        }


        //创建菜单
        try {
            wxService.getMenuService().menuDelete();
            wxService.getMenuService().menuCreate(menu);
        } catch (WxErrorException e) {
            throw new BadRequestException(e.getMessage());
           // e.printStackTrace();
        }

        return new ResponseEntity(HttpStatus.OK);
    }




}
