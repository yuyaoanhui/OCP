package com.ocp.rabbit.repository.algorithm.structure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.util.CharTranfer;

public class FormatUtil {

  public static String format(String tmp) {
    String strTrim = tmp.replaceAll("[ |　]+", "");
    strTrim = strTrim.replaceAll("\t", "");
    strTrim = strTrim.replaceAll("(?<=人民检察院)。$", "");
    strTrim = moneyFormat(strTrim, "\\d[,，\\s\\+\\＋]+\\d", "[,\\uff0c\\s\\+\\＋]+", "");
    strTrim = moneyFormat(strTrim, "(万余元|余万元)", "(万余元|余万元)", "万元");
    strTrim = moneyFormat(strTrim, "(亿余元|余亿元)", "(亿余元|余亿元)", "亿元");
    strTrim = moneyFormat(strTrim, "(余人)", "(余人)", "人");
    strTrim = moneyFormat(strTrim, "(余元)", "(余元)", "元");
    strTrim = moneyFormat(strTrim, "(余克)", "(余克)", "克");
    strTrim = moneyFormat(strTrim, "(余小时)", "(余小时)", "小时");
    strTrim = moneyFormat(strTrim, "(多张)", "(多张)", "张");
    strTrim = moneyFormat(strTrim, "(多部)", "(多部)", "部");
    strTrim = moneyFormat(strTrim, "(多个)", "(多个)", "个");
    strTrim = moneyFormat(strTrim, "(余次)", "(余次)", "次");
    strTrim = moneyFormat(strTrim, "(多人)", "(多人)", "人");
    strTrim = CharTranfer.qj2bj(strTrim);
    return strTrim;
  }

  private static String moneyFormat(String sentence, String regex, String targetRegex,
      String result) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(sentence);
    while (matcher.find()) {
      String target = matcher.group();
      target = target.replaceAll(targetRegex, result);
      sentence = sentence.replaceAll(matcher.group(), target);
    }
    return sentence;
  }
}
