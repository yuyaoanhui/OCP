package com.ocp.rabbit.middleware.orm.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 设置数据源
 * 
 * @author yu.yao 2018年9月6日
 *
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {
  private static final Logger logger = LoggerFactory.getLogger(DynamicRoutingDataSource.class);

  @Override
  protected Object determineCurrentLookupKey() {
    logger.info("当前数据源：" + DynamicDataSourceContextHolder.get());
    return DynamicDataSourceContextHolder.get();
  }
}
