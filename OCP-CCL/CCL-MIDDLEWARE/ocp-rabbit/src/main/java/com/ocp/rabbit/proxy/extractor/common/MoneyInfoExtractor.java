package com.ocp.rabbit.proxy.extractor.common;

import java.util.List;

import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;

/**
 * 金额、利率
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class MoneyInfoExtractor {
  private static NumberRecognizer nr =
      new NumberRecognizer(new String[] {"元", "圆", "mg", "ｍｇ", "毫克", "㎎", "MG"});

  public static Double extractMoney(String money) {
    Double rsltMoney;

    List<WrapNumberFormat> numbers = nr.getNumbers(new String[] {money}, true);
    if ((numbers == null) || numbers.size() == 0) {
      return null;
    }
    rsltMoney = numbers.get(0).getArabicNumber();

    return rsltMoney;
  }

  public static Double extractPercentage(String sent, String model) {
    Double rsltPercentage;

    List<WrapNumberFormat> percentages = nr.getPercentage(sent);
    if ((percentages == null) || percentages.size() == 0) {
      return null;
    }
    // 这里如果匹配到了多个数字也只能取出第一个作为结果
    double number = percentages.get(0).getArabicNumber();
    // 下面是判断抽取的是年利率、月利率、日利率，不够严谨，可优化...
    if ((sent.contains("基准利率")) || (sent.contains("同期利率"))) {
      rsltPercentage = number;
      return rsltPercentage;
    }
    if ("月".equals(model)) {
      if ((sent.contains("每日")) || (sent.contains("每天")) || (sent.contains("日利率"))
          || (sent.contains("天利率"))) {
        number = number * 30;
      } else if ((sent.contains("每年")) || (sent.contains("年利率"))) {
        number = number / 12;
      }
    } else if ("日".equals(model)) {
      if ((sent.contains("每月")) || (sent.contains("月利率"))) {
        number = number / 30;
      } else if ((sent.contains("每年")) || (sent.contains("年利率"))) {
        number = number / 365;
      }
    } else {
      if ((sent.contains("每月")) || (sent.contains("月利率"))) {
        number = number * 12;
      }
      if ((sent.contains("每日")) || (sent.contains("每天")) || (sent.contains("日利率"))
          || (sent.contains("天利率"))) {
        number = number * 365;
      }
    }
    rsltPercentage = number;

    return rsltPercentage;
  }

  public static Double extractPercentage(String sent) {
    Double rsltPercentage;

    List<WrapNumberFormat> percentages = nr.getPercentage(sent);
    if ((percentages == null) || percentages.size() == 0) {
      return null;
    }
    double number = percentages.get(0).getArabicNumber();
    // 下面是判断抽取的是年利率、月利率、日利率，不够严谨，可优化...
    if (sent.contains("月利率")) {
      number = number * 12;
    } else if ((sent.contains("日利率")) || (sent.contains("天利率"))) {
      number = number * 365;
    }
    rsltPercentage = number;

    return rsltPercentage;
  }

}
