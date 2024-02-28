package com.mailvor.modules.sales.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.modules.sales.domain.StoreAfterSalesStatus;
import com.mailvor.modules.sales.service.StoreAfterSalesStatusService;
import com.mailvor.modules.sales.service.mapper.StoreAfterSalesStatusMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : gzlv 2021/6/27 15:56
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class StoreAfterSalesStatusServiceImpl extends BaseServiceImpl<StoreAfterSalesStatusMapper, StoreAfterSalesStatus> implements StoreAfterSalesStatusService {
}
