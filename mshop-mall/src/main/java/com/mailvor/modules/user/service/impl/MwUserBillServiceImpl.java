/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.common.utils.QueryHelpPlus;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.enums.BillDetailEnum;
import com.mailvor.enums.BillEnum;
import com.mailvor.enums.BillInfoEnum;
import com.mailvor.enums.ShopCommonEnum;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.service.MwUserBillService;
import com.mailvor.modules.user.service.dto.*;
import com.mailvor.modules.user.service.mapper.UserBillMapper;
import com.mailvor.modules.user.vo.BillVo;
import com.mailvor.modules.user.vo.MwUserBillQueryVo;
import com.mailvor.utils.FileUtil;
import com.mailvor.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwUserBillServiceImpl extends BaseServiceImpl<UserBillMapper, MwUserBill> implements MwUserBillService {

    private final IGenerator generator;
    private final UserBillMapper mwUserBillMapper;


    /**
     * 增加支出流水
     * @param uid uid
     * @param title 账单标题
     * @param category 明细种类
     * @param type 明细类型
     * @param number 明细数字
     * @param balance 剩余
     * @param mark 备注
     */
    @Override
    public void expend(Long uid,Long origUid, String title,String category,
                       String type,double number,
                       double balance,String mark,String linkid, Date orderCreateTime){
        MwUserBill userBill = MwUserBill.builder()
                .uid(uid)
                .origUid(origUid)
                .title(title)
                .category(category)
                .type(type)
                .number(BigDecimal.valueOf(number))
                .balance(BigDecimal.valueOf(balance))
                .mark(mark)
                .pm(BillEnum.PM_0.getValue())
                .linkId(linkid)
                .orderCreateTime(orderCreateTime)
                .build();

        mwUserBillMapper.insert(userBill);
    }
    @Override
    public void income(Long uid,String title,String category,String type,double number,
                       double balance,String mark,String linkid){
        MwUserBill userBill = MwUserBill.builder()
                .uid(uid)
                .title(title)
                .category(category)
                .type(type)
                .number(BigDecimal.valueOf(number))
                .balance(BigDecimal.valueOf(balance))
                .mark(mark)
                .pm(BillEnum.PM_1.getValue())
                .linkId(linkid)
                .build();

        mwUserBillMapper.insert(userBill);
    }
    /**
     * 增加收入/支入流水
     * @param uid uid
     * @param title 账单标题
     * @param category 明细种类
     * @param type 明细类型
     * @param number 明细数字
     * @param balance 剩余
     * @param mark 备注
     * @param linkid 关联id
     */
    @Override
    public void income(Long uid,Long origUid,String title,String category,String type,double number,
                       double balance,String mark,String linkid){
        MwUserBill userBill = MwUserBill.builder()
                .uid(uid)
                .origUid(origUid)
                .title(title)
                .category(category)
                .type(type)
                .number(BigDecimal.valueOf(number))
                .balance(BigDecimal.valueOf(balance))
                .mark(mark)
                .pm(BillEnum.PM_1.getValue())
                .linkId(linkid)
                .build();

        mwUserBillMapper.insert(userBill);
    }
    @Override
    public void income(Long uid,Long origUid,String title,String category,String type, String platform, double number,
                       double balance,String mark,String linkid, Date orderCreateTime){
        MwUserBill userBill = MwUserBill.builder()
                .uid(uid)
                .origUid(origUid)
                .title(title)
                .category(category)
                .type(type)
                .platform(platform)
                .number(BigDecimal.valueOf(number))
                .balance(BigDecimal.valueOf(balance))
                .mark(mark)
                .pm(BillEnum.PM_1.getValue())
                .linkId(linkid)
                .orderCreateTime(orderCreateTime)
                .build();

        mwUserBillMapper.insert(userBill);
    }
    /**
     * 签到了多少次
     *
     * @param uid
     * @return
     */
    @Override
    public Long cumulativeAttendance(Long uid) {
       LambdaQueryWrapper<MwUserBill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MwUserBill::getUid,uid).eq(MwUserBill::getCategory,"integral")
                .eq(MwUserBill::getType,"sign").eq(MwUserBill::getPm,1);
        return mwUserBillMapper.selectCount(wrapper);
    }

    /**
     * 获取推广订单列表
     * @param uid   uid
     * @param page  page
     * @param limit limit
     * @return Map
     */
    @Override
    public Map<String, Object> spreadOrder(Long uid, int page, int limit) {
       QueryWrapper<MwUserBill> wrapper = new QueryWrapper<>();
        wrapper.lambda().in(MwUserBill::getUid, uid)
                .eq(MwUserBill::getType, BillDetailEnum.TYPE_2.getValue())
                .eq(MwUserBill::getCategory, BillDetailEnum.CATEGORY_1.getValue());
        wrapper.orderByDesc("time").groupBy("time");
        Page<MwUserBill> pageModel = new Page<>(page, limit);
        List<String> list = mwUserBillMapper.getBillOrderList(wrapper, pageModel);

        Long count = mwUserBillMapper.selectCount(Wrappers.<MwUserBill>lambdaQuery()
                .eq(MwUserBill::getUid, uid)
                .eq(MwUserBill::getType, BillDetailEnum.TYPE_2.getValue())
                .eq(MwUserBill::getCategory, BillDetailEnum.CATEGORY_1.getValue()));
        List<BillOrderDto> listT = new ArrayList<>();
        for (String str : list) {
            BillOrderDto billOrderDTO = new BillOrderDto();
            List<BillOrderRecordDto> orderRecordDTOS = mwUserBillMapper
                    .getBillOrderRList(str, uid);
            billOrderDTO.setChild(orderRecordDTOS);
            billOrderDTO.setCount(orderRecordDTOS.size());
            billOrderDTO.setTime(str);

            listT.add(billOrderDTO);
        }

        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("list", listT);
        map.put("count", count);

        return map;
    }

    /**
     * 获取用户账单记录
     * @param page page
     * @param limit limit
     * @param uid uid
     * @param type BillDetailEnum
     * @return map
     */
    @Override
    public Map<String,Object> getUserBillList(int page, int limit, long uid, int type) {
       QueryWrapper<MwUserBill> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MwUserBill::getUid,uid).orderByDesc(MwUserBill::getCreateTime)
                .orderByAsc(MwUserBill::getId);
        wrapper.groupBy("time");
        switch (BillInfoEnum.toType(type)){
            case PAY_PRODUCT:
                wrapper.lambda().eq(MwUserBill::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(MwUserBill::getType,BillDetailEnum.TYPE_3.getValue());
                break;
            case RECHAREGE:
                wrapper.lambda().eq(MwUserBill::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(MwUserBill::getType,BillDetailEnum.TYPE_1.getValue());
                break;
            case BROKERAGE:
                wrapper.lambda().eq(MwUserBill::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(MwUserBill::getType,BillDetailEnum.TYPE_2.getValue());
                break;
            case EXTRACT:
                wrapper.lambda().eq(MwUserBill::getCategory,BillDetailEnum.CATEGORY_1.getValue());
                wrapper.lambda().eq(MwUserBill::getType,BillDetailEnum.TYPE_4.getValue());
                break;
            case SIGN_INTEGRAL:
                wrapper.lambda().eq(MwUserBill::getCategory,BillDetailEnum.CATEGORY_2.getValue());
                wrapper.lambda().eq(MwUserBill::getType,BillDetailEnum.TYPE_10.getValue());
                break;
            default:
                wrapper.lambda().eq(MwUserBill::getCategory,BillDetailEnum.CATEGORY_1.getValue());

        }
        Page<MwUserBill> pageModel = new Page<>(page, limit);
        List<BillVo> billDTOList = mwUserBillMapper.getBillList(wrapper,pageModel);
        for (BillVo billDTO : billDTOList) {
           LambdaQueryWrapper<MwUserBill> wrapperT = new LambdaQueryWrapper<>();
            wrapperT.in(MwUserBill::getId,Arrays.asList(billDTO.getIds().split(",")));
            wrapperT.orderByDesc(MwUserBill::getCreateTime);
            billDTO.setList(mwUserBillMapper.getUserBillList(wrapperT));

        }
        Map<String,Object> map = new HashMap<>();
        map.put("list",billDTOList);
        map.put("total",pageModel.getTotal());
        map.put("totalPage",pageModel.getPages());
        return map;
       // return billDTOList;
    }

    @Override
    public double getBrokerage(int uid) {
        return mwUserBillMapper.sumPrice(uid);
    }

    /**
     * 统计昨天的佣金
     * @param uid uid
     * @return double
     */
    @Override
    public double yesterdayCommissionSum(Long uid) {
        return mwUserBillMapper.sumYesterdayPrice(uid);
    }

    /**
     * 根据类别获取账单记录
     * @param uid uid
     * @param category  BillDetailEnum
     * @param page page
     * @param limit limit
     * @return List
     */
    @Override
    public Map<String, Object> userBillList(Long uid,String category,String type, String platform, int page,int limit) {
       LambdaQueryWrapper<MwUserBill> wrapper = new LambdaQueryWrapper<>();
        wrapper
                .eq(MwUserBill::getStatus, ShopCommonEnum.IS_STATUS_1.getValue())
                .eq(MwUserBill::getUid,uid)
                .eq(MwUserBill::getCategory,category)
                .orderByDesc(MwUserBill::getId);
        if(StringUtils.isNotBlank(type)) {
            wrapper.eq(MwUserBill::getType,type);
        }
        if(StringUtils.isNotBlank(platform)) {
            wrapper.eq(MwUserBill::getPlatform,platform);
        }
        Page<MwUserBill> pageModel = new Page<>(page, limit);
        IPage<MwUserBill> pageList = mwUserBillMapper.selectPage(pageModel,wrapper);
        ;
        Map<String, Object> map = new HashMap<>();
        map.put("list", generator.convert(pageList.getRecords(), MwUserBillQueryVo.class));
        map.put("total", pageList.getTotal());
        map.put("totalPage", pageList.getPages());
        return map;
    }


    //============================================//

    @Override
    //@Cacheable
    public Map<String, Object> queryAll(MwUserBillQueryCriteria criteria, Pageable pageable) {
        getPage(pageable);
        PageInfo<MwUserBillDto> page = new PageInfo<>(baseMapper.selectList(QueryHelpPlus.getPredicate(MwUserBill.class, criteria)));
        Map<String, Object> map = new LinkedHashMap<>(2);
        map.put("content", page.getList());
        map.put("totalElements", page.getTotal());
        return map;
    }

    @Override
    public void download(List<MwUserBillDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MwUserBillDto mwUserBill : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户uid", mwUserBill.getUid());
            map.put("关联id", mwUserBill.getLinkId());
            map.put("0 = 支出 1 = 获得", mwUserBill.getPm());
            map.put("账单标题", mwUserBill.getTitle());
            map.put("明细种类", mwUserBill.getCategory());
            map.put("明细类型", mwUserBill.getType());
            map.put("明细数字", mwUserBill.getNumber());
            map.put("剩余", mwUserBill.getBalance());
            map.put("备注", mwUserBill.getMark());
            map.put("0 = 带确定 1 = 有效 -1 = 无效", mwUserBill.getStatus());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}
