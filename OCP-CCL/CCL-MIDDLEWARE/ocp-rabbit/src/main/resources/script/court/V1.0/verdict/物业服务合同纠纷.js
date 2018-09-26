var NAMESPACE_COURT_VERDICT_物业服务合同纠纷 = {
	/**
	 * point-1:滞纳金每日标准
	 */
	info_late_fee_standard : {
		params : [
				{
					tagList : "court_opinion",
					regex : "滞纳金[^。；；]*?按(.*?)(缴|纳|给|付|计)|按(.*?)(缴|纳|给|付|收|计)[^,，\\.。；；]滞纳金",
					unit : "日"
				},
				{
					tagList : "judgement_content",
					regex : "滞纳金[^。；；]*?按(.*?)(缴|纳|给|付|计)|按(.*?)(缴|纳|给|付|收|计)[^,，\\.。；；]滞纳金",
					unit : "日"
				} ],
		method : "rule_func_extract_percentage",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.0005#0.01#0.05",
				valueRanges : "0-5‱#5‱-1%#1%-5%#5%以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-2:滞纳金
	 */
	info_late_fee : {
		params : [ {
			tagList : "judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "滞纳金(\\d+(?:\\.\\d+)?)元|(\\d+(?:\\.\\d+)?)元滞纳金",
			unit : "日"
		} ],
		method : "rule_func_extract_money",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.1#1",
				valueRanges : "0-1千元#1千元-1万元#1万元以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-3:物业费
	 */
	info_property_management_fee : {
		params : [ {
			tagList : "judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "物业(?:管理)?(?:服务)?费(\\d+(?:\\.\\d+)?)元|(\\d+(?:\\.\\d+)?)元物业(?:管理)?(?:服务)?费",
			unit : "日"
		} ],
		method : "rule_func_extract_money",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.1#1#10",
				valueRanges : "0-1千元#1千元-1万元#1-10万元#10万元以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-4:诉讼双方
	 */
	info_litigant_parties : {
		params : [],
		method : "rule_func_extract_parse_both_sides"
	}
}