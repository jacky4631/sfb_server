package com.dtk.api.response.special;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 16:38
 */
@Getter
@Setter
public class DtkAlbumListResponse {
    private String id;
    private String title;
    private String label;
    private String userName;
    private String headImg;
    private String userGoodAt;
    private String userDesc;
    private String goodsCount;
    private String likeNum;
    private String promoteCount;
    private String endType;
    private String releaseTime;
    private String endTime;
    private List<GoodsListMessage> goodsList;


    @Data
    public static class GoodsListMessage {
        private String pic;
        private String goodsid;
    }
}
