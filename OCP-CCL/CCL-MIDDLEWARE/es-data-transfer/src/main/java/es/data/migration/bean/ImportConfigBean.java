package es.data.migration.bean;

import es.data.migration.util.PropertiesUtil;

/**
 * 数据导入配置bean
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class ImportConfigBean {
  private int bulkActions;// 当前累计加入多少请求时进行提交,默认1000
  private long byteSizeValue;// 数据大小满多少开始提交,单位M,默认为5M
  private int concurrentRequests;// 同时还可以有多少个请求线程可以被执行,默认为1即两个
  private long flushInterval;// 多长时间提交一次,单位秒,默认不设置即为0

  private int readLines;// 每次从文件中读多少条数据

  private String dic;// 从哪个目录下读取存档文件

  public ImportConfigBean() {
    this.bulkActions = Integer.parseInt(PropertiesUtil.getProperty("import.bulkActions"));
    this.byteSizeValue = Long.parseLong(PropertiesUtil.getProperty("import.byteSizeValue"));
    this.concurrentRequests =
        Integer.parseInt(PropertiesUtil.getProperty("import.concurrentRequests"));
    this.flushInterval = Long.parseLong(PropertiesUtil.getProperty("import.flushInterval"));
    this.dic = PropertiesUtil.getProperty("import.dic");
    this.readLines = Integer.parseInt(PropertiesUtil.getProperty("import.read.lines"));
  }

  public int getBulkActions() {
    return bulkActions;
  }

  public void setBulkActions(int bulkActions) {
    this.bulkActions = bulkActions;
  }

  public long getByteSizeValue() {
    return byteSizeValue;
  }

  public void setByteSizeValue(long byteSizeValue) {
    this.byteSizeValue = byteSizeValue;
  }

  public int getConcurrentRequests() {
    return concurrentRequests;
  }

  public void setConcurrentRequests(int concurrentRequests) {
    this.concurrentRequests = concurrentRequests;
  }

  public long getFlushInterval() {
    return flushInterval;
  }

  public void setFlushInterval(long flushInterval) {
    this.flushInterval = flushInterval;
  }

  public int getReadLines() {
    return readLines;
  }

  public void setReadLines(int readLines) {
    this.readLines = readLines;
  }

  public String getDic() {
    return dic;
  }

  public void setDic(String dic) {
    this.dic = dic;
  }

}
