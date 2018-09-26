package com.ocp.rabbit.repository.tool.algorithm.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Created by ocp on 16/6/29.
 */

/**
 * 大写数字转化模块是将汉字表示的数字转化为阿拉伯数字的功能模块，具体说明如下： 1.该模块服务于时间表达式提取的预处理阶段，但在功能上完全独立于时间表达式提取，可支持复用。
 * 2.该模块目前支持的正确转化范围是0-99999999的整形数。 3.该模块可将字符串中所有大写数字无差别全部转化，如：
 * "这里有一千两百个人，六百零五个来自中国"可以转化为"这里有1200个人，605个来自中国"。 4.该模块添加支持部分不规则表达的大写数字转化，如： 两万零六百五可转化为20650
 * 两百一十四和两百十四都可以转化为214 一六零加一五八可以转化为160加158
 */
public class TimeUnit {
  /**
   * 新版本中将根据上下文相关信息动态获得timeBase,故取消参数Time_Initial 及相关构造方法，添加参数normalizer。
   */
  private String Time_Expression = null;
  public String Time_Expression_Orginal = null;
  public int[] time_full;
  public int offset;

  private boolean falseDateFlag = false;
  private TimeNormalizer normalizer = null;
  private TimePoint _tp = new TimePoint();
  private TimePoint _tp_origin = new TimePoint();

  /**
   * 时间表达式单元规范化的内部类
   * <p/>
   * 时间表达式单元规范化对应的内部类, 对应时间表达式规范化的每个字段， 六个字段分别是：年-月-日-时-分-秒， 每个字段初始化为-1
   */
  public class TimePoint {
    int[] tunit = {-1, -1, -1, -1, -1, -1};
  }

  public String getTimeBase() {
    return normalizer.getTimeBase();
  }

  /**
   * 时间表达式单元构造方法 该方法作为时间表达式单元的入口，将时间表达式字符串传入
   *
   * @param exp_time 时间表达式字符串
   */

  public TimeUnit(String exp_time, TimeNormalizer n) {
    Time_Expression = exp_time;
    normalizer = n;
    Time_Normalization();
  }

  public TimeUnit(String exp_time, TimeNormalizer n, int offset) {
    Time_Expression = exp_time;
    normalizer = n;
    checkFalseDate();
    // 如果是假的时间，则退出
    // 不会修改上下文时间
    if (falseDateFlag)
      return;
    Time_Normalization();
    this.offset = offset;
  }

  private static final Pattern pattern =
      Pattern.compile("(?<![Oo一二三四五六七八九十\\d])[一二三四五六七八九十\\d](十[一二三四五六七八九\\d]?)?年");

  private void checkFalseDate() {
    /*
     * 形如：六年，八十一年，九十八年，七十年，7年，则认为是时间长度 不能匹配形如： 十二年，12年，95年，78年这种情况（这种情况我们认为是时间的简写，比如78年对应为1978年）
     */
    if (pattern.matcher(Time_Expression_Orginal).find())
      falseDateFlag = true;
  }

  public TimeUnit(String exp_time, String exp_time_original, TimeNormalizer n, int offset) {
    Time_Expression = exp_time;
    Time_Expression_Orginal = exp_time_original;
    normalizer = n;
    checkFalseDate();
    if (falseDateFlag)
      return;
    Time_Normalization();
    this.offset = offset;
  }

  /**
   * 农历->公历-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的年字段
   */
  private void norm_setDate() {
    String rule = "正月|腊月";
    final Pattern pattern = Pattern.compile(rule);
    Matcher match = pattern.matcher(Time_Expression);
    boolean flag = false;
    if (match.find()) {
      if (match.group().equals("正月"))
        _tp.tunit[1] = 1;
      else
        _tp.tunit[1] = 12;
      flag = true;
    }

    if (_tp.tunit[0] < 0 && _tp.tunit[1] < 0 && _tp.tunit[2] < 0) {
      return;
    }
    rule = "(农历|阴历)";
    final Pattern pattern1 = Pattern.compile(rule);
    match = pattern1.matcher(Time_Expression);
    if (flag || match.find()) {
      Lunar2Calendar l2c = new Lunar2Calendar();
      Lunar lunar = new Lunar();
      lunar.lunarYear = _tp.tunit[0];
      lunar.lunarMonth = _tp.tunit[1];
      lunar.lunarDay = _tp.tunit[2];
      lunar.isleap = Lunar2Calendar.IsLeapYear(_tp.tunit[0]);
      Solar solar = l2c.LunarToSolar(lunar);
      _tp.tunit[0] = solar.solarYear;
      _tp.tunit[1] = solar.solarMonth;
      _tp.tunit[2] = solar.solarDay;
    }
  }

  private static final Pattern[] pattern_year =
      {Pattern.compile("[0-9]{1,2}(?=年)"), Pattern.compile("[0-9]?[0-9]{3}(?=年)")};

  /**
   * 年-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的年字段
   */
  private void norm_setyear() {
    Matcher match = pattern_year[0].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[0] = Integer.parseInt(match.group());
    }
    // 不仅局限于支持1XXX年和2XXX年的识别，可识别三位数和四位数表示的年份
    match = pattern_year[1].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[0] = Integer.parseInt(match.group());
    }
  }

  private static final Pattern[] pattern_month = {Pattern.compile("((10)|(11)|(12)|([1-9]))(?=月)")};

  /**
   * 月-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的月字段
   */
  private void norm_setmonth() {
    Matcher match = pattern_month[0].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[1] = Integer.parseInt(match.group());
    }
  }

  private static final Pattern[] pattern_day =
      {Pattern.compile("((?<!\\d))([0-3][0-9]|[1-9])(?=([日号]))")};

  /**
   * 日-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的日字段
   */
  private void norm_setday() {
    Matcher match = pattern_day[0].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[2] = Integer.parseInt(match.group());
    }
  }

  private static final Pattern[] pattern_hour =
      {Pattern.compile("(?<!(周|星期))([0-2]?[0-9])(?=([点时]))"), Pattern.compile("(中午)|(午间)"),
          Pattern.compile("(下午)|(午后)|(pm)|(PM)"), Pattern.compile("晚")};

  /**
   * 时-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的时字段
   */
  private void norm_sethour() {
    Matcher match = pattern_hour[0].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[3] = Integer.parseInt(match.group());
    }
    /**
     * 对关键字：中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM的正确时间计算 </br>
     * 规约： </br>
     * 1.中午/午间0-10点视为12-22点 </br>
     * 2.下午/午后0-11点视为12-23点 </br>
     * 3.晚上/傍晚/晚间/晚1-11点视为13-23点，12点视为0点 4.0-11点pm/PM视为12-23点
     */
    match = pattern_hour[1].matcher(Time_Expression);
    if (match.find()) {
      if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
        _tp.tunit[3] += 12;
    }
    match = pattern_hour[2].matcher(Time_Expression);
    if (match.find()) {
      if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
        _tp.tunit[3] += 12;
    }
    match = pattern_hour[3].matcher(Time_Expression);
    if (match.find()) {
      if (_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
        _tp.tunit[3] += 12;
      else if (_tp.tunit[3] == 12)
        _tp.tunit[3] = 0;
    }
  }

  private static final Pattern[] pattern_minute =
      {Pattern.compile("([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!小)[点时]))[0-5]?[0-9](?!刻))"),
          Pattern.compile("(?<=[点时])[1一]刻(?!钟)"), Pattern.compile("(?<=[点时])半"),
          Pattern.compile("(?<=[点时])[3三]刻(?!钟)")};

  /**
   * 分-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的分字段
   */
  private void norm_setminute() {
    /*
     * 添加了省略“分”说法的时间 如17点15
     */
    Matcher match = pattern_minute[0].matcher(Time_Expression);
    if (match.find()) {
      if (!match.group().equals("")) {
        _tp.tunit[4] = Integer.parseInt(match.group());
      }
    }
    /*
     * 添加对一刻，半，3刻的正确识别（1刻为15分，半为30分，3刻为45分）
     */
    match = pattern_minute[1].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[4] = 15;
    }
    match = pattern_minute[2].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[4] = 30;
    }
    match = pattern_minute[3].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[4] = 45;
    }
  }

  private static final Pattern[] pattern_second =
      {Pattern.compile("([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])")};

  /**
   * 秒-规范化方法
   * <p/>
   * 该方法识别时间表达式单元的秒字段
   */
  private void norm_setsecond() {
    /*
     * 添加了省略“分”说法的时间 如17点15分32
     */
    Matcher match = pattern_second[0].matcher(Time_Expression);
    if (match.find()) {
      _tp.tunit[5] = Integer.parseInt(match.group());
    }
  }

  private static final Pattern[] pattern_total = {
      Pattern.compile("(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]"),
      Pattern.compile("(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]"), Pattern.compile("(中午)|(午间)"),
      Pattern.compile("(下午)|(午后)|(pm)|(PM)"), Pattern.compile("晚"),
      Pattern.compile("[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])"),
      Pattern.compile("((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}"),
      Pattern.compile(
          "[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])")};

  /**
   * 特殊形式的规范化方法
   * <p/>
   * 该方法识别特殊形式的时间表达式单元的各个字段
   */
  private void norm_setTotal() {
    Matcher match;
    String[] tmp_parser;
    String tmp_target;
    /*
     * 修改了函数中所有的匹配规则使之更为严格
     */
    match = pattern_total[0].matcher(Time_Expression);
    if (match.find()) {
      tmp_parser = new String[3];
      tmp_target = match.group();
      tmp_parser = tmp_target.split(":");
      _tp.tunit[3] = Integer.parseInt(tmp_parser[0]);
      _tp.tunit[4] = Integer.parseInt(tmp_parser[1]);
      _tp.tunit[5] = Integer.parseInt(tmp_parser[2]);
    } else {// 添加了省略秒的:固定形式的时间规则匹配
      match = pattern_total[1].matcher(Time_Expression);
      if (match.find()) {
        tmp_parser = new String[2];
        tmp_target = match.group();
        tmp_parser = tmp_target.split(":");
        _tp.tunit[3] = Integer.parseInt(tmp_parser[0]);
        _tp.tunit[4] = Integer.parseInt(tmp_parser[1]);
      }
    }
    // 增加了:固定形式时间表达式的 中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM 的正确时间计算，规约同上
    match = pattern_total[2].matcher(Time_Expression);
    if (match.find()) {
      if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 10)
        _tp.tunit[3] += 12;
    }
    match = pattern_total[3].matcher(Time_Expression);
    if (match.find()) {
      if (_tp.tunit[3] >= 0 && _tp.tunit[3] <= 11)
        _tp.tunit[3] += 12;
    }
    match = pattern_total[4].matcher(Time_Expression);
    if (match.find()) {
      if (_tp.tunit[3] >= 1 && _tp.tunit[3] <= 11)
        _tp.tunit[3] += 12;
      else if (_tp.tunit[3] == 12)
        _tp.tunit[3] = 0;
    }
    match = pattern_total[5].matcher(Time_Expression);
    if (match.find()) {
      tmp_parser = new String[3];
      tmp_target = match.group();
      tmp_parser = tmp_target.split("-");
      _tp.tunit[0] = Integer.parseInt(tmp_parser[0]);
      _tp.tunit[1] = Integer.parseInt(tmp_parser[1]);
      _tp.tunit[2] = Integer.parseInt(tmp_parser[2]);
    }
    match = pattern_total[6].matcher(Time_Expression);
    if (match.find()) {
      tmp_parser = new String[3];
      tmp_target = match.group();
      tmp_parser = tmp_target.split("/");
      _tp.tunit[1] = Integer.parseInt(tmp_parser[0]);
      _tp.tunit[2] = Integer.parseInt(tmp_parser[1]);
      _tp.tunit[0] = Integer.parseInt(tmp_parser[2]);
    }
    /*
     * 增加了:固定形式时间表达式 年.月.日 的正确识别 add by 曹零
     */
    match = pattern_total[7].matcher(Time_Expression);
    if (match.find()) {
      tmp_parser = new String[3];
      tmp_target = match.group();
      tmp_parser = tmp_target.split("\\.");
      _tp.tunit[0] = Integer.parseInt(tmp_parser[0]);
      _tp.tunit[1] = Integer.parseInt(tmp_parser[1]);
      _tp.tunit[2] = Integer.parseInt(tmp_parser[2]);
    }
  }

  private static final Pattern[] pattern_base_related = {Pattern.compile("\\d+(?=天[以之]?前)"),
      Pattern.compile("\\d+(?=天[以之]?后)"), Pattern.compile("次日"),
      Pattern.compile("\\d+(?=(个)?月[以之]?前)"), Pattern.compile("\\d+(?=(个)?月[以之]?后)"),
      Pattern.compile("\\d+(?=年[以之]?前)"), Pattern.compile("\\d+(?=年[以之]?后)"),
      Pattern.compile("(第)(\\d)(年)"), Pattern.compile("([次下])年"), Pattern.compile("(当年|同年|同一年)"),
      Pattern.compile("(当月|这个月)"), Pattern.compile("(当天|当日|同日|同一天)")};

  /**
   * 设置以上文时间为基准的时间偏移计算
   */
  private void norm_setBaseRelated() {
    String[] time_grid;
    time_grid = normalizer.getTimeBase().split("-");
    int[] ini = new int[6];
    for (int i = 0; i < 6; i++)
      ini[i] = Integer.parseInt(time_grid[i]);
    Calendar calendar = Calendar.getInstance();
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    calendar.set(ini[0], ini[1] - 1, ini[2], ini[3], ini[4], ini[5]);
    calendar.getTime();
    boolean[] flag = {false, false, false};// 观察时间表达式是否因当前相关时间表达式而改变时间
    Matcher match = pattern_base_related[0].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int day = Integer.parseInt(match.group());
      calendar.add(Calendar.DATE, -day);
    }
    match = pattern_base_related[1].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int day = Integer.parseInt(match.group());
      calendar.add(Calendar.DATE, day);
    }
    match = pattern_base_related[2].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, 1);
    }
    match = pattern_base_related[3].matcher(Time_Expression);
    if (match.find()) {
      flag[1] = true;
      int month = Integer.parseInt(match.group());
      calendar.add(Calendar.MONTH, -month);
    }
    match = pattern_base_related[4].matcher(Time_Expression);
    if (match.find()) {
      flag[1] = true;
      int month = Integer.parseInt(match.group());
      calendar.add(Calendar.MONTH, month);
    }
    match = pattern_base_related[5].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      int year = Integer.parseInt(match.group());
      calendar.add(Calendar.YEAR, -year);
    }
    match = pattern_base_related[6].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      int year = Integer.parseInt(match.group());
      calendar.add(Calendar.YEAR, year);
    }
    match = pattern_base_related[7].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      int year = Integer.parseInt(match.group(2));
      calendar.add(Calendar.YEAR, year - 1);
    }
    match = pattern_base_related[8].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, 1);
    }
    match = pattern_base_related[9].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, 0);
    }
    match = pattern_base_related[10].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.MONTH, 0);
    }
    match = pattern_base_related[11].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, 0);
    }
    String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
    String[] time_fin = s.split("-");
    if (flag[0] || flag[1] || flag[2]) {
      _tp.tunit[0] = Integer.parseInt(time_fin[0]);
    }
    if (flag[1] || flag[2])
      _tp.tunit[1] = Integer.parseInt(time_fin[1]);
    if (flag[2])
      _tp.tunit[2] = Integer.parseInt(time_fin[2]);
  }

  private static final Pattern[] pattern_cur_related = {Pattern.compile("前年"),
      Pattern.compile("去年"), Pattern.compile("今年"), Pattern.compile("明年"), Pattern.compile("后年"),
      Pattern.compile("上(个)?月"), Pattern.compile("(本|这个)月"), Pattern.compile("下(个)?月"),
      Pattern.compile("大前天"), Pattern.compile("(?<!大)前天"), Pattern.compile("昨"),
      Pattern.compile("今(?!年)"), Pattern.compile("明(?!年)"), Pattern.compile("(?<!大)后天"),
      Pattern.compile("大后天"), Pattern.compile("(?<=(上上(周|星期)))[1-7]"),
      Pattern.compile("(?<=((?<!上)上(周|星期)))[1-7]"), Pattern.compile("(?<=((?<!下)下(周|星期)))[1-7]"),
      Pattern.compile("(?<=(下下(周|星期)))[1-7]"), Pattern.compile("(?<=((?<!([上下]))(周|星期)))[1-7]")};

  /**
   * 设置当前时间相关的时间表达式
   */
  private void norm_setCurRelated() {
    String[] time_grid;
    time_grid = normalizer.getOldTimeBase().split("-");
    int[] ini = new int[6];
    for (int i = 0; i < 6; i++) {
      ini[i] = Integer.parseInt(time_grid[i]);
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    calendar.set(ini[0], ini[1] - 1, ini[2], ini[3], ini[4], ini[5]);
    calendar.getTime();
    boolean[] flag = {false, false, false};// 观察时间表达式是否因当前相关时间表达式而改变时间
    Matcher match = pattern_cur_related[0].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, -2);
    }
    match = pattern_cur_related[1].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, -1);
    }
    match = pattern_cur_related[2].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, 0);
    }
    match = pattern_cur_related[3].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, 1);
    }
    match = pattern_cur_related[4].matcher(Time_Expression);
    if (match.find()) {
      flag[0] = true;
      calendar.add(Calendar.YEAR, 2);
    }
    match = pattern_cur_related[5].matcher(Time_Expression);
    if (match.find()) {
      flag[1] = true;
      calendar.add(Calendar.MONTH, -1);

    }
    match = pattern_cur_related[6].matcher(Time_Expression);
    if (match.find()) {
      flag[1] = true;
      calendar.add(Calendar.MONTH, 0);
    }
    match = pattern_cur_related[7].matcher(Time_Expression);
    if (match.find()) {
      flag[1] = true;
      calendar.add(Calendar.MONTH, 1);
    }
    match = pattern_cur_related[8].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, -3);
    }
    match = pattern_cur_related[9].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, -2);
    }
    match = pattern_cur_related[10].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, -1);
    }
    match = pattern_cur_related[11].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, 0);
    }
    match = pattern_cur_related[12].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, 1);
    }
    match = pattern_cur_related[13].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, 2);
    }
    match = pattern_cur_related[14].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      calendar.add(Calendar.DATE, 3);
    }
    match = pattern_cur_related[15].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int week = Integer.parseInt(match.group());
      if (week == 7)
        week = 1;
      else
        week++;
      calendar.add(Calendar.WEEK_OF_MONTH, -2);
      calendar.set(Calendar.DAY_OF_WEEK, week);
    }
    match = pattern_cur_related[16].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int week = Integer.parseInt(match.group());
      if (week == 7)
        week = 1;
      else
        week++;
      calendar.add(Calendar.WEEK_OF_MONTH, -1);
      calendar.set(Calendar.DAY_OF_WEEK, week);
    }
    match = pattern_cur_related[17].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int week = Integer.parseInt(match.group());
      if (week == 7)
        week = 1;
      else
        week++;
      calendar.add(Calendar.WEEK_OF_MONTH, 1);
      calendar.set(Calendar.DAY_OF_WEEK, week);
    }
    match = pattern_cur_related[18].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int week = Integer.parseInt(match.group());
      if (week == 7)
        week = 1;
      else
        week++;
      calendar.add(Calendar.WEEK_OF_MONTH, 2);
      calendar.set(Calendar.DAY_OF_WEEK, week);
    }
    match = pattern_cur_related[19].matcher(Time_Expression);
    if (match.find()) {
      flag[2] = true;
      int week = Integer.parseInt(match.group());
      if (week == 7)
        week = 1;
      else
        week++;
      calendar.add(Calendar.WEEK_OF_MONTH, 0);
      calendar.set(Calendar.DAY_OF_WEEK, week);
    }
    String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
    String[] time_fin = s.split("-");
    if (flag[0] || flag[1] || flag[2]) {
      _tp.tunit[0] = Integer.parseInt(time_fin[0]);
    }
    if (flag[1] || flag[2])
      _tp.tunit[1] = Integer.parseInt(time_fin[1]);
    if (flag[2])
      _tp.tunit[2] = Integer.parseInt(time_fin[2]);
  }

  /**
   * 该方法用于更新timeBase使之具有上下文关联性
   */
  private void modifyTimeBase() {
    String[] time_grid;
    time_grid = normalizer.getTimeBase().split("-");
    String s = "";
    if (_tp.tunit[0] != -1)
      s += Integer.toString(_tp.tunit[0]);
    else
      s += time_grid[0];
    for (int i = 1; i < 6; i++) {
      s += "-";
      if (_tp.tunit[i] != -1)
        s += Integer.toString(_tp.tunit[i]);
      else
        s += time_grid[i];
    }
    normalizer.setTimeBase(s);
  }

  /**
   * 时间表达式规范化的入口
   * <p/>
   * 时间表达式识别后，通过此入口进入规范化阶段， 具体识别每个字段的值
   */
  private void Time_Normalization() {
    norm_setyear();
    norm_setmonth();
    norm_setday();
    norm_sethour();
    norm_setminute();
    norm_setsecond();
    norm_setTotal();
    norm_setBaseRelated();
    norm_setCurRelated();
    modifyTimeBase();
    norm_setDate();
    _tp_origin.tunit = _tp.tunit.clone();
    String[] time_grid;
    time_grid = normalizer.getTimeBase().split("-");
    int tunitpointer = 5;
    while (tunitpointer >= 0 && _tp.tunit[tunitpointer] < 0) {
      tunitpointer--;
    }
    for (int i = 0; i < tunitpointer; i++) {
      if (_tp.tunit[i] < 0)
        _tp.tunit[i] = Integer.parseInt(time_grid[i]);
    }
    time_full = _tp.tunit.clone();
  }

  public String toString() {
    String s = "";
    if (time_full[0] != -1) {
      s += String.valueOf(time_full[0]) + "年";
      if (time_full[1] != -1) {
        s += String.valueOf(time_full[1]) + "月";
        if (time_full[2] != -1) {
          s += String.valueOf(time_full[2]) + "日";
          if (time_full[3] != -1) {
            s += String.valueOf(time_full[3]) + "时";
            if (time_full[4] != -1) {
              s += String.valueOf(time_full[4]) + "分";
              if (time_full[5] != -1) {
                s += String.valueOf(time_full[5]) + "秒";
              }
            }
          }
        }
      }
    }
    return s;
  }

  public boolean isFalseDateFlag() {
    return falseDateFlag;
  }
}
