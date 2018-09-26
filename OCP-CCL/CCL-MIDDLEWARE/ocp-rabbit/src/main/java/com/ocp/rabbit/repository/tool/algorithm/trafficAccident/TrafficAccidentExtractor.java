package com.ocp.rabbit.repository.tool.algorithm.trafficAccident;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.AddressClassifier;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.HospitalInfo;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

public class TrafficAccidentExtractor {
  private Context context;

  public TrafficAccidentExtractor(Context context) {
    this.context = context;
  }

  private ReferLigitantRelatedInfoExtrator referExtractor =
      new ReferLigitantRelatedInfoExtrator(context);
  private SeparateMethod seperateMethod = new SeparateMethod(context);

  public void extractor() {
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    // 抽取就医范围
    if (extractHosptial() != null) {
      context.rabbitInfo.getExtractInfo()
          .put(InfoPointKey.info_hospital_location[InfoPointKey.mode], extractHosptial());
    }
    // 抽取被告包含保险公司个数
    context.rabbitInfo.getExtractInfo()
        .put(InfoPointKey.info_number_insurance_company[InfoPointKey.mode], insuranceCompany());
    // 抽取肇事者被列为被告
    if (defendantPerpetrator(lr) != null) {
      context.rabbitInfo.getExtractInfo().put(
          InfoPointKey.info_defendant_rerpetrator[InfoPointKey.mode], defendantPerpetrator(lr));
    }
    // 抽取案件发生双方
    if (extractBothSides() != null) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.info_both_sides[InfoPointKey.mode],
          extractBothSides());
    }
    // 抽取事故责任划分
    if (responsibility(lr) != null) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.info_responsibility[InfoPointKey.mode],
          responsibility(lr));
    }
    // 抽取被告赔付数额，个人和保险赔付比例
    partAmount();
  }

  // 抽取就医范围
  private static final Pattern reg_hos =
      Pattern.compile("(入住|住[^院地所]|送[往至到]|[^收]入|经[^济]|[于在至到])([\\u4e00-\\u9fa5]+医院)");

  private String extractHosptial() {
    String[] tagStrList = {"plaintiff_args", "facts_found", "court_opinion"};
    StringBuilder sbParagraph = TextUtils.getSbParagraph(context, tagStrList);
    if (sbParagraph.length() == 0) {
      return null;
    }
    String paragraph = sbParagraph.toString();
    List<HospitalInfo> totalhos = ResourceReader.readExcel("dictionary/hospital/全国医院名录.xls");
    String[] ranges = {"市内就医", "省内就医", "省外就医"};
    String court = (String) context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_court_name[InfoPointKey.mode]);
    AddressClassifier addrClassfy = AddressClassifier.getInstance();
    String[] courtArea = AddressClassifier.getEachLevel(court, addrClassfy);
    Matcher m1 = reg_hos.matcher(paragraph);
    Set<String> hospitalSet = new HashSet<>();
    List<String[]> hosAreaList = new ArrayList<>();
    while (m1.find()) {
      String names = m1.group(2);
      m1 = reg_hos.matcher(names);
      if (m1.find()) {
        names = m1.group(2);
      }
      String[] hospitals = names.split("、");
      for (String hospital : hospitals) {
        // 可能出现 本市医院 的说明，则认为跟法院同一市
        if (hospital.startsWith("市") && null != courtArea[1]) {
          hospital = courtArea[1] + hospital.substring(1);
        }
        hospitalSet.add(hospital);
      }
    }

    for (String hospital : hospitalSet) {

      String address = "";
      boolean find = false;
      int count = 0;

      List<HospitalInfo> mlist = new ArrayList<>();
      for (HospitalInfo info : totalhos) {
        if ((info.getName() + info.getAlias()).contains(hospital)) {
          mlist.add(info);
          count++;
        }
      }
      if (count > 1) {
        for (HospitalInfo info : mlist) {
          if (null != courtArea[1]) {
            if ((info.getName() + info.getAlias()).contains(hospital)
                && (info.getName() + info.getAlias())
                    .contains(courtArea[1].substring(0, courtArea[1].length() - 1))) {
              address = info.getAddress();
              find = true;
              break;
            }
          }
        }
      } else if (count == 1) {
        address = mlist.get(0).getAddress();
        find = true;
      } else if (hospital.contains("解放军")) {
        hospital = hospital.replace("第", "");
        String reg = hospital.substring(0, hospital.indexOf("军") + 1) + "(第)?"
            + hospital.substring(hospital.indexOf("军") + 1);
        Pattern reg_jun = Pattern.compile(reg);
        for (HospitalInfo info : totalhos) {
          Matcher m = reg_jun.matcher(info.getName() + info.getAlias());
          if (m.find()) {
            address = info.getAddress();
            find = true;
            break;
          }
        }
      }
      String[] area;
      if (find) {
        area = AddressClassifier.getEachLevel(address, addrClassfy);
      } else {
        area = AddressClassifier.getEachLevel(hospital, addrClassfy);
      }
      if (area != null) {
        hosAreaList.add(area);
      }
    }

    int range = 0;
    for (String[] arr : hosAreaList) {
      if (null != arr[0] && null != courtArea[0]) {
        if (!arr[0].equals(courtArea[0])) {
          range = 2;
        } else if (null != arr[1] && null != courtArea[1]) {
          if (!arr[1].equals(courtArea[1])) {
            if (range <= 1) {
              range = 1;
            }
          } else if (null != arr[2] && null != courtArea[2]) {
            if (!arr[2].equals(courtArea[2])) {
              if (range <= 1) {
                range = 0;
              }
            }
          }
        }
      }
    }
    return ranges[range];
  }

  // 抽取案件双方
  private static final Pattern reg_bothSides = Pattern.compile(
      "(驾驶|停|骑)[^证]([a-zA-Z\\d\u4e00-\u9fa5、“”\"（）/]+)(货车|轿车|越野车|摩托车|客车|汽车|拖拉机|摩托|民用车|(?<!非)机动车|面包车|助力车|牵引车|运输车|出租车|商务车|农用车|自行车|挂号车|号车|燃油车|半挂车|电动([\u4e00-\u9fa5]{1,5})?车|三轮车)|(行人|步行|行走|推(.{1})?自行车)");
  private static final Pattern reg_bothSides2 = Pattern.compile(
      "(驾驶|停|骑)(货车|轿车|越野车|摩托车|客车|汽车|拖拉机|摩托|民用车|机动车|面包车|助力车|牵引车|运输车|出租车|商务车|农用车|自行车|挂号车|号车|燃油车|半挂车|电动([\u4e00-\u9fa5]{1,5})?车|三轮车)|(行人|步行|行走|推(.{1})?自行车)");
  private static final Pattern reg_auto = Pattern
      .compile("(货车|轿车|越野车|摩托车|客车|汽车|拖拉机|摩托|民用车|机动车|面包车|助力车|牵引车|运输车|出租车|商务车|农用车|挂号车|号车|燃油车|半挂车)");
  private static final Pattern reg_bike = Pattern.compile("(自行车|电动([\u4e00-\u9fa5]{1,5})?车)");
  private static final Pattern reg_motor = Pattern.compile("[^非]机动车");
  private static final Pattern reg_sent = Pattern.compile("(驾驶|停|骑)[^证].*?(造成|致)");

  private String extractBothSides() {
    String[] tagStrList = {"facts_found", "court_opinion"};
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.size() == 0) {
      return null;
    }
    Set<String> set = new HashSet<>();
    String both_side = null;
    for (String paragraph : paragraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByOnePeriod(paragraph);
      for (String sent : sentences) {
        Matcher mrange = reg_sent.matcher(sent);
        if (mrange.find()) {
          Matcher ma = reg_bothSides.matcher(sent);
          while (ma.find()) {
            if (null != ma.group(3)) {
              String veh = ma.group(3);
              Matcher m1 = reg_auto.matcher(veh);
              Matcher m2 = reg_bike.matcher(veh);
              if (m1.find()) {
                set.add("机动车");
              } else if (m2.find()) {
                set.add("非机动车");
              }
            }
            if (null != ma.group(5)) {
              set.add("人");
            }
          }
          if (set.size() < 2) {
            Matcher ma2 = reg_bothSides2.matcher(sent);
            while (ma2.find()) {
              if (null != ma2.group(2)) {
                String veh = ma2.group(2);
                Matcher m1 = reg_auto.matcher(veh);
                Matcher m2 = reg_bike.matcher(veh);
                if (m1.find()) {
                  set.add("机动车");
                } else if (m2.find()) {
                  set.add("非机动车");
                }
              }
              if (null != ma2.group(4)) {
                set.add("人");
              }
            }
          }
          if (set.size() == 1) {
            String str = "";
            for (String aSet : set) {
              str = aSet.toString();
            }
            if (str.equals("人")) {
              // both_side = "人和人";
            } else if (str.contains("非")) {
              // both_side = "双" + str;
            } else {
              both_side = "双" + str;
            }
          } else {
            String t = set.toString();
            Matcher m = reg_motor.matcher(t);
            boolean motor = m.find();
            if (motor) {
              if (t.contains("非机动车")) {
                both_side = "机动车和非机动车";
              } else if (t.contains("人")) {
                both_side = "机动车和人";
              }
            } else if (t.contains("非机动车") && t.contains("人")) {
              // both_side="非机动车和人";
            }
          }
          break;
        }
      }
    }
    return both_side;
  }

  // 抽取事故责任划分
  private static final Pattern posPattern = Pattern.compile("(全部|主要|同等|次要|无)责任");

  private String responsibility(LitigantRecognizer lr) {
    String[] tagStrList = {"facts_found", "facts_found_primary", "facts_found_secondary",
        "facts_found_base", "court_opinion", "court_base_opinion", "court_primary_opinion",
        "court_secondary_opinion"};
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.size() == 0) {
      return null;
    }
    for (String paragraph : paragraphList) {
      for (String sentence : DocumentUtils.splitOneParagraphByOnePeriod(paragraph)) {
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, posPattern);
        if (nes_action.length == 0) {
          continue;
        }
        NamedEntity[] defs = NamedEntityRecognizer.recognizeLitigantByType(sentence,
            LitigantUnit.LABEL_DEFENDANT, lr);
        if (defs.length == 0)
          continue;
        // 去除保险公司
        List<NamedEntity> tmpDefs = new ArrayList<>();
        for (NamedEntity ne : defs) {
          LitigantUnit litigantUnit = (LitigantUnit) ne.getInfo();
          if (!litigantUnit.getExpression().contains("保险")) {
            tmpDefs.add(ne);
          }
        }
        defs = tmpDefs.toArray(new NamedEntity[tmpDefs.size()]);
        Integer[] commas = NamedEntityRecognizer.recognizeComma(sentence);
        List<NamedEntity[]> lnes_def_action =
            NamedEntityRecognizer.entityMatch(sentence, commas, defs, nes_action, false, false);
        if (lnes_def_action.size() == 0)
          continue;
        String source = lnes_def_action.get(0)[1].getSource();
        if (source.contains("全")) {
          return "全部责任";
        } else if (source.contains("主")) {
          return "主要责任";
        } else if (source.contains("同")) {
          return "同等责任";
        } else if (source.contains("次")) {
          return "次要责任";
        } else if (source.contains("无")) {
          return "无责";
        }
      }
    }
    return null;
  }

  // 抽取被告包含保险公司个数
  @SuppressWarnings("unchecked")
  private int insuranceCompany() {
    List<String> defs = (List<String>) context.rabbitInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.meta_defendant_names[InfoPointKey.mode], new ArrayList<>());
    int count = 0;
    for (String str : defs) {
      if (str.contains("保险")) {
        count++;
      }
    }
    return count;
  }

  // 抽取肇事者被列为被告
  private static final Pattern pattern = Pattern.compile("驾驶");

  private Boolean defendantPerpetrator(LitigantRecognizer lr) {
    String[] tagStrList = {"facts_found", "court_opinion"};
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.size() == 0) {
      return null;
    }
    for (String paragraph : paragraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        // 正则位置
        NamedEntity[] nes_action = NamedEntityRecognizer.recognizeEntityByRegex(sentence, pattern);
        if (nes_action.length == 0)
          continue;
        // 被告位置
        NamedEntity[] nes_defs = NamedEntityRecognizer.recognizeLitigantByType(sentence,
            LitigantUnit.LABEL_DEFENDANT, lr);
        if (nes_defs.length == 0)
          continue;
        List<NamedEntity[]> lnes_litigant_action =
            NamedEntityRecognizer.entityMatch(sentence, nes_defs, nes_action, true, false);
        if (lnes_litigant_action.size() != 0) {
          return true;
        }
      }
    }
    return false;
  }

  // 抽取被告赔付数额，个人和保险赔付比例
  private void partAmount() {
    // 获取判决段落
    String[] tagStrList = {"judgement_content", "court_opinion"};
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    // 获取原告诉称段落
    String[] tagList = {"plaintiff_args"};
    List<String> paragraps = TextUtils.getParagraphList(context, tagList);
    // 传入判决段落
    Map<String, Map<String, Double>> partCompensation =
        seperateMethod.parseCompensationPartAmount(paragraphList, false);
    // 传入原告诉称段落
    Map<String, Map<String, Double>> partRequest =
        seperateMethod.parseCompensationPartAmount(paragraps, true);
    // 结果合并
    Map<String, Double> resultRC = SeparateMethod.combineResult(partCompensation, partRequest);
    if (resultRC.size() != 0) {
      double insurancePctg = resultRC.getOrDefault("保险比例", -1.);
      if (insurancePctg >= 0 && insurancePctg <= 1) {
        context.rabbitInfo.getExtractInfo()
            .put(InfoPointKey.info_insurance_compensation_pctg[InfoPointKey.mode], insurancePctg);
        context.rabbitInfo.getExtractInfo().put(
            InfoPointKey.info_individual_compensation_pctg[InfoPointKey.mode], 1.0 - insurancePctg);
      }
      context.rabbitInfo.getExtractInfo()
          .put(InfoPointKey.info_traffic_compensation_amout_info[InfoPointKey.mode], resultRC);
    }
  }
}
