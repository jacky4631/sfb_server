/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.print;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 打印订单数据
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPrint {
	private String title;
	private String price;
	private String num;
}
