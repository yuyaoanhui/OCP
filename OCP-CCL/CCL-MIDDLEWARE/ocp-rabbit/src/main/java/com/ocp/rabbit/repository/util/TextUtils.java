package com.ocp.rabbit.repository.util;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.entity.InfoPointKey;

public class TextUtils {
  public static boolean isEmpty(CharSequence str) {
    if (str == null || str.length() <= 0)
      return true;
    else
      return false;
  }

  public static <T> List<T> deduplicate(List<T> list) {
    List<T> tarList = new ArrayList<>();
    for (T str : list) {
      if (!tarList.contains(str)) {
        tarList.add(str);
      }
    }
    return tarList;
  }

  @SuppressWarnings("unchecked")
  public static <T> T[] deduplicateArray(T[] arrays) {
    List<T> tarList = new ArrayList<T>();
    for (T str : arrays) {
      if (!tarList.contains(str)) {
        tarList.add(str);
      }
    }
    return (T[]) tarList.toArray();
  }

  public static String getRightKeyByName(String name) {
    String[] keys = null;
    try {
      Class<?> clazz = Class.forName("com.ocp.rabbit.repository.entity.InfoPointKey");
      Field field = clazz.getField(name);
      keys = (String[]) (field.get(name));
    } catch (Exception e) {
    }

    if (keys == null || keys.length != 2) {
      return "";
    }
    return keys[InfoPointKey.mode];
  }

  public static List<String> getParagraphList(Context context, String[] tagStrList) {
    List<String> paragraphList = new ArrayList<>();
    List<Map<Integer, String>> mapList =
        context.docInfo.getParaLabels().getContentByLabels(Arrays.asList(tagStrList));
    if (!mapList.isEmpty()) {
      for (Map<Integer, String> map : mapList) {
        if (!map.isEmpty()) {
          for (int i : map.keySet()) {
            String paragraph = map.get(i);
            paragraphList.add(paragraph);
          }
        }
      }
    }
    return paragraphList;
  }

  public static StringBuilder getSbParagraph(Context context, String[] tagStrList) {
    StringBuilder sbParagraph = new StringBuilder("");
    List<Map<Integer, String>> mapList =
        context.docInfo.getParaLabels().getContentByLabels(Arrays.asList(tagStrList));
    for (Map<Integer, String> map : mapList) {
      for (int i : map.keySet()) {
        String paragraph = map.get(i);
        sbParagraph.append(paragraph);
      }
    }
    return sbParagraph;
  }
}
