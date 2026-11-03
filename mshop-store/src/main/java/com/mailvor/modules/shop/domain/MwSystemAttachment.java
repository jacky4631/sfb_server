package com.mailvor.modules.shop.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 附件管理表
 * </p>
 *
 * @author huangyu
 * @since 2019-11-11
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "附件管理表")
public class MwSystemAttachment extends BaseDomain {

    private static final long serialVersionUID = 1L;

    @TableId(value = "att_id", type = IdType.AUTO)
    private Long attId;

    @Schema(description = "附件名称")
    private String name;

    @Schema(description = "附件路径")
    private String attDir;

    @Schema(description = "压缩图片路径")
    private String sattDir;

    @Schema(description = "附件大小")
    private String attSize;

    @Schema(description = "附件类型")
    private String attType;

    @Schema(description = "分类ID0编辑器,1产品图片,2拼团图片,3砍价图片,4秒杀图片,5文章图片,6组合数据图")
    private Integer pid;

    @Schema(description = "图片上传类型 1本地 2七牛云 3OSS 4COS ")
    private Integer imageType;

    @Schema(description = "图片上传模块类型 1 后台上传 2 用户生成")
    private Integer moduleType;

    private Long uid;

    private String inviteCode;

}
