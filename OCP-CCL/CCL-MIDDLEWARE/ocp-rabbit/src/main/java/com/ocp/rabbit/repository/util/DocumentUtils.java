package com.ocp.rabbit.repository.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DocumentUtils {

  private static final String regex_whiteSpace = "[ \\u007F\f\r\\x0B\\xa0\\u3000\t　]+";;
  private static final String regex_linesplitter = "[\r\n]+";
  private static final String regex_shortSentences = "[\\,，\\;；。：:]+";
  private static final String regex_shortSentences3 = "[\\,，\\;；。]+";
  private static final String regex_sentences = "[\\;；。]+";
  private static final String regex_sentence = "。";
  private static final String regex_shortSentences2 = "[\\,，：:]+";
  public static final String regex_dropBrackets = "(?:[\\(（](.*?)[\\)）])";
  public static final Pattern Pattern_Comma_Semicolon_Period = Pattern.compile("[,，；;。]");
  public static final Pattern Pattern_Comma = Pattern.compile("[,，]+");
  public static final Pattern Pattern_Semicolon = Pattern.compile("[；;]");
  public static final Pattern Pattern_Periods = Pattern.compile("[；;。]+");
  // 如果出现 3，400元这样的数字，则把逗号去掉，以免影响后面按照逗号分句。
  private static final String regex_numberSpliter = "(\\d)[，,](\\d)";

  // 如果“、”在书名号里面，则不再拆分。
  private static final Pattern PATTERN_DUN = Pattern.compile("《[^《》]*、[^《》]*》");

  private static final String MINMUM_SEPERATOR = "[，,；;。、]+";

  private static final Pattern PATTERN_BRACKETS_1 =
      Pattern.compile("([\\(（]([^\\(（\\)）]*?)[\\)）])");

  /**
   * 将不同的段落用句号连接起来。
   */
  public static String mergeParagraphs(List<String> paragraphs) {
    String s = "";
    for (String para : paragraphs) {
      s = s + para + "。";
    }
    return s;
  }

  /**
   * 根据最小的标点符号分割
   */
  public static String[] splitSentenceByMinimumSeperator(String s) {
    return s.split(MINMUM_SEPERATOR);
  }

  public static String[] splitSentenceByCommaSemicolon(String s) {
    return s.split(regex_shortSentences3);
  }

  public static List<String> splitSentenceByCommaSemicolonPeriod(String s) {
    List<String> rsltList = new ArrayList<>();
    String[] tmpList = s.split(regex_shortSentences3);
    for (String tmpStr : tmpList) {
      rsltList.add(tmpStr);
    }
    return rsltList;
  }

  public static String replaceInterpunction(String text) {
    // TODO: 替换英文标点为中文标点
    // 如果出现 3，400元这样的数字，则把逗号去掉，以免影响后面按照逗号分句。
    return text.replaceAll(regex_numberSpliter, "$1$2");
  }

  public static String replaceWhiteSpace(String text) {
    text = text.replaceAll(regex_whiteSpace, "");
    return text;
  }

  public static String[] splitParagraphs(String text) {
    String[] paragraphs = text.split(regex_linesplitter);
    return paragraphs;
  }

  public static String replaceMultiLineSplit(String text) {
    if (TextUtils.isEmpty(text))
      return text;
    // 将多个换行符号替换成一个
    text = text.replaceAll("[\n\r]+", "\n");
    return text;
  }

  /**
   * 去掉括号 目前支持去掉 (),（）
   */
  public static String dropBrackets(String s) {
    java.util.regex.Matcher matcher = PATTERN_BRACKETS_1.matcher(s);
    String tmp = matcher.replaceAll("");
    return tmp;
  }


  public static String[][] splitSentences(String[] paragraphs) {

    String[][] shortSents = new String[paragraphs.length][];
    for (int i = 0; i < paragraphs.length; i++) {
      shortSents[i] = paragraphs[i].split(regex_shortSentences);
    }
    return shortSents;
  }

  public static String[][] splitSentences(List<String> paragraphs) {

    String[][] shortSents = new String[paragraphs.size()][];
    for (int i = 0; i < paragraphs.size(); i++) {
      shortSents[i] = paragraphs.get(i).split(regex_shortSentences);
    }
    return shortSents;
  }

  public static String[][] splitSentencesByPeriod(String[] paragraphs) {

    String[][] shortSents = new String[paragraphs.length][];
    for (int i = 0; i < paragraphs.length; i++) {
      shortSents[i] = paragraphs[i].split(regex_sentences);
    }
    return shortSents;
  }

  public static String[][] splitSentencesByPeriod(List<String> paragraphs) {

    String[][] shortSents = new String[paragraphs.size()][];
    for (int i = 0; i < paragraphs.size(); i++) {
      shortSents[i] = paragraphs.get(i).split(regex_sentences);
    }
    return shortSents;
  }

  public static String[] splitOneParagraphByPeriod(String paragraph) {
    return paragraph.split(regex_sentences);
  }

  public static String[] splitOneParagraphByOnePeriod(String paragraph) {
    return paragraph.split(regex_sentence);
  }

  // TODO 涉及到含有“,”的数字时，不能这样切分,比如"2，110"
  public static String[] splitOneSentenceByComma(String sentence) {

    String[] firstSplits = sentence.split(regex_shortSentences2);
    List<String> results = new ArrayList<>();
    for (String s : firstSplits) {
      if (PATTERN_DUN.matcher(s).find()) {
        results.add(s);
      } else {
        results.addAll(Arrays.asList(s.split("[、]+")));
      }
    }
    String[] res = new String[results.size()];
    results.toArray(res);
    return res;
  }

}
