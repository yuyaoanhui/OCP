package com.ocp.rabbit.middleware.orm.datasource;

/**
 * 动态数据源切换管理
 * 
 * @author yu.yao 2018年9月6日
 *
 */
public class DynamicDataSourceContextHolder {

  private static final ThreadLocal<DataSourceKey> currentDatesource = new ThreadLocal<>();

  /**
   * 清除当前数据源
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void clear() {
    currentDatesource.remove();
  }

  /**
   * 获取当前使用的数据源
   * 
   * @author yu.yao
   * @param
   * @return 当前使用数据源的ID
   */
  public static DataSourceKey get() {
    return currentDatesource.get();
  }

  /**
   * 设置当前使用的数据源
   * 
   * @author yu.yao
   * @param value 需要设置的数据源ID
   */
  public static void set(DataSourceKey value) {
    currentDatesource.set(value);
  }

}
