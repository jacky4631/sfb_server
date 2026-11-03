/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.utils;

import cn.hutool.core.util.ObjectUtil;
import com.mailvor.exception.BadRequestException;

/**
 * 验证工具
 * @author Zheng Jie
 * @date 2018-11-23
 */
public class ValidationUtil{

    /**
     * 验证空
     */
    public static void isNull(Object obj, String entity, String parameter , Object value){
        if(ObjectUtil.isNull(obj)){
            String msg = entity + " 不存在: "+ parameter +" is "+ value;
            throw new BadRequestException(msg);
        }
    }
}
