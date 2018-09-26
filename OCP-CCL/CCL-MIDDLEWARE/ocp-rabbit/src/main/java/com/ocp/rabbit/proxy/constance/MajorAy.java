package com.ocp.rabbit.proxy.constance;

import java.util.ArrayList;
import java.util.List;

/**
 * 案由大类
 * 
 * @author yu.yao 2018年7月31日
 *
 */
public enum MajorAy {
  base, // 基础信息点，虚拟案由大类
  criminal, // 刑事
  civil, // 民事
  administration, // 行政
  other;// 其他

  public static List<String> getMajorList() {
    List<String> list = new ArrayList<String>();
    for (MajorAy mjor : MajorAy.values()) {
      list.add(mjor.name());
    }
    return list;
  }
}
