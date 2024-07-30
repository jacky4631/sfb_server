/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.shop.service;


import com.mailvor.common.service.BaseService;
import com.mailvor.modules.shop.domain.MwSystemAttachment;

/**
 * <p>
 * 附件管理表 服务类
 * </p>
 *
 * @author huangyu
 * @since 2019-11-11
 */
public interface MwSystemAttachmentService extends BaseService<MwSystemAttachment> {

    /**
     *  根据名称获取
     * @param name name
     * @return MWSystemAttachment
     */
    MwSystemAttachment getInfo(String name);

    /**
     *  根据code获取
     * @param code code
     * @return MWSystemAttachment
     */
    MwSystemAttachment getByCode(String code);

    /**
     * 添加附件记录
     * @param name 名称
     * @param attSize 附件大小
     * @param attDir 路径
     * @param sattDir 路径
     */
    void attachmentAdd(String name,String attSize,String attDir,String sattDir);

    /**
     * 添加附件记录
     * @param name 名称
     * @param attSize 附件大小
     * @param attDir 路径
     * @param sattDir 路径
     * @param uid 用户id
     * @param code 邀请码
     */
    void newAttachmentAdd(String name,String attSize,String attDir,String sattDir,Long uid,String code);


}
