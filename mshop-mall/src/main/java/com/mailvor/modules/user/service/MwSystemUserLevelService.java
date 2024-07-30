/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.dto.UserLevelDto;
import com.mailvor.modules.user.service.dto.MwSystemUserLevelDto;
import com.mailvor.modules.user.service.dto.MwSystemUserLevelQueryCriteria;
import com.mailvor.modules.user.vo.MwSystemUserLevelQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwSystemUserLevelService extends BaseService<MwSystemUserLevel>{


    /**
     * 获取当前的下一个会员等级id
     * @param levelId 等级id
     * @return int
     */
    int getNextLevelId(int levelId);

    //boolean getClear(int levelId);


    /**
     * 获取会员等级列表及其任务列表
     * @return UserLevelDto
     */
    UserLevelDto getLevelInfo(String platform);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwSystemUserLevelQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwSystemUserLevelDto>
    */
    List<MwSystemUserLevel> queryAll(MwSystemUserLevelQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwSystemUserLevelDto> all, HttpServletResponse response) throws IOException;

    List<MwSystemUserLevelQueryVo> getLevelListAndGrade(String platform);

    List<MwSystemUserLevelQueryVo> getLevelListAndGrade();

    MwSystemUserLevel getSystemLevelInfo(Integer grade);

    MwSystemUserLevel getSystemLevelInfo(Integer grade, String type);

    MwSystemUserLevel getLevel(String platform, String rechargeId);

    List<MwSystemUserLevel> getSystemLevels(List<Integer> grades);

    MwSystemUserLevel getUserLevel(MwUser user, String platform);

    List<MwSystemUserLevel> getPlatformLevels(String platform);
}
