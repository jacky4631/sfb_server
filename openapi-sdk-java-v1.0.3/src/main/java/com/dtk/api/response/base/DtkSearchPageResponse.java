package com.dtk.api.response.base;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * dtk api: page param，independent of business entity
 *
 * @author 1
 * @date 2020/11/9 17:15
 */
@Getter
@Setter
public class DtkSearchPageResponse<T> extends DtkPageResponse<T> {

    /**
     * 商品品牌信息
     */
    private List<BrandInfo> brandList;

    @Data
    public static class BrandInfo {
        private Long brandId;
        private String brandLogo;
        private String brandName;
    }
}
