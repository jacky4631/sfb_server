package com.dtk.api.response.special;

import lombok.Data;

/**
 * 官方活动推广响应结果实体
 *
 * @author baige
 * @date 2020/11/12 16:03
 */
@Data
public class DtkGetTbTopicListResponse {
    private Integer id;
    private String promotionSceneId;
    private String activityStartTime;
    private String activityEndTime;
    private String activityInfo;
    private Integer type;
    private String activityLink;
    private String materialLink;
    private String activityName;
}
