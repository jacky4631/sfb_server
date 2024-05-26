/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageInfo;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.constant.SystemConfigConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.enums.PayTypeEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.event.TemplateBean;
import com.mailvor.event.TemplateEvent;
import com.mailvor.event.TemplateListenEnum;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.activity.domain.MwUserExtract;
import com.mailvor.modules.activity.domain.MwUserExtractConfig;
import com.mailvor.modules.activity.param.UserExtParam;
import com.mailvor.modules.activity.service.MwUserExtractService;
import com.mailvor.modules.activity.service.dto.MwExtractConfigDto;
import com.mailvor.modules.activity.service.dto.MwExtractConfigParam;
import com.mailvor.modules.activity.service.dto.MwUserExtractDto;
import com.mailvor.modules.activity.service.dto.MwUserExtractQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwUserExtractConfigMapper;
import com.mailvor.modules.activity.service.mapper.MwUserExtractMapper;
import com.mailvor.modules.push.service.JPushService;
import com.mailvor.modules.shop.domain.MwSystemConfig;
import com.mailvor.modules.shop.service.MwSystemConfigService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.domain.MwUserBank;
import com.mailvor.modules.user.domain.MwUserUnion;
import com.mailvor.modules.user.service.MwUserBankService;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.modules.user.service.MwUserUnionService;
import com.mailvor.modules.utils.TkUtil;
import com.mailvor.utils.DateUtils;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.RedisUtils;
import com.mailvor.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.mailvor.config.PayConfig.PAY_NAME;
import static com.mailvor.constant.SystemConfigConstants.*;


/**
* @author huangyu
* @date 2020-05-13
*/
@Slf4j
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserExtractServiceImpl extends BaseServiceImpl<MwUserExtractMapper, MwUserExtract> implements MwUserExtractService {

    private final IGenerator generator;
    private final MwUserExtractMapper extractMapper;
    private final MwUserService userService;
    private final MwUserBillService billService;
    private final ApplicationEventPublisher publisher;
    @Resource
    private JPushService jPushService;

    @Resource
    private MwSystemConfigService systemConfigService;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private MwUserExtractConfigMapper extractConfigMapper;

    @Resource
    private MwUserBankService userBankService;

    @Resource
    private MwUserUnionService userUnionService;
    /**
     * 开始提现
     * @param userInfo 用户
     * @param param UserExtParam
     */
    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ, propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
    public void userExtract(MwUser userInfo, UserExtParam param,String ip) {

        double decMoney = Double.valueOf(param.getMoney());
        if(decMoney <= 0) {
            throw new MshopException("提现佣金大于0");
        }
        Double extractMinPrice = getExtractMinPrice();
        if(decMoney < extractMinPrice) {
            throw new MshopException("提现佣金小于最低额度");
        }
        Double extractMaxPrice = getExtractMaxPrice();
        if(decMoney > extractMaxPrice) {
            throw new MshopException("提现佣金大于最高额度");
        }
        Long uid = userInfo.getUid();
        if(StrUtil.isEmpty(userInfo.getRealName())){
            throw new MshopException("姓名为空");
        }
        if(userInfo.getSpreadUid() == 0) {
            throw new MshopException("推广用户不存在");
        }
        MwUser user = userService.getById(uid);
        double extractPrice = user.getNowMoney().doubleValue();

        if(extractPrice <= 0) {
            throw new MshopException("提现佣金不足");
        }
        if(extractPrice < decMoney ) {
            throw new MshopException("提现佣金不足");
        }

        //找到今天的提现订单
        LambdaQueryWrapper<MwUserExtract> wrapperOne = new LambdaQueryWrapper<>();
        wrapperOne
                .ge(MwUserExtract::getCreateTime, DateUtil.beginOfDay(new Date()))
                .eq(MwUserExtract::getUid, uid);
        Long count = extractMapper.selectCount(wrapperOne);
        Integer extractCount = getExtractCount();
        if(count >= extractCount) {
            throw new MshopException("超过每天最大提现次数");
        }

        MwExtractConfigDto obj = systemConfigService.getAppExtractConfig();
        double charge = Double.parseDouble(obj.getCharge());
        double extractScale = NumberUtil.div(NumberUtil.sub(100.0, charge), 100);

        MwUserExtract userExtract = new MwUserExtract();
        userExtract.setUid(userInfo.getUid());
        userExtract.setExtractType(param.getExtractType());
        BigDecimal decMoneyB = new BigDecimal(decMoney);
        userExtract.setExtractPrice(NumberUtil.round(NumberUtil.mul(decMoneyB, extractScale), 2));
        userExtract.setBalance(decMoneyB);
        userExtract.setIp(ip);
        userExtract.setName(PAY_NAME);


        userExtract.setMark(param.getMark());
        userExtract.setRealName(userInfo.getRealName());

        String mark = "";

        if(PayTypeEnum.ALI.getValue().equals(param.getExtractType())){
            if(userInfo.getAliProfile() == null ){
                throw new MshopException("请授权登录支付宝");
            }
            count = extractMapper.selectCount(new LambdaQueryWrapper<MwUserExtract>()
                        .ge(MwUserExtract::getCreateTime, DateUtil.beginOfDay(new Date()))
                        .eq(MwUserExtract::getAlipayCode, userInfo.getAliProfile().getUserId()));
            if(count >= extractCount) {
                throw new MshopException("该支付宝账号超过每天最大提现次数");
            }
            userExtract.setAlipayCode(userInfo.getAliProfile().getUserId());
            mark = "支付宝提现"+decMoney+"元";
        }else if(PayTypeEnum.WEIXIN.getValue().equals(param.getExtractType())){
            MwUserUnion userUnion = userUnionService.getOne(uid);
            if(userUnion == null || StrUtil.isEmpty(userUnion.getOpenId())){
                throw new MshopException("请授权登录微信");
            }
            String openId = userUnion.getWxProfile().getOpenid();
            count = extractMapper.selectCount(new LambdaQueryWrapper<MwUserExtract>()
                    .ge(MwUserExtract::getCreateTime, DateUtil.beginOfDay(new Date()))
                    .eq(MwUserExtract::getWechat, openId));
            if(count >= extractCount) {
                throw new MshopException("该微信号超过每天最大提现次数");
            }
            userExtract.setWechat(openId);
            mark = "微信提现"+decMoney+"元";
        }else if(PayTypeEnum.BANK.getValue().equals(param.getExtractType())){
            MwUserBank userBank = userBankService.getById(param.getBankId());
            if(userBank == null) {
                throw new MshopException("未找到绑定的银行卡");
            }
            //保存默认提现卡
            userBankService.setExtractDefault(uid, userBank.getId());

            userExtract.setBankCode(userBank.getBankNo());
            mark = "银行卡提现"+decMoney+"元";
        }

        //新增提现记录，需要去后台手动审核
        extractMapper.insert(userExtract);
        BigDecimal newMoney = NumberUtil.sub(user.getNowMoney(), decMoney);
        user.setNowMoney(newMoney);
        userService.updateById(user);

        mark += " 扣除余额" + decMoney;

        //插入流水
        billService.expend(userInfo.getUid(),userInfo.getUid(), "提现", BillDetailEnum.CATEGORY_1.getValue(),
                BillDetailEnum.TYPE_4.getValue(),decMoney,newMoney.doubleValue(), mark,
                userExtract.getId().toString(),userExtract.getCreateTime());

        //TODO 管理页通知 管理页uid配置到后台
        try {
            jPushService.push("有用户申请提现啦，请去后台操作", 6L);
        }catch (Exception e) {
            e.printStackTrace();
        }
        //校验用户是否禁止提现
        MwUserExtractConfig extractConfig = extractConfigMapper.selectById(uid);
        if(extractConfig != null && extractConfig.getAutoExtract() == 1) {
            return;
        }

        //自动提现
        String extractAutoStr =  systemConfigService.getData(TkUtil.getMixedPlatformKey(SystemConfigConstants.USER_EXTRACT_AUTO));
        if(StringUtils.isNotBlank(extractAutoStr))  {
            Integer extractAuto = Integer.parseInt(extractAutoStr);
            //设置最大提现金额
            double extractMax = Double.parseDouble(systemConfigService.getData(TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX)));
            //开启自动提现 同时提现金额小于设置的最大金额，自动提现
            if(extractAuto == 1 && decMoney < extractMax) {
                userExtract.setStatus(1);
                doExtract(userExtract);
            }
        }

    }

    public Double getExtractMinPrice() {
        String obj = systemConfigService.getData(TkUtil.getMixedPlatformKey(USER_EXTRACT_MIN_PRICE));
        if("".equals(obj)) {
            return 1d;
        }
        return Double.parseDouble(obj);
    }

    public Double getExtractMaxPrice() {
        String obj = systemConfigService.getData(TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX_PRICE));
        if("".equals(obj)) {
            return 3000D;
        }
        return Double.parseDouble(obj);
    }
    public Integer getExtractCount() {
        String obj = systemConfigService.getData(TkUtil.getMixedPlatformKey(USER_EXTRACT_COUNT));
        if("".equals(obj)) {
            return 1;
        }
        return Integer.parseInt(obj);
    }

    public void setExtractConfig(MwExtractConfigParam param) {
        MwSystemConfig minPriceConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName, TkUtil.getMixedPlatformKey(USER_EXTRACT_MIN_PRICE)));
        if(minPriceConfig == null) {
            minPriceConfig = new MwSystemConfig();
            minPriceConfig.setMenuName(TkUtil.getMixedPlatformKey(USER_EXTRACT_MIN_PRICE));
        }
        minPriceConfig.setValue(param.getMinPrice().toString());
        redisUtils.set(TkUtil.getMixedPlatformKey(USER_EXTRACT_MIN_PRICE), param.getMinPrice().toString());
        MwSystemConfig maxPriceConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX_PRICE) ));
        if(maxPriceConfig == null) {
            maxPriceConfig = new MwSystemConfig();
            maxPriceConfig.setMenuName(TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX_PRICE));
        }
        maxPriceConfig.setValue(param.getMaxPrice().toString());
        redisUtils.set(TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX_PRICE), param.getMaxPrice().toString());

        MwSystemConfig countConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_COUNT)));
        if(countConfig == null) {
            countConfig = new MwSystemConfig();
            countConfig.setMenuName(TkUtil.getMixedPlatformKey(USER_EXTRACT_COUNT));
        }
        countConfig.setValue(param.getCount().toString());
        redisUtils.set(TkUtil.getMixedPlatformKey(USER_EXTRACT_COUNT), param.getCount().toString());

        MwSystemConfig extractAuto = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_AUTO)));
        if(extractAuto == null) {
            extractAuto = new MwSystemConfig();
            extractAuto.setMenuName(TkUtil.getMixedPlatformKey(USER_EXTRACT_AUTO));
        }
        extractAuto.setValue(param.getAuto().toString());
        redisUtils.set(TkUtil.getMixedPlatformKey(USER_EXTRACT_AUTO), param.getAuto().toString());

        MwSystemConfig extractAutoMax = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX)));
        if(extractAutoMax == null) {
            extractAutoMax = new MwSystemConfig();
            extractAutoMax.setMenuName(TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX));
        }
        extractAutoMax.setValue(param.getAutoMax().toString());
        redisUtils.set(TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX), param.getAutoMax().toString());

        MwSystemConfig appExtractConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(EXTRACT_CONFIG)));
        if(appExtractConfig == null) {
            appExtractConfig = new MwSystemConfig();
            appExtractConfig.setMenuName(TkUtil.getMixedPlatformKey(EXTRACT_CONFIG));
        }
        MwExtractConfigDto appConfig = generator.convert(param, MwExtractConfigDto.class);
        appConfig.setExtractMin(param.getMinPrice().toString());
        appConfig.setExtractMax(param.getMaxPrice().toString());
        appExtractConfig.setValue(JSON.toJSONString(appConfig));
        redisUtils.set(TkUtil.getMixedPlatformKey(EXTRACT_CONFIG), appConfig);

        //保存
        List<MwSystemConfig> systemConfigs = Arrays.asList(minPriceConfig, maxPriceConfig,
                countConfig, extractAuto, extractAutoMax, appExtractConfig);
        systemConfigService.saveOrUpdateBatch(systemConfigs);

    }

    public MwExtractConfigParam getExtractConfig() {
        MwExtractConfigParam param = new MwExtractConfigParam();
        MwSystemConfig minPriceConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName, TkUtil.getMixedPlatformKey(USER_EXTRACT_MIN_PRICE)));
        if(minPriceConfig != null) {
            param.setMinPrice(Integer.parseInt(minPriceConfig.getValue()));
        }
        MwSystemConfig maxPriceConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX_PRICE) ));
        if(maxPriceConfig != null) {
            param.setMaxPrice(Integer.parseInt(maxPriceConfig.getValue()));
        }

        MwSystemConfig countConfig = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_COUNT)));
        if(countConfig != null) {
            param.setCount(Integer.parseInt(countConfig.getValue()));
        }

        MwSystemConfig extractAuto = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_AUTO)));
        if(extractAuto != null) {
            param.setAuto(Integer.parseInt(extractAuto.getValue()));
        }

        MwSystemConfig extractAutoMax = systemConfigService.getOne(new LambdaQueryWrapper<MwSystemConfig>()
                .eq(MwSystemConfig::getMenuName,TkUtil.getMixedPlatformKey(USER_EXTRACT_MAX)));
        if(extractAutoMax != null) {
            param.setAutoMax(Integer.parseInt(extractAutoMax.getValue()));
        }

        MwExtractConfigDto appConfig = systemConfigService.getAppExtractConfig();

        param.setAlipay(Integer.parseInt(appConfig.getAlipay()));
        param.setBank(Integer.parseInt(appConfig.getBank()));
        param.setCharge(Integer.parseInt(appConfig.getCharge()));
        param.setWeixin(Integer.parseInt(appConfig.getWeixin()));
        param.setExtractFeeDesc(appConfig.getExtractFeeDesc());
        param.setExtractIntervalDesc(appConfig.getExtractIntervalDesc());
        param.setExtractMinDesc(appConfig.getExtractMinDesc());

        return param;
    }
    /**
     * 累计提现金额
     * @param uid uid
     * @return double
     */
    @Override
    public double extractSum(Long uid) {
        return extractMapper.sumPrice(uid);
    }


    //==============================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwUserExtractQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserExtract> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", generator.convert(page.getList(), MwUserExtractDto.class));
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwUserExtract> queryAll(MwUserExtractQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwUserExtract.class, criteria));
    }


    @Override
    public void download(List<MwUserExtractDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwUserExtractDto mwUserExtract : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" uid",  mwUserExtract.getUid());
            map.put("名称", mwUserExtract.getRealName());
            map.put("bank = 银行卡 alipay = 支付宝wx=微信", mwUserExtract.getExtractType());
            map.put("银行卡", mwUserExtract.getBankCode());
            map.put("开户地址", mwUserExtract.getBankAddress());
            map.put("支付宝账号", mwUserExtract.getAlipayCode());
            map.put("提现金额", mwUserExtract.getExtractPrice());
            map.put(" mark",  mwUserExtract.getMark());
            map.put(" balance",  mwUserExtract.getBalance());
            map.put("无效原因", mwUserExtract.getFailMsg());
            map.put(" failTime",  mwUserExtract.getFailTime());
            map.put("-1 未通过 0 审核中 1 已提现", mwUserExtract.getStatus());
            map.put("微信号", mwUserExtract.getWechat());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 操作提现
     * @param resources MwUserExtract
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doExtract(MwUserExtract resources){
//        if(ShopCommonEnum.EXTRACT_1.getValue().equals(resources.getStatus())
//                && DateUtils.betweenHour(0, 8)) {
//            throw new BadRequestException("不在提现时间段");
//        }

        if(resources.getStatus() == null){
            throw new BadRequestException("请选择审核状态");
        }

        if(ShopCommonEnum.EXTRACT_0.getValue().equals(resources.getStatus())){
            throw new BadRequestException("请选择审核状态");
        }
        MwUserExtract userExtract = this.getById(resources.getId());
        if(userExtract == null) {
            throw new BadRequestException("提现请求不存在！");
        }
        if(!ShopCommonEnum.EXTRACT_0.getValue().equals(userExtract.getStatus())){
            throw new BadRequestException("该申请已经处理过啦！");
        }
        if(ShopCommonEnum.EXTRACT_MINUS_1.getValue().equals(resources.getStatus())){
            if(StrUtil.isEmpty(resources.getFailMsg())){
                throw new BadRequestException("请填写失败原因");
            }
            //防止无限添加佣金
            if (ObjectUtil.isNull(userExtract.getFailTime())) {
                String mark = "提现失败,退回佣金"+userExtract.getExtractPrice()+"元";
                MwUser mwUser = userService.getById(userExtract.getUid());
//
//                double balance = NumberUtil.add(mwUser.getBrokeragePrice(),resources.getExtractPrice()).doubleValue();
//                //插入流水
//                billService.income(resources.getUid(),"提现失败", BillDetailEnum.CATEGORY_1.getValue(),
//                        BillDetailEnum.TYPE_4.getValue(),resources.getExtractPrice().doubleValue(),balance,
//                        mark,resources.getId().toString());
//
//                //todo 取消设置订单为已提现
//                //返回提现金额
//                userService.incBrokeragePrice(resources.getExtractPrice(),resources.getUid());

                resources.setFailTime(new Date());
            }

        }else{
            //模板消息支付成功发布事件
            TemplateBean templateBean = TemplateBean.builder()
                    .extractId( userExtract.getId())
                    .templateType(TemplateListenEnum.TYPE_8.getValue())
                    .build();
            //{@link TemplateListener}
            publisher.publishEvent(new TemplateEvent(this,templateBean));
        }
        userExtract.setStatus(resources.getStatus());
        userExtract.setFailTime(resources.getFailTime());
        this.saveOrUpdate(userExtract);
        log.info("1.用户提现申请完成:{}",JSON.toJSONString(userExtract));
    }

    @Async
    @Override
    public void doExtracts(List<MwUserExtract> resources){
        for(MwUserExtract userExtract : resources) {
            try {
                doExtract(userExtract);
                Thread.sleep(1000);
            }catch (Exception e) {
                log.error("用户{}提现失败:{}", JSON.toJSONString(userExtract), e.getMessage());
            }

        }
    }

    public Double totalExtractPrice(Integer type){
        switch (type){
            case 1:
                return extractMapper.sumExtractPrice(getWrapper(DateUtils.getToday()));
            case 2:
                return extractMapper.sumExtractPrice(getWrapperPro(DateUtils.getProDay(), DateUtils.getToday()));
            case 3:
                return extractMapper.sumExtractPrice(getWrapper(DateUtils.getMonth()));
            case 4:
                return extractMapper.sumExtractPrice(getWrapperPro(DateUtils.getProMonth(), DateUtils.getMonth()));
            case 5:
                return extractMapper.sumExtractPrice(getWrapper(DateUtils.get30Day()));
            case 6:
                return extractMapper.sumExtractPrice(getWrapper(DateUtils.get7Day()));
        }
        return 0.0;
    }


    private LambdaQueryWrapper<MwUserExtract> getWrapper(DateTime start) {
        return getWrapperPro(start, null);
    }

    private LambdaQueryWrapper<MwUserExtract> getWrapperPro(Date start, Date end) {
        LambdaQueryWrapper<MwUserExtract> wrapperTwo = new LambdaQueryWrapper<>();

        if(start != null) {
            wrapperTwo.ge(MwUserExtract::getCreateTime, start);
        }
        if(end != null) {
            wrapperTwo.lt(MwUserExtract::getCreateTime, end);
        }
        wrapperTwo.eq(MwUserExtract::getStatus, 1);
        return wrapperTwo;
    }

    @Override
    public void updateUnpaidAliCode(Long uid, String userId) {
        extractMapper.updateUnpaidAliCode(uid, userId);
    }

}
