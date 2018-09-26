package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.CriminalRecord;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 目前该类还不能直接取代CriminalRecordExtractor。因为CriminalRecordExtractor是为法院定制的，
 * 而CriminalRecordExtractor2是嵌在PeopleBaseInfoExtractor中的
 * 后续需要将PeopleBaseInfoExtractor完善并适用于法院，这样CriminalRecordExtractor就可以删去，
 * 同意用CriminalRecordExtractor2了。 Created by Administrator on 2017/7/5.
 */
/**
 * 检察院前科
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class CriminalRecordExtractor2 {
  private Context context;

  public CriminalRecordExtractor2(Context context) {
    this.context = context;
  }

  private final static String[] keywords = new String[] {"因犯", "罪名", "罪名类型", "缓刑", "罚金", "剥夺政治权利",
      "没收财产", "判处时间长度", "判处时间", "公安局", "法院", "释放", ","};
  // 刑事处罚种类
  private final static Set<String> principalCriminalPenalty = new HashSet<String>();
  // 行政处罚种类
  private final static Set<String> adminPenalty = new HashSet<String>();
  private static final Map<String, Integer> conditionsOfCrimeSplits =
      new HashMap<String, Integer>();
  static {
    principalCriminalPenalty.add("拘役");
    principalCriminalPenalty.add("管制");
    principalCriminalPenalty.add("有期徒刑");
    principalCriminalPenalty.add("无期徒刑");
    principalCriminalPenalty.add("死刑");
    adminPenalty.add("行政拘留");
    adminPenalty.add("劳教");
    adminPenalty.add("劳动教养");
    conditionsOfCrimeSplits.put("因犯", 1);
    conditionsOfCrimeSplits.put("罪名", 1);
    conditionsOfCrimeSplits.put("罪名类型", 1);
    conditionsOfCrimeSplits.put("公安局", 1);
    conditionsOfCrimeSplits.put("法院", 1);
    conditionsOfCrimeSplits.put("释放", 1);
  }
  // 004FA3045DAE11E5BBD9000C29217B49
  private static final Pattern[] patternsRecordAction = {Pattern.compile("因犯?|由于|犯")};
  private static final Pattern[] patternsIrrelevantAction = {Pattern.compile("涉嫌")};
  // 刑事+行政处罚类型
  private static final Pattern[] patternsTypes =
      {Pattern.compile("拘役|管制|有期徒刑|无期徒刑|死刑|免于刑事处罚|行政拘留|劳教|劳动教养")};
  private static final Pattern[] patternsPolice = {Pattern.compile("派出所|公安分?局|公安机关")};
  private static final Pattern[] patternsReleaseAction = {Pattern.compile("释放|出狱")};
  private static final Pattern[] patternProbation = {Pattern.compile("缓刑")};
  private static final Pattern[] patternFine = {Pattern.compile("罚金")};
  private static final Pattern[] patternExpropriation =
      {Pattern.compile("没收[^,，。\\.；;]*?财产|财([产物])[^\\.。]*?没收")};
  private static final Pattern[] patternConfiscateProperty = {Pattern.compile("剥夺政治权利")};
  private static final Pattern[] patternLifelong = {Pattern.compile("(?<=剥夺政治权利)终身")};

  // 根据前科罪名、是否受过刑事处罚 罪名 推理出 是否因xxx罪受过刑事处罚；
  // 根据前科罪名、一年内是否受过行政处罚 罪名 推理出 一年内是否因xxx罪受过刑事处罚
  @SuppressWarnings("unchecked")
  public void extractRecordPenalty(String signatureDateKey, String crime,
      String crimeCriminalPenaltyKey, String crimeAdminPenaltyKey) {
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]) == null) {
      return;
    }
    String signatureDate = null;
    if (context.rabbitInfo.getExtractInfo().get(signatureDateKey) != null) {
      signatureDate = (String) (context.rabbitInfo.getExtractInfo().get(signatureDateKey));
    }
    List<People> peopleAattr = (List<People>) (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]));
    for (People people : peopleAattr) {
      if (people.getRecordlist() == null) {
        continue;
      }
      List<CriminalRecord> recordList = people.getRecordlist();
      for (CriminalRecord record : recordList) {
        if (record.getCriminalRecordName() == null) {
          continue;
        }
        if (record.getCriminalRecordName().contains(crime)) {
          String sentenceType = record.getSentenceType();
          // 是否受过刑事处罚和一年内受过行政处罚
          if (principalCriminalPenalty.contains(sentenceType)) {
            people.getPeopleAttrMap().put(crimeCriminalPenaltyKey, true);
          } else if (adminPenalty.contains(sentenceType)) {
            String judgeDate = record.getJudgeDate();
            if ((judgeDate != null) && (signatureDate != null)) {
              try {
                int dayDiff = DateHandler.getDayDiff(DateHandler.makeDateTime(judgeDate),
                    DateHandler.makeDateTime(signatureDate));
                if (dayDiff < 0) {
                  dayDiff = dayDiff * (-1);
                }
                if (dayDiff <= 365) {
                  people.getPeopleAttrMap().put(crimeAdminPenaltyKey, true);
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }

        }
      }
    }
  }

  // 根据前科罪名、是否受过刑事处罚 罪名 推理出 是否因xxx罪受过刑事处罚；
  // 根据前科罪名、几年内是否受过行政处罚 罪名 推理出 几年内是否因xxx罪受过刑事处罚
  public void extractRecordPenalty(String signatureDateKey, String crime,
      String crimeAdminPenaltyKey, int num) {
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]) == null) {
      return;
    }
    String signatureDate = null;
    if (context.rabbitInfo.getExtractInfo().get(signatureDateKey) != null) {
      signatureDate = (String) (context.rabbitInfo.getExtractInfo().get(signatureDateKey));
    }
    @SuppressWarnings("unchecked")
    List<People> peopleAattr = (List<People>) (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]));
    for (People people : peopleAattr) {
      if (people.getRecordlist() == null) {
        continue;
      }
      List<CriminalRecord> recordList = people.getRecordlist();
      for (CriminalRecord record : recordList) {
        if (record.getCriminalRecordName() == null) {
          continue;
        }
        if (record.getCriminalRecordName().contains(crime)) {
          String sentenceType = record.getSentenceType();
          // 是否受过刑事处罚和几年内受过行政处罚
          if (adminPenalty.contains(sentenceType)) {
            String judgeDate = record.getJudgeDate();
            if ((judgeDate != null) && (signatureDate != null)) {
              try {
                int dayDiff = DateHandler.getDayDiff(DateHandler.makeDateTime(judgeDate),
                    DateHandler.makeDateTime(signatureDate));
                if (dayDiff < 0) {
                  dayDiff = dayDiff * (-1);
                }
                if (dayDiff <= 365 * num) {
                  people.getPeopleAttrMap().put(crimeAdminPenaltyKey, true);
                }
              } catch (Exception e) {
                e.printStackTrace();
              }
            }
          }

        }
      }
    }
  }

  public static void extractCriminalRecord(String paragraph, String courtName, People people) {
    List<CriminalRecord> criminalRecordList = new ArrayList<>();
    NamedEntity NES_LAST_COURT = null;
    Integer[] commas;
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
    for (String sentence : sentences) {
      NamedEntity[] nes_irrelevant =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsIrrelevantAction);
      // 如果短句中涉嫌犯罪和前科之间没有用分号或者逗号分割,则将涉嫌的句子部分去掉再处理
      if (nes_irrelevant.length != 0) {
        // 找出涉嫌关键词最左边的标点符号的位置，从该位置到结束把sentence 截断。
        commas = NamedEntityRecognizer.recognizeComma(sentence);
        // “涉嫌”关键词左边最靠近的位置
        int pos = NamedEntityRecognizer.binarySearch(commas, nes_irrelevant[0].getOffset());
        if (pos == -1) {
          continue;
        } else {
          // 改变sentence，即去掉涉嫌后面的句子
          sentence = sentence.substring(0, commas[pos]);
        }
      }
      // 前科表述
      NamedEntity[] nes_action =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsRecordAction);
      if (nes_action.length == 0) {
        continue;
      }
      // 罪名
      NamedEntity[] nes_crimeNames = NamedEntityRecognizer.recognizeCrimeNames(sentence);
      if (nes_crimeNames.length == 0) {
        continue;
      }
      // 罪名类型
      NamedEntity[] nes_crimeTypes =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsTypes);
      if (nes_crimeTypes.length == 0) {
        continue;
      }
      // 缓刑
      NamedEntity[] nes_probation =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternProbation);
      // 罚金
      NamedEntity[] nes_fine = NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternFine);
      // 剥夺政治权利
      NamedEntity[] nes_confiscate_property =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternConfiscateProperty);
      // 没收财产
      NamedEntity[] nes_expropriation =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternExpropriation);
      // 判决里面的时间长度
      NamedEntity[] nes_tmp1 = NamedEntityRecognizer.recognizeDuration(sentence, "月");
      // 处理"终身"之类的表述，这里为了统一，将“终身”转成无穷大的时间
      NamedEntity[] nes_tmp2 =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternLifelong);
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
      // 公安局
      NamedEntity[] nes_policeStation =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsPolice);;
      // 法院 这个函数要优化啊，最好搞成static, 不要每次调用它时都要初始化一个NamedEntityRecognizer
      NamedEntity[] nes_court =
          NamedEntityRecognizer.recognizeCourtName(sentence, true, courtName, NES_LAST_COURT);;
      if (nes_court.length != 0) {
        NES_LAST_COURT = nes_court[nes_court.length - 1];
      }
      // 释放
      NamedEntity[] nes_release =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsReleaseAction);
      // 逗号
      commas = NamedEntityRecognizer.recognizeComma(sentence);
      NamedEntity[] nes_allEntities = NamedEntityRecognizer.combineEntities(keywords, nes_action,
          nes_crimeNames, nes_crimeTypes, nes_probation, nes_fine, nes_confiscate_property,
          nes_expropriation, nes_duration, nes_dates, nes_policeStation, nes_court, nes_release,
          NamedEntityRecognizer.convertCommasFormat(commas));
      criminalRecordList.addAll(entityMatch(sentence, commas, nes_allEntities));
    }
    if (criminalRecordList.size() != 0) {
      people.setRecordlist(criminalRecordList);
      List<String> recordNames = new ArrayList<>();
      for (CriminalRecord record : criminalRecordList) {
        List<String> names = record.getCriminalRecordName();
        for (String name : names) {
          if (!recordNames.contains(name)) {
            recordNames.add(name);
          }
        }
      }
      if (recordNames.size() > 0) {
        people.getPeopleAttrMap().put(InfoPointKey.info_record_crime[InfoPointKey.mode],
            recordNames);
      }
      people.getPeopleAttrMap().put(InfoPointKey.info_record[InfoPointKey.mode], true);

    }
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
      NamedEntity[] nes_crime = strArrayMap.getOrDefault("罪名", new NamedEntity[0]);
      if (nes_crime.length == 0) {
        continue;
      }
      NamedEntity[] nes_crimeTypes = strArrayMap.getOrDefault("罪名类型", new NamedEntity[0]);
      NamedEntity[] nes_fine = strArrayMap.getOrDefault("罚金", new NamedEntity[0]);
      NamedEntity[] nes_expropriation = strArrayMap.getOrDefault("没收财产", new NamedEntity[0]);
      NamedEntity[] nes_confiscate_property =
          strArrayMap.getOrDefault("剥夺政治权利", new NamedEntity[0]);
      if ((nes_crimeTypes.length == 0) && ((nes_fine.length == 0) || (nes_expropriation.length == 0)
          || (nes_confiscate_property.length == 0))) {
        continue;
      }
      CriminalRecord cr = new CriminalRecord();
      // 罪名
      if (nes_crime.length != 0) {
        cr.setCriminalRecordName((List<String>) (nes_crime[0].getInfo()));
      }
      NamedEntity[] nes_probation = strArrayMap.getOrDefault("缓刑", new NamedEntity[0]);
      NamedEntity[] nes_duration = strArrayMap.getOrDefault("判处时间长度", new NamedEntity[0]);
      List<NamedEntity[]> lne_crimeType_duration = NamedEntityRecognizer.entityMatch(sentence,
          commas, nes_crimeTypes, nes_duration, true, false);
      List<NamedEntity[]> lne_probation_duration = NamedEntityRecognizer.entityMatch(sentence,
          commas, nes_probation, nes_duration, true, false);
      List<NamedEntity[]> lne_confiscate_property_duration = NamedEntityRecognizer
          .entityMatch(sentence, commas, nes_confiscate_property, nes_duration, true, false);
      String sentenceType = null;
      int probationTime = 0;
      int sentenceTime = 0;
      double fine = 0.0f;
      boolean expropriation = false;
      int confiscateProperty = 0;
      // 判处时间和类型
      if (lne_crimeType_duration.size() != 0) {
        sentenceType = lne_crimeType_duration.get(0)[0].getSource();
        sentenceTime = (int) (double) (lne_crimeType_duration.get(0)[1].getInfo());
      }
      // 没有判罚的刑期，判断是不是单处罚金，无期徒刑，免于刑事处罚
      for (NamedEntity ne : nes_crimeTypes) {
        if ("免于刑事处罚".equals(ne.getType())) {
          sentenceType = "免于刑事处罚";
          break;
        } else if ("无期徒刑".equals(ne.getType())) {
          sentenceType = "无期徒刑";
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
      // 没收财产
      if (nes_expropriation.length > 0) {
        expropriation = true;
      }
      cr.setSentenceType(sentenceType);
      cr.setSentenceTime(sentenceTime);
      cr.setProbationTime(probationTime);
      cr.setFine(fine);
      cr.setExpropriation(expropriation);
      cr.setConfiscateProperty(confiscateProperty);
      // 公安局逮捕时间，法院判处时间，释放时间
      // 找每个关键词左边的时间，如果没有找到则找到右边
      NamedEntity[] nes_org = NamedEntityRecognizer.combineEntities(null,
          strArrayMap.getOrDefault("公安局", new NamedEntity[0]),
          strArrayMap.getOrDefault("法院", new NamedEntity[0]),
          strArrayMap.getOrDefault("释放", new NamedEntity[0]));
      NamedEntity[] nes_date = strArrayMap.getOrDefault("判处时间", new NamedEntity[0]);
      if (nes_org.length != 0 && nes_date.length != 0) {
        List<NamedEntity[]> lne_date_org =
            NamedEntityRecognizer.entityMatch(sentence, commas, nes_date, nes_org, false, true);
        if (lne_date_org.size() != 0) {
          // 时间
          for (NamedEntity[] nes : lne_date_org) {
            if ("公安局".equals(nes[1].getType())) {
              cr.setPolice((String) (nes[1].getInfo()));
              String time = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
              if (null != time) {
                cr.setPrisonDate(time);
              }
            } else if ("法院".equals(nes[1].getType())) {
              cr.setCourt((String) (nes[1].getInfo()));
              String time = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
              if (null != time) {
                cr.setJudgeDate(time);
              }
            } else if ("释放".equals(nes[1].getType())) {
              String time = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
              if (null != time) {
                cr.setReleaseDate(time);
              }
            }
          }
        }
      } else if (nes_date.length != 0) {
        // 公安局、法院、释放等关键字都没有，则认为时间就是判处时间
        // TODO 考虑多个时间的情况
        String time = DateHandler.convertDateTimeFormat((String) nes_date[0].getInfo());
        if (null != time)
          cr.setJudgeDate(time);
      }
      // 设置完毕，加入结果集
      criminalRecordList.add(cr);
    }
    return criminalRecordList;
  }

}
