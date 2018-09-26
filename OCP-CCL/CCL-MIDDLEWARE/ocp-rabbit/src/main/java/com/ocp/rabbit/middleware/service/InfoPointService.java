package com.ocp.rabbit.middleware.service;

import java.util.List;

import com.ocp.rabbit.middleware.orm.QueryDTO;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.repository.tool.RabbitException;

/**
 * 信息点管理服务接口
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public interface InfoPointService {
  /**
   * 按照文书类型查询信息点
   * 
   * @author yu.yao
   * @param
   * @return
   * @throws RabbitException
   */
  public List<InfoPoint> queryByType(String docType) throws RabbitException;

  /**
   * 按照条件查询信息点
   * 
   * @author yu.yao
   * @param
   * @return
   * @throws RabbitException
   */
  public List<InfoPoint> queryByCondition(QueryDTO dto) throws RabbitException;

  /**
   * 统计个数
   * 
   * @author yu.yao
   * @param
   * @return
   * @throws RabbitException
   */
  public long queryCount(QueryDTO dto) throws RabbitException;

}
