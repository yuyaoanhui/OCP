var NAMESPACE_COURT_VERDICT_减刑假释 = {
	/**
	 * point-1:记录时间
	 */
	meta_doc_date : {
		params : [ {
			tagList : "record_date",
			regex : "^(.*?)$",
			reverse : "0",
			type : "日期"
		} ],
		method : "rule_func_extract_time",
		adjust : [ {
			params : [ {
				tagList : "case_summary#judgement_content",
			} ],
			method : "rule_func_extract_commutation_parole"
		} ]
	}
};