package com.ocp.rabbit.proxy.extractor.custom.divoce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.entity.RabbitInfo;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 房屋分配
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class HouseAlloExtractor {
  private Context context;

  private ReferLigitantRelatedInfoExtrator referExtractor;
  public HouseAlloExtractor(Context context) {
    this.context = context;
    referExtractor =
            new ReferLigitantRelatedInfoExtrator(context);
  }

  private static final Pattern[] patternsHouse =
      new Pattern[] {Pattern.compile("房|屋|宅|公寓|\\d+室|一间|[一两二三四五六七八九十]+[层楼]")};
  private static final Pattern[] patternsAction =
      new Pattern[] {Pattern.compile("所有|使用|分得|居住|平均分割|各半?享有")};
  private static final Pattern[] patternsBoth =
      new Pattern[] {Pattern.compile("共同(?:所有|使用)|各分得|平均分割|各半?享有")};

  @SuppressWarnings("unchecked")
  public String parseHouseAllocation(List<Map<Integer, String>> paragraphs) {
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    List<Map<String, String>> childInfo2 =
        (List<Map<String, String>>) context.rabbitInfo.getExtractInfo()
            .getOrDefault(InfoPointKey.info_kid_info[InfoPointKey.mode], new ArrayList<>());
    List<String[]> childInfo = new ArrayList<>();
    for (Map<String, String> ci : childInfo2) {
      String[] child = {null, null, null};
      child[0] = ci.getOrDefault(ChildExtractor.childName, null);
      child[1] = ci.getOrDefault(ChildExtractor.childGender, null);
      child[2] = ci.getOrDefault(ChildExtractor.childDob, null);
      childInfo.add(child);
    }
    String result = null;
    Set<String> map = new HashSet<>();
    List<String> mergedParas = new ArrayList<String>();
    for (Map<Integer, String> paragraph : paragraphs) {
      for (int i : paragraph.keySet()) {
        mergedParas.add(paragraph.get(i));
      }
    }
    for (String parapgraph : mergedParas) {
      for (String sentence : DocumentUtils.splitOneParagraphByPeriod(parapgraph)) {
        // 命名实体识别
        NamedEntity[] nes_house =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsHouse);
        if (nes_house.length == 0)
          continue;
        // ｀System.out.println(document.getId()+" :"+sentence+"\n");
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsAction);
        if (nes_action.length == 0)
          continue;
        NamedEntity[] nes_litigant = lr.recognize(sentence);

        // NamedEntity[] nes_children = ChildCustodyExtractor.findChildren(ner, parapgraph,
        // childNames, childInfo,PATTERN_CHILD);
        NamedEntity[] nes_children = new NamedEntity[0];
        // 不考虑子女所有
        NamedEntity[] nes_both =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternsBoth);
        Integer[] commas = NamedEntityRecognizer.recognizeComma(sentence);
        result = entityMatch(sentence, commas, nes_house, nes_action, nes_litigant, nes_children,
            nes_both);
        if (result != null) {
          map.add(result);
        }
      }
    }
    if (map.contains("原告所有") && map.contains("被告所有")) {
      result = "按份共有";
    }
    return result;
  }

  public static final String both = "按份共有";
  public static final String defendantOwner = "被告所有";
  public static final String plaintiffOwner = "原告所有";
  public static final String childOwner = "子女所有";

  private static String entityMatch(String sentence, Integer[] commas, NamedEntity[] nes_house,
      NamedEntity[] nes_action, NamedEntity[] nes_litigant, NamedEntity[] nes_children,
      NamedEntity[] nes_both) {

    // 先配对 （房子，所有）组合
    List<NamedEntity[]> lne_house_action =
        NamedEntityRecognizer.entityMatch(sentence, nes_house, nes_action, false, false);
    NamedEntity[] nes_roles =
        NamedEntityRecognizer.combineEntities(null, nes_children, nes_litigant, nes_both);
    // 需要寻找（房子，所有）组合里离这个组合最近的实体组合。
    // 交换（房子，所有）的顺序为（所有，房子）
    for (NamedEntity[] ne : lne_house_action) {
      NamedEntity tmp0 = ne[0];
      NamedEntity tmp1 = ne[1];
      ne[0] = tmp1;
      ne[1] = tmp0;
    }
    List<NamedEntity[]> lne_roles = new ArrayList<>();
    for (NamedEntity ne : nes_roles)
      lne_roles.add(new NamedEntity[] {ne});

    // 先看是不是共同拥有
    List<NamedEntity[]> lne_both =
        NamedEntityRecognizer.entityMatch(sentence, nes_house, nes_both, true, true);
    if (lne_both.size() != 0)
      return both;

    List<NamedEntity[]> lne_role_action_house =
        NamedEntityRecognizer.entityMatch(commas, lne_roles, lne_house_action, false, true);

    // 分析找到的配对结果
    int count_kid = 0, count_def = 0, count_plt = 0, count_both = 0;
    for (NamedEntity[] nes : lne_role_action_house) {
      Object obj = nes[0].getInfo();
      if (obj == null) {
        count_both++;
      } else if (obj instanceof LitigantUnit) {
        LitigantUnit lu = (LitigantUnit) obj;
        if (lu.getLabel().equals("被告")) {
          count_def++;
        } else if (lu.getLabel().equals("原告")) {
          count_plt++;
        } else {
          count_both++;
        }
      } else if (obj instanceof String[]) {
        count_kid++;
      } else {
        count_both++;
      }
    }
    if (count_both != 0) {
      return both;
    } else if (count_def == 0 && count_plt == 0) {
      if (count_kid != 0) {
        return childOwner;
      } else {
        return null;
      }
    } else {
      if (count_plt == 0)
        return defendantOwner;
      else if (count_def == 0)
        return plaintiffOwner;
      else
        return both;
    }
  }

  // 抽取再婚
  public Map<String, Boolean> parse_remarriage(RabbitInfo rbInfo,
      List<Map<Integer, String>> paragraphList, String regex) {
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    Map<String, Boolean> map = new HashMap<>();
    Pattern remarriage = Pattern.compile(regex);
    List<String> mergedParas = new ArrayList<String>();
    for (Map<Integer, String> paragraphMap : paragraphList) {
      for (int i : paragraphMap.keySet()) {
        mergedParas.add(paragraphMap.get(i));
      }
    }
    for (String paragraps : mergedParas) {
      String[] sentences = DocumentUtils.splitOneParagraphByOnePeriod(paragraps);
      for (String sentence : sentences) {
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, remarriage);
        if (nes_action.length == 0) {
          continue;
        }
        if (sentence.contains("均系再婚")) {
          map.put("原告", true);
          map.put("被告", true);
          break;
        }
        NamedEntity[] nes_lit = lr.recognize(sentence);
        if (nes_lit.length > 0) {
          List<NamedEntity[]> lnes =
              NamedEntityRecognizer.entityMatch(sentence, nes_lit, nes_action, false, false);
          for (NamedEntity[] nes : lnes) {
            String label = ((LitigantUnit) nes[0].getInfo()).getLabel();
            if (label.equals("原被告")) {
              map.put("原告", true);
              map.put("被告", true);
              break;
            } else if (label.equals("被告")) {
              map.put("被告", true);
              break;
            } else if (label.equals("原告")) {
              map.put("原告", true);
              break;
            }
          }
        }
      }
      if (map.size() > 0) {
        break;
      }
    }
    return map;
  }

}
