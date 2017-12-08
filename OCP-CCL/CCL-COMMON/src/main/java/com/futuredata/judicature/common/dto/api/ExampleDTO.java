package com.futuredata.judicature.common.dto.api;

import java.io.Serializable;

/**
 * @author yu.yao
 *
 */
public class ExampleDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * id
   */
  private String id;

  /**
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * @param id
   */
  public void setVersionId(String id) {
    this.id = id;
  }

}
