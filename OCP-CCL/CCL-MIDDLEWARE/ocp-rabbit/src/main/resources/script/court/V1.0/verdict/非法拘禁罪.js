var NAMESPACE_COURT_VERDICT_非法拘禁罪 = {
	// point-1:殴打、侮辱
	info_beat_insult : {
		params : [ {
			tagList : "court_opinion",
			regex : "殴打|侮辱|辱骂",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-2:国家公职人员利用职权
	info_gover_officials_use_authority : {
		params : [ {
			tagList : "court_opinion",
			regex : "利用(职权|职务之便)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-3:拘禁孕妇
	info_detention_pregnant : {
		params : [ {
			tagList : "court_opinion",
			regex : "孕妇|被害人[^,，\\.。;；:：]*孕|孕[^,，\\.。;；:：]*被害人",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-4:多次非法拘禁
	info_detention_multiple : {
		params : [ {
			tagList : "court_opinion",
			regex : "[多数]次[^,，\\.。;；:：]*?(拘禁|非法剥夺|参与)#拘禁[^,，\\.。;；:：]*?多次",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-5:冒充军警人员
	info_pretending_police_soldier : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion#plaintiff_args",
			regex : "(冒充|假冒|谎称)[^,，\\.。;；:：]*(部队|军人|警|公安)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-6:冒充司法人员
	info_pretending_judicial_officer : {
		params : [ {
			tagList : "court_opinion",
			regex : "(冒充|假冒|谎称)[^,，\\.。;；:：]*(警|法院|检察)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-7:持凶器
	info_hold_weapon : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "(持|携|携带|拿|用)(凶器|械|枪|作案工具|[^,，;；\\.。]*?刀)#(用|持|找出)[^,，;；\\.。]*?进行殴打",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-8:为索取非法债务
	info_claim_illegal_debt : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "索[取要](非法|高利贷)债务",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-9:传销
	info_pyramid_scheme : {
		params : [ {
			tagList : "court_opinion",
			regex : "传销",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			regex : "传销",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-10:为合法债务或权益
	info_legal_debt_rights : {
		params : [ {
			tagList : "court_opinion",
			regex : "合法(债务|权益)#[索讨][取要](债务|欠款)|索债",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-11:伤害后果
	info_damage_consequences : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "轻微伤#轻伤#重伤#死亡#(使|致)[^,，\\.。;；:：]*?残疾",
			reverseRegex : "轻微伤#轻伤#重伤#死亡#残疾",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	// point-12:伤亡人数
	info_minor_injury : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					regex : "(致|造成)[^,，\\.。;；:：、]*?轻微伤#鉴定[^;；\\.。]*?轻微伤",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?轻微伤#轻微伤[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-13:伤亡人数
	info_minor_number : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					regex : "(致|造成)[^,，\\.。;；:：、]*?轻伤#鉴定[^;；\\.。]*?轻伤",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?轻伤#轻伤[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-14:伤亡人数
	info_seriously_people : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					regex : "(致|造成)[^,，\\.。;；:：、]*?重伤#鉴定[^;；\\.。]*?重伤",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?重伤#重伤[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-15:伤亡人数
	info_death_toll : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					regex : "(致|造成)[^,，\\.。;；:：、]*?死亡",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?死亡#死亡[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-16:伤残等级
	info_minor_injury_level : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "([一二三四五六七八九十]+级)轻微伤#轻微伤[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			cacheKey : "meta_people_name2obj",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	// point-17:伤残等级
	info_minor_level : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "([一二三四五六七八九十]+级)轻伤#轻伤[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			cacheKey : "meta_people_name2obj",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	// point-18:伤残等级
	info_severity_disability_grade : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "([一二三四五六七八九十]+级)重伤#重伤[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			cacheKey : "meta_people_name2obj",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	// point-19:伤残等级
	info_disabled_level : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "([一二三四五六七八九十]+级)伤残#伤残[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			cacheKey : "meta_people_name2obj",
			capture : "\\1#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	// point-20:拘禁时间
	info_detention_time : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "(拘禁|长达|限制自由)[^,，\\.。;；:：、]*?(天|小时|分钟|日)#合计[^,，\\.。;；:：]*?(天|小时|分钟|日)",
			reverseRegex : "超过[^,，\\.。;；:：]*?小时",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "时间",
			unit : "天"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-21:拘禁人数
	info_detention_number : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					regex : "(限制|剥夺)[^,，\\.。;；:：]*?人身自由",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion#facts_found",
					regex : "拘禁[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)(人|名)#(限制|剥夺)[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人的人身自由",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	}
};
