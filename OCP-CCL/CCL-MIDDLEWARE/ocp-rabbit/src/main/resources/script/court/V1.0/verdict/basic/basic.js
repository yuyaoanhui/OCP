var NAMESPACE_COURT_VERDICT_basic = {
	basicPrePoints : {// 要提前抽取的信息点,程序会根据文书案由自动选择部分执行
		base : [ "courtMetas", "caseTypes", "caseIds",
				"meta_procuratorate_name", "info_absence",
				"meta_case_procedure_type", "meta_case_session",
				"meta_procuratorator", "meta_case_prosecute_date",
				"meta_case_register_date", "meta_case_start_date",
				"caseAbouts", "caseFroms", "caseResults", "meta_doc_date",
				"section_title2", "section_doc_first_part2",
				"section_fact_const2", "section_reason2",
				"section_judge_base2", "section_judge_main2",
				"section_last_part2", "section_signature2",
				"section_relate_law2", "section_other2", "peopleInfos",
				"meta_case_remand" ],
		civil : [ "meta_fee_case" ],
		criminal : [ "info_crime_leader", "info_principle_criminal",
				"info_accessory", "info_not_main_crimer", "info_offender",
				"info_jailbird", "info_gang_crime", "info_compensation",
				"info_active_compensation", "info_understanding",
				"info_confession", "info_confession_incourt", "info_surrender",
				"info_merits_gain", "info_contrition",
				"info_criminal_reconciliation", "info_crime_multiple_spot",
				"info_abetment", "info_coerced_offender",
				"info_prepared_offender", "info_aborted_offender",
				"info_incomplete_offender", "info_duress_offender",
				"info_minor_abetment", "info_abetment_unfulfilled",
				"info_degree_heavier", "info_degree_lower",
				"info_excessive_defence", "info_excessive_danger_avoid",
				"info_mental_disorder", "info_deaf_mute",
				"info_defendant_wrongdoing", "info_innocent_reason",
				"info_victim_pregnant", "info_victim_minor", "info_victim_old",
				"info_victim_disabled", "penalties", "info_deportation" ]
	},
	controversy_focus : {
		// 哪些案由包含争议焦点的抽取
		ay : [ "房屋买卖合同纠纷", "民间借贷纠纷", "离婚纠纷" ],
		method : "rule_func_extract_controversy_focus"
	}
};
