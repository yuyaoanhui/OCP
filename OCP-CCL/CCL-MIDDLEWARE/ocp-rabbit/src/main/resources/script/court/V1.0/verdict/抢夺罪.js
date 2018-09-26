var NAMESPACE_COURT_VERDICT_抢夺罪 = {
	/**
	 * point-1:伤害后果
	 */
	info_damage_consequences : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤#重伤#使[^,，\\.。;；:：]*?残疾#死亡#精神失常",
			capture : "轻微伤#轻伤#重伤#残疾#死亡#精神失常",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-2:轻微伤人数
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
	 * point-3:轻伤人数
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
	 * point-4:重伤人数
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
	 * point-4:死亡人数
	 */
	info_death_toll : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(致|造成)[^,，\\.。;；:：、]*?死亡",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?死亡#死亡[^,，\\.。;；:：、]*?([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-5:自杀人数
	 */
	info_suicide_number : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(致|造成)[^,，\\.。;；:：、]*?自杀",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "人"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?自杀",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "int"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-6:抢夺数额
	 */
	info_gross_amount : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(共|总|累计|价值)[^,，;；\\.。]*?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-7:多次抢夺
	 */
	info_rob_many_times : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "抢[^,，;；\\.。]*?(多|数|几|([一二三四五六七八九十两\\d]+))次#(多|数|几|([一二三四五六七八九十两\\d]+))次[^,，;；\\.。]*?抢",
			meanWhile : "0",
			order : "0",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:抢夺弱势群体财物
	 */
	info_rob_property_group : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(抢|夺)[^,，\\.。;；:：]*?(老年|未成年|孕妇|携带婴幼儿|残疾|丧失劳动能力|病人)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:抢夺特殊用途款物
	 */
	info_grab_special_purpose_items : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "救灾|抢险|防汛|优抚|扶贫|移民|救济",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:抢夺亲友财物
	 */
	info_spoil : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "抢[^,，;；\\.。]*?(亲属|朋友)|与[^,，;；\\.。]*?(亲属|朋友)",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:突发事件期间在事件发生地抢夺
	 */
	info_robbery_at_incident_scene : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "突发事件期间在事件发生地抢夺",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-12:驾驶机动车
	 */
	info_drive_motor_vehicle : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(驾|行驶|骑)[^,，:：；;\\.。非]*?(机动车|摩托车)",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-13:驾驶非机动车
	 */
	info_drive_non_motor_vehicle : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "驾驶非机动车|(电动)?自行车",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-14:组织未成年人
	 */
	info_underage : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "组织[^,，:：；;\\.。]*?未成年人",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-15:控制未成年人
	 */
	info_control_minors : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "控制[^,，:：；;\\.。]*?未成年人",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-16:主动归还
	 */
	info_active_return : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(主动|积极)[归退返]还",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
};