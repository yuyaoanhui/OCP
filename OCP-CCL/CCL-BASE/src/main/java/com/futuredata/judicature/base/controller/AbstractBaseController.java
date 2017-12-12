package com.futuredata.judicature.base.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.futuredata.judicature.sdk.utils.UniqRequestIdGen;

/**
 * 控制层基础控制类抽象模板,提供基础请求/回复信息的获取
 * <p>
 * 同时为每个request设置requestId
 * </p>
 * 
 * @author yu.yao
 */
@Aspect
public abstract class AbstractBaseController {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBaseController.class);

  public HttpServletRequest getRequest() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    request.setAttribute("requestId", UniqRequestIdGen.generatorRequestId());
    logger.info((String) request.getAttribute("requestId"));
    return request;
  }

  public HttpServletResponse getResponse() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
  }

  public HttpSession getSession() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    return request.getSession();
  }
}
