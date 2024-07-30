package com.mailvor.modules.mp.vo;

import com.mailvor.modules.mp.service.dto.MwWechatLiveDto;
import lombok.Data;

import java.util.List;

@Data
public class WechatLiveVo {

    private List<MwWechatLiveDto> content;

    private Long totalElements;

    private Integer pageNumber;

    private Integer lastPage;


}
