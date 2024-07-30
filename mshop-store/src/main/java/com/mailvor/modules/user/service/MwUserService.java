/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.mailvor.common.service.BaseService;
import com.mailvor.domain.PageResult;
import com.mailvor.modules.order.service.dto.UserRefundDto;
import com.mailvor.modules.order.vo.MwStoreOrderQueryVo;
import com.mailvor.modules.user.domain.MwUser;
import com.mailvor.modules.user.service.dto.*;
import com.mailvor.modules.user.vo.MwCommissionInfoQueryVo;
import com.mailvor.modules.user.vo.MwUserQueryVo;
import com.mailvor.modules.user.vo.MwUserVipQueryVo;
import com.mailvor.modules.user.vo.UserFeeQueryVo;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserService extends BaseService<MwUser>{

    /**
     * 返回用户累计充值金额与消费金额
     * @param uid uid
     * @return Double[]
     */
    Double[] getUserMoney(Long uid);

    /**
     * 一级返佣
     * @param order 订单
     */
    void backOrderBrokerage(MwStoreOrderQueryVo order);


    void remove(Long uid);
    /**
     * 统计分销人员
     *
     * @param uid uid
     * @return map
     */
    Map<String,Long> getSpreadCount(Long uid);
    /**
     * 统计分销人员
     *
     * @param uid uid
     * @return map
     */
    Long getSpreadCount(Long uid, Integer grade);

    /**
     * 获取我的分销下人员列表
     * @param uid uid
     * @param page page
     * @param limit limit
     * @param grade ShopCommonEnum.GRADE_0
     * @param keyword 关键字搜索
     * @param sort 排序
     * @return list
     */
    List<PromUserDto> getUserSpreadGrade(Long uid, int page, int limit, Integer grade, String keyword, String sort);

    /**
     * 减去用户积分
     * @param uid 用户id
     * @param integral 积分
     */
    void decIntegral(Long uid,double integral);

    /**
     * 增加购买次数
     * @param uid uid
     */
    void incPayCount(Long uid);

    /**
     * 减去用户余额
     * @param uid uid
     * @param payPrice 金额
     */
    void decPrice(Long uid, BigDecimal payPrice);

    /**
     * 更新用户余额
     * @param uid y用户id
     * @param price 金额
     */
    void incMoney(Long uid,BigDecimal price);

    /**
     * 获取用户信息
     * @param uid uid
     * @return MwUserQueryVo
     */
    MwUserQueryVo getMwUserById(Long uid);
    MwCommissionInfoQueryVo getCommissionInfo(MwUser mwUser);
    /**
     * 获取用户个人详细信息
     * @param mwUser mwUser
     * @return MwUserQueryVo
     */
    MwUserQueryVo getNewMwUserById(MwUser mwUser);
    MwUserQueryVo getNewMwUserById(MwUser mwUser, Boolean baseInfo);

    MwUserVipQueryVo getUserVipInfo(MwUser mwUser);

    /**
     * 返回会员价
     * @param price 原价
     * @param uid 用户id
     * @return vip 价格
     */
    double setLevelPrice(double price, long uid);

    /**
     * 设置推广关系
     * @param spread 上级人
     * @param uid 本人
     */
    boolean setSpread(String spread, long uid);

    Boolean hasUnlockOrder(MwUser mwUser);
    /**
     * 查看下级
     * @param uid uid
     * @param grade 等级
     * @return list
     */
    List<PromUserDto> querySpread(Long uid, Integer grade);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwUserQueryCriteria criteria, Pageable pageable);

    PageResult<ShopDetailDto> queryAll(Wrapper<MwUser> queryWrapper, Pageable pageable);
    /**
    * 查询所有数据不分页
    * @param criteria 条件参数
    * @return List<MwUserDto>
    */
    List<MwUser> queryAll(MwUserQueryCriteria criteria);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwUserDto> all, HttpServletResponse response) throws IOException;

    /**
     * 更新用户状态
     * @param uid uid
     * @param status ShopCommonEnum
     */
    void onStatus(Long uid, Integer status);

    /**
     * 修改余额
     * @param param UserMoneyDto
     */
    void updateMoney(UserMoneyDto param);
    /**
     * 修改余额
     * @param param UserIntegralDto
     */
    void updateIntegral(UserIntegralDto param);

    void updateAdditionalNo(Long uid, String goodsAddi);

    Long todayCount();

    Long proCount();
    Long monthCount();

    void gainParentMoney(Long uid, BigDecimal price, String orderId, Date orderCreateTime, String platform,Integer type);

    UserFeeQueryVo userFeeInfo(MwUser mwUser);

    void initFeeLogs(Integer type, List<Long> uidList);
    List<MwUser> getSpreadList(Long uid, Integer grade);

    List<Long> getSpreadIdList(Long uid, Integer grade);

    List<MwUser> queryContainsAdditionalNo(List<String> nos);

    List<PromUserDto> selectIds(Wrapper<MwUser> wrapper);

    List<UserRefundDto> getUserRefunds(List<Long> uidList);

    List<MwUser> getMemberExpiredList(Date now);

    void expiredUser();

    void spreadUserHb(MwUser parentUser, MwUser user);

    void setUserLevel(String orderId);
}
