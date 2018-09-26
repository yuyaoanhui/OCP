package com.ocp.rabbit.repository.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.util.DocumentUtils;

public class AyIdentify {
  private static Pattern PATT_CASE_SUMMAY = Pattern.compile("一案|审理终结|提起公诉|以[^\\.。;；:：]*?指控");

  /**
   * 根据文书内容判断案由和案由大类
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static List<String> getAnyouByTextContent(String textContent) {
    List<String> list = new ArrayList<>();
    String[] paragraphList = textContent.split("\n");
    Matcher matcher = null;
    for (String paragraph : paragraphList) {
      matcher = PATT_CASE_SUMMAY.matcher(paragraph);
      if (matcher.find()) {
        String[] sentList = DocumentUtils.splitOneParagraphByPeriod(paragraph);
        for (String sent : sentList) {
          Map<String, List<String>> anyouDictionary = ResourceReader.readSource();
          for (String word : anyouDictionary.get("刑事案由")) {
            if (sent.contains(word)) {
              list.add("criminal");
              list.add(word);
              return list;
            }
          }
          for (String word : anyouDictionary.get("民事案由")) {
            if (sent.contains(word)) {
              list.add("civil");
              list.add(word);
              return list;
            }
          }
          for (String word : anyouDictionary.get("行政案由")) {
            if (sent.contains(word)) {
              list.add("civil");
              list.add(word);
              return list;
            }
          }
          for (String word : anyouDictionary.get("赔偿案由")) {
            if (sent.contains("赔偿") && sent.contains(word)) {
              list.add("civil");
              list.add(word);
              return list;
            }
          }
          for (String word : anyouDictionary.get("执行案由")) {
            if (sent.contains("执行") && sent.contains(word)) {
              list.add("civil");
              list.add(word);
              return list;
            }
          }
        }
      }
    }
    return list;
  }
}
