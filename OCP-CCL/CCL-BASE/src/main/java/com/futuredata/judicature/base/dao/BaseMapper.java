package com.futuredata.judicature.base.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.futuredata.judicature.base.model.BaseModel;


/**
 * 基础dao，包含基本增删改查和其他通用法法
 * 
 * @author yu.yao
 *
 * @param T model对象类型
 * @param TExample model对应的查询对象类型
 */
public interface BaseMapper<T extends BaseModel, TExample> {

  /**
   * 统计满足TExample条件的记录的个数
   * 
   * @author yu.yao
   * @param example
   * @return 统计个数
   */
  long countByExample(TExample example);

  /**
   * 查询满足TExample条件的记录列表
   * 
   * @author yu.yao
   * @param example 查询条件
   * @return 查询出的记录列表
   */
  List<T> selectByExample(TExample example);

  /**
   * 根据主键查询记录的详情
   * 
   * @author yu.yao
   * @param id 主键
   * @return 该主键对应的记录详情
   */
  T selectByPrimaryKey(String id);

  /**
   * 全字段插入记录T
   * 
   * @author yu.yao
   * @param record 需要插入的记录
   * @return 插入条数
   */
  int insert(T record);

  /**
   * 根据主键进行更新记录中的全部字段
   * 
   * @author yu.yao
   * @param record 需要更新的记录(包含主键)
   * @return 更新条数
   */
  int updateByPrimaryKey(T record);

  /**
   * 更新单条记录中满足TExample条件的全部字段
   * 
   * @author yu.yao
   * @param record 需要更新的记录对象，包含需要更新的字段
   * @param example 满足的条件
   * @return 更新条数
   */
  int updateByExample(@Param("record") T record, @Param("example") TExample example);

  /**
   * 根据id删除<T> model对象
   * 
   * @author yu.yao
   * @param id 主键
   * @return 成功:1,失败:0
   */
  int deleteByPrimaryKey(String id);

  /**
   * 根据id列表批量删除<T> model对象
   * 
   * @author yu.yao
   * @param ids 主键列表
   * @return 成功:1,失败:0
   */
  int deleteByPrimaryKeys(String ids);

  /**
   * 根据条件删除实体
   * 
   * @author yu.yao
   * @param example 条件对象
   * @return 成功:1,失败:0
   */
  int deleteByExample(TExample example);

}
