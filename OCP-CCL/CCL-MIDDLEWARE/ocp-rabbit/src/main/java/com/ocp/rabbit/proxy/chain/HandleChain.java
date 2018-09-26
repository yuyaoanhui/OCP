package com.ocp.rabbit.proxy.chain;

import java.util.ArrayList;
import java.util.List;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.proxy.CommonUnit;

/**
 * 流程处理责任链——定义构件的装配逻辑
 * 
 * @author yu.yao 2018年8月5日
 *
 * @param <T> 具体的处理逻辑实现类
 */
public class HandleChain implements Handle {

  public ResultCode code;
  /**
   * 存储处理逻辑
   */
  @SuppressWarnings("rawtypes")
  private List<CommonUnit> handles = new ArrayList<CommonUnit>();
  /**
   * 用于标记处理逻辑的引用顺序
   */
  private int index = 0;

  /**
   * 往处理链条中添加处理逻辑
   * 
   * @author yu.yao
   * @param
   * @return
   */
  @SuppressWarnings("rawtypes")
  public HandleChain addHandle(CommonUnit t) {
    handles.add(t);
    return this;
  }

  @SuppressWarnings("rawtypes")
  public List<CommonUnit> getHandles() {
    return handles;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void doHandle(HandleChain chain) {
    if (index == handles.size()) {// 结束条件
      return;
    }
    CommonUnit h = handles.get(index);
    index++;// 每添加一个处理逻辑，index自增1
    h.handle();
    chain.doHandle(chain);
  }

  /**
   * 强制结束
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void stopNow(HandleChain chain, ResultCode reason) {
    index = handles.size();
    code = reason;
    doHandle(chain);
  }

}
