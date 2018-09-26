package com.ocp.rabbit.repository.entity;

import com.ocp.rabbit.repository.util.PropertiesUtil;
/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class InfoPointKey {
  public static int mode = Integer.valueOf(PropertiesUtil.getProperty("rabbit.properties", "infopointkey.mode")); // 0-english1-中文
  //p1:section
  public static final String[] section_title = new String[]{"section_title", "section_标题"};
  public static final String[] section_doc_first_part = new String[]{"section_doc_first_part", "section_文书首部"};
  public static final String[] section_sub_fact = new String[]{"section_sub_fact", "section_案情简要"};
  public static final String[] section_fact_const = new String[]{"section_fact_const", "section_事实构成"};
  public static final String[] section_plaintiff_statement = new String[]{"section_plaintiff_statement", "section_原告陈述"};
  public static final String[] section_accused_statement = new String[]{"section_accused_statement", "section_被告陈述"};
  public static final String[] section_reason = new String[]{"section_reason", "section_理由"};
  public static final String[] section_judge_base = new String[]{"section_judge_base", "section_裁判依据"};
  public static final String[] section_judge_main = new String[]{"section_judge_main", "section_判决主文"};
  public static final String[] section_last_part = new String[]{"section_last_part", "section_文书尾部"};
  public static final String[] section_signature = new String[]{"section_signature", "section_落款"};
  public static final String[] section_relate_law = new String[]{"section_relate_law", "section_相关法条"};
  public static final String[] section_other = new String[]{"section_other", "section_其他"};
  public static final String[] section_controversy_focus = new String[]{"section_controversy_focus", "section_争议焦点"}; //?
  public static final String[] section_title2 = new String[]{"section_title2", "section_标题2"};
  public static final String[] section_doc_first_part2 = new String[]{"section_doc_first_part2", "section_文书首部2"};
  public static final String[] section_sub_fact2 = new String[]{"section_sub_fact2", "section_案情简要2"};
  public static final String[] section_fact_const2 = new String[]{"section_fact_const2", "section_事实构成2"};
  public static final String[] section_reason2 = new String[]{"section_reason2", "section_理由2"};
  public static final String[] section_judge_base2 = new String[]{"section_judge_base2", "section_裁判依据2"};
  public static final String[] section_judge_main2 = new String[]{"section_judge_main2", "section_判决主文2"};
  public static final String[] section_last_part2 = new String[]{"section_last_part2", "section_文书尾部2"};
  public static final String[] section_signature2 = new String[]{"section_signature2", "section_落款2"};
  public static final String[] section_relate_law2 = new String[]{"section_relate_law2", "section_相关法条2"};
  public static final String[] section_other2 = new String[]{"section_other2", "section_其他2"};
  public static final String[] section_controversy_focus2 = new String[]{"section_controversy_focus2", "section_争议焦点2"};


  //p2:document base
  public static final String[] pos = new String[]{"pos", "高亮位置"};
  public static final String[] caseId = new String[]{"caseId", "caseId"}; //caseId字段，每个文书都是唯一
  public static final String[] meta_id = new String[]{"meta_id", "meta_编号"};
  public static final String[] meta_source_text_name = new String[]{"meta_source_text_name", "meta_文书名称"};
  public static final String[] meta_case_name = new String[]{"meta_case_name", "meta_案件名称"};
  public static final String[] meta_case_ay = new String[]{"meta_case_ay", "meta_案由"};
  public static final String[] meta_case_from = new String[]{"meta_case_from", "meta_案件来源"};
  public static final String[] meta_case_remand = new String[]{"meta_case_remand", "meta_发回重审"};
  public static final String[] meta_case_procedure_type = new String[]{"meta_case_procedure_type", "meta_审理程序类型"};
  public static final String[] meta_case_register_date = new String[]{"meta_case_register_date", "meta_立案时间"};
  public static final String[] meta_case_prosecute_date = new String[]{"meta_case_prosecute_date", "meta_提起公诉时间"};
  public static final String[] meta_case_start_date = new String[]{"meta_case_start_date", "meta_开庭审理时间"};
  public static final String[] meta_case_session = new String[]{"meta_case_session", "meta_开庭情况"};
  public static final String[] meta_procuratorate_name = new String[]{"meta_procuratorate_name", "meta_检察院名称"};
  public static final String[] meta_procuratorator = new String[]{"meta_procuratorator", "meta_出庭检察官"};
  public static final String[] meta_court_name = new String[]{"meta_court_name", "meta_法院名称"};
  public static final String[] meta_court_province = new String[]{"meta_court_province", "meta_法院_省"};
  public static final String[] meta_court_city = new String[]{"meta_court_city", "meta_法院_市"};
  public static final String[] meta_court_county = new String[]{"meta_court_county", "meta_法院_区县"};
  public static final String[] meta_court_hierarchy = new String[]{"meta_court_hierarchy", "meta_法院层级"};
  public static final String[] meta_case_type = new String[]{"meta_case_type", "meta_案件类型"};
  public static final String[] meta_judgement_type = new String[]{"meta_judgement_type", "meta_判决类型"};
  public static final String[] meta_doc_name = new String[]{"meta_doc_name", "meta_判决书名字"};
  public static final String[] meta_case_id = new String[]{"case_id", "meta_案号"};
  public static final String[] meta_case_hierarchy = new String[]{"meta_case_hierarchy", "meta_判决层级"};
  public static final String[] meta_case_close_manner = new String[]{"meta_case_close_manner", "meta_案件结果"};
  public static final String[] meta_doc_date = new String[]{"meta_doc_date", "meta_裁判日期"};
  public static final String[] meta_legal_provision = new String[]{"meta_legal_provision", "meta_法律条款"};
  public static final String[] meta_legal_provision_id = new String[]{"meta_legal_provision_id", "meta_法律条款索引"};
  //p3:personage
  public static final String[] info_absence = new String[]{"info_absence", "info_被告不在场"};
  public static final String[] meta_defendant_names = new String[]{"meta_defendant_names", "meta_被告"};
  public static final String[] meta_plaintiff_names = new String[]{"meta_plaintiff_names", "meta_原告"};
  public static final String[] meta_law_name = new String[]{"meta_law_name", "meta_律师"};
  public static final String[] meta_law_firm = new String[]{"meta_law_firm", "meta_律所"};
  public static final String[] meta_people_relation = new String[]{"meta_people_relation", "meta_当事人关系"};
  public static final String[] meta_people_name2obj = new String[]{"meta_people_name2obj", "meta_名字到实体的映射"};
  public static final String[] meta_company_simply = new String[]{"meta_company_simply", "meta_公司机构简称"}; // 暂时没有用到
  public static final String[] meta_people_attr = new String[]{"meta_people_attr", "meta_人物信息"};
  public static final String[] meta_chief_judge = new String[]{"meta_chief_judge", "meta_审判长"};
  public static final String[] meta_judges = new String[]{"meta_judges", "meta_审判员"};
  public static final String[] meta_clerk = new String[]{"meta_clerk", "meta_书记员"};
  public static final String[] meta_fee_case_total = new String[]{"meta_fee_case_total", "meta_案件受理费总额"};
  public static final String[] meta_fee_case = new String[]{"meta_fee_case", "meta_案件受理费"};
  public static final String[] meta_related_caseid = new String[]{"meta_related_caseid", "meta_关联案号"};
  public static final String[] meta_related_case = new String[]{"meta_related_case", "meta_关联文书"};
  public static final String[] meta_indictment = new String[]{"meta_indictment", "meta_起诉书"};
  //p4:people other arr
  public static final String[] info_all_litigant_positions = new String[]{"info_all_litigant_positions", "info_当事人所有地位"};
  public static final String[] info_litigant_position = new String[]{"info_litigant_position", "info_当事人地位"};
  public static final String[] info_company = new String[]{"info_company", "info_工作单位"};
  public static final String[] info_political_party = new String[]{"info_political_party", "info_党派"};
  public static final String[] info_gov_ins_category = new String[]{"info_gov_ins_category", "info_政府事业单位分类"};
  public static final String[] info_administrative_level = new String[]{"info_administrative_level", "info_行政级别分类"};
  public static final String[] info_occupation = new String[]{"info_occupation", "info_职业"};
  public static final String[] info_occupation_category = new String[]{"info_occupation_category", "info_职业_分类"};
  public static final String[] info_occupation_category_tdh = new String[]{"info_occupation_category_tdh", "info_职业_分类_TDH"};
  public static final String[] info_id_card = new String[]{"info_id_card", "info_身份证号码"};
  public static final String[] info_organization_code = new String[]{"info_organization_code", "info_组织机构代码"};
  public static final String[] info_residence_addr = new String[]{"info_residence_addr", "info_居住地址"};
  public static final String[] info_cr_addr = new String[]{"info_cr_addr", "info_户籍地址"};
  public static final String[] info_dob_addr = new String[]{"info_dob_addr", "info_出生地址"};
  public static final String[] info_residence_addr_category = new String[]{"info_residence_addr_category", "info_居住地址_分类"};
  public static final String[] info_cr_addr_category = new String[]{"info_cr_addr_category", "info_户籍地址_分类"};
  public static final String[] info_dob_addr_category = new String[]{"info_dob_addr_category", "info_出生地址_分类"};
  public static final String[] info_birthday = new String[]{"info_birthday", "info_出生日期"};
  public static final String[] info_ethnicity = new String[]{"info_ethnicity", "info_民族"};
  public static final String[] info_age = new String[]{"info_age", "info_年龄"};
  public static final String[] info_gender = new String[]{"info_gender", "info_性别"};
  public static final String[] info_educational_status = new String[]{"info_educational_status", "info_教育程度"};
  public static final String[] info_14_16 = new String[]{"info_14_16", "info_14_16周岁"};
  public static final String[] info_16_18 = new String[]{"info_16_18", "info_16_18周岁"};
  public static final String[] info_yonger = new String[]{"info_yonger", "info_14_18周岁"};
  public static final String[] info_exceed_65 = new String[]{"info_exceed_65", "info_满65周岁"};
  public static final String[] info_older = new String[]{"info_older", "info_满75周岁"};
  public static final String[] info_first_crime_date = new String[]{"info_first_crime_date", "info_第一次犯案时间"};
  public static final String[] info_custody_date = new String[]{"info_custody_date", "info_逮捕或拘留时间"};
  public static final String[] info_crime_duration = new String[]{"info_crime_duration", "info_犯罪时长"};
  public static final String[] info_degree_heavier = new String[]{"info_degree_heavier", "info_从重"};
  public static final String[] info_degree_lower = new String[]{"info_degree_lower", "info_从轻"};
  public static final String[] info_active_compensation = new String[]{"info_active_compensation", "info_积极赔偿"};
  public static final String[] info_criminal_reconciliation = new String[]{"info_criminal_reconciliation", "info_刑事和解"};
  public static final String[] info_principle_criminal = new String[]{"info_principle_criminal", "info_主犯"};
  public static final String[] info_accessory = new String[]{"info_accessory", "info_从犯"};
  public static final String[] info_not_main_crimer = new String[]{"info_not_main_crimer", "info_非主从犯"};
  public static final String[] info_crime_leader = new String[]{"info_crime_leader", "info_首要分子"};
  public static final String[] info_abetment = new String[]{"info_abetment", "info_教唆犯"};
  public static final String[] info_excessive_defence = new String[]{"info_excessive_defence", "info_防卫过当"};
  public static final String[] info_excessive_danger_avoid = new String[]{"info_excessive_danger_avoid", "info_避险过当"};
  public static final String[] info_custody_start_date = new String[]{"info_custody_start_date", "info_羁押开始时间"};
  public static final String[] info_custody_end_date = new String[]{"info_custody_end_date", "info_羁押结束时间"};
  public static final String[] info_blind = new String[]{"info_blind", "info_盲人"};
  public static final String[] info_compensation = new String[]{"info_compensation", "info_退赃退赔"};
  public static final String[] info_understanding = new String[]{"info_understanding", "info_谅解"};
  public static final String[] info_gang_crime = new String[]{"info_gang_crime", "info_团伙"};
  public static final String[] info_confession = new String[]{"info_confession", "info_坦白"};
  public static final String[] info_confession_incourt = new String[]{"info_confession_incourt", "info_认罪"};
  public static final String[] info_jailbird = new String[]{"info_jailbird", "info_一般累犯"};
  public static final String[] info_offender = new String[]{"info_offender", "info_初犯偶犯"};
  public static final String[] info_contrition = new String[]{"info_contrition", "info_悔罪"};
  public static final String[] info_tell_truth = new String[]{"info_tell_truth", "info_如实供述"};
  public static final String[] info_active_return = new String[]{"info_active_return", "info_主动归还"};
  public static final String[] info_crime_multiple_spot = new String[]{"info_crime_multiple_spot", "info_流窜作案"};
  public static final String[] info_surrender = new String[]{"info_surrender", "info_自首"};
  public static final String[] info_merits_gain = new String[]{"info_merits_gain", "info_立功"};
  public static final String[] info_mental_disorder = new String[]{"info_mental_disorder", "info_精神病人"};
  public static final String[] info_deaf_mute = new String[]{"info_deaf_mute", "info_又聋又哑"};
  public static final String[] info_coerced_offender = new String[]{"info_coerced_offender", "info_胁从犯"};
  public static final String[] info_prepared_offender = new String[]{"info_prepared_offender", "info_预备犯"};
  public static final String[] info_aborted_offender = new String[]{"info_aborted_offender", "info_中止犯"};
  public static final String[] info_incomplete_offender = new String[]{"info_incomplete_offender", "info_未遂犯"};
  public static final String[] info_duress_offender = new String[]{"info_duress_offender", "info_被胁迫参加犯罪"};
  public static final String[] info_minor_abetment = new String[]{"info_minor_abetment", "info_教唆未成年人犯罪"};
  public static final String[] info_record_crime = new String[]{"info_record_crime", "info_前科罪名"};
  public static final String[] info_record = new String[]{"info_record", "info_前科"};
  public static final String[] info_sufferd_criminal_penalty = new String[]{"info_sufferd_criminal_penalty", "受过刑事处罚"}; //暂时没用
  public static final String[] info_sufferd_administrative_penalty_in_year = new String[]{"info_sufferd_admin_penalty_in_year", "一年内受过行政处罚"}; //暂时没用
  public static final String[] info_judgement_name = new String[]{"info_judgement_name", "info_主刑罪名"};
  public static final String[] info_criminal_judgetype = new String[]{"info_criminal_judgetype", "info_判罚类型"};
  public static final String[] info_judgement_time = new String[]{"info_judgement_time", "info_主刑月数"};
  public static final String[] info_fine = new String[]{"info_fine", "info_罚金数额"};
  public static final String[] info_probation_time = new String[]{"info_probation_time", "info_缓刑月数"};
  public static final String[] info_probation = new String[]{"info_probation", "info_缓刑"};
  public static final String[] info_deportation = new String[]{"info_deportation", "info_驱逐出境"};
  public static final String[] info_confiscate_property = new String[]{"info_confiscate_property", "info_没收财产"};
  public static final String[] info_deprive_politic_rights = new String[]{"info_deprive_politic_rights", "info_剥夺政治权利"};
  public static final String[] info_deprive_politic_term = new String[]{"info_deprive_politic_term", "info_剥夺政治权利期限"};
  public static final String[] info_above_fixed_term_penalty = new String[]{"info_above_fixed_term_penalty", "info_有期徒刑以上刑"};
  public static final String[] info_under_fixed_term_penalty = new String[]{"info_under_fixed_term_penalty", "info_有期徒刑以下刑"};
  public static final String[] info_additional_penalty = new String[]{"info_additional_penalty", "info_独立适用附加刑"};
  public static final String[] info_innocent_reason = new String[]{"info_innocent_reason", "info_无罪理由"};
  public static final String[] info_abetment_deaf_mute_restricts = new String[]{"info_abetment_deaf_mute_restricts", "info_教唆限制行为能力人或聋哑人"};


  public static final String[] info_department_opinion = new String[]{"info_department_opinion", "info_决定机关"};
  public static final String[] info_department_enforcement = new String[]{"info_department_enforcement", "info_执行机关"};
  public static final String[] info_capture_date = new String[]{"info_capture_date", "info_抓获时间"};
  public static final String[] info_detention_opinion_date = new String[]{"info_detention_opinion_date", "info_决定刑事拘留时间"};
  public static final String[] info_detention_action_date = new String[]{"info_detention_action_date", "info_执行刑事拘留时间"};
  public static final String[] info_arrest_opinion_date = new String[]{"info_arrest_opinion_date", "info_决定逮捕时间"};
  public static final String[] info_arrest_action_date = new String[]{"info_arrest_action_date", "info_执行逮捕时间"};
  public static final String[] info_bail_procuratorate_date = new String[]{"info_bail_procuratorate_date", "info_检察院取保时间"};
  public static final String[] info_bail_court_date = new String[]{"info_bail_court_date", "info_法院取保时间"};
  public static final String[] info_bail_police_date = new String[]{"info_bail_police_date", "info_公安局取保时间"};
  public static final String[] info_residence_monitor_date = new String[]{"info_residence_monitor_date", "info_监视居住时间"};
  public static final String[] info_residence_call_date = new String[]{"info_residence_call_date", "info_拘传时间"};

  //审查逮捕意见书
  public static final String[] section_suspect_process = new String[]{"section_suspect_process", "section_受案和审查过程"};
  public static final String[] section_suspect_base_info = new String[]{"section_suspect_base_info", "section_犯罪嫌疑人基本情况"};
  public static final String[] section_solve_process = new String[]{"section_solve_process", "section_发案、立案、破案经过"};
  public static final String[] section_case_fact_evidence = new String[]{"section_case_fact_evidence", "section_经审查认定的案件事实及证据"};
  public static final String[] section_ques_to_explain = new String[]{"section_ques_to_explain", "section_需要说明的问题"};
  public static final String[] section_risk_analysis = new String[]{"section_risk_analysis", "section_社会危险性分析"};
  public static final String[] section_risk_asses_plan = new String[]{"section_risk_asses_plan", "section_办案风险评估及预案"};
  public static final String[] section_handle_opinion = new String[]{"section_handle_opinion", "section_处理意见"};
  public static final String[] meta_signature_date = new String[]{"meta_signature_date", "meta_落款日期"};

  public static final String[] meta_procuratorate_province = new String[]{"meta_procuratorate_province", "meta_检察院_省"};
  public static final String[] meta_procuratorate_city = new String[]{"meta_procuratorate_city", "meta_检察院_市"};
  public static final String[] meta_procuratorate_county = new String[]{"meta_procuratorate_county", "meta_检察院_区县"};
  public static final String[] meta_document_type = new String[]{"meta_document_type", "meta_文书类型"};
  public static final String[] meta_procurate_type = new String[]{"meta_procurate_type", "meta_检察业务类型"};
  public static final String[] meta_investigate_time = new String[]{"meta_investigate_time", "meta_侦查机关移送时间"};
  public static final String[] meta_investigate_name = new String[]{"meta_investigate_name", "meta_侦查机关名称"};
  public static final String[] meta_contractor = new String[]{"meta_contractor", "meta_承办人"};
  public static final String[] meta_suspect = new String[]{"meta_suspect", "meta_嫌疑人"};
  public static final String[] info_justice_situation = new String[]{"info_justice_situation", "info_归案情况"};
  public static final String[] info_infracted_object_classification = new String[]{"info_infracted_object_classification", "info_侵害物品分类"};
  public static final String[] info_infracted_spot_classification = new String[]{"info_infracted_spot_classification", "info_侵害场所分类"};
  //职务侵占刑事案由
  public static final String[] info_encroach_amount = new String[]{"info_encroach_amount", "info_职务侵占数额"};
  public static final String[] info_impact_production = new String[]{"info_impact_production", "info_严重影响生产经营"};
  public static final String[] info_severe_loss = new String[]{"info_severe_loss", "info_造成严重损失"};
  public static final String[] info_bad_influence = new String[]{"info_bad_influence", "info_恶劣影响"};
  public static final String[] info_enterprise_in_restructuring = new String[]{"info_enterprise_in_restructuring", "info_在企业改制、破产、重组过程中"};
  public static final String[] info_special_relief_property = new String[]{"info_special_relief_property", "info_侵占专项、灾害款物"};
  public static final String[] info_multiple_encroach = new String[]{"info_multiple_encroach", "info_多次侵占"};
  public static final String[] info_for_illegal_purpose = new String[]{"info_for_illegal_purpose", "info_为违法活动"};
  //四级民事案由-金融借款合同纠纷
  //案件事实
  public static final String[] info_loan_principal = new String[]{"info_loan_principal", "info_借贷本金"};
  public static final String[] info_loan_time = new String[]{"info_loan_time", "info_借贷期限"};
  public static final String[] info_loan_interest_rate = new String[]{"info_loan_interest_rate", "info_借贷年利率"};
  public static final String[] info_interest_account_method = new String[]{"info_interest_account_method", "info_利息结算方式"};
  public static final String[] info_overdue_interest_rate = new String[]{"info_overdue_interest_rate", "info_逾期年利率"};
  //裁判结果
  public static final String[] info_support_loan_principal = new String[]{"info_support_loan_principal", "info_支持借贷本金"};
  public static final String[] info_support_loan_interest_rate = new String[]{"info_support_loan_interest_rate", "info_支持借贷年利率"};
  public static final String[] info_support_overdue_interest_rate = new String[]{"info_support_overdue_interest_rate", "info_支持逾期年利率"};
  public static final String[] info_support_duty_undertake_method = new String[]{"info_support_duty_undertake_method", "info_担保责任承担方式"};
  //四级民事案由-劳动合同纠纷
  //案件事实
  public static final String[] info_labor_contract_signing = new String[]{"info_labor_contract_signing", "info_劳动合同签订"};
  public static final String[] info_labor_contract_term = new String[]{"info_labor_contract_term", "info_劳动合同期限"};
  public static final String[] info_dissolve_contract_speaker = new String[]{"info_dissolve_contract_speaker", "info_解除合同提起方"};
  public static final String[] info_dissolve_contract_reason = new String[]{"info_dissolve_contract_reason", "info_解除合同原因"};
  public static final String[] info_economic_compensation_time = new String[]{"info_economic_compensation_time", "info_经济补偿金计算时间"};
  public static final String[] info_pay_compensation_reason = new String[]{"info_pay_compensation_reason", "info_支付经济赔偿金原因"};
  //裁判结果
  public static final String[] info_labor_relation_affirm = new String[]{"info_labor_relation_affirm", "info_劳动关系认定"};
  public static final String[] info_labor_contract_determine = new String[]{"info_labor_contract_determine", "info_劳动合同履行判定"};
  //四级民事案由-追索劳动报酬纠纷
  //案件事实
  public static final String[] info_labor_arrear_time = new String[]{"info_labor_arrear_time", "info_劳动报酬拖欠时间"};
  public static final String[] info_overtime_salary_standard = new String[]{"info_overtime_salary_standard", "info_加班工资计算基数"};
  //劳务派遣合同纠纷 案件事实
  public static final String[] info_labor_dispatch_time = new String[]{"info_labor_dispatch_time", "info_劳务派遣时间"};
  //竞业限制纠纷 案件事实
  public static final String[] info_non_competitive_time = new String[]{"info_non_competitive_time", "info_离职后竞业限制时间"};
  //竞业限制纠纷 裁判结果
  public static final String[] info_liquidated_damages = new String[]{"info_liquidated_damages", "info_违约金"};
  //非全日制用工纠纷 案件事实
  public static final String[] info_work_time_per_week = new String[]{"info_work_time_per_week", "info_每周工作时间"};
  public static final String[] info_salary_calc_method = new String[]{"info_salary_calc_method", "info_工资计算方式"};
  public static final String[] info_salary_calc_cycle = new String[]{"info_salary_calc_cycle", "info_工资结算周期"};
  //物业服务合同纠纷 案件事实
  public static final String[] info_litigant_parties = new String[]{"info_litigant_parties", "info_诉讼双方"};
  public static final String[] info_late_fee = new String[]{"info_late_fee", "info_滞纳金"};
  //物业服务合同纠纷 裁判结果
  public static final String[] info_late_fee_standard = new String[]{"info_late_fee_standard", "info_滞纳金每日标准"};
  public static final String[] info_property_management_fee = new String[]{"info_property_management_fee", "info_物业费"};
  //房屋买卖合同纠纷 案件事实
  public static final String[] info_contract_effect = new String[]{"info_contract_effect", "info_合同效力"};
  public static final String[] info_contract_nature = new String[]{"info_contract_nature", "info_合同性质"};
  public static final String[] info_house_uses_nature = new String[]{"info_house_uses_nature", "info_房屋使用性质"};
  public static final String[] info_payment_method = new String[]{"info_payment_method", "info_付款方式"};
  //违约行为未用到，后期可删除
  public static final String[] info_breach_contract = new String[]{"info_breach_contract", "info_违约行为"};
  public static final String[] info_breach_contract_degree = new String[]{"info_breach_contract_degree", "info_违约程度"};
  //违约方暂时未抽取
  public static final String[] info_breach_contract_party = new String[]{"info_breach_contract_party", "info_违约方"};
  //房屋买卖合同纠纷 裁判结果
  public static final String[] info_contract_effect_judgement = new String[]{"info_contract_effect_judgement", "info_合同效力判定"};
  public static final String[] info_contract_fulfill_judgement = new String[]{"info_contract_fulfill_judgement", "info_合同履行判定"};
  public static final String[] info_interest_payment_class = new String[]{"info_interest_payment_class", "info_支付利率类别"};
  public static final String[] info_payment_method_judgement = new String[]{"info_payment_method_judgement", "info_付款方式判定"};
  public static final String[] info_fine_rule_judgement = new String[]{"info_fine_rule_judgement", "info_适用定金罚则"};
  public static final String[] info_breach_contract_rate = new String[]{"info_breach_contract_rate", "info_违约金计算基数"};
  //非法拘禁 刑事案由
  public static final String[] info_beat_insult = new String[]{"info_beat_insult", "info_殴打、侮辱"};
  public static final String[] info_gover_officials_use_authority = new String[]{"info_gover_officials_use_authority", "info_国家公职人员利用职权"};
  public static final String[] info_detention_pregnant = new String[]{"info_detention_pregnant", "info_拘禁孕妇"};
  public static final String[] info_detention_multiple = new String[]{"info_detention_multiple", "info_多次非法拘禁"};
  public static final String[] info_pretending_police_soldier = new String[]{"info_pretending_police_soldier", "info_冒充军警人员"};
  public static final String[] info_pretending_judicial_officer = new String[]{"info_pretending_judicial_officer", "info_冒充司法人员"};
  public static final String[] info_hold_weapon = new String[]{"info_hold_weapon", "info_持凶器"};
  public static final String[] info_claim_illegal_debt = new String[]{"info_claim_illegal_debt", "info_为索取非法债务"};
  public static final String[] info_pyramid_scheme = new String[]{"info_pyramid_scheme", "info_传销"};
  public static final String[] info_legal_debt_rights = new String[]{"info_legal_debt_rights", "info_为合法债务或权益"};
  public static final String[] info_detention_time = new String[]{"info_detention_time", "info_拘禁时间"};
  public static final String[] info_detention_number = new String[]{"info_detention_number", "info_非法拘禁人数"};
  public static final String[] info_death_toll = new String[]{"info_death_toll", "info_死亡人数"};

  //抢劫罪 刑事案由
  public static final String[] info_public_place = new String[]{"info_public_place", "info_公共场所"};
  public static final String[] info_rob_family_members = new String[]{"info_rob_family_members", "info_抢劫家庭成员"};
  public static final String[] info_close_relatives = new String[]{"info_close_relatives", "info_抢劫近亲属财物"};
  public static final String[] info_plot_degree = new String[]{"info_plot_degree", "info_情节严重程度"};
  public static final String[] info_robbery_counts = new String[]{"info_robbery_counts", "info_抢劫次数"};
  public static final String[] info_rob_amount = new String[]{"info_rob_amount", "info_抢劫数额"};
  public static final String[] info_damage_consequences = new String[]{"info_damage_consequences", "info_伤害后果"};
  public static final String[] info_drug_quantity = new String[]{"info_drug_quantity", "info_毒品数量"};
  public static final String[] info_counterfeit_number = new String[]{"info_counterfeit_number", "info_假币数量"};
  public static final String[] info_obscene_quantity = new String[]{"info_obscene_quantity", "info_淫秽物品数量"};

  //刑事案由-妨害公务罪
  public static final String[] info_agitate_masses = new String[]{"info_agitate_masses", "info_煽动群众"};
  public static final String[] info_violent_attacks = new String[]{"info_violent_attacks", "info_暴力袭击正在执行职务的人民警察"};
  public static final String[] info_armed = new String[]{"info_armed", "info_持械"};
  public static final String[] info_adverse_effects = new String[]{"info_adverse_effects", "info_造成恶劣社会影响"};
  public static final String[] info_affected_social_order = new String[]{"info_affected_social_order", "info_造成交通阻塞影响社会秩序"};
  public static final String[] info_official_duties_not_standardized = new String[]{"info_official_duties_not_standardized", "info_执行公务不规范"};
  public static final String[] info_consequence = new String[]{"info_consequence", "info_后果"};
  public static final String[] info_minor_injury = new String[]{"info_minor_injury", "info_轻微伤人数"};
  public static final String[] info_minor_number = new String[]{"info_minor_number", "info_轻伤人数"};
  public static final String[] info_damage_amount = new String[]{"info_damage_amount", "info_毁损财物数额"};

  //刑事案由-聚众斗殴
  public static final String[] info_civil_dispute = new String[]{"info_civil_dispute", "info_因民间纠纷"};
  public static final String[] info_organize_minors = new String[]{"info_organize_minors", "info_组织未成年人聚众斗殴"};
  public static final String[] info_property_damage = new String[]{"info_property_damage", "info_财产损失较大"};
  public static final String[] info_black_social = new String[]{"info_black_social", "info_带有黑社会性质"};
  public static final String[] info_fight_number = new String[]{"info_fight_number", "info_斗殴人数"};
  public static final String[] info_fights_number = new String[]{"info_fights_number", "info_聚众斗殴次数"};
  public static final String[] info_means_degree = new String[]{"info_means_degree", "info_手段严重程度"};

  //刑事案由-故意伤害罪
  public static final String[] info_hurt_pregnant = new String[]{"info_hurt_pregnant", "info_伤害孕妇"};
  public static final String[] info_defendant_wrongdoing = new String[]{"info_defendant_wrongdoing", "info_被害人有过错"};
  public static final String[] info_rescus = new String[]{"info_rescus", "info_积极施救"};
  public static final String[] info_hire_someone = new String[]{"info_hire_someone", "info_雇佣他人"};
  public static final String[] info_use_weapon = new String[]{"info_use_weapon", "info_使用凶器"};
  public static final String[] info_retaliation = new String[]{"info_retaliation", "info_报复伤害"};
  public static final String[] info_illegal_activities = new String[]{"info_illegal_activities", "info_因实施其他违法犯罪活动"};
  public static final String[] info_casualty = new String[]{"info_casualty", "info_伤亡人数"};
  public static final String[] info_minor_injury_level = new String[]{"info_minor_injury_level", "info_轻微伤伤残等级"};
  public static final String[] info_minor_level = new String[]{"info_minor_level", "info_轻伤伤残等级"};
  public static final String[] info_severity_disability_grade = new String[]{"info_severity_disability_grade", "info_重伤伤残等级"};
  public static final String[] info_disabled_level = new String[]{"info_disabled_level", "info_伤残等级"};
  public static final String[] info_cruelty_method = new String[]{"info_cruelty_method", "info_手段残忍程度"};

  //刑事案由-强奸罪
  public static final String[] info_adultery_times = new String[]{"info_adultery_times", "info_奸淫多次"};
  public static final String[] info_gang_rape_times = new String[]{"info_gang_rape_times", "info_轮奸多次"};
  public static final String[] info_arm_associatbd = new String[]{"info_arm_associatbd", "info_携带凶器"};
  public static final String[] info_enter_minor_residence = new String[]{"info_enter_minor_residence", "info_进入未成年人住所"};
  public static final String[] info_enter_school_dormitory = new String[]{"info_enter_school_dormitory", "info_进入学校集体宿舍"};
  public static final String[] info_illegal_detention = new String[]{"info_illegal_detention", "info_非法拘禁、捆绑、侮辱、虐待"};
  public static final String[] info_carnal_abuse = new String[]{"info_carnal_abuse", "info_强奸幼女"};
  public static final String[] info_rapist = new String[]{"info_rapist", "info_强奸残疾人"};
  public static final String[] info_seriously_people = new String[]{"info_seriously_people", "info_重伤人数"};
  public static final String[] info_rape_number = new String[]{"info_rape_number", "info_强奸人数"};
  public static final String[] info_plot_severity = new String[]{"info_plot_severity", "info_情节恶劣程度"};
  public static final String[] info_special_relationships = new String[]{"info_special_relationships", "info_利用特殊关系"};
  public static final String[] info_special_identity = new String[]{"info_special_identity", "info_利用特殊身份"};
  public static final String[] info_compulsory_means = new String[]{"info_compulsory_means", "info_强制手段"};
  public static final String[] info_vulnerable_group = new String[]{"info_vulnerable_group", "info_强奸弱势群体"};


  //审查逮捕意见书 补充信息点
  public static final String[] info_cultural_relic_level = new String[]{"info_cultural_relic_level", "info_文物级别"};
  public static final String[] info_serious_circumstances = new String[]{"info_serious_circumstances", "info_情节严重"};
  public static final String[] info_slight_circumstances = new String[]{"info_slight_circumstances", "info_情节轻微"};
  public static final String[] info_less_stolen_goods = new String[]{"info_less_stolen_goods", "info_没有参与分赃或者获赃较少"};
  public static final String[] info_advice_approve_arrest = new String[]{"info_advice_approve_arrest", "info_建议批准逮捕"};
  public static final String[] info_advice_reject_arrest = new String[]{"info_advice_reject_arrest", "info_建议不批准逮捕"};
  public static final String[] info_reason = new String[]{"info_reason", "info_理由"};

  //检察院其他文书分段
  public static final String[] section_ask_reauthen_reason = new String[]{"section_ask_reauthen_reason", "section_提请复核或复议的理由及根据"};
  public static final String[] section_review_fact_evidence = new String[]{"section_review_fact_evidence", "section_经复核或复议认定的的案件事实及证据"};
  public static final String[] section_investigate_fact_and_reason = new String[]{"section_investigate_fact_and_reason", "section_侦查机关认定的案件事实及理由"};
  public static final String[] section_reason_not_arrest = new String[]{"section_reason_not_arrest", "section_原不批准逮捕的理由"};

  //贪污贿赂罪
  public static final String[] info_umulative_punishmen = new String[]{"info_umulative_punishmen", "info_数罪并罚"};
  public static final String[] info_plus_punishmen = new String[]{"info_plus_punishmen", "info_合并刑罚"};
  public static final String[] info_joint_crimes = new String[]{"info_joint_crimes", "info_共同犯罪"};
  public static final String[] info_circumstances_serious = new String[]{"info_circumstances_serious", "info_贪污贿赂犯罪情节"};
  public static final String[] info_circumstances_leniently = new String[]{"info_circumstances_leniently", "info_从宽量刑情节"};
  public static final String[] info_circumstances_leniently_result = new String[]{"info_circumstances_leniently_result", "info_从宽量刑情节结果"};
  public static final String[] info_crime_amount = new String[]{"info_crime_amount", "info_犯罪金额"};
  public static final String[] info_advice_stand_arrest_decision = new String[]{"info_advice_stand_arrest_decision", "info_建议维持原逮捕决定"};
  public static final String[] info_advice_stand_unarrest_decision = new String[]{"info_advice_stand_unarrest_decision", "info_建议维持原不逮捕决定"};
  public static final String[] info_advice_reject_arrest_decision = new String[]{"info_advice_reject_arrest_decision", "info_建议撤销原逮捕决定"};
  public static final String[] info_advice_reject_unarrest_decision = new String[]{"info_advice_reject_unarrest_decision", "info_建议撤销原不逮捕决定"};

  //集资诈骗罪-专题？
  public static final String[] info_victim_amount = new String[]{"info_victim_amount", "info_受害人数"};
  public static final String[] info_fraud_method = new String[]{"info_fraud_method", "info_诈骗方法"};
  public static final String[] info_fraud_money_together = new String[]{"info_fraud_money_together", "info_共同诈骗金额"};
  public static final String[] info_fraud_money_single = new String[]{"info_fraud_money_single", "info_单独诈骗金额"};
  public static final String[] info_fraud_money_useage = new String[]{"info_fraud_money_useage", "info_诈骗资金用途"};
  public static final String[] info_fraud_duration = new String[]{"info_fraud_duration", "info_诈骗持续时间"};
  public static final String[] info_cross_regional_fraud = new String[]{"info_cross_regional_fraud", "info_跨区域诈骗"};
  public static final String[] info_money_payback = new String[]{"info_money_payback", "info_已返还被害人金额"};

  //盗窃罪
  public static final String[] info_number_theft = new String[]{"info_number_theft", "info_盗窃总次数"};
  public static final String[] info_amount_range = new String[]{"info_amount_range", "info_金额范围"};
  public static final String[] info_total_amount = new String[]{"info_total_amount", "info_总金额"};
  public static final String[] info_loss_bigger_than_amount = new String[]{"info_loss_bigger_than_amount", "info_损失大于盗窃数额"};
  public static final String[] info_arm_associated = new String[]{"info_arm_associated", "info_携带凶器"};
  public static final String[] info_multiple_theft = new String[]{"info_multiple_theft", "info_多次盗窃"};
  public static final String[] info_minor_control = new String[]{"info_minor_control", "info_组织控制未成年人盗窃"};
  public static final String[] info_indoor_theft = new String[]{"info_indoor_theft", "info_入户盗窃"};
  public static final String[] info_pocket_pick = new String[]{"info_pocket_pick", "info_扒窃"};
  public static final String[] info_replacement = new String[]{"info_replacement", "info_案发前放回原处"};
  public static final String[] info_steal_while_emergency = new String[]{"info_steal_while_emergency", "info_突发事件期间盗窃"};
  public static final String[] info_special_victims = new String[]{"info_special_victims", "info_盗窃特殊对象"};
  public static final String[] info_special_material = new String[]{"info_special_material", "info_盗窃特殊物资"};
  public static final String[] info_special_spot = new String[]{"info_special_spot", "info_特殊地点盗窃"};
  public static final String[] info_family_theft = new String[]{"info_family_theft", "info_盗窃家庭成员或近亲属"};
  public static final String[] info_means_destructive = new String[]{"info_means_destructive", "info_破坏性手段"};
  public static final String[] info_life_purpose = new String[]{"info_life_purpose", "info_因生活所迫学习治病急需"};
  public static final String[] info_abetment_unfulfilled = new String[]{"info_abetment_unfulfilled", "info_教唆他人犯罪但被教唆者未犯被教唆的罪"};
  public static final String[] info_illegal_purpose = new String[]{"info_illegal_purpose", "info_为违法活动盗窃"};
  public static final String[] info_penalty_admin_record = new String[]{"info_penalty_admin_record", "info_一年内因盗窃受过行政处罚"};
  public static final String[] info_penalty_for_theft_record = new String[]{"info_penalty_for_theft_record", "info_因盗窃受过刑事处罚"};
  public static final String[] info_theft_medical_fee = new String[]{"info_theft_medical_fee", "info_盗窃医疗费"};
  public static final String[] info_cultural_relic = new String[]{"info_cultural_relic", "info_盗窃文物"};
  public static final String[] info_state_cultural_relic_general = new String[]{"info_state_cultural_relic_general", "info_盗窃国有馆藏一般文物"};
  public static final String[] info_state_cultural_relic_third = new String[]{"info_state_cultural_relic_third", "info_盗窃国有馆藏三级文物"};
  public static final String[] info_state_cultural_relic_second = new String[]{"info_state_cultural_relic_second", "info_盗窃国有馆藏二级以上文物"};
  public static final String[] info_state_cultural_relic_general_num = new String[]{"info_state_cultural_relic_general_num", "info_盗窃国有馆藏一般文物数量"};
  public static final String[] info_state_cultural_relic_third_num = new String[]{"info_state_cultural_relic_third_num", "info_盗窃国有馆藏三级文物数量"};
  public static final String[] info_state_cultural_relic_second_num = new String[]{"info_state_cultural_relic_second_num", "info_盗窃国有馆藏二级文物数量"};
  public static final String[] info_state_cultural_relic_frist_num = new String[]{"info_state_cultural_relic_frist_num", "info_盗窃国有馆藏一级文物数量"};
  public static final String[] info_victim_pregnant = new String[]{"info_victim_pregnant", "info_被害人为孕妇"};

  //刑事案由 交通肇事罪
  public static final String[] info_accident_responsibility = new String[]{"info_accident_responsibility", "info_事故责任划分"};
  public static final String[] info_property_severe_loss = new String[]{"info_property_severe_loss", "info_重大财产损失"};
  public static final String[] info_property_loss_num = new String[]{"info_property_loss_num", "info_财产损失数额"};
  public static final String[] info_abscond_death = new String[]{"info_abscond_death", "info_因逃逸致人死亡"};
  public static final String[] info_abscond = new String[]{"info_abscond", "info_逃逸"};
  public static final String[] info_drunk_driving = new String[]{"info_drunk_driving", "info_酒驾"};
  public static final String[] info_drug_associated = new String[]{"info_drug_associated", "info_毒驾"};
  public static final String[] info_no_license = new String[]{"info_no_license", "info_无证驾驶"};
  public static final String[] info_no_plate = new String[]{"info_no_plate", "info_明知是无牌证或已报废"};
  public static final String[] info_unsafe_vehicle = new String[]{"info_unsafe_vehicle", "info_车辆不安全"};
  public static final String[] info_overload = new String[]{"info_overload", "info_严重超载"};
  public static final String[] info_active_rescue = new String[]{"info_active_rescue", "info_积极施救"};
  public static final String[] info_bad_impact = new String[]{"info_bad_impact", "info_造成恶劣影响"};
  public static final String[] info_compensate_unable = new String[]{"info_compensate_unable", "info_无能力赔偿"};
  public static final String[] info_victim_minor = new String[]{"info_victim_minor", "info_被害人为未成年人"};
  public static final String[] info_victim_old = new String[]{"info_victim_old", "info_被害人为老年人"};
  public static final String[] info_victim_disabled = new String[]{"info_victim_disabled", "info_被害人为残疾人"};

  //刑事案由 诈骗罪
  public static final String[] info_fraud_special_victims = new String[]{"info_fraud_special_victims", "info_诈骗特殊对象"};
  public static final String[] info_fraud_special_material = new String[]{"info_fraud_special_material", "info_诈骗特殊物资"};
  public static final String[] info_fraud_unspecific_victims = new String[]{"info_fraud_unspecific_victims", "info_对不特定多数人"};
  public static final String[] info_donation_cover = new String[]{"info_donation_cover", "info_赈灾募捐名义"};
  public static final String[] info_grave_consequence = new String[]{"info_grave_consequence", "info_造成严重后果"};
  public static final String[] info_fraud_other_serious_plots = new String[]{"info_fraud_other_serious_plots", "info_其他严重情节"};
  public static final String[] info_multiple_fraud = new String[]{"info_multiple_fraud", "info_多次诈骗"};
  public static final String[] info_family_forgiven_fraud = new String[]{"info_family_forgiven_fraud", "info_诈骗近亲属财物并获得谅解"};
  public static final String[] info_internet_fraud = new String[]{"info_internet_fraud", "info_电信网络诈骗"};
  public static final String[] info_fraud_times = new String[]{"info_fraud_times", "info_诈骗时间"};
  public static final String[] info_fraud_means = new String[]{"info_fraud_means", "info_诈骗手段"};
  public static final String[] info_fraud_mount = new String[]{"info_fraud_mount", "info_诈骗金额"};


  //危险驾驶 刑事案由
//  public static final String[] info_highway_dangerdriving = new String[]{"info_highway_dangerdriving", "info_高速路危险驾驶"};
//  public static final String[] info_incident_time_hour = new String[]{"info_incident_time_hour", "info_案发时间小时"};
//  public static final String[] info_incident_time = new String[]{"info_incident_time", "info_案发时间"};
//  public static final String[] info_danger_drunk_driving = new String[]{"info_danger_drunk_driving", "info_醉酒驾驶"};
//  public static final String[] info_overloadind = new String[]{"info_overloadind", "info_旅客运输超载"};
//  public static final String[] info_speedinf = new String[]{"info_speedinf", "info_旅客运输超速"};
  public static final String[] info_speed  = new String[]{"info_speed", "info_超速"};
  public static final String[] info_blood_alcohol_concentration = new String[]{"info_blood_alcohol_concentration", "info_血液酒精浓度"};
  public static final String[] info_cause_environmental_pollution  = new String[]{"info_cause_environmental_pollution", "info_造成环境污染"};
  public static final String[] info_motor_vehicle_carrying_passengers  = new String[]{"info_motor_vehicle_carrying_passengers", "info_驾驶有乘客的营运车辆"};
  public static final String[] info_chase_race = new String[]{"info_chase_race", "info_追逐竞驶"};
  public static final String[] info_transport_overspeed_loading = new String[]{"info_transport_overspeed_loading", "info_从事校车等客运超速超载"};
  public static final String[] info_hazardous_chemicals = new String[]{"info_hazardous_chemicals", "info_违规运输危险化学品"};
  public static final String[] info_travel_section = new String[]{"info_travel_section", "info_行驶路段"};
  public static final String[] info_overspeed_ratio = new String[]{"info_overspeed_ratio", "info_超速比例"};
  public static final String[] info_restructured_discarded = new String[]{"info_restructured_discarded", "info_非法改装或已报废"};
  public static final String[] info_license_plate_problem = new String[]{"info_license_plate_problem", "info_伪造、故意遮挡或无牌证"};
  public static final String[] info_violating_traffic_lights = new String[]{"info_violating_traffic_lights", "info_违反交通信号灯"};
  public static final String[] info_passenger_vehicle_type = new String[]{"info_passenger_vehicle_type", "info_驾驶客车类型"};
  public static final String[] info_overloaded_people = new String[]{"info_overloaded_people", "info_超载人数"};
  public static final String[] info_drugs_hazardous_chemicals = new String[]{"info_drugs_hazardous_chemicals", "info_吸毒后运输危险化学品"};
  public static final String[] info_hazardous_chemicals_overloading_ratio = new String[]{"info_hazardous_chemicals_overloading_ratio", "info_危险化学品超载比例"};
  public static final String[] info_hazardous_chemicals_recursor = new String[]{"info_hazardous_chemicals_recursor", "info_二年内曾因违反规定运输危险化学品受过二次以上行政处罚"};
  public static final String[] info_hazardous_chemicals_recidivism = new String[]{"info_hazardous_chemicals_recidivism", "info_曾因违反规定运输危险化学品受过刑事处罚"};


  //抢夺罪 刑事案由
  public static final String[] info_suicide_number = new String[]{"info_suicide_number", "info_自杀人数"};
  public static final String[] info_gross_amount = new String[]{"info_gross_amount", "info_抢夺数额"};
  public static final String[] info_rob_many_times = new String[]{"info_rob_many_times", "info_多次抢夺"};
  public static final String[] info_rob_property_group = new String[]{"info_rob_property_group", "info_抢夺弱势群体财物"};
  public static final String[] info_spoil = new String[]{"info_spoil", "info_抢夺亲友财物"};
  public static final String[] info_grab_special_purpose_items = new String[]{"info_grab_special_purpose_items", "info_抢夺特殊用途款物"};
  public static final String[] info_robbery_at_incident_scene = new String[]{"info_robbery_at_incident_scene", "info_突发事件期间在事件发生地抢夺"};
  public static final String[] info_drive_motor_vehicle = new String[]{"info_drive_motor_vehicle", "info_驾驶机动车"};
  public static final String[] info_drive_non_motor_vehicle = new String[]{"info_drive_non_motor_vehicle", "info_驾驶非机动车"};
  public static final String[] info_underage = new String[]{"info_underage", "info_组织未成年人"};
  public static final String[] info_control_minors = new String[]{"info_control_minors", "info_控制未成年人"};

  //敲诈勒索罪
  public static final String[] info_blackmail_number = new String[]{"info_blackmail_number", "info_敲诈次数"};
  public static final String[] info_blackmail_amount = new String[]{"info_blackmail_amount", "info_敲诈数额"};
  public static final String[] info_blackmail_many_times = new String[]{"info_blackmail_many_times", "info_多次敲诈"};
  public static final String[] info_administrative_penalty_racketeering = new String[]{"info_administrative_penalty_racketeering", "info_一年内因敲诈勒索受过行政处罚"};
  public static final String[] info_criminal_punishment_extortion= new String[]{"info_criminal_punishment_extortion", "info_因敲诈勒索受过刑事处罚"};
  public static final String[] info_illegal_means_obtaining_privacy = new String[]{"info_illegal_means_obtaining_privacy", "info_非法手段获取他人隐私"};
  public static final String[] info_victim_mentally_disturbed = new String[]{"info_victim_mentally_disturbed", "info_造成被害人精神失常"};
  public static final String[] info_extortion_close_relatives_property = new String[]{"info_extortion_close_relatives_property", "info_敲诈近亲属财物"};

  //民间借贷纠纷
  public static final String[] info_loan_mortgage = new String[]{"info_loan_mortgage", "info_抵押"};
  public static final String[] info_loan_guarantor = new String[]{"info_loan_guarantor", "info_担保人"};
  public static final String[] info_guaranty_style = new String[]{"info_guaranty_style", "info_担保方式"};
  public static final String[] info_payment_evidence = new String[]{"info_payment_evidence", "info_支付凭证"};
  public static final String[] info_payment_request = new String[]{"info_payment_request", "info_催告"};
  public static final String[] info_payment_pepayment = new String[]{"info_payment_pepayment", "info_归还部分"};
  public static final String[] info_borrower_num_bigger_than_one = new String[]{"info_borrower_num_bigger_than_one", "info_借款人数大于1"};
  public static final String[] info_nominal_borrower = new String[]{"info_nominal_borrower", "info_名义借款人"};
  public static final String[] info_credit_card_borrower = new String[]{"info_credit_card_borrower", "info_信用卡借用人"};
  public static final String[] info_sole_proprietorship_investor = new String[]{"info_sole_proprietorship_investor", "info_个人独资企业投资人"};
  public static final String[] info_partnership_organization = new String[]{"info_partnership_organization", "info_合伙组织"};
  public static final String[] info_blank_receipt = new String[]{"info_blank_receipt", "info_空白收据"};
  public static final String[] info_out_of_business_no_liquidation_tbam = new String[]{"info_out_of_business_no_liquidation_tbam", "info_企业歇业未成立清算组"};
  public static final String[] info_couple_common_property = new String[]{"info_couple_common_property", "info_夫妻共同财产"};
  public static final String[] info_false_lawsuit = new String[]{"info_false_lawsuit", "info_虚假诉讼"};
  public static final String[] info_wee_loan_platform_guarantee = new String[]{"info_wee_loan_platform_guarantee", "info_网络贷款平台的担保责任"};
  public static final String[] info_compound_interest = new String[]{"info_compound_interest", "info_约定复利"};
  public static final String[] info_repayment_method = new String[]{"info_repayment_method", "info_还款方式"};
  public static final String[] info_ir_paper = new String[]{"info_ir_paper", "info_借条利率"};
  public static final String[] info_prcp_paper = new String[]{"info_prcp_paper", "info_借条本金"};
  public static final String[] info_loan_start_time = new String[]{"info_loan_start_time", "info_借款时间"};
  public static final String[] info_loan_duration = new String[]{"info_loan_duration", "info_借款时长"};
  public static final String[] info_loan_deadline = new String[]{"info_loan_deadline", "info_借款期限"};
  public static final String[] info_loan_carrier = new String[]{"info_loan_carrier", "info_借款载体"};
  public static final String[] info_ir_pequest = new String[]{"info_ir_pequest", "info_诉求利率"};
  public static final String[] info_prcp_pequest = new String[]{"info_prcp_pequest", "info_诉求本金"};
  public static final String[] info_ir_judgement = new String[]{"info_ir_judgement", "info_支持利率"};
  public static final String[] info_prcp_judgement = new String[]{"info_prcp_judgement", "info_支持本金"};
  public static final String[] info_total_judement = new String[]{"info_total_judement", "info_支持本金和利息"};
  public static final String[] info_collection_for_others = new String[]{"info_collection_for_others", "info_代替收款"};
  public static final String[] info_repayment_for_others = new String[]{"info_repayment_for_others", "info_代替还款"};
  public static final String[] info_borrowing_for_others = new String[]{"info_borrowing_for_others", "info_代替借款"};
  public static final String[] info_deet_accession = new String[]{"info_deet_accession", "info_债务加入"};
  public static final String[] info_deet_succession = new String[]{"info_deet_succession", "info_债务继承"};
  public static final String[] info_creditor_right_transfer = new String[]{"info_creditor_right_transfer", "info_债权转让"};
  public static final String[] info_deet_transfer = new String[]{"info_deet_transfer", "info_债务转移"};
  public static final String[] info_cohabitation_common_deet = new String[]{"info_cohabitation_common_deet", "info_同居共同债务"};
  public static final String[] info_admit_loan_fact = new String[]{"info_admit_loan_fact", "info_承认借款"};
  public static final String[] info_ir_request_paper = new String[]{"info_ir_request_paper", "info_支付方式"};
  public static final String[] info_couple_common_liability = new String[]{"info_couple_common_liability", "info_夫妻共同债务"};

  //机动车交通事故责任纠纷
  public static final String[] info_hospital_duration = new String[]{"info_hospital_duration", "info_住院时长"};
  public static final String[] info_care_duration = new String[]{"info_care_duration", "info_护理期"};
  public static final String[] info_job_delay_fee = new String[]{"info_job_delay_fee", "info_误工费用"};
  public static final String[] info_job_delay_duration = new String[]{"info_job_delay_duration", "info_误工时长"};
  public static final String[] info_nutrition_period = new String[]{"info_nutrition_period", "info_营养期"};
  public static final String[] info_number_victiom = new String[]{"info_number_victiom", "info_受害总人数"};
  public static final String[] info_rest_duration = new String[]{"info_rest_duration", "info_医嘱休息时长"};
  public static final String[] info_responsibility = new String[]{"info_responsibility", "info_事故责任划分"};
  public static final String[] info_responsibility_ratio = new String[]{"info_responsibility_ratio", "info_承担责任比例"};
  public static final String[] info_undertake_duty_way = new String[]{"info_undertake_duty_way", "info_承担责任方式"};
  public static final String[] info_number_insurance_company = new String[]{"info_number_insurance_company", "info_被告包括保险公司个数"};
  public static final String[] info_coutract_anchor = new String[]{"info_coutract_anchor", "info_挂靠协议"};
  public static final String[] info_defendant_rerpetrator = new String[]{"info_defendant_rerpetrator", "info_肇事者列为被告"};
  public static final String[] info_insurance_compensation_pctg = new String[]{"info_insurance_compensation_pctg", "info_保险赔付比例"};
  public static final String[] info_individual_compensation_pctg = new String[]{"info_individual_compensation_pctg", "info_个人赔付比例"};
  public static final String[] info_traffic_compensation_amout_info = new String[]{"info_traffic_compensation_amout_info","info_被告赔付数额"};
  public static final String[] info_traffic_compensation_amout_total = new String[]{"info_traffic_compensation_amout_total","info_被告赔付总额"};
  public static final String[] info_traffic_request_amount = new String[]{"info_traffic_request_amount", "info_索要赔偿金额"};
  public static final String[] info_both_sides = new String[]{"info_both_sides", "info_案件发生双方"};
  public static final String[] info_insurance_type = new String[]{"info_insurance_type", "info_投保类型"};
  public static final String[] info_is_man_dead = new String[]{"info_is_man_dead", "info_造成人员死亡"};
  public static final String[] info_perpetrators_relation = new String[]{"info_perpetrators_relation", "info_肇事者与车主关系"};
  public static final String[] info_compensation_standard = new String[]{"info_compensation_standard", "info_赔偿标准"};
  public static final String[] info_hospital_location = new String[]{"info_hospital_location", "info_就医范围"};
  public static final String[] info_injury_part = new String[]{"info_injury_part", "info_伤情选择"};
  public static final String[] info_rule_violation = new String[]{"info_rule_violation", "info_违规情况"};
  public static final String[] info_rent_scenartio = new String[]{"info_rent_scenartio", "info_租赁借用"};
  public static final String[] info_drive_without_permission = new String[]{"info_drive_without_permission", "info_擅自驾驶他人车辆"};
  public static final String[] info_no_register = new String[]{"info_no_register", "info_转让交付未办理登记"};
  public static final String[] info_drive_training = new String[]{"info_drive_training", "info_驾驶培训活动"};
  public static final String[] info_test_ride = new String[]{"info_test_ride", "info_试乘"};
  public static final String[] info_good_intention = new String[]{"info_good_intention", "info_好意同乘"};
  public static final String[] info_partner_training = new String[]{"info_partner_training", "info_陪练"};
  public static final String[] info_drive_service = new String[]{"info_drive_service", "info_服务场所提供泊车代驾"};
  public static final String[] info_vehicle_assemeled = new String[]{"info_vehicle_assemeled", "info_转让拼装报废"};
  public static final String[] info_vehicle_rob = new String[]{"info_vehicle_rob", "info_盗抢"};
  public static final String[] info_plate_fake = new String[]{"info_plate_fake", "info_套牌车"};
  public static final String[] info_vehicle_affiliated = new String[]{"info_vehicle_affiliated", "info_机动车挂靠"};
  public static final String[] info_driver_abscond = new String[]{"info_driver_abscond", "info_驾驶人逃逸"};
  //数模方
  public static final String[] info_responsible_subject_type= new String[]{"info_responsible_subject_type", "info_责任主体类别"};
  public static final String[] info_responsibility_multiple_vehicle= new String[]{"info_responsibility_multiple_vehicle", "info_多辆机动车主体责任"};
  public static final String[] info_materiel_loss_in_vehiclle= new String[]{"info_materiel_loss_in_vehiclle", "info_车载物品损失"};
  public static final String[] info_compulsory_insurance_allocation= new String[]{"info_compulsory_insurance_allocation", "info_多个被侵权人交强险分配"};
  public static final String[] info_compulsory_insurance_allocation_multiple_vehicle= new String[]{"info_compulsory_insurance_allocation_multiple_vehicle", "info_多辆机动车分配交强险"};

  //离婚纠纷
  public static final String[] info_affair_marriage = new String[]{"info_affair_marriage", "info_婚外情"};
  public static final String[] info_marriage_type = new String[]{"info_marriage_type", "info_结婚原因"};
  public static final String[] info_respondent_wrongdoing = new String[]{"info_respondent_wrongdoing", "info_被告有过错"};
  public static final String[] info_violence = new String[]{"info_violence", "info_实施家庭暴力"};
  public static final String[] info_drug_gambling = new String[]{"info_drug_gambling", "info_有赌博毒品等恶习"};
  public static final String[] info_kid_info = new String[]{"info_kid_info", "info_子女信息"};
  public static final String[] info_eco_compensation = new String[]{"info_eco_compensation", "info_经济补偿"};
  public static final String[] info_allowdivorce = new String[]{"info_allowdivorce", "info_判决离婚"};
  public static final String[] info_suiteefore = new String[]{"info_suiteefore", "info_曾经起诉离婚"};
  public static final String[] info_marriage_date = new String[]{"info_marriage_date", "info_结婚时间"};
  public static final String[] info_marriage_years = new String[]{"info_marriage_years", "info_婚龄"};
  public static final String[] info_plaintiff_gender = new String[]{"info_plaintiff_gender", "info_原告性别"};
  public static final String[] info_housedes= new String[]{"info_housedes", "info_涉及房产分配"};
  public static final String[] info_childraise= new String[]{"info_childraise", "info_涉及小孩抚养权"};
  public static final String[] info_number_suit_record= new String[]{"info_number_suit_record", "info_曾经起诉离婚次数"};
  public static final String[] info_cause_divorce= new String[]{"info_cause_divorce", "info_离婚原因"};
  public static final String[] info_child_raise_info= new String[]{"info_child_raise_info", "info_小孩抚养权信息"};
  public static final String[] info_house_allocation= new String[]{"info_house_allocation", "info_房屋分配"};
  public static final String[] info_divorce_number_kids= new String[]{"info_divorce_number_kids", "info_子女个数"};
  public static final String[] info_divorce_number_girl= new String[]{"info_divorce_number_girl", "info_女孩个数"};
  public static final String[] info_divorce_number_boy= new String[]{"info_divorce_number_boy", "info_男孩个数"};
  public static final String[] info_divorce_raiser_girl= new String[]{"info_divorce_raiser_girl", "info_女孩抚养人"};
  public static final String[] info_divorce_raiser_boy= new String[]{"info_divorce_raiser_boy", "info_男孩抚养人"};
  public static final String[] info_divorce_all_kid_raiser= new String[]{"info_divorce_all_kid_raiser", "info_所有小孩抚养人"};
  public static final String[] info_divorce_alimony_payment_freq= new String[]{"info_divorce_alimony_payment_freq", "info_抚养费支付方式"};
  public static final String[] info_divorce_agree_divorce= new String[]{"info_divorce_agree_divorce", "info_同意离婚"};
  public static final String[] info_divorce_alimony_mother= new String[]{"info_divorce_alimony_mother", "info_母亲所付抚养费"};
  public static final String[] info_divorce_alimony_father= new String[]{"info_divorce_alimony_father", "info_父亲所付抚养费"};
  public static final String[] info_divorce_gift_money= new String[]{"info_divorce_gift_money", "info_礼金"};
  public static final String[] info_divorce_bigamy= new String[]{"info_divorce_bigamy", "info_重婚"};
  public static final String[] info_divorce_common_property= new String[]{"info_divorce_common_property", "info_共同财产"};
  public static final String[] info_divorce_seperation_time= new String[]{"info_divorce_seperation_time", "info_分居时间"};
  public static final String[] info_plt_remarriage= new String[]{"info_plt_remarriage", "info_原告再婚"};
  public static final String[] info_def_remarriage= new String[]{"info_def_remarriage", "info_被告再婚"};
  public static final String[] info_divorce_kids_age_range= new String[]{"info_divorce_kids_age_range", "info_子女年龄段"};

  //贪污受贿类犯罪
  public static final String[] info_corruption_means= new String[]{"info_corruption_means", "info_贪污受贿手段"};

  //拐卖妇女儿童罪
  public static final String[] info_abroad_traffick= new String[]{"info_abroad_traffick", "info_拐卖到境外"};
  public static final String[] info_traffick_means= new String[]{"info_traffick_means", "info_拐卖手段"};
  public static final String[] info_traffick_crime_type= new String[]{"info_traffick_crime_type", "info_拐卖罪名"};
  public static final String[] info_rape_related= new String[]{"info_rape_related", "info_涉及奸淫"};
  public static final String[] info_forced_prostitution= new String[]{"info_forced_prostitution", "info_强迫卖淫"};
  public static final String[] info_impairment= new String[]{"info_impairment", "info_造成死亡或伤残"};

  //起诉意见书
  public static final String[] info_usedname= new String[]{"info_usedname", "info_曾用名"};
  public static final String[] info_alias= new String[]{"info_alias", "info_别名"};
  public static final String[] info_pseudonym= new String[]{"info_pseudonym", "info_化名"};
  public static final String[] info_nickname= new String[]{"info_nickname", "info_绰号"};
  public static final String[] info_certificates_type= new String[]{"info_certificates_type", "info_证件类型"};
  public static final String[] info_certificates_num= new String[]{"info_certificates_num", "info_证件号码"};
  public static final String[] info_duties= new String[]{"info_duties", "info_职务"};
  public static final String[] info_record_sent= new String[]{"info_record_sent", "info_前科信息"};
  public static final String[] info_decide_dept= new String[]{"info_decide_dept", "info_批准或决定机关"};
  public static final String[] info_execute_date= new String[]{"info_execute_date", "info_执行日期"};
  public static final String[] info_coercive_measures= new String[]{"info_coercive_measures", "info_强制措施"};
  public static final String[] info_custody_place= new String[]{"info_custody_place", "info_羁押场所"};
  public static final String[] meta_reconnaissance_name= new String[]{"meta_reconnaissance_name", "meta_侦察机关名称"};
  public static final String[] meta_transfer_case= new String[]{"meta_transfer_case", "meta_移送案由"};
  public static final String[] meta_transfer_dept= new String[]{"meta_transfer_dept", "meta_移送单位"};
  public static final String[] meta_complaint_opinion_num = new String[]{"meta_complaint_opinion_num", "meta_起诉意见书文号"};

  public static final String[] info_crime_while_emergency = new String[]{"info_crime_while_emergency", "info_突发事件期间犯罪"};
  //集资诈骗罪
  public static final String[] info_crime_main_subject = new String[]{"info_crime_main_subject", "info_犯罪主体"};
  public static final String[] info_fraud_victim_amount = new String[]{"info_fraud_victim_amount", "info_受害人数"};
  public static final String[] info_disadvantaged_victim = new String[]{"info_disadvantaged_victim", "info_被害人为弱势群体"};
  public static final String[] info_pretending_dept_welfare = new String[]{"info_pretending_dept_welfare", "info_假冒国家机关或公益组织"};
  public static final String[] info_lost_retrieve = new String[]{"info_lost_retrieve", "info_损失被挽回"};

  //信用卡诈骗罪
  public static final String[] info_falsify_credit = new String[]{"info_falsify_credit", "info_伪造信用卡"};
  public static final String[] info_falsify_identity_4credit = new String[]{"info_falsify_identity_4credit", "info_虚假身份骗领信用卡"};
  public static final String[] info_invalidated_credit = new String[]{"info_invalidated_credit", "info_作废的信用卡"};
  public static final String[] info_uttering_other_credit = new String[]{"info_uttering_other_credit", "info_冒用他人信用卡"};
  public static final String[] info_malicious_overdraft = new String[]{"info_malicious_overdraft", "info_恶意透支"};
  public static final String[] info_more_credit = new String[]{"info_more_credit", "info_不满十张空白或五张他人信用卡"};
  public static final String[] info_abled_refused_return = new String[]{"info_abled_refused_return", "info_有能力但拒绝退赃退赔"};
  public static final String[] info_reimburse_overdraft = new String[]{"info_reimburse_overdraft", "info_已偿还全部透支款息"};

  //非法持有毒品罪
  public static final String[] info_drug_variety  = new String[]{"info_drug_variety", "info_毒品种类"};
  public static final String[] info_drug_place  = new String[]{"info_drug_place ", "info_在戒毒场所、监管场所非法持有毒品"};
  public static final String[] info_drug_abetting_teenager  = new String[]{"info_drug_abetting_teenager", "info_利用、教唆未成年人非法持有毒品"};
  public static final String[] info_drug_national_staff  = new String[]{"info_drug_national_staff", "info_国家工作人员非法持有毒品"};
  public static final String[] info_drug_again  = new String[]{"info_drug_again", "info_毒品再犯"};
  public static final String[] info_drug_low_content  = new String[]{"info_drug_low_content", "info_毒品含量明显偏低"};
  public static final String[] info_drug_decoy  = new String[]{"info_drug_decoy", "info_被利用或被诱骗非法持有毒品"};
  public static final String[] info_heroin_number = new String[]{"info_heroin_number", "info_海洛因数量"};
  public static final String[] info_methamphetamine_number = new String[]{"info_methamphetamine_number", "info_甲基苯丙胺数量"};
  public static final String[] info_cocaine_quantity = new String[]{"info_cocaine_quantity", "info_可卡因数量"};
  public static final String[] info_opium_number = new String[]{"info_opium_number", "info_鸦片数量"};
  public static final String[] info_methadone_number = new String[]{"info_methadone_number", "info_美沙酮数量"};
  public static final String[] info_MDMA_number = new String[]{"info_MDMA_number", "info_MDMA数量"};
  public static final String[] info_morphine_number = new String[]{"info_morphine_number", "info_吗啡数量"};
  public static final String[] info_ketamine_number = new String[]{"info_ketamine_number", "info_氯胺酮数量"};
  public static final String[] info_fentanyl_number = new String[]{"info_fentanyl_number", "info_芬太尼数量"};
  public static final String[] info_number_of_methadone = new String[]{"info_number_of_methadone", "info_甲卡西酮数量"};
  public static final String[] info_dihydroetorphine_number = new String[]{"info_dihydroetorphine_number", "info_二氢埃托啡数量"};
  public static final String[] info_degrees_number = new String[]{"info_degrees_number", "info_哌替啶数量"};
  public static final String[] info_tramadol_number = new String[]{"info_tramadol_number", "info_曲马多数量"};
  public static final String[] info_hydroxybutyric_number = new String[]{"info_hydroxybutyric_number", "info_γ-羟丁酸数量"};
  public static final String[] info_oil_quantity = new String[]{"info_oil_quantity", "info_大麻油数量"};
  public static final String[] info_cannabinoid_number = new String[]{"info_cannabinoid_number", "info_大麻脂数量"};
  public static final String[] info_cannabis_leaf_number = new String[]{"info_cannabis_leaf_number", "info_大麻叶大麻烟数量"};
  public static final String[] info_codeine_number = new String[]{"info_codeine_number", "info_可待因数量"};
  public static final String[] info_buprenorphine_number = new String[]{"info_buprenorphine_number", "info_丁丙诺啡数量"};
  public static final String[] info_quaalude_number = new String[]{"info_quaalude_number", "info_安眠酮数量"};
  public static final String[] info_alprazolam_number = new String[]{"info_alprazolam_number", "info_阿普唑仑数量"};
  public static final String[] info_chartgrass_number = new String[]{"info_chartgrass_number", "info_恰特草数量"};
  public static final String[] info_triazolam_quantity = new String[]{"info_triazolam_quantity", "info_三唑仑数量"};
  public static final String[] info_caffeine_quantity = new String[]{"info_caffeine_quantity", "info_咖啡因数量"};
  public static final String[] info_poppy_shell_quantity = new String[]{"info_poppy_shell_quantity", "info_罂粟壳数量"};
  public static final String[] info_barbitone_number = new String[]{"info_barbitone_number", "info_巴比妥数量"};
  public static final String[] info_phenobarbital_number  = new String[]{"info_phenobarbital_number", "info_苯巴比妥数量"};
  public static final String[] info_nata_quantity = new String[]{"info_nata_quantity", "info_安钠咖数量"};
  public static final String[] info_nimetazepam_number  = new String[]{"info_nimetazepam_number", "info_尼美西泮数量"};
  public static final String[] info_chlorazonium_number= new String[]{"info_chlorazonium_number", "info_氯氮卓数量"};
  public static final String[] info_estazolam_number= new String[]{"info_estazolam_number", "info_艾司唑仑数量"};
  public static final String[] info_diazepam_quantity = new String[]{"info_diazepam_quantity", "info_地西泮数量"};
  public static final String[] info_bromazepam_number = new String[]{"info_bromazepam_number", "info_溴西泮数量"};

  //非法吸收公众存款罪
  public static final String[] info_help_for_charge = new String[]{"info_help_for_charge", "info_提供帮助收取费用"};
  public static final String[] info_usual_can_repaying = new String[]{"info_usual_can_repaying", "info_用于正常经营能够清退资金"};

  //容留他人吸毒罪
  public static final String[] info_once_shelter_num  = new String[]{"info_once_shelter_num", "info_一次容留人数"};
  public static final String[] info_2year_shelter_times  = new String[]{"info_2year_shelter_times", "info_二年内容留次数"};
  public static final String[] info_2year_drug_punish  = new String[]{"info_2year_drug_punish", "info_二年内曾因容留他人吸食、注射毒品受过行政处罚"};
  public static final String[] info_drug_shelter_teenager  = new String[]{"info_drug_shelter_teenager", "info_容留未成年人吸食、注射毒品"};
  public static final String[] info_drug_shelter_profit  = new String[]{"info_drug_shelter_profit", "info_以牟利为目的容留他人吸食、注射毒品"};
  public static final String[] info_shelter_national_staff  = new String[]{"info_shelter_national_staff", "info_国家工作人员容留他人吸食、注射毒品"};
  public static final String[] info_drug_shelter_relatives  = new String[]{"info_drug_shelter_relatives", "info_容留近亲属吸食、注射毒品"};

  //引诱、容留、介绍卖淫罪
  public static final String[] info_seduce_shelter_times  = new String[]{"info_seduce_shelter_times", "info_引诱、容留、介绍次数"};
  public static final String[] info_seduce_teen_num  = new String[]{"info_seduce_teen_num", "info_已满十四周岁不满十八周岁人数"};
  public static final String[] info_seduce_young_num  = new String[]{"info_seduce_young_num", "info_不满十四周岁人数"};
  public static final String[] info_seduce_adult_num  = new String[]{"info_seduce_adult_num", "info_已满十八周岁人数"};
  public static final String[] info_prostitute_std  = new String[]{"info_prostitute_std", "info_卖淫者患有严重性病"};
  public static final String[] info_prostitute_main_responsible  = new String[]{"info_prostitute_main_responsible", "info_单位的主要负责人"};
  public static final String[] info_prostitute_punish  = new String[]{"info_prostitute_punish", "info_曾因介绍他人卖淫受过处罚"};
  public static final String[] info_prostitute_internet  = new String[]{"info_prostitute_internet", "info_利用互联网小广告等介绍卖淫"};
  public static final String[] info_prostitute_public  = new String[]{"info_prostitute_public", "info_公共场所介绍卖淫"};
  public static final String[] info_prostitute_abroad  = new String[]{"info_prostitute_abroad", "info_引诱、介绍他人到境外卖淫或者引诱、容留、介绍境外人员到境内卖淫"};


  public static final String[] info_defend_appeal_reason  = new String[]{"info_defend_appeal_reason", "info_被告或上诉人上诉抗诉理由"};
  public static final String[] info_pleader_appeal_reason  = new String[]{"info_pleader_appeal_reason", "info_辩护人辩护理由"};
  public static final String[] info_procur_appeal_reason  = new String[]{"info_procur_appeal_reason", "info_检察机关意见"};
}
