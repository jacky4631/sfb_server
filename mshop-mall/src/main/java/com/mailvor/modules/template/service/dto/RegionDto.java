package com.mailvor.modules.template.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @ClassName RegionDto
 * @author mazhongjun
 * @Date 2020/5/25
 **/
@Getter
@Setter
public class RegionDto {

    private String name;

    private String city_id;

    List<RegionChildrenDto> children;

}
