package com.ocp.rabbit.repository.tool.algorithm.personage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ocp.rabbit.repository.tool.algorithm.CriminalJudgement;
import com.ocp.rabbit.repository.tool.algorithm.CriminalRecord;
import com.ocp.rabbit.repository.util.Position;

/**
 * 人物信息定义类
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class People {
  /**
   * 相关当事人的名字.
   */
  private String pname;

  /**
   * 相关当事人的类别,一个人可能有多个类别,如：原告、代理人;上诉人、一审被告.
   */
  private List<PeopleType> ptypes;

  // 人物的主类比，设置为list的第一个元素
  private PeopleType ptype;

  /**
   * 段落编号
   */
  private int index;

  /**
   * 名称类型：人名还是公司名<br>
   * 参见：{@link NameTypeConstants}
   */
  private int pnameType;

  @JsonIgnore
  private Position position;

  private List<CriminalRecord> recordList;

  private List<CriminalJudgement> judgeList;

  /**
   * 通过Map的形式添加属性,新建一个People的时候,必须初始化peopleAttrMap
   */
  public Map<String, Object> peopleAttrMap;// <信息点名，信息点值>

  public People(String pname) {
    this.pname = pname;
    peopleAttrMap = new HashMap<String, Object>();
  }

  public People(String pname, PeopleType ptype, int pnameType) {
    this.pname = pname;
    this.ptype = ptype;
    this.ptypes = new ArrayList<PeopleType>();
    this.ptypes.add(ptype);
    this.pnameType = pnameType;
    peopleAttrMap = new HashMap<String, Object>();
  }

  /**
   *
   * @param pname 名称
   * @param ptype 人物类型 被告、原告等
   * @param pnameType 名称类型 1 人名 2 公司
   */
  public People(String pname, PeopleType ptype, int pnameType, int index) {
    this.pname = pname;
    this.ptype = ptype;
    this.index = index;
    this.ptypes = new ArrayList<PeopleType>();
    this.ptypes.add(ptype);
    this.pnameType = pnameType;
    peopleAttrMap = new HashMap<String, Object>();
  }

  public People(String pname, PeopleType ptype, int pnameType, Position position) {
    super();
    this.pname = pname;
    this.ptype = ptype;
    this.pnameType = pnameType;
    this.position = position;
    this.ptypes = new ArrayList<PeopleType>();
    this.ptypes.add(ptype);
    peopleAttrMap = new HashMap<String, Object>();
  }

  public People(String pname, PeopleType ptype) {
    this.pname = pname;
    this.ptype = ptype;
    this.ptypes = new ArrayList<PeopleType>();
    this.ptypes.add(ptype);
    peopleAttrMap = new HashMap<String, Object>();
  }

  public People(String pname, List<PeopleType> types, PeopleType ptype, int pnameType) {
    super();
    this.pname = pname;
    this.ptypes = types;
    this.ptype = ptype;
    this.pnameType = pnameType;
    peopleAttrMap = new HashMap<String, Object>();

  }

  public People(String pname, List<PeopleType> ptypes) {
    this.pname = pname;
    this.ptypes = ptypes;
    if (ptypes.size() == 0) {
      this.ptype = PeopleType.DEFAULT_TYPE;
    } else {
      this.ptype = ptypes.get(0);
    }
    peopleAttrMap = new HashMap<String, Object>();
  }

  public boolean containType(PeopleType pt) {
    for (PeopleType t : ptypes) {
      if (t.equals(pt)) {
        return true;
      }
    }
    return false;
  }

  public int getPnameType() {
    return pnameType;
  }

  public void setPnameType(int pnameType) {
    this.pnameType = pnameType;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  public String getPname() {
    return pname;
  }

  public void setPname(String pname) {
    this.pname = pname;
  }

  public List<PeopleType> getPtypes() {
    return ptypes;
  }

  public void setPtypes(List<PeopleType> ptypes) {
    this.ptypes = ptypes;
  }

  public PeopleType getPtype() {
    return ptype;
  }

  public void setPtype(PeopleType ptype) {
    this.ptype = ptype;
  }

  public Map<String, Object> getPeopleAttrMap() {
    return peopleAttrMap;
  }

  public void setPeopleAttrMap(Map<String, Object> peopleAttrMap) {
    this.peopleAttrMap = peopleAttrMap;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public List<CriminalRecord> getRecordlist() {
    return recordList;
  }

  public void setRecordlist(List<CriminalRecord> recordlist) {
    this.recordList = recordlist;
  }

  public List<CriminalJudgement> getJudgeList() {
    return judgeList;
  }

  public void setJudgeList(List<CriminalJudgement> judgeList) {
    this.judgeList = judgeList;
  }

  @Override
  public String toString() {
    return "personage{" + "pname='" + pname + '\'' + ", ptypes=" + ptypes + ", ptype=" + ptype
        + ", pnameType=" + pnameType + ", recordList=" + recordList + ", judgeList=" + judgeList
        + ", peopleAttrMap=" + peopleAttrMap + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    People people = (People) o;
    if (!pname.equals(people.pname))
      return false;
    return ptype == people.ptype;
  }

  @Override
  public int hashCode() {
    int result = pname.hashCode();
    result = 31 * result + ptype.hashCode();
    return result;
  }
}
