package com.ocp.rabbit.repository.tool.algorithm.date;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.ocp.rabbit.repository.entity.NamedEntity;

/**
 * 日期处理类
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class DateHandler {

  static DateTimeFormatter dtf1 = DateTimeFormat.forPattern("yyyy年MM月dd日");
  static DateTimeFormatter dtf2 = DateTimeFormat.forPattern("yyyy-MM-dd");
  static DateTimeFormatter isoDateTimeFormat = ISODateTimeFormat.dateTimeNoMillis();
  // A wrapper of the Date normalization algorithm
  private TimeNormalizer normalizer;
  private TimeUnit[] timeUnits;

  // 初始化DateHandler
  public DateHandler(String raw) {
    normalizer = new TimeNormalizer(raw);
    timeUnits = normalizer.getTimeUnit();
  }

  public DateHandler(String raw, String timeBase) {

    if (null == timeBase) {
      normalizer = new TimeNormalizer(raw);
    } else {
      normalizer = new TimeNormalizer(raw, timeBase);
    }
    timeUnits = normalizer.getTimeUnit();
  }

  public TimeUnit[] parse(String date) {
    normalizer.parse(date);
    return normalizer.getTimeUnit();
  }

  public TimeUnit[] getTimeUnits() {
    return timeUnits;
  }

  private static final Pattern patternTimeUnstd = Pattern.compile("[\\d]{2}年[\\d]{1,2}月");
  private static final Pattern patternTimeLegal = Pattern.compile("[年月日天]");

  public NamedEntity[] getTimeUnitsNE() {
    TimeUnit[] tus = timeUnits;
    List<NamedEntity> lne = new ArrayList<>();
    for (int i = 0; i < tus.length; i++) {
      if (tus[i].time_full[0] > 0) {
        // 这里 对时间进行过滤和转换
        if (!(patternTimeLegal.matcher(tus[i].Time_Expression_Orginal).find())) {
          continue;
        }
        NamedEntity ne = null;
        if (tus[i].time_full[0] > 1900 && tus[i].time_full[0] < 2999)
          ne = new NamedEntity(tus[i].Time_Expression_Orginal, tus[i].offset, tus[i].toString());
        else if (patternTimeUnstd.matcher(tus[i].Time_Expression_Orginal).find()) {
          if (tus[i].time_full[0] <= 10 || tus[i].time_full[0] < 20) {
            tus[i].time_full[0] = 2000 + tus[i].time_full[0];
            ne = new NamedEntity(tus[i].Time_Expression_Orginal, tus[i].offset, tus[i].toString());
          } else if (tus[i].time_full[0] >= 50) {
            tus[i].time_full[0] = 1900 + tus[i].time_full[0];
            ne = new NamedEntity(tus[i].Time_Expression_Orginal, tus[i].offset, tus[i].toString());
          }
        }
        if (null != ne)
          lne.add(ne);
      }
    }
    return lne.toArray(new NamedEntity[lne.size()]);
  }


  public String getTimeBase() {
    return normalizer == null ? null : normalizer.getTimeBase();
  }

  public static int getDayDiff(DateTime startDate, DateTime endDate) throws Exception {
    return Days.daysBetween(startDate, endDate).getDays();
  }

  public static int getMonthDiff(DateTime startDate, DateTime endDate) throws Exception {
    return Months.monthsBetween(startDate, endDate).getMonths();
  }

  public static int getYearDiff(DateTime startDate, DateTime endDate) throws Exception {
    return Years.yearsBetween(startDate, endDate).getYears();
  }

  private static final DateTime dtBase =
      new DateTime(2000, 1, 1, 0, 0, 0, ISOChronology.getInstanceUTC());

  public static DateTime max(DateTime... dts) {
    DateTime tmpMax = null;
    int intTmpMax = -10000000;
    for (DateTime dt : dts) {
      try {
        int tmp = getDayDiff(dtBase, dt);
        if (tmp > intTmpMax) {
          tmpMax = dt;
          intTmpMax = tmp;
        }
      } catch (Exception e) {
      }
    }
    return tmpMax;
  }

  public static DateTime min(DateTime... dts) {
    DateTime tmpMin = null;
    int intTmpMin = 10000000;
    for (DateTime dt : dts) {
      try {
        int tmp = getDayDiff(dtBase, dt);
        if (tmp < intTmpMin) {
          tmpMin = dt;
          intTmpMin = tmp;
        }
        if (dt == null) {
          continue;
        }
      } catch (Exception e) {
      }
    }
    return tmpMin;
  }

  public static String min(String... dates) {
    String tmpMin = null;
    int intTmpMin = 10000000;
    for (String date : dates) {
      try {
        DateTime dt = makeDateTime(date);
        int tmp = getDayDiff(dtBase, dt);
        if (tmp < intTmpMin) {
          tmpMin = date;
          intTmpMin = tmp;
        }
      } catch (Exception e) {
      }
    }
    return tmpMin;
  }

  public static String max(String... dates) {
    String tmpMax = null;
    int intTmpMax = -10000000;
    for (String date : dates) {
      try {
        DateTime dt = makeDateTime(date);
        int tmp = getDayDiff(dtBase, dt);
        if (tmp > intTmpMax) {
          tmpMax = date;
          intTmpMax = tmp;
        }
        if (dt == null) {
          continue;
        }
      } catch (Exception e) {
      }
    }
    return tmpMax;
  }

  // TODO 转换成solr格式
  public static String convertDateTimeFormat(String s) {

    DateTime dt = makeDateTime(s);
    if (dt != null) {
      return dt.toString(dtf2);
    }
    return null;
  }

  // 计算跟s + i日的时间
  public static String convertDateTimeFormat(String s, int i) {
    DateTime dt = makeDateTime(s, i);
    if (dt != null) {
      return dt.toString(dtf2);
    }
    return null;
  }

  public static String convertDateTimeFormat(DateTime dt) {

    if (dt != null) {
      return dt.toString(dtf2);
    }
    return null;
  }

  public static String convertISODateTimeFormat(String s) {

    DateTime dt = makeDateTimeMinut(s);
    if (dt != null) {
      return dt.toString(isoDateTimeFormat);
    }
    return null;
  }

  public static String convertISODateTimeFormat(DateTime dt) {

    if (dt != null) {
      return dt.toString(isoDateTimeFormat);
    }
    return null;
  }

  public static DateTime makeDateTime(String s) {
    if (s == null)
      return null;
    String[] strs = s.split("[年月日-]");
    if (strs.length > 0) {
      if (Integer.valueOf(strs[0]) < 1900 || Integer.valueOf(strs[0]) > 2025) {
        return null;
      }
    }
    if (strs.length == 1) {
      return new DateTime(Integer.valueOf(strs[0]), 1, 1, 0, 0, ISOChronology.getInstanceUTC());
    } else if (strs.length == 2) {
      return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]), 1, 0, 0,
          ISOChronology.getInstanceUTC());
    } else if (strs.length >= 3) {
      return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
          Integer.valueOf(strs[2]), 0, 0, ISOChronology.getInstanceUTC());
    }
    return null;
  }

  // 计算跟s + i日的时间
  public static DateTime makeDateTime(String s, int i) {
    if (s == null)
      return null;
    String[] strs = s.split("[年月日-]");
    try {
      if (strs.length > 0) {
        if (Integer.valueOf(strs[0]) < 1900 || Integer.valueOf(strs[0]) > 2025) {
          return null;
        }
      }

      if (strs.length == 1) {
        return new DateTime(Integer.valueOf(strs[0]), 1, 1, 0, 0, ISOChronology.getInstanceUTC());
      } else if (strs.length == 2) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]), 1, 0, 0,
            ISOChronology.getInstanceUTC());
      } else if (strs.length >= 3) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
            Integer.valueOf(strs[2]) + i, 0, 0, ISOChronology.getInstanceUTC());
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }

  public static DateTime makeDateTimeMinut(String s) {
    if (s == null)
      return null;
    String[] strs = s.split("[年月日时分秒-]");
    try {
      if (strs.length == 1) {
        return new DateTime(Integer.valueOf(strs[0]), 1, 1, 0, 0, 0,
            ISOChronology.getInstanceUTC());
      } else if (strs.length == 2) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]), 1, 0, 0, 0,
            ISOChronology.getInstanceUTC());
      } else if (strs.length == 3) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
            Integer.valueOf(strs[2]), 0, 0, 0, ISOChronology.getInstanceUTC());
      } else if (strs.length == 4) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
            Integer.valueOf(strs[2]), Integer.valueOf(strs[3]), 0, 0,
            ISOChronology.getInstanceUTC());
      } else if (strs.length == 5) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
            Integer.valueOf(strs[2]), Integer.valueOf(strs[3]), Integer.valueOf(strs[4]), 0,
            ISOChronology.getInstanceUTC());
      } else if (strs.length == 6) {
        return new DateTime(Integer.valueOf(strs[0]), Integer.valueOf(strs[1]),
            Integer.valueOf(strs[2]), Integer.valueOf(strs[3]), Integer.valueOf(strs[4]),
            Integer.valueOf(strs[5]), ISOChronology.getInstanceUTC());
      }
      return null;
    } catch (Exception e) {
      return null;
    }
  }


  public static String[] convertNamedEntity2String(NamedEntity[] nes) {
    String[] dates = new String[nes.length];
    for (int i = 0; i < dates.length; i++) {
      dates[i] = (String) nes[i].getInfo();
    }
    return dates;
  }

  /**
   * 过滤掉生日时间list
   */
  public static String birthdayFilter(String s) {
    String regex =
        "(^(\\d{4}|\\\\d{2})([\\-\\/\\.])\\d{1,2}\\3\\\\d{1,2}出?生$)|(^\\d{4}年\\d{1,2}月\\d{1,2}日出?生$)|(^生于(\\d{4}|\\d{2})([\\-\\/\\.])\\d{1,2}\\3\\d{1,2}$)|(^生于\\d{4}年\\d{1,2}月\\d{1,2}日$)";
    Set<String> birthdayList = new HashSet<String>();
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(s);
    String paraBirdayFilted = s;
    if (m.find()) {
      birthdayList.addAll(getTimeList(regex, paraBirdayFilted));

    }
    return paraBirdayFilted;
  }

  /**
   * 提取生日时间list子程序
   */
  private static Set<String> getTimeList(String regex, String paraBirdayFilted) {

    Set<String> resultSet = new HashSet<String>();
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(paraBirdayFilted);
    if (m.find()) {

      resultSet.add(m.group());
      if (paraBirdayFilted.split(m.group()).length > 1) {
        paraBirdayFilted =
            paraBirdayFilted.split(m.group())[0] + paraBirdayFilted.split(m.group())[1];
        resultSet.addAll(getTimeList(regex, paraBirdayFilted));

      }

    }
    return resultSet;
  }


  public static DateTime[] convertNamedEntity2DateTime(NamedEntity[] nes, DateTime minDt,
      DateTime maxDt) {
    List<DateTime> ldts = new ArrayList<>();
    for (int i = 0; i < nes.length; i++) {
      DateTime dt = makeDateTime((String) nes[i].getInfo());
      if (null != dt) {
        if (dt.isAfter(minDt) && dt.isBefore(maxDt)) {
          ldts.add(dt);
        }
      }
    }
    DateTime[] dts = ldts.toArray(new DateTime[ldts.size()]);
    return dts;
  }

  public static DateTime[] convertNamedEntity2dateTime(NamedEntity[] nes) {
    List<DateTime> ldts = new ArrayList<>();
    for (int i = 0; i < nes.length; i++) {
      DateTime dt = makeDateTime((String) nes[i].getInfo());
      if (null != dt) {
        ldts.add(dt);
      }
    }
    DateTime[] dts = ldts.toArray(new DateTime[ldts.size()]);
    return dts;
  }

}
