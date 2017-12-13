package com.futuredata.judicature.base.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futuredata.judicature.base.result.ResultCode;


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
public final class FdBizException extends Exception {
  private static final Logger logger = LoggerFactory.getLogger(FdBizException.class);
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

  private String infoJson;

  /**
   * 构造一个<B>有堆栈信息</B>、<B>无cause by</B>的基本异常，Throwable默认调用{@link Throwable#fillInStackTrace()}方法爬栈
   * 
   * @param requestId 请求唯一标识
   * @param resultCode 错误码
   * @param args 需要输出的业务对象列表
   */
  public <T extends ResultCode> FdBizException(String requestId, T resultCode, Object[] args) {
    super(serilizeJson(requestId, resultCode), null, false, true);
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
  public <T extends ResultCode> FdBizException(String requestId, T resultCode, Object[] args,
      Throwable t) {
    super(serilizeJson(requestId, resultCode), t);
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
  public <T extends ResultCode> FdBizException(T resultCode) {
    super(serilizeJson(null, resultCode), null, false, true);
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
   * @return
   */
  public int getCode() {
    return code;
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
  public Object[] getArgs() {
    return args;
  }

  public void printInfo() {
    this.serilizeJson(this.requestId, this.code, this.msg, this.args);
    logger.error(infoJson);
  }

  private static <T extends ResultCode> String serilizeJson(String requestId, T resultCode) {
    return "{requestId:" + requestId + ",code:" + resultCode.getCode() + ",message:"
        + resultCode.getMsg() + "}";
  }

  private <T extends ResultCode> void serilizeJson(String requestId, int code, String message,
      Object[] args) {
    infoJson = "{requestId:" + requestId + ",code:" + code + ",message:" + message + ",args:";
    if (args != null && args.length > 0) {
      StringBuilder sb = new StringBuilder("[");
      for (int i = 0; i < args.length; i++) {
        sb.append(args[i].toString());
        if (i == args.length - 1) {
          sb.append("]");
        } else {
          sb.append(",");
        }
      }
      infoJson = infoJson.concat(sb.toString()).concat("}");
    } else {
      infoJson = infoJson.concat("[]}");
    }
  }

}
