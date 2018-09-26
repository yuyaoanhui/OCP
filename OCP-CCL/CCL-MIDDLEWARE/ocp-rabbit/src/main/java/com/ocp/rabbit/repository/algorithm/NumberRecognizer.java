package com.ocp.rabbit.repository.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.tool.algorithm.number.NumberHandler;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.Position;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 数字识别器（目前包含通用数字和时间数字）
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class NumberRecognizer {
  private List<String> units = new ArrayList<String>();// 数字单位集合
  private static final List<String> orderWords = new ArrayList<String>();// 序数词集合

  /**
   * 通用数字识别相关变量
   */
  // 非汉字非数字（可有可无）＋一串数字＋非汉字非数字,可以包含小数点,分割符暂时不考虑
  private static String number_regex =
      "([^０１２３４５６７８９⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]{0,2})"
          + "([点\\.０１２３４５６７８９⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]+)"
          + "([^０１２３４５６７８９\" +\n⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]{0,3})";
  // 识别规则
  private static final Pattern PATTERN_NUMBER = Pattern.compile(number_regex);

  /**
   * 时间数字识别相关变量
   */
  // 可以包含小数点，分割符暂时不考虑
  private static String number_time_regex =
      "([^０１２３４５６７８９⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]{0,2})"
          + "([\\.０１２３４５６７８９⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]+)"
          + "([^０１２３４５６７８９\" +\n⑴⑵⑶⑷⑸⑹⑺⑻⑼⑽⑾⑿⒀⒁⒂⒃⒄⒅⒆⒇一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]{0,3})";
  // 识别规则
  private static final Pattern PATTERN_NUMBER_FOR_TIME = Pattern.compile(number_time_regex);

  /**
   * 利率识别相关变量
   */
  // 百分比单位符号
  private static final String[] PCTG = {"%", "％", "‰"};
  // 百分比数字识别器
  private static final NumberRecognizer PCTG_NR = new NumberRecognizer(PCTG);
  // 百分比识别规则
  private static String percent_regex =
      "([佰百千仟万萬])分之([０１２３４５６７８９一二三四五六七八九十零〇○Ｏ０OoΟ0О0百千万两\\.点\\d壹贰叁肆伍陆柒捌玖拾佰仟萬亿]+)";
  private static final Pattern PATTERN_PCTG = Pattern.compile(percent_regex);

  static {
    orderWords.add("第");
  }

  public NumberRecognizer(Collection<String> units) {
    this.units.addAll(units);
  }

  public NumberRecognizer(String[] units) {
    for (String s : units) {
      this.units.add(s);
    }
  }

  public NumberRecognizer() {

  }

  /**
   * 获取时间数字,可选择是否包含单位<br>
   * 【注：匹配的时候，不应包括 "点"，否则会和 15点20之类的表述重合】
   * 
   * @author yu.yao
   * @param shortSents 要识别的短语列表
   * @param containUnit 是否包含单位
   * @return
   */
  public List<WrapNumberFormat> getNumbersForTime(String[] shortSents, boolean containUnit) {
    List<WrapNumberFormat> numbers = new ArrayList<WrapNumberFormat>();
    for (int i = 0; i < shortSents.length; i++) {
      String s = shortSents[i];
      Matcher matcher = PATTERN_NUMBER_FOR_TIME.matcher(s);
      boolean order = false;
      String number;
      String unit = "";
      while (matcher.find()) {
        order = containOrderWords(matcher.group(1));
        unit = getUnit(matcher.group(3));
        if ("".equals(unit) && containUnit) {
          continue;
        }
        number = matcher.group(2);
        Position position = new Position(number + unit, "number", 1, i, matcher.start(2));
        WrapNumberFormat wnf = new WrapNumberFormat(order, number, unit, position);
        try {
          Double convertedNumber = NumberHandler.convert2Double(number);
          wnf.setArabicNumber(convertedNumber);
          numbers.add(wnf);
        } catch (Exception e) {
          continue;
        }
      }
    }
    return numbers;
  }

  // 输入为一句话
  private List<WrapNumberFormat> getNumbersInt(String[] shortSents, boolean containUnit) {
    List<WrapNumberFormat> numbers = new ArrayList<>();
    for (int i = 0; i < shortSents.length; i++) {
      String s = shortSents[i];
      Matcher matcher = PATTERN_NUMBER.matcher(s);
      boolean order = false;
      String number;
      String unit = "";
      while (matcher.find()) {
        order = containOrderWords(matcher.group(1));
        // 是否含有单位
        unit = getUnit(matcher.group(3));
        if (unit == "" && containUnit) {
          continue;
        }
        // 得到数字
        number = matcher.group(2);
        // para, sentenceByComma, pos_sentenceByComma
        Position position = new Position(number + unit, "number", 1, i, matcher.start(2));
        WrapNumberFormat wnf = new WrapNumberFormat(order, number, unit, position);
        // convert to digit
        try {
          // 数字转换出错，进入下一个；
          Integer convertedNumber = NumberHandler.convert2Int(number);
          wnf.setPeopNumber(convertedNumber);
          numbers.add(wnf);
        } catch (Exception e) {
          continue;
        }
        // System.out.println(matcher.group());
      }
    }
    return numbers;
  }

  // 输入为一句话
  public List<WrapNumberFormat> getNumbers(String[] shortSents, boolean containUnit) {
    List<WrapNumberFormat> numbers = new ArrayList<>();
    for (int i = 0; i < shortSents.length; i++) {
      String s = shortSents[i];
      Matcher matcher = PATTERN_NUMBER.matcher(s);
      boolean order = false;
      String number;
      String unit = "";
      while (matcher.find()) {
        order = containOrderWords(matcher.group(1));
        // 是否含有单位
        unit = getUnit(matcher.group(3));
        if (unit == "" && containUnit) {
          continue;
        }
        // 得到数字
        number = matcher.group(2);
        // para, sentenceByComma, pos_sentenceByComma
        Position position = new Position(number + unit, "number", 1, i, matcher.start(2));
        WrapNumberFormat wnf = new WrapNumberFormat(order, number, unit, position);
        // convert to digit
        try {
          // 数字转换出错，进入下一个；
          Double convertedNumber = NumberHandler.convert2Double(number);
          wnf.setArabicNumber(convertedNumber);
          numbers.add(wnf);
        } catch (Exception e) {
          continue;
        }
        // System.out.println(matcher.group());
      }
    }
    return numbers;
  }

  public List<WrapNumberFormat> getPercentage(String str) {
    String[] shortSents = DocumentUtils.splitOneSentenceByComma(str);
    return getPercentage(shortSents);
  }

  public String replaceChineseCharacters(String str) {
    List<WrapNumberFormat> lwnf = getNumbers(new String[] {str}, true);
    String tmp = str;
    try {
      for (WrapNumberFormat wnf : lwnf) {
        String word = wnf.getNumber();
        if (TextUtils.isEmpty(wnf.getUnit()) == false)
          tmp = tmp.replaceFirst(word + wnf.getUnit(),
              String.valueOf((int) wnf.getArabicNumber()) + wnf.getUnit());
        else
          tmp = tmp.replaceFirst(word, String.valueOf((int) wnf.getArabicNumber()));
      }
    } catch (Exception e) {
      tmp = str;
    }
    return tmp;
  }

  public static List<WrapNumberFormat> getPercentage(String[] shortSents) {
    // 得到常规写法的百分比，千分比
    // 3%, 4.5％
    List<WrapNumberFormat> lwnf = PCTG_NR.getNumbers(shortSents, true);
    // 得到中文写法的百分比，千分比，万分比，比如：百分之三
    for (int i = 0; i < shortSents.length; i++) {
      String s = shortSents[i];
      Matcher matcher = PATTERN_PCTG.matcher(s);
      String number;
      String unit = "";
      while (matcher.find()) {
        // 单位
        unit = matcher.group(1);
        // 得到数字
        number = matcher.group(2);
        // para, sentenceByComma, pos_sentenceByComma
        Position position = new Position(matcher.group(), "number", 1, i, matcher.start());
        WrapNumberFormat wnf = new WrapNumberFormat(false, number, unit, position);
        // convert to digit
        try {
          // 数字转换出错，进入下一个；
          Double convertedNumber = NumberHandler.convert2Double(number);
          wnf.setArabicNumber(convertedNumber);
          lwnf.add(wnf);
        } catch (Exception e) {
          continue;
        }
      }
    }
    // 根据单位，转换数字。
    for (WrapNumberFormat wnf : lwnf) {
      double convertedNumber = wnf.getArabicNumber();
      String unit = wnf.getUnit();
      switch (unit) {
        case "百":
        case "佰":
        case "%":
        case "％": {
          convertedNumber /= 100;
          break;
        }
        case "仟":
        case "千":
        case "‰": {
          convertedNumber /= 1000;
          break;
        }
        case "萬":
        case "万": {
          convertedNumber /= 10000;
          break;
        }
        default: {
          // should never reach here!!!
          convertedNumber /= 100;
        }
      }
      wnf.setArabicNumber(convertedNumber);
    }
    return lwnf;
  }

  public List<WrapNumberFormat> getNumbersInt(String str, boolean containUnit) {
    String[] shortSents = DocumentUtils.splitOneSentenceByComma(str);
    return getNumbersInt(shortSents, containUnit);
  }

  public List<WrapNumberFormat> getNumbers(String str, boolean containUnit) {
    String[] shortSents = DocumentUtils.splitOneSentenceByComma(str);
    return getNumbers(shortSents, containUnit);
  }

  /**
   * 是否含有序数词语
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static boolean containOrderWords(String str) {
    for (String orderWord : orderWords) {
      if (str.contains(orderWord)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取单位
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private String getUnit(String str) {
    for (String s : units) {
      if (str.indexOf(s, 0) == 0) {
        return s;
      }
    }
    return "";
  }
}
