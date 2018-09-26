package com.ocp.rabbit.middleware.orm.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ocp.rabbit.middleware.orm.model.BaseModel;


/**
 * 基础dao
 * <p>
 * 1.比自动生成的TMapper约定了更多的基础方法
 * </p>
 * <p>
 * 2.用于注入基础service中
 * </p>
 * 
 * @author yu.yao
 *
 * @param T model对象类型
 *        <p>
 *        既可以对应数据库中的物理表，也可能对应一个复杂查询应该返回的结果集
 *        </p>
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
  T selectByPrimaryKey(Long id);

  /**
   * 全字段插入记录T
   * 
   * @author yu.yao
   * @param record 需要插入的记录
   * @return 插入条数
   */
  int insert(T record);

  /**
   * 非空字段插入记录T
   * 
   * @author yu.yao
   * @param record 需要插入的记录
   * @return 插入条数
   */
  int insertSelective(T record);

  /**
   * 根据主键进行更新记录中的全部字段
   * 
   * @author yu.yao
   * @param record 需要更新的记录(包含主键)
   * @return 更新条数
   */
  int updateByPrimaryKey(T record);

  /**
   * 根据主键进行更新记录中的非null字段
   * 
   * @author yu.yao
   * @param record 需要更新的记录(包含主键)
   * @return 更新条数
   */
  int updateByPrimaryKeySelective(T record);

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
   * 更新单条记录中满足TExample条件的非null字段
   * 
   * @author yu.yao
   * @param
   * @return
   */
  int updateByExampleSelective(T record, TExample example);

  /**
   * 根据id删除<T> model对象
   * 
   * @author yu.yao
   * @param id 主键
   * @return 成功:1,失败:0
   */
  int deleteByPrimaryKey(Long id);

  /**
   * 根据id列表批量删除<T> model对象
   * 
   * @author yu.yao
   * @param ids 主键列表
   * @return 成功:1,失败:0
   */
  int deleteByPrimaryKeys(Long[] ids);

  /**
   * 根据条件删除实体
   * 
   * @author yu.yao
   * @param example 条件对象
   * @return 成功:1,失败:0
   */
  int deleteByExample(TExample example);

  /**
   * 批量插入(全字段)
   * 
   * @author yu.yao
   * @param
   * @return
   */
  int insertBatch(List<T> list);

  /**
   * 批量插入(非null字段)
   * 
   * @author yu.yao
   * @param
   * @return
   */
  int insertSelectiveBatch(List<T> list);

}
