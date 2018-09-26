package com.ocp.rabbit.plugin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.plugin.entity.PluginMsg;
import com.ocp.rabbit.proxy.CommonUnit;
import com.ocp.rabbit.proxy.component.CompContext;

/**
 * 插件抽象类
 * 
 * @author yu.yao 2018年8月2日
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractPlugin<T extends AbstractPlugin<?>> extends CommonUnit<AbstractPlugin>
    implements IPlugin {
  private static final Logger logger = LoggerFactory.getLogger(AbstractPlugin.class);

  /**
   * 定义成内部类便于信息隐藏——本context存储的数据
   * 
   * @author yu.yao 2018年8月5日
   *
   */
  public static final class Context extends CompContext {

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

    private static Context getInstance() {
      return instance;
    }

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
  }

  public static final class PluginHub extends AbstractPluginHub {
    private static volatile int count;
    private static volatile PluginHub instance;

    private PluginHub() {
      synchronized (PluginHub.class) {// 防止反射破坏单例
        if (count > 0) {
          throw new RuntimeException("禁止创建两个实例");
        }
        count = 1;
      }
    }

    private static PluginHub getInstance() {
      return instance;
    }

    private synchronized static PluginHub getPluginHub() {// double check
      if (instance == null) {
        synchronized (PluginHub.class) {
          if (instance == null) {
            instance = new PluginHub();
          }
        }
      }
      return instance;
    }
  }

  static {
    PluginHub.getPluginHub();
    Context.getContext();
  }

  /**
   * 插件总线
   */
  private static final PluginHub hub = PluginHub.getInstance();

  public static final Context context = Context.getInstance();

  private AbstractPlugin customPlugin;// 自定义的插件

  public AbstractPlugin getCustomPlugin() {
    return customPlugin;
  }

  public void setCustomPlugin(AbstractPlugin customPlugin) {
    this.customPlugin = customPlugin;
  }

  public static final PluginHub getHub() {
    return hub;
  }

  @Override
  public void register() {
    if (customPlugin == null) {
      throw new RuntimeException("插件未设置！");
    } else {
      logger.info("成功注册插件：" + customPlugin.getClass().getName());
    }
    hub.addPlugin(customPlugin);
  }

  @Override
  public void initRegister() {
    if (customPlugin == null) {
      throw new RuntimeException("插件未设置！");
    }
    hub.addPlugin(customPlugin);
  }

  @Override
  public void unregister() {
    hub.removePlugin(customPlugin);
  }

  @Override
  public void consume() {
    PluginMsg msg = hub.getQueueEle();
    List<String> recievers = msg.getRecievers();
    try {
      for (String reviever : recievers) {
        Class<?> clazz = Class.forName(reviever);
        AbstractPlugin plugin = (AbstractPlugin) clazz.newInstance();
        plugin.handle();
      }
      hub.removeQueue();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      logger.error("反射失败", e);
      throw new RuntimeException();
    }
  }

  @Override
  public void deliver(PluginMsg msg) {
    hub.addQueue(msg);
  }

  /**
   * 重写equals方法,用于使用set集合 </br>
   */
  @Override
  public boolean equals(Object obj) {
    if (obj.getClass().getName().equals(this.getClass().getName())) {
      return true;
    }
    return false;
  }
}
