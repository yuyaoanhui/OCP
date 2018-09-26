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
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.CriminalJudgement;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;

/**
 * 判罚信息
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class CriminalSentenceExtractor {

  private Context context;
  private ReferLigitantRelatedInfoExtrator referExtractor;

  public CriminalSentenceExtractor(Context context) {
    this.context = context;
    referExtractor = new ReferLigitantRelatedInfoExtrator(context);
  }

  private final static String[] keywords =
      new String[] {"被告", "罪名", "判处时间长度", "罪名类型", "缓刑", "罚金", "没收财产", "剥夺政治权利", "金额", "合并执行", ","};
  // 刑事处罚种类
  private final static Set<String> aboveFixedTermPenalty = new HashSet<>();
  static {
    aboveFixedTermPenalty.add("有期徒刑");
    aboveFixedTermPenalty.add("无期徒刑");
    aboveFixedTermPenalty.add("死刑");
  }
  // 行政处罚种类
  private final static Set<String> underFixedTermPenalty = new HashSet<>();
  static {
    underFixedTermPenalty.add("行政拘留");
    underFixedTermPenalty.add("劳教");
    underFixedTermPenalty.add("劳动教养");
    underFixedTermPenalty.add("拘役");
    underFixedTermPenalty.add("管制");
  }

  public void extract(List<String> paragraphs) {
    // 抽取判罚信息
    parseCriminalJudgement(paragraphs);
    // 抽取羁押开始结束日期
    parseCustody(paragraphs);
  }

  private static final Pattern patternCrimeName = Pattern.compile("犯[\u4e00-\u9fa5、]+罪");
  private static final Pattern patternPunishmentTypes =
      Pattern.compile("拘役|管制|有期徒刑|无期徒刑|死刑|免于刑事处罚|行政拘留|劳教|劳动教养");
  private static final Pattern patternProbation = Pattern.compile("缓刑|缓期");
  private static final Pattern patternFine = Pattern.compile("罚金");
  private static final Pattern patternExpropriation =
      Pattern.compile("没收[^,，。\\.；;]*?财产|财([产物])[^\\.。]*?没收");
  private static final Pattern patternConfiscateProperty = Pattern.compile("剥夺政治权利");
  private static final Pattern patternLifelong = Pattern.compile("(?<=剥夺政治权利)终身");
  private static final Pattern patternCombine = Pattern.compile("合并|决定");
  private static final Pattern patternNoCrime = Pattern.compile("无罪");
  private static final Pattern patternCustodyAction = Pattern.compile("折抵刑期");

  @SuppressWarnings("unchecked")
  private void parseCriminalJudgement(List<String> paragraphs) {
    List<People> l_p = (List<People>) context.rabbitInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<People>());
    if (l_p.size() == 0) {
      return;
    }
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    Map<String, List<CriminalJudgement>> criminalJudgements = new HashMap<>();
    Pattern pattern = Pattern.compile("撤销");
    for (String paragraph : paragraphs) { // 判决部分的每一段都是对一个被告的判决
      if (!patternCrimeName.matcher(paragraph).find()) {
        continue;
      }
      if (pattern.matcher(paragraph).find()) {
        continue;
      }
      // 命名实体识别
      // 罪名
      NamedEntity[] nes_crimes = NamedEntityRecognizer.recognizeCrimeNames(paragraph);
      if (nes_crimes.length == 0) {
        continue;
      }
      // 被告
      NamedEntity[] nes_defs = lr.recognize(paragraph);
      if (nes_defs.length == 0) {
        continue;
      }
      // 判决里面的时间长度
      NamedEntity[] nes_tmp1 = NamedEntityRecognizer.recognizeDuration(paragraph, "月");
      // 处理"终身"之类的表述，这里为了统一，将“终身”转成无穷大的时间
      NamedEntity[] nes_tmp2 =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternLifelong);
      List<NamedEntity> nes_tmp = new ArrayList<>();
      Collections.addAll(nes_tmp, nes_tmp1);
      for (NamedEntity ne : nes_tmp2) {
        Double val = Double.MAX_VALUE;
        ne.setInfo(val);
        nes_tmp.add(ne);
      }
      NamedEntity[] nes_duration = nes_tmp.toArray(new NamedEntity[nes_tmp.size()]);
      // 处罚类型
      NamedEntity[] nes_punishmentType =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternPunishmentTypes);
      // 缓刑
      NamedEntity[] nes_probation =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternProbation);
      // 罚金
      NamedEntity[] nes_fine = NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternFine);
      // 没收财产
      NamedEntity[] nes_expropriation =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternExpropriation);
      // 剥夺政治权利
      NamedEntity[] nes_confiscate_property =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternConfiscateProperty);
      // 金额
      NamedEntity[] nes_money = NamedEntityRecognizer.recognizeMoney(paragraph);
      // 是否合并执行
      NamedEntity[] nes_combinedKeyWord =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patternCombine);
      // 标点符号位置
      Integer[][] comma_semicolon = new Integer[2][];
      Integer[] commas =
          NamedEntityRecognizer.recognizeCommaAndSemiColon(paragraph, comma_semicolon);

      NamedEntity[] nes_allEntities = NamedEntityRecognizer.combineEntities(keywords, nes_defs,
          nes_crimes, nes_duration, nes_punishmentType, nes_probation, nes_fine, nes_expropriation,
          nes_confiscate_property, nes_money, nes_combinedKeyWord,
          NamedEntityRecognizer.convertCommasFormat(commas));
      combineEntity(paragraph, nes_allEntities, commas, criminalJudgements);

      // 开始羁押和结束日期
      // parseCustodyStartEndDate(paragraph,recentDefendantName,custodyDates,criminalJudgements);
    }



    List<String> ay_arr = new ArrayList<>();
    for (People p : l_p) {
      if (criminalJudgements.containsKey(p.getPname())) {
        List<CriminalJudgement> list = criminalJudgements.get(p.getPname());
        p.setJudgeList(list);
        for (CriminalJudgement judge : list) {
          if (judge.getJudgementName() != null) {
            for (String name : judge.getJudgementName()) {
              if (!ay_arr.contains(name)) {
                ay_arr.add(name);
              }
            }
            // 存储犯罪记录的罪名的个数
            judge.setJudgementNameSize(judge.getJudgementName().size());
          }
          if (judge.getCombinedResults()) {
            if (judge.getJudgementName() != null) {
              p.getPeopleAttrMap().put(InfoPointKey.info_judgement_name[InfoPointKey.mode],
                  judge.getJudgementName());
            }
            if (judge.getJudgementTime() > 0) {
              p.getPeopleAttrMap().put(InfoPointKey.info_judgement_time[InfoPointKey.mode],
                  judge.getJudgementTime());
            }
            if (judge.getFine() > 0) {
              p.getPeopleAttrMap().put(InfoPointKey.info_fine[InfoPointKey.mode], judge.getFine());
            }
            if (judge.getProbationTime() > 0) {
              p.getPeopleAttrMap().put(InfoPointKey.info_probation_time[InfoPointKey.mode],
                  judge.getProbationTime());
              p.getPeopleAttrMap().put(InfoPointKey.info_probation[InfoPointKey.mode], true);
            }
            if (judge.getJudgementType() != null) {
              p.getPeopleAttrMap().put(InfoPointKey.info_criminal_judgetype[InfoPointKey.mode],
                  judge.getJudgementType());
            }
            if (judge.getConfiscateProperty() > 0) {
              p.getPeopleAttrMap().put(InfoPointKey.info_deprive_politic_term[InfoPointKey.mode],
                  judge.getConfiscateProperty());
              p.getPeopleAttrMap().put(InfoPointKey.info_deprive_politic_rights[InfoPointKey.mode],
                  true);
            }
            if (judge.getExpropriation()) {
              p.getPeopleAttrMap().put(InfoPointKey.info_confiscate_property[InfoPointKey.mode],
                  judge.getExpropriation());
            }
            break;
          }
        }
        // 增加独立适用附加刑、有期徒刑以下型、有期徒刑以上刑
        CriminalJudgement lastCj = list.get(list.size() - 1);
        if (lastCj.getJudgementType() != null) {
          String sentenceType = lastCj.getJudgementType();
          if (aboveFixedTermPenalty.contains(sentenceType)) {
            p.getPeopleAttrMap().put(InfoPointKey.info_above_fixed_term_penalty[InfoPointKey.mode],
                true);
          } else if (underFixedTermPenalty.contains(sentenceType)) {
            p.getPeopleAttrMap().put(InfoPointKey.info_under_fixed_term_penalty[InfoPointKey.mode],
                true);
          }
          if ((sentenceType == null) && ((lastCj.getFine() > 0)
              || (lastCj.getExpropriation() == true) || (lastCj.getConfiscateProperty() > 0))) {
            p.getPeopleAttrMap().put(InfoPointKey.info_additional_penalty[InfoPointKey.mode], true);
          }
        }
      }
    }
    if (!ay_arr.isEmpty()) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_case_ay[InfoPointKey.mode],
          ay_arr.get(0));
    }
  }

  @SuppressWarnings("unchecked")
  private void parseCustody(List<String> paragraphs) {
    List<People> l_p = (List<People>) context.rabbitInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<People>());
    if (l_p.size() == 0) {
      return;
    }
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    Map<String, String[]> custodyDates = new HashMap<>();
    for (int i = 0; i < paragraphs.size(); i++) {
      NamedEntity[] nes =
          NamedEntityRecognizer.recognizeEntityByRegex(paragraphs.get(i), patternCustodyAction);
      if (nes.length == 0)
        continue;
      // 被告
      NamedEntity[] nes_defs = lr.recognize(paragraphs.get(i));
      if (nes_defs.length == 0) {
        if (i > 0) {
          nes_defs = lr.recognize(paragraphs.get(i - 1));
        }
        if (nes_defs.length == 0)
          continue;
      }
      String name = getRecentDefendantName(nes_defs);
      String sentence = paragraphs.get(i).substring(nes[nes.length - 1].getOffset());
      NamedEntity[] dates = NamedEntityRecognizer.recognizeTime(sentence);
      if (dates.length == 0)
        return;

      NamedEntity[] nes_start = new NamedEntity[0];
      NamedEntity[] nes_end = new NamedEntity[0];
      if (dates.length != 2) {
        nes_start =
            NamedEntityRecognizer.recognizeEntityByString(paragraphs.get(i), new String[] {"起"});
        nes_end =
            NamedEntityRecognizer.recognizeEntityByString(paragraphs.get(i), new String[] {"止"});
      }

      List<NamedEntity> lne = new ArrayList<>();
      if (nes_start.length > 0) {
        nes_start[nes_start.length - 1].setType("起");
        lne.add(nes_start[nes_start.length - 1]);
      }
      if (nes_end.length > 0 && nes_start.length > 0) {
        nes_start[nes_start.length - 1].setType("止");
        lne.add(nes_end[nes_end.length - 1]);
      }
      NamedEntity[] nes_action = null;
      if (lne.size() == 1) {
        nes_action = new NamedEntity[] {lne.get(0)};
      } else if (lne.size() == 2) {
        nes_action = new NamedEntity[] {lne.get(0), lne.get(1)};
      }

      if (dates.length >= 2) {
        String time1 = DateHandler.convertDateTimeFormat((String) dates[0].getInfo());
        String time2 = DateHandler.convertDateTimeFormat((String) dates[1].getInfo());
        if (null != time1 || null != time2) {
          String[] tmp = {time1, time2};
          custodyDates.put(name, tmp);
        }
      } else if (null != nes_action) {

        List<NamedEntity[]> lne_dates =
            NamedEntityRecognizer.entityMatch(sentence, dates, nes_action, false, false);
        String[] tmp = {null, null};
        for (NamedEntity[] tokens : lne_dates) {
          String time = DateHandler.convertDateTimeFormat((String) tokens[0].getInfo());
          if ("起".equals(tokens[1].getType())) {
            tmp[0] = time;
          } else {
            tmp[1] = time;
          }
        }
        if (null != tmp[0] || null != tmp[1]) {
          custodyDates.put(name, tmp);
        }
      }
    }

    for (People p : l_p) {
      if (custodyDates.containsKey(p.getPname())) {
        String[] custodyStartEndDates = custodyDates.get(p.getPname());
        if (custodyStartEndDates.length == 2) {
          if (custodyStartEndDates[0] != null) {
            p.getPeopleAttrMap().put(InfoPointKey.info_custody_start_date[InfoPointKey.mode],
                custodyStartEndDates[0]);
          }
          if (custodyStartEndDates[1] != null) {
            p.getPeopleAttrMap().put(InfoPointKey.info_custody_end_date[InfoPointKey.mode],
                custodyStartEndDates[1]);
          }
        }
      }
    }

  }

  private static final Map<String, Integer> conditionsOfDefendantSplits =
      new HashMap<String, Integer>();
  private static final Map<String, Integer> conditionsOfCrimesSplits =
      new HashMap<String, Integer>();
  static {
    conditionsOfDefendantSplits.put("被告", 1);
    conditionsOfCrimesSplits.put("罪名", 1);
    conditionsOfCrimesSplits.put("罪名类型", 1);
    conditionsOfCrimesSplits.put("缓刑", 1);
    conditionsOfCrimesSplits.put("合并执行", 0);
    conditionsOfCrimesSplits.put("罚金", 1);
    conditionsOfCrimesSplits.put("没收财产", 1);
    conditionsOfCrimesSplits.put("剥夺政治权利", 1);
  }

  @SuppressWarnings("unchecked")
  private void combineEntity(String sentence, NamedEntity[] nes_allEntities, Integer[] commas,
      Map<String, List<CriminalJudgement>> crimeJudgements) {

    // 这里定义组合逻辑。
    // 一般情况, []表示可选
    // 情况一： nes_defs1 ... nes_crimes1 ... nes_punishmentType1 nes_duration1 ... [nes_fine1] ... ,
    // nes_crimes2 ... nes_punishmentType2 nes_duration2 .. [nes_fine2] ...,
    // [nes_combinedKeyWord ... nes_punishmentType3 nes_duration3 ... [nes_fine3]]
    // 举例： 被告人王某某，犯盗窃罪，分别被判处有期徒刑三年零八个月罚金1万元和拘役5个月罚金3000元，
    // 决定执行有期徒刑三年10个月，罚金12000万元
    // 情况二： nes_defs1 nes_crimes1, nes_crimes2, .. nes_punishmentType1 nes_duration1 [nes_fine1]
    // nes_punishmentType2 nes_duration2 [nes_fine2] ...
    // [nes_combinedKeyWord ... nes_punishmentType3 nes_duration3 ... [nes_fine3]]
    // 举例： 被告人王某某，犯盗窃罪、抢劫罪，分别被判处有期徒刑三年零八个月罚金1万元和拘役5个月罚金3000元，
    // 决定执行有期徒刑三年10个月，罚金12000万元
    // step 0: 先用人物实体把句子分配到人，即下面要处理的逻辑里面只含有一个命名实体（这个实体可能含有多个名字）
    //
    // step 1 : 组合判罚类型_[刑期]_[罚金]，组合和组合之间用判罚类型分开；如果在两个判罚类型之间存在多个刑期和罚金，取判罚类型右边最靠近的一个
    // 举例：有期徒刑三年零3个月，五年，罚金1000元，管制，拘役4个月,决定合并执行有期徒刑三年五个月，罚金1000元
    // 组合为 ［有期徒刑，三年零3个月，1000元］,［拘役，4个月，null］，［有期徒刑，三年五个月，1000元］注意中间的五年被忽略，管制没有找到匹配
    // step 2: 在step1里面组合里面看左边是否由关键词combinedKeyWord,扩展组合为，以下称为判罚结果实体:
    // ［null，有期徒刑，三年零3个月，1000元］,［null，拘役，4个月，null］，［合并，有期徒刑，三年五个月，1000元］
    // step 3: 将判罚结果实体和罪名实体结合
    // 3.1 对判罚结果实体从左到右遍历（除掉最后是合并的判罚结果实体）
    // 3.2 对当前的判罚结果实体，寻找判罚结果实体左边最临近的罪名实体，组成［罪名实体，判罚结果实体］组合。注意：同一个罪名
    // 实体如果被多次使用，info里面的罪名要根据使用的次序从list里面去掉。举例
    // 罪名实体1 info=[盗窃罪，抢劫罪，诈骗罪], 如果最后和两个判罚结果实体合并：
    // [罪名实体1_1,判罚实体1］，［罪名实体1_2,判罚实体2], 那么罪名实体1_1 info=[盗窃罪],罪名实体1_2 info = [抢劫罪，诈骗罪]
    // 3.3 对最后的合并判罚结果 设置一个罪名实体 info 为之前的罪名实体的合集，需要遍历。比如上面例子中的[盗窃罪，抢劫罪，诈骗罪]
    // step 4: 将［罪名，判罚结果实体］和 被告实体 结合 组成［被告实体，罪名，判罚结果］，将里面信息取出来，输出
    // 4.1 根据 被告实体里面被告的数目 和 与之匹配的 ［罪名，判罚结果实体］（不包括合并量刑）数目进行配对。如果被告数目和后面的
    // ［罪名实体，判罚结果实体］配对数目相同，则一一对应即可。如果被告数目为1，则和后面一一配对；其余情况属于异常！！
    List<List<NamedEntity>> allDefs =
        NamedEntityRecognizer.splitEntityByType(nes_allEntities, conditionsOfDefendantSplits);
    for (List<NamedEntity> lnes : allDefs) {
      // 下面都针对一个被告实体，可能含有多个被告,如果没有找到被告，则认为是无效
      NamedEntity ne_def = NamedEntityRecognizer.findOneEntityByType("被告", lnes);
      if (null == ne_def)
        continue;
      LitigantUnit lu = (LitigantUnit) ne_def.getInfo();
      String[] defNames = lu.getNames();
      String defName = null;
      if (defNames.length > 0)
        defName = defNames[0];
      if (null == defName)
        continue;
      NamedEntity[] oneDefEntity = lnes.toArray(new NamedEntity[lnes.size()]);

      // 可以保证下面至少有一个list 含有被告实体（可能有多个被告）
      List<List<NamedEntity>> allCrimesOfOneDefEntity =
          NamedEntityRecognizer.splitEntityByType(oneDefEntity, conditionsOfCrimesSplits);

      // 需要将罪名和判罚关联，有的判罚可能省略了罪名，因此要根据上下文补全，这里定义个最近的非空罪名列表，以及一个取罪名的计数器,再定义一个所有罪名列表
      List<String> allCrimeNames = new ArrayList<>();
      List<CriminalJudgement> lcj = new ArrayList<>();
      for (List<NamedEntity> oneCrimeOfOneDefEntity : allCrimesOfOneDefEntity) {
        // 下面只针对一个罪名实体（可能有多个罪名），一个判罚
        Map<String, NamedEntity[]> strArrayMap =
            NamedEntityRecognizer.regroupEntities(oneCrimeOfOneDefEntity);
        NamedEntity[] nes_crimeTypes = strArrayMap.getOrDefault("罪名类型", new NamedEntity[0]);
        NamedEntity[] nes_fine = strArrayMap.getOrDefault("罚金", new NamedEntity[0]);
        NamedEntity[] nes_expropriation = strArrayMap.getOrDefault("没收财产", new NamedEntity[0]);
        NamedEntity[] nes_confiscate_property =
            strArrayMap.getOrDefault("剥夺政治权利", new NamedEntity[0]);
        if ((nes_crimeTypes.length == 0) && ((nes_fine.length == 0)
            || (nes_expropriation.length == 0) || (nes_confiscate_property.length == 0))) {
          continue;
        }
        NamedEntity[] nes_probation = strArrayMap.getOrDefault("缓刑", new NamedEntity[0]);
        NamedEntity[] nes_duration = strArrayMap.getOrDefault("判处时间长度", new NamedEntity[0]);
        NamedEntity[] nes_crimes = strArrayMap.getOrDefault("罪名", new NamedEntity[0]);
        NamedEntity[] nes_money = strArrayMap.getOrDefault("金额", new NamedEntity[0]);
        NamedEntity[] nes_combinedKeyWord = strArrayMap.getOrDefault("合并执行", new NamedEntity[0]);

        List<NamedEntity[]> lne_crimeType_duration = NamedEntityRecognizer.entityMatch(sentence,
            commas, nes_crimeTypes, nes_duration, true, false);
        List<NamedEntity[]> lne_probation_duration = NamedEntityRecognizer.entityMatch(sentence,
            commas, nes_probation, nes_duration, true, false);
        List<NamedEntity[]> lne_fine_money =
            NamedEntityRecognizer.entityMatch(sentence, commas, nes_fine, nes_money, true, false);
        List<NamedEntity[]> lne_confiscate_property_money = NamedEntityRecognizer
            .entityMatch(sentence, commas, nes_confiscate_property, nes_duration, true, false);

        List<String> judgementName = new ArrayList<>();
        String judgementType = null;
        int probationTime = 0;
        int sentenceTime = 0;
        double fine = 0.0f;
        boolean expropriation = false;
        int confiscateProperty = 0;
        boolean combinedResults = false;

        // 罪名
        if (nes_crimes.length != 0) {
          judgementName = (List<String>) nes_crimes[0].getInfo();
          allCrimeNames.addAll(judgementName);
        }
        // 判处时间和类型
        if (lne_crimeType_duration.size() != 0) {
          judgementType = lne_crimeType_duration.get(0)[0].getSource();
          sentenceTime = (int) (double) (lne_crimeType_duration.get(0)[1].getInfo());
        }
        // 没有判罚的刑期，判断是不是单处罚金，无期徒刑，免于刑事处罚
        for (NamedEntity ne : nes_crimeTypes) {
          if ("免于刑事处罚".equals(ne.getSource())) {
            judgementType = "免于刑事处罚";
            break;
          } else if ("无期徒刑".equals(ne.getSource())) {
            judgementType = "无期徒刑";
            break;
          } else if ("死刑".equals(ne.getSource())) {
            judgementType = "死刑";
            break;
          }
        }
        // 缓刑时间
        if (lne_probation_duration.size() != 0) {
          probationTime = (int) (double) (lne_probation_duration.get(0)[1].getInfo());
        }
        if (lne_fine_money.size() != 0) {
          fine = (double) (lne_fine_money.get(0)[1].getInfo());
        }
        if (nes_expropriation.length > 0) {
          expropriation = true;
        }
        if (lne_confiscate_property_money.size() > 0) {
          confiscateProperty = (int) (double) (lne_confiscate_property_money.get(0)[1].getInfo());
        }
        //  是否合并处罚
        if (nes_combinedKeyWord.length != 0) {
          combinedResults = true;
        }
        // 根据上下文赋值
        CriminalJudgement criminalJudgment = new CriminalJudgement();
        criminalJudgment.setJudgementName(judgementName);
        criminalJudgment.setJudgementType(judgementType);
        criminalJudgment.setJudgementTime(sentenceTime);
        criminalJudgment.setProbationTime(probationTime);
        criminalJudgment.setFine(fine);
        criminalJudgment.setExpropriation(expropriation);
        criminalJudgment.setConfiscateProperty(confiscateProperty);
        criminalJudgment.setCombinedResults(combinedResults);
        lcj.add(criminalJudgment);
      }
      // 根据找到的判处结果，重新组合调整，根据上下文填充缺失的罪名
      if (lcj.size() > 0) {
        if (lcj.size() == 1) {
          lcj.get(0).setCombinedResults(true);
          // crimeJudgements.put(defName, lcj);
        } else {
          if (lcj.get(0).getJudgementName().size() > 1
              && lcj.get(0).getJudgementName().size() == lcj.size() - 1) {
            List<String> names = lcj.get(0).getJudgementName();
            for (int i = 0; i < lcj.size() - 1; i++) {
              CriminalJudgement cj = lcj.get(i);
              cj.setJudgementName(new ArrayList<>());
              cj.getJudgementName().add(names.get(i));
              cj.setCombinedResults(false);
            }
            // 最后一个是合并量刑的情况 或者 没有罪名
            if (lcj.get(lcj.size() - 1).getCombinedResults()
                || lcj.get(lcj.size() - 1).getJudgementName().size() == 0) {
              lcj.get(lcj.size() - 1).setJudgementName(names);
            }
            lcj.get(lcj.size() - 1).setCombinedResults(true);
          } else {
            List<String> tmpAllNames = new ArrayList<>();
            for (int i = 0; i < lcj.size() - 1; i++) {
              CriminalJudgement cj = lcj.get(i);
              cj.setCombinedResults(false);
              tmpAllNames.addAll(cj.getJudgementName());
            }
            // 最后一个判罚结果认为是总的判罚结果
            lcj.get(lcj.size() - 1).setCombinedResults(true);
            lcj.get(lcj.size() - 1).setJudgementName(tmpAllNames);
          }
        }
        if (!crimeJudgements.containsKey(defName)) {
          crimeJudgements.put(defName, lcj);
        }
      }

      // 处理无罪的情况
      if (lcj.size() == 0) {
        NamedEntity[] nes_no_crime =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternNoCrime);
        if (nes_no_crime.length != 0) {
          if (ne_def.getOffset() < nes_no_crime[0].getOffset()
              && !NamedEntityRecognizer.betweenTwoCommas(ne_def, nes_no_crime[0], commas)) {
            CriminalJudgement cj = new CriminalJudgement();
            cj.setJudgementType("无罪");
            lcj.add(cj);
            crimeJudgements.put(defName, lcj);
          }
        }
      }
    }
  }

  private String getRecentDefendantName(NamedEntity[] nes_defs) {
    if (nes_defs.length == 0)
      return null;
    LitigantUnit lu = (LitigantUnit) nes_defs[nes_defs.length - 1].getInfo();
    String[] defNames = lu.getNames();
    String defName = null;
    if (defNames.length > 0)
      defName = defNames[0];
    return defName;
  }

}
