package com.ocp.rabbit.middleware;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.ocp.rabbit.middleware.orm.datasource.DataSourceKey;
import com.ocp.rabbit.middleware.orm.datasource.DynamicRoutingDataSource;

/**
 * 系统配置bean
 * 
 * @author yu.yao 2018年6月28日
 *
 */
@PropertySource("classpath:application.properties")
@Configuration
@MapperScan("com.futuredata.rabbit.middleware.orm.mapper")
public class ConfigBean {

  @Value("${mybatis.mapper-locations}")
  private String mapperLocations;

  @Bean
  public SqlSessionFactory myGetSqlSessionFactory(DataSource dataSource) throws Exception {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dynamicDataSource());
    sqlSessionFactoryBean.setMapperLocations(
        new PathMatchingResourcePatternResolver().getResources(mapperLocations));
    return sqlSessionFactoryBean.getObject();
  }

  @Bean
  public DataSource dynamicDataSource() {
    DynamicRoutingDataSource dataSource = new DynamicRoutingDataSource();
    Map<Object, Object> dataSourceMap = new HashMap<>();
    dataSource.setDefaultTargetDataSource(masterDataSource());
    dataSourceMap.put(DataSourceKey.MASTER, masterDataSource());
    dataSourceMap.put(DataSourceKey.SECOND, secondDataSource());
    dataSource.setTargetDataSources(dataSourceMap);
    return dataSource;
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "multiple.datasource.master")
  public DataSource masterDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  @ConfigurationProperties(prefix = "multiple.datasource.second")
  public DataSource secondDataSource() {
    return DataSourceBuilder.create().build();
  }

  @Bean
  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
    return new SqlSessionTemplate(sqlSessionFactory);
  }

  @Bean
  public PlatformTransactionManager transactionManager(
      @Qualifier("dynamicDataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  public String getMapperLocations() {
    return mapperLocations;
  }

}
