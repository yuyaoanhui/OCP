package com.ocp.rabbit.repository.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 案件类型
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public enum CaseType {
  CRIMINAL_CASE("刑事"), CIVIL_CASE("民事"), ADMIN_CASE("行政"), CRIMINAL_CIVIL_CASE(
      "刑事附带民事"), COMPENSATION("赔偿"), ENFORCEMENT("执行"), ORAL_CASE("笔录"), DEFAULT("其他");


  private String name;

  private CaseType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  @JsonValue
  public String getName() {
    return name;
  }

  public boolean equals(String str) {
    if (this.toString().equals(str)) {
      return true;
    } else {
      return false;
    }
  }
}
