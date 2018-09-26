var NAMESPACE_COURT_JUDGEMENT_抢劫罪 = {
	/**
	 * point-1:公共场所
	 */
	info_public_place : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "公共场所",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:因生活所迫学习治病急需
	 */
	info_life_purpose : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "生活所迫|经济(拮据|困难)|没钱|(为)[^\\.。;；]*?(学习|上学)|(为|钱)[^\\.。;；]*?治病",
			reverseRegex : "行为影响[^\\.。;；,，：:、]*?(学习|治病|生活)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:抢劫家庭成员
	 */
	info_rob_family_members : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(抢|劫|是|即|系)[^,，\\.。;；:：]*(其)(家庭成员|家人|父|母|夫|妻)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:抢劫近亲属
	 */
	info_close_relatives : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "抢劫(近|)亲属",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:持凶器
	 */
	info_hold_weapon : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(持|携|携带|拿|用)(凶器|械|枪|作案工具|[^,，;；\\.。]*?刀)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
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
	 * point-6:情节严重程度
	 */
	info_plot_degree : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "入户|入室",
			capture : "入户",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "公共交通工具",
			capture : "公共交通工具",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "抢劫金融机构",
			capture : "抢劫金融机构",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(军用|抢险|救灾|救济)物资",
			capture : "抢劫军用物资或者抢险、救灾、救济物资",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(冒充|假冒|谎称)[^,，\\.。;；:：]*(军|警|公安)",
			capture : "冒充军警",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "凶器|械|枪|作案工具|[^,，;；\\.。]*?刀",
			capture : "使用凶器",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "威胁|胁迫",
			capture : "以凶器相威胁",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "轻伤|重伤|死亡",
			capture : "使用暴力致人轻微伤以上",
			reverseRegex : "未[^,，\\.。;；:：]*(轻伤|重伤|死亡)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-7:抢劫次数
	 */
	info_robbery_counts : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "抢劫[^,，;；\\.。]*?次#[^,，;；\\.。]*?(次|起)[^,，;；\\.。]*?劫",
			capture : "轻微伤#轻伤#重伤#残疾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "次"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-8:抢劫数额
	 */
	info_rob_amount : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "价值[^,，;；\\.。]*?元#抢[^,，;；\\.。]*?元#折(款|合人民币)[^,，;；\\.。]*?元",
			capture : "轻微伤#轻伤#重伤#残疾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-9:轻微伤人数
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
	 * point-10:轻伤人数
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
	 * point-11:重伤人数
	 */
	info_seriously_people : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?重伤#鉴定[^;；\\.。]*?重伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?重伤#重伤[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-12:伤残等级-轻微伤
	 */
	info_minor_injury_level : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([一二三四五六七八九十]+级)轻微伤#轻微伤[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-13:伤残等级-轻伤
	 */
	info_minor_level : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([一二三四五六七八九十]+级)轻伤#轻伤[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-14:伤残等级-重伤
	 */
	info_severity_disability_grade : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([一二三四五六七八九十]+级)重伤#重伤[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-15:伤残等级-伤残
	 */
	info_disabled_level : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([一二三四五六七八九十]+级)伤残#伤残[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	}
}