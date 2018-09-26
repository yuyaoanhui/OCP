package com.ocp.rabbit.plugin.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.proxy.extractor.common.CommutationParoleExtractor;
import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.extractor.custom.law.LegalProvisionExtractor;
import com.ocp.rabbit.repository.bean.ParaLabelBean;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 需要依赖信息点的信息点抽取
 * 
 * @author yu.yao 2018年8月22日
 *
 */
public class DependPointPlugin extends AbstractRuleFunctionPlugin {
  public DependPointPlugin(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    super(context);
  }

  private static final String SPLITER = "#";
  private ReferLigitantRelatedInfoExtrator referExtractor =
      new ReferLigitantRelatedInfoExtrator(context);
  private CommutationParoleExtractor commutatExtractor = new CommutationParoleExtractor(context);
  private LegalProvisionExtractor lpExtractor = new LegalProvisionExtractor(context);

  /**
   * 功能：1、对section中对应的法律法规内容的位置加上超链接;2、获取对应的法律法规和id
   * 
   * @author yu.yao
   * @param fucParams 1-原section信息点 2-落款日期时间点 3-新section信息点 4-法律法规信息点 5-法律法规id信息点
   * @return
   */
  public ResultCode rule_func_build_section_with_url(ParamsBean fucParams) {
    String[] dependents = fucParams.getDependentPoints().split(SPLITER);
    String oldSectionKey = TextUtils.getRightKeyByName(dependents[0]);
    if (TextUtils.isEmpty(oldSectionKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String signatureDateKey = TextUtils.getRightKeyByName(dependents[1]);
    if (TextUtils.isEmpty(signatureDateKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String newSectionKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(newSectionKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String lawKey = TextUtils.getRightKeyByName(dependents[2]);
    if (TextUtils.isEmpty(lawKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String lawIdKey = TextUtils.getRightKeyByName(dependents[3]);
    if (TextUtils.isEmpty(lawIdKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    lpExtractor.extract(oldSectionKey, signatureDateKey, lawKey, lawIdKey);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 处理既遂犯,只要不是未遂,中止,预备,就默认他是既遂
   *
   * @param fucParams 1-依赖信息点名称（多个#隔开） 2-信息点名称
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_handel_accomplete_offense(ParamsBean fucParams) {
    String[] infoNames = fucParams.getDependentPoints().split(SPLITER);
    String targetInfo = fucParams.getInfoPointName();
    List<People> peoples = (List<People>) (context.rabbitInfo.extractInfo
        .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<People>()));
    if (peoples.size() == 0) {
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    boolean flag = true;
    for (People people : peoples) {
      Map<String, Object> peopleAttrMap = people.getPeopleAttrMap();
      for (String name : infoNames) {
        if (peopleAttrMap.containsKey(name)) {
          flag = false;
        }
      }
      if (flag) {
        people.getPeopleAttrMap().put(targetInfo, true);
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取人物的类型信息,该方法只针对刑事
   *
   * @param fucParams 1-人物信息(依赖信息点) 2-信息点名称
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_people_type_info(ParamsBean fucParams) {
    String infoKey = TextUtils.getRightKeyByName(fucParams.getDependentPoints().split(SPLITER)[0]);
    String infoName = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    List<People> peoples = (List<People>) (context.rabbitInfo.extractInfo.getOrDefault(infoKey,
        new ArrayList<People>()));
    if (peoples == null || peoples.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    for (People people : peoples) {
      if ("被告".equals(
          people.getPeopleAttrMap().get(InfoPointKey.info_litigant_position[InfoPointKey.mode]))) {
        if (people.getPnameType() == 1) {
          people.getPeopleAttrMap().put(infoName, "个人");
        } else if (people.getPnameType() == 2) {
          people.getPeopleAttrMap().put(infoName, "单位");
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取存在人物指代的两年内的次数
   * 
   * @author yu.yao
   * @param fucParams 1-段落标签 2-信息点名 3-meta_people_name2obj 4-判决日期 5-匹配的正向正则 6-反向正则
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_times_info(ParamsBean fucParams) {
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
    String signatureDateKey =
        TextUtils.getRightKeyByName(fucParams.getDependentPoints().split(SPLITER)[0]);
    if (TextUtils.isEmpty(signatureDateKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String positivePatternStr = fucParams.getRegex();
    String negativePatternStr = fucParams.getReverseRegex();
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    Map<String, Double> rsltMap = referExtractor.extractFrequency(paragraphList, signatureDateKey,
        positivePatternStr, negativePatternStr);
    if (rsltMap.isEmpty()) {
      tagStrList = new String[] {"office_opinion"};
      paragraphList = TextUtils.getParagraphList(context, tagStrList);
      rsltMap = referExtractor.extractFrequency(paragraphList, signatureDateKey, positivePatternStr,
          negativePatternStr);
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
   * 功能：判断集资诈骗、合同诈骗的金额范围 fucParams传入的参数：1-犯罪主体信息点名称 2-诈骗金额信息点名称 3-meta_people_name2obj 4-案由名称
   * 5-金额范围信息点
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_range_judgment(ParamsBean fucParams) {
    String[] dependents = fucParams.getDependentPoints().split(SPLITER);
    String keySubject = TextUtils.getRightKeyByName(dependents[0]);
    if (TextUtils.isEmpty(keySubject)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String keyMoney = TextUtils.getRightKeyByName(dependents[1]);
    if (TextUtils.isEmpty(keyMoney)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String anyou = TextUtils.getRightKeyByName(dependents[2]);
    if (TextUtils.isEmpty(anyou)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    for (Map.Entry<String, People> entry : name2People.entrySet()) {
      String reslt = (String) entry.getValue().getPeopleAttrMap().get(infoKey);
      if (reslt != null)
        continue;
      if (entry.getValue().getPeopleAttrMap().get(keySubject) == null)
        continue;
      String subject = (String) entry.getValue().getPeopleAttrMap().get(keySubject);
      if (entry.getValue().getPeopleAttrMap().get(keyMoney) == null)
        continue;
      double money = (double) entry.getValue().getPeopleAttrMap().get(keyMoney);
      if ("集资诈骗罪".equals(anyou)) {
        if ("个人".equals(subject)) {
          if (money >= 100000 && money < 300000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额较大");
          } else if (money >= 300000 && money < 1000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额巨大");
          } else if (money >= 1000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额特别巨大");
          }
        } else if ("单位".equals(subject)) {
          if (money >= 500000 && money < 1500000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额较大");
          } else if (money >= 1500000 && money < 5000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额巨大");
          } else if (money >= 5000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额特别巨大");
          }
        }
      } else if ("合同诈骗罪".equals(anyou)) {
        if ("个人".equals(subject)) {
          if (money >= 20000 && money < 100000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额较大");
          } else if (money >= 100000 && money < 1000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额巨大");
          } else if (money >= 1000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额特别巨大");
          }
        } else if ("单位".equals(subject)) {
          if (money >= 100000 && money < 1000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额较大");
          } else if (money >= 1000000 && money < 5000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额巨大");
          } else if (money >= 5000000) {
            name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额特别巨大");
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：判断信用卡诈骗的金额范围 fucParams传入的参数：1-4种诈骗手段信息点名称（#隔开）2-恶意透支信息点名称 3-诈骗金额信息点名称 4-meta_people_name2obj
   * 5-金额范围信息点
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_adjust_money_range(ParamsBean fucParams) {
    String[] fourKeys = {"info_falsify_credit", "info_falsify_identity_4credit",
        "info_invalidated_credit", "info_uttering_other_credit"};
    String[] keyMeans = fucParams.getDependentPoints().split(SPLITER);
    List<String> list = new ArrayList<>();
    for (String key : fourKeys) {
      if (!TextUtils.isEmpty(key)) {
        list.add(TextUtils.getRightKeyByName(key));
      }
    }
    String maliciousOverdraft = TextUtils.getRightKeyByName(keyMeans[4]);
    if ((TextUtils.isEmpty(maliciousOverdraft)) && (list.size() == 0)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String moneyKey = TextUtils.getRightKeyByName(keyMeans[5]);
    if (TextUtils.isEmpty(moneyKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    for (Map.Entry<String, People> entry : name2People.entrySet()) {
      String reslt = (String) entry.getValue().getPeopleAttrMap().get(infoKey);
      if (reslt != null)
        continue;
      if (entry.getValue().getPeopleAttrMap().get(moneyKey) == null)
        continue;
      double money = (double) entry.getValue().getPeopleAttrMap().get(moneyKey);
      if (entry.getValue().getPeopleAttrMap().containsKey(maliciousOverdraft)) {
        if (money >= 10000 && money < 100000) {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额较大");
        } else if (money >= 100000 && money < 1000000) {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额巨大");
        } else if (money >= 1000000) {
          name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额特别巨大");
        }
      } else {
        for (String key : list) {
          String rest = (String) entry.getValue().getPeopleAttrMap().get(infoKey);
          if (rest != null)
            break;
          if (entry.getValue().getPeopleAttrMap().containsKey(key)) {
            if (money >= 5000 && money < 50000) {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额较大");
            } else if (money >= 50000 && money < 500000) {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额巨大");
            } else if (money >= 500000) {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, "数额特别巨大");
            }
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：判断是否次数多次犯罪 fucParams传入的参数：1-信息点名称 2-meta_people_name2obj 3-依赖的信息点——次数
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_litigant_judgment_num(ParamsBean fucParams) {
    String infoKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(infoKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    String num = fucParams.getDependentPoints().split(SPLITER)[0];
    if ("".equals(num)) {
      Map<String, Double> resltMap = referExtractor.parsePeopleAttrDegree();
      for (Map.Entry<String, Double> entry : resltMap.entrySet()) {
        if (name2People.containsKey(entry.getKey())) {
          if (!name2People.get(entry.getKey()).getPeopleAttrMap().containsKey(infoKey)) {
            double value = entry.getValue();
            if (value > 1) {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, true);
            }
          }
        }
      }
    } else {
      String time = TextUtils.getRightKeyByName(num);
      for (Map.Entry<String, People> entry : name2People.entrySet()) {
        if (!entry.getValue().getPeopleAttrMap().containsKey(infoKey)) {
          if (entry.getValue().getPeopleAttrMap().containsKey(time)) {
            int value = (int) entry.getValue().getPeopleAttrMap().get(time);
            if (value > 1) {
              name2People.get(entry.getKey()).getPeopleAttrMap().put(infoKey, true);
            }
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 减刑假释抽取
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public ResultCode rule_func_extract_commutation_parole(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<ParaLabelBean> paragraphList =
        context.docInfo.getParaLabels().getByLabels(Arrays.asList(tagStrList));
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String[] dependent = fucParams.getDependentPoints().split(SPLITER);
    String signatureDateKey = TextUtils.getRightKeyByName(dependent[0]);
    if (TextUtils.isEmpty(signatureDateKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String signatureDate = null;
    if (context.rabbitInfo.extractInfo.get(signatureDateKey) != null) {
      signatureDate = (String) (context.rabbitInfo.extractInfo.get(signatureDateKey));
    }
    List<String> listStr = new ArrayList<String>();
    for (ParaLabelBean pl : paragraphList) {
      for (int i : pl.getContent().keySet()) {
        listStr.add(i, pl.getContent().get(i));
      }
    }
    commutatExtractor.extractCommutationParole(listStr, signatureDate);
    return RabbitResultCode.RABBIT_SUCCESS;
  }
}
