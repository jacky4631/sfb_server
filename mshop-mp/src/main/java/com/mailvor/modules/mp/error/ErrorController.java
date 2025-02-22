/**
 * Copyright (C) 2018-2024
 * All rights reserved, Designed By www.mailvor.com
 */
package com.mailvor.modules.mp.error;

import io.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <pre>
 * 出错页面控制器
 * </pre>
 *
 */
@Api(tags = "商城：异常统一处理")
@Controller
@RequestMapping("/error")
public class ErrorController {

  @GetMapping(value = "/404")
  public String error404() {
    return "error";
  }

  @GetMapping(value = "/500")
  public String error500() {
    return "error";
  }

}
