package com.ocp.rabbit.repository.tool.algorithm;

import java.util.List;

/**
 * 判罚信息
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class CriminalJudgement {
  private int probationTime;// 缓刑期限,默认单位月
  private int judgmentTime;// 刑期,默认单位月
  private String judgmentType;// 判罚类别
  private boolean combinedResults;// 是否数罪并罚
  private List<String> judgmentName; // 主判罚类型
  private int judgmentNameSize;// 罪名个数
  private double fine;// 罚金，默认单位元
  private boolean expropriation;// 是否没收个人财产
  private int confiscateProperty;// 剥夺政治权利期限,默认单位月

  public CriminalJudgement() {}

  public CriminalJudgement(int probationTime, int judgementTime, String judgementType,
      boolean combinedResults, List<String> judgementName, int judgementNameSize, double fine) {
    this.probationTime = probationTime;
    this.judgmentTime = judgementTime;
    this.judgmentType = judgementType;
    this.combinedResults = combinedResults;
    this.judgmentName = judgementName;
    this.judgmentNameSize = judgementNameSize;
    this.fine = fine;
  }

  public int getProbationTime() {
    return probationTime;
  }

  public int getJudgementTime() {
    return judgmentTime;
  }

  public String getJudgementType() {
    return judgmentType;
  }

  public boolean getCombinedResults() {
    return combinedResults;
  }

  public List<String> getJudgementName() {
    return judgmentName;
  }

  public int getJudgementNameSize() {
    return judgmentNameSize;
  }

  public double getFine() {
    return fine;
  }

  public boolean getExpropriation() {
    return expropriation;
  }

  public int getConfiscateProperty() {
    return confiscateProperty;
  }

  public void setProbationTime(int probationTime) {
    this.probationTime = probationTime;
  }

  public void setJudgementTime(int judgementTime) {
    this.judgmentTime = judgementTime;
  }

  public void setJudgementType(String judgementType) {
    this.judgmentType = judgementType;
  }

  public void setCombinedResults(boolean combinedResults) {
    this.combinedResults = combinedResults;
  }

  public void setJudgementName(List<String> judgementName) {
    this.judgmentName = judgementName;
  }

  public void setJudgementNameSize(int judgementNameSize) {
    this.judgmentNameSize = judgementNameSize;
  }

  public void setFine(double fine) {
    this.fine = fine;
  }

  public void setExpropriation(boolean expropriation) {
    this.expropriation = expropriation;
  }

  public void setConfiscateProperty(int confiscateProperty) {
    this.confiscateProperty = confiscateProperty;
  }

  @Override
  public String toString() {
    return "CriminalJudgment [judgmentName=" + judgmentName + ", probationTime=" + probationTime
        + ", judgmentTime=" + judgmentTime + ", judgmentType=" + judgmentType + ", combinedResults="
        + combinedResults + ", judgmentNameSize=" + judgmentNameSize + ", fine=" + fine
        + ", expropriation=" + expropriation + ", confiscateProperty=" + confiscateProperty + "]";
  }
}
