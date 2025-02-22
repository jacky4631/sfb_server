/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author huangyu
 * 账单明细相关枚举
 */
@Getter
@AllArgsConstructor
public enum BillDetailEnum {

	TYPE_1("recharge","充值"),
	TYPE_2("brokerage","返佣"),
	TYPE_3("pay_product","消费"),
	TYPE_4("extract","提现"),
	TYPE_5("pay_product_refund","退款"),
	TYPE_6("system_add","系统添加"),
	TYPE_7("system_sub","系统减少"),
	TYPE_8("deduction","减去"),
	TYPE_9("gain","奖励"),
	TYPE_10("sign","签到"),
	TYPE_11("order","下单"),

	TYPE_12("upgrade","加盟星选会员奖励"),
	TYPE_13("retail","订单分销奖励"),
	TYPE_14("retail","邀请用户奖励"),


	CATEGORY_1("now_money","金额"),
	CATEGORY_2("integral","积分");



	private String value;
	private String desc;


}
