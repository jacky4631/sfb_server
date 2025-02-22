/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-13
*/

@Data
@TableName("system_qiniu_config")
public class QiniuConfig implements Serializable {

    /** ID */
    @TableId
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Column(name = "id")
    private Long id;


    /** accessKey */
   // @Column(name = "access_key")
    private String accessKey;


    /** Bucket 识别符 */
   // @Column(name = "bucket")
    private String bucket;


    /** 外链域名 */
   // @Column(name = "host",nullable = false)
    private String host;


    /** secretKey */
   // @Column(name = "secret_key")
    private String secretKey;


    /** 空间类型 */
   // @Column(name = "type")
    private String type;


    /** 机房 */
   // @Column(name = "zone")
    private String zone;


    public void copy(QiniuConfig source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
