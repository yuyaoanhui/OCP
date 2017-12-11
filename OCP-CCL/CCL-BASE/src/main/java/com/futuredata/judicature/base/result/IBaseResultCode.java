package com.futuredata.judicature.base.result;

import java.util.HashSet;
import java.util.Set;

/**
 * 系统级返回码
 * 
 * @author yu.yao
 *
 */
public interface IBaseResultCode {
  
  Set<Integer> codeSet = new HashSet<Integer>();

  ResultCode SYS_SUCCESS = new ResultCode(1000, "操作成功");

  ResultCode SYS_ERROR = new ResultCode(1099, "系统发生未知错误");

  /**
   * 是否有重复编码
   * 
   * @return
   */
  default boolean hasRepeatCode() {
    return false;
//    for (Field field : this.getClass().getFields()) {
//      field.
//    }
  }
}
