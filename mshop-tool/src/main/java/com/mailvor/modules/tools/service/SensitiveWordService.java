/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service;

import com.mailvor.modules.tools.domain.SensitiveWord;

import java.util.List;

/**
 * @author chaijing
 * @date 2022-10-06
 */
public interface SensitiveWordService {
    List<SensitiveWord> findAll();
}
