package com.ocp.rabbit.repository.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.tool.algorithm.law.UrlLabel;

/**
 * 抽取信息点记录类
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class ExtractPositionRecorder {

  private Context context;

  public ExtractPositionRecorder(Context context) {
    this.context = context;
  }

  /**
   * 功能：记录信息点匹配到的句子在对应section部分的起止位置
   * 
   * @param infoPoint 信息点名称
   * @param sent 该信息点匹配到的位置
   * @param plInfo PaladinInfo
   */
  public void recordInfoPointMatchSentPos(String infoPoint, String sent) {
    Map<String, List<int[]>> sentPosMap =
        getSentPosInSectionParagraph(context.rabbitInfo.getExtractInfo(), sent);
    Map<String, List<UrlLabel>> posLabels = new HashMap<>();
    for (String sectionkey : sentPosMap.keySet()) {
      List<int[]> posList = sentPosMap.get(sectionkey);
      List<UrlLabel> lawLabels = new ArrayList<>();
      for (int[] e : posList) {
        UrlLabel urlLabel = new UrlLabel(UrlLabel.LABEL_TYPE_EM, "<em>", "</em>", e[0], e[1]);
        if (posLabels.containsKey(sectionkey)) {
          lawLabels = posLabels.get(sectionkey);
        }
        lawLabels.add(urlLabel);
        posLabels.put(sectionkey, lawLabels);
      }
    }
    if (context.rabbitInfo.getUrlLabels().containsKey(infoPoint)) {
      posLabels.putAll(context.rabbitInfo.getUrlLabels().get(infoPoint));
    }
    context.rabbitInfo.getUrlLabels().put(infoPoint, posLabels);
  }

  public static Map<String, List<int[]>> getSentPosInSectionParagraph(Map<String, Object> map,
      String sent) {
    Map<String, List<int[]>> sentPosMap = new HashMap<>();
    for (String key : map.keySet()) {
      if (key.contains("section_") && (!key.contains("2"))) {
        List<int[]> posList = new ArrayList<>();
        String pargraphStr = (String) (map.get(key));
        if (pargraphStr.contains(sent)) {
          int start = pargraphStr.indexOf(sent);
          int end = start + sent.length();
          if (end > pargraphStr.length())
            continue;
          int[] pos = {start, end};
          posList.add(pos);
        }
        if (!posList.isEmpty()) {
          sentPosMap.put(key, posList);
        }
      }
    }
    return sentPosMap;
  }

}
