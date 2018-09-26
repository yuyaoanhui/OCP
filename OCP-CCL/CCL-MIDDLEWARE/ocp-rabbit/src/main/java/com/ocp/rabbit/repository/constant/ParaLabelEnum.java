package com.ocp.rabbit.repository.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 段落标签枚举类 </br>
 * 大标签没有匹配规则，是逻辑上对小标签的分类
 * 
 * @author yu.yao 2018年8月2日
 *
 */
public enum ParaLabelEnum {
  ////////////////////////////////////// 大标签0/////////////////////////////////////////
  DIR_NULL("dir_null", "court", "文书首部", 1, 0, null, null), //
  NULL("null", "court", "文书名称", 2, 1, DIR_NULL, null), //

  ////////////////////////////////////// 大标签1/////////////////////////////////////////
  DIR_DOC_DESC("dir_doc_desc", "court", "文书首部", 1, 1, null, null), //
  COURT("court", "court", "法院", 2, 1, DIR_DOC_DESC, null), //
  CASE_TYPE("case_type", "court", "文书类型", 2, 2, DIR_DOC_DESC, null), //
  CASE_ID("case_id", "court", "案号", 2, 3, DIR_DOC_DESC, null), //

  ////////////////////////////////////// 大标签2/////////////////////////////////////////
  DIR_PEOPLE("dir_people", "court", "人物信息", 1, 2, null, new ParaLabelEnum[] {DIR_DOC_DESC}), //
  REPRESENTATIVE("representative", "court", "法定代表人", 2, 1, DIR_PEOPLE, null), //
  ASSIGNED("assigned", "court", "代理人/监护人", 2, 2, DIR_PEOPLE, null), //
  ATTORNEY("attorney", "court", "律师", 2, 3, DIR_PEOPLE, null), //
  ENTRUSTED("entrusted", "court", "委托代理人", 2, 4, DIR_PEOPLE, null), //
  DEFENDANT("defendant", "court", "被告", 2, 5, DIR_PEOPLE, null), //
  PLAINTIFF("plaintiff", "court", "原告", 2, 6, DIR_PEOPLE, null), //
  THIRD_PERSON("third_person", "court", "第三人", 2, 7, DIR_PEOPLE, null), //

  ///////////////////////////////////// 大标签3/////////////////////////////////////////
  DIR_CASE_SUMMARY("dir_case_summary", "court", "审理经过", 1, 3, null,
      new ParaLabelEnum[] {DIR_PEOPLE}), // 虚拟标签,文件夹
  CASE_SUMMARY("case_summary", "court", "审理经过", 2, 1, DIR_CASE_SUMMARY, null), //

  /////////////////////////////////////// 大标签4///////////////////////////////////////
  DIR_ARGUMENTS("dir_arguments", "court", "事实", 1, 4, null,
      new ParaLabelEnum[] {DIR_PEOPLE, DIR_CASE_SUMMARY}), //
  PLAINTIFF_ARGS_FISRT("plaintiff_args_first", "court", "一审原告诉称", 2, 1, DIR_ARGUMENTS, null), //
  PLAINTIFF_ARGS_SECOND("plaintiff_args_second", "court", "二审原告诉称", 2, 2, DIR_ARGUMENTS, null), //
  PLAINTIFF_ARGS_REVIEW("plaintiff_args_review", "court", "再审原告诉称", 2, 3, DIR_ARGUMENTS, null), //
  PLAINTIFF_ARGS_ORIGINAL("plaintiff_args_original", "court", "原审原告诉称", 2, 4, DIR_ARGUMENTS, null), //
  PLAINTIFF_ARGS("plaintiff_args", "court", "原告诉称", 2, 5, DIR_ARGUMENTS, null), //
  DEFENDANT_ARGS_FIRST("defendant_args_first", "court", "被告一审辩称", 2, 6, DIR_ARGUMENTS, null), //
  DEFENDANT_ARGS_SECOND("defendant_args_second", "court", "被告二审辩称", 2, 7, DIR_ARGUMENTS, null), //
  DEFENDANT_ARGS_REVIEW("defendant_args_review", "court", "被告再审辩称", 2, 8, DIR_ARGUMENTS, null), //
  DEFENDANT_ARGS_ORIGINAL("defendant_args_original", "court", "被告原审辩称", 2, 9, DIR_ARGUMENTS, null), //
  DEFENDANT_ARGS("defendant_args", "court", "被告辩称", 2, 10, DIR_ARGUMENTS, null), //
  FACTS_FOUND_REVIEW("facts_found_review", "court", "再审查明", 2, 11, DIR_ARGUMENTS, null), //
  FACTS_FOUND_SECONDARY("facts_found_secondary", "court", "二审查明", 2, 12, DIR_ARGUMENTS, null), //
  FACTS_FOUND_PRIMARY("facts_found_primary", "court", "一审查明", 2, 13, DIR_ARGUMENTS, null), //
  FACTS_FOUND_BASE("facts_found_base", "court", "原审查明", 2, 14, DIR_ARGUMENTS, null), //
  FACTS_FOUND("facts_found", "court", "经审查明", 2, 15, DIR_ARGUMENTS, null), //
  FACTS_FOUND_CMPL("facts_found_cmpl", "court", "另查明", 2, 16, DIR_ARGUMENTS, null), //
  FACTS_ABOVE("facts_above", "court", "上诉事实", 2, 17, DIR_ARGUMENTS, null), //
  COURT_BASE_OPINION("court_base_opinion", "court", "原审法院认为(观点)", 2, 18, DIR_ARGUMENTS, null), //
  COURT_PRIMARY_OPINION("court_primary_opinion", "court", "一审法院认为(观点)", 2, 19, DIR_ARGUMENTS, null), //
  COURT_SECONDARY_OPINION("court_secondary_opinion", "court", "二审法院认为(观点)", 2, 20, DIR_ARGUMENTS,
      null), //
  COURT_REVIEW_OPINION("court_review_opinion", "court", "再审法院认为(观点)", 2, 21, DIR_ARGUMENTS, null), //
  OFFICE_OPINION("office_opinion", "court", "公(抗)诉机关意见", 2, 22, DIR_ARGUMENTS, null), //
  PLEADER_OPINION("pleader_opinion", "court", "辩护人意见", 2, 23, DIR_ARGUMENTS, null), //

  /////////////////////////////////////////// 大标签5////////////////////////////////////////
  DIR_COURT_OPINION("dir_court_opinion", "court", "法院观点(本院认为)", 1, 5, null,
      new ParaLabelEnum[] {DIR_CASE_SUMMARY, DIR_ARGUMENTS}), //
  COURT_OPINION("court_opinion", "court", "法院观点(本院认为)", 2, 1, DIR_COURT_OPINION, null), //

  /////////////////////////////////////////// 大标签6///////////////////////////////////////
  DIR_JUDGEMENT_CONTENT("dir_judgement_content", "court", "裁判结果(主文)", 1, 6, null,
      new ParaLabelEnum[] {}), //
  JUDGEMENT_CONTENT("judgement_content", "court", "裁判结果(主文)", 2, 1, DIR_JUDGEMENT_CONTENT, null), //

  /////////////////////////////////////////// 大标签7///////////////////////////////////////
  DIR_JUDGE("dir_judge", "court", "审判人员", 1, 7, null,
      new ParaLabelEnum[] {DIR_COURT_OPINION, DIR_CASE_SUMMARY, DIR_PEOPLE}), //
  CHIEF_JUDGE("chief_judge", "court", "审判长", 2, 1, DIR_JUDGE, null), //
  JUDGES("judges", "court", "审判员", 2, 2, DIR_JUDGE, null), //
  JUDGE_ASSESSOR("judge_assessor", "court", "人民陪审员", 2, 3, DIR_JUDGE, null), //
  CHIEF_EXECUTIVE("chief_executive", "court", "执行长", 2, 4, DIR_JUDGE, null), //
  EXECUTOR("executor", "court", "执行员", 2, 5, DIR_JUDGE, null), //

  ///////////////////////////////////////// 大标签8/////////////////////////////////////////
  DIR_RECORD_DATE("dir_record_date", "court", "落款日期", 1, 8, null,
      new ParaLabelEnum[] {DIR_JUDGE, COURT_OPINION}), //
  RECORD_DATE("record_date", "court", "落款日期", 2, 1, DIR_RECORD_DATE, null), //

  ///////////////////////////////////////// 大标签9/////////////////////////////////////////
  DIR_CLERK("dir_clerk", "court", "书记员", 1, 9, null,
      new ParaLabelEnum[] {DIR_JUDGE, COURT_OPINION}), //
  CLERK("clerk", "court", "书记员", 2, 1, DIR_CLERK, null), //

  //////////////////////////////////////// 大标签10//////////////////////////////////////////
  DIR_LEGAL_PROVISION("dir_legal_provision", "court", "附：相关法条", 1, 10, null,
      new ParaLabelEnum[] {}), //
  LEGAL_PROVISION("legal_provision", "court", "附：相关法条", 2, 1, DIR_LEGAL_PROVISION, null), //

  //////////////////////////////////////// 检察院//////////////////////////////////////////
  DIR_PROCURATORATE("dir_procuratorate", "procuratorate", "检察院文书大标签", 1, 1, null, null), //
  THIRD_PERSON_ARGS("third_person_args", "procuratorate", "第三人诉称", 2, 1, DIR_PROCURATORATE, null), //
  SUSPECT_BASE_INFO("SUSPECT_BASE_INFO", "procuratorate", "犯罪嫌疑人基本情况", 2, 2, DIR_PROCURATORATE,
      null), //
  PROCURATORATE("procuratorate", "procuratorate", "检察院", 2, 3, DIR_PROCURATORATE, null), //
  TITLE("title", "procuratorate", "标题", 2, 4, DIR_PROCURATORATE, null), //
  SOLVE_PROCESS("solve_process", "procuratorate", "发案、立案、破案经过", 2, 5, DIR_PROCURATORATE, null), //
  CASE_FACT_EVIDENCE("case_fact_evidence", "procuratorate", "经审查认定的案件事实及证据", 2, 6,
      DIR_PROCURATORATE, null), //
  HANDLE_OPINION("handle_opinion", "procuratorate", "经审查认定的案件事实及证据", 2, 7, DIR_PROCURATORATE, null), //
  SIGNATURE("signature", "procuratorate", "落款日期", 2, 8, DIR_PROCURATORATE, null), //
  SUSPECT_PROCESS("suspect_process", "procuratorate", "受案和审查过程", 2, 9, DIR_PROCURATORATE, null), //
  REAUTHEN_REASON("reauthen_reason", "procuratorate", "提请复核或复议的理由及根据", 2, 10, DIR_PROCURATORATE,
      null), //
  QUES_TO_EXPLAIN("ques_to_explain", "procuratorate", "需要说明的问题", 2, 11, DIR_PROCURATORATE, null), //
  RISK_ANALYSIS("risk_analysis", "procuratorate", "社会危险性", 2, 12, DIR_PROCURATORATE, null), //
  RISK_ASSES_PLAN("risk_asses_plan", "procuratorate", "办案风险评估及预案", 2, 13, DIR_PROCURATORATE, null), //
  ADDITION("addition", "procuratorate", "文书尾部", 2, 14, DIR_PROCURATORATE, null), //
  ASK_REAUTHEN_REASON("ask_reauthen_reason", "procuratorate", "提请复核或复议的理由及根据", 2, 15,
      DIR_PROCURATORATE, null), //
  REVIEW_FACT_EVIDENCE("review_fact_evidence", "procuratorate", "经复核或复议认定的的案件事实及证据", 2, 16,
      DIR_PROCURATORATE, null), //
  INVESTIGATE_FACT_AND_REASON("investigate_fact_and_reason", "procuratorate", "侦查机关认定的案件事实及理由", 2,
      17, DIR_PROCURATORATE, null), //
  REASON_NOT_ARREST("reason_not_arrest", "procuratorate", "原不批准逮捕的理由", 2, 18, DIR_PROCURATORATE,
      null);

  private String label;// 段落标签
  private String org;// 组织机构
  private String desc;// 标签描述
  private int level;// 标签级别(从1开始)
  private int sequence;// 标签编号(各自级别下从1开始)
  private ParaLabelEnum superLabel;// 父标签
  private ParaLabelEnum[] preLabel;// 前置标签

  /**
   * construct
   * 
   * @param label String ：标签code
   * @param desc String ：标签描述
   * @param level int ：标签级别(从1开始)
   * @param sequence int ：标签编号(各自级别下从1开始)
   * @param superLabel ParaLabelEnum ：父标签
   * @param String content : 标签内容
   */
  private ParaLabelEnum(String label, String org, String desc, int level, int sequence,
      ParaLabelEnum superLabel, ParaLabelEnum[] preLabel) {
    this.label = label;
    this.org = org;
    this.desc = desc;
    this.level = level;
    this.sequence = sequence;
    this.superLabel = superLabel;
    this.preLabel = preLabel;
  }

  public static Map<ParaLabelEnum, List<ParaLabelEnum>> getPreCondition() {
    Map<ParaLabelEnum, List<ParaLabelEnum>> map = new HashMap<ParaLabelEnum, List<ParaLabelEnum>>();
    for (ParaLabelEnum instance : ParaLabelEnum.values()) {
      if (instance.org.equals("court")) {
        if (instance.level == 1) {
          if (instance.preLabel != null) {
            map.put(instance, Arrays.asList(instance.preLabel));
          }
        }
      }
    }
    return map;
  }


  /**
   * 根据标签名获得标签列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static ParaLabelEnum getByLabel(String label) {
    for (ParaLabelEnum instance : ParaLabelEnum.values()) {
      if (label.equals(instance.label)) {
        return instance;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    return label;
  }

  public String getLabel() {
    return label;
  }

  public int getLevel() {
    return level;
  }

  public int getSequence() {
    return sequence;
  }

  public ParaLabelEnum getSuperLabel() {
    return superLabel;
  }

  public String getDesc() {
    return desc;
  }

  public ParaLabelEnum[] getPreLabel() {
    return preLabel;
  }

  public void setPreLabel(ParaLabelEnum[] preLabel) {
    this.preLabel = preLabel;
  }

  public String getOrg() {
    return org;
  }
}
