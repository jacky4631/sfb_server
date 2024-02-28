package com.mailvor.modules.energy.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户流量扶持记录
 * </p>
 *
 * @author huangyu
 * @since 2023-2-4
 */
@Data
public class UserEnergyOrderLogDto extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long uid;
    private Long uidOne;

    private Long uidTwo;

    private BigDecimal money;

    private Integer scale;

    private Integer scaleOne;

    private Integer scaleTwo;

    private String platform;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

}
