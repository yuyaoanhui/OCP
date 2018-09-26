package com.ocp.rabbit.repository.tool.algorithm.address;

import java.util.List;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class AreaStructure {
  String code;
  String name;
  String upperLevelCode;
  String level;
  String ifLastLevel;
  AreaStructure parent;
  List<AreaStructure> children;

  public String toString() {
    return name;
  }

  public AreaStructure(String code, String upperLevelCode, String level, String ifLastLevel) {
    this.code = code;
    this.upperLevelCode = upperLevelCode;
    this.level = level;
    this.ifLastLevel = ifLastLevel;
  }

  public AreaStructure(String code, String name, String upperLevelCode, String level,
      String ifLastLevel) {
    this.code = code;
    this.name = name;
    this.upperLevelCode = upperLevelCode;
    this.level = level;
    this.ifLastLevel = ifLastLevel;
  }

  // depth first
  public static AreaStructure findNodeByCode(AreaStructure root, AreaStructure search) {
    if (root.code.equals(search.code))
      return root;
    else {
      for (AreaStructure as : root.children) {
        AreaStructure temp = findNodeByCode(as, search);
        if (temp != null)
          return temp;
      }
      return null;
    }
  }

  // width first
  public static AreaStructure findNodeByCodeWidthFirst(AreaStructure root, AreaStructure search) {
    if (root.code.equals(search.code))
      return root;
    else {
      for (AreaStructure as : root.children) {
        if (as.code.equals(search.code))
          return as;
      }
      // Not found
    }
    return null;
  }
}
