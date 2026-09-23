package com.dtk.api.response.special;

import lombok.Getter;
import lombok.Setter;

/**
 * @author:YSH
 * @date: 2021/7/8
 * @time: 14:56
 */
@Getter
@Setter
public class DtkCarouselMapResponseResponse {
    private String topicId;
    private String topicName;
    private String topicImage;
    private String link;
    private String sourceType;
    private String activityId;
}
