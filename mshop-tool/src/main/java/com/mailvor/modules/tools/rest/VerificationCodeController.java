/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.tools.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.mailvor.modules.tools.domain.VerificationCode;
import com.mailvor.modules.tools.domain.vo.EmailVo;
import com.mailvor.modules.tools.service.EmailConfigService;
import com.mailvor.modules.tools.service.VerificationCodeService;
import com.mailvor.utils.MshopConstant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangyu
 * @date 2018-12-26
 */
@RestController
@RequestMapping("/api/code")
@Tag(name = "工具：验证码管理")
public class VerificationCodeController {

    private final VerificationCodeService verificationCodeService;

    private final EmailConfigService emailService;

    public VerificationCodeController(VerificationCodeService verificationCodeService,  EmailConfigService emailService) {
        this.verificationCodeService = verificationCodeService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/resetEmail")
    @Operation(summary = "重置邮箱，发送验证码")
    public ResponseEntity<Object> resetEmail(@RequestBody VerificationCode code) throws Exception {
        code.setScenes(MshopConstant.RESET_MAIL);
        EmailVo emailVo = verificationCodeService.sendEmail(code);
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/email/resetPass")
    @Operation(summary = "重置密码，发送验证码")
    public ResponseEntity<Object> resetPass(@RequestParam String email) throws Exception {
        VerificationCode code = new VerificationCode();
        code.setType("email");
        code.setValue(email);
        code.setScenes(MshopConstant.RESET_MAIL);
        EmailVo emailVo = verificationCodeService.sendEmail(code);
        emailService.send(emailVo,emailService.find());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/validated")
    @Operation(summary = "验证码验证")
    public ResponseEntity<Object> validated(VerificationCode code){
        verificationCodeService.validated(code);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
