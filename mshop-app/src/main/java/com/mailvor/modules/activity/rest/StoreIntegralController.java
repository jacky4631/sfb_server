package com.mailvor.modules.activity.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mailvor.api.ApiResult;
import com.mailvor.modules.product.param.MwStoreProductQueryParam;
import com.mailvor.modules.product.service.MwStoreProductService;
import com.mailvor.modules.product.vo.MwStoreProductQueryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 积分兑换前端控制器
 * @author mshop
 */
@Slf4j
@RestController
@RequestMapping
@Tag(name = "营销:积分兑换")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreIntegralController {

    private final MwStoreProductService storeProductService;

    /**
     * 获取积分产品列表
     */
    @GetMapping("/products/integral")
    @Operation(summary = "获取积分产品列表", description = "获取积分产品列表")
    public ApiResult<List<MwStoreProductQueryVo>> goodsList(MwStoreProductQueryParam productQueryParam){
        return ApiResult.ok(storeProductService.getGoodsList(productQueryParam));
    }
}
