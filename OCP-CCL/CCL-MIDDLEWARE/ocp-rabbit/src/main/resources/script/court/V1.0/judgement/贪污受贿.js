var NAMESPACE_COURT_JUDGEMENT_贪污受贿 = {
	/**
	 * point-1:贪污受贿手段
	 */
	info_corruption_means : {
		params : [ {
			tagList : "office_opinion#plaintiff_args",
			regex : "直接(占有|控制|管领|支配|占领|所有|拥有|占据)",
			capture : "直接占有"
		}, {
			tagList : "office_opinion#plaintiff_args",
			regex : "截留|扣留|私吞|侵占|截取|克扣",
			capture : "截留"
		}, {
			tagList : "office_opinion#plaintiff_args",
			regex : "套取|骗取|诈骗|骗夺|攫取|骗套|窃取",
			capture : "套取"
		}, {
			tagList : "office_opinion#plaintiff_args",
			regex : "小金库|账外款|账外账|小钱柜|帐外帐|私账|灰色收入",
			capture : "小金库"
		}, {
			tagList : "office_opinion#plaintiff_args",
			regex : "转移|变卖|毁损|隐藏|抛弃|恶意低价转让|隐匿|赠与|赠予",
			capture : "转移"
		}, {
			tagList : "facts_found",
			regex : "直接(占有|控制|管领|支配|占领|所有|拥有|占据)",
			capture : "直接占有"
		}, {
			tagList : "facts_found",
			regex : "截留|扣留|私吞|侵占|截取|克扣",
			capture : "截留"
		}, {
			tagList : "facts_found",
			regex : "套取|骗取|诈骗|骗夺|攫取|骗套|窃取",
			capture : "套取"
		}, {
			tagList : "facts_found",
			regex : "小金库|账外款|账外账|小钱柜|帐外帐|私账|灰色收入",
			capture : "小金库"
		}, {
			tagList : "facts_found",
			regex : "转移|变卖|毁损|隐藏|抛弃|恶意低价转让|隐匿|赠与|赠予",
			capture : "转移"
		}, {
			tagList : "court_opinion",
			regex : "直接(占有|控制|管领|支配|占领|所有|拥有|占据)",
			capture : "直接占有"
		}, {
			tagList : "court_opinion",
			regex : "截留|扣留|私吞|侵占|截取|克扣",
			capture : "截留"
		}, {
			tagList : "court_opinion",
			regex : "套取|骗取|诈骗|骗夺|攫取|骗套|窃取",
			capture : "套取"
		}, {
			tagList : "court_opinion",
			regex : "小金库|账外款|账外账|小钱柜|帐外帐|私账|灰色收入",
			capture : "小金库"
		}, {
			tagList : "court_opinion",
			regex : "转移|变卖|毁损|隐藏|抛弃|恶意低价转让|隐匿|赠与|赠予",
			capture : "转移"
		} ],
		method : "rule_func_extract_times_info"
	}
}