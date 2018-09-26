var NAMESPACE_COURT_JUDGEMENT_妨害公务罪 = {
	info_agitate_masses : {// point—1:煽动群众
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "煽动(群众|村民)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	info_violent_attacks : {// point—2:暴力袭击正在执行职务的人民警察
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(袭|[殴追]打|扔|暴力)[^,，;；\\.。]*?(警|公安)|(警|公安)[^,，;；\\.。]*?(殴打|伤|暴力)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	info_armed : {// point—3:持械
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "凶器|械|枪|作案工具|[^,，;；\\.。]*?刀|手持[^,，;；\\.。]*?",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	info_adverse_effects : {// point—4:造成恶劣社会影响
		params : [ {
			tagList : "court_opinion#office_opinion#plaintiff_args",
			cacheKey : "meta_people_name2obj",
			regex : "(恶劣|严重)的?(社会)?影响|影响[^,，;；\\.。]*(恶劣|极坏)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	info_affected_social_order : {// point—5:造成交通阻塞影响社会秩序
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "交通(堵|阻)塞|影响社会秩序"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	info_official_duties_not_standardized : {// point—6:执行公务不规范
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(执行公务|执法)[^,，\\.。;；:：]*?不规范",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	info_consequence : {// point—7:后果
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤",
			capture : "轻微伤#轻伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "损(毁|坏)(财物|[\\u4e00-\\u9fae]*?车)|(财[物产]|车)(损[毁坏失害]|毁损)",
			capture : "毁损财物",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "严重后果|后果严重",
			capture : "严重后果",
			reverseRegex : "(没|未|意识)[^,，\\.。;；:：、]*?严重后果",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	info_minor_injury : {// point—8:轻微伤人数
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
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?轻微伤#轻微伤[^,，\\.。;；:：、]*人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	info_minor_number : {// point—9:轻伤人数
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
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?轻伤#轻伤[^,，\\.。;；:：、]*人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	info_damage_amount : {// point—10:损毁财物数额
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "损(坏|毁)[^,，\\。;；:：]*?元#价值[^,，\\。;；:：]*?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "1",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	}
};
