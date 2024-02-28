package com.mailvor.modules.user.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PromParam
 * @author huangyu
 * @Date 2019/11/12
 **/
@Data
public class PromFeeParam implements Serializable {
    @NotEmpty
    @ApiModelProperty(value = "1级用户uid")
    private List<Long> uids;
}
