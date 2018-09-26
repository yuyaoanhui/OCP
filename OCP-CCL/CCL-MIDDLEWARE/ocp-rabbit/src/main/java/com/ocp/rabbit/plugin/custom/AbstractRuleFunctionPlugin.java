package com.ocp.rabbit.plugin.custom;

import com.ocp.rabbit.plugin.AbstractPlugin;

public abstract class AbstractRuleFunctionPlugin extends AbstractPlugin<AbstractRuleFunctionPlugin>
    implements Cloneable {
  public com.ocp.rabbit.proxy.process.AbstractProcess.Context context;

  public void setContext(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    this.context = context;
  }

  public AbstractRuleFunctionPlugin(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    this.context = context;
  }

  @Override
  public void handle() {

  }
}
