package com.ocp.rabbit.proxy.extractor.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;

/**
 * 初次犯罪日期
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class FirstCrimDateExtractor {

  private Context context;

  public FirstCrimDateExtractor(Context context) {
    this.context = context;
  }

  public void extract(List<String> paragraphList, String name2peopleObjKey, String firstCrimeKey) {
    @SuppressWarnings("unchecked")
    Map<String, People> name2peopleObjMap = (Map<String, People>) context.rabbitInfo
        .getExtractInfo().getOrDefault(name2peopleObjKey, new HashMap<>());
    String s = "";
    for (String paragraph : paragraphList) {
      s = s + paragraph;
    }
    // 过滤被害者生日日期
    String firstCrimeDate = null;
    s = DateHandler.birthdayFilter(s);
    NamedEntity[] nes = NamedEntityRecognizer.recognizeTime(s);
    if (nes.length > 0) {
      DateTime[] dateTimes = new DateTime[nes.length];
      for (int i = 0; i < nes.length; i++) {
        DateTime dt = DateHandler.makeDateTime((String) (nes[i].getInfo()));
        dateTimes[i] = dt;
      }
      if (DateHandler.min(dateTimes) != null) {
        firstCrimeDate = DateHandler.convertDateTimeFormat(DateHandler.min(dateTimes));
      }
    }
    if (null != firstCrimeDate) {
      for (Map.Entry<String, People> entry : name2peopleObjMap.entrySet()) {
        entry.getValue().getPeopleAttrMap().put(firstCrimeKey, firstCrimeDate);
      }
    }
  }
}
