package es.data.migration.tool;

import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.data.migration.bean.ClusterConfigBean;
import es.data.migration.bean.ImportConfigBean;

/**
 * bulkProcessor生成器
 * 
 * @author yu.yao 2018年6月23日
 *
 */
public class BulkProcessorTool {
  public static final Logger logger = LoggerFactory.getLogger(BulkProcessorTool.class);

  public static BulkProcessor createBulkProcessor(ClusterConfigBean esConfig,
      ImportConfigBean importConfig) {
    String clusterName = esConfig.getNewClusterName();
    String clusterAddr = esConfig.getNewClusterAddress();

    if (clusterName == null || clusterName.isEmpty()) {
      logger.error("invalid cluster name.");
      return null;
    }
    if (clusterAddr == null || clusterAddr.isEmpty()) {
      logger.error("invalid cluster address.");
      return null;
    }

    // 初始化Bulk处理器
    BulkProcessor bulkProcessor =
        BulkProcessor.builder(ClientTool.getInstance(), new BulkProcessor.Listener() {
          public void beforeBulk(long paramLong, BulkRequest paramBulkRequest) {
          }

          // 执行出错时执行
          public void afterBulk(long paramLong, BulkRequest paramBulkRequest,
              Throwable paramThrowable) {
            paramThrowable.printStackTrace();
          }

          // 执行成功时执行
          public void afterBulk(long paramLong, BulkRequest paramBulkRequest,
              BulkResponse paramBulkResponse) {
            System.out.println("---尝试操作" + paramBulkRequest.numberOfActions() + "条数据成功---");
          }
        }).setBulkActions(importConfig.getBulkActions())
            .setBulkSize(new ByteSizeValue(importConfig.getByteSizeValue(), ByteSizeUnit.MB))
            .setConcurrentRequests(importConfig.getConcurrentRequests())
            .setFlushInterval(TimeValue.timeValueSeconds(importConfig.getFlushInterval()))
            .setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1), 3))// 指数退避
            .build();
    return bulkProcessor;
  }
}
