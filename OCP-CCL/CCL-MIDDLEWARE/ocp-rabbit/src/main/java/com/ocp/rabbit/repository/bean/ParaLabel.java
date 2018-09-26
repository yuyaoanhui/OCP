package com.ocp.rabbit.repository.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.ocp.rabbit.repository.constant.ParaLabelEnum;


public class ParaLabel {

  // <大标签,小标签列表>
  public final Map<ParaLabelEnum, List<ParaLabelBean>> paralabels =
      new TreeMap<ParaLabelEnum, List<ParaLabelBean>>();

  public ParaLabel() {
    for (ParaLabelEnum instance : ParaLabelEnum.values()) {
      if (instance.getOrg().equals("court")) {
        if (instance.getLevel() == 1) {
          paralabels.put(instance, new ArrayList<ParaLabelBean>());
        }
      }
    }
    for (ParaLabelEnum instance : ParaLabelEnum.values()) {
      if (instance.getOrg().equals("court")) {
        if (instance.getLevel() == 2) {
          ParaLabelBean tmp = new ParaLabelBean(instance, instance.getLabel(), null, null);
          paralabels.get(instance.getSuperLabel()).add(tmp);
        }
      }
    }
  }

  public List<List<ParaLabelBean>> map2List() {
    List<List<ParaLabelBean>> list = new ArrayList<List<ParaLabelBean>>();
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      List<ParaLabelBean> tmpList = new ArrayList<ParaLabelBean>();
      tmpList.add(new ParaLabelBean(enumLabel, enumLabel.getLabel(), null, null));
      for (ParaLabelBean tmp : paralabels.get(enumLabel)) {
        tmpList.add(tmp);
      }
      list.add(tmpList);
    }
    return list;
  }

  public List<ParaLabelBean> getBeanByEnum(List<ParaLabelEnum> facts) {
    List<ParaLabelBean> list = new ArrayList<ParaLabelBean>();
    for (ParaLabelEnum key : paralabels.keySet()) {
      for (ParaLabelBean ele : paralabels.get(key)) {
        if (facts.contains(ele.enumLabel)) {
          list.add(ele);
        }
      }
    }
    return list;
  }
  //检察院
  public ParaLabelBean getProBeanByEnum(ParaLabelEnum fact) {
    for (ParaLabelBean ele : paralabels.get(fact)) {
      if (fact.equals(ele.enumLabel)) {
        return ele;
      }
    }
    return null;
  }

  /**
   * 根据标签名获得标签列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public List<ParaLabelBean> getByLabels(List<String> labels) {
    List<ParaLabelBean> list = new ArrayList<ParaLabelBean>();
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (labels.contains(bean.label)) {
          list.add(bean);
        }
      }
    }
    return list;
  }

  /**
   * 根据标签名获得标签列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public ParaLabelBean getByLabel(String label) {
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (label.equals(bean.label)) {
          return bean;
        }
      }
    }
    return null;
  }

  /**
   * 根据标签名获得标签内容
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public List<Map<Integer, String>> getContentByLabels(List<String> labels) {
    List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (labels.contains(bean.enumLabel.getLabel()) && bean.getContent() != null) {
          list.add(bean.getContent());
        }
      }
    }
    return list;
  }

  /**
   * 将根据标签名获得标签内容相连
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public StringBuilder getContentSumByLabels(List<String> labels) {
    StringBuilder builder = new StringBuilder();
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (labels.contains(bean.enumLabel.getLabel()) && bean.getContent() != null) {
          SortedSet<Integer> set = new TreeSet<Integer>();
          set.addAll(bean.getContent().keySet());
          for (int i : set) {
            builder.append(bean.getContent().get(i));
          }
        }
      }
    }
    return builder;
  }

  /**
   * 获取有效的标签列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public List<ParaLabelBean> getValueableParaLabel() {
    List<ParaLabelBean> list = new ArrayList<ParaLabelBean>();
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (bean.getContent() != null) {
          list.add(bean);
        }
      }
    }
    return list;
  }

  /**
   * 获取有效的标签列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public List<String> getValueableLabel() {
    List<String> list = new ArrayList<String>();
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (bean.getContent() != null) {
          list.add(bean.label);
        }
      }
    }
    return list;
  }

  /**
   * 获取某个自然段属于哪个标签
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public ParaLabelBean getParagraphLabel(int number) {
    for (ParaLabelEnum enumLabel : paralabels.keySet()) {
      for (ParaLabelBean bean : paralabels.get(enumLabel)) {
        if (bean.getContent() != null && bean.getContent().keySet().contains(number)) {
          return bean;
        }
      }
    }
    return null;
  }

}
