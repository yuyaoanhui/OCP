package com.ocp.rabbit.bootstrap.context;

import com.ocp.rabbit.fsm.FSM;
import com.ocp.rabbit.fsm.FSMContext;
import com.ocp.rabbit.fsm.IState;

@SuppressWarnings("rawtypes")
public abstract class RabbitContext extends FSMContext {
  /**
   * context体系中应用上下文类型，分别对应不同级别的系统部件
   * 
   * @author yu.yao 2018年8月3日
   *
   */
  public static enum TYPE {
    RABBIT, FSM, PROCESS, CHAIN, EXTRACTOR, COMPONENT
  }

  /**
   * 当前处于的状态
   */
  private IState stat;

  public IState getStat() {
    return stat;
  }

  public void setStat(IState stat) {
    this.stat = stat;
  }

  public static void init() {
    FSM.getInstance();
  }
}
