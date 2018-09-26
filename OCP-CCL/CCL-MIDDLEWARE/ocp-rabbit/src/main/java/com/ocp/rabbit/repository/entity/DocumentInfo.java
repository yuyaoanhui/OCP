package com.ocp.rabbit.repository.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.proxy.constance.MajorAy;
import com.ocp.rabbit.repository.bean.ParaLabel;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;

/**
 * 文书信息类
 * 
 * @author yu.yao 2018年6月28日
 *
 */
public class DocumentInfo {

  private DocumentType docType;// 文书类型
  private String ay;// 案由名称
  private MajorAy majorAy;// 案由大类
  private ParaLabel paraLabels;// 标签体系
  private Map<ParaLabelEnum, List<ParaLabelEnum>> preCondition = ParaLabelEnum.getPreCondition();// 大标签前置条件
  private Map<ParaLabelEnum, List<Pattern>> labelPatterns =
      new HashMap<ParaLabelEnum, List<Pattern>>();// 标签的识别规则

  public DocumentType getDocType() {
    return docType;
  }

  public void setDocType(DocumentType docType) {
    this.docType = docType;
  }

  public String getAy() {
    return ay;
  }

  public void setAy(String ay) {
    this.ay = ay;
  }

  public MajorAy getMajorAy() {
    return majorAy;
  }

  public void setMajorAy(MajorAy majorAy) {
    this.majorAy = majorAy;
  }

  public ParaLabel getParaLabels() {
    return paraLabels;
  }

  public void setParaLabels(ParaLabel paraLabels) {
    this.paraLabels = paraLabels;
  }

  public Map<ParaLabelEnum, List<ParaLabelEnum>> getPreCondition() {
    return preCondition;
  }

  public Map<ParaLabelEnum, List<Pattern>> getLabelPatterns() {
    return labelPatterns;
  }

}
