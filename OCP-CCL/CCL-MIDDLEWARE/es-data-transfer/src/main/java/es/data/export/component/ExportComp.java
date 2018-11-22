package es.data.export.component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import es.data.export.bean.ExportConfigBean;
import es.data.export.tool.ClientTool;
import es.data.export.tool.DataHandleTool;

/**
 * <br>
 * 数据导出构件</br>
 * <br>
 * 使用scan+scroll高效获取某个索引的全量数据</br>
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class ExportComp extends AbstractArchivComp {

  /**
   * 批量导出数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  @SuppressWarnings({"resource", "deprecation"})
  public static void exportDataBatch(Map<String, String> indeces,
      Map<String, List<String>> indexTypes, String dic_path, long docSize, long scroll)
      throws IOException {

    long doc_data = 1024 * 1024 * docSize;// 每个文档最大大小,单位字节Byte
    Client client = ClientTool.getInstance();

    /*
     * 建立存数据文档，不放在run方法中是为了线程安全考虑
     */
    for (String index : indeces.keySet()) {
      String indexDicPath = getIndexDicPath(index, dic_path);
      if (!new File(indexDicPath).exists()) {
        new File(indexDicPath).mkdirs();
      }
    }

    CountDownLatch latch = new CountDownLatch(indeces.keySet().size());
    ExecutorService service = Executors.newFixedThreadPool(indeces.keySet().size());
    for (String index : indeces.keySet()) {
      final int size = new ExportConfigBean().getPageSize().get(index);
      service.submit(new Runnable() {
        @Override
        public void run() {
          try {
            String indexDicPath = getIndexDicPath(index, dic_path);
            int maxNum = getMaxFileNum(index, dic_path);
            AtomicInteger file_num = new AtomicInteger(maxNum + 1);
            int i = 1;
            for (String type : indexTypes.get(index)) {// 按照type分类查询
              /*
               * 构建查询条件并获取第一页结果
               */
              QueryBuilder qb = QueryBuilders.matchAllQuery();
              SearchResponse response = client.prepareSearch(indeces.get(index)).setTypes(type)
                  .setSearchType(SearchType.SCAN).setScroll(new TimeValue(scroll, TimeUnit.SECONDS))
                  .setQuery(qb).setSize(size).execute().actionGet();
              new File(indexDicPath + String.valueOf(file_num.get()));
              /*
               * 打开文件通道准备写数据
               */
              FileChannel fc;
              fc = new RandomAccessFile(indexDicPath + String.valueOf(file_num.get()), "rw")
                  .getChannel();
              while (true) {
                if (fc.size() >= doc_data) {// 文档大小已经超过上限
                  fc.close();
                  new File(indexDicPath + file_num.incrementAndGet());
                  fc = new RandomAccessFile(indexDicPath + file_num, "rw").getChannel();
                }
                for (SearchHit hit : response.getHits().getHits()) {
                  String sourceAsString = hit.getSourceAsString();
                  sourceAsString = DataHandleTool.handle(index, sourceAsString, type) + "\n";// 对数据的业务处理
                  long length = fc.size(); // 有来设置映射区域的开始位置
                  byte[] bytes = sourceAsString.getBytes();
                  MappedByteBuffer mbb =
                      fc.map(FileChannel.MapMode.READ_WRITE, length, bytes.length);
                  mbb.put(bytes); // 写入新数据
                }
                System.out.println("已获取索引" + indeces.get(index) + "第" + i + "页数据......");
                i++;
                /*
                 * 通过scrollid来实现深度翻页
                 */
                response = client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(scroll, TimeUnit.SECONDS)).execute().actionGet();
                // 翻页结束
                if (response.getHits().getHits().length == 0) {
                  break;
                }
              }
            }
            System.out.println("索引" + indeces.get(index) + "的所有数据获取完毕。");
            latch.countDown();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      });
    }
    try {
      latch.await(24L, TimeUnit.HOURS);// 若超过24小时未完成任务，立即结束
      client.close();
      System.out.println("导出结束！");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (!service.isShutdown()) {
        service.shutdownNow();
      }
    }
  }

}
