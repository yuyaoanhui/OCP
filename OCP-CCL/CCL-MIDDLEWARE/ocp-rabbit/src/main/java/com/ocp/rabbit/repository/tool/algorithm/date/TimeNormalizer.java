package com.ocp.rabbit.repository.tool.algorithm.date;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.repository.tool.ResourceReader;

/**
 * 支持获得推测后时间和推测前时间两种时间信息
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class TimeNormalizer implements Serializable {
  private static final Logger logger = LoggerFactory.getLogger(TimeNormalizer.class);
  private static final long serialVersionUID = 463541045644656392L;
  private String timeBase;
  private String oldTimeBase;
  private static Pattern patterns = null;
  private String target;
  private TimeUnit[] timeToken = new TimeUnit[0];

  public TimeNormalizer() {
    if (patterns == null) {
      try {
        patterns = readTxtExpToObj();
      } catch (Exception e) {
        logger.error("Read model error! TimeNormalizer() : ", e);
      }
    }
  }

  public TimeNormalizer(String raw) {
    if (patterns == null) {
      try {
        patterns = readTxtExpToObj();
      } catch (Exception e) {
        logger.error("Read model error! TimeNormalizer(String) : ", e);
      }
    }
    timeToken = parse(raw);
  }

  public TimeNormalizer(String raw, String timeBase) {
    if (patterns == null) {
      try {
        patterns = readTxtExpToObj();
      } catch (Exception e) {
        logger.error("Read model error! TimeNormalizer(String,String) : ", e);
      }
    }
    timeToken = parse(raw, timeBase);
  }

  /**
   * TimeNormalizer的构造方法，根据提供的待分析字符串和timeBase进行时间表达式提取 在构造方法中已完成对待分析字符串的表达式提取工作
   *
   * @param target 待分析字符串
   * @param timeBase 给定的timeBase
   * @return 返回值
   */
  private TimeUnit[] parse(String target, String timeBase) {
    this.target = target;
    this.timeBase = timeBase;
    this.oldTimeBase = timeBase;
    // 字符串预处理
    // preHandling();
    timeToken = TimeEx(this.target, timeBase);
    return timeToken;
  }

  private static final String TIME_BASE_DEFAULT = "2999-01-01-01-01-01";

  /**
   * 同上的TimeNormalizer的构造方法，timeBase取默认的系统当前时间
   *
   * @param target 待分析字符串
   * @return 时间单元数组
   */
  public TimeUnit[] parse(String target) {
    this.target = target;
    this.timeBase = TIME_BASE_DEFAULT;
    this.oldTimeBase = timeBase;
    // preHandling();//字符串预处理
    timeToken = TimeEx(this.target, timeBase);
    return timeToken;
  }

  //

  /**
   * timeBase的get方法
   *
   * @return 返回值
   */
  public String getTimeBase() {
    return timeBase;
  }

  /**
   * oldTimeBase的get方法
   *
   * @return 返回值
   */
  public String getOldTimeBase() {
    return oldTimeBase;
  }

  /**
   * timeBase的set方法
   *
   * @param s timeBase
   */
  public void setTimeBase(String s) {
    timeBase = s;
  }

  /**
   * 重置timeBase为oldTimeBase
   */
  public void resetTimeBase() {
    timeBase = oldTimeBase;
  }

  /**
   * 时间分析结果以TimeUnit组的形式出现，此方法为分析结果的get方法
   *
   * @return 返回值
   */
  public TimeUnit[] getTimeUnit() {
    return timeToken;
  }

  public String getTarget() {
    return target;
  }

  /**
   * 有基准时间输入的时间表达式识别
   * <p/>
   * 这是时间表达式识别的主方法， 通过已经构建的正则表达式对字符串进行识别，并按照预先定义的基准时间进行规范化 将所有别识别并进行规范化的时间表达式进行返回，
   * 时间表达式通过TimeUnit类进行定义
   * 
   * @return TimeUnit[] 时间表达式类型数组
   */
  private TimeUnit[] TimeEx(String tar, String timebase) {
    List<int[]> tempListIndex = new ArrayList<>();
    LinkedList<int[]> tempListCombinedIndex = new LinkedList<>();
    OffsetAdjustment oa = new OffsetAdjustment(tar);
    tar = oa.srcModified;
    Matcher match = patterns.matcher(tar);
    // 找到最小时间单元
    while (match.find()) {
      tempListIndex.add(new int[] {match.start(), match.end()});
    }
    // 合并连续的时间单元；
    for (int i = 0; i < tempListIndex.size(); i++) {
      // 如果是第一个；或者当前的开始和上一个的末尾不相等，则建立新的元素
      if (i == 0 || tempListIndex.get(i)[0] != tempListIndex.get(i - 1)[1]) {
        tempListCombinedIndex.addLast(tempListIndex.get(i));
      } else {// 否则最后一个元素更新
        tempListCombinedIndex.getLast()[1] = tempListIndex.get(i)[1];
      }
    }
    List<TimeUnit> Time_Result_list = new ArrayList<>();
    for (int[] k : tempListCombinedIndex) {
      int[] offset = new int[2];
      String Time_Expression_Orginal = oa.getOriginalPosition(new int[] {k[0], k[1] - 1}, offset);
      TimeUnit tu =
          new TimeUnit(tar.substring(k[0], k[1]), Time_Expression_Orginal, this, offset[0]);
      if (tu.isFalseDateFlag())
        continue;
      Time_Result_list.add(tu);
    }
    return Time_Result_list.toArray(new TimeUnit[Time_Result_list.size()]);
  }

  private Pattern readTxtExpToObj() {
    return Pattern.compile(ResourceReader.timeExp);
  }

  public static void writeModel(String file, Pattern p) throws Exception {

    ObjectOutputStream out = new ObjectOutputStream(
        new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file))));
    out.writeBytes(p.toString());
    out.close();
  }

  public Pattern getPatterns() {
    return patterns;
  }
}


