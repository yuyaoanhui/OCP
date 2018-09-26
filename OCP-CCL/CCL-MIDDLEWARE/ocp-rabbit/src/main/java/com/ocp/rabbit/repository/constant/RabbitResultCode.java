package com.ocp.rabbit.repository.constant;

import com.ocp.base.result.BaseResultCode;
import com.ocp.base.result.ResultCode;

/**
 * 系统返回码
 * 
 * @author yu.yao
 *
 */
public class RabbitResultCode extends BaseResultCode {

  public static final ResultCode RABBIT_SUCCESS = new ResultCode(1, "操作成功");
  public static final ResultCode RABBIT_WAITING = new ResultCode(2, "平台启动中，请耐心等待！");
  public static final ResultCode RABBIT_ID_NULL = new ResultCode(20001, "主键id为空");
  public static final ResultCode RABBIT_PARAM_NULL = new ResultCode(20002, "方法参数为空");
  public static final ResultCode RABBIT_INSERT_FAIL = new ResultCode(20003, "新增数据失败");
  public static final ResultCode RABBIT_UPDATE_FAIL = new ResultCode(20004, "修改数据失败");
  public static final ResultCode RABBIT_DELETE_FAIL = new ResultCode(20005, "删除数据失败");
  public static final ResultCode RABBIT_QUERY_FAIL = new ResultCode(20006, "查询数据失败");
  public static final ResultCode RABBIT_REFLECT_ERROR = new ResultCode(20007, "方法反射出错");

  public static final ResultCode RABBIT_FAIL = new ResultCode(-1, "系统发生未知错误");
  public static final ResultCode RABBIT_EXCEPTION = new ResultCode(-2, "系统发生异常");

  public static final ResultCode RABBIT_GENERAL = new ResultCode(80000, "系统发生异常");// 0x13880
  public static final ResultCode RABBIT_FILE_NOT_FOUND = new ResultCode(80001, "找不到文件"); // 0x13881
  public static final ResultCode RABBIT_OPEN_FILE_FAILD = new ResultCode(80002, "文件打开失败"); // 0x13882
  public static final ResultCode RABBIT_INVALID_RES = new ResultCode(80003, "非法的资源"); // 0x13883
  public static final ResultCode RABBIT_RES_NOT_FOUND = new ResultCode(80004, "找不到资源"); // 0x13884
  public static final ResultCode RABBIT_RES_LOAD_FIALED = new ResultCode(80005, "加载资源失败"); // 0x13885
  public static final ResultCode RABBIT_OUT_OF_MEMORY = new ResultCode(80006, "内存溢出"); // 0x13886
  public static final ResultCode RABBIT_INVALID_PARAM = new ResultCode(80007, "参数不合法"); // 0x13887

  public static final ResultCode RABBIT_UNSUPPORTED_DOC_TYPE = new ResultCode(80008, "不支持的文书类型"); // 0x13888

  public static final ResultCode RABBIT_NO_EXTRACT_INFO = new ResultCode(10000, "信息点不存在");

}
