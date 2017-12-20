package com.ocp.base.model;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.base.result.BaseResultCode;

/**
 * 基础Model类，定义了id并重写了toString()方法
 * <p>
 * 作为数据库表的实体类，也可以作为复杂查询结果的返回集类
 * </p>
 * 
 * @author yu.yao
 *
 */
public class BaseModel implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(BaseModel.class);
  private static final long serialVersionUID = 1L;

  @NotNull
  private String id;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  /**
   * 重写toString()用于方便地获取实体属性的值
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    try {
      Class<?> c = this.getClass();
      sb.append("{class:" + this.getClass().getName());
      for (Method m : c.getMethods()) {
        if (m.getName().startsWith("get") && m.getName().length() > 3
            && m.getParameterTypes().length == 0 && !"getClass".endsWith(m.getName())) {
          sb.append("," + m.getName().substring(3, 4).toLowerCase() + m.getName().substring(4) + ":"
              + m.invoke(this));
        }
      }
      sb.append("}");
    } catch (Exception e) {
      logger.error(BaseResultCode.SYS_REFLECT_ERROR.getMsg(), e);
      return null;
    }
    return sb.toString();
  }

}
