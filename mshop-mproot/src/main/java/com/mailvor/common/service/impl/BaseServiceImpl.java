/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.mailvor.common.service.BaseService;
import com.mailvor.common.web.param.OrderQueryParam;
import com.mailvor.common.web.param.QueryParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

/**
 * @author huangyu
 * @since 2019-10-16
 */
@Slf4j
@SuppressWarnings("unchecked")
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {

    protected Page setPageParam(QueryParam queryParam) {
        return setPageParam(queryParam,null);
    }

    protected Page setPageParam(QueryParam queryParam, OrderItem defaultOrder) {
        Page page = new Page();
        // 设置当前页码
        page.setCurrent(queryParam.getPage());
        // 设置页大小
        page.setSize(queryParam.getLimit());
        /**
         * 如果是queryParam是OrderQueryParam，并且不为空，则使用前端排序
         * 否则使用默认排序
         */
        if (queryParam instanceof OrderQueryParam){
            OrderQueryParam orderQueryParam = (OrderQueryParam) queryParam;
            List<OrderItem> orderItems = orderQueryParam.getOrders();
            if (CollectionUtil.isEmpty(orderItems)){
                page.setOrders(Arrays.asList(defaultOrder));
            }else{
                page.setOrders(orderItems);
            }
        }else{
            page.setOrders(Arrays.asList(defaultOrder));
        }

        return page;
    }

    protected void getPage(Pageable pageable) {
        String order=null;
        if(pageable.getSort()!=null){
            order= pageable.getSort().toString();
            order=order.replace(":","");
            if("UNSORTED".equals(order)){
                order="id desc";
            }
        }
        PageHelper.startPage(pageable.getPageNumber()+1, pageable.getPageSize(),order);
    }

}
