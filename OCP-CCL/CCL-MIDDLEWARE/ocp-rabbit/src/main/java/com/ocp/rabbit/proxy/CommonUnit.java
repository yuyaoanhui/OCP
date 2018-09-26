package com.ocp.rabbit.proxy;

/**
 * 系统部件通用模板</br>
 * 适用于：component、extractor、process
 * 
 * @author yu.yao 2018年8月5日
 *
 */
@SuppressWarnings("rawtypes")
public abstract class CommonUnit<T extends CommonUnit> {
  /**
   * 系统部件核心处理
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public abstract void handle();
}
