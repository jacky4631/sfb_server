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

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
* @author huangyu
* @date 2020-05-13
*/

@Data
@TableName("alipay_config")
public class AlipayConfig implements Serializable {

    /** 主键 */
    @TableId
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
   // @Column(name = "id")
    private Long id;

    @NotBlank
    private String name;

    /** 应用ID */
   // @Column(name = "app_id")
    private String appId;


    /** 编码 */
   // @Column(name = "charset")
    private String charset;


    /** 类型 固定格式json */
   // @Column(name = "format")
    private String format;


    /** 网关地址 */
   // @Column(name = "gateway_url")
    private String gatewayUrl;


    /** 异步回调 */
   // @Column(name = "notify_url")
    private String notifyUrl;


    /** 应用私钥 */
   // @Column(name = "private_key")
    private String privateKey;


    /** 支付宝公钥 */
   // @Column(name = "public_key")
    private String publicKey;


    /** 回调地址 */
   // @Column(name = "return_url")
    private String returnUrl;


    /** 签名方式 */
   // @Column(name = "sign_type")
    private String signType;


    /** 商户号 */
   // @Column(name = "sys_service_provider_id")
    private String sysServiceProviderId;

    /**
     * 应用公钥证书
     * */
    private String appPublicCert;
    /**
     * 支付宝公钥证书
     * */
    private String aliPublicCert;
    /**
     * 支付宝根证书
     * */
    private String aliRootCert;

    public void copy(AlipayConfig source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
