package com.ocp.common.object.transfer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 封装查询请求
 * 
 * @author yu.yao
 */
public class QueryDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * 以like方式查询{字段名：值}
   */
  private Map<String, String> like;

  /**
   * 以==方式查询{字段名：值}
   */
  private Map<String, String> equals;

  /**
   * 以!=方式查询{字段名：值}
   */
  private Map<String, String> notEquals;

  /**
   * 以***开始的方式查询{字段名：值}
   */
  private Map<String, String> startWith;

  /**
   * 以***结束方式查询{字段名：值}
   */
  private Map<String, String> endWith;

  /**
   * 以>=的方式查询{字段名：值}
   */
  private Map<String, Object> greaterThanOrEquals;

  /**
   * 以>的方式查询{字段名：值}
   */
  private Map<String, Object> greaterThan;

  /**
   * 以<=的方式查询{字段名：值}
   */
  private Map<String, Object> lessThanOrEquals;

  /**
   * 以<的方式查询{字段名：值}
   */
  private Map<String, Object> lessThan;

  /**
   * 以in方式查询{字段名：值集}
   */
  private Map<String, Object> inSet;

  /**
   * 以not in方式查询{字段名：值集}
   */
  private Map<String, Object> notInSet;

  /**
   * 以is null方式查询
   */
  private List<String> isNull;

  /**
   * 以is not null方式查询
   */
  private List<String> isNotNull;

  /**
   * 按**字段正序(** asc)或逆序(** desc)
   */
  private String orderBy;

  /**
   * 开始页码
   */
  private Integer pageNo = 1;

  /**
   * 每页多少
   */
  private Integer pageSize = 10;

  public Map<String, String> getLike() {
    return like;
  }

  public void setLike(Map<String, String> like) {
    this.like = like;
  }

  public Map<String, String> getEquals() {
    return equals;
  }

  public void setEquals(Map<String, String> equals) {
    this.equals = equals;
  }

  public Map<String, String> getNotEquals() {
    return notEquals;
  }

  public void setNotEquals(Map<String, String> notEquals) {
    this.notEquals = notEquals;
  }

  public Map<String, String> getStartWith() {
    return startWith;
  }

  public void setStartWith(Map<String, String> startWith) {
    this.startWith = startWith;
  }

  public Map<String, String> getEndWith() {
    return endWith;
  }

  public void setEndWith(Map<String, String> endWith) {
    this.endWith = endWith;
  }

  public Map<String, Object> getGreaterThanOrEquals() {
    return greaterThanOrEquals;
  }

  public void setGreaterThanOrEquals(Map<String, Object> greaterThanOrEquals) {
    this.greaterThanOrEquals = greaterThanOrEquals;
  }

  public Map<String, Object> getGreaterThan() {
    return greaterThan;
  }

  public void setGreaterThan(Map<String, Object> greaterThan) {
    this.greaterThan = greaterThan;
  }

  public Map<String, Object> getLessThanOrEquals() {
    return lessThanOrEquals;
  }

  public void setLessThanOrEquals(Map<String, Object> lessThanOrEquals) {
    this.lessThanOrEquals = lessThanOrEquals;
  }

  public Map<String, Object> getLessThan() {
    return lessThan;
  }

  public void setLessThan(Map<String, Object> lessThan) {
    this.lessThan = lessThan;
  }

  public Map<String, Object> getInSet() {
    return inSet;
  }

  public void setInSet(Map<String, Object> inSet) {
    this.inSet = inSet;
  }

  public Map<String, Object> getNotInSet() {
    return notInSet;
  }

  public void setNotInSet(Map<String, Object> notInSet) {
    this.notInSet = notInSet;
  }

  public List<String> getIsNull() {
    return isNull;
  }

  public void setIsNull(List<String> isNull) {
    this.isNull = isNull;
  }

  public List<String> getIsNotNull() {
    return isNotNull;
  }

  public void setIsNotNull(List<String> isNotNull) {
    this.isNotNull = isNotNull;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

}
