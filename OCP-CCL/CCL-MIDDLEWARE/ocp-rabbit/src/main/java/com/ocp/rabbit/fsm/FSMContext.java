package com.ocp.rabbit.fsm;

import com.ocp.rabbit.proxy.process.AbstractProcess;
import com.ocp.rabbit.proxy.process.ProcessContext;

/**
 * FSM的上下文抽象模板
 * 
 * @author yu.yao 2018年8月5日
 *
 */
@SuppressWarnings("rawtypes")
public abstract class FSMContext<T extends AbstractProcess> extends ProcessContext {

}
