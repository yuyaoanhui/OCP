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
  public static final ResultCode SYS_INSERT_FAIL = new ResultCode(1003, "新增数据失败");
  public static final ResultCode SYS_UPDATE_FAIL = new ResultCode(1004, "修改数据失败");
  public static final ResultCode SYS_DELETE_FAIL = new ResultCode(1005, "删除数据失败");
  public static final ResultCode SYS_QUERY_FAIL = new ResultCode(1006, "查询数据失败");
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
