package com.ocp.rabbit.plugin.custom;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.constant.CaseHierarchy;
import com.ocp.rabbit.repository.constant.CaseType;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 基础信息点处理
 * 
 * @author yu.yao 2018年8月27日
 *
 */
public class BasicPointPlugin extends AbstractRuleFunctionPlugin {
  public BasicPointPlugin(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    super(context);
  }

  private static final String SPLITER = "#";

  /**
   * 功能：给定标签对应的段落中，在正则匹配到的句子结尾添加上要设置的标点符号(这么做是为了解决人物属性抽取的问题)
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则 3-要设置的标点符号
   * @return
   */
  public ResultCode rule_func_add_punc(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    Map<String, Integer> paragraph2idMap = new HashMap<>();
    List<Map<Integer, String>> paragraphs =
        context.docInfo.getParaLabels().getContentByLabels(Arrays.asList(tagStrList));
    for (Map<Integer, String> paraMap : paragraphs) {
      for (int i : paraMap.keySet()) {
        paragraph2idMap.put(paraMap.get(i), i);
      }
    }
    if (paragraph2idMap.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Pattern pattern = Pattern.compile(fucParams.getRegex());
    String punc = fucParams.getPunctuation();
    Matcher matcher;
    for (Map.Entry<String, Integer> entry : paragraph2idMap.entrySet()) {
      String paragraph = entry.getKey();
      matcher = pattern.matcher(paragraph);
      while (matcher.find()) {
        String paraFirst = paragraph.substring(0, matcher.end() - 1);
        String paraLast = paragraph.substring(matcher.end(), paragraph.length());
        paragraph = paraFirst + punc + paraLast;
        context.docInfo.getParaLabels().getParagraphLabel(entry.getValue()).getContent()
            .put(entry.getValue(), paragraph);
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取法院信息 法院名称、法院省、法院市、法院区县、法院层级
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-法院资源名称 3-法院层级正则
   * @return
   */
  public ResultCode rule_func_extract_court_by_regex(ParamsBean fucParams) {
    String tagStr = fucParams.getTagList();
    String filePath = "classification.court.china";
    String regexStr = fucParams.getRegex();
    ResourceReader.getInstance().readCourt(filePath);
    Pattern patternCourtHierarchy = Pattern.compile(regexStr);
    String paragraphStr = "";
    // 获取当前标签下的所有自然段
    Map<Integer, String> map = context.docInfo.getParaLabels().getByLabel(tagStr).getContent();
    if (map == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    for (int i : map.keySet()) {
      paragraphStr = map.get(i);
      Matcher mater = Pattern.compile("([\u4e00-\u9fae]+)*?法院").matcher(paragraphStr);
      if (mater.find()) {
        paragraphStr = mater.group();
      } else {
        return RabbitResultCode.RABBIT_INVALID_PARAM;
      }
      Matcher matcherCourtName =
          ResourceReader.getInstance().patternCourtName.matcher(paragraphStr);
      if (matcherCourtName.find()) {
        String court = matcherCourtName.group();
        int start = paragraphStr.indexOf(court);
        String paragraph = paragraphStr.substring(0, start);
        if (court.equals(paragraphStr) || paragraph.endsWith("省") || paragraph.endsWith("市")) {
          String[] oneArea = ResourceReader.getInstance().stdCourt2Area.get(court);
          context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_name[InfoPointKey.mode],
              court);
          if ((oneArea != null) && (oneArea[0] != null)) {
            context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_province[InfoPointKey.mode],
                oneArea[0]);
            if ((oneArea.length > 1) && (oneArea[1] != null)) {
              context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_city[InfoPointKey.mode],
                  oneArea[1]);
              if ((oneArea.length > 2) && (oneArea[2] != null)) {
                context.rabbitInfo.extractInfo
                    .put(InfoPointKey.meta_court_county[InfoPointKey.mode], oneArea[2]);
              }
            }
          }
        } else {
          Pattern pattern;
          if (paragraphStr.contains("省")) {
            int start2 = paragraphStr.indexOf("省");
            String s = paragraphStr.substring(start2 + 1, paragraphStr.length());
            pattern = Pattern.compile(s);
          } else {
            pattern = Pattern.compile(paragraphStr);
          }
          for (Map.Entry<String, String[]> entry : ResourceReader.getInstance().stdCourt2Area
              .entrySet()) {
            String key = entry.getKey();
            Matcher matcher = pattern.matcher(key);
            if (matcher.find()) {
              context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_name[InfoPointKey.mode],
                  key);
              String[] oneArea = entry.getValue();
              if ((oneArea != null) && (oneArea[0] != null)) {
                context.rabbitInfo.extractInfo
                    .put(InfoPointKey.meta_court_province[InfoPointKey.mode], oneArea[0]);
                if ((oneArea.length > 1) && (oneArea[1] != null)) {
                  context.rabbitInfo.extractInfo
                      .put(InfoPointKey.meta_court_city[InfoPointKey.mode], oneArea[1]);
                  if ((oneArea.length > 2) && (oneArea[2] != null)) {
                    context.rabbitInfo.extractInfo
                        .put(InfoPointKey.meta_court_county[InfoPointKey.mode], oneArea[2]);
                  }
                }
              }
              break;
            }
          }
        }
        if (context.rabbitInfo.extractInfo
            .get(InfoPointKey.meta_court_name[InfoPointKey.mode]) == null) {
          String[] oneArea =
              ResourceReader.getInstance().stdCourt2Area.get(matcherCourtName.group());
          context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_name[InfoPointKey.mode],
              matcherCourtName.group());
          if ((oneArea != null) && (oneArea[0] != null)) {
            context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_province[InfoPointKey.mode],
                oneArea[0]);
            if ((oneArea.length > 1) && (oneArea[1] != null)) {
              context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_city[InfoPointKey.mode],
                  oneArea[1]);
              if ((oneArea.length > 2) && (oneArea[2] != null)) {
                context.rabbitInfo.extractInfo
                    .put(InfoPointKey.meta_court_county[InfoPointKey.mode], oneArea[2]);
              }
            }
          }
        }
      } else {
        Pattern pattern = Pattern.compile(paragraphStr);
        for (Map.Entry<String, String[]> entry : ResourceReader.getInstance().stdCourt2Area
            .entrySet()) {
          String key = entry.getKey();
          Matcher matcher = pattern.matcher(key);
          if (matcher.find()) {
            String court = matcher.group();
            int start = key.indexOf(court);
            if (start < 1)
              break;
            String address = key.substring(0, start);
            if (address.endsWith("省") || address.endsWith("市")) {
              context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_name[InfoPointKey.mode],
                  key);
              String[] oneArea = entry.getValue();
              if ((oneArea != null) && (oneArea[0] != null)) {
                context.rabbitInfo.extractInfo
                    .put(InfoPointKey.meta_court_province[InfoPointKey.mode], oneArea[0]);
                if ((oneArea.length > 1) && (oneArea[1] != null)) {
                  context.rabbitInfo.extractInfo
                      .put(InfoPointKey.meta_court_city[InfoPointKey.mode], oneArea[1]);
                  if ((oneArea.length > 2) && (oneArea[2] != null)) {
                    context.rabbitInfo.extractInfo
                        .put(InfoPointKey.meta_court_county[InfoPointKey.mode], oneArea[2]);
                  }
                }
              }
              break;
            }
          }
        }
        if (context.rabbitInfo.extractInfo
            .get(InfoPointKey.meta_court_name[InfoPointKey.mode]) == null) {
          if (paragraphStr.contains("省")) {
            int end = paragraphStr.indexOf("省") + 1;
            String province = paragraphStr.substring(0, end);
            String court = paragraphStr.substring(end, paragraphStr.length());
            Pattern pattern1 = Pattern.compile(court);
            for (Map.Entry<String, String[]> entry : ResourceReader.getInstance().stdCourt2Area
                .entrySet()) {
              String key = entry.getKey();
              Matcher matcher = pattern1.matcher(key);
              if (matcher.find()) {
                String[] oneArea = entry.getValue();
                if ((oneArea != null) && (oneArea[0] != null)) {
                  if (oneArea[0].equals(province)) {
                    context.rabbitInfo.extractInfo
                        .put(InfoPointKey.meta_court_name[InfoPointKey.mode], key);
                    context.rabbitInfo.extractInfo
                        .put(InfoPointKey.meta_court_province[InfoPointKey.mode], oneArea[0]);
                    if ((oneArea.length > 1) && (oneArea[1] != null)) {
                      context.rabbitInfo.extractInfo
                          .put(InfoPointKey.meta_court_city[InfoPointKey.mode], oneArea[1]);
                      if ((oneArea.length > 2) && (oneArea[2] != null)) {
                        context.rabbitInfo.extractInfo
                            .put(InfoPointKey.meta_court_county[InfoPointKey.mode], oneArea[2]);
                      }
                    }
                    break;
                  }
                }
              }
            }
          }
        }
      }
      Matcher matcherCourtHierarchy = patternCourtHierarchy.matcher(paragraphStr);
      if (matcherCourtHierarchy.find()) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_hierarchy[InfoPointKey.mode],
            matcherCourtHierarchy.group(1));
      } else {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_court_hierarchy[InfoPointKey.mode],
            "基层");
      }
      break;
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取案号、判决层级、案件类型
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则(案号)
   * @return
   */
  public ResultCode rule_func_extract_case_id_by_regex(ParamsBean fucParams) {
    String tagStr = fucParams.getTagList();
    String regexStr1 = fucParams.getRegex();
    Pattern patternCaseId = Pattern.compile(regexStr1);
    CaseHierarchy caseHierarchy = CaseHierarchy.DEFAULT;
    String paragraphStr;
    // 遍历每一个subinfo的mean_name
    Map<Integer, String> map = context.docInfo.getParaLabels().getByLabel(tagStr).getContent();
    if (map == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    for (int i : map.keySet()) {
      String preTag = context.docInfo.getParaLabels().getParagraphLabel(i).label;
      if (!preTag.equals(tagStr)) {
        continue;
      }
      paragraphStr = map.get(i);
      Matcher matcherCaseId = patternCaseId.matcher(paragraphStr);
      if (matcherCaseId.find()) {
        String result = matcherCaseId.group();
        result = result.replaceAll("[\\(﹝〔【\\[]", "（");
        result = result.replaceAll("[\\)〕﹞】\\]]", "）");
        if (null == context.rabbitInfo.extractInfo
            .get(InfoPointKey.meta_case_id[InfoPointKey.mode]))
          if (result.length() < 32)
            context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_id[InfoPointKey.mode],
                result);
      }
      for (Map.Entry<String, String> entry : ResourceReader.getInstance().anhao2Pattern
          .entrySet()) {
        Pattern tmpCompile = Pattern.compile(entry.getKey());
        Matcher matcher = tmpCompile.matcher(paragraphStr);
        if (matcher.find()) {
          String numStr = entry.getValue();
          String caseLevel = ResourceReader.getInstance().anhao2CaseLevel.get(numStr);
          if ("一审".equals(caseLevel)) {
            caseHierarchy = CaseHierarchy.FIRST_TRIAL;
          } else if ("二审".equals(caseLevel)) {
            caseHierarchy = CaseHierarchy.SECOND_TRIAL;
          } else if ("再审".equals(caseLevel)) {
            caseHierarchy = CaseHierarchy.RE_TRIAL;
          }

          if (null == context.rabbitInfo.extractInfo
              .get(InfoPointKey.meta_case_type[InfoPointKey.mode])) {
            String caseType = ResourceReader.getInstance().anhao2CaseType.get(numStr);
            CaseType ct = CaseType.DEFAULT;
            if ("民事".equals(caseType)) {
              ct = CaseType.CIVIL_CASE;
            } else if ("刑事".equals(caseType)) {
              ct = CaseType.CRIMINAL_CASE;
            } else if ("行政".equals(caseType)) {
              ct = CaseType.ADMIN_CASE;
            } else if ("赔偿".equals(caseType)) {
              ct = CaseType.COMPENSATION;
            } else if ("执行".equals(caseType)) {
              ct = CaseType.ENFORCEMENT;
            }
            context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_type[InfoPointKey.mode],
                ct.toString());
          }
          break;
        }
      }
    }
    context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode],
        caseHierarchy.toString());

    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取案件类型信息 案件类型、判决类型、判决书名称
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-正则(民事、刑事、行政赔偿、判决书、裁定书、调解书) 3-正则(决定书、通知书、法令)
   * @return
   */
  public ResultCode rule_func_extract_case_type_by_regex(ParamsBean fucParams) {
    String tagStr = fucParams.getTagList();
    String regexStr1 = fucParams.getRegex().split(SPLITER)[0];
    String regexStr2 = fucParams.getRegex().split(SPLITER)[1];
    Pattern pattern1 = Pattern.compile(regexStr1);
    Pattern pattern2 = Pattern.compile(regexStr2);
    String paragraphStr;
    // 遍历每一个subinfo的mean_name
    Map<Integer, String> map = context.docInfo.getParaLabels().getByLabel(tagStr).getContent();
    if (map == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    for (int i : map.keySet()) {
      String preTag = context.docInfo.getParaLabels().getParagraphLabel(i).label;
      if (!preTag.equals(tagStr)) {
        continue;
      }
      paragraphStr = map.get(i);
      CaseType caseType = null;
      String judgementType = "";
      Matcher matcher1 = pattern1.matcher(paragraphStr);
      if (matcher1.find()) {
        switch (matcher1.group(1)) {
          case "民事": {
            if (paragraphStr.contains("附")) {
              caseType = CaseType.CRIMINAL_CIVIL_CASE;
            } else {
              caseType = CaseType.CIVIL_CASE;
            }
            break;
          }
          case "刑事": {
            if (paragraphStr.contains("附")) {
              caseType = CaseType.CRIMINAL_CIVIL_CASE;
            } else {
              caseType = CaseType.CRIMINAL_CASE;
            }
            break;
          }
          case "行政赔偿": {
            caseType = CaseType.COMPENSATION;
            break;
          }
          default:
            break;
        }
        switch (matcher1.group(2)) {
          case "判决书": {
            judgementType = "判决书";
            break;
          }
          case "调解书": {
            judgementType = "调解书";
            break;
          }
          default: {
            judgementType = "裁定书";
            break;
          }
        }
      } else {
        Matcher matcher2 = pattern2.matcher(paragraphStr);
        if (matcher2.find()) {
          switch (matcher2.group(1)) {
            case "决定书": {
              judgementType = "决定书";
              break;
            }
            case "通知书": {
              judgementType = "通知书";
              break;
            }
            default: {
              judgementType = "令";
              break;
            }
          }
        }
      }
      if (caseType != null) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_type[InfoPointKey.mode],
            caseType.toString());
      }
      if (!"".equals(judgementType)) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_judgement_type[InfoPointKey.mode],
            judgementType);
      }
      if ((caseType == null) && ("".equals(judgementType)))
        continue;
      context.rabbitInfo.extractInfo.put(InfoPointKey.meta_doc_name[InfoPointKey.mode],
          paragraphStr);
      break;
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：1-根据上个段落已有的tag给没有tag的段落打上tag 2-构建section
   * 
   * @author yu.yao
   * @param fucParams 1-标签，多个用#隔开 2-section信息点名称
   * @return
   */
  public ResultCode rule_func_add_section_by_tag(ParamsBean fucParams) {
    String[] tagList = fucParams.getTagList().split(SPLITER);
    String sectionKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(sectionKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    context.rabbitInfo.extractInfo.put(sectionKey,
        TextUtils.getSbParagraph(context, tagList).toString());
    return RabbitResultCode.RABBIT_SUCCESS;
  }
}
