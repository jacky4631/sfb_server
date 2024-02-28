/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;


import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.vo.MwUserBankQueryVo;

import java.util.List;

/**
 * <p>
 * 用户地址表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-10-28
 */
public interface MwUserBankService extends BaseService<MwUserBank> {

    /**
     * 设置默认地址
     * @param uid uid
     * @param bankId 地址id
     */
    void setDefault(Long uid,Long bankId);
    void setExtractDefault(Long uid,Long bankId);
    /**
     * 获取用户地址
     * @param uid uid
     * @param page page
     * @param limit limit
     * @return List
     */
    List<MwUserBankQueryVo> getList(Long uid, int page, int limit);
    List<MwUserBankQueryVo> getExtractList(Long uid, int page, int limit);
    /**
     * 获取默认地址
     * @param uid uid
     * @return MwUserAddress
     */
    MwUserBank getUserDefaultBank(Long uid);

    MwUserBank findOne(Long uid, String bankNo);
    MwUserBank findByRequestNo(String protocolNo);
}
