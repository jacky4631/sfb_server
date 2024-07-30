/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.user.domain.MwUserFeeLog;
import com.mailvor.modules.user.domain.MwUserFeeLogOpt;
import com.mailvor.modules.user.service.MwUserFeeLogOptService;
import com.mailvor.modules.user.service.mapper.UserFeeLogOptMapper;
import com.mailvor.modules.utils.FeeUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
public class MwUserFeeLogOptServiceImpl extends BaseServiceImpl<UserFeeLogOptMapper, MwUserFeeLogOpt> implements MwUserFeeLogOptService {

    private final IGenerator generator;
    private final UserFeeLogOptMapper feeLogMapper;

    @Override
    public List<MwUserFeeLog> feeList(Long uid, Integer cid, Integer type) {
        //type 1=今日 2=昨日 3=本月 4=上月 5=近30日
        return feeList(uid, type).stream().filter(feeLog -> feeLog.getCid().equals(cid)).collect(toList());
    }
    @Override
    public List<MwUserFeeLog> feeList(Long uid, Integer type) {
        //type 1=今日 2=昨日 3=本月 4=上月 5=近30日
        MwUserFeeLogOpt opt = this.getById(uid);
        if(opt == null) {
            return Collections.emptyList();
        }

        return FeeUtil.getCurrentLog(type, opt);

    }
    @Override
    public Map<String, MwUserFeeLog> userFeeDetail(Long uid, Integer cid, Integer type) {
        //type 1=今日 2=昨日 3=本月 4=上月 5=近30日
        List<MwUserFeeLog> feeLogs = feeList(uid, cid, type);
        feeLogs.forEach(mwUserFeeLog -> mwUserFeeLog.setFeeValue(null));
        return feeLogs.stream().collect(Collectors.toMap(MwUserFeeLog::getPlatform, Function.identity()));

    }

    @Override
    public int mysqlInsertOrUpdateBath(List list) {
        return feeLogMapper.mysqlInsertOrUpdateBath(list);
    }
}
