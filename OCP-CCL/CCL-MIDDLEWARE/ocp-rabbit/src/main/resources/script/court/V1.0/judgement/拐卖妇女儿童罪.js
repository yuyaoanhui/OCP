var NAMESPACE_COURT_JUDGEMENT_拐卖妇女儿童罪 = {
	/**
	 * point-1:强迫卖淫
	 */
	info_forced_prostitution : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "卖淫"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-2:造成残疾或死亡
	 */
	info_impairment : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "死亡|伤残|残疾"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},

	/**
	 * point-3:拐卖手段
	 */
	info_traffick_means : {
		params : [ {
			tagList : "court_opinion",
			regex : "诈骗|骗至|骗到|拐骗|诱骗|行骗|诱拐",
			capture : "拐骗"
		}, {
			tagList : "court_opinion",
			regex : "偷盗|盗窃|盗取",
			capture : "偷盗"
		}, {
			tagList : "court_opinion",
			regex : "绑架|赎金|麻醉|昏迷",
			capture : "绑架"
		}, {
			tagList : "court_opinion",
			regex : "抢夺",
			capture : "抢夺"
		}, {
			tagList : "court_opinion",
			regex : "收买",
			capture : "收买"
		}, {
			tagList : "court_opinion",
			regex : "贩卖",
			capture : "贩卖"
		}, {
			tagList : "court_opinion",
			regex : "接送|中转|藏匿",
			capture : "中转"
		}, {
			tagList : "judgement_content",
			regex : "诈骗|骗至|骗到|拐骗|诱骗|行骗|诱拐",
			capture : "拐骗"
		}, {
			tagList : "judgement_content",
			regex : "偷盗|盗窃|盗取",
			capture : "偷盗"
		}, {
			tagList : "judgement_content",
			regex : "绑架|赎金|麻醉|昏迷",
			capture : "绑架"
		}, {
			tagList : "judgement_content",
			regex : "抢夺",
			capture : "抢夺"
		}, {
			tagList : "judgement_content",
			regex : "收买",
			capture : "收买"
		}, {
			tagList : "judgement_content",
			regex : "贩卖",
			capture : "贩卖"
		}, {
			tagList : "judgement_content",
			regex : "接送|中转|藏匿",
			capture : "中转"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-4:拐卖到境外
	 */
	info_abroad_traffick : {
		params : [ {
			tagList : "court_opinion#facts_found"
		} ],
		method : "rule_func_extract_abroad_traffick"
	},
	/**
	 * point-5:涉及奸淫
	 */
	info_rape_related : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "强奸|奸淫|强行[一-龥]*?发生性关系"
		}, {
			tagList : "judgement_content",
			regex : "强奸罪"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-6:拐卖罪名
	 */
	info_traffick_crime_type : {
		params : [ {
			tagList : "judgement_content",
			regex : "拐卖妇女罪",
			capture : "拐卖妇女罪"
		}, {
			tagList : "judgement_content",
			regex : "拐卖儿童罪",
			capture : "拐卖儿童罪"
		} ],
		method : "rule_func_extract_info_by_regex"
	}
};