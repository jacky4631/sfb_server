package com.mailvor.modules.activity.service.dto;

import com.mailvor.modules.activity.domain.MwStorePink;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName PinkUserDto
 * @author mazhongjun
 * @Date 2020/6/22
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PinkUserDto {

    /**拼团的团员*/
    @ApiModelProperty(value = "拼团的团员信息")
    private List<MwStorePink> pinkAll;

    /**单个拼团信息*/
    @ApiModelProperty(value = "单个拼团信息")
    private MwStorePink pinkT;

    /**拼团id集合*/
    @ApiModelProperty(value = "拼团id集合")
    private List<Long> idAll;

    /**拼团用户id集合*/
    @ApiModelProperty(value = "拼团用户id集合")
    private List<Long> uidAll;

    /**还差几人成团*/
    @ApiModelProperty(value = "还差几人成团")
    private Integer count;
}
