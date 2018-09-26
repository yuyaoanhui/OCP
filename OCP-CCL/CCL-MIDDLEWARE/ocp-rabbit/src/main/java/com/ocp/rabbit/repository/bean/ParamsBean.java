package com.ocp.rabbit.repository.bean;

/**
 * 正则匹配参数
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class ParamsBean {
  private String tagList;// 标签列表
  private String cacheKey;// 缓存字段key
  private String infoPointName;// 信息点名称
  private String regex;// 通用的常规正向正则
  // 具体业务需要的多个正向正则，用于抽取存在人物指代的计算 (超出部分)/限制数量 的比例
  private String regex_limitAmount;// 匹配限制数量
  private String regex_totalAmount;// 匹配总量
  private String regex_overtopAmount;// 匹配超出数量
  private String capture;// 捕获模式
  private String reverseRegex;// 反向正则,多个用#隔开
  private String meanWhile;// 人物和属性是否限定同时出现在一个连续无符号的短句中.1表示限定,0表示不限定.
  private String order;// 是否限定人物和属性出现的固定顺序.0-不限定,1-人在前属性在后 ,2-人在后属性在前
  private String defaultAll;// 如果匹配到属性但没有找人对应的人,是否需要默认是所有人的.0-不需要,1-需要
  private String type;// 信息点类型(金额/时间/其他)
  private String unit;// 输出单位
  private String defaultVal;// 默认填充值
  private String reverse;// 每个标签对应句子集合是否翻转,(0-否 1-是),抽取时间和日期时用到
  private String range;// 设定取值范围，0-取匹配内容所在的整个段落 1-匹配内容所在的整个句子(结尾为。或;)
  private String mutex;// 互斥信息点,多个用#隔开
  private String valueNodes;// 范围对应的各个节点值，以#隔开
  private String valueRanges;// 范围，以#隔开
  private String crimeName;// 罪名
  private String dependentPoints;// 依赖信息点名,多个用#隔开
  private String yearsNum;// 几年内是否因XXX受过XX处罚
  private String orgName;// 组织机构：法院|检察院
  private String filePaths;// 资源配置文件名称,多个用#隔开
  private String resultFlag;// 结果为比例还是差值 0-比例 1-差值
  private String caseType;// 案件类型
  private String judgementType;// 判决类型
  private String punctuation;// 标点符号

  public String getTagList() {
    return tagList;
  }

  public String getCacheKey() {
    return cacheKey;
  }

  public void setCacheKey(String cacheKey) {
    this.cacheKey = cacheKey;
  }

  public String getInfoPointName() {
    return infoPointName;
  }

  public void setInfoPointName(String infoPointName) {
    this.infoPointName = infoPointName;
  }

  public void setTagList(String tagList) {
    this.tagList = tagList;
  }

  public String getRegex() {
    return regex;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public String getRegex_limitAmount() {
    return regex_limitAmount;
  }

  public void setRegex_limitAmount(String regex_limitAmount) {
    this.regex_limitAmount = regex_limitAmount;
  }

  public String getRegex_totalAmount() {
    return regex_totalAmount;
  }

  public void setRegex_totalAmount(String regex_totalAmount) {
    this.regex_totalAmount = regex_totalAmount;
  }

  public String getRegex_overtopAmount() {
    return regex_overtopAmount;
  }

  public void setRegex_overtopAmount(String regex_overtopAmount) {
    this.regex_overtopAmount = regex_overtopAmount;
  }

  public String getReverseRegex() {
    return reverseRegex;
  }

  public void setReverseRegex(String reverseRegex) {
    this.reverseRegex = reverseRegex;
  }

  public String getCapture() {
    return capture;
  }

  public void setCapture(String capture) {
    this.capture = capture;
  }

  public String getMeanWhile() {
    return meanWhile;
  }

  public void setMeanWhile(String meanWhile) {
    this.meanWhile = meanWhile;
  }

  public String getOrder() {
    return order;
  }

  public void setOrder(String order) {
    this.order = order;
  }

  public String getDefaultAll() {
    return defaultAll;
  }

  public void setDefaultAll(String defaultAll) {
    this.defaultAll = defaultAll;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public String getDefaultVal() {
    return defaultVal;
  }

  public void setDefaultVal(String defaultVal) {
    this.defaultVal = defaultVal;
  }

  public String getReverse() {
    return reverse;
  }

  public void setReverse(String reverse) {
    this.reverse = reverse;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public String getMutex() {
    return mutex;
  }

  public void setMutex(String mutex) {
    this.mutex = mutex;
  }

  public String getValueNodes() {
    return valueNodes;
  }

  public void setValueNodes(String valueNodes) {
    this.valueNodes = valueNodes;
  }

  public String getValueRanges() {
    return valueRanges;
  }

  public void setValueRanges(String valueRanges) {
    this.valueRanges = valueRanges;
  }

  public String getCrimeName() {
    return crimeName;
  }

  public void setCrimeName(String crimeName) {
    this.crimeName = crimeName;
  }

  public String getDependentPoints() {
    return dependentPoints;
  }

  public void setDependentPoints(String dependentPoints) {
    this.dependentPoints = dependentPoints;
  }

  public String getYearsNum() {
    return yearsNum;
  }

  public void setYearsNum(String yearsNum) {
    this.yearsNum = yearsNum;
  }

  public String getOrgName() {
    return orgName;
  }

  public void setOrgName(String orgName) {
    this.orgName = orgName;
  }

  public String getFilePaths() {
    return filePaths;
  }

  public void setFilePaths(String filePaths) {
    this.filePaths = filePaths;
  }

  public String getResultFlag() {
    return resultFlag;
  }

  public void setResultFlag(String resultFlag) {
    this.resultFlag = resultFlag;
  }

  public String getCaseType() {
    return caseType;
  }

  public void setCaseType(String caseType) {
    this.caseType = caseType;
  }

  public String getJudgementType() {
    return judgementType;
  }

  public void setJudgementType(String judgementType) {
    this.judgementType = judgementType;
  }

  public String getPunctuation() {
    return punctuation;
  }

  public void setPunctuation(String punctuation) {
    this.punctuation = punctuation;
  }

}
