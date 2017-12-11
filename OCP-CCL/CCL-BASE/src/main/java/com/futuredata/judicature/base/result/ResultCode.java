package com.futuredata.judicature.base.result;

import com.futuredata.judicature.sdk.utils.StringUtils;

/**
 * 定义返回码类
 * 
 * @author yu.yao
 *
 */
public class ResultCode {

  /**
   * 返回信息
   */
  private String msg;

  /**
   * 返回编码
   */
  private int code;

  /**
   * 避免反射方式构造非法返回码
   */
  protected ResultCode(int code, String msg) {
    if (code == 0) {
      throw new IllegalArgumentException("返回码编码不能为0.");
    }
    if (StringUtils.isEmpty(msg)) {
      throw new IllegalArgumentException("返回码描述信息不能为空.");
    }
    this.code = code;
    this.msg = msg;
  }

  /**
   * 避免反射方式构造非法返回码
   */
  @SuppressWarnings("unused")
  private ResultCode() {
    throw new IllegalArgumentException("缺少初始化参数");
  }

  /**
   * 
   * @return
   */
  public String getMsg() {
    return msg;
  }

  /**
   * 
   * @return
   */
  public int getCode() {
    return code;
  }

  /**
   * 重写equals方法
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ResultCode) {
      ResultCode ele = (ResultCode) obj;
      if (ele.getCode() == this.code) {
        return true;
      }
      return false;
    } else
      throw new IllegalArgumentException("参数类型不合法.");
  }

}
