package com.ocp.rabbit.middleware.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ocp.rabbit.middleware.TargetDataSource;
import com.ocp.rabbit.middleware.orm.QueryDTO;
import com.ocp.rabbit.middleware.orm.datasource.DataSourceKey;
import com.ocp.rabbit.middleware.orm.mapper.InfoPointMapper;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.orm.model.InfoPointExample;
import com.ocp.rabbit.middleware.orm.model.InfoPointExample.Criteria;
import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.repository.tool.RabbitException;
import com.ocp.rabbit.repository.util.PropertiesUtil;

/**
 * 信息点管理服务接口实现类
 * 
 * @author yu.yao 2018年8月1日
 *
 */
@Service
public class InfoPointServiceImpl implements InfoPointService {
  private static final Logger logger = LoggerFactory.getLogger(InfoPointServiceImpl.class);

  static String version_court =
      PropertiesUtil.getProperty("application.properties", "version.court");
  static String version_procuratorate =
      PropertiesUtil.getProperty("application.properties", "version.procuratorate");
  @Autowired
  private InfoPointMapper infoPointMapper;

  @Override
  @TargetDataSource(dataSourceKey = DataSourceKey.MASTER)
  public List<InfoPoint> queryByType(String docType) throws RabbitException {
    String org = DocumentType.valueOf(docType).getOrgnization();
    String version = "";
    if (org.equals("court")) {
      version = version_court;
    } else if (org.equals("procuratorate")) {
      version = version_procuratorate;
    }
    InfoPointExample example = new InfoPointExample();
    example.createCriteria().andOrgEqualTo(org).andDoctypeEqualTo(docType)
        .andVersionEqualTo(version);
    List<InfoPoint> list = infoPointMapper.selectByExample(example);
    logger.info("查询文书类型为" + docType + "的信息点：" + (list == null ? 0 : list.size()) + "条");
    return list;
  }

  @Override
  @TargetDataSource(dataSourceKey = DataSourceKey.MASTER)
  public long queryCount(QueryDTO dto) throws RabbitException {
    InfoPointExample example = new InfoPointExample();
    Criteria criteria = example.createCriteria();
    if (!StringUtils.isEmpty(dto.getVariable())) {
      criteria.andVariableEqualTo(dto.getVariable());
    }
    if (!StringUtils.isEmpty(dto.getAy())) {
      criteria.andAyEqualTo(dto.getAy());
    }
    if (!StringUtils.isEmpty(dto.getMajoray())) {
      criteria.andMajorayEqualTo(dto.getMajoray());
    }
    if (!StringUtils.isEmpty(dto.getDoctype())) {
      criteria.andDoctypeEqualTo(dto.getDoctype());
    }
    if (!StringUtils.isEmpty(dto.getName())) {
      criteria.andNameLike(dto.getName());
    }
    if (!StringUtils.isEmpty(dto.getOrg())) {
      criteria.andOrgEqualTo(dto.getOrg());
    }
    if (!StringUtils.isEmpty(dto.getVersion())) {
      criteria.andVersionEqualTo(dto.getVersion());
    }
    if (!StringUtils.isEmpty(dto.getW())) {
      criteria.andWEqualTo(dto.getW());
    }
    return infoPointMapper.countByExample(example);
  }

  @Override
  @TargetDataSource(dataSourceKey = DataSourceKey.MASTER)
  public List<InfoPoint> queryByCondition(QueryDTO dto) throws RabbitException {
    InfoPointExample example = new InfoPointExample();
    Criteria criteria = example.createCriteria();
    if (!StringUtils.isEmpty(dto.getVariable())) {
      criteria.andVariableEqualTo(dto.getVariable());
    }
    if (!StringUtils.isEmpty(dto.getAy())) {
      criteria.andAyEqualTo(dto.getAy());
    }
    if (!StringUtils.isEmpty(dto.getMajoray())) {
      criteria.andMajorayEqualTo(dto.getMajoray());
    }
    if (!StringUtils.isEmpty(dto.getDoctype())) {
      criteria.andDoctypeEqualTo(dto.getDoctype());
    }
    if (!StringUtils.isEmpty(dto.getName())) {
      criteria.andNameLike(dto.getName());
    }
    if (!StringUtils.isEmpty(dto.getOrg())) {
      criteria.andOrgEqualTo(dto.getOrg());
    }
    if (!StringUtils.isEmpty(dto.getVersion())) {
      criteria.andVersionEqualTo(dto.getVersion());
    }
    if (!StringUtils.isEmpty(dto.getW())) {
      criteria.andWEqualTo(dto.getW());
    }
    int start = (dto.getPageNum() - 1) * dto.getPageSize();
    int end = start + dto.getPageSize();
    example.setStartNum(start);
    example.setEndNum(end);
    return infoPointMapper.selectByExample(example);
  }

}
