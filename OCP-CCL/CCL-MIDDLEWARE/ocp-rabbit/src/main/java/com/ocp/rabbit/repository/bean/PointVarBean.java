package com.ocp.rabbit.repository.bean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 信息点配置包装类
 * 
 * @author yu.yao 2018年8月21日
 *
 */
public class PointVarBean {
  private List<ParamsBean> paramsList;
  private Method method;
  private List<PointVarBean> adjust;

  public List<ParamsBean> getParamsList() {
    return paramsList;
  }

  public void setParamsList(List<ParamsBean> paramsList) {
    this.paramsList = paramsList;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public List<PointVarBean> getAdjust() {
    return adjust;
  }

  public void setAdjust(List<PointVarBean> adjust) {
    this.adjust = adjust;
  }

}
