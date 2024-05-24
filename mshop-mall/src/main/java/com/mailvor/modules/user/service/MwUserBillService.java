/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.user.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.user.domain.MwUserBill;
import com.mailvor.modules.user.service.dto.MwUserBillDto;
import com.mailvor.modules.user.service.dto.MwUserBillQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2020-05-12
*/
public interface MwUserBillService extends BaseService<MwUserBill>{

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
//    void expend(Long uid,String title,String category,String type,double number,double balance,String mark,String linkid);
    public void expend(Long uid,Long origUid, String title,String category,
                       String type,double number,
                       double balance,String mark,String linkid, Date orderCreateTime);
    void income(Long uid,Long origUid,String title,String category,String type,double number,
                double balance,String mark,String linkid);
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
    void income(Long uid,String title,String category,String type,double number,
                double balance,String mark,String linkid);
    void income(Long uid,Long origUid,String title,String category,String type,String platform, double number,
                double balance,String mark,String linkid, Date orderCreateTime, Integer status, Date unlockTime);
    Long cumulativeAttendance(Long uid);

    /**
     * 获取推广订单列表
     * @param uid   uid
     * @param page  page
     * @param limit limit
     * @return Map
     */
    Map<String,Object> spreadOrder(Long uid,int page,int limit);

    /**
     * 获取用户账单记录
     * @param page page
     * @param limit limit
     * @param uid uid
     * @param type BillDetailEnum
     * @return map
     */
    Map<String,Object> getUserBillList(int page, int limit, long uid, int type, String month);


    double getBrokerage(int uid);

    /**
     * 统计昨天的佣金
     * @param uid uid
     * @return double
     */
    double yesterdayCommissionSum(Long uid);

    /**
     * 根据类别获取账单记录
     * @param uid uid
     * @param category  BillDetailEnum
     * @param page page
     * @param limit limit
     * @return List
     */
    Map<String, Object> userBillList(Long uid,String category,String type, String platform, int page,int limit, Integer unlockStatus);

    /**
    * 查询数据分页
    * @param criteria 条件
    * @param pageable 分页参数
    * @return Map<String,Object>
    */
    Map<String,Object> queryAll(MwUserBillQueryCriteria criteria, Pageable pageable);

    /**
    * 导出数据
    * @param all 待导出的数据
    * @param response /
    * @throws IOException /
    */
    void download(List<MwUserBillDto> all, HttpServletResponse response) throws IOException;

    List<MwUserBill> getListByOrderId(String linkId);

    void invalidByOrderId(String orderId);

    double sumUnlockMoney(Long uid);

    List<MwUserBill> getUnlockList(Integer limit);
}
