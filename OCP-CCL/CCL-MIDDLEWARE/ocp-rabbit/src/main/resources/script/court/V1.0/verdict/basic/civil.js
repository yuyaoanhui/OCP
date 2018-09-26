var NAMESPACE_COURT_VERDICT_civil = {
	/**
	 * civil point-1:案件受理费
	 */
	meta_fee_case : {
		params : [],
		method : "rule_func_extract_case_fee"
	}
};

/**
 * 参数解释：<br>
 * tagList;// 标签列表<br>
 * cacheKey;// 缓存字段key<br>
 * infoPointName;// 信息点名称<br>
 * regex;// 通用的常规正向正则<br>
 * regex_limitAmount;// 匹配限制数量<br>
 * regex_totalAmount;// 匹配总量<br>
 * regex_overtopAmount;// 匹配超出数量<br>
 * capture;// 捕获模式<br>
 * reverseRegex;// 反向正则,多个用#隔开<br>
 * meanWhile;// 人物和属性是否限定同时出现在一个连续无符号的短句中.1表示限定,0表示不限定.<br>
 * order;// 是否限定人物和属性出现的固定顺序.0-不限定,1-人在前属性在后 ,2-人在后属性在前<br>
 * defaultAll;// 如果匹配到属性但没有找人对应的人,是否需要默认是所有人的.0-不需要,1-需要<br>
 * type;// 信息点类型(金额/时间/其他)<br>
 * unit;// 输出单位
 * <p>
 * a、若信息点类型为'金额'，取值为'万'、'千万'等，若取值设为'',则默认不需要单位转换
 * b、若信息点类型为'时间'，取值为'年'、'月'、'日'、'小时'、'分钟'，若取值设为'',则默认不需要单位转换
 * c、若信息点类型为'其他'，取值为'int'表示将double转成int，取值为''表示不需要转换*
 * </p>
 * defaultVal;// 默认填充值<br>
 * reverse;// 每个标签对应句子集合是否翻转,(0-否 1-是),抽取时间和日期时用到<br>
 * range;// 设定取值范围，0-取匹配内容所在的整个段落 1-匹配内容所在的整个句子(结尾为。或;)<br>
 * mutex;// 互斥信息点,多个用#隔开<br>
 * valueNodes;// 范围对应的各个节点值，以#隔开<br>
 * valueRanges;// 范围，以#隔开<br>
 * crimeName;// 罪名<br>
 * dependentPoints;// 依赖信息点名,多个用#隔开<br>
 * yearsNum;// 几年内是哦夫因XXX受过XX处罚<br>
 * orgName;// 组织机构：法院|检察院<br>
 * filePaths;// 资源配置文件名称,多个用#隔开<br>
 * resultFlag;// 结果为比例还是差值 0-比例 1-差值<br>
 * caseType;// 案件类型<br>
 * judgementType;// 判决类型<br>
 */
