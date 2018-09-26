package com.ocp.rabbit.repository.tool;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.core.type.TypeReference;
import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.entity.HospitalInfo;
import com.ocp.rabbit.repository.tool.algorithm.profession.ProfessionContainer;
import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;
import com.ocp.rabbit.repository.util.JSONUtil;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class ResourceReader {
  private Context context;

  public ResourceReader(Context context) {
    this.context = context;
  }

  private static ResourceReader resourceReader = new ResourceReader();

  // 所有案由，分成5类，{刑事案由，民事案由，行政案由，赔偿案由，执行案由}
  private static final Set<String> generalAnyou = new HashSet<String>();
  // 犯罪名称同义词
  private static final Map<String, String> synNameToStdNameMap = readSynCrimeNames();
  // 数字-字符映射表
  private static final Map<String, Integer> chNumberMap = new HashMap<String, Integer>();
  private ReferLigitantRelatedInfoExtrator referExtractor =
      new ReferLigitantRelatedInfoExtrator(context);
  static {
    generalAnyou.add("民事");
    generalAnyou.add("刑事");
    generalAnyou.add("执行");
    generalAnyou.add("赔偿");
    generalAnyou.add("行政");
    chNumberMap.put("零", 0);
    chNumberMap.put("一", 1);
    chNumberMap.put("1", 1);
    chNumberMap.put("二", 2);
    chNumberMap.put("2", 2);
    chNumberMap.put("两", 2);
    chNumberMap.put("三", 3);
    chNumberMap.put("3", 3);
    chNumberMap.put("四", 4);
    chNumberMap.put("4", 4);
    chNumberMap.put("五", 5);
    chNumberMap.put("5", 5);
    chNumberMap.put("六", 6);
    chNumberMap.put("6", 6);
    chNumberMap.put("七", 7);
    chNumberMap.put("7", 7);
    chNumberMap.put("八", 8);
    chNumberMap.put("8", 8);
    chNumberMap.put("九", 9);
    chNumberMap.put("9", 9);
  }
  // 识别法院名称的正则
  public Pattern patternCourtName;
  // 法院标准名称—地区映射表
  public Map<String, String[]> stdCourt2Area;
  // 职业
  public static ProfessionContainer PROFESSION_CONTAINER = ProfessionContainer.getInstance();

  private ResourceReader() {
    readAnhaoFeature();
  }

  public static ResourceReader getInstance() {
    return resourceReader;
  }

  /**
   * 列举的职业信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Map<Integer, Set<String>> loadProfessionType() {
    try {
      InputStream is = FileUtils.loadProperties("profession.data");
      Map<Integer, Set<String>> sortedProfessionType = new HashMap<>();
      List<String> lines = FileOperate.readTxtToArrays(is, "UTF-8");
      for (String line : lines) {
        String[] thisLine = line.split("\t");
        if (thisLine.length <= 0)
          continue;
        if (!sortedProfessionType.containsKey(thisLine[0].length())) {
          sortedProfessionType.put(thisLine[0].length(), new HashSet<>());
        }
        sortedProfessionType.get(thisLine[0].length()).add(thisLine[0]);
      }
      return sortedProfessionType;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * 民族信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Map<String, String> loadEthnicGroupMapper() {
    InputStream is = FileUtils.loadProperties("nation.mapper");
    String s = FileOperate.readTxt(is, "utf-8");
    Map<String, String> map = JSONUtil.toObject(s, new TypeReference<Map<String, String>>() {});
    return map;
  }

  /**
   * 教育信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Map<String, String> loadEducationMapper() {
    InputStream is = FileUtils.loadProperties("education.mapper");
    String s = FileOperate.readTxt(is, "utf-8");
    Map<String, String> map = JSONUtil.toObject(s, new TypeReference<Map<String, String>>() {});
    return map;
  }

  /**
   * 根据"受教育水平"标准表述(json文件中),构造识别教育水平的正则表达式
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Pattern makeEducationPattern() {
    Map<String, String> educationMapper = loadEducationMapper();
    List<String> list = new ArrayList<>(educationMapper.keySet());
    // 按长度排序
    list.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });
    String s = "";
    for (String str : list) {
      if (s.length() == 0) {
        s = s + str;
      } else {
        s = s + ("|" + str);
      }
    }
    return Pattern.compile(s);
  }

  /**
   * 根据所有国家、地区名字的标准描述(json文件中),构造识别的正则表达式
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static Pattern makeCountryNamePattern() {
    InputStream is = FileUtils.loadProperties("name.country");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    String s = "国外|海外|境外|欧美|中东|欧洲|美洲";
    for (String line : lines) {
      if (line.length() >= 2) {
        s = s + "|" + line;
      }
    }
    return Pattern.compile(s);
  }

  public static Pattern crimesCompile() {
    String s = "(";
    for (String key : synNameToStdNameMap.keySet()) {
      if (s == "(") {
        s = s + key;
      } else {
        s = s + "|" + key;
      }
      // 去掉顿号匹配
      String tmp = key.replaceAll("、", "");
      if (tmp.length() != key.length())
        s = s + "|" + tmp;
    }
    s = s + ")";
    return Pattern.compile(s);
  }

  public static Map<String, String> stdNameMapper() {
    Map<String, String> nameMapper = new HashMap<>();
    for (Map.Entry<String, String> entry : synNameToStdNameMap.entrySet()) {
      String key = entry.getKey();
      nameMapper.put(key, entry.getValue());
      // 去掉顿号
      String tmp = entry.getKey().replaceAll("、", "");
      nameMapper.put(tmp, entry.getValue());
      nameMapper.put(tmp.substring(0, tmp.length() - 1), entry.getValue());
      nameMapper.put(key.substring(0, key.length() - 1), entry.getValue());
    }
    return nameMapper;
  }

  public static Pattern crimesCompileSimple() {
    String s = "(";
    for (String key : synNameToStdNameMap.keySet()) {
      if (s == "(") {
        s = s + key;
      } else {
        s = s + "|" + key.substring(0, key.length() - 1);
      }
      // 去掉顿号匹配
      String tmp = key.replaceAll("、", "");
      if (tmp.length() != key.length())
        s = s + "|" + tmp;
    }
    s = s + ")";
    return Pattern.compile(s);
  }

  ////////////////////////// private helper functions//////////////////
  private static Map<String, String> readCaseTypes() {
    Map<String, String> allCaseTypes = new HashMap<>();
    InputStream is = FileUtils.loadProperties("criminal.cause.data");
    List<String> lines = FileOperate.readTxtToArrays(is, "UTF-8");
    for (String line : lines) {
      String[] cases = line.split("\t");
      if (cases.length < 2)
        continue;
      if (!allCaseTypes.containsKey(cases[1]))
        allCaseTypes.put(cases[1], cases[0]);
    }
    return allCaseTypes;
  }

  /**
   * 获取犯罪名称同义词
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static Map<String, String> readSynCrimeNames() {
    InputStream is = FileUtils.loadProperties("charge.data");
    List<String> lines = FileOperate.readTxtToArrays(is, "UTF-8");
    Map<String, List<List<String>>> synNameMap = new HashMap<>();
    for (String line : lines) {
      line = line.replaceAll("[（）\\(\\)]", "");
      String[] thisLine = line.split("[｜|]");
      if (thisLine.length < 2) {
        continue;
      }
      List<List<String>> thisMergelines = new ArrayList<>();
      int ri = 0;
      for (int i = 1; i < thisLine.length; i++) {
        if (thisLine[i].length() < 1 || thisLine[i].equals("")) {
          continue;
        }
        String[] thisItem = thisLine[i].split("、");
        thisMergelines.add(new ArrayList<>());
        for (String aThisItem : thisItem) {
          if (thisItem.length < 1) {
            continue;
          }
          thisMergelines.get(ri).add(aThisItem);
        }
        ri++;
      }
      synNameMap.put(thisLine[0], thisMergelines);
    }
    Map<String, String> synNameToStdNameMap = new HashMap<>();
    String reg_border = "(.*)(国\\(边\\))(.*)";
    Pattern pattern = Pattern.compile(reg_border);
    Matcher m;
    Map<String, String> allCaseTypes = readCaseTypes();
    for (String name : allCaseTypes.keySet()) {
      if (synNameMap.containsKey(name)) {
        List<String> values = printLoL(synNameMap.get(name));
        for (String value : values) {
          synNameToStdNameMap.put(value, name);
        }
        synNameToStdNameMap.put(name, name);
      } else {
        synNameToStdNameMap.put(name, name);
      }
      m = pattern.matcher(name);
      if (m.find()) {
        String s1 = m.group(1) + "边" + m.group(3);
        String s2 = m.group(1) + "国" + m.group(3);
        String s3 = m.group(1) + "国边" + m.group(3);
        synNameToStdNameMap.put(s1, name);
        synNameToStdNameMap.put(s2, name);
        synNameToStdNameMap.put(s3, name);
      }
    }
    synNameToStdNameMap.put("过失杀人罪", "过失致人死亡罪");
    synNameToStdNameMap.put("流氓罪", "流氓罪");
    synNameToStdNameMap.put("妨碍公务罪", "妨害公务罪");
    synNameToStdNameMap.put("拐卖人口罪", "拐卖妇女、儿童罪");
    synNameToStdNameMap.put("掩饰隐瞒罪", "掩饰、隐瞒犯罪所得、犯罪所得收益罪");
    synNameToStdNameMap.put("隐瞒罪", "掩饰、隐瞒犯罪所得、犯罪所得收益罪");
    synNameToStdNameMap.put("危险方法危害公共安全罪", "以危险方法危害公共安全罪");
    synNameToStdNameMap.put("非法侵入他人住宅罪", "非法侵入住宅罪");
    synNameToStdNameMap.put("组织他人偷越边境罪", "组织他人偷越边境罪");
    synNameToStdNameMap.put("故意损坏公私财物罪", "故意损坏财物罪");
    synNameToStdNameMap.put("协助组织他人卖淫罪", "协助组织卖淫罪");
    synNameToStdNameMap.put("故意损坏财物罪", "故意毁坏财物罪");
    synNameToStdNameMap.put("逃脱罪", "脱逃罪");
    return synNameToStdNameMap;
  }

  private static List<String> printLoL(List<List<String>> lol) {
    if (lol.size() == 1) {
      return lol.get(0);
    } else {
      return mergeTwoLists(lol.get(0), printLoL(lol.subList(1, lol.size())));
    }
  }

  private static List<String> mergeTwoLists(List<String> ls1, List<String> ls2) {
    List<String> ls = new ArrayList<>();
    for (String aLs1 : ls1) {
      for (String aLs2 : ls2) {
        ls.add(aLs1 + aLs2);
      }
    }
    return ls;
  }

  public static int[][] transformChineseNums(String s) {
    int[][] formatted = new int[][] {{0, 0, 0}, {0, 0, 0}};
    if (s == null) {
      return formatted;
    }
    String reg = "(.*)缓刑(.*)", s1 = "", s0 = s;
    String year = "([^年月日]*)年", month = "([^年月日]*)月", day = "([^年月日]*)天";
    String dec = "(.*)十(.*)", num = "(\\d+)";
    String[] date = new String[] {year, month, day};
    Matcher m = Pattern.compile(reg).matcher(s);
    Matcher m_dec, m_num;
    Pattern p_dec = Pattern.compile(dec), p_num = Pattern.compile(num);
    // 判断是否有缓刑
    if (m.find()) {
      s1 = m.group(2);
      s0 = m.group(1);
    }
    String[] sfinal = new String[] {s0, s1};
    int before, after, allnum;
    for (int k = 0; k < 2; k++) {
      // 对主刑和缓刑分别匹配
      if (sfinal.equals("")) {
        continue;
      }
      for (int i = 0; i < 3; i++) {
        // 对年月日分别匹配
        m = Pattern.compile(date[i]).matcher(sfinal[k]);
        if (m.find()) {
          // 判断是否含有阿拉伯数字
          String s_dec = m.group(1);
          m_num = p_num.matcher(s_dec);
          if (m_num.find()) {
            try {
              allnum = Integer.parseInt(m_num.group(1));
            } catch (Exception e) {
              allnum = 0;
            }
            continue;
          }
          // 否则匹配汉字数字
          else {
            // 否则判断十前面的数字和后面的数字
            m_dec = p_dec.matcher(s_dec);
            if (m_dec.find()) {
              String m_dec1 = m_dec.group(1), m_dec2 = m_dec.group(2);
              before = 1;
              after = 0;
              allnum = 0;
              // 如果十位数不是空,默认是1
              if (!m_dec1.equals("")) {
                for (String key : chNumberMap.keySet()) {
                  if (key.equals("零")) {
                    continue;
                  }
                  if (m_dec1.contains(key)) {
                    before = chNumberMap.get(key);
                    break;
                  }
                }
              }
              // 如果个位数不是空，默认是0
              if (!m_dec2.equals("")) {
                for (String key : chNumberMap.keySet()) {
                  if (key.equals("零")) {
                    continue;
                  }
                  if (m_dec2.contains(key)) {
                    after = chNumberMap.get(key);
                    break;
                  }
                }
              }
              allnum = before * 10 + after;
            } else {
              // 没有十这个关键字，直接寻找数字
              allnum = 0;
              for (String key : chNumberMap.keySet()) {
                if (s_dec.contains(key)) {
                  allnum = chNumberMap.get(key);
                  break;
                }
              }
            }
          }
          formatted[k][i] = allnum;
        }
      }
    }
    return formatted;
  }

  public static int[][] transformDates(String s) {
    String reg_dates = "(\\d{2,4})年(?:(\\d{1,2})月)?(?:(\\d{1,2})日)?";
    int[][] dates = new int[][] {{0, 0, 0}, {-1, -1, -1} // -->indicates if inferred;
    };
    if (s == null)
      return dates;
    Matcher m = Pattern.compile(reg_dates).matcher(s);
    if (m.find()) {
      for (int i = 0; i < 3; i++) {
        if (m.group(i + 1) != null && !m.group(i + 1).equals("")) {
          try {
            dates[0][i] = Integer.parseInt(m.group(i + 1));
            dates[1][i] = 0;
          } catch (Exception e) {
            // never reaches here
            dates[0][i] = 0;
            dates[1][i] = 2;
          }
        } else {
          if (i == 1)
            dates[0][i] = 0;
          else
            dates[0][i] = 0;
          dates[1][i] = 1;
        }
      }
      if (dates[0][0] < 2000) {
        if (dates[0][0] < 100) {
          dates[0][0] = dates[0][0] + 1900;
        } else if (dates[0][0] < 20) {
          dates[0][0] = dates[0][0] + 2000;
        }
      }
    }
    return dates;
  }

  public static Set<String> transformCrimes(String s) {
    Set<String> crimes = new HashSet<>();
    if (s == null)
      return crimes;
    String reg_prefix = "^(?:曾|曾经)?(?:因|犯|因犯|因为)(.*)";
    Pattern p_prefix = Pattern.compile(reg_prefix);
    Matcher m = p_prefix.matcher(s);
    String s1 = s;
    if (m.find()) {
      s1 = m.group(1);
    }
    String reg_m = "[（\\(].*[）\\)]";
    s1 = s1.replaceAll(reg_m, "");
    if (s1.length() <= 1)
      return crimes;
    if (!s1.substring(s1.length() - 1).equals("罪")) {
      s1 = s1 + "罪";
    } else if (s1.length() >= 2 && s1.substring(s1.length() - 2).equals("犯罪")) {
      s1 = s1.substring(0, s1.length() - 2) + "罪";
    }
    if (synNameToStdNameMap.containsKey(s1)) {
      crimes.add(synNameToStdNameMap.get(s1));
    } else {
      String[] possibleCrimes = s1.split("(、|罪和|罪及)");
      for (String c : possibleCrimes) {
        if (c.length() <= 1)
          continue;
        String mc = c.substring(c.length() - 1);
        if (!mc.equals("罪"))
          c = c + "罪";
        if (synNameToStdNameMap.containsKey(c))
          crimes.add(synNameToStdNameMap.get(c));
      }
    }
    return crimes;
  }

  public static Map<String, List<String>> readSource() {
    Map<String, Set<String>> ANYOU = new HashMap<>();
    InputStream is = FileUtils.loadProperties("all.anyou");
    List<String> lines = FileOperate.readTxtToArrays(is, "UTF-8");
    for (int i = 1; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.length() <= 7)
        continue;
      String[] tokens = line.split("\t");
      if (tokens[5].length() != 1)
        continue;
      if (!ANYOU.containsKey(tokens[5])) {
        ANYOU.put(tokens[5], new HashSet<>());
      }
      Set<String> set = ANYOU.get(tokens[5]);
      if (generalAnyou.contains(tokens[1]) || tokens[6].contains("2002")
          || tokens[6].contains("2008") || tokens[1].length() == 1 || tokens[1].contains("其他"))
        continue;
      set.add(tokens[1]);
    }
    Map<String, List<String>> ANYOU2 = new HashMap<>();
    if (ANYOU.containsKey("1")) {
      Map<String, String> stdCrimeNameMapper = stdNameMapper();
      List<String> tmp = new ArrayList<String>(stdCrimeNameMapper.keySet());
      tmp.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o2.length() - o1.length();
        }
      });
      ANYOU2.put("刑事案由", tmp);
    }
    if (ANYOU.containsKey("2")) {
      List<String> tmp = new ArrayList<String>(ANYOU.get("2"));
      tmp.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o2.length() - o1.length();
        }
      });
      ANYOU2.put("民事案由", tmp);
    }
    if (ANYOU.containsKey("3")) {
      List<String> tmp = new ArrayList<String>(ANYOU.get("3"));
      tmp.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o2.length() - o1.length();
        }
      });
      ANYOU2.put("行政案由", tmp);
    }
    if (ANYOU.containsKey("4")) {
      List<String> tmp = new ArrayList<String>(ANYOU.get("4"));
      tmp.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o2.length() - o1.length();
        }
      });
      ANYOU2.put("赔偿案由", tmp);
    }
    if (ANYOU.containsKey("5")) {
      List<String> tmp = new ArrayList<String>(ANYOU.get("5"));
      tmp.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o2.length() - o1.length();
        }
      });
      ANYOU2.put("执行案由", tmp);
    }
    return ANYOU2;
  }

  public Map<String, String> anhao2CaseLevel;
  public Map<String, String> anhao2CaseType;
  public Map<String, String> anhao2Pattern;

  private void readAnhaoFeature() {
    InputStream is = FileUtils.loadProperties("anhao.feature");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    anhao2CaseLevel = new HashMap<>();
    anhao2CaseType = new HashMap<>();
    anhao2Pattern = new HashMap<>();

    int n = 0;
    for (String line : lines) {
      if (Pattern.compile("#").matcher(line).find()) {
        continue;
      }
      String[] tokens = line.split("[\\s\t]+");
      if (tokens.length <= 1)
        continue;
      // s = s + "|" + tokens[0];
      anhao2Pattern.put(tokens[0], String.valueOf(n));
      anhao2CaseLevel.put(String.valueOf(n), tokens[1]);
      if (tokens.length >= 3) {
        anhao2CaseType.put(String.valueOf(n), tokens[2]);
      }
      n++;
    }
  }

  public void readCourt(String filePath) {
    if (patternCourtName != null && stdCourt2Area != null) {
      return;
    }
    StringBuilder sb = new StringBuilder("");
    InputStream in = null;
    List<String> lines = null;
    try {
      in = FileUtils.loadProperties(filePath);
      lines = FileOperate.readTxtToArrays(in, "utf-8");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    stdCourt2Area = new HashMap<>();
    for (String line : lines) {
      if (line.contains("#") || line.equals("")) {
        continue;
      }
      String[] strs = line.split("\t");
      if (strs.length < 5) {
        continue;
      }
      String[] oneArea = new String[4];
      for (int i = 1; i < 5; i++) {
        if (strs[i].equals("null") || strs[i].isEmpty()) {
          oneArea[i - 1] = null;
        } else {
          oneArea[i - 1] = strs[i];
        }
      }
      stdCourt2Area.put(strs[0], oneArea);
      sb.append(strs[0]);
      sb.append("|");
    }

    patternCourtName =
        Pattern.compile("(" + sb.toString().substring(0, sb.toString().length() - 1) + ")");
  }

  public static Map<String, String> readCourtNames() {
    InputStream is = FileUtils.loadProperties("names.court.china");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    Map<String, String> name2StdCourtName = new HashMap<>();
    Pattern pattern = Pattern.compile("原名[^\u4e00-\u9fa5]*(.*)");

    for (String line : lines) {
      String[] tokens = line.split("[\\s\t]+");
      if (tokens.length < 5)
        continue;
      name2StdCourtName.put(tokens[2], tokens[2]);
      if (tokens.length >= 6) {
        java.util.regex.Matcher matcher = pattern.matcher(tokens[3]);
        if (matcher.find() && matcher.group(1).contains("法院")) {
          name2StdCourtName.put(matcher.group(1), tokens[2]);
        }
      }
    }
    return name2StdCourtName;
  }

  public static Map<String, String[]> readCourtClassification() {
    InputStream is = FileUtils.loadProperties("classification.court.china");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    Map<String, String[]> courtLevels = new HashMap<>();
    for (String line : lines) {
      String[] tokens = line.split("[\\s\t]+");
      if (tokens.length <= 4)
        continue;
      String[] levels = new String[4];
      for (int i = 1; i < 5; i++) {
        if (tokens[i].equals("null")) {
          levels[i - 1] = null;
        } else
          levels[i - 1] = tokens[i];
      }
      courtLevels.put(tokens[0], levels);
    }
    return courtLevels;
  }

  public Pattern patternDepartmentName;
  public Map<String, String> name2StdDepartmentName;
  public Map<String, String[]> stdDepartment2Atea;

  public void readDepartment(String filePath) {
    if (patternDepartmentName != null || stdDepartment2Atea != null) {
      return;
    }
    StringBuilder sb = new StringBuilder("");
    InputStream is = FileUtils.loadProperties(filePath);
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    stdDepartment2Atea = new HashMap<String, String[]>();
    for (String line : lines) {
      if (line.contains("#") || line.equals("")) {
        continue;
      }
      String[] strs = line.split("\t");
      if (strs.length < 5) {
        continue;
      }
      String[] oneArea = new String[4];
      for (int i = 1; i < 5; i++) {
        if (strs[i].equals("null") || strs[i].isEmpty()) {
          oneArea[i - 1] = null;
        } else {
          oneArea[i - 1] = strs[i];
        }
      }
      stdDepartment2Atea.put(strs[0], oneArea);
      sb.append(strs[0]);
      sb.append("|");
    }
    patternDepartmentName =
        Pattern.compile("(" + sb.toString().substring(0, sb.toString().length() - 1) + ")");

  }

  private static String PATTERN_OBJECT_TYPE;
  private static Map<String, String> OBJECT_TYPES;
  private static String PATTERN_CRIME_PORT;
  private static Map<String, String> CRIME_PORT_TYPES;

  public static void readInfraction() {
    if ((PATTERN_OBJECT_TYPE != null) || (OBJECT_TYPES != null) || (PATTERN_CRIME_PORT != null)
        || (CRIME_PORT_TYPES != null)) {
      return;
    }
    InputStream is = FileUtils.loadProperties("property.related.infraction");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    Map<String, List<String>> objectTypes = new HashMap<>();
    Map<String, String> objectCategory = new HashMap<>();
    Map<String, List<String>> crimeSpotTypes = new HashMap<>();
    Map<String, String> crimeSpotCategory = new HashMap<>();

    String sectionFlag = null;
    for (String line : lines) {
      if (line.length() <= 1) {
        continue;
      }
      Matcher matcher = Pattern.compile("##").matcher(line);
      if (matcher.find()) {
        if (line.contains("侵害物品")) {
          sectionFlag = "侵害物品";
        } else if (line.contains("侵害场所")) {
          sectionFlag = "侵害场所";
        }
        continue;
      }
      String[] tokens = line.split("[\\s\t]+");
      if (tokens.length <= 1) {
        continue;
      }
      List<String> currentKeyWordList = Arrays.asList(tokens);
      if ("侵害物品".equals(sectionFlag)) {
        objectTypes.put(tokens[0], currentKeyWordList);
        for (String token : currentKeyWordList) {
          objectCategory.put(token, tokens[0]);
        }
      } else if ("侵害场所".equals(sectionFlag)) {
        crimeSpotTypes.put(tokens[0], currentKeyWordList);
        for (String token : tokens) {
          crimeSpotCategory.put(token, tokens[0]);
        }
      }
    }

    String temps = "";
    for (String token : objectCategory.keySet()) {
      temps = temps + "|" + token;
    }
    PATTERN_OBJECT_TYPE = "(" + temps.substring(1) + ")";
    OBJECT_TYPES = objectCategory;

    temps = "";
    for (String token : crimeSpotCategory.keySet()) {
      temps = temps + "|" + token;
    }
    PATTERN_CRIME_PORT = "(" + temps.substring(1) + ")";
    CRIME_PORT_TYPES = crimeSpotCategory;
  }

  public Map<String, List<String>> classifyCrimeSpot(List<String> sbParagraph, String infoName,
      List<String> nameList) {
    Pattern objectPattern = Pattern.compile(PATTERN_CRIME_PORT);
    Map<String, List<String>> rsltMap = new HashMap<>();
    for (String sentence : sbParagraph) {
      Matcher matcher = objectPattern.matcher(sentence);
      while (matcher.find()) {
        rsltMap = referExtractor.extractStringlistInfo(sbParagraph, infoName, nameList,
            PATTERN_CRIME_PORT, CRIME_PORT_TYPES.get(matcher.group()), "", 0, 1, 1);
      }
    }
    return rsltMap;
  }

  public Pattern AnhuiProPattern;
  public List<String> AnhuiProList;
  public Pattern AnhuiPattern;
  public List<String> AnhuiList;
  public Map<String, String> anhuiProMap;

  public void readInAnhuiProcurator(String filePath) {
    if (AnhuiProPattern != null || AnhuiPattern != null) {
      return;
    }
    List<String> procurator = new ArrayList<>();
    List<String> proator = new ArrayList<>();
    Map<String, String> proMap = new HashMap<>();
    InputStream is = FileUtils.loadProperties(filePath);
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    for (String line : lines) {
      String[] tokens = line.split("[\\s\t]+");
      procurator.add(tokens[0]);
      if (tokens.length > 1) {
        for (int i = 1; i < tokens.length; i++) {
          proMap.put(tokens[i], tokens[0]);
          proator.add(tokens[i]);
        }
      }
    }
    StringBuilder sb1 = new StringBuilder("");
    for (String s : procurator) {
      sb1.append("|");
      sb1.append(s);
    }
    StringBuilder sb2 = new StringBuilder("");
    for (String s : proator) {
      sb2.append("|");
      sb2.append(s);
    }
    AnhuiProPattern = Pattern.compile(sb1.toString().substring(1));
    AnhuiPattern = Pattern.compile(sb2.toString().substring(1));
    AnhuiProList = procurator;
    AnhuiList = proator;
    anhuiProMap = proMap;
  }

  public Map<String, List<String>> classifyInfractedObject(List<String> sbParagraph,
      String infoName, List<String> nameList) {
    Pattern objectPattern = Pattern.compile(PATTERN_OBJECT_TYPE);
    Map<String, List<String>> rsltMap = new HashMap<>();
    for (String sentence : sbParagraph) {
      Matcher matcher = objectPattern.matcher(sentence);
      while (matcher.find()) {
        rsltMap = referExtractor.extractStringlistInfo(sbParagraph, infoName, nameList,
            PATTERN_OBJECT_TYPE, OBJECT_TYPES.get(matcher.group()), "", 0, 0, 1);
      }
    }
    return rsltMap;
  }

  // 读取医院资源信息
  public static List<HospitalInfo> readExcel(String filePath) {
    String fileType = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length());
    List<HospitalInfo> list = new ArrayList<HospitalInfo>();
    try {
      InputStream stream =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
      Workbook wb = null;
      if (fileType.equals("xls")) {
        wb = new HSSFWorkbook(stream);
      } else if (fileType.equals("xlsx")) {
        wb = new XSSFWorkbook(stream);
      } else {
        System.out.println("您输入的excel格式不正确");
      }

      Sheet sheet1 = wb.getSheetAt(0);
      int r = 0;
      for (Row row : sheet1) {
        if (r == 0) {
          r++;
          continue;
        }
        HospitalInfo hos = new HospitalInfo();
        for (Cell cell : row) {
          cell.setCellType(Cell.CELL_TYPE_STRING);// 设置cell格式，避免报解析错误
          if (!cell.getStringCellValue().equals("")) {
            if (cell.getColumnIndex() == 0) {
              hos.setName(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 1) {
              hos.setAlias(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 2) {
              hos.setAddress(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 3) {
              hos.setPhoneNum(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 4) {
              hos.setLevel(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 5) {
              hos.setDepartments(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 6) {
              hos.setOperation(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 7) {
              hos.setFax(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 8) {
              hos.setMail(cell.getStringCellValue());
            } else if (cell.getColumnIndex() == 9) {
              hos.setWebsite(cell.getStringCellValue());
            }
          }
        }
        r++;
        list.add(hos);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static Pattern readAnyou(String patternStr) {
    InputStream is = FileUtils.loadProperties("all.anyou");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    List<String> anyou = new ArrayList<>();

    for (String line : lines) {
      Pattern pattern = Pattern.compile(patternStr);
      Matcher matcher = pattern.matcher(line);
      if (matcher.find()) {
        anyou.add(matcher.group(1).trim());
      }
    }
    String anyouPattern = "";
    for (String anyouStr : anyou)
      anyouPattern = anyouPattern + (anyouStr + "|");
    anyouPattern = anyouPattern.substring(0, anyouPattern.length() - 1);
    Pattern pattern = Pattern.compile("(" + anyouPattern + ")");
    return pattern;
  }

  // 法律法规信息
  public static Map<String, String> readLawNameMapper() {
    Map<String, String> rsltMap;
    InputStream is = FileUtils.loadProperties("law.name.mapper");
    String s = FileOperate.readTxt(is, "utf-8");
    rsltMap = JSONUtil.toObject(s, new TypeReference<Map<String, String>>() {});
    return rsltMap;
  }

  // date相关资源
  public static String timeExp =
      FileUtils.readToString(FileUtils.loadProperties("ml.model.timeExpressionPattern")).trim();

}
