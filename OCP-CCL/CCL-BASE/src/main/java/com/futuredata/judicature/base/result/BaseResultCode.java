package com.futuredata.judicature.base.result;


public class BaseResultCode {

  public static final BaseResultCode SYS_SUCCESS = new BaseResultCode(1000, "操作成功");

  public static final BaseResultCode SYS_ERROR = new BaseResultCode(1099, "系统发生未知错误");

  /**
   * 错误编码
   */
  private int code;

  /**
   * 错误信息
   */
  private String msg;

  BaseResultCode(int code, String msg) throws IllegalArgumentException {
    if (code > 1099 || code < 1000) {
      throw new IllegalArgumentException("错误码大小越界");
    }
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

}
