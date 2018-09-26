var NAMESPACE_COURT_VERDICT_房屋买卖合同纠纷 = {
	info_contract_effect : {// point-1:合同效力
		params : [ {
			tagList : "court_opinion",
			regex : "(协议|合同)[^,，\\.。;；]*?(无效|废)",
			capture : "无效",
			reverseRegex : "不予(认定|支持|采信)"
		}, {
			tagList : "court_opinion",
			regex : "(协议|合同)[^\\.。;；]*?(合法|有效|予以(确认|确定|认定|肯定)|可信|系双方真实意思表示)",
			capture : "有效",
			reverseRegex : "(合同|协议)是否有效"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_contract_nature : {// point-2:合同性质
		params : [ {
			tagList : "facts_found",
			regex : "预[^,，》《\\.。;；]*?(合同|协议)",
			capture : "预约合同",
			defaultVal : "本约合同"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_house_uses_nature : {// point-3:房屋使用性质
		params : [ {
			tagList : "court_opinion#facts_found",
			regex : "住宅|商铺|商品房|营业房|(社|小)区|存量房",
			capture : "商品房"
		}, {
			tagList : "court_opinion#facts_found",
			regex : "公有(住)?房|公房|房改房|国有(企业|单位)分房",
			capture : "公有住房"
		}, {
			tagList : "court_opinion#facts_found",
			regex : "经济适用(房)?",
			capture : "经济适用房",
			type : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_payment_method : {// point-4:付款方式
		params : [ {
			tagList : "facts_found",
			regex : "分期|按揭|首付|预付|余款",
			capture : "分期"
		}, {
			tagList : "facts_found",
			regex : "按约付清|一次性|(给|付)[^\\.。;；]*?(全款|全额)|(全款|全额)[^\\.。;；]*?(给|付)",
			capture : "一次性"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_breach_contract_degree : {// point-5:违约程度
		params : [ {
			tagList : "court_opinion",
			regex : "(?!根本)违约",
			capture : "一般违约"
		}, {
			tagList : "court_opinion",
			regex : "(根本)违约",
			capture : "根本违约",
			reverseRegex : "(不宜认定|是否|不构成)[^,，；;\\.。]*?根本违约"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_contract_effect_judgement : {// point-6:合同效力判定
		params : [ {
			tagList : "judgement_content",
			regex : "(协议|合同)[^\\.。;；]*?(合法|有效|予以(确认|确定|认定|肯定)|可信|系双方真实意思表示)",
			capture : "有效"
		}, {
			tagList : "judgement_content",
			regex : "(协议|合同)[^,，\\.。;；]*?(无效|废)",
			capture : "无效"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_contract_fulfill_judgement : {// point-7:合同履行判定
		params : [ {
			tagList : "judgement_content",
			regex : "继续履行",
			capture : "继续履行"
		}, {
			tagList : "court_opinion#judgement_content",
			regex : "(违约解除|违法解除)",
			capture : "\\1"
		}, {
			tagList : "court_opinion#judgement_content",
			regex : "(协商|也同意)[^,，\\.。;；]*?(解除)",
			capture : "协商一致解除"
		}, {
			tagList : "judgement_content",
			regex : "解除[^,，\\.。;；]*?(协议|合同)",
			capture : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_interest_payment_class : {// point-8:支付利率类别
		params : [ {
			tagList : "judgement_content",
			regex : "按[^，,\\.。;；]*?(同期银行|金融机构|银行同期)[^,，\\.。;；]*?贷款(基准)?利率",
			capture : "同期银行贷款利率",
			defaultVal : "其他"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_payment_method_judgement : {// point-9:付款方式
		params : [ {
			tagList : "facts_found",
			regex : "[分按][期年月次]",
			capture : "分期"
		}, {
			tagList : "facts_found",
			regex : "一次性|给付|支付|返还",
			capture : "一次性"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_fine_rule_judgement : {// point-10:适用定金罚则
		params : [ {
			tagList : "judgement_content",
			regex : "定金[^,，\\.。;；、]*?(扣除|双倍)|(扣除|双倍)[^,，\\.。;；、]*?定金",
			capture : "适用",
			defaultVal : "不适用"
		} ],
		method : "rule_func_extract_info_by_regex"
	},
	info_breach_contract_rate : {// point-11:违约金计算基数
		params : [ {
			tagList : "court_opinion",
			regex : "合同约定",
			capture : "合同约定数额"
		}, {
			tagList : "court_opinion",
			regex : "损失",
			capture : "实际损失"
		}, {
			tagList : "court_opinion",
			regex : "银行同期贷款基准利率|同期银行贷款利率",
			capture : "可得利益损失"
		} ],
		method : "rule_func_extract_info_by_regex"
	}
};
