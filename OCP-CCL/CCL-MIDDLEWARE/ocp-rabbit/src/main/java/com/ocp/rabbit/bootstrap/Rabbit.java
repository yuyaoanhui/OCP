package com.ocp.rabbit.bootstrap;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.api.RabbitApi;
import com.ocp.rabbit.repository.util.FileOperate;

/**
 * 主程序入口类
 * 
 * @author yu.yao 2018年8月3日
 *
 */
public class Rabbit {
  private static Logger logger = LoggerFactory.getLogger(Rabbit.class);

  /**
   * 输入文书内容返回抽取结果
   * 
   * @author yu.yao
   * @param content 文书内容
   * @return
   */
  public Map<String, Object> RabbitInterpret(String content) {

    return null;
  }

  /**
   * 主程序入口
   * 
   * @author yu.yao
   * @param
   * @return
   * @throws UnsupportedEncodingException
   */
  public static void main(String[] args) throws UnsupportedEncodingException {
    logger.info("主程序启动......");
    RabbitApi api = new RabbitApi();
    String document = FileOperate
        .readTxt("C:\\Users\\Alex\\Desktop\\8fd169c8-5fcc-481c-b686-a7e40154f614.txt", "utf-8");
    api.RabbitInterpret(document, "盗窃罪");
    logger.info("主程序结束......");
  }

}
