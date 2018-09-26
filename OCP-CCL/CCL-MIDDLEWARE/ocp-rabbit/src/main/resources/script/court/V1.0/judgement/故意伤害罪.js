var NAMESPACE_COURT_JUDGEMENT_故意伤害罪 = {
	/**
	 * point-1:伤害孕妇
	 */
	info_hurt_pregnant : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(伤害|犯罪|被害人)[^,，;；\\.。]*?孕妇|对孕妇实施犯罪|孕[^,，\\.。;；:：]*被害人|胎儿死亡|流产",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:积极施救
	 */
	info_rescus : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "积极施救#送医",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:雇佣他人
	 */
	info_hire_someone : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "雇佣[^,，\\.。;；:：]*?(故意|实施)|雇凶|出资|提供资金",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:使用凶器
	 */
	info_use_weapon : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(持|携|携带|拿|用)(凶器|械|枪|作案工具|[^,，;；\\.。]*?刀)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:报复伤害
	 */
	info_retaliation : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "报复",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:因实施其他违法犯罪活动
	 */
	info_illegal_activities : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "因实施其他(违法|犯罪|违法犯罪)活动",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:伤害后果
	 */
	info_damage_consequences : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤#重伤",
			capture : "轻微伤#轻伤#重伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "死亡|致死",
			capture : "死亡",
			reverseRegex : "胎儿死亡",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-8:轻微伤伤害人数
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
	 * point-9:轻伤伤害人数
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
	 * point-10:重伤伤害人数
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
					regex : "([一二三四五六七八九十两\\d]+)(人|名)[^,，\\.。;；:：、]*?重伤#重伤[^,，\\.。;；:：、]*?([一二三四五六七八九十两\\d]+)人",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-11:死亡伤害人数
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
	 * point-12:伤残等级
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
	 * point-13:伤残等级
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
	 * point-14:伤残等级
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
	 * point-15:伤残等级
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
	},
	/**
	 * point-16:手段残忍程度
	 */
	info_cruelty_method : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(挖|割|挑|砍|剜)[^,，;；\\.。]*?眼睛|眼[^,，;；\\.。]*?脱落",
					capture : "挖人眼睛",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(挖|割|挑|砍|剜)[^,，;；\\.。]*?耳朵",
					capture : "割人耳朵",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(挖|割|挑|砍|剜)[^,，;；\\.。]*?鼻子",
					capture : "割人鼻子",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(挖|割|挑|砍|剜)[^,，;；\\.。]*?脚筋",
					capture : "挑人脚筋",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(挖|割|挑|砍|剜)[^,，;；\\.。]*?(手|脚|足|四肢|臂)|(手|脚|足|四肢|臂)[^,，;；\\.。]*?(挖|割|挑|砍|剜)",
					capture : "砍人手足",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(挖|割|挑|砍|剜)[^,，;；\\.。]*?(髌骨|膝盖)",
					capture : "剜人髌骨",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(以(锐器|硫酸|腐蚀性溶液)严重毁容)",
					capture : "\\1",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "((电击|烧烫)要害部位)",
					capture : "\\1",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "冷冻",
					capture : "冷冻",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "毒蛇猛兽撕咬",
					capture : "毒蛇猛兽撕咬",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "court_opinion#facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "其他特别残忍手段",
					capture : "其他特别残忍手段",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_stringlist_info"
	}
}