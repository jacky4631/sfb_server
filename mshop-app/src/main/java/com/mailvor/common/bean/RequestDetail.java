/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * 请求详情信息
 * @author mazhongjun
 * @date 2020-04-30
 */
@Data
@Accessors(chain = true)
public class RequestDetail implements Serializable {
	private static final long serialVersionUID = 2543641512850125440L;

	/**
     * 请求ip地址
     */
    private String ip;

    /**
     * 请求路径
     */
    private String path;

}
