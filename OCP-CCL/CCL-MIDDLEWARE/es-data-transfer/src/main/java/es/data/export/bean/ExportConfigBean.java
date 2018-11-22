package es.data.export.bean;

import java.util.HashMap;
import java.util.Map;

import es.data.export.util.PropertiesUtil;

/**
 * 数据导出的配置bean
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class ExportConfigBean {
  private String dicPath;// 导出数据保存文件的目录
  private long docSize;// 导出数据保存为多个文件，设置单个文件的最大大小。但得M
  private long scroll;// 游标查询数据保存多久可删除，要保证大于一页数据的查询时间。单位秒

  private Map<String, Integer> pageSize = new HashMap<>();// 不同索引每次每个分片导出多少条数据

  public ExportConfigBean() {
    this.dicPath = PropertiesUtil.getProperty("export.dic");
    this.docSize = Long.parseLong(PropertiesUtil.getProperty("export.doc.size"));
    this.scroll = Long.parseLong(PropertiesUtil.getProperty("export.scroll"));
    this.pageSize.put("fdlawcase", 1500);
    this.pageSize.put("fdsearchtype", 6000);
    this.pageSize.put("lxfdlawcase", 1500);
    this.pageSize.put("fdlaw", 1500);
    this.pageSize.put("ppofdlawcase", 1500);
  }

  public String getDicPath() {
    return dicPath;
  }

  public void setDicPath(String dicPath) {
    this.dicPath = dicPath;
  }

  public long getDocSize() {
    return docSize;
  }

  public void setDocSize(long docSize) {
    this.docSize = docSize;
  }

  public long getScroll() {
    return scroll;
  }

  public void setScroll(long scroll) {
    this.scroll = scroll;
  }

  public Map<String, Integer> getPageSize() {
    return pageSize;
  }

  public void setPageSize(Map<String, Integer> pageSize) {
    this.pageSize = pageSize;
  }

}
