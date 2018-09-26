var NAMESPACE_PROCURATORATE_APPEALARREST_交通肇事罪 = {
	/**
	 * point-1:无牌证或已报废
	 */
	info_no_plate : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "已[被经]*?(报废|注销)|驾驶[^,，\\.。;；、]*?报废|(没有|无|未)[^,，\\.。;；、]*?(牌照|号牌|牌证)|无牌",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "已[被经]*?(报废|注销)|驾驶[^,，\\.。;；、]*?报废|(没有|无|未)[^,，\\.。;；、]*?(牌照|号牌|牌证)|无牌",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:被害人有过错
	 */
	info_defendant_wrongdoing : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(醉酒|无证|次要责任|横穿马路)|(醉酒|无证)[^,，;；\\.。据]*?([被受])害人"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-3:积极施救
	 */
	info_active_rescue : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(抢救|施救|救治|护送|急救|救护)(?!费)",
			reverseRegex : "(不|没有|未|应)[^\\.。;；、]*?(抢救|施救|救治|护送|急救|救护)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:车辆不安全
	 */
	info_unsafe_vehicle : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "(未|没有|无)[^,，\\.。;；、]*?(年检|安检|年审|检验|检测)|车有故障|车辆[一-龥]*?安全隐患|已过报废期限|安全(性能|技术|状况)+?不合格|安全(装置|设施)不全|制动[^,，\\.。;；、]*?(不良|不合格)|未投保交通事故责任强制保险|不符合[^,，\\.。;；、]*?(规定|标准|条件|要求)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "(未|没有|无)[^,，\\.。;；、]*?(年检|安检|年审|检验|检测)|车有故障|车辆[一-龥]*?安全隐患|已过报废期限|安全(性能|技术|状况)+?不合格|安全(装置|设施)不全|制动[^,，\\.。;；、]*?(不良|不合格)|未投保交通事故责任强制保险|不符合[^,，\\.。;；、]*?(规定|标准|条件|要求)",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:因逃逸致人死亡
	 */
	info_abscond_death : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "(逃逸|逃跑|溜走|逃离|离开)[^\\.。;；、]*?死亡|死亡[^\\.;；、]*?(逃逸|逃跑|溜走|逃离|离开)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "(逃逸|逃跑|溜走|逃离)[^\\.。;；、]*?死亡",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:逃逸
	 */
	info_abscond : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "逃逸|逃跑|(离开|逃离)[^,，\\.。;；、]*?现场",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "逃逸|逃跑|(离开|逃离)[^,，\\.。;；、]*?现场",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:无证驾驶
	 */
	info_no_license : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "无[证照](驾驶)?|未(依法)?取得([一-龥]+)?驾驶(资格|证)|无(机动车)?驾驶(资格|证)|与[所准]驾车型不符",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "无[证照](驾驶)?|未(依法)?取得([一-龥]+)?驾驶(资格|证)|无(机动车)?驾驶(资格|证)|与[所准]驾车型不符",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:酒驾
	 */
	info_drunk_driving : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "酒驾|酒后|喝酒|醉酒|饮酒|醉驾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "酒驾|酒后|喝酒|醉酒|饮酒|醉驾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:毒驾
	 */
	info_drug_associated : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "(驾驶|驾车|开车)[^,，\\.。;；]*?(吸毒|毒品)|(吸毒|毒品)[^,，\\.。;；]*?(驾驶|驾车|开车)|毒驾",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "(驾驶|驾车|开车)[^,，\\.。;；]*?(吸毒|毒品)|(吸毒|毒品)[^,，\\.。;；]*?(驾驶|驾车|开车)|毒驾",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:财产损失数额
	 */
	info_property_loss_num : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(损失|费用)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		}, {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(总|共|合计)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		}, {
			tagList : "HANDLE_OPINION",
			regex : "(损失|费用)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		}, {
			tagList : "HANDLE_OPINION",
			regex : "(总|共|合计)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-11:事故责任划分
	 */
	info_accident_responsibility : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "全部责任#主要责任#同等责任",
			capture : "全部责任#主要责任#同等责任",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "全部责任#主要责任#同等责任",
			capture : "全部责任#主要责任#同等责任",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-12:重伤人数
	 */
	info_seriously_people : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?重伤#鉴定[^;；\\.。]*?重伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?重伤#重伤[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?重伤#鉴定[^;；\\.。]*?重伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "HANDLE_OPINION",
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
	 * point-13:死亡人数
	 */
	info_death_toll : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；、]*?死亡",
					reverseRegex : "(未|没有|无)[^,，\\.。;；、]*?死亡",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；、]*?死亡#死亡[^,，\\.。;；、]*([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；、]*?死亡",
					reverseRegex : "(未|没有|无)[^,，\\.。;；、]*?死亡",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；、]*?死亡#死亡[^,，\\.。;；、]*([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-14:超载
	 */
	info_overload : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "超载",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "超载",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-15:造成恶劣影响
	 */
	info_bad_impact : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "影响[^,，\\.。;；、不]*?恶劣|恶劣[^,，\\.。;；、不]*?影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "恶劣影响",
			reverseRegex : "[不没无][^,，;；\\.。]*?影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
};