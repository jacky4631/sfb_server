package com.mailvor.modules.wechat.rest.param;

import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.common.web.param.QueryParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 微信模板 查询参数对象
 * </p>
 *
 * @author mazhongjun
 * @date 2019-12-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "微信模板查询参数")
public class MwWechatTemplateQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
