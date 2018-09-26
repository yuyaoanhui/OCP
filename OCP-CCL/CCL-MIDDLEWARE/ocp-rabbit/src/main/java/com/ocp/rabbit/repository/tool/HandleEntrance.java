package com.ocp.rabbit.repository.tool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ocp.rabbit.plugin.internal.RuleFunction;

/**
 * 信息点处理统一入口(对应js脚本文件)
 * 
 * @author yu.yao 2018年8月6日
 *
 */
public class HandleEntrance {
  private static List<String> methodNames = new ArrayList<String>();
  // 存储扩展插件中的自定义方法
  private static Map<Class<?>, List<String>> extendNameMap = new HashMap<Class<?>, List<String>>();

  public static Map<Class<?>, List<String>> getExtendNameMap() {
    return extendNameMap;
  }

  static {
    Method[] methods = RuleFunction.class.getMethods();
    for (Method method : methods) {
      methodNames.add(method.getName());
    }
  }

  /**
   * 信息点抽取逻辑处理统一入口函数
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Class<?> entrance(String methodName) {
    if (methodNames.contains(methodName)) {
      return RuleFunction.class;
    } else {
      for (Class<?> clazz : extendNameMap.keySet()) {
        if (extendNameMap.get(clazz).contains(methodName)) {
          return clazz;
        }
      }
    }
    return null;
  }
}
