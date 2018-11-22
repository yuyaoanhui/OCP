package es.data.migration.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.data.migration.util.FileUtil;

/**
 * 导入导出涉及的存档目录管理抽象类
 * 
 * @author yu.yao 2018年6月24日
 *
 */
public abstract class AbstractArchivComp {
  /**
   * 获取所有索引的导出数据存档文件目录
   * 
   * @author yu.yao
   * @param
   * @return
   */
  static List<String> getIndecesDicPaths(List<String> indeces, String dic_path) {
    List<String> indeces_dic_path = new ArrayList<>();
    for (String index : indeces) {
      indeces_dic_path.add(dic_path + index + File.separator);
    }
    return indeces_dic_path;
  }

  /**
   * 获取指定索引的导出数据存档文件目录
   * 
   * @author yu.yao
   * @param
   * @return
   */
  static String getIndexDicPath(String index, String dic_path) {
    return dic_path + index + File.separator;
  }

  /**
   * 找出某个文件夹下最大的文件编号
   * 
   * @author Alex
   * @param
   * @return
   */
  static int getMaxFileNum(String index, String dic_path) {
    List<String> fileNameList = FileUtil.listFilesInDic(dic_path + index + File.separator);
    if (fileNameList.isEmpty()) {
      return 0;
    } else {
      String numStr = fileNameList.get(fileNameList.size() - 1);
      return Integer.parseInt(numStr);
    }
  }
}
