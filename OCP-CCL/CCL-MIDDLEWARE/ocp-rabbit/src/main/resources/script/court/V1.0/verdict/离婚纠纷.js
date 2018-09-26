var NAMESPACE_COURT_VERDICT_离婚纠纷 = {
	/**
	 * point-1:涉及房产分配
	 */
	info_housedes : {
		params : [ {
			tagList : "plaintiff_args",
			regex : "(房产|房屋|房子).*?(划分|分配|归|判)?"
		}, {
			tagList : "plaintiff_args",
			regex : "(房产|房屋|房子).*?(划分|分配|归|判)?"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-2:涉及小孩抚养权
	 */
	info_childraise : {
		params : [
				{
					tagList : "plaintiff_args#plaintiff_args_original",
					regex : "(婚生子|婚生女|女儿|子女|生子|生女|儿子|女儿|男孩|女孩|小孩|孩子|之子|之女|^子|^儿|^孩|^女|[一二两三长次][子女])[^;；。\\.]*?[由随跟归][^,，。；;]*"
				},
				{
					tagList : "plaintiff_args#plaintiff_args_original",
					regex : "(要求|由|归|随|跟)[^,，\\.。；;]*?抚养"
				},
				{
					tagList : "defendant_args",
					regex : "(婚生子|婚生女|女儿|子女|生子|生女|儿子|女儿|男孩|女孩|小孩|孩子|之子|之女|^子|^儿|^孩|^女|[一二两三长次][子女])[^;；。\\.]*?[由随跟归][^,，。；;]*"
				}, {
					tagList : "plaintiff_args#plaintiff_args_original",
					regex : "(要求|由|归|随|跟)[^,，\\.。；;]*?抚养"
				} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-3:同意离婚
	 */
	info_divorce_agree_divorce : {
		params : [ {
			tagList : "defendant_args",
			regex : "[^不](同意|愿意?)[^,，。；]*?离婚|感情[^未没不]*?破裂"
		}, {
			tagList : "court_opinion",
			regex : "被告[^不](同意|愿意?)[^,，。；]*?离婚"
		} ],
		method : "rule_func_judge_whether_by_regex"
	},
	/**
	 * point-4:礼金
	 */
	info_divorce_gift_money : {
		params : [ {
			tagList : "plaintiff_args",
			regex : "礼金|彩礼"
		}, {
			tagList : "facts_found",
			regex : "礼金|彩礼"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-5:结婚时间
	 */
	info_marriage_date : {
		params : [ {
			tagList : "plaintiff_args",
			regex : "[\\d]+年[\\d]+月([\\d]+日)?[^;；。,，]*?结婚",
			defaultVal : "0",
			type : "日期"
		}, {
			tagList : "defendant_args",
			regex : "[\\d]+年[\\d]+月([\\d]+日)?[^;；。,，]*?结婚",
			defaultVal : "0",
			type : "日期"
		}, {
			tagList : "facts_found#facts_found_primary#facts_found_base",
			regex : "[\\d]+年[\\d]+月([\\d]+日)?[^;；。,，]*?结婚",
			defaultVal : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_time",
        adjust : [ {
            params : [ {
                tagList : "facts_found_base#facts_found_primary#facts_found#plaintiff_args",
                dependentPoints:"info_divorce_alimony_payment_freq#info_divorce_alimony_father#info_divorce_alimony_mother#info_divorce_raiser_boy#info_divorce_raiser_girl#info_divorce_all_kid_raiser"
            } ],
            method : "rule_func_extract_divorce"
        } ]
	},
	/**
	 * point-6:结婚原因
	 */
	info_marriage_type : {
		params : [ {
			tagList : "plaintiff_args",
			regex : "买卖婚姻",
			capture : "买卖婚姻"
		}, {
			tagList : "plaintiff_args",
			regex : "经[一-龥]{0,3}介绍",
			capture : "经人介绍",
			defaultVal : "自由恋爱"
		}, {
			tagList : "facts_found",
			regex : "买卖婚姻",
			capture : "买卖婚姻"
		}, {
			tagList : "facts_found",
			regex : "经[一-龥]{0,3}介绍",
			capture : "经人介绍",
			defaultVal : "自由恋爱"
		}, {
			tagList : "court_opinion",
			regex : "买卖婚姻",
			capture : "买卖婚姻"
		}, {
			tagList : "court_opinion",
			regex : "经[一-龥]{0,3}介绍",
			capture : "经人介绍",
			defaultVal : "自由恋爱"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-7:婚外情
	 */
	info_affair_marriage : {
		params : [ {
			tagList : "facts_found",
			regex : "婚外情|小三|暧昧|第三者|情人|外遇|出轨|包养|不正当关系|与他人同居|婚外异性"
		}, {
			tagList : "court_opinion",
			regex : "婚外情|小三|暧昧|第三者|情人|外遇|出轨|包养|不正当关系|与他人同居|婚外异性"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-8:被告有过错
	 */
	info_respondent_wrongdoing : {
		params : [ {
			tagList : "facts_found",
			regex : "被告[一-龥]*?(过错|家庭暴力|打了|吸毒|吸食毒品|赌博|婚外情|不正当关系|同居)"
		}, {
			tagList : "court_opinion",
			regex : "被告[一-龥]*?(过错|家庭暴力|打了|吸毒|吸食毒品|赌博|婚外情|不正当关系|同居)"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-9:实施家庭暴力
	 */
	info_violence : {
		params : [ {
			tagList : "facts_found",
			regex : "暴力|虐待|遗弃|殴打|打了"
		}, {
			tagList : "court_opinion",
			regex : "暴力|虐待|遗弃|殴打|打了"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-10:有赌博毒品等恶习
	 */
	info_drug_gambling : {
		params : [ {
			tagList : "facts_found",
			regex : "赌博|吸毒|酗酒|饮酒"
		}, {
			tagList : "court_opinion",
			regex : "赌博|吸毒|酗酒|饮酒"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-11:经济补偿
	 */
	info_eco_compensation : {
		params : [ {
			tagList : "facts_found",
			regex : "补偿"
		}, {
			tagList : "judgement_content",
			regex : "补偿|赔偿|(经济|生活)帮助|生活困难补助费|抚慰金"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-12:曾经起诉离婚
	 */
	info_suiteefore : {
		params : [
				{
					tagList : "facts_found",
					regex : "(判决)?(不|驳回|撤回)([^,，;；。]*)(离婚|起诉|诉讼|请求)"
				},
				{
					tagList : "facts_found",
					regex : "(离婚|起诉|诉讼|请求).*(((未|不)([^,，;；。]*)准许)|驳回|撤回)"
				},
				{
					tagList : "facts_found",
					regex : "(曾.*?([两二三四五六七八])?|再)次([一-龥]+)?(具状|起诉|提起诉讼|诉至|要求([一-龥]+)?离婚|提出|诉求|提起)"
				},
				{
					tagList : "court_opinion",
					regex : "(判决)?(不|驳回|撤回)([^,，;；。]*)(离婚|起诉|诉讼|请求)"
				},
				{
					tagList : "court_opinion",
					regex : "(离婚|起诉|诉讼|请求).*(((未|不)([^,，;；。]*)准许)|驳回|撤回)"
				},
				{
					tagList : "court_opinion",
					regex : "(曾.*?([两二三四五六七八])?|再)次([一-龥]+)?(具状|起诉|提起诉讼|诉至|要求([一-龥]+)?离婚|提出|诉求|提起)"
				} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-13:离婚原因
	 */
	info_cause_divorce : {
		params : [ {
			tagList : "facts_found",
			regex : "下落不明|不知去向|失踪至今",
			capture : "下落不明"
		}, {
			tagList : "facts_found",
			regex : "不务正业|游手好闲",
			capture : "不务正业"
		}, {
			tagList : "facts_found",
			regex : "不履行(夫妻|家庭)义务",
			capture : "不履行家庭义务"
		}, {
			tagList : "facts_found",
			regex : "冷暴力",
			capture : "冷暴力"
		}, {
			tagList : "facts_found",
			regex : "买卖婚姻|包办(买卖)?婚姻",
			capture : "包办买卖婚姻"
		}, {
			tagList : "facts_found",
			regex : "双方分居|开始分居",
			capture : "双方分居"
		}, {
			tagList : "facts_found",
			regex : "婆媳关系不(和|合)",
			capture : "婆媳关系不合"
		}, {
			tagList : "facts_found",
			regex : "婚前感情(基础)?薄弱|婚前了解(不够|甚少)|婚前缺乏(必要的)?(相互)?了解|草率结婚|婚前相识时间较短",
			capture : "婚前缺乏了解"
		}, {
			tagList : "facts_found",
			regex : "婚外情|小三|暧昧|第三者|情人|外遇|出轨|包养|不正当关系",
			capture : "婚外情"
		}, {
			tagList : "facts_found",
			regex : "开始分居|互不履行夫妻义务|婚姻生活不和谐|经常吵架",
			capture : "婚姻生活不和谐"
		}, {
			tagList : "facts_found",
			regex : "家暴|家庭暴力|多次对原告打骂|拳脚相加|殴打|拳打脚踢|打了我",
			capture : "家庭暴力"
		}, {
			tagList : "facts_found",
			regex : "性格不(合|投|搭)|网络认识|性格相差太远|性格差异大",
			capture : "性格不合"
		}, {
			tagList : "facts_found",
			regex : "患有精神病",
			capture : "患有精神病"
		}, {
			tagList : "facts_found",
			regex : "房产(证)?纠纷",
			capture : "房产证纠纷"
		}, {
			tagList : "facts_found",
			regex : "无法正常生活",
			capture : "无法正常生活"
		}, {
			tagList : "facts_found",
			regex : "无法沟通|无法交流",
			capture : "无法沟通"
		}, {
			tagList : "facts_found",
			regex : "无法相处",
			capture : "无法相处"
		}, {
			tagList : "facts_found",
			regex : "生活习惯不同",
			capture : "生活习惯不同"
		}, {
			tagList : "facts_found",
			regex : "(家庭|生活|家务)琐事|生活作风不好|常发生纠纷|因琐事发生争吵",
			capture : "生活家庭琐事"
		}, {
			tagList : "facts_found",
			regex : "生理缺陷|患有疾病",
			capture : "生理缺陷"
		}, {
			tagList : "facts_found",
			regex : "离家(不归|出走|外出)|至今(未归|不回)|回(到)?娘家|在外[^,，\\.。；;]*?居住",
			capture : "离家出走"
		}, {
			tagList : "facts_found",
			regex : "无法沟通|交流频少|缺[乏少](一定的)?(沟通|交流)",
			capture : "缺少交流"
		}, {
			tagList : "facts_found",
			regex : "草率结婚",
			capture : "草率结婚"
		}, {
			tagList : "facts_found",
			regex : "经常赌博|嗜赌|好赌",
			capture : "赌博"
		}, {
			tagList : "facts_found",
			regex : "违法犯罪行为|判处有期徒刑|服刑|违法犯罪|犯罪|坐牢|服刑|吸毒",
			capture : "违法犯罪行为"
		}, {
			tagList : "facts_found",
			regex : "酗酒|嗜酒",
			capture : "酗酒"
		}, {
			tagList : "facts_found",
			regex : "重婚",
			capture : "重婚"
		}, {
			tagList : "facts_found",
			regex : "分居(至今|两地|生活)|分居[一二三四五六七八九十两\\d]+年|长期分居",
			capture : "长期分居"
		}, {
			tagList : "facts_found",
			regex : "骗婚",
			capture : "骗婚"
		},

		{
			tagList : "court_opinion",
			regex : "下落不明|不知去向|失踪至今",
			capture : "下落不明"
		}, {
			tagList : "court_opinion",
			regex : "不务正业|游手好闲",
			capture : "不务正业"
		}, {
			tagList : "court_opinion",
			regex : "不履行(夫妻|家庭)义务",
			capture : "不履行家庭义务"
		}, {
			tagList : "court_opinion",
			regex : "冷暴力",
			capture : "冷暴力"
		}, {
			tagList : "court_opinion",
			regex : "买卖婚姻|包办(买卖)?婚姻",
			capture : "包办买卖婚姻"
		}, {
			tagList : "court_opinion",
			regex : "双方分居|开始分居",
			capture : "双方分居"
		}, {
			tagList : "court_opinion",
			regex : "婆媳关系不(和|合)",
			capture : "婆媳关系不合"
		}, {
			tagList : "court_opinion",
			regex : "婚前感情(基础)?薄弱|婚前了解(不够|甚少)|婚前缺乏(必要的)?(相互)?了解|草率结婚|婚前相识时间较短",
			capture : "婚前缺乏了解"
		}, {
			tagList : "court_opinion",
			regex : "婚外情|小三|暧昧|第三者|情人|外遇|出轨|包养|不正当关系",
			capture : "婚外情"
		}, {
			tagList : "court_opinion",
			regex : "开始分居|互不履行夫妻义务|婚姻生活不和谐|经常吵架",
			capture : "婚姻生活不和谐"
		}, {
			tagList : "court_opinion",
			regex : "家暴|家庭暴力|多次对原告打骂|拳脚相加|殴打|拳打脚踢|打了我",
			capture : "家庭暴力"
		}, {
			tagList : "court_opinion",
			regex : "性格不(合|投|搭)|网络认识|性格相差太远|性格差异大",
			capture : "性格不合"
		}, {
			tagList : "court_opinion",
			regex : "患有精神病",
			capture : "患有精神病"
		}, {
			tagList : "court_opinion",
			regex : "房产(证)?纠纷",
			capture : "房产证纠纷"
		}, {
			tagList : "court_opinion",
			regex : "无法正常生活",
			capture : "无法正常生活"
		}, {
			tagList : "court_opinion",
			regex : "无法沟通|无法交流",
			capture : "无法沟通"
		}, {
			tagList : "court_opinion",
			regex : "无法相处",
			capture : "无法相处"
		}, {
			tagList : "court_opinion",
			regex : "生活习惯不同",
			capture : "生活习惯不同"
		}, {
			tagList : "court_opinion",
			regex : "(家庭|生活|家务)琐事|生活作风不好|常发生纠纷|因琐事发生争吵",
			capture : "生活家庭琐事"
		}, {
			tagList : "court_opinion",
			regex : "生理缺陷|患有疾病",
			capture : "生理缺陷"
		}, {
			tagList : "court_opinion",
			regex : "离家(不归|出走|外出)|至今(未归|不回)|回(到)?娘家|在外[^,，\\.。；;]*?居住",
			capture : "离家出走"
		}, {
			tagList : "court_opinion",
			regex : "无法沟通|交流频少|缺[乏少](一定的)?(沟通|交流)",
			capture : "缺少交流"
		}, {
			tagList : "court_opinion",
			regex : "草率结婚",
			capture : "草率结婚"
		}, {
			tagList : "court_opinion",
			regex : "经常赌博|嗜赌|好赌",
			capture : "赌博"
		}, {
			tagList : "court_opinion",
			regex : "违法犯罪行为|判处有期徒刑|服刑|违法犯罪|犯罪|坐牢|服刑|吸毒",
			capture : "违法犯罪行为"
		}, {
			tagList : "court_opinion",
			regex : "酗酒|嗜酒",
			capture : "酗酒"
		}, {
			tagList : "court_opinion",
			regex : "重婚",
			capture : "重婚"
		}, {
			tagList : "court_opinion",
			regex : "分居(至今|两地|生活)|分居[一二三四五六七八九十两\\d]+年|长期分居",
			capture : "长期分居"
		}, {
			tagList : "court_opinion",
			regex : "骗婚",
			capture : "骗婚"
		} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-14:重婚
	 */
	info_divorce_bigamy : {
		params : [ {
			tagList : "facts_found#facts_found_base",
			regex : "[^）尊]重婚",
			reverseRegex : "证据不足|没有证据|不能证明|无证据证明|未提交证据"
		}, {
			tagList : "court_opinion#court_base_opinion",
			regex : "重婚",
			reverseRegex : "证据不足|没有证据|不能证明|无证据证明|未提交证据"
		} ],
		method : "rule_func_judge_truth_by_regex"
	},
	/**
	 * point-15:共同财产
	 */
	info_divorce_common_property : {
		params : [ {
			tagList : "facts_found",
			regex : "房屋|住房",
			capture : "房屋"
		}, {
			tagList : "facts_found",
			regex : "车辆",
			capture : "车辆"
		}, {
			tagList : "facts_found",
			regex : "股权|股票",
			capture : "股权"
		}, {
			tagList : "facts_found",
			regex : "存款",
			capture : "存款"
		}, {
			tagList : "facts_found",
			regex : "债权",
			capture : "债权"
		}, {
			tagList : "facts_found",
			regex : "投资收益",
			capture : "投资收益"
		}, {
			tagList : "court_opinion",
			regex : "房屋|住房",
			capture : "房屋"
		}, {
			tagList : "court_opinion",
			regex : "车辆",
			capture : "车辆"
		}, {
			tagList : "court_opinion",
			regex : "股权|股票",
			capture : "股权"
		}, {
			tagList : "court_opinion",
			regex : "存款",
			capture : "存款"
		}, {
			tagList : "court_opinion",
			regex : "债权",
			capture : "债权"
		}, {
			tagList : "court_opinion",
			regex : "投资收益",
			capture : "投资收益"
		} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-16:分居时间
	 */
	info_divorce_seperation_time : {
		params : [ {
			tagList : "facts_found",
			regex : "分居[^,，;；。\\.]*?[年月]",
            reverse : "0",
			unit : "月"
		} ],
		method : "rule_func_extract_time"
	}
};