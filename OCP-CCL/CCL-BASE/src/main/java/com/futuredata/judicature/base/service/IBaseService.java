package com.futuredata.judicature.base.service;

import java.util.List;

import com.futuredata.judicature.base.exception.FdBizException;
import com.futuredata.judicature.base.model.BaseModel;

/**
 * 
 * @author yu.yao
 *
 */
public interface IBaseService<T extends BaseModel, TExample> {

  /**
   * 保存实体
   * 
   * @author yu.yao
   * @param
   * @return id
   */
  String save(T t) throws FdBizException;

  /**
   * 更新实体
   * 
   * @author yu.yao
   * @param
   * @return
   */
  boolean update(T t) throws FdBizException;

  /**
   * 根据id删除
   * 
   * @author yu.yao
   * @param
   * @return
   */
  boolean delete(String id) throws FdBizException;

  /**
   * 根据id列表批量删除
   * 
   * @author yu.yao
   * @param
   * @return
   */
  boolean deleteBatch(String[] ids) throws FdBizException;

  /**
   * 根据id找到对应的一条记录
   * 
   * @author yu.yao
   * @param
   * @return
   */
  T findOne(String id) throws FdBizException;

  /**
   * 根据example找到符合条件的记录列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  List<T> findList(TExample example) throws FdBizException;
}
