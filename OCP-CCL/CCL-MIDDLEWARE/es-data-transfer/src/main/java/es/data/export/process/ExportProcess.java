package es.data.export.process;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import es.data.export.bean.ClusterConfigBean;
import es.data.export.bean.ExportConfigBean;
import es.data.export.component.ExportComp;

/**
 * <br>
 * 数据导出流程</br>
 * <br>
 * 使用scan+scroll高效获取某个索引的全量数据</br>
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class ExportProcess {

  public static ClusterConfigBean clusterConfig = new ClusterConfigBean();
  public static ExportConfigBean exportConfig = new ExportConfigBean();

  public static void main(String[] args) throws IOException {
    Map<String, String> indecesMap = clusterConfig.getIndecesMap();
    Map<String, List<String>> indexTypes = clusterConfig.getIndexTypes();
    String exportDicPath = exportConfig.getDicPath();
    long exportDocSize = exportConfig.getDocSize();
    long scroll = exportConfig.getScroll();
    ExportComp.exportDataBatch(indecesMap, indexTypes, exportDicPath, exportDocSize, scroll);
  }
}
