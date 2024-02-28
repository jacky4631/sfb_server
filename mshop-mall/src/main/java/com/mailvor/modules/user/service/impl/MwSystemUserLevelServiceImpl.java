/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.shop.domain.MwSystemUserLevel;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwSystemUserLevelService;
import com.mailvor.modules.user.service.dto.MwSystemUserLevelDto;
import com.mailvor.modules.user.service.dto.MwSystemUserLevelQueryCriteria;
import com.mailvor.modules.user.service.dto.UserLevelDto;
import com.mailvor.modules.user.service.mapper.SystemUserLevelMapper;
import com.mailvor.modules.user.vo.MwSystemUserLevelQueryVo;
import com.mailvor.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwSystemUserLevelServiceImpl extends BaseServiceImpl<SystemUserLevelMapper, MwSystemUserLevel> implements MwSystemUserLevelService {

    @Autowired
    private IGenerator generator;

    /**
     * 获取当前的下一个会员等级id
     * @param levelId 等级id
     * @return int
     */
    @Override
    public int getNextLevelId(int levelId) {
        List<MwSystemUserLevel> list = this.lambdaQuery()
                .orderByAsc(MwSystemUserLevel::getGrade)
                .list();

        int grade = 0;
        for (MwSystemUserLevel userLevel : list) {
            if(userLevel.getId() == levelId) {
                grade = userLevel.getGrade();
            }
        }

        MwSystemUserLevel userLevel = this.lambdaQuery()
                .eq(MwSystemUserLevel::getIsShow,ShopCommonEnum.SHOW_1.getValue())
                .orderByAsc(MwSystemUserLevel::getGrade)
                .gt(MwSystemUserLevel::getGrade,grade)
                .last("limit 1")
                .one();
        if(ObjectUtil.isNull(userLevel)) {
            return 0;
        }
        return userLevel.getId();
    }

//    @Override
//    public boolean getClear(int levelId) {
//        List<MWSystemUserLevelQueryVo> systemUserLevelQueryVos = this.getLevelListAndGrade(levelId);
//        for (MWSystemUserLevelQueryVo userLevelQueryVo : systemUserLevelQueryVos) {
//            if(userLevelQueryVo.getId() == levelId) return userLevelQueryVo.getIsClear();
//        }
//        return false;
//    }



    /**
     * 获取会员等级列表及其任务列表
     * @return UserLevelDto
     */
    @Override
    public UserLevelDto getLevelInfo(String platform) {
        //会员等级列表
        List<MwSystemUserLevelQueryVo> list;
        if(platform == null) {
            list = this.getLevelListAndGrade();
        } else {
            list = this.getLevelListAndGrade(platform);
        }
        if(list.isEmpty()) {
            throw new MshopException("请后台设置会员等级");
        }

        UserLevelDto userLevelDTO = new UserLevelDto();
        userLevelDTO.setList(list);

        return userLevelDTO;
    }

    /**
     * 获取会员等级列表
     * @return list
     */
    public List<MwSystemUserLevelQueryVo> getLevelListAndGrade(String platform) {
        List<MwSystemUserLevel> list = this.lambdaQuery()
                .eq(MwSystemUserLevel::getType, platform)
                .orderByAsc(MwSystemUserLevel::getGrade)
                .list();
        return generator.convert(list, MwSystemUserLevelQueryVo.class);
    }

    @Override
    public MwSystemUserLevel getLevel(String platform, String rechargeId) {
        return this.lambdaQuery()
                .eq(MwSystemUserLevel::getType, platform)
                .eq(MwSystemUserLevel::getRechargeId, rechargeId)
                .one();
    }

    /**
     * 获取会员等级列表
     * @return list
     */
    public List<MwSystemUserLevelQueryVo> getLevelListAndGrade() {
        List<MwSystemUserLevel> list = this.lambdaQuery()
                .eq(MwSystemUserLevel::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .orderByAsc(MwSystemUserLevel::getGrade)
                .list();
        return generator.convert(list, MwSystemUserLevelQueryVo.class);
    }
    //=========================================================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwSystemUserLevelQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwSystemUserLevel> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwSystemUserLevelDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwSystemUserLevel> queryAll(MwSystemUserLevelQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwSystemUserLevel.class, criteria));
    }


    @Override
    public void download(List<MwSystemUserLevelDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwSystemUserLevelDto mwSystemUserLevel : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商户id", mwSystemUserLevel.getMerId());
            map.put("会员名称", mwSystemUserLevel.getName());
            map.put("购买金额", mwSystemUserLevel.getMoney());
            map.put("有效时间", mwSystemUserLevel.getValidDate());
            map.put("是否为永久会员", mwSystemUserLevel.getIsForever());
            map.put("是否购买,1=购买,0=不购买", mwSystemUserLevel.getIsPay());
            map.put("是否显示 1=显示,0=隐藏", mwSystemUserLevel.getIsShow());
            map.put("会员等级", mwSystemUserLevel.getGrade());
            map.put("享受折扣", mwSystemUserLevel.getDiscount());
            map.put("会员卡背景", mwSystemUserLevel.getImage());
            map.put("会员图标", mwSystemUserLevel.getIcon());
            map.put("说明", mwSystemUserLevel.getExplain());
            map.put("添加时间", mwSystemUserLevel.getAddTime());
            map.put("是否删除.1=删除,0=未删除", mwSystemUserLevel.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 获取会员等级详情
     * @param grade 等级
     * @return MwSystemUserLevel
     */
    @Override
    public MwSystemUserLevel getSystemLevelInfo(Integer grade) {
        return getSystemLevelInfo(grade, "tb");
    }
    @Override
    public MwSystemUserLevel getSystemLevelInfo(Integer grade, String type) {
        return this.lambdaQuery()
                .eq(MwSystemUserLevel::getGrade, grade)
                .eq(MwSystemUserLevel::getType, type)
                .one();
    }
    @Override
    public List<MwSystemUserLevel> getSystemLevels(List<Integer> grades) {
        return this.lambdaQuery()
                .eq(MwSystemUserLevel::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .in(MwSystemUserLevel::getGrade, grades)
                .list();
    }

    @Override
    public MwSystemUserLevel getUserLevel(MwUser user, String platform) {
        if(user == null) {
            return null;
        }
        Integer curLevel;
        if("pdd".equals(platform) && user.getLevelPdd() > 0) {
            curLevel = user.getLevelPdd();
        }else if("jd".equals(platform) && user.getLevelJd() > 0) {
            curLevel = user.getLevelJd();
        }else if("dy".equals(platform) && user.getLevelDy() > 0) {
            curLevel = user.getLevelDy();
        }else if("vip".equals(platform) && user.getLevelVip() > 0) {
            curLevel = user.getLevelVip();
        } else {
            //当其他平台会员不存在时，默认读取tb会员
            curLevel = user.getLevel();
            platform = "tb";
        }

        MwSystemUserLevel userLevel = getSystemLevelInfo(curLevel, platform);
        //防止后台误删其他平台，导致会员不存在后，读取tb会员
        if(userLevel == null) {
            userLevel = getSystemLevelInfo(user.getLevel());
        }
        return userLevel;
    }

    @Override
    public List<MwSystemUserLevel> getPlatformLevels(String platform) {

        return this.lambdaQuery()
                .eq(MwSystemUserLevel::getType, platform)
                .list();
    }
}
