package com.ocp.rabbit.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;

import com.ocp.rabbit.plugin.entity.PluginMsg;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;

/**
 * 插件总线类，用于管理插件的注册、注销、消息传递、数据传输
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public abstract class AbstractPluginHub {

  /**
   * 无界阻塞优先级队列(并发队列)，用于存储接收到的总线消息(包含数据)
   */
  private static Queue<PluginMsg> msgQueue = new PriorityBlockingQueue<PluginMsg>();

  /**
   * 已注册的所有插件
   */
  private static Set<IPlugin> pulginSet = Collections.synchronizedSet(new HashSet<IPlugin>());

  public static IPlugin getPluginByType(Class<?> clazz, Context context) {
    try {
      for (IPlugin plugin : pulginSet) {
        if (plugin.getClass().equals(clazz)) {
          Constructor<?> c0 = clazz.getDeclaredConstructor(Context.class);
          return (IPlugin) c0.newInstance(context);
        }
      }
    } catch (NoSuchMethodException | SecurityException | InstantiationException
        | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 获取已注册的所有插件——不可修改
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Set<IPlugin> getPluginSet() {
    
    return new CopyOnWriteArraySet<IPlugin>(pulginSet);
  }

  /**
   * 传播消息(指令)
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void notice(PluginMsg msg) {
    setMailer(msg);
    for (IPlugin plugin : pulginSet) {
      plugin.deliver(msg);
    }
  }

  /**
   * 向队列中加入一条消息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  void addQueue(PluginMsg msg) {
    msgQueue.add(msg);
  }

  /**
   * 从队列中移除一条消息
   * 
   * @author yu.yao
   * @return
   */
  PluginMsg removeQueue() {
    return msgQueue.poll();
  }

  /**
   * 获取队列头的消息
   * 
   * @author yu.yao
   * @return
   */
  PluginMsg getQueueEle() {
    return msgQueue.peek();
  }

  /**
   * 向集合中加入一条数据
   * 
   * @author yu.yao
   * @return
   */
  synchronized void addPlugin(IPlugin e) {
    pulginSet.add(e);
  }

  /**
   * 从集合中移除一条数据
   * 
   * @author yu.yao
   * @return
   */
  synchronized void removePlugin(IPlugin e) {
    pulginSet.remove(e);
  }

  /**
   * 从集合中移除所有数据
   * 
   * @author yu.yao
   * @return
   */
  public synchronized static void clear() {
    pulginSet.clear();
  }

  /**
   * 根据指令内容设置收信人，并封装信封
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private void setMailer(PluginMsg msg) {
    /* PluginCmd cmd = */msg.getCommand();
    List<String> recievers = new ArrayList<String>();
    // TODO 根据消息内容设置recievers
    msg.setRecievers(recievers);
  }

}
