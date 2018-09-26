var NAMESPACE_COURT_JUDGEMENT_信用卡诈骗罪 = {
	/**
	 * point-1:为违法活动
	 */
	info_for_illegal_purpose : {
		params : [ {
			tagList : "office_opinion",
			regex : "(为|用于)[^,，；;:：。\\.]*?((违法(犯罪)?|非法)活动|吸毒|赌博)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			regex : "(为|用于)[^,，；;:：。\\.]*?((违法(犯罪)?|非法)活动|吸毒|赌博)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "(为|用于)[^,，；;:：。\\.]*?((违法(犯罪)?|非法)活动|吸毒|赌博)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:诈骗金额
	 */
	info_fraud_mount : {
		params : [
				{
					tagList : "office_opinion",
					regex : "(共|累计|尚欠)[\\u4e00-\\u9fae]*?[\\d.]+元",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "office_opinion",
					regex : "(透支|套取|共欠|共计|累计|合计|退缴|骗得|诈骗)[\\u4e00-\\u9fae]*?[\\d.]+元",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "court_opinion",
					regex : "(透支|套取|共欠|共计|累计|合计|退缴|骗得|诈骗)[\\u4e00-\\u9fae]*?[\\d.]+元",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "judgement_content",
					regex : "([违非]法所得|共计|透支|退赔|赃款)[\\u4e00-\\u9fae]*?([\\d.]+元|[一二三四五六七八九十零百千万元角分]+)",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-3:多次诈骗
	 */
	info_multiple_fraud : {
		params : [ {
			tagList : "facts_found",
			regex : "[多数]次[^,，；;:：。\\.]*?诈骗",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "[多数]次[^,，；;:：。\\.]*?诈骗",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info",
		adjust : [ {
			params : [ {
				cacheKey : "meta_people_name2obj"
			} ],
			method : "rule_func_extract_litigant_judgment_num"
		} ]
	},
	/**
	 * point-4:金额范围
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
				cacheKey : "meta_people_name2obj",
				dependentPoints : "info_falsify_credit#info_falsify_identity_4credit#info_invalidated_credit#info_uttering_other_credit#info_malicious_overdraft#info_fraud_mount"
			} ],
			method : "rule_func_extract_adjust_money_range"
		} ]
	},
	/**
	 * point-5:突发事件期间犯罪
	 */
	info_crime_while_emergency : {
		params : [ {
			tagList : "court_opinion",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:使用伪造的信用卡
	 */
	info_falsify_credit : {
		params : [ {
			tagList : "court_opinion",
			regex : "伪造信用卡",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:以虚假的身份证明骗领的信用卡
	 */
	info_falsify_identity_4credit : {
		params : [ {
			tagList : "court_opinion",
			regex : "骗领的?信用卡",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:作废的信用卡
	 */
	info_invalidated_credit : {
		params : [ {
			tagList : "court_opinion",
			regex : "作废的信用卡",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:冒用他人信用卡
	 */
	info_uttering_other_credit : {
		params : [ {
			tagList : "court_opinion",
			regex : "冒用他人信用卡",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:恶意透支
	 */
	info_malicious_overdraft : {
		params : [ {
			tagList : "court_opinion",
			regex : "恶意透支",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:因生活所迫学习治病急需
	 */
	info_life_purpose : {
		params : [ {
			tagList : "court_opinion",
			regex : "家庭(生活)?困难|生活所迫|经济(拮据|困难)|没钱|(为)[^\\.。;；]*?(学习|上学)|(为|钱|用)[^,，\\.。;；]*?治病|因[\\u4e00-\\u9fae]*?病",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-12:已偿还全部透支款息
	 */
	info_reimburse_overdraft : {
		params : [ {
			tagList : "court_opinion",
			regex : "(偿还|归还|退还|返还)了?(全部)?透[支资](款(本?息|项|及利息)|本息)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-13:有能力但拒绝退赃退赔
	 */
	info_abled_refused_return : {
		params : [ {
			tagList : "court_opinion",
			regex : "有(经济|还款)能力[^,，：:；;\\.。]*?(逃避|拒绝)(退赃|偿还|赔偿|还款)|仍未尽到还款责任",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-14:持有不满十张空白信用卡或者非法持有不满五张他人信用卡
	 */
	info_more_credit : {
		params : [ {
			tagList : "court_opinion",
			regex : "不满十张空白信用卡|非法持有不满五张他人信用卡",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
}