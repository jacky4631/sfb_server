package com.mailvor.modules.wechat.rest.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
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
@ApiModel(value="MwWechatTemplateQueryParam对象", description="微信模板查询参数")
public class MwWechatTemplateQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
