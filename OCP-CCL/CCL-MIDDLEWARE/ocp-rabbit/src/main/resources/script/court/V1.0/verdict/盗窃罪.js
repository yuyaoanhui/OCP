var NAMESPACE_COURT_VERDICT_盗窃罪 = {
	/**
	 * point-1:因盗窃受过刑事处罚
	 */
	info_penalty_for_theft_record : {
		params : [ {
			tagList : "defendant",
			regex : "因[^\\.。;；、]*?[窃偷盗]+[^\\.。;；、]*?[受判处][^\\.。;；、]*?(刑事|有期徒刑|拘役|管制|缓刑)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:多次盗窃
	 */
	info_multiple_theft : {
		params : [ {
			tagList : "facts_found",
			regex : "多次[\\u4e00-\\u9fa5]*?[窃盗偷]+",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "多次[\\u4e00-\\u9fa5]*?[窃盗偷]+",
			cacheKey : "meta_people_name2obj",
			reverseRegex : "多次[\\u4e00-\\u9fa5]*?[窃盗偷]+[\\u4e00-\\u9fa5]+前科",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:入户盗窃
	 */
	info_indoor_theft : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "入室|入户",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-4:携带凶器
	 */
	info_arm_associated : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "凶器",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:破坏性手段
	 */
	info_means_destructive : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "破坏性(手段|方式)|破坏[^,，\\.。;；、]*?盗窃",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:为违法活动盗窃
	 */
	info_illegal_purpose : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "(为|企图|目的|用于|进行)[^,，\\.。;；]*?(毒|赌|嫖|违法)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:扒窃
	 */
	info_pocket_pick : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "扒窃",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:教唆他人犯罪但被教唆者未犯被教唆的罪
	 */
	info_abetment_unfulfilled : {
		params : [ {
			tagList : "court_opinion#facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "教唆[^,，\\.。;；、]*?犯罪[^,，\\.。;；、]*?(未|没有|无)(参与|作案|盗窃)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:突发事件期间盗窃
	 */
	info_steal_while_emergency : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-10:因生活所迫学习治病急需
	 */
	info_life_purpose : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "生活所迫|经济(拮据|困难)|没钱|(为)[^\\.。;；]*?(学习|上学)|(为|钱)[^\\.。;；]*?治病",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-11:特殊地点盗窃
	 */
	info_special_spot : {
		params : [ {
			tagList : "facts_found#plaintiff_args#office_opinion#court_opinion",
			regex : "(医院|门诊|特殊地点)[^\\.。;；、]*?[偷盗窃]|[偷盗窃][^\\.。;；、]*?(医院|门诊|特殊地点)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-12:盗窃家庭成员或近亲属
	 */
	info_family_theft : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "[窃偷盗][^,，\\.。;；、]*?(父|母|夫|妻|家庭成员|亲属|近亲|自家财产)|[是为系][^,，\\.。;；、]*?(父|母|夫|妻|家庭成员|亲属|自家财产)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-13:盗窃特殊对象
	 */
	info_special_victims : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "(犯罪|盗窃|扒窃)(残疾|(孤寡|留守)老人|(丧失|无)劳动能力)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-14:盗窃特殊物资
	 */
	info_special_material : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "[盗窃][\\u4e00-\\u9fa5]*?(救灾|抢险|防汛|优抚|扶贫|移民|救济款物)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-15:损失大于盗窃数额
	 */
	info_loss_bigger_than_amount : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "损失[\\u4e00-\\u9fa5]*?(大于|不止|超过)",
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-16:盗窃文物级别——盗窃国有馆藏三级文物
	 */
	info_state_cultural_relic_third : {
		params : [ {
			tagList : "facts_found#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "[3三]级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?[3三]级",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_option",
			cacheKey : "meta_people_name2obj",
			regex : "[3三]级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?[3三]级",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-17:盗窃文物级别——盗窃国有馆藏二级以上文物
	 */
	info_state_cultural_relic_second : {
		params : [ {
			tagList : "facts_found#facts_above",
			regex : "([特一二12])级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?([特一二12])级",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_option",
			regex : "([特一二12])级[^,，\\.。;；、]*?文物|文物[^,，\\.。;；、]*?([特一二12])级",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-18:盗窃文物级别——盗窃国有馆藏一般文物
	 */
	info_state_cultural_relic_general : {
		params : [ {
			tagList : "facts_found#facts_above",
			regex : "文物",
			cacheKey : "meta_people_name2obj",
			reverseRegex : "([特一二三123]级)[^,，\\.。;；、]*?文物",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_option",
			regex : "文物",
			cacheKey : "meta_people_name2obj",
			reverseRegex : "([特一二三123]级)[^,，\\.。;；、]*?文物",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-19:盗窃文物数目——盗窃国有馆藏三级文物
	 */
	info_state_cultural_relic_third_num : {
		params : [
				{
					tagList : "facts_found#facts_above",
					regex : "[3三]级[^,，\\.。;；、]*?文物[^,，\\.。;；、]*?(个|件|只|尊)|[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+(个|件|只|尊)[^,，\\.。;；、]*?([3三]级)[^,，\\.。;；、]*?文物",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "0",
					defaultAll : "1",
					type : "其他",
					unit : "int"
				},
				{
					tagList : "court_option",
					regex : "[3三]级[^,，\\.。;；、]*?文物[^,，\\.。;；、]*?(个|件|只|尊)|[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+(个|件|只|尊)[^,，\\.。;；、]*?([3三]级)[^,，\\.。;；、]*?文物",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "0",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-20:盗窃文物数目——盗窃国有馆藏二级以上文物
	 */
	info_state_cultural_relic_second_num : {
		params : [
				{
					tagList : "facts_found#facts_above",
					regex : "([特一1二2])级[^,，\\.。;；、]*?文物[^,，\\.。;；、]*?(个|件|只|尊)|[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+(个|件|只|尊)[^,，\\.。;；、]*?([特一1二2])级[^,，\\.。;；、]*?文物",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "0",
					defaultAll : "1",
					type : "其他",
					unit : "int"
				},
				{
					tagList : "court_option",
					regex : "([特一1二2])级[^,，\\.。;；、]*?文物[^,，\\.。;；、]*?(个|件|只|尊)|[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+(个|件|只|尊)[^,，\\.。;；、]*?([特一1二2])级[^,，\\.。;；、]*?文物",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "0",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-21:盗窃文物数目——盗窃国有馆藏一般文物
	 */
	info_state_cultural_relic_general_num : {
		params : [
				{
					tagList : "facts_found#facts_above",
					regex : "文物[^,，\\.。;；、]*?(个|件|只|尊)|[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+(个|件|只|尊)[^,，\\.。;；、]*?文物",
					reverseRegex : "([特一二三123]级)[^,，\\.。;；、]*?文物",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "0",
					defaultAll : "1",
					type : "其他",
					unit : "int"
				},
				{
					tagList : "court_option",
					regex : "文物[^,，\\.。;；、]*?(个|件|只|尊)|[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+(个|件|只|尊)[^,，\\.。;；、]*?文物",
					reverseRegex : "([特一二三123]级)[^,，\\.。;；、]*?文物",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "0",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-22:总金额
	 */
	info_total_amount : {
		params : [
				{
					tagList : "facts_found",
					regex : "(涉案|金额|价值|数额|现金|人民币)[^。;；]*?([\\d\\.]+)([余万]*?)元",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_found",
					regex : "(总|共|合计)[^。;；]*?([\\d\\.]+)([余万]*?)元",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "court_opinion",
					regex : "(总计|总价值|共计|涉案|金额|价值|数额|合计|总共|现金|人民币)([\\u4e00-\\u9fa5]+)?([\\d\\.]+)元",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				}, {
					tagList : "judgement_content",
					regex : "赃款[^,，\\.。;；、]*?元[^,，\\.。;；、]*?(退还|返还|归还)",
					cacheKey : "meta_people_name2obj",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-23:案发前放回原处
	 */
	info_replacement : {
		params : [ {
			tagList : "facts_found",
			regex : "案发前[^\\.。;；、]*?放回原处",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "案发前[^\\.。;；、]*?放回原处",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-24:盗窃文物
	 */
	info_cultural_relic : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "文物",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "(盗窃|窃取)[^\\.。;；]*?文物",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-25:金额范围
	 */
	info_amount_range : {
		params : [ {
			tagList : "court_opinion",
			regex : "额较大#额巨大#额特别巨大|特别巨大",
			cacheKey : "meta_people_name2obj",
			capture : "数额较大#数额巨大#数额特别巨大",// 捕获模式与正则一一对应
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * point-26:盗窃总次数
	 */
	info_number_theft : {
		params : [ {
			tagList : "court_opinion#facts_found#facts_above",
			cacheKey : "meta_people_name2obj",
			regex : "([窃偷盗]+|作案)[^,，\\.。;；、]*?[０１２３４５６７８９1234567890一二两三四五六七八九十〇○Ｏ０OoΟ零]+[起次桩]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "次"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-27:组织控制未成年人盗窃
	 */
	info_minor_control : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(组织|控制|强迫|逼迫)[^,，\\.。;；、]*?(未成年|青少年)[^,，\\.。;；、]*?([窃偷盗]+|作案)",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-28:主动归还
	 */
	info_active_return : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(主动|积极|已|自动)[^\\.。;；、，,]*?(放回|归还|返还|退还|退赃|退赔|退出)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-29:一年内因盗窃受过行政处罚
	 */
	info_penalty_admin_record : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "一年(之)?内([\\u4e00-\\u9fa5、]+)?因(为)?盗窃([\\u4e00-\\u9fa5、]+)?受(到)?过([\\u4e00-\\u9fa5、]+)?行政处罚",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
}
