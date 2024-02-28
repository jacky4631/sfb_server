/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.energy.service;

import com.mailvor.common.service.BaseService;
import com.mailvor.modules.energy.domain.UserEnergyOrder;
import com.mailvor.modules.energy.dto.EnergyConfigDto;
import com.mailvor.modules.energy.dto.ExpCardConfigDto;
import com.mailvor.modules.energy.dto.UserEnergyOrderQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author huangyu
* @date 2023-02-04
*/
public interface UserEnergyOrderService extends BaseService<UserEnergyOrder>{
    List<UserEnergyOrder> getUnpackEnergyList();
    List<UserEnergyOrder> getUnpaidList(List<String> platforms);
    void unlockOrder();
    List<UserEnergyOrder> getRefundList();

    UserEnergyOrder getByOrderId(String orderId, Long uid);

    Map<String, Object> queryAll(UserEnergyOrderQueryCriteria criteria, Pageable pageable);

    List<UserEnergyOrder> queryAll(UserEnergyOrderQueryCriteria criteria);

    int mysqlInsertOrUpdateBath(List list);
    void createEnergyOrders(Long uid, BigDecimal energy, String platform, EnergyConfigDto configDto, Integer type);

    void createEnergyOrders(Long uid, BigDecimal energy, String platform, EnergyConfigDto configDto, Integer type, BigDecimal times);
    void createExpOrders(Long uid, String platform, ExpCardConfigDto config, Integer type);
}
