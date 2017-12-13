package com.futuredata.judicature.sdk.interceptor;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.futuredata.judicature.sdk.utils.UniqRequestIdGen;

/**
 * 为每个请求头添加requestId属性
 * 
 * @author yu.yao
 *
 */
@Aspect
@Component
public class RequestIDEnhanceInterceptor {

  @Before("@annotation(RequestIDEnhance)") // 拦截被RequestIDEnhance注解的方法
  public void setID() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    request.setAttribute("requestId", UniqRequestIdGen.generatorRequestId());
  }

}
