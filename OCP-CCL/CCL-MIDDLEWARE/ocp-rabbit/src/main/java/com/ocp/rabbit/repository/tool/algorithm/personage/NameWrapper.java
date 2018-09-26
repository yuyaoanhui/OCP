package com.ocp.rabbit.repository.tool.algorithm.personage;

import java.util.List;

/**
 * 人物名称的包装类
 * 
 * @author yu.yao 2018年8月15日
 *
 */
public class NameWrapper {
  private int nameType;// 人名的类别：人、机构
  private String nameCleaned;// 人名,与namesSplit不会同时存在
  private String namePinyin;// 名字的拼音
  private List<String> namesSplit;// 待匹配字符串中的多个人名

  public NameWrapper() {}

  public NameWrapper(int nameType) {
    this.nameType = nameType;
  }

  public NameWrapper(int nameType, String nameCleaned) {
    this.nameType = nameType;
    this.nameCleaned = nameCleaned;
  }

  public NameWrapper(int nameType, List<String> namesSplit) {
    this.nameType = nameType;
    this.namesSplit = namesSplit;
  }

  public NameWrapper(int nameType, String nameCleaned, List<String> namesSplit) {
    this.nameType = nameType;
    this.nameCleaned = nameCleaned;
    this.namesSplit = namesSplit;
  }

  public String getNamePinyin() {
    return namePinyin;
  }

  public void setNamePinyin(String namePinyin) {
    this.namePinyin = namePinyin;
  }

  public int getNameType() {
    return nameType;
  }

  public void setNameType(int nameType) {
    this.nameType = nameType;
  }

  public String getNameCleaned() {
    return nameCleaned;
  }

  public void setNameCleaned(String nameCleaned) {
    this.nameCleaned = nameCleaned;
  }

  public List<String> getNamesSplit() {
    return namesSplit;
  }

  public void setNamesSplit(List<String> namesSplit) {
    this.namesSplit = namesSplit;
  }
}
