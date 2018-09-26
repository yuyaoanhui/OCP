package com.ocp.rabbit.fsm;

/**
 * 信息抽取流程状态接口，本状态用来控制抽取器在责任链中分支走向
 * 
 * @author yu.yao 2018年8月2日
 *
 */
public interface IState {
  /**
   * 状态进入时执行动作
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void enter();

  /**
   * 状态退出时执行动作
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void exit();

  /**
   * 状态的内部执行机制
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void update();

  /**
   * 状态的初始化
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void init();

  /**
   * 状态转移判断
   * 
   * @author yu.yao
   * @param
   * @return
   */
  AbstractFSMState checkTransition();
}
