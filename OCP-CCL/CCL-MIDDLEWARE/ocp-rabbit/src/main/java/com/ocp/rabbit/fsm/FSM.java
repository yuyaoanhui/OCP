package com.ocp.rabbit.fsm;

import java.util.List;

/**
 * 状态机(machine)
 * 
 * @author yu.yao 2018年8月3日
 *
 */
public final class FSM {
  /**
   * FSM上下文类继承自FSMContext抽象模板</br>
   * 定义成内部类便于信息隐藏——本context存储的数据
   * 
   * @author yu.yao 2018年8月4日
   *
   */
  @SuppressWarnings("rawtypes")
  public static final class Context extends FSMContext {
    private static volatile int count;
    private static volatile Context instance;

    private Context() {
      synchronized (Context.class) {// 防止反射破坏单例
        if (count > 0) {
          throw new RuntimeException("禁止创建两个实例");
        }
        count = 1;
      }
    }

    /**
     * 保证返回正确的非空实例,速度慢
     *
     * @author yu.yao
     * @param
     * @return
     */
    private synchronized static Context getContext() {// double check
      if (instance == null) {
        synchronized (Context.class) {
          if (instance == null) {
            instance = new Context();
          }
        }
      }
      return instance;
    }

    /**
     * 返回上下文操作非空句柄,速度快
     *
     * @author yu.yao
     * @param
     * @return 句柄(可能是null)
     */
    private static Context getInstance() {
      return instance;
    }
  }

  static {
    Context.getContext();
  }

  /**
   * FSM上下文
   */
  public static final Context context = Context.getInstance();

  /**
   * 所有状态集合
   */
  private volatile List<AbstractFSMState> states;
  /**
   * 当前状态
   */
  private volatile AbstractFSMState currentState;
  /**
   * 默认状态
   */
  private volatile AbstractFSMState defaultState;
  /**
   * 目标状态
   */
  private volatile AbstractFSMState goalState;
  /**
   * 目标ID
   */
  private int goalID;

  public List<AbstractFSMState> getStates() {
    return states;
  }

  /**
   * 获取当前状态
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public AbstractFSMState getCurrentState() {
    return currentState;
  }

  /**
   * 获取默认状态
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public AbstractFSMState getDefaultState() {
    return defaultState;
  }

  /**
   * 获取目标状态
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public AbstractFSMState getGoalState() {
    return goalState;
  }

  /**
   * 获取目标ID
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public int getGoalID() {
    return goalID;
  }

  /**
   * 更新状态机状态
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public synchronized void updateFSMState() {

  }

  /**
   * 给状态机添加状态
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void addFSMState(AbstractFSMState state) {

  }

  /**
   * 状态转移
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public synchronized void transitionFSMState(int goalID) {

  }

  /**
   * 状态重置
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public synchronized void reset() {

  }

  private static volatile int count;
  private static volatile FSM instance;

  static {
    getContext();
  }

  private FSM() {
    synchronized (FSM.class) {// 防止反射破坏单例
      if (count > 0) {
        throw new RuntimeException("禁止创建两个实例");
      }
      count = 1;
    }
  }

  /**
   * 保证返回正确的非空实例,速度慢
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private synchronized static FSM getContext() {// double check
    if (instance == null) {
      synchronized (FSM.class) {
        if (instance == null) {
          instance = new FSM();
        }
      }
    }
    return instance;
  }

  /**
   * 返回上下文操作非空句柄,速度快
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static FSM getInstance() {
    return instance;
  }
}
