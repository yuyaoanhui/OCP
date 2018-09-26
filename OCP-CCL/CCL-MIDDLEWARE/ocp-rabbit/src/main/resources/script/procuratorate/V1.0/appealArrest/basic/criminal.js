var NAMESPACE_PROCURATORATE_APPEALARREST_criminal = {
	/**
	 * criminal point-1:主犯
	 */
	info_principle_criminal : {
		params : [ {
			tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "主犯|(主要|领导)作用",
			reverseRegex : "不[^，,\\.。;；、]*?[主从]犯|[主从]犯[^，,\\.。;；、]*?(没有|未)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-2:从犯
	 */
	info_accessory : {
		params : [ {
			tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "从犯(?!罪)|(次要)作用",
			reverseRegex : "(不|没有)[^，,\\.。;；、]*?主(犯)?(、)?从犯|[主从]犯[^，,\\.。;；、]*?(没有|未)|事实不清|证据不足|胁从犯",
			meanWhile : "0",
			order : "0",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-3:初犯偶犯
	 */
	info_offender : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "初犯[^\\.。;；]*?偶犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-4:累犯惯犯
	 */
	info_jailbird : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "累犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-5:团伙
	 */
	info_gang_crime : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "团伙|结伙",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-6:退赃退赔
	 */
	info_compensation : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "退赃|退赔|赔偿",
			reverseRegex : "([未没不无]|希望|要求)[^，,\\.。;；、]*?(退赃|退赔|赔偿)|有立功表现或者积极退赃",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-7:积极赔偿
	 */
	info_active_compensation : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "([赔补]偿|赔付)",
					reverseRegex : "([要诉请]求|[没未无不]|保险|公司)[\\u4e00-\\u9fa5\\d]*?([赔补]偿|赔付)",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				},
				{
					tagList : "HANDLE_OPINION#RISK_ANALYSIS#QUES_TO_EXPLAIN",
					cacheKey : "meta_people_name2obj",
					regex : "(积极|主动|达成)[^,，\\.。;；、]*?([赔补]偿|赔付)|([赔补]偿|赔付)[\\u4e00-\\u9fa5\\d]*?元",
					reverseRegex : "([要诉请]求|[没未无不]|保险|公司)[\\u4e00-\\u9fa5\\d]*?([赔补]偿|赔付)",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-8:谅解
	 */
	info_understanding : {
		params : [
				{
					tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "谅解",
					reverseRegex : "[无未不没劝][^\\.。;；:：]*?(谅解|原谅)|(谅解|原谅)[^\\.。;；:：]*?[无未没]",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "谅解书|([受被]害人)[^\\.。;；:：]*?谅解",
					reverseRegex : "[无未不没劝][^\\.。;；:：]*?(谅解|原谅)|(谅解|原谅)[^\\.。;；:：]*?[无未没]",
					meanWhile : "0",
					order : "1",
					defaultAll : "1"
				} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-9:坦白
	 */
	info_confession : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "坦白#如实供述",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-10:认罪
	 */
	info_confession_incourt : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "认罪|悔罪",
			reverseRegex : "([未没不无拒]|表现)[^，,\\.。;；、]*?(认罪|悔罪)|(认罪|悔罪)[^\\.。;；:：]*?([差]|机会)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-11:自首
	 */
	info_surrender : {
		params : [ {
			tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "自首|投案",
			reverseRegex : "[无未不没劝][^\\.。;；:：]*?(自首|投案)|(自首|投案)[^\\.。;；:：]*?[无未没]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-12:立功
	 */
	info_merits_gain : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "立功",
			reverseRegex : "([无未否不没]|提出|希望|争取)[^,，\\.。;；:：]*?立功|立功[^,，\\.。;；:：]*?([无未没]|提出)|关于处理自首和立功|有立功表现或者积极退赃",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-13:刑事和解
	 */
	info_criminal_reconciliation : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "[和调]解",
			reverseRegex : "[未没无][\\u4e00-\\u9fa5]*?[和调]解",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-14:流窜作案
	 */
	info_crime_multiple_spot : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "流窜",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-15:教唆犯
	 */
	info_abetment : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "教唆",
			reverseRegex : "[被在][^\\.。;；、]*?教唆",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "[被在][^\\.。;；、]*?教唆",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-16:胁从犯
	 */
	info_coerced_offender : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "胁从犯",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-17:预备犯
	 */
	info_prepared_offender : {
		params : [ {
			tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE#SOLVE_PROCESS",
			cacheKey : "meta_people_name2obj",
			regex : "(盗窃|犯)(罪)?[（(]?预备|预备[^\\.。;；、]*?(盗窃|犯罪)",
			reverseRegex : "不予认[定可]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-18:中止犯
	 */
	info_aborted_offender : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "(犯罪|盗窃)[（(]?中止",
			reverseRegex : "不予认[定可]",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-19:未遂犯
	 */
	info_incomplete_offender : {
		params : [ {
			tagList : "HANDLE_OPINION#CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "未遂",
			reverseRegex : "(没有|未|不)[^,，\\.。;；、]*?(未遂)",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-20:被胁迫参加犯罪
	 */
	info_duress_offender : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "被胁迫",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-21:教唆未成年人犯罪
	 */
	info_minor_abetment : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "教唆未成年人",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-22:防卫过当
	 */
	info_excessive_defence : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "防卫[^，,\\.。;；、]*?过当|过当[^，,\\.。;；、]*?防卫",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-23:避险过当
	 */
	info_excessive_danger_avoid : {
		params : [ {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "避险[^，,\\.。;；、]*?过当|过当[^，,\\.。;；、]*?避险",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-24:避险过当
	 */
	info_mental_disorder : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "精神分裂#精神病#精神发育#精神残疾#精神有问题",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-25:又聋又哑
	 */
	info_deaf_mute : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "又聋又哑#聋哑",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-26:被害人有过错
	 */
	info_defendant_wrongdoing : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "被害人[^，,\\.。;；、没不]*?(失误|过错)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-27:无罪理由
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
	 * criminal point-28:被害人信息——被害人为孕妇
	 */
	info_victim_pregnant : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(怀孕|身孕|孕妇|受孕)"
		}, {
			tagList : "HANDLE_OPINION",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?(怀孕|身孕|孕妇|受孕)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-29:被害人信息——被害人为未成年人
	 */
	info_victim_minor : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。有\\d]*?(未成年|不满18岁|([1-9]|1[0-7])岁)"
				},
				{
					tagList : "HANDLE_OPINION",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。有\\d]*?(未成年|不满18岁|([1-9]|1[0-7])岁)"
				} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-30:被害人信息——被害人为老年人
	 */
	info_victim_old : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。有\\d]*?(老(年)?人|[6-9][0-9]岁|享年[6-9][0-9]|年纪大)"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					regex : "(老(年)?人|年[龄纪]大)[^;；\\.。有]*?(([被受])害人|死者|伤者|交通事故)"
				},
				{
					tagList : "HANDLE_OPINION",
					regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。有\\d]*?(老(年)?人|[6-9][0-9]岁|享年[6-9][0-9]|年纪大)"
				}, {
					tagList : "HANDLE_OPINION",
					regex : "(老(年)?人|年[龄纪]大)[^;；\\.。有]*?(([被受])害人|死者|伤者|交通事故)"
				} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-31:被害人信息——被害人为残疾人
	 */
	info_victim_disabled : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?[为是有属][^;；\\.。]*?(残疾)"
		}, {
			tagList : "HANDLE_OPINION",
			regex : "(([被受])害人|死[者亡]|伤者)[^;；\\.。]*?[为是有属][^;；\\.。]*?(残疾)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * criminal point-32:盲人
	 */
	info_blind : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			regex : "盲|失明",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-33:主动归还
	 */
	info_active_return : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "(主动)[^,，\\.。;；、]*?归还",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-34:如实供述
	 */
	info_tell_truth : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "如实供述|主动供述",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal point-35:社会危险性
	 */
	info_social_risk : {
		params : [ {
			tagList : "HANDLE_OPINION",
			regex : "社会危[险害]性|有逮捕必要|社会矛盾",
			reverseRegex : "[未没无不][^，,\\.。;；、防止]*?(社会危[险害]性|逮捕必要|社会矛盾)|(社会危[险害]性|社会矛盾)[^，,\\.。;；、]*?([低小少])",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * criminal points:是否既遂犯
	 */
	accomplishedCrime : {
		params : [ {
			tagList : "HANDLE_OPINION",
			dependentPoints : "info_预备犯#info_中止犯#info_未遂犯"
		} ],
		method : "rule_func_handel_accomplete_offense"
	}
}