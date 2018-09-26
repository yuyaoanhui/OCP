package com.ocp.rabbit.repository.constant;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @author yu.yao 2018年8月20日
 *
 */
public enum JudgementType {
  JUDGEMENT("判决书"), ARBITRAL("裁定书"), MEDIATION("调解书"), NOTICE("通知书"), DESICION("决定书"), DECREE(
      "令"), ORAL_CASE("笔录"), DEFAULT_TYPE("未知");

  private String name;

  private JudgementType(String name) {
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
