package com.ocp.rabbit.repository.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.tool.algorithm.number.NumberConverter;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 命名实体识别
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class NamedEntityRecognizer {
  private String[] courtAddr;
  private List<String> attorneys;

  private Context context;

  public NamedEntityRecognizer(Context context) {
    this.context = context;
    courtAddr = new String[] {null, null, null, null};// {省级法院，市级法院，县区法院，乡镇法院}
    if (this.context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_court_province[InfoPointKey.mode]) != null) {
      courtAddr[0] = (String) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_court_province[InfoPointKey.mode]);
    }
    if (this.context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_court_city[InfoPointKey.mode]) != null) {
      courtAddr[1] = (String) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_court_city[InfoPointKey.mode]);
    }
    if (this.context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_court_county[InfoPointKey.mode]) != null) {
      courtAddr[2] = (String) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_court_county[InfoPointKey.mode]);
    }
  }

  private static final Pattern PATTERN_THIS_COURT = Pattern.compile("本院|我院");
  private static final Pattern PATTERN_THAT_COURT = Pattern.compile("该院");
  private static final Pattern PATTERN_FILTER_COURT = Pattern.compile("法院|本院|该院");

  /**
   *
   * @return 找到的法院名字实体，info 为 String，即标准法院名字
   */
  private static NamedEntity[] parseCourtAddress(String rawAddr, boolean FILTER,
      NamedEntity NES_LAST) {
    boolean flag;
    if (FILTER) {
      if (!PATTERN_FILTER_COURT.matcher(rawAddr).find()) {
        return new NamedEntity[0];
      } else {
        flag = true;
      }
    } else {
      flag = true;
    }
    if (flag) {
      List<NamedEntity> lne = new ArrayList<>();
      ResourceReader.getInstance().readCourt("classification.court.china");
      Matcher matcher = ResourceReader.getInstance().patternCourtName.matcher(rawAddr);
      Map<String, String> name2StdCourtName = ResourceReader.readCourtNames();
      while (matcher.find()) {
        if (name2StdCourtName.containsKey(matcher.group())) {
          lne.add(new NamedEntity(matcher.group(), matcher.start(),
              name2StdCourtName.get(matcher.group()), "法院"));
        }
      }
      // 加入该院
      NamedEntity[] nes_that_court =
          NamedEntityRecognizer.recognizeEntityByRegex(rawAddr, PATTERN_THAT_COURT);
      NamedEntity[] nes = lne.toArray(new NamedEntity[lne.size()]);
      if (nes_that_court.length != 0) {
        return NamedEntityRecognizer.coreferenceResolutionWithNearestEntity(nes_that_court, nes,
            NES_LAST);
      }
      return nes;
    }
    return new NamedEntity[0];
  }

  /**
   * 简单匹配标准法院名字
   */
  public static String parseCourtAddress(String rawAddr) {
    ResourceReader.getInstance().readCourt("classification.court.china");
    Matcher matcher = ResourceReader.getInstance().patternCourtName.matcher(rawAddr);
    if (matcher.find()) {
      if (ResourceReader.readCourtNames().containsKey(matcher.group())) {
        return ResourceReader.readCourtNames().get(matcher.group());
      }
    }
    return null;
  }

  /**
   * 给法院地址分类
   */
  public static String[] classifyCourtAddress(String rawAddr) {
    if (ResourceReader.readCourtNames().values().contains(rawAddr)) {
      if (ResourceReader.readCourtClassification().containsKey(rawAddr))
        return ResourceReader.readCourtClassification().get(rawAddr);
    }
    AddressClassifier addrClassfy = AddressClassifier.getInstance();
    return AddressClassifier.getEachLevel(rawAddr, addrClassfy);
  }


  /**
   * 识别全国法院名字，可以识别“我院”，“本院”等指代
   * 
   * @param s 要识别的字符串
   * @param FILTER 是否需要过滤，如果是True，表示先判断是否含有 “法院|本院|该院”；如果是False,则不进行判断
   * @return NamedEntity[]识别结果，NamedEntity 里面 info取值为法院标准名字
   */
  public static NamedEntity[] recognizeCourtName(String s, boolean FILTER, String thisCourt,
      NamedEntity NES_LAST_COURT) {
    List<NamedEntity> lne = new ArrayList<>();
    if (null != thisCourt) {
      Matcher matcher = PATTERN_THIS_COURT.matcher(s);
      while (matcher.find()) {
        lne.add(new NamedEntity(matcher.group(), matcher.start(), thisCourt, "法院"));
      }
      Collections.addAll(lne, parseCourtAddress(s, FILTER, NES_LAST_COURT));
      lne.sort(new Comparator<NamedEntity>() {
        @Override
        public int compare(NamedEntity o1, NamedEntity o2) {
          return o1.getOffset() - o2.getOffset();
        }
      });
      return lne.toArray(new NamedEntity[lne.size()]);
    } else {
      return parseCourtAddress(s, FILTER, NES_LAST_COURT);
    }
  }

  /**
   * 识别律师，根据已经提取的律师名字，识别其余段落语句里面出现的律师名字
   * 
   * @param source 带识别的字符串
   * @return NamedEntity[] 识别结果，info为律师名字
   */
  public NamedEntity[] recognizeAttorney(String source) {

    if (null == attorneys)
      return new NamedEntity[0];
    List<NamedEntity> lne = new ArrayList<>();
    for (String target : attorneys) {
      int i = 0;
      while (i != -1) {
        int j = source.indexOf(target, i);
        if (j != -1) {
          // 更新至下一个位置
          lne.add(new NamedEntity(target, j, null));
          i = j + target.length();
        } else {
          i = -1;
        }
      }
    }

    lne.sort((ne1, ne2) -> {
      if (ne1.offset != ne2.getOffset())
        return ne1.offset - ne2.offset;
      else
        return ne2.source.length() - ne1.source.length();
    });
    // 去掉重复的
    return dropDuplicates(lne);
  }

  /**
   * 给地址分类
   * 
   * @param s 待分类的地址字符串
   * @return NamedEntity 一个实体，info为String[4] 分别为省／市／县（区）／乡（镇）
   */
  public NamedEntity recognizeAddress(String s) {
    int[] pos = new int[2];
    String[] addr =
        AddressClassifier.getEachLevel(s, pos, courtAddr, AddressClassifier.getInstance());
    if (pos[0] != -1 && pos[1] != -1 && null != addr[0]) {
      return new NamedEntity(s.substring(pos[0], pos[1]), pos[0], addr);
    }
    return null;
  }

  public static NamedEntity recognizeAddress(String s, String[] addrlevels) {
    int[] pos = new int[2];
    AddressClassifier.getInstance();
    String[] addr =
        AddressClassifier.getEachLevel(s, pos, addrlevels, AddressClassifier.getInstance());
    if (pos[0] != -1 && pos[1] != -1 && null != addr[0]) {
      return new NamedEntity(s.substring(pos[0], pos[1]), pos[0], addr);
    }
    return null;
  }


  /**
   * 识别时间，如果时间是紧密相邻的，则同为一个实体。比如：1945年4月2日16时30分是一个实体，1945年4月2日和次日是两个实体
   * 
   * @param s 待识别的字符串
   * @return 时间识别结果，info类型为String，时间没有进行标准化
   */
  public static NamedEntity[] recognizeTime(String s) {
    return new DateHandler(s).getTimeUnitsNE();
  }

  /**
   * 识别时间，如果时间是紧密相邻的，则同为一个实体。比如：1945年4月2日16时30分是一个实体，1945年4月2日和次日是两个实体 同时考虑了基准时间，及 如果句子为
   * "次日18点，她来到了学校"，如果timeBase（可能是上一个句子识别的结果，也可能是其他外部传入的信息） 是1998年3月2日，则该句里 “次日18点”会识别为 1998年3月3日18时
   * 
   * @param s 待识别的时间字符串
   * @param timeBase 基准时间，可能是上一个句子识别的结果，也可能是其他外部传入的信息
   * @return NamedEntity[] 识别结果，info为 XX年XX月XX日XX时XX分XX秒结构的时间，没有标准化
   */
  public NamedEntity[] recognizeTime(String s, String timeBase) {
    return new DateHandler(s, timeBase).getTimeUnitsNE();
  }

  /**
   * 识别时间
   * 
   * @param dh 时间识别类，里面有一个field，timeBase，会不断改变
   * @return NamedEntity[] 识别结果，info为 XX年XX月XX日XX时XX分XX秒结构的时间，没有标准化
   */
  public static NamedEntity[] recognizeTime(DateHandler dh) {
    return dh.getTimeUnitsNE();
  }

  /**
   *
   * @return 钱识别结果，info类型为double
   */
  public static NamedEntity[] recognizeMoney(String s) {
    return recognizeNumber(s, new String[] {"元", "圆"});
  }

  /**
   * 从字符串中识别数字
   * 
   * @param s 含数字信息的字符串
   * @param units 数字单位集合
   */
  public static NamedEntity[] recognizeNumber(String s, String[] units) {
    List<WrapNumberFormat> lwnf = new NumberRecognizer(units).getNumbers(new String[] {s}, true);
    NamedEntity[] nes = new NamedEntity[lwnf.size()];
    for (int i = 0; i < lwnf.size(); i++) {
      WrapNumberFormat wnf = lwnf.get(i);
      nes[i] = new NamedEntity(wnf.getNumber() + wnf.getUnit(),
          wnf.getPosition().getPos_of_sentenceByComma(), wnf.getArabicNumber());
    }
    return nes;
  }

  /**
   * 识别利率,默认是月。
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public NamedEntity[] recognizeInterestRate(String s) {
    List<WrapNumberFormat> lwnf =
        new NumberRecognizer(new String[] {"分", "厘", "角"}).getNumbers(new String[] {s}, true);
    LinkedList<NamedEntity> lwnf_new = new LinkedList<>();
    for (int i = 0; i < lwnf.size(); i++) {
      WrapNumberFormat wnf = lwnf.get(i);
      int start = wnf.getPosition().getPos_of_sentenceByComma();
      String word = wnf.getPosition().getValue();
      double tmpSum = 0;
      double unit;
      switch (wnf.getUnit()) {
        case "角":
          tmpSum += wnf.getArabicNumber() / 10;
          break;
        case "分":
          tmpSum += wnf.getArabicNumber() / 100;
          break;
        case "厘":
          tmpSum += wnf.getArabicNumber() / 1000;
          break;
        default:
          tmpSum += wnf.getArabicNumber();
          break;
      }
      // 如果是第一个；或者当前的开始和上一个的末尾不相等，则建立新的元素
      if (i == 0 || start != lwnf_new.getLast().source.length() + lwnf_new.getLast().offset) {
        if (i != 0) {
          int end = lwnf_new.getLast().source.length() + lwnf_new.getLast().offset;
          if (end < s.length()) {
            NamedEntity ne = lwnf_new.getLast();
            double addedValue = 0;
            if (NumberConverter.num2Arabic.containsKey(s.charAt(end))) {
              if (s.charAt(end - 1) == '角') {
                unit = 0.1;
              } else if (s.charAt(end - 1) == '分') {
                unit = 0.01;
              } else {
                unit = 0.001;
              }
              addedValue = NumberConverter.num2Arabic.get(s.charAt(end)) * unit / 10;
              // update
              ne.setInfo((double) ne.getInfo() + addedValue);
              ne.setSource(ne.getSource() + s.substring(end, end + 1));
            } else if (s.charAt(end) == '半') {
              if (s.charAt(end - 1) == '角') {
                unit = 0.1;
              } else if (s.charAt(end - 1) == '分') {
                unit = 0.01;
              } else {
                unit = 0.001;
              }
              addedValue += 0.5 * unit;
              // update
              ne.setInfo((double) ne.getInfo() + addedValue);
              ne.setSource(ne.getSource() + s.substring(end, end + 1));
            }
          }
        }
        lwnf_new.addLast(new NamedEntity(word, start, tmpSum));
      }
      // 更新最后一个元素，即将最后一个元素和当前元素连接
      else {
        NamedEntity ne = lwnf_new.getLast();
        ne.source = ne.source + word;
        double newSum = (double) ne.getInfo() + tmpSum;
        ne.setInfo(newSum);
      }
    }
    List<WrapNumberFormat> lwnf_pctg = NumberRecognizer.getPercentage(new String[] {s});
    for (WrapNumberFormat wnf : lwnf_pctg) {
      String word = wnf.getPosition().getValue();
      int offset = wnf.getPosition().getPos_of_sentenceByComma();
      double value = wnf.getArabicNumber();
      lwnf_new.add(new NamedEntity(word, offset, value));
    }
    lwnf_new.sort((ne1, ne2) -> {
      if (ne1.offset != ne2.getOffset())
        return ne1.offset - ne2.offset;
      else
        return ne2.source.length() - ne1.source.length();
    });
    // 去掉重复的
    return dropDuplicates(lwnf_new);
  }

  public NamedEntity[] recognizeDurationByRegex(String s) {
    return null;
  }

  private static final Pattern HALF_DURATION = Pattern.compile("半个?([年月天])");

  /**
   * 持续时间识别<br>
   * 
   * @author yu.yao
   * @param
   * @return int[3] 年 月 日
   */
  public static NamedEntity[] recognizeDuration(String s, String unitFlag) {
    // TODO 相邻时间单元合并并转换单位
    List<WrapNumberFormat> lwnf =
        new NumberRecognizer(new String[] {"年", "月", "天", "日", "个月", "多月"})
            .getNumbers(new String[] {s}, true);
    LinkedList<NamedEntity> lwnf_new = new LinkedList<>();
    for (int i = 0; i < lwnf.size(); i++) {
      WrapNumberFormat wnf = lwnf.get(i);
      int start = wnf.getPosition().getPos_of_sentenceByComma();
      String word = wnf.getNumber() + wnf.getUnit();
      double[] dates = new double[] {0, 0, 0};
      int pos;
      switch (wnf.getUnit()) {
        case "年":
          dates[0] = (int) wnf.getArabicNumber();
          pos = 0;
          break;
        case "天":
        case "日":
          dates[2] = (int) wnf.getArabicNumber();
          pos = 2;
          break;
        default:
          dates[1] = (int) wnf.getArabicNumber();
          pos = 1;
          break;
      }
      // 如果是第一个；或者当前的开始和上一个的末尾不相等，则建立新的元素
      if (i == 0 || start != lwnf_new.getLast().source.length() + lwnf_new.getLast().offset) {
        if (i != 0) {
          int end = lwnf_new.getLast().source.length() + lwnf_new.getLast().offset;
          int prevPos;
          if (end < s.length()) {
            NamedEntity ne = lwnf_new.getLast();
            if (s.charAt(end) == '半') {
              if (s.charAt(end - 1) == '年') {
                prevPos = 0;
              } else if (s.charAt(end - 1) == '月') {
                prevPos = 1;
              } else {
                prevPos = 2;
              }
              // update
              double[] prevDates = (double[]) ne.getInfo();
              prevDates[prevPos] += 0.5;
              ne.setSource(ne.getSource() + s.substring(end, end + 1));
            }
          }
        }
        lwnf_new.addLast(new NamedEntity(word, start, dates));
      }
      // 更新最后一个元素，即将最后一个元素和当前元素连接
      else {
        NamedEntity ne = lwnf_new.getLast();
        ne.source = ne.source + word;
        ((double[]) ne.info)[pos] = dates[pos];
      }
    }
    Matcher matcher = HALF_DURATION.matcher(s);
    while (matcher.find()) {
      double[] dates = {0, 0, 0};
      switch (matcher.group(1)) {
        case "年":
          dates[0] = 0.5;
          break;
        case "月":
          dates[1] = 0.5;
          break;
        case "天":
          dates[2] = 0.5;
          break;
      }
      lwnf_new.add(new NamedEntity(matcher.group(), matcher.start(), dates));
    }
    return dropDuplicatesAndFilterDates(lwnf_new, unitFlag);
  }

  /**
   * 识别出刑法里面定义的罪名，如果罪名紧邻，则放在同一个实体 这里定义的相邻指前后两个罪名相差不超过1个字符 info输出为 List <String>，即这个实体包含的罪名
   */
  @SuppressWarnings("unchecked")
  public static NamedEntity[] recognizeCrimeNames(String sent) {
    Matcher matcher;
    Pattern patternCrimes = ResourceReader.crimesCompile();
    matcher = patternCrimes.matcher(sent);
    Pattern patternCrimesWithoutZui = ResourceReader.crimesCompileSimple();
    if (!matcher.find()) {
      matcher = patternCrimesWithoutZui.matcher(sent);
    } else {
      matcher.reset();
    }
    LinkedList<NamedEntity> lne = new LinkedList<>();
    Map<String, String> stdCrimeNameMapper = ResourceReader.stdNameMapper();
    while (matcher.find()) {
      String crimeName = matcher.group();
      String stdCrimeName = stdCrimeNameMapper.get(crimeName);
      if (null == stdCrimeName)
        continue;
      int offset = matcher.start();
      // int end = matcher.end();
      if (lne.size() == 0) {
        List<String> crimes = new ArrayList<>();
        crimes.add(stdCrimeName);
        lne.add(new NamedEntity(crimeName, offset, crimes));
      } else {
        int prev_end = lne.getLast().getOffset() + lne.getLast().getSource().length();
        // 如果相差2个字符之类，认为是相邻的
        if (prev_end + 3 >= offset) {
          NamedEntity ne = lne.getLast();
          ne.setSource(sent.substring(lne.getLast().getOffset(), matcher.end()));
          ((List<String>) ne.getInfo()).add(stdCrimeName);
        } else {
          List<String> crimes = new ArrayList<>();
          crimes.add(stdCrimeName);
          lne.add(new NamedEntity(crimeName, offset, crimes));
        }
      }
    }
    return lne.toArray(new NamedEntity[lne.size()]);
  }

  /**
   * @param pattern 正则表达式
   * @return 匹配上的正则表达位置，不关心具体标准化取值，因此设置为null
   */
  public static NamedEntity[] recognizeEntityByRegex(String s, Pattern pattern) {
    java.util.regex.Matcher matcher = pattern.matcher(s);
    List<NamedEntity> lne = new ArrayList<>();
    while (matcher.find()) {
      lne.add(new NamedEntity(matcher.group(), matcher.start(), null));
    }
    return lne.toArray(new NamedEntity[lne.size()]);
  }

  /**
   * 功能：根据正则表达式匹配实体，并用boolean值填充到匹配实体的info字段
   */
  public static NamedEntity[] recognizeEntityByRegex(String s, Pattern[] patterns,
      boolean boolVal) {

    List<NamedEntity> lne = new ArrayList<>();
    for (Pattern pattern : patterns) {
      java.util.regex.Matcher matcher = pattern.matcher(s);
      while (matcher.find()) {
        lne.add(new NamedEntity(matcher.group(), matcher.start(), boolVal));
      }
    }
    NamedEntity[] nes = lne.toArray(new NamedEntity[lne.size()]);
    return nes;
  }

  /**
   * 功能: 根据正则获取匹配，并用fillVal填充匹配结果，支持捕获模式
   * 
   * @param sent 要进行匹配的字符串
   * @param pattern 正则表达式
   * @param fillVal 填充字符串，支持捕获模式, 形如："\1\2"、"中国" 、"\1中国\2"
   * @return 匹配到的实体集合
   */
  public static NamedEntity[] recognizeEntityByRegex(String sent, Pattern pattern, String fillVal) {
    java.util.regex.Matcher matcher = pattern.matcher(sent);
    List<NamedEntity> lne = new ArrayList<>();
    while (matcher.find()) {
      String tmpVal = "";
      List<Integer[]> numList = getMatchPositionsByRegex(fillVal, Pattern.compile("(\\\\\\d)"));
      if ((numList == null) || (numList.size() == 0)) {
        tmpVal = fillVal;
      } else {
        StringBuilder sbValue = new StringBuilder("");
        sbValue.append(fillVal.substring(0, numList.get(0)[0]));
        int index;
        Integer[] lastTmp = null;
        for (Integer[] tmp : numList) {
          if (lastTmp != null) {
            sbValue.append(fillVal.substring(lastTmp[1], tmp[0]));
          }
          index = Integer.valueOf(fillVal.substring(tmp[0] + 1, tmp[1]));
          sbValue.append(matcher.group(index));
          lastTmp = tmp;
        }
        sbValue.append(fillVal.substring(numList.get(numList.size() - 1)[1]));
        tmpVal = sbValue.toString();
      }
      lne.add(new NamedEntity(matcher.group(), matcher.start(), tmpVal));
    }

    return lne.toArray(new NamedEntity[lne.size()]);
  }

  private static List<Integer[]> getMatchPositionsByRegex(String str, Pattern patt) {
    List<Integer[]> rsltList = new ArrayList<>();

    Matcher matcher = patt.matcher(str);
    while (matcher.find()) {
      Integer[] tmp = new Integer[2];
      tmp[0] = matcher.start();
      tmp[1] = matcher.end();
      rsltList.add(tmp);
    }

    return rsltList;
  }

  /**
   * 根据正则提取，同时把关心的分组放进info里面
   */
  public static NamedEntity[] recognizeEntityByRegex(String s, Pattern pattern, int group) {
    java.util.regex.Matcher matcher = pattern.matcher(s);
    List<NamedEntity> lne = new ArrayList<>();
    while (matcher.find()) {
      lne.add(new NamedEntity(matcher.group(), matcher.start(), matcher.group(group)));
    }
    NamedEntity[] nes = lne.toArray(new NamedEntity[lne.size()]);
    return nes;
  }

  /**
   * 根据正则表达式匹配实体,并且根据types分类，必须保证types维数和patterns维数一样
   */
  public static NamedEntity[] recognizeEntityByRegex(String s, Pattern[] patterns, Object[] types) {
    List<NamedEntity> lne = new ArrayList<>();
    for (int i = 0; i < patterns.length; i++) {
      Pattern pattern = patterns[i];
      java.util.regex.Matcher matcher = pattern.matcher(s);
      while (matcher.find()) {
        NamedEntity ne = new NamedEntity(matcher.group(), matcher.start(), types[i]);
        ne.setType((String) types[i]);
        lne.add(ne);
      }
    }
    return lne.toArray(new NamedEntity[lne.size()]);
  }

  /**
   * 根据正则表达式匹配实体
   */
  public static NamedEntity[] recognizeEntityByRegex(String s, Pattern[] patterns) {
    List<NamedEntity> lne = new ArrayList<>();
    for (Pattern pattern : patterns) {
      java.util.regex.Matcher matcher = pattern.matcher(s);
      while (matcher.find()) {
        lne.add(new NamedEntity(matcher.group(), matcher.start(), matcher.group()));
      }
    }
    return lne.toArray(new NamedEntity[lne.size()]);
  }

  /**
   * @param matches 字符数组
   * @return 匹配上的正则表达位置，不关心具体标准化取值，因此设置为null
   */
  public static NamedEntity[] recognizeEntityByString(String s, String[] matches) {

    List<NamedEntity> lne = new ArrayList<>();
    for (String match : matches) {
      int offset = s.indexOf(match, 0);
      while (offset >= 0) {
        lne.add(new NamedEntity(match, offset, null));
        offset = offset + match.length();
        offset = s.indexOf(match, offset);
      }
    }
    // lne.sort((o1,o2) -> { return o1.getOffset() - o2.getOffset();});
    lne.sort((ne1, ne2) -> {
      if (ne1.offset != ne2.getOffset())
        return ne1.offset - ne2.offset;
      else
        return ne2.source.length() - ne1.source.length();
    });
    return dropDuplicates(lne);
    // NamedEntity[] nes = lne.toArray(new NamedEntity[lne.size()]);
  }

  /**
   * 识别{,，；;。}的位置
   * 
   * @return 标点符号的位置，包括{,，；;。}
   */
  public static Integer[] recognizeComma(String s) {
    java.util.regex.Matcher matcher = DocumentUtils.Pattern_Comma_Semicolon_Period.matcher(s);
    List<Integer> lne = new ArrayList<>();
    while (matcher.find()) {
      lne.add(matcher.start());
    }
    Integer[] nes = lne.toArray(new Integer[lne.size()]);
    return nes;
  }

  public static Integer[] recognizePeriods(String s) {
    java.util.regex.Matcher matcher = DocumentUtils.Pattern_Periods.matcher(s);
    List<Integer> lne = new ArrayList<>();
    while (matcher.find()) {
      lne.add(matcher.start());
    }
    return lne.toArray(new Integer[lne.size()]);
  }

  /**
   *
   * @param comma_semicolon 记录逗号和分号的位置，分别记录在第一维和第二维
   * @return 逗号和分号的位置合并在一起输出
   */
  public static Integer[] recognizeCommaAndSemiColon(String s, Integer[][] comma_semicolon) {
    java.util.regex.Matcher matcher = DocumentUtils.Pattern_Comma.matcher(s);
    List<Integer> lne = new ArrayList<>();
    List<Integer> lne_comma = new ArrayList<>();
    List<Integer> lne_semicolon = new ArrayList<>();
    while (matcher.find()) {
      lne.add(matcher.start());
      lne_comma.add(matcher.start());
    }

    matcher = DocumentUtils.Pattern_Semicolon.matcher(s);
    while (matcher.find()) {
      lne.add(matcher.start());
      lne_semicolon.add(matcher.start());
    }

    comma_semicolon[0] = lne_comma.toArray(new Integer[lne_comma.size()]);
    comma_semicolon[1] = lne_semicolon.toArray(new Integer[lne_semicolon.size()]);
    lne.sort((i1, i2) -> {
      return (i1 - i2);
    });
    return lne.toArray(new Integer[lne.size()]);
  }

  /**
   * 看nesTarget数组是否有元素在ne1和ne2之间，有的话，返回位置信息；没有返回null
   */
  public static Integer[] positionsBetween(NamedEntity ne1, NamedEntity ne2,
      NamedEntity[] nesTarget) {
    Integer[] positions = null;
    List<Integer> integerList = new ArrayList<>();
    int left = ne1.getOffset();
    int right = ne2.getOffset();
    if (left >= right)
      return null;
    for (NamedEntity ne : nesTarget) {
      if (ne.getOffset() <= ne1.getOffset()) {
        continue;
      } else if (ne.getOffset() >= ne2.getOffset())
        break;
      else {
        integerList.add(ne.getOffset());
      }
    }
    if (integerList.size() == 0)
      return null;
    positions = integerList.toArray(new Integer[integerList.size()]);
    return positions;
  }

  /**
   * 同上一个函数，只是参数变成整形数组
   * 
   * @param ne1
   * @param ne2
   * @param intTarget
   * @return
   */
  public static Integer[] positionsBetween(NamedEntity ne1, NamedEntity ne2, Integer[] intTarget) {
    Integer[] positions = null;
    List<Integer> integerList = new ArrayList<>();
    int left = ne1.getOffset();
    int right = ne2.getOffset();
    if (left >= right)
      return positions;
    for (int pos : intTarget) {
      if (pos <= ne1.getOffset())
        continue;
      else if (pos >= ne2.getOffset())
        break;
      else {
        integerList.add(pos);
      }
    }
    if (integerList.size() == 0)
      return positions;
    positions = integerList.toArray(new Integer[integerList.size()]);
    return positions;
  }

  /**
   * 合并多个命名实体数组并去掉重复的，以及排序
   * 
   * @param roles 不同的实体数组对应的角色
   * @param all 实体数组列表
   * @return
   */
  public static NamedEntity[] combineEntities(String[] roles, NamedEntity[]... all) {
    List<NamedEntity> lnes = new LinkedList<>();
    int i = 0;
    boolean flag = true;
    if (roles == null || roles.length == 0)
      flag = false;
    for (NamedEntity[] nes : all) {
      if (flag) {
        for (NamedEntity ne : nes) {
          if (null != roles[i])
            ne.setType(roles[i]);
        }
      }
      for (NamedEntity ele : nes) {
        while (lnes.size() <= ele.offset + 1) {
          lnes.add(null);
        }
        if (lnes.get(ele.offset) == null) {
          lnes.remove(ele.offset);
          lnes.add(ele.offset, ele);
        } else {
          if (lnes.get(ele.offset).source.length() >= ele.source.length()) {
            lnes.add(ele.offset + 1, ele);
          } else {
            lnes.add(ele.offset, ele);
          }
        }
      }
      i++;
    }
    if (lnes.size() > 0) {
      for (int j = lnes.size() - 1; j >= 0; j--) {
        if (lnes.get(j) == null) {
          lnes.remove(j);
        }
      }
    }
    return dropDuplicates(lnes);
  }

  // 7.19cywei新重载的，可以考虑与上面的那个的统一
  public static NamedEntity[] combineEntities(String[] roles, ArrayList<NamedEntity[]> all) {
    List<NamedEntity> lnes = new ArrayList<>();
    int i = 0;
    boolean flag = true;
    if (roles == null || roles.length == 0)
      flag = false;
    for (NamedEntity[] nes : all) {
      if (flag) {
        for (NamedEntity ne : nes) {
          if (null != roles[i]) {
            ne.setType(roles[i]);
          }
        }
      }
      lnes.addAll(Arrays.asList(nes));
      i++;
    }

    lnes.sort((ne1, ne2) -> {
      if (ne1.offset != ne2.getOffset())
        return ne1.offset - ne2.offset;
      else
        return ne2.source.length() - ne1.source.length();
    });
    return dropDuplicates(lnes);
  }

  /**
   * 根据roles来分割实体
   * 
   * @param nes
   * @return
   */
  public static List<List<NamedEntity>> splitEntityByType(NamedEntity[] nes,
      Map<String, Integer> conditions) {
    List<List<NamedEntity>> result = new ArrayList<>();
    Map<String, Integer> currentCondition = new HashMap<>();
    for (NamedEntity ne : nes) {
      // 开始条件
      if (result.size() == 0) {
        result.add(new ArrayList<>());
        currentCondition = new HashMap<>();
        if (conditions.keySet().contains(ne.getType())) {
          // 更新新的条件
          currentCondition.put(ne.getType(), 1);
        }
      } else {
        if (conditions.keySet().contains(ne.getType())) {
          int count = currentCondition.getOrDefault(ne.getType(), 0);
          currentCondition.put(ne.getType(), count + 1);
          if (count + 1 > conditions.get(ne.getType())) {
            result.add(new ArrayList<>());
            currentCondition = new HashMap<>();
            // 更新新的条件
            currentCondition.put(ne.getType(), 1);
          }
        }
      }

      result.get(result.size() - 1).add(ne);
    }
    return result;
  }

  /**
   * 根据实体的类别Type,重新组合实体并放到一个Map里，Key为类型，Value为该类型下面的实体数组
   * 
   * @param lnes
   * @return
   */
  public static Map<String, NamedEntity[]> regroupEntities(List<NamedEntity> lnes) {
    Map<String, List<NamedEntity>> strArrayMap = new HashMap<>();
    for (NamedEntity ne : lnes) {
      List<NamedEntity> tmp;
      if (strArrayMap.containsKey(ne.getType()))
        tmp = strArrayMap.get(ne.getType());
      else {
        strArrayMap.put(ne.getType(), new ArrayList<>());
        tmp = strArrayMap.get(ne.getType());
      }
      tmp.add(ne);
    }
    Map<String, NamedEntity[]> result = new HashMap<>();
    for (Map.Entry<String, List<NamedEntity>> entry : strArrayMap.entrySet()) {
      NamedEntity[] tmp = entry.getValue().toArray(new NamedEntity[entry.getValue().size()]);
      result.put(entry.getKey(), tmp);
    }
    return result;
  }

  public Map<String, List<NamedEntity>> decomposeEntities(List<NamedEntity> lne) {
    Map<String, List<NamedEntity>> result = new HashMap<>();
    for (NamedEntity ne : lne) {
      if (null != ne.getType()) {
        List<NamedEntity> tmp = result.getOrDefault(ne.getType(), new ArrayList<>());
        tmp.add(ne);
      }
    }
    return result;
  }

  /**
   * 去掉重复的命名实体
   * 
   * @param nes sorted
   */
  public static NamedEntity[] dropDuplicates(List<NamedEntity> nes) {
    List<NamedEntity> lne = new ArrayList<>();
    NamedEntity prev = null;
    for (NamedEntity ne : nes) {
      if (prev != null) {
        if (ne.getOffset() + ne.getSource().length() > prev.getOffset()
            + prev.getSource().length()) {
          lne.add(ne);
          prev = ne;
        }
      } else {
        lne.add(ne);
        prev = ne;
      }
    }
    NamedEntity[] result = lne.toArray(new NamedEntity[lne.size()]);
    return result;
  }

  /**
   * 去掉重复的并去掉很有可能是时间的长度
   * 
   * @param nes 排序好的时间长度实体
   * @param unitFlag 时间实体的单位
   */
  private static NamedEntity[] dropDuplicatesAndFilterDates(List<NamedEntity> nes,
      String unitFlag) {
    List<NamedEntity> lne = new ArrayList<>();
    NamedEntity prev = null;
    for (NamedEntity ne : nes) {
      if (prev != null) {
        if (ne.getOffset() + ne.getSource().length() > prev.getOffset()
            + prev.getSource().length()) {
          if (((double[]) ne.getInfo())[0] < 1900) {
            lne.add(ne);
            prev = ne;
            double[] dates = (double[]) ne.getInfo();
            double result;
            if (unitFlag.equals("年")) {
              result = (dates[0] + dates[1] / 12 + dates[2] / 360);
            } else if (unitFlag.equals("月")) {
              result = (dates[0] * 12 + dates[1] + dates[2] / 30);
            } else {
              result = (dates[0] * 360 + dates[1] * 30 + dates[2]);
            }
            ne.setInfo(result);
          }
        }
      } else {
        if (((double[]) ne.getInfo())[0] < 1900) {
          lne.add(ne);
          prev = ne;
          double[] dates = (double[]) ne.getInfo();
          double result;
          if (unitFlag.equals("年")) {
            result = (dates[0] + dates[1] / 12 + dates[2] / 360);
          } else if (unitFlag.equals("月")) {
            result = (dates[0] * 12 + dates[1] + dates[2] / 30);
          } else {
            result = (dates[0] * 360 + dates[1] * 30 + dates[2]);
          }
          ne.setInfo(result);
        }
      }
    }
    NamedEntity[] result = lne.toArray(new NamedEntity[lne.size()]);
    return result;
  }

  /**
   * 去掉重复的并且把相邻的实体合并
   * 
   * @param nes 排序好的实体数组
   */
  public NamedEntity[] dropDuplicatesAndConcateNeighbor(List<NamedEntity> nes) {
    LinkedList<NamedEntity> lne = new LinkedList<>();
    NamedEntity prev = null;
    for (NamedEntity ne : nes) {
      if (prev != null) {
        if (ne.getOffset() + ne.getSource().length() > prev.getOffset()
            + prev.getSource().length()) {
          if (ne.getOffset() == prev.getOffset() + prev.getSource().length()) {
            NamedEntity tmp =
                new NamedEntity(lne.getLast().getSource() + ne.getSource(), prev.getOffset(), null);
            lne.addLast(tmp);
          }
          lne.add(ne);
          prev = ne;
        }
      } else {
        lne.add(ne);
        prev = ne;
      }
    }
    NamedEntity[] result = lne.toArray(new NamedEntity[lne.size()]);
    return result;
  }

  /**
   * 如果两个命名实体之间有标点返回true，否则返回false
   * 
   * @param ne1 命名实体1
   * @param ne2 命名实体2
   * @param nesComma 标点符号位置
   */
  public static boolean betweenTwoCommas(NamedEntity ne1, NamedEntity ne2, Integer[] nesComma) {

    int pos1 = binarySearch(nesComma, ne1.offset);
    int pos2 = binarySearch(nesComma, ne2.offset);
    return pos1 != pos2 ? true : false;
  }

  /**
   * 功能：要求leftNes必须出现在rightNes的左边，并符合参数条件的相匹配的命名实体对数组
   * 
   * @param leftNes 排序好的命名实体数组1
   * @param rightNes 排序好的命名实体数组2
   * @param commas 两个命名实体所属句子中的逗号集合
   ** @param noCommaBetween 是否允许相匹配的两实体间有标点符号
   * @return 配对好的命名实体对数组
   */
  public static List<NamedEntity[]> entityMatchWithLRorder(NamedEntity[] leftNes,
      NamedEntity[] rightNes, Integer[] commas, boolean noCommaBetween) {
    List<NamedEntity[]> lnes = new ArrayList<>();

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = 0; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      for (int j = 0; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 如果左边的实体位置小于右边
        if (left.offset <= right.offset) {
          // 如果两实体中间不能有标点符号并且确实这两个实体中间没有标点符号
          if (noCommaBetween && (!betweenTwoCommas(left, right, commas))) {
            lnes.add(new NamedEntity[] {left, right});
          } else if (!noCommaBetween) { // 如果不要求两实体中间不能有标点符号
            lnes.add(new NamedEntity[] {left, right});
          }
        }
      }
    }

    return lnes;
  }

  /**
   * 功能：符合参数条件的相匹配的命名实体对数组
   * 
   * @param leftNes 排序好的命名实体数组1
   * @param rightNes 排序好的命名实体数组2
   * @param commas 两个命名实体所属句子中的逗号集合
   ** @param noCommaBetween 是否允许相匹配的两实体间有标点符号
   * @return 配对好的命名实体对数组
   */
  public static List<NamedEntity[]> entityMatchWithNOorder(NamedEntity[] leftNes,
      NamedEntity[] rightNes, Integer[] commas, boolean noCommaBetween) {
    List<NamedEntity[]> lnes = new ArrayList<>();

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = 0; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      for (int j = 0; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 如果两实体中间不能有标点符号并且确实这两个实体中间没有标点符号
        if (noCommaBetween && (!betweenTwoCommas(left, right, commas))) {
          lnes.add(new NamedEntity[] {left, right});
        } else if (!noCommaBetween) { // 如果不要求两实体中间不能有标点符号
          lnes.add(new NamedEntity[] {left, right});
        }
      }
    }

    return lnes;
  }

  /**
   * 两个命名实体配对，找nes2每个元素最左边的nes1元素
   * 
   * @param leftNes 排序好的命名实体数组1
   * @param rightNes 排序好的命名实体数组2
   * @param NO_COMMA_BETWEEN 是否排除配对好的命名实体对，如果中间有标点符号
   * @return 配对好的命名实体对数组
   */
  public static List<NamedEntity[]> entityMatch(String s, NamedEntity[] leftNes,
      NamedEntity[] rightNes, boolean NO_COMMA_BETWEEN, boolean ADD_RIGHT) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    Integer[] nesCommas = new Integer[0];
    if (NO_COMMA_BETWEEN) {
      nesCommas = recognizeComma(s);
    }
    int startLeft = 0, startRight = 0;

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];

      int pos = -1, posRight = -1;
      for (int j = startLeft; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 如果左边的元素位置小于右边，继续遍历
        if (left.offset <= right.offset) {
          pos = j;
        } else {
          posRight = j;
          break;
        }
      }

      // if find
      if (pos != -1) {
        if (NO_COMMA_BETWEEN && betweenTwoCommas(leftNes[pos], right, nesCommas)) {
          // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
        } else {
          lnes.add(new NamedEntity[] {leftNes[pos], right});
        }
        // 更新左边的起始顺序
        startLeft = pos + 1;
      }
      // 全部找完了
      // 表示当前右边的元素一个也找不到在它左边的左边元素了
      else {
        if (ADD_RIGHT && posRight != -1) {
          if (!betweenTwoCommas(leftNes[posRight], right, nesCommas)) {
            lnes.add(new NamedEntity[] {leftNes[posRight], right});
            startLeft = posRight + 1;
          }
        }
        // 如果不能添加在同句的右边或者在右边里面没有找到句子
        else {
          // 右边的元素一个也没有找到在它左边的元素，直接检查下一个右边元素，左边元素的初始位置不变
          continue;
          // break;
        }
      }
    }
    return lnes;
  }

  /**
   * 两个命名实体配对，找nes2每个元素最左边的nes1元素
   * 
   * @param nesCommas 标点符号位置
   * @param leftNes 排序好的命名实体数组1
   * @param rightNes 排序好的命名实体数组2
   * @param noCommaBetwen 是否排除配对好的命名实体对，如果中间有标点符号
   * @return 配对好的命名实体对数组
   */
  // 人物在正则之前
  public static List<NamedEntity[]> entityMatch(String s, Integer[] nesCommas,
      NamedEntity[] leftNes, NamedEntity[] rightNes, boolean noCommaBetwen, boolean addRight) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    int startLeft = 0, startRight = 0;

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      int pos;
      for (int j = startLeft; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 左边的元素位置小于右边
        if (left.offset <= right.offset) {
          pos = j;
          if (noCommaBetwen && betweenTwoCommas(leftNes[pos], right, nesCommas)) {
            // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
          } else {
            lnes.add(new NamedEntity[] {leftNes[pos], right});
          }
        } else {
          break;
        }
      }
    }
    return lnes;
  }

  // 正则在人物之前
  public static List<NamedEntity[]> entityMatchReverse(String s, Integer[] nesCommas,
      NamedEntity[] leftNes, NamedEntity[] rightNes, boolean noCommaBetwen, boolean addRight) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    int startLeft = 0, startRight = 0;

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      int pos = -1, posRight = -1;
      for (int j = startLeft; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 左边的元素位置小于右边
        if (left.offset >= right.offset) {
          pos = j;
        } else {
          posRight = j;
          break;
        }
      }

      // if find
      if (pos != -1) {
        // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
        if (noCommaBetwen && betweenTwoCommas(leftNes[pos], right, nesCommas)) {
          // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
        } else {
          lnes.add(new NamedEntity[] {leftNes[pos], right});
        }
        // 更新左边的起始顺序
        startLeft = pos + 1;
      }
      // 全部找完了
      // 表示当前右边的元素一个也找不到在它左边的左边元素了
      else {
        if (addRight && (posRight != -1)) {
          if (!betweenTwoCommas(leftNes[posRight], right, nesCommas)) {
            lnes.add(new NamedEntity[] {leftNes[posRight], right});
            startLeft = posRight + 1;
          }
        } else {
          // 右边的元素一个也没有找到在它左边的元素，直接检查下一个右边元素，左边元素的初始位置不变
          continue;
          // break;
        }
      }
    }

    return lnes;
  }

  // 添加的为List集合，判断人物在正则之前，该信息属于该人物，放入集合中
  public static List<NamedEntity[]> entityMuchMatch(String s, Integer[] nesCommas,
      NamedEntity[] leftNes, NamedEntity[] rightNes, boolean noCommaBetwen, boolean addRight) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    int startLeft = 0, startRight = 0;

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      int pos;
      for (int j = startLeft; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 左边的元素位置小于右边
        if (left.offset <= right.offset) {
          pos = j;
          // if find
          // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
          if (noCommaBetwen && betweenTwoCommas(leftNes[pos], right, nesCommas)) {
            // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
          } else {
            lnes.add(new NamedEntity[] {leftNes[pos], right});
          }
        } else {
          break;
        }
      }
    }

    return lnes;
  }

  public static Map<String, List<Object>> entityMatchSentence(String s, Integer[] nesCommas,
      NamedEntity[] leftNes, NamedEntity[] rightNes, boolean noCommaBetwen, boolean addRight) {
    Map<String, List<Object>> result = new HashMap<>();
    int startLeft = 0, startRight = 0;

    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      int pos;
      for (int j = startLeft; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 左边的元素位置小于右边
        if (left.offset <= right.offset) {
          pos = j;
          // if find
          // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
          if (noCommaBetwen && betweenTwoCommas(leftNes[pos], right, nesCommas)) {
            // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
          } else {
            LitigantUnit lu = (LitigantUnit) leftNes[j].getInfo();
            String[] names = lu.getNames();
            for (String name : names) {
              List<Object> tmpList;
              if (!result.containsKey(name)) {
                tmpList = new ArrayList<>();
                result.put(name, tmpList);
              }
              tmpList = result.get(name);
              if (!tmpList.contains(s)) {
                result.get(name).add(s);
              }
            }
          }
        } else {
          break;
        }
      }
    }
    return result;
  }

  // 7.19重载的，可以考虑与上面那个的统一
  public static List<NamedEntity[]> entityMatch(Integer[] nesCommas, NamedEntity[] leftNes,
      NamedEntity[] rightNes, boolean noCommaBetwen) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    int startLeft = 0, startRight = 0;
    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      ArrayList<Integer> arrPos = new ArrayList<>();
      for (int j = startLeft; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 左边的元素位置小于右边
        if (left.offset <= right.offset) {
          if (noCommaBetwen && (!betweenTwoCommas(left, right, nesCommas))) {
            arrPos.add(j);
          }
        } else {
          break;
        }
      }
      if (arrPos.size() > 0) {
        NamedEntity[] tmp = new NamedEntity[arrPos.size() + 1];
        tmp[0] = right;
        for (int k = 0; k < arrPos.size(); k++) {
          tmp[k + 1] = leftNes[arrPos.get(k)];
          startLeft = arrPos.get(k) + 1;
        }
        lnes.add(tmp);
      }
    }

    return lnes;
  }

  /**
   * 两个命名实体配对，找nes2每个元素最左边的nes1元素 返回配对好的命名实体对数
   * 
   * @param nesCommas 标点符号位置
   * @param leftNes 排序好的命名实体数组1
   * @param rightNes 排序好的命名实体数组2
   * @param NO_COMMA_BETWEEN 是否排除配对好的命名实体对，如果中间有标点符号
   * @return 配对好的命名实体对数组
   */
  public static List<NamedEntity[]> entityMatch(Integer[] nesCommas, List<NamedEntity[]> leftNes,
      List<NamedEntity[]> rightNes, boolean NO_COMMA_BETWEEN, boolean ADD_RIGHT) {

    List<NamedEntity[]> lnes = new ArrayList<>();
    int startLeft = 0, startRight = 0;
    int size = leftNes.size() > 0 && rightNes.size() > 0
        ? leftNes.get(0).length + rightNes.get(0).length : 0;
    if (size == 0)
      return lnes;
    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.size(); i++) {
      NamedEntity right = rightNes.get(i)[0];
      int pos = -1, posRight = -1;
      for (int j = startLeft; j < leftNes.size(); j++) {
        NamedEntity left = leftNes.get(j)[leftNes.get(0).length - 1];
        // 如果左边的元素位置小于右边，继续遍历
        if (left.offset <= right.offset) {
          pos = j;
        } else {
          posRight = j;
          break;
        }
      }

      // if find
      if (pos != -1) {
        if (NO_COMMA_BETWEEN
            && betweenTwoCommas(leftNes.get(pos)[leftNes.get(0).length - 1], right, nesCommas)) {
          // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
        } else {
          int start_pos = pos;
          for (int l = start_pos; l <= pos; l++) {
            NamedEntity[] nes = new NamedEntity[size];
            for (int k = 0; k < leftNes.get(pos).length; k++) {
              nes[k] = leftNes.get(pos)[k];
            }
            for (int k = leftNes.get(pos).length; k < size; k++) {
              nes[k] = rightNes.get(i)[k - leftNes.get(pos).length];
            }
            lnes.add(nes);
          }
        }
        // 更新左边的起始顺序
        startLeft = pos + 1;
      }
      // 全部找完了
      // 表示当前右边的元素一个也找不到在它左边的左边元素了
      else {
        if (ADD_RIGHT && posRight != -1) {
          if (!betweenTwoCommas(leftNes.get(posRight)[leftNes.get(0).length - 1], right,
              nesCommas)) {

            NamedEntity[] nes = new NamedEntity[size];
            for (int k = 0; k < leftNes.get(posRight).length; k++) {
              nes[k] = leftNes.get(posRight)[k];
            }
            for (int k = leftNes.get(posRight).length; k < size; k++) {
              nes[k] = rightNes.get(i)[k - leftNes.get(posRight).length];
            }
            lnes.add(nes);
            startLeft = posRight + 1;
          }
        } else {
          // 右边的元素一个也没有找到在它左边的元素，直接检查下一个右边元素，左边元素的初始位置不变
          continue;
          // break;
        }
      }
    }
    return lnes;
  }

  // 支持左边的实体匹配多个右边的，比如：本院先后于2008年11月、2010年11月、2012年6月裁定对其分别减去有期徒刑一年一个月、一年八个月、一年四个月。
  public static List<NamedEntity[]> entityMatchSupportOneToMore(Integer[] commas,
      NamedEntity[] leftNes, NamedEntity[] rightNes, boolean noCommaBetwen) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = 0; i < rightNes.length; i++) {
      NamedEntity right = rightNes[i];
      for (int j = 0; j < leftNes.length; j++) {
        NamedEntity left = leftNes[j];
        // 左边的元素位置小于右边
        if (left.offset <= right.offset) {
          if (noCommaBetwen && !(betweenTwoCommas(left, right, commas))) {
            lnes.add(new NamedEntity[] {left, right});
          }
        } else {
          break;
        }
      }
    }
    return lnes;
  }

  /**
   * 对右边的元素，添加所有在其左边的元素或者在右边但是在同一个句子里。
   */
  public static List<NamedEntity[]> entityMatchAllLeft(String s, Integer[] nesCommas,
      List<NamedEntity[]> leftNes, List<NamedEntity[]> rightNes, boolean NO_COMMA_BETWEEN,
      boolean ADD_RIGHT) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    int startLeft = 0, startRight = 0;
    int size = leftNes.size() > 0 && rightNes.size() > 0
        ? leftNes.get(0).length + rightNes.get(0).length : 0;
    if (size == 0)
      return lnes;
    // 对于每一个右边的元素，遍历左边的元素进行配对
    for (int i = startRight; i < rightNes.size(); i++) {
      NamedEntity right = rightNes.get(i)[0];
      int pos = -1, posRight = -1;
      for (int j = startLeft; j < leftNes.size(); j++) {
        NamedEntity left = leftNes.get(j)[leftNes.get(0).length - 1];
        // 如果左边的元素位置小于右边，继续遍历
        if (left.offset <= right.offset) {
          pos = j;
        } else {
          posRight = j;
          break;
        }
      }
      int start_pos = pos;
      if (pos == -1 && posRight == -1)
        break;
      else if (pos == -1) {
        if (betweenTwoCommas(leftNes.get(posRight)[leftNes.get(0).length - 1], right, nesCommas)) {
          continue;
        }
        start_pos = posRight;
      } else if (posRight == -1) {
        start_pos = pos;
      } else {
        if (betweenTwoCommas(leftNes.get(posRight)[leftNes.get(0).length - 1], right, nesCommas)) {
          start_pos = pos;
        } else
          start_pos = posRight;
      }

      // if find
      if (start_pos != -1) {
        if (betweenTwoCommas(leftNes.get(start_pos)[leftNes.get(0).length - 1], right, nesCommas)) {
          // 如果中间不能有标点符号并且确实这两个实体中间没有标点符号，则不添加到结果
        } else {
          // 如果可以添加到右边 或者 只能添加到左边，标号是左边的标号
          if (ADD_RIGHT || (!ADD_RIGHT && start_pos == pos)) {
            for (int l = startLeft; l <= start_pos; l++) {
              // 添加 start_pos - startLeft + 1 各元素
              // 每个元素都是 size 维度
              NamedEntity[] nes = new NamedEntity[size];
              for (int k = 0; k < leftNes.get(l).length; k++) {
                nes[k] = leftNes.get(l)[k];
              }
              for (int k = leftNes.get(l).length; k < size; k++) {
                nes[k] = rightNes.get(i)[k - leftNes.get(l).length];
              }
              // 判断左边的这个元素是不是中间有标点
              if (!NO_COMMA_BETWEEN
                  || !(NO_COMMA_BETWEEN && betweenTwoCommas(nes[leftNes.get(l).length - 1],
                      nes[leftNes.get(l).length], nesCommas))) {
                lnes.add(nes);
              }
            }
          }
        }
        // 更新左边的起始顺序
        startLeft = start_pos + 1;
      }
    }

    return lnes;
  }

  /**
   * 对单词进行后处理，若果某个单词中间包含由某些特定字符（这些字符的位置由Integer[] a 定义），那么单词结尾被最近的那个字符截断 比如
   * 婚生子女王小三、王大三，如果提取的单词为"王小三、王大三，"，通过定义",、"的位置，最后输出为 "王小三"
   * 
   * @param s 待处理字符串
   * @param a 位置
   */
  public static String cutoffWords(String s, Integer[] a, String word, int offset) {
    int start = binarySearch(a, offset);
    word = word.replaceAll("[(（][\\u4e00-\\u9fa5\\d]*?[)）]", "");
    int end = binarySearch(a, offset + word.length());
    String result = word;
    if (end > start && a[end] > offset) {
      // 说明跨了几个
      return s.substring(offset, a[start + 1]);
    }
    return result;
  }

  /**
   * 返回数组中key的位置 数组中的位置， 比如 [1,4,6,7] key = 5 则位置在2，(4和6中间) key = 4 则位置为2 (4) key = 0 位置为 -1 key = 7
   * 位置为3 key = 8 位置为3
   * 
   * @param a 排序好的数组
   * @param key 要查找的数
   * @return 数组中的位置
   */
  public static int binarySearch(Integer[] a, int key) {

    int size = a.length;
    if (size == 0)
      return -1;
    int left = 0, right = size - 1, middle = (left + right) / 2; // 偶数个靠左边，奇数个中间
    if (key < a[left])
      return -1;
    if (key >= a[right])
      return right;
    while (left < right) {

      // 先判断是否在边界上
      if (key == a[left])
        return left;
      if (key == a[right])
        return right;
      // 判断左右边界已经相邻
      if (left + 1 == right)
        return left;
      if (a[middle] == key)
        return middle;
      else if (a[middle] > key) {
        right = middle;
        middle = (left + right) / 2;
      } else {
        left = middle;
        middle = (left + right) / 2;
      }
    }
    return left;
  }

  /**
   * 数组中key的位置，同上，输入参数不同 返回数组中的位置，比如 [1,4,6,7] key = 5 则位置在2，(4和6中间) key = 4 则位置为2 (4) key= 0 位置为 -1
   * key = 7 位置为3 key = 8 位置为3
   * 
   * @param a 排序好的数组
   * @param key 要查找的数
   * @return 数组中的位置
   */
  public static int binarySearch(NamedEntity[] a, int key) {

    int size = a.length;
    if (size == 0)
      return -1;
    int left = 0, right = size - 1, middle = (left + right) / 2; // 偶数个靠左边，奇数个中间
    if (key < a[left].offset)
      return -1;
    if (key >= a[right].offset)
      return right;
    while (left < right) {

      // 先判断是否在边界上
      if (key == a[left].offset)
        return left;
      if (key == a[right].offset)
        return right;
      // 判断左右边界已经相邻
      if (left + 1 == right)
        return left;
      if (a[middle].offset == key)
        return middle;
      else if (a[middle].offset > key) {
        right = middle;
        middle = (left + right) / 2;
      } else {
        left = middle;
        middle = (left + right) / 2;
      }
    }
    return left;
  }

  /**
   * 转换格式，NamedEntity[] -》List<NamedEntity[]>，其中list里每一个数组都只有一维，里面的元素就是原数组里面的每个元素
   */
  public static List<NamedEntity[]> changeFormat(NamedEntity[] nes) {
    List<NamedEntity[]> lnes = new ArrayList<>();
    for (NamedEntity ne : nes) {
      lnes.add(new NamedEntity[] {ne});
    }
    return lnes;
  }

  /**
   * 根据传入的字符串，按顺序打印实体，方便调试找规律以及后续机器学习样本标注 比如句子
   * “2015年3月1日，被告王XX和与昂高张某某来到合肥市蜀山区人民法院进行调解，次日原被告不满调解结果又来到该院” 定义三组实体 NamedEntity[] (time),
   * NamedEntity[] (Litigant), NamedEntity[] (court), 如果定义打印的字符串为 String[] entityNames =
   * {"time","litigant","court"},则打印出来的为 "time litigant litigant court time litigant court"
   * 
   * @param sentence 待识别的句子
   * @param entityNames 要打印出来的名字
   * @param nes 实体
   */
  public static String printEntityPositions(String sentence, String[] entityNames,
      NamedEntity[]... nes) {
    List<Object[]> typePositions = new ArrayList<>();

    for (int i = 0; i < nes.length; i++) {
      for (NamedEntity ne : nes[i]) {
        typePositions.add(new Object[] {entityNames[i], ne.getOffset()});
      }
    }
    typePositions.sort(new Comparator<Object[]>() {
      @Override
      public int compare(Object[] o1, Object[] o2) {
        return (int) o1[1] - (int) o2[1];
      }
    });

    String s = "";
    for (Object[] objs : typePositions) {
      s += (String) objs[0] + "\t";
    }

    return s;
  }

  /**
   * 改变数据格式，从Integer[] 到 NamedEntity[]
   * 
   * @param commas 输入数据格式
   */
  public static NamedEntity[] convertCommasFormat(Integer[] commas) {
    NamedEntity[] nes = new NamedEntity[commas.length];
    for (int i = 0; i < nes.length; i++) {
      nes[i] = new NamedEntity(",", commas[i], null);
    }
    return nes;
  }

  /**
   * 查看列表里是否有某个元素类型
   * 
   * @param types 要核对的类型集合
   * @param orCondition 如果为True，表示要满足：全集里的至少一个元素的类型是类型集合里面的一个；如果为false,
   *        要满足：全集含有的元素类型要涵盖类型集合里面的所有元素类型
   * @param lne 全量的实体
   */
  public boolean containsType(String[] types, boolean orCondition, List<NamedEntity> lne) {
    if (orCondition) {
      for (NamedEntity ne : lne) {
        for (String type : types) {
          if (type.equals(ne.getType()))
            return true;
        }
      }
      return false;
    } else {
      Set<NamedEntity> sne = new HashSet<>(lne);
      // 对每一类型遍历，开始默认没有遍历到，如果有找到，则设置为true，同时去掉这个元素，对下个元素遍历。
      for (String type : types) {
        boolean flag = false;
        for (NamedEntity ne : sne) {
          if (type.equals(ne.getType())) {
            flag = true;
            sne.remove(ne);
            break;
          }
        }
        if (!flag)
          return false;
      }
      return true;
    }
  }

  /**
   * 根据类型找实体
   * 
   * @param type 当事人类型
   * @param lne 待过滤的实体全集
   * @return 找到的第一个满足要求的实体
   */
  public static NamedEntity findOneEntityByType(String type, List<NamedEntity> lne) {
    for (NamedEntity ne : lne) {
      if (type.equals(ne.getType()))
        return ne;
    }
    return null;
  }

  /**
   * 根据类型找实体
   * 
   * @param type 当事人类别
   * @param lne 待过滤的实体全集
   * @return 找到的所有满足要求的实体
   */
  public List<NamedEntity> findAllEntityByType(String type, List<NamedEntity> lne) {

    List<NamedEntity> result = new ArrayList<>();
    for (NamedEntity ne : lne) {
      if (type.equals(ne.getType()))
        result.add(ne);
    }
    return result;
  }

  /**
   * 先通过本句里面传过来的法院做指代消解，如果没有，用上一句最后一个法院名字做指代消解 如果都没有找到，则扔掉“该院”这个实体
   * 
   * @param nes_ref 代表 “该院”等指示代词
   * @param nes_target 本句子里面其他解析出来的法院
   * @param ne_last 上一句最后一个解析出来的法院
   * @return 识别结果
   */
  private static NamedEntity[] coreferenceResolutionWithNearestEntity(NamedEntity[] nes_ref,
      NamedEntity[] nes_target, NamedEntity ne_last) {

    int nearest = 0;
    List<NamedEntity> lne = new ArrayList<>();
    for (NamedEntity aNes_ref : nes_ref) {
      int ref_offset = aNes_ref.getOffset();
      boolean findOne = false;
      for (int j = nes_target.length - 1; j >= nearest; j--) {
        if (nes_target[j].getOffset() < ref_offset) {
          nearest = j;
          findOne = true;
          break;
        }
      }
      if (findOne) {
        aNes_ref.setInfo(nes_target[nearest].getInfo());
        lne.add(aNes_ref);
      } else {
        if (null != ne_last) {
          aNes_ref.setInfo(ne_last.getInfo());
          lne.add(aNes_ref);
        }
      }
    }
    Collections.addAll(lne, nes_target);
    lne.sort((o1, o2) -> o1.getOffset() - o2.getOffset());
    return lne.toArray(new NamedEntity[lne.size()]);
  }

  /**
   * 仅仅识别一种当事人实体，被告或者原告
   */
  public static NamedEntity[] recognizeLitigantByType(String s, String litigantType,
      LitigantRecognizer lr) {
    LitigantUnit[] lus = lr.nameRecognize(s);
    if (lus == null || lus.length == 0)
      return new NamedEntity[0];
    List<NamedEntity> lne = new ArrayList<>();
    for (LitigantUnit lu : lus) {
      if (litigantType.equals(lu.getLabel()))
        lne.add(new NamedEntity(lu.getExpression(), lu.getOffset(), lu));
    }
    return lne.toArray(new NamedEntity[lne.size()]);
  }

  public static String subStringUntilComma(String s, NamedEntity ne, Integer[] lints) {
    String word;
    if (lints.length == 0) {
      word = s.substring(ne.offset, s.length());
    } else {
      int pos = binarySearch(lints, ne.offset + ne.source.length());
      if (pos == lints.length - 1) {
        word = s.substring(ne.offset, s.length());
      } else {
        word = s.substring(ne.offset, lints[pos + 1]);
      }
    }
    Matcher matcher = Pattern.compile("\\d+").matcher(word);
    if (matcher.find()) {
      word = word.substring(0, matcher.start());
    }
    return word;
  }
}
