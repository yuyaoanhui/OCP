package com.ocp.base.service;

import java.util.List;
import com.ocp.base.exception.FdBizException;
import com.ocp.base.model.BaseModel;

/**
 * 
 * @author yu.yao
 *
 */
public interface IBaseService<T extends BaseModel, TExample> {

  /**
   * 保存实体,返回id
   * 
   * @author yu.yao
   * @param t 要保存的实体
   * @param notNull 是否排除空字段
   * @return id
   */
  String save(T t, boolean notNull) throws FdBizException;

  /**
   * 修改实体
   * 
   * @author yu.yao
   * @param t 要保存的实体
   * @param notNull 是否排除空字段
   * @return
   */
  boolean modify(T t, boolean notNull) throws FdBizException;

  /**
   * 根据id删除
   * 
   * @author yu.yao
   * @param
   * @return
   */
  boolean deleteById(String id) throws FdBizException;

  /**
   * 根据id列表批量删除
   * 
   * @author yu.yao
   * @param
   * @return
   */
  boolean deleteByIds(String[] ids) throws FdBizException;

  /**
   * 根据id找到对应的一条记录
   * 
   * @author yu.yao
   * @param
   * @return
   */
  T queryOneById(String id) throws FdBizException;

  /**
   * 根据id列表找到对应的多条记录
   * 
   * @author yu.yao
   * @param
   * @return
   */
  T queryListByIds(String[] ids) throws FdBizException;

  /**
   * 根据example找到符合条件的记录列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  List<T> queryList(TExample example) throws FdBizException;
}
