package com.ocp.rabbit.proxy.chain;

/**
 * 构件的责任链接口
 * 
 * @author yu.yao 2018年8月5日
 *
 */
public interface Handle {
  /**
   * 执行逻辑处理并进入下一步直到链条结束
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void doHandle(HandleChain chain);
}
