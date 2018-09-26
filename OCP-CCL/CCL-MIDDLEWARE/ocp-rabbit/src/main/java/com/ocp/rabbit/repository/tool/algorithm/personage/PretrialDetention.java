package com.ocp.rabbit.repository.tool.algorithm.personage;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class PretrialDetention {

  // 决定机关
  private String departmentOpinion;
  // 执行机关
  private String departmentEnforcement;
  /**
   * 被抓获时间
   */
  private String dateCapture;
  /**
   * 决定刑事拘留时间
   */
  private String dateDetentionOpinion;
  /**
   * 执行刑事拘留时间
   */
  private String dateDetentionEnforcement;
  /**
   * 决定逮捕时间
   */
  private String dateArrestOpinion;
  /**
   * 执行逮捕时间
   */
  private String dateArrestEnforcement;
  /**
   * 检察院决定取保候审时间
   */
  private String dateBailProcuratorate;
  /**
   * 法院决定取保候审时间
   */
  private String dateBailCourt;

  /**
   * 公安机关决定取保候审时间
   */
  private String dateBailPolice;


  /**
   * 被抓获、刑事拘留、逮捕的最小时间，即权利机关采取措施的时间，用被告人描述段落里相关时间集合里，最小时间指代
   */
  private String dateFirstCustody;

  /**
   * 监视居住时间
   */
  private String dateResidenceMonitor;
  // 拘传时间
  private String dateResidenceRecall;

  public String getDepartmentOpinion() {
    return departmentOpinion;
  }

  public void setDepartmentOpinion(String departmentOpinion) {
    this.departmentOpinion = departmentOpinion;
  }

  public String getDepartmentEnforcement() {
    return departmentEnforcement;
  }

  public void setDepartmentEnforcement(String departmentEnforcement) {
    this.departmentEnforcement = departmentEnforcement;
  }

  public String getDateFirstCustody() {
    return dateFirstCustody;
  }

  public void setDateFirstCustody(String dateFirstCustody) {
    this.dateFirstCustody = dateFirstCustody;
  }

  public String getDateCapture() {
    return dateCapture;
  }

  public void setDateCapture(String dateCapture) {
    this.dateCapture = dateCapture;
  }

  public String getDateDetentionOpinion() {
    return dateDetentionOpinion;
  }

  public void setDateDetentionOpinion(String dateDetentionOpinion) {
    this.dateDetentionOpinion = dateDetentionOpinion;
  }

  public String getDateDetentionEnforcement() {
    return dateDetentionEnforcement;
  }

  public void setDateDetentionEnforcement(String dateDetentionEnforcement) {
    this.dateDetentionEnforcement = dateDetentionEnforcement;
  }

  public String getDateArrestOpinion() {
    return dateArrestOpinion;
  }

  public void setDateArrestOpinion(String dateArrestOpinion) {
    this.dateArrestOpinion = dateArrestOpinion;
  }

  public String getDateArrestEnforcement() {
    return dateArrestEnforcement;
  }

  public void setDateArrestEnforcement(String dateArrestEnforcement) {
    this.dateArrestEnforcement = dateArrestEnforcement;
  }

  public String getDateBailProcuratorate() {
    return dateBailProcuratorate;
  }

  public void setDateBailProcuratorate(String dateBailProcuratorate) {
    this.dateBailProcuratorate = dateBailProcuratorate;
  }

  public String getDateBailCourt() {
    return dateBailCourt;
  }

  public void setDateBailCourt(String dateBailCourt) {
    this.dateBailCourt = dateBailCourt;
  }

  public String getDateResidenceMonitor() {
    return dateResidenceMonitor;
  }

  public void setDateResidenceMonitor(String dateResidenceMonitor) {
    this.dateResidenceMonitor = dateResidenceMonitor;
  }

  public String getDateBailPolice() {
    return dateBailPolice;
  }

  public void setDateBailPolice(String dateBailPolice) {
    this.dateBailPolice = dateBailPolice;
  }

  public String getDateResidenceRecall() {
    return dateResidenceRecall;
  }

  public void setDateResidenceRecall(String dateResidenceRecall) {
    this.dateResidenceRecall = dateResidenceRecall;
  }

  @Override
  public String toString() {
    return "PretrialDetention{" + "dateCapture='" + dateCapture + '\'' + ", dateDetentionOpinion='"
        + dateDetentionOpinion + '\'' + ", dateDetentionEnforcement='" + dateDetentionEnforcement
        + '\'' + ", dateArrestOpinion='" + dateArrestOpinion + '\'' + ", dateArrestEnforcement='"
        + dateArrestEnforcement + '\'' + ", dateBailProcuratorate='" + dateBailProcuratorate + '\''
        + ", dateBailCourt='" + dateBailCourt + '\'' + ", dateBailPolice='" + dateBailPolice + '\''
        +
        // ", dateFirstCustody='" + dateFirstCustody + '\'' +
        ", dateResidenceMonitor='" + dateResidenceMonitor + '\'' + ", dateResidenceRecall='"
        + dateResidenceRecall + '\'' + '}';
  }
}
