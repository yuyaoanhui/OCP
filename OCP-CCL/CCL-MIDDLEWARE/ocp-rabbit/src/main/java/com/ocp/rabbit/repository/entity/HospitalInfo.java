package com.ocp.rabbit.repository.entity;

public class HospitalInfo {
  private String name;
  private String alias;
  private String address;
  private String phoneNum;
  private String level;
  private String departments;
  private String operation;
  private String fax;
  private String mail;
  private String website;

  public String getName() {
    return name;
  }

  public String getAlias() {
    return alias;
  }

  public String getAddress() {
    return address;
  }

  public String getPhoneNum() {
    return phoneNum;
  }

  public String getLevel() {
    return level;
  }

  public String getDepartments() {
    return departments;
  }

  public String getOperation() {
    return operation;
  }

  public String getFax() {
    return fax;
  }

  public String getMail() {
    return mail;
  }

  public String getWebsite() {
    return website;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public void setPhoneNum(String phoneNum) {
    this.phoneNum = phoneNum;
  }

  public void setLevel(String level) {
    this.level = level;
  }

  public void setDepartments(String departments) {
    this.departments = departments;
  }

  public void setOperation(String operation) {
    this.operation = operation;
  }

  public void setFax(String fax) {
    this.fax = fax;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

}
