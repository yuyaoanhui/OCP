package com.ocp.rabbit.repository.tool.algorithm;

/**
 * Created by chengyong on 2017/8/17.
 */
public class RelatedCase {
    private String strPreCaseCourt; //审判的法院名称
    private String strPreCaseId;    //审判案号
    private String strPreCaseDate;  //审判时间
    private String strCaseName;     //案件名称

    public RelatedCase(String strPreCaseId) {
        this.strPreCaseId = strPreCaseId;
    }

    public RelatedCase(String strPreCaseCourt, String strPreCaseId) {
        this.strPreCaseCourt = strPreCaseCourt;
        this.strPreCaseId = strPreCaseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatedCase that = (RelatedCase) o;

        return strPreCaseId != null ? strPreCaseId.equals(that.strPreCaseId) : that.strPreCaseId == null;

    }

    @Override
    public int hashCode() {
        return strPreCaseId != null ? strPreCaseId.hashCode() : 0;
    }

    public String getStrPreCaseCourt() {
        return strPreCaseCourt;
    }

    public void setStrPreCaseCourt(String strPreCaseCourt) {
        this.strPreCaseCourt = strPreCaseCourt;
    }

    public String getStrPreCaseId() {
        return strPreCaseId;
    }

    public void setStrPreCaseId(String strPreCaseId) {
        this.strPreCaseId = strPreCaseId;
    }

    public String getStrPreCaseDate() {
        return strPreCaseDate;
    }

    public void setStrPreCaseDate(String strPreCaseDate) {
        this.strPreCaseDate = strPreCaseDate;
    }

    public String getStrCaseName() {
        return strCaseName;
    }

    public void setStrCaseName(String strCaseName) {
        this.strCaseName = strCaseName;
    }
}
