package com.ocp.rabbit.repository.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @author yu.yao 2018年8月20日
 *
 */
public enum CaseHierarchy {

  FIRST_TRIAL("一审"), SECOND_TRIAL("二审"), RE_TRIAL("再审"), DEFAULT("其他");

  private String name;

  private CaseHierarchy(String name) {
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
}

