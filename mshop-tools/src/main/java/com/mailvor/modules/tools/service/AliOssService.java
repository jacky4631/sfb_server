/**
 * Copyright (C) 2018-2022
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @author huangyu
 * @date 2018-12-31
 */
public interface AliOssService {

    String uploadFileAvatar(MultipartFile file);

    String uploadCard(InputStream inputStream, String filename, String path);
}
