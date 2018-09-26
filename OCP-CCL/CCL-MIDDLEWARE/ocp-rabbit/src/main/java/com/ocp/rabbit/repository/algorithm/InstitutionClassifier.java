package com.ocp.rabbit.repository.algorithm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.tool.algorithm.profession.InstitutionSynonym;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

/**
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class InstitutionClassifier {

  List<Pattern> PATTERN_INSTITUTION;
  Map<String, String> STD_CATEGORY;


  private static Map<String, Integer> rankingToLevel = new HashMap<String, Integer>();

  private static Map<Integer, String> levelToRanking = new HashMap<Integer, String>();

  static {
    rankingToLevel.put("省部级正职", 0);
    rankingToLevel.put("省部级副职", 1);
    rankingToLevel.put("厅局级正职", 2);
    rankingToLevel.put("厅局级副职", 3);
    rankingToLevel.put("县处级正职", 4);
    rankingToLevel.put("县处级副职", 5);
    levelToRanking.put(0, "省部级正职");
    levelToRanking.put(1, "省部级副职");
    levelToRanking.put(2, "厅局级正职");
    levelToRanking.put(3, "厅局级副职");
    levelToRanking.put(4, "县处级正职");
    levelToRanking.put(5, "县处级副职");
  }

  private InstitutionClassifier(String filePath, boolean endTag) {
    readSource(filePath, endTag);
  }

  private static InstitutionClassifier ic = new InstitutionClassifier("category.government", false);
  private static InstitutionClassifier ic_ranking =
      new InstitutionClassifier("ranking.administrative", true);

  private synchronized void readSource(String filePath, boolean endTag) {
    InputStream is = FileUtils.loadProperties(filePath);
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    Map<Integer, List<String>> levels = new HashMap<>();
    Map<String, String> stdCategory = new HashMap<>();
    int recentLevel = -1;
    for (String line : lines) {
      if (line.length() <= 1 || Pattern.compile("##[^\\d]").matcher(line).find())
        continue;
      java.util.regex.Matcher matcher = Pattern.compile("##(\\d)").matcher(line);
      if (matcher.find()) {
        Integer level = Integer.valueOf(matcher.group(1));
        levels.put(level, new ArrayList<>());
        recentLevel = level;
        continue;
      }
      if (-1 != recentLevel) {
        List<String> currentList = levels.get(recentLevel);
        String[] tokens = line.split("[\\s\t]+");
        for (int i = 1; i < tokens.length; i++) {
          currentList.add(tokens[i]);
          stdCategory.put(tokens[i], tokens[0]);
        }
      }
    }
    List<Pattern> patterns = new ArrayList<>();

    List<Integer> lint = new ArrayList<Integer>(levels.keySet());
    lint.sort(Comparator.reverseOrder());
    for (int i = 0; i <= lint.get(0); i++) {
      List<String> currentList = levels.get(i);
      if (null == currentList)
        continue;
      currentList.sort(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
          return o2.length() - o1.length();
        }
      });

      String s = "";
      for (String token : currentList) {
        s = s + "|" + token;
      }
      s = "(" + s.substring(1) + ")";
      if (true == endTag)
        s = s + "$";
      Pattern pattern = Pattern.compile(s);
      patterns.add(pattern);
    }
    PATTERN_INSTITUTION = patterns;
    STD_CATEGORY = stdCategory;
  }


  /**
   * 对政府机构分类
   * 
   * @param name
   * @return
   */
  public static String classifyInstitution(String name) {

    for (Pattern pattern : ic.PATTERN_INSTITUTION) {
      java.util.regex.Matcher matcher = pattern.matcher(name);
      if (matcher.find()) {
        return ic.STD_CATEGORY.get(matcher.group());
      }
    }
    return null;
  }

  /**
   * 对行政级别进行分类 考虑的特征主要有： 1.关键词 2. 省市县乡分类
   * 
   * @param name
   * @return
   */
  public static String classifyAdministrativeRanking(String name) {

    // 删除 "原"
    String newName = deleteYuan(name);
    // 删除 括号
    newName = DocumentUtils.dropBrackets(newName);
    // 多个职位，按照标点符号分开
    String[] names = DocumentUtils.splitSentenceByMinimumSeperator(newName);
    List<Integer> rankings = new ArrayList<>();
    for (String token : names) {
      for (Pattern pattern : ic_ranking.PATTERN_INSTITUTION) {
        // 将名字里面的名称统一成一个标准简称
        String newToken = InstitutionSynonym.replaceSynonym(token);

        java.util.regex.Matcher matcher = pattern.matcher(newToken);
        if (matcher.find()) {
          String ranking = ic_ranking.STD_CATEGORY.get(matcher.group());
          if (null != ranking) {
            rankings.add(rankingToLevel.get(ranking));
            break;
          }
        }
      }
    }
    if (rankings.size() == 0)
      return null;
    rankings.sort(Comparator.naturalOrder());
    return levelToRanking.get(rankings.get(0));
  }

  /**
   * 删除职务里面的 “原”
   * 
   * @param sent
   * @return
   */
  private static String deleteYuan(String sent) {
    int i = sent.indexOf("原");
    if (i < 0) {
      return sent;
    } else if (i == 0) {
      if (sent.length() >= 2)
        return sent.substring(1);
    }
    List<Term> lterm = StandardTokenizer.segment(sent);
    String s = "";
    for (Term term : lterm) {
      if ("原".equals(term.word) || "原副".equals(term.word)) {
        continue;
      } else {
        s = s + term.word;
      }
    }
    return s;
  }

  public static void classifyRankingTest() {
    String filePath = "/Users/zhenjia/Desktop/单位分类.txt";
    List<String> lines = new ArrayList<>();
    lines = FileOperate.readTxtToArrays(filePath, "utf-8");
    for (String line : lines) {
      java.util.regex.Matcher matcher = Pattern.compile("txt:(.*)").matcher(line);
      if (matcher.find()) {
        String ctgr = classifyAdministrativeRanking(matcher.group(1));
        if (null != ctgr) {
        }
      }
    }
  }
}
