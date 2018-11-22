package es.data.migration.process;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import es.data.migration.bean.ClusterConfigBean;
import es.data.migration.bean.ImportConfigBean;
import es.data.migration.component.ImportComp;
import es.data.migration.component.IndexComp;
import es.data.migration.tool.ClientTool;

/**
 * ES数据迁移过程类
 * 
 * @author yu.yao 2018年6月23日
 *
 */
public class MigrationProcess {
  public final Logger logger = LoggerFactory.getLogger(MigrationProcess.class);

  public static ClusterConfigBean clusterConfig = new ClusterConfigBean();
  public static ImportConfigBean importConfig = new ImportConfigBean();

  /**
   * 初始化索引
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void init() {
    TransportClient client = ClientTool.getInstance();
    /**
     * 打成jar包时要加上resources，否则读不出文件
     */
    String fdlawcasePath = "/resources/schema/fdlawcase.json";
    String fdsearchtypePath = "/resources/schema/fdsearchtype.json";
    String lxfdlawcasePath = "/resources/schema/lxfdlawcase.json";
    String fdlawPath = "/resources/schema/fdlaw.json";
    String ppofdlawcasePath = "/resources/schema/ppofdlawcase.json";
    /**
     * eclipse中运行使用该路径
     */
    if (IndexComp.class.getResourceAsStream(fdlawcasePath) == null) {
      fdlawcasePath = "/schema/fdlawcase.json";
    }
    if (IndexComp.class.getResourceAsStream(fdsearchtypePath) == null) {
      fdsearchtypePath = "/schema/fdsearchtype.json";
    }
    if (IndexComp.class.getResourceAsStream(lxfdlawcasePath) == null) {
      lxfdlawcasePath = "/schema/lxfdlawcase.json";
    }
    if (IndexComp.class.getResourceAsStream(fdlawPath) == null) {
      fdlawPath = "/schema/fdlaw.json";
    }
    if (IndexComp.class.getResourceAsStream(ppofdlawcasePath) == null) {
      ppofdlawcasePath = "/schema/ppofdlawcase.json";
    }
    try {
      if (!StringUtils.isEmpty(clusterConfig.getFdlawcase())) {
        IndexComp.checkCreateIndex(client, clusterConfig.getFdlawcase(), fdlawcasePath);
      }
      if (!StringUtils.isEmpty(clusterConfig.getFdsearchtype())) {
        IndexComp.checkCreateIndex(client, clusterConfig.getFdsearchtype(), fdsearchtypePath);
      }
      if (!StringUtils.isEmpty(clusterConfig.getLxfdlawcase())) {
        IndexComp.checkCreateIndex(client, clusterConfig.getLxfdlawcase(), lxfdlawcasePath);
      }
      if (!StringUtils.isEmpty(clusterConfig.getFdlaw())) {
        IndexComp.checkCreateIndex(client, clusterConfig.getFdlaw(), fdlawPath);
      }
      if (!StringUtils.isEmpty(clusterConfig.getPpofdlawcase())) {
        IndexComp.checkCreateIndex(client, clusterConfig.getPpofdlawcase(), ppofdlawcasePath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println("参数个数不对.");
      return;
    } else if (args.length == 1) {
      String arg = args[0];
      if (arg.equals("indeces")) {
        System.out.println("执行创建索引功能,加载资源,请稍后......");
        init();
      } else if (arg.equals("migration")) {
        System.out.println("执行数据导入功能,加载资源,请稍后......");
        Map<String, String> indecesMap = clusterConfig.getIndecesMap();
        Map<String, String> typesMap = clusterConfig.getTypesMap();
        String importDicPath = importConfig.getDic();
        int readLines = importConfig.getReadLines();
        ImportComp.importDataBatch(indecesMap, typesMap, importDicPath, readLines);
      } else {
        System.out.println("参数不正确.");
      }
    }
  }
}
