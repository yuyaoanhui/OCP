package com.ocp.rabbit.repository.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ocp.rabbit.repository.constant.ParaLabelEnum;

public class ParaLabelBean implements Comparable<ParaLabelBean> {
  public ParaLabelEnum enumLabel;

  public String label;
  private Map<Integer, String> content = new HashMap<Integer, String>();// 标签内容,多个自然段
  private Map<Integer, String> orgContent = new HashMap<Integer, String>();// 标签原始内容,多个自然段

  public ParaLabelBean(ParaLabelEnum enumLabel, String label, Map<Integer, String> content,
      Map<Integer, String> orgContent) {
    this.enumLabel = enumLabel;
    this.label = label;
    this.content = content;
    this.orgContent = orgContent;
  }

    public static void  remove(List<ParaLabelBean> labels,int para){
      for (ParaLabelBean bean:labels){
          if(bean.getContent() != null && !bean.getContent().isEmpty()) {
              if (bean.getContent().keySet().contains(para)) {
                  bean.getContent().remove(para);
              }
          }
      }
    }
  // 升序
  @Override
  public int compareTo(ParaLabelBean obj) {
    if (this.enumLabel == null || obj == null) {
      return 0;
    }
    if (obj instanceof ParaLabelBean) {
      int thisNum = Integer.parseInt((String.valueOf(this.enumLabel.getLevel())
          + String.valueOf(this.enumLabel.getSequence())));
      int thatNum = Integer.parseInt((String.valueOf(((ParaLabelBean) obj).enumLabel.getLevel())
          + String.valueOf(((ParaLabelBean) obj).enumLabel.getSequence())));
      if (thisNum == thatNum) {
        if (this.enumLabel.getOrg().equals("court")) {
          return 1;
        } else {
          return -1;
        }
      }
      return thisNum - thatNum;
    }
    return 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (this.enumLabel == null) {
      return false;
    }
    if (obj instanceof ParaLabelBean) {
      return this.enumLabel.equals(((ParaLabelBean) obj));
    }
    return super.equals(obj);
  }

  @Override
  public int hashCode() {
    return this.enumLabel.hashCode();
  }

  public Map<Integer, String> getContent() {
    return content;
  }

  public void setContent(Map<Integer, String> content) {
    this.content = content;
  }

  public Map<Integer, String> getOrgContent() {
    return orgContent;
  }

  public void setOrgContent(Map<Integer, String> orgContent) {
    this.orgContent = orgContent;
  }

}
