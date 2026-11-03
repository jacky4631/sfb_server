package com.mailvor.modules.activity.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <p>
 * 砍价用户帮助表
 * </p>
 *
 * @author huangyu
 * @since 2019-12-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "砍价用户帮助表")
public class MwStoreBargainUserHelp extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @Schema(description = "砍价用户帮助表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "帮助的用户id")
    private Long uid;

    @Schema(description = "砍价产品ID")
    private Long bargainId;

    @Schema(description = "用户参与砍价表id")
    private Long bargainUserId;

    @Schema(description = "帮助砍价多少金额")
    private BigDecimal price;

}
