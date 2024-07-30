/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import cn.binarywang.wx.miniapp.json.WxMaGsonBuilder;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.mp.domain.MwWechatLiveGoods;
import com.mailvor.modules.mp.service.MwWechatLiveGoodsService;
import com.mailvor.modules.mp.service.MwWechatLiveService;
import com.mailvor.modules.mp.service.dto.*;
import com.mailvor.modules.mp.service.mapper.MwWechatLiveMapper;
import com.mailvor.modules.mp.vo.WechatLiveVo;
import com.mailvor.modules.mp.domain.MwWechatLive;
import com.mailvor.modules.mp.config.WxMaConfiguration;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.bean.result.WxMediaUploadResult;
import me.chanjar.weixin.common.enums.WxType;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.util.json.GsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.binarywang.wx.miniapp.constant.WxMaApiUrlConstants.Broadcast.Room.CREATE_ROOM;


/**
* @author huangyu
* @date 2020-08-10
*/
@Service
//@CacheConfig(cacheNames = "mwWechatLive")
@Slf4j
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = Exception.class)
public class MwWechatLiveServiceImpl extends BaseServiceImpl<MwWechatLiveMapper, MwWechatLive> implements MwWechatLiveService {

    private final IGenerator generator;
    @Value("${file.path}")
    private String uploadDirStr;
    private final MwWechatLiveGoodsService wechatLiveGoodsService;

    private final MwWechatLiveMapper wechatLiveMapper;

    public MwWechatLiveServiceImpl(IGenerator generator, MwWechatLiveGoodsService wechatLiveGoodsService, MwWechatLiveMapper wechatLiveMapper) {
        this.generator = generator;
        this.wechatLiveGoodsService = wechatLiveGoodsService;
        this.wechatLiveMapper = wechatLiveMapper;
    }

    /**
     * 同步直播间
     * @return
     */
    //@Cacheable
    @Override
    public boolean synchroWxOlLive() {
        try {
            WxMaService wxMaService = WxMaConfiguration.getWxMaService();
            List<WxMaLiveResult.RoomInfo> liveInfos = wxMaService.getLiveService().getLiveInfos();
            List<MwWechatLive> convert = generator.convert(liveInfos, MwWechatLive.class);
            this.saveOrUpdateBatch(convert);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return true;
    }
    @Override
    //@Cacheable
    public WechatLiveVo queryAll(MwWechatLiveQueryCriteria criteria, Pageable pageable) {
        String order=null;
        if(pageable.getSort()!=null){
            order= pageable.getSort().toString();
            order=order.replace(":","");
            if("UNSORTED".equals(order)){
                order="start_time desc";
            }
        }
        PageHelper.startPage(pageable.getPageNumber()+1, pageable.getPageSize(),order);
        PageInfo<MwWechatLive> page = new PageInfo<>(queryAll(criteria));
        WechatLiveVo wechatLiveVo = new WechatLiveVo();
//            List<WxMaLiveResult.RoomInfo> liveInfos = wxMaLiveService.getLiveInfos();
        List<MwWechatLiveDto> liveDtos = generator.convert(page.getList(), MwWechatLiveDto.class);
        //获取所有商品
        liveDtos.forEach(i ->{
            if(StringUtils.isNotBlank(i.getProductId())){
                List<MwWechatLiveGoodsDto> wechatLiveGoodsDtos = generator.convert(
                        wechatLiveGoodsService.list(new LambdaQueryWrapper<MwWechatLiveGoods>().in(MwWechatLiveGoods::getGoodsId,i.getProductId().split(",")))
                        , MwWechatLiveGoodsDto.class);
                i.setProduct(wechatLiveGoodsDtos);
            }
            i.setId(i.getRoomId());
        });
        wechatLiveVo.setContent(liveDtos);
        wechatLiveVo.setTotalElements(page.getTotal());
        wechatLiveVo.setPageNumber(page.getPageNum());
        wechatLiveVo.setLastPage(page.getPages());
        return wechatLiveVo;
    }

    @Override
    public boolean addGoods(UpdateGoodsDto resources) {
        MwWechatLive wechatLive = new MwWechatLive();

        WxMaService wxMaService = WxMaConfiguration.getWxMaService();
        if(StringUtils.isNotBlank(resources.getProductId())){
            wechatLive.setRoomId(Long.valueOf(resources.getRoomId()));
            wechatLive.setProductId(resources.getProductId());
            String[] productIds = resources.getProductId().split(",");
            List<Integer> pids = new ArrayList<>();
            for (String productId : productIds) {
                pids.add(Integer.valueOf(productId));
            }
            //添加商品
            try {
                wxMaService.getLiveService().addGoodsToRoom(resources.getRoomId().intValue(), pids);
                this.saveOrUpdate(wechatLive);
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    @Override
    public boolean saveLive(MwWechatLive resources){
        WxMaService wxMaService = WxMaConfiguration.getWxMaService();
        try {
            resources.setFeedsImg(uploadPhotoToWx(wxMaService,resources.getFeedsImg()).getMediaId());
            resources.setStartTime(Long.valueOf(OrderUtil.dateToTimestamp(resources.getStartDate())));
            resources.setEndTime(Long.valueOf(OrderUtil.dateToTimestamp(resources.getEndDate())));
            resources.setAnchorImg(uploadPhotoToWx(wxMaService,resources.getAnchorImge()).getMediaId());
            resources.setCoverImg(uploadPhotoToWx(wxMaService,resources.getCoverImge()).getMediaId());
            resources.setShareImg(uploadPhotoToWx(wxMaService,resources.getShareImge()).getMediaId());
            MwMaLiveInfo.RoomInfo roomInfo = generator.convert(resources, MwMaLiveInfo.RoomInfo.class);
            Integer status = this.createRoom(roomInfo);
            resources.setRoomId(Long.valueOf(status));
            if(StringUtils.isNotBlank(resources.getProductId())){
                String[] productIds = resources.getProductId().split(",");
                List<Integer> pids = new ArrayList<>();
                for (String productId : productIds) {
                    pids.add(Integer.valueOf(productId));
                }
                //添加商品
                wxMaService.getLiveService().addGoodsToRoom(status, pids);
            }
            this.save(resources);
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new BadRequestException(e.toString());
        }
        return false;
    }

    @Override
    //@Cacheable
    public List<MwWechatLive> queryAll(MwWechatLiveQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwWechatLive.class, criteria));
    }

    @Override
    //@Cacheable
    public List<WxMaLiveResult.LiveReplay> getLiveReplay(Integer roomId){
        WxMaService wxMaService = WxMaConfiguration.getWxMaService();
        WxMaLiveResult get_replay = new WxMaLiveResult();
        try {
             get_replay = wxMaService.getLiveService().getLiveReplay("get_replay", roomId, 0, 100);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return get_replay.getLiveReplay();
    }
    @Override
    public void download(List<MwWechatLiveDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwWechatLiveDto mwWechatLive : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("直播间标题", mwWechatLive.getName());
            map.put("背景图", mwWechatLive.getCoverImge());
            map.put("分享图片", mwWechatLive.getShareImge());
            map.put("直播间状态", mwWechatLive.getLiveStatus());
            map.put("开始时间", mwWechatLive.getStartTime());
            map.put("预计结束时间", mwWechatLive.getEndTime());
            map.put("主播昵称", mwWechatLive.getAnchorName());
            map.put("主播微信号", mwWechatLive.getAnchorWechat());
            map.put("主播头像", mwWechatLive.getAnchorImge());
            map.put("直播间类型 1：推流 0：手机直播", mwWechatLive.getType());
            map.put("横屏、竖屏 【1：横屏，0：竖屏】", mwWechatLive.getScreenType());
            map.put("是否关闭货架 【0：开启，1：关闭】", mwWechatLive.getCloseLike());
            map.put("是否关闭评论 【0：开启，1：关闭】", mwWechatLive.getCloseComment());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public Integer createRoom(MwMaLiveInfo.RoomInfo roomInfo) throws WxErrorException {
        WxMaService wxMaService = WxMaConfiguration.getWxMaService();
        String responseContent = wxMaService.post(CREATE_ROOM, WxMaGsonBuilder.create().toJson(roomInfo));
        JsonObject jsonObject = GsonParser.parse(responseContent);
        if (jsonObject.get("errcode").getAsInt() != 0) {
            throw new WxErrorException(WxError.fromJson(responseContent, WxType.MiniApp));
        }
        return jsonObject.get("roomId").getAsInt();
    }
    /**
     * 上传临时素材
     * @param wxMaService WxMaService
     * @param picPath 图片路径
     * @return WxMpMaterialUploadResult
     * @throws WxErrorException
     */
    private WxMediaUploadResult uploadPhotoToWx(WxMaService wxMaService, String picPath) throws WxErrorException {
        String filename = (int) System.currentTimeMillis() + ".png";
        String downloadPath = uploadDirStr + filename;
        long size = HttpUtil.downloadFile(picPath, cn.hutool.core.io.FileUtil.file(downloadPath));
        picPath = downloadPath;
        File picFile = new File( picPath );
        log.info( "picFile name : {}", picFile.getName() );
        WxMediaUploadResult wxMediaUploadResult = wxMaService.getMediaService().uploadMedia( WxConsts.MediaFileType.IMAGE, picFile );
        log.info( "wxMpMaterialUploadResult : {}", JSONUtil.toJsonStr( wxMediaUploadResult ) );
        return wxMediaUploadResult;
    }


    /**
     * 直播间列表
     * @param page 页码
     * @param limit 条数
     * @param order ProductEnum
     * @return List
     */
    @Override
    public List<MwWechatLiveDto> getList(int page, int limit, int order) {
        //todo 添加状态判断
       LambdaQueryWrapper<MwWechatLive> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MwWechatLive::getStartTime);


        Page<MwWechatLive> pageModel = new Page<>(page, limit);

        IPage<MwWechatLive> pageList = wechatLiveMapper.selectPage(pageModel,wrapper);


        return generator.convert(pageList.getRecords(), MwWechatLiveDto.class);
    }


}
