/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.activity.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.mailvor.api.MshopException;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.constant.ShopConstants;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.CouponEnum;
import com.mailvor.enums.CouponGetEnum;
import com.mailvor.modules.activity.domain.MwStoreCoupon;
import com.mailvor.modules.activity.domain.MwStoreCouponUser;
import com.mailvor.modules.activity.service.MwStoreCouponService;
import com.mailvor.modules.activity.service.MwStoreCouponUserService;
import com.mailvor.modules.activity.service.dto.MwStoreCouponUserDto;
import com.mailvor.modules.activity.service.dto.MwStoreCouponUserQueryCriteria;
import com.mailvor.modules.activity.service.mapper.MwStoreCouponUserMapper;
import com.mailvor.modules.activity.vo.StoreCouponUserVo;
import com.mailvor.modules.activity.vo.MwStoreCouponUserQueryVo;
import com.mailvor.modules.cart.service.MwStoreCartService;
import com.mailvor.modules.cart.vo.MwStoreCartQueryVo;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.MwUserService;
import com.mailvor.utils.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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
import java.util.*;
import java.util.stream.Collectors;


/**
* @author huangyu
* @date 2020-05-13
*/
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreCouponUserServiceImpl extends BaseServiceImpl<MwStoreCouponUserMapper, MwStoreCouponUser> implements MwStoreCouponUserService {

    @Autowired
    private IGenerator generator;


    @Autowired
    private MwStoreCouponUserMapper mwStoreCouponUserMapper;

    @Autowired
    private MwUserService userService;
    @Autowired
    private MwStoreCouponService storeCouponService;
    @Autowired
    private MwStoreCartService mwStoreCartService;

    /**
     * 获取当前用户优惠券数量
     *
     * @param uid uid
     * @return int
     */
    @Override
    public Long getUserValidCouponCount(Long uid) {
        this.checkInvalidCoupon();
        return this.lambdaQuery()
                .eq(MwStoreCouponUser::getStatus, CouponEnum.STATUS_0.getValue())
                .eq(MwStoreCouponUser::getUid,uid)
                .count();
    }

    /**
     * 获取满足条件的可用优惠券
     * @param cartIds 购物车ids
     * @return list
     */
    @Override
    public List<StoreCouponUserVo> beUsableCouponList(Long uid,String cartIds) {

        Map<String, Object> cartGroup = mwStoreCartService.getUserProductCartList(uid,
                cartIds, ShopConstants.MSHOP_ONE_NUM);

        List<MwStoreCartQueryVo> cartInfo = (List<MwStoreCartQueryVo>)cartGroup.get("valid");

        BigDecimal sumPrice = BigDecimal.ZERO;
        for (MwStoreCartQueryVo storeCart : cartInfo) {
            sumPrice = NumberUtil.add(sumPrice,NumberUtil.mul(storeCart.getCartNum(),storeCart.getTruePrice()));
        }

        List<String> productIds = cartInfo.stream()
                .map(MwStoreCartQueryVo::getProductId)
                .map(Object::toString)
                .collect(Collectors.toList());


        return this.getUsableCouponList(uid, sumPrice.doubleValue(), productIds);
    }

    /**
     * 获取下单时候满足的优惠券
     * @param uid uid
     * @param price 总价格
     * @param productIds list
     * @return list
     */
    @Override
    public List<StoreCouponUserVo> getUsableCouponList(Long uid, double price, List<String> productIds) {
        Date now = new Date();
        List<StoreCouponUserVo> storeCouponUsers = mwStoreCouponUserMapper.selectCouponList(now, price, uid);
        return storeCouponUsers.stream()
                .filter(coupon ->
                        CouponEnum.TYPE_2.getValue().equals(coupon.getType()) ||
                                CouponEnum.TYPE_0.getValue().equals(coupon.getType())
                                || (CouponEnum.TYPE_1.getValue().equals(coupon.getType())
                                && isSame(Arrays.asList(coupon.getProductId().split(",")),productIds)))
                .collect(Collectors.toList());

    }




    /**
     * 获取用户优惠券
     * @param id 优惠券id
     * @param uid 用户id
     * @return MwStoreCouponUser
     */
    @Override
    public MwStoreCouponUser getCoupon(Integer id, Long uid) {
        return this.lambdaQuery()
                .eq(MwStoreCouponUser::getIsFail, CouponEnum.FALI_0.getValue())
                .eq(MwStoreCouponUser::getStatus,CouponEnum.STATUS_0.getValue())
                .eq(MwStoreCouponUser::getUid,uid)
                .eq(MwStoreCouponUser::getId,id)
                .one();
    }

    @Override
    public void useCoupon(int id) {
        MwStoreCouponUser couponUser = new MwStoreCouponUser();
        couponUser.setId((long)id);
        couponUser.setStatus(1);
        couponUser.setUseTime(new Date());
        mwStoreCouponUserMapper.updateById(couponUser);
    }



    /**
     * 获取用户优惠券
     * @param uid uid
     * @return list
     */
    @Override
    public List<MwStoreCouponUserQueryVo> getUserCoupon(Long uid) {
        //this.checkInvalidCoupon();
        List<MwStoreCouponUser> storeCouponUsers = mwStoreCouponUserMapper
                .selectList(Wrappers.<MwStoreCouponUser>lambdaQuery()
                        .eq(MwStoreCouponUser::getUid,uid));
        List<MwStoreCouponUserQueryVo> storeCouponUserQueryVoList = new ArrayList<>();
        long nowTime = System.currentTimeMillis();
        for (MwStoreCouponUser couponUser : storeCouponUsers) {
            MwStoreCouponUserQueryVo queryVo = generator.convert(couponUser, MwStoreCouponUserQueryVo.class);
            if(couponUser.getIsFail() == 1){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已失效");
            }else if (couponUser.getStatus() == 1){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已使用");
            }else if (couponUser.getStatus() == 2){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已过期");
            }else if(couponUser.getCreateTime().getTime() > nowTime || couponUser.getEndTime().getTime() < nowTime){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已过期");
            }else{
                queryVo.set_type(CouponEnum.USE_1.getValue());
                queryVo.set_msg("可使用");
            }

            storeCouponUserQueryVoList.add(queryVo);
        }
        return storeCouponUserQueryVoList;
    }

    /**
     * 添加优惠券记录
     * @param uid 用户id
     * @param cid 优惠券id
     */
    @Override
    public void addUserCoupon(Long uid, Integer cid) {
        MwStoreCoupon storeCoupon = storeCouponService.getById(cid);
        if(storeCoupon == null) {
            throw new MshopException("优惠劵不存在");
        }

        Date now = new Date();

        Date endTime = DateUtil.offsetDay(now,storeCoupon.getCouponTime());

        MwStoreCouponUser storeCouponUser = MwStoreCouponUser.builder()
                .cid(storeCoupon.getId())
                .uid(uid)
                .couponPrice(storeCoupon.getCouponPrice())
                .couponTitle(storeCoupon.getTitle())
                .useMinPrice(storeCoupon.getUseMinPrice())
                .endTime(endTime)
                .type(CouponGetEnum.GET.getValue())
                .build();

        this.save(storeCouponUser);

    }


    /**
     * 判断两个list是否有相同值
     * @param list1 list
     * @param list2 list
     * @return boolean
     */
    private boolean isSame(List<String> list1,List<String> list2){
        if(list2.isEmpty()) {
            return true;
        }
        list1 = new ArrayList<>(list1);
        list2 = new ArrayList<>(list2);
        list1.addAll(list2);
        int total = list1.size();

        List<String> newList = new ArrayList<>(new HashSet<>(list1));

        int newTotal = newList.size();


        return total > newTotal;
    }


    /**
     * 检查优惠券状态
     */
    private void checkInvalidCoupon() {
        Date nowTime = new Date();
       LambdaQueryWrapper<MwStoreCouponUser> wrapper= new LambdaQueryWrapper<>();
        wrapper.lt(MwStoreCouponUser::getEndTime,nowTime)
                .eq(MwStoreCouponUser::getStatus,CouponEnum.STATUS_0.getValue());
        MwStoreCouponUser couponUser = new MwStoreCouponUser();
        couponUser.setStatus(CouponEnum.STATUS_2.getValue());
        mwStoreCouponUserMapper.update(couponUser,wrapper);

    }



    //=========================================================================================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwStoreCouponUserQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwStoreCouponUser> page = new PageInfo<>(queryAll(criteria));
        List<MwStoreCouponUserDto> storeOrderDTOS = generator.convert(page.getList(), MwStoreCouponUserDto.class);
        for (MwStoreCouponUserDto couponUserDTO : storeOrderDTOS) {
            couponUserDTO.setNickname(userService.getOne(new LambdaQueryWrapper<MwUser>()
                    .eq(MwUser::getUid,couponUserDTO.getUid())).getNickname());
        }
        Map<String,Object> map = new LinkedHashMap<>(2);
        map.put("content",storeOrderDTOS);
        map.put("totalElements", page.getTotal());
        return map;
    }


    @Override
    //@Cacheable
    public List<MwStoreCouponUser> queryAll(MwStoreCouponUserQueryCriteria criteria){
        return baseMapper.selectList(QueryHelpPlus.getPredicate(MwStoreCouponUser.class, criteria));
    }
    @Override
    public void download(List<MwStoreCouponUserDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwStoreCouponUserDto mwStoreCouponUser : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("兑换的项目id", mwStoreCouponUser.getCid());
            map.put("优惠券所属用户", mwStoreCouponUser.getUid());
            map.put("优惠券名称", mwStoreCouponUser.getCouponTitle());
            map.put("优惠券的面值", mwStoreCouponUser.getCouponPrice());
            map.put("最低消费多少金额可用优惠券", mwStoreCouponUser.getUseMinPrice());
            map.put("优惠券创建时间", mwStoreCouponUser.getAddTime());
            map.put("优惠券结束时间", mwStoreCouponUser.getEndTime());
            map.put("使用时间", mwStoreCouponUser.getUseTime());
            map.put("获取方式", mwStoreCouponUser.getType());
            map.put("状态（0：未使用，1：已使用, 2:已过期）", mwStoreCouponUser.getStatus());
            map.put("是否有效", mwStoreCouponUser.getIsFail());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public Map<String, Object> getUserPCCoupon(Long uid, int page, int limit, Integer type) {
        Page<MwStoreCouponUser> mwStoreCouponUserPage = new Page<>(page, limit);
        mwStoreCouponUserMapper.selectPage(mwStoreCouponUserPage,Wrappers.<MwStoreCouponUser>lambdaQuery()
                        .eq(MwStoreCouponUser::getUid,uid).eq(MwStoreCouponUser::getStatus,type));

        List<MwStoreCouponUserQueryVo> storeCouponUserQueryVoList = new ArrayList<>();
        long nowTime = System.currentTimeMillis();
        for (MwStoreCouponUser couponUser : mwStoreCouponUserPage.getRecords()) {
            MwStoreCouponUserQueryVo queryVo = generator.convert(couponUser, MwStoreCouponUserQueryVo.class);
            if(couponUser.getIsFail() == 1){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已失效");
            }else if (couponUser.getStatus() == 1){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已使用");
            }else if (couponUser.getStatus() == 2){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已过期");
            }else if(couponUser.getCreateTime().getTime() > nowTime || couponUser.getEndTime().getTime() < nowTime){
                queryVo.set_type(CouponEnum.USE_0.getValue());
                queryVo.set_msg("已过期");
            }else{
                queryVo.set_type(CouponEnum.USE_1.getValue());
                queryVo.set_msg("可使用");
            }

            storeCouponUserQueryVoList.add(queryVo);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("list",storeCouponUserQueryVoList);
        map.put("total",mwStoreCouponUserPage.getTotal());
        map.put("totalPage",mwStoreCouponUserPage.getPages());
        return map;
    }
}
