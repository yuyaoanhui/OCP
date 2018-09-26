var NAMESPACE_COURT_VERDICT_强奸罪 = {
	/**
	 * point-1:奸淫多次
	 */
	info_adultery_times : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(奸淫|强奸)[^,，\\.。;；:：]*?多次#多次[^,，\\.。;；:：]*?(发生性(关系|行为)|奸淫|强奸)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:轮奸多次
	 */
	info_gang_rape_times : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "轮奸多次|多次轮奸",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:携带凶器
	 */
	info_arm_associatbd : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(持|携|携带|拿|用)(凶器|械|枪|作案工具|[^,，;；\\.。]*?刀)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:进入未成年人住所
	 */
	info_enter_minor_residence : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "进入未成年[^,，\\.。;；:：]*住所"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-5:进入学校集体宿舍
	 */
	info_enter_school_dormitory : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "进入(学校|学生)集体宿舍"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-6:非法拘禁、捆绑、侮辱、虐待
	 */
	info_illegal_detention : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "非法(拘禁|限制他人人身自由)|捆绑|侮辱|虐待",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:强奸幼女
	 */
	info_carnal_abuse : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "幼女#(未|不)满十四周岁",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:强奸残疾人
	 */
	info_rapist : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "残疾人|精神异常|痴呆患?者|患精神病的妇女",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:伤害后果
	 */
	info_damage_consequences : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤#重伤",
			capture : "轻微伤#轻伤#重伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(致|使)[^,，\\.。;；:：]*?残疾",
			capture : "残疾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-10:伤害人数
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
	 * point-11:伤害人数
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
	 * point-12:重伤人数
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
	 * point-13:强奸人数
	 */
	info_rape_number : {
		params : [
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "强奸|奸淫|发生性关系",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(强奸|奸淫)[^,，\\.。;；:：]*([一二三四五六七八九十两\\d]+)人#([一二三四五六七八九十两\\d]+)(名|人)[^,，\\.。;；:：]*?(发生性关系|奸淫)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-14:情节恶劣程度
	 */
	info_plot_severity : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "情节恶劣|(幼女|未成年人)怀孕引产",
			capture : "强奸妇女、奸淫幼女情节恶劣",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(强奸|奸淫)[^,，;；\\.。]*多[人名]",
			capture : "强奸妇女、奸淫幼女多人",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "公共场所",
			capture : "在公共场所当众强奸妇女",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "轮奸",
			capture : "二人以上轮奸",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "重伤|死亡|自杀|严重后果|身心造成[\\u4e00-\\u9fae]*?伤害",
			capture : "致使被害人重伤、死亡或者造成其他严重后果",
			reverseRegex : "(放弃|避免|中止|未|没有)[^,，\\.。;；:：]*?严重后果",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-15:利用特殊关系
	 */
	info_special_relationships : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(教养|监护|职务|养父养女|父女)[^,，;；\\.。]*关系",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-16:利用特殊身份
	 */
	info_special_identity : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "共同((家庭)?生活|居住)|对未成年人具有特殊职责|国家工作人员",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-17:强制手段
	 */
	info_compulsory_means : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "暴力|胁迫|威胁|强迫|麻醉|迷晕|恐吓|恫吓|殴打",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-18:强奸弱势群体
	 */
	info_vulnerable_group : {
		params : [
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "农村留守儿童|严重残疾的未成年人|(精神(发育迟滞|残疾)|智能障碍|智力(障碍|残疾|发育迟滞))[^,，;；\\.。]*?(未成年人|儿童|幼女)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(不|未)满(十二|12)周岁[^,，;；\\.。]*?(儿童|幼女)|[^\\d一二三四五六七八九十]((10|11|十一)|[一二三四五六七八九十]|[1-9])(周)?岁",
					reverseRegex : "儿子[（(][一二三四五六七八九十\\d]+(周)?岁[)）]",
                    meanWhile : "0",
                    order : "1",
                    defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-19:轻微伤伤残等级
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
	 * point-20:轻伤伤残等级
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
	 * point-21:重伤伤残等级
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
	 * point-22:伤残等级
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
};