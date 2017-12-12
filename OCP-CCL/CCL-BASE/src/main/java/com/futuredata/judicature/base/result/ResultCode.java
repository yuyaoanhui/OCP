package com.futuredata.judicature.base.result;

import java.util.HashSet;
import java.util.Set;

import com.futuredata.judicature.base.exception.FdBizException;
import com.futuredata.judicature.sdk.utils.StringUtils;

/**
 * 定义返回码类
 * 
 * @author yu.yao
 *
 */
public class ResultCode {

  private static final Set<Integer> loadedCodes = new HashSet<Integer>();

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
   * <p>
   * 校验是否有重复定义的返回码
   * </p>
   * 
   * @throws FdBizException
   */
  public ResultCode(int code, String msg) {
    if (code == 0) {
      throw new IllegalArgumentException("返回码编码不能为0.");
    }
    if (StringUtils.isEmpty(msg)) {
      throw new IllegalArgumentException("返回码描述信息不能为空.");
    }
    this.code = code;
    this.msg = msg;
    if (loadedCodes.contains(this.getCode())) {
      throw new IllegalArgumentException("定义的返回码已存在,不能重复定义.");
    } else {
      loadedCodes.add(this.getCode());
    }
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

}
