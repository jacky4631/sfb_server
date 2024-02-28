/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserFeeLog;
import com.mailvor.modules.user.domain.MwUserFeeLogOpt;

import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-05-12
*/
public interface MwUserFeeLogOptService extends BaseService<MwUserFeeLogOpt>{
    List<MwUserFeeLog> feeList(Long uid, Integer cid, Integer type);
    List<MwUserFeeLog> feeList(Long uid, Integer type);
    Map<String, MwUserFeeLog> userFeeDetail(Long uid, Integer cid, Integer type);
    int mysqlInsertOrUpdateBath(List list);
}
