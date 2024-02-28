package com.mailvor.modules.activity.vo;


import com.mailvor.modules.user.vo.MwUserQueryVo;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName BargainVo
 * @author huangyu
 * @Date 2019/12/21
 **/
@Data
@Builder
public class BargainVo implements Serializable {
    private MwStoreBargainQueryVo bargain;
    private MwUserQueryVo userInfo;
    private Long bargainSumCount;//砍价支付成功订单数量
}
