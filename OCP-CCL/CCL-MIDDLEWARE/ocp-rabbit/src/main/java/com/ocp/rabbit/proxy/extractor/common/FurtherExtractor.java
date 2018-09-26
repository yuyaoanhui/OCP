package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

public class FurtherExtractor {

  private Context context;

  public FurtherExtractor(Context context) {
    this.context = context;
  }

  private ResourceReader reader = new ResourceReader(context);

  // 提起侵害物品和侵害场所信息
  public void infracted(List<String> paragraphList, String key, String name2peopleKey) {
    List<String> nameList = new ArrayList<>();
    @SuppressWarnings("unchecked")
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    ResourceReader.readInfraction();
    if (key.equals("info_infracted_object_classification")) {
      Map<String, List<String>> infractedObjectMap =
          reader.classifyInfractedObject(paragraphList, key, nameList);
      for (Map.Entry<String, List<String>> entry : infractedObjectMap.entrySet()) {
        if (name2People.containsKey(entry.getKey())) {
          List<String> value = entry.getValue();
          name2People.get(entry.getKey()).getPeopleAttrMap().put(key, value);
        }
      }
    } else if (key.equals("info_infracted_spot_classification")) {
      Map<String, List<String>> infractedSpotMap =
          reader.classifyCrimeSpot(paragraphList, key, nameList);
      for (Map.Entry<String, List<String>> entry : infractedSpotMap.entrySet()) {
        if (name2People.containsKey(entry.getKey())) {
          List<String> value = entry.getValue();
          name2People.get(entry.getKey()).getPeopleAttrMap().put(key, value);
        }
      }
    }
  }

  // 物业服务合同抽取诉讼双方
  @SuppressWarnings("unchecked")
  public String parseBothSides() {
    String result = null;
    List<String> plts = (List<String>) context.rabbitInfo.extractInfo
        .getOrDefault(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode], new ArrayList<>());
    List<String> defs = (List<String>) context.rabbitInfo.extractInfo
        .getOrDefault(InfoPointKey.meta_defendant_names[InfoPointKey.mode], new ArrayList<>());
    Pattern tenement = Pattern.compile("物业");
    Pattern property = Pattern.compile("房地产");
    Pattern proprietor = Pattern.compile("业主");
    for (String name : plts) {
      Matcher matcher = tenement.matcher(name);
      Matcher mat = property.matcher(name);
      Matcher ma = proprietor.matcher(name);
      if (matcher.find()) {
        for (String designation : defs) {
          Matcher matcher1 = property.matcher(designation);
          Matcher matcher2 = proprietor.matcher(designation);
          if (matcher1.find()) {
            result = "物业与房地产";
          } else if (matcher2.find()) {
            result = "物业与业主委员会";
          } else {
            result = "物业与业主";
          }
          return result;
        }
      } else if (mat.find()) {
        for (String designation : defs) {
          Matcher matcher1 = tenement.matcher(designation);
          Matcher matcher2 = property.matcher(designation);
          if (matcher1.find()) {
            result = "物业与业主委员会";
          } else if (matcher2.find()) {
            result = "房地产与业主委员会";
          } else {
            result = "物业与业主";
          }
          return result;
        }
      } else if (ma.find()) {
        for (String designation : defs) {
          Matcher matcher1 = tenement.matcher(designation);
          Matcher matcher2 = proprietor.matcher(designation);
          if (matcher1.find()) {
            result = "物业与业主委员会";
          } else if (matcher2.find()) {
            result = "房地产与业主委员会";
          }
          return result;
        }
      } else {
        for (String designation : defs) {
          Matcher matcher1 = tenement.matcher(designation);
          Matcher matcher2 = proprietor.matcher(designation);
          if (matcher1.find()) {
            result = "物业与业主";
          } else if (matcher2.find()) {
            result = "房地产与业主";
          } else {
            result = "其他";
          }
          return result;
        }
      }
    }
    return result;
  }

  // 抽取拐卖到境外 （目前无法确定是从境外到境内还是从境内到境外，定义为涉外）
  public static boolean judgeForeign(StringBuilder sbParagraph) {
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    Pattern patternCountryName = ResourceReader.makeCountryNamePattern();
    for (String sentence : sentences) {
      if (patternCountryName.matcher(sentence).find()) {
        return true;
      }
    }
    return false;
  }

  // 抽取金融借款合同中的利率
  public static double interestRate(StringBuilder sbParagraph, String regexStr, String negRegex,
      String model) {
    Matcher matcher;
    double rate = 0.0;
    Pattern pattern = Pattern.compile(regexStr);
    Pattern pattern1 = null;
    if (!TextUtils.isEmpty(negRegex)) {
      pattern1 = Pattern.compile(negRegex);
    }
    String[] sentences = DocumentUtils.splitSentenceByCommaSemicolon(sbParagraph.toString());
    for (int j = 0; j < sentences.length; j++) {
      String sent = sentences[j];
      if (pattern1 != null && pattern1.matcher(sent).find()) {
        continue;
      }
      matcher = pattern.matcher(sent);
      if (matcher.find()) {
        if ((sent.contains("基准利率")) || (sent.contains("同期利率"))) {
          double aDouble = 0.06;
          if (sent.contains("上浮")) {
            Double dDate = MoneyInfoExtractor.extractPercentage(sent, model);
            if (dDate != null && dDate < 1) {
              aDouble = aDouble * (1 + dDate);
            }
          } else if (sent.contains("下浮")) {
            Double dDate = MoneyInfoExtractor.extractPercentage(sent, model);
            if (dDate != null && dDate < 1) {
              aDouble = aDouble * (1 - dDate);
            }
          } else if (sent.contains("倍")) {
            double rslt = 0.0;
            NumberRecognizer nr = new NumberRecognizer(new String[] {"倍"});
            List<WrapNumberFormat> numbers = nr.getNumbers(new String[] {sent}, true);
            if (numbers.size() > 0)
              rslt = numbers.get(0).getArabicNumber();
            aDouble = aDouble * rslt;
          }
          return aDouble;
        } else {
          Double dDate = MoneyInfoExtractor.extractPercentage(sent, model);
          if (dDate != null && dDate < 1) {
            double dt = dDate;
            return dt;
          }
        }
      }
    }
    return rate;
  }
}
