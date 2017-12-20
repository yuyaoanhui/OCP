package com.ocp.common.model.repository;

/**
 * 资源库接口
 * 
 * @author yu.yao
 *
 */
public interface Repository {
  /**
   * 根据id从仓库中获取实体资源
   * 
   * @author yu.yao
   * @param
   * @return
   */
  <T> T find(String id);

  /**
   * 保存实体
   * 
   * @author yu.yao
   * @param
   * @return
   */
  <T> void save(T t);
}
