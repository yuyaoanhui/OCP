package com.ocp.rabbit.proxy.component;

import com.ocp.rabbit.proxy.CommonUnit;
import com.ocp.rabbit.proxy.chain.HandleChain;

/**
 * 构件抽象类
 * 
 * @author yu.yao 2018年7月30日
 *
 */
public abstract class AbstractComp extends CommonUnit<AbstractComp> {

  public static final HandleChain chain = new HandleChain();
}
