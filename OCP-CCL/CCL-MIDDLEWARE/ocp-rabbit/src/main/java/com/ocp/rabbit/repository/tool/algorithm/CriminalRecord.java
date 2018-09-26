package com.ocp.rabbit.repository.tool.algorithm;

import java.util.List;

/**
 * 前科
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class CriminalRecord {
  private List<String> criminalRecordName; // 前科名字
  private String sentenceType;// 判罚类型：三种种的一种
  private int sentenceTime;// 被判处时间 单位：月
  private int probationTime;// 缓刑时间 单位：月
  private double fine; // 罚金
  private boolean expropriation; // 没收财产
  private int confiscateProperty; // 剥夺政治权利
  private String police;// 公安机关
  private String prisonDate;// 公安机关逮捕时间：xxxx-xx-xx
  private String court;// 判处法院
  private String judgeDate;// 标准化判决时间：xxxx-xx-xx
  private String releaseDate;// 标准化释放时间: xxxx-xx-xx
  private List<String> tmpDateList;

  public CriminalRecord() {}

  public String getCourt() {
    return court;
  }

  public void setCourt(String court) {
    this.court = court;
  }

  public List<String> getCriminalRecordName() {
    return criminalRecordName;
  }

  public void setCriminalRecordName(List<String> criminalRecordName) {
    this.criminalRecordName = criminalRecordName;
  }

  public String getPolice() {
    return police;
  }

  public void setPolice(String police) {
    this.police = police;
  }

  public String getPrisonDate() {
    return prisonDate;
  }

  public void setPrisonDate(String prisonDate) {
    this.prisonDate = prisonDate;
  }

  public String getJudgeDate() {
    return judgeDate;
  }

  public void setJudgeDate(String judgeDate) {
    this.judgeDate = judgeDate;
  }

  public String getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(String releaseDate) {
    this.releaseDate = releaseDate;
  }

  public String getSentenceType() {
    return sentenceType;
  }

  public void setSentenceType(String sentenceType) {
    this.sentenceType = sentenceType;
  }

  public int getSentenceTime() {
    return sentenceTime;
  }

  public void setSentenceTime(int sentenceTime) {
    this.sentenceTime = sentenceTime;
  }

  public int getProbationTime() {
    return probationTime;
  }

  public void setProbationTime(int probationTime) {
    this.probationTime = probationTime;
  }

  public double getFine() {
    return fine;
  }

  public void setFine(double fine) {
    this.fine = fine;
  }

  public List<String> getTmpDate() {
    return tmpDateList;
  }

  public void setTmpDate(List<String> tmpDateList) {
    this.tmpDateList = tmpDateList;
  }

  public boolean getExpropriation() {
    return expropriation;
  }

  public int getConfiscateProperty() {
    return confiscateProperty;
  }

  public void setExpropriation(boolean expropriation) {
    this.expropriation = expropriation;
  }

  public void setConfiscateProperty(int confiscateProperty) {
    this.confiscateProperty = confiscateProperty;
  }

  public CriminalRecord(List<String> criminalRecordName, String police, String court,
      String prisonDate, String judgeDate, String releaseDate, String sentenceType,
      int sentenceTime, int probationTime, double fine) {
    super();
    this.criminalRecordName = criminalRecordName;
    this.police = police;
    this.court = court;
    this.prisonDate = prisonDate;
    this.judgeDate = judgeDate;
    this.releaseDate = releaseDate;
    this.sentenceType = sentenceType;
    this.sentenceTime = sentenceTime;
    this.probationTime = probationTime;
    this.fine = fine;
  }

  @Override
  public String toString() {
    return "CriminalRecord [criminalRecordName=" + criminalRecordName + ", sentenceType="
        + sentenceType + ", sentenceTime=" + sentenceTime + ", probationTime=" + probationTime
        + ", fine=" + fine + ", expropriation=" + expropriation + ", confiscateProperty="
        + confiscateProperty + ", police=" + police + ", court=" + court + ", prisonDate="
        + prisonDate + ", judgeDate=" + judgeDate + ", releaseDate=" + releaseDate + "]";
  }
}

