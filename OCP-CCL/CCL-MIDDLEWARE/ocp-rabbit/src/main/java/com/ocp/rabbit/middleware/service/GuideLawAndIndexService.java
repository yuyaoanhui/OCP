package com.ocp.rabbit.middleware.service;

import java.util.List;

import com.ocp.rabbit.middleware.orm.model.GuideLawIndex;
import com.ocp.rabbit.middleware.orm.model.GuideLawWithBLOBs;
import com.ocp.rabbit.repository.tool.RabbitException;

/**
 * 法律法规及索引服务接口
 * 
 * @author yu.yao 2018年9月6日
 *
 */
public interface GuideLawAndIndexService {
  /**
   * 查询法律法规
   * 
   * @author yu.yao
   * @param
   * @return
   * @throws RabbitException
   */
  public List<GuideLawWithBLOBs> queryLaw() throws RabbitException;

  /**
   * 查询法律法规索引信息
   * 
   * @author yu.yao
   * @param
   * @return
   * @throws RabbitException
   */
  public List<GuideLawIndex> queryLawIndex() throws RabbitException;

}
