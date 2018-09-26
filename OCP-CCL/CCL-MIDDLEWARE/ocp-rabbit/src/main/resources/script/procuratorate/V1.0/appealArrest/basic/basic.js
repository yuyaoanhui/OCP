var NAMESPACE_PROCURATORATE_APPEALARREST_basic = {
	basicPrePoints : {// 要提前抽取的信息点,程序会根据文书案由自动选择部分执行
		base : [ "meta_document_type", "meta_signature_date",
				"suspectBaseInfo", "section_title", "section_title",
				"section_suspect_base_info", "section_solve_process",
				"section_case_fact_evidence", "section_handle_opinion",
				"section_signature" ],
		criminal : [ "info_principle_criminal", "info_accessory",
				"info_offender", "info_jailbird", "info_gang_crime",
				"info_compensation", "info_active_compensation",
				"info_understanding", "info_confession",
				"info_confession_incourt", "info_surrender",
				"info_merits_gain", "info_criminal_reconciliation",
				"info_crime_multiple_spot", "info_abetment",
				"info_coerced_offender", "info_prepared_offender",
				"info_aborted_offender", "info_incomplete_offender",
				"info_duress_offender", "info_minor_abetment",
				"info_excessive_defence", "info_excessive_danger_avoid",
				"info_mental_disorder", "info_deaf_mute",
				"info_defendant_wrongdoing", "info_innocent_reason",
				"info_victim_pregnant", "info_victim_minor", "info_victim_old",
				"info_victim_disabled", "info_blind", "info_active_return",
				"info_tell_truth", "info_social_risk", "accomplishedCrime" ]
	}
}