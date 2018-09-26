package com.ocp.rabbit.repository.algorithm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.tool.algorithm.personage.NameWrapper;
import com.ocp.rabbit.repository.tool.algorithm.personage.NameTypeConstants;
import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;

/**
 * 名称类型识别器
 * 
 * @author yu.yao 2018年8月14日
 *
 */
public class NameTypeRecognizer extends NameTypeConstants {
  // public static final int
  static String whitespace = "[“”\"\\s\\xa0\\u3000\\u007F\t　]+";
  static String namesplitter = "[，,、\\s\\xa0\\u3000\t　]+";

  static String bracket = "[\\(（\\[［][^\\(（\\[［\\)）\\]］]+[\\)）\\]］]";
  static Pattern p_bracket = Pattern.compile(bracket);

  // 可能的Pattern: 等，等人，等被告，等4人，等2被告，等五人
  static String multiFeature = "(.*?)等共?(?:[\\d两二三四五六七八九十]+)?(?:人|名|被告)?$";
  static Pattern p_multiFeature = Pattern.compile(multiFeature);
  static String multiFeature2 = "(.*?)共?[\\d两二三四五六七八九十]+人$";
  static Pattern p_multiFeature2 = Pattern.compile(multiFeature2);

  static String withCloseCh = "(.*?)(.)[\\(（]([\u4e00-\u9fa5])[\\)）](.*)";
  static Pattern p_withCloseCh = Pattern.compile(withCloseCh);

  static String startWithNonChineseCh = "^[^\u4e00-\u9fa5]";
  static Pattern p_startWithNonChineseCh = Pattern.compile(startWithNonChineseCh);

  static String startWithEnWord = "^[a-z_\\-\\.]+$";
  static Pattern p_startWithEnWord = Pattern.compile(startWithEnWord, Pattern.CASE_INSENSITIVE);

  static String orgEnFeature = "(limited|ltd|org\\.|corporation|corp\\.|s\\.a\\.?|company)";
  static Pattern p_orgEnFeature = Pattern.compile(orgEnFeature, Pattern.CASE_INSENSITIVE);
  // 全是数字
  static String allDigits = "^\\d+$";
  static Pattern p_allDigits = Pattern.compile(allDigits);

  // 开头和结尾是非法字符或者无，中间是2到4个汉字，则认为是常用汉语名字
  static String commonNames = "^[^\u4e00-\u9fa5]?([\u4e00-\u9fa5\\d×*]{2,4})[^\u4e00-\u9fa5]?$";
  static Pattern p_commonNames = Pattern.compile(commonNames);

  static String minorityNameSplitter = "[\\.。·]";
  static Pattern p_minorityNameSplitter = Pattern.compile(minorityNameSplitter);

  // 匹配“XXX（）”等特征
  static String usedName = "^([\u4e00-\u9fa5]{2,})[\\(（].+[\\)）]$";
  static Pattern p_usedName = Pattern.compile(usedName);

  // 匹配 “刘刚，曾用名刘钢”等字符
  static String usedName2 = "^([\u4e00-\u9fa5]{2,3})(?:，|,|曾用名|又名|外号|小名|绰号)";
  static Pattern p_usedName2 = Pattern.compile(usedName2);

  // 匹配汉字拼音混合型规则 如"李han"
  static String mixedName = "^(?:[a-z]+)?([\u4e00-\u9fa5]{1,3})(?:[a-z]+)?$";
  static Pattern p_mixedName = Pattern.compile(mixedName, Pattern.CASE_INSENSITIVE);

  // 名字后面是数字
  static String nameEndWithDigits = "^(?:\\d+)?([\u4e00-\u9fa5]{2,4})(?:\\d+)?$";
  static Pattern p_nameEndWithDigits = Pattern.compile(nameEndWithDigits);
  // 替换所有非汉字字符
  static String nonChineseCh = "[^\u4e00-\u9fa5]+";


  private static Set<String> org_suffix_words = loadOrgSuffixData();
  private static Map<Character, String> hanzi_pinyin_map = loadHanziPinyinData();

  private static Set<String> loadOrgSuffixData() {
    InputStream org_suffix_path = FileUtils.loadProperties("name.org.suffix");
    List<String> lines = FileOperate.readTxtToArrays(org_suffix_path, "UTF-8");
    Set<String> org_suffix_words = new HashSet<>();
    for (String line : lines) {
      if (line.length() == 2) {
        org_suffix_words.add(line);
      }
    }
    return org_suffix_words;
  }

  private static Map<Character, String> loadHanziPinyinData() {
    InputStream hanzi_pinyin_map_path_is = FileUtils.loadProperties("name.hanzi.pinyin.map");
    List<String> lines = FileOperate.readTxtToArrays(hanzi_pinyin_map_path_is, "UTF-8");
    Map<Character, String> hanzi_pinyin_map = new HashMap<>();
    for (String line : lines) {
      String[] items = line.split("[\\s\t]+");
      if (items.length < 2 || items[0].length() != 1)
        continue;
      char hanzi = items[0].charAt(0);
      hanzi_pinyin_map.put(hanzi, items[1]);
    }
    return hanzi_pinyin_map;
  }

  public void convert2Pinyin(NameWrapper nc) {

    if (nc != null && nc.getNameCleaned() != null) {
      String nameCleaned = nc.getNameCleaned();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < nameCleaned.length(); i++) {
        char c = nameCleaned.charAt(i);
        if (hanzi_pinyin_map.containsKey(c)) {
          sb.append(hanzi_pinyin_map.get(c));
        } else
          sb.append(c);
      }
      nc.setNamePinyin(sb.toString());
    }
  }

  // TODO: 调用getNameContainer,根据不同的输出，判断结果
  // name1表示提取的名字，name2表示tdh名字
  // 算法：根据对tdh名字的分类，决定是否进行比较
  // 如果TDH的名字个数和提取的名字个数相同，不再比较，取提取的名字为准
  // 去除TDH的非法名字后，比较清洗过的名字

  public boolean compareNames(String name1, String name2) {
    return true;
  }

  public String convertPinyin(String name) {
    StringBuffer sb = new StringBuffer();
    for (int index = 0; index < name.length(); index++) {
      char nameChar = name.charAt(index);
      if (hanzi_pinyin_map.containsKey(nameChar)) {
        sb.append(hanzi_pinyin_map.get(nameChar));
      } else
        sb.append(nameChar);
    }
    return sb.toString();
  }
  // TODO: 已解决。常用名字限制为4个字，但是里面会有一些表示ORG的词语，如“网乐网吧”,需要排除掉---->解决方案是参考之前职业的特征词，建立一个排除字典
  // TODO: 已解决。增加非法名字范围，目前仅仅是长度为1的为非法名字 ----> 考虑增加全是数字的也是非法名字
  // TODO: 已解决。"果伟二人"规则添加－－－》mulFeauture2
  // TODO: 已解决。"刘松，曾用名刘收","常兴圣又名常兴胜","井德祥又名井老六",增加对此种规则的覆盖-->usedName2
  // TODO: 以解决。汉字拼音混写的名字目前分在ORG下面，如“鲍来lou”，“zha圣宝”，“li李俊平”，需要增加对此类名字的规则----->考虑添加一个所有拼音的集合，然后去掉这些拼音
  // TODO: 已解决。等人规则里面，需要增加对“等共19人”之类的支持，“崔军伟三人”等的支持，“赵中红等367人”
  // TODO: 已解决。Bug to fix,"戚明林","孙金明"等被错误分到了ORG －－－》空白编码增加 \u007F 编码
  // TODO: 已解决。名字加日期被分到了ORG，如“陈正付1970”,"王娜娜19944"，“豆星海19721117”，增加此类（PERSON）的规则 ---->匹配末尾连续的数字
  // p_nameEndWithDigits
  // TODO: 以解决。"张以l加"－－》去除所有非汉字字符，判断汉字字符个数是否是2或者3
  // TODO: 少数民族的名字5个字或以上如果只有一部分，目前被分到了ORG，需要增加此类名字规则，“阿牛克古木”，“阿苏么日果”
  // TODO: 优化程序，根据这些规则统计不同名字出现在各个规则的数目，调整顺序，使得高频的规则出现在前面，低频的规则出现在后面
  // TODO: 优化程序，先判断开头或结尾不是汉字的名字
  // 匹配顺序
  // 0. 去掉空格和引号
  // 1. 名字长度是否小于1-－》Y：Illeganame; N:转入2
  // 2. 开头不是汉字－－》Y：2.1 N：3
  // 2.1 全是字母（包括“.”和“－”符号 --->Y :2.2 N:2.3
  // 2.2 是否含有英语机构名词关键字（ORG,INC.,LTD.,...) －－》Y: ORG N:PERSON
  // 2.3 除掉开头和结尾非汉字字符，其余字符是否是2-4个汉字 －－》Y：PERSON N：3
  // 3. 字符数小于等于5 且除掉开头和结尾非汉字字符，其余字符是2-4个汉字-->Y: 3.1 N－》4
  // 3.1 能否匹配上“等人”规则且前面字符数大于1 Y：MULPERSON (前面个数为1则为PERSON) N：3.2
  // 3.2 匹配的字符长度等于4且最后两个字是机构名字－－》Y：ORG N: PERSON
  // 4. 匹配上“等人规则”且前面字符数大于1 －－》Y:MULPERSON (前面个数为1则为PERSON) N: 5
  // 5. 字符个数是否是5或者6 －－》Y：5.1 N：6
  // 5.1 匹配上形近字规则名字，如"王小（晓）军"，"张大宝（堡）"此类名字-->Y: PERSON N：6
  // 6. 如果最后面是括号.*(.+) －－》 Y:6.1 N:7
  // 6.1 括号前面字符长度小于等于4 －－》 Y: PERSON_WITH_USEDNAME N:ORG_WITH_USEDNAME
  //
  // 7. 根据特定的符号分割少数民族名字，如果列表长度是2：---> Y:7.1 N:8
  // 7.1 每部分的长度再2到6之间（包括）－－－》 Y：PERSON N:8
  // 8. 根据特定符号分割原始名字，如果长度大于1 －－》Y: 8.1 N:9
  // 8.1 每部分长度都再2和4之间（包括） －－》Y：MULPERSON N:9
  // 9. 替换括号为空，判断生于字符长度小于等于4 －－》 Y：PERSON（为4的时候继续判断是否有ORG关键词） N：10
  // 10. 匹配 “刘刚，曾用名刘钢”等规则 －－》Y:PERSON N: ORG

  // 目前处理速度，100k items/s
  public static NameWrapper getNameWrapper(String rawName) {

    Matcher matcher;
    String name = rawName.replaceAll(whitespace, "");
    // 名字长度小于1，非法名字
    if (name.length() <= 1) {
      return new NameWrapper(ILLEGALNAME, name);
    }

    matcher = p_startWithNonChineseCh.matcher(name);
    // 如果开头不是汉字
    if (matcher.find()) {
      // 全是字母，认为是外国人名，否则认为是非法名字
      matcher = p_startWithEnWord.matcher(name);
      if (matcher.find()) {
        matcher = p_orgEnFeature.matcher(name);
        if (matcher.find()) {
          return new NameWrapper(ORG, name);
        }
        return new NameWrapper(PERSON, name);
      } else {
        // 如果是常用名可以匹配，则认为是人名，否则非法
        matcher = p_commonNames.matcher(name);
        if (matcher.find()) {
          return new NameWrapper(PERSON, matcher.group(1));
        } else {
          matcher = p_allDigits.matcher(name);
          if (matcher.find()) {
            return new NameWrapper(ILLEGALNAME, name);
          }
        }
      }
    }

    // 以下开头都是是汉字
    if (name.length() <= 5) {
      matcher = p_commonNames.matcher(name);
      // 如果匹配上常用汉字名
      if (matcher.find()) {
        String possibleName = matcher.group(1);

        // 如果匹配上等人
        matcher = p_multiFeature.matcher(possibleName);
        boolean atLeastOneMatch = false;
        String name2 = "";
        if (matcher.find()) {
          atLeastOneMatch = true;
          name2 = matcher.group(1);
        } else {
          matcher = p_multiFeature2.matcher(possibleName);
          if (matcher.find()) {
            atLeastOneMatch = true;
            name2 = matcher.group(1);
          }
        }
        // "等人"
        if (atLeastOneMatch) {
          if (name2.length() >= 1) {
            String nameCleaned = name2 + "等";
            return new NameWrapper(PERSON, nameCleaned);
          } else {
            return new NameWrapper(MULPERSON);
          }
        }

        // 如果最后两个字是ORG关键字，则返回ORG
        if (possibleName.length() == 4 && org_suffix_words.contains(possibleName.substring(2))) {
          return new NameWrapper(ORG, possibleName);
        }
        // 都不是前两种情况，返回PERSON
        return new NameWrapper(PERSON, possibleName);
      }
    }


    // 名字最后是等或等人，且前面有超过一个字符，认为是多个名字
    // 如果前面是一个字符，则认为是合法的人名
    // TODO 将多个公司和多个人区别开来
    matcher = p_multiFeature.matcher(name);
    boolean atLeastOneMatch = false;
    String name_2 = "";
    if (matcher.find()) {
      atLeastOneMatch = true;
      name_2 = matcher.group(1);
    } else {
      matcher = p_multiFeature2.matcher(name);
      if (matcher.find()) {
        atLeastOneMatch = true;
        name_2 = matcher.group(1);
      }
    }
    // "等人"
    if (atLeastOneMatch) {
      if (name_2.length() == 1) {
        String nameCleaned = name_2 + "等";
        return new NameWrapper(PERSON, nameCleaned);
      } else {
        return new NameWrapper(MULPERSON);
      }
    }

    // 如果是三个字或两个字的名字并且含有带有一个字的括号，认为是具有形近字的名字
    // eg: "王小（晓）军"，"张大宝（堡）"此类名字
    if (name.length() == 5 || name.length() == 6) {
      matcher = p_withCloseCh.matcher(name);
      if (matcher.find()) {
        String name1 = matcher.group(1) + matcher.group(2) + matcher.group(4);
        String name2 = matcher.group(1) + matcher.group(3) + matcher.group(4);
        List<String> namesSplit = new ArrayList<>();
        namesSplit.add(name1);
        namesSplit.add(name2);
        String nameCleaned = name1;
        return new NameWrapper(PERSON_WITH_CLOSE_CH, nameCleaned, namesSplit);
      }
    }

    // 最后面是括号，如果前面汉字数目在2到4之间，认为是名字，并且括号里是曾用名，绰号，外号等备注信息
    // 如果前面汉字个数超过4个，则认为是公司名字，并且括号里是备足信息
    matcher = p_usedName.matcher(name);
    if (matcher.find()) {
      if (matcher.group(1).length() <= 4) {
        return new NameWrapper(PERSON_WITH_USEDNAME, matcher.group(1));
      } else {
        return new NameWrapper(ORG_WITH_USEDNAME, matcher.group(1));
      }
    }

    // 根据特定的标点符号分割名字，如果名字是两部分，且每部分的长度在2到6之间，则认为是合法的少数名族名字
    // 例如： 吐尔地·买买提
    String[] minorityNames = p_minorityNameSplitter.split(name);
    if (minorityNames.length == 2) {
      boolean findMinorityNames = true;
      for (int i = 0; i < 2; i++) {
        if (minorityNames[i].length() > 6 || minorityNames[i].length() < 2) {
          findMinorityNames = false;
          break;
        }
      }
      if (findMinorityNames) {

        String nameCleaned = minorityNames[0] + "·" + minorityNames[1];
        // String nameCleaned=minorityNames[0]+minorityNames[1];
        return new NameWrapper(PERSON, nameCleaned);
      }
    }

    String[] names = rawName.split(namesplitter);
    boolean findMultiPersons = true;
    if (names.length > 1) {
      List<String> pNames = new ArrayList<>();
      for (int i = 0; i < names.length; i++) {
        if (names[i].length() < 2 || names[i].length() > 4) {
          findMultiPersons = false;
          break;
        }
        pNames.add(names[i]);
      }
      if (findMultiPersons) {
        return new NameWrapper(MULPERSON, pNames);
      }
    }

    // 替换括号里的字符为空，根据字符个数判断是人名还是公司名
    String name2 = name.replaceAll(bracket, "");
    if (name2.length() <= 3) {
      return new NameWrapper(PERSON, name2);
    } else if (name2.length() == 4) {
      if (org_suffix_words.contains(name2.substring(2)))
        return new NameWrapper(ORG, name2);
      else
        return new NameWrapper(PERSON, name2);
    }
    matcher = p_usedName2.matcher(name2);
    if (matcher.find()) {
      return new NameWrapper(PERSON_WITH_USEDNAME, matcher.group(1));
    }
    matcher = p_mixedName.matcher(name2);
    if (matcher.find()) {
      return new NameWrapper(PERSON_WITH_PINYIN, name2);
    }

    String name3 = name2.replaceAll(nonChineseCh, "");
    if (name3.length() == 2 || name3.length() == 3) {
      return new NameWrapper(PERSON, name3);
    }
    return new NameWrapper(ORG, name2);
  }
}
