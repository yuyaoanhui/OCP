package es.data.migration.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取application.properties文件配置项工具类
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class PropertiesUtil {
  public static Properties p = null;

  static {
    if (null == p) {
      p = new Properties();
      InputStream in = PropertiesUtil.class.getResourceAsStream("/application.properties");
      if(in == null){
        in = PropertiesUtil.class.getResourceAsStream("/resources/application.properties");
      }
      try {
        p.load(new InputStreamReader(in, "UTF-8"));
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          in.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static String getProperty(String key) {
    return p.getProperty(key);
  }
}
