package com.ocp.rabbit.fsm;

/**
 * 状态节点抽象模板
 * 
 * @author yu.yao 2018年8月3日
 *
 */
public abstract class AbstractFSMState implements IState {
  /**
   * 所有状态类共用一个状态机
   */
  public static final FSM machine = FSM.getInstance();
  /**
   * 该状态的ID
   */
  private int ID;

  public int getID() {
    return ID;
  }

  @Override
  public void enter() {
    // TODO Auto-generated method stub

  }

  @Override
  public void exit() {
    // TODO Auto-generated method stub

  }

  @Override
  public void update() {
    // TODO Auto-generated method stub

  }

  @Override
  public void init() {
    // TODO Auto-generated method stub

  }

  @Override
  public AbstractFSMState checkTransition() {
    // TODO Auto-generated method stub
    return null;
  }

}
