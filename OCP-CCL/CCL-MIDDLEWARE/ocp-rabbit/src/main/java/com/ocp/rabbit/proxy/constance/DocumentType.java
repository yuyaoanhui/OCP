package com.ocp.rabbit.proxy.constance;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义文书类型
 * 
 * @author yu.yao 2018年6月28日
 *
 */
public enum DocumentType {
  verdict("裁定书", "court"), //
  judgement("判决书", "court"), //
  conciliation("调解书", "court"), //
  decision("决定书", "court"), //
  notice("通知书", "court"), //
  reply("批复", "court"), //
  answer("答复", "court"), //
  letter("函", "court"), //
  order("令", "court"), //
  other("其他", "court"), //
  examiningCatchingOpinion("审查逮捕意见书", "procuratorate"), //
  indictment("起诉书", "procuratorate"), //
  nonProsecutionDecision("不起诉决定书", "procuratorate"), //
  recheckExaminingOpinion("复议(核)案件审查意见书", "procuratorate"), //
  appealArrest("提请批准(捕)逮捕书", "procuratorate");

  private String typeName;// 文书类型名称
  private String orgnization;// 组织机构：法院、检察院等

  public String getTypeName() {
    return typeName;
  }

  public String getOrgnization() {
    return orgnization;
  }

  /**
   * 默认private,枚举类型可保证绝对单例
   * 
   * @param typeName
   * @param orgnization
   * @param sourceDir
   */
  DocumentType(String typeName, String orgnization) {
    this.typeName = typeName;
    this.orgnization = orgnization;
  }

  public static List<String> getCourt() {
    List<String> types = new ArrayList<String>();
    for (DocumentType type : DocumentType.values()) {
      if (type.getOrgnization().equals("court")) {
        types.add(type.typeName);
      }
    }
    return types;
  }

  public static List<String> getProcuratorate() {
    List<String> types = new ArrayList<String>();
    for (DocumentType type : DocumentType.values()) {
      if (type.getOrgnization().equals("procuratorate")) {
        types.add(type.typeName);
      }
    }
    return types;
  }

}
