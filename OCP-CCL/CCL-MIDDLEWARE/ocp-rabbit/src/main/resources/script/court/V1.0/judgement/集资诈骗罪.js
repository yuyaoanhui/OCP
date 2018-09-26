var NAMESPACE_COURT_JUDGEMENT_集资诈骗罪 = {
	/**
	 * point-1:犯罪主体
	 */
	info_crime_main_subject : {
		params : [ {
			dependentPoints : "meta_people_attr"
		} ],
		method : "rule_func_extract_people_type_info"
	},
	/**
	 * point-2:诈骗金额
	 */
	info_fraud_mount : {
		params : [ {
			tagList : "office_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(诈骗|骗取|集资|[合共]计|尚有|骗得)[^,，；;:：。]*?[\\d.]+万?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "(诈骗|骗取|集资|[合共]计)[^,，；;:：。]*?[\\d.]+万?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(诈骗|骗取|集资|[合共]计|价值)[^,，；;:：。]*?[\\d.]+万?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		}, {
			tagList : "judgement_content",
			cacheKey : "meta_people_name2obj",
			regex : "(合计|退赔)[^,，；;:：。]*?[\\d.]+万?元",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "金额"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-2:假冒国家机关或公益性组织
	 */
	info_pretending_dept_welfare : {
		params : [
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(假冒|冒充)(国家机关|公益性?组织|公安|检察院|法院|银行|[^,，：：；;\\.。]*?委员会)|虚假政府文件|(假借|用)[^,，：：；;\\.。]*?慈善[^,，：：；;\\.。]*?名义",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(假冒|冒充)(国家机关|公益性?组织|公安|检察院|法院|银行)|虚假政府文件|(假借|用)[^,，：：；;\\.。]*?慈善[^,，：：；;\\.。]*?名义",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(假冒|冒充)(国家机关|公益性?组织|公安|检察院|法院|银行)|虚假政府文件|(假借|用)[^,，：：；;\\.。]*?慈善[^,，：：；;\\.。]*?名义",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-3:诈骗人数
	 */
	info_fraud_victim_amount : {
		params : [
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，；;：:\\.。]*?人[^,，；;：:\\.。]*?(集资|借款|筹款|骗取)",
					reverseRegex : "[向从](被害人|多人|他人|不特定的人|更多的人)[^,，；;：:\\.。]*?(集资|借款|筹款|骗取)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，；;：:\\.。]*?[\\d]+[人名户][^,，；;::\\.。]*?(集资|借款|筹款|骗取)|骗[^,，；;：:\\.。]*?[\\d]+[人名户]",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "平台[^,，；;：:\\.。]*?注册[^,，；;：:\\.。]*?会员[^,，；;：:\\.。]*?[\\d]+[人名户]",
					meanWhile : "0",
					order : "0",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},

				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，；;：:\\.。]*?集资",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，；;：:\\.。]*?[\\d]+[人名户][^,，；;::\\.。]*?集资|骗[^,，；;：:\\.。]*?[\\d]+[人名户]",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "平台[^,，；;：:\\.。]*?注册[^,，；;：:\\.。]*?会员[^,，；;：:\\.。]*?[\\d]+[人名户]",
					meanWhile : "0",
					order : "0",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，；;：:\\.。]*?集资",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，；;：:\\.。]*?[\\d]+[人名户][^,，；;::\\.。]*?集资|骗[^,，；;：:\\.。]*?[\\d]+[人名户]",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "人"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-4:为违法活动
	 */
	info_for_illegal_purpose : {
		params : [
				{
					tagList : "facts_found",
					regex : "(为|用于)[^,，；;:：\\.。]*?((违法(犯罪)?|非法)活动|赌博|吸毒)|赌博|归还赌债|属于违法活动",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					regex : "(为|用于)[^,，；;:：\\.。]*?((违法(犯罪)?|非法)活动|赌博|吸毒)|赌博|归还赌债|属于违法活动",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:突发事件期间犯罪
	 */
	info_crime_while_emergency : {
		params : [ {
			tagList : "court_opinion",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:未参与分赃或者分赃较少
	 */
	info_less_stolen_goods : {
		params : [ {
			tagList : "court_opinion",
			regex : "(未|没有)参与分赃|(分赃|(分得|所得)赃款)[\\u4e00-\\u9fae]*?较少",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:被害人绝大部分损失被挽回的
	 */
	info_lost_retrieve : {
		params : [ {
			tagList : "court_opinion",
			regex : "损失被挽回|挽回[\\u4e00-\\u9fae]*?损失|退还大部分赃款",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:造成严重后果
	 */
	info_grave_consequence : {
		params : [ {
			tagList : "court_opinion",
			regex : "自杀|精神失常|严重后果|数额(特别)?巨大",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-9:被害人为弱势群体
	 */
	info_disadvantaged_victim : {
		params : [ {
			tagList : "court_opinion",
			regex : "(被害人[^；;：:\\.。]*?|诈骗)(残疾|老年|丧失劳动能力)|对象[\\u4e00-\\u9fae、]*?老年?人|[向对以][^,，；;:：\\.。]*?(老年人|弱势群体)[^,，；;:：\\.。]*?(诈骗|集资|作案)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-10:金额范围
	 */
	info_amount_range : {
		params : [ {
			tagList : "court_opinion",
			regex : "额较大#额巨大#额特别巨大|特别巨大",
			capture : "数额较大#数额巨大#数额特别巨大",
			cacheKey : "meta_people_name2obj"
		} ],
		method : "rule_func_extract_litigant_string_info",
		adjust : [ {
			params : [ {
				dependentPoints : "info_crime_main_subject#info_fraud_mount#meta_case_ay",
				cacheKey : "meta_people_name2obj"
			} ],
			method : "rule_func_extract_range_judgment"
		} ]
	}
};