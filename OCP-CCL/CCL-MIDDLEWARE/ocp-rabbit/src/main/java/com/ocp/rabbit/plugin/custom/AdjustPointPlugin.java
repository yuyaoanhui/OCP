package com.ocp.rabbit.plugin.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.proxy.extractor.common.FurtherExtractor;
import com.ocp.rabbit.proxy.extractor.common.NumberInfoExtractor;
import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

public class AdjustPointPlugin extends AbstractRuleFunctionPlugin {

  public AdjustPointPlugin(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    super(context);
  }

  private ReferLigitantRelatedInfoExtrator referExtractor =
      new ReferLigitantRelatedInfoExtrator(context);
  private static final String SPLITER = "#";

  /**
   * 功能：根据信息点和随机码生成唯一标识
   * 
   * @author yu.yao
   * @param fucParams 1-信息点名称，多个之间用#隔开 2-caseId信息点，用于检察院为文书生成唯一标识
   * @return
   */
  public ResultCode rule_func_generate_unique_idfentification(ParamsBean fucParams) {
    String[] keyStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sb = new StringBuilder("");
    for (String str : keyStrList) {
      String infoKey = TextUtils.getRightKeyByName(str);
      if (TextUtils.isEmpty(infoKey)) {
        return RabbitResultCode.RABBIT_INVALID_PARAM;
      }
      if (context.rabbitInfo.extractInfo.get(infoKey) == null) {
        sb.append("null");
      } else {
        sb.append((String) (context.rabbitInfo.extractInfo.get(infoKey)));
      }
      sb.append("_");
    }
    sb.append(UUID.randomUUID());
    String outputKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(outputKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    context.rabbitInfo.extractInfo.put(outputKey, UUID.randomUUID());
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 依据优先级按照排斥逻辑调整人物信息相关的几个信息点
   * 
   * @author yu.yao
   * @param fucParams 1-meta_people_attr 2-多个互斥的信息点用#隔开,取最后一个
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_adjust_exclude_by_priority(ParamsBean fucParams) {
    String peopleInfoKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (context.rabbitInfo.extractInfo.get(peopleInfoKey) == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    ArrayList<People> peopleInfos =
        (ArrayList<People>) (context.rabbitInfo.extractInfo.get(peopleInfoKey));
    String[] peopleAttrInfoNames = fucParams.getMutex().split("#");
    ArrayList<String> peopleAttrInfoKeys = new ArrayList<>();
    for (String str : peopleAttrInfoNames) {
      peopleAttrInfoKeys.add(TextUtils.getRightKeyByName(str));
    }
    if (peopleAttrInfoKeys.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    for (People pe : peopleInfos) {
      for (int i = 0; i < peopleAttrInfoKeys.size(); i++) {
        if (pe.getPeopleAttrMap().containsKey(peopleAttrInfoKeys.get(i))) {
          for (int j = i + 1; j < peopleAttrInfoKeys.size(); j++) {
            pe.getPeopleAttrMap().remove(peopleAttrInfoKeys.get(j));
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：识别文本中的罪名并输出标准罪名
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签,多个之间用#隔开 2-正则表达式 3-信息点名称
   * @return
   */
  public ResultCode rule_func_extract_crime(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Matcher matcher = null;
    Pattern pattern = Pattern.compile(regexStr);
    List<String> crimeList = new ArrayList<>();
    for (String paragraph : paragraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sent : sentences) {
        matcher = pattern.matcher(sent);
        if (matcher.find()) {
          int findFlg = 0;
          Map<String, List<String>> anyouDictionary = ResourceReader.readSource();
          for (String word : anyouDictionary.get("刑事案由")) {
            if (sent.contains(word)) {
              crimeList.add(word);
              findFlg = 1;
              break;
            }
          }
          if (findFlg == 1) {
            break;
          }
          for (String word : anyouDictionary.get("民事案由")) {
            if (sent.contains(word)) {
              crimeList.add(word);
              findFlg = 1;
              break;
            }
          }
          if (findFlg == 1) {
            break;
          }
          for (String word : anyouDictionary.get("行政案由")) {
            if (sent.contains(word)) {
              crimeList.add(word);
              findFlg = 1;
              break;
            }
          }
          if (findFlg == 1) {
            break;
          }
          for (String word : anyouDictionary.get("赔偿案由")) {
            if (sent.contains("赔偿") && sent.contains(word)) {
              crimeList.add(word);
              findFlg = 1;
              break;
            }
          }
          if (findFlg == 1) {
            break;
          }
          for (String word : anyouDictionary.get("执行案由")) {
            if (sent.contains("执行") && sent.contains(word)) {
              crimeList.add(word);
              findFlg = 1;
              break;
            }
          }
          if (findFlg == 1) {
            break;
          }
        }
      }
    }
    context.rabbitInfo.extractInfo.put(key, TextUtils.deduplicate(crimeList));
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能:抽取利率，对利率进行计算(针对银行同期或基准贷款利率的上浮下浮倍数) 抽取出来的利率默认规整成double型
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签(支持传入多个，用#隔开) 2-匹配目标短句的正则 3-信息点 4-反向正则 5-输出的单位
   *        取值为"月利率""日利率""年利率",没有则默认为年利率
   * @return
   */
  public ResultCode rule_func_extract_interest_rate(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph =
        context.docInfo.getParaLabels().getContentSumByLabels(Arrays.asList(tagStrList));
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String negRegex = fucParams.getReverseRegex();
    String model = fucParams.getUnit();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key) || (context.rabbitInfo.extractInfo.get(key) != null)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    double rate = FurtherExtractor.interestRate(sbParagraph, regexStr, negRegex, model);
    if (rate > 0) {
      context.rabbitInfo.extractInfo.put(key, rate);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取数量（人数）
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签(多个#隔开) 2-信息点名称 3-正则 4-单位（int/人） int--文书直接描述 人--抽取受伤、死亡人数
   * @return
   */
  public ResultCode rule_func_extract_parse_number(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    String regex = fucParams.getRegex();
    String point = fucParams.getUnit();
    Pattern pattern = Pattern.compile(regex);
    double number = 0.0;
    if (point.equals("int") || point.equals("人")) {
      number = NumberInfoExtractor.parseNumber(paragraphList, pattern, point);
    }
    if (number > 0) {
      context.rabbitInfo.extractInfo.put(key, (int) number);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：利用正则表达式判断是否
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则表达式（表示是） 3-信息点名 4-反向正则
   * @return
   */
  public ResultCode rule_func_judge_whether_by_regex(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph =
        context.docInfo.getParaLabels().getContentSumByLabels(Arrays.asList(tagStrList));
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String regexStr = fucParams.getRegex();
    String regex = fucParams.getReverseRegex();
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Pattern patternStr = Pattern.compile(regexStr);
    Pattern pattern = null;
    if (!TextUtils.isEmpty(regex)) {
      pattern = Pattern.compile(regex);
    }
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(sbParagraph.toString());
    for (String sent : sentences) {
      if (pattern != null && pattern.matcher(sent).find()) {
        continue;
      }
      if (patternStr.matcher(sent).find()) {
        context.rabbitInfo.extractInfo.put(key, true);
        return RabbitResultCode.RABBIT_SUCCESS;
      }
    }
    if (context.rabbitInfo.extractInfo.get(key) == null) {
      context.rabbitInfo.extractInfo.put(key, false);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的一次容留人数
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-信息点名 3-meta_people_name2obj 4-匹配的正向正则 5-反向正则
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_people_num_info(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      tagStrList = new String[] {"office_opinion"};
      paragraphList = TextUtils.getParagraphList(context, tagStrList);
      if (paragraphList.isEmpty()) {
        return RabbitResultCode.RABBIT_INVALID_PARAM;
      }
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
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    Map<String, Double> rsltMap =
        referExtractor.extractPeopleNum(paragraphList, positivePatternStr, negativePatternStr);
    if (rsltMap.isEmpty()) {
      tagStrList = new String[] {"office_opinion"};
      paragraphList = TextUtils.getParagraphList(context, tagStrList);
      rsltMap =
          referExtractor.extractPeopleNum(paragraphList, positivePatternStr, negativePatternStr);
    }
    for (Map.Entry<String, Double> entry : rsltMap.entrySet()) {
      if (name2People.containsKey(entry.getKey())) {
        double value = entry.getValue();
        name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, (int) value);

      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的计算 (超出部分)/限制数量 的比例
   * 
   * @author yu.yao
   * @param fucParams :</br>
   *        1-段落标签 <br>
   *        2-信息点名 <br>
   *        3-meta_people_name2obj<br>
   *        4-匹配限制数量的正向正则,多个用#隔开<br>
   *        5-匹配总量的正向正则,多个用#隔开<br>
   *        6-超出数量的正则匹配,多个用#隔开<br>
   *        9-如果匹配到属性但没有找人对应的人，是否需要默认是所有人的 0-不需要 1-需要 <br>
   *        10-结果为比例还是差值 0-比例 1-差值 <br>
   *        11-信息点类型(金额/时间/其他)<br>
   *        12-输出单位:<br>
   *        a、若信息点类型为'金额'，取值为'万'、'千万'等，若取值设为'',则默认不需要单位转换<br>
   *        b、若信息点类型为'时间'，取值为'年'、'月'、'日'、'小时'、'分钟'，若取值设为'',则默认不需要单位转换<br>
   *        c、若信息点类型为'其他'，取值为'int'表示将double转成int，取值为''表示不需要转换<br>
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_rate_info(ParamsBean fucParams) {
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
    String limitPatternStr = fucParams.getRegex_limitAmount();
    String totalPatternStr = fucParams.getRegex_totalAmount();
    String targetPatternStr = fucParams.getRegex_overtopAmount();
    int limitSameShortSentFlg = Integer.valueOf(fucParams.getMeanWhile());
    int orderFlg = Integer.valueOf(fucParams.getOrder());
    int allLitigantFlg = Integer.valueOf(fucParams.getDefaultAll());
    String resultFlg = fucParams.getResultFlag();
    String valueType = fucParams.getType();
    String outputUnit = fucParams.getUnit();

    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    // 抽取限定的
    Map<String, Double> limitMap =
        referExtractor.extractDoubleInfo(paragraphList, infoKey, nameList, limitPatternStr, "",
            limitSameShortSentFlg, orderFlg, allLitigantFlg, outputUnit, valueType);
    // 抽取超出的
    Map<String, Double> targetMap =
        referExtractor.extractDoubleInfo(paragraphList, infoKey, nameList, targetPatternStr, "",
            limitSameShortSentFlg, orderFlg, allLitigantFlg, outputUnit, valueType);
    // 抽取实际的
    Map<String, Double> totalMap = new HashMap<>();
    if (targetMap.size() == 0) {
      totalMap = referExtractor.extractDoubleInfo(paragraphList, infoKey, nameList, totalPatternStr,
          "", limitSameShortSentFlg, orderFlg, allLitigantFlg, outputUnit, valueType);
    }
    if ("1".equals(resultFlg)) {
      if (targetMap.size() > 0) {
        for (Map.Entry<String, Double> entry : targetMap.entrySet()) {
          if (name2People.containsKey(entry.getKey())) {
            double targetValue = entry.getValue();
            if ("int".equals(outputUnit)) {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, (int) targetValue);
            } else {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, targetValue);
            }
          }
        }
      } else {
        for (Map.Entry<String, Double> entry : limitMap.entrySet()) {
          if (name2People.containsKey(entry.getKey()) && totalMap.containsKey(entry.getKey())) {
            double limitValue = entry.getValue();
            double totalValue = totalMap.get(entry.getKey());
            if (limitValue > 0 && totalValue > 0 && totalValue > limitValue) {
              double value = totalValue - limitValue;
              if ("int".equals(outputUnit)) {
                name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, (int) value);
              } else {
                name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, value);
              }
            }
          }
        }
      }
    } else {
      if (targetMap.size() > 0) {
        for (Map.Entry<String, Double> entry : targetMap.entrySet()) {
          if (name2People.containsKey(entry.getKey()) && limitMap.containsKey(entry.getKey())) {
            double targetValue = entry.getValue();
            double limitValue = limitMap.get(entry.getKey());
            if (limitValue > 0 && targetValue > 0) {
              double rate = (targetValue / limitValue) * 100;
              rate = (double) Math.round(rate * 100) / 100;
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, rate);
            }
          }
        }
      } else {
        for (Map.Entry<String, Double> entry : limitMap.entrySet()) {
          if (name2People.containsKey(entry.getKey()) && totalMap.containsKey(entry.getKey())) {
            double limitValue = entry.getValue();
            double totalValue = totalMap.get(entry.getKey());
            if (limitValue > 0 && totalValue > 0 && totalValue > limitValue) {
              double rate = ((totalValue - limitValue) / limitValue) * 100;
              rate = (double) Math.round(rate * 100) / 100;
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, rate);
            }
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取引诱、容留、介绍卖淫罪的人数 fucParams传入的参数：1-段落标签 2-meta_people_name2obj 3-匹配的正向正则,多个用#隔开
   * 4-不能匹配的正则,多个用#隔开
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_people_num(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String positivePatternStr = fucParams.getRegex();
    String negativePatternStr = fucParams.getReverseRegex();
    int limitSameShortSentFlg = 0;
    int orderFlg = 1;
    List<String> nameList = new ArrayList<>();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    nameList.addAll(name2People.keySet());
    referExtractor.extractPeopleNum(paragraphList, nameList, positivePatternStr, negativePatternStr,
        limitSameShortSentFlg, orderFlg, name2People);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

}
