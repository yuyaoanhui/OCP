package com.ocp.rabbit.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 服务启动执行
 * 
 * @author yu.yao 2018年8月1日
 *
 */
@Component
@Order(value = 1) // 启动时各runner的执行顺序
public class StartupRunner implements CommandLineRunner {

  @Override
  public void run(String... args) throws Exception {
    // Bootstrap.init();
  }

}
