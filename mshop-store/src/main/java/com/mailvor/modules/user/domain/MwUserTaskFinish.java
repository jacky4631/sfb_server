package com.mailvor.modules.user.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户任务完成记录表
 * </p>
 *
 * @author huangyu
 * @since 2019-12-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mw_user_task_finish")
public class MwUserTaskFinish extends BaseDomain {


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "任务id")
    private Integer taskId;

    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "是否有效")
    private Integer status;


}
