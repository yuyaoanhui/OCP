var NAMESPACE_COURT_JUDGEMENT_合同诈骗罪 = {
	/**
	 * point-1:犯罪主体
	 */
	info_crime_main_subject : {
		params : [ {
			dependentPoints : "meta_people_attr"
		} ],
		method : "rule_func_extract_people_type_info"
	},
	/**
	 * point-2:诈骗金额
	 */
	info_fraud_mount : {
		params : [ {
			tagList : "office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(违法所得|赃款|总价款(为|是)|价值|骗)[^,，；;:：。]*?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([共合总]计|骗[取得]|诈骗|价值|涉案|赃款|报销|虚报)[^,，；;:：。]*?[\\d.]+万?元",
			reverseRegex : "偿款[共合总]计[^,，；;:：。]*?[\\d.]+万?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "([共合总]计|骗[取得]|诈骗|价值|涉案|赃款|犯罪数额)[^,，；;:：。]*?[\\d.]+万?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(违法所得|赃款)[^,，；;:：。]*?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-3:诈骗特殊物资
	 */
	info_fraud_special_material : {
		params : [ {
			tagList : "office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(骗|套取)[^,，\\.。;；、]*?(救灾|抢险|优抚|扶贫|移民|救济款物|医疗|国家财产)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "[诈骗][^,，\\.。;；、]*?(救灾|抢险|优抚|扶贫|移民|救济款物|医疗|国家财产)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(骗|套取)[^,，\\.。;；、]*?(救灾|抢险|优抚|扶贫|移民|救济款物|医疗|国家财产)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:假冒国家机关或公益性组织
	 */
	info_pretending_dept_welfare : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(假冒|冒充)(国家|公益性?组织|公安|检察院|法院|银行)|虚假政府文件|(谎称|虚假|冒充)[^,，：：；;\\.。]*?(公职|主任|领导)|(假借|用)[^,，：：；;\\.。]*?慈善[^,，：：；;\\.。]*?名义",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:为违法活动
	 */
	info_for_illegal_purpose : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(为|用于|因)[^,，；;:：。\\.]*?((违法(犯罪)?|非法)活动|吸毒|赌博|赌资)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(为|用于)[^,，；;:：。\\.]*?((违法(犯罪)?|非法)活动|吸毒|赌博)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:多次诈骗
	 */
	info_multiple_fraud : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "[多数]次[^,，；;:：。\\.]*?(诈骗|骗取|作案|签订[^,，；;:：。\\.]*?合同)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:金额范围
	 */
	info_amount_range : {
		params : [ {
			tagList : "court_opinion",
			regex : "额较大#额巨大#额特别巨大|特别巨大",
			capture : "数额较大#数额巨大#数额特别巨大",
			cacheKey : "meta_people_name2obj",
            meanWhile : "0",
            order : "1",
            defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info",
		adjust : [ {
			params : [ {
				dependentPoints : "info_crime_main_subject#info_fraud_mount#meta_case_ay",
				cacheKey : "meta_people_name2obj"
			} ],
			method : "rule_func_extract_range_judgment"
		} ]
	},
	/**
	 * point-8:被害人为弱势群体
	 */
	info_disadvantaged_victim : {
		params : [ {
			tagList : "court_opinion",
			regex : "(被害人|诈骗)[^;；：:\\.。]*?(残疾|老年|丧失劳动能力)|(对象|骗取)[\\u4e00-\\u9fae、]*?老年?人|[向对][^,，；;:：\\.。]*?(老年人|弱势群体)[^,，；;:：\\.。]*?诈骗",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:造成严重后果
	 */
	info_grave_consequence : {
		params : [ {
			tagList : "court_opinion",
			regex : "自杀|精神失常|严重后果|数额系?(特别)?巨大",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:因生活所迫学习治病急需
	 */
	info_life_purpose : {
		params : [ {
			tagList : "court_opinion",
			regex : "生活所迫|(经济|生活)(拮据|困难)|没钱|(为)[^\\.。;；]*?(学习|上学)|(为|钱)[^\\.。;；]*?治病||因[\\u4e00-\\u9fae]*?病",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:未参与分赃或者分赃较少
	 */
	info_less_stolen_goods : {
		params : [ {
			tagList : "court_opinion",
			regex : "(未|没有)参与分赃|分赃较少",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-12:有能力但拒绝退赃退赔
	 */
	info_abled_refused_return : {
		params : [ {
			tagList : "court_opinion",
			regex : "拒[不绝]退[赃赔]|未退赃|拒不返还赃款",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
};