var NAMESPACE_COURT_JUDGEMENT_criminal = {
	/**
	 * criminal point-1:首要分子
	 */
	info_crime_leader : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "首要分子",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-2:主犯
	 */
	info_principle_criminal : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "主犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-3:从犯
	 */
	info_accessory : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "从犯",
			reverseRegex : "不[一-龥]{0,5}主犯?、?从犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-4:非从犯
	 */
	info_not_main_crimer : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "不[一-龥]{0,5}主犯?、?从犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-5:初犯偶犯
	 */
	info_offender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "初犯#偶犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-6:累犯惯犯
	 */
	info_jailbird : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "累犯#惯犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-7:团伙
	 */
	info_gang_crime : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "团伙#多人#结伙",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-8:退赃退赔
	 */
	info_compensation : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "退出#退钱#退还#退赃#退赔#退缴#退回#清退#归还#退清#返还#退换#发还",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-9:积极赔偿
	 */
	info_active_compensation : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "赔偿",
			reverseRegex : "(依法予以|应)[^,，\\.。;；:：]*?赔偿",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-10:谅解
	 */
	info_understanding : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "谅解#原谅",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-11:坦白
	 */
	info_confession : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "坦白#如实供述",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-12:认罪
	 */
	info_confession_incourt : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "(当庭|庭审)[\\u4e00-\\u9fa5]*?认罪",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-13:自首
	 */
	info_surrender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "自首",
			reverseRegex : "(判决|裁定)如下|不[^,，\\.。;；:：]*?认定[^,，\\.。;；:：]*?自首",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-14:立功
	 */
	info_merits_gain : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "立功",
			reverseRegex : "(判决|裁定)如下|提出[^,，\\.。;；:：]*?立功",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-15:悔罪悔改
	 */
	info_contrition : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "悔罪#悔改",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-16:刑事和解
	 */
	info_criminal_reconciliation : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "和解",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "和解(书|协议)|达成(刑事)?和解",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-17:流窜作案
	 */
	info_crime_multiple_spot : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "流窜",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-18:教唆犯
	 */
	info_abetment : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "教唆犯#教唆",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-19:胁从犯
	 */
	info_coerced_offender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "胁从犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-20:预备犯
	 */
	info_prepared_offender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "预备犯#(系|属于)[\\u4e00-\\u9fa5]*?犯罪预备",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-21:中止犯
	 */
	info_aborted_offender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "中止犯#中止#自动放弃犯罪",
			reverseRegex : "[不非][^,，\\.。;；:：]*?中止",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-22:未遂犯
	 */
	info_incomplete_offender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "未遂犯#未遂",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-23:被胁迫参加犯罪
	 */
	info_duress_offender : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "被胁迫",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-24:教唆未成年人犯罪
	 */
	info_minor_abetment : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "教唆(限制行为能力|聋哑|又聋又哑)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "教唆(未成年人|[未不]满(18|十八)周?岁)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-25:教唆他人犯罪但被教唆者未犯被教唆的罪
	 */
	info_abetment_unfulfilled : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "教唆[^,，\\.。;；、]*?犯罪[^,，\\.。;；、]*?(未|没有|无)(参与|作案)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-26:从重
	 */
	info_degree_heavier : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "从重",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-27:从轻
	 */
	info_degree_lower : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "从轻",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-28:防卫过当
	 */
	info_excessive_defence : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "防卫过当",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-29:避险过当
	 */
	info_excessive_danger_avoid : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "避险过当",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-30:避险过当
	 */
	info_mental_disorder : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "精神分裂#精神病#精神发育#精神残疾#精神有问题",
			reverseRegex : "无精神病",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "精神分裂#精神病#精神发育#精神残疾#精神有问题",
			reverseRegex : "无精神病",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-31:又聋又哑
	 */
	info_deaf_mute : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "又聋又哑#聋哑",
			reverseRegex : "无精神病",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-32:被害人有过错
	 */
	info_defendant_wrongdoing : {
		params : [ {
			tagList : "court_opinion",
			regex : "((([被受])害|当事)人|其)[^,，;；\\.。]*?(有|存在)[^,，;；\\.。]*?(过错|责任)|双方[均都]有(责任|过错)",
			reverseRegex : "(无|没有|不)[^,，;；\\.。]*?(证据|证实|证明)[^,，;；\\.。]*?(过错)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-33:无罪理由
	 */
	info_innocent_reason : {
		params : [ {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "免于刑事处罚",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			range : "1"
		} ],
		method : "rule_func_extract_litigant_sentenceList_info"
	},
	/**
	 * criminal point-34:被害人信息——被害人为孕妇
	 */
	info_victim_pregnant : {
		params : [ {
			tagList : "facts_found",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(怀孕|身孕|孕妇|受孕)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-35:被害人信息——被害人为未成年人
	 */
	info_victim_minor : {
		params : [ {
			tagList : "facts_found",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。有\\d]*?(未成年|不满18岁|([1-9]|1[0-7])岁)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-36:被害人信息——被害人为老年人
	 */
	info_victim_old : {
		params : [
				{
					tagList : "facts_found",
					regex : "(老(年)?人|年[龄纪]大)[^;；\\.。有]*?(([被受])害人|死者|伤者|交通事故)"
				},
				{
					tagList : "facts_found",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。有\\d]*?(老(年)?人|[6-9][0-9]岁|享年[6-9][0-9]|年纪大)"
				} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-37:被害人信息——被害人为残疾人
	 */
	info_victim_disabled : {
		params : [ {
			tagList : "facts_found",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?[为是有属][^;；\\.。]*?(残疾)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal points:判罚信息,包含一系列信息点
	 */
	penalties : {
		params : [ {
			tagList : "judgement_content"
		} ],
		method : "rule_func_extract_criminal_sentence"
	},
	/**
	 * criminal point-38:驱逐出境
	 */
	info_deportation : {
		params : [ {
			tagList : "judgement_content",
			regex : "驱逐出境",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
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
