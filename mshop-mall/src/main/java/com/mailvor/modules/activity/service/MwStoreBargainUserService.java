/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;


import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreBargainUser;
import com.mailvor.modules.activity.vo.MwStoreBargainUserQueryVo;

import java.util.List;

/**
 * <p>
 * 用户参与砍价表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-21
 */
public interface MwStoreBargainUserService extends BaseService<MwStoreBargainUser> {

    /**
     * 修改用户砍价状态
     * @param bargainId 砍价产品id
     * @param uid 用户id
     */
    void setBargainUserStatus(Long bargainId, Long uid);

    /**
     * 砍价取消
     * @param bargainId 砍价商品id
     * @param uid uid
     */
    void bargainCancel(Long bargainId,Long uid);

    /**
     * 获取用户的砍价产品
     * @param bargainUserUid 用户id
     * @param page page
     * @param limit limit
     * @return List
     */
    List<MwStoreBargainUserQueryVo> bargainUserList(Long bargainUserUid, int page, int limit);

    /**
     * 判断用户是否还可以砍价
     * @param bargainId 砍价产品id
     * @param bargainUserUid 开启砍价用户id
     * @param uid  当前用户id
     * @return false=NO true=YES
     */
    boolean isBargainUserHelp(Long bargainId,Long bargainUserUid,Long uid);

    /**
     * 添加砍价记录
     * @param bargainId 砍价商品id
     * @param uid 用户id
     */
    void setBargain(Long bargainId,Long uid);

    //double getBargainUserDiffPrice(int id);


    /**
     * 获取某个用户参与砍价信息
     * @param bargainId 砍价id
     * @param uid 用户id
     * @return  MwStoreBargainUser
     */
    MwStoreBargainUser getBargainUserInfo(Long bargainId, Long uid);

    //List<MwStoreBargainUserQueryVo> getBargainUserList(int bargainId,int status);

    /**
     * 获取参与砍价的用户数量
     *
     * @param bargainId 砍价id
     * @param status    状态  OrderInfoEnum 1 进行中  2 结束失败  3结束成功
     * @return int
     */
    Long getBargainUserCount(Long bargainId,Integer status);


}
