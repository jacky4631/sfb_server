/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.template.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.domain.MwShippingTemplatesFree;
import com.mailvor.modules.template.domain.MwShippingTemplatesRegion;
import com.mailvor.modules.template.service.MwShippingTemplatesFreeService;
import com.mailvor.modules.template.service.MwShippingTemplatesRegionService;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.mailvor.modules.template.service.dto.AppointInfoDto;
import com.mailvor.modules.template.service.dto.RegionChildrenDto;
import com.mailvor.modules.template.service.dto.RegionDto;
import com.mailvor.modules.template.service.dto.RegionInfoDto;
import com.mailvor.modules.template.service.dto.ShippingTemplatesDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesDto;
import com.mailvor.modules.template.service.dto.MwShippingTemplatesQueryCriteria;
import com.mailvor.modules.template.service.mapper.MwShippingTemplatesMapper;
import com.mailvor.utils.FileUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
* @author mazhongjun
* @date 2020-06-29
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwShippingTemplatesServiceImpl extends BaseServiceImpl<MwShippingTemplatesMapper, MwShippingTemplates> implements MwShippingTemplatesService {

    private final IGenerator generator;


    private final MwShippingTemplatesRegionService mwShippingTemplatesRegionService;
    private final MwShippingTemplatesFreeService mwShippingTemplatesFreeService;


    /**
     * 新增与更新模板
     * @param id 模板id
     * @param shippingTemplatesDto ShippingTemplatesDto
     */
    @Override
    public void addAndUpdate(Integer id,ShippingTemplatesDto shippingTemplatesDto) {
        if(ShopCommonEnum.ENABLE_1.getValue().equals(shippingTemplatesDto.getAppoint())
                && shippingTemplatesDto.getAppointInfo().isEmpty()){
            throw new MshopException("请指定包邮地区");
        }
        MwShippingTemplates shippingTemplates = new MwShippingTemplates();
        BeanUtil.copyProperties(shippingTemplatesDto,shippingTemplates);
        shippingTemplates.setRegionInfo(JSON.toJSONString(shippingTemplatesDto.getRegionInfo()));
        shippingTemplates.setAppointInfo(JSON.toJSONString(shippingTemplatesDto.getAppointInfo()));
        if(id != null && id > 0){
            shippingTemplates.setId(id);
            this.updateById(shippingTemplates);
        }else{
            this.save(shippingTemplates);
        }

        this.saveRegion(shippingTemplatesDto,shippingTemplates.getId());
        this.saveFreeReigion(shippingTemplatesDto,shippingTemplates.getId());
    }

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwShippingTemplatesQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwShippingTemplates> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwShippingTemplatesDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwShippingTemplates> queryAll(MwShippingTemplatesQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwShippingTemplates.class, criteria));
    }


    @Override
    public void download(List<MwShippingTemplatesDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwShippingTemplatesDto mwShippingTemplates : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("模板名称", mwShippingTemplates.getName());
            map.put("计费方式", mwShippingTemplates.getType());
            map.put("地域以及费用", mwShippingTemplates.getRegionInfo());
            map.put("指定包邮开关", mwShippingTemplates.getAppoint());
            map.put("指定包邮内容", mwShippingTemplates.getAppointInfo());
            map.put("添加时间", mwShippingTemplates.getCreateTime());
            map.put(" updateTime",  mwShippingTemplates.getUpdateTime());
            map.put(" isDel",  mwShippingTemplates.getIsDel());
            map.put("排序", mwShippingTemplates.getSort());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 保存包邮区域
     * @param mwShippingTemplates ShippingTemplatesDto
     * @param tempId 模板id
     */
    private void saveFreeReigion(ShippingTemplatesDto mwShippingTemplates,Integer tempId){

        if(mwShippingTemplates.getAppointInfo() == null
                || mwShippingTemplates.getAppointInfo().isEmpty()){
            return;
        }

        Long count = mwShippingTemplatesFreeService.count(Wrappers
                .<MwShippingTemplatesFree>lambdaQuery()
                .eq(MwShippingTemplatesFree::getTempId,tempId));
        if(count > 0) {
            mwShippingTemplatesFreeService.remove(Wrappers
                    .<MwShippingTemplatesFree>lambdaQuery()
                    .eq(MwShippingTemplatesFree::getTempId,tempId));
        }

        List<MwShippingTemplatesFree> shippingTemplatesFrees = new ArrayList<>();


        List<AppointInfoDto> appointInfo = mwShippingTemplates.getAppointInfo();
        for (AppointInfoDto appointInfoDto : appointInfo){
            String uni = IdUtil.simpleUUID();

            if(appointInfoDto.getPlace() != null && !appointInfoDto.getPlace().isEmpty()){
                for (RegionDto regionDto : appointInfoDto.getPlace()){
                    if(regionDto.getChildren() != null && !regionDto.getChildren().isEmpty()){
                        for (RegionChildrenDto childrenDto : regionDto.getChildren()){
                            MwShippingTemplatesFree shippingTemplatesFree = MwShippingTemplatesFree.builder()
                                    .tempId(tempId)
                                    .number(new BigDecimal(appointInfoDto.getA_num()))
                                    .price(new BigDecimal(appointInfoDto.getA_price()))
                                    .type(mwShippingTemplates.getType())
                                    .uniqid(uni)
                                    .provinceId(Integer.valueOf(regionDto.getCity_id()))
                                    .cityId(Integer.valueOf(childrenDto.getCity_id()))
                                    .build();
                            shippingTemplatesFrees.add(shippingTemplatesFree);
                        }
                    }
                }
            }
        }


        if(shippingTemplatesFrees.isEmpty()) {
            throw new MshopException("请添加包邮区域");
        }

        mwShippingTemplatesFreeService.saveBatch(shippingTemplatesFrees);


    }

    /**
     * 保存模板设置的区域价格
     * @param mwShippingTemplates ShippingTemplatesDTO
     * @param tempId 运费模板id
     */
    private void saveRegion(ShippingTemplatesDto mwShippingTemplates,Integer tempId){
        Long count = mwShippingTemplatesRegionService.count(Wrappers
                .<MwShippingTemplatesRegion>lambdaQuery()
                .eq(MwShippingTemplatesRegion::getTempId,tempId));
        if(count > 0) {
            mwShippingTemplatesRegionService.remove(Wrappers
                    .<MwShippingTemplatesRegion>lambdaQuery()
                    .eq(MwShippingTemplatesRegion::getTempId,tempId));
        }

        List<MwShippingTemplatesRegion> shippingTemplatesRegions = new ArrayList<>();


        List<RegionInfoDto> regionInfo = mwShippingTemplates.getRegionInfo();


        for (RegionInfoDto regionInfoDto : regionInfo){
            String uni = IdUtil.simpleUUID();
            if(regionInfoDto.getRegion() != null && !regionInfoDto.getRegion().isEmpty()){
                for (RegionDto regionDto : regionInfoDto.getRegion()){
                    if(regionDto.getChildren() != null && !regionDto.getChildren().isEmpty()){
                        for (RegionChildrenDto childrenDtp : regionDto.getChildren()){
                            MwShippingTemplatesRegion shippingTemplatesRegion = MwShippingTemplatesRegion.builder()
                                    .tempId(tempId)
                                    .first(new BigDecimal(regionInfoDto.getFirst()))
                                    .firstPrice(new BigDecimal(regionInfoDto.getPrice()))
                                    .continues(new BigDecimal(regionInfoDto.get_continue()))
                                    .continuePrice(new BigDecimal(regionInfoDto.getContinue_price()))
                                    .type(mwShippingTemplates.getType())
                                    .uniqid(uni)
                                    .provinceId(Integer.valueOf(regionDto.getCity_id()))
                                    .cityId(Integer.valueOf(childrenDtp.getCity_id()))
                                    .build();
                            shippingTemplatesRegions.add(shippingTemplatesRegion);
                        }
                    }else{
                        MwShippingTemplatesRegion shippingTemplatesRegion = MwShippingTemplatesRegion.builder()
                                .tempId(tempId)
                                .first(new BigDecimal(regionInfoDto.getFirst()))
                                .firstPrice(new BigDecimal(regionInfoDto.getPrice()))
                                .continues(new BigDecimal(regionInfoDto.get_continue()))
                                .continuePrice(new BigDecimal(regionInfoDto.getContinue_price()))
                                .type(mwShippingTemplates.getType())
                                .uniqid(uni)
                                .provinceId(Integer.valueOf(regionDto.getCity_id()))
                                .build();
                        shippingTemplatesRegions.add(shippingTemplatesRegion);
                    }
                }
            }
        }

        if(shippingTemplatesRegions.isEmpty()) {
            throw new BusinessException("请添加区域");
        }

        mwShippingTemplatesRegionService.saveBatch(shippingTemplatesRegions);

    }


}
