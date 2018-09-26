package com.ocp.rabbit.repository.tool.algorithm.personage;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 人物类别
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public enum PeopleType {
  DEFENDANT("被告"), //
  PLAINTIFF("原告"), //
  SUSPECT("犯罪嫌疑人"), //
  THIRD_PERSON("第三人"), //
  DEFENDANT_FIRST_TRIAL("一审被告"), //
  DEFENDANT_SECOND_TRIAL("二审被告"), //
  ENTRUSTED("委托代理人"), //
  ATTORNEY("律师"), //
  ASSIGNED("法定代理人"), //
  REPRESENTATIVE("法定代表人"), //
  CHIEF_JUDGE("审判长"), //
  JUDGE("审判员"), //
  JUDGE_ASSESSOR("人民陪审员"), //
  CLERK("书记员"), //
  DEFAULT_TYPE("默认"), //
  PROCURATORATE("检察院"), //

  // 一审
  F_PLAINTIFF("原告"), //
  F_PLAINTIFFM("原告人"), //
  F_DEFENDANT("被告"), //
  F_DEFENDANTM("被告人"), //
  F_CLAIMDEFENDANT("反诉被告"), //
  F_CONCLAIMDEFENDANTM("反诉被告人"), //
  F_CONCLAIMPLAINTIFFM("反诉原告人"), //
  F_CONCLAIMPLAINTIFF("反诉原告"), //
  F_THIRD_PERSON("第三人"), //

  // 二审
  S_APPELLANT("上诉人"), //
  S_O_TRIAL_PLAINTIFF("原审原告"), //
  S_O_TRIAL_PLAINTIFFM("原审原告人"), //
  S_C_CLAIM_DEFENDANT("反诉被告"), //
  S_C_CLAIM_DEFENDANTM("反诉被告人"), //
  S_O_TRIAL_DEFENDANT("原审被告"), //
  S_O_TRIAL_DEFENDANTM("原审被告人"), //
  S_C_CLAIM_PLAINTIFF("反诉原告"), //
  S_C_CLAIM_PLAINTIFFM("反诉原告人"), //
  S_F_THIRD_PERSON("一审第三人"), //
  S_APPELLEE("被上诉人"), //

  // 再审
  R_PROTESTORG("抗诉机关"), //
  R_COMPLAINANT("申诉人"), //
  R_C_CLAIM_PLAINTIFF("反诉原告"), //
  R_C_CLAIM_DEFENDANT("反诉被告"), //
  R_S_APPELLANT("二审上诉人"), //
  R_S_APPELLEE("二审被上诉人"), //
  R_RETRIAL_APPLICANT("再审申请人"), //
  R_RETRIAL_APPLICATION("申请再审人"), //
  R_O_RETRIAL_APPLICATION("原申请再审人"), //
  R_O_APPLICANT("原被申请人"), //
  R_RETRIAL_APPELLEE("再审被申请人"), //
  R_O_RETRIAL_APPLICANT("原再审申请人"), //
  R_O_RETRIAL_APPELLEE("原再审被申请人"), //
  R_F_THIRD_PERSON("一审第三人"), //
  R_O_RESPONDENT("原被申诉人"), //
  R_O_COMPLAINANT("原申诉人");//

  private String type;

  private PeopleType(String type) {
    this.type = type;
  }

  @Override
  public String toString() {
    return type;
  }

  @JsonValue
  public String getType() {
    return type;
  }

  public boolean equals(String str) {
    if (this.toString().equals(str)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * 根据段落标签大写后获得人物类型
   * 
   * @param type 段落标签大写
   * @return
   */
  public static PeopleType getPeopleType(String type) {
    switch (type) {
      case "DEFENDANT":
        return PeopleType.DEFENDANT;
      case "PLAINTIFF":
        return PeopleType.PLAINTIFF;
      case "THIRD_PERSON":
        return PeopleType.THIRD_PERSON;
      case "ENTRUSTED":
        return PeopleType.ENTRUSTED;
      case "ASSIGNED":
        return PeopleType.ASSIGNED;
      case "REPRESENTATIVE":
        return PeopleType.REPRESENTATIVE;
      case "ATTORNEY":
        return PeopleType.ATTORNEY;
      case "CHIEF_JUDGE":
        return PeopleType.CHIEF_JUDGE;
      case "JUDGES":
        return PeopleType.JUDGE;
      case "JUDGE_ASSESSOR":
        return PeopleType.JUDGE_ASSESSOR;
      case "CLERK":
        return PeopleType.CLERK;
      default:
        return null;
    }
  }
}
