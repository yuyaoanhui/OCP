package es.data.migration.component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.data.migration.bean.ClusterConfigBean;
import es.data.migration.bean.ImportConfigBean;
import es.data.migration.tool.BulkProcessorTool;
import es.data.migration.util.FileUtil;
import es.data.migration.util.JSONUtil;

/**
 * 数据导入构件
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class ImportComp extends AbstractArchivComp {
  public static final Logger logger = LoggerFactory.getLogger(ImportComp.class);

  /**
   * 批量导入数据
   * 
   * @author yu.yao
   * @param indeces : <索引，索引名>
   * @param dic_path : 存档目录(索引文件夹父级)
   * @return
   */
  @SuppressWarnings({"resource", "unchecked"})
  public static void importDataBatch(Map<String, String> indeces, Map<String, String> types,
      String dic_path, int size) {
    BulkProcessor proc =
        BulkProcessorTool.createBulkProcessor(new ClusterConfigBean(), new ImportConfigBean());

    CountDownLatch latch = new CountDownLatch(indeces.keySet().size());
    ExecutorService service = Executors.newFixedThreadPool(indeces.keySet().size());

    for (String index : indeces.keySet()) {
      service.submit(new Runnable() {
        @Override
        public void run() {
          String type = types.get(index);
          FileChannel fcin = null;
          try {
            String indexDicPath = getIndexDicPath(index, dic_path);
            List<String> fileNameList = FileUtil.listFilesInDic(indexDicPath);
            for (String fileName : fileNameList) {// 逐个文件的读
              fcin = new RandomAccessFile(indexDicPath + fileName, "r").getChannel();// 要读取的文件
              ByteBuffer rBuffer = ByteBuffer.allocate(1024 * 200);// 一条数据的缓冲大小:200KB
              List<String> dataList = new ArrayList<String>(size);// 每次读size条数据
              long nextPosition = 0L;
              nextPosition =
                  FileUtil.readFileByMultiLines(size, nextPosition, fcin, rBuffer, dataList);
              while (!dataList.isEmpty()) {
                for (String source : dataList) {
                  Map<String, Object> data = JSONUtil.toObject(source, Map.class);
                  proc.add(new IndexRequest(indeces.get(index), type).source(data));// 执行bulkProcessor将读出的数据批量写入ES
                }
                dataList.clear();
                nextPosition =
                    FileUtil.readFileByMultiLines(size, nextPosition, fcin, rBuffer, dataList);
              }
              fcin.close();
            }
          } catch (FileNotFoundException e) {
            logger.error("文件未找到！！！");
            e.printStackTrace();
          } catch (IOException e) {
            logger.error("文件通道关闭失败!!!");
            e.printStackTrace();
          } finally {
            latch.countDown();
            if (fcin.isOpen()) {
              try {
                fcin.close();
              } catch (IOException e) {
                logger.error("文件通道关闭失败!!!");
                e.printStackTrace();
              }
            }
          }
        }
      });
    }
    try {
      latch.await(72L, TimeUnit.HOURS);// 若超过72小时未完成任务，立即结束
      proc.flush();
      proc.close();
      System.out.println("------------所有数据导入成功-----------");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (!service.isShutdown()) {
        service.shutdownNow();
      }
    }
  }

}
