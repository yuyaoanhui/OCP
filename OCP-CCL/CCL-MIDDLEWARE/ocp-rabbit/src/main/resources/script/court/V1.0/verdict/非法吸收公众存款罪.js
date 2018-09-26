var NAMESPACE_COURT_VERDICT_非法吸收公众存款罪 = {
	/**
	 * point-1:犯罪金额
	 */
	info_crime_amount : {
		params : [
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(吸收|业务总额)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(共(计|吸?收)|合计)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "office_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(实际|实存|实收)[^,，。；;:：\\.]*?(金额|存款)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(吸收|业务总额)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(共(计|吸?收)|合计)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "(实际|实存|实收)[^,，。；;:：\\.]*?(金额|存款)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_above",
					cacheKey : "meta_people_name2obj",
					regex : "(吸收|业务总额)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_above",
					cacheKey : "meta_people_name2obj",
					regex : "(共(计|吸?收)|合计)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "facts_above",
					cacheKey : "meta_people_name2obj",
					regex : "(实际|实存|实收)[^,，。；;:：\\.]*?(金额|存款)[^,，。；;:：]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(实收|犯罪数额|吸收[\\u4e00-\\u9fae]*?(存款|资金|投资))[^,，。；;:：]*?元(?!以上)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-2:财产损失数额
	 */
	info_property_loss_num : {
		params : [
				{
					tagList : "office_opinion#facts_found#facts_above",
					cacheKey : "meta_people_name2obj",
					regex : "造成[^,，。；;:：\\.]*?损失[^,，。；;:：]*?元|造成[^,，。；;:：]*?元[^,，。；;:：\\.]*?(损失|(无法|未能?)([返退归]还|兑付))|未([返退归]还|兑付)[^,，。；;:：]*?元|有[^,，。；;:：]*?元未([返退归]还|兑付)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "造成[^,，。；;:：\\.]*?损失[^,，。；;:：]*?元|(造成|导致)[^,，。；;:：]*?元[^,，。；;:：\\.]*?((无法|未能?)[返退归]还|损失)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "金额"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-3:受害人数
	 */
	info_victim_amount : {
		params : [
				{
					tagList : "office_opinion#facts_found#facts_above",
					cacheKey : "meta_people_name2obj",
					regex : "[向从][^,，。；;:：\\.]*?[\\d]+[人名户][^,，。；;:：\\.]*?吸收|(吸收|共|涉及)[^,，。；;:：\\.]*?[\\d]+[人名户位]|(储户|存款人?)[\\d]+[人名]|(与|诱骗)[^,，。；;:：\\.]*?[\\d]+[人名][^,，。；;:：\\.]*?(签订|参加|参与)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				}, {
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "[\\d]+[人名户]",
					meanWhile : "0",
					order : "1",
					defaultAll : "0",
					type : "其他",
					unit : "int"
				} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-4:提供帮助收取费用
	 */
	info_help_for_charge : {
		params : [
				{
					tagList : "facts_found",
					cacheKey : "meta_people_name2obj",
					regex : "管理费|好处费|返点|提成|代理费|佣金|手续费|按融资额[^,，;；:：。\\.]*?从中获利|从中获取[^,，;；:：。]*?元",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "court_opinion",
					cacheKey : "meta_people_name2obj",
					regex : "(业务|信贷|代办)员|(收取|获[得取]|[赚领]取|退[出赔])[\\u4e00-\\u9fae、]*?(管理费|好处费|返点|提成|代理费|佣金|非法利益)",
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
			cacheKey : "meta_people_name2obj",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:犯罪主体
	 */
	info_crime_main_subject : {
		params : [ {
			dependentPoints : "meta_people_attr"
		} ],
		method : "rule_func_extract_people_type_info"
	},
	/**
	 * point-7:造成严重后果
	 */
	info_grave_consequence : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "自杀|精神失常|造成[^,，；;:：。]*?(极大危害|严重)后果|后果严重|巨大(经济)?损失|损失(数额)?巨大",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:用于正常经营能够清退资金
	 */
	info_usual_can_repaying : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "用于[\\u4e00-\\u9fa5]*?经营[\\u4e00-\\u9fa5,，]*?((已|及时)[\\u4e00-\\u9fa5]*?清退|未[\\u4e00-\\u9fa5]*?造成损失|能够兑付[\\u4e00-\\u9fa5]*?本金)",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
};