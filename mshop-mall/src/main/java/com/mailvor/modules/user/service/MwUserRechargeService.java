/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserRecharge;
import com.mailvor.modules.user.service.dto.MwUserRechargeDto;
import com.mailvor.modules.user.service.dto.MwUserRechargeQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserRechargeService extends BaseService<MwUserRecharge>{


    void updateRecharge(MwUserRecharge userRecharge);

    MwUserRecharge getInfoByOrderId(String orderId);

    /**
     * 添加充值记录
     * @param user 用户
     * @param price 充值金额
     */
    String addRecharge(MwUser user, String price, String rechargeType, Integer grade, String platform,String rechargeId, Long channelId);
    String addRecharge(MwUser user, String price, String payType, Integer grade,
                       String platform,String rechargeId, Long channelId, Long uid, Integer type);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwUserRechargeQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwUserRechargeDto>
    */
    List<MwUserRecharge> queryAll(MwUserRechargeQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwUserRechargeDto> all, HttpServletResponse response) throws IOException;

    MwUserRecharge getLatestPaidInfo(Long uid, String platform, Integer grade);

    Double totalRechargePrice(Integer type);

    MwUserRecharge getRecharge(String orderId);
}
