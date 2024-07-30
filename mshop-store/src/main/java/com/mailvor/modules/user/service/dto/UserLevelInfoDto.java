package com.mailvor.modules.user.service.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @ClassName UserLevelInfoDto
 * @author huangyu
 * @Date 2019/12/7
 **/
@Data
public class UserLevelInfoDto implements Serializable {

    private Integer id;

    /** 会员名称 */
    private String name;


    /** 购买金额 */
    private BigDecimal money;


    /** 有效时间 */
    private Integer validDate;


    /** 是否为永久会员 */
    private Integer isForever;


    /** 是否购买,1=购买,0=不购买 */
    private Integer isPay;


    /** 是否显示 1=显示,0=隐藏 */
    private Integer isShow;


    /** 会员等级 */
    private Integer grade;


    /** 返利比例 */
    private BigDecimal discount;

    /** 下级返利比例 */
    private BigDecimal discountOne;

    /** 下下级返利比例 */
    private BigDecimal discountTwo;

    /** 会员卡背景 */
    private String image;

    /** 会员图标 */
    private String icon;


    /** 说明 */
    private String explain;

    private String rechargeId;
    /**
     *  用户类型 pdd、jd、vip、dy
     *  默认null，通用类型，tb使用该类型
     * */
    private String type;

}
