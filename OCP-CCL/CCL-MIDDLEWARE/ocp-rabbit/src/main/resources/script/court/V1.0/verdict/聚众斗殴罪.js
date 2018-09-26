var NAMESPACE_COURT_VERDICT_聚众斗殴罪 = {
	/**
	 * point-1:伤害人数
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
	 * point-2:伤害人数
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
	 * point-3:伤害后果
	 */
	info_damage_consequences : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤",
			capture : "轻微伤#轻伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-4:因民间纠纷
	 */
	info_civil_dispute : {
		params : [ {
			tagList : "court_opinion",
			regex : "民间(纠纷|矛盾)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-5:组织未成年人聚众斗殴
	 */
	info_organize_minors : {
		params : [ {
			tagList : "court_opinion",
			regex : "(组织|纠集)未成年人[^,，\\.。;；:：]*?斗殴",
			reverseRegex : "未组织未成年人",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:财产损失较大
	 */
	info_property_damage : {
		params : [ {
			tagList : "court_opinion",
			regex : "(损失|数额)较大|较大(财[产物])?损失|损失严重"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-7:带有黑社会性质的
	 */
	info_black_social : {
		params : [ {
			tagList : "court_opinion",
			regex : "带有黑社会性质|(参加|领导)黑社会性质组织"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-8:斗殴人数
	 */
	info_fight_number : {
		params : [ {
			tagList : "court_opinion",
			regex : "([一二三四五六七八九十两\\d]+)人[^,，\\.。;；:：]*?斗殴",
			unit : "int"
		} ],
		method : "rule_func_extract_parse_number"
	},
	/**
	 * point-8:斗殴人数
	 */
	info_fights_number : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "斗殴[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)次#([一二三四五六七八九十两\\d]+)次[^,，\\.。;；:：]*?斗殴",
			meanWhile : "0",
			order : "1",
			defaultAll : "1",
			type : "其他",
			unit : "int"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-9:手段严重程度
	 */
	info_means_degree : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "交通秩序混乱|扰乱交通秩序",
			capture : "造成交通秩序混乱",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "扰乱社会秩序|恶劣社会影响|严重(破坏|妨害)(社会|公共)管理秩序",
			capture : "严重扰乱社会秩序，造成恶劣社会影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "多次[^,，\\.。;；:：]*?斗殴",
			capture : "多次聚众斗殴",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "斗殴[^,，\\.。;；:：]*?(人数多|规模大)",
			capture : "聚众斗殴人数多，规模大，社会影响恶劣",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "公共(场所|道路)|交通要道",
			capture : "在公共场所或者交通要道聚众斗殴，造成社会秩序严重混乱",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "持械|械斗",
			capture : "持械聚众斗殴",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	}
}