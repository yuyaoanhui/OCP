package com.ocp.rabbit.middleware;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ocp.rabbit.middleware.orm.datasource.DataSourceKey;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TargetDataSource {
  DataSourceKey dataSourceKey() default DataSourceKey.MASTER;
}
