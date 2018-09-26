var NAMESPACE_COURT_VERDICT_职务侵占罪 = {
	/**
	 * point-1:严重影响生产经营
	 */
	info_impact_production : {
		params : [ {
			tagList : "court_opinion",
			regex : "严重影响[^,，\\.。;；:：]*(生产|经营)|(生产|经营)[^,，\\.。;；:：]*严重影响"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-2:严重损失
	 */
	info_severe_loss : {
		params : [ {
			tagList : "court_opinion",
			regex : "(大|重)[^,，\\.。;；:：]*损失|损失[^,，\\.。;；:：]*(重|大)#数额巨大"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-3:恶劣影响
	 */
	info_bad_influence : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "影响(恶劣|重大)|(恶劣|重大)[^,，\\.。;；:：]*影响",
			reverseRegex : "(没|无|未|不)[^,，\\.。;；:：]*?影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:侵占专项、灾害款物
	 */
	info_special_relief_property : {
		params : [ {
			tagList : "court_opinion#facts_found#plaintiff_args#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(专项|救灾|灾害|援助|补偿|帮扶工程项目)[^,，\\.。;；:：]*(款|资|金|物)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-5:在企业改制、破产、重组过程中
	 */
	info_enterprise_in_restructuring : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(改制|破产|重组|清算)[^,，\\.。;；:：]*?(中|时|期间|之际)",
			reverseRegex : "清算[^,，\\.。;；:：]*?(帐|款|中心)|[帐款][^,，\\.。;；:：]*?清算"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-6:多次侵占
	 */
	info_multiple_encroach : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([几数多])次[^,，\\.。;；:：]*(侵占|侵吞)|(侵占|侵吞)[^,，\\.。;；:：]*(几|数|多)次",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:为犯罪活动
	 */
	info_for_illegal_purpose : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(为|企图|目的)[^,，\\.。;；:：]*违法(活动|行为)#(用于|进行)[^,，;；\\.。]*?(赌博|吸毒)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:侵占数额
	 */
	info_encroach_amount : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(价值|共|得|分|侵占|侵吞)[^,，\\。;；:：]*?元#[^,，\\。;；:：]*?元[^,，\\。;；:：]*?占为己有",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	}
}