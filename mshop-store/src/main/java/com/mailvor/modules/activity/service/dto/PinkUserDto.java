package com.mailvor.modules.activity.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.activity.domain.MwStorePink;
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
    @Schema(description = "拼团的团员信息")
    private List<MwStorePink> pinkAll;

    /**单个拼团信息*/
    @Schema(description = "单个拼团信息")
    private MwStorePink pinkT;

    /**拼团id集合*/
    @Schema(description = "拼团id集合")
    private List<Long> idAll;

    /**拼团用户id集合*/
    @Schema(description = "拼团用户id集合")
    private List<Long> uidAll;

    /**还差几人成团*/
    @Schema(description = "还差几人成团")
    private Integer count;
}
