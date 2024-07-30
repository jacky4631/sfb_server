/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.system.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.system.domain.UserAvatar;
import com.mailvor.modules.system.service.dto.UserAvatarDto;
import com.mailvor.modules.system.service.dto.UserAvatarQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-14
*/
public interface UserAvatarService  extends BaseService<UserAvatar>{

/**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(UserAvatarQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<UserAvatarDto>
    */
    List<UserAvatar> queryAll(UserAvatarQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<UserAvatarDto> all, HttpServletResponse response) throws IOException;

    UserAvatar saveFile(UserAvatar userAvatar);
}
