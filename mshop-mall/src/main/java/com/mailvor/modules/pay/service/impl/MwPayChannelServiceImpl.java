/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.modules.pay.domain.MwPayBind;
import com.mailvor.modules.pay.domain.MwPayChannel;
import com.mailvor.modules.pay.dto.PayChannelDto;
import com.mailvor.modules.pay.dto.PayChannelQueryCriteria;
import com.mailvor.modules.pay.service.MwPayBindService;
import com.mailvor.modules.pay.service.MwPayChannelService;
import com.mailvor.modules.pay.service.mapper.PayChannelMapper;
import com.mailvor.modules.utils.RsaUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.modules.config.RSAConfig.KEY_RSA_PRIVATE;
import static com.mailvor.modules.pay.enums.PayChannelEnum.*;
import static com.mailvor.modules.utils.PayUtil.*;
import static java.util.stream.Collectors.toList;


/**
* @author wangke
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwPayChannelServiceImpl extends BaseServiceImpl<PayChannelMapper, MwPayChannel> implements MwPayChannelService {

    @Resource
    private IGenerator generator;
    @Resource
    private PayChannelMapper mapper;
    @Resource
    private MwPayBindService payBindService;

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(PayChannelQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwPayChannel> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<PayChannelDto> payChannelDtos = generator.convert(page.getList(), PayChannelDto.class);

        map.put("content",payChannelDtos);
        map.put("totalElements",page.getTotal());
        return map;
    }

    @Override
    //@Cacheable
    public List<MwPayChannel> queryAll(PayChannelQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwPayChannel.class, criteria));
    }

    @Override
    public MwPayChannel randomChannel(Integer payType) {
        List filterTypeList;
        if(payType == 0) {
            filterTypeList = CHANNEL_TYPE_IOS_PAY;
        } else if(payType == 1) {
            filterTypeList = CHANNEL_TYPE_ALI_PAY;
        } else if(payType == 2) {
            filterTypeList = CHANNEL_TYPE_WECHAT;
        }else if(payType == 3) {
            filterTypeList = CHANNEL_TYPE_BANK;
        }else if(payType == 4) {
            filterTypeList = CHANNEL_TYPE_BANK_BIND;
        } else {
            filterTypeList = CHANNEL_TYPE_YUNSHANFU;
        }
        List<MwPayChannel> channelList = this.list(new LambdaQueryWrapper<MwPayChannel>()
                .in(MwPayChannel::getName, Arrays.asList(PAY_NAME, ""))
                .eq(MwPayChannel::getStatus, 8)
                .in(MwPayChannel::getType, filterTypeList));
        return filterChannel(channelList);
    }
    @Override
    public MwPayChannel selectChannel(Integer payType, Long companyId) {
        //优选同主体同平台通道，次选同主体不同平台通道，如果都无效，换绑定，app端提示合同重签
        //payType "支付类型 1=支付宝 2=微信 3=银行卡 其他未知"
        LambdaQueryWrapper<MwPayChannel> wrapper = new LambdaQueryWrapper<MwPayChannel>()
                .eq(MwPayChannel::getStatus, 8);

        if(companyId != null) {
            wrapper.eq(MwPayChannel::getCompanyId, companyId);
        }
        wrapper.in(MwPayChannel::getName, Arrays.asList(PAY_NAME, ""));

        List<MwPayChannel> channelList = list(wrapper);

        List<MwPayChannel> filterList;
        if(payType == 1) {
            filterList = channelList.stream().filter(channel -> CHANNEL_TYPE_ALI_PAY.contains(channel.getType())).collect(toList());
        } else if(payType == 2) {
            filterList = channelList.stream().filter(channel -> CHANNEL_TYPE_WECHAT.contains(channel.getType())).collect(toList());
        } else if(payType == 3) {
            filterList = channelList.stream().filter(channel -> CHANNEL_TYPE_BANK.contains(channel.getType())).collect(toList());
        } else if(payType == 4) {
            filterList = channelList.stream().filter(channel -> CHANNEL_TYPE_BANK_BIND.contains(channel.getType())).collect(toList());
        }else {
            filterList = channelList.stream().filter(channel -> CHANNEL_TYPE_YUNSHANFU.contains(channel.getType())).collect(toList());
        }
        MwPayChannel selectChannel = filterChannel(filterList);
        if(selectChannel != null) {
            return selectChannel;
        }
        //次选同主体不同平台通道
        selectChannel = filterChannel(channelList);
        if(selectChannel != null) {
            if(selectChannel.getType() != payType) {
                return null;
            }
            return selectChannel;
        }
        //如果都无效，换绑定，app端提示合同重签
        return null;
    }

    protected MwPayChannel filterChannel(List<MwPayChannel> channelList) {
        if(CollectionUtils.isEmpty(channelList)) {
            return null;
        }
        if(channelList.size() == 1) {
            return channelList.get(0);
        }
        channelList = channelList.stream().sorted().collect(toList());
        //得到当前比例最高的通道
        MwPayChannel firstChannel = channelList.get(0);
        double scale = firstChannel.getAmount()/firstChannel.getMaxAmount();
        //过滤最高比例小于10%额通道 并按照最大额度排序 取第一个
        MwPayChannel select = channelList.stream().filter(channel -> channel.getAmount()/channel.getMaxAmount() > (scale -10))
                .sorted(Comparator.comparing(MwPayChannel::getMaxAmount)).findFirst().orElse(null);
        return select;
    }

    @Override
    public MwPayChannel decPrice(BigDecimal orderPrice, Long channelId){
        mapper.decPrice(orderPrice, channelId);
        MwPayChannel payChannel = getById(channelId);
        if(payChannel != null) {
            //如果额度小于5% 临时关闭
            if(100 * payChannel.getAmount()/payChannel.getMaxAmount() < 5) {
                payChannel.setStatus(2);
                updateById(payChannel);
            }

        }
        return payChannel;
    }

    @Override
    public MwPayChannel channel(Long uid, Integer payType) {
        MwPayBind payBind = payBindService.findBindChannel(uid, payType);
        //todo 通道选型算法
        //todo 优选剩余额度比例高的，额度差距在10%以内的，优选总额度越高的
        //todo 回调时扣除额度，如果低于一定值，暂停通道

        //todo 通道绑定用户
        //todo 无绑定时，按照上面算法选通道
        //todo 有绑定时，校验通道是否有效，
        //todo      有效 继续支付
        //          无效，优选同主体同平台通道，次选同主体不同平台通道，如果都无效，换绑定，app端提示合同重签

        MwPayChannel payChannel;
        if(payBind == null) {
            //通道选型算法
            //优选剩余额度比例高的，额度差距在10%以内的，优选总额度越高的
            payChannel = randomChannel(payType);

            if(payChannel == null) {
                throw new MshopException("无支付通道可选择");
            }
            //通道绑定用户
            payBindService.bindChannel(uid, payChannel.getId(), payType);
        } else {
            payChannel = getById(payBind.getChannelId());
            //有绑定时，校验通道是否有效，
            if(payChannel.getStatus() != 8) {
                //优选同主体同平台通道，次选同主体不同平台通道，如果都无效，换绑定，app端提示合同重签
                Long companyId = payChannel.getCompanyId();
                payChannel = selectChannel(payType, companyId);
                if(payChannel == null) {
                    //如果都无效，换绑定，app端提示合同重签
                    payChannel = randomChannel(payType);
                    if(payChannel == null) {
                        throw new MshopException("无支付通道可选择");
                    }
                    //更换绑定通道
                    payBindService.rebindChannel(uid, payChannel.getId(), payType);
                }
            }

        }
        return payChannel;
    }

    @Override
    public PayChannelDto channelDto(Long uid, Integer payType){
        MwPayChannel payChannel = channel(uid, payType);
        if(payChannel == null) {
            return null;
        }
        PayChannelDto channelDto = generator.convert(payChannel, PayChannelDto.class);
        channelDto.setCertProfile(RsaUtil.decrypt(new String(payChannel.getCertProfileEnc()), KEY_RSA_PRIVATE));
        return channelDto;
    }

    @Override
    public PayChannelDto getExtractChannel(String extractType){
        LambdaQueryWrapper<MwPayChannel> wrapper = new LambdaQueryWrapper<MwPayChannel>()
                .eq(MwPayChannel::getStatus, 8)
                .eq(MwPayChannel::getExtract, 1)
                .in(MwPayChannel::getName, Arrays.asList(PAY_NAME, ""))
                .last("limit 1");
        if(StringUtils.isNotBlank(extractType)) {
            if("alipay".equals(extractType)) {
                wrapper.eq(MwPayChannel::getChannelKey, ALIPAY.getKey());
            } else if ("weixin".equals(extractType)) {
                wrapper.eq(MwPayChannel::getChannelKey, WECHATPAY.getKey());
            }  else if ("bank".equals(extractType)) {
                wrapper.eq(MwPayChannel::getChannelKey, YSEPAY_BANK_BIND.getKey());
            }
        }
        MwPayChannel channel = this.getOne(wrapper);

        if(channel == null) {
            return null;
        }

        PayChannelDto channelDto = generator.convert(channel, PayChannelDto.class);
        channelDto.setCertProfile(RsaUtil.decrypt(new String(channel.getCertProfileEnc()), KEY_RSA_PRIVATE));
        return channelDto;
    }
    @Override
    public int reset(){
        mapper.resetClosed();
        return mapper.reset();
    }


    @Override
    public PayChannelDto getChannel(Long channelId) {

        MwPayChannel payChannel = getById(channelId);
        if(payChannel == null) {
            throw new MshopException("通道不存在");
        }
        if(payChannel.getCertProfileEnc() ==null) {
            throw new MshopException("证书信息不存在");
        }
        PayChannelDto channelDto = generator.convert(payChannel, PayChannelDto.class);
        channelDto.setCertProfile(RsaUtil.decrypt(new String(payChannel.getCertProfileEnc()), KEY_RSA_PRIVATE));

        return channelDto;
    }

}
