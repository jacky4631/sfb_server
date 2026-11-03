package com.mailvor.modules.activity.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 砍价用户帮助表 查询结果对象
 * </p>
 *
 * @author huangyu
 * @date 2019-12-21
 */
@Data
@Schema(description = "砍价用户帮助表查询参数")
public class MwStoreBargainUserHelpQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "砍价用户帮助表ID")
    private Long id;

    @Schema(description = "帮助的用户id")
    private Long uid;

    @Schema(description = "砍价产品ID")
    private Long bargainId;

    @Schema(description = "用户参与砍价表id")
    private Long bargainUserId;

    @Schema(description = "帮助砍价多少金额")
    private BigDecimal price;

    private String nickname;

    private String avatar;

}
