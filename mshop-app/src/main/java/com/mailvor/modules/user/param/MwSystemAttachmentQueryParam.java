package com.mailvor.modules.user.param;

import com.mailvor.common.web.param.QueryParam;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 附件管理表 查询参数对象
 * </p>
 *
 * @author huangyu
 * @date 2019-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="MwSystemAttachmentQueryParam对象", description="附件管理表查询参数")
public class MwSystemAttachmentQueryParam extends QueryParam {
    private static final long serialVersionUID = 1L;
}
