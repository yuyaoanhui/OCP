package com.ocp.rabbit.plugin.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.plugin.custom.AbstractRuleFunctionPlugin;
import com.ocp.rabbit.proxy.extractor.common.FirstCrimDateExtractor;
import com.ocp.rabbit.proxy.extractor.common.MoneyInfoExtractor;
import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.extractor.common.SequenceTimeInfoExtractor;
import com.ocp.rabbit.proxy.extractor.common.TimeInfoExtractor;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.tool.ExtractPositionRecorder;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 
 * @author yu.yao 2018年8月16日
 *
 */
public class RuleFunction extends AbstractRuleFunctionPlugin {
  public RuleFunction(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    super(context);
  }

  private static final String SPLITER = "#";
  private ReferLigitantRelatedInfoExtrator referExtractor =
      new ReferLigitantRelatedInfoExtrator(context);
  private FirstCrimDateExtractor fcExtractor = new FirstCrimDateExtractor(context);
  private ExtractPositionRecorder posRecodExtractor = new ExtractPositionRecorder(context);


  /**
   * 功能：利用正则表达式判断真假
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则表达式 3-信息点名 4-反向正则
   * @return
   */
  public ResultCode rule_func_judge_truth_by_regex(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph = TextUtils.getSbParagraph(context, tagStrList);
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String regex = fucParams.getReverseRegex();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Pattern pattern = Pattern.compile(regexStr);
    Pattern pattern1 = null;
    if (!TextUtils.isEmpty(regex)) {
      pattern1 = Pattern.compile(regex);
    }
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    for (int j = sentences.length - 1; j >= 0; j--) {
      String sent = sentences[j];
      if (pattern1 != null && pattern1.matcher(sent).find()) {
        continue;
      }
      if (pattern.matcher(sent).find()) {
        context.rabbitInfo.extractInfo.put(key, true);
        posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
        break;
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：利用正则表达式提取信息String，没有提取到且默认值不为空则用默认值填充
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签(支持多个，用#隔开) 2-正则 3-捕获模式,形如:\1\2,str1\1\2str2,等等 4-信息点名 5-默认填充值 6--反向正则
   * @return
   */
  public ResultCode rule_func_extract_info_by_regex(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph = TextUtils.getSbParagraph(context, tagStrList);
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String valModeStr = fucParams.getCapture();
    List<Integer[]> numList = getMatchPositionsByRegex(valModeStr, Pattern.compile("(\\\\\\d)"));
    String defaultVal = fucParams.getDefaultVal();
    String regex = fucParams.getReverseRegex();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Matcher matcher;
    Pattern pattern = Pattern.compile(regexStr);
    Pattern pattern1 = null;
    if (!TextUtils.isEmpty(regex)) {
      pattern1 = Pattern.compile(regex);
    }
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    for (String sent : sentences) {
      if (pattern1 != null && pattern1.matcher(sent).find()) {
        continue;
      }
      matcher = pattern.matcher(sent);
      if (matcher.find()) {
        if ((numList == null) || (numList.size() == 0)) {
          context.rabbitInfo.extractInfo.put(key, valModeStr);
        } else {
          StringBuilder sbValue = new StringBuilder("");
          sbValue.append(valModeStr.substring(0, numList.get(0)[0]));
          int index;
          Integer[] lastTmp = null;
          for (Integer[] tmp : numList) {
            if (lastTmp != null) {
              sbValue.append(valModeStr.substring(lastTmp[1], tmp[0]));
            }
            index = Integer.valueOf(valModeStr.substring(tmp[0] + 1, tmp[1]));
            sbValue.append(matcher.group(index));
            lastTmp = tmp;
          }
          sbValue.append(valModeStr.substring(numList.get(numList.size() - 1)[1]));
          context.rabbitInfo.extractInfo.put(key, sbValue.toString());
        }
        posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
        return RabbitResultCode.RABBIT_SUCCESS;
      }
    }
    if (!TextUtils.isEmpty(defaultVal) && (context.rabbitInfo.extractInfo.get(key) == null)) {
      context.rabbitInfo.extractInfo.put(key, defaultVal);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：利用正则表达式提取多个信息值List<String>，没有提取到且默认值不为空则用默认值填充
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签(支持多个，用#隔开) 2-正则 3-捕获模式,形如:\1\2, str1\1\2str2,等等 4-信息点名 5-默认填充值 6-方向正则
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_multi_info_by_regex(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph = TextUtils.getSbParagraph(context, tagStrList);
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String valModeStr = fucParams.getCapture();
    List<Integer[]> numList = getMatchPositionsByRegex(valModeStr, Pattern.compile("(\\\\\\d)"));
    String defaultVal = fucParams.getDefaultVal();
    String regex = fucParams.getReverseRegex();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Matcher matcher;
    Pattern pattern = Pattern.compile(regexStr);
    Pattern pattern1 = null;
    if (!TextUtils.isEmpty(regex)) {
      pattern1 = Pattern.compile(regex);
    }
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    int matchFlg = 0;
    for (String sent : sentences) {
      if (pattern1 != null && pattern1.matcher(sent).find()) {
        continue;
      }
      matcher = pattern.matcher(sent);
      while (matcher.find()) {
        posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
        matchFlg = 1;
        ArrayList<String> arrTmp;
        if (context.rabbitInfo.extractInfo.get(key) == null) {
          arrTmp = new ArrayList<>();
          context.rabbitInfo.extractInfo.put(key, arrTmp);
        }
        arrTmp = (ArrayList<String>) (context.rabbitInfo.extractInfo.get(key));
        if ((numList == null) || (numList.size() == 0)) {
          if (!arrTmp.contains(valModeStr)) {
            arrTmp.add(valModeStr);
          }
        } else {
          StringBuilder sbValue = new StringBuilder("");
          sbValue.append(valModeStr.substring(0, numList.get(0)[0]));
          int index;
          Integer[] lastTmp = null;
          for (Integer[] tmp : numList) {
            if (lastTmp != null) {
              sbValue.append(valModeStr.substring(lastTmp[1], tmp[0]));
            }
            index = Integer.valueOf(valModeStr.substring(tmp[0] + 1, tmp[1]));
            sbValue.append(matcher.group(index));
            lastTmp = tmp;
          }
          sbValue.append(valModeStr.substring(numList.get(numList.size() - 1)[1]));
          if (!arrTmp.contains(sbValue.toString())) {
            arrTmp.add(sbValue.toString());
          }
        }
      }
    }
    if ((matchFlg == 1) || (TextUtils.isEmpty(defaultVal))) {
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    // 若没匹配到则添加默认值
    ArrayList<String> arrTmp = null;
    if (context.rabbitInfo.extractInfo.get(key) == null) {
      arrTmp = new ArrayList<>();
      context.rabbitInfo.extractInfo.put(key, arrTmp);
    }
    arrTmp = (ArrayList<String>) (context.rabbitInfo.extractInfo.get(key));
    if (!arrTmp.contains(defaultVal)) {
      arrTmp.add(defaultVal);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的取值为boolean的属性信息
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签,支持多个，用#隔开 2-信息点名 3-meta_people_name2obj 4-匹配的正则,多个用#隔开 5-不能匹配的正则,多个用#隔开
   *        6-人物和属性是否限定同时出现在一个连续无符号的短句中，1表示限定，0表示不限定 7-是否限定人物和属性出现的固定顺序，0-不限定 1-人在前，属性在后 2-人在后，属性在前
   *        8-如果匹配到属性但没有找人对应的人，是否需要默认是所有人的 0-不需要 1-需要
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_boolean_info(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    String positivePatternStr = fucParams.getRegex();
    String negativePatternStr = fucParams.getReverseRegex();
    int limitSameShortSentFlg = Integer.valueOf(fucParams.getMeanWhile());
    int orderFlg = Integer.valueOf(fucParams.getOrder());
    int allLitigantFlg = Integer.valueOf(fucParams.getDefaultAll());
    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    Map<String, Boolean> rsltMap =
        referExtractor.extractBooleanInfo(paragraphList, infoKey, nameList, positivePatternStr,
            negativePatternStr, limitSameShortSentFlg, orderFlg, allLitigantFlg);
    for (Map.Entry<String, Boolean> entry : rsltMap.entrySet()) {
      String name = entry.getKey();
      if ((name2People.containsKey(name))
          && (name2People.get(name).getPeopleAttrMap().get(infoKey) == null)) {
        Boolean value = entry.getValue();
        name2People.get(name).getPeopleAttrMap().put(infoKey, value);
      }
    }
    if (rsltMap.isEmpty()) {
      return RabbitResultCode.RABBIT_FAIL;
    } else {
      return RabbitResultCode.RABBIT_SUCCESS;
    }
  }

  /**
   * 功能：抽取存在人物指代的取值为double的属性信息 fucParams传入的参数：1-段落标签 2-信息点名 3-meta_people_name2obj 4-匹配的正向正则,多个用#隔开
   * 5-不能匹配的正则,多个用#隔开 6-人物和属性是否限定同时出现在一个连续无符号的短句中，1表示限定，0表示不限定 7-是否限定人物和属性出现的固定顺序，0-不限定 1-人在前，属性在后
   * 2-人在后，属性在前 8-如果匹配到属性但没有找人对应的人，是否需要默认是所有人的 0-不需要 1-需要 9-信息点类型(金额/时间/其他)
   * 10-输出单位:a、若信息点类型为'金额'，取值为'万'、'千万'等，若取值设为'',则默认不需要单位转换
   * b、若信息点类型为'时间'，取值为'年'、'月'、'日'、'小时'、'分钟'，若取值设为'',则默认不需要单位转换
   * c、若信息点类型为'其他'，取值为'int'表示将double转成int，取值为''表示不需要转换
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_double_info(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.size() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String positivePatternStr = fucParams.getRegex();
    String negativePatternStr = fucParams.getReverseRegex();
    int limitSameShortSentFlg = Integer.valueOf(fucParams.getMeanWhile());
    int orderFlg = Integer.valueOf(fucParams.getOrder());
    int allLitigantFlg = Integer.valueOf(fucParams.getDefaultAll());
    String valueType = fucParams.getType();
    String outputUnit = fucParams.getUnit();
    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    Map<String, Double> rsltMap = referExtractor.extractDoubleInfo(paragraphList, infoKey, nameList,
        positivePatternStr, negativePatternStr, limitSameShortSentFlg, orderFlg, allLitigantFlg,
        outputUnit, valueType);
    for (Map.Entry<String, Double> entry : rsltMap.entrySet()) {
      if (name2People.containsKey(entry.getKey())) {
        double value = entry.getValue();
        if ((outputUnit.equals("int")) || (outputUnit.equals("人")) || (outputUnit.equals("次"))) {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, (int) value);
        } else {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, value);
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的取值为List<String>的属性信息 fucParams传入的参数：1-段落标签,支持多个，用#隔开 2-信息点名 3-meta_people_name2obj
   * 4-匹配的正则,多个用#隔开 5-捕获模式与正则一一对应，形如：\1\2、中国、\1中国\2 6-不能匹配的正则,多个用#隔开
   * 7-人物和属性是否限定同时出现在一个连续无符号的短句中，1表示限定，0表示不限定 8-是否限定人物和属性出现的固定顺序，0-不限定 1-人在前，属性在后 2-人在后，属性在前
   * 9-如果匹配到属性但没有找人对应的人，是否需要默认是所有人的 0-不需要 1-需要
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_stringlist_info(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.size() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String positivePatternStr = fucParams.getRegex();
    String valModeStr = fucParams.getCapture();
    String negativePatternStr = fucParams.getReverseRegex();
    int limitSameShortSentFlg = Integer.valueOf(fucParams.getMeanWhile());
    int orderFlg = Integer.valueOf(fucParams.getOrder());
    int allLitigantFlg = Integer.valueOf(fucParams.getDefaultAll());
    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    Map<String, List<String>> rsltMap =
        referExtractor.extractStringlistInfo(paragraphList, infoKey, nameList, positivePatternStr,
            valModeStr, negativePatternStr, limitSameShortSentFlg, orderFlg, allLitigantFlg);
    for (Map.Entry<String, List<String>> entry : rsltMap.entrySet()) {
      if (name2People.containsKey(entry.getKey())) {
        List<String> list = entry.getValue();
        if (list.size() == 0)
          continue;
        Map<String, Object> peopleAttrMap = name2People.get(entry.getKey()).getPeopleAttrMap();
        if (peopleAttrMap.containsKey(infoKey)) {
          List<String> valList = (List<String>) (peopleAttrMap.get(infoKey));
          for (String tmp : list) {
            if (!valList.contains(tmp)) {
              valList.add(tmp);
            }
          }
        } else {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, list);
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的取值为String的属性信息 fucParams传入的参数：1-段落标签,支持多个，用#隔开 2-信息点名 3-meta_people_name2obj
   * 4-匹配的正则,多个用#隔开 5-捕获模式与正则一一对应，形如：\1\2、中国、\1中国\2 6-不能匹配的正则,多个用#隔开
   * 7-人物和属性是否限定同时出现在一个连续无符号的短句中，1表示限定，0表示不限定 8-是否限定人物和属性出现的固定顺序，0-不限定 1-人在前，属性在后 2-人在后，属性在前
   * 9-如果匹配到属性但没有找人对应的人，是否需要默认是所有人的 0-不需要 1-需要
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_string_info(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String positivePatternStr = fucParams.getRegex();
    String valModeStr = fucParams.getCapture();
    String negativePatternStr = fucParams.getReverseRegex();
    int limitSameShortSentFlg = Integer.valueOf(fucParams.getMeanWhile());
    int orderFlg = Integer.valueOf(fucParams.getOrder());
    int allLitigantFlg = Integer.valueOf(fucParams.getDefaultAll());
    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    Map<String, List<String>> rsltMap =
        referExtractor.extractStringlistInfo(paragraphList, infoKey, nameList, positivePatternStr,
            valModeStr, negativePatternStr, limitSameShortSentFlg, orderFlg, allLitigantFlg);
    for (Map.Entry<String, List<String>> entry : rsltMap.entrySet()) {
      if (name2People.containsKey(entry.getKey())) {
        List<String> value = entry.getValue();
        if (value.size() > 0) {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, value.get(0));
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代 信息为List<sentence>正则所在的的句子 fucParams传入的参数：1-段落标签 2-信息点名 3-meta_people_name2obj
   * 4-匹配的正则,多个用#隔开 5-不能匹配的正则,多个用#隔开 6-人物和属性是否限定同时出现在一个连续无符号的短句中，1表示限定，0表示不限定
   * 7-是否限定人物和属性出现的固定顺序，0-不限定 1-人在前，属性在后 2-人在后，属性在前 8-如果匹配到属性但没有找到对应的人，是否需要默认是所有人的 0-不需要 1-需要
   * 9-设定取值范围，0-取匹配内容所在的整个段落 1-匹配内容所在的整个句子(结尾为。或;)
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_sentenceList_info(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.size() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String positivePatternStr = fucParams.getRegex();
    String negativePatternStr = fucParams.getReverseRegex();
    int limitSameShortSentFlg = Integer.valueOf(fucParams.getMeanWhile());
    int orderFlg = Integer.valueOf(fucParams.getOrder());
    int allLitigantFlg = Integer.valueOf(fucParams.getDefaultAll());
    int rangeFlg = Integer.valueOf(fucParams.getRange());
    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    Map<String, List<String>> rsltMap =
        referExtractor.extractSentenceListInfo(paragraphList, nameList, positivePatternStr,
            negativePatternStr, limitSameShortSentFlg, orderFlg, allLitigantFlg, rangeFlg);
    for (Map.Entry<String, List<String>> entry : rsltMap.entrySet()) {
      if (name2People.containsKey(entry.getKey())) {
        List<String> list = entry.getValue();
        name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, list);
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能:抽取时间和日期
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则表达式 3-每个标签对应的句子集合是否翻转(0-否 1-是) 4-返回类型(日期/年/月/日/小时/分钟) 5-信息点名 6-反向正则
   * @return
   */
  public ResultCode rule_func_extract_time(ParamsBean fucParams) {
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    int reverseFlg = Integer.valueOf(fucParams.getReverse());
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> sents = new ArrayList<String>();
    for (String tag : tagStrList) {
      StringBuilder sbParagraph = new StringBuilder("");
      if (context.docInfo.getParaLabels().getByLabel(tag) == null) {
        continue;
      }
      Map<Integer, String> paraphs = context.docInfo.getParaLabels().getByLabel(tag).getContent();
      if (paraphs == null || paraphs.isEmpty()) {
        return RabbitResultCode.RABBIT_INVALID_PARAM;
      }
      for (int i : paraphs.keySet()) {
        sbParagraph.append(paraphs.get(i));
      }
      List<String> sentArr =
          DocumentUtils.splitSentenceByCommaSemicolonPeriod(sbParagraph.toString());
      if (reverseFlg == 1) {
        Collections.reverse(sentArr);
      }
      sents.addAll(sentArr);
    }
    if (sents.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String type = fucParams.getType();
    if ("".equals(type)) {
      type = fucParams.getUnit();
    }
    String regex = fucParams.getReverseRegex();
    Pattern pattern1 = null;
    if (!TextUtils.isEmpty(regex)) {
      pattern1 = Pattern.compile(regex);
    }
    Matcher matcher = null;
    Pattern pattern = Pattern.compile(regexStr);
    for (String sent : sents) {
      if (pattern1 != null && pattern1.matcher(sent).find()) {
        continue;
      }
      matcher = pattern.matcher(sent);
      if (matcher.find()) {
        String lastmacher = matcher.group();
        if (type.equals("日期")) {
          String value = TimeInfoExtractor.extractDate(sent);
          if (value == null) {
            return RabbitResultCode.RABBIT_NO_EXTRACT_INFO;
          }
          context.rabbitInfo.extractInfo.put(key, value);
          posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
          return RabbitResultCode.RABBIT_SUCCESS;
        } else if (type.equals("月") && matcher.group().contains("半")) {
          Double value = TimeInfoExtractor.extractTime(lastmacher, type);
          if (value == null) {
            return RabbitResultCode.RABBIT_NO_EXTRACT_INFO;
          }
          Double val = value + 0.5;
          context.rabbitInfo.extractInfo.put(key, val);
          posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
          return RabbitResultCode.RABBIT_SUCCESS;
        }
        Double value = TimeInfoExtractor.extractTime(lastmacher, type);
        if (value == null) {
          return RabbitResultCode.RABBIT_NO_EXTRACT_INFO;
        }
        double time = Math.round(value * 100) / 100.0;
        context.rabbitInfo.extractInfo.put(key, time);
        posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
        return RabbitResultCode.RABBIT_SUCCESS;
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的第一犯案时间
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-meta_people_name2obj 3-信息点名
   * @return
   */
  public ResultCode rule_func_extract_first_crime_time(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    String name2peopleObjKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    String firstCrimeKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(name2peopleObjKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    if (TextUtils.isEmpty(firstCrimeKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    fcExtractor.extract(paragraphList, name2peopleObjKey, firstCrimeKey);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取double值 抽取出来的金额默认规整成double值，单位为万元<br>
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签(支持传入多个，用#隔开) 2-匹配目标短句的正则 3-信息点 4-输出单位
   * @return
   */
  public ResultCode rule_func_extract_money(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph = TextUtils.getSbParagraph(context, tagStrList);
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String model = fucParams.getCapture();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Matcher matcher;
    Pattern pattern = Pattern.compile(regexStr);
    String[] sentences = DocumentUtils.splitSentenceByCommaSemicolon(sbParagraph.toString());
    for (String sent : sentences) {
      matcher = pattern.matcher(sent);
      if (matcher.find()) {
        String s = matcher.group();
        Double dMoney = MoneyInfoExtractor.extractMoney(s);
        if (dMoney == null || dMoney < 0) {
          return RabbitResultCode.RABBIT_NO_EXTRACT_INFO;
        }
        double my = dMoney;
        if ("元".equals(model) || "mg/100ml".equals(model)) {
          context.rabbitInfo.extractInfo.put(key, my);
        } else {
          context.rabbitInfo.extractInfo.put(key, my / 10000);
        }
        posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
        return RabbitResultCode.RABBIT_SUCCESS;
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能:抽取百分比、千分比等 抽取出来的利率默认规整成double型，单位为年利率
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签(支持传入多个，用#隔开) 2-匹配目标短句的正则 3-信息点 4-输出的单位 取值为"月""日""年",没有则默认为年
   * @return
   */
  public ResultCode rule_func_extract_percentage(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph = TextUtils.getSbParagraph(context, tagStrList);
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String model = fucParams.getCapture();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key) || (context.rabbitInfo.extractInfo.get(key) != null)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Matcher matcher;
    Pattern pattern = Pattern.compile(regexStr);
    String[] sentences = DocumentUtils.splitSentenceByCommaSemicolon(sbParagraph.toString());
    for (String sent : sentences) {
      matcher = pattern.matcher(sent);
      if (matcher.find()) {
        Double dDate = MoneyInfoExtractor.extractPercentage(sent, model);
        if (dDate == null || dDate < 0) {
          return RabbitResultCode.RABBIT_NO_EXTRACT_INFO;
        }
        double dt = dDate;
        context.rabbitInfo.extractInfo.put(key, dt);
        posRecodExtractor.recordInfoPointMatchSentPos(key, sent);
        return RabbitResultCode.RABBIT_SUCCESS;
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取连续的上下文相关的时间，比如，决定刑事拘留时间，执行刑事拘留时间...<br>
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则集合，用#隔开 3-信息点名集合，用#隔开,2,3传入的一一对应
   * @return
   */
  public ResultCode rule_func_extract_sequence_times(ParamsBean fucParams) {
    String tagStr = fucParams.getTagList();
    if (context.docInfo.getParaLabels().getByLabel(tagStr) == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String patternStr = fucParams.getRegex();
    String infoName = fucParams.getInfoPointName();
    StringBuilder paragraph = new StringBuilder("");
    Map<Integer, String> map = context.docInfo.getParaLabels().getByLabel(tagStr).getContent();
    if (map != null) {
      for (int i : map.keySet()) {
        paragraph.append(map.get(i));
      }
    }
    String[] strArray = patternStr.split(SPLITER);
    Pattern[] patterns = new Pattern[strArray.length];
    for (int i = 0; i < strArray.length; i++) {
      patterns[i] = Pattern.compile(strArray[i]);
    }
    Map<String, Object> rsltMap =
        SequenceTimeInfoExtractor.extractTimes(paragraph.toString(), patterns, infoName);
    for (Map.Entry<String, Object> entry : rsltMap.entrySet()) {
      String key = TextUtils.getRightKeyByName(entry.getKey());
      Object val = entry.getValue();
      if (val instanceof ArrayList) {
        context.rabbitInfo.extractInfo.put(key, val);
      } else if (val instanceof String) {
        context.rabbitInfo.extractInfo.put(key, val);
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  private List<Integer[]> getMatchPositionsByRegex(String str, Pattern patt) {
    List<Integer[]> rsltList = new ArrayList<>();
    Matcher matcher = patt.matcher(str);
    while (matcher.find()) {
      Integer[] tmp = new Integer[2];
      tmp[0] = matcher.start();
      tmp[1] = matcher.end();
      rsltList.add(tmp);
    }
    return rsltList;
  }
}
