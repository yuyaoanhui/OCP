package com.ocp.rabbit.proxy.extractor.common;

import java.math.BigDecimal;
import java.util.List;

import org.joda.time.DateTime;

import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;

/**
 * Created by chengyong on 2017/7/27.
 */
/**
 * 时间日期工具类
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class TimeInfoExtractor {
  private static NumberRecognizer nr =
      new NumberRecognizer(new String[] {"月", "个月", "年", "日", "天", "小时", "周", "星期"});

  // 抽取日期
  public static String extractDate(String timeStr) {
    NamedEntity[] nes = (new DateHandler(timeStr)).getTimeUnitsNE();
    if (nes.length != 1) {
      return null;
    }
    return DateHandler.convertDateTimeFormat((String) (nes[0].getInfo()));
  }

  // 抽取时间
  public static Double extractTime(String timeStr, String newUnit) {
    NamedEntity[] dates = NamedEntityRecognizer.recognizeTime(timeStr);
    if (dates.length == 2) { // 起始和截止时间格式
      return calcTimeDiff((String) (dates[0].getInfo()), (String) (dates[1].getInfo()), newUnit);
    }

    Double rsltTime = null;
    List<WrapNumberFormat> lwnf = nr.getNumbers(new String[] {timeStr}, true);
    if (lwnf.size() != 1) {
      return null;
    }
    String curUnit = lwnf.get(0).getUnit();
    double curNumber = lwnf.get(0).getArabicNumber();
    if (curUnit.contains("年")) {
      if (newUnit.equals("年")) {
        rsltTime = curNumber;
      } else if (newUnit.equals("月")) {
        rsltTime = curNumber * 12;
      } else if (newUnit.equals("日")) {
        rsltTime = curNumber * 365;
      }
    } else if (curUnit.contains("月")) {
      if (newUnit.equals("年")) {
        rsltTime = curNumber / 12;
      } else if (newUnit.equals("月")) {
        rsltTime = curNumber;
      } else if (newUnit.equals("日")) {
        rsltTime = curNumber * 30;
      }
    } else if (curUnit.contains("日") || curUnit.contains("天")) {
      if (newUnit.equals("年")) {
        rsltTime = curNumber / 365;
      } else if (newUnit.equals("月")) {
        rsltTime = curNumber / 30;
      } else if (newUnit.equals("日")) {
        rsltTime = curNumber;
      }
    } else if (curUnit.contains("小时")) {
      if (newUnit.equals("小时")) {
        rsltTime = curNumber;
      }
    } else if (curUnit.contains("周") || curUnit.contains("星期")) {
      if (newUnit.equals("年")) {
        rsltTime = (curNumber * 7) / 365;
      } else if (newUnit.equals("月")) {
        rsltTime = (curNumber * 7) / 30;
      } else if (newUnit.equals("日")) {
        rsltTime = curNumber * 7;
      }
    }

    return rsltTime;
  }

  public static Double calcTimeDiff(String timeStr1, String timeStr2, String newUnit) {
    DateTime d1 = DateHandler.makeDateTime(timeStr1);
    DateTime d2 = DateHandler.makeDateTime(timeStr2);
    double diff = 0;
    try {
      if (newUnit.equals("年")) {
        // diff = DateHandler.getYearDiff(d1, d2);
        diff = DateHandler.getDayDiff(d1, d2);
        diff = diff / 365;
        BigDecimal b = new BigDecimal(diff);
        diff = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
      } else if (newUnit.equals("月")) {
        diff = DateHandler.getMonthDiff(d1, d2);
      } else if (newUnit.equals("日")) {
        diff = DateHandler.getDayDiff(d1, d2);
      }
      if (diff < 0) {
        diff = diff * (-1);
      }
    } catch (Exception e) {
    }

    return Double.valueOf(diff);
  }

}
