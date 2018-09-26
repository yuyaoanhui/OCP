var NAMESPACE_PROCURATORATE_RECHECKEXAMINGOPINION_base = {
	/**
	 * 提取落款日期
	 */
	meta_signature_date : {
		params : [ {
			tagList : "SIGNATURE",
			regex : "([\d年月日]+)",
			defaultVal : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取嫌疑人基本信息
	 */
	suspect_base_info : {
		params : [ {
			dependentPoints : "meta_court_name",
			orgName : "procuratorate",
			filePaths : "litigant.role#classification.court.china"
		} ],
		method : "rule_func_extract_people_info"
	},
	/**
	 * 提取文书类型
	 */
	meta_document_type : {
		params : [ {
			tagList : "TITLE",
			regex : "院(.*?)(意见书)",
			capture : "\1\2"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取业务类型
	 */
	meta_procurate_type : {
		params : [ {
			tagList : "TITLE",
			regex : "复议",
			capture : "不捕复议"
		}, {
			tagList : "TITLE",
			regex : "复核",
			capture : "不捕复核"
		}, {
			tagList : "TITLE",
			regex : "申诉",
			capture : "(不)捕申诉"
		}, {
			tagList : "TITLE",
			regex : "撤案",
			capture : "捕后撤案"
		}, {
			tagList : "TITLE",
			regex : "起诉",
			capture : "一审公诉"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 建议维持原逮捕决定
	 */
	info_advice_stand_arrest_decision : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "建议[^,，\.。;；]*?维持[^,，\.。;；不]*?(批准|逮捕|批捕)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "0",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * 建议维持原不逮捕决定
	 */
	info_advice_stand_unarrest_decision : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "建议[^,，\.。;；]*?维持[^,，\.。;；]*?不(予)?(捕|批准|逮捕|批捕)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "0",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * 建议撤销原逮捕决定
	 */
	info_advice_reject_arrest_decision : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "建议[^,，\.。;；]*?撤销[^,，\.。;；不]*?(捕|批准|逮捕|批捕)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "0",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * 建议撤销原不逮捕决定
	 */
	info_advice_reject_unarrest_decision : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "建议[^,，\.。;；]*?撤销[^,，\.。;；]*?不(予)?(捕|批准|逮捕|批捕)",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "0",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * 撤销理由
	 */
	info_reason : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "建议[^,，\.。;；]*?撤销",
			meanWhile : "0",
			order : "0",
			defaultAll : "1",
			range : "1"
		} ],
		method : "rule_func_extract_litigant_sentenceList_info"
	}
}