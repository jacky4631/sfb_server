/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.service.impl;

import cn.hutool.extra.mail.Mail;
import cn.hutool.extra.mail.MailAccount;
import com.mailvor.common.service.impl.BaseServiceImpl;
import com.mailvor.dozer.service.IGenerator;
import com.mailvor.exception.BadRequestException;
import com.mailvor.modules.tools.domain.EmailConfig;
import com.mailvor.modules.tools.domain.vo.EmailVo;
import com.mailvor.modules.tools.service.EmailConfigService;
import com.mailvor.modules.tools.service.mapper.EmailConfigMapper;
import com.mailvor.utils.EncryptUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
// 默认不使用缓存
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;


/**
* @author huangyu
* @date 2020-05-13
*/
@Service
@AllArgsConstructor
//@CacheConfig(cacheNames = "emailConfig")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class EmailConfigServiceImpl extends BaseServiceImpl<EmailConfigMapper, EmailConfig> implements EmailConfigService {

    private final IGenerator generator;

    @Override
//    @CachePut(key = "'1'")
    @Transactional(rollbackFor = Exception.class)
    public void update(EmailConfig emailConfig, EmailConfig old) {
        try {
            if(!emailConfig.getPass().equals(old.getPass())){
                // 对称加密
                emailConfig.setPass(EncryptUtils.desEncrypt(emailConfig.getPass()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
         this.saveOrUpdate(emailConfig);
    }

    @Override
//    @Cacheable(key = "'1'")
    public EmailConfig find() {
        EmailConfig emailConfig = this.list().get(0);
        return emailConfig;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(EmailVo emailVo, EmailConfig emailConfig){
        if(emailConfig == null){
            throw new BadRequestException("请先配置，再操作");
        }
        // 封装
        MailAccount account = new MailAccount();
        // 设置用户
        String user = emailConfig.getFromUser().split("@")[0];
        account.setUser(user);
        account.setHost(emailConfig.getHost());
        account.setPort(Integer.parseInt(emailConfig.getPort()));
        account.setAuth(true);
        try {
            // 对称解密
            account.setPass(EncryptUtils.desDecrypt(emailConfig.getPass()));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
        account.setFrom(emailConfig.getUser()+"<"+emailConfig.getFromUser()+">");
        // ssl方式发送
        account.setSslEnable(true);
        // 使用STARTTLS安全连接
        account.setStarttlsEnable(true);
        String content = emailVo.getContent();
        // 发送
        try {
            int size = emailVo.getTos().size();
            Mail.create(account)
                    .setTos(emailVo.getTos().toArray(new String[size]))
                    .setTitle(emailVo.getSubject())
                    .setContent(content)
                    .setHtml(true)
                    //关闭session
                    .setUseGlobalSession(false)
                    .send();
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
    }
}
