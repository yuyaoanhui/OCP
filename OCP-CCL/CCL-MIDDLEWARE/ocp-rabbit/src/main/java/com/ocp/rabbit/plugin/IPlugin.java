package com.ocp.rabbit.plugin;

import com.ocp.rabbit.plugin.entity.PluginMsg;

/**
 * 插件接口类，定义插拔方式
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public interface IPlugin {
  /**
   * 向插件总线注册指定插件
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void register();

  /**
   * 从插件总线注销指定插件
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void unregister();

  /**
   * 消费接收到的消息和数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void consume();

  /**
   * 向总线中传递消息和输出数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void deliver(PluginMsg msg);

  /**
   * 执行本插件的逻辑处理
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void handle();

  /**
   * 实例化注册
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void initRegister();
}
