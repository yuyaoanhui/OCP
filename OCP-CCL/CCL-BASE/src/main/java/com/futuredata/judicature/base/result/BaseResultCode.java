package com.futuredata.judicature.base.result;

/**
 * 系统级返回码
 * 
 * @author yu.yao
 *
 */
public class BaseResultCode {

  public static final ResultCode SYS_SUCCESS = new ResultCode(1000, "操作成功");
  public static final ResultCode SYS_ID_NULL = new ResultCode(1001, "主键id为空");
  public static final ResultCode SYS_PARAM_NULL = new ResultCode(1002, "方法参数为空");
  public static final ResultCode SYS_ERROR = new ResultCode(1099, "系统发生未知错误");

  /**
   * 是否有重复编码
   * 
   * @return
   */
  boolean hasRepeatCode() {
    return false;
  }

}
