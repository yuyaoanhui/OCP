var NAMESPACE_COURT_JUDGEMENT_经济补偿金纠纷 = {
	/**
	 * point-1:劳动合同签订
	 */
	info_labor_contract_signing : {
		params : [
				{
					tagList : "facts_found#court_opinion",
					regex : "(签|订|达成)[^\\.。,，;；、]*(劳动|用人|用工|聘)[^\\.。;；、]*(合同|协议)",
					capture : "签订",
					reverseRegex : "(没有|未|无)[^\\.。,，;；、]*?(劳动|用人|用工|聘)[^\\.。;；、]*?(合同|协议)"
				},
				{
					tagList : "facts_found#court_opinion",
					regex : "(劳动|用人|用工|聘)[^\\.。,，;；、]*(合同|协议)[^\\.。;；、]*(合法|有效|认可)",
					capture : "签订"
				}, {
					tagList : "facts_found#court_opinion",
					regex : "(未|没有)[^\\.。,，;；、]*?(合同|协议)",
					capture : "未签订"
				}, {
					tagList : "facts_found#court_opinion",
					regex : "期限届满.*?继续.*?工作",
					capture : "过期未签订"
				} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-2:劳动合同期限
	 */
	info_labor_contract_term : {
		params : [ {
			tagList : "facts_found#court_opinion",
			regex : "期[^,，\\d年月日]*?([\\d年月日]+)[^,，\\d年月日]+([\\d年月日]+)[^\\d年月日]*?|(期限为|为期)([一二三四五六七八九十两\\d]+)年",
            reverse : "0",
			unit : "年"
		} ],
		method : "rule_func_extract_time",
		adjust : [ {
			params : [ {
				valueNodes : "0#1#3",
				valueRanges : "0-1年#1-3年#3年以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		}, {
			params : [ {
				tagList : "facts_found#court_opinion",
				regex : "无固定期限",
				capture : "无固定期限"
			} ],
			method : "rule_func_extract_info_by_regex"
		} ]
	},
	/**
	 * point-3:解除合同提起方
	 */
	info_dissolve_contract_speaker : {
		params : [
				{
					tagList : "court_opinion#facts_found",
					regex : "离(职|岗)|辞职|向[^,，\\.。;；、]*?(公司|店|单位)提出[^,，\\.。;；、]*?解除",
					capture : "劳动者"
				},
				{
					tagList : "court_opinion#facts_found",
					regex : "解雇|(辞|劝)退|(公司|店|单位)[^,，\\.。;；、]*?(通知|作出)[^,，\\.。;；、]*?(解除[^,，\\.。;；、]*?劳动(合同|关系)|劳动合同解除)|除名",
					capture : "用人单位"
				} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-4:解除合同原因
	 */
	info_dissolve_contract_reason : {
		params : [ {
			tagList : "court_opinion",
			regex : "同意[^\\.。;；、]*解除|解除[^\\.。;；、不]*同意|协商解除|离职",
			reverseRegex : "(不同意|违[法约])[^\\.。;；、]*?解除",
			capture : "双方协商"
		}, {
			tagList : "court_opinion",
			regex : "(违法解除|违约解除)",
			reverseRegex : "不予",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "解除[^\\.。;；、没不]*(违法)",
			capture : "违法解除"
		}, {
			tagList : "court_opinion",
			regex : "解除[^\\.。;；、没不]*(违约)",
			capture : "违法解除",
			defaultVal : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-5:经济补偿金计算时间
	 */
	info_economic_compensation_time : {
		params : [
				{
					tagList : "court_opinion#judgement_content",
					regex : "补偿[^\\.。;；、]*[\\d\\.]+元[（\\(][\\d\\.]+元(?:\\/|\\／)月(?:×|＊|x|X|\\*)([\\d\\.]+)(个?)月.*?[）\\)]",
					type : "月",
                    reverse : "0"
				},
				{
					tagList : "court_opinion#judgement_content",
					regex : "补偿[^\\.。;；、]*[\\d\\.]+元[（\\(]([\\d\\.]+)(个?)月(?:×|＊|x|X|\\*)[\\d\\.]+元(?:\\/|\\／)月[）\\)]",
					type : "月",
                    reverse : "0"
				},
				{
					tagList : "court_opinion#judgement_content",
					regex : "(?:补偿|计算)[^\\.。;；、]*[\\d\\.]+元(?:\\/|\\／)月(?:×|＊|x|X|\\*)([\\d\\.]+)(个?)月=([\\d\\.]+)元",
					type : "月",
                    reverse : "0"
				}, {
					tagList : "court_opinion#judgement_content",
					regex : "([\\d\\.一二三四五六七八九十两]+个(半?)月)[^\\.。,，;；、]*补偿",
					type : "月",
                	reverse : "0"
				} ],
		method : "rule_func_extract_time",
		adjust : [ {
			params : [ {
				valueNodes : "0#3#5#10#12",
				valueRanges : "0-3月#3-5月#5-10月#10-12月#12月以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-6:支付经济补偿金原因
	 */
	info_pay_compensation_reason : {
		params : [
				{
					tagList : "court_opinion",
					regex : "(未|无|没有)[^\\.。,，;；、]*(劳动|用|聘)[^\\.。,，;；、]*(合同|协议)[^\\.。;；、]*(赔偿)金",
					capture : "未订立书面劳动合同"
				},
				{
					tagList : "court_opinion",
					regex : "试用期[^\\.。,，;；、]*(超期|过长|超过)[^\\.。;；、]*(赔偿)金",
					capture : "试用期超期"
				},
				{
					tagList : "court_opinion",
					regex : "((未|无|没有)[^\\.。,，;；、]*(支付|给|付)|拖欠|拒绝)[^\\.。,，;；、]*报酬[^\\.。;；、]*(赔偿)金",
					capture : "拖欠工资"
				},
				{
					tagList : "court_opinion",
					regex : "(低|少|小于)[^\\.。,，;；、]*工资标准[^\\.。;；、]*(赔偿)金",
					capture : "低于最低工资标准"
				},
				{
					tagList : "court_opinion",
					regex : "(未|无|没有)[^\\.。,，;；、]*(加班费)[^\\.。;；、]*(赔偿)金",
					capture : "未支付加班费"
				},
				{
					tagList : "court_opinion",
					regex : "(未|无|没有)[^\\.。;；、]*(规定|约定|法规)[^\\.。;；、]*(补偿金)[^\\.。;；、]*(赔偿金)|(补偿金)[^\\.。;；、]*(未|无|没有)[^\\.。;；、]*(规定|约定|法规)[^\\.。;；、]*(赔偿金)",
					capture : "未按规定支付经济补偿金"
				},
				{
					tagList : "court_opinion",
					regex : "(违法[^\\.。,，;；、]*解除|解除[^\\.。,，;；、]*违法)[^\\.。;；、]*(赔偿金)",
					capture : "违法解除合同"
				} ],
		method : "rule_func_extract_multi_info_by_regex"
	},
	/**
	 * point-7:劳动关系认定
	 */
	info_labor_relation_affirm : {
		params : [ {
			tagList : "judgement_content",
			regex : "(有效|存在|是|系|有)[^\\.。,，;；]*?劳动关系|劳动关系[^\\.。;；不]*?(有效|存在|合法)",
			capture : "存在",
			reverseRegex : "(不|没有|否)[^\\.。,，;；]*?劳动"
		}, {
			tagList : "judgement_content",
			regex : "(无效|不存在)[^\\.。,，;；]*?劳动关系|劳动关系[^\\.。,，;；]*?(无效|不存在|不合法)",
			capture : "不存在"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-8:劳动合同履行判定
	 */
	info_labor_contract_determine : {
		params : [ {
			tagList : "judgement_content",
			regex : "继续履行",
			capture : "继续履行"
		}, {
			tagList : "court_opinion",
			regex : "(违约解除|违法解除)",
			capture : "\\1"
		}, {
			tagList : "court_opinion",
			regex : "协商[^,，\\.。;；]*?解除",
			capture : "协商一致解除",
			type : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	}
};