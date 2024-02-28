/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.BusinessException;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ProductTypeEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.product.domain.MwStoreProductAttr;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrResultService;
import com.mailvor.modules.product.service.MwStoreProductAttrService;
import com.mailvor.modules.product.service.MwStoreProductAttrValueService;
import com.mailvor.modules.product.service.dto.AttrValueDto;
import com.mailvor.modules.product.service.dto.FromatDetailDto;
import com.mailvor.modules.product.service.dto.ProductFormatDto;
import com.mailvor.modules.product.service.mapper.StoreProductAttrMapper;
import com.mailvor.modules.product.service.mapper.StoreProductAttrValueMapper;
import com.mailvor.modules.product.vo.MwStoreProductAttrQueryVo;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MwStoreProductAttrServiceImpl extends BaseServiceImpl<StoreProductAttrMapper, MwStoreProductAttr> implements MwStoreProductAttrService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private StoreProductAttrMapper mwStoreProductAttrMapper;
    @Autowired
    private StoreProductAttrValueMapper mwStoreProductAttrValueMapper;

    @Autowired
    private MwStoreProductAttrValueService storeProductAttrValueService;
    @Autowired
    private MwStoreProductAttrResultService storeProductAttrResultService;

    /**
     * 新增商品属性
     * @param items attr
     * @param attrs value
     * @param productId 商品id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertMwStoreProductAttr(List<FromatDetailDto> items, List<ProductFormatDto> attrs,
                                         Long productId)
    {
        List<MwStoreProductAttr> attrGroup = new ArrayList<>();
        for (FromatDetailDto fromatDetailDto : items) {
            MwStoreProductAttr mwStoreProductAttr = MwStoreProductAttr.builder()
                    .productId(productId)
                    .attrName(fromatDetailDto.getValue())
                    .attrValues(StrUtil.join(",",fromatDetailDto.getDetail()))
                    .build();

            attrGroup.add(mwStoreProductAttr);
        }

        /*int count = storeProductAttrValueService.count(Wrappers.<MwStoreProductAttrValue>lambdaQuery().eq(MwStoreProductAttrValue::getProductId, productId));
        if (count > 0 ) {
            throw new BadRequestException("该产品已被添加到其他活动,禁止操作!");
        }*/

        List<MwStoreProductAttrValue> valueGroup = new ArrayList<>();
        for (ProductFormatDto productFormatDto : attrs) {

            if(productFormatDto.getPinkStock()>productFormatDto.getStock() || productFormatDto.getSeckillStock()>productFormatDto.getStock()){
                throw new BadRequestException("活动商品库存不能大于原有商品库存");
            }
            List<String> stringList = new ArrayList<>(productFormatDto.getDetail().values());
            Collections.sort(stringList);

            MwStoreProductAttrValue mwStoreProductAttrValue = MwStoreProductAttrValue.builder()
                    .productId(productId)
                    .sku(StrUtil.join(",",stringList))
                    .price(BigDecimal.valueOf(productFormatDto.getPrice()))
                    .cost(BigDecimal.valueOf(productFormatDto.getCost()))
                    .otPrice(BigDecimal.valueOf(productFormatDto.getOtPrice()))
                    .unique(IdUtil.simpleUUID())
                    .image(productFormatDto.getPic())
                    .barCode(productFormatDto.getBarCode())
                    .weight(BigDecimal.valueOf(productFormatDto.getWeight()))
                    .volume(BigDecimal.valueOf(productFormatDto.getVolume()))
                    .brokerage(BigDecimal.valueOf(productFormatDto.getBrokerage()))
                    .brokerageTwo(BigDecimal.valueOf(productFormatDto.getBrokerageTwo()))
                    .stock(productFormatDto.getStock())
                    .integral(productFormatDto.getIntegral())
                    .pinkPrice(BigDecimal.valueOf(productFormatDto.getPinkPrice()==null?0:productFormatDto.getPinkPrice()))
                    .seckillPrice(BigDecimal.valueOf(productFormatDto.getSeckillPrice()==null?0:productFormatDto.getSeckillPrice()))
                    .pinkStock(productFormatDto.getPinkStock()==null?0:productFormatDto.getPinkStock())
                    .seckillStock(productFormatDto.getSeckillStock()==null?0:productFormatDto.getSeckillStock())
                    .build();


            valueGroup.add(mwStoreProductAttrValue);
        }

        if(attrGroup.isEmpty() || valueGroup.isEmpty()){
            throw new BusinessException("请设置至少一个属性!");
        }

        //清理属性
        this.clearProductAttr(productId);

        //批量添加
        this.saveBatch(attrGroup);
        storeProductAttrValueService.saveBatch(valueGroup);

        Map<String,Object> map = new LinkedHashMap<>();
        map.put("attr",items);
        map.put("value",attrs);

        storeProductAttrResultService.insertMwStoreProductAttrResult(map,productId);
    }

    /**
     * 删除MwStoreProductAttrValue表的属性
     * @param productId 商品id
     */
    private void clearProductAttr(Long productId) {
        if(ObjectUtil.isNull(productId)) {
            throw new MshopException("产品不存在");
        }

        mwStoreProductAttrMapper.delete(Wrappers.<MwStoreProductAttr>lambdaQuery()
                .eq(MwStoreProductAttr::getProductId,productId));
        mwStoreProductAttrValueMapper.delete(Wrappers.<MwStoreProductAttrValue>lambdaQuery()
                .eq(MwStoreProductAttrValue::getProductId,productId));

    }


    /**
     * 增加库存减去销量
     * @param num 数量
     * @param productId 商品id
     * @param unique sku唯一值
     */
    @Override
    public void incProductAttrStock(Integer num, Long productId, String unique, String type ) {

        if(ProductTypeEnum.COMBINATION.getValue().equals(type)){
           mwStoreProductAttrValueMapper.incCombinationStockDecSales(num,productId,unique);
        }else if(ProductTypeEnum.SECKILL.getValue().equals(type)){
           mwStoreProductAttrValueMapper.incSeckillStockDecSales(num,productId,unique);
        }else {
            mwStoreProductAttrValueMapper.incStockDecSales(num,productId,unique);
        }
    }

    /**
     * 减少库存增加销量（针对sku操作）
     * @param num 数量
     * @param productId 商品id
     */
    @Override
    public void decProductAttrStock(int num, Long productId, String type) {
        int res =  mwStoreProductAttrValueMapper.decStockIncSales(num,productId);
        if(res == 0) {
            throw new MshopException("商品库存不足");
        }
    }



    /**
     * 更加sku 唯一值获取sku对象
     * @param unique 唯一值
     * @return MwStoreProductAttrValue
     */
    @Override
    public MwStoreProductAttrValue uniqueByAttrInfo(String unique) {
        return mwStoreProductAttrValueMapper.selectOne(Wrappers.<MwStoreProductAttrValue>lambdaQuery()
                .eq(MwStoreProductAttrValue::getUnique,unique));
    }


    /**
     * 获取商品sku属性
     * @param productId 商品id
     * @return map
     */
    @Override
    public Map<String, Object> getProductAttrDetail(long productId) {

        List<MwStoreProductAttr>  storeProductAttrs = mwStoreProductAttrMapper
                .selectList(Wrappers.<MwStoreProductAttr>lambdaQuery()
                        .eq(MwStoreProductAttr::getProductId,productId)
                        .orderByAsc(MwStoreProductAttr::getAttrValues));

        List<MwStoreProductAttrValue>  productAttrValues = storeProductAttrValueService
                .list(Wrappers.<MwStoreProductAttrValue>lambdaQuery()
                        .eq(MwStoreProductAttrValue::getProductId,productId));


        Map<String, MwStoreProductAttrValue> map = productAttrValues.stream()
                .collect(Collectors.toMap(MwStoreProductAttrValue::getSku, p -> p));

        List<MwStoreProductAttrQueryVo> mwStoreProductAttrQueryVoList = new ArrayList<>();

        for (MwStoreProductAttr attr : storeProductAttrs) {
            List<String> stringList = Arrays.asList(attr.getAttrValues().split(","));
            List<AttrValueDto> attrValueDTOS = new ArrayList<>();
            for (String str : stringList) {
                AttrValueDto attrValueDTO = new AttrValueDto();
                attrValueDTO.setAttr(str);
                attrValueDTOS.add(attrValueDTO);
            }
            MwStoreProductAttrQueryVo attrQueryVo = generator.convert(attr, MwStoreProductAttrQueryVo.class);
            attrQueryVo.setAttrValue(attrValueDTOS);
            attrQueryVo.setAttrValueArr(stringList);

            mwStoreProductAttrQueryVoList.add(attrQueryVo);
        }

        Map<String, Object> returnMap = new LinkedHashMap<>(2);
        returnMap.put("productAttr",mwStoreProductAttrQueryVoList);
        returnMap.put("productValue",map);

        return returnMap;
    }

}
