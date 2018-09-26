var NAMESPACE_COURT_VERDICT_非法持有毒品罪 = {
	// point-1:海洛因数量
	info_heroin_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "海洛因[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-2:甲基苯丙胺数量
	info_methamphetamine_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "甲基苯丙胺[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-3:可卡因数量
	info_cocaine_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "可卡因[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-4:鸦片数量
	info_opium_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "鸦片[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-5:美沙酮数量
	info_methadone_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "美沙酮[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-6:MDMA数量数量
	info_MDMA_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(3，4-亚甲二氧基甲基苯丙胺|MDMA)[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-7:吗啡数量数量
	info_morphine_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "吗啡[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-8:氯胺酮数量数量
	info_ketamine_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "氯胺酮[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-9:芬太尼数量数量
	info_fentanyl_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "芬太尼[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-10:甲卡西酮数量数量
	info_number_of_methadone : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "甲卡西酮[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-11:二氢埃托啡数量数量
	info_dihydroetorphine_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "二氢埃托啡[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-12:哌替啶数量
	info_degrees_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(哌替啶|度冷丁)[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-13:曲马多数量
	info_tramadol_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "曲马多[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-14:γ-羟丁酸数量
	info_hydroxybutyric_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "γ-羟丁酸[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-15:大麻油数量
	info_oil_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "大麻油[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-16:大麻脂数量
	info_cannabinoid_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "大麻脂[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-17:大麻叶|大麻烟数量
	info_cannabis_leaf_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(大麻叶|大麻烟)[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-18:可待因数量
	info_codeine_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "可待因[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-19:丁丙诺啡数量
	info_buprenorphine_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "丁丙诺啡[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-20:三唑仑数量
	info_quaalude_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "三唑仑[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-21:安眠酮数量
	info_alprazolam_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "安眠酮[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-22:阿普唑仑数量
	info_chartgrass_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "阿普唑仑[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-23:恰特草数量
	info_triazolam_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "恰特草[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-24:咖啡因数量
	info_caffeine_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "咖啡因[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-25:罂粟壳数量
	info_poppy_shell_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "罂粟壳[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-26:巴比妥数量
	info_barbitone_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "巴比妥[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-27:苯巴比妥数量
	info_phenobarbital_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "苯巴比妥[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-28:安钠咖数量
	info_nata_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "安钠咖[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-29:尼美西泮|甲硝安定数量
	info_nimetazepam_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(尼美西泮|甲硝安定)[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-30:氯氮卓数量
	info_chlorazonium_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "氯氮卓[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-31:艾司唑仑数量
	info_estazolam_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "艾司唑仑[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-32:地西泮数量
	info_diazepam_quantity : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "地西泮[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-33:溴西泮数量
	info_bromazepam_number : {
		params : [ {
			tagList : "facts_found#court_opinion#judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "溴西泮[^，,;；：:。]*?克",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	// point-34:毒品含量明显偏低
	info_drug_low_content : {
		params : [ {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "毒品[^,，;；：:\\.。]*?含量[\\u4e00-\\u9fae]*?低",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "毒品[^,，;；：:\\.。]*?含量[\\u4e00-\\u9fae]*?低",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-35:毒品种类
	info_drug_variety : {
		params : [ {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "海洛因",
			capture : "海洛因",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "甲基苯丙胺|冰毒",
			capture : "甲基苯丙胺",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "可卡因",
			capture : "可卡因",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "鸦片",
			capture : "鸦片",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "美沙酮",
			capture : "美沙酮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "3，4-亚甲二氧基甲基苯丙胺|MDMA",
			capture : "3，4-亚甲二氧基甲基苯丙胺",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "吗啡",
			capture : "吗啡",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "氯胺酮",
			capture : "氯胺酮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "芬太尼",
			capture : "芬太尼",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "甲卡西酮",
			capture : "甲卡西酮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "二氢埃托啡",
			capture : "二氢埃托啡",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "哌替啶|度冷丁",
			capture : "哌替啶",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "曲马多",
			capture : "曲马多",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "γ-羟丁酸",
			capture : "γ-羟丁酸",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "大麻油",
			capture : "大麻油",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "大麻脂",
			capture : "大麻脂",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "大麻叶|大麻烟",
			capture : "大麻叶及大麻烟",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "可待因",
			capture : "可待因",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "丁丙诺啡",
			capture : "丁丙诺啡",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "三唑仑",
			capture : "三唑仑",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "安眠酮",
			capture : "安眠酮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "阿普唑仑",
			capture : "阿普唑仑",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "恰特草",
			capture : "恰特草",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "咖啡因",
			capture : "咖啡因",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "罂粟壳",
			capture : "罂粟壳",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "巴比妥",
			capture : "巴比妥",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "苯巴比妥",
			capture : "苯巴比妥",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "安钠咖",
			capture : "安钠咖",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "尼美西泮|甲硝安定",
			capture : "尼美西泮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "氯氮卓",
			capture : "氯氮卓",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "艾司唑仑",
			capture : "艾司唑仑",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "地西泮",
			capture : "地西泮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "溴西泮",
			capture : "溴西泮",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-36:突发事件期间犯罪
	info_crime_while_emergency : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-37:在戒毒场所、监管场所非法持有毒品
	info_drug_place : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "在(戒毒|监管)场所非法持有毒品",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-38:利用、教唆未成年人
	info_drug_abetting_teenager : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(利用|教唆)未成年人",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-39:国家工作人员非法持有毒品
	info_drug_national_staff : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(身为|系|作为)(国家工作人员|国有事业单位)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},

	// point-40:被利用或被诱骗非法持有毒品
	info_drug_decoy : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "被利用或被诱骗非法持有毒品",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	// point-41:毒品再犯
	info_drug_again : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "毒品(犯罪)?再犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
};
