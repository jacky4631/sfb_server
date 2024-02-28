/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.service.impl;

import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.modules.product.domain.MwStoreProductAttrValue;
import com.mailvor.modules.product.service.MwStoreProductAttrValueService;
import com.mailvor.modules.product.service.mapper.StoreProductAttrValueMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
* @author huangyu
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MwStoreProductAttrValueServiceImpl extends BaseServiceImpl<StoreProductAttrValueMapper, MwStoreProductAttrValue> implements MwStoreProductAttrValueService {


}
