package es.data.migration.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件工具类
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public class FileUtil {
  public static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

  /**
   * 从文件中指定位置逐行逐批读取数据
   * 
   * @author yu.yao
   * @param
   * @return 下次要读取的position
   */
  public static long readFileByMultiLines(int batchNum, long position, FileChannel fcin,
      ByteBuffer rBuffer, List<String> dataList) {
    byte[] lineByte = new byte[0];
    String encode = "UTF-8";
    int num = 0;
    long nextPosition = position;
    try {
      /*
       * 按固定字节读取，在一次读取中，第一行和最后一行经常是不完整的行; 定义此变量来存储上次的最后一行和这次的第一行的内容并将之连接成完成的一行;
       * 否则会出现汉字被拆分成2个字节，并被提前转换成字符串而乱码的问题.
       */
      byte[] temp = new byte[0];
      while (fcin.read(rBuffer) != -1) {// fcin.read(rBuffer)：从文件管道读取内容到缓冲区(rBuffer)
        if (num == batchNum) {
          return nextPosition;
        }
        // 读取结束后的位置，相当于读取的长度
        int rSize = rBuffer.position();
        // 存放读取的内容的数组
        byte[] bs = new byte[rSize];
        // 将position设回0
        rBuffer.rewind();
        // 从position初始位置开始相对读bs.length个byte,写入bs[0]到bs[bs.length-1]的区域
        rBuffer.get(bs, 0, bs.length);

        rBuffer.clear();

        int startNum = 0;
        int LF = 10;// 换行符
        int CR = 13;// 回车符
        boolean hasLF = false;// 是否有换行符
        for (int i = 0; i < rSize; i++) {
          if (bs[i] == LF) {
            hasLF = true;
            int tempNum = temp.length;
            int lineNum = i - startNum;
            lineByte = new byte[tempNum + lineNum];// 数组大小已经去掉换行符

            System.arraycopy(temp, 0, lineByte, 0, tempNum);// 填充了lineByte[0]~lineByte[tempNum-1]
            temp = new byte[0];
            System.arraycopy(bs, startNum, lineByte, tempNum, lineNum);// 填充lineByte[tempNum]~lineByte[tempNum+lineNum-1]
            String line = new String(lineByte, 0, lineByte.length, encode);// 一行完整的字符串(过滤了换行和回车)
            num++;
            dataList.add(line);
            // 过滤回车符和换行符
            if (i + 1 < rSize && bs[i + 1] == CR) {
              startNum = i + 2;
              nextPosition += lineByte.length + 2;
            } else {
              startNum = i + 1;
              nextPosition += lineByte.length + 1;
            }
          }
        }
        if (hasLF) {
          temp = new byte[bs.length - startNum];
          System.arraycopy(bs, startNum, temp, 0, temp.length);
        } else {// 兼容单次读取的内容不足一行的情况
          byte[] toTemp = new byte[temp.length + bs.length];
          System.arraycopy(temp, 0, toTemp, 0, temp.length);
          System.arraycopy(bs, 0, toTemp, temp.length, bs.length);
          temp = toTemp;
        }
      }
      if (temp != null && temp.length > 0) {// 兼容文件最后一行没有换行的情况
        String line = new String(temp, 0, temp.length, encode);
        dataList.add(line);
        nextPosition += lineByte.length;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return nextPosition;
  }

  /**
   * 遍历某个文件夹下所有文件，返回文件名列表。并按照文件名升序。
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static List<String> listFilesInDic(String dicPath) {
    List<String> fileNameList = new ArrayList<String>();
    File dir = new File(dicPath);
    File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        if (files[i].isFile()) {
          String strFileName = files[i].getName();
          fileNameList.add(strFileName);
        }
      }
    }
    Collections.sort(fileNameList);
    return fileNameList;
  }

}
