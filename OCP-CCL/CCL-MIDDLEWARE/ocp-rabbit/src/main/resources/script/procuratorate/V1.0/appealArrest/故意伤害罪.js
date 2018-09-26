var NAMESPACE_PROCURATORATE_APPEALARREST_故意伤害罪 = {
	/**
	 * point-1:积极施救
	 */
	info_rescus : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "(救助|施救|抢救|送往医院|送医)",
			reverseRegex : "[没未无不][^,，\\.。;；、]*?(救助|施救)",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "(救助|施救|抢救|送往医院|送医)",
			reverseRegex : "[没未无不][^,，\\.。;；、]*?(救助|施救)",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_boolean_info"
	},
	/**
	 * point-2:伤害后果
	 */
	info_damage_consequences : {
		params : [ {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤#重伤",
			capture : "轻微伤#轻伤#重伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "CASE_FACT_EVIDENCE",
			cacheKey : "meta_people_name2obj",
			regex : "死亡|致死",
			capture : "死亡",
			reverseRegex : "胎儿死亡",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "轻微伤#轻伤#重伤",
			capture : "轻微伤#轻伤#重伤",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		}, {
			tagList : "HANDLE_OPINION",
			cacheKey : "meta_people_name2obj",
			regex : "死亡|致死",
			capture : "死亡",
			reverseRegex : "胎儿死亡",
			meanWhile : "0",
			order : "1",
			defaultAll : "1"
		} ],
		method : "rule_func_extract_litigant_stringlist_info"
	},
	/**
	 * point-3:手段残忍程度
	 */
	info_cruelty_method : {
		params : [
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?眼睛|眼[^,，;；\\.。]*?脱落",
					capture : "挖人眼睛",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?耳朵",
					capture : "割人耳朵",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?鼻子",
					capture : "割人鼻子",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?脚筋",
					capture : "挑人脚筋",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?([手脚足臂]|四肢)|([手脚足臂]|四肢)[^,，;；\\.。]*?[挖割挑砍剜]",
					capture : "砍人手足",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?(髌骨|膝盖)",
					capture : "剜人髌骨",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "(以(锐器|硫酸|腐蚀性溶液)严重毁容)",
					capture : "\\1",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "((电击|烧烫)要害部位)",
					capture : "\\1",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "冷冻",
					capture : "冷冻",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "毒蛇猛兽撕咬",
					capture : "毒蛇猛兽撕咬",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "其他特别残忍手段",
					capture : "其他特别残忍手段",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "拳打脚踢|棍棒击打|互掐|推搡|撞击|挤压|[殴厮捶拳踢]打|[打踢撞撞]伤|打架",
					capture : "殴打",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "砸伤",
					capture : "砸伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "[捅砍刺扎划]伤|刀[\\u4e00-\\u9fa5]*?[捅砍刺扎划伤]",
					capture : "持刀伤害",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "烫伤",
					capture : "烫伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "推[\\u4e00-\\u9fa5]*?[倒伤][^;；\\.。]*?(伤|导致|造成)",
					capture : "推倒致伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "咬[\\u4e00-\\u9fa5]*?伤",
					capture : "咬伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "CASE_FACT_EVIDENCE",
					cacheKey : "meta_people_name2obj",
					regex : "抓[\\u4e00-\\u9fa5]*?伤",
					capture : "抓伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},

				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?眼睛|眼[^,，;；\\.。]*?脱落",
					capture : "挖人眼睛",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?耳朵",
					capture : "割人耳朵",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?鼻子",
					capture : "割人鼻子",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?脚筋",
					capture : "挑人脚筋",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				},
				{
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?([手脚足臂]|四肢)|([手脚足臂]|四肢)[^,，;；\\.。]*?[挖割挑砍剜]",
					capture : "砍人手足",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[挖割挑砍剜][^,，;；\\.。]*?(髌骨|膝盖)",
					capture : "剜人髌骨",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "(以(锐器|硫酸|腐蚀性溶液)严重毁容)",
					capture : "\\1",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "((电击|烧烫)要害部位)",
					capture : "\\1",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "冷冻",
					capture : "冷冻",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "毒蛇猛兽撕咬",
					capture : "毒蛇猛兽撕咬",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "其他特别残忍手段",
					capture : "其他特别残忍手段",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "拳打脚踢|棍棒击打|互掐|推搡|撞击|挤压|[殴厮捶拳踢]打|[打踢撞撞]伤|打架",
					capture : "殴打",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "砸伤",
					capture : "砸伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "[捅砍刺扎划]伤|刀[\\u4e00-\\u9fa5]*?[捅砍刺扎划伤]",
					capture : "持刀伤害",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "烫伤",
					capture : "烫伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "推[\\u4e00-\\u9fa5]*?[倒伤][^;；\\.。]*?(伤|导致|造成)",
					capture : "推倒致伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "咬[\\u4e00-\\u9fa5]*?伤",
					capture : "咬伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				}, {
					tagList : "HANDLE_OPINION",
					cacheKey : "meta_people_name2obj",
					regex : "抓[\\u4e00-\\u9fa5]*?伤",
					capture : "抓伤",
					meanWhile : "0",
					order : "1",
					defaultAll : "0"
				} ],
		method : "rule_func_extract_litigant_stringlist_info"
	}
}