package com.mailvor.modules.activity.vo;

import lombok.Data;

import java.util.List;

@Data
public class CombinationQueryVo {

    private List<MwStoreCombinationQueryVo> storeCombinationQueryVos;

    private Long lastPage;

}
