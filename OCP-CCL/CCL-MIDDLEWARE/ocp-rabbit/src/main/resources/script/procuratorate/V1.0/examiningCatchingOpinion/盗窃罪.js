var NAMESPACE_PROCURATORATE_EXAMININGCATCHINGOPINION_盗窃罪 = {
	/**
	 * point-1:因盗窃受过刑事处罚
	 */
	info_penalty_for_theft_record : {
		params : [
				{
					tagList : "SUSPECT_BASE_INFO#CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "([曾因]|涉嫌)[^,，\\.。;；、]*?[窃偷盗][^\\.。;；、《》可能]*?(刑事|有期徒刑|拘役|管制|缓刑)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "QUES_TO_EXPLAIN",
					cacheKey : "meta_people_name2obj",
					regex : "([曾因]|涉嫌)[^,，\\.。;；、]*?[窃偷盗][^\\.。;；、《》可能]*?(刑事|有期徒刑|拘[役留]|管制|缓刑)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info",
		adjust : [ {
			params : [ {
				dependentPoints : "meta_doc_date",
				crimeName : "盗窃罪"
			} ],
			method : "rule_func_adjust_record_penalty"
		} ]
	},
	/**
	 * point-2:携带凶器
	 */
	info_arm_associated : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "凶器",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:多次盗窃
	 */
	info_multiple_theft : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "多次[\\u4e00-\\u9fa5]*?[窃盗偷]+",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:入户盗窃
	 */
	info_indoor_theft : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "入室|入户",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:扒窃
	 */
	info_pocket_pick : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "扒窃",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:盗窃文物
	 */
	info_cultural_relic : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "文物",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:盗窃特殊对象
	 */
	info_special_victims : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(?:被害人|盗窃)(?:[\\u4e00-\\u9fa5]+)?(残疾|(?:孤寡|留守)老人|(?:丧失|无)劳动能力)",
			capture : "\\1",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-8:盗窃特殊物资
	 */
	info_special_material : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "盗窃(?:[一-龥]+)?(救灾|抢险|优抚|扶贫|移民|救济款物)",
			capture : "\\1",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-9:破坏性手段
	 */
	info_means_destructive : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "破坏性(手段|方式)|破坏[^,，\\.。;；、]*?盗窃",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "RISK_ANALYSIS",
			regex : "破坏性手段",
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
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					regex : "生活所迫|没钱|学习|治病|急需",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE#HANDLE_OPINION",
					regex : "生活所迫|(没有|为了)[^,，\\.。;；:：]*?(生活|收入|工作)|经济(拮据|困难)|(为)[^\\.。;；]*?(学习|上学)|(学习|上学)[^\\.。;；]*?(钱|负担)|[生治]病",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:为违法活动盗窃
	 */
	info_illegal_purpose : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "吸毒|毒品|赌|嫖",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-12:总金额
	 */
	info_total_amount : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(总计|总价值|共计|涉案|金额|价值|数额|合计|总共|现金|人民币|价格)[^,，\\.。;；:：]*?([\\d\\.]+)元",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-13:盗窃文物级别
	 */
	info_cultural_relic_level : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "三级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?三级#(一|二)级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?(一|二)级#((?!(一级|二级|三级))文物)",
					capture : "三级文物#二级以上#普通文物",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "三级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?三级#(一|二)级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?(一|二)级#((?!(一级|二级|三级))文物)",
					capture : "三级文物#二级以上#普通文物",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-14:情节严重
	 */
	info_serious_circumstances : {
		params : [ {
			tagList : "HANDLE_OPINION#RISK_ANALYSIS#QUES_TO_EXPLAIN",
			cacheKey : "meta_people_name2obj",
			regex : "情节[^,，\\.。;；、否]*?(严重|较重|恶劣)",
			reverseRegex : "情节(严重|较重)的|(严重|较重|恶劣)[^,，\\.。;；、]*?([外])",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-15:情节轻微
	 */
	info_slight_circumstances : {
		params : [ {
			tagList : "HANDLE_OPINION#RISK_ANALYSIS#QUES_TO_EXPLAIN",
			cacheKey : "meta_people_name2obj",
			regex : "情节[^,，\\.。;；、否]*?(轻微|较轻)",
			reverseRegex : "情节轻微的|《中华人民共和国刑事诉讼法》第十五条",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-16:没有参与分赃或者获赃较少
	 */
	info_less_stolen_goods : {
		params : [ {
			tagList : "HANDLE_OPINION#RISK_ANALYSIS#QUES_TO_EXPLAIN",
			cacheKey : "meta_people_name2obj",
			regex : "(未|没有|无|较少|少量)[^,，\\.。;；、]*?(赃物|分赃)|(赃物|分赃)[^,，\\.。;；、]*?(未|没有|无|较少|少量)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-17:金额范围
	 */
	info_amount_range : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE#QUES_TO_EXPLAIN#RISK_ANALYSIS#HANDLE_OPINION",
					regex : "额较大#额巨大#额特别巨大|特别巨大",
					cacheKey : "meta_people_name2obj",
					capture : "数额较大#数额巨大#数额特别巨大",// 捕获模式与正则一一对应
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE#QUES_TO_EXPLAIN#RISK_ANALYSIS#HANDLE_OPINION",
					regex : "额较大#额巨大#额特别巨大|特别巨大",
					cacheKey : "meta_people_name2obj",
					capture : "数额较大#数额巨大#数额特别巨大",// 捕获模式与正则一一对应
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-18:盗窃总次数
	 */
	info_number_theft : {
		params : [ {
			tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "([窃偷盗]+|作案)[^,，\\.。;；、]*?[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+[起次桩]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "次"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-19:一年内因盗窃受过行政处罚
	 */
	info_penalty_admin_record : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE#RISK_ANALYSIS#QUES_TO_EXPLAIN#HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "一年(之)?内([\\u4e00-\\u9fa5、]+)?因(为)?盗窃([\\u4e00-\\u9fa5、]+)?受(到)?过([\\u4e00-\\u9fa5、]+)?行政处罚",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "QUES_TO_EXPLAIN",
					cacheKey : "meta_people_name2obj",
					regex : "[一去今]年[^,，\\.。;；、无]*?盗窃[^,，\\.。;；、]*?[处罚]",
					capture : "一年内曾因盗窃受过行政处罚的",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info",
		adjust : [ {
			params : [ {
				dependentPoints : "meta_doc_date",
				crimeName : "盗窃罪"
			} ],
			method : "rule_func_adjust_record_penalty"
		} ]
	},
	/**
	 * point-20:第一次翻案时间
	 */
	info_first_crime_date : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj"
		} ],
		method : "rule_func_extract_first_crime_time"
	},
	/**
	 * point-21:团伙
	 */
	info_gang_crime : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "团伙|多人",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
}