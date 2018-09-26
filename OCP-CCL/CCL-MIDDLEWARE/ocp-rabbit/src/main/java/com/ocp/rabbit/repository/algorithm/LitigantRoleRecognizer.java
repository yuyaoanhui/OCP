package com.ocp.rabbit.repository.algorithm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.splitWord.analysis.NlpAnalysis;

import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.personage.NameHandler;
import com.ocp.rabbit.repository.tool.algorithm.personage.NameWrapper;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.tool.algorithm.personage.PeopleType;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;
import com.ocp.rabbit.repository.util.Position;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

/**
 * 诉讼参与人角色识别
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class LitigantRoleRecognizer {
  // 所有当事人角色
  private static volatile Pattern PATTERN_LITIGANT_ROLE;
  private static Pattern PATTERN_LITIGANT_ROLE_DEFENDANT;
  private static Pattern PATTERN_LITIGANT_ROLE_PLAINTIFF;
  private static Map<String, String> ROLE_2_STD_ROLE;
  private static final Pattern[] PATTERN_NAME_PARSE = new Pattern[] {
      Pattern.compile(
          "(?:[\\(（].*?[\\)）])?[:：]?([－\\-a-z\u4e00-\u9fa5·\\.“”\"×*\\d、﹒]{2,}?)[,，;；。\\(（]",
          Pattern.CASE_INSENSITIVE),
      Pattern.compile(
          "(?:[\\(（].*?[\\)）])?[:：]?([－\\-a-z\u4e00-\u9fa5\\(（\\)）·\\.“”\"]{2,})[,，;；。]",
          Pattern.CASE_INSENSITIVE),};
  private static final Pattern PATTERN_CONTAIN_CHNCHAR = Pattern.compile("[\u4e00-\u9fa5]");
  private static final Pattern PATTERN_ESP[] = new Pattern[] {
      Pattern.compile("(?:[\\(（](.*?)[\\)）])"), Pattern.compile("一审|二审|原审|再审|申诉|申请|被告|原告|上诉|,|，")};

  private static Map<String, List<String>> litigantRoleTmp = new HashMap<>();

  /**
   * 从配置文件中读取诉讼参与人角色数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void readLitigantRole(String filePath) {
    if (PATTERN_LITIGANT_ROLE != null) {
      return;
    }
    List<String> lines = new ArrayList<>();
    if (!litigantRoleTmp.containsKey(filePath)) {
      InputStream is = FileUtils.loadProperties(filePath);
      lines = FileOperate.readTxtToArrays(is, "utf-8");
      litigantRoleTmp.put(filePath, lines);
    }
    lines = litigantRoleTmp.get(filePath);
    Map<String, Set<String>> rolesByCaseType = new HashMap<>();
    Map<String, String> role2StdRole = new HashMap<>();
    Set<String> attack = new HashSet<>(), defense = new HashSet<>();
    for (String line : lines) {
      if (Pattern.compile("#").matcher(line).find()) {
        continue;
      }
      String[] tokens = line.split("[\\s\t]+");
      if (tokens.length <= 3) {
        continue;
      }
      String caseType = tokens[0].substring(0, tokens[0].length() - 1);
      if (!rolesByCaseType.containsKey(caseType)) {
        rolesByCaseType.put(caseType, new HashSet<String>());
      }
      Set<String> roles = rolesByCaseType.get(caseType);
      roles.add(tokens[2]);
      for (int i = 3; i < tokens.length; i++) {
        if (tokens[i].length() >= 2) {
          role2StdRole.put(tokens[i], tokens[2]);
          if (tokens[1].equals("0")) {
            attack.add(tokens[i]);
          } else if (tokens[1].equals("1")) {
            defense.add(tokens[i]);
          }
        }
      }
    }
    List<String> tmp = new ArrayList<String>(role2StdRole.keySet());
    tmp.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });
    StringBuilder sb = new StringBuilder("");
    for (String role : tmp) {
      sb.append("|");
      sb.append(role);
    }
    PATTERN_LITIGANT_ROLE = Pattern.compile(sb.toString().substring(1));
    tmp = new ArrayList<>(attack);
    tmp.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });
    sb.delete(0, sb.length());
    for (String role : tmp) {
      sb.append("|");
      sb.append(role);
    }
    PATTERN_LITIGANT_ROLE_PLAINTIFF = Pattern.compile("^(" + sb.toString().substring(1) + ")");
    tmp = new ArrayList<>(defense);
    tmp.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });
    sb.delete(0, sb.length());
    for (String role : tmp) {
      sb.append("|");
      sb.append(role);
    }
    PATTERN_LITIGANT_ROLE_DEFENDANT = Pattern.compile("^(" + sb.toString().substring(1) + ")");
    ROLE_2_STD_ROLE = role2StdRole;
  }

  public static List<Position> parseName(String paragraph, int infoIndex, PeopleType pt,
      Set<String> foundNames) {
    List<Position> positions = new ArrayList<Position>();
    Position position = recognizeRoles(paragraph, infoIndex, pt);
    if (null != position && !positions.contains(position)) {
      positions.add(position);
      foundNames.add(position.getValue());
    }
    return positions;
  }

  private static Position recognizeRoles(String paragraph, int para_num, PeopleType pt) {
    List<String> roles = new ArrayList<>();
    NamedEntity[] nes =
        NamedEntityRecognizer.recognizeEntityByRegex(paragraph, PATTERN_LITIGANT_ROLE);
    if (nes.length == 0 || nes[0].getOffset() != 0) {
      return null;
    }
    // 根据角色之间的相对位置，确定边界
    int lastPos = -1;
    for (NamedEntity ne : nes) {
      if (lastPos != -1 || ne.getOffset() <= 10) {
        // 如果当前实体的位置距离上一个实体的位置超过4个字符，则认为不合法，跳出循环，并且不记录之后的所有实体
        if (lastPos != -1 && ne.getOffset() - lastPos >= 4) {
          break;
        } else {
          String role = ROLE_2_STD_ROLE.get(ne.getSource());
          roles.add(role);
          lastPos = ne.getOffset() + ne.getSource().length();
        }
      }
    }
    if (roles.isEmpty()) {
      return null;
    }
    // 确定了边界lastPos
    // 确定右边的字符是 [)）:，;]
    String truncatedSentence = paragraph.substring(lastPos);
    truncatedSentence = truncatedSentence.replaceFirst("^[\\)）：:，,;；。]+", "");
    int endPos = truncatedSentence.length();
    Matcher matcherEndPos = Pattern.compile("[；;。,，]+").matcher(truncatedSentence);
    if (matcherEndPos.find()) {
      endPos = matcherEndPos.start();
    }
    truncatedSentence = truncatedSentence.substring(0, endPos) + ",";
    Position p, p2 = null, p3 = null;
    // 如果括号里内容满足一定条件，删掉括号。
    String l = deleteStringWithParenthese(truncatedSentence);
    // 1. 先用不含括号的规则匹配，如果找到并且是合法名字，则暂时放入p
    // 2. 如果第一次找到的名字长度超过40个并且找到的名字含有汉字并且含有括号的匹配能够匹配上而且合法，则用第二次匹配的代替第一次匹配的。
    Matcher matcher = PATTERN_NAME_PARSE[0].matcher(l), matcher2;
    if (matcher.find() && !illegalName(matcher.group(1))) {
      p = new Position(matcher.group(1), pt.toString(), para_num, matcher.start());
      // 如果名字过长并且含有汉字，则认为可能是公司名字，用另外一个规则匹配
      if (matcher.group(1).length() > MAX_LENGTH_NAME
          && PATTERN_CONTAIN_CHNCHAR.matcher(matcher.group(1)).find()) {
        matcher2 = PATTERN_NAME_PARSE[1].matcher(l);
        if (matcher2.find() && !illegalName(matcher2.group(1))) {
          p2 = new Position(matcher2.group(1), pt.toString(), para_num, matcher2.start());
        }
      } else {
        String str = l.replaceAll("^[一二三四五六七八九十]", "");
        Matcher matcher1 = PATTERN_NAME_PARSE[0].matcher(str);
        if (matcher1.find()) {
          String name = matcher1.group(1);
          if ((name.length() <= 4) || (name.contains("·"))) {
            p3 = new Position(matcher1.group(1), pt.toString(), para_num, matcher1.start());
          } else if (name.length() <= 20) {
            // 针对名字长度大于4的进行分词处理，
            List<Term> lterm = StandardTokenizer.segment(str);
            // 如果第一个词标为人名
            if (lterm.get(0).nature.name().equals("nr")) {
              // 判断第二个词是人名或者名词，两个词合并
              if ((lterm.size() > 1) && ((lterm.get(1).nature.name().equals("nr"))
                  || (lterm.get(1).nature.name().equals("n")))) {
                p3 = new Position(lterm.get(0).word + lterm.get(1).word, pt.toString(), para_num,
                    matcher1.start());
              } else {
                p3 = new Position(lterm.get(0).word, pt.toString(), para_num, matcher1.start());
              }
              // 如果第一个词标为日语人名，认为是人民
            } else if (lterm.get(0).nature.name().equals("nrj")) {
              p3 = new Position(lterm.get(0).word, pt.toString(), para_num, matcher1.start());
              // 如果第二个词标为其他专名，认为是人名拆开了，与前一个词合并
            } else if (lterm.size() > 1 && lterm.get(1).nature.name().equals("nz")) {
              p3 = new Position(lterm.get(0).word + lterm.get(1).word, pt.toString(), para_num,
                  matcher1.start());
              // 如果第一个词标为其他专名，认为是人名
            } else if (lterm.get(0).nature.name().equals("nz")) {
              p3 = new Position(lterm.get(0).word, pt.toString(), para_num, matcher1.start());
            }
          }
        }
      }
      if (p2 != null) {
        p = p2;
      } else if (p3 != null) {
        p = p3;
      }
      // 把找到的信息放入Position
      if (p != null) {
        p.setInfo(roles);
        return p;
      }
    }
    return null;
  }

  // 2017.08.15 writed by cywei2 功能：根据诉讼参与人资源来识别人物，包括名字和角色
  public People recognizePeople(String paragraph, PeopleType pt) {
    People sp;
    paragraph = paragraph.trim();
    List<String> roles = new ArrayList<>();
    NamedEntity[] nes =
        NamedEntityRecognizer.recognizeEntityByRegex(paragraph, PATTERN_LITIGANT_ROLE);
    if ((nes.length == 0) || (nes[0].getOffset() != 0)) {
      return null;
    }
    // 获取人物角色
    int lastPos = -1;
    for (NamedEntity ne : nes) {
      if ((lastPos != -1) || (ne.getOffset() <= 10)) { // 10这个数值是根据诉讼角色的最大可能长度设置
        // 如果当前实体的位置距离上一个实体的位置超过4个字符，则认为不合法，跳出循环，并且不记录之后的所有实体
        if ((lastPos != -1) && (ne.getOffset() - lastPos >= 4)) {
          break;
        } else {
          String role = ROLE_2_STD_ROLE.get(ne.getSource());
          roles.add(role);
          lastPos = ne.getOffset() + ne.getSource().length();
        }
      }
    }
    if (roles.size() == 0) {
      return null;
    }
    // 确定了边界lastPos
    // 确定右边的字符是 [)）:，;]
    String truncatedSentence = paragraph.substring(lastPos);
    truncatedSentence = truncatedSentence.replaceFirst("^[\\)）：:，,;；。]+", "");
    int endPos = truncatedSentence.length();
    Matcher matcherEndPos = Pattern.compile("[；;。,，]+").matcher(truncatedSentence);
    if (matcherEndPos.find()) {
      endPos = matcherEndPos.start();
    }
    truncatedSentence = truncatedSentence.substring(0, endPos) + ",";
    People sp2 = null;
    // 如果括号里内容满足一定条件，删掉括号。
    String l = deleteStringWithParenthese(truncatedSentence);
    // 1. 先用不含括号的规则匹配，如果找到并且是合法名字，则暂时放入p
    // 2. 如果第一次找到的名字长度超过40个并且找到的名字含有汉字并且含有括号的匹配能够匹配上而且合法，则用第二次匹配的代替第一次匹配的。
    Matcher matcher = PATTERN_NAME_PARSE[0].matcher(l), matcher2;
    if (matcher.find() && !illegalName(matcher.group(1))) {
      // p = new Position(matcher.group(1), pt.toString(), para_num, matcher.start());
      NameWrapper nc = NameHandler.getNameType(matcher.group(1));
      String nameCleaned = nc.getNameCleaned();
      int nameType = nc.getNameType();
      sp = new People(nameCleaned, pt, nameType);
      // 如果名字过长并且含有汉字，则认为可能是公司名字，用另外一个规则匹配
      if (matcher.group(1).length() > MAX_LENGTH_NAME
          && PATTERN_CONTAIN_CHNCHAR.matcher(matcher.group(1)).find()) {
        matcher2 = PATTERN_NAME_PARSE[1].matcher(l);
        if (matcher2.find() && !illegalName(matcher2.group(1))) {
          // p2 = new Position(matcher2.group(1), pt.toString(), para_num, matcher2.start());
          nc = NameHandler.getNameType(matcher.group(1));
          nameCleaned = nc.getNameCleaned();
          nameType = nc.getNameType();
          sp2 = new People(nameCleaned, pt, nameType);
        }
      }
      if (sp2 != null) {
        sp = sp2;
      }
      // 把找到的信息放入sp
      if (null != sp) {
        sp.getPeopleAttrMap().put(InfoPointKey.info_litigant_position[InfoPointKey.mode],
            roles.get(0));
        sp.getPeopleAttrMap().put(InfoPointKey.info_all_litigant_positions[InfoPointKey.mode],
            roles);
        return sp;
      }
    }

    return null;
  }


  public static Pattern getRoleDefendantPattern() {
    return PATTERN_LITIGANT_ROLE_DEFENDANT;
  }

  public static Pattern getRolePlaintiffPattern() {
    return PATTERN_LITIGANT_ROLE_PLAINTIFF;
  }

  private static boolean illegalName(String s) {
    if (s == null || s.length() > MAX_LENGTH_NAME)
      return true;
    if (s.contains("诉讼") || s.contains("一案"))
      return true;
    return false;
  }

  private static final int MAX_LENGTH_NAME = 40;

  /**
   * 如果括号里有 一审等关键字或者由逗号等标点，则删除该括号<br>
   * 保留本来出现在名字里面的括号,如 "XXX（上海）公司"就不能把括号删掉。
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static String deleteStringWithParenthese(String source) {
    String target = source;
    Matcher matcher = PATTERN_ESP[0].matcher(source);
    if (matcher.find()) {
      Matcher matcher1 = PATTERN_ESP[1].matcher(matcher.group(1));
      if (matcher1.find()) {
        target = matcher.replaceFirst("");
      }
    }
    return target;
  }

  public static String recognizeRoles(String paragraph, List<String> roles) {
    String name;
    NamedEntity[] nes =
        NamedEntityRecognizer.recognizeEntityByRegex(paragraph, PATTERN_LITIGANT_ROLE);
    if (nes.length == 0 || nes[0].getOffset() != 0)
      return null;
    // 根据角色之间的相对位置，确定边界
    int lastPos = -1;
    for (NamedEntity ne : nes) {
      if (lastPos != -1 || ne.getOffset() <= 10) {
        // 如果当前实体的位置距离上一个实体的位置超过4个字符，则认为不合法，跳出循环，并且不记录之后的所有实体
        if (lastPos != -1 && ne.getOffset() - lastPos >= 4) {
          break;
        } else {
          String role = ROLE_2_STD_ROLE.get(ne.getSource());
          roles.add(role);
          lastPos = ne.getOffset() + ne.getSource().length();
        }
      }
    }
    if (roles.isEmpty()) {
      return null;
    }
    // 确定了边界lastPos
    // 确定右边的字符是 [)）:，;]
    String truncatedSentence = paragraph.substring(lastPos);
    truncatedSentence = truncatedSentence.replaceFirst("^[\\)）：:，,;；。]+", "");
    int endPos = truncatedSentence.length();
    Matcher matcherEndPos = Pattern.compile("[；;。,，]+").matcher(truncatedSentence);
    if (matcherEndPos.find()) {
      endPos = matcherEndPos.start();
    }
    truncatedSentence = truncatedSentence.substring(0, endPos) + ",";
    truncatedSentence = truncatedSentence.replaceFirst("^[一二三四五六七八九十]+", "");
    // Position p = null, p2 = null;
    // 如果括号里内容满足一定条件，删掉括号。
    String l = deleteStringWithParenthese(truncatedSentence);
    // 1. 先用不含括号的规则匹配，如果找到并且是合法名字，则为默认名称
    // 2. 如果第一次找到的名字长度超过40个并且找到的名字含有汉字并且含有括号的匹配能够匹配上而且合法，则用第二次匹配的代替第一次匹配的。
    Matcher matcher = PATTERN_NAME_PARSE[0].matcher(l), matcher2;
    if (matcher.find() && !illegalName(matcher.group(1))) {
      name = matcher.group(1);
      // 如果名字过长并且含有汉字，则认为可能是公司名字，用另外一个规则匹配
      if (matcher.group(1).length() > MAX_LENGTH_NAME
          && PATTERN_CONTAIN_CHNCHAR.matcher(matcher.group(1)).find()) {
        matcher2 = PATTERN_NAME_PARSE[1].matcher(l);
        if (matcher2.find() && !illegalName(matcher2.group(1))) {
          name = matcher.group(1);
        }
      }
      return name;
    }
    return null;
  }

  private static final Pattern KEY_WORDS =
      Pattern.compile("律师|法定代理人|委托代理人|诉讼代理人|法定代表人|代表人|责任人|负责人|辩护人|代理人");

  /**
   * 
   * @author yu.yao
   * @param para_num 段落编号
   * @return
   */
  public static Position recognizeNameRoles(String paragraph, int para_num, PeopleType pt) {
    List<String> roles = new ArrayList<>();
    String[] paras = DocumentUtils.splitSentenceByCommaSemicolon(paragraph);
    if (paras.length < 1)
      return null;
    String s = paras[0];
    NamedEntity[] nes;
    if (PeopleType.DEFENDANT.equals(pt) || (PeopleType.PLAINTIFF.equals(pt))
        || (PeopleType.THIRD_PERSON.equals(pt))) {
      nes = NamedEntityRecognizer.recognizeEntityByRegex(s, PATTERN_LITIGANT_ROLE);
      if (nes.length == 0)
        return null;
      for (NamedEntity ne : nes) {
        String role = ROLE_2_STD_ROLE.get(ne.getSource());
        roles.add(role);
      }
    } else {
      nes = NamedEntityRecognizer.recognizeEntityByRegex(s, KEY_WORDS);
      if (nes.length == 0)
        return null;
      String role = nes[0].getSource();
      roles.add(role);
    }
    int lastPos = -1;
    for (int i = nes.length - 1; i >= 0; i--) {
      if (nes[i].getOffset() + nes[i].getSource().length() < s.length()) {
        lastPos = nes[i].getOffset() + nes[i].getSource().length();
        break;
      }
    }
    if (lastPos == -1)
      return null;
    // 确定了边界lastPos
    // 确定右边的字符是 [)）:，;]
    String truncatedSentence = s.substring(lastPos);
    truncatedSentence = truncatedSentence.replaceAll("[(（][^，,;；。]*?[)）]", "");
    truncatedSentence = truncatedSentence.replaceFirst("^[\\)）：:，,;；。]+", "");
    truncatedSentence = truncatedSentence.replaceFirst("^(自报)", "");
    truncatedSentence = truncatedSentence + ",";
    if (truncatedSentence.contains("羁押"))
      return null;
    Position p, p2 = null, p3 = null;
    // 如果括号里内容满足一定条件，删掉括号。
    String l;
    if (PeopleType.DEFENDANT.equals(pt) || (PeopleType.PLAINTIFF.equals(pt))
        || (PeopleType.THIRD_PERSON.equals(pt))) {
      l = deleteStringWithParenthese(truncatedSentence);
    } else {
      l = truncatedSentence.replaceAll("[\\(（]([\u4e00-\u9fa5]+)[\\)）]", "");
    }
    // 1. 先用不含括号的规则匹配，如果找到并且是合法名字，则暂时放入p
    // 2. 如果第一次找到的名字长度超过40个并且找到的名字含有汉字并且含有括号的匹配能够匹配上而且合法，则用第二次匹配的代替第一次匹配的。
    Matcher matcher = PATTERN_NAME_PARSE[0].matcher(l), matcher2;
    if (matcher.find() && !illegalName(matcher.group(1))) {
      p = new Position(matcher.group(1), pt.toString(), para_num, matcher.start());
      // 如果名字过长并且含有汉字，则认为可能是公司名字，用另外一个规则匹配
      if (matcher.group(1).length() > MAX_LENGTH_NAME
          && PATTERN_CONTAIN_CHNCHAR.matcher(matcher.group(1)).find()) {
        matcher2 = PATTERN_NAME_PARSE[1].matcher(l);
        if (matcher2.find() && !illegalName(matcher2.group(1))) {
          p2 = new Position(matcher2.group(1), pt.toString(), para_num, matcher2.start());
        }
      } else {
        Matcher matcher1 = PATTERN_NAME_PARSE[0].matcher(l);
        if (matcher1.find()) {
          String name = matcher1.group(1);
          if (s.contains("被告单位")) {
            p = new Position(name, pt.toString(), para_num, matcher1.start());
            return p;
          }
          if (name.startsWith("的")) {
            name = name.replaceFirst("^的", "");
          }
          if ((name.length() <= 4) || (name.contains("·")) || (name.endsWith("公司"))) {
            p3 = new Position(name, pt.toString(), para_num, matcher1.start());
          } else if (name.length() <= 20 || name.contains("涉嫌")) {
            // 针对名字长度大于4的进行分词处理
            List<org.ansj.domain.Term> terms = NlpAnalysis.parse(name).getTerms();
            for (org.ansj.domain.Term term : terms) {
              if (term.getNatureStr().equals("nr")) {
                if (term.getName().length() <= 4 && term.getName().length() > 1) {
                  p3 = new Position(term.getName(), pt.toString(), para_num, matcher1.start());
                  break;
                }
              }
            }
            if (p3 == null) {
              for (int i = 0; i < terms.size(); i++) {
                if ((terms.get(i).getNatureStr().equals("r"))
                    && (terms.get(i).getName().equals("某某"))) {
                  if ((i > 0) && (terms.get(i - 1).getName().length() == 1)) {
                    p3 = new Position(terms.get(i - 1).getName() + terms.get(i).getName(),
                        pt.toString(), para_num, matcher1.start());
                  }
                }
              }
            }
          }
        }
      }
      if (p2 != null) {
        p = p2;
      } else if (p3 != null) {
        p = p3;
      }
      // 把找到的信息放入Position
      p.setInfo(roles);
      return p;
    }
    return null;
  }
}
