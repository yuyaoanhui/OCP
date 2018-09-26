var NAMESPACE_COURT_VERDICT_base = {
	/**
	 * 提取法院名信息——包含多个信息点
	 */
	courtMetas : {
		params : [ {
			tagList : "court",
			filePaths : "classification.court.china",
			regex : "(最高|高级|中级)"
		} ],
		method : "rule_func_extract_court_by_regex"
	},
	/**
	 * 提取案件类型信息——包含多个信息点
	 */
	caseTypes : {
		params : [ {
			tagList : "case_type",
            regex : "^(民事|刑事|行政|执行|赔偿).*?(判决书|调解书|裁定书)#(决定书|通知书|令)$"
		} ],
		method : "rule_func_extract_case_type_by_regex"
	},
	/**
	 * 提取案件号信息——包含多个信息点
	 */
	caseIds : {
		params : [ {
			tagList : "case_id",
			regex : "[\\(（〔【\\[]\\d{4}[\\)）〕】\\]].*?第?\\d+(号([之-][一二三四五六七八九十\\d])?|之[一二三四五六七八九十]号)",
		} ],
		method : "rule_func_extract_case_id_by_regex"
	},
	/**
	 * point-1:检察院名称
	 */
	meta_procuratorate_name : {
		params : [ {
			tagList : "case_summary",
			regex : "((?:(?!理)[^，|,|;|；|、|。])*检察院)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-2:被告是否到场
	 */
	info_absence : {
		params : [ {
			tagList : "case_summary",
			regex : "(未|没有)(到庭|出庭)",
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-3:审理程序类型
	 */
	meta_case_procedure_type : {
		params : [ {
			tagList : "case_summary",
			regex : "(简易程序|普通程序|速裁程序)",
			capture : "\\1",
			defaultVal : "普通程序"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-4:开庭情况
	 */
	meta_case_session : {
		params : [ {
			tagList : "case_summary",
			regex : "(不开庭|不公开开庭|公开开庭)[^,，\\.。;；、]*(审理)",
			capture : "\\1\\2",
			defaultVal : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-5:出庭检察官
	 */
	meta_procuratorator : {
		params : [ {
			tagList : "case_summary",
			regex : "(?:代理)?检察员([^，|,|;|；|、|。]{1,4})(?:出庭|参加|、|，|,)",
			capture : "\\1"
		} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-6:提起公诉时间
	 */
	meta_case_prosecute_date : {
		params : [ {
			tagList : "case_summary",
			regex : "提起公诉"
		} ],
		method : "rule_func_extract_sequence_times"
	},
	/**
	 * point-7:提起立案时间
	 */
	meta_case_register_date : {
		params : [ {
			tagList : "case_summary",
			regex : "立案"
		} ],
		method : "rule_func_extract_sequence_times"
	},
	/**
	 * point-8:提起开庭审理时间
	 */
	meta_case_start_date : {
		params : [ {
			tagList : "case_summary",
			regex : "开庭审理"
		} ],
		method : "rule_func_extract_sequence_times"
	},
	/**
	 * points:提取关联id、案件名称等多个信息点
	 */
	caseAbouts : {
		params : [ {
			tagList : "case_summary"
		} ],
		method : "rule_func_extract_case_summary"
	},
	/**
	 * points:提取案件来源信息
	 */
	caseFroms : {
		params : [ {
			tagList : "section_sub_fact"
		} ],
		method : "rule_func_extract_case_from"
	},
	/**
	 * points:提取案件结果信息
	 */
	caseResults : {
		params : [ {
			tagList : "judgement_content"
		} ],
		method : "rule_func_extract_close_manner"
	},
	/**
	 * point-9:提取记录时间
	 */
	meta_doc_date : {
		params : [ {
			tagList : "record_date",
			regex : "^(.*?)$",
			reverse : "0",
			unit : "日期"
		} ],
		method : "rule_func_extract_time"
	},
    /**
     * point-10:为section_title增加超链接
     */
    section_title2 : {
        params : [ {
            dependentPoints : "section_title#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-11:为section_doc_first_part增加超链接
     */
    section_doc_first_part2 : {
        params : [ {
            dependentPoints : "section_doc_first_part#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-12:为section_fact_const增加超链接
     */
    section_fact_const2 : {
        params : [ {
            dependentPoints : "section_fact_const#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-13:为section_reason增加超链接
     */
    section_reason2 : {
        params : [ {
            dependentPoints : "section_reason#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-14:为section_judge_base增加超链接
     */
    section_judge_base2 : {
        params : [ {
            dependentPoints : "section_judge_base#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-15:为section_judge_main增加超链接
     */
    section_judge_main2 : {
        params : [ {
            dependentPoints : "section_judge_main#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-16:为section_last_part增加超链接
     */
    section_last_part2 : {
        params : [ {
            dependentPoints : "section_last_part#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-17:为section_signature增加超链接
     */
    section_signature2 : {
        params : [ {
            dependentPoints : "section_signature#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-18:为section_relate_law增加超链接
     */
    section_relate_law2 : {
        params : [ {
            dependentPoints : "section_relate_law#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
    /**
     * point-19:为section_other增加超链接
     */
    section_other2 : {
        params : [ {
            dependentPoints : "section_other#meta_doc_date#meta_legal_provision#meta_legal_provision_id"
        } ],
        method : "rule_func_build_section_with_url"
    },
	/**
	 * point-19:peopleInfos
	 */
	peopleInfos : {
		params : [ {
			orgName : "court",
			filePaths : "litigant.role#classification.court.china",
			dependentPoints : "meta_court_name"
		} ],
		method : "rule_func_extract_people_info"
	},
	/**
	 * point-20:判断是否发回重审
	 */
	meta_case_remand : {
		params : [ {
			tagList : "judgement_content",
			regex : "发回重审",
			orgName : "court",
			filePaths : "litigant.role#classification.court.china"
		} ],
		method : "rule_func_judge_truth_by_regex"
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
