package com.ocp.rabbit.proxy.extractor.custom.divoce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.entity.RabbitInfo;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 离婚信息
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class DivorceExtractor {
  private Context context;

  private HouseAlloExtractor houseExtractor;

  public DivorceExtractor(Context context) {
    this.context = context;
    houseExtractor = new HouseAlloExtractor(context);
  }


  public void extract(RabbitInfo rbInfo) {
    // 抽取性别
    String gender = genderExtract(rbInfo);
    if (gender != null) {
      rbInfo.getExtractInfo().put(InfoPointKey.info_plaintiff_gender[InfoPointKey.mode], gender);
    }
    // 抽取房屋分配
    String house = houseExtract(rbInfo);
    if (house != null) {
      rbInfo.getExtractInfo().put(InfoPointKey.info_house_allocation[InfoPointKey.mode], house);
    }
    // 抽取婚龄
    int marriageYears = marriageYearsExtract(rbInfo);
    if (marriageYears > 0) {
      rbInfo.getExtractInfo().put(InfoPointKey.info_marriage_years[InfoPointKey.mode],
          marriageYears);
    }
    // 抽取再婚
    remarriageExtract(rbInfo);
    // 抽取分居时间
    int seperation = seperationExtract(rbInfo);
    if (seperation > 0) {
      rbInfo.getExtractInfo().put(InfoPointKey.info_divorce_seperation_time[InfoPointKey.mode],
          seperation);
    }
    // 抽取判决离婚
    rbInfo.getExtractInfo().put(InfoPointKey.info_allowdivorce[InfoPointKey.mode],
        divorceResultExtract(rbInfo));
    // 抽取曾起诉离婚次数
    rbInfo.getExtractInfo().put(InfoPointKey.info_number_suit_record[InfoPointKey.mode],
        parseNumberDivorce(rbInfo));
  }

  // 抽取房屋分配
  private String houseExtract(RabbitInfo rbInfo) {
    String result = null;
    List<String> labels = new ArrayList<String>();
    labels.add("judgement_content");
    List<Map<Integer, String>> paragraphList =
        context.docInfo.getParaLabels().getContentByLabels(labels);
    if (paragraphList.isEmpty()) {
      return null;
    }
    result = new HouseAlloExtractor(this.context).parseHouseAllocation(paragraphList);
    if (result != null) {
      if ((result.equals("按份共有")) || (result.equals("子女所有"))) {
        return result;
      } else {
        String gender = (String) rbInfo.getExtractInfo()
            .getOrDefault(InfoPointKey.info_plaintiff_gender[InfoPointKey.mode], null);
        if (gender != null) {
          if (gender.equals("女")) {
            if (result.equals(LitigantUnit.LABEL_DEFENDANT))
              result = "男方所有";
            else if (result.equals(LitigantUnit.LABEL_PLAINTIFF)) {
              result = "女方所有";
            }
          } else {
            if (result.equals(LitigantUnit.LABEL_DEFENDANT))
              result = "女方所有";
            else if (result.equals(LitigantUnit.LABEL_PLAINTIFF)) {
              result = "男方所有";
            }
          }
        }
      }
    }
    return result;
  }

  // 抽取起诉离婚方性别
  @SuppressWarnings("unchecked")
  private String genderExtract(RabbitInfo rbInfo) {
    String result = null;
    List<People> namePeople = (List<People>) rbInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<>());
    for (People p : namePeople) {
      List<String> position = (List<String>) p.getPeopleAttrMap()
          .get(InfoPointKey.info_all_litigant_positions[InfoPointKey.mode]);
      if (position == null) {
        continue;
      }
      for (String pos : position) {
        if (pos.contains("原告")) {
          if (null != p.getPeopleAttrMap().get(InfoPointKey.info_gender[InfoPointKey.mode])) {
            result = (String) p.getPeopleAttrMap().get(InfoPointKey.info_gender[InfoPointKey.mode]);
            return result;
          }
        }
      }
    }
    return result;
  }

  // 抽取婚龄
  @SuppressWarnings("unchecked")
  private int marriageYearsExtract(RabbitInfo rbInfo) {
    int year = 0;
    String dtMarriage =
        (String) rbInfo.getExtractInfo().get(InfoPointKey.info_marriage_date[InfoPointKey.mode]);
    String dtJudge =
        (String) rbInfo.getExtractInfo().get(InfoPointKey.meta_doc_date[InfoPointKey.mode]);
    if (null != dtJudge && null != dtMarriage) {
      try {
        year = DateHandler.getYearDiff(DateHandler.makeDateTime(dtMarriage),
            DateHandler.makeDateTime(dtJudge));
        List<People> listP = (List<People>) rbInfo.getExtractInfo()
            .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<>());
        if (null != listP) {
          for (People p : listP) {
            int value = (int) p.getPeopleAttrMap()
                .getOrDefault(InfoPointKey.info_age[InfoPointKey.mode], 0);
            if (value > 0 && value < 100) {
              if (year < value) {
                return year;
              }
            }
          }
        }
        if (year > 0 && year < 100) {
          return year;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return year;
  }

  // 抽取再婚
  private void remarriageExtract(RabbitInfo rbInfo) {
    String regex = "再婚";
    List<String> labels = new ArrayList<String>();
    labels.add("facts_found");
    labels.add("court_opinion");
    List<Map<Integer, String>> paragraphList =
        context.docInfo.getParaLabels().getContentByLabels(labels);
    if (paragraphList.size() == 0) {
      return;
    }
    Map<String, Boolean> map = houseExtractor.parse_remarriage(rbInfo, paragraphList, regex);
    Iterator<Map.Entry<String, Boolean>> iter = map.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, Boolean> entry = iter.next();
      String name = entry.getKey();
      Boolean value = entry.getValue();
      if (name.equals("原告")) {
        rbInfo.getExtractInfo().put(InfoPointKey.info_plt_remarriage[InfoPointKey.mode], value);
      } else if (name.equals("被告")) {
        rbInfo.getExtractInfo().put(InfoPointKey.info_def_remarriage[InfoPointKey.mode], value);
      }
    }
  }

  // 抽取分居时间
  private static final String regex = "([\\d]+年[\\d]+月([\\d]+日)?)[,，、一-龥]*?(分居|离家出走|搬出|在外|异地)";

  private int seperationExtract(RabbitInfo rbInfo) {
    Matcher matcher = null;
    String leaveTime = null;
    int months = 0;
    List<String> labels = new ArrayList<String>();
    labels.add("facts_found");
    StringBuilder sbParagraph = context.docInfo.getParaLabels().getContentSumByLabels(labels);
    if (sbParagraph.length() == 0) {
      return months;
    }
    Pattern pattern = Pattern.compile(regex);
    String[] sents = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    for (String sent : sents) {
      matcher = pattern.matcher(sent);
      if (matcher.find()) {
        leaveTime = matcher.group(1);
      }
    }
    if (leaveTime != null) {
      String dtJudge =
          (String) rbInfo.getExtractInfo().get(InfoPointKey.meta_doc_date[InfoPointKey.mode]);
      DateTime d1 = DateHandler.makeDateTime(leaveTime);
      DateTime d2 = DateHandler.makeDateTime(dtJudge);
      try {
        int diff = DateHandler.getMonthDiff(d1, d2);
        if (diff < 0) {
          diff = diff * (-1);
        }
        if (diff > 0)
          months = diff;
      } catch (Exception e) {
      }
    }
    return months;
  }

  // 抽取判决离婚
  private static final String regexStr = "(驳回|不).*?(离婚|起诉|诉讼|请求)|(离婚|起诉|诉讼|请求)[^,，；;。\\.]*?(驳回|不)";

  private boolean divorceResultExtract(RabbitInfo rbInfo) {
    List<String> labels = new ArrayList<String>();
    labels.add("judgement_content");
    StringBuilder sbParagraph = context.docInfo.getParaLabels().getContentSumByLabels(labels);
    Pattern patternStr = Pattern.compile(regexStr);
    Pattern pattern = Pattern.compile("驳回|不|撤");
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    String jt = (String) rbInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.meta_judgement_type[InfoPointKey.mode], null);
    if ("判决书".equals(jt)) {
      for (int j = 0; j < Math.min(2, sentences.length); j++) {
        String sent = sentences[j];
        if (patternStr.matcher(sent).find()) {
          return false;
        }
      }
      return true;
    } else if ("裁定书".equals(jt)) {
      return false;
    } else if ("调解书".equals(jt)) {
      for (int j = 0; j < Math.min(2, sentences.length); j++) {
        String sent = sentences[j];
        if (!(sent.contains("和好") && sent.contains("不")) && sent.contains("离婚")) {
          return true;
        }
      }
      return false;
    } else {
      for (int j = 0; j < Math.min(2, sentences.length); j++) {
        String sent = sentences[j];
        if (sent.contains("离婚") && pattern.matcher(sent).find()) {
          return true;
        }
      }
      return false;
    }
  }

  // 抽取曾离婚次数
  private static NumberRecognizer nr = new NumberRecognizer(new String[] {"次"});
  private static final Pattern pattern =
      Pattern.compile("(曾.*?([两二三四五六七八])?|再)次([一-龥]+)?(具状|起诉|提起诉讼|诉至|要求([一-龥]+)?离婚|提出|诉求|提起)");
  private static final Pattern reg_char1 =
      Pattern.compile("于([\\d]+年[\\d]+月[\\d]+日、?)*?[\\u4e00-\\u9fa5]*?(诉至|提起(离婚)?诉讼)");
  private static final Pattern reg_char2 = Pattern.compile(
      "[\\d]+年[\\d]+月([\\d]+日)?[,，]?[\\u4e00-\\u9fa5]*?(具状|起诉|提起诉讼|诉至|要求([一-龥]+)?离婚|提出|诉求|提起)");

  private int parseNumberDivorce(RabbitInfo rbInfo) {
    double number = 0.0;
    double no = 0.0;
    List<String> labels = new ArrayList<String>();
    labels.add("facts_found");
    labels.add("facts_found_cmpl");
    labels.add("court_opinion");
    List<Map<Integer, String>> paragraphList =
        context.docInfo.getParaLabels().getContentByLabels(labels);
    if (paragraphList.isEmpty()) {
      return 0;
    }
    List<String> mergedParas = new ArrayList<String>();
    for (Map<Integer, String> map : paragraphList) {
      for (int i : map.keySet()) {
        mergedParas.add(map.get(i));
      }
    }
    for (String paragraph : mergedParas) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        Matcher matcher = pattern.matcher(sentence);
        if (matcher.find() && no == 0) {
          String s = matcher.group();
          if (s.contains("曾")) {
            List<WrapNumberFormat> wnfs = nr.getNumbers(s, true);
            if (wnfs.size() == 1) {
              number = wnfs.get(0).getArabicNumber();
            } else {
              number = 1;
            }
          } else if (s.contains("再")) {
            number = 1;
          }
          break;
        }
        if (number == 0 && no == 0) {
          Matcher ma = reg_char1.matcher(sentence);
          if (ma.find()) {
            String s = ma.group();
            NamedEntity[] nes = (new DateHandler(s)).getTimeUnitsNE();
            if (nes.length > 0) {
              number = nes.length;
              break;
            }
          }
        }
        if (number <= no) {
          Matcher match = reg_char2.matcher(sentence);
          if (match.find()) {
            String s = match.group();
            NamedEntity[] nes = (new DateHandler(s)).getTimeUnitsNE();
            if (nes.length > 0) {
              no = no + nes.length;
            }
          }
        }
      }
      if (number > 0) {
        break;
      }
    }
    if (no > 0 && number == 0) {
      number = no;
    }
    return (int) number;
  }
}
