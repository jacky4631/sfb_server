/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.util.NumberUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.OrderInfoEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.activity.domain.MwStoreBargain;
import com.mailvor.modules.activity.domain.MwStoreBargainUser;
import com.mailvor.modules.activity.domain.MwStoreBargainUserHelp;
import com.mailvor.modules.activity.service.MwStoreBargainService;
import com.mailvor.modules.activity.service.MwStoreBargainUserHelpService;
import com.mailvor.modules.activity.service.MwStoreBargainUserService;
import com.mailvor.modules.activity.service.dto.MwStoreBargainDto;
import com.mailvor.modules.activity.service.dto.MwStoreBargainQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreBargainMapper;
import com.mailvor.modules.activity.vo.BargainCountVo;
import com.mailvor.modules.activity.vo.BargainVo;
import com.mailvor.modules.activity.vo.TopCountVo;
import com.mailvor.modules.activity.vo.MwStoreBargainQueryVo;
import com.mailvor.modules.order.service.MwStoreOrderService;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.OrderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;



/**
 * @author huangyu
 * @date 2020-05-13
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreBargainServiceImpl extends BaseServiceImpl<MwStoreBargainMapper, MwStoreBargain> implements MwStoreBargainService {

    @Autowired
    private IGenerator generator;

    @Autowired
    private MwStoreBargainMapper mwStoreBargainMapper;


    @Autowired
    private MwStoreBargainUserService storeBargainUserService;
    @Autowired
    private MwStoreOrderService storeOrderService;
    @Autowired
    private MwStoreBargainUserHelpService storeBargainUserHelpService;



    /**
     * 退回库存销量
     * @param num 数量
     * @param bargainId 砍价产品id
     */
    @Override
    public void incStockDecSales(int num, Long bargainId) {
        mwStoreBargainMapper.incStockDecSales(num,bargainId);
    }

    /**
     * 增加销量 减少库存
     * @param num 数量
     * @param bargainId 砍价id
     */
    @Override
    public void decStockIncSales(int num, Long bargainId) {
        int res = mwStoreBargainMapper.decStockIncSales(num,bargainId);
        if(res == 0) {
            throw new MshopException("砍价产品库存不足");
        }
    }

//    @Override
//    public MwStoreBargain getBargain(int bargainId) {
//        QueryWrapper<MwStoreBargain> wrapper = new QueryWrapper<>();
//        int nowTime = OrderUtil.getSecondTimestampTwo();
//        wrapper.eq("id",bargainId).eq("is_del",0).eq("status",1)
//                .le("start_time",nowTime).ge("stop_time",nowTime);
//        return mwStoreBargainMapper.selectOne(wrapper);
//    }



    /**
     * 开始帮助好友砍价
     * @param bargainId 砍价产品id
     * @param bargainUserUid 开启砍价用户id
     * @param uid 当前用户id
     */
    @Override
    public void doHelp(Long bargainId, Long bargainUserUid, Long uid) {
        //开始真正的砍价
        MwStoreBargainUser storeBargainUser = storeBargainUserService
                .getBargainUserInfo(bargainId,bargainUserUid);


        MwStoreBargain storeBargain = this.getById(bargainId);
        //用户可以砍掉的金额 好友砍价之前获取可以砍价金额
        double coverPrice = NumberUtil.sub(storeBargainUser.getBargainPrice()
                ,storeBargainUser.getBargainPriceMin()).doubleValue();

        double random = 0d;
        if(coverPrice > 0 ){
            //用户剩余要砍掉的价格
            double surplusPrice = NumberUtil.sub(coverPrice,
                    storeBargainUser.getPrice()).doubleValue();
            if(surplusPrice == 0) {
                return;
            }


            //生成一个区间随机数
            random = OrderUtil.randomNumber(
                    storeBargain.getBargainMinPrice().doubleValue(),
                    storeBargain.getBargainMaxPrice().doubleValue());
            if(random > surplusPrice) {
                random = surplusPrice;
            }
        }


        //添加砍价帮助表
        MwStoreBargainUserHelp storeBargainUserHelp = MwStoreBargainUserHelp
                .builder()
                .uid(uid)
                .bargainId(bargainId)
                .bargainUserId(storeBargainUser.getId())
                .price(BigDecimal.valueOf(random))
                .build();
        storeBargainUserHelpService.save(storeBargainUserHelp);

        //累计砍掉的金额
        double totalPrice = NumberUtil.add(storeBargainUser.getPrice().doubleValue(),random);

        //更新砍价参与表
        MwStoreBargainUser bargainUser = MwStoreBargainUser
                .builder()
                .id(storeBargainUser.getId())
                .price(BigDecimal.valueOf(totalPrice))
                .build();

        storeBargainUserService.updateById(bargainUser);
    }

    /**
     * 顶部统计
     * @param bargainId 砍价商品id
     * @return TopCountVo
     */
    @Override
    public TopCountVo topCount(Long bargainId) {
        if(bargainId != null) {
            this.addBargainShare(bargainId);
        }
        return TopCountVo.builder()
                .lookCount(mwStoreBargainMapper.lookCount())
                .shareCount(mwStoreBargainMapper.shareCount())
                .userCount(storeBargainUserService.count())
                .build();
    }

    /**
     * 砍价 砍价帮总人数、剩余金额、进度条、已经砍掉的价格
     * @param bargainId 砍价商品id
     * @param uid 砍价用户id
     * @param myUid 当前用户id
     * @return BargainCountVo
     */
    @Override
    public BargainCountVo helpCount(Long bargainId, Long uid, Long myUid) {
        MwStoreBargainUser storeBargainUser = storeBargainUserService
                .getBargainUserInfo(bargainId,uid);
        // 是否帮别人砍,没砍是true，砍了false
        boolean userBargainStatus = true;
        if(storeBargainUser == null) {
            return BargainCountVo
                    .builder()
                    .count(0L)
                    .alreadyPrice(0d)
                    .status(0)
                    .pricePercent(0)
                    .price(0d)
                    .userBargainStatus(userBargainStatus)
                    .build();
        }


        Long helpCount = storeBargainUserHelpService.lambdaQuery()
                .eq(MwStoreBargainUserHelp::getBargainUserId,storeBargainUser.getId())
                .eq(MwStoreBargainUserHelp::getBargainId,bargainId)
                .eq(MwStoreBargainUserHelp::getUid,myUid)
                .count();

        if(helpCount > 0) {
            userBargainStatus = false;
        }


        Long count = storeBargainUserHelpService
                .getBargainUserHelpPeopleCount(bargainId,storeBargainUser.getId());
        //用户可以砍掉的价格
        double diffPrice = NumberUtil.sub(storeBargainUser.getBargainPrice()
                ,storeBargainUser.getBargainPriceMin()).doubleValue();
        //砍价进度条百分比
        int pricePercent = 0;
        if(diffPrice <= 0) {
            pricePercent = 100;
        }else{
            pricePercent = NumberUtil.round(NumberUtil.mul(NumberUtil.div(
                    storeBargainUser.getPrice(),diffPrice),100)
                    ,0).intValue();
        }



        //剩余的砍价金额
        double surplusPrice = NumberUtil.sub(diffPrice,storeBargainUser.getPrice()).doubleValue();

        return BargainCountVo
                .builder()
                .count(count)
                .alreadyPrice(storeBargainUser.getPrice().doubleValue())
                .status(storeBargainUser.getStatus())
                .pricePercent(pricePercent)
                .price(surplusPrice)
                .userBargainStatus(userBargainStatus)
                .build();
    }





    /**
     * 砍价详情
     * @param id 砍价id
     * @param mwUser 用户
     * @return BargainVo
     */
    @Override
    public BargainVo getDetail(Long id, MwUser mwUser) {

        Date now = new Date();
        MwStoreBargain storeBargain = this.lambdaQuery().eq(MwStoreBargain::getId,id)
                .eq(MwStoreBargain::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .le(MwStoreBargain::getStartTime,now)
                .ge(MwStoreBargain::getStopTime,now)
                .one();

        if(storeBargain == null) {
            throw new MshopException("砍价已结束");
        }

        this.addBargainLook(id);

        MwStoreBargainQueryVo storeBargainQueryVo = generator.convert(storeBargain,
                MwStoreBargainQueryVo.class);

        return  BargainVo
                .builder()
                .bargain(storeBargainQueryVo)
                .userInfo(generator.convert(mwUser, MwUserQueryVo.class))
                .bargainSumCount(this.getBargainPayCount(id))
                .build();
    }

    /**
     * 获取砍价商品列表
     * @param page page
     * @param limit limit
     * @return List
     */
    @Override
    public List<MwStoreBargainQueryVo> getList(int page, int limit) {
        Page<MwStoreBargain> pageModel = new Page<>(page, limit);
        LambdaQueryWrapper<MwStoreBargain> wrapper = new LambdaQueryWrapper<>();
        Date nowTime = new Date();
        wrapper.eq(MwStoreBargain::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .lt(MwStoreBargain::getStartTime,nowTime)
                .gt(MwStoreBargain::getStopTime,nowTime);

        List<MwStoreBargainQueryVo> mwStoreBargainQueryVos = generator.convert(
                mwStoreBargainMapper.selectPage(pageModel,wrapper).getRecords(),
                MwStoreBargainQueryVo.class);

        mwStoreBargainQueryVos.forEach(item->{
            item.setPeople(storeBargainUserService.getBargainUserCount(item.getId(),
                    OrderInfoEnum.BARGAIN_STATUS_1.getValue()));
        });

        return mwStoreBargainQueryVos;
    }


    /**
     * 增加分享次数
     * @param id 砍价商品id
     */
    private void addBargainShare(Long id) {
        mwStoreBargainMapper.addBargainShare(id);
    }

    /**
     * 增加浏览次数
     * @param id 砍价商品id
     */
    private void addBargainLook(Long id) {
        mwStoreBargainMapper.addBargainLook(id);
    }


    /**
     * 砍价支付成功订单数量
     * @param bargainId 砍价id
     * @return int
     */
    private Long getBargainPayCount(Long bargainId) {
        return storeOrderService.lambdaQuery()
                .count();
    }


    //===================================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreBargainQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreBargain> page = new PageInfo<>(queryAll(criteria));
        Map<String, Object> map = new LinkedHashMap<>(2);
        List<MwStoreBargainDto> storeBargainDtoList = generator.convert(page.getList(), MwStoreBargainDto.class);
        for (MwStoreBargainDto storeBargainDto : storeBargainDtoList) {

            String statusStr = OrderUtil.checkActivityStatus(storeBargainDto.getStartTime(),
                    storeBargainDto.getStopTime(), storeBargainDto.getStatus());
            storeBargainDto.setStatusStr(statusStr);
        }
        map.put("content", storeBargainDtoList);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreBargain> queryAll(MwStoreBargainQueryCriteria criteria) {
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreBargain.class, criteria));
    }


    @Override
    public void download(List<MwStoreBargainDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreBargainDto mwStoreBargain : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("关联产品ID", mwStoreBargain.getProductId());
            map.put("砍价活动名称", mwStoreBargain.getTitle());
            map.put("砍价活动图片", mwStoreBargain.getImage());
            map.put("单位名称", mwStoreBargain.getUnitName());
            map.put("库存", mwStoreBargain.getStock());
            map.put("销量", mwStoreBargain.getSales());
            map.put("砍价产品轮播图", mwStoreBargain.getImages());
            map.put("砍价开启时间", mwStoreBargain.getStartTime());
            map.put("砍价结束时间", mwStoreBargain.getStopTime());
            map.put("砍价产品名称", mwStoreBargain.getStoreName());
            map.put("砍价金额", mwStoreBargain.getPrice());
            map.put("砍价商品最低价", mwStoreBargain.getMinPrice());
            map.put("每次购买的砍价产品数量", mwStoreBargain.getNum());
            map.put("用户每次砍价的最大金额", mwStoreBargain.getBargainMaxPrice());
            map.put("用户每次砍价的最小金额", mwStoreBargain.getBargainMinPrice());
            map.put("用户每次砍价的次数", mwStoreBargain.getBargainNum());
            map.put("砍价状态 0(到砍价时间不自动开启)  1(到砍价时间自动开启时间)", mwStoreBargain.getStatus());
            map.put("砍价详情", mwStoreBargain.getDescription());
            map.put("反多少积分", mwStoreBargain.getGiveIntegral());
            map.put("砍价活动简介", mwStoreBargain.getInfo());
            map.put("成本价", mwStoreBargain.getCost());
            map.put("排序", mwStoreBargain.getSort());
            map.put("是否推荐0不推荐1推荐", mwStoreBargain.getIsHot());
            map.put("是否包邮 0不包邮 1包邮", mwStoreBargain.getIsPostage());
            map.put("邮费", mwStoreBargain.getPostage());
            map.put("砍价规则", mwStoreBargain.getRule());
            map.put("砍价产品浏览量", mwStoreBargain.getLook());
            map.put("砍价产品分享量", mwStoreBargain.getShare());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 删除砍价海报
     *
     * @param name
     */
    @Override
    public void deleteBargainImg(String name) {
        baseMapper.deleteBargainImg(name);
    }
}
