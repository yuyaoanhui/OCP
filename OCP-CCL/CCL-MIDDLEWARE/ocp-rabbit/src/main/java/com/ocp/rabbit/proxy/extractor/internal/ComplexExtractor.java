package com.ocp.rabbit.proxy.extractor.internal;

import com.ocp.rabbit.proxy.extractor.AbstractExtractor;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;

/**
 * 复杂信息点抽取器：在简单信息点之上增加若干条件判断的逻辑
 * 
 * @author yu.yao 2018年8月2日
 *
 */
public class ComplexExtractor extends AbstractExtractor {

  private Context context;

  public ComplexExtractor(Context context) {
    this.context = context;
  }

  @Override
  public void handle() {
    automatic(context);
    doParams(context);
  }
}
