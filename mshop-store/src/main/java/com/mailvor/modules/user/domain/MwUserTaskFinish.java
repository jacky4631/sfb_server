package com.mailvor.modules.user.domain;


import com.mailvor.domain.BaseDomain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty(value = "任务id")
    private Integer taskId;

    @ApiModelProperty(value = "用户id")
    private Long uid;

    @ApiModelProperty(value = "是否有效")
    private Integer status;


}
