package com.futuredata.judicature.common.result.code;

/**
 * 
 * @author yu.yao
 *
 */
public class ResultCode {

  public static final ResultCode SUCCESS = new ResultCode(0000, "操作成功");

  public static final ResultCode ERROR = new ResultCode(9999, "系统发生未知错误");

  /**
   * 错误编码
   */
  private int code;

  /**
   * 错误信息
   */
  private String msg;

  ResultCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
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

}
