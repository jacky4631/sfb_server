/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.pay.domain.MwPayBind;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwPayBindService extends BaseService<MwPayBind>{
    MwPayBind findBindChannel(Long uid, Integer payType);

    void bindChannel(Long uid , Long channelId, Integer payType);

    void rebindChannel(Long uid , Long channelId, Integer payType);
}
