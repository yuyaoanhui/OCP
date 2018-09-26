package com.ocp.rabbit.repository.tool.algorithm.law;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class LawInfo {
  public String strLawName;
  public String strLawName2; // 去除、号的法律名称，文书中引用的法律经常去除、号
  public String strRowKey;
  public java.sql.Date exeDate;

  /**
   * @return the strLawName
   */
  public String getStrLawName() {
    return strLawName;
  }

  /**
   * @param strLawName the strLawName to set
   */
  public void setStrLawName(String strLawName) {
    this.strLawName = strLawName;
  }

  /**
   * @return the strLawName2
   */
  public String getStrLawName2() {
    return strLawName2;
  }

  /**
   * @param strLawName2 the strLawName2 to set
   */
  public void setStrLawName2(String strLawName2) {
    this.strLawName2 = strLawName2;
  }

  /**
   * @return the strRowKey
   */
  public String getStrRowKey() {
    return strRowKey;
  }

  /**
   * @param strRowKey the strRowKey to set
   */
  public void setStrRowKey(String strRowKey) {
    this.strRowKey = strRowKey;
  }

  /**
   * @return the exeDate
   */
  public java.sql.Date getExeDate() {
    return exeDate;
  }

  /**
   * @param exeDate the exeDate to set
   */
  public void setExeDate(java.sql.Date exeDate) {
    this.exeDate = exeDate;
  }

  public LawInfo(String lawname, String lawname2, String rowkey, java.sql.Date date) {
    strLawName = lawname;
    strLawName2 = lawname2;
    strRowKey = rowkey;
    exeDate = date;
  }
}
