package com.ocp.rabbit.repository.tool.algorithm.profession;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;

/**
 * 职业信息容器类
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class ProfessionContainer {

  // 读取rules.txt 文件，key值是lastWord, values值是所有可能的 sector,lastWord词语对
  private static Map<String, List<ProfessionKeyWords>> conflictKwtoAllPossibilities;

  // 读取rules.txt 文件，通过 sector,lastWord 词语对，找到对应的职业分类
  private static Map<ProfessionKeyWords, ProfessionLevel> kwsToClasses;

  // 读取synonyms.txt, 得到一个词语的编号
  private static Map<String, Integer> synNames;

  // 读取 synonyms.txt,通过词语编号，得到该编号下的所有同义词
  private static Map<Integer, Set<String>> synNamesInverse;

  // 读取rules.txt 文件，按照 numOfChsToKw 的 key值长度从大到小排序，得到一个列表
  private static List<Integer> allSorttedNums;

  // 读取rules.txt 文件，key值是lastWord的长度，value是该长度下所有可能的lastWord
  private static Map<Integer, Set<String>> numOfChsToKw;

  // 根据自定义的职业分类映射到通达海数据库职业分类
  private static Map<ProfessionLevel, ProfessionLevel> convert2TDH;

  // 通用化的sector 词语
  private static Set<String> generalSectorWords;

  private static ProfessionContainer pc = new ProfessionContainer();

  public static ProfessionContainer getInstance() {
    return pc;
  }

  private ProfessionContainer() {
    try {
      InputStream rulesPath = FileUtils.loadProperties("profession.rules");
      InputStream synNamesPath = FileUtils.loadProperties("profession.synonyms");
      InputStream convertPath = FileUtils.loadProperties("profession.ToTDHmapper");
      InputStream generalSectorWordsPath =
          FileUtils.loadProperties("profession.generalSectorWords");
      genProfessionData(rulesPath, synNamesPath, convertPath, generalSectorWordsPath);
    } catch (Exception e) {
      pc = null;
    }
  }

  public static ProfessionLevel[] genProfessionLevels(String professionDataRaw) {
    ProfessionLevel[] pls = new ProfessionLevel[2];
    pls[0] = getProfessionLevel(professionDataRaw);
    pls[1] = convertClassification(pls[0]);
    return pls;
  }

  public static ProfessionLevel convertClassification(ProfessionLevel pl) {
    if (convert2TDH.containsKey(pl)) {
      return convert2TDH.get(pl);
    } else
      return ProfessionLevel.otherLevelTDH;
  }

  public static ProfessionLevel getProfessionLevel(String professionDataRaw) {


    // 前处理，去除不相关的词语
    String rawProfessionData = professionDataRaw;
    rawProfessionData = rawProfessionData.replaceAll("[\\\"“”]", "");
    rawProfessionData = rawProfessionData.replaceAll("[\\(（].*[\\)）]", "");
    rawProfessionData = rawProfessionData.replaceAll("(至今|至案发|等职|之一)$", "");
    int len = rawProfessionData.length();

    // 所有可能的职业词语集合
    List<ProfessionKeyWords> allPossibleProfessionKws = new ArrayList<>();

    for (int num : allSorttedNums) {
      // 如果长度大于字符串长度，继续
      if (num > len)
        continue;
      // 从末尾截取num个字符串的词语kw
      String kw = rawProfessionData.substring(len - num, len);

      // 初始化,allPossibleWords 代表需要匹配的 lastword 及其同义词集合
      List<String> allPossibleWords = new ArrayList<>(), temp = new ArrayList<>();
      Set<String> tempSet;

      // 如果 kw 在同义词库里面，则找到它所有的同义词
      if (synNames.containsKey(kw)) {
        tempSet = synNamesInverse.get(synNames.get(kw));
        // tempSet.remove(kw);
        temp.addAll(tempSet);
      }

      // 将词语本身及其同义词加入到所有可能的集合里
      allPossibleWords.add(kw);
      allPossibleWords.addAll(temp);

      // 遍历所有的lastWord
      for (String s : allPossibleWords) {

        if (conflictKwtoAllPossibilities.containsKey(s)) {
          // 所有可能的sector,lastWord 组合
          List<ProfessionKeyWords> allKws = conflictKwtoAllPossibilities.get(s);
          ProfessionKeyWords oneKw;
          boolean findOtherKey = false;
          for (ProfessionKeyWords kws : allKws) {
            if (kws.sectorWord.equals("")) {
              oneKw = kws;
              allPossibleProfessionKws.add(oneKw);
              continue;
            }
            Set<String> possibleKws;
            if (synNames.containsKey(kws.sectorWord)) {
              possibleKws = synNamesInverse.get(synNames.get(kws.sectorWord));
            } else {
              possibleKws = new HashSet<>();
              possibleKws.add(kws.sectorWord);
            }
            boolean findNonGeneralSectorWord = false;
            for (String k : possibleKws) {
              if (rawProfessionData.contains(k)) {
                findOtherKey = true;
                allPossibleProfessionKws.add(kws);
                if (kws.sectorWord.length() <= 1 || generalSectorWords.contains(kws.sectorWord)) {
                  continue;
                }
                findNonGeneralSectorWord = true;
                break;
              }
            }
            if (findOtherKey && findNonGeneralSectorWord) {
              break;
            }
          }
          if (allPossibleProfessionKws.size() != 0) {
            int length = allPossibleProfessionKws.size();
            for (ProfessionKeyWords pkw : allPossibleProfessionKws) {
              if (!"".equals(pkw.sectorWord))
                return kwsToClasses.get(pkw);
            }
            return kwsToClasses.get(allPossibleProfessionKws.get(length - 1));
          }
        }
      }
    }
    if (allPossibleProfessionKws.size() != 0) {
      int length = allPossibleProfessionKws.size();
      ProfessionKeyWords pk = allPossibleProfessionKws.get(length - 1);
      if (pk.sectorWord.length() > 1 && !generalSectorWords.contains(pk.sectorWord)
          && kwsToClasses.containsKey(pk)) {
        return kwsToClasses.get(pk);
      } else if (kwsToClasses.containsKey(pk)) {
        return kwsToClasses.get(pk);
      }
    }
    return ProfessionLevel.otherLevel;
  }

  private void genProfessionData(InputStream rulesPath, InputStream synNamesPath,
      InputStream convertPath, InputStream generalSectorWordsPath) {


    Map<String, Set<ProfessionKeyWords>> conflictKwtoAllPossibilities = new HashMap<>();
    Map<ProfessionKeyWords, ProfessionLevel> kwsToClasses = new HashMap<>();
    Map<String, Integer> synNames = new HashMap<>();
    Map<Integer, Set<String>> synNamesInverse = new HashMap<>();
    Map<Integer, Set<String>> numOfChsToKw = new HashMap<>();
    List<Integer> allSorttedNums = new ArrayList<Integer>();
    Map<ProfessionLevel, ProfessionLevel> convert2TDH = new HashMap<>();
    Set<String> generalSectorWords = new HashSet<>();

    List<String> lines = FileOperate.readTxtToArrays(rulesPath, "UTF-8");
    for (String line : lines) {
      String[] ln = line.split("[\\s\t]+");
      if (ln.length < 3)
        continue;
      for (int i = 1; i < ln.length; i++) {

        String l = ln[i];
        if (l.equals(""))
          continue;
        if (!l.contains("，")) {
          ProfessionKeyWords pk = new ProfessionKeyWords(l, "");
          ProfessionLevel pl = new ProfessionLevel(ln[1], ln[0]);
          if (!kwsToClasses.containsKey(pk))
            kwsToClasses.put(pk, pl);
          if (!conflictKwtoAllPossibilities.containsKey(l))
            conflictKwtoAllPossibilities.put(l, new HashSet<>());
          conflictKwtoAllPossibilities.get(l).add(pk);
        } else {
          String[] ls = l.split("，");
          if (ls.length != 2)
            continue;
          ProfessionKeyWords pk = new ProfessionKeyWords(ls[1], ls[0]);
          ProfessionLevel pl = new ProfessionLevel(ln[1], ln[0]);
          if (!kwsToClasses.containsKey(pk))
            kwsToClasses.put(pk, pl);
          if (!conflictKwtoAllPossibilities.containsKey(ls[1]))
            conflictKwtoAllPossibilities.put(ls[1], new HashSet<>());
          conflictKwtoAllPossibilities.get(ls[1]).add(pk);
        }
      }
    }

    lines = FileOperate.readTxtToArrays(synNamesPath, "UTF-8");
    int count = 0;
    for (String line : lines) {
      String[] ln = line.split("[\\s\t]+");
      if (ln.length < 2)
        continue;

      for (String l : ln) {
        if (l.equals(""))
          continue;
        synNames.put(l, count);
        if (!synNamesInverse.containsKey(count))
          synNamesInverse.put(count, new HashSet<>());
        synNamesInverse.get(count).add(l);
      }
      count = count + 1;
    }

    for (ProfessionKeyWords pk : kwsToClasses.keySet()) {
      int len = pk.lastWord.length();
      if (!numOfChsToKw.containsKey(len))
        ;
      numOfChsToKw.put(len, new HashSet<>());
      numOfChsToKw.get(len).add(pk.lastWord);
    }

    allSorttedNums.addAll(numOfChsToKw.keySet());
    allSorttedNums.sort(new CompareLength());

    lines = FileOperate.readTxtToArrays(convertPath, "UTF-8");
    for (String line : lines) {
      String[] ln = line.split("[\\s\t]+");
      if (ln.length < 4)
        continue;
      convert2TDH.put(new ProfessionLevel(ln[3], ln[2]), new ProfessionLevel(ln[1], ln[0]));
    }
    convert2TDH.put(ProfessionLevel.otherLevel, ProfessionLevel.otherLevelTDH);

    lines = FileOperate.readTxtToArrays(generalSectorWordsPath, "UTF-8");
    for (String line : lines) {
      String[] ln = line.split("[\\s\t]+");
      if (ln.length != 1)
        continue;
      generalSectorWords.add(ln[0]);
    }

    ProfessionContainer.conflictKwtoAllPossibilities = new HashMap<>();
    for (String key : conflictKwtoAllPossibilities.keySet()) {
      List<ProfessionKeyWords> list =
          new ArrayList<ProfessionKeyWords>(conflictKwtoAllPossibilities.get(key));
      Collections.sort(list, new Comparator<ProfessionKeyWords>() {
        @Override
        public int compare(ProfessionKeyWords o1, ProfessionKeyWords o2) {
          return o2.sectorWord.length() - o1.sectorWord.length();
        }
      });
      ProfessionContainer.conflictKwtoAllPossibilities.put(key, list);
    }

    ProfessionContainer.kwsToClasses = kwsToClasses;
    ProfessionContainer.synNames = synNames;
    ProfessionContainer.synNamesInverse = synNamesInverse;
    ProfessionContainer.setNumOfChsToKw(numOfChsToKw);
    ProfessionContainer.allSorttedNums = allSorttedNums;
    ProfessionContainer.convert2TDH = convert2TDH;
    ProfessionContainer.generalSectorWords = generalSectorWords;
  }

  public static Map<Integer, Set<String>> getNumOfChsToKw() {
    return numOfChsToKw;
  }

  public static void setNumOfChsToKw(Map<Integer, Set<String>> numOfChsToKw) {
    ProfessionContainer.numOfChsToKw = numOfChsToKw;
  }

  private class CompareLength implements Comparator<Integer> {
    @Override
    public int compare(Integer s1, Integer s2) {
      return s2.intValue() - s1.intValue();
    }
  }

}
