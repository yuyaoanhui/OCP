package com.ocp.rabbit.repository.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ocp.rabbit.repository.tool.algorithm.law.UrlLabel;

/**
 * 抽取结果信息类
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class RabbitInfo {
  // 存放抽取结果
  public Map<String, Object> extractInfo = new HashMap<String, Object>();

  private Map<String, Map<String, List<UrlLabel>>> urlLabels = new HashMap<>();

  public Map<String, Object> getExtractInfo() {
    return extractInfo;
  }

  public void setExtractInfo(Map<String, Object> extractInfo) {
    this.extractInfo = extractInfo;
  }

  public Map<String, Map<String, List<UrlLabel>>> getUrlLabels() {
    return urlLabels;
  }

  public void setUrlLabels(Map<String, Map<String, List<UrlLabel>>> urlLabels) {
    this.urlLabels = urlLabels;
  }

}
