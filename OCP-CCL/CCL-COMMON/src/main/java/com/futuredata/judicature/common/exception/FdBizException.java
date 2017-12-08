package com.futuredata.judicature.common.exception;

/**
 * 自定义异常类
 * 
 * @author yu.yao
 *
 */
public class FdBizException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * 请求唯一标识
   */
  private String id;

  /**
   * 异常信息编码
   */
  private int code;

  /**
   * 异常信息
   */
  private String msg;

  /**
   * 异常对象列表
   */
  private Object[] args;

  /**
   * 
   * @param code
   * @param msg
   */
  public FdBizException(int code, String msg) {
    this(code, msg, null);
  }

  /**
   * 
   * @param code
   * @param msg
   * @param args
   */
  public FdBizException(int code, String msg, Object[] args) {
    super(msg);
    this.code = code;
    this.msg = msg;
    this.args = args;
  }

  /**
   * 
   * @param code
   * @param msg
   * @param agrs
   * @param t
   */
  public FdBizException(int code, String msg, Object[] agrs, Throwable t) {
    this(null, code, msg, agrs, t);
  }

  /**
   * 
   * @param id
   * @param code
   * @param msg
   * @param args
   * @param t
   */
  public FdBizException(String id, int code, String msg, Object[] args, Throwable t) {
    super(msg, t);
    this.id = id;
    this.code = code;
    this.msg = msg;
    this.args = args;
  }

  /**
   * 
   * @return
   */
  public String getId() {
    return id;
  }

  /**
   * 
   * @param id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * 
   * @return
   */
  public int getCode() {
    return code;
  }

  /**
   * 
   * @param code
   */
  public void setCode(int code) {
    this.code = code;
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
   * @param msg
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * 
   * @return
   */
  public Object[] getArgs() {
    return args;
  }

  /**
   * 
   * @param args
   */
  public void setArgs(Object[] args) {
    this.args = args;
  }

}
