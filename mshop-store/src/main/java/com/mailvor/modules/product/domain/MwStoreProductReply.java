/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.product.domain;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.mailvor.domain.BaseDomain;
import com.mailvor.modules.user.domain.MwUser;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
* @author huangyu
* @date 2020-05-12
*/

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("mw_store_product_reply")
public class MwStoreProductReply extends BaseDomain {

    /** 评论ID */
    @TableId
    private Long id;


    /** 用户ID */
    private Long uid;


    /** 订单ID */
    private Long oid;


    /** 唯一id */
    @TableField(value = "`unique`")
    private String unique;


    /** 产品id */
    private Long productId;


    /** 某种商品类型(普通商品、秒杀商品） */
    private String replyType;


    /** 商品分数 */
    private Integer productScore;


    /** 服务分数 */
    private Integer serviceScore;


    /** 评论内容 */
    private String comment;


    /** 评论图片 */
    private String pics;



    /** 管理员回复内容 */
    @NotBlank(message = "回复内容不能为空")
    private String merchantReplyContent;


    /** 管理员回复时间 */
    private Date merchantReplyTime;


    /** 0未回复1已回复 */
    private Integer isReply;

    @TableField(exist = false)
    private MwStoreProduct storeProduct;

    @TableField(exist = false)
    private MwUser user;

    public void copy(MwStoreProductReply source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
