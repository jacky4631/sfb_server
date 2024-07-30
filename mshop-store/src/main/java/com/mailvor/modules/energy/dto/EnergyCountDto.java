package com.mailvor.modules.energy.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnergyCountDto {
    private Integer self;

    private Integer one;

    private Integer two;
}
