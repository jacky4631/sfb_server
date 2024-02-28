/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;



import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserSign;
import com.mailvor.modules.user.vo.SignVo;
import com.mailvor.modules.user.vo.MwUserQueryVo;

import java.util.List;

/**
 * <p>
 * 签到记录表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-05
 */
public interface MwUserSignService extends BaseService<MwUserSign> {

    /**
     *
     * @param mwUser 用户
     * @return 签到积分
     */
    int sign(MwUser mwUser);

    /**
     * 分页获取用户签到数据
     * @param uid 用户id
     * @param page  page
     * @param limit limit
     * @return list
     */
    List<SignVo> getSignList(Long uid, int page, int limit);

    //boolean getYesterDayIsSign(int uid);

    //boolean getToDayIsSign(int uid);

    //int getSignSumDay(int uid);

    /**
     * 获取签到用户信息
     * @param mwUser  mwUser
     * @return MwUserQueryVo
     */
    MwUserQueryVo userSignInfo(MwUser mwUser);


}
