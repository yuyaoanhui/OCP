package com.ocp.rabbit.repository.tool.algorithm.number;

import java.util.HashMap;
import java.util.Map;

/**
 * 中文数字转为阿拉伯数字<br>
 * 暂时没有考虑多种写法的混合模式，比如“五15”
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class Chi2Ara {
  /**
   * chinese numeric chars. <br />
   * i have put the chars into the lexicon file lex-cn-numeric.lex for the old version. <r /> it's
   * better to follow the current work.
   */
  private static final Character[] CN_NUMERIC = {'一', '二', '三', '四', '五', '六', '七', '八', '九', '０',
      '１', '２', '３', '４', '５', '６', '７', '８', '９', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '十',
      '百', '千', '拾', '佰', '仟', '万', '亿', '○', 'Ｏ', '零', '⑴', '⑵', '⑶', '⑷', '⑸', '⑹', '⑺', '⑻', '⑼',
      '⑽', '⑾', '⑿', '⒀', '⒁', '⒂', '⒃', '⒄', '⒅', '⒆', '⒇'};


  private static Map<Character, Integer> cnNumeric = null;

  static {
    cnNumeric = new HashMap<Character, Integer>(40, 0.85f);
    for (int j = 0; j < 9; j++)
      cnNumeric.put(CN_NUMERIC[j], j + 1);
    for (int j = 9; j < 19; j++)
      cnNumeric.put(CN_NUMERIC[j], j - 9);
    for (int j = 19; j < 28; j++)
      cnNumeric.put(CN_NUMERIC[j], j - 18);

    String strZero = "零〇○Ｏ０OoΟ0О";
    for (int i = 0; i < strZero.length(); i++) {
      cnNumeric.put(strZero.charAt(i), 0);
    }
    // cnNumeric.put('零', 0);
    // cnNumeric.put('○', 0);
    // cnNumeric.put('Ｏ', 0);
    cnNumeric.put('两', 2);
    cnNumeric.put('十', 10);
    cnNumeric.put('拾', 10);
    cnNumeric.put('百', 100);
    cnNumeric.put('佰', 100);
    cnNumeric.put('千', 1000);
    cnNumeric.put('仟', 1000);
    cnNumeric.put('万', 10000);
    cnNumeric.put('萬', 10000);
    cnNumeric.put('亿', 100000000);
    cnNumeric.put('⑴', 1);
    cnNumeric.put('⑵', 2);
    cnNumeric.put('⑶', 3);
    cnNumeric.put('⑷', 4);
    cnNumeric.put('⑸', 5);
    cnNumeric.put('⑹', 6);
    cnNumeric.put('⑺', 7);
    cnNumeric.put('⑻', 8);
    cnNumeric.put('⑼', 9);
    cnNumeric.put('⑽', 10);
    cnNumeric.put('⑾', 11);
    cnNumeric.put('⑿', 12);
    cnNumeric.put('⒀', 13);
    cnNumeric.put('⒁', 14);
    cnNumeric.put('⒂', 15);
    cnNumeric.put('⒃', 16);
    cnNumeric.put('⒄', 17);
    cnNumeric.put('⒅', 18);
    cnNumeric.put('⒆', 19);
    cnNumeric.put('⒇', 20);
  }


  /**
   * check the given char is chinese numeric or not. <br />
   *
   * @param c <br />
   * @return boolean true yes and false for not.
   */
  public static int isCNNumeric(char c) {
    Integer i = cnNumeric.get(c);
    if (i == null)
      return -1;
    return i.intValue();
  }


  /**
   * a static method to turn the Chinese numeric to Arabic numbers.
   */

  public static boolean inUnit(String str) {
    char[] CN_UNIT = {'十', '百', '千', '拾', '佰', '仟', '万', '亿', '萬'};
    int count = 0;
    TESTFORNULL: for (int i = 0; i < str.length(); i++) {
      for (int j = 0; j < CN_UNIT.length; j++) {
        if (str.charAt(i) == CN_UNIT[j]) {
          count++;
          break TESTFORNULL;
        }
      }
    }
    if (count == 0) {
      return false;
    } else {
      return true;
    }
  }

  public static boolean isNumber(String str) {
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
      if (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.') {
        count++;
      }
    }
    if (count == str.length()) {
      return true;
    } else
      return false;
  }

  public static boolean inNumeric(String str) {
    for (int i = 0; i < str.length(); i++) {
      if (Character.isDigit(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  public static double ExNumeric(String str) {
    String str2 = "";
    for (int i = 0; i < str.length(); i++)
      if (str.charAt(i) == '点')
        str2 += '.';
      else if (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')
        str2 += str.charAt(i);
    // return str2;
    Double dou = Double.valueOf(str2);
    double d = dou.doubleValue();
    return d;
  }

  public static String ExUnNumeric(String str) {
    String str2 = "";
    for (int i = 0; i < str.length(); i++)
      if (!Character.isDigit(str.charAt(i)) && str.charAt(i) != '.')
        str2 += str.charAt(i);
    return str2;
  }

  public static long cnNumericToArabic(String cnn, boolean flag) {

    cnn = cnn.trim();

    double dig = 0.0;
    if (inNumeric(cnn)) {
      dig = ExNumeric(cnn);
      cnn = ExUnNumeric(cnn);
    }

    long dtoi = 0;
    if (cnn.length() == 1) {
      dtoi = isCNNumeric(cnn.charAt(0));
      if (dig != 0) {
        dtoi *= dig;
      }
      return dtoi;
    }

    // if ( cnn.length() == 1 )
    // return isCNNumeric(cnn.charAt(0));

    if (flag)
      cnn = cnn.replace("佰", "百").replace("仟", "千").replace("拾", "十").replace("零", "").replace("萬",
          "万");
    // System.out.println(cnn);
    int yi = -1, wan = -1, qian = -1, bai = -1, shi = -1;
    long val = 0;
    yi = cnn.lastIndexOf("亿");
    if (yi > -1) {
      val += cnNumericToArabic(cnn.substring(0, yi), false) * 100000000;
      if (yi < cnn.length() - 1)
        cnn = cnn.substring(yi + 1, cnn.length());
      else
        cnn = "";

      if (cnn.length() == 1) {
        int arbic = isCNNumeric(cnn.charAt(0));
        if (arbic <= 10)
          val += arbic * 10000000;
        cnn = "";
      }
    }

    wan = cnn.lastIndexOf("万");
    if (wan > -1) {
      val += cnNumericToArabic(cnn.substring(0, wan), false) * 10000;
      if (wan < cnn.length() - 1)
        cnn = cnn.substring(wan + 1, cnn.length());
      else
        cnn = "";
      if (cnn.length() == 1) {
        int arbic = isCNNumeric(cnn.charAt(0));
        if (arbic <= 10)
          val += arbic * 1000;
        cnn = "";
      }
    }

    qian = cnn.lastIndexOf("千");
    if (qian > -1) {
      val += cnNumericToArabic(cnn.substring(0, qian), false) * 1000;
      if (qian < cnn.length() - 1)
        cnn = cnn.substring(qian + 1, cnn.length());
      else
        cnn = "";
      if (cnn.length() == 1) {
        int arbic = isCNNumeric(cnn.charAt(0));
        if (arbic <= 10)
          val += arbic * 100;
        cnn = "";
      }
    }

    bai = cnn.lastIndexOf("百");
    if (bai > -1) {
      val += cnNumericToArabic(cnn.substring(0, bai), false) * 100;
      if (bai < cnn.length() - 1)
        cnn = cnn.substring(bai + 1, cnn.length());
      else
        cnn = "";
      if (cnn.length() == 1) {
        int arbic = isCNNumeric(cnn.charAt(0));
        if (arbic <= 10)
          val += arbic * 10;
        cnn = "";
      }
    }

    shi = cnn.lastIndexOf("十");
    if (shi > -1) {
      if (shi == 0)
        val += 1 * 10;
      else
        val += cnNumericToArabic(cnn.substring(0, shi), false) * 10;
      if (shi < cnn.length() - 1)
        cnn = cnn.substring(shi + 1, cnn.length());
      else
        cnn = "";
    }

    cnn = cnn.trim();
    for (int j = 0; j < cnn.length(); j++)
      val += isCNNumeric(cnn.charAt(j)) * Math.pow(10, cnn.length() - j - 1);

    if (dig != 0)
      val *= dig;
    return val;
  }

  public static double arabicToArabic(String cnn) {
    cnn = cnn.trim();
    Double dou = Double.valueOf(cnn);
    double num = dou.doubleValue();
    return num;
  }


  public static double chStr2int(String cnn) throws Exception {
    if (cnn == "") {
      int val4 = 0;
      return val4;
    } else if (isNumber(cnn)) {
      double val1 = arabicToArabic(cnn);
      return val1;
    } else if (!inUnit(cnn)) {
      String numStr = "";
      for (int j = 0; j < cnn.length(); j++) {
        numStr += cnNumeric.get(cnn.charAt(j));
      }
      Integer inte = Integer.valueOf(numStr);
      long val3 = inte.longValue();
      return val3;
    } else {
      long val2 = cnNumericToArabic(cnn, true);
      return val2;
    }
  }
}
