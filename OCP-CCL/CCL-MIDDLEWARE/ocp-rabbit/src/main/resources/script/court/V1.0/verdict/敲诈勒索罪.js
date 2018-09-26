var NAMESPACE_COURT_VERDICT_敲诈勒索罪 = {
	/**
	 * point-1:情节严重程度
	 */
	info_plot_degree : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "危害公共安全",
			capture : "对未成年人、残疾人、老年人、丧失劳动能力人,以将要实施危害公共安全犯罪相威胁",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "未成年|残疾人|老年人|丧失劳动能力人",
			capture : "对未成年人、残疾人、老年人、丧失劳动能力人,以将要实施严重侵犯公民人身权利犯罪相威胁",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "黑恶势力|黑社会",
			capture : "以黑恶势力名义",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "利用[^,，\\.。;；:：]*(军|警|公安|记者|(国家|新闻)[^,，\\.。;；:：]*人员)",
			capture : "利用特殊身份",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(冒充|假冒)[^,，\\.。;；:：]*(军|警|公安|记者|国家[^,，\\.。;；:：]*人员)",
			capture : "冒充特殊身份",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "造成其他严重后果",
			capture : "造成其他严重后果",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-2:因敲诈勒索受过刑事处罚
	 */
	info_criminal_punishment_extortion : {
		params : [ {
			tagList : "defendant",
			cacheKey : "meta_people_name2obj",
			regex : "因[^\\.。;；、]*?(敲诈|勒索)[^\\.。;；、]*?[受判处][^\\.。;；、]*?(刑事|有期徒刑|拘役|管制|缓刑)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info",
		adjust : [ {
			params : [ {
				dependentPoints : "meta_doc_date#info_administrative_penalty_racketeering",
				crimeName : "敲诈勒索罪"
			} ],
			method : "rule_func_adjust_record_penalty"
		} ]
	},
	/**
	 * point-3:因敲诈勒索受过刑事处罚
	 */
	info_blackmail_number : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "敲诈[^,，;；\\.。]*?([一二三四五六七八九十两\\d]+)次#([一二三四五六七八九十两\\d]+)次敲诈",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "次"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-4:敲诈数额
	 */
	info_blackmail_amount : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(共|总|累计|价值)[^,，;；\\。]*?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(数额|敲诈|勒索|索要|索得|支付)[^,，;；\\。]*?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(退赔|返还|违法所得)[^,，;；\\。]*?元",
			reverseRegex : "继续",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-5:伤害后果
	 */
	info_damage_consequences : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤#重伤#使[^,，\\.。;；:：]*?残疾",
			capture : "轻微伤#轻伤#重伤#残疾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-6:轻微伤人数
	 */
	info_minor_injury : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?轻微伤#鉴定[^;；\\.。]*?轻微伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?轻微伤#轻微伤[^,，\\.。;；:：、]*?([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-7:轻伤人数
	 */
	info_minor_number : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?轻伤#鉴定[^;；\\.。]*?轻伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?轻伤#轻伤[^,，\\.。;；:：、]*?([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-8:多次敲诈
	 */
	info_blackmail_many_times : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(多|数|几|([一二三四五六七八九十两\\d]+))次[^,，\\.。;；:：]*敲诈|敲诈[^,，\\.。;；:：]*(多|数|几|([一二三四五六七八九十两\\d]+))次",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:为违法活动
	 */
	info_for_illegal_purpose : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(为|企图|目的|因)[^,，\\.。;；:：]*违法(活动|行为)|(用于|进行)[^,，;；\\.。]*?[赌|毒|嫖]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:非法手段获取他人隐私
	 */
	info_illegal_means_obtaining_privacy : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "非法手段获取他人隐私",
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
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(生活|学习|治病|医疗)所迫|为[^,，\\.。;；:：]*?(生活|学习|治病|医疗)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-12:敲诈近亲属财物
	 */
	info_extortion_close_relatives_property : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "敲诈(勒索)?(近)?亲属",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-13:造成被害人精神失常
	 */
	info_victim_mentally_disturbed : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "精神失常",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-14:一年内因敲诈勒索受过行政处罚
	 */
	info_administrative_penalty_racketeering : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "一年(之)?内([\\u4e00-\\u9fa5、]+)?因(为)?(敲诈|勒索)([\\u4e00-\\u9fa5、]+)?受(到)?过([\\u4e00-\\u9fa5、]+)?行政处罚",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
}