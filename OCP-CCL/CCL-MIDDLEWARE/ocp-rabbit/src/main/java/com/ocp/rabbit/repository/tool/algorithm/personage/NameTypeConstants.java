package com.ocp.rabbit.repository.tool.algorithm.personage;

/**
 * 名称类型：是人名、机构名？
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class NameTypeConstants {
  /**
   * 人名
   */
  public static final int PERSON = 1;
  /**
   * 机构名
   */
  public static final int ORG = 2;
  /**
   * 含有多个人名
   */
  public static final int MULPERSON = 3;
  /**
   * 含有多个机构名
   */
  public static final int MULORG = 4;
  /**
   * 名字含有括号
   */
  public static final int PERSON_WITH_CLOSE_CH = 5;
  /**
   * 名字含有曾用名
   */
  public static final int PERSON_WITH_USEDNAME = 6;
  /**
   * 机构含有曾用名
   */
  public static final int ORG_WITH_USEDNAME = 7;
  /**
   * 名字含有拼音
   */
  public static final int PERSON_WITH_PINYIN = 8;
  /**
   *
   */
  public static final int PERSON_WITH_ID_NO = 9;
  public static final int ILLEGALNAME = 99;
}
