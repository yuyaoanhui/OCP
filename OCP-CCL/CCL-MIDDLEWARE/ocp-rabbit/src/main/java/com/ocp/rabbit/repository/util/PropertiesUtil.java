package com.ocp.rabbit.repository.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 读取rabbit.properties文件配置项工具类
 * 
 * @author yu.yao 2018年7月28日
 *
 */
public class PropertiesUtil {
  public static Map<String, Properties> propMap = null;

  static {
    if (null == propMap) {
      propMap = new HashMap<String, Properties>();
      Properties prop1 = new Properties();
      Properties prop2 = new Properties();
      InputStream in1 = PropertiesUtil.class.getResourceAsStream("/rabbit.properties");
      InputStream in2 = PropertiesUtil.class.getResourceAsStream("/application.properties");
      try {
        prop1.load(new InputStreamReader(in1, "UTF-8"));
        prop2.load(new InputStreamReader(in2, "UTF-8"));
        propMap.put("rabbit.properties", prop1);
        propMap.put("application.properties", prop2);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in1.close();
          in2.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static String getProperty(String fileName, String key) {
    return propMap.get(fileName).getProperty(key);
  }
}
