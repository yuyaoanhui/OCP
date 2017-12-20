package com.ocp.midlleware.data.cache;

/**
 * 内存缓存的管理接口
 * 
 * @author yu.yao
 *
 */
public interface Initor {

  /**
   * 初始化，从配置中心读取配置信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void init();

  /**
   * 刷新，从配置中心同步配置信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void refresh(String type);

}
