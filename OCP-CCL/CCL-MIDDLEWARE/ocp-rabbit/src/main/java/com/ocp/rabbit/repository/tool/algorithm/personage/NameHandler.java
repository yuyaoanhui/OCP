package com.ocp.rabbit.repository.tool.algorithm.personage;

import com.ocp.rabbit.repository.algorithm.NameTypeRecognizer;

/**
 * 
 * @author yu.yao 2018年8月15日
 *
 */
public class NameHandler {
  public static NameWrapper getNameType(String raw) {
    return NameTypeRecognizer.getNameWrapper(raw);
  }

  /**
   * 根据名字类型，以及自定义规则，判断名字是否是合法名字
   * 
   * @param raw
   * @param nameType
   * @return
   */
  public static boolean validName(String raw, int nameType) {
    if (1 == nameType) {

    } else if (2 == nameType) {

    } else {

    }
    return true;
  }
}
