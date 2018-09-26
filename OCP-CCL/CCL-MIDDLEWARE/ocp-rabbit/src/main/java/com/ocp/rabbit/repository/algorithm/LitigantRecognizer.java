package com.ocp.rabbit.repository.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantStruct;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.util.Position;

/**
 * 诉讼当事人识别类
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class LitigantRecognizer {
  private List<String> defs;
  private List<String> plts;
  private List<String> suspects;
  private List<LitigantStruct> litigants;// 诉讼人物结构
  private static Set<String> andWords = new HashSet<String>();

  static {
    andWords.add("、");
    andWords.add(",");
    andWords.add("，");
    andWords.add("和");
    andWords.add("与");
  }

  public LitigantRecognizer() {}

  public LitigantRecognizer(List<LitigantStruct> litigants, List<String> defs, List<String> plts) {
    this.litigants = litigants;
    this.defs = defs;
    this.plts = plts;
  }

  public LitigantRecognizer(List<LitigantStruct> litigants, List<String> suspects) {
    this.litigants = litigants;
    this.suspects = suspects;
  }

  public LitigantUnit[] nameRecognize(String sentence) {
    List<Position> positions = new ArrayList<Position>();
    for (LitigantStruct structure : this.litigants) {
      String label = structure.getLabel();
      if (!structure.getReferFlg()) {// 不做人物指代
        for (String name : structure.getNames()) {
          insertIntoPositions(sentence, name, label, false, positions);
        }
      } else {// 人物指代
        insertIntoPositions(sentence, structure.getPattern(), label, true, positions);
      }
    }
    if (positions.isEmpty()) {
      return null;
    }
    LitigantUnit[] units = merge(positions, sentence);
    return units;
  }

  /**
   * 在当前句子中识别出当事人
   * 
   * @author yu.yao
   * @param
   * @return 当事人名称实体列表
   */
  public NamedEntity[] recognize(String sentence) {
    LitigantUnit[] units = nameRecognize(sentence);
    if (units == null || units.length == 0) {
      return new NamedEntity[0];
    }
    NamedEntity[] entities = new NamedEntity[units.length];
    for (int i = 0; i < units.length; i++) {
      entities[i] = new NamedEntity(units[i].getExpression(), units[i].getOffset(), units[i]);
    }
    return entities;
  }

  /**
   * 不做人物指代
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private void insertIntoPositions(String source, String target, String label, boolean flag,
      List<Position> positions) {
    int i = 0;
    while (i != -1) {
      int j = source.indexOf(target, i);// 从第i个位置开始匹配第一个target的位置
      if (j != -1) {
        positions.add(new Position(target, label, j, flag));
        i = j + target.length();// 更新至下一个位置
      } else {
        i = -1;// 匹配结束j=-1，执行到此，while循环结束
      }
    }
  }

  /**
   * 人物指代
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private void insertIntoPositions(String source, Pattern pattern, String label, boolean flag,
      List<Position> positions) {
    Matcher matcher = pattern.matcher(source);
    while (matcher.find()) {
      positions.add(new Position(matcher.group(), label, matcher.start(), flag));
    }
  }

  /**
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private LitigantUnit[] merge(List<Position> positions, String sentance) {
    Collections.sort(positions, new Comparator<Position>() {
      @Override
      public int compare(Position o1, Position o2) {
        if (o1.getPos_of_sentenceByComma() == o2.getPos_of_sentenceByComma())
          return o2.getValue().length() - o1.getValue().length();
        return o1.getPos_of_sentenceByComma() - o2.getPos_of_sentenceByComma();
      }
    });
    LinkedList<int[]> lint = new LinkedList<int[]>();
    LinkedList<Position> lp = new LinkedList<Position>();
    LinkedList<List<Position>> lp2 = new LinkedList<List<Position>>();
    // 去除重复或者交叉的
    // 合并相邻的实体
    // 规则1: 实体相邻
    // 规则2: 上一个实体最后一个字符和当前实体第一个字符中间相隔一个特定符号，定义在andWords里面
    for (Position p : positions) {
      if (lp.isEmpty() || p.getPos_of_sentenceByComma() > lp.getLast().getPos_of_sentenceByComma()
          + lp.getLast().getValue().length() - 1) {
        lp.add(p);
        int start = p.getPos_of_sentenceByComma();
        int end = p.getPos_of_sentenceByComma() + p.getValue().length() - 1;
        if (!lint.isEmpty() && (lint.getLast()[1] == start - 1 || (lint.getLast()[1] == start - 2
            && andWords.contains(sentance.substring(start - 1, start))))) {
          lint.getLast()[1] = end;
          lp2.getLast().add(p);
        } else {
          lint.add(new int[] {start, end});
          lp2.add(new ArrayList<>());
          lp2.getLast().add(p);
        }
      }
    }
    if (lp2.isEmpty()) {
      return null;
    }
    LitigantUnit[] litigantUnits = new LitigantUnit[lp2.size()];
    List<String> recentNamesDef = new ArrayList<>();
    List<String> recentNamesPlt = new ArrayList<>();
    List<String> recentNamesSuspects = new ArrayList<>();
    for (int i = 0; i < lp2.size(); i++) {
      int[] cur = lint.get(i);
      boolean flag_def = false, flag_plt = false, flag_def_count = false, flag_plt_count = false,
          flag_suspects = false;
      List<String> names = new ArrayList<>();
      // 下面这部分需要进一步通用化
      for (Position p : lp2.get(i)) {
        switch (p.label) {
          case "被告":
            flag_def = true;
            if (!(boolean) p.getInfo()) {
              names.add(p.getValue());
              flag_def_count = true;
            }
            break;
          case "原告":
            flag_plt = true;
            if (!(boolean) p.getInfo()) {
              names.add(p.getValue());
              flag_plt_count = true;
            }
            break;
          case "嫌疑人":
            flag_suspects = true;
            if (!(boolean) p.getInfo()) {
              names.add(p.getValue());
            }
            break;
          case "原被告":
            flag_def = true;
            flag_plt = true;
          default:
            break;
        }
      }
      String label = null;
      List<String> originalNames = new ArrayList<>(names);
      if (flag_def && !flag_plt && !flag_suspects) {
        if (names.isEmpty()) {
          if (recentNamesDef.isEmpty())
            names = new ArrayList<>(defs);
          else
            // 应该硬copy,否则recentNames变化会导致结果跟着变化
            names = new ArrayList<>(recentNamesDef);
        }
        label = "被告";
      } else if (!flag_def && flag_plt && !flag_suspects) {
        if (names.isEmpty()) {
          if (recentNamesPlt.isEmpty())
            names = new ArrayList<>(plts);
          else
            names = new ArrayList<>(recentNamesPlt);
        }
        label = "原告";
      } else if (!flag_def && !flag_plt && flag_suspects) {
        if (names.isEmpty()) {
          if (recentNamesSuspects.isEmpty())
            names = new ArrayList<>(suspects);
          else
            names = new ArrayList<>(recentNamesSuspects);
        }
        label = "嫌疑人";
      } else if (flag_def && flag_plt && !flag_suspects) {
        label = "原被告";
        if (!flag_def_count)
          names.addAll(defs);
        if (!flag_plt_count)
          names.addAll(plts);
      }
      // 更新最近找到的被告实体包含的名字，即当前被告实体有名字，则更新，否则，保留上一次的名字集合
      // 初始集合是空集
      // 所有操作之后再更新,应该是硬copy
      if (!originalNames.isEmpty()) {
        if ("被告".equals(label))
          recentNamesDef = new ArrayList<>(originalNames);
        else if ("原告".equals(label))
          recentNamesPlt = new ArrayList<>(originalNames);
        else if ("嫌疑人".equals(label))
          recentNamesSuspects = new ArrayList<>(originalNames);
      }
      LitigantUnit lu = new LitigantUnit(sentance.substring(cur[0], cur[1] + 1), cur[0],
          (String[]) names.toArray(new String[names.size()]), label);
      litigantUnits[i] = lu;
    }
    return litigantUnits;
  }
}
