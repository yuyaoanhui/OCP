var NAMESPACE_COURT_VERDICT_容留他人吸毒罪 = {
	/**
	 * point-1:二年内容留次数
	 */
	info_2year_shelter_times : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			dependentPoints : "meta_doc_date",
			regex : "(共|先后|涉嫌)[^,，；;:：\\.。]*?次"
		} ],
		method : "rule_func_extract_times_info"
	},
	/**
	 * point-2:一次容留人数
	 */
	info_once_shelter_num : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "容留[^,，；;:：\\.。]*?吸食",
			reverseRegex : "如下[:：]#(共|先后|涉嫌)[^,，；;:：\\.。]*?次"
		} ],
		method : "rule_func_extract_people_num_info"
	},
	/**
	 * point-3:以牟利为目的
	 */
	info_drug_shelter_profit : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "以牟利为目的|有偿(提供场所|容留)|从中牟利",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "以牟利为目的|有偿(提供场所|容留)|从中牟利",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:造成恶劣社会影响
	 */
	info_adverse_effects : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "影响(恶劣|极坏)|恶劣(社会)?影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:突发事件期间犯罪
	 */
	info_crime_while_emergency : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:容留未成年人
	 */
	info_drug_shelter_teenager : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(容留|包[括含]|提供)[^,，;；：:\\.。]*?未成年人",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:造成严重后果
	 */
	info_grave_consequence : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "死亡|精神疾病|高额债务|造成严重后果|受伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:国家工作人员容留他人吸食、注射毒品
	 */
	info_shelter_national_staff : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "[为属系]国家(机关)?工作人员",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:造成恶劣社会影响
	 */
	info_adverse_effects : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "影响(恶劣|极坏)|恶劣(社会)?影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:毒品再犯
	 */
	info_drug_again : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "毒品再犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:容留近亲属
	 */
	info_drug_shelter_relatives : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "容留其?近亲属|(属|有|系其?|是)近亲属",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-12:二年内曾因容留他人吸食、注射毒品受过行政处罚
	 */
	info_2year_drug_punish : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "二年内曾?因容留他人(吸毒|吸食毒品)受过行政处罚",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info",
		adjust : [ {
			params : [ {
				crimeName : "容留他人吸毒罪",
				dependentPoints : "meta_doc_date",
				yearsNum : "2"
			} ],
			method : "rule_func_adjust_administrative_penalty"
		} ]
	}
}