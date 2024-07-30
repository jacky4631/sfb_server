/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserPool;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserPoolService extends BaseService<MwUserPool>{

    Integer getRefund(Long uid);
    Integer addRefund(Long uid);

    void resetRefund(Long uid);

    void setRefund(Long uid, Integer refund);
}
