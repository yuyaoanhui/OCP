package com.ocp.rabbit.plugin.custom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.base.result.ResultCode;
import com.ocp.rabbit.proxy.extractor.common.CaseFeeExtractor;
import com.ocp.rabbit.proxy.extractor.common.CriminalRecordExtractor;
import com.ocp.rabbit.proxy.extractor.common.CriminalSentenceExtractor;
import com.ocp.rabbit.proxy.extractor.common.FurtherExtractor;
import com.ocp.rabbit.proxy.extractor.common.PreCaseIdExtractor;
import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.extractor.custom.dispute.TestRegexBasedModelExtractor;
import com.ocp.rabbit.proxy.extractor.custom.dispute.TestRegexBasedModelExtractor3;
import com.ocp.rabbit.proxy.extractor.custom.divoce.ChildCustodyExtractor;
import com.ocp.rabbit.proxy.extractor.custom.divoce.ChildExtractor;
import com.ocp.rabbit.proxy.extractor.custom.divoce.DivorceExtractor;
import com.ocp.rabbit.proxy.extractor.internal.PeopleInfoExtractor;
import com.ocp.rabbit.repository.algorithm.LitigantRoleRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.bean.ParaLabelBean;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.constant.CaseType;
import com.ocp.rabbit.repository.constant.JudgementType;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.tool.algorithm.trafficAccident.TrafficAccidentExtractor;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 无法用正则匹配参数的信息点抽取
 *
 * @author yu.yao 2018年8月22日
 */
public class BussinessPointPlugin extends AbstractRuleFunctionPlugin {
  private ReferLigitantRelatedInfoExtrator referExtractor;
  private CriminalSentenceExtractor cse;
  private PreCaseIdExtractor caseIdExtractor;
  private FurtherExtractor furtherExtractor;
  private DivorceExtractor de;
  private ChildExtractor childExtractor;
  private ChildCustodyExtractor childCustodyExtractor;
  private TestRegexBasedModelExtractor trbmExtractor;
  private TestRegexBasedModelExtractor3 trbmExtractor3;
  private TrafficAccidentExtractor traffic;

  public BussinessPointPlugin(com.ocp.rabbit.proxy.process.AbstractProcess.Context context) {
    super(context);
    referExtractor = new ReferLigitantRelatedInfoExtrator(context);
    cse = new CriminalSentenceExtractor(context);
    caseIdExtractor = new PreCaseIdExtractor(context);
    furtherExtractor = new FurtherExtractor(context);
    de = new DivorceExtractor(context);
    childExtractor = new ChildExtractor(context);
    childCustodyExtractor = new ChildCustodyExtractor(context);
    trbmExtractor = new TestRegexBasedModelExtractor(context);
    trbmExtractor3 = new TestRegexBasedModelExtractor3(context);
    traffic = new TrafficAccidentExtractor(context);
  }


  private static final String SPLITER = "#";

  /**
   * 抽取关联案号、关联文书、起诉书信息
   *
   * @param fucParams 1-段落标签
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_case_summary(ParamsBean fucParams) {
    String tagStr = fucParams.getTagList();
    if (context.docInfo.getParaLabels().getByLabel(tagStr).getContent() == null) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    List<String> paragraphs = new ArrayList<String>();
    Map<Integer, String> map = context.docInfo.getParaLabels().getByLabel(tagStr).getContent();
    for (int i : map.keySet()) {
      paragraphs.add(map.get(i));
    }
    caseIdExtractor.extract(paragraphs);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取案件受理费
   *
   * @param
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_case_fee(ParamsBean fucParams) {
    CaseFeeExtractor cfe = new CaseFeeExtractor(this.context);
    cfe.extract();
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取前科信息
   *
   * @param
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_criminal_record(ParamsBean fucParams) {
    CriminalRecordExtractor cre = new CriminalRecordExtractor(this.context);
    cre.extract();
    return RabbitResultCode.RABBIT_SUCCESS;
  }


  /**
   * 功能：抽取判罚信息
   *
   * @param fucParams 1-段落标签
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_criminal_sentence(ParamsBean fucParams) {
    String[] tagStr = fucParams.getTagList().split(SPLITER);
    List<ParaLabelBean> labels = context.docInfo.getParaLabels().getByLabels(Arrays.asList(tagStr));
    List<String> paragraphs = new ArrayList<String>();
    for (ParaLabelBean pl : labels) {
      for (int i : pl.getContent().keySet()) {
        paragraphs.add(pl.getContent().get(i));
      }
    }
    if (paragraphs.isEmpty()) {
      return RabbitResultCode.RABBIT_NO_EXTRACT_INFO;
    }
    cse.extract(paragraphs);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：获取公检法机关名称及所在的省市区县信息
   *
   * @param fucParams 1-段落标签 2-(法院|检察院) 3-资源配置文件名称
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_dept_info(ParamsBean fucParams) {
    String tagName = fucParams.getTagList();
    String deptName = fucParams.getOrgName();
    String filePath = fucParams.getFilePaths();
    ResourceReader.getInstance().readDepartment(filePath);
    List<String> tags = new ArrayList<String>();
    tags.add(tagName);
    String tarTitle = context.docInfo.getParaLabels().getContentSumByLabels(tags).toString();
    if (tarTitle.contains(deptName)) {
      String matcherString = "";
      Pattern pattern = Pattern.compile("^(.*)人民" + deptName);
      Matcher matcher = pattern.matcher(tarTitle);
      if (matcher.find()) {
        matcherString = matcher.group(0);
      }
      Matcher matcherDeptName =
          ResourceReader.getInstance().patternDepartmentName.matcher(matcherString);
      if (matcherDeptName.find() && ResourceReader.getInstance().stdDepartment2Atea
          .get(matcherDeptName.group(0)) != null) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_procuratorate_name[InfoPointKey.mode],
            matcherString);
        String[] oneArea =
            ResourceReader.getInstance().stdDepartment2Atea.get(matcherDeptName.group());
        if (oneArea.length > 0 && oneArea[0] != null) {
          context.rabbitInfo.extractInfo
              .put(InfoPointKey.meta_procuratorate_province[InfoPointKey.mode], oneArea[0]);
          if (oneArea.length > 1 && oneArea[1] != null) {
            context.rabbitInfo.extractInfo
                .put(InfoPointKey.meta_procuratorate_city[InfoPointKey.mode], oneArea[1]);
            if (oneArea.length > 2 && oneArea[2] != null) {
              context.rabbitInfo.extractInfo
                  .put(InfoPointKey.meta_procuratorate_county[InfoPointKey.mode], oneArea[2]);
            }
          }
        }
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取信息点:1.侵害物品,2.侵害场所
   *
   * @param
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_infracted(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<ParaLabelBean> paragraphList =
        context.docInfo.getParaLabels().getByLabels(Arrays.asList(tagStrList));
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String name2peopleKey = TextUtils.getRightKeyByName(fucParams.getCacheKey());
    if (TextUtils.isEmpty(name2peopleKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    List<String> listStr = new ArrayList<String>();
    for (ParaLabelBean pl : paragraphList) {
      for (int i : pl.getContent().keySet()) {
        listStr.add(i, pl.getContent().get(i));
      }
    }
    furtherExtractor.infracted(listStr, key, name2peopleKey);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取物业服务合同纠纷中的诉讼双方
   *
   * @param fucParams 1-信息点名称
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_parse_both_sides(ParamsBean fucParams) {
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    String result = furtherExtractor.parseBothSides();
    if (result != null) {
      context.rabbitInfo.extractInfo.put(key, result);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取机动车交通事故责任纠纷中的信息点
   *
   * @param
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_traffic_accdient(ParamsBean fucParams) {
    traffic.extractor();
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取离婚纠纷中的信息点
   *
   * @param fucParams 1-抽取子女信息段落标签 2-抚养权信息点名称（抚养费支付方式/父亲所付抚养费/母亲所付抚养费/男孩抚养人/女孩抚养人/所有小孩抚养人）
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_divorce(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    List<String> paragraphList = TextUtils.getParagraphList(context, tagStrList);
    if (paragraphList.isEmpty()) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    // 抽取子女信息
    childExtractor.childExtract(paragraphList);
    // 抽取信息
    de.extract(context.rabbitInfo);
    String[] infoKeys = fucParams.getDependentPoints().split(SPLITER);
    List<String> keyNames = new ArrayList<String>();
    for (String keyName : infoKeys) {
      keyNames.add(TextUtils.getRightKeyByName(keyName));
    }
    // 抽取抚养权信息
    childCustodyExtractor.childCustodyExtract(context.rabbitInfo, keyNames);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取拐卖到境外 （目前无法确定是从境外到境内还是从境内到境外，定义为涉外）
   *
   * @param fucParams 1-段落标签 2-信息点名称
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_abroad_traffick(ParamsBean fucParams) {
    String[] tagStrList = fucParams.getTagList().split(SPLITER);
    StringBuilder sbParagraph =
        context.docInfo.getParaLabels().getContentSumByLabels(Arrays.asList(tagStrList));
    if (sbParagraph.length() == 0) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String key = TextUtils.getRightKeyByName(fucParams.getInfoPointName());
    if (TextUtils.isEmpty(key)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    if (FurtherExtractor.judgeForeign(sbParagraph)) {
      context.rabbitInfo.extractInfo.put(key, true);
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取人物的基本信息、犯罪前科信息和羁押信息
   *
   * @param fucParams 1-角色资源文件 2-类型，法院court 检察院procuratorate 3-法院名称信息点
   *        4-法院资源的路径，"classification.court.china"
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_people_info(ParamsBean fucParams) {
    String[] filePaths = fucParams.getFilePaths().split(SPLITER);
    String litigantRoleFile = filePaths[0];
    LitigantRoleRecognizer.readLitigantRole(litigantRoleFile);
    // court:法院 procuratorate：检察院
    String type = fucParams.getOrgName();
    String courtNameKey =
        TextUtils.getRightKeyByName(fucParams.getDependentPoints().split(SPLITER)[0]);
    String courtFilePath = filePaths[1];
    ResourceReader.getInstance().readCourt(courtFilePath);
    if (TextUtils.isEmpty(courtNameKey)) {
      return RabbitResultCode.RABBIT_INVALID_PARAM;
    }
    String courtName = null;
    if (context.rabbitInfo.extractInfo.get(courtNameKey) != null) {
      courtName = (String) (context.rabbitInfo.extractInfo.get(courtNameKey));
    }
    PeopleInfoExtractor p = new PeopleInfoExtractor(context, litigantRoleFile, type, courtName);
    p.extract();
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：抽取引诱、容留、介绍卖淫罪涉外 （目前无法确定是从境外到境内还是从境内到境外，定义为涉外）
   *
   * @param fucParams 1-段落标签 2-信息点名称
   * @return
   * @author yu.yao
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_abroad_prostitute(ParamsBean fucParams) {
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
    Map<String, People> name2People = (Map<String, People>) context.rabbitInfo.extractInfo
        .getOrDefault(name2peopleKey, new HashMap<>());
    Map<String, Boolean> rsltMap = referExtractor.extractAbroadProstitute(paragraphList, infoKey);
    for (Map.Entry<String, Boolean> entry : rsltMap.entrySet()) {
      String name = entry.getKey();
      if ((name2People.containsKey(name))
          && (name2People.get(name).getPeopleAttrMap().get(infoKey) == null)) {
        Boolean value = entry.getValue();
        name2People.get(name).getPeopleAttrMap().put(infoKey, value);
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取案件来源信息
   *
   * @param
   * @return
   * @author yu.yao
   */
  @SuppressWarnings("unchecked")
  public ResultCode rule_func_extract_case_from(ParamsBean fucParams) {
    String caseHierarchy = "";
    String caseType = "";
    if (context.rabbitInfo.extractInfo
        .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode]) != null) {
      caseHierarchy = (String) (context.rabbitInfo.extractInfo
          .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode])); // 审判层级
    }
    if (context.rabbitInfo.extractInfo
        .get(InfoPointKey.meta_case_type[InfoPointKey.mode]) != null) {
      caseType = context.rabbitInfo.extractInfo.get(InfoPointKey.meta_case_type[InfoPointKey.mode])
          .toString(); // 案件类型
    }
    // 案件来源
    boolean bProtest = false;
    boolean bPeopleApply = false;
    boolean bPublicProsecution = false;
    List<People> lp = (List<People>) (context.rabbitInfo.extractInfo
        .getOrDefault(InfoPointKey.meta_people_attr[InfoPointKey.mode], new ArrayList<People>()));
    for (People p : lp) {
      if (!p.getPeopleAttrMap()
          .containsKey(InfoPointKey.info_litigant_position[InfoPointKey.mode])) {
        continue;
      }
      String roles = (String) (p.getPeopleAttrMap()
          .get(InfoPointKey.info_litigant_position[InfoPointKey.mode]));
      if (roles == null) {
        continue;
      }
      if (roles.contains("抗诉机关")) {
        bProtest = true;
        break;
      } else if (roles.contains("公诉")) {
        bPublicProsecution = true;
      } else if (roles.contains("自诉") || roles.contains("再审申请人") || roles.contains("申请再审人")) {
        bPeopleApply = true;
        break;
      }
    }
    if (caseHierarchy.equals("一审")) {
      if (caseType.contains("刑事")) {// 刑事案件
        if (bPublicProsecution) {
          context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode], "公诉");
        } else if (bPeopleApply) {
          context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode], "自诉");
        } else {
          // 不存在公诉机关的情况
          // 用最后一个匹配到的情况判断
          String s = (String) (context.rabbitInfo.extractInfo
              .getOrDefault(InfoPointKey.section_sub_fact[InfoPointKey.mode], ""));
          NamedEntity[] nes =
              NamedEntityRecognizer.recognizeEntityByRegex(s, Pattern.compile("提起公诉|自诉"));
          if (nes.length == 0)
            context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode],
                "其他");
          else {
            if (nes[nes.length - 1].getSource().contains("提起公诉"))
              context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode],
                  "公诉");
            else
              context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode],
                  "自诉");
          }
        }
      } else {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode], "其他");
      }
    }
    if (caseHierarchy.equals("二审")) {
      if (bProtest) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode], "抗诉");
      } else {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode], "上诉");
      }
    } else if (caseHierarchy.equals("再审")) {
      if (bProtest) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode], "抗诉");
      } else if (bPeopleApply) {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode],
            "当事人申请再审");
      } else {
        context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_from[InfoPointKey.mode],
            "法院提起再审");
      }
    }
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 抽取案件结果信息
   *
   * @param fucParams 1-段落标签
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_close_manner(ParamsBean fucParams) {
    String tagStr = fucParams.getTagList();
    Pattern patternOptionalJudge = Pattern.compile("驳回[\u4e00-\u9fa5]*(其他|其它|部分)[的地得]?诉讼请求");
    Pattern patternOptionalJudge2 = Pattern.compile("驳回[\u4e00-\u9fa5]*对[\u4e00-\u9fa5]+诉讼请求");
    Pattern patternRejectClaim = Pattern.compile("驳回[\u4e00-\u9fa5]*诉讼请求");
    Pattern patternRejectProsection = Pattern.compile("驳回[\u4e00-\u9fa5]*起诉");
    Pattern patternOptionalJudge3 = Pattern.compile("[二三四五六七八九十]");
    String paragraph;
    String strCloseManner = "";
    Map<Integer, String> map = context.docInfo.getParaLabels().getByLabel(tagStr).getContent();
    for (int i : map.keySet()) {
      String preTag = context.docInfo.getParaLabels().getParagraphLabel(i).label;
      // 如果找到了该tag
      if (!preTag.equals(tagStr)) {
        continue;
      }
      paragraph = map.get(i);
      Matcher matcher = patternOptionalJudge.matcher(paragraph);
      if (matcher.find()) {
        strCloseManner = "部分支持";
        break;
      } else {
        matcher = patternOptionalJudge2.matcher(paragraph);
        if (matcher.find()) {
          strCloseManner = "部分支持";
          break;
        }
      }
    }
    if (strCloseManner.isEmpty()) {
      for (int i : map.keySet()) {
        String preTag = context.docInfo.getParaLabels().getParagraphLabel(i).label;
        // 如果找到了该tag
        if (!preTag.equals(tagStr)) {
          continue;
        }
        paragraph = map.get(i);
        Matcher matcher = patternRejectClaim.matcher(paragraph);
        if (matcher.find()) {
          if (patternOptionalJudge3.matcher(paragraph).find())
            strCloseManner = "部分支持";
          else
            strCloseManner = "驳回诉讼请求";
          break;
        }
      }
    }
    if (strCloseManner.isEmpty()) {
      for (int i : map.keySet()) {
        String preTag = context.docInfo.getParaLabels().getParagraphLabel(i).label;
        // 如果找到了该tag
        if (!preTag.equals(tagStr)) {
          continue;
        }
        paragraph = map.get(i);
        Matcher matcher = patternRejectProsection.matcher(paragraph);
        if (matcher.find()) {
          strCloseManner = "驳回起诉";
          break;
        }
      }
    }
    if (strCloseManner.isEmpty()) {
      List<String> units = context.getAllUnits();
      for (String unit : units) {
        if (unit.contains("判决如下")) {
          strCloseManner = "全部支持";
          break;
        }
      }
    }
    if (strCloseManner.isEmpty()) {
      strCloseManner = "其他";
    }
    context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_close_manner[InfoPointKey.mode],
        strCloseManner);
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 争议焦点抽取
   *
   * @param
   * @return
   * @author yu.yao
   */
  public ResultCode rule_func_extract_controversy_focus(ParamsBean fucParams) {
    trbmExtractor.extract();
    trbmExtractor3.extract();
    return RabbitResultCode.RABBIT_SUCCESS;
  }

  /**
   * 功能：进行人物信息的抽取之前对案件类型进行筛选 不抽取笔录，刑事和刑事附带民事只抽取判决书和裁定书
   *
   * @param fucParams 1-案件类型 2-判决类型
   * @return
   * @author yu.yao
   */
  public boolean rule_has_people(ParamsBean fucParams) {
    String metaCaseType = TextUtils.getRightKeyByName(fucParams.getCaseType());
    String metaJudgementType = TextUtils.getRightKeyByName(fucParams.getJudgementType());
    if (context.rabbitInfo.extractInfo.get(metaCaseType) == null) {
      return false;
    }
    String caseType = (String) context.rabbitInfo.extractInfo.get(metaCaseType);
    if (context.rabbitInfo.extractInfo.get(metaJudgementType) == null) {
      return false;
    }
    String judgeType = (String) context.rabbitInfo.extractInfo.get(metaJudgementType);
    if (JudgementType.ORAL_CASE.equals(judgeType)) {
      return false;
    }
    if ((CaseType.CRIMINAL_CASE.equals(caseType) || CaseType.CRIMINAL_CIVIL_CASE.equals(caseType))
        && !(JudgementType.JUDGEMENT.equals(judgeType)
            || JudgementType.ARBITRAL.equals(judgeType))) {
      return false;
    }
    return true;
  }
}
