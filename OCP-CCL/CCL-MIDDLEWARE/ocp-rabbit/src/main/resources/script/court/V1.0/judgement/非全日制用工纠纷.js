var NAMESPACE_COURT_JUDGEMENT_非全日制用工纠纷 = {
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
	 * point-2:支付经济赔偿金原因
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
	 * point-3:每周工作时间
	 */
	info_work_time_per_week : {
		params : [ {
			tagList : "court_opinion",
			regex : "(?:一周|每周)[^,，\\.。;；、]*工作(\\d*)小时",
			reverse : "0",
			unit : "小时"
		} ],
		method : "rule_func_extract_time",
		adjust : [ {
			params : [ {
				valueNodes : "0#6#12#24",
				valueRanges : "0-6时#6-12时#12-24时#24时以上"
			} ],
			method : "rule_func_adjust_value_to_range"
		} ]
	},
	/**
	 * point-4:工资计算方式
	 */
	info_salary_calc_method : {
		params : [
				{
					tagList : "court_opinion",
					regex : "(按|以)[^,，\\.。;；、]*(小时|工时|天|时间|时长)[^,，\\.。;；、]*(酬|工资|薪)|计时[^,，\\.。;；、]*(酬|工资|薪)",
					capture : "计时"
				},
				{
					tagList : "court_opinion",
					regex : "(按|以)[^,，\\.。;；、]*(件|个|量)[^,，\\.。;；、]*(酬|工资|薪)|计件[^,，\\.。;；、]*(酬|工资|薪)",
					capture : "计件"
				} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-5:工资结算周期
	 */
	info_salary_calc_cycle : {
		params : [ {
			tagList : "court_opinion",
			regex : "次[^,，\\.。;；、也|或]*(结|领|发放)",
			capture : "单次"
		}, {
			tagList : "court_opinion",
			regex : "(天|日)[^,，\\.。;；、也|或]*(结|领|发放)",
			capture : "每天"
		}, {
			tagList : "court_opinion",
			regex : "(周|星期)[^,，\\.。;；、也|或]*(结|领|发放)",
			capture : "每周"
		}, {
			tagList : "court_opinion",
			regex : "(15天|两周)[^,，\\.。;；、也|或]*(结|领|发放)",
			capture : "15天"
		}, {
			tagList : "court_opinion",
			regex : "月[^,，\\.。;；、也|或]*(结|领|发放)",
			capture : "15天以上"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	/**
	 * point-6:解除合同提起方
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
	 * point-7:劳动关系认定
	 */
	info_labor_relation_affirm : {
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "(有效|存在|是|系|有)[^\\.。,，;；]*?劳动关系|劳动关系[^\\.。;；不]*?(有效|存在|合法)",
			capture : "存在",
			reverseRegex : "(不|没有|否)[^\\.。,，;；]*?劳动"
		}, {
			tagList : "judgement_content",
			regex : "(无效|不存在)[^\\.。,，;；]*?劳动关系|劳动关系[^\\.。,，;；]*?(无效|不存在|不合法)",
			capture : "不存在"
		} ],
		method : "rule_func_extract_info_by_regex"
	}
};