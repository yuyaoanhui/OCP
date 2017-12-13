package com.futuredata.judicature.base.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 控制层基础控制类抽象模板,提供基础请求/回复信息的获取
 * <p>
 * 同时为每个request设置requestId
 * </p>
 * 
 * @author yu.yao
 */
public abstract class AbstractBaseController {

  public HttpServletRequest getRequest() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return request;
  }

  public HttpServletResponse getResponse() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
  }

  public HttpSession getSession() {
    return getRequest().getSession();
  }
}
