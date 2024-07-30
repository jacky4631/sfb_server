/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.modules.pay.domain.MwPayChannel;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.dto.PayChannelQueryCriteria;
import com.mailvor.modules.pay.enums.PayChannelEnum;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwPayChannelService extends BaseService<MwPayChannel>{

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(PayChannelQueryCriteria criteria, Pageable pageable);

    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MWSystemGroupDataDto>
    */
    List<MwPayChannel> queryAll(PayChannelQueryCriteria criteria);

    MwPayChannel randomChannel(Integer payType);

    MwPayChannel selectChannel(Integer payType, Long companyId);

    MwPayChannel decPrice(BigDecimal orderPrice, Long channelId);

    /**
     * Channel mw pay channel.
     *
     * @param uid     the uid
     * @param payType 支付类型 1=支付宝 2=微信 3=银行卡 4=银行卡绑卡 其他未知
     * @return the mw pay channel
     */
    MwPayChannel channel(Long uid, Integer payType);

    /**
     * Channel dto pay channel dto.
     *
     * @param uid        the uid
     * @param payType    支付类型 1=支付宝 2=微信 3=银行卡 4=银行卡绑卡 其他未知
     * @return the pay channel dto
     */
    PayChannelDto channelDto(Long uid, Integer payType);

    int reset();

    PayChannelDto getExtractChannel(String extractType);

    PayChannelDto getChannel(Long channelId);

    /**
     * Gets pay channel.
     *
     * @param channelEnum the channel enum
     * @param typeEnum    the type enum
     * @return the pay channel
     */
    PayChannelDto getPayChannel(PayChannelEnum channelEnum, PayTypeEnum typeEnum);
}
