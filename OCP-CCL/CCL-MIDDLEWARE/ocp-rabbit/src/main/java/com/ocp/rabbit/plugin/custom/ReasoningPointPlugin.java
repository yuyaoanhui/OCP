package com.ocp.rabbit.plugin.custom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.proxy.extractor.common.CriminalRecordExtractor2;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.tool.algorithm.personage.PeopleType;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 用于信息点推理的插件
 * 
 * @author yu.yao 2018年8月21日
 *
 */
public class ReasoningPointPlugin extends AbstractRuleFunctionPlugin {
  public ReasoningPointPlugin(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    super(context);
  }

  private static final String SPLITER = "#";
  private CriminalRecordExtractor2 criminal2Extractor = new CriminalRecordExtractor2(context);

  /**
   * 功能1：根据前科罪名、是否受过刑事处罚 推理出 是否因xxx罪受过刑事处罚； <br>
   * 功能2：根据前科罪名、一年内是否受过行政处罚 推理出 一年内是否因xxx罪受过行政处罚； <br>
   * 
   * @author yu.yao
   * @param fucParams 1-落款日期，2-xxx罪 3-是否因xxx罪受过刑事处罚信息点 4-一年内是否因xxx罪受过行政处罚信息点
   * @return
   */
  public ResultCode rule_func_adjust_record_penalty(ParamsBean fucParams) {
    String crimeName = fucParams.getCrimeName();
    String signatureDateKey =
        TextUtils.getRightKeyByName(fucParams.getDependentPoints().split(SPLITER)[0]);
    if (TextUtils.isEmpty(signatureDateKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String crimeCriminalPenaltyKey = fucParams.getDependentPoints().split(SPLITER)[1];
    String crimeAdminPenaltyKey = fucParams.getDependentPoints().split(SPLITER)[2];
    if (TextUtils.isEmpty(crimeCriminalPenaltyKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    if (TextUtils.isEmpty(crimeAdminPenaltyKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    criminal2Extractor.extractRecordPenalty(signatureDateKey, crimeName, crimeCriminalPenaltyKey,
        crimeAdminPenaltyKey);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：根据前科罪名、几年内是否受过行政处罚 推理出 几年内是否因xxx罪受过行政处罚；<br>
   * 
   * @author yu.yao
   * @param fucParams 1-落款日期,2-xxx罪,3-几年内是否因xxx罪受过行政处罚信息点,4-几年
   * @return
   */
  public ResultCode rule_func_adjust_administrative_penalty(ParamsBean fucParams) {
    String crimeName = fucParams.getCrimeName();
    String[] arrays = fucParams.getDependentPoints().split(SPLITER);// 依赖信息点名列表
    String signatureDateKey = TextUtils.getRightKeyByName(arrays[0]);
    if (TextUtils.isEmpty(signatureDateKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String crimeAdminPenaltyKey = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(crimeAdminPenaltyKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    int num = Integer.parseInt(fucParams.getYearsNum());
    criminal2Extractor.extractRecordPenalty(signatureDateKey, crimeName, crimeAdminPenaltyKey, num);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：根据前科罪名 推理出 是否因xxx罪受过行政处罚或刑事处罚；<br>
   * 
   * @author yu.yao
   * @param fucParams 1-xxx罪 2-人物信息 3-信息点名称
   * @return
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_adjust_punished(ParamsBean fucParams) {
    String crimeName = fucParams.getCrimeName();
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    String infoName = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    for (Map.Entry<String, People> entry : name2People.entrySet()) {
      People people = entry.getValue();
      List<PeopleType> list = people.getPtypes();
      for (PeopleType pos : list) {
        if (pos.toString().contains("被告")) {
          if (people.getPeopleAttrMap()
              .get(InfoPointKey.info_record_crime[InfoPointKey.mode]) != null) {
            List<String> crimes = (List<String>) people.getPeopleAttrMap()
                .get(InfoPointKey.info_record_crime[InfoPointKey.mode]);
            for (String crime : crimes) {
              if (crime.contains(crimeName)) {
                people.getPeopleAttrMap().put(infoName, true);
              }
            }
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 根据值找到对应的范围
   * 
   * @author yu.yao
   * @param fucParams 1-信息点名称 2-范围对应的各个节点值，以#隔开 3-范围，以#隔开
   * @return
   */
  public ResultCode rule_func_adjust_value_to_range(ParamsBean fucParams) {
    String key = TextUtils.getRightKeyByName(fucParams.getTagList());
    if (context.rabbitInfo.extractInfo.get(key) == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    double rawValue = Double.valueOf(context.rabbitInfo.extractInfo.get(key).toString());
    String[] valueNodes = fucParams.getValueNodes().split(SPLITER);
    String[] valueRanges = fucParams.getValueRanges().split(SPLITER);
    if (rawValue > Double.valueOf(valueNodes[valueNodes.length - 1])) {
      context.rabbitInfo.extractInfo.put(key, valueRanges[valueRanges.length - 1]);
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    if (rawValue == Double.valueOf(valueNodes[0])) {
      context.rabbitInfo.extractInfo.put(key, valueRanges[0]);
      return RabbitResultCode.RABBIT_SUCCESS;
    }
    for (int i = 0; i < valueNodes.length; i++) {
      double compareValue = Double.valueOf(valueNodes[i]);
      if (rawValue <= compareValue) {
        context.rabbitInfo.extractInfo.put(key, valueRanges[i - 1]);
        return RabbitResultCode.RABBIT_SUCCESS;
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }
}
