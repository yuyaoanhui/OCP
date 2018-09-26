package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;

/**
 * Created by chengyong on 2017/7/19. 该抽取器提供静态函数的功能： 1、用来抽取连在一起的多个时间信息点，比如：连在一起的提起公诉时间、立案时间、审理时间
 * 2、抽取两个时间的时间差
 */
public class SequenceTimeInfoExtractor {
  private final static String SPLITER = "#";

  public static Map<String, Object> extractTimes(String paragraph, Pattern[] pattern, String info) {
    Map<String, Object> rsltMap = new HashMap<String, Object>();
    String[] infos = info.split(SPLITER);
    if ((infos.length < 1) && (pattern.length != infos.length)) {
      return rsltMap;
    }
    NamedEntity[] dates = NamedEntityRecognizer.recognizeTime(paragraph);
    if (dates.length < 1) {
      return rsltMap;
    }
    ArrayList<NamedEntity[]> entitys = new ArrayList<>();
    ArrayList<Pattern> patterns = new ArrayList<>();
    for (int i = 0; i < infos.length; i++) {
      patterns.add(pattern[i]);
      entitys.add(NamedEntityRecognizer.recognizeEntityByRegex(paragraph, patterns.get(i)));
    }
    NamedEntity[] nesAllEntities = NamedEntityRecognizer.combineEntities(infos, entitys);
    if (nesAllEntities.length < 1) {
      return rsltMap;
    }
    Integer[] commas = NamedEntityRecognizer.recognizeComma(paragraph);
    List<NamedEntity[]> lnes =
        NamedEntityRecognizer.entityMatch(commas, dates, nesAllEntities, true);
    for (NamedEntity[] nes : lnes) {
      String type = nes[0].getType();
      if (nes.length >= 2) {
        String date = DateHandler.convertDateTimeFormat((String) (nes[1].getInfo()));
        if (null == date) {
          continue;
        }
        rsltMap.put(type, date);
      }
    }
    return rsltMap;
  }
}
