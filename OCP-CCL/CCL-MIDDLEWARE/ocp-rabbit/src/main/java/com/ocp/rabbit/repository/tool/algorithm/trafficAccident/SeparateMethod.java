package com.ocp.rabbit.repository.tool.algorithm.trafficAccident;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.util.DocumentUtils;


public class SeparateMethod {

  private Context context;

  public SeparateMethod(Context context) {
    this.context = context;
  }

  private ReferLigitantRelatedInfoExtrator referExtractor =
      new ReferLigitantRelatedInfoExtrator(context);
  private static final Logger logger = LoggerFactory.getLogger(SeparateMethod.class);
  private static Pattern[] PATTERN_MONEY_TYPE = {Pattern.compile("医疗费|医疗赔偿"),
      Pattern.compile("护理费"), Pattern.compile("伤残|残疾"), Pattern.compile("死亡赔偿"),
      Pattern.compile("丧葬费"), Pattern.compile("误工费"), Pattern.compile("精神(损害|抚慰)"),
      Pattern.compile("营养费"), Pattern.compile("合计|总计|共计|总额|累计|共")};
  private static String[] MONEY_TYPE =
      {"医疗费", "护理费", "残疾赔偿金", "死亡赔偿金", "丧葬费", "误工费", "精神损害抚慰金", "营养费", "总计"};

  public Map<String, Map<String, Double>> parseCompensationPartAmount(List<String> paragraphs,
      boolean ifRequest) {
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    Map<String, Map<String, Double>> finalResult = new HashMap<>();
    for (String paragraph : paragraphs) {
      NamedEntity recentDef = null;
      for (String sentence : DocumentUtils.splitOneParagraphByOnePeriod(paragraph)) {
        NamedEntity[] nes_money = NamedEntityRecognizer.recognizeMoney(sentence);
        if (nes_money.length == 0)
          continue;
        NamedEntity[] nes_defs = NamedEntityRecognizer.recognizeLitigantByType(sentence,
            LitigantUnit.LABEL_DEFENDANT, lr);
        if (nes_defs.length == 0) {
          if (null == recentDef)
            continue;
        } else
          recentDef = nes_defs[nes_defs.length - 1];
        NamedEntity[] nes_moneyType =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_MONEY_TYPE, MONEY_TYPE);
        if (nes_moneyType.length == 0) {
          double tmpMax = 0;
          for (NamedEntity ne : nes_money) {
            if (tmpMax < (double) ne.getInfo()) {
              tmpMax = (double) ne.getInfo();
            }
          }
          String[] names = null;
          if (nes_defs.length > 0)
            names = ((LitigantUnit) nes_defs[0].getInfo()).getNames();
          else if (null != recentDef)
            names = ((LitigantUnit) recentDef.getInfo()).getNames();
          if (null != names && names.length > 0) {
            String name = names[0];
            Map<String, Double> resultOfOneDef = finalResult.getOrDefault(name, new HashMap<>());
            if ((false == resultOfOneDef.containsKey("总计")) && (tmpMax != 0)) {
              resultOfOneDef.put("总计", tmpMax);
              finalResult.put(name, resultOfOneDef);
            }
          }
          continue;
        }

        Integer[] commas = NamedEntityRecognizer.recognizeComma(sentence);
        // if (nes_defs.length == 0 && null != recentDef){
        // nes_defs = new NamedEntity[]{recentDef};
        // }
        NamedEntity[] nes_allEntities =
            NamedEntityRecognizer.combineEntities(new String[] {"被告", "数额", null, ","}, nes_defs,
                nes_money, nes_moneyType, NamedEntityRecognizer.convertCommasFormat(commas));

        entityMatch(sentence, commas, nes_allEntities, recentDef, finalResult, ifRequest);
      }
    }
    // 处理总金额
    for (Map.Entry<String, Map<String, Double>> oneDef : finalResult.entrySet()) {
      Map<String, Double> result = oneDef.getValue();
      if (false == oneDef.getValue().containsKey("总计")) {
        double temp = 0;
        for (Map.Entry<String, Double> entry : result.entrySet()) {
          if (false == entry.getKey().equals("最大值")) {
            temp += entry.getValue();
          }
        }
        double temp2 = result.getOrDefault("最大值", new Double(0));
        if (temp < temp2) {
          temp = temp2;
        }
        result.put("总计", temp);
      }
      if (result.keySet().contains("最大值"))
        result.remove("最大值");
    }
    return finalResult;
  }

  private static final Map<String, Integer> conditionsOfDefendantSplits =
      new HashMap<String, Integer>();
  private static final Map<String, Integer> conditionsOfTypeSplits = new HashMap<String, Integer>();

  static {
    conditionsOfDefendantSplits.put("被告", 1);
    conditionsOfTypeSplits.put("医疗费", 3);
    conditionsOfTypeSplits.put("护理费", 3);
    conditionsOfTypeSplits.put("残疾赔偿金", 3);
    conditionsOfTypeSplits.put("死亡赔偿金", 3);
    conditionsOfTypeSplits.put("丧葬费", 3);
    conditionsOfTypeSplits.put("误工费", 3);
    conditionsOfTypeSplits.put("精神损害抚慰金", 3);
    conditionsOfTypeSplits.put("营养费", 3);
    conditionsOfTypeSplits.put("总计", 1);
  }

  private static void entityMatch(String sentence, Integer[] commas, NamedEntity[] nes_allEntities,
      NamedEntity recentDef, Map<String, Map<String, Double>> finalResult, boolean ifRequest) {
    List<List<NamedEntity>> allDefs;
    if (ifRequest) {
      allDefs = new ArrayList<>();
      List<NamedEntity> lne = new ArrayList<>();
      for (NamedEntity ne : nes_allEntities) {
        lne.add(ne);
      }
      lne.sort(new Comparator<NamedEntity>() {
        @Override
        public int compare(NamedEntity o1, NamedEntity o2) {
          return o1.getOffset() - o2.getOffset();
        }
      });
      allDefs.add(lne);
    } else {
      allDefs =
          NamedEntityRecognizer.splitEntityByType(nes_allEntities, conditionsOfDefendantSplits);
    }
    for (List<NamedEntity> lnes : allDefs) {
      // 下面都针对一个被告实体，可能含有多个被告,如果没有找到被告，则认为是无效
      NamedEntity ne_def = NamedEntityRecognizer.findOneEntityByType("被告", lnes);
      if ((null == ne_def) && (null == recentDef))
        continue;
      String defName = null;
      String[] defNames = new String[0];
      if (null != ne_def) {
        LitigantUnit lu = (LitigantUnit) ne_def.getInfo();
        defNames = lu.getNames();
      } else if (null != recentDef) {
        LitigantUnit lu = (LitigantUnit) recentDef.getInfo();
        defNames = lu.getNames();
      }
      if (defNames.length > 0)
        defName = defNames[0];
      if (null == defName)
        continue;
      // NamedEntity[] oneDefEntity = lnes.toArray(new NamedEntity[lnes.size()]);
      // 可以保证下面至少有一个list 含有被告实体（可能有多个被告）
      // List<List<NamedEntity>> allMoneyOfOneDefEntity = ner.splitEntityByType(oneDefEntity,
      // conditionsOfTypeSplits);
      // 得到type
      Map<String, NamedEntity[]> nes_moneyTypes = NamedEntityRecognizer.regroupEntities(lnes);
      NamedEntity[] nes_money = nes_moneyTypes.getOrDefault("数额", new NamedEntity[0]);

      Map<String, Double> resultOfOneDef = finalResult.getOrDefault(defName, new HashMap<>());
      List<NamedEntity> lne = new ArrayList<>();
      for (Map.Entry<String, NamedEntity[]> entry : nes_moneyTypes.entrySet()) {
        if (conditionsOfTypeSplits.keySet().contains(entry.getKey())) {
          for (NamedEntity ne : entry.getValue())
            lne.add(ne);
        }
      }
      lne.sort(new Comparator<NamedEntity>() {

        @Override
        public int compare(NamedEntity o1, NamedEntity o2) {
          return o1.getOffset() - o2.getOffset();
        }
      });

      NamedEntity[] nes_type = lne.toArray(new NamedEntity[lne.size()]);

      List<NamedEntity[]> moneyType =
          NamedEntityRecognizer.entityMatch(sentence, commas, nes_type, nes_money, true, false);
      for (NamedEntity[] oneMoneyType : moneyType) {
        resultOfOneDef.put(oneMoneyType[0].getType(), (Double) oneMoneyType[1].getInfo());
      }


      // 把最大金额也放进去
      double tmpMax = 0;
      for (NamedEntity ne : nes_money) {
        if ((Double) ne.getInfo() > tmpMax)
          tmpMax = (Double) ne.getInfo();
      }
      if (tmpMax != 0) {
        double tmpMax2 = resultOfOneDef.getOrDefault("最大值", new Double(0));
        if (tmpMax2 < tmpMax)
          resultOfOneDef.put("最大值", tmpMax);
      }
      finalResult.put(defName, resultOfOneDef);
    }
  }

  public static Map<String, Double> combineResult(Map<String, Map<String, Double>> partCompensation,
      Map<String, Map<String, Double>> partRequest) {

    double insurancePart = 0, feeMed = 0, feeCare = 0, disableFee = 0, deathFee = 0, funeralFee = 0,
        jobDelayFee = 0, mentalFee = 0, nutritionFee = 0, totalFee = 0;
    Map<String, Double> result = new HashMap<>();
    for (Map.Entry<String, Map<String, Double>> entry : partCompensation.entrySet()) {
      for (Map.Entry<String, Double> fees : entry.getValue().entrySet()) {
        if (fees.getValue() < 0) {
          logger.warn("TrafficAccidentExtractor | combineResult: FEE is not correct:"
              + fees.getKey() + ":" + fees.getValue());
          continue;
        }
        switch (fees.getKey()) {
          case "医疗费":
            feeMed += fees.getValue();
            break;
          case "护理费":
            feeCare += fees.getValue();
            break;
          case "残疾赔偿金":
            disableFee += fees.getValue();
            break;
          case "死亡赔偿金":
            deathFee += fees.getValue();
            break;
          case "丧葬费":
            funeralFee += fees.getValue();
            break;
          case "误工费":
            jobDelayFee += fees.getValue();
            break;
          case "精神损害抚慰金":
            mentalFee += fees.getValue();
            break;
          case "营养费":
            nutritionFee += fees.getValue();
            break;
          case "总计":
            totalFee += fees.getValue();
            break;
          default:
            break;
        }
      }
      if (isInsuranceCompany(entry.getKey())) {
        insurancePart += totalFee;
      }
    }

    double pctg_ins = 0.0;
    if (totalFee != 0) {
      pctg_ins = (insurancePart + 0.0) / totalFee;
    }

    if (feeMed != 0)
      result.put("医疗费", feeMed);
    if (feeCare != 0)
      result.put("护理费", feeCare);
    if (disableFee != 0)
      result.put("残疾赔偿金", disableFee);
    if (deathFee != 0)
      result.put("死亡赔偿金", deathFee);
    if (funeralFee != 0)
      result.put("丧葬费", funeralFee);
    if (jobDelayFee != 0)
      result.put("误工费", jobDelayFee);
    if (mentalFee != 0)
      result.put("精神损害抚慰金", mentalFee);
    if (nutritionFee != 0)
      result.put("营养费", nutritionFee);
    if (totalFee != 0)
      result.put("赔偿总计", totalFee);

    result.put("保险比例", pctg_ins);

    feeMed = 0;
    feeCare = 0;
    disableFee = 0;
    deathFee = 0;
    funeralFee = 0;
    jobDelayFee = 0;
    mentalFee = 0;
    nutritionFee = 0;
    totalFee = 0;
    // Map<String,Double> result = new HashMap<>();for (Map.Entry<String,Map<String,Double>>
    // entry:partCompensation.entrySet()){

    for (Map.Entry<String, Map<String, Double>> entry : partRequest.entrySet()) {
      for (Map.Entry<String, Double> fees : entry.getValue().entrySet()) {
        switch (fees.getKey()) {
          case "医疗费":
            feeMed += fees.getValue();
            break;
          case "护理费":
            feeCare += fees.getValue();
            break;
          case "残疾赔偿金":
            disableFee += fees.getValue();
            break;
          case "死亡赔偿金":
            deathFee += fees.getValue();
            break;
          case "丧葬费":
            funeralFee += fees.getValue();
            break;
          case "误工费":
            jobDelayFee += fees.getValue();
            break;
          case "精神损害抚慰金":
            mentalFee += fees.getValue();
            break;
          case "营养费":
            nutritionFee += fees.getValue();
            break;
          case "总计":
            totalFee += fees.getValue();
            break;
          default:
            break;
        }
      }
    }

    if (feeMed != 0)
      result.put("请求医疗费", feeMed);
    if (feeCare != 0)
      result.put("请求护理费", feeCare);
    if (disableFee != 0)
      result.put("请求残疾赔偿金", disableFee);
    if (deathFee != 0)
      result.put("请求死亡赔偿金", deathFee);
    if (funeralFee != 0)
      result.put("请求丧葬费", funeralFee);
    if (jobDelayFee != 0)
      result.put("请求误工费", jobDelayFee);
    if (mentalFee != 0)
      result.put("请求精神损害抚慰金", mentalFee);
    if (nutritionFee != 0)
      result.put("请求营养费", nutritionFee);
    if (totalFee != 0)
      result.put("请求赔偿总计", totalFee);

    double x = 0, y = 0;
    if ((result.containsKey("请求医疗费")) && (result.containsKey("医疗费"))) {
      double lambda = (result.get("医疗费")) / (result.get("请求医疗费"));
      result.put("医疗费支付比例", lambda);
      x += result.get("医疗费");
      y += result.get("请求医疗费");
    }
    if ((result.containsKey("请求残疾赔偿金")) && (result.containsKey("残疾赔偿金"))) {
      double lambda = (result.get("残疾赔偿金")) / (result.get("请求残疾赔偿金"));
      result.put("残疾赔偿金支付比例", lambda);
      x += result.get("残疾赔偿金");
      y += result.get("请求残疾赔偿金");
    }
    if ((result.containsKey("请求死亡赔偿金")) && (result.containsKey("死亡赔偿金"))) {
      double lambda = (result.get("死亡赔偿金")) / (result.get("请求死亡赔偿金"));
      result.put("死亡赔偿金支付比例", lambda);
      x += result.get("死亡赔偿金");
      y += result.get("请求死亡赔偿金");
    }
    if (x != 0 && y != 0) {
      result.put("三项支付比例", x / y);
    }
    return result;
  }

  private static boolean isInsuranceCompany(String name) {
    if (name.contains("保险")) {
      return true;
    }
    return false;
  }
}
