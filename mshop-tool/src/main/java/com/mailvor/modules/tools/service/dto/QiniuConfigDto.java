/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service.dto;

import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-13
*/
@Data
public class QiniuConfigDto implements Serializable {

    /** ID */
    private Long id;

    /** accessKey */
    private String accessKey;

    /** Bucket 识别符 */
    private String bucket;

    /** 外链域名 */
    private String host;

    /** secretKey */
    private String secretKey;

    /** 空间类型 */
    private String type;

    /** 机房 */
    private String zone;
}
