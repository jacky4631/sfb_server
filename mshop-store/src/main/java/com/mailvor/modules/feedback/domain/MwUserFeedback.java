/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.feedback.domain;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mailvor.domain.BaseDomain;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import javax.validation.constraints.*;

/**
* @author wangjun
* @date 2024-05-27
*/
@Data
@TableName("mw_user_feedback")
public class MwUserFeedback extends BaseDomain {
    /** id */
    @TableId
    private Long id;

    /** 用户id */
    @NotNull
    private Long uid;

    /** 反馈内容 */
    @NotBlank
    private String feedback;





    public void copy(MwUserFeedback source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
