/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.dto.WechatUserDto;

import java.util.List;

/**
* @author huangyu
* @date 2023-05-12
*/
public interface MwUserUnionService extends BaseService<MwUserUnion>{
    List<MwUserUnion> queryAllContainsChannelId();

    MwUserUnion getOne(Long uid);
    MwUserUnion getByOpenId(String openId);
    void save(Long uid, WechatUserDto wechatUserDto);

    void update(MwUserUnion userUnion, WechatUserDto wechatUserDto);

    void remove(Long uid);

    List<MwUserUnion> getByUid(Long uid);

    List<MwUserUnion> listByTbPid(List<String> pids);
}
