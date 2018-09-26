package com.ocp.rabbit.middleware;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.ocp.rabbit.middleware.orm.datasource.DataSourceKey;
import com.ocp.rabbit.middleware.orm.datasource.DynamicDataSourceContextHolder;

@Aspect
@Order(-1)
@Component
public class DynamicDataSourceAspect {
  private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);

  @Pointcut("execution(* com.futuredata.rabbit.middleware.orm.service.*Impl.*(..))")
  public void pointCut() {}

  /**
   * 执行方法前更换数据源
   *
   * @param joinPoint 切点
   * @param targetDataSource 动态数据源
   */
  @Before("@annotation(targetDataSource)")
  public void doBefore(JoinPoint joinPoint, TargetDataSource targetDataSource) {
    DataSourceKey dataSourceKey = targetDataSource.dataSourceKey();
    logger.info(String.format("设置数据源为  %s", dataSourceKey));
    DynamicDataSourceContextHolder.set(dataSourceKey);
  }

  /**
   * 执行方法后清除数据源设置
   *
   * @param joinPoint 切点
   * @param targetDataSource 动态数据源
   */
  @After("@annotation(targetDataSource)")
  public void doAfter(JoinPoint joinPoint, TargetDataSource targetDataSource) {
    logger.info(String.format("当前数据源  %s  执行清理方法", targetDataSource.dataSourceKey()));
    DynamicDataSourceContextHolder.clear();
  }
}
