/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CommonEnum;
import com.mailvor.enums.ProductEnum;
import com.mailvor.enums.ProductTypeEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.enums.SortEnum;
import com.mailvor.enums.SpecTypeEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.exception.ErrorRequestException;
import com.mailvor.modules.category.service.MwStoreCategoryService;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.domain.MwStoreProductRelation;
import com.mailvor.modules.product.param.MwStoreProductQueryParam;
import com.mailvor.modules.product.service.MwStoreProductAttrService;
import com.mailvor.modules.product.service.MwStoreProductAttrValueService;
import com.mailvor.modules.product.service.MwStoreProductRelationService;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.service.dto.DetailDto;
import com.mailvor.modules.product.service.dto.FromatDetailDto;
import com.mailvor.modules.product.service.dto.ProductFormatDto;
import com.mailvor.modules.product.service.dto.ProductResultDto;
import com.mailvor.modules.product.service.dto.StoreProductDto;
import com.mailvor.modules.product.service.dto.MwStoreProductDto;
import com.mailvor.modules.product.service.dto.MwStoreProductQueryCriteria;
import com.mailvor.modules.product.service.mapper.StoreProductMapper;
import com.mailvor.modules.product.vo.ProductVo;
import com.mailvor.modules.product.vo.MwStoreProductAttrQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
import com.mailvor.modules.shop.service.MwSystemStoreService;
import com.mailvor.modules.template.domain.MwShippingTemplates;
import com.mailvor.modules.template.service.MwShippingTemplatesService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtil;
import com.mailvor.utils.RegexUtil;
import com.mailvor.utils.ShopKeyUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.qiniu.util.StringUtils;
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
 * @author mazhongjun
 * @date 2020-05-12
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class MwStoreProductServiceImpl extends BaseServiceImpl<StoreProductMapper, MwStoreProduct> implements MwStoreProductService {

    @Autowired
    private IGenerator generator;
    @Autowired
    private StoreProductMapper storeProductMapper;
    @Autowired
    private MwStoreCategoryService mwStoreCategoryService;
    @Autowired
    private MwStoreProductAttrService mwStoreProductAttrService;
    @Autowired
    private MwStoreProductAttrValueService mwStoreProductAttrValueService;
    @Autowired
    private MwUserService userService;
    @Autowired
    private MwStoreProductReplyService replyService;
    @Autowired
    private MwStoreProductRelationService relationService;
    @Autowired
    private MwSystemStoreService systemStoreService;
    @Autowired
    private MwShippingTemplatesService shippingTemplatesService;


    /**
     * 增加库存 减少销量
     *
     * @param num       数量
     * @param productId 商品id
     * @param unique    sku唯一值
     */
    @Override
    public void incProductStock(Integer num, Long productId, String unique, Long activityId, String type) {
        //处理属性sku
        if (StrUtil.isNotEmpty(unique)) {
            mwStoreProductAttrService.incProductAttrStock(num, productId, unique, type);
        }
        //先处理商品库存，活动商品也要处理，因为共享库存
        storeProductMapper.incStockDecSales(num, productId);
        //处理商品外层显示的库存
        if (ProductTypeEnum.COMBINATION.getValue().equals(type)) {
            storeProductMapper.incCombinationStockIncSales(num, productId, activityId);
        } else if (ProductTypeEnum.SECKILL.getValue().equals(type)) {
            storeProductMapper.incSeckillStockIncSales(num, productId, activityId);
        }
        //todo 处理砍价商品库存
    }

    /**
     * 减少库存与增加销量
     *
     * @param num       数量
     * @param productId 商品id
     */
    @Override
    public void decProductStock(int num, Long productId, Long activityId, String type) {
        //处理属性sku
        mwStoreProductAttrService.decProductAttrStock(num, productId, type);
    }


    @Override
    public MwStoreProduct getProductInfo(Long id) {
       LambdaQueryWrapper<MwStoreProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProduct::getIsShow, 1).eq(MwStoreProduct::getId, id);
        MwStoreProduct storeProduct = this.baseMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new ErrorRequestException("商品不存在或已下架");
        }

        return storeProduct;
    }


    /**
     * 获取单个商品
     *
     * @param id 商品id
     * @return MwStoreProductQueryVo
     */
    @Override
    public MwStoreProductQueryVo getStoreProductById(Long id) {
        return generator.convert(this.baseMapper.selectById(id), MwStoreProductQueryVo.class);
    }


    /**
     * 返回普通商品库存
     *
     * @param productId 商品id
     * @param unique    sku唯一值
     * @return int
     */
    @Override
    public int getProductStock(Long productId, String unique, String type) {
        MwStoreProductAttrValue storeProductAttrValue = mwStoreProductAttrValueService
                .getOne(Wrappers.<MwStoreProductAttrValue>lambdaQuery()
                        .eq(MwStoreProductAttrValue::getUnique, unique)
                        .eq(MwStoreProductAttrValue::getProductId, productId));

        if (storeProductAttrValue == null) {
            return 0;
        }
        if (ProductTypeEnum.PINK.getValue().equals(type)) {
            return storeProductAttrValue.getPinkStock();
        } else if (ProductTypeEnum.SECKILL.getValue().equals(type)) {
            return storeProductAttrValue.getSeckillStock();
        }
        return storeProductAttrValue.getStock();

    }


    /**
     * 商品列表
     *
     * @param productQueryParam MwStoreProductQueryParam
     * @return list
     */
    @Override
    public List<MwStoreProductQueryVo> getGoodsList(MwStoreProductQueryParam productQueryParam) {

        LambdaQueryWrapper<MwStoreProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProduct::getIsShow, CommonEnum.SHOW_STATUS_1.getValue());
        wrapper.eq(MwStoreProduct::getIsDel, CommonEnum.DEL_STATUS_0.getValue());
//        wrapper.eq(MwStoreProduct::getIsIntegral, CommonEnum.SHOW_STATUS_1.getValue());

        if(productQueryParam.getIsIntegral()!=null){
            wrapper.eq(MwStoreProduct::getIsIntegral, productQueryParam.getIsIntegral());
        }
        //多字段模糊查询分类搜索
        if (StrUtil.isNotBlank(productQueryParam.getSid()) &&
                !ShopConstants.MSHOP_ZERO.equals(productQueryParam.getSid())) {
            wrapper.eq(MwStoreProduct::getCateId, productQueryParam.getSid());
        }
        //关键字搜索
        if (StrUtil.isNotEmpty(productQueryParam.getKeyword())) {
            wrapper.and(wrapper1 -> {
                wrapper1.or();
                wrapper1.like(MwStoreProduct::getStoreName, productQueryParam.getKeyword());
                wrapper1.or();
                wrapper1.like(MwStoreProduct::getStoreInfo, productQueryParam.getKeyword());
                wrapper1.or();
                wrapper1.like(MwStoreProduct::getKeyword, productQueryParam.getKeyword());
            });
        }
        //新品搜索
        if (StrUtil.isNotBlank(productQueryParam.getNews()) &&
                !ShopConstants.MSHOP_ZERO.equals(productQueryParam.getNews())) {
            wrapper.eq(MwStoreProduct::getIsNew, ShopCommonEnum.IS_NEW_1.getValue());
        }

        //销量排序
        if (SortEnum.DESC.getValue().equals(productQueryParam.getSalesOrder())) {
            wrapper.orderByDesc(MwStoreProduct::getSales);
        } else if (SortEnum.ASC.getValue().equals(productQueryParam.getSalesOrder())) {
            wrapper.orderByAsc(MwStoreProduct::getSales);
        }

        //价格排序
        if (SortEnum.DESC.getValue().equals(productQueryParam.getPriceOrder())) {
            wrapper.orderByDesc(MwStoreProduct::getPrice);
        } else if (SortEnum.ASC.getValue().equals(productQueryParam.getPriceOrder())) {
            wrapper.orderByAsc(MwStoreProduct::getPrice);
        }

        //无其他排序条件时,防止因为商品排序导致商品重复
        if (StringUtils.isNullOrEmpty(productQueryParam.getPriceOrder()) && StringUtils.isNullOrEmpty(productQueryParam.getSalesOrder())) {
            wrapper.orderByDesc(MwStoreProduct::getId);
            wrapper.orderByDesc(MwStoreProduct::getSort);
        }
        Page<MwStoreProduct> pageModel = new Page<>(productQueryParam.getPage(),
                productQueryParam.getLimit());

        IPage<MwStoreProduct> pageList = storeProductMapper.selectPage(pageModel, wrapper);

        List<MwStoreProductQueryVo> list = generator.convert(pageList.getRecords(), MwStoreProductQueryVo.class);

        return list;
    }

    /**
     * 商品详情
     *
     * @param id        商品id
     * @param uid       用户id
     * @param latitude  纬度
     * @param longitude 经度
     * @return ProductVo
     */
    @Override
    public ProductVo goodsDetail(Long id, Long uid, String latitude, String longitude) {
       LambdaQueryWrapper<MwStoreProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwStoreProduct::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .eq(MwStoreProduct::getId, id);
        MwStoreProduct storeProduct = storeProductMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(storeProduct)) {
            throw new ErrorRequestException("商品不存在或已下架");
        }

        //获取商品sku
        Map<String, Object> returnMap = mwStoreProductAttrService.getProductAttrDetail(id);
        ProductVo productVo = new ProductVo();
        MwStoreProductQueryVo storeProductQueryVo = generator.convert(storeProduct, MwStoreProductQueryVo.class);

        //设置销量
        storeProductQueryVo.setSales(storeProductQueryVo.getSales() + storeProductQueryVo.getFicti());

        if (uid.longValue() > 0) {
            //设置VIP价格
            double vipPrice = userService.setLevelPrice(
                    storeProductQueryVo.getPrice().doubleValue(), uid);
            storeProductQueryVo.setVipPrice(BigDecimal.valueOf(vipPrice));

            //收藏
            boolean isCollect = relationService.isProductRelation(id.toString(), uid);
            storeProductQueryVo.setUserCollect(isCollect);
        }
        //总条数
        Long totalCount = replyService.productReplyCount(id);
        productVo.setReplyCount(totalCount);

        //评价
        MwStoreProductReplyQueryVo storeProductReplyQueryVo = replyService.getReply(id);
        productVo.setReply(storeProductReplyQueryVo);

        //好评比例
        String replyPer = replyService.replyPer(id);
        productVo.setReplyChance(replyPer);

        //获取运费模板名称
        String storeFreePostage = RedisUtil.get("store_free_postage");
        String tempName = "";
        if (StrUtil.isBlank(storeFreePostage)
                || !NumberUtil.isNumber(storeFreePostage)
                || Integer.valueOf(storeFreePostage) == 0) {
            tempName = "全国包邮";
        } else {
            MwShippingTemplates shippingTemplates = shippingTemplatesService.getById(storeProduct.getTempId());
            if (ObjectUtil.isNotNull(shippingTemplates)) {
                tempName = shippingTemplates.getName();
            } else {
                throw new BadRequestException("请配置运费模板");
            }

        }
        productVo.setTempName(tempName);

        //设置商品相关信息
        productVo.setStoreInfo(storeProductQueryVo);
        productVo.setProductAttr((List<MwStoreProductAttrQueryVo>) returnMap.get("productAttr"));
        productVo.setProductValue((Map<String, MwStoreProductAttrValue>) returnMap.get("productValue"));


        //门店
        productVo.setSystemStore(systemStoreService.getStoreInfo(latitude, longitude));
        productVo.setMapKey(RedisUtil.get(ShopKeyUtils.getTengXunMapKey()));
        if (uid.longValue() > 0) {
            //添加足迹
            MwStoreProductRelation foot = relationService.getOne(new LambdaQueryWrapper<MwStoreProductRelation>()
                    .eq(MwStoreProductRelation::getUid, uid)
                    .eq(MwStoreProductRelation::getProductId, storeProductQueryVo.getId())
                    .eq(MwStoreProductRelation::getType, "foot"));

            if (ObjectUtil.isNotNull(foot)) {
                foot.setCreateTime(new Date());
                relationService.saveOrUpdate(foot);
            } else {
                MwStoreProductRelation storeProductRelation = new MwStoreProductRelation();
                storeProductRelation.setProductId(storeProductQueryVo.getId().toString());
                storeProductRelation.setUid(uid);
                storeProductRelation.setCreateTime(new Date());
                storeProductRelation.setType("foot");
                relationService.save(storeProductRelation);
            }
        }

        return productVo;
    }

    /**
     * 商品浏览量
     *
     * @param productId
     */
    @Override
    public void incBrowseNum(Long productId) {
        storeProductMapper.incBrowseNum(productId);
    }


    /**
     * 商品列表
     *
     * @param page  页码
     * @param limit 条数
     * @param order ProductEnum
     * @return List
     */
    @Override
    public List<MwStoreProductQueryVo> getList(int page, int limit, int order) {

       LambdaQueryWrapper<MwStoreProduct> wrapper = new LambdaQueryWrapper<>();
       wrapper.eq(MwStoreProduct::getIsShow, ShopCommonEnum.SHOW_1.getValue())
                .eq(MwStoreProduct::getIsDel,ShopCommonEnum.DELETE_0.getValue())
                .orderByDesc(MwStoreProduct::getSort);
        wrapper.eq(MwStoreProduct::getIsIntegral,0);
        // order
        switch (ProductEnum.toType(order)) {
            //精品推荐
            case TYPE_1:
                wrapper.eq(MwStoreProduct::getIsBest,
                        ShopCommonEnum.IS_STATUS_1.getValue());
                break;
            //首发新品
            case TYPE_3:
                wrapper.eq(MwStoreProduct::getIsNew,
                        ShopCommonEnum.IS_STATUS_1.getValue());
                break;
            // 猜你喜欢
            case TYPE_4:
                wrapper.eq(MwStoreProduct::getIsBenefit,
                        ShopCommonEnum.IS_STATUS_1.getValue());
                break;
            // 热门榜单
            case TYPE_2:
                wrapper.eq(MwStoreProduct::getIsHot,
                        ShopCommonEnum.IS_STATUS_1.getValue());
                break;
            default:
        }
        Page<MwStoreProduct> pageModel = new Page<>(page, limit);

        IPage<MwStoreProduct> pageList = storeProductMapper.selectPage(pageModel, wrapper);

        return generator.convert(pageList.getRecords(), MwStoreProductQueryVo.class);
    }


    //============ 分割线================//


    @Override
    public Map<String, Object> queryAll(MwStoreProductQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreProduct> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwStoreProductDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    public List<MwStoreProduct> queryAll(MwStoreProductQueryCriteria criteria) {
        List<MwStoreProduct> mwStoreProductList = baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreProduct.class, criteria));
        mwStoreProductList.forEach(mwStoreProduct -> {
            mwStoreProduct.setStoreCategory(mwStoreCategoryService.getById(mwStoreProduct.getCateId()));
        });
        return mwStoreProductList;
    }


    @Override
    public void download(List<MwStoreProductDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreProductDto mwStoreProduct : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("商户Id(0为总后台管理员创建,不为0的时候是商户后台创建)", mwStoreProduct.getMerId());
            map.put("商品图片", mwStoreProduct.getImage());
            map.put("轮播图", mwStoreProduct.getSliderImage());
            map.put("商品名称", mwStoreProduct.getStoreName());
            map.put("商品简介", mwStoreProduct.getStoreInfo());
            map.put("关键字", mwStoreProduct.getKeyword());
            map.put("产品条码（一维码）", mwStoreProduct.getBarCode());
            map.put("分类id", mwStoreProduct.getCateId());
            map.put("商品价格", mwStoreProduct.getPrice());
            map.put("会员价格", mwStoreProduct.getVipPrice());
            map.put("市场价", mwStoreProduct.getOtPrice());
            map.put("邮费", mwStoreProduct.getPostage());
            map.put("单位名", mwStoreProduct.getUnitName());
            map.put("排序", mwStoreProduct.getSort());
            map.put("销量", mwStoreProduct.getSales());
            map.put("库存", mwStoreProduct.getStock());
            map.put("状态（0：未上架，1：上架）", mwStoreProduct.getIsShow());
            map.put("是否热卖", mwStoreProduct.getIsHot());
            map.put("是否优惠", mwStoreProduct.getIsBenefit());
            map.put("是否精品", mwStoreProduct.getIsBest());
            map.put("是否新品", mwStoreProduct.getIsNew());
            map.put("产品描述", mwStoreProduct.getDescription());
            map.put("添加时间", mwStoreProduct.getAddTime());
            map.put("是否包邮", mwStoreProduct.getIsPostage());
            map.put("是否删除", mwStoreProduct.getIsDel());
            map.put("商户是否代理 0不可代理1可代理", mwStoreProduct.getMerUse());
            map.put("获得积分", mwStoreProduct.getGiveIntegral());
            map.put("成本价", mwStoreProduct.getCost());
            map.put("秒杀状态 0 未开启 1已开启", mwStoreProduct.getIsSeckill());
            map.put("砍价状态 0未开启 1开启", mwStoreProduct.getIsBargain());
            map.put("是否优品推荐", mwStoreProduct.getIsGood());
            map.put("虚拟销量", mwStoreProduct.getFicti());
            map.put("浏览量", mwStoreProduct.getBrowse());
            map.put("产品二维码地址(用户小程序海报)", mwStoreProduct.getCodePath());
            map.put("淘宝京东1688类型", mwStoreProduct.getSoureLink());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    /**
     * 商品上架下架
     *
     * @param id     商品id
     * @param status ShopCommonEnum
     */
    @Override
    public void onSale(Long id, Integer status) {
        if (ShopCommonEnum.SHOW_1.getValue().equals(status)) {
            status = ShopCommonEnum.SHOW_0.getValue();
        } else {
            status = ShopCommonEnum.SHOW_1.getValue();
        }
        storeProductMapper.updateOnsale(status, id);
    }


    /**
     * 新增/保存商品
     *
     * @param storeProductDto 商品
     */
    @Override
    public void insertAndEditMwStoreProduct(StoreProductDto storeProductDto) {
        storeProductDto.setDescription(RegexUtil.converProductDescription(storeProductDto.getDescription()));
        ProductResultDto resultDTO = this.computedProduct(storeProductDto.getAttrs());

        //添加商品
        MwStoreProduct mwStoreProduct = new MwStoreProduct();
        BeanUtil.copyProperties(storeProductDto, mwStoreProduct, "sliderImage");
        if (storeProductDto.getSliderImage().isEmpty()) {
            throw new MshopException("请上传轮播图");
        }

        mwStoreProduct.setPrice(BigDecimal.valueOf(resultDTO.getMinPrice()));
        mwStoreProduct.setOtPrice(BigDecimal.valueOf(resultDTO.getMinOtPrice()));
        mwStoreProduct.setCost(BigDecimal.valueOf(resultDTO.getMinCost()));
        mwStoreProduct.setIntegral(resultDTO.getMinIntegral());
        mwStoreProduct.setStock(resultDTO.getStock());
        mwStoreProduct.setSliderImage(String.join(",", storeProductDto.getSliderImage()));

        if (storeProductDto.getId() != null) {
            //清空商品转发图
            deleteForwardImg(storeProductDto.getId());
        }

        this.saveOrUpdate(mwStoreProduct);

        //属性处理
        //处理单sKu
        if (SpecTypeEnum.TYPE_0.getValue().equals(storeProductDto.getSpecType())) {
            FromatDetailDto fromatDetailDto = FromatDetailDto.builder()
                    .value("规格")
                    .detailValue("")
                    .attrHidden("")
                    .detail(ListUtil.toList("默认"))
                    .build();
            List<ProductFormatDto> attrs = storeProductDto.getAttrs();
            ProductFormatDto productFormatDto = attrs.get(0);
            productFormatDto.setValue1("规格");
            Map<String, String> map = new HashMap<>();
            map.put("规格", "默认");
            productFormatDto.setDetail(map);
            mwStoreProductAttrService.insertMwStoreProductAttr(ListUtil.toList(fromatDetailDto),
                    ListUtil.toList(productFormatDto), mwStoreProduct.getId());
        } else {
            mwStoreProductAttrService.insertMwStoreProductAttr(storeProductDto.getItems(),
                    storeProductDto.getAttrs(), mwStoreProduct.getId());
        }


    }


    /**
     * 获取生成的属性
     *
     * @param id      商品id
     * @param jsonStr jsonStr
     * @return map
     */
    @Override
    public Map<String, Object> getFormatAttr(Long id, String jsonStr, boolean isActivity) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Map<String, Object> resultMap = new LinkedHashMap<>(3);

        if (jsonObject == null || jsonObject.get("attrs") == null || jsonObject.getJSONArray("attrs").isEmpty()) {
            resultMap.put("attr", new ArrayList<>());
            resultMap.put("value", new ArrayList<>());
            resultMap.put("header", new ArrayList<>());
            return resultMap;
        }


        List<FromatDetailDto> fromatDetailDTOList = JSON.parseArray(jsonObject.get("attrs").toString(),
                FromatDetailDto.class);

        //fromatDetailDTOList
        DetailDto detailDto = this.attrFormat(fromatDetailDTOList);

        List<Map<String, Object>> headerMapList = null;
        List<Map<String, Object>> valueMapList = new ArrayList<>();
        String align = "center";
        Map<String, Object> headerMap = new LinkedHashMap<>();
        for (Map<String, Map<String, String>> map : detailDto.getRes()) {
            Map<String, String> detail = map.get("detail");
            String[] detailArr = detail.values().toArray(new String[]{});
            Arrays.sort(detailArr);

            String sku = String.join(",", detailArr);

            Map<String, Object> valueMap = new LinkedHashMap<>();

            List<String> detailKeys =
                    detail.entrySet()
                            .stream()
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

            int i = 0;
            headerMapList = new ArrayList<>();
            for (String title : detailKeys) {
                headerMap.put("title", title);
                headerMap.put("minWidth", "130");
                headerMap.put("align", align);
                headerMap.put("key", "value" + (i + 1));
                headerMap.put("slot", "value" + (i + 1));
                headerMapList.add(ObjectUtil.clone(headerMap));
                i++;
            }

            String[] detailValues = detail.values().toArray(new String[]{});
            for (int j = 0; j < detailValues.length; j++) {
                String key = "value" + (j + 1);
                valueMap.put(key, detailValues[j]);
            }
//            /** 拼团属性对应的金额 */
//            private BigDecimal pinkPrice;
//
//            /** 秒杀属性对应的金额 */
//            private BigDecimal seckillPrice;
//            /** 拼团库存属性对应的库存 */
//            private Integer pinkStock;
//
//            private Integer seckillStock;
            valueMap.put("detail", detail);
            valueMap.put("sku", "");
            valueMap.put("pic", "");
            valueMap.put("price", 0);
            valueMap.put("cost", 0);
            valueMap.put("ot_price", 0);
            valueMap.put("stock", 0);
            valueMap.put("bar_code", "");
            valueMap.put("weight", 0);
            valueMap.put("volume", 0);
            valueMap.put("brokerage", 0);
            valueMap.put("brokerage_two", 0);
            valueMap.put("pink_price", 0);
            valueMap.put("seckill_price", 0);
            valueMap.put("pink_stock", 0);
            valueMap.put("seckill_stock", 0);
            valueMap.put("integral", 0);
            if (id > 0) {
                MwStoreProductAttrValue storeProductAttrValue = mwStoreProductAttrValueService
                        .getOne(Wrappers.<MwStoreProductAttrValue>lambdaQuery()
                                .eq(MwStoreProductAttrValue::getProductId, id)
                                .eq(MwStoreProductAttrValue::getSku, sku));
                if (storeProductAttrValue != null) {
                    valueMap.put("sku",storeProductAttrValue.getSku());
                    valueMap.put("pic", storeProductAttrValue.getImage());
                    valueMap.put("price", storeProductAttrValue.getPrice());
                    valueMap.put("cost", storeProductAttrValue.getCost());
                    valueMap.put("ot_price", storeProductAttrValue.getOtPrice());
                    valueMap.put("stock", storeProductAttrValue.getStock());
                    valueMap.put("bar_code", storeProductAttrValue.getBarCode());
                    valueMap.put("weight", storeProductAttrValue.getWeight());
                    valueMap.put("volume", storeProductAttrValue.getVolume());
                    valueMap.put("brokerage", storeProductAttrValue.getBrokerage());
                    valueMap.put("brokerage_two", storeProductAttrValue.getBrokerageTwo());
                    valueMap.put("pink_price", storeProductAttrValue.getPinkPrice());
                    valueMap.put("seckill_price", storeProductAttrValue.getSeckillPrice());
                    valueMap.put("pink_stock", storeProductAttrValue.getPinkStock());
                    valueMap.put("seckill_stock", storeProductAttrValue.getSeckillStock());
                    valueMap.put("integral", storeProductAttrValue.getIntegral());
                }
            }

            valueMapList.add(ObjectUtil.clone(valueMap));

        }

        this.addMap(headerMap, headerMapList, align, isActivity);


        resultMap.put("attr", fromatDetailDTOList);
        resultMap.put("value", valueMapList);
        resultMap.put("header", headerMapList);

        return resultMap;
    }


    /**
     * 计算产品数据
     *
     * @param attrs attrs
     * @return ProductResultDto
     */
    private ProductResultDto computedProduct(List<ProductFormatDto> attrs) {
        //取最小价格
        Double minPrice = attrs
                .stream()
                .map(ProductFormatDto::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(0d);

        //取最小积分
        Integer minIntegral = attrs
                .stream()
                .map(ProductFormatDto::getIntegral)
                .min(Comparator.naturalOrder())
                .orElse(0);

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
                .map(ProductFormatDto::getStock)
                .reduce(Integer::sum)
                .orElse(0);

        if (stock <= 0) {
            throw new MshopException("库存不能低于0");
        }

        return ProductResultDto.builder()
                .minPrice(minPrice)
                .minOtPrice(minOtPrice)
                .minCost(minCost)
                .stock(stock)
                .minIntegral(minIntegral)
                .build();
    }

    /**
     * mapTobean
     *
     * @param listMap listMap
     * @return list
     */
    private List<ProductFormatDto> ListMapToListBean(List<Map<String, Object>> listMap) {
        List<ProductFormatDto> list = new ArrayList<>();
        // 循环遍历出map对象
        for (Map<String, Object> m : listMap) {
            list.add(BeanUtil.mapToBean(m, ProductFormatDto.class, true));
        }
        return list;
    }

    /**
     * 增加表头
     *
     * @param headerMap     headerMap
     * @param headerMapList headerMapList
     * @param align         align
     */
    private void addMap(Map<String, Object> headerMap, List<Map<String, Object>> headerMapList, String align, boolean isActivity) {
        headerMap.put("title", "图片");
        headerMap.put("slot", "pic");
        headerMap.put("align", align);
        headerMap.put("minWidth", 80);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "售价");
        headerMap.put("slot", "price");
        headerMap.put("align", align);
        headerMap.put("minWidth", 120);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "成本价");
        headerMap.put("slot", "cost");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "原价");
        headerMap.put("slot", "ot_price");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "库存");
        headerMap.put("slot", "stock");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "产品编号");
        headerMap.put("slot", "bar_code");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "重量(KG)");
        headerMap.put("slot", "weight");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "体积(m³)");
        headerMap.put("slot", "volume");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        headerMap.put("title", "所需兑换积分");
        headerMap.put("slot", "integral");
        headerMap.put("align", align);
        headerMap.put("minWidth", 140);
        headerMapList.add(ObjectUtil.clone(headerMap));

        if (isActivity) {
            headerMap.put("title", "拼团价");
            headerMap.put("slot", "pink_price");
            headerMap.put("align", align);
            headerMap.put("minWidth", 140);
            headerMapList.add(ObjectUtil.clone(headerMap));

            headerMap.put("title", "拼团活动库存");
            headerMap.put("slot", "pink_stock");
            headerMap.put("align", align);
            headerMap.put("minWidth", 140);
            headerMapList.add(ObjectUtil.clone(headerMap));

            headerMap.put("title", "秒杀价");
            headerMap.put("slot", "seckill_price");
            headerMap.put("align", align);
            headerMap.put("minWidth", 140);
            headerMapList.add(ObjectUtil.clone(headerMap));

            headerMap.put("title", "秒杀活动库存");
            headerMap.put("slot", "seckill_stock");
            headerMap.put("align", align);
            headerMap.put("minWidth", 140);
            headerMapList.add(ObjectUtil.clone(headerMap));
        }

        headerMap.put("title", "操作");
        headerMap.put("slot", "action");
        headerMap.put("align", align);
        headerMap.put("minWidth", 70);
        headerMapList.add(ObjectUtil.clone(headerMap));
    }

    /**
     * 组合规则属性算法
     *
     * @param fromatDetailDTOList
     * @return DetailDto
     */
    private DetailDto attrFormat(List<FromatDetailDto> fromatDetailDTOList) {

        List<String> data = new ArrayList<>();
        List<Map<String, Map<String, String>>> res = new ArrayList<>();

        fromatDetailDTOList.stream()
                .map(FromatDetailDto::getDetail)
                .forEach(i -> {
                    if (i == null || i.isEmpty()) {
                        throw new MshopException("请至少添加一个规格值哦");
                    }
                    String str = ArrayUtil.join(i.toArray(), ",");
                    if (str.contains("-")) {
                        throw new MshopException("规格值里包含'-',请重新添加");
                    }
                });

        if (fromatDetailDTOList.size() > 1) {
            for (int i = 0; i < fromatDetailDTOList.size() - 1; i++) {
                if (i == 0) {
                    data = fromatDetailDTOList.get(i).getDetail();
                }
                List<String> tmp = new LinkedList<>();
                for (String v : data) {
                    for (String g : fromatDetailDTOList.get(i + 1).getDetail()) {
                        String rep2 = "";
                        if (i == 0) {
                            rep2 = fromatDetailDTOList.get(i).getValue() + "_" + v + "-"
                                    + fromatDetailDTOList.get(i + 1).getValue() + "_" + g;
                        } else {
                            rep2 = v + "-"
                                    + fromatDetailDTOList.get(i + 1).getValue() + "_" + g;
                        }

                        tmp.add(rep2);

                        if (i == fromatDetailDTOList.size() - 2) {
                            Map<String, Map<String, String>> rep4 = new LinkedHashMap<>();
                            Map<String, String> reptemp = new LinkedHashMap<>();
                            for (String h : Arrays.asList(rep2.split("-"))) {
                                List<String> rep3 = Arrays.asList(h.split("_"));
                                if (rep3.size() > 1) {
                                    reptemp.put(rep3.get(0), rep3.get(1));
                                } else {
                                    reptemp.put(rep3.get(0), "");
                                }
                            }
                            rep4.put("detail", reptemp);

                            res.add(rep4);
                        }
                    }

                }

                if (!tmp.isEmpty()) {
                    data = tmp;
                }
            }
        } else {
            List<String> dataArr = new ArrayList<>();
            for (FromatDetailDto fromatDetailDTO : fromatDetailDTOList) {
                for (String str : fromatDetailDTO.getDetail()) {
                    Map<String, Map<String, String>> map2 = new LinkedHashMap<>();
                    dataArr.add(fromatDetailDTO.getValue() + "_" + str);
                    Map<String, String> map1 = new LinkedHashMap<>();
                    map1.put(fromatDetailDTO.getValue(), str);
                    map2.put("detail", map1);
                    res.add(map2);
                }
            }
            String s = StrUtil.join("-", dataArr);
            data.add(s);
        }

        DetailDto detailDto = new DetailDto();
        detailDto.setData(data);
        detailDto.setRes(res);

        return detailDto;
    }

    @Override
    public void deleteForwardImg(Long id) {
        baseMapper.deleteForwardImg(id, "_product_detail_wap");
    }

}
