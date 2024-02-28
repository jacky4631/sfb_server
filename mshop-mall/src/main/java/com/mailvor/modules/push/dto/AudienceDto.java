package com.mailvor.modules.push.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AudienceDto {
    private List<String> alias = new ArrayList<>();
}
