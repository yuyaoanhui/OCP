package com.ocp.rabbit.repository.tool.algorithm.personage;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class Relation {
  private String sourcePersonName;
  private String targetPersonName;

  private List<RelationType> relations;

  public Relation(String sourcePersonName, String targetPersonName, List<RelationType> relations) {
    this.sourcePersonName = sourcePersonName;
    this.targetPersonName = targetPersonName;
    this.relations = relations;
  }

  public Relation(String sourcePersonName, String targetPersonName, RelationType relation) {
    this.sourcePersonName = sourcePersonName;
    this.targetPersonName = targetPersonName;
    if (relations == null)
      relations = new ArrayList<>();
    relations.add(relation);
  }


  public void add(RelationType rt) {
    if (!relations.contains(rt))
      relations.add(rt);
  }

  @Override
  public String toString() {
    return targetPersonName + " 是 " + sourcePersonName + " 的 " + relations;
  }

  public String getSourcePersonName() {
    return sourcePersonName;
  }

  public void setSourcePersonName(String sourcePersonName) {
    this.sourcePersonName = sourcePersonName;
  }

  public String getTargetPersonName() {
    return targetPersonName;
  }

  public void setTargetPersonName(String targetPersonName) {
    this.targetPersonName = targetPersonName;
  }

  public List<RelationType> getRelations() {
    return relations;
  }
}
