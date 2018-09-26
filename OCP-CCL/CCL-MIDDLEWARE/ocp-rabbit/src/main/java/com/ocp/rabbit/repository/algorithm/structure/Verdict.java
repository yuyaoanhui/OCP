package com.ocp.rabbit.repository.algorithm.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.proxy.component.custom.PatternComp;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.bean.ParaLabelBean;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.InfoPointKey;

/**
 * 裁定书分段和打标签算法实现类
 * 
 * @author yu.yao 2018年9月6日
 *
 */
public class Verdict {
  private static final String REGEX_PREFIX_MANUSCRIPT =
      "^(签发|拟稿|核稿人|核稿|签发人|拟稿人|事由|案号|字号|附件|发送机关|发文|校对|文件名称)[:：]?";
  private Context context;

  private PatternComp pattern;
  Map<ParaLabelEnum, List<ParaLabelEnum>> preCondition;
  Map<ParaLabelEnum, List<Pattern>> labelPatternMap;
  List<List<ParaLabelBean>> paraLabels;

  public Verdict(Context context) {
    this.context = context;
    pattern = new PatternComp(context);
    preCondition = context.docInfo.getPreCondition();
    labelPatternMap = context.docInfo.getLabelPatterns();
    paraLabels = context.docInfo.getParaLabels().map2List();// 标签体系<大标签,小标签列表>
  }

  public void doStructure() {
    /*************** 分段 start ****************/
    Map<Integer, String> unPaterrened = new HashMap<Integer, String>();// 未匹配上的自然段
    Set<ParaLabelEnum> visited = new HashSet<ParaLabelEnum>();// 已访问过的大标签
    List<String> documentUnits = context.getAllUnits();
    for (int i = 0; i < documentUnits.size(); i++) {
      String unit = documentUnits.get(i);
      if (i == 0) {
        unit = unit.replaceFirst(REGEX_PREFIX_MANUSCRIPT, "");
      }
      /*************** 打标签 start ****************/
      context.setContent(unit);
      boolean unitAdded = false;
      boolean people_or_argument_state = false;
      int start = visited.isEmpty() ? 0 : visited.size();
      List<ParaLabelBean> beanRemove = new ArrayList<ParaLabelBean>();
      for (; start < paraLabels.size(); start++) {// 标签分组循环
        List<ParaLabelBean> labels = paraLabels.get(start);
        /***************** 大标签前置条件判断 START *******************/
        if (preCondition.containsKey(labels.get(0).enumLabel) && !visited.isEmpty()) {// 若需要前置标签判断
          boolean preCond = false;
          for (ParaLabelEnum pl : visited) {
            ParaLabelEnum[] preEnums = labels.get(0).enumLabel.getPreLabel();
            if (preEnums == null || preEnums.length == 0) {
              preCond = true;
              break;// 无前置标签默认通过
            }
            if (preCondition.get(labels.get(0).enumLabel).contains(pl)) {
              preCond = true;
              break;// 只要访问过一个前置标签就通过
            }
          }
          if (!preCond) {
            continue;
          }
        }
        /***************** 大标签前置条件判断 END *******************/
        boolean flag = false;
        for (int smallStart = 1; smallStart < labels.size(); smallStart++) {// 该组小标签遍历
          ParaLabelBean ele = labels.get(smallStart);
          if (!labelPatternMap.containsKey(ele.enumLabel)) {// 去除无效标签
            continue;
          }
          context.regex = labelPatternMap.get(ele.enumLabel);
          pattern.handle();// 判断是否满足小标签匹配规则
          if (context.isPatterned) {
            if (ele.getContent() == null) {// 第一个自然段
              ele.setContent(new HashMap<Integer, String>());
            }
            ParaLabelBean.remove(beanRemove, i + 1);
            ele.getContent().put(i + 1, unit);
            unitAdded = true;
            flag = true;
            break;
          }
        }
        if (unitAdded) {
          // 如果当前状态是描述人物或者辩论部分,因为不确定后面有多少个段落,所以本标签不加入已访问列表中
          if (labels.get(0).enumLabel.equals(ParaLabelEnum.DIR_PEOPLE)
              || labels.get(0).enumLabel.equals(ParaLabelEnum.DIR_ARGUMENTS)) {
            people_or_argument_state = true;
            visited.add(labels.get(0).enumLabel); // 大标签(分组标签)放入已访问列表中,下次访问下一个大标签
            beanRemove = labels;
            continue;
          }
          if (flag) {
            if (labels.get(0).enumLabel.getLabel().equals("dir_case_summary") && visited.size() == 3
                && visited.contains(labels.get(0).enumLabel)) {
              beanRemove = labels;
              continue;
            }
            visited.add(labels.get(0).enumLabel);// 大标签(分组标签)放入已访问列表中,下次访问下一个大标签
            break;
          }
          if (people_or_argument_state) {
            break;
          }
        }
      }
      if (!unitAdded) {// 哪些段落匹配不到标签
        if (i == 0) {
          ParaLabelBean ele = paraLabels.get(0).get(1);
          ele.setContent(new HashMap<Integer, String>());
          ele.getContent().put(i + 1, unit);
        } else {
          unPaterrened.put(i + 1, unit);
        }
      }
      /*************** 打标签 end ****************/
    }
    // 对judgement_content标签特殊处理
    addLackedLabel(unPaterrened);
    // 剩余匹配不到标签的段落则向上找一个标签
    for (int i : unPaterrened.keySet()) {
      int number = i;
      ParaLabelBean label = context.docInfo.getParaLabels().getParagraphLabel(number);
      while (label == null) {
        label = context.docInfo.getParaLabels().getParagraphLabel(--number);
        if (number == 0) {
          break;
        }
      }
      label.getContent().put(i, unPaterrened.get(i));
    }
    /*************** 分段 end ****************/
    // 抽取section信息点
    extractSection();
    // 调整已经抽取的部分section信息点
    adjustSection();
  }

  /**
   * 对judgement_content标签特殊处理
   *
   * @param
   * @return
   * @author yu.yao
   */
  private void addLackedLabel(Map<Integer, String> unPaterrened) {
    String regexStr = "(判决|裁定)如下";
    String regexStr2 = "^[\\(（]?[一1][\\)）]?[、\\.,，:：]";
    String regexStr3 = "(如|以)下(:|：)";
    Pattern pattern = Pattern.compile(regexStr);
    if (!unPaterrened.isEmpty()) {
      Map<Integer, String> content = new HashMap<Integer, String>();
      SortedSet<Integer> set = new TreeSet<Integer>();
      set.addAll(unPaterrened.keySet());
      boolean flag = false;
      while (!set.isEmpty()) {
        String paragraph = context.getAllUnits().get(set.last() - 2);
        Matcher matcher = pattern.matcher(paragraph);
        if (matcher.find()) {
          if (unPaterrened.containsKey(set.last())) {
            content.put(set.last(), unPaterrened.get(set.last()));
            flag = true;
            break;
          }
        }
        set = set.headSet(set.last());
      }
      if (!flag) {
        set = new TreeSet<Integer>();
        set.addAll(unPaterrened.keySet());
        pattern = Pattern.compile(regexStr2);
        while (!set.isEmpty()) {
          String paragraph = context.getAllUnits().get(set.last() - 2);
          Matcher matcher = pattern.matcher(paragraph);
          if (matcher.find()) {
            content.put(set.last(), unPaterrened.get(set.last()));
            flag = true;
            break;
          }
          set = set.headSet(set.last());
        }
      }
      if (!flag) {
        set = new TreeSet<Integer>();
        set.addAll(unPaterrened.keySet());
        pattern = Pattern.compile(regexStr3);
        while (!set.isEmpty()) {
          String paragraph = context.getAllUnits().get(set.last() - 2);
          Matcher matcher = pattern.matcher(paragraph);
          if (matcher.find()) {
            if (unPaterrened.containsKey(set.last()))
              content.put(set.last(), unPaterrened.get(set.last()));
            flag = true;
            break;
          }
          set = set.headSet(set.last());
        }
      }
      for (int i : content.keySet()) {
        unPaterrened.remove(i);
      }
      List<ParaLabelBean> list = new ArrayList<ParaLabelBean>();
      list.add(new ParaLabelBean(ParaLabelEnum.JUDGEMENT_CONTENT, "judgement_content", content,
          content));
      context.docInfo.getParaLabels().paralabels.put(ParaLabelEnum.DIR_JUDGEMENT_CONTENT, list);
    }
  }

  /**
   * 根据分段打标签后的文书抽取section信息点
   *
   * @param
   * @return
   * @author yu.yao
   */
  private ResultCode extractSection() {
    // 要抽取的section信息点
    String k_title = InfoPointKey.section_title[InfoPointKey.mode];
    String k_doc_first_part = InfoPointKey.section_doc_first_part[InfoPointKey.mode];
    String k_fact_const = InfoPointKey.section_fact_const[InfoPointKey.mode];
    String k_plaintiff_statement = InfoPointKey.section_plaintiff_statement[InfoPointKey.mode];
    String k_accused_statement = InfoPointKey.section_accused_statement[InfoPointKey.mode];
    String k_reason = InfoPointKey.section_reason[InfoPointKey.mode];
    String k_judge_base = InfoPointKey.section_judge_base[InfoPointKey.mode];
    String k_judge_main = InfoPointKey.section_judge_main[InfoPointKey.mode];
    String k_last_part = InfoPointKey.section_last_part[InfoPointKey.mode];
    String k_signature = InfoPointKey.section_signature[InfoPointKey.mode];
    String k_relate_law = InfoPointKey.section_relate_law[InfoPointKey.mode];
    String k_controversy_focus = InfoPointKey.section_controversy_focus[InfoPointKey.mode];
    StringBuilder[] v_content = new StringBuilder[13];
    for (int i = 0; i < 13; i++) {
      v_content[i] = new StringBuilder("");
    }
    int preKeyIdx = -1;
    List<String> paraghs = context.getAllUnits();
    for (int i = 0; i < paraghs.size(); i++) {
      String label = context.docInfo.getParaLabels().getParagraphLabel(i + 1).label;
      ParaLabelEnum tag = context.docInfo.getParaLabels().getByLabel(label).enumLabel;
      if (ParaLabelEnum.COURT.equals(tag) || ParaLabelEnum.CASE_TYPE.equals(tag)
          || ParaLabelEnum.CASE_ID.equals(tag)) {
        v_content[0].append(paraghs.get(i));
        v_content[0].append("\n");
        preKeyIdx = 0;
      } else if (ParaLabelEnum.DEFENDANT.equals(tag) || ParaLabelEnum.PLAINTIFF.equals(tag)
          || ParaLabelEnum.THIRD_PERSON.equals(tag) || ParaLabelEnum.REPRESENTATIVE.equals(tag)
          || ParaLabelEnum.ASSIGNED.equals(tag) || ParaLabelEnum.ENTRUSTED.equals(tag)
          || ParaLabelEnum.ATTORNEY.equals(tag)) {
        v_content[1].append(paraghs.get(i));
        v_content[1].append("\n");
        preKeyIdx = 1;
      } else if (ParaLabelEnum.CASE_SUMMARY.equals(tag)) {
        v_content[2].append(paraghs.get(i));
        v_content[2].append("\n");
        preKeyIdx = 2;
      } else if (ParaLabelEnum.PLAINTIFF_ARGS.equals(tag)
          || ParaLabelEnum.PLAINTIFF_ARGS_FISRT.equals(tag)
          || ParaLabelEnum.PLAINTIFF_ARGS_SECOND.equals(tag)
          || ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL.equals(tag)
          || ParaLabelEnum.PLAINTIFF_ARGS_REVIEW.equals(tag)
          || ParaLabelEnum.DEFENDANT_ARGS_REVIEW.equals(tag)
          || ParaLabelEnum.DEFENDANT_ARGS.equals(tag)
          || ParaLabelEnum.DEFENDANT_ARGS_FIRST.equals(tag)
          || ParaLabelEnum.THIRD_PERSON_ARGS.equals(tag) || ParaLabelEnum.OFFICE_OPINION.equals(tag)
          || ParaLabelEnum.DEFENDANT_ARGS_SECOND.equals(tag)
          || ParaLabelEnum.DEFENDANT_ARGS_ORIGINAL.equals(tag)
          || ParaLabelEnum.FACTS_FOUND_BASE.equals(tag)
          || ParaLabelEnum.FACTS_FOUND_PRIMARY.equals(tag)
          || ParaLabelEnum.FACTS_FOUND_SECONDARY.equals(tag)
          || ParaLabelEnum.FACTS_FOUND.equals(tag) || ParaLabelEnum.FACTS_FOUND_CMPL.equals(tag)
          || ParaLabelEnum.FACTS_ABOVE.equals(tag) || ParaLabelEnum.FACTS_FOUND_REVIEW.equals(tag)
          || ParaLabelEnum.PLEADER_OPINION.equals(tag)) {
        v_content[3].append(paraghs.get(i));
        v_content[3].append("\n");
        preKeyIdx = 3;
        if (ParaLabelEnum.PLAINTIFF_ARGS.equals(tag)
            || ParaLabelEnum.PLAINTIFF_ARGS_FISRT.equals(tag)
            || ParaLabelEnum.PLAINTIFF_ARGS_SECOND.equals(tag)
            || ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL.equals(tag)
            || ParaLabelEnum.PLAINTIFF_ARGS_REVIEW.equals(tag)) {
          v_content[4].append(paraghs.get(i));
          v_content[4].append("\n");
          preKeyIdx = 4;
        } else if (ParaLabelEnum.DEFENDANT_ARGS.equals(tag)
            || ParaLabelEnum.DEFENDANT_ARGS_FIRST.equals(tag)
            || ParaLabelEnum.DEFENDANT_ARGS_SECOND.equals(tag)
            || ParaLabelEnum.DEFENDANT_ARGS_ORIGINAL.equals(tag)
            || ParaLabelEnum.DEFENDANT_ARGS_REVIEW.equals(tag)) {
          v_content[5].append(paraghs.get(i));
          v_content[5].append("\n");
          preKeyIdx = 5;
        }
      } else if (ParaLabelEnum.COURT_BASE_OPINION.equals(tag)
          || ParaLabelEnum.COURT_PRIMARY_OPINION.equals(tag)
          || ParaLabelEnum.COURT_SECONDARY_OPINION.equals(tag)
          || ParaLabelEnum.COURT_REVIEW_OPINION.equals(tag)
          || ParaLabelEnum.COURT_OPINION.equals(tag)) {
        v_content[6].append(paraghs.get(i));
        v_content[6].append("\n");
        preKeyIdx = 6;
      } else if (ParaLabelEnum.JUDGEMENT_CONTENT.equals(tag)) {
        v_content[8].append(paraghs.get(i));
        v_content[8].append("\n");
        preKeyIdx = 8;
      } else if (ParaLabelEnum.CHIEF_JUDGE.equals(tag) || ParaLabelEnum.JUDGES.equals(tag)
          || ParaLabelEnum.JUDGE_ASSESSOR.equals(tag) || ParaLabelEnum.RECORD_DATE.equals(tag)
          || ParaLabelEnum.CLERK.equals(tag) || ParaLabelEnum.EXECUTOR.equals(tag)
          || ParaLabelEnum.CHIEF_EXECUTIVE.equals(tag)) {
        v_content[10].append(paraghs.get(i));
        v_content[10].append("\n");
        preKeyIdx = 10;
      } else if (ParaLabelEnum.LEGAL_PROVISION.equals(tag)) {
        v_content[11].append(paraghs.get(i));
        v_content[11].append("\n");
        preKeyIdx = 11;
      } else {
        if (preKeyIdx != -1) {
          v_content[preKeyIdx].append(paraghs.get(i));
          v_content[preKeyIdx].append("\n");
        } else {
          Matcher matcher = Pattern.compile("[\\u4e00-\\u9fae]").matcher(paraghs.get(i));
          if (matcher.find()) {
            v_content[0].append(paraghs.get(i));
            v_content[0].append("\n");
          }
        }
      }
    }
    // 把案情简要加到文书首部
    v_content[1].append(v_content[2].toString());
    if (v_content[0].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_title, v_content[0].toString());
    }
    if (v_content[1].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_doc_first_part, v_content[1].toString());
    }
    if (v_content[3].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_fact_const, v_content[3].toString());
    }
    if (v_content[4].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_plaintiff_statement, v_content[4].toString());
    }
    if (v_content[5].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_accused_statement, v_content[5].toString());
    }
    if (v_content[6].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_reason, v_content[6].toString());
    }
    if (v_content[7].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_judge_base, v_content[7].toString());
    }
    if (v_content[8].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_judge_main, v_content[8].toString());
    }
    if (v_content[9].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_last_part, v_content[9].toString());
    }
    if (v_content[10].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_signature, v_content[10].toString());
    }
    if (v_content[11].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_relate_law, v_content[11].toString());
    }
    if (v_content[12].length() > 0) {
      context.rabbitInfo.extractInfo.put(k_controversy_focus, v_content[12].toString());
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 调整已经抽取的部分section信息点
   *
   * @param
   * @return
   * @author yu.yao
   */
  private ResultCode adjustSection() {
    // 遍历标签下每一个自然段
    Map<String, Object> extractInfo = context.rabbitInfo.extractInfo;
    if (extractInfo.get(InfoPointKey.section_reason[InfoPointKey.mode]) != null) {
      String str_reason = (String) extractInfo.get(InfoPointKey.section_reason[InfoPointKey.mode]);
      if (!str_reason.isEmpty()) {
        String[] paragraphs = str_reason.split("\n");
        String temp = "";
        String lawBase = "";
        String strControversyFocus = "";
        Pattern patternControversyFocus =
            Pattern.compile("争议焦点(?:是)?([^。;；,，\\?？]*)(?:[。;；,，:：\\?？])");
        if (paragraphs.length >= 1) {
          String paragraph = paragraphs[paragraphs.length - 1];
          Pattern period = Pattern.compile("[。;]");
          Pattern patternProvisionName = Pattern.compile("《([^《》]*)》");
          int begin = -1;
          Matcher matcher = period.matcher(paragraph);
          while (matcher.find()) {
            begin = matcher.start();
          }
          String reason = paragraph;
          if (begin != -1 && begin < paragraph.length() - 1) {
            if (!patternProvisionName.matcher(paragraph).find()) {
              lawBase = "";
              reason = paragraph;
            } else {
              reason = paragraph.substring(0, begin + 1);
              lawBase = paragraph.substring(begin + 1, paragraph.length()) + "\n";
            }
          } else if (begin == -1) {
            if (patternProvisionName.matcher(paragraph).find()) {
              reason = paragraph.substring(0, begin + 1);
              lawBase = paragraph + "\n";
            }
          }
          for (int i = 0; i < paragraphs.length - 1; i++) {
            temp = temp + paragraphs[i] + "\n";
          }
          temp = temp + reason + "\n";
          Matcher matcherControversyFocus = patternControversyFocus.matcher(paragraph);
          while (matcherControversyFocus.find()) {
            strControversyFocus = matcherControversyFocus.group(1);
          }
        }
        temp = temp.replaceAll("[\n\r]+", "\n");
        lawBase = lawBase.replaceAll("[\n\r]+", "\n");
        strControversyFocus = strControversyFocus.replaceAll("[\n\r]+", "\n");
        if (!"".equals(temp)) {
          extractInfo.put(InfoPointKey.section_reason[InfoPointKey.mode], temp);
        }
        if (!"".equals(lawBase)) {
          extractInfo.put(InfoPointKey.section_judge_base[InfoPointKey.mode], lawBase);
        }
        if (!"".equals(strControversyFocus)) {
          extractInfo.put(InfoPointKey.section_controversy_focus[InfoPointKey.mode],
              strControversyFocus);
        }
      }
    }
    if (extractInfo.get(InfoPointKey.section_judge_main[InfoPointKey.mode]) != null) {
      String strJudgeInfo =
          (String) extractInfo.get(InfoPointKey.section_judge_main[InfoPointKey.mode]);
      boolean bFind = false;
      String judge = "";
      String lastpart = "";
      if (!strJudgeInfo.isEmpty()) {
        String[] strSplit = strJudgeInfo.split("\n");
        for (String split : strSplit) {
          if (split.contains("如不服本判决") || split.contains("如不服本裁定")) {
            bFind = true;
          }
          if (bFind)
            lastpart = lastpart + split + "\n";
          else
            judge = judge + split + "\n";
        }
      }
      judge = judge.replaceAll("[\n\r]+", "\n");
      extractInfo.put(InfoPointKey.section_judge_main[InfoPointKey.mode], judge);
      lastpart = lastpart.replaceAll("[\n\r]+", "\n");
      extractInfo.put(InfoPointKey.section_last_part[InfoPointKey.mode], lastpart);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

}
