/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.pay.service.impl;

import com.mailvor.modules.pay.service.MwPayRestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


/**
* @author wangke
* @date 2020-05-12
*/
@Service
@AllArgsConstructor
public class MwPayRestServiceImpl implements MwPayRestService {

    @Resource
    private RestTemplate restTemplate;
}
