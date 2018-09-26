var NAMESPACE_COURT_VERDICT_民间借贷纠纷 = {
	/**
	 * point-1:抵押
	 */
	info_loan_mortgage : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "抵押"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-2:担保人
	 */
	info_loan_guarantor : {
		params : [ {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "担保人|为[^,，\\。;；:：]*?担保"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-3:担保方式
	 */
	info_guaranty_style : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "(保证|抵押|质押|留置|定金)(?![、])",
			capture : "\\1"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-4:担保责任承担方式
	 */
	info_support_duty_undertake_method : {
		params : [ {
			tagList : "court_opinion",
			regex : "一般担保",
			capture : "一般担保",
			reverseRegex : "(不|没有|未)[^;；。\\.]*?担保"
		}, {
			tagList : "court_opinion",
			regex : "连带[^,，；。\\.]*?(责任|保证|担保|义务)|担保人[^;；。\\.不]*?(责任|承担义务)",
			capture : "连带责任",
			reverseRegex : "(不|没有|未)[^;；。\\.]*?担保"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-5:支付凭证
	 */
	info_payment_evidence : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "现金|(汇款|转款|取款)凭证|(汇入|汇款|转入|转款)[^，,。；;]*?(元|账户)|转账(回单|凭证)|收条|转款凭条",
			capture : "一般担保",
			reverseRegex : "(不|没有|未)[^;；。\\.]*?担保"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-6:催告
	 */
	info_payment_request : {
		params : [ {
			tagList : "court_opinion#facts_found#plaintiff_args",
			regex : "催[要讨告款还收债]|[讨索]要|追讨|以种种理由|(?:拒不(?:归还|履行|还款|偿还))|多次[^，；。;,]*要求"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-7:归还部分
	 */
	info_payment_pepayment : {
		params : [ {
			tagList : "court_opinion",
			regex : "([向已]|曾经|)[\\u4e00-\\u9fa5]*?(偿还|归还|给付|支付|还款|还清)|(偿还|归还|给付|支付|还款|还清)[^,，\\。;；:：]*?(部分|[后元])"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-8:借款人数大于1
	 */
	info_borrower_num_bigger_than_one : {
		params : [ {
			tagList : "court_opinion",
			regex : "[数多等二三四五六七八九十]人[^,，\\。;；:：]*?借款|借款[^,，\\。;；:：]*?[多数等二三四五六七八九十]人"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-9:名义借款人
	 */
	info_nominal_borrower : {
		params : [ {
			tagList : "court_opinion",
			regex : ".+名义.+借款"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-10:信用卡借用人
	 */
	info_credit_card_borrower : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "信用卡借用人|借用[^,，\\。;；:：]*?信用卡"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-11:个人独资企业投资人
	 */
	info_sole_proprietorship_investor : {
		params : [ {
			tagList : "court_opinion",
			regex : "个人独资企业投资人"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-12:合伙组织
	 */
	info_partnership_organization : {
		params : [ {
			tagList : "court_opinion",
			regex : "合伙组织|合伙经营"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-13:空白收据
	 */
	info_blank_receipt : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "空白[^,，\\。;；:：]*?收据"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-14:企业歇业未成立清算组
	 */
	info_out_of_business_no_liquidation_tbam : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "((公司|企业)[^;；\\.。]*?未[^;；\\.。]*?歇业清算)|((公司|企业)歇业[^;；\\.。]*?未成立清算组)|(未成立清算组[^;；\\.。]*?(公司|企业)歇业)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-15:夫妻共同财产
	 */
	info_couple_common_property : {
		params : [ {
			tagList : "court_opinion",
			regex : "夫妻共同财产"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-16:虚假诉讼
	 */
	info_false_lawsuit : {
		params : [ {
			tagList : "court_opinion",
			regex : "虚假诉讼"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-17:网络贷款平台的担保责任
	 */
	info_wee_loan_platform_guarantee : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "(网(络)?贷(款)?平台[^;；\\.。]*?(担保|责任|出借人))|((担保|责任|出借人)[^;；\\.。]*?网(络)?贷(款)?平台)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-18:约定复利
	 */
	info_compound_interest : {
		params : [ {
			tagList : "plaintiff_args#court_opinion",
			regex : "复利|利滚利"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-19:支付方式
	 */
	info_ir_request_paper : {
		params : [ {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "现金",
			capture : "现金"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "汇款",
			capture : "银行汇款"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "转账",
			capture : "电子转账",
			type : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-20:还款方式
	 */
	info_repayment_method : {
		params : [ {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "口头约定",
			capture : "口头约定"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "先本后息",
			capture : "先本后息"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "先息后本",
			capture : "先息后本"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "等额本息",
			capture : "等额本息"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "等额本金",
			capture : "等额本金"
		}, {
			tagList : "facts_found#court_opinion#plaintiff_args",
			regex : "本息一次付清",
			capture : "本息一次付清"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-21:借条利率
	 */
	info_ir_paper : {
		params : [ {
			tagList : "plaintiff_args#facts_found",
			regex : "利率|利息[^,，\\。;；:：]*?[分%]|[分%][^,，\\。;；:：]*?利息",
			unit : "月"
		} ],
		method : "rule_func_extract_percentage"
	},
	/**
	 * point-22:借条本金
	 */
	info_prcp_paper : {
		params : [ {
			tagList : "facts_found#plaintiff_args",
			regex : "(借|贷)款[^,，\\。;；:：]*?元|[^,，\\.。;；:：]*?元[^,，\\.。;；:：]*?(借|贷)款",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-23:借条时间
	 */
	info_loan_start_time : {
		params : [ {
			tagList : "plaintiff_args#facts_found",
			regex : "([\\d]+年[\\d]+月[\\d]+日)[^;；\\.。:：还]*?[与向][^,，;；\\.。:：还]*?[借贷]",
            reverse : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-24:借条时长
	 */
	info_loan_duration : {
		params : [
				{
					tagList : "facts_found",
					regex : "期限[^,，\\.。;；:：]*?[年|月|日|天]",
                    reverse : "0",
					type : "月"
				},
				{
					tagList : "facts_found",
					regex : "([借贷]|时间|期限)[^,，\\.。;；:：]*?[自从][^,，\\.。;；:：]*?[至到][^,，\\.。;；:：]*?[月日]",
                    reverse : "0",
					type : "月"
				} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-25:借款期限
	 */
	info_loan_deadline : {
		params : [ {
			tagList : "plaintiff_args#facts_found",
			regex : "(约定)([\\d]+年[\\d]+月[\\d]+日)(还款|归还)|[至到][\\d]+年[\\d]+月[\\d]+日",
            reverse : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-26:借款载体
	 */
	info_loan_carrier : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "口头约定",
			capture : "口头约定"
		}, {
			tagList : "facts_found#court_opinion",
			regex : "借款承诺书",
			capture : "借款承诺书"
		}, {
			tagList : "facts_found#court_opinion",
			regex : "立据|借条|借据|借款凭据",
			capture : "借条"
		}, {
			tagList : "facts_found#court_opinion",
			regex : "欠条",
			capture : "欠条"
		}, {
			tagList : "facts_found#court_opinion",
			regex : "借款(担保)?合同",
			capture : "借款合同"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-27:诉求利率
	 */
	info_ir_pequest : {
		params : [ {
			tagList : "plaintiff_args#facts_found#court_opinion",
			regex : "按[^,，\\。;；:：]*?利率",
			unit : "月"
		} ],
		method : "rule_func_extract_percentage"
	},
	/**
	 * point-28:诉求本金
	 */
	info_prcp_pequest : {
		params : [ {
			tagList : "facts_found#plaintiff_args",
			regex : "(借|贷)款[^,，\\。;；:：]*?元|[^,，\\.。;；:：]*?元[^,，\\.。;；:：]*?(借|贷)款",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-29:代替收款
	 */
	info_collection_for_others : {
		params : [ {
			tagList : "court_opinion",
			regex : "(委托|[代替])[^,，\\。;；:：不]*?收款"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-30:代替还款
	 */
	info_repayment_for_others : {
		params : [ {
			tagList : "court_opinion",
			regex : "(委托|[代替])[^,，\\。;；:：不]*?(偿还|还款)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-31:代替借款
	 */
	info_borrowing_for_others : {
		params : [ {
			tagList : "court_opinion",
			regex : "代替借款"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-32:债务加入
	 */
	info_deet_accession : {
		params : [ {
			tagList : "court_opinion",
			regex : "债[^,，\\。;；:：不]*?加入"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-33:债务继承
	 */
	info_deet_succession : {
		params : [ {
			tagList : "court_opinion",
			regex : "债务[^,，\\。;；:：不]*?继承|继承[^,，\\。;；:：不]*?债务",
			reverseRegex : "不予",
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-34:债权转让
	 */
	info_creditor_right_transfer : {
		params : [ {
			tagList : "court_opinion",
			regex : "债权转让"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-35:债务转移
	 */
	info_deet_transfer : {
		params : [ {
			tagList : "court_opinion",
			regex : "债务转移"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-36:同居共同债务
	 */
	info_cohabitation_common_deet : {
		params : [ {
			tagList : "court_opinion",
			regex : "同居共同债务"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-37:承认借款
	 */
	info_admit_loan_fact : {
		params : [ {
			tagList : "court_opinion#defendant_args",
			regex : "承认借款|(借([钱款]|信用卡)|贷款)[^,，\\.。;；:：]*?[属事]实|向[^,，\\.。;；:：没]*?([贷借])",
			reverseRegex : "(没有|不承认)[^,，\\.。;；:：]*?借款"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-38:夫妻共同债务
	 */
	info_couple_common_liability : {
		params : [ {
			tagList : "plaintiff_args#defendant_args#court_opinion",
			regex : "夫妻[^,，\\.。;；:：]*?共同[^,，\\.。;；:：]*?债务|(债务|[两二]被告)[^,，\\.。;；:：]*?夫妻"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-39:支持利率
	 */
	info_ir_judgement : {
		params : [ {
			tagList : "judgement_content",
			regex : "[年月日]利率|按[^,，\\。;；:：]*?[分%]",
			unit : "月"
		} ],
		method : "rule_func_extract_percentage"
	},
	/**
	 * point-40:支持本金
	 */
	info_prcp_judgement : {
		params : [ {
			tagList : "judgement_content",
			regex : "(借|贷)款[^,，\\。;；:：]*?元[^,，\\.。;；:：无]*?$|[^,，\\.。;；:：]*?元[^,，\\.。;；:：]*?(借|贷)款[^,，\\.。;；:：无]*?$",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-41:支持本金和利息
	 */
	info_total_judement : {
		params : [ {
			tagList : "judgement_content",
			regex : "本金及利息|共计|一共|合计|总共|本息",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	}
};