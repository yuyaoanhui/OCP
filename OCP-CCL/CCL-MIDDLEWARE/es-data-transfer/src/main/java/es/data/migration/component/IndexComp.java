package es.data.migration.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.client.Client;

import es.data.migration.util.JSONUtil;

/**
 * 索引操作构件
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class IndexComp {

  /**
   * 判断索引是否存在
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static boolean indexExists(Client client, String index) {
    IndicesExistsRequest request = new IndicesExistsRequest(index);
    IndicesExistsResponse response = client.admin().indices().exists(request).actionGet();
    if (response.isExists()) {
      return true;
    }
    return false;
  }

  /**
   * 创建索引入口方法。若索引不存在则创建,若索引存在则跳过
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void checkCreateIndex(Client client, String index, String configFile)
      throws IOException {
    boolean flag = indexExists(client, index);
    if (!flag) {
      System.out.println("开始创建索引: " + index);
      if (createIndexInternal(client, index, configFile)) {
        System.out.println("索引 " + index + " 创建成功!");
      } else {
        System.out.println("索引 " + index + " 创建失败!");
      }
    } else {
      System.out.println("索引 " + index + " 已经存在!");
    }
  }

  /**
   * 创建索引核心方法
   * 
   * @author yu.yao
   * @param
   * @return
   */
  @SuppressWarnings("unchecked")
  private static boolean createIndexInternal(Client client, String index, String configFile)
      throws IOException {
    CreateIndexRequestBuilder cib = client.admin().indices().prepareCreate(index);
    InputStream input = IndexComp.class.getResourceAsStream(configFile);
    String str = getJson(input);
    Map<String, Object> data = JSONUtil.toObject(str, Map.class);
    for (String ss : data.keySet()) {
      // 索引配置参数
      if (ss.equals("settings")) {
        Map<String, Object> settings = JSONUtil.toObject(JSONUtil.toJson(data.get(ss)), Map.class);
        cib.setSettings(settings);
      } else if (ss.equals("mappings")) {
        Map<String, Object> mappings = JSONUtil.toObject(JSONUtil.toJson(data.get(ss)), Map.class);
        // type配置参数
        for (String esType : mappings.keySet()) {
          Map<String, Object> mapping =
              JSONUtil.toObject(JSONUtil.toJson(mappings.get(esType)), Map.class);
          cib.addMapping(esType, mapping);
        }
      }
    }
    CreateIndexResponse response = cib.execute().actionGet();
    if (response.isAcknowledged()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 读取定义索引mappings的json文件
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static String getJson(InputStream is) {
    InputStreamReader reader = null;
    BufferedReader in = null;
    StringBuffer buffer = null;
    try {
      reader = new InputStreamReader(is, "UTF-8");
      in = new BufferedReader(reader);
      buffer = new StringBuffer();
      String line = " ";
      while ((line = in.readLine()) != null) {
        buffer.append(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
          in = null;
        }
        if (reader != null) {
          reader.close();
          reader = null;
        }
        if (is != null) {
          is.close();
          is = null;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return buffer.toString();
  }
}
