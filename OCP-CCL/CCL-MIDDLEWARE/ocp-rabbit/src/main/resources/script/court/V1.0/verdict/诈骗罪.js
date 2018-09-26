var NAMESPACE_COURT_VERDICT_诈骗罪 = {
	/**
	 * point-1:多次诈骗
	 */
	info_multiple_fraud : {
		params : [
				{
					tagList : "office_opinion#plaintiff_args",
					cacheKey : "meta_people_name2obj",
					regex : "(先后|([数多]|([3-9]|[1-9]\\d+))[起次])[^\\.。;；不]*?[诈骗]|([诈骗]|作案)[^\\.。;；不]*?(先后|([数多]|([3-9]|[1-9]\\d+))[起次])",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(先后|([数多]|([3-9]|[1-9]\\d+|[一二两三四五六七八九十]+))[起次])[^\\.。;；不]*?[诈骗]|([诈骗]|作案)[^,，\\.。;；、不]*?(先后|([数多]|([3-9]|[1-9]\\d+|[一二两三四五六七八九十]+))[起次])",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:为违法活动诈骗
	 */
	info_for_illegal_purpose : {
		params : [ {
			tagList : "office_opinion#plaintiff_args",
			cacheKey : "meta_people_name2obj",
			regex : "[诈骗筹集][^\\.。;；、]*?[毒赌嫖]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "[诈骗筹集][^\\.。;；、]*?[毒赌嫖]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:诈骗特殊对象
	 */
	info_fraud_special_victims : {
		params : [ {
			tagList : "office_opinion#plaintiff_args",
			cacheKey : "meta_people_name2obj",
			regex : "(?:被害人|[诈骗])[^,，\\.。;；、]*?(残疾|老(年)?人|丧失劳动能力)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(?:被害人|[诈骗])[^,，\\.。;；、]*?(残疾|老(年)?人|丧失劳动能力)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(残疾|老(年)?人|丧失劳动能力)[^,，\\.。;；、]*?(?:犯罪|[诈骗])",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:诈骗特殊物资
	 */
	info_fraud_special_material : {
		params : [
				{
					tagList : "office_opinion#plaintiff_args",
					cacheKey : "meta_people_name2obj",
					regex : "[诈骗][^,，\\.。;；、]*?(救灾|抢险|优抚|扶贫|移民|救济款物|医疗|国家财产)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "[诈骗][^,，\\.。;；、]*?(救灾|抢险|优抚|扶贫|移民|赈灾|救灾|捐款|救济款物|医[疗保]|国家财产)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:电信网络诈骗
	 */
	info_internet_fraud : {
		params : [ {
			tagList : "office_opinion#plaintiff_args",
			cacheKey : "meta_people_name2obj",
			regex : "互联网|网络|群呼机|电话推销|基站|拨打电话|诈骗电话",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "互联网|网络|群呼机|电话推销|基站|拨打电话|诈骗电话",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:诈骗时间
	 */
	info_fraud_times : {
		params : [ {
			tagList : "office_opinion#plaintiff_args",
			cacheKey : "meta_people_name2obj",
			regex : "([\\d]+年[\\d]+月[\\d]+日)",
			capture : "\\1",
			meanWhile : "0",
			order : "0",
			defaultAll : "1"
		}, {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(\\d+年\\d+月\\d+日)",
			capture : "\\1",
			meanWhile : "0",
			order : "0",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-7:诈骗手段
	 */
	info_fraud_means : {
		params : [
				{
					tagList : "office_opinion#plaintiff_args#facts_found_cmpl",
					cacheKey : "meta_people_name2obj",
					regex : "中奖#假借销售商品#网购[^,，\\.。;；、]*?退款|网购[^,，\\.。;；、]*?退票|改签#冒充|(QQ|微信|社交平台)[^\\.。;；、]*?[诈骗虚假]#二维码|红包#兼职",
					capture : "中奖#假借销售商品#网购退款、退票改签#冒充熟人好友领导客户银行政府机关等#虚假二维码、红包#虚假兼职",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "中奖#假借销售商品#网购[^,，\\.。;；、]*?退款|网购[^,，\\.。;；、]*?退票|改签#冒充|(QQ|微信|社交平台)[^\\.。;；、]*?[诈骗虚假]#二维码|红包#兼职",
					capture : "中奖#假借销售商品#网购退款、退票改签#冒充熟人好友领导客户银行政府机关等#虚假二维码、红包#虚假兼职",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "中奖#假借销售商品#网购[^,，\\.。;；、]*?退款|网购[^,，\\.。;；、]*?退票|改签#冒充|(QQ|微信|社交平台)[^\\.。;；、]*?[诈骗虚假]#二维码|红包#兼职",
					capture : "中奖#假借销售商品#网购退款、退票改签#冒充熟人好友领导客户银行政府机关等#虚假二维码、红包#虚假兼职",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-8:主动归还
	 */
	info_active_return : {
		params : [ {
			tagList : "office_opinion#plaintiff_args#facts_found_cmpl",
			cacheKey : "meta_people_name2obj",
			regex : "(主动|积极)[^\\.。;；、]*?(放回|归还|返还|退还|退赔)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(主动|积极)[^\\.。;；、]*?(放回|归还|返还|退还|退赔)",
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
		params : [
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(被害人|受害人)[^,，\\.。;；、]*?(自杀|精神失常|(其他|等)严重后果)|(后果)[^,，\\.。;；、不]*?严重",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(被害人|受害人)[^,，\\.。;；、]*?(自杀|精神失常|(其他|等)严重后果)|(后果)[^,，\\.。;；、不]*?严重",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(被害人|受害人)[^,，\\.。;；、]*?(自杀|精神失常|(其他|等)严重后果)|(后果)[^,，\\.。;；、不]*?严重",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:诈骗金额
	 */
	info_fraud_mount : {
		params : [
				{
					tagList : "office_opinion#plaintiff_args#facts_found_cmpl",
					cacheKey : "meta_people_name2obj",
					regex : "([诈骗取]|总计|总价值|共计|涉案|金额|价值|数额|合计|总共|现金|人民币)([一-龥]+)?([\\d\\.]+)(余)?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "([诈骗取]|总计|总价值|共计|涉案|金额|价值|数额|合计|总共|现金|人民币)[^,，。;；:：]*?([\\d\\.一二三四五六七八九十零百千万]+)([余多]*?)元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-11:金额范围
	 */
	info_amount_range : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "额较大#额巨大#额特别巨大|特别巨大",
			capture : "数额较大#数额巨大#数额特别巨大",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-12:对不特定的多数人
	 */
	info_fraud_unspecific_victims : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(不特定)[^,，\\.。;；、]*?诈骗|诈骗[^,，\\.。;；、]*?(不特定)|在互联网上发布虚假信息",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-13:赈灾募捐名义实施诈骗
	 */
	info_donation_cover : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(赈灾|救灾|捐款|献爱心|援助|公益)[^,，\\.。;；、]*?(理由|名义|幌子)[^,，\\.。;；、]*?([诈骗])|([诈骗])[^,，\\.。;；、]*?(赈灾|救灾|捐款|献爱心|援助|公益)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-14:其他严重情节
	 */
	info_fraud_other_serious_plots : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(等|其他)[^,，\\.。;；、不]*?(严重情节|情节严重)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-15:因生活所迫学习治病急需
	 */
	info_life_purpose : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "生活所迫|经济(拮据|困难)|没钱|(为)[^\\.。;；]*?(学习|上学)|(为|钱)[^\\.。;；]*?治病",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-16:诈骗近亲属财物并获得谅解
	 */
	info_family_forgiven_fraud : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "[诈骗]+[^,，\\.。;；、]*?(父|母|夫|妻|家庭成员|亲属|自家财产)[^\\.。;；、不]*?(谅解|原谅)|[是为系][^,，\\.。;；、]*?(父|母|夫|妻|家庭成员|亲属|自家财产)[^\\.。;；、不]*?(谅解|原谅)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-17:因盗窃受过刑事处罚
	 */
	info_penalty_for_theft_record : {
		params : [ {
			dependentPoints : "meta_doc_date#info_penalty_for_theft_record#info_penalty_admin_record",
			crimeName : "诈骗罪"
		} ],
		method : "rule_func_adjust_record_penalty"
	},
	/**
	 * point-18:一年内因盗窃受过行政处罚
	 */
	info_penalty_admin_record : {
		params : [ {
			dependentPoints : "meta_doc_date#info_penalty_for_theft_record#info_penalty_admin_record",
			crimeName : "诈骗罪"
		} ],
		method : "rule_func_adjust_record_penalty"
	}
};