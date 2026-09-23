package com.dtk.api.response.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 联盟搜索响应结果商品图片实体
 *
 * @author 1
 * @date 2020/11/11 17:37
 */
@Getter
@Setter
public class DtkGetTbServiceImagesResponse {
    private List<String> string;
}
