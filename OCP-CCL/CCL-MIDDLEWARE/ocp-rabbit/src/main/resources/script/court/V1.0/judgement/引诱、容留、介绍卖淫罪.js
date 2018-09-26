var NAMESPACE_COURT_JUDGEMENT_引诱X容留X介绍卖淫罪 = {// 注：key中的"、"用"X"替换
	/**
	 * point-1:引诱、容留、介绍已满十八周岁人数
	 */
	info_seduce_adult_num : {
		params : [ {
			tagList : "facts_found#facts_found_cmpl#court_opinion",
			regex : "(引诱|介绍|容留|招聘)(卖淫女)?[^,，\\.。;；:：]*?(卖淫|性交易)",
			cacheKey : "meta_people_name2obj"
		} ],
		method : "rule_func_extract_litigant_people_num",
		adjust : [ {
			params : [
					{
						tagList : "facts_found",
						cacheKey : "meta_people_name2obj",
						regex : "(引诱|容留|介绍)[^,，\\.。;；:：]*?[\\d一二三四五六七八九十两]+[人名]",
						meanWhile : "0",
						order : "1",
						defaultAll : "0",
						type : "其他",
						unit : "int"
					},
					{
						tagList : "court_opinion",
						cacheKey : "meta_people_name2obj",
						regex : "(引诱|容留|介绍)[^,，\\.。;；:：]*?[\\d一二三四五六七八九十两]+[人名][^,，\\.。;；:：]*?卖淫",
						reverseRegex : "[\\d一二三四五六七八九十两]+[人名][^,，\\.。;；:：]*?(幼女|[未不]满(十四|14|十八|18)周?岁|未成年)",
						meanWhile : "0",
						order : "1",
						defaultAll : "0",
						type : "其他",
						unit : "int"
					} ],
			method : "rule_func_extract_litigant_double_info"
		} ]
	},
	/**
	 * point-2:引诱、容留、介绍已满十四周岁不满十八周岁人数
	 */
	info_seduce_teen_num : {
		params : [ {
			tagList : "facts_found#facts_found_cmpl#court_opinion",
			regex : "(引诱|介绍|容留|招聘)(卖淫女)?[^,，\\.。;；:：]*?(卖淫|性交易)",
			cacheKey : "meta_people_name2obj"
		} ],
		method : "rule_func_extract_litigant_people_num",
		adjust : [ {
			params : [
					{
						tagList : "facts_found",
						cacheKey : "meta_people_name2obj",
						regex : "[\\d一二三四五六七八九十两]+名(未成年|[未不]满(十八|18)周?岁)|[\\d一二三四五六七八九十两]+[名人][\\u4e00-\\u9fae]*?[系是](未成年|[未不]满(十八|18)周?岁)",
						meanWhile : "0",
						order : "1",
						defaultAll : "0",
						type : "其他",
						unit : "int"
					},
					{
						tagList : "court_opinion",
						cacheKey : "meta_people_name2obj",
						regex : "[\\d一二三四五六七八九十两]+名(未成年|[未不]满(十八|18)周?岁)|[\\d一二三四五六七八九十两]+[名人][\\u4e00-\\u9fae]*?[系是](未成年|[未不]满(十八|18)周?岁)",
						meanWhile : "0",
						order : "1",
						defaultAll : "0",
						type : "其他",
						unit : "int"
					} ],
			method : "rule_func_extract_litigant_double_info"
		} ]
	},
	/**
	 * point-3:引诱、容留、介绍不满十四周岁人数
	 */
	info_seduce_young_num : {
		params : [ {
			tagList : "facts_found#facts_found_cmpl#court_opinion",
			regex : "(引诱|介绍|容留|招聘)(卖淫女)?[^,，\\.。;；:：]*?(卖淫|性交易)",
			cacheKey : "meta_people_name2obj"
		} ],
		method : "rule_func_extract_litigant_people_num",
		adjust : [ {
			params : [
					{
						tagList : "facts_found",
						cacheKey : "meta_people_name2obj",
						regex : "[\\d一二三四五六七八九十两]+名(幼女|[未不]满(十四|14)周?岁)|[\\d一二三四五六七八九十两]+[名人][\\u4e00-\\u9fae]*?[系是](幼女|[未不]满(十四|14)周?岁)",
						meanWhile : "0",
						order : "1",
						defaultAll : "0",
						type : "其他",
						unit : "int"
					},
					{
						tagList : "facts_found",
						cacheKey : "meta_people_name2obj",
						regex : "[\\d一二三四五六七八九十两]+名(幼女|[未不]满(十四|14)周?岁)|[\\d一二三四五六七八九十两]+[名人][\\u4e00-\\u9fae]*?[系是](幼女|[未不]满(十四|14)周?岁)",
						meanWhile : "0",
						order : "1",
						defaultAll : "0",
						type : "其他",
						unit : "int"
					} ],
			method : "rule_func_extract_litigant_double_info"
		} ]
	},
	/**
	 * point-4:卖淫者患有严重性病
	 */
	info_prostitute_std : {
		params : [ {
			tagList : "office_opinion",
			regex : "患有严重性病|梅毒",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "facts_found",
			regex : "患有严重性病|梅毒",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "court_opinion",
			regex : "患有严重性病|梅毒",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-5:公共场所介绍卖淫
	 */
	info_prostitute_public : {
		params : [ {
			tagList : "office_opinion",
			regex : "公共场所",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "公共场所",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-6:单位主要负责人
	 */
	info_prostitute_main_responsible : {
		params : [ {
			tagList : "office_opinion",
			regex : "单位主要负责人|经营|开[办设]",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			regex : "单位主要负责人|经营|开[办设]",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "单位主要负责人|经营|开[办设]",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-7:利用互联网小广告等介绍卖淫
	 */
	info_prostitute_internet : {
		params : [ {
			tagList : "office_opinion",
			regex : "(卖淫|招嫖)信息|QQ|微信|招嫖卡片|发卡招嫖",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "facts_found",
			regex : "(卖淫|招嫖)信息|QQ|微信|招嫖卡片|发卡招嫖",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "(卖淫|招嫖)信息|QQ|微信|招嫖卡片|发卡招嫖",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-8:曾因组织、强迫、引诱、容留、介绍他人卖淫受过行政处罚或者刑事追究的
	 */
	info_prostitute_punish : {
		params : [ {
			tagList : "office_opinion",
			regex : "因犯?(容留|介绍|引诱)[^;；：:\\.。]*?(刑事|行政)[^,，；;:：\\.。]*?处罚",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		}, {
			tagList : "court_opinion",
			regex : "因犯?(容留|介绍|引诱)[^;；：:\\.。]*?(刑事|行政)[^,，；;:：\\.。]*?处罚",
			cacheKey : "meta_people_name2obj",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info",
		adjust : [ {
			params : [ {
				crimeName : "引诱、容留、介绍卖淫罪",
				cacheKey : "meta_people_name2obj"
			} ],
			method : "rule_func_adjust_punished"
		} ]
	},
	/**
	 * point-9:引诱、介绍他人到境外卖淫或者引诱、容留、介绍境外人员到境内
	 */
	info_prostitute_abroad : {
		params : [ {
			tagList : "facts_found#facts_found_cmpl",
			cacheKey : "meta_people_name2obj"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj"
		} ],
		method : "rule_func_extract_abroad_prostitute"
	},
	/**
	 * point-10:引诱、容留、介绍次数
	 */
	info_seduce_shelter_times : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "卖淫约?[\\d一二三四五六七八九十两]+[余人]?次|共[\\u4e00-\\u9fae\\d]*?次",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "int"
		}, {
			tagList : "court_opinion",
			cacheKey : "meta_people_name2obj",
			regex : "卖淫约?[\\d一二三四五六七八九十两]+[余人]?次|共[\\u4e00-\\u9fae\\d]*?次",
			meanWhile : "0",
			order : "1",
			defaultAll : "0",
			type : "其他",
			unit : "int"
		} ],
		method : "rule_func_extract_litigant_double_info"
	},
	/**
	 * point-11:突发事件期间犯罪
	 */
	info_crime_while_emergency : {
		params : [ {
			tagList : "facts_found",
			cacheKey : "meta_people_name2obj",
			regex : "突发事件|地震|洪涝|灾害|社会安全事件",
			meanWhile : "0",
			order : "1",
			defaultAll : "0"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	}
};