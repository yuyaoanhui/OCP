package com.ocp.rabbit.repository.tool.algorithm.personage;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public enum RelationType {
  ATTORNEY("律师"), ENTRUSTED("委托代理人"), ASSIGNED("法定代理人"), REPRESENTATIVE("法定代表人"), PARENT(
      "父母"), FATHER("父亲"), MOTHER("母亲"), SON("儿子"), DAUGHTER(
          "女儿"), BROTHER("兄弟"), SISTER("姐妹"), SIBILING("兄弟姐妹"), UNCLE("叔伯"), DEFAULT_TYPE("默认");

  private String relation;

  private RelationType(String rel) {
    this.relation = rel;
  }

  @Override
  public String toString() {
    return relation;
  }

  @JsonValue
  public String getRelation() {
    return relation;
  }

  public static RelationType getRelationType(String type) {
    switch (type) {
      case "ATTORNEY":
        return RelationType.ATTORNEY;
      case "ENTRUSTED":
        return RelationType.ENTRUSTED;
      case "ASSIGNED":
        return RelationType.ASSIGNED;
      case "REPRESENTATIVE":
        return RelationType.REPRESENTATIVE;
      default:
        return null;
    }
  }
}
