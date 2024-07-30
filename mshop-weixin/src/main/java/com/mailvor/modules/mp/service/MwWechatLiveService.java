/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.service;

import cn.binarywang.wx.miniapp.bean.live.WxMaLiveResult;
import com.mailvor.common.service.BaseService;
import com.mailvor.modules.mp.service.dto.UpdateGoodsDto;
import com.mailvor.modules.mp.service.dto.MwMaLiveInfo;
import com.mailvor.modules.mp.service.dto.MwWechatLiveDto;
import com.mailvor.modules.mp.service.dto.MwWechatLiveQueryCriteria;
import com.mailvor.modules.mp.vo.WechatLiveVo;
import com.mailvor.modules.mp.domain.MwWechatLive;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @author mazhongjun
* @date 2020-08-10
*/
public interface MwWechatLiveService extends BaseService<MwWechatLive>{




    /**
     * 同步直播间
     * @return
     */
    boolean synchroWxOlLive();

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    WechatLiveVo queryAll(MwWechatLiveQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwWechatLiveDto>
    */
    List<MwWechatLive> queryAll(MwWechatLiveQueryCriteria criteria);


    boolean saveLive(MwWechatLive resources);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwWechatLiveDto> all, HttpServletResponse response) throws IOException;


    /**
     * 创建直播间
     * <pre>
     * 调用此接口创建直播间，创建成功后将在直播间列表展示，调用额度：10000次/一天
     * 文档地址：https://developers.weixin.qq.com/miniprogram/dev/framework/liveplayer/studio-api.html#1
     * http请求方式：POST https://api.weixin.qq.com/wxaapi/broadcast/room/create?access_token=ACCESS_TOKEN
     * </pre>
     *
     * @param roomInfo 直播间信息
     * @return .
     * @throws WxErrorException .
     */
    Integer createRoom(MwMaLiveInfo.RoomInfo roomInfo) throws WxErrorException;

    /**
     * 获取直播回放
     * @param roomId
     * @return
     */
     List<WxMaLiveResult.LiveReplay> getLiveReplay(Integer roomId);


    /**
     * 商品列表
     * @param page 页码
     * @param limit 条数
     * @param order ProductEnum
     * @return List
     */
    List<MwWechatLiveDto> getList(int page, int limit, int order);

    boolean addGoods(UpdateGoodsDto resources);
}
