package com.mailvor.modules.user.vo;

import com.mailvor.modules.user.service.dto.MUserBillDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @ClassName BillVo
 * @author huangyu
 * @Date 2019/11/12
 **/
@Data
public class BillVo {
    private String time;
    @JsonIgnore
    private String ids;
    private List<MUserBillDto> list;
}
