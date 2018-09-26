package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.CriminalRecord;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.tool.algorithm.personage.PeopleType;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 前科-法院
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class CriminalRecordExtractor {

  private Context context;

  public CriminalRecordExtractor(Context context) {
    this.context = context;
  }

  public void extract() {
    NamedEntityRecognizer ner = new NamedEntityRecognizer(this.context);
    @SuppressWarnings("unchecked")
    List<People> peopleList = (List<People>) context.rabbitInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<>());
    for (People people : peopleList) {
      if (people.getPtype().equals(PeopleType.DEFENDANT)) {
        parseCriminalRecord(context.getAllUnits().get(people.getPosition().getPara()), ner, people);
      }
    }
  }

  // 004FA3045DAE11E5BBD9000C29217B49
  private static final Pattern[] patternsRecordAction = {Pattern.compile("因犯?|由于")};
  private static final Pattern[] patternsIrrelevantAction = {Pattern.compile("涉嫌")};
  private static final Pattern[] patternsTypes = {Pattern.compile("有期徒刑|拘役|管制|行政拘留|劳教|劳动教养|缓刑")};
  private static final Pattern[] patternsPolice = {Pattern.compile("派出所|公安分?局|公安机关")};
  private static final Pattern[] patternsReleaseAction = {Pattern.compile("释放|出狱")};

  private void parseCriminalRecord(String paragraph, NamedEntityRecognizer ner, People people) {
    List<CriminalRecord> criminalRecordList = new ArrayList<>();
    NamedEntity NES_LAST_COURT = null;
    String timeBase = null;
    Integer[] commas;
    for (String sentence : DocumentUtils.splitOneParagraphByPeriod(paragraph)) {
      // Handling here
      NamedEntity[] nes_irrelevant =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsIrrelevantAction);
      // 如果是涉嫌犯罪，跳出循环
      /*
       * TODO 如果涉嫌犯罪和前科之间没有用分号或者逗号分割，则把前科信息丢掉了。改进办法：手工把涉嫌后面的语句设置为空。即改变sentence
       */
      if (nes_irrelevant.length != 0) {
        // 找出涉嫌关键词最左边的标点符号的位置，从该位置到结束把sentence 截断。
        commas = NamedEntityRecognizer.recognizeComma(sentence);
        // “涉嫌”关键词左边最靠近的位置
        int pos = NamedEntityRecognizer.binarySearch(commas, nes_irrelevant[0].getOffset());
        if (pos == -1)
          continue;
        else {
          // 改变sentence，即去掉涉嫌后面的句子
          sentence = sentence.substring(0, commas[pos]);
        }
      }
      NamedEntity[] nes_action =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsRecordAction);
      if (nes_action.length == 0)
        continue;
      NamedEntity[] nes_crimeTypes =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsTypes);
      if (nes_crimeTypes.length == 0)
        continue;
      NamedEntity[] nes_crimeNames = NamedEntityRecognizer.recognizeCrimeNames(sentence);
      if (nes_crimeNames.length == 0)
        continue;
      NamedEntity[] nes_duration = NamedEntityRecognizer.recognizeDuration(sentence, "月");

      // 时间位置，考虑了上下文的时间提取
      DateHandler dh = new DateHandler(sentence, timeBase);
      NamedEntity[] nes_dates = NamedEntityRecognizer.recognizeTime(dh);
      if (nes_dates.length > 0) {
        timeBase = dh.getTimeBase();
      }

      NamedEntity[] nes_policeStation = findPoliceStation(sentence, ner);
      NamedEntity[] nes_court = findCourt(sentence, NES_LAST_COURT, ner);
      if (nes_court.length != 0)
        NES_LAST_COURT = nes_court[nes_court.length - 1];
      NamedEntity[] nes_release =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsReleaseAction);
      commas = NamedEntityRecognizer.recognizeComma(sentence);
      NamedEntity[] nes_allEntities = NamedEntityRecognizer.combineEntities(
          new String[] {"因犯", "罪名类型", "罪名", "判处时间长度", "判处时间", "公安局", "法院", "释放", ","}, nes_action,
          nes_crimeTypes, nes_crimeNames, nes_duration, nes_dates, nes_policeStation, nes_court,
          nes_release, NamedEntityRecognizer.convertCommasFormat(commas));
      criminalRecordList.addAll(entityMatch(sentence, ner, commas, nes_allEntities));
    }
    if (criminalRecordList.size() != 0) {
      people.setRecordlist(criminalRecordList);
      List<String> recordNames = new ArrayList<String>();
      for (CriminalRecord record : criminalRecordList) {
        List<String> names = record.getCriminalRecordName();
        recordNames.addAll(names);
      }
      if (recordNames.size() > 0) {
        people.getPeopleAttrMap().put(InfoPointKey.info_record_crime[InfoPointKey.mode],
            recordNames);
      }
      people.getPeopleAttrMap().put(InfoPointKey.info_record[InfoPointKey.mode], true);
    }
  }

  private static final Map<String, Integer> conditionsOfCrimeSplits =
      new HashMap<String, Integer>();
  static {
    conditionsOfCrimeSplits.put("因犯", 1);
    conditionsOfCrimeSplits.put("罪名", 1);
    conditionsOfCrimeSplits.put("公安局", 1);
    conditionsOfCrimeSplits.put("法院", 1);
    conditionsOfCrimeSplits.put("释放", 1);
  }

  /**
   * 用罪名分成几段，然后依次加入list,如果某个罪名所属的list已经加入了某个属性，则不再继续加入。
   */
  private List<CriminalRecord> entityMatch(String sentence, NamedEntityRecognizer ner,
      Integer[] commas, NamedEntity[] nes_allEntities) {

    List<CriminalRecord> criminalRecordList = new ArrayList<>();
    List<List<NamedEntity>> result =
        NamedEntityRecognizer.splitEntityByType(nes_allEntities, conditionsOfCrimeSplits);
    // 需要组合 罪名类型 + 罪名 + 判处时间长度
    for (List<NamedEntity> lnes : result) {
      Map<String, NamedEntity[]> strArrayMap = NamedEntityRecognizer.regroupEntities(lnes);
      if (strArrayMap.containsKey("判处时间长度") && strArrayMap.containsKey("罪名类型")
          && strArrayMap.containsKey("罪名")) {
        List<NamedEntity[]> lne_type_crime = NamedEntityRecognizer.entityMatch(sentence, commas,
            strArrayMap.get("罪名类型"), strArrayMap.get("判处时间长度"), true, false);
        if (lne_type_crime.size() > 0) {
          CriminalRecord cr = new CriminalRecord();

          NamedEntity[] nes_court = strArrayMap.getOrDefault("法院", new NamedEntity[0]);
          // 法院
          if (nes_court.length > 0) {
            cr.setCourt((String) nes_court[0].getInfo());
          }
          @SuppressWarnings("unchecked")
          List<String> tempCrimeNames = (List<String>) strArrayMap.get("罪名")[0].getInfo();
          // 罪名
          cr.setCriminalRecordName(tempCrimeNames);
          // 判处时间
          int probationTime = 0;
          String sentenceType = null;
          int sentenceTime = 0;
          for (NamedEntity[] nes : lne_type_crime) {
            if ("缓刑".equals(nes[0].getSource())) {
              probationTime = (int) (double) nes[1].getInfo();
            } else {
              sentenceType = nes[0].getSource();
              sentenceTime = (int) (double) nes[1].getInfo();
            }
          }
          if (sentenceType != null) {
            cr.setSentenceType(sentenceType);
            cr.setSentenceTime(sentenceTime);
            cr.setProbationTime(probationTime);
          }
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
                  String time = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
                  if (null != time)
                    cr.setPrisonDate(time);
                } else if ("法院".equals(nes[1].getType())) {
                  String time = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
                  if (null != time)
                    cr.setJudgeDate(time);
                } else if ("释放".equals(nes[1].getType())) {
                  String time = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
                  if (null != time)
                    cr.setReleaseDate(time);
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
      }
    }
    return criminalRecordList;
  }


  private NamedEntity[] findPoliceStation(String s, NamedEntityRecognizer ner) {
    return NamedEntityRecognizer.recognizeEntityByRegex(s, patternsPolice);
  }

  private NamedEntity[] findCourt(String s, NamedEntity NES_LAST_COURT, NamedEntityRecognizer ner) {
    return NamedEntityRecognizer.recognizeCourtName(s, true, null, NES_LAST_COURT);
    // return ner.recognizeEntityByRegex(s,PATTERNS_COURT);
  }
}
