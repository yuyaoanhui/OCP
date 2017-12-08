package com.futuredata.judicature.common.result;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author yu.yao
 *
 * @param <T>
 */
public class PageResult<T> implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * 返回结果为一个列表
   */
  private List<T> list;
  /**
   * 分页结果总数
   */
  private Integer total;

  public List<T> getList() {
    return list;
  }

  public void setList(List<T> list) {
    this.list = list;
  }

  public Integer getTotal() {
    return total;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }
}
