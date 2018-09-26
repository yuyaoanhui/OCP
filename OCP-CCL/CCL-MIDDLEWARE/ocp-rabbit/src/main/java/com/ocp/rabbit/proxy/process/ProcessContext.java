package com.ocp.rabbit.proxy.process;

import com.ocp.rabbit.proxy.chain.HandleChain;
import com.ocp.rabbit.proxy.extractor.AbstractExtractor;
import com.ocp.rabbit.proxy.extractor.ExtractorContext;
import com.ocp.rabbit.repository.entity.RabbitInfo;

/**
 * 抽取流程通用上下文 </br>
 * 该类存储公开数据和方法，更多为了体现层级继承关系
 * 
 * @author yu.yao 2018年8月2日
 *
 */
@SuppressWarnings("rawtypes")
public abstract class ProcessContext<T extends AbstractExtractor> extends ExtractorContext {
  public RabbitInfo rabbitInfo = new RabbitInfo();

  public final HandleChain complexChain = new HandleChain();
}
