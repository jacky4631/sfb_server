package com.dtk.api.response.special;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 15:12
 */
@Getter
@Setter
public class DtkTipOffResponse {
    private List<ListInfo> list;
    private List<TimeOption> timeOption;
    private String totalNum;
    private String pageId;

    @Data
    public static class ListInfo {
        private String itemId;
        private String type;
        private String img;
        private String title;
        private String remark;
        private String url;
        private String activityPrice;
        private String price;
        private String commissionRate;
        private String couponPrice;
        private String couponStartTime;
        private String couponEndTime;
    }

    @Data
    public static class TimeOption {
        private String label;
        private String value;
    }


}
