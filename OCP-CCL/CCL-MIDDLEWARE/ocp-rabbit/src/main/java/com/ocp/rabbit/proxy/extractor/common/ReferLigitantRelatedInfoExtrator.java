package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.joda.time.DateTime;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.ExtractPositionRecorder;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantStruct;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 抽取主语可能是指代人物的诉讼参与人的相关信息
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class ReferLigitantRelatedInfoExtrator {

  private Context context;
  private ExtractPositionRecorder posRecodExtractor;
  private static Pattern countryNamePattern = ResourceReader.makeCountryNamePattern();

  public ReferLigitantRelatedInfoExtrator(Context context) {
    this.context = context;
    posRecodExtractor = new ExtractPositionRecorder(context);
  }


  private final static String SPLITER = "#";
  private final static String CAPTURE_MODE = "捕获模式";
  private final static String POSTIVE_PATTERN = "正向正则";
  private final static String NEGTIVE_PATTERN = "反向正则";
  private static NumberRecognizer moneyNr = new NumberRecognizer(new String[] {"元", "圆"});
  private static NumberRecognizer timeNr =
      new NumberRecognizer(new String[] {"个月", "年", "日", "天", "小时"});
  private static NumberRecognizer otherNr = new NumberRecognizer(
      new String[] {"人", "次", "名", "克", "张", "部", "个", "mg", "ｍｇ", "毫克", "㎎", "MG", "起", "尊", "桩",
          "件", "户", "多次", "km", "公里", "千米", "%", "％", "kg", "KG", "公斤", "吨", "位"});

  @SuppressWarnings("unchecked")
  public LitigantRecognizer buildLitigantRecognizer() {
    LitigantRecognizer lr;
    List<LitigantStruct> litigants = new ArrayList<>();
    if (this.context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_suspect[InfoPointKey.mode]) == null) {
      List<String> defs = (List<String>) this.context.rabbitInfo.getExtractInfo()
          .getOrDefault(InfoPointKey.meta_defendant_names[InfoPointKey.mode], new ArrayList<>());
      litigants.add(new LitigantStruct("被告", false, defs));
      List<String> plts = (List<String>) this.context.rabbitInfo.getExtractInfo()
          .getOrDefault(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode], new ArrayList<>());
      litigants.add(new LitigantStruct("原告", false, plts));
      litigants.add(new LitigantStruct("被告", true, Pattern.compile("被告人?|被上诉人?|被申诉人?|被申请人?")));
      litigants.add(new LitigantStruct("原告", true, Pattern.compile("原告人?|上诉人|申诉人|申请人|申请再审人?")));
      litigants.add(new LitigantStruct("原被告", true, Pattern.compile("原、被告")));
      lr = new LitigantRecognizer(litigants, defs, plts);
    } else {
      List<String> suspects = (List<String>) this.context.rabbitInfo.getExtractInfo()
          .getOrDefault(InfoPointKey.meta_suspect[InfoPointKey.mode], new ArrayList<>());
      litigants.add(new LitigantStruct("嫌疑人", false, suspects));
      litigants.add(new LitigantStruct("嫌疑人", true, Pattern.compile("嫌疑人?")));
      lr = new LitigantRecognizer(litigants, suspects);
    }
    return lr;
  }

  // 抽取取值为boolean类型的人物的属性
  public Map<String, Boolean> extractBooleanInfo(List<String> parapraphList, String infoKey,
      List<String> nameList, String positivePatternStr, String negativePatternStr,
      int limitSameShortSentFlg, int orderFlg, int allLitigantFlg) {
    LitigantRecognizer lr = buildLitigantRecognizer();
    Map<String, Pattern[]> patternMap = buildPattern(positivePatternStr, negativePatternStr);
    Map<String, Object> objectMap =
        parsePeopleAttr(parapraphList, infoKey, lr, true, nameList, patternMap.get(POSTIVE_PATTERN),
            patternMap.get(NEGTIVE_PATTERN), limitSameShortSentFlg, orderFlg, allLitigantFlg);
    Map<String, Boolean> rsltMap = getPeopleAttrValue(objectMap);
    return rsltMap;
  }

  // 抽取取值为double的人物的属性
  public Map<String, Double> extractDoubleInfo(List<String> parapraphList, String infoKey,
      List<String> nameList, String positivePatternStr, String negativePatternStr,
      int limitSameShortSentFlg, int orderFlg, int allLitigantFlg, String outputUnit,
      String valueType) {
    Map<String, Double> doubleMap = new HashMap<>();

    LitigantRecognizer lr = buildLitigantRecognizer();
    Map<String, Pattern[]> patternMap = buildPattern(positivePatternStr, negativePatternStr);
    Map<String, Object> objectMap = parsePeopleAttr(parapraphList, infoKey, lr, "String", nameList,
        patternMap.get(POSTIVE_PATTERN), patternMap.get(NEGTIVE_PATTERN), limitSameShortSentFlg,
        orderFlg, allLitigantFlg);
    switch (valueType) {
      case "金额":
        doubleMap = getPeopleAttrDoubleValue(objectMap, moneyNr, outputUnit);
        break;
      case "时间":
        doubleMap = getPeopleAttrDoubleValue(objectMap, timeNr, outputUnit);
        break;
      case "其他":
        doubleMap = getPeopleAttrDoubleValue(objectMap, otherNr, outputUnit);
        if (outputUnit.equals("人") && doubleMap.size() == 0 && objectMap.size() > 0) {
          doubleMap = parsePeopleAttrStatistics(objectMap, patternMap.get(POSTIVE_PATTERN));
        } else if (outputUnit.equals("次") && doubleMap.size() == 0) {
          doubleMap = parsePeopleAttrDegree();
        }
        break;
    }
    return doubleMap;
  }

  // 抽取取值为多个string(比如：["孤寡老人"、"残障人士"],...)的人物的属性
  public Map<String, List<String>> extractStringlistInfo(List<String> parapraphList, String infoKey,
      List<String> nameList, String positivePatternStr, String valModeStr,
      String negativePatternStr, int limitSameShortSentFlg, int orderFlg, int allLitigantFlg) {
    LitigantRecognizer lr = buildLitigantRecognizer();
    Map<String, Object> patternMap =
        buildPattern(valModeStr, positivePatternStr, negativePatternStr);
    Pattern[] positivePatterns = (Pattern[]) (patternMap.get(POSTIVE_PATTERN));
    String[] valModeStrList = (String[]) (patternMap.get(CAPTURE_MODE));
    Pattern[] negativePatterns = (Pattern[]) (patternMap.get(NEGTIVE_PATTERN));
    Map<Pattern, String> patt2valmModeStrMap = new HashMap<>();
    for (int i = 0; i < valModeStrList.length; i++) {
      patt2valmModeStrMap.put(positivePatterns[i], valModeStrList[i]);
    }

    Map<String, List<Object>> listMap = parsePeopleAttrList(parapraphList, infoKey, lr, nameList,
        patt2valmModeStrMap, negativePatterns, limitSameShortSentFlg, orderFlg, allLitigantFlg);

    return getPeopleAttrValue(listMap);
  }

  // 抽取取值为多个句子的人物的属性
  public Map<String, List<String>> extractSentenceListInfo(List<String> parapraphList,
      List<String> nameList, String positivePatternStr, String negativePatternStr,
      int limitSameShortSentFlg, int orderFlg, int allLitigantFlg, int rangeFlg) {
    LitigantRecognizer lr = buildLitigantRecognizer();
    Map<String, Pattern[]> patternMap = buildPattern(positivePatternStr, negativePatternStr);

    Map<String, List<Object>> objectMap = parsePeopleAttrSentence(parapraphList, lr, nameList,
        patternMap.get(POSTIVE_PATTERN), patternMap.get(NEGTIVE_PATTERN), limitSameShortSentFlg,
        orderFlg, allLitigantFlg, rangeFlg);

    Map<String, List<String>> sentenceListMap = getPeopleAttrValue(objectMap);

    return sentenceListMap;
  }

  // 抽取引诱、容留、介绍卖淫罪人数
  private final static Pattern ageWord = Pattern
      .compile("([\\u4e00-\\u9fae\\da-zA-Z]*?)[（(]([^未不已,，；;\\.。]*?[\\d一二三四五六七八九十]+周?岁)[)）]");
  private final static Pattern birthDay = Pattern
      .compile("([\\u4e00-\\u9fae\\da-zA-Z]*?)[（(].*?([\\d]+年(([\\d]+月)?([\\d]+日)?)?)出?.生*?[)）]");
  private final static Pattern teen =
      Pattern.compile("([\\u4e00-\\u9fae\\da-zA-Z]*?)[(（]已满(十四|14)周岁[未不]满(十八|18)周?岁[)）]");
  private final static Pattern young =
      Pattern.compile("([\\u4e00-\\u9fae\\da-zA-Z]*?)[(（][未不]满(十四|14)周?岁[)）]");
  private final static Pattern keyWord1 =
      Pattern.compile("未成年|(?<![(（])已满(十四|14)周岁[未不]满(十八|18)周?岁(?![)）])");
  private final static Pattern keyWord2 = Pattern.compile("幼女|(?<![(（])[未不]满(十四|14)周?岁(?![)）])");
  private static NumberRecognizer pr = new NumberRecognizer(new String[] {"周岁", "岁"});

  public void extractPeopleNum(List<String> parapraphList, List<String> nameList,
      String positivePatternStr, String negativePatternStr, int limitSameShortSentFlg, int orderFlg,
      Map<String, People> name2People) {
    LitigantRecognizer lr = buildLitigantRecognizer();
    // 已满十八周岁人名集合
    Map<String, Set<String>> adultMap = new HashMap<>();
    // 十四至十八周岁人名集合
    Set<String> teenNames = new HashSet<String>();
    Map<String, Set<String>> teenMap = new HashMap<>();
    // 未满十四周岁人名集合
    Set<String> youngNames = new HashSet<String>();
    Map<String, Set<String>> youngMap = new HashMap<>();
    Matcher matcher;
    Pattern negativePatterns = null;
    if (!TextUtils.isEmpty(negativePatternStr)) {
      negativePatterns = Pattern.compile(negativePatternStr);
    }
    Pattern positivePatterns = Pattern.compile(positivePatternStr);
    for (String paragraph : parapraphList) {
      NamedEntity[] dates = NamedEntityRecognizer.recognizeTime(paragraph);
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (int i = 0; i < sentences.length; i++) {
        String sentence = sentences[i];
        String lastSentence = null;
        if (i > 0) {
          lastSentence = sentences[i - 1];
        }
        // 标点的位置
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 找到描述十四至十八周岁和未满十四周岁人名的关键词
        String[] sentces = sentence.split("[,，]");
        for (String s : sentces) {
          NamedEntity[] nes_keyWord1 = NamedEntityRecognizer.recognizeEntityByRegex(s, keyWord1);
          NamedEntity[] nes_keyWord2 = NamedEntityRecognizer.recognizeEntityByRegex(s, keyWord2);
          // 查找十四至十八周岁人名集合
          if (nes_keyWord1.length > 0) {
            // 只有十四至十八周岁的人名
            if (nes_keyWord2.length == 0) {
              String sent = s.substring(0, nes_keyWord1[0].getOffset());
              NamedEntity[] nes_litigants = lr.recognize(sent);
              for (NamedEntity ne : nes_litigants) {
                sent = sent.replaceAll(ne.getSource(), "");
              }
              List<Term> lterm = NlpAnalysis.parse(sent).getTerms();
              for (Term term : lterm) {
                if (term.getNatureStr().equals("nr")) {
                  if (!nameList.contains(term.getName())) {
                    teenNames.add(term.getName());
                  }
                }
              }
            } else {
              // 句中有未满十四周岁的人名
              List<NamedEntity[]> nes_litigant_action = NamedEntityRecognizer
                  .entityMatchWithLRorder(nes_keyWord2, nes_keyWord1, commas, false);
              // 未满十四周岁的人名在前
              if (nes_litigant_action.size() > 0) {
                int a = 0;
                for (int j = 1; j < nes_litigant_action.size(); j++) {
                  for (int k = 0; k < nes_litigant_action.size() - 1; k++) {
                    if (nes_litigant_action.get(k)[0]
                        .getOffset() > nes_litigant_action.get(k + 1)[0].getOffset()) {
                      a = k;
                    }
                  }
                }
                String sent = s.substring(nes_litigant_action.get(a)[0].getOffset(),
                    nes_litigant_action.get(a)[1].getOffset());
                List<Term> lterm = NlpAnalysis.parse(sent).getTerms();
                for (Term term : lterm) {
                  if (term.getNatureStr().equals("nr")) {
                    if (!nameList.contains(term.getName()))
                      teenNames.add(term.getName());
                  }
                }
              } else {// 未满十四周岁的人名在后
                String sent = s.substring(0, nes_keyWord1[0].getOffset());
                NamedEntity[] nes_litigants = lr.recognize(sent);
                for (NamedEntity ne : nes_litigants) {
                  sent = sent.replaceAll(ne.getSource(), "");
                }
                List<Term> lterm = NlpAnalysis.parse(sent).getTerms();
                for (Term term : lterm) {
                  if (term.getNatureStr().equals("nr")) {
                    if (!nameList.contains(term.getName()))
                      teenNames.add(term.getName());
                  }
                }
              }
            }
          }
          // 未满十四周岁人名集合
          if (nes_keyWord2.length > 0) {
            // 只有未满十四周岁的人名
            if (nes_keyWord2.length == 0) {
              String sent = s.substring(0, nes_keyWord2[0].getOffset());
              NamedEntity[] nes_litigants = lr.recognize(sent);
              for (NamedEntity ne : nes_litigants) {
                sent = sent.replaceAll(ne.getSource(), "");
              }
              List<Term> lterm = NlpAnalysis.parse(sent).getTerms();
              for (Term term : lterm) {
                if (term.getNatureStr().equals("nr")) {
                  if (!nameList.contains(term.getName()))
                    youngNames.add(term.getName());
                }
              }
            } else {
              // 句中有十四至十八周岁的人名
              List<NamedEntity[]> nes_litigant_action = NamedEntityRecognizer
                  .entityMatchWithLRorder(nes_keyWord1, nes_keyWord2, commas, false);
              // 十四至十八周岁的人名在前
              if (nes_litigant_action.size() > 0) {
                int a = 0;
                for (int j = 1; j < nes_litigant_action.size(); j++) {
                  for (int k = 0; k < nes_litigant_action.size() - 1; k++) {
                    if (nes_litigant_action.get(k)[0]
                        .getOffset() > nes_litigant_action.get(k + 1)[0].getOffset()) {
                      a = k;
                    }
                  }
                }
                String sent = s.substring(nes_litigant_action.get(a)[0].getOffset(),
                    nes_litigant_action.get(a)[1].getOffset());
                List<Term> lterm = NlpAnalysis.parse(sent).getTerms();
                for (Term term : lterm) {
                  if (term.getNatureStr().equals("nr")) {
                    if (!nameList.contains(term.getName()))
                      youngNames.add(term.getName());
                  }
                }
              } else {
                // 十四至十八周岁的人名在后
                String sent = s.substring(0, nes_keyWord2[0].getOffset());
                NamedEntity[] nes_litigants = lr.recognize(sent);
                for (NamedEntity ne : nes_litigants) {
                  sent = sent.replaceAll(ne.getSource(), "");
                }
                List<Term> lterm = NlpAnalysis.parse(sent).getTerms();
                for (Term term : lterm) {
                  if (term.getNatureStr().equals("nr")) {
                    if (!nameList.contains(term.getName()))
                      youngNames.add(term.getName());
                  }
                }
              }
            }
          }
        }
        // 负向正则表达式是否存在匹配结果
        if (negativePatterns != null) {
          NamedEntity[] nes_negationPatterns =
              NamedEntityRecognizer.recognizeEntityByRegex(sentence, negativePatterns);
          if (nes_negationPatterns.length != 0) {
            continue;
          }
        }
        // 正向正则表达式是否存在匹配结果
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, positivePatterns);
        if (nes_action.length == 0) {
          continue;
        }
        // 得到被告人的位置
        NamedEntity[] nes_litigants;
        String caseHierarchy = (String) context.rabbitInfo.getExtractInfo()
            .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode]);
        if (caseHierarchy != null && caseHierarchy.equals("一审")) {
          nes_litigants = NamedEntityRecognizer.recognizeLitigantByType(sentence,
              LitigantUnit.LABEL_DEFENDANT, lr);
          if (nes_litigants.length == 0) {
            if (lastSentence != null) {
              nes_litigants = NamedEntityRecognizer.recognizeLitigantByType(lastSentence,
                  LitigantUnit.LABEL_DEFENDANT, lr);
              if (nes_litigants.length == 0) {
                continue;
              } else {
                for (NamedEntity ne : nes_litigants) {
                  ne.setOffset(-100);
                }
              }
            } else {
              continue;
            }
          }
        } else {
          nes_litigants = lr.recognize(sentence);
          if (nes_litigants.length == 0) {
            if (lastSentence != null) {
              nes_litigants = lr.recognize(lastSentence);
              if (nes_litigants.length == 0) {
                continue;
              } else {
                for (NamedEntity ne : nes_litigants) {
                  ne.setOffset(-100);
                }
              }
            } else {
              continue;
            }
          }
        }
        // 找到组合
        List<NamedEntity[]> nes_litigant_action =
            combineEntitie(nes_litigants, nes_action, commas, limitSameShortSentFlg, orderFlg);
        if (nes_litigant_action.size() == 0) {
          continue;
        }
        for (NamedEntity[] nes : nes_litigant_action) {
          String sent = nes[1].getSource();
          LitigantUnit lu = (LitigantUnit) (nes[0].getInfo());
          String[] names = lu.getNames();
          for (String name : names) {
            // 已满十八周岁人名集合
            Set<String> adultNames = new HashSet<String>();
            // 十四至十八周岁人名集合
            Set<String> teens = new HashSet<String>();
            // 未满十四周岁人名集合
            Set<String> youngs = new HashSet<String>();
            String[] sents = sent.split("、");
            for (String s : sents) {
              List<Term> lterm = NlpAnalysis.parse(s).getTerms();
              for (Term term : lterm) {
                if (term.getNatureStr().equals("nr") || term.getNatureStr().equals("nw")) {
                  adultNames.add(term.getName());
                }
              }
              matcher = ageWord.matcher(s);
              if (matcher.find()) {
                String prostitution = matcher.group(1);
                String source = matcher.group(2);
                List<WrapNumberFormat> wnfs = pr.getNumbers(source, true);
                if (wnfs.size() > 0) {
                  double age = wnfs.get(0).getArabicNumber();
                  if (age <= 14) {
                    youngs.add(prostitution);
                  } else if (age > 14 && age <= 18) {
                    teens.add(prostitution);
                  }
                }
              }
              matcher = birthDay.matcher(s);
              if (matcher.find()) {
                String prostitution = matcher.group(1);
                String source = matcher.group(2);
                NamedEntity[] birthDate = NamedEntityRecognizer.recognizeTime(source);
                if (dates.length > 0 && birthDate.length > 0) {
                  DateTime dateTime = DateHandler.makeDateTime(dates[0].getSource());
                  DateTime birthTime = DateHandler.makeDateTime(birthDate[0].getSource());
                  try {
                    int age = DateHandler.getYearDiff(birthTime, dateTime);
                    if (age <= 14) {
                      youngs.add(prostitution);
                    } else if (age <= 18) {
                      teens.add(prostitution);
                    }
                  } catch (Exception e) {
                  }
                }
              }
              matcher = teen.matcher(s);
              if (matcher.find()) {
                String prostitution = matcher.group(1);
                teens.add(prostitution);
              }
              matcher = young.matcher(s);
              if (matcher.find()) {
                String prostitution = matcher.group(1);
                youngs.add(prostitution);
              }
            }
            if (adultNames.size() > 0) {
              adultMap.put(name, adultNames);
            }
            if (teens.size() > 0) {
              teenMap.put(name, teens);
            }
            if (youngs.size() > 0) {
              youngMap.put(name, youngs);
            }
          }
        }
      }
    }
    // 十四至十八周岁人数
    if (teenMap.size() > 0) {
      if (teenNames.size() > 0) {
        for (Map.Entry<String, Set<String>> entry : teenMap.entrySet()) {
          entry.getValue().addAll(teenNames);
        }
      }
      for (Map.Entry<String, Set<String>> entry : teenMap.entrySet()) {
        if (name2People.containsKey(entry.getKey())) {
          if (name2People.containsKey(entry.getKey())) {
            name2People.get(entry.getKey()).getPeopleAttrMap()
                .put(InfoPointKey.info_seduce_teen_num[InfoPointKey.mode], entry.getValue().size());
            teenNames.addAll(entry.getValue());
          }
        }
      }
    } else {
      if (teenNames.size() > 0) {
        for (String name : nameList) {
          if ("被告".equals(name2People.get(name).getPeopleAttrMap()
              .get(InfoPointKey.info_litigant_position[InfoPointKey.mode]))) {
            name2People.get(name).getPeopleAttrMap()
                .put(InfoPointKey.info_seduce_teen_num[InfoPointKey.mode], teenNames.size());
          }
        }
      }
    }
    // 不满十四周岁人数
    if (youngMap.size() > 0) {
      if (youngNames.size() > 0) {
        for (Map.Entry<String, Set<String>> entry : youngMap.entrySet()) {
          entry.getValue().addAll(youngNames);
        }
      }
      for (Map.Entry<String, Set<String>> entry : youngMap.entrySet()) {
        if (name2People.containsKey(entry.getKey())) {
          if (name2People.containsKey(entry.getKey())) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(
                InfoPointKey.info_seduce_young_num[InfoPointKey.mode], entry.getValue().size());
            youngNames.addAll(entry.getValue());
          }
        }
      }

    } else {
      if (youngNames.size() > 0) {
        for (String name : nameList) {
          if ("被告".equals(name2People.get(name).getPeopleAttrMap()
              .get(InfoPointKey.info_litigant_position[InfoPointKey.mode]))) {
            name2People.get(name).getPeopleAttrMap()
                .put(InfoPointKey.info_seduce_young_num[InfoPointKey.mode], youngNames.size());
          }
        }
      }
    }
    // 已满十八周岁人数
    for (Map.Entry<String, Set<String>> entry : adultMap.entrySet()) {
      if (name2People.containsKey(entry.getKey())) {
        if (name2People.containsKey(entry.getKey())) {
          Set<String> value = entry.getValue();
          int i = 0;
          for (String name : value) {
            for (String s1 : teenNames) {
              if (name.equals(s1))
                ++i;
            }
            for (String s2 : youngNames) {
              if (name.equals(s2))
                ++i;
            }
          }
          if (value.size() - i > 0)
            name2People.get(entry.getKey()).getPeopleAttrMap()
                .put(InfoPointKey.info_seduce_adult_num[InfoPointKey.mode], value.size() - i);
        }
      }
    }
  }

  // 抽取容留他人吸毒罪两年内容留他人次数
  private static NumberRecognizer freqNr = new NumberRecognizer(new String[] {"次"});

  public Map<String, Double> extractFrequency(List<String> parapraphList, String signatureDateKey,
      String positivePatternStr, String negativePatternStr) {
    Map<String, Double> doubleMap = new HashMap<>();
    String signatureDate = null;
    if (context.rabbitInfo.getExtractInfo().get(signatureDateKey) != null) {
      signatureDate = (String) (context.rabbitInfo.getExtractInfo().get(signatureDateKey));
    }
    if (signatureDate == null)
      return doubleMap;
    LitigantRecognizer lr = buildLitigantRecognizer();
    Map<String, Pattern[]> patternMap = buildPattern(positivePatternStr, negativePatternStr);
    for (String paragraph : parapraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (int i = 0; i < sentences.length; i++) {
        String sentence = sentences[i];
        String lastSentence = null;
        if (i > 0) {
          lastSentence = sentences[i - 1];
        }
        sentence = sentence.replaceAll("[(（].*?[)）]", "");
        // 反向正则
        NamedEntity[] nes_negationPatterns =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternMap.get(NEGTIVE_PATTERN));
        if (nes_negationPatterns.length != 0)
          continue;
        // 正向正则
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternMap.get(POSTIVE_PATTERN));
        if (nes_action.length == 0)
          continue;
        // 时间
        NamedEntity[] dates;
        Matcher matcher = Pattern.compile("[\\d]+.([\\d]{4}年)").matcher(sentence);
        if (matcher.find()) {
          String s = sentence.replace(matcher.group(), matcher.group(1));
          matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(s);
          if (matcher.find()) {
            s = s.replace(matcher.group(1), "");
          }
          dates = NamedEntityRecognizer.recognizeTime(s);
        } else {
          matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(sentence);
          String s = sentence;
          if (matcher.find()) {
            s = s.replace(matcher.group(1), "");
          }
          dates = NamedEntityRecognizer.recognizeTime(s);
        }
        if (dates.length == 0) {
          dates = NamedEntityRecognizer.recognizeTime(lastSentence);
          for (int m = 0; m < dates.length; m++) {
            dates[m].setOffset(-m - 1);
          }
        }
        if (dates.length == 0)
          continue;
        try {
          int dayDiff =
              DateHandler.getDayDiff(DateHandler.makeDateTime((String) dates[0].getInfo()),
                  DateHandler.makeDateTime(signatureDate));
          if (dayDiff < 0) {
            dayDiff = dayDiff * (-1);
          }
          // 两年以内
          if (dayDiff <= 365 * 2) {
            // 人物信息
            NamedEntity[] nes_litigants = lr.recognize(sentence);
            if (nes_litigants.length == 0) {
              if (lastSentence != null) {
                nes_litigants = lr.recognize(lastSentence);
                for (int m = 0; m < nes_litigants.length; m++) {
                  nes_litigants[m].setOffset(-m - 1);
                }
              }
            }
            if (nes_litigants.length == 0)
              continue;
            Integer[][] comma_semicolon = new Integer[2][];
            Integer[] commas =
                NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
            // 组合
            List<NamedEntity[]> nes_litigant_action = NamedEntityRecognizer
                .entityMatchWithLRorder(nes_litigants, nes_action, commas, false);
            // 放入集合
            for (NamedEntity[] nes : nes_litigant_action) {
              for (String name : ((LitigantUnit) (nes[0].getInfo())).getNames()) {
                if (!doubleMap.containsKey(name)) {
                  List<WrapNumberFormat> wnfs =
                      freqNr.getNumbers((String) (nes[1].getInfo()), true);
                  if (wnfs.size() != 1) {
                    continue;
                  }
                  doubleMap.put(name, wnfs.get(0).getArabicNumber());
                }
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    // 未直接说明次数或说明的次数不是两年以内
    if (doubleMap.size() == 0) {
      doubleMap = extractTimes1(parapraphList, signatureDateKey);
    }
    return doubleMap;
  }

  // 抽取容留他人吸毒罪两年内容留次数
  private Map<String, Double> extractTimes1(List<String> parapraphList, String signatureDateKey) {
    Map<String, Double> doubleMap = new HashMap<>();
    Pattern turePattern = Pattern.compile("事实如下[：:]");
    for (int n = 0; n < parapraphList.size(); n++) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(parapraphList.get(n));
      for (int j = 0; j < sentences.length; j++) {
        Matcher matcher = turePattern.matcher(sentences[j]);
        if (matcher.find()) {
          doubleMap = extractTimes2(parapraphList, signatureDateKey);
          continue;
        }
      }
    }
    if (doubleMap.size() == 0) {
      doubleMap = extractTimes(parapraphList, signatureDateKey);
    }
    return doubleMap;
  }

  private static Pattern posPattern = Pattern.compile("[一二三四五六七八九十\\d]+[.、]");
  private static Pattern posPattern1 =
      Pattern.compile("(容留|与|及|邀)[^;；：:\\.。]*?([(（].*?[）)])?[^;；：:\\.。]*吸(食|毒)");
  private static Pattern negPattern =
      Pattern.compile("如下[:：]|多次(容留|与)[^;；：:\\.。]*?([(（].*?[）)])?[^;；：:\\.。]*?吸(食|毒)");

  // 抽取容留他人吸毒罪两年内容留次数
  private Map<String, Double> extractTimes2(List<String> parapraphList, String signatureDateKey) {
    Map<String, Double> doubleMap = new HashMap<>();
    String signatureDate = null;
    if (context.rabbitInfo.getExtractInfo().get(signatureDateKey) != null) {
      signatureDate = (String) (context.rabbitInfo.getExtractInfo().get(signatureDateKey));
    }
    if (signatureDate == null)
      return doubleMap;
    LitigantRecognizer lr = this.buildLitigantRecognizer();
    for (int j = 0; j < parapraphList.size(); j++) {
      // 反向过滤
      NamedEntity[] nes_negation =
          NamedEntityRecognizer.recognizeEntityByRegex(parapraphList.get(j), negPattern);
      if (nes_negation.length > 0)
        continue;
      // 正向匹配
      NamedEntity[] nes_action =
          NamedEntityRecognizer.recognizeEntityByRegex(parapraphList.get(j), posPattern);
      if (nes_action.length == 0)
        continue;
      // 正向匹配1
      NamedEntity[] nes_action1 =
          NamedEntityRecognizer.recognizeEntityByRegex(parapraphList.get(j), posPattern1);
      if (nes_action1.length == 0)
        continue;
      // 时间
      NamedEntity[] dates;
      Matcher matcher = Pattern.compile("[\\d]+.([\\d]{4}年)").matcher(parapraphList.get(j));
      if (matcher.find()) {
        String s = parapraphList.get(j).replace(matcher.group(), matcher.group(1));
        matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(s);
        if (matcher.find()) {
          s = s.replace(matcher.group(1), "");
        }
        dates = NamedEntityRecognizer.recognizeTime(s);
      } else {
        matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(parapraphList.get(j));
        String s = parapraphList.get(j);
        if (matcher.find()) {
          s = s.replace(matcher.group(1), "");
        }
        dates = NamedEntityRecognizer.recognizeTime(s);
      }
      if (dates.length == 0) {
        List<NamedEntity> list = new ArrayList<>();
        dates = NamedEntityRecognizer.recognizeTime(parapraphList.get(j));
        if (dates.length == 0) {
          matcher = Pattern.compile("[\\d]+月").matcher(parapraphList.get(j));
          if (matcher.find()) {
            StringBuilder sb = new StringBuilder();
            List<String> sentslist = new ArrayList<>();
            for (int n = 0; n <= j; n++) {
              sentslist.add(parapraphList.get(n));
            }
            for (String sents : sentslist) {
              sb.append(sents);
            }
            dates = NamedEntityRecognizer.recognizeTime(sb.toString());
            if (dates.length > 1) {
              NamedEntity ne = dates[dates.length - 1];
              list.add(ne);
            }
            dates = list.toArray(new NamedEntity[list.size()]);
          } else
            continue;
        } else
          continue;
      }
      if (dates.length == 0)
        continue;
      double i = 0;
      for (NamedEntity date : dates) {
        try {
          int dayDiff = DateHandler.getDayDiff(DateHandler.makeDateTime((String) date.getInfo()),
              DateHandler.makeDateTime(signatureDate));
          if (dayDiff < 0) {
            dayDiff = dayDiff * (-1);
          }
          if (dayDiff > 365 * 2)
            continue;
          i++;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (i == 0)
        continue;
      // 人物信息
      NamedEntity[] nes_litigants = lr.recognize(parapraphList.get(j));
      if (nes_litigants.length == 0)
        continue;
      NamedEntity ne = nes_litigants[0];
      LitigantUnit lu = (LitigantUnit) ne.getInfo();
      String[] names = lu.getNames();
      for (String name : names) {
        if (!doubleMap.containsKey(name)) {
          doubleMap.put(name, i);
        } else {
          doubleMap.put(name, doubleMap.get(name) + i);
        }
      }
    }
    return doubleMap;
  }

  // 抽取容留他人吸毒罪一次容留人数
  public Map<String, Double> extractPeopleNum(List<String> parapraphList, String positivePatternStr,
      String negativePatternStr) {
    Map<String, Double> doubleMap = new HashMap<>();
    LitigantRecognizer lr = buildLitigantRecognizer();
    Map<String, Pattern[]> patternMap = buildPattern(positivePatternStr, negativePatternStr);
    for (int k = 0; k < parapraphList.size(); k++) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(parapraphList.get(k));
      for (int i = 0; i < sentences.length; i++) {
        String sentence = sentences[i];
        String lastSentence = null;
        if (i > 0) {
          lastSentence = sentences[i - 1];
        }
        sentence = sentence.replaceAll("[(（].*?[)）]", "");
        // 反向正则
        NamedEntity[] nes_negationPatterns =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternMap.get(NEGTIVE_PATTERN));
        if (nes_negationPatterns.length != 0)
          continue;
        // 正向正则
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternMap.get(POSTIVE_PATTERN));
        if (nes_action.length == 0)
          continue;
        // 时间
        NamedEntity[] dates;
        Matcher matcher = Pattern.compile("[\\d]+.([\\d]{4}年)").matcher(sentence);
        if (matcher.find()) {
          String s = sentence.replace(matcher.group(), matcher.group(1));
          matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(s);
          if (matcher.find()) {
            s = s.replace(matcher.group(1), "");
          }
          dates = NamedEntityRecognizer.recognizeTime(s);
        } else {
          matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(sentence);
          String s = sentence;
          if (matcher.find()) {
            s = s.replace(matcher.group(1), "");
          }
          dates = NamedEntityRecognizer.recognizeTime(s);
        }
        if (dates.length == 0) {
          if (lastSentence != null) {
            dates = NamedEntityRecognizer.recognizeTime(lastSentence);
            for (int m = 0; m < dates.length; m++) {
              dates[m].setOffset(-m - 1);
            }
          }
        }
        if (dates.length == 0) {
          List<NamedEntity> list = new ArrayList<>();
          dates = NamedEntityRecognizer.recognizeTime(parapraphList.get(k));
          if (dates.length == 0 && parapraphList.get(k).contains("同年")) {
            StringBuilder sb = new StringBuilder();
            List<String> sentslist = new ArrayList<>();
            for (int n = 0; n <= k; n++) {
              sentslist.add(parapraphList.get(n));
            }
            for (String sents : sentslist) {
              sb.append(sents);
            }
            dates = NamedEntityRecognizer.recognizeTime(sb.toString());
            if (dates.length > 1) {
              NamedEntity ne = dates[dates.length - 1];
              list.add(ne);
            }
            dates = list.toArray(new NamedEntity[list.size()]);
          } else if (dates.length == 0) {
            matcher = Pattern.compile("[\\d]+月").matcher(parapraphList.get(k));
            if (matcher.find()) {
              StringBuilder sb = new StringBuilder();
              List<String> sentslist = new ArrayList<>();
              for (int n = 0; n <= k; n++) {
                sentslist.add(parapraphList.get(n));
              }
              for (String sents : sentslist) {
                sb.append(sents);
              }
              dates = NamedEntityRecognizer.recognizeTime(sb.toString());
              if (dates.length > 1) {
                NamedEntity ne = dates[dates.length - 1];
                list.add(ne);
              }
              dates = list.toArray(new NamedEntity[list.size()]);
            } else
              continue;
          } else
            continue;
        } else if (dates.length > 2)
          continue;
        else if (dates.length == 2) {
          try {
            int dayDiff =
                DateHandler.getDayDiff(DateHandler.makeDateTime((String) dates[0].getInfo()),
                    DateHandler.makeDateTime((String) dates[1].getInfo()));
            if (dayDiff < 0) {
              dayDiff = dayDiff * (-1);
            }
            if (dayDiff > 24)
              continue;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        // 人物信息
        NamedEntity[] nes_litigants = lr.recognize(sentence);
        if (nes_litigants.length == 0) {
          if (lastSentence != null) {
            nes_litigants = lr.recognize(lastSentence);
            for (int m = 0; m < nes_litigants.length; m++) {
              nes_litigants[m].setOffset(-m - 1);
            }
          }
        }
        if (nes_litigants.length == 0)
          continue;
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 组合
        List<NamedEntity[]> nes_litigant_action =
            NamedEntityRecognizer.entityMatchWithLRorder(nes_litigants, nes_action, commas, false);
        // 放入集合
        for (NamedEntity[] nes : nes_litigant_action) {
          for (String name : ((LitigantUnit) (nes[0].getInfo())).getNames()) {
            for (Pattern pattern : patternMap.get(POSTIVE_PATTERN)) {
              String sent = (String) nes[1].getInfo();
              sent = sent.replaceAll("[(（].*?[)）]", "");
              double number = NumberInfoExtractor.parseStatistics(sent, pattern);
              if (!doubleMap.containsKey(name)) {
                doubleMap.put(name, number);
              } else {
                if (number > doubleMap.get(name)) {
                  doubleMap.put(name, number);
                }
              }
            }
          }
        }

      }
    }
    return doubleMap;
  }

  // 抽取涉外
  public Map<String, Boolean> extractAbroadProstitute(List<String> parapraphList, String infoKey) {
    Map<String, Boolean> rsltMap = new HashMap<>();
    LitigantRecognizer lr = buildLitigantRecognizer();
    for (String paragraph : parapraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, countryNamePattern);
        if (nes_action.length == 0)
          continue;
        NamedEntity[] nes_litigants = lr.recognize(sentence);
        if (nes_litigants.length == 0)
          continue;
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 组合
        List<NamedEntity[]> nes_litigant_action =
            NamedEntityRecognizer.entityMatchWithLRorder(nes_litigants, nes_action, commas, false);
        for (NamedEntity[] nes : nes_litigant_action) {
          for (String name : ((LitigantUnit) (nes[0].getInfo())).getNames()) {
            if (!rsltMap.containsKey(name)) {
              rsltMap.put(name, true);
            }
          }
        }
      }
    }
    return rsltMap;
  }

  private Map<String, Object> parsePeopleAttr(List<String> paragraphList, String infoKey,
      LitigantRecognizer lr, Object fillType, List<String> nameList, Pattern[] positivePatterns,
      Pattern[] negativePatterns, int limitSameShortSentFlg, int orderFlg, int allLitigantFlg) {
    Map<String, Object> surrender = new HashMap<>();
    NamedEntity[] nes_all_litigant_action = new NamedEntity[] {};
    String all_sent = "";
    for (String paragraph : paragraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (int i = 0; i < sentences.length; i++) {
        String sentence = sentences[i];
        String lastSentence = null;
        if (i > 0) {
          lastSentence = sentences[i - 1];
        }
        // 负向正则表达式是否存在匹配结果
        NamedEntity[] nes_negationPatterns =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, negativePatterns);
        if (nes_negationPatterns.length != 0) {
          continue;
        }
        // 正向正则表达式是否存在匹配结果
        NamedEntity[] nes_action = new NamedEntity[] {};
        if (fillType instanceof Boolean) {
          nes_action =
              NamedEntityRecognizer.recognizeEntityByRegex(sentence, positivePatterns, true);
        } else if (fillType instanceof String) {
          nes_action = NamedEntityRecognizer.recognizeEntityByRegex(sentence, positivePatterns);
        }
        if (nes_action.length == 0) {
          continue;
        }
        nes_all_litigant_action = nes_action;
        all_sent = sentence;
        // 得到被告人的位置
        NamedEntity[] nes_litigants;
        String caseHierarchy = (String) context.rabbitInfo.getExtractInfo()
            .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode]);
        if (caseHierarchy != null && caseHierarchy.equals("一审")) {
          nes_litigants = NamedEntityRecognizer.recognizeLitigantByType(sentence,
              LitigantUnit.LABEL_DEFENDANT, lr);
          if (nes_litigants.length == 0) {
            if (lastSentence != null) {
              nes_litigants = NamedEntityRecognizer.recognizeLitigantByType(lastSentence,
                  LitigantUnit.LABEL_DEFENDANT, lr);
              if (nes_litigants.length == 0) {
                continue;
              } else {
                for (NamedEntity ne : nes_litigants) {
                  ne.setOffset(-100);
                }
              }
            } else {
              continue;
            }
          }
        } else {
          nes_litigants = lr.recognize(sentence);
          if (nes_litigants.length == 0) {
            if (lastSentence != null) {
              nes_litigants = lr.recognize(lastSentence);
              if (nes_litigants.length == 0) {
                continue;
              } else {
                for (NamedEntity ne : nes_litigants) {
                  ne.setOffset(-100);
                }
              }
            } else {
              continue;
            }
          }
        }
        // 标点的位置
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 律师、辩护人的位置
        NamedEntity[] nes_attorneys =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, ATTORNEY_OPINION);
        // 否定表达的位置
        NamedEntity[] nes_negations =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_NEGATIONS);
        Integer[] semicolon = comma_semicolon[1];
        int oldSize = surrender.size();
        combineEntities(sentence, nes_litigants, nes_action, fillType, commas,
            limitSameShortSentFlg, orderFlg, semicolon, nes_attorneys, nes_negations, surrender);
        if (surrender.size() > oldSize) {
          posRecodExtractor.recordInfoPointMatchSentPos(infoKey, sentence);
        }
      }
    }

    // 如果匹配到了属性但没有找到人，就认为是所有人
    if ((allLitigantFlg == 1) && (surrender.size() == 0)
        && ((nes_all_litigant_action.length > 0))) {
      for (String name : nameList) {
        surrender.put(name, nes_all_litigant_action[0].getInfo());
      }
      posRecodExtractor.recordInfoPointMatchSentPos(infoKey, all_sent);
    }

    return surrender;
  }

  private Map<String, List<Object>> parsePeopleAttrList(List<String> parapraphList, String infoKey,
      LitigantRecognizer lr, List<String> nameList, Map<Pattern, String> patt2valmModeStrMap,
      Pattern[] negationPatterns, int limitSameShortSentFlg, int orderFlg, int allLitigantFlg) {
    Map<String, List<Object>> rsltMap = new HashMap<>();
    NamedEntity[] nes_all_litigant_action = new NamedEntity[] {};
    for (String paragraph : parapraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (int i = 0; i < sentences.length; i++) {
        String sentence = sentences[i];
        String lastSentence = null;
        if (i > 0) {
          lastSentence = sentences[i - 1];
        }
        NamedEntity[] nes_negationPatterns =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, negationPatterns);
        if (nes_negationPatterns.length != 0) {
          continue;
        }
        // 得到表明关键词或者正则表达式的位置及对应替换字符串
        List<NamedEntity> listNesAction = new ArrayList<>();
        for (Map.Entry<Pattern, String> entry : patt2valmModeStrMap.entrySet()) {
          NamedEntity[] nes_tmp = NamedEntityRecognizer.recognizeEntityByRegex(sentence,
              entry.getKey(), entry.getValue());
          Collections.addAll(listNesAction, nes_tmp);
        }
        NamedEntity[] nes_action = listNesAction.toArray(new NamedEntity[listNesAction.size()]);
        if (nes_action.length == 0) {
          continue;
        }
        // 得到被告人的位置
        NamedEntity[] nes_litigants;
        String caseHierarchy = (String) context.rabbitInfo.getExtractInfo()
            .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode]);
        if (caseHierarchy != null && caseHierarchy.equals("一审")) {
          nes_litigants = NamedEntityRecognizer.recognizeLitigantByType(sentence,
              LitigantUnit.LABEL_DEFENDANT, lr);
          if (nes_litigants.length == 0) {
            if (lastSentence != null) {
              nes_litigants = NamedEntityRecognizer.recognizeLitigantByType(lastSentence,
                  LitigantUnit.LABEL_DEFENDANT, lr);
              if (nes_litigants.length == 0) {
                continue;
              } else {
                for (NamedEntity ne : nes_litigants) {
                  ne.setOffset(-100);
                }
              }
            } else {
              continue;
            }
          }
        } else {
          nes_litigants = lr.recognize(sentence);
          if (nes_litigants.length == 0) {
            if (lastSentence != null) {
              nes_litigants = lr.recognize(lastSentence);
              if (nes_litigants.length == 0) {
                continue;
              } else {
                for (NamedEntity ne : nes_litigants) {
                  ne.setOffset(-100);
                }
              }
            } else {
              continue;
            }
          }
        }

        nes_all_litigant_action = nes_action;
        // 标点的位置
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 根据刚找到的实体和他们之间的逻辑关系，综合判断最后的结果
        List<NamedEntity[]> nes_litigant_action =
            combineEntitie(nes_litigants, nes_action, commas, limitSameShortSentFlg, orderFlg);
        if (nes_litigant_action.size() == 0) {
          continue;
        }
        // 被告和关键词组合，被告在左边，关键词在右边
        for (NamedEntity[] nes : nes_litigant_action) {
          updateMapAttrList((LitigantUnit) (nes[0].getInfo()), nes[1].getInfo(), rsltMap);
        }
        if (rsltMap.size() > 0) {
          posRecodExtractor.recordInfoPointMatchSentPos(infoKey, sentence);
        }
      }
    }
    // 如果匹配到了属性但没有找到人，就认为是所有人,这一种情况暂未出现，故逻辑暂未实现
    if ((allLitigantFlg == 1) && (rsltMap.size() == 0) && ((nes_all_litigant_action.length > 0))) {
      for (String name : nameList) {
        rsltMap.put(name, new ArrayList<>());
      }
    }

    return rsltMap;
  }

  private static Map<String, List<Object>> parsePeopleAttrSentence(List<String> paragraphList,
      LitigantRecognizer lr, List<String> nameList, Pattern[] positivePatterns,
      Pattern[] negationPatterns, int limitSameShortSentFlg, int orderFlg, int allLitigantFlg,
      int rangeFlg) {
    Map<String, List<Object>> rsltMap = new HashMap<>();
    String all_paragraph = "";
    String all_sent = "";
    for (String paragraph : paragraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        NamedEntity[] nes_negationPatterns =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, negationPatterns);
        if (nes_negationPatterns.length != 0) {
          continue;
        }

        NamedEntity[] nes_action;
        // 得到匹配正则表达式的位置
        nes_action = NamedEntityRecognizer.recognizeEntityByRegex(sentence, positivePatterns);
        if (nes_action.length == 0) {
          continue;
        }
        all_paragraph = paragraph;
        all_sent = sentence;
        // 得到被告人的位置
        NamedEntity[] nes_litigants = lr.recognize(sentence);
        if (nes_litigants.length == 0) {
          continue;
        }
        // 标点的位置
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 根据刚找到的实体和他们之间的逻辑关系，综合判断最后的结果
        List<NamedEntity[]> nes_litigant_action =
            combineEntitie(nes_litigants, nes_action, commas, limitSameShortSentFlg, orderFlg);
        if (nes_litigant_action.size() == 0) {
          continue;
        }
        // 被告和关键词组合，被告在左边，关键词在右边
        if (rangeFlg == 0) {
          for (NamedEntity[] nes : nes_litigant_action) {
            updateMapAttrList((LitigantUnit) (nes[0].getInfo()), paragraph, rsltMap);
          }
        } else if (rangeFlg == 1) {
          for (NamedEntity[] nes : nes_litigant_action) {
            updateMapAttrList((LitigantUnit) (nes[0].getInfo()), sentence, rsltMap);
          }
        }
      }
    }
    // 如果匹配到了属性但没有找到人，就认为是所有人的
    if ((allLitigantFlg == 1) && (rsltMap.size() == 0) && (all_paragraph.length() > 0)) {
      for (String name : nameList) {
        List<Object> arr = new ArrayList<>();
        if (rangeFlg == 0) {
          arr.add(all_paragraph);
        } else if (rangeFlg == 1) {
          arr.add(all_sent);
        }
        if (arr.size() > 0) {
          rsltMap.put(name, arr);
        }
      }
    }

    return rsltMap;
  }

  // 抽取人数
  private static Map<String, Double> parsePeopleAttrStatistics(Map<String, Object> objectMap,
      Pattern[] positivePatterns) {
    Map<String, Double> rsltMap = new HashMap<>();
    for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
      double number = 0.0;
      String key = entry.getKey();
      String value = (String) (entry.getValue());
      // 分句中是否涉及人员缩小范围，认为只有一个长句子是描述的
      for (Pattern pattern : positivePatterns) {
        Matcher m2 = pattern.matcher(value);
        if (m2.find()) {
          number = NumberInfoExtractor.parseStatistics(value, pattern);
        }
      }
      rsltMap.put(key, number);
    }

    return rsltMap;

  }

  // 抽取次数
  public Map<String, Double> parsePeopleAttrDegree() {
    Map<String, Double> rsltMap = new HashMap<>();
    Map<String, List<Object>> result = null;
    List<List<String>> list = new ArrayList<>();
    Pattern positivePattern =
        Pattern.compile("([一二三四五六七八九十\\d]+、|[（(][一二三四五六七八九十]+[)）])[\\d]+年[\\d、]+月");
    Pattern pattern = Pattern.compile("(同|[\\d]+)年[\\d]+月[\\d]+日[\\u4e00-\\u9fa5]+[\\d]+时许");
    Pattern negativePattern = Pattern.compile("投案|自首|(?<!当场)抓获|和解|拘传");
    if (list.size() == 0) {
      return rsltMap;
    } else {
      LitigantRecognizer lr = buildLitigantRecognizer();
      for (List<String> parapraps : list) {
        result = extraction(parapraps, negativePattern, positivePattern, lr);
        if (result.size() == 0) {
          result = extraction(parapraps, negativePattern, pattern, lr);
        }
        if (result.size() > 0)
          break;
      }
    }
    for (Map.Entry<String, List<Object>> entry : result.entrySet()) {
      String key = entry.getKey();
      List<Object> value = entry.getValue();
      rsltMap.put(key, (double) value.size());
    }
    return rsltMap;
  }

  private static Map<String, List<Object>> extraction(List<String> parapraphList,
      Pattern negativePattern, Pattern positivePattern, LitigantRecognizer lr) {
    Map<String, List<Object>> result = new HashMap<>();
    Matcher matcher;
    Matcher ma;
    for (String paragraph : parapraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        matcher = negativePattern.matcher(sentence);
        if (matcher.find())
          continue;
        ma = positivePattern.matcher(sentence);
        if (!ma.find())
          continue;
        // 得到正则位置
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, positivePattern);
        // 得到被告人的位置
        NamedEntity[] nes_litigants = lr.recognize(sentence);
        if (nes_litigants.length == 0) {
          continue;
        }
        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        List<NamedEntity[]> nes_litigant_action =
            NamedEntityRecognizer.entityMatchWithNOorder(nes_litigants, nes_action, commas, false);
        // 被告和关键词组合，被告在左边，关键词在右边
        for (NamedEntity[] nes : nes_litigant_action) {
          updateMapAttrList((LitigantUnit) (nes[0].getInfo()), nes[1], result);
        }
      }
    }
    return result;
  }

  // 抽取容留他人吸毒罪两年内容留次数
  private Map<String, Double> extractTimes(List<String> parapraphList, String signatureDateKey) {
    Map<String, Double> doubleMap = new HashMap<>();
    String signatureDate = null;
    if (context.rabbitInfo.getExtractInfo().get(signatureDateKey) != null) {
      signatureDate = (String) (context.rabbitInfo.getExtractInfo().get(signatureDateKey));
    }
    if (signatureDate == null)
      return doubleMap;
    Pattern posPattern = Pattern.compile("(容留|与|及|邀)[^;；：:\\.。]*?([(（].*?[）)])?[^;；：:\\.。]*吸(食|毒)");
    Pattern negPattern =
        Pattern.compile("如下[:：]|多次(容留|与)[^;；：:\\.。]*?([(（].*?[）)])?[^;；：:\\.。]*?吸(食|毒)");
    LitigantRecognizer lr = this.buildLitigantRecognizer();
    for (int k = 0; k < parapraphList.size(); k++) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(parapraphList.get(k));
      for (int j = 0; j < sentences.length; j++) {
        String sentence = sentences[j];
        String lastSentence = null;
        if (j > 0) {
          lastSentence = sentences[j - 1];
        }
        sentence = sentence.replaceAll("[(（].*?[)）]", "");
        // 反向过滤
        NamedEntity[] nes_negation =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, negPattern);
        if (nes_negation.length > 0)
          continue;
        // 正向匹配
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, posPattern);
        if (nes_action.length == 0)
          continue;
        // 时间
        NamedEntity[] dates;
        Matcher matcher = Pattern.compile("[\\d]+.([\\d]{4}年)").matcher(sentence);
        if (matcher.find()) {
          String s = sentence.replace(matcher.group(), matcher.group(1));
          matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(s);
          if (matcher.find()) {
            s = s.replace(matcher.group(1), "");
          }
          dates = NamedEntityRecognizer.recognizeTime(s);
        } else {
          matcher = Pattern.compile("[\\d]+月(前后|[前后])").matcher(sentence);
          String s = sentence;
          if (matcher.find()) {
            s = s.replace(matcher.group(1), "");
          }
          dates = NamedEntityRecognizer.recognizeTime(s);
        }
        if (dates.length == 0) {
          if (lastSentence != null) {
            dates = NamedEntityRecognizer.recognizeTime(lastSentence);
            for (int m = 0; m < dates.length; m++) {
              dates[m].setOffset(-m - 1);
            }
          }
        }
        if (dates.length == 0) {
          List<NamedEntity> list = new ArrayList<>();
          dates = NamedEntityRecognizer.recognizeTime(sentence);
          if (dates.length == 0 && sentence.contains("同年")) {
            StringBuilder sb = new StringBuilder();
            List<String> sentslist = new ArrayList<>();
            for (int n = 0; n <= j; n++) {
              sentslist.add(sentences[n]);
            }
            for (String sents : sentslist) {
              sb.append(sents);
            }
            dates = NamedEntityRecognizer.recognizeTime(sb.toString());
            if (dates.length > 1) {
              NamedEntity ne = dates[dates.length - 1];
              list.add(ne);
            }
            dates = list.toArray(new NamedEntity[list.size()]);
          } else
            continue;
        }
        if (dates.length == 0)
          continue;
        if (dates.length > 1) {
          List<NamedEntity> list = new ArrayList<>();
          NamedEntity[] nes_allEntities =
              NamedEntityRecognizer.combineEntities(new String[] {"时间", null}, dates, nes_action);
          for (int n = 0; n < nes_allEntities.length - 1; n++) {
            if ("时间".equals(nes_allEntities[n].getType())
                && null == nes_allEntities[n + 1].getType()) {
              list.add(nes_allEntities[n]);
            }
          }
          dates = list.toArray(new NamedEntity[list.size()]);
        }
        double i = 0;
        for (NamedEntity date : dates) {
          try {
            int dayDiff = DateHandler.getDayDiff(DateHandler.makeDateTime((String) date.getInfo()),
                DateHandler.makeDateTime(signatureDate));
            if (dayDiff < 0) {
              dayDiff = dayDiff * (-1);
            }
            if (dayDiff > 365 * 2)
              continue;
            i++;
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        if (i == 0)
          continue;
        // 人物信息
        NamedEntity[] nes_litigants = lr.recognize(sentence);
        if (nes_litigants.length == 0) {
          if (lastSentence != null) {
            nes_litigants = lr.recognize(lastSentence);
            for (int m = 0; m < nes_litigants.length; m++) {
              nes_litigants[m].setOffset(-m - 1);
            }
          }
        }
        if (nes_litigants.length == 0)
          continue;
        if (nes_litigants.length > 1) {
          List<NamedEntity> list = new ArrayList<>();
          NamedEntity[] nes_allEntities = NamedEntityRecognizer
              .combineEntities(new String[] {"时间", null}, dates, nes_litigants);
          for (int m = 0; m < nes_allEntities.length - 1; m++) {
            if ("时间".equals(nes_allEntities[m].getType())
                && null == nes_allEntities[m + 1].getType()) {
              list.add(nes_allEntities[m + 1]);
            }
          }
          nes_litigants = list.toArray(new NamedEntity[list.size()]);
        }

        Integer[][] comma_semicolon = new Integer[2][];
        Integer[] commas =
            NamedEntityRecognizer.recognizeCommaAndSemiColon(sentence, comma_semicolon);
        // 组合人物和正则
        List<NamedEntity[]> nes_litigant_action =
            NamedEntityRecognizer.entityMatchWithLRorder(nes_litigants, nes_action, commas, false);
        // 放入集合
        List<String[]> names = new ArrayList<>();
        List<NamedEntity[]> nesList = new ArrayList<>();
        for (NamedEntity[] nes : nes_litigant_action) {
          LitigantUnit lu = (LitigantUnit) nes[0].getInfo();
          String[] nameArray = lu.getNames();
          if (!names.contains(nameArray)) {
            names.add(nameArray);
            nesList.add(nes);
          }
        }
        for (NamedEntity[] nes : nesList) {
          for (String name : ((LitigantUnit) (nes[0].getInfo())).getNames()) {
            if (!doubleMap.containsKey(name)) {
              doubleMap.put(name, i);
            } else {
              doubleMap.put(name, doubleMap.get(name) + i);
            }
          }
        }
      }
    }
    return doubleMap;
  }



  private static void combineEntities(String sentence, NamedEntity[] nes_litigants,
      NamedEntity[] nes_action, Object fillType, Integer[] commas, int limitSameShortSentFlg,
      int orderFlg, Integer[] semicolon, NamedEntity[] nes_attorney, NamedEntity[] nes_negations,
      Map<String, Object> result) {
    List<NamedEntity[]> nes_litigant_action =
        combineEntitie(nes_litigants, nes_action, commas, limitSameShortSentFlg, orderFlg);
    if (nes_litigant_action.size() == 0) {
      return;
    }
    if (fillType instanceof String) {
      for (NamedEntity[] nes : nes_litigant_action) {
        updateMapAttr((LitigantUnit) (nes[0].getInfo()), nes[1].getInfo(), result);
      }
    } else if (fillType instanceof Boolean) {
      // 没有否定表述
      if (nes_negations.length == 0) {
        // 没有辩护意见
        if (nes_attorney.length == 0) {
          // 被告和关键词组合，被告在左边，关键词在右边
          for (NamedEntity[] nes : nes_litigant_action) {
            updateMapAttr((LitigantUnit) (nes[0].getInfo()), true, result);
          }
        } else {
          // 辩护意见位置
          int j = nes_attorney[0].getOffset();
          // 如果句子里面有律师等出现，怎么判断是否是法官的观点
          NamedEntity[] nes_judges =
              NamedEntityRecognizer.recognizeEntityByRegex(sentence, JUDGES_ATTORNEY);
          if (nes_judges.length > 0) {
            // 给每个被告赋值
            for (NamedEntity[] nes : nes_litigant_action) {
              updateMapAttr((LitigantUnit) (nes[0].getInfo()), true, result);
            }
          } else {
            for (NamedEntity[] nes : nes_litigant_action) {
              if (nes[1].getOffset() < j) {
                updateMapAttr((LitigantUnit) (nes[0].getInfo()), true, result);
              }
            }
          }
        }
      } else {
        // 存在否定表述
        // 有辩护意见
        if (nes_attorney.length != 0) {
          // 辩护意见位置
          int j = nes_attorney[0].getOffset();
          // 如果句子里面有律师等出现，怎么判断是否是法官的观点
          NamedEntity[] nes_judges =
              NamedEntityRecognizer.recognizeEntityByRegex(sentence, JUDGES_ATTORNEY);
          if (nes_judges.length > 0) {
            int k = nes_judges[0].getOffset();
            // 给每个被告赋值
            for (NamedEntity[] nes : nes_litigant_action) {
              if ((nes[1].getOffset() > k) && (k > j))
                updateMapAttr((LitigantUnit) (nes[0].getInfo()), true, result);
            }
          } else {
            // 给每个被告赋值
            for (NamedEntity[] nes : nes_litigant_action) {
              if (nes[1].getOffset() < j) {
                updateMapAttr((LitigantUnit) (nes[0].getInfo()), true, result);
              }
            }
          }

        }
        // // 需要判断到底否定的是什么
        // // 第一步，先组合被告_关键词_否定，组合
        // List<NamedEntity[]> lne_negations = new ArrayList<>();
        // for (NamedEntity ne : nes_negations)
        // lne_negations.add(new NamedEntity[]{ne});
        //
        // // 暂时不考虑标点符号的影响
        // List<NamedEntity[]> lnes = NamedEntityRecognizer.entityMatch(semicolon,
        // nes_litigant_action, lne_negations, false, false);
        // for (NamedEntity[] nes : lnes) {
        // // 要处理的情况为：被告XXX，...,予以支持;...,不予认定。
        // // 这种情况一般是前面说某种属性（比如“自首”）,后面说另外一种属性（“比如坦白”），因此必须考虑否定表达式的位置
        // // 排除规则为：属性和否定表述之间有 “；”或肯定表述
        // // ner.entityMatch()
        // Integer[] semicolonPositions = NamedEntityRecognizer.positionsBetween(nes[1], nes[2],
        // semicolon);
        // // 如果存在分号
        // if (semicolonPositions != null) {
        // NamedEntity[] nes_judges = NamedEntityRecognizer.recognizeEntityByRegex(sentence,
        // JUDGES);
        // Integer[] positiveArgsPositions = NamedEntityRecognizer.positionsBetween(nes[1], nes[2],
        // nes_judges);
        // // 如果存在肯定表述
        // if (positiveArgsPositions != null) {
        // // 判断肯定表述在“;”前面，则认为实际是肯定表述
        // if (positiveArgsPositions[0] < semicolonPositions[semicolonPositions.length - 1]) {
        // updateMapAttr((LitigantUnit) (nes[0].getInfo()), true, result);
        // // System.out.println(document.getId()+"
        // // 肯定表述:"+sentence);
        // }
        // }
        // }
        // }

      }
    }
  }

  private static List<NamedEntity[]> combineEntitie(NamedEntity[] nes_litigants,
      NamedEntity[] nes_action, Integer[] commas, int limitSameShortSentFlg, int orderFlg) {
    List<NamedEntity[]> nes_litigant_action = new ArrayList<>();
    // 不要求人物实体、属性实体出现在同一个连续短句中；人物实体、属性实体出现的先后顺序不固定
    if ((limitSameShortSentFlg == 0) && (orderFlg == 0)) {
      nes_litigant_action =
          NamedEntityRecognizer.entityMatchWithNOorder(nes_litigants, nes_action, commas, false);
    }
    // 不要求人物实体、属性实体出现在同一个连续短句中；人物实体固定出现在属性实体前面
    else if ((limitSameShortSentFlg == 0) && (orderFlg == 1)) {
      nes_litigant_action =
          NamedEntityRecognizer.entityMatchWithLRorder(nes_litigants, nes_action, commas, false);
    }
    // 不要求人物实体、属性实体出现在同一个连续短句中；人物实体固定出现在属性实体后面
    else if ((limitSameShortSentFlg == 0) && (orderFlg == 2)) {
      List<NamedEntity[]> nes_litigant_action_tmp = new ArrayList<>();
      nes_litigant_action_tmp =
          NamedEntityRecognizer.entityMatchWithLRorder(nes_action, nes_litigants, commas, false);
      for (NamedEntity[] nes_tmp : nes_litigant_action_tmp) {
        NamedEntity[] nes = new NamedEntity[2];
        nes[0] = nes_tmp[1];
        nes[1] = nes_tmp[0];
        nes_litigant_action.add(nes);
      }
    }
    // 要求人物实体、属性实体出现在同一个连续短句中；人物实体、属性实体出现的先后顺序不固定
    else if ((limitSameShortSentFlg == 1) && (orderFlg == 0)) {
      nes_litigant_action =
          NamedEntityRecognizer.entityMatchWithNOorder(nes_litigants, nes_action, commas, true);
    }
    // 要求人物实体、属性实体出现在同一个连续短句中；人物实体固定出现在属性实体前面
    else if ((limitSameShortSentFlg == 1) && (orderFlg == 1)) {
      nes_litigant_action =
          NamedEntityRecognizer.entityMatchWithLRorder(nes_litigants, nes_action, commas, true);
    }
    // 不要求人物实体、属性实体出现在同一个连续短句中；人物实体固定出现在属性实体后面
    else if ((limitSameShortSentFlg == 1) && (orderFlg == 2)) {
      List<NamedEntity[]> nes_litigant_action_tmp = new ArrayList<>();
      nes_litigant_action_tmp =
          NamedEntityRecognizer.entityMatchWithLRorder(nes_action, nes_litigants, commas, true);
      for (NamedEntity[] nes_tmp : nes_litigant_action_tmp) {
        NamedEntity[] nes = new NamedEntity[2];
        nes[0] = nes_tmp[1];
        nes[1] = nes_tmp[0];
        nes_litigant_action.add(nes);
      }
    }
    return nes_litigant_action;
  }

  // 泛型方法
  // 根据人物属性对应的str获取string信息
  @SuppressWarnings("unchecked")
  private static <T, E> Map<String, T> getPeopleAttrValue(Map<String, E> rawMap) {
    Map<String, T> rsltMap = new HashMap<>();
    for (Map.Entry<String, E> entry : rawMap.entrySet()) {
      String key = entry.getKey();
      E value = entry.getValue();
      rsltMap.put(key, (T) (value));
    }

    return rsltMap;
  }

  // 根据人物属性对应的str获取double数值信息
  private static Map<String, Double> getPeopleAttrDoubleValue(Map<String, Object> rawMap,
      NumberRecognizer nr, String outputUnit) {
    Map<String, Double> rsltMap = new HashMap<>();

    for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
      String key = entry.getKey();
      String value = (String) (entry.getValue());
      List<WrapNumberFormat> wnfs = nr.getNumbers(value, true);
      if (wnfs.size() != 1) {
        continue;
      }
      String curUnit = wnfs.get(0).getUnit();
      if (TextUtils.isEmpty(outputUnit) || (outputUnit.equals("int")) || (outputUnit.equals("次"))
          || (outputUnit.equals("人"))) {
        rsltMap.put(key, wnfs.get(0).getArabicNumber());
      } else if (outputUnit.equals("天")) {
        if (curUnit.contains("月")) {
          rsltMap.put(key, (wnfs.get(0).getArabicNumber()) * 30);
        } else if (curUnit.contains("小时"))
          rsltMap.put(key, Math.round(((wnfs.get(0).getArabicNumber()) / 24) * 100) / 100.0);
        else if (curUnit.contains("天") || curUnit.contains("日")) {
          rsltMap.put(key, wnfs.get(0).getArabicNumber());
        }
      } else {
      }
    }

    return rsltMap;
  }

  private static void updateMapAttr(LitigantUnit lu, Object value, Map<String, Object> result) {
    String[] names = lu.getNames();
    for (String name : names) {
      if (!result.containsKey(name)) {
        result.put(name, value);
      }
    }
  }

  private static void updateMapAttrList(LitigantUnit lu, Object val,
      Map<String, List<Object>> result) {
    String[] names = lu.getNames();
    for (String name : names) {
      List<Object> tmpList;
      if (!result.containsKey(name)) {
        tmpList = new ArrayList<>();
        result.put(name, tmpList);
      }
      tmpList = result.get(name);
      if (!tmpList.contains(val)) {
        result.get(name).add(val);
      }
    }
  }

  // 根据输入的字符串构建关键词，正向正则，反向正则
  private static Map<String, Object> buildPattern(String valModeStr, String positivePatternStr,
      String negativePatternStr) {
    Map<String, Object> rsltMap = new HashMap<>();

    String[] keywords;
    if (TextUtils.isEmpty(valModeStr)) {
      keywords = new String[] {};
    } else {
      keywords = valModeStr.split(SPLITER);
    }
    Pattern[] positivePatterns;
    if (TextUtils.isEmpty(positivePatternStr)) {
      positivePatterns = new Pattern[] {};
    } else {
      String[] tmp = positivePatternStr.split(SPLITER);
      positivePatterns = new Pattern[tmp.length];
      for (int i = 0; i < tmp.length; i++) {
        positivePatterns[i] = Pattern.compile(tmp[i]);
      }
    }
    Pattern[] negativePatterns;
    if (TextUtils.isEmpty(negativePatternStr)) {
      negativePatterns = new Pattern[] {};
    } else {
      String[] tmp = negativePatternStr.split(SPLITER);
      negativePatterns = new Pattern[tmp.length];
      for (int i = 0; i < tmp.length; i++) {
        negativePatterns[i] = Pattern.compile(tmp[i]);
      }
    }

    rsltMap.put(CAPTURE_MODE, keywords);
    rsltMap.put(POSTIVE_PATTERN, positivePatterns);
    rsltMap.put(NEGTIVE_PATTERN, negativePatterns);

    return rsltMap;
  }

  // 根据输入的字符串构建关键词，正向正则，反向正则
  private static Map<String, Pattern[]> buildPattern(String positivePatternStr,
      String negativePatternStr) {
    Map<String, Pattern[]> rsltMap = new HashMap<>();

    Pattern[] positivePatterns;
    if (TextUtils.isEmpty(positivePatternStr)) {
      positivePatterns = new Pattern[] {};
    } else {
      String[] tmp = positivePatternStr.split(SPLITER);
      positivePatterns = new Pattern[tmp.length];
      for (int i = 0; i < tmp.length; i++) {
        positivePatterns[i] = Pattern.compile(tmp[i]);
      }
    }
    Pattern[] negativePatterns;
    if (TextUtils.isEmpty(negativePatternStr)) {
      negativePatterns = new Pattern[] {};
    } else {
      String[] tmp = negativePatternStr.split(SPLITER);
      negativePatterns = new Pattern[tmp.length];
      for (int i = 0; i < tmp.length; i++) {
        negativePatterns[i] = Pattern.compile(tmp[i]);
      }
    }

    rsltMap.put(POSTIVE_PATTERN, positivePatterns);
    rsltMap.put(NEGTIVE_PATTERN, negativePatterns);

    return rsltMap;
  }

  private static final Pattern[] PATTERN_NEGATIONS =
      new Pattern[] {Pattern.compile("不[\u4e00-\u9fa5]*(支持|采纳|采信|认定|成立|构成|符合|属于)")};
  private static final Pattern ATTORNEY_OPINION = Pattern.compile("辩护人|代理人|律师|被告认为");
  private static final Pattern JUDGES_ATTORNEY = Pattern.compile("本院|经查|采纳|采信|支持|予以?(认定|确认)");
}
