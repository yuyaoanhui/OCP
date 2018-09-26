var NAMESPACE_PROCURATORATE_EXAMININGCATCHINGOPINION_base = {
	/**
	 * 提取检察院名称及省市县区
	 */
	document_base_info : {
		params : [ {
			tagList : "TITLE",
			orgName : "检察院",
			filePaths : "classification.procuratorate.china"
		} ],
		method : "rule_func_extract_dept_info"
	},
	/**
	 * 提取文书类型
	 */
	meta_document_type : {
		params : [ {
			tagList : "TITLE",
			regex : "院(.*?)(意见书)",
			capture : "\\1\\2"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取业务类型
	 */
	meta_procurate_type : {
		params : [ {
			tagList : "TITLE",
			regex : "院(.*?)(意见书)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取文书名称
	 */
	meta_source_text_name : {
		params : [ {
			tagList : "TITLE",
			regex : "((.*)院(.*?)书)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取caseid
	 */
	case_id : {
		params : [ {
			tagList : "TITLE",
			regex : "(检.*?号)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取承办人
	 */
	meta_contractor : {
		params : [ {
			tagList : "SIGNATURE",
			regex : "承办人(?:：|:)([^,，\\.。;；\\d\\n\\r]*)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取落款日期
	 */
	meta_signature_date : {
		params : [ {
			tagList : "SIGNATURE",
			regex : "([\\d年月日]+)",
			defaultVal : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取侦查机移送时间
	 */
	meta_investigate_time : {
		params : [ {
			tagList : "SOLVE_PROCESS",
			regex : "于([\\d年月日]+)移送",
			defaultVal : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取侦查机关名称
	 */
	meta_investigate_name : {
		params : [ {
			tagList : "SUSPECT_PROCESS",
			regex : "接到(.*?(?:院|公安局))[^。\\.；;,，]*?以(.*?)文书"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * 提取案由
	 */
	meta_case_ay : {
		params : [ {
			tagList : "SUSPECT_PROCESS",
			regex : "[^\\d]+?(罪|案)一案"
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
	 * 提取归案情况
	 */
	info_justice_situation : {
		params : [ {
			tagList : "SOLVE_PROCESS",
			regex : "(传唤|抓获|主动|扭送)#(在逃)",
			capture : "\\1归案#\\1",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_string_info"
	},
	/**
	 * 建议不批捕
	 */
	info_advice_reject_arrest : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "建议[^,，\\.。;；不予]*?(批准逮捕|批捕|决定逮捕)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * 建议批捕
	 */
	info_advice_approve_arrest : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "建议[^,，\\.。;；]*?不(予)?(批准逮捕|批捕|决定逮捕)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * 逮捕理由
	 */
	info_reason : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "逮捕必要|建议[^,，\\.。;；]*?(逮捕|批捕)|事实清楚|证据充分|逮捕条件",
			meanWhile : "0",
			order : "0",
			defaultAll : "0",
			range : "1"
		}, {
			tagList : "RISK_ANALYSIS",
			cacheKey : "meta_people_name2obj",
			regex : "逮捕必要|建议[^,，\\.。;；]*?(逮捕|批捕)|事实清楚|证据充分|逮捕条件",
			meanWhile : "0",
			order : "0",
			defaultAll : "0",
			range : "1"
		} ],
		method : "rule_func_extract_litigant_sentenceList_info"
	},
	/**
	 * 结尾添加上要设置的标点符号
	 */
	add_punc : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "建议[^,，\\.。;；]*?(批准逮捕|批捕|决定逮捕)[^,，\\.。;；]*?[,，\\.。;；]",
			punctuation : "。"
		} ],
		method : "rule_func_add_punc"
	},
	/**
	 * 生成文书唯一ID
	 */
	unique_id : {
		params : [ {
			tagList : "caseId",
			dependentPints : "meta_document_type#meta_signature_date",
		} ],
		method : "rule_func_generate_unique_idfentification"
	},
	section_title : {
		params : [ {
			tagList : "TITLE"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_suspect_process : {
		params : [ {
			tagList : "SUSPECT_PROCESS"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_suspect_base_info : {
		params : [ {
			tagList : "SUSPECT_BASE_INFO"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_solve_process : {
		params : [ {
			tagList : "SOLVE_PROCESS"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_case_fact_evidence : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_return_reason : {
		params : [ {
			tagList : "REAUTHEN_REASON"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_ques_to_explain : {
		params : [ {
			tagList : "QUES_TO_EXPLAIN"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_risk_analysis : {
		params : [ {
			tagList : "RISK_ANALYSIS"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_risk_asses_plan : {
		params : [ {
			tagList : "RISK_ASSES_PLAN"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_handle_opinion : {
		params : [ {
			tagList : "HANDLE_OPINION"
		} ],
		method : "rule_func_add_section_by_tag"
	},
	section_signature : {
		params : [ {
			tagList : "SIGNATURE"
		} ],
		method : "rule_func_add_section_by_tag"
	}
};

/**
 * 参数解释：<br>
 * tagList;// 标签列表<br>
 * cacheKey;// 缓存字段key<br>
 * infoPointName;// 信息点名称<br>
 * regex;// 通用的常规正向正则<br>
 * regex_limitAmount;// 匹配限制数量<br>
 * regex_totalAmount;// 匹配总量<br>
 * regex_overtopAmount;// 匹配超出数量<br>
 * capture;// 捕获模式<br>
 * reverseRegex;// 反向正则,多个用#隔开<br>
 * meanWhile;// 人物和属性是否限定同时出现在一个连续无符号的短句中.1表示限定,0表示不限定.<br>
 * order;// 是否限定人物和属性出现的固定顺序.0-不限定,1-人在前属性在后 ,2-人在后属性在前<br>
 * defaultAll;// 如果匹配到属性但没有找人对应的人,是否需要默认是所有人的.0-不需要,1-需要<br>
 * type;// 信息点类型(金额/时间/其他)<br>
 * unit;// 输出单位
 * <p>
 * a、若信息点类型为'金额'，取值为'万'、'千万'等，若取值设为'',则默认不需要单位转换
 * b、若信息点类型为'时间'，取值为'年'、'月'、'日'、'小时'、'分钟'，若取值设为'',则默认不需要单位转换
 * c、若信息点类型为'其他'，取值为'int'表示将double转成int，取值为''表示不需要转换*
 * </p>
 * defaultVal;// 默认填充值<br>
 * reverse;// 每个标签对应句子集合是否翻转,(0-否 1-是),抽取时间和日期时用到<br>
 * range;// 设定取值范围，0-取匹配内容所在的整个段落 1-匹配内容所在的整个句子(结尾为。或;)<br>
 * mutex;// 互斥信息点,多个用#隔开<br>
 * valueNodes;// 范围对应的各个节点值，以#隔开<br>
 * valueRanges;// 范围，以#隔开<br>
 * crimeName;// 罪名<br>
 * dependentPoints;// 依赖信息点名,多个用#隔开<br>
 * yearsNum;// 几年内是哦夫因XXX受过XX处罚<br>
 * orgName;// 组织机构：法院|检察院<br>
 * filePaths;// 资源配置文件名称,多个用#隔开<br>
 * resultFlag;// 结果为比例还是差值 0-比例 1-差值<br>
 * caseType;// 案件类型<br>
 * judgementType;// 判决类型<br>
 */
