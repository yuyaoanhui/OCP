package com.ocp.rabbit.middleware.datacache;

import com.ocp.rabbit.repository.tool.RabbitException;

/**
 * 内存缓存的管理接口
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public interface Initor {

  /**
   * 初始化，从数据库读取信息点信息
   * 
   * @author yu.yao
   * @return
   * @throws RabbitException
   */
  public void init() throws RabbitException;

  /**
   * 刷新，从数据库同步信息点信息
   * 
   * @author yu.yao
   * @return
   * @throws RabbitException
   */
  public void refresh() throws RabbitException;

}
