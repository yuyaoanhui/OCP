package com.ocp.rabbit.proxy.process;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.plugin.AbstractPluginHub;
import com.ocp.rabbit.plugin.IPlugin;
import com.ocp.rabbit.plugin.custom.AbstractRuleFunctionPlugin;
import com.ocp.rabbit.proxy.CommonUnit;
import com.ocp.rabbit.proxy.IProcess;

/**
 * 信息抽取流程抽象模板
 * 
 * @author yu.yao 2018年8月2日
 *
 */
public abstract class AbstractProcess extends CommonUnit<AbstractProcess> implements IProcess {
  private static final Logger logger = LoggerFactory.getLogger(AbstractProcess.class);

  /**
   * 定义成内部类便于信息隐藏——本context存储的数据</br>
   * 该类存储私密数据和方法
   * 
   * @author yu.yao 2018年8月5日
   *
   */
  @SuppressWarnings("rawtypes")
  public static final class Context extends ProcessContext {
    public List<AbstractRuleFunctionPlugin> plugins = new ArrayList<AbstractRuleFunctionPlugin>();

    public Context(String daemon) {
      if ("daemon".equals(daemon)) {
        logger.info("启动守护线程");
      }
    }

    public Context() {
      try {
        Set<IPlugin> plugins = AbstractPluginHub.getPluginSet();
        for (IPlugin plugin : plugins) {
          if (plugin instanceof AbstractRuleFunctionPlugin) {
            AbstractRuleFunctionPlugin obj = ((AbstractRuleFunctionPlugin) plugin);
            Constructor<? extends AbstractRuleFunctionPlugin> c0 =
                obj.getClass().getDeclaredConstructor(Context.class);
            this.plugins.add(c0.newInstance(this));
          }
        }
      } catch (NoSuchMethodException | SecurityException | InstantiationException
          | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  public final Context context = new Context();

}
