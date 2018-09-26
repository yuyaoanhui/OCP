package com.ocp.rabbit.repository.tool.algorithm.number;

import java.util.HashMap;
import java.util.Map;

/**
 * 罗马数字转换为阿拉伯数字
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class Roma2Ara {
  private static Map<String, Integer> romaNumeric = null;

  static {
    romaNumeric = new HashMap<String, Integer>();

    romaNumeric.put("I", 1);
    romaNumeric.put("II", 2);
    romaNumeric.put("III", 3);
    romaNumeric.put("IV", 4);
    romaNumeric.put("V", 5);
    romaNumeric.put("VI", 6);
    romaNumeric.put("VII", 7);
    romaNumeric.put("VIII", 8);
    romaNumeric.put("IX", 9);
    romaNumeric.put("X", 10);

  }


  /**
   * check the given char is roma numeric or not. <br />
   *
   * @return value yes and -1 for not.
   */
  public static int isRomaNumeric(String value) {
    Integer i = romaNumeric.get(value);
    if (i == null)
      return -1;
    return i.intValue();
  }
}
