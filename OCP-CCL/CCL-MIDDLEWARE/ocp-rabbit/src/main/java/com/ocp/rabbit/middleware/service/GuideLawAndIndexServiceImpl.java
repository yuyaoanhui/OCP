package com.ocp.rabbit.middleware.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ocp.rabbit.middleware.TargetDataSource;
import com.ocp.rabbit.middleware.orm.datasource.DataSourceKey;
import com.ocp.rabbit.middleware.orm.mapper.GuideLawIndexMapper;
import com.ocp.rabbit.middleware.orm.mapper.GuideLawMapper;
import com.ocp.rabbit.middleware.orm.model.GuideLawExample;
import com.ocp.rabbit.middleware.orm.model.GuideLawIndex;
import com.ocp.rabbit.middleware.orm.model.GuideLawIndexExample;
import com.ocp.rabbit.middleware.orm.model.GuideLawWithBLOBs;
import com.ocp.rabbit.repository.tool.RabbitException;

/**
 * 信息点管理服务接口实现类
 * 
 * @author yu.yao 2018年8月1日
 *
 */
@Service
public class GuideLawAndIndexServiceImpl implements GuideLawAndIndexService {

  @Autowired
  private GuideLawMapper guideLawMapper;

  @Autowired
  private GuideLawIndexMapper guideLawIndexMapper;

  @Override
  @TargetDataSource(dataSourceKey = DataSourceKey.SECOND)
  public List<GuideLawWithBLOBs> queryLaw() throws RabbitException {
    GuideLawExample example = new GuideLawExample();
    List<GuideLawWithBLOBs> result = guideLawMapper.selectByExampleWithBLOBs(example);
    return result;
  }

  @Override
  @TargetDataSource(dataSourceKey = DataSourceKey.SECOND)
  public List<GuideLawIndex> queryLawIndex() throws RabbitException {
    GuideLawIndexExample example = new GuideLawIndexExample();
    List<GuideLawIndex> result = guideLawIndexMapper.selectByExample(example);
    return result;
  }
}
