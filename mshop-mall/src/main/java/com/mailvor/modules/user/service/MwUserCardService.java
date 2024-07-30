/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserCard;
import com.mailvor.modules.user.service.dto.MwUserCardQueryCriteria;
import com.mailvor.modules.user.vo.MwUserCardQueryVo;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserCardService extends BaseService<MwUserCard>{
    MwUserCardQueryVo getUserCardById(Long uid);
    MwUserCard getByCardNo(String cardNo);
    Map<String, Object> queryAll(MwUserCardQueryCriteria criteria, Pageable pageable);

    void clearSignature(Long uid);

    String generateContract(Long uid, MwUserCard card) throws Exception;
    String generateContract(Long uid, MwUserCard card, boolean convert) throws Exception;
    void cleanCard(Long uid);
    void cleanCardBank(Long uid);
    void cleanCardPhone(Long uid);

    void re(String id) throws Exception;
    void convertContract(Long uid);
}
