package com.mailvor.modules.energy.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.mailvor.domain.BaseDomain;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 用户热度记录
 * </p>
 *
 * @author huangyu
 * @since 2023-2-4
 */
@Data
public class UserEnergyLogDto extends BaseDomain {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long uid;
    private Long oid;
    private BigDecimal energy;
    private Integer type;

    private String platform;

    private BigDecimal totalEnergy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

}
