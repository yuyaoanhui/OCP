package com.ocp.common.result;

import java.io.Serializable;
import com.ocp.base.result.ResultCode;

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
    this.code = CommonResultCode.SYS_SUCCESS.getCode();
    this.msg = CommonResultCode.SYS_SUCCESS.getMsg();
  }

  /**
   * 无数据返回码
   * 
   * @param rc
   */
  public WebApiResult(ResultCode rc) {
    this.code = rc.getCode();
    this.msg = rc.getMsg();
  }

  /**
   * 有数据返回码
   * 
   * @param rc
   * @param data
   */
  public WebApiResult(ResultCode rc, T data) {
    this.code = rc.getCode();
    this.msg = rc.getMsg();
    this.data = data;
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
