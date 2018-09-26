package com.ocp.rabbit.repository.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ocp.rabbit.repository.tool.algorithm.address.AddrMatch;
import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;

/**
 * 地址分类器
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class AddressClassifier {
  private AddressClassifier addrClassfy;
  // 用来给地址分类
  private AddrMatch addrMatch;
  private static Map<String, String[]> allCourts;
  private static Map<String, List<String>> levelsToCourt;
  private Set<String> courts;
  private static Map<String, String> uniqueStringToStdCourtName;
  // 添加所有的法院映射文件
  private static List<String> txtcourts;
  static {
    txtcourts = FileOperate.readTxtToArrays(FileUtils.loadProperties("court.anhui.data2"), "utf-8");
  }

  private AddressClassifier() {}

  /**
   * 初始化addrMatch
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static AddressClassifier getInstance() {
    AddressClassifier addrClassfy = new AddressClassifier();
    addrClassfy.addrMatch = AddrMatch.getInstance();
    Set<String> set = new HashSet<>();
    addrClassfy.courts = set;
    for (String str : txtcourts) {
      String stdCname = str.substring(0, str.indexOf("\t"));
      set.add(stdCname);
    }
    loadLevelsToCourt();
    return addrClassfy;
  }

  public Set<String> getCourts() {
    return courts;
  }

  public void setAddrClassfy(AddressClassifier addrClassfy) {
    this.addrClassfy = addrClassfy;
  }

  public AddressClassifier getAddrClassfy() {
    return addrClassfy;
  }

  public static String[] getEachLevel(String addr, AddressClassifier addrClassfy) {
    return addrClassfy.addrMatch.getEachLevel(addr);
  }

  private static String[] getEachLevelOfCourts(String addr, AddressClassifier addrClassfy) {
    if (allCourts.containsKey(addr)) {
      return allCourts.get(addr);
    }
    String[] result = new String[4];
    if (null != addr && !"".equals(addr)) {
      result = addrClassfy.addrMatch.getEachLevel(addr);
      allCourts.put(addr, result);
    }
    return result;
  }

  public static String[] getEachLevel(String addr, int[] pos, String[] addrCourt,
      AddressClassifier addrClassfy) {
    return addrClassfy.addrMatch.splitAddr(addr, pos, addrCourt);
  }

  // 根据省、市、县区来确定来映射到标准的法院名称

  /**
   *
   * @return 标准化法院名字 根据相似度选择最接近的法院名字
   */
  public static String convertToStdCourtName(String courtName) {
    AddressClassifier addrClassfy = getInstance();
    String[] levels = getEachLevelOfCourts(courtName, addrClassfy);
    return convertToStdCourtName(levels, courtName);
  }

  /**
   * 计算两个字符串的相似度
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static float calcSimilarity(String str1, String str2) {
    // 计算两个字符串的长度。
    int len1 = str1.length();
    int len2 = str2.length();
    // 建立上面说的数组，比字符长度大一个空间
    int[][] dif = new int[len1 + 1][len2 + 1];
    // 赋初值，步骤B。
    for (int a = 0; a <= len1; a++) {
      dif[a][0] = a;
    }
    for (int a = 0; a <= len2; a++) {
      dif[0][a] = a;
    }
    // 计算两个字符是否一样，计算左上的值
    int temp;
    for (int i = 1; i <= len1; i++) {
      for (int j = 1; j <= len2; j++) {
        if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
          temp = 0;
        } else {
          temp = 1;
        }
        // 取三个值中最小的
        dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1);
      }
    }
    // 取数组右下角的值，同样不同位置代表不同字符串的比较
    // 计算相似度
    return 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
  }

  // 得到最小值
  private static int min(int... is) {
    int min = Integer.MAX_VALUE;
    for (int i : is) {
      if (min > i) {
        min = i;
      }
    }
    return min;
  }

  /**
   *
   * @param level:省、市、县区、镇
   * @return 标准化法院名字
   */
  public static String convertToStdCourtName(String[] level, String courtName) {
    if (null == courtName || "".equals(courtName)) {
      return "";
    }
    if (courtName.contains("九华"))
      return "安徽省九华山风景区法院";
    String key = genMapKey(level);
    List<String> candidates = levelsToCourt.getOrDefault(key, new ArrayList<>());
    if (candidates.size() == 0) {
      return courtName;
    }
    if (candidates.size() == 1) {
      return candidates.get(0);
    }
    boolean[] flag = new boolean[] {false, false};
    if (courtName.contains("中级")) {
      flag[0] = true;
    }
    if (courtName.contains("铁路")) {
      flag[1] = true;
    }
    boolean flag2 = false, flag3 = false;
    if (courtName.contains("高级")) {
      flag2 = true;
    }
    if (courtName.contains("经济")) {
      flag3 = true;
    }
    Set<String> posssibleCandidates1 = new HashSet<>();
    Set<String> posssibleCandidates2 = new HashSet<>();
    for (int i = 0; i < flag.length; i++) {
      boolean f = flag[i];
      for (String candidate : candidates) {
        if (i == 0) {
          if (candidate.contains("中级")) {
            if (f)
              posssibleCandidates1.add(candidate);
          } else {
            if (!f)
              posssibleCandidates1.add(candidate);
          }
        } else {
          if (candidate.contains("铁路")) {
            if (f)
              posssibleCandidates2.add(candidate);
          } else {
            if (!f)
              posssibleCandidates2.add(candidate);
          }
        }
      }
    }
    List<String> list = new ArrayList<>();
    for (String s : posssibleCandidates1) {
      if (flag2) {
        if (!s.contains("高级")) {
          continue;
        }
      } else {
        if (s.contains("高级")) {
          continue;
        }
      }
      if (flag3) {
        if (!s.contains("经济")) {
          continue;
        }
      } else {
        if (s.contains("经济")) {
          continue;
        }
      }
      if (posssibleCandidates2.contains(s)) {
        list.add(s);
      }
    }
    if (list.size() == 0) {
      if (courtName.contains("合肥市人民法院")) {
        return "合肥市中级人民法院";
      }
      list.add(candidates.get(0));
    }
    if (list.size() != 0) {
      for (String s : list)
        if (!s.contains("九华山"))
          return list.get(0);
    }
    // 这里处理没有找到的情况
    String tmp = courtName.replaceAll("安徽省?|市|县|人民|法院", "");
    for (int i = 1; i <= tmp.length(); i++) {
      String c = tmp.substring(0, i);
      if (uniqueStringToStdCourtName.containsKey(c)) {
        return uniqueStringToStdCourtName.get(c);
      }
    }
    return courtName;
  }

  private static void loadLevelsToCourt() {
    Map<String, List<String>> levelsToCourt_temp = new HashMap<>();
    Map<String, String> stringToStdCourtName = new HashMap<>();
    for (String court : txtcourts) {
      String[] tokens = court.split("[\\s\t]+");
      String[] levels = new String[] {tokens[1], tokens[2], tokens[3], tokens[4]};
      String key = genMapKey(levels);
      if (!levelsToCourt_temp.containsKey(key)) {
        levelsToCourt_temp.put(key, new ArrayList<>());
      }
      levelsToCourt_temp.get(key).add(tokens[0]);
      for (int i = 5; i < tokens.length; i++) {
        if (tokens[i].length() == 1) {
          stringToStdCourtName.put(tokens[i], tokens[0]);
        }
      }
    }
    levelsToCourt = levelsToCourt_temp;
    uniqueStringToStdCourtName = stringToStdCourtName;
  }

  private static String genMapKey(String[] levels) {
    String result = "";
    for (String s : levels) {
      if (s == null || s.equals("null"))
        result = result + "_";
      else
        result = result + "_" + s;
    }
    return result;
  }

}
