var NAMESPACE_COURT_VERDICT_交通肇事罪 = {
	/**
	 * point-1:无牌证或已报废
	 */
	info_no_plate : {
		params : [
				{
					tagList : "facts_found_cmpl",
					cacheKey : "meta_people_name2obj",
					regex : "已[被经]*?(报废|注销)|驾驶[^,，\\.。;；、]*?报废|(没有|无|未)[^,，\\.。;；、]*?(牌照|号牌|牌证)|无牌",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "facts_above",
					cacheKey : "meta_people_name2obj",
					regex : "已[被经]*?(报废|注销)|驾驶[^,，\\.。;；、]*?报废|(没有|无|未)[^,，\\.。;；、]*?(牌照|号牌|牌证)|无牌",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "已[被经]*?(报废|注销)|驾驶[^,，\\.。;；、]*?报废|(没有|无|未)[^,，\\.。;；、]*?(牌照|号牌|牌证)|无牌",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
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
		params : [
				{
					tagList : "plaintiff_args#office_opinion",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(醉酒|无证|次要责任|横穿马路)|(醉酒|无证)[^,，;；\\.。据]*?([被受])害人"
				},
				{
					tagList : "facts_above",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(醉酒|无证|次要责任|横穿马路)|(醉酒|无证)[^,，;；\\.。据]*?([被受])害人"
				},
				{
					tagList : "facts_found",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(醉酒|无证|次要责任|横穿马路)|(醉酒|无证)[^,，;；\\.。据]*?([被受])害人"
				},
				{
					tagList : "court_opinion",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(醉酒|无证|次要责任|横穿马路)|(醉酒|无证)[^,，;；\\.。据]*?([被受])害人"
				}, {
					tagList : "judgement_content",
					regex : "([受被]害人)[^,，;；\\.。]*?(有|存在|负|承担)[^,，;；\\.。]*?(责任)"
				} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-3:被害人信息
	 */
	info_victim_old : {
		params : [ {
			tagList : "facts_above",
			regex : "(老(年)?人|[6-9][0-9]岁|享年[6-9][0-9]|年[龄纪]大)[^;；\\.。]*?(([被受])害人|死者|伤者)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-4:积极施救
	 */
	info_active_rescue : {
		params : [ {
			tagList : "facts_above#facts_found_cmpl",
			regex : "(抢救|施救|救治|护送)(?!费)",
			cacheKey : "meta_people_name2obj",
			reverseRegex : "(不|没有|未|应)[^\\.。;；、]*?(抢救|施救|救治)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			regex : "(抢救|施救|救治|护送|急救|救护)(?!费)",
			cacheKey : "meta_people_name2obj",
			reverseRegex : "(不|没有|未|应)[^\\.。;；、]*?(抢救|施救|救治|护送|急救|救护)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "(抢救|施救|救治|护送|急救|救护)(?!费)",
			cacheKey : "meta_people_name2obj",
			reverseRegex : "(不|没有|未|应)[^\\.。;；、]*?(抢救|施救|救治|护送|急救|救护)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:车辆不安全
	 */
	info_unsafe_vehicle : {
		params : [
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(未|没有|无)[^,，\\.。;；、]*?(年检|安检|年审|检验|检测)|车有故障|车辆[一-龥]*?安全隐患|已过报废期限|安全(性能|技术|状况)+?不合格|安全(装置|设施)不全|制动[^,，\\.。;；、]*?(不良|不合格)|未投保交通事故责任强制保险|不符合[^,，\\.。;；、]*?(规定|标准|条件|要求)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(未|没有|无)[^,，\\.。;；、]*?(年检|安检|年审|检验|检测)|车有故障|车辆[一-龥]*?安全隐患|已过报废期限|安全(性能|技术|状况)+?不合格|安全(装置|设施)不全|制动[^,，\\.。;；、]*?(不良|不合格)|未投保交通事故责任强制保险|不符合[^,，\\.。;；、]*?(规定|标准|条件|要求)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:因逃逸致人死亡
	 */
	info_abscond_death : {
		params : [
				{
					tagList : "facts_found#plaintiff_args#office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(逃逸|逃跑|溜走|逃离|离开)[^\\.。;；、]*?死亡|死亡[^\\.;；、]*?(逃逸|逃跑|溜走|逃离|离开)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(逃逸|逃跑|溜走|逃离)[^\\.。;；、]*?死亡",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:逃逸
	 */
	info_abscond : {
		params : [ {
			tagList : "facts_found#plaintiff_args#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "逃逸|逃跑|(离开|逃离)[^,，\\.。;；、]*?现场",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "逃逸|逃跑|(离开|逃离)[^,，\\.。;；、]*?现场",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:无证驾驶
	 */
	info_no_license : {
		params : [
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "无[证照](驾驶)?|未(依法)?取得([一-龥]+)?驾驶(资格|证)|无(机动车)?驾驶(资格|证)|与[所准]驾车型不符",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "无[证照](驾驶)?|未(依法)?取得([一-龥]+)?驾驶(资格|证)|无(机动车)?驾驶(资格|证)|与[所准]驾车型不符",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:酒驾
	 */
	info_drunk_driving : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "酒驾|酒后|喝酒|醉酒|饮酒|醉驾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "酒驾|酒后|喝酒|醉酒|饮酒|醉驾",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:毒驾
	 */
	info_drug_associated : {
		params : [
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(驾驶|驾车|开车)[^,，\\.。;；]*?(吸毒|毒品)|(吸毒|毒品)[^,，\\.。;；]*?(驾驶|驾车|开车)|毒驾",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(驾驶|驾车|开车)[^,，\\.。;；]*?(吸毒|毒品)|(吸毒|毒品)[^,，\\.。;；]*?(驾驶|驾车|开车)|毒驾",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:财产损失数额
	 */
	info_property_loss_num : {
		params : [ {
			tagList : "facts_found#facts_found_cmpl",
			regex : "(损失|费用)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		}, {
			tagList : "facts_found#facts_found_cmpl",
			regex : "(总|共|合计)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		}, {
			tagList : "court_opinion",
			regex : "(损失|费用)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		}, {
			tagList : "court_opinion",
			regex : "(总|共|合计)[^,，\\.。、]*?([\\d\\.]+)(万)?元",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-12:事故责任划分
	 */
	info_accident_responsibility : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "全部责任",
			capture : "全部责任",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "主要责任",
			capture : "主要责任",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(共同|同等)责任",
			capture : "(共同|同等)责任",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-13:重伤人数
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
	 * point-14:死亡人数
	 */
	info_death_toll : {
		params : [
				{
					tagList : "court_opinion#facts_found",
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
					tagList : "court_opinion",
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
	 * point-15:超载
	 */
	info_overload : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "超载",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-16:无能力赔偿
	 */
	info_compensate_unable : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(无法|未能|无(能)?力|不足以|不具有)[^,，\\.。;；、]*?(赔款|赔偿|偿还|赔付)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-17:造成恶劣影响
	 */
	info_bad_impact : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "影响[^,，\\.。;；、不]*?恶劣|恶劣[^,，\\.。;；、不]*?影响",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-18:重大财产损失
	 */
	info_property_severe_loss : {
		params : [ {
			tagList : "court_opinion",
			regex : "大[^,，\\.。;；、不]*?财产损失|重大损失|损失[重巨]大",
			reverseRegex : "(未|没有|无)[^,，\\.。;；、不]*?重大损失"
		} ],
		method : "rule_func_judge_truth_by_regex"
	}
};