package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.CriminalRecord;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 减刑假释
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class CommutationParoleExtractor {
  // private static Logger logger = LoggerFactory.getLogger(CommutationParoleExtractor.class);
  private final static String[] KEYWORDS =
      new String[] {"罪名", "罪名类型", "缓刑", "罚金", "剥夺政治权利", "判处时间长度", "判处时间", "金额", ","};
  private final static String[] MINUS_KEYWORDS =
      new String[] {"减刑", "罪名类型", "缓刑", "罚金", "剥夺政治权利", "判处时间长度", "判处时间", "金额", ","};
  private static final Pattern[] PATTERNS_CRIMINAL_JUDGEMENT = {Pattern.compile("刑事判决")};
  private static final Pattern[] PATTERNS_CHANGE_JUDGEMENT = {Pattern.compile("改判")};
  private static final Pattern[] PATTERNS_MINUS_JUDGEMENT = {Pattern.compile("减去|减为")};
  private static final Pattern[] PATTERNS_MINUS_OPINION = {Pattern.compile("提出减刑")};
  private static final Pattern[] PATTERNS_JUDGE_ARRIVE = {Pattern.compile("裁定送达")};
  private static final Pattern[] PATTERNS_LIMIT_MINUS = {Pattern.compile("限制减刑")};

  private Context context;

  public CommutationParoleExtractor(Context context) {
    this.context = context;
  }

  // 刑事+行政处罚类型
  private static final Pattern[] PATTERNS_TYPES =
      {Pattern.compile("拘役|管制|有期徒刑|无期徒刑|死刑|免于刑事处罚|行政拘留|劳教|劳动教养")};
  private static final Pattern[] PATTERN_PROBATION = {Pattern.compile("缓刑|缓期")};
  private static final Pattern[] PATTERN_FINE = {Pattern.compile("罚金")};
  private static final Pattern[] PATTERN_CONFISCATE_PROPERTY = {Pattern.compile("剥夺政治权利")};
  private static final Pattern[] PATTERN_LIFELONG = {Pattern.compile("(?<=剥夺政治权利)终身")};

  // 减刑假释的抽取
  public void extractCommutationParole(List<String> paragraphList, String signatureDate) {
    // 借用一下CriminalRecord的结构存储一下抽取结果
    List<CriminalRecord> criminalRecordList = new ArrayList<>();
    for (String paragraph : paragraphList) {
      // 拆分成句
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        NamedEntity[] nesJudge1 =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_CRIMINAL_JUDGEMENT);
        NamedEntity[] nesJudge2 =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_CHANGE_JUDGEMENT);
        NamedEntity[] nesJudge3 =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_MINUS_JUDGEMENT);
        NamedEntity[] nesJudge4 =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_MINUS_OPINION);
        if (nesJudge4.length > 0) {
          break;
        } else if (nesJudge2.length > 0) { // 二审判决
          criminalRecordList = new ArrayList<>();
          List<CriminalRecord> crsTmp = parseSingleJudge(sentence);
          for (CriminalRecord cr : crsTmp) {
            cr.setPrisonDate("改判");
          }
          criminalRecordList.addAll(crsTmp);
        } else if (nesJudge1.length > 0) { // 一审判决
          List<CriminalRecord> crsTmp = parseSingleJudge(sentence);
          for (CriminalRecord cr : crsTmp) {
            cr.setPrisonDate("原判");
          }
          criminalRecordList.addAll(crsTmp);
        } else if (nesJudge3.length > 0) { // 减刑判决
          List<CriminalRecord> crsTmp = parseSingleMinusJudge(sentence, signatureDate);
          for (CriminalRecord cr : crsTmp) {
            if (cr.getSentenceTime() > 0) {
              cr.setPrisonDate(nesJudge3[0].getSource() + cr.getSentenceTime());
              cr.setSentenceTime(0);
            }
          }
          criminalRecordList.addAll(crsTmp);
        }
      }
    }
    if (criminalRecordList.size() > 0) {
      if (context.rabbitInfo.getExtractInfo().get("info_减刑情况") == null) {
        context.rabbitInfo.getExtractInfo().put("info_减刑情况", criminalRecordList);
      } else {
        @SuppressWarnings("unchecked")
        List<CriminalRecord> crs =
            (List<CriminalRecord>) (context.rabbitInfo.getExtractInfo().get("info_减刑情况"));
        crs.addAll(criminalRecordList);
      }
    }
  }

  private static List<CriminalRecord> parseSingleJudge(String sentence) {
    // 罪名
    NamedEntity[] nes_crimeNames = NamedEntityRecognizer.recognizeCrimeNames(sentence);
    // 罪名类型
    NamedEntity[] nes_crimeTypes =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_TYPES);
    // 缓刑
    NamedEntity[] nes_probation =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_PROBATION);
    // 罚金
    NamedEntity[] nes_fine = NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_FINE);
    // 剥夺政治权利
    NamedEntity[] nes_confiscate_property =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_CONFISCATE_PROPERTY);
    // 判决里面的时间长度
    NamedEntity[] nes_tmp1 = NamedEntityRecognizer.recognizeDuration(sentence, "月");
    // 处理"终身"之类的表述，这里为了统一，将“终身”转成无穷大的时间
    NamedEntity[] nes_tmp2 =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_LIFELONG);
    List<NamedEntity> nes_tmp = new ArrayList<>();
    Collections.addAll(nes_tmp, nes_tmp1);
    for (NamedEntity ne : nes_tmp2) {
      Double val = Double.MAX_VALUE;
      ne.setInfo(val);
      nes_tmp.add(ne);
    }
    NamedEntity[] nes_duration = nes_tmp.toArray(new NamedEntity[nes_tmp.size()]);
    // 时间点
    NamedEntity[] nes_dates = NamedEntityRecognizer.recognizeTime(sentence);
    // 金额
    NamedEntity[] nes_money = NamedEntityRecognizer.recognizeMoney(sentence);
    // 逗号
    Integer[] commas = NamedEntityRecognizer.recognizeComma(sentence);
    NamedEntity[] nes_allEntities = NamedEntityRecognizer.combineEntities(KEYWORDS, nes_crimeNames,
        nes_crimeTypes, nes_probation, nes_fine, nes_confiscate_property, nes_duration, nes_dates,
        nes_money, NamedEntityRecognizer.convertCommasFormat(commas));

    return entityMatch(sentence, commas, nes_allEntities);
  }

  private static List<CriminalRecord> parseSingleMinusJudge(String sentence, String signatureDate) {
    NamedEntity[] nes_minus =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_MINUS_JUDGEMENT);
    if (nes_minus.length != 1) {
      return new ArrayList<>();
    }
    NamedEntity[] nes_judge_arrive =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_JUDGE_ARRIVE);
    NamedEntity[] nes_duration;
    List<NamedEntity> nes_minus_penalty = new ArrayList<>();
    NamedEntity[] nes_dates = new NamedEntity[] {};
    NamedEntity[] nes_crimeTypes = new NamedEntity[] {};
    if (nes_judge_arrive.length == 1) { // 包含“裁定送达”关键词
      nes_minus_penalty.add(new NamedEntity("减去余刑", -100, null));
      nes_minus[0].setOffset(-200);
      if (!TextUtils.isEmpty(signatureDate)) { // 如果不存在时间，默认是落款日期；并且将"减(去|为)"之前的内容去掉
        List<NamedEntity> lneTmp = new ArrayList<>();
        lneTmp.add(new NamedEntity(signatureDate, -300, signatureDate));
        nes_dates = lneTmp.toArray(new NamedEntity[lneTmp.size()]);
      }
    } else {
      // 时间点
      nes_dates = NamedEntityRecognizer.recognizeTime(sentence);
      if ((nes_dates.length == 0) && (!TextUtils.isEmpty(signatureDate))) { // 如果不存在时间，默认是落款日期；并且将"减(去|为)"之前的内容去掉
        List<NamedEntity> lneTmp = new ArrayList<>();
        lneTmp.add(new NamedEntity(signatureDate, -100, signatureDate));
        nes_dates = lneTmp.toArray(new NamedEntity[lneTmp.size()]);
        NamedEntity[] nesTmp =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_MINUS_JUDGEMENT);
        if (nesTmp.length != 0) {
          Integer[] commaTmp = NamedEntityRecognizer.recognizeComma(sentence);
          int pos = NamedEntityRecognizer.binarySearch(commaTmp, nesTmp[0].getOffset());
          if (pos != -1) {
            sentence = sentence.substring(commaTmp[pos] + 1, sentence.length());
          }
        }
      }
      // 罪名类型
      nes_crimeTypes = NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_TYPES);
    }
    // 判决里面的时间长度
    NamedEntity[] nes_tmp1 = NamedEntityRecognizer.recognizeDuration(sentence, "月");
    // 处理"终身"之类的表述，这里为了统一，将“终身”转成无穷大的时间
    NamedEntity[] nes_tmp2 =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_LIFELONG);
    List<NamedEntity> nes_tmp = new ArrayList<>();
    for (NamedEntity ne : nes_minus_penalty) {
      nes_tmp.add(ne);
    }
    Collections.addAll(nes_tmp, nes_tmp1);
    for (NamedEntity ne : nes_tmp2) {
      Double val = Double.MAX_VALUE;
      ne.setInfo(val);
      nes_tmp.add(ne);
    }
    nes_duration = nes_tmp.toArray(new NamedEntity[nes_tmp.size()]);
    // 缓刑
    NamedEntity[] nes_probation =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_PROBATION);
    // 罚金
    NamedEntity[] nes_fine = NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_FINE);
    // 剥夺政治权利
    NamedEntity[] nes_confiscate_property =
        NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_CONFISCATE_PROPERTY);
    // 金额
    NamedEntity[] nes_money = NamedEntityRecognizer.recognizeMoney(sentence);
    // 逗号
    Integer[] commas = NamedEntityRecognizer.recognizeComma(sentence);
    NamedEntity[] nes_allEntities = NamedEntityRecognizer.combineEntities(MINUS_KEYWORDS, nes_minus,
        nes_crimeTypes, nes_probation, nes_fine, nes_confiscate_property, nes_duration, nes_dates,
        nes_money, NamedEntityRecognizer.convertCommasFormat(commas));

    return minusEntityMatch(sentence, commas, nes_allEntities);
  }

  private static final Map<String, Integer> conditionsOfCrimeSplits =
      new HashMap<String, Integer>();
  static {
    conditionsOfCrimeSplits.put("罪名", 1);
    conditionsOfCrimeSplits.put("罪名类型", 1);
  }

  /**
   * 用罪名分成几段，然后依次加入list,如果某个罪名所属的list已经加入了某个属性，则不再继续加入。
   */
  @SuppressWarnings("unchecked")
  private static List<CriminalRecord> entityMatch(String sentence, Integer[] commas,
      NamedEntity[] nes_allEntities) {

    List<CriminalRecord> criminalRecordList = new ArrayList<>();
    List<List<NamedEntity>> result =
        NamedEntityRecognizer.splitEntityByType(nes_allEntities, conditionsOfCrimeSplits);
    // 需要组合 罪名类型 + 罪名 + 判处时间长度
    for (List<NamedEntity> lnes : result) {
      Map<String, NamedEntity[]> strArrayMap = NamedEntityRecognizer.regroupEntities(lnes);
      NamedEntity[] nes_date = strArrayMap.getOrDefault("判处时间", new NamedEntity[0]);
      NamedEntity[] nes_crime = strArrayMap.getOrDefault("罪名", new NamedEntity[0]);
      NamedEntity[] nes_crimeTypes = strArrayMap.getOrDefault("罪名类型", new NamedEntity[0]);
      NamedEntity[] nes_fine = strArrayMap.getOrDefault("罚金", new NamedEntity[0]);
      NamedEntity[] nes_confiscate_property =
          strArrayMap.getOrDefault("剥夺政治权利", new NamedEntity[0]);
      CriminalRecord cr = new CriminalRecord();
      if (nes_date.length > 0) {
        List<String> tmpDate = new ArrayList<>();
        for (NamedEntity ne : nes_date) {
          tmpDate.add(DateHandler.convertDateTimeFormat((String) (ne.getInfo())));
        }
        cr.setTmpDate(tmpDate);
      }
      // 罪名
      if (nes_crime.length != 0) {
        cr.setCriminalRecordName((List<String>) (nes_crime[0].getInfo()));
      }
      NamedEntity[] nes_probation = strArrayMap.getOrDefault("缓刑", new NamedEntity[0]);
      NamedEntity[] nes_duration = strArrayMap.getOrDefault("判处时间长度", new NamedEntity[0]);
      NamedEntity[] nes_money = strArrayMap.getOrDefault("金额", new NamedEntity[0]);
      List<NamedEntity[]> lne_crimeType_duration = NamedEntityRecognizer.entityMatch(sentence,
          commas, nes_crimeTypes, nes_duration, true, false);
      List<NamedEntity[]> lne_probation_duration = NamedEntityRecognizer.entityMatch(sentence,
          commas, nes_probation, nes_duration, true, false);
      List<NamedEntity[]> lne_confiscate_property_duration = NamedEntityRecognizer
          .entityMatch(sentence, commas, nes_confiscate_property, nes_duration, true, false);
      List<NamedEntity[]> lne_fine_money =
          NamedEntityRecognizer.entityMatch(sentence, commas, nes_fine, nes_money, true, false);
      String sentenceType = null;
      int probationTime = 0;
      int sentenceTime = 0;
      double fine = 0.0f;
      int confiscateProperty = 0;
      // 判处时间和类型
      if (lne_crimeType_duration.size() != 0) {
        sentenceType = lne_crimeType_duration.get(0)[0].getSource();
        sentenceTime = (int) (double) (lne_crimeType_duration.get(0)[1].getInfo());
      }
      // 没有判罚的刑期，判断是不是单处罚金，无期徒刑，免于刑事处罚
      for (NamedEntity ne : nes_crimeTypes) {
        if ("免于刑事处罚".equals(ne.getSource())) {
          sentenceType = "免于刑事处罚";
          break;
        } else if ("无期徒刑".equals(ne.getSource())) {
          sentenceType = "无期徒刑";
          break;
        } else if ("死刑".equals(ne.getSource())) {
          sentenceType = "死刑";
          break;
        }
      }
      // 缓刑时间
      if (lne_probation_duration.size() != 0) {
        probationTime = (int) (double) (lne_probation_duration.get(0)[1].getInfo());
      }
      // 剥夺政治权利
      if (lne_confiscate_property_duration.size() > 0) {
        confiscateProperty = (int) (double) (lne_confiscate_property_duration.get(0)[1].getInfo());
      }
      // 罚金
      if (lne_fine_money.size() > 0) {
        fine = (double) (lne_fine_money.get(0)[1].getInfo());
      }
      NamedEntity[] nes_limit_minus =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_LIMIT_MINUS);
      if (nes_limit_minus.length > 0) {
        cr.setSentenceType(sentenceType + "(限制减刑)");
      } else {
        cr.setSentenceType(sentenceType);
      }
      cr.setSentenceTime(sentenceTime);
      cr.setProbationTime(probationTime);
      cr.setFine(fine);
      cr.setConfiscateProperty(confiscateProperty);
      // 设置完毕，加入结果集
      criminalRecordList.add(cr);
    }

    return criminalRecordList;
  }

  private static List<CriminalRecord> minusEntityMatch(String sentence, Integer[] commas,
      NamedEntity[] nes_allEntities) {
    List<CriminalRecord> criminalRecordList = new ArrayList<>();

    List<List<NamedEntity>> result =
        NamedEntityRecognizer.splitEntityByType(nes_allEntities, new HashMap<>());
    Map<String, NamedEntity[]> strArrayMap = NamedEntityRecognizer.regroupEntities(result.get(0));
    NamedEntity[] nes_date = strArrayMap.getOrDefault("判处时间", new NamedEntity[0]);
    NamedEntity[] nes_minus = strArrayMap.getOrDefault("减刑", new NamedEntity[0]);
    NamedEntity[] nes_crimeTypes = strArrayMap.getOrDefault("罪名类型", new NamedEntity[0]);
    List<NamedEntity[]> lne_minus_crimeType =
        NamedEntityRecognizer.entityMatch(sentence, commas, nes_minus, nes_crimeTypes, true, false);
    if (lne_minus_crimeType.size() > 0) {
      nes_crimeTypes = new NamedEntity[] {lne_minus_crimeType.get(0)[1]};
    }
    NamedEntity[] nes_fine = strArrayMap.getOrDefault("罚金", new NamedEntity[0]);
    NamedEntity[] nes_confiscate_property = strArrayMap.getOrDefault("剥夺政治权利", new NamedEntity[0]);
    NamedEntity[] nes_probation = strArrayMap.getOrDefault("缓刑", new NamedEntity[0]);
    NamedEntity[] nes_duration = strArrayMap.getOrDefault("判处时间长度", new NamedEntity[0]);
    NamedEntity[] nes_money = strArrayMap.getOrDefault("金额", new NamedEntity[0]);
    List<NamedEntity[]> lne_crimeType_duration = NamedEntityRecognizer
        .entityMatchSupportOneToMore(commas, nes_crimeTypes, nes_duration, true);
    List<NamedEntity[]> lne_probation_duration = NamedEntityRecognizer.entityMatch(sentence, commas,
        nes_probation, nes_duration, true, false);
    List<NamedEntity[]> lne_confiscate_property_duration = NamedEntityRecognizer
        .entityMatch(sentence, commas, nes_confiscate_property, nes_duration, true, false);
    List<NamedEntity[]> lne_fine_money =
        NamedEntityRecognizer.entityMatch(sentence, commas, nes_fine, nes_money, true, false);
    String sentenceType = null;
    int probationTime = 0;
    int sentenceTime = 0;
    double fine = 0.0f;
    int confiscateProperty = 0;
    // 判处时间和类型
    if (lne_crimeType_duration.size() == 1) {
      sentenceType = lne_crimeType_duration.get(0)[0].getSource();
      sentenceTime = (int) (double) (lne_crimeType_duration.get(0)[1].getInfo());
      CriminalRecord cr = new CriminalRecord();
      // 日期
      if (nes_date.length > 0) {
        List<String> tmpDate = new ArrayList<>();
        for (NamedEntity ne : nes_date) {
          tmpDate.add(DateHandler.convertDateTimeFormat((String) (ne.getInfo())));
        }
        cr.setTmpDate(tmpDate);
      }
      cr.setSentenceType(sentenceType);
      cr.setSentenceTime(sentenceTime);
      criminalRecordList.add(cr);

    } else if (lne_crimeType_duration.size() > 1) { // 存在多个匹配结果
      for (int i = 0; i < lne_crimeType_duration.size(); i++) {
        sentenceType = lne_crimeType_duration.get(i)[0].getSource();
        sentenceTime = (int) (double) (lne_crimeType_duration.get(i)[1].getInfo());
        CriminalRecord cr = new CriminalRecord();
        // 日期
        if (nes_date.length > 0) {
          List<String> tmpDate = new ArrayList<>();
          tmpDate.add(DateHandler.convertDateTimeFormat((String) (nes_date[i].getInfo())));
          cr.setTmpDate(tmpDate);
        }
        cr.setSentenceType(sentenceType);
        cr.setSentenceTime(sentenceTime);
        criminalRecordList.add(cr);
      }
    } else if (lne_crimeType_duration.size() == 0) {
      // 没有判罚的刑期，判断是不是单处罚金，无期徒刑，免于刑事处罚
      for (NamedEntity ne : nes_crimeTypes) {
        if ("免于刑事处罚".equals(ne.getSource())) {
          sentenceType = "免于刑事处罚";
          break;
        } else if ("无期徒刑".equals(ne.getSource())) {
          sentenceType = "无期徒刑";
          break;
        } else if ("死刑".equals(ne.getSource())) {
          sentenceType = "死刑";
          break;
        }
      }
      if (!TextUtils.isEmpty(sentenceType)) {
        CriminalRecord cr = new CriminalRecord();
        if (nes_date.length > 0) {
          List<String> tmpDate = new ArrayList<>();
          for (NamedEntity ne : nes_date) {
            tmpDate.add(DateHandler.convertDateTimeFormat((String) (ne.getInfo())));
          }
          cr.setTmpDate(tmpDate);
        }
        cr.setSentenceType(sentenceType);
        criminalRecordList.add(cr);
      } else {
        for (NamedEntity ne : nes_duration) {
          if ("减去余刑".equals(ne.getSource())) {
            sentenceType = "减去余刑";
            break;
          }
        }
        if (!TextUtils.isEmpty(sentenceType)) {
          CriminalRecord cr = new CriminalRecord();
          if (nes_date.length > 0) {
            List<String> tmpDate = new ArrayList<>();
            for (NamedEntity ne : nes_date) {
              tmpDate.add(DateHandler.convertDateTimeFormat((String) (ne.getInfo())));
            }
            cr.setTmpDate(tmpDate);
          }
          cr.setPrisonDate(sentenceType);
          criminalRecordList.add(cr);
        }
      }
    }
    // 缓刑时间
    if (lne_probation_duration.size() != 0) {
      probationTime = (int) (double) (lne_probation_duration.get(0)[1].getInfo());
    }
    // 剥夺政治权利
    if (lne_confiscate_property_duration.size() > 0) {
      confiscateProperty = (int) (double) (lne_confiscate_property_duration.get(0)[1].getInfo());
    }
    // 罚金
    if (lne_fine_money.size() > 0) {
      fine = (double) (lne_fine_money.get(0)[1].getInfo());
    }
    for (CriminalRecord cr : criminalRecordList) {
      cr.setProbationTime(probationTime);
      cr.setFine(fine);
      cr.setConfiscateProperty(confiscateProperty);
    }

    return criminalRecordList;
  }

}
