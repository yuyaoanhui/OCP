package com.ocp.rabbit.middleware.orm;

import java.io.Serializable;

/**
 * 封装查询请求
 * 
 * @author yu.yao 2018年9月2日
 */
public class QueryDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  private int pageNum;
  private int pageSize;
  /**
   * 组织机构(court/procuratorate)
   */
  private String org;

  /**
   * 文书类型
   *
   */
  private String doctype;

  /**
   * 案由
   */
  private String ay;

  /**
   * 案由大类
   *
   */
  private String majoray;

  /**
   * 信息点变量名
   */
  private String variable;

  /**
   * 信息点名
   */
  private String name;

  /**
   * 版本
   */
  private String version;

  /**
   * 是否可写
   */
  private String w;

  public String getOrg() {
    return org;
  }

  public void setOrg(String org) {
    this.org = org;
  }

  public String getDoctype() {
    return doctype;
  }

  public void setDoctype(String doctype) {
    this.doctype = doctype;
  }

  public String getAy() {
    return ay;
  }

  public void setAy(String ay) {
    this.ay = ay;
  }

  public String getMajoray() {
    return majoray;
  }

  public void setMajoray(String majoray) {
    this.majoray = majoray;
  }

  public String getVariable() {
    return variable;
  }

  public void setVariable(String variable) {
    this.variable = variable;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getW() {
    return w;
  }

  public void setW(String w) {
    this.w = w;
  }

  public int getPageNum() {
    return pageNum;
  }

  public void setPageNum(int pageNum) {
    this.pageNum = pageNum;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

}
