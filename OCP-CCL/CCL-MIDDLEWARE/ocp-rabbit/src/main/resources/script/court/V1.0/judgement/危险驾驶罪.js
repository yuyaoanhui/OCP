var NAMESPACE_COURT_JUDGEMENT_危险驾驶罪 = {
	/**
	 * point-1:血液酒精浓度
	 */
	info_blood_alcohol_concentration : {
		params : [ {
			tagList : "facts_found_cmpl#office_opinion#facts_above#facts_found#court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "([\\d\\.·０１２３４５６７８９〇○Ｏ０OoΟ0О0l]+)(?:mg|ｍｇ|MG|毫克|㎎|ml|ML)[每/／\\-]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-2:追逐竞驶
	 */
	info_chase_race : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(相互|互相)追[赶逐]|追逐竞驶|追逐驾驶|飙车|竞驶|赛车|比赛|竞赛|追逐",
			reverseRegex : "根据《中华人民共和国刑法》第一百三十三条",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:超速
	 */
	info_speed : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "超速|超过规定时速|超过限速标志标明的最高时速|超过限速",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:从事校车业务或者旅客运输，超速超载
	 */
	info_transport_overspeed_loading : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(从事|驾驶)[\\u4e00-\\u9fa5]*?(校车|客车|大巴|巴士|出租车|旅客运输)+.*?(超速|超过规定时速|超载|超过额定乘员|超员)|超限额载客|超过额定人数运输旅客",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:驾驶载有乘客的营运机动车
	 */
	info_motor_vehicle_carrying_passengers : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "载有乘客|搭乘|载人|同乘|超过额定(乘员|人数)|超员|驾驶载客营运车辆|驾驶[^,，；;:：\\.。]*?客车载",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:违规运输危险化学品
	 */
	info_hazardous_chemicals : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(违反|违规).*?运输.*?((危险)?化学品|易燃|易爆|辐射|泄漏)|未取得[\\u4e00-\\u9fae]*?运输危险化学品资质",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:行驶路段
	 */
	info_travel_section : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "高速(公)?路",
			capture : "高速公路",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found#office_opinion#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "快速路",
			capture : "城市快速路",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found#office_opinion#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "铁(路)?([有无]人)?(看守)?道口",
			capture : "铁路道口",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found#office_opinion#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "特殊标志|窄路|窄桥|急弯路|掉头|转弯|下陡坡|傍山险路|连续下坡|连续弯路|路面结冰",
			capture : "设有明显道路安全提示标志的道路",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found#office_opinion#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "雾|[大暴]雨|雪|沙尘|冰雹|能见度[\\u4e00-\\u9fa5]*?50米",
			capture : "能见度在50米以内的不利气象条件",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-8:事故责任划分
	 */
	info_accident_responsibility : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "全部责任",
			capture : "全部责任",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		}, {
			tagList : "court_opinion#facts_found#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "主要责任",
			capture : "主要责任",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-9:吸毒后运输危险化学品
	 */
	info_drugs_hazardous_chemicals : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(吸毒|毒品).*?运输.*?(危险化学品|易燃|易爆|辐射|泄漏)",
			capture : "全部责任",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:轻伤人数
	 */
	info_minor_number : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?轻伤|鉴定[^;；\\.。]*?轻伤",
					capture : "全部责任",
					meanWhile : "0",
					order : "1",
					defaultAll : "1",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)[人名][^,，\\.。;；:：、]*?轻伤|轻伤[^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
					capture : "全部责任",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-11:财产损失数额
	 */
	info_property_loss_num : {
		params : [
				{
					tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl",
					cacheKey : "meta_people_name2obj",
					regex : "(损失|费用|车损价值|修复价格)[^,，。、；;]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "1",
					type : "金额"
				}, {
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(总|共|合计)[^,，。、;；]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "1",
					type : "金额"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-12:逃逸
	 */
	info_abscond : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "逃逸|逃跑|(离开|逃离)[^,，\\.。;；、]*?现场|继续驾车行驶",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-13:无证驾驶
	 */
	info_no_license : {
		params : [ {
			tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "无[证照](驾驶)?|未(依法)?取得([一-龥]+)?驾驶(资格|证)|无(有效)?(机动车)?驾驶(资格|证)|与(所持驾驶证)?[所准]驾车型不相?符||(没有|无)[\\u4e00-\\u9fae]*?(驾驶资质|资质驾驶)|未按照驾驶证载明的准驾车型驾驶",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-14:非法改装、拼装或已报废
	 */
	info_restructured_discarded : {
		params : [ {
			tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "已[被经]*?(报废|注销)|驾驶[^,，\\.。;；、]*?报废|([非违]法|[没未]登记)[\\u4e00-\\u9fa5]*?[改拼]装|拼装|私自改装|改装",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-15:伪造、故意遮挡或无牌证
	 */
	info_license_plate_problem : {
		params : [ {
			tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "([没无未遮挡污损]|伪造|变造)[^,，\\.。;；、]*?(牌照|号牌|牌证)|无牌|套牌|驾驶(未[\\u4e00-\\u9fae]*?(登记|注册)|[\\u4e00-\\u9fae]*?被注销)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-16:违反交通信号灯
	 */
	info_license_plate_problem : {
		params : [ {
			tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(违反|[未不]按|无视)[\\u4e00-\\u9fa5]*?信号(指示)?灯|闯[\\u4e00-\\u9fae]*?红灯|红灯禁止通行|遇黄灯闪烁|交通信号灯为红灯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-17:造成环境污染
	 */
	info_cause_environmental_pollution : {
		params : [ {
			tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "环境污染|污染环境|污染",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-18:驾驶客车类型
	 */
	info_passenger_vehicle_type : {
		params : [
				{
					tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "车",
					capture : "其他",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found_cmpl#office_opinion#facts_above#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "([大中小微])(?:[型轿客][\\u4e00-\\u9fa5]*?车|巴)",
					capture : "\\1型",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-19:曾因违反规定运输危险化学品受过刑事追究
	 */
	info_hazardous_chemicals_recidivism : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "[曾因].*?(危险化学品|易燃|易爆|辐射|泄漏).*?(逮捕|拘留|判处|刑事处罚)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-20:二年内曾因违反规定运输危险化学品被给予二次以上行政处罚
	 */
	info_hazardous_chemicals_recursor : {
		params : [
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "曾因[\\u4e00-\\u9fae]*?运输危险化学品(被行政处罚[两二]次|受过[两二]次行政处罚)",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				},
				{
					tagList : "defendant",
					cacheKey : "meta_people_name2obj",
					regex : "[两二]次因[\\u4e00-\\u9fae]*?运输危险物质被[\\u4e00-\\u9fae]*?(行政拘留|劳教|劳动教养)",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-21:超载人数
	 */
	info_overloaded_people : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl",
			cacheKey : "meta_people_name2obj",
			regex_limitAmount : "核定?载[\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两]+人",
			regex_totalAmount : "(载[乘客有]|实际?载|共计|[车承共]载|实际乘坐)[\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两]+[人名]",
			regex_overtopAmount : "超[载员][\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两]+人",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			resultFlag : "1",
			type : "其他",
			unit : "int"
		} ],
		method : "rule_func_extract_litigant_rate_info",
		adjust : [ {
			params : [ {
				tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl#facts_above",
				cacheKey : "meta_people_name2obj",
				regex : "([一二三四五六七八九十两\\d]+)[人名][^,，\\.。;；:：、]*?超载#超[出载员][^,，\\.。;；:：]*?([一二三四五六七八九十两\\d]+)人",
				meanWhile : "0",
				order : "1",
				defaultAll : "1",
				type : "其他",
				unit : "int"
			} ],
			method : "rule_func_extract_litigant_double_info"
		} ]
	},
	/**
	 * point-22:超速比例
	 */
	info_overspeed_ratio : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl",
			cacheKey : "meta_people_name2obj",
			regex_limitAmount : "限[速制][\\u4e00-\\u9fa5]*?[\\d\\.一二三四五六七八九十两]+(km|公里|千米)",
			regex_totalAmount : "(车速|时速|实速|行驶速度)[\\u4e00-\\u9fa5]*?[\\d\\.一二三四五六七八九十两]+(km|公里|千米)",
			regex_overtopAmount : "超速[\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两\\.]+(km|千米|公里)",
			meanWhile : "0",
			order : "1",
			defaultAll : "1",
			resultFlag : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_rate_info",
		adjust : [ {
			params : [ {
				tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl",
				cacheKey : "meta_people_name2obj",
				regex : "(超过[\\u4e00-\\u9fa5]*?速|超速)[\\u4e00-\\u9fa5]*?([\\d\\.]+)[%％]",
				meanWhile : "0",
				order : "1",
				defaultAll : "1",
				type : "其他"
			} ],
			method : "rule_func_extract_litigant_double_info"
		} ]
	},
	/**
	 * point-23:危险化学品超载比例
	 */
	info_hazardous_chemicals_overloading_ratio : {
		params : [ {
			tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl",
			cacheKey : "meta_people_name2obj",
			regex_limitAmount : "核定?载[\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两\\.]+(kg|公斤|吨|KG)",
			regex_totalAmount : "([总共计]|实际?载|运载)[\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两\\.]+(kg|公斤|吨|KG)",
			regex_overtopAmount : "(超载|超出核定载质量)[\\u4e00-\\u9fa5]*?[\\d一二三四五六七八九十两\\.]+(kg|公斤|吨|KG)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			resultFlag : "0",
			type : "其他"
		} ],
		method : "rule_func_extract_litigant_rate_info",
		adjust : [ {
			params : [ {
				tagList : "court_opinion#facts_found#office_opinion#facts_found_cmpl",
				cacheKey : "meta_people_name2obj",
				regex : "(超载|超出核定载质量)[\\u4e00-\\u9fa5]*?[\\d\\.]+[%％]",
				meanWhile : "0",
				order : "1",
				defaultAll : "1",
				type : "其他"
			} ],
			method : "rule_func_extract_litigant_double_info"
		} ]
	}
}