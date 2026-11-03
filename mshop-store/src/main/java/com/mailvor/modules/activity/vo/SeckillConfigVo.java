package com.mailvor.modules.activity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import com.mailvor.modules.activity.service.dto.SeckillTimeDto;
import lombok.Data;

import java.util.List;

@Data
public class SeckillConfigVo {

    @Schema(description = "秒杀产品时间信息")
    private List<SeckillTimeDto> seckillTime;

    private String lovely;

    @Schema(description = "秒杀产品时间索引")
    private Integer seckillTimeIndex;
}
