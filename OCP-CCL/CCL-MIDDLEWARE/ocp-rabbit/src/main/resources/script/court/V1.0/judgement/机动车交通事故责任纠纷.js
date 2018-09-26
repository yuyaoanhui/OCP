var NAMESPACE_COURT_JUDGEMENT_机动车交通事故责任纠纷 = {
	/**
	 * point-1:索要赔偿金额
	 */
	info_traffic_request_amount : {
		params : [ {
			tagList : "plaintiff_args",
			regex : "([合共总]计|赔款)[^,，;；：:。]*?元",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-2:投保类型
	 */
	info_insurance_type : {
		params : [
				{
					tagList : "plaintiff_args",
					regex : "(交强险|强制(保)?险)",
					capture : "交强险",
					reverseRegex : "[未没][\\u4e00-\\u9fa5]*(交强险|强制(保)?险)"
				},
				{
					tagList : "plaintiff_args",
					regex : "(约定)?不计免赔[^率]",
					capture : "不计免赔商业险",
					reverseRegex : "[无没未][\\u4e00-\\u9fa5]*不计免赔"
				},
				{
					tagList : "plaintiff_args",
					regex : "(商业|责任(保)?险|三责(保)?险|三者(保)?险)",
					capture : "商业险",
					reverseRegex : "不计免赔"
				},
				{
					tagList : "facts_found#facts_found_cmpl#facts_found_base#plaintiff_args",
					regex : "(交强险|强制(保)?险)",
					reverseRegex : "[未没][\\u4e00-\\u9fa5]*(交强险|强制(保)?险)",
					capture : "交强险"
				},
				{
					tagList : "facts_found#facts_found_cmpl#facts_found_base#plaintiff_args",
					regex : "(约定)?不计免赔[^率]",
					reverseRegex : "[无没未][\\u4e00-\\u9fa5]*不计免赔",
					capture : "不计免赔商业险",
				},
				{
					tagList : "facts_found#facts_found_cmpl#facts_found_base#plaintiff_args",
					regex : "(商业|责任(保)?险|三责(保)?险|三者(保)?险)",
					capture : "商业险",
					reverseRegex : "不计免赔"
				}, {
					tagList : "court_opinion",
					regex : "(交强险|强制(保)?险)",
					capture : "交强险",
					reverseRegex : "[未没][\\u4e00-\\u9fa5]*(交强险|强制(保)?险)"
				}, {
					tagList : "court_opinion",
					regex : "(约定)?不计免赔[^率]",
					capture : "不计免赔商业险",
					reverseRegex : "[无没未][\\u4e00-\\u9fa5]*不计免赔"
				}, {
					tagList : "court_opinion",
					regex : "(商业|责任(保)?险|三责(保)?险|三者(保)?险)",
					capture : "商业险",
					reverseRegex : "不计免赔"
				} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-3:住院时长
	 */
	info_hospital_duration : {
		params : [ {
			tagList : "facts_found",
			regex : "[\\d]+年[\\d]+月[\\d]+日至[\\d]+年[\\d]+月[\\d]+日住院",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "facts_found",
			regex : "住院伙食补助费[^,，;；。]*?[（(][^,，;；。]*?([\\d]+天)[)）]",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "facts_found",
			regex : "住院[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?(月|星期))",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "court_opinion",
			regex : "[\\d]+年[\\d]+月[\\d]+日至[\\d]+年[\\d]+月[\\d]+日住院",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "court_opinion",
			regex : "住院伙食补助费[^,，;；。]*?[（(][^,，;；。]*?([\\d]+天)[)）]",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "court_opinion",
			regex : "住院[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?(月|星期))",
            reverse : "0",
			unit : "日"
		} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-4:护理期
	 */
	info_care_duration : {
		params : [ {
			tagList : "facts_found#facts_found_secondary#facts_found_cmpl",
			regex : "护理费[^,，；;。]*?[（(][^,，;；。]*?([\\d]+天)[)）]",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "facts_found#facts_found_secondary#facts_found_cmpl",
			regex : "护理[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?(月|星期))",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "court_opinion",
			regex : "护理费[^,，；;。]*?[（(][^,，;；。]*?([\\d]+天)[)）]",
            reverse : "0",
			unit : "日"
		}, {
			tagList : "court_opinion",
			regex : "护理[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?(月|星期))",
            reverse : "0",
			unit : "日"
		} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-5:误工费用
	 */
	info_job_delay_fee : {
		params : [ {
			tagList : "facts_found",
			regex : "误工费[^,，;；。]*?元",
			unit : "元"
		}, {
			tagList : "court_opinion",
			regex : "误工费[^,，;；。]*?元",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-6:误工时长
	 */
	info_job_delay_duration : {
		params : [
				{
					tagList : "facts_found#facts_found_cmpl",
					regex : "误工期限[自应][\\d]+年[\\d]+月[\\d]+日[\\u4e00-\\u9fa5]*?至[\\d鉴定]+年[\\d]+月[\\d]+日",
					defaultVal : "0",
					unit : "日"
				},
				{
					tagList : "facts_found#facts_found_cmpl",
					regex : "误工费[^,，；;。]*?[（(][^,，;；。]*?([\\d]+天)[^,，;；。]*?[)）]",
					defaultVal : "0",
					unit : "日"
				},
				{
					tagList : "facts_found#facts_found_cmpl",
					regex : "误工[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?(月|星期))",
					defaultVal : "0",
					unit : "日"
				},
				{
					tagList : "court_opinion",
					regex : "误工期限[自|应][\\d]+年[\\d]+月[\\d]+日[\\u4e00-\\u9fa5]*?至[\\d|鉴定]+年[\\d]+月[\\d]+日",
					defaultVal : "0",
					unit : "日"
				},
				{
					tagList : "court_opinion",
					regex : "误工费[^,，；;。]*?[（(][^,，;；。]*?([\\d]+天)[^,，;；。]*?[)）]",
					defaultVal : "0",
					unit : "日"
				},
				{
					tagList : "court_opinion#court_primary_opinion",
					regex : "误工[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?月|星期)",
					defaultVal : "0",
					unit : "日"
				} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-6:营养期
	 */
	info_nutrition_period : {
		params : [ {
			tagList : "facts_found",
			regex : "营养费[^,，；;。]*?[（(][^,，;；。]*?([\\d]+天)[)）]",
			defaultVal : "0",
			unit : "日"
		}, {
			tagList : "facts_found",
			regex : "营养期[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?(月|星期))",
			defaultVal : "0",
			unit : "日"
		}, {
			tagList : "court_opinion",
			regex : "营养费[^,，；;。]*?[（(][^,，;；。]*?([\\d]+天)[)）]",
			defaultVal : "0",
			unit : "日"
		}, {
			tagList : "court_opinion#court_primary_opinion",
			regex : "营养期[\\u4e00-\\u9fa5]*?[一二三四五六七八九十两\\d]+([天周日]|(个)?月|星期)",
			defaultVal : "0",
			unit : "日"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-7:受害总人数
	 */
	info_number_victiom : {
		params : [
				{
					tagList : "facts_found",
					regex : "([０１２３４５６７８９1234567890一二三四五六七八九〇○Ｏ０OoΟ零两个十百千万]+)人(当场|抢救无效)?(死亡|受伤|重伤)",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					regex : "([０１２３４５６７８９1234567890一二三四五六七八九〇○Ｏ０OoΟ零两个十百千万]+)人(当场|抢救无效)?(死亡|受伤|重伤)",
					unit : "人"
				} ],
		method : "rule_func_extract_parse_number"
	},
	/**
	 * point-8:医嘱休息时长
	 */
	info_rest_duration : {
		params : [ {
			tagList : "facts_found",
			regex : "(休息|全休|卧[\\u4e00-\\u9fa5]*?床)[^,，;；:：\\.。]*?([天周月年日]|星期)",
            reverse : "0",
            unit : "日"
		} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-9:挂靠协议
	 */
	info_coutract_anchor : {
		params : [ {
			tagList : "facts_found",
			regex : "挂户|挂靠"
		}, {
			tagList : "court_opinion",
			regex : "挂户|挂靠"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-10:伤残等级
	 */
	info_disabled_level : {
		params : [ {
			tagList : "court_opinion",
			regex : "([一二三四五六七八九十]+级)伤残",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "伤残[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			capture : "\\1"
		}, {
			tagList : "facts_found",
			regex : "[×Xx]级伤残",
			capture : "十级"
		}, {
			tagList : "facts_found",
			regex : "伤残[^,，\\.。;；:：]*?[×Xx]级",
			capture : "十级"
		}, {
			tagList : "court_opinion",
			regex : "([一二三四五六七八九十]+级)伤残",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "伤残[^,，\\.。;；:：]*?([一二三四五六七八九十]+级)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "[×Xx]级伤残",
			capture : "十级"
		}, {
			tagList : "court_opinion",
			regex : "伤残[^,，\\.。;；:：]*?[×Xx]级",
			capture : "十级"
		} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-12:死亡人数
	 */
	info_death_toll : {
		params : [
				{
					tagList : "facts_found",
					regex : "([０１２３４５６７８９1234567890一二三四五六七八九〇○Ｏ０OoΟ零两个十百千万]+)人(当场|抢救无效)?死",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					regex : "([０１２３４５６７８９1234567890一二三四五六七八九〇○Ｏ０OoΟ零两个十百千万]+)人(当场|抢救无效)?死",
					unit : "人"
				} ],
		method : "rule_func_extract_parse_number"
	},
	/**
	 * point-13:造成人员死亡
	 */
	info_is_man_dead : {
		params : [ {
			tagList : "facts_found",
			regex : "(致|造成|抢救无效)[^,，；;\\.。]*?死亡|当场死亡"
		}, {
			tagList : "court_opinion",
			regex : "(致|造成|抢救无效)[^,，；;\\.。]*?死亡|当场死亡"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-14:肇事者与车主关系
	 */
	info_perpetrators_relation : {
		params : [
				{
					tagList : "facts_found",
					regex : "(本人所有|[为系]其本人|[为是系][\\u4e00-\\u9fa5]{2,3}本人|[为系][\\u4e00-\\u9fa5]+车主)|所有人为被告",
					capture : "本人"
				},
				{
					tagList : "facts_found",
					regex : "挂靠",
					capture : "挂靠"
				},
				{
					tagList : "facts_found",
					regex : "雇佣",
					capture : "雇佣"
				},
				{
					tagList : "facts_found",
					regex : "借用",
					capture : "借用",
					type : "其他"
				},
				{
					tagList : "court_opinion",
					regex : "(本人所有|[为系]其本人|[为是系][\\u4e00-\\u9fa5]{2,3}本人|[为系][\\u4e00-\\u9fa5]+车主)|所有人为被告",
					capture : "本人"
				}, {
					tagList : "court_opinion",
					regex : "挂靠",
					capture : "挂靠"
				}, {
					tagList : "court_opinion",
					regex : "雇佣",
					capture : "雇佣"
				}, {
					tagList : "court_opinion",
					regex : "借用",
					capture : "借用",
					type : "其他"
				} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-15:赔偿标准
	 */
	info_compensation_standard : {
		params : [
				{
					tagList : "facts_found#facts_found_cmpl",
					regex : "[系属是为][^,，\\.。；;:：非]*?(农业|农村)(家庭)?(户口|居民)|农[民村]标准",
					capture : "农村标准"
				},
				{
					tagList : "facts_found#facts_found_cmpl",
					regex : "[系属是为]?(城镇|非农业|非农)(家庭)?(户口|居民)|城镇(标准|居民)",
					capture : "城镇标准"
				},
				{
					tagList : "facts_found#facts_found_cmpl",
					regex : "((请求|要求|主张)[^,，；;\\.。]*?城镇(居民的?)?标准[^!！。.；;？?不]*?(支持|准许|采信|采纳|确认|计算|适用))|((按|适用|比照|参考|按照|以)[^,，；;\\.。]*?城镇(居民的?)?(标准|人均))|按[\\u4e00-\\u9fa5]*?经常居住地[\\u4e00-\\u9fa5]*?标准",
					capture : "按城镇标准"
				},
				{
					tagList : "court_opinion",
					regex : "[系属是为][^,，\\.。；;:：非]*?(农业|农村)(家庭)?(户口|居民)|农[民村]标准",
					capture : "农村标准"
				},
				{
					tagList : "court_opinion",
					regex : "[系属是为]?(城镇|非农业|非农)(家庭)?(户口|居民)|城镇(标准|居民)",
					capture : "城镇标准"
				},
				{
					tagList : "court_opinion",
					regex : "((请求|要求|主张)[^,，；;\\.。]*?城镇(居民的?)?标准[^!！。.；;？?不]*?(支持|准许|采信|采纳|确认|计算|适用))|((按|适用|比照|参考|按照|以)[^,，；;\\.。]*?城镇(居民的?)?(标准|人均))|按[\\u4e00-\\u9fa5]*?经常居住地[\\u4e00-\\u9fa5]*?标准",
					capture : "按城镇标准"
				} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-16:伤情选择
	 */
	info_injury_part : {
		params : [ {
			tagList : "facts_found",
			regex : "死亡",
			reverseRegex : "未(造成|导致)[^,，；;\\.。]*?死亡",
			capture : "死亡"
		}, {
			tagList : "facts_found",
			regex : "伤残",
			reverseRegex : "未(造成|导致|构成)[^,，；;\\.。]*?伤残",
			capture : "伤残",
			defaultVal : "未构成伤残"
		}, {
			tagList : "court_opinion",
			regex : "死亡",
			reverseRegex : "未(造成|导致)[^,，；;\\.。]*?死亡",
			capture : "死亡"
		}, {
			tagList : "court_opinion",
			regex : "伤残",
			reverseRegex : "未(造成|导致|构成)[^,，；;\\.。]*?伤残",
			capture : "伤残",
			defaultVal : "未构成伤残"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-17:违规情况
	 */
	info_rule_violation : {
		params : [
				{
					tagList : "facts_found",
					regex : "酒驾|饮酒|酒后",
					capture : "饮酒驾驶"
				},
				{
					tagList : "facts_found",
					regex : "被害人故意",
					capture : "被害人故意"
				},
				{
					tagList : "facts_found",
					regex : "醉酒|醉驾",
					capture : "醉酒驾驶"
				},
				{
					tagList : "facts_found",
					regex : "超速|行驶速度超过",
					capture : "超速"
				},
				{
					tagList : "facts_found",
					regex : "超载|载物超过核定",
					capture : "超载"
				},
				{
					tagList : "facts_found",
					regex : "(未取得|无)驾驶资格|无(驾驶)?证驾驶|(未(依法)?取得|无)(机动车)?驾驶证|无证[^,，\\.。；;]*?驾驶",
					capture : "未取得驾驶资格"
				},
				{
					tagList : "facts_found",
					regex : "逃逸",
					capture : "肇事后逃逸"
				},
				{
					tagList : "facts_found",
					regex : "盗抢期间",
					capture : "被盗抢期间肇事"
				},
				{
					tagList : "facts_found",
					regex : "被保险人故意",
					capture : "被保险人故意"
				},

				{
					tagList : "court_opinion",
					regex : "酒驾|饮酒|酒后",
					capture : "饮酒驾驶"
				},
				{
					tagList : "court_opinion",
					regex : "被害人故意",
					capture : "被害人故意"
				},
				{
					tagList : "court_opinion",
					regex : "醉酒|醉驾",
					capture : "醉酒驾驶"
				},
				{
					tagList : "court_opinion",
					regex : "超速|行驶速度超过",
					capture : "超速"
				},
				{
					tagList : "court_opinion",
					regex : "超载|载物超过核定",
					capture : "超载"
				},
				{
					tagList : "court_opinion#court_primary_opinion",
					regex : "(未取得|无)驾驶资格|无(驾驶)?证驾驶|(未(依法)?取得|无)(机动车)?驾驶证|无证[^,，\\.。；;]*?驾驶",
					capture : "未取得驾驶资格"
				}, {
					tagList : "court_opinion",
					regex : "逃逸",
					capture : "肇事后逃逸"
				}, {
					tagList : "court_opinion",
					regex : "盗抢期间",
					capture : "被盗抢期间肇事"
				}, {
					tagList : "court_opinion",
					regex : "被保险人故意",
					capture : "被保险人故意"
				}

		],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-17:租赁借用
	 */
	info_rent_scenartio : {
		params : [ {
			tagList : "facts_found",
			regex : "租赁|借用|租用|租给|转租|租出|转借|出借|租借"
		}, {
			tagList : "court_opinion",
			regex : "租赁|借用|租用|租给|转租|租出|转借|出借|租借"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-18:擅自驾驶他人车辆
	 */
	info_drive_without_permission : {
		params : [ {
			tagList : "facts_found",
			regex : "擅自驾驶|擅自|私自"
		}, {
			tagList : "court_opinion#court_base_opinion#court_primary_opinion",
			regex : "擅自驾驶|擅自|私自"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-19:转让交付未办理登记
	 */
	info_no_register : {
		params : [ {
			tagList : "facts_found",
			regex : "未办理.*?(登记|过户)"
		}, {
			tagList : "court_opinion",
			regex : "未办理.*?(登记|过户)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-20:驾驶培训活动
	 */
	info_drive_training : {
		params : [ {
			tagList : "facts_found",
			regex : "驾驶培训|培训"
		}, {
			tagList : "court_opinion",
			regex : "驾驶培训|培训"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-21:试乘
	 */
	info_test_ride : {
		params : [ {
			tagList : "facts_found",
			regex : "试乘"
		}, {
			tagList : "court_opinion",
			regex : "试乘"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-22:好意同乘
	 */
	info_good_intention : {
		params : [ {
			tagList : "facts_found",
			regex : "善意.*?搭[载乘]|好意同乘"
		}, {
			tagList : "court_opinion",
			regex : "善意.*?搭[载乘]|好意同乘"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-23:陪练
	 */
	info_partner_training : {
		params : [ {
			tagList : "facts_found",
			regex : "陪练"
		}, {
			tagList : "court_opinion",
			regex : "陪练"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-24:服务场所提供泊车代驾
	 */
	info_drive_service : {
		params : [ {
			tagList : "facts_found",
			regex : "泊车|代驾"
		}, {
			tagList : "court_opinion",
			regex : "泊车|代驾"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-25:转让拼装报废
	 */
	info_vehicle_assemeled : {
		params : [ {
			tagList : "facts_found",
			regex : "拼装|报废"
		}, {
			tagList : "court_opinion",
			regex : "拼装|报废"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-26:盗抢
	 */
	info_vehicle_rob : {
		params : [ {
			tagList : "facts_found",
			regex : "盗抢|盗窃|抢夺|抢劫"
		}, {
			tagList : "court_opinion",
			regex : "盗抢|盗窃|抢夺|抢劫"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-27:套牌车
	 */
	info_plate_fake : {
		params : [ {
			tagList : "facts_found",
			regex : "套牌|冒用|套用"
		}, {
			tagList : "court_opinion",
			regex : "套牌|冒用|套用"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-28:机动车挂靠
	 */
	info_vehicle_affiliated : {
		params : [ {
			tagList : "facts_found",
			regex : "登记.*?名下|挂户|挂靠"
		}, {
			tagList : "court_opinion",
			regex : "登记.*?名下|挂户|挂靠"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-29:驾驶人逃逸
	 */
	info_driver_abscond : {
		params : [ {
			tagList : "facts_found",
			regex : "逃逸|逃离|驶离"
		}, {
			tagList : "court_opinion",
			regex : "逃逸|逃离|驶离"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-30:多辆机动车主体责任
	 */
	info_responsibility_multiple_vehicle : {
		params : [ {
			tagList : "facts_found",
			regex : "(主要责任)",
			capture : "\\1"
		}, {
			tagList : "facts_found",
			regex : "(全部责任)",
			capture : "\\1"
		}, {
			tagList : "facts_found",
			regex : "(部分责任)",
			capture : "\\1"
		}, {
			tagList : "facts_found",
			regex : "(次要责任)",
			capture : "\\1"
		}, {
			tagList : "facts_found",
			regex : "(无责任)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "(主要责任)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "(全部责任)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "(部分责任)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "(次要责任)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "(无责任)",
			capture : "\\1"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-30:医嘱休息时长
	 */
	info_rest_duration : {
		params : [ {
			tagList : "court_opinion",
			regex : "(休息|全休|卧[\\u4e00-\\u9fa5]*?床)[^,，;；:：\\.。]*?([天周日月年]|星期)",
            reverse : "0",
            unit : "日"
		} ],
		method : "rule_func_extract_time"
	},
	/**
	 * point-31:被告赔付总额
	 */
	info_traffic_compensation_amout_total : {
		params : [ {
			tagList : "court_opinion",
			regex : "[共总合]计[^,，;；。]*?元",
			unit : "元"
		} ],
		method : "rule_func_extract_money"
	},
	/**
	 * point-31:承担责任方式
	 */
	info_undertake_duty_way : {
		params : [ {
			tagList : "court_opinion",
			regex : "连带(责任|承担|赔偿)",
			capture : "连带责任",
			unit : "元"
		}, {
			tagList : "court_opinion",
			regex : "共同责任",
			capture : "共同责任"
		}, {
			tagList : "court_opinion",
			regex : "连带(责任|承担|赔偿)",
			capture : "单独责任"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-32:车载物品损失
	 */
	info_materiel_loss_in_vehiclle : {
		params : [ {
			tagList : "court_opinion",
			regex : "车载物品损失"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-33:多辆机动车分配交强险
	 */
	info_compulsory_insurance_allocation_multiple_vehicle : {
		params : [ {
			tagList : "court_opinion",
			regex : "多辆机动车分配交强险"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-34:多个被侵权人交强险分配
	 */
	info_compulsory_insurance_allocation : {
		params : [ {
			tagList : "court_opinion",
			regex : "多个被侵权人交强险分配"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-35:承担责任比例
	 */
	info_responsibility_ratio : {
		params : [ {
			tagList : "judgement_content",
			regex : "全部责任",
			capture : "全部责任"
		}, {
			tagList : "judgement_content",
			regex : "主要责任",
			capture : "主要责任"
		}, {
			tagList : "judgement_content",
			regex : "同等责任",
			capture : "同等责任"
		}, {
			tagList : "judgement_content",
			regex : "次要责任",
			capture : "次要责任"
		} ],
		method : "rule_func_judge_truth_by_regex"
	}
};