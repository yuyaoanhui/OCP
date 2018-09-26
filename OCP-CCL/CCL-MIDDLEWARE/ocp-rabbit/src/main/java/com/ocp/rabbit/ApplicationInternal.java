package com.ocp.rabbit;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;

import com.ocp.rabbit.bootstrap.Bootstrap;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan(basePackages = {"com.futuredata.rabbit.middleware.orm.mapper"})
public class ApplicationInternal extends SpringBootServletInitializer {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationInternal.class);
  public static ApplicationContext springContext = null;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(ApplicationInternal.class);
  }

  public static void main(String[] args) {
    logger.info("rabbit平台开始初始化......");
    springContext = SpringApplication.run(ApplicationInternal.class, args);
    Bootstrap.springContext = springContext;// 使用springboot初始化
    Bootstrap.init();
  }
}
