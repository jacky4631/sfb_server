/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.enums.SpecTypeEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.activity.domain.MwStoreSeckill;
import com.mailvor.modules.activity.service.MwStoreSeckillService;
import com.mailvor.modules.activity.service.dto.MwStoreSeckillDto;
import com.mailvor.modules.activity.service.dto.MwStoreSeckillQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreSeckillMapper;
import com.mailvor.modules.activity.vo.StoreSeckillVo;
import com.mailvor.modules.activity.vo.MwStoreSeckillQueryVo;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrService;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.dto.FromatDetailDto;
import com.mailvor.modules.product.service.dto.ProductFormatDto;
import com.mailvor.modules.product.service.dto.ProductResultDto;
import com.mailvor.modules.product.vo.MwStoreProductAttrQueryVo;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.OrderUtil;
import com.mailvor.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
* @author huangyu
* @date 2020-05-13
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreSeckillServiceImpl extends BaseServiceImpl<MwStoreSeckillMapper, MwStoreSeckill> implements MwStoreSeckillService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwStoreSeckillMapper mwStoreSeckillMapper;

    @Autowired
    private MwStoreProductReplyService replyService;

    @Autowired
    private MwStoreProductAttrService mwStoreProductAttrService;

    @Autowired
    private MwShippingTemplatesService shippingTemplatesService;

    /**
     * 产品详情
     * @param id 砍价商品id
     * @return StoreSeckillVo
     */
    @Override
    public StoreSeckillVo getDetail(Long id){
        Date now = new Date();
        MwStoreSeckill storeSeckill = this.lambdaQuery().eq(MwStoreSeckill::getId,id)
                .eq(MwStoreSeckill::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .eq(MwStoreSeckill::getIsShow,ShopCommonEnum.SHOW_1.getValue())
                .le(MwStoreSeckill::getStartTime,now)
                .ge(MwStoreSeckill::getStopTime,now)
                .one();

        if(storeSeckill == null){
            throw new MshopException("秒杀产品不存在或已下架");
        }
        //获取商品sku
        Map<String, Object> returnMap = mwStoreProductAttrService.getProductAttrDetail(storeSeckill.getProductId());
        //获取运费模板名称
        String storeFreePostage = RedisUtil.get("store_free_postage");
        String tempName = "";
        if(StrUtil.isBlank(storeFreePostage)
                || !NumberUtil.isNumber(storeFreePostage)
                || Integer.parseInt(storeFreePostage) == 0){
            tempName = "全国包邮";
        }else{
            MwShippingTemplates shippingTemplates = shippingTemplatesService.getById(storeSeckill.getTempId());
            if(ObjectUtil.isNotNull(shippingTemplates)){
                tempName = shippingTemplates.getName();
            }else {
                throw new BadRequestException("请配置运费模板");
            }

        }
        return StoreSeckillVo.builder()
                .productAttr((List<MwStoreProductAttrQueryVo>)returnMap.get("productAttr"))
                .productValue((Map<String, MwStoreProductAttrValue>)returnMap.get("productValue"))
                .storeInfo(generator.convert(storeSeckill, MwStoreSeckillQueryVo.class))
                .reply(replyService.getReply(storeSeckill.getProductId()))
                .replyCount(replyService.productReplyCount(storeSeckill.getProductId()))
                .tempName(tempName)
                .build();
    }

    /**
     * 秒杀产品列表
     * @param page page
     * @param limit limit
     * @return list
     */
    @Override
    public List<MwStoreSeckillQueryVo> getList(int page, int limit, int time) {
        Date nowTime = new Date();
        Page<MwStoreSeckill> pageModel = new Page<>(page, limit);
       LambdaQueryWrapper<MwStoreSeckill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreSeckill::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .eq(MwStoreSeckill::getTimeId,time)
                .le(MwStoreSeckill::getStartTime,nowTime)
                .ge(MwStoreSeckill::getStopTime,nowTime)
                .orderByDesc(MwStoreSeckill::getSort);
        List<MwStoreSeckillQueryVo> mwStoreSeckillQueryVos = generator.convert
               (mwStoreSeckillMapper.selectPage(pageModel,wrapper).getRecords(),
                       MwStoreSeckillQueryVo.class);
        mwStoreSeckillQueryVos.forEach(item->{
            Integer sum = item.getSales() + item.getStock();
            item.setPercent(NumberUtil.round(NumberUtil.mul(NumberUtil.div(item.getSales(),sum),
                    100),0).intValue());
        });
        return mwStoreSeckillQueryVos;
    }
    /**
     * 秒杀产品列表（首页用）
     * @param page page
     * @param limit limit
     * @return list
     */
    @Override
    public List<MwStoreSeckillQueryVo> getList(int page, int limit) {
        Date nowTime = new Date();
        Page<MwStoreSeckill> pageModel = new Page<>(page, limit);
       LambdaQueryWrapper<MwStoreSeckill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreSeckill::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .eq(MwStoreSeckill::getIsHot,1)
                .le(MwStoreSeckill::getStartTime,nowTime)
                .ge(MwStoreSeckill::getStopTime,nowTime)
                .orderByDesc(MwStoreSeckill::getSort);
        List<MwStoreSeckillQueryVo> mwStoreSeckillQueryVos = generator.convert
                (mwStoreSeckillMapper.selectPage(pageModel,wrapper).getRecords(),
                        MwStoreSeckillQueryVo.class);
        mwStoreSeckillQueryVos.forEach(item->{
            Integer sum = item.getSales() + item.getStock();
            item.setPercent(NumberUtil.round(NumberUtil.mul(NumberUtil.div(item.getSales(),sum),
                    100),0).intValue());
        });
        return mwStoreSeckillQueryVos;
    }
    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreSeckillQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreSeckill> page = new PageInfo<>(queryAll(criteria));
        List<MwStoreSeckillDto> storeSeckillDTOS = generator.convert(page.getList(), MwStoreSeckillDto.class);
        for (MwStoreSeckillDto storeSeckillDTO : storeSeckillDTOS){
            String statusStr = OrderUtil.checkActivityStatus(storeSeckillDTO.getStartTime(),
                    storeSeckillDTO.getStopTime(), storeSeckillDTO.getStatus());
            storeSeckillDTO.setStatusStr(statusStr);
        }
        Map<String,Object> map = new LinkedHashMap<>(2);
        map.put("content",storeSeckillDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreSeckill> queryAll(MwStoreSeckillQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreSeckill.class, criteria));
    }


    @Override
    public void download(List<MwStoreSeckillDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreSeckillDto mwStoreSeckill : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("推荐图", mwStoreSeckill.getImage());
            map.put("轮播图", mwStoreSeckill.getImages());
            map.put("活动标题", mwStoreSeckill.getTitle());
            map.put("简介", mwStoreSeckill.getInfo());
            map.put("返多少积分", mwStoreSeckill.getGiveIntegral());
            map.put("排序", mwStoreSeckill.getSort());
            map.put("库存", mwStoreSeckill.getStock());
            map.put("销量", mwStoreSeckill.getSales());
            map.put("单位名", mwStoreSeckill.getUnitName());
            map.put("邮费", mwStoreSeckill.getPostage());
            map.put("内容", mwStoreSeckill.getDescription());
            map.put("开始时间", mwStoreSeckill.getStartTime());
            map.put("结束时间", mwStoreSeckill.getStopTime());
            map.put("产品状态", mwStoreSeckill.getStatus());
            map.put("是否包邮", mwStoreSeckill.getIsPostage());
            map.put("热门推荐", mwStoreSeckill.getIsHot());
            map.put("最多秒杀几个", mwStoreSeckill.getNum());
            map.put("显示", mwStoreSeckill.getIsShow());
            map.put("时间段id", mwStoreSeckill.getTimeId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public boolean saveSeckill(MwStoreSeckillDto resources) {
        ProductResultDto resultDTO = this.computedProduct(resources.getAttrs());

        //添加商品
        MwStoreSeckill mwStoreSeckill = new MwStoreSeckill();
        BeanUtil.copyProperties(resources,mwStoreSeckill,"images");
        if(resources.getImages().isEmpty()) {
            throw new MshopException("请上传轮播图");
        }

        mwStoreSeckill.setStock(resultDTO.getStock());
        mwStoreSeckill.setOtPrice(BigDecimal.valueOf(resultDTO.getMinOtPrice()));
        mwStoreSeckill.setPrice(BigDecimal.valueOf(resultDTO.getMinPrice()));
        mwStoreSeckill.setCost(BigDecimal.valueOf(resultDTO.getMinCost()));
        mwStoreSeckill.setStock(resultDTO.getStock());
        mwStoreSeckill.setImages(String.join(",", resources.getImages()));
        this.saveOrUpdate(mwStoreSeckill);

        //属性处理
        //处理单sKu
        if(SpecTypeEnum.TYPE_0.getValue().equals(resources.getSpecType())){
            FromatDetailDto fromatDetailDto = FromatDetailDto.builder()
                    .value("规格")
                    .detailValue("")
                    .attrHidden("")
                    .detail(ListUtil.toList("默认"))
                    .build();
            List<ProductFormatDto> attrs = resources.getAttrs();
            ProductFormatDto productFormatDto = attrs.get(0);
            productFormatDto.setValue1("规格");
            Map<String,String> map = new HashMap<>();
            map.put("规格","默认");
            productFormatDto.setDetail(map);
            mwStoreProductAttrService.insertMwStoreProductAttr(ListUtil.toList(fromatDetailDto),
                    ListUtil.toList(productFormatDto),resources.getProductId());
        }else{
            mwStoreProductAttrService.insertMwStoreProductAttr(resources.getItems(),
                    resources.getAttrs(),resources.getProductId());
        }
        return true;
    }

    /**
     * 计算产品数据
     * @param attrs attrs
     * @return ProductResultDto
     */
    private ProductResultDto computedProduct(List<ProductFormatDto> attrs){
        //取最小价格
        Double minPrice = attrs
                .stream()
                .map(ProductFormatDto::getSeckillPrice)
                .min(Comparator.naturalOrder())
                .orElse(0d);

        Double minOtPrice = attrs
                .stream()
                .map(ProductFormatDto::getOtPrice)
                .min(Comparator.naturalOrder())
                .orElse(0d);

        Double minCost = attrs
                .stream()
                .map(ProductFormatDto::getCost)
                .min(Comparator.naturalOrder())
                .orElse(0d);
        //计算库存
        Integer stock = attrs
                .stream()
                .map(ProductFormatDto::getSeckillStock)
                .reduce(Integer::sum)
                .orElse(0);

        if(stock <= 0) {
            throw new MshopException("库存不能低于0");
        }

        return ProductResultDto.builder()
                .minPrice(minPrice)
                .minOtPrice(minOtPrice)
                .minCost(minCost)
                .stock(stock)
                .build();
    }

}
