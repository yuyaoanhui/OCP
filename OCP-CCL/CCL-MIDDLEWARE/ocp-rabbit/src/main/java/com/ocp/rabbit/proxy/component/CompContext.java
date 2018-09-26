package com.ocp.rabbit.proxy.component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.middleware.datacache.InfoPointCache;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.repository.bean.PointVarBean;
import com.ocp.rabbit.repository.entity.DocumentInfo;

/**
 * 构件通用上下文</br>
 * 该类存储公开数据和方法，更多为了体现层级继承关系
 * 
 * @author yu.yao 2018年8月2日
 *
 */
public abstract class CompContext {
  /**
   * 所有自然段内容
   */
  private List<String> allUnits;
  /**
   * 文书原始全部内容
   */
  public String document;

  /**
   * 待处理的内容(可能是一句话或一段话或全文等)
   */
  private String content;

  /**
   * 要处理的信息点列表,根据文书类型划分
   */
  public Map<String, Map<InfoPoint, PointVarBean>> points = new HashMap<>();// <docType,Map>

  /**
   * 要提前处理的信息点列表,根据文书类型和案由大类划分
   */
  public Map<String, Map<String, LinkedHashMap<InfoPoint, PointVarBean>>> prePoints =
      InfoPointCache.prePoints;

  public List<Pattern> regex;

  public Pattern reverseRegex;

  public String capture;

  public InfoPoint point;

  public boolean isPatterned = false;
  /**
   * 文书信息
   */
  public final DocumentInfo docInfo = new DocumentInfo();

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public List<String> getAllUnits() {
    return allUnits;
  }

  public void setAllUnits(List<String> allUnits) {
    this.allUnits = allUnits;
  }

}
