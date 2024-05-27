/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.feedback.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.feedback.domain.MwUserFeedback;
import com.mailvor.utils.FileUtil;
import lombok.AllArgsConstructor;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mailvor.modules.feedback.service.MwUserFeedbackService;
import com.mailvor.modules.feedback.service.dto.MwUserFeedbackDto;
import com.mailvor.modules.feedback.service.dto.MwUserFeedbackQueryCriteria;
import com.mailvor.modules.feedback.service.mapper.MwUserFeedbackMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.mailvor.utils.ShopUtil.getPage;

/**
* @author wangjun
* @date 2024-05-27
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "mwUserFeedback")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserFeedbackServiceImpl extends BaseServiceImpl<MwUserFeedbackMapper, MwUserFeedback> implements MwUserFeedbackService {

    private final IGenerator generator;

    @Override
    //@Cacheable
    public PageResult<MwUserFeedbackDto> queryAll(MwUserFeedbackQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserFeedback> page = new PageInfo<>(queryAll(criteria));
        return generator.convertPageInfo(page,MwUserFeedbackDto.class);
    }


    @Override
    //@Cacheable
    public List<MwUserFeedback> queryAll(MwUserFeedbackQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUserFeedback.class, criteria));
    }


    @Override
    public void download(List<MwUserFeedbackDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwUserFeedbackDto mwUserFeedback : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户id", mwUserFeedback.getUid());
            map.put("反馈内容", mwUserFeedback.getFeedback());
            map.put("添加时间", mwUserFeedback.getCreateTime());
            map.put("更新时间", mwUserFeedback.getUpdateTime());
            map.put(" isDel",  mwUserFeedback.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
