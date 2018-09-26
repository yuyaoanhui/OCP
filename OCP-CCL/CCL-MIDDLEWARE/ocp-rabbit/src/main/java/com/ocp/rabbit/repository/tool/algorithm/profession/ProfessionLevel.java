package com.ocp.rabbit.repository.tool.algorithm.profession;

/**
 * 专业水平
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class ProfessionLevel {

  public static ProfessionLevel otherLevel = new ProfessionLevel("不便分类", "0");
  public static ProfessionLevel otherLevelTDH = new ProfessionLevel("不便分类的其他劳动者", "255");
  public String name;
  public String code;

  public ProfessionLevel(String name, String code) {
    this.name = name;
    this.code = code;
  }

  public String toString() {
    return name;
  }

  @Override
  public int hashCode() {
    return (code + name).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this.hashCode() == obj.hashCode())
      return true;
    return false;
  }
}
