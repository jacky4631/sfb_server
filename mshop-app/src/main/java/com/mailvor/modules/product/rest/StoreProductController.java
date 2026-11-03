/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.rest;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.mailvor.api.ApiResult;
import com.mailvor.api.MshopException;
import com.mailvor.common.aop.NoRepeatSubmit;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.AuthCheck;
import com.mailvor.constant.ShopConstants;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.domain.PageResult;
import com.mailvor.enums.AppFromEnum;
import com.mailvor.enums.ProductEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.logging.aop.log.AppLog;
import com.mailvor.modules.product.domain.MwStoreProduct;
import com.mailvor.modules.product.param.CollectDelFootParam;
import com.mailvor.modules.product.param.MwStoreProductQueryParam;
import com.mailvor.modules.product.param.MwStoreProductRelationQueryParam;
import com.mailvor.modules.product.service.MwStoreProductRelationService;
import com.mailvor.modules.product.service.MwStoreProductReplyService;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import com.mailvor.modules.product.vo.MwStoreProductReplyQueryVo;
import com.mailvor.modules.product.vo.ProductVo;
import com.mailvor.modules.product.vo.ReplyCountVo;
import com.mailvor.modules.services.CreatShareProductService;
import com.mailvor.modules.shop.domain.MwSystemAttachment;
import com.mailvor.modules.shop.service.MwSystemAttachmentService;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.tk.domain.MailvorMtOrder;
import com.mailvor.modules.tk.service.*;
import com.mailvor.modules.tk.service.dto.*;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品控制器
 * </p>
 *
 * @author huangyu
 * @since 2019-10-19
 */
@Slf4j
@RestController
@Tag(name = "商城：产品模块")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreProductController {

    private final MwStoreProductService storeProductService;
    private final MwStoreProductRelationService productRelationService;
    private final MwStoreProductReplyService replyService;
    private final MwSystemConfigService systemConfigService;
    private final MwSystemAttachmentService systemAttachmentService;
    private final CreatShareProductService creatShareProductService;
    private final  MailvorTbOrderService tbOrderService;
    private final  MailvorJdOrderService jdOrderService;
    private final  MailvorPddOrderService pddOrderService;
    private final  MailvorVipOrderService vipOrderService;
    private final  MailvorDyOrderService dyOrderService;
    private final  MailvorMtOrderService mtOrderService;

    private final MwUserService userService;

    @Value("${file.path}")
    private String path;


    /**
     * 获取首页更多产品
     */
    @GetMapping("/groom/list/{type}")
    @Parameters({
            @Parameter(name = "type", description = "类型：1精品推荐，2热门榜单，3首发新品，4促销单品")
    })
    @Operation(summary = "获取首页更多产品", description = "获取首页更多产品")
    public ApiResult<Map<String,Object>> moreGoodsList(@PathVariable Integer type){
        Map<String,Object> map = new LinkedHashMap<>();
        // 精品推荐
        if(ProductEnum.TYPE_1.getValue().equals(type)){
            map.put("list",storeProductService.getList(1,20,ProductEnum.TYPE_1.getValue()));
        // 热门榜单
        }else if(type.equals(ProductEnum.TYPE_2.getValue())){
            map.put("list",storeProductService.getList(1,20,ProductEnum.TYPE_2.getValue()));
        // 首发新品
        }else if(type.equals(ProductEnum.TYPE_3.getValue())){
            map.put("list",storeProductService.getList(1,20,ProductEnum.TYPE_3.getValue()));
        // 促销单品
        }else if(type.equals(ProductEnum.TYPE_4.getValue())){
            map.put("list",storeProductService.getList(1,20,ProductEnum.TYPE_4.getValue()));
        }

        return ApiResult.ok(map);
    }

    /**
     * 获取产品列表
     */
    @GetMapping("/products")
    @Operation(summary = "商品列表", description = "商品列表")
    public ApiResult<List<MwStoreProductQueryVo>> goodsList(MwStoreProductQueryParam productQueryParam){
        return ApiResult.ok(storeProductService.getGoodsList(productQueryParam));
    }

    /**
     * 为你推荐
     */
    @GetMapping("/product/hot")
    @Operation(summary = "为你推荐", description = "为你推荐")
    public ApiResult<List<MwStoreProductQueryVo>> productRecommend(MwStoreProductQueryParam queryParam){
        return ApiResult.ok(storeProductService.getList(queryParam.getPage(), queryParam.getLimit(),
                ShopCommonEnum.IS_STATUS_1.getValue()));
    }

    /**
     * 商品详情海报
     */
    @AppLog(value = "商品详情海报", type = 1)
    @AuthCheck
    @GetMapping("/product/poster/{id}")
    @Parameters({
            @Parameter(name = "id", description = "商品ID")
    })
    @Operation(summary = "商品详情海报", description = "商品详情海报")
    public ApiResult<String> prodoctPoster(@PathVariable Integer id,@RequestParam(value = "from",defaultValue = "h5") String from) throws IOException, FontFormatException {
        MwUser userInfo = LocalUser.getUser();

        long uid = userInfo.getUid();

        MwStoreProduct storeProduct = storeProductService.getById(id);
        // 海报
        String siteUrl = systemConfigService.getData(SystemConfigConstants.SITE_URL);
        if(StrUtil.isEmpty(siteUrl)){
            return ApiResult.fail("未配置h5地址");
        }
        String apiUrl = systemConfigService.getData(SystemConfigConstants.API_URL);
        if(StrUtil.isEmpty(apiUrl)){
            return ApiResult.fail("未配置api地址");
        }
        String name = id+"_"+uid + "_"+from+"_product_detail_wap.jpg";
        MwSystemAttachment attachment = systemAttachmentService.getInfo(name);
        String sepa = File.separator;
        String fileDir = path+"qrcode"+ sepa;
        String qrcodeUrl = "";
        if(ObjectUtil.isNull(attachment)){
            File file = FileUtil.mkdir(new File(fileDir));
            //如果类型是小程序
            if(AppFromEnum.ROUNTINE.getValue().equals(from)){
                siteUrl = siteUrl+"/product/";
                //生成二维码
                QrCodeUtil.generate(siteUrl+"?id="+id+"&spread="+uid+"&pageType=good&codeType="+AppFromEnum.ROUNTINE.getValue(), 180, 180,
                        FileUtil.file(fileDir+name));
            }
            else if(AppFromEnum.APP.getValue().equals(from)){
                siteUrl = siteUrl+"/product/";
                //生成二维码
                QrCodeUtil.generate(siteUrl+"?id="+id+"&spread="+uid+"&pageType=good&codeType="+AppFromEnum.APP.getValue(), 180, 180,
                        FileUtil.file(fileDir+name));
            //如果类型是h5
            }else if(AppFromEnum.H5.getValue().equals(from)){
                //生成二维码
                QrCodeUtil.generate(siteUrl+"/detail/"+id+"?spread="+uid, 180, 180,
                        FileUtil.file(fileDir+name));
            }else {
                //生成二维码
                String uniUrl = systemConfigService.getData(SystemConfigConstants.UNI_SITE_URL);
                siteUrl =  StrUtil.isNotBlank(uniUrl) ? uniUrl :  ShopConstants.DEFAULT_UNI_H5_URL;
                QrCodeUtil.generate(siteUrl+"/pages/shop/GoodsCon/index?id="+id+"&spread="+uid, 180, 180,
                        FileUtil.file(fileDir+name));
            }
            systemAttachmentService.attachmentAdd(name,String.valueOf(FileUtil.size(file)),
                    fileDir+name,"qrcode/"+name);

            qrcodeUrl = apiUrl + "/api/file/qrcode/"+name;
        }else{
            qrcodeUrl = apiUrl + "/api/file/" + attachment.getSattDir();
        }
        String spreadPicName = id+"_"+uid + "_"+from+"_product_user_spread.jpg";
        String spreadPicPath = fileDir+spreadPicName;
        String rr =  creatShareProductService.creatProductPic(storeProduct,qrcodeUrl,
                spreadPicName,spreadPicPath,apiUrl);
        return ApiResult.ok(rr);
    }



    /**
     * 普通商品详情
     */
    //@AppLog(value = "普通商品详情", type = 1)
    //@AuthCheck
    @GetMapping("/product/detail/{id}")
    @Parameters({
            @Parameter(name = "id", description = "商品ID", required = true),
            @Parameter(name = "latitude", description = "纬度"),
            @Parameter(name = "longitude", description = "经度"),
            @Parameter(name = "from", description = "来自:")
    })
    @Operation(summary = "普通商品详情", description = "普通商品详情")
    public ApiResult<ProductVo> detail(@PathVariable long id,
                                       @RequestParam(value = "",required=false) String latitude,
                                       @RequestParam(value = "",required=false) String longitude,
                                       @RequestParam(value = "",required=false) String from)  {
        long uid = LocalUser.getUidByToken();
        storeProductService.incBrowseNum(id);
        ProductVo productDTO = storeProductService.goodsDetail(id,uid,latitude,longitude);
        return ApiResult.ok(productDTO);
    }

    /**
     * 添加收藏
     */
    @AppLog(value = "添加收藏", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/collect/add")
    @Operation(summary = "添加收藏", description = "添加收藏")
    public ApiResult<Boolean> collectAdd(@Validated @RequestBody MwStoreProductRelationQueryParam param){
        long uid = LocalUser.getUser().getUid();
        productRelationService.addProductRelation(param.getId(),uid,param.getCategory(),
                param.getImg(), param.getTitle(), param.getStartPrice(), param.getEndPrice(), param.getOriginalId());
        return ApiResult.ok();
    }

    /**
     * 取消收藏
     */
    @AppLog(value = "取消收藏", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/collect/del")
    @Operation(summary = "取消收藏", description = "取消收藏")
    public ApiResult<Boolean> collectDel(@Validated @RequestBody MwStoreProductRelationQueryParam param){
        long uid = LocalUser.getUser().getUid();
        productRelationService.delProductRelation(param.getId(), uid);
        return ApiResult.ok();
    }
    /**
     * 取消收藏
     */
    @AppLog(value = "获取商品是否收藏", type = 1)
    @AuthCheck
    @GetMapping("/collect")
    @Operation(summary = "获取商品是否收藏", description = "获取商品是否收藏")
    public ApiResult<Boolean> collect(String id){
        long uid = LocalUser.getUser().getUid();
        boolean relation = productRelationService.isProductRelation(id,
                uid);
        return ApiResult.ok(relation);
    }
    /**
     * 添加收藏
     */
    @AppLog(value = "添加足迹", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @PostMapping("/collect/addFoot")
    @Operation(summary = "添加足迹", description = "添加足迹")
    public ApiResult<Boolean> footAdd(@Validated @RequestBody MwStoreProductRelationQueryParam param){
        long uid = LocalUser.getUser().getUid();
        productRelationService.addProductFoot(param.getId(),uid,param.getCategory(),
                param.getImg(), param.getTitle(), param.getStartPrice(), param.getEndPrice(), param.getOriginalId());
        return ApiResult.ok();
    }
    /**
     * 取消收藏/足跡
     */
    @AppLog(value = "删除足跡", type = 1)
    @NoRepeatSubmit
    @AuthCheck
    @DeleteMapping("/collect/delFoot")
    @Operation(summary = "删除足跡", description = "删除足跡")
    public ApiResult<Boolean> collectDelFoot(@Validated @RequestBody CollectDelFootParam param){
        if (CollectionUtil.isEmpty(param.getIds())){
            throw new MshopException("参数非法");
        }
        productRelationService.collectDelFoot(param.getIds());
        return ApiResult.ok();
    }

    /**
     * 获取产品评论
     */
    @GetMapping("/reply/list/{id}")
    @Parameters({
            @Parameter(name = "id", description = "商品ID", required = true),
            @Parameter(name = "type", description = "评论分数类型"),
            @Parameter(name = "page", description = "页码,默认为1"),
            @Parameter(name = "limit", description = "页大小,默认为10")
    })
    @Operation(summary = "获取产品评论", description = "获取产品评论")
    public ApiResult<List<MwStoreProductReplyQueryVo>> replyList(@PathVariable Long id,
                                                                 @RequestParam(value = "type",defaultValue = "0") int type,
                                                                 @RequestParam(value = "page",defaultValue = "1") int page,
                                                                 @RequestParam(value = "limit",defaultValue = "10") int limit){
        return ApiResult.ok(replyService.getReplyList(id,type, page,limit));
    }

    /**
     * 获取产品评论数据
     */
    @GetMapping("/reply/config/{id}")
    @Parameters({
            @Parameter(name = "id", description = "商品ID")
    })
    @Operation(summary = "获取产品评论数据", description = "获取产品评论数据")
    public ApiResult<ReplyCountVo> replyCount(@PathVariable Integer id){
        return ApiResult.ok(replyService.getReplyCount(id));
    }

    /**
     * 获取淘宝订单列表
     */
    @AuthCheck
    @GetMapping("/tao/orders")
    @Operation(summary = "商品列表", description = "商品列表")
    public ResponseEntity<PageResult<MailvorTbOrderDto>> tbOrders(MailvorTbOrderQueryCriteria criteria, Pageable pageable){
        boolean hasChild = initCriteria(criteria);
        if(!hasChild) {
            return new ResponseEntity<>(new PageResult(0, Collections.emptyList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(tbOrderService.queryAll(criteria,pageable), HttpStatus.OK);
    }
    /**
     * 获取京东订单列表
     */
    @AuthCheck
    @GetMapping("/jd/orders")
    @Operation(summary = "商品列表", description = "商品列表")
    public ResponseEntity<PageResult<MailvorJdOrderDto>> jdOrders(MailvorJdOrderQueryCriteria criteria, Pageable pageable){
        boolean hasChild = initCriteria(criteria);
        if(!hasChild) {
            return new ResponseEntity<>(new PageResult(0, Collections.emptyList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(jdOrderService.queryAll(criteria,pageable), HttpStatus.OK);
    }
    /**
     * 获取拼多多订单列表
     */
    @AuthCheck
    @GetMapping("/pdd/orders")
    @Operation(summary = "商品列表", description = "商品列表")
    public ResponseEntity<PageResult<MailvorPddOrderDto>> pddOrders(MailvorPddOrderQueryCriteria criteria, Pageable pageable){
        boolean hasChild = initCriteria(criteria);
        if(!hasChild) {
            return new ResponseEntity<>(new PageResult(0, Collections.emptyList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(pddOrderService.queryAll(criteria,pageable), HttpStatus.OK);
    }
    /**
     * 获取唯品会订单列表
     */
    @AuthCheck
    @GetMapping("/vip/orders")
    @Operation(summary = "商品列表", description = "商品列表")
    public ResponseEntity<PageResult<MailvorVipOrderDto>> vipOrders(MailvorVipOrderQueryCriteria criteria, Pageable pageable){
        boolean hasChild = initCriteria(criteria);
        if(!hasChild) {
            return new ResponseEntity<>(new PageResult(0, Collections.emptyList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(vipOrderService.queryAll(criteria,pageable), HttpStatus.OK);
    }
    /**
     * 获取抖音订单列表
     */
    @AuthCheck
    @GetMapping("/dy/orders")
    @Operation(summary = "抖音订单列表", description = "抖音订单列表")
    public ResponseEntity<PageResult<MailvorDyOrderDto>> dyOrders(MailvorDyOrderQueryCriteria criteria, Pageable pageable){
        boolean hasChild = initCriteria(criteria);
        if(!hasChild) {
            return new ResponseEntity<>(new PageResult(0, Collections.emptyList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(dyOrderService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    /**
     * 获取淘宝订单列表
     */
    @AuthCheck
    @GetMapping("/mt/orders")
    @Operation(summary = "美团商品列表", description = "美团商品列表")
    public ResponseEntity<PageResult<MailvorMtOrder>> mtOrders(MailvorMtOrderQueryCriteria criteria, Pageable pageable){
        boolean hasChild = initCriteria(criteria);
        if(!hasChild) {
            return new ResponseEntity<>(new PageResult(0, Collections.emptyList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(mtOrderService.queryAll(criteria,pageable), HttpStatus.OK);
    }

    protected boolean initCriteria(MailvorOrderQueryCriteria criteria) {
        Long uid = LocalUser.getUser().getUid();
        Integer level = criteria.getLevel();
        if(level != null && level > 0) {
            Integer grade = 0;
            if(level == 2) {
                grade = 1;
            }
            List<MwUser> users = userService.getSpreadList(uid, grade);
            if(users.isEmpty()) {
                return false;
            }
            Set<Long> uids = users.stream().map(MwUser::getUid).collect(Collectors.toSet());
            criteria.setUids(uids);
        } else {
            criteria.setUid(uid);
        }
        return true;
    }
}

