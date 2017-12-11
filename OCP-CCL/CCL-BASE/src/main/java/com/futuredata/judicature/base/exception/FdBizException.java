package com.futuredata.judicature.base.exception;

import com.futuredata.judicature.base.result.BaseResultCode;

/**
 * 自定义异常类：
 * <p>
 * 1.<B>统一</B>开发者对外异常展示的方式,.
 * </p>
 * <p>
 * 2.在某些校验或者遇到某些问题时，通过抛出自定义异常<B>直接结束</B>当前的请求.
 * </p>
 * <p>
 * 3.在某些特殊的业务逻辑处即便无语法问题也能<B>灵活</B>选择是否抛出异常.
 * </p>
 * <p>
 * 4.隐藏底层的异常，自定义输出信息包括堆栈，更<B>安全</B>与<B>直观</B>.
 * </p>
 * 
 * @author yu.yao
 *
 */
public class FdBizException extends Exception {

  /**
   * 序列化ID
   */
  private static final long serialVersionUID = 1L;

  /**
   * 请求唯一标识(reuqest id)
   */
  private String requestId;

  /**
   * 异常信息编码
   */
  private int code;

  /**
   * 异常信息
   */
  private String msg;

  /**
   * 需要输出的业务对象列表
   */
  private Object[] args;

  /**
   * 构造一个<B>有堆栈信息</B>、<B>无cause by</B>的基本异常，Throwable默认调用{@link Throwable#fillInStackTrace()}方法爬栈
   * 
   * @param requestId 请求唯一标识
   * @param resultCode 错误码
   * @param args 需要输出的业务对象列表
   */
  public <T extends BaseResultCode> FdBizException(String requestId, T resultCode, Object[] args) {
    super(resultCode.getMsg(), null, false, true);
    this.requestId = requestId;
    this.code = resultCode.getCode();
    this.msg = resultCode.getMsg();
    this.args = args;
  }

  /**
   * 构造一个<B>有堆栈信息</B>、<B>有cause by</B>的基本异常，Throwable默认调用{@link Throwable#fillInStackTrace()}方法爬栈
   * 
   * @param requestId 请求唯一标识
   * @param resultCode 错误码
   * @param args 需要输出的业务对象列表
   * @param t 被捕获到的异常
   */
  public <T extends BaseResultCode> FdBizException(String requestId, T resultCode, Object[] args,
      Throwable t) {
    super(resultCode.getMsg(), t);
    this.requestId = requestId;
    this.code = resultCode.getCode();
    this.msg = resultCode.getMsg();
    this.args = args;
  }

  /**
   * 构造一个<B>无堆栈信息</B>的基本异常(此方法关闭了爬栈开关，可以提高抛出异常的性能).
   * <p>
   * 此方法即使没有捕获到异常也可以根据业务逻辑灵活选择抛出
   * </p>
   * 
   * @param resultCode 错误码
   */
  public <T extends BaseResultCode> FdBizException(T resultCode) {
    super(resultCode.getMsg(), null, false, true);
    this.code = resultCode.getCode();
    this.msg = resultCode.getMsg();
  }

  /**
   * 构造一个<B>无堆栈信息</B>的基本异常(此方法关闭了爬栈开关，可以提高抛出异常的性能).
   * <p>
   * 此方法即使没有捕获到异常也可以根据业务逻辑灵活选择抛出
   * </p>
   * 
   * @param msg 要输出的信息
   */
  public FdBizException(String msg) {
    super(msg, null, false, false);
    this.msg = msg;
  }

  /**
   * 
   * @return
   */
  public String getRequestId() {
    return requestId;
  }

  /**
   * 
   * @param requestId
   */
  public void setRequestId(String requestId) {
    this.requestId = requestId;
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


  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  public synchronized Throwable getCause() {
    // TODO Auto-generated method stub
    return super.getCause();
  }

  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  public synchronized Throwable initCause(Throwable cause) {
    // TODO Auto-generated method stub
    return super.initCause(cause);
  }

  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  public String toString() {
    // TODO Auto-generated method stub
    return super.toString();
  }

  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  public synchronized Throwable fillInStackTrace() {
    // TODO Auto-generated method stub
    return super.fillInStackTrace();
    // return this;
  }

  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  public void setStackTrace(StackTraceElement[] stackTrace) {
    // TODO Auto-generated method stub
    super.setStackTrace(stackTrace);
  }

  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    // TODO Auto-generated method stub
    return super.clone();
  }

  /**
   * 重写父类的方法
   * <p>
   * {@inheritDoc}
   * </p>
   */
  @Override
  protected void finalize() throws Throwable {
    // TODO Auto-generated method stub
    super.finalize();
  }
}
