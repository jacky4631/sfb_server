/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service;


import com.mailvor.common.service.BaseService;
import com.mailvor.modules.activity.domain.MwStoreBargainUserHelp;
import com.mailvor.modules.activity.vo.MwStoreBargainUserHelpQueryVo;

import java.util.List;

/**
 * <p>
 * 砍价用户帮助表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-12-21
 */
public interface MwStoreBargainUserHelpService extends BaseService<MwStoreBargainUserHelp> {

    /**
     * 获取砍价帮
     * @param bargainId 砍价商品id
     * @param bargainUserUid 砍价用户id
     * @param page page
     * @param limit limit
     * @return list
     */
    List<MwStoreBargainUserHelpQueryVo> getList(Long bargainId, Long bargainUserUid, int page, int limit);

    /**
     * 获取砍价帮总人数
     *
     * @param bargainId      砍价产品ID
     * @param bargainUserUid 用户参与砍价表id
     * @return int
     */
    Long getBargainUserHelpPeopleCount(Long bargainId,Long bargainUserUid);


}
