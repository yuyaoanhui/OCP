package com.futuredata.judicature.sdk.utils;

/**
 * StringUtils : 字符串工具类
 */
public class StringUtils {

  /**
   * 返回去除首尾空格的字符串
   * 
   * @param str
   * @return
   */
  public static String trim2Empty(String str) {
    if (null == str || str.isEmpty()) {
      return "";
    }
    if (str.equals(" ")) {
      return str;
    }
    return str.trim();
  }

  /**
   * 返回去除尾部多余的逗号的字符串
   * 
   * @param str
   * @return
   */
  public static String trimComma(String str) {
    if (null == str || str.isEmpty()) {
      return "";
    }
    if (str.endsWith(",")) {
      return str.substring(0, str.length() - 1);
    }
    return str;
  }

  /**
   * 
   * 返回去除尾部多余的逗号的字符串
   * 
   * @param sb
   * @return
   */
  public static String trimComma(StringBuilder sb) {
    return trimComma(sb.toString());
  }

  /**
   * 右侧补齐0
   * 
   * @param str
   * @param len
   * @return
   */
  public static String padZeroLeft(String str, int len) {
    if (str == null) {
      str = "";
    }

    int strLen = str.length();
    if (strLen >= len) {
      return str;
    }

    StringBuffer pad = new StringBuffer();
    int diffLen = len - strLen;
    for (int i = 0; i < diffLen; i++) {
      pad.append("0");
    }

    return str.concat(pad.toString());

  }

  public static String padZeroRight(String str, int len) {
    if (str == null) {
      str = "";
    }

    int strLen = str.length();
    if (strLen >= len) {
      return str;
    }

    StringBuffer pad = new StringBuffer();
    int diffLen = len - strLen;
    for (int i = 0; i < diffLen; i++) {
      pad.append("0");
    }

    return pad.append(str).toString();

  }

  /**
   * 
   * 判断字符串是否为空
   * 
   * @param str
   * @return
   */
  public static boolean isEmpty(Object str) {
    return (null == str || "".equals(str));
  }

}
