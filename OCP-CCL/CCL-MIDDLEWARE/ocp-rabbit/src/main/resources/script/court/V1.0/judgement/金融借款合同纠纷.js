var NAMESPACE_COURT_JUDGEMENT_金融借款合同纠纷 = {
	/**
	 * point-1:借贷本金
	 */
	info_loan_principal : {
		params : [ {
			tagList : "facts_found",
			regex : "(借|贷)款[^度,，\\。;；:：]*?元|[^,，\\.。;；:：]*?元[^,，\\.。;；:：]*?(借|贷)款"
		} ],
		method : "rule_func_extract_money",
		adjust : [ {
			params : [ {
				valueNodes : "0#2000#3000#10000#30000",
				valueRanges : "0-2千万#2-3千万#3千万-1亿#1-3亿#3亿以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-2:借款期限
	 */
	info_loan_time : {
		params : [ {
			tagList : "facts_found",
			regex : "期限[从自为]*?(\\d{4}年\\d{1,2}月(\\d{1,2}日)?)[起]?至(\\d{4}年\\d{1,2}月(\\d{1,2}日)?)|期限[^,，\\.。;；:：]*?[年月日天]",
            reverse : "0",
			unit : "年"
		} ],
		method : "rule_func_extract_time",
		adjust : [ {
			params : [ {
				valueNodes : "0#1#3#5",
				valueRanges : "0-1年#1-3年#3-5年#5年以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-3:借款年率
	 */
	info_loan_interest_rate : {
		params : [ {
			tagList : "plaintiff_args#facts_found",
			regex : "利率",
			reverseRegex : "逾期",
			defaultVal : "0",
			unit : "年"
		} ],
		method : "rule_func_extract_interest_rate",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.06#0.24#0.36",
				valueRanges : "0-6%#6%-24%#24%-36%#36%以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-4:逾期年利率
	 */
	info_overdue_interest_rate : {
		params : [ {
			tagList : "plaintiff_args#facts_found#court_opinion",
			regex : "逾期(贷款)?罚息利率为|支付逾期利息"
		} ],
		method : "rule_func_extract_interest_rate",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.09#0.36#0.54",
				valueRanges : "0-9%#9%-36%#36%-54%#54%以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-5:利息结算方式
	 */
	info_interest_account_method : {
		params : [ {
			tagList : "plaintiff_args#facts_found#court_opinion",
			regex : "(?:当|按)(年|季|月|日)(?:应收利|结)?息",
			capture : "按\\1结息"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-6:担保方式
	 */
	info_guaranty_style : {
		params : [ {
			tagList : "plaintiff_args#facts_found#court_opinion",
			regex : "(保证|质押|抵押|定金|留置)(担保|人|责任|合同|贷款|权|金)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-7:支持借贷本金
	 */
	info_support_loan_principal : {
		params : [ {
			tagList : "judgement_content",
			regex : "以([\\d\\.万]+)元为基数|(借|贷)款[^,，\\。;；:：]*?元|([\\d\\.万]+)元[^,，\\.。;；:：]*?(借|贷)款"
		} ],
		method : "rule_func_extract_money",
		adjust : [ {
			params : [ {
				valueNodes : "0#2000#3000#10000#30000",
				valueRanges : "0-2千万#2-3千万#3千万-1亿#1-3亿#3亿以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-8:支持借贷年利率
	 */
	info_support_loan_interest_rate : {
		params : [ {
			tagList : "judgement_content",
			regex : "(年|月|日)利率"
		} ],
		method : "rule_func_extract_percentage",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.06#0.24#0.36",
				valueRanges : "0-6%#6%-24%#24%-36%#36%以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-9:支持逾期年利率
	 */
	info_support_overdue_interest_rate : {
		params : [ {
			tagList : "judgement_content",
			regex : "逾期利息[\\(（].*?(年|月|日|天)利率([\\d\\.%]+).*?[\\)）]|(清偿之日|逾期利息).*按(年|月|日|天)利率([\\d\\.%]+).*?计算"
		} ],
		method : "rule_func_extract_percentage",
		adjust : [ {
			params : [ {
				valueNodes : "0#0.09#0.36#0.54",
				valueRanges : "0-9%#9%-36%#36%-54%#54%以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-11:担保责任承担方式
	 */
	info_support_duty_undertake_method : {
		params : [ {
			tagList : "judgement_content",
			regex : "(连带|补充)[^,，\\.。;；:：]*?责任",
			capture : "\\1责任"
		} ],
		method : "rule_func_extract_info_by_regex"
	}
}