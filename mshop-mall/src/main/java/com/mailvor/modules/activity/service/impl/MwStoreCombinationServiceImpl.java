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
import com.mailvor.enums.ProductTypeEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.enums.SpecTypeEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.activity.domain.MwStoreCombination;
import com.mailvor.modules.activity.domain.MwStorePink;
import com.mailvor.modules.activity.domain.MwStoreVisit;
import com.mailvor.modules.activity.service.MwStoreCombinationService;
import com.mailvor.modules.activity.service.MwStorePinkService;
import com.mailvor.modules.activity.service.dto.PinkAllDto;
import com.mailvor.modules.activity.service.dto.MwStoreCombinationDto;
import com.mailvor.modules.activity.service.dto.MwStoreCombinationQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreCombinationMapper;
import com.mailvor.modules.activity.service.mapper.MwStorePinkMapper;
import com.mailvor.modules.activity.service.mapper.MwStoreVisitMapper;
import com.mailvor.modules.activity.vo.CombinationQueryVo;
import com.mailvor.modules.activity.vo.StoreCombinationVo;
import com.mailvor.modules.activity.vo.MwStoreCombinationQueryVo;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrService;
import com.mailvor.modules.product.service.MwStoreProductAttrValueService;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.service.dto.FromatDetailDto;
import com.mailvor.modules.product.service.dto.ProductFormatDto;
import com.mailvor.modules.product.service.dto.ProductResultDto;
import com.mailvor.modules.product.vo.MwStoreProductAttrQueryVo;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


/**
* @author huangyu
* @date 2020-05-13
*/
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MwStoreCombinationServiceImpl extends BaseServiceImpl<MwStoreCombinationMapper, MwStoreCombination> implements MwStoreCombinationService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwStorePinkMapper mwStorePinkMapper;
    @Autowired
    private MwStoreVisitMapper mwStoreVisitMapper;

    @Autowired
    private MwStoreCombinationMapper mwStoreCombinationMapper;
    @Autowired
    private MwStoreProductReplyService replyService;
    @Autowired
    private MwStorePinkService storePinkService;
    @Autowired
    private MwStoreProductAttrService mwStoreProductAttrService;
    @Autowired
    private MwStoreProductAttrValueService mwStoreProductAttrValueService;
    @Autowired
    private MwShippingTemplatesService shippingTemplatesService;

    @Autowired
    private MwStoreProductService storeProductService;



    /**
     * 获取拼团详情
     * @param id 拼团产品id
     * @param uid uid
     * @return StoreCombinationVo
     */
    @Override
    public StoreCombinationVo getDetail(Long id, Long uid) {
        Date now = new Date();
        MwStoreCombination storeCombination = this
                .lambdaQuery().eq(MwStoreCombination::getId,id)
                .eq(MwStoreCombination::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .le(MwStoreCombination::getStartTime,now)
                .ge(MwStoreCombination::getStopTime,now)
                .one();
        if(storeCombination == null){
            throw new MshopException("拼团不存在或已下架");
        }
        //获取商品sku
        Map<String, Object> returnMap = mwStoreProductAttrService.getProductAttrDetail(storeCombination.getProductId());

        MwStoreCombinationQueryVo storeCombinationQueryVo = generator.convert(storeCombination,
                MwStoreCombinationQueryVo.class);

        StoreCombinationVo storeCombinationVo = new StoreCombinationVo();

        storeCombinationVo.setStoreInfo(storeCombinationQueryVo);

        //评价
        storeCombinationVo.setReply(replyService
                .getReply(storeCombinationQueryVo.getProductId()));
        Long replyCount = replyService.productReplyCount(storeCombinationQueryVo.getProductId());
        //总条数
        storeCombinationVo.setReplyCount(replyCount);
        //好评比例
        storeCombinationVo.setReplyChance(replyService.replyPer(storeCombinationQueryVo.getProductId()));

        //获取运费模板名称
        String storeFreePostage = RedisUtil.get("store_free_postage");
        String tempName = "";
        if(StrUtil.isBlank(storeFreePostage)
                || !NumberUtil.isNumber(storeFreePostage)
                || Integer.valueOf(storeFreePostage) == 0){
            tempName = "全国包邮";
        }else{
            MwShippingTemplates shippingTemplates = shippingTemplatesService.getById(storeCombination.getTempId());
            if(ObjectUtil.isNotNull(shippingTemplates)){
                tempName = shippingTemplates.getName();
            }else {
                throw new BadRequestException("请配置运费模板");
            }

        }
        storeCombinationVo.setTempName(tempName);

        PinkAllDto pinkAllDto = storePinkService.getPinkAll(id);
        storeCombinationVo.setPindAll(pinkAllDto.getPindAll());
        storeCombinationVo.setPink(pinkAllDto.getList());
        storeCombinationVo.setPinkOkList(storePinkService.getPinkOkList(uid));
        storeCombinationVo.setPinkOkSum(storePinkService.getPinkOkSumTotalNum());
        storeCombinationVo.setProductAttr((List<MwStoreProductAttrQueryVo>)returnMap.get("productAttr"));
        storeCombinationVo.setProductValue((Map<String, MwStoreProductAttrValue>)returnMap.get("productValue"));
        return storeCombinationVo;
    }

    /**
     * 拼团列表
     * @param page page
     * @param limit limit
     * @return list
     */
    @Override
    public CombinationQueryVo getList(int page, int limit) {
        CombinationQueryVo combinationQueryVo = new CombinationQueryVo();
        Date nowTime = new Date();
        Page<MwStoreCombination> pageModel = new Page<>(page, limit);
       LambdaQueryWrapper<MwStoreCombination> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(MwStoreCombination::getIsShow,1)
                .le(MwStoreCombination::getStartTime,nowTime)
                .ge(MwStoreCombination::getStopTime,nowTime)
                .orderByDesc(MwStoreCombination::getSort);
        IPage<MwStoreCombination> mwStoreCombinationIPage = mwStoreCombinationMapper.selectPage(pageModel, wrapper);

        List<MwStoreCombinationQueryVo> collect = mwStoreCombinationIPage.getRecords().stream().map(i -> {
            MwStoreCombinationQueryVo mwStoreCombinationQueryVo = new MwStoreCombinationQueryVo();
            BeanUtils.copyProperties(i, mwStoreCombinationQueryVo);
            return mwStoreCombinationQueryVo;
        }).collect(Collectors.toList());
        combinationQueryVo.setStoreCombinationQueryVos(collect);
        combinationQueryVo.setLastPage(mwStoreCombinationIPage.getPages());
        return combinationQueryVo;
    }


    //=======================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreCombinationQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCombination> page = new PageInfo<>(queryAll(criteria));

        List<MwStoreCombinationDto> combinationDTOS = generator.convert(page.getList(), MwStoreCombinationDto.class);
        for (MwStoreCombinationDto combinationDTO : combinationDTOS) {
            //参与人数
            combinationDTO.setCountPeopleAll(mwStorePinkMapper.selectCount(new LambdaQueryWrapper<MwStorePink>()
                    .eq(MwStorePink::getCid,combinationDTO.getId())));

            //成团人数
            combinationDTO.setCountPeoplePink(mwStorePinkMapper.selectCount(new LambdaQueryWrapper<MwStorePink>()
                    .eq(MwStorePink::getCid,combinationDTO.getId())
                    .eq(MwStorePink::getKId,0)));//团长
            //获取查看拼团产品人数
            combinationDTO.setCountPeopleBrowse(mwStoreVisitMapper.selectCount(new LambdaQueryWrapper<MwStoreVisit>()
                    .eq(MwStoreVisit::getProductId,combinationDTO.getId())
                    .eq(MwStoreVisit::getProductType, ProductTypeEnum.COMBINATION.getValue())));
        }
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content",combinationDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreCombination> queryAll(MwStoreCombinationQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCombination.class, criteria));
    }


    @Override
    public void download(List<MwStoreCombinationDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCombinationDto mwStoreCombination : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商品id", mwStoreCombination.getProductId());
            map.put("推荐图", mwStoreCombination.getImage());
            map.put("轮播图", mwStoreCombination.getImages());
            map.put("活动标题", mwStoreCombination.getTitle());
            map.put("参团人数", mwStoreCombination.getPeople());
            map.put("简介", mwStoreCombination.getInfo());
            map.put("价格", mwStoreCombination.getPrice());
            map.put("排序", mwStoreCombination.getSort());
            map.put("销量", mwStoreCombination.getSales());
            map.put("库存", mwStoreCombination.getStock());
            map.put("推荐", mwStoreCombination.getIsHost());
            map.put("产品状态", mwStoreCombination.getIsShow());
            map.put(" combination",  mwStoreCombination.getCombination());
            map.put("商户是否可用1可用0不可用", mwStoreCombination.getMerUse());
            map.put("是否包邮1是0否", mwStoreCombination.getIsPostage());
            map.put("邮费", mwStoreCombination.getPostage());
            map.put("拼团内容", mwStoreCombination.getDescription());
            map.put("拼团开始时间", mwStoreCombination.getStartTime());
            map.put("拼团结束时间", mwStoreCombination.getStopTime());
            map.put("拼团订单有效时间", mwStoreCombination.getEffectiveTime());
            map.put("拼团产品成本", mwStoreCombination.getCost());
            map.put("浏览量", mwStoreCombination.getBrowse());
            map.put("单位名", mwStoreCombination.getUnitName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 修改状态
     * @param id 拼团产品id
     * @param status ShopCommonEnum
     */
    @Override
    public void onSale(Long id, Integer status) {
        if(ShopCommonEnum.SHOW_1.getValue().equals(status)){
            status = ShopCommonEnum.SHOW_0.getValue();
        }else{
            status = ShopCommonEnum.SHOW_1.getValue();
        }
        MwStoreCombination mwStoreCombination = new MwStoreCombination();
        mwStoreCombination.setIsShow(status);
        mwStoreCombination.setId(id);
        this.saveOrUpdate(mwStoreCombination);
    }

    @Override
    public boolean saveCombination(MwStoreCombinationDto resources) {
        ProductResultDto resultDTO = this.computedProduct(resources.getAttrs());

        //添加商品
        MwStoreCombination mwStoreCombination = new MwStoreCombination();
        BeanUtil.copyProperties(resources,mwStoreCombination,"images");
        if(resources.getImages().isEmpty()) {
            throw new MshopException("请上传轮播图");
        }

        mwStoreCombination.setPrice(BigDecimal.valueOf(resultDTO.getMinPrice()));
        mwStoreCombination.setProductPrice(BigDecimal.valueOf(resultDTO.getMinOtPrice()));
        mwStoreCombination.setCost(resultDTO.getMinCost().intValue());
        mwStoreCombination.setStock(resultDTO.getStock());
        mwStoreCombination.setImages(String.join(",", resources.getImages()));
        this.saveOrUpdate(mwStoreCombination);

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
                .map(ProductFormatDto::getPinkPrice)
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
                .map(ProductFormatDto::getPinkStock)
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

    /**
     * mapTobean
     * @param listMap listMap
     * @return list
     */
    private List<ProductFormatDto> ListMapToListBean(List<Map<String, Object>> listMap){
        List<ProductFormatDto> list = new ArrayList<>();
        // 循环遍历出map对象
        for (Map<String, Object> m : listMap) {
            list.add(BeanUtil.mapToBean(m,ProductFormatDto.class,true));
        }
        return list;
    }
}
