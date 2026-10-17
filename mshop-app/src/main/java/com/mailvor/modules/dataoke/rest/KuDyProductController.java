package com.mailvor.modules.dataoke.rest;

import cn.hutool.core.bean.BeanUtil;
import com.mailvor.api.ApiResult;
import com.mailvor.common.bean.LocalUser;
import com.mailvor.common.interceptor.UserCheck;
import com.mailvor.modules.tk.param.GoodsListDyParam;
import com.mailvor.modules.tk.service.KuService;
import com.mailvor.modules.tk.vo.*;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/** Product endpoints used by the TSQ app, adapted to SFB's response envelope. */
@RestController
@RequestMapping("/ku/dy/goods")
public class KuDyProductController {
    @Resource private KuService kuService;

    private void requireSuccess(Integer code, String message) {
        if (!Integer.valueOf(0).equals(code) && !Integer.valueOf(200).equals(code)) {
            throw new IllegalStateException(message == null ? "抖音商品服务暂不可用" : message);
        }
    }

    @GetMapping("/cate")
    public ApiResult<List<Map<String, Object>>> categories() {
        DyCateDataVO result = kuService.dyProductCateList();
        requireSuccess(result == null ? null : result.getCode(), result == null ? null : result.getMsg());
        return ApiResult.ok(result.getData() == null ? Collections.emptyList() :
                result.getData().stream().map(BeanUtil::beanToMap).collect(Collectors.toList()));
    }

    @GetMapping("/list")
    public ApiResult<Map<String, Object>> list(GoodsListDyParam param) {
        DySearchListVO result = kuService.dyProductList(param);
        requireSuccess(result == null ? null : result.getCode(), result == null ? null : result.getMsg());
        Map<String, Object> data = new HashMap<>();
        data.put("minId", result.getMinId());
        data.put("list", result.getData() == null ? Collections.emptyList() :
                result.getData().stream().map(BeanUtil::beanToMap).collect(Collectors.toList()));
        return ApiResult.ok(data);
    }

    @GetMapping("/detail")
    public ApiResult<Map<String, Object>> detail(@RequestParam String goodsId) {
        DyGoodsDetailDataVo result = kuService.dyProductDetail(goodsId);
        requireSuccess(result == null ? null : result.getCode(), result == null ? null : result.getMsg());
        return ApiResult.ok(result.getDetail() == null ? Collections.emptyMap() : BeanUtil.beanToMap(result.getDetail()));
    }

    @UserCheck
    @GetMapping("/word")
    public ApiResult<Map<String, Object>> word(@RequestParam String goodsId) {
        String channel = LocalUser.getUser() == null ? "0" : LocalUser.getUser().getUid().toString();
        DyWordGoodsDataVO result = kuService.dyProductWord(goodsId, channel);
        requireSuccess(result == null ? null : result.getCode(), result == null ? null : result.getMsg());
        DyWordGoodsVO word = result.getData();
        if (word == null || ((word.getLink() == null || word.getLink().isEmpty()) &&
                (word.getLongLink() == null || word.getLongLink().isEmpty()))) {
            throw new IllegalStateException("抖音转链失败，请稍后重试");
        }
        return ApiResult.ok(BeanUtil.beanToMap(word));
    }
}
