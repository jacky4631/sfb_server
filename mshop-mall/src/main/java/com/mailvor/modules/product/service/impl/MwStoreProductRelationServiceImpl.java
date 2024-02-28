/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.domain.PageResult;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.product.domain.MwStoreProductRelation;
import com.mailvor.modules.product.service.MwStoreProductRelationService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.service.dto.MwStoreProductRelationDto;
import com.mailvor.modules.product.service.dto.MwStoreProductRelationQueryCriteria;
import com.mailvor.modules.product.service.mapper.MwStoreProductRelationMapper;
import com.mailvor.modules.product.vo.MwStoreProductRelationQueryVo;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 商品点赞和收藏表 服务实现类
 * </p>
 *
 * @author mazhongjun
 * @since 2019-10-23
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class MwStoreProductRelationServiceImpl extends BaseServiceImpl<MwStoreProductRelationMapper, MwStoreProductRelation> implements MwStoreProductRelationService {

    private final MwStoreProductRelationMapper mwStoreProductRelationMapper;
    private final MwStoreProductService storeProductService;
    private final MwUserService userService;
    private final IGenerator generator;

    /**
     * 获取用户收藏列表
     * @param page page
     * @param limit limit
     * @param uid 用户id
     * @return list
     */
    @Override
    public Map<String,Object> userCollectProduct(int page, int limit, Long uid,String type) {
//        Page<MwStoreProductRelation> pageModel = new Page<>(page, limit);
//        List<MwStoreProductRelationQueryVo> list = MwStoreProductRelationMapper.selectRelationList(pageModel,uid,type);
//        return list;
        LambdaQueryWrapper<MwStoreProductRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProductRelation::getUid, uid);
        wrapper.eq(MwStoreProductRelation::getType, type);
        wrapper.orderByDesc(MwStoreProductRelation::getCreateTime);

        Page<MwStoreProductRelation> pageModel = new Page<>(page, limit);
        IPage<MwStoreProductRelation> pageList = mwStoreProductRelationMapper.selectPage(pageModel, wrapper);
        List<MwStoreProductRelationQueryVo> list = generator.convert(pageList.getRecords(), MwStoreProductRelationQueryVo.class);
        Map<String, Object> map = new HashMap<>();
        map.put("list", list.stream()
                .collect(Collectors.toList()));
        map.put("total", pageList.getTotal());
        map.put("totalPage", pageList.getPages());
        return map;
    }

    /**
     * 添加收藏
     * @param productId 商品id
     * @param uid 用户id
     */
    @Override
    public void addProductRelation(String productId, long uid, String category
            ,String img, String title, String startPrice, String endPrice, String originalId) {
        if(isProductRelation(productId,uid)) {
            throw new MshopException("已收藏");
        }
        MwStoreProductRelation storeProductRelation = MwStoreProductRelation.builder()
                .productId(productId)
                .originalProductId(originalId)
                .uid(uid)
                .type("collect")
                .img(img)
                .title(title)
                .category(category)
                .startPrice(startPrice)
                .endPrice(endPrice)
                .build();
        mwStoreProductRelationMapper.insert(storeProductRelation);
    }

    @Override
    public void saveOrUpdateRelation(String productId, long uid, String category) {
        //添加足迹
        MwStoreProductRelation foot = getOne(new LambdaQueryWrapper<MwStoreProductRelation>()
                .eq(MwStoreProductRelation::getUid, uid)
                .eq(MwStoreProductRelation::getProductId, productId)
                .eq(MwStoreProductRelation::getType, "foot")
                .eq(MwStoreProductRelation::getCategory, category));

        if (ObjectUtil.isNotNull(foot)) {
            foot.setCreateTime(new Date());
            saveOrUpdate(foot);
        } else {
            MwStoreProductRelation storeProductRelation = new MwStoreProductRelation();
            storeProductRelation.setProductId(productId);
            storeProductRelation.setUid(uid);
            storeProductRelation.setCreateTime(new Date());
            storeProductRelation.setType("foot");
            storeProductRelation.setCategory(category);
            save(storeProductRelation);
        }
    }

    /**
     * 取消收藏
     * @param productId 商品id
     * @param uid 用户id
     */
    @Override
    public void delProductRelation(String productId, long uid) {
        MwStoreProductRelation productRelation = findOne(productId, uid, "collect");
        if(productRelation == null) {
            throw new MshopException("已取消");
        }
        this.removeById(productRelation.getId());
    }
    public MwStoreProductRelation findOne(String productId, long uid, String type) {
        return this.lambdaQuery()
                .eq(MwStoreProductRelation::getProductId,productId)
                .eq(MwStoreProductRelation::getUid,uid)
                .eq(MwStoreProductRelation::getType,type)
                .one();
    }


    /**
     * 是否收藏
     * @param productId 商品ID
     * @param uid 用户ID
     * @return Boolean
     */
    @Override
    public Boolean isProductRelation(String productId, long uid) {
        Long count = mwStoreProductRelationMapper
                .selectCount(Wrappers.<MwStoreProductRelation>lambdaQuery()
                        .eq(MwStoreProductRelation::getUid,uid)
                        .eq(MwStoreProductRelation::getType,"collect")
                        .eq(MwStoreProductRelation::getProductId,productId));
        if(count > 0) {
            return true;
        }

        return false;
    }

    @Override
    //@Cacheable
    public PageResult<MwStoreProductRelationDto> queryAll(MwStoreProductRelationQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreProductRelation> page = new PageInfo<>(queryAll(criteria));
        PageResult<MwStoreProductRelationDto> relationDtoPageResult = generator.convertPageInfo(page, MwStoreProductRelationDto.class);
        relationDtoPageResult.getContent().forEach(i ->{
            i.setProduct(storeProductService.getById(i.getProductId()));
            i.setUserName(userService.getMwUserById(i.getUid()).getNickname());
        });
        return relationDtoPageResult;
    }


    @Override
    //@Cacheable
    public List<MwStoreProductRelation> queryAll(MwStoreProductRelationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreProductRelation.class, criteria));
    }


    @Override
    public void download(List<MwStoreProductRelationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreProductRelationDto mwStoreProductRelation : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户ID", mwStoreProductRelation.getUid());
            map.put("商品ID", mwStoreProductRelation.getProductId());
            map.put("类型(收藏(collect）、点赞(like))", mwStoreProductRelation.getType());
            map.put("某种类型的商品(普通商品、秒杀商品)", mwStoreProductRelation.getCategory());
            map.put("添加时间", mwStoreProductRelation.getCreateTime());
            map.put(" updateTime",  mwStoreProductRelation.getUpdateTime());
            map.put(" isDel",  mwStoreProductRelation.getIsDel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void collectDelFoot(List<Long> ids) {
        mwStoreProductRelationMapper.deleteBatchIds(ids);
    }

    @Override
    //@Cacheable
    public List<MwStoreProductRelation> queryAll(Integer interval, String category, String type) {
        LambdaQueryWrapper<MwStoreProductRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProductRelation::getCategory, category);
        wrapper.eq(MwStoreProductRelation::getType, type);
        LocalDateTime now = LocalDateTime.now().minusMinutes(interval);
        //查找interval分之前的订单
        Date date = Date.from(now.atZone( ZoneId.systemDefault()).toInstant());
        wrapper.and(wq->wq.ge(MwStoreProductRelation::getCreateTime, date)
                        .or()
                        .ge(MwStoreProductRelation::getUpdateTime, date)
                );
        return mwStoreProductRelationMapper.selectList(wrapper);
    }

}
