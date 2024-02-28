package com.mailvor.modules.activity.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TopCountVo
 * @author huangyu
 * @Date 2019/12/21
 **/
@Data
@Builder
public class TopCountVo implements Serializable {
    private Integer lookCount;
    private Integer shareCount;
    private Long userCount;


}
