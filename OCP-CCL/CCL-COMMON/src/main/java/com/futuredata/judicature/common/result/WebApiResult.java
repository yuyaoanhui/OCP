package com.futuredata.judicature.common.result;

import java.io.Serializable;

import com.futuredata.judicature.common.result.code.ResultCode;

/**
 * 
 * @author yu.yao
 *
 * @param <T>
 */
public class WebApiResult<T> implements Serializable {

  private static final long serialVersionUID = 1L;

  private int code;
  private String msg;
  private T data;

  /**
   * 初始化默认返回值
   */
  public WebApiResult() {
    this.code = ResultCode.SUCCESS.getCode();
    this.msg = ResultCode.SUCCESS.getMsg();
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public void setResultCode(ResultCode rc) {
    this.code = rc.getCode();
    this.msg = rc.getMsg();
  }
}
