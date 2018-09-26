package com.ocp.rabbit.repository.tool.algorithm.personage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证验证的工具（支持5位或18位省份证） 身份证号码结构： 17位数字和1位校验码：6位地址码数字，8位生日数字，3位出生时间顺序号，1位校验码。
 * 地址码（前6位）：表示对象常住户口所在县（市、镇、区）的行政区划代码，按GB/T2260的规定执行。 出生日期码，（第七位
 * 至十四位）：表示编码对象出生年、月、日，按GB按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
 * 顺序码（第十五位至十七位）：表示在同一地址码所标示的区域范围内，对同年、同月、同日出生的人编订的顺序号， 顺序码的奇数分配给男性，偶数分配给女性。 校验码（第十八位数）：
 * 十七位数字本体码加权求和公式 s = sum(Ai*Wi), i = 0,,16，先对前17位数字的权求和； Ai:表示第i位置上的身份证号码数字值.Wi:表示第i位置上的加权因.Wi: 7 9
 * 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2； 计算模 Y = mod(S, 11) 通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1
 * 0 X 9 8 7 6 5 4 3 2
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class IDCardJudge {
  final static Map<Integer, String> zoneNum = new HashMap<Integer, String>();

  static {
    zoneNum.put(11, "北京");
    zoneNum.put(12, "天津");
    zoneNum.put(13, "河北");
    zoneNum.put(14, "山西");
    zoneNum.put(15, "内蒙古");
    zoneNum.put(21, "辽宁");
    zoneNum.put(22, "吉林");
    zoneNum.put(23, "黑龙江");
    zoneNum.put(31, "上海");
    zoneNum.put(32, "江苏");
    zoneNum.put(33, "浙江");
    zoneNum.put(34, "安徽");
    zoneNum.put(35, "福建");
    zoneNum.put(36, "江西");
    zoneNum.put(37, "山东");
    zoneNum.put(41, "河南");
    zoneNum.put(42, "湖北");
    zoneNum.put(43, "湖南");
    zoneNum.put(44, "广东");
    zoneNum.put(45, "广西");
    zoneNum.put(46, "海南");
    zoneNum.put(50, "重庆");
    zoneNum.put(51, "四川");
    zoneNum.put(52, "贵州");
    zoneNum.put(53, "云南");
    zoneNum.put(54, "西藏");
    zoneNum.put(61, "陕西");
    zoneNum.put(62, "甘肃");
    zoneNum.put(63, "青海");
    zoneNum.put(64, "新疆");
    zoneNum.put(71, "台湾");
    zoneNum.put(81, "香港");
    zoneNum.put(82, "澳门");
    zoneNum.put(91, "外国");
  }

  final static int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
  final static int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

  /**
   * 身份证验证
   *
   * @param idCardNo 号码内容
   * @return 是否有效 null和"" 都是false
   */
  public static boolean isIDCard(String idCardNo) {

    // 判断号码的长度 15位或18位
    if (idCardNo == null || (idCardNo.length() != 15 && idCardNo.length() != 18)) {
      return false;
    }

    // 18位身份证前17位位数字
    if (idCardNo.length() == 18) {
      if (!isNumeric(idCardNo.substring(0, 17))) {
        return false;
      }
      // 身份证15位号码都应为数字
    } else if (idCardNo.length() == 15) {
      if (!isNumeric(idCardNo)) {
        return false;
      }
    }

    // 校验区位码
    if (!zoneNum.containsKey(Integer.valueOf(idCardNo.substring(0, 2)))) {
      return false;
    }

    // 校验出生日期:身份证出生日期无效
    if (!isDate(getBirthday(idCardNo))) {
      return false;
    }

    final char[] cs = idCardNo.toUpperCase().toCharArray();
    // 校验位数
    int power = 0;
    for (int i = 0; i < cs.length; i++) {
      if (i == cs.length - 1 && cs[i] == 'X')
        break;// 最后一位可以 是X或x
      if (cs[i] < '0' || cs[i] > '9')
        return false;
      if (i < cs.length - 1) {
        power += (cs[i] - '0') * POWER_LIST[i];
      }
    }
    if (cs[cs.length - 1] != PARITYBIT[power % 11]) {
      return false;
    }

    return true;
  }

  /**
   * 判断字符串是否为数字,0-9重复0次或者多次
   *
   * @param strnum
   * @return
   */
  private static boolean isNumeric(String strnum) {
    Pattern pattern = Pattern.compile("[0-9]*");
    Matcher isNum = pattern.matcher(strnum);
    if (isNum.matches()) {
      return true;
    } else {
      return false;
    }
  }

  // 从身份证号码中提取性别
  public static String getSex(String id) {
    if (id == null) {
      return "";
    }

    String sex = "0";
    if (id.length() == 18) {
      sex = id.substring(16, 17);
    } else if (id.length() == 15) {
      sex = id.substring(14, 15);
    } else {
      return "";
    }

    if (Integer.parseInt(sex) % 2 == 0) {
      return "女";
    } else {
      return "男";
    }
  }

  // 从身份证号码中截取出生日期
  public static String getBirthday(String id) {
    String tempId = "";
    if (id == null) {
      tempId = "";
    } else if (id.length() == 18) {
      tempId = id.substring(6, 14);
    } else if (id.length() == 15) {
      tempId = "19" + id.substring(6, 12);
    }

    if (tempId.length() == 8) {
      StringBuffer sb = new StringBuffer();
      sb.append(tempId.substring(0, 4)).append("-").append(tempId.substring(4, 6)).append("-")
          .append(tempId.substring(6, 8));
      return sb.toString();
    }
    return null;
  }

  /**
   * 功能：判断字符串出生日期是否符合正则表达式：包括年月日，闰年、平年和每月31天、30天和闰月的28天或者29天
   *
   * @param strDate
   * @return
   */
  private static boolean isDate(String strDate) {

    Pattern pattern = Pattern.compile(
        "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))?$");
    Matcher m = pattern.matcher(strDate);
    if (m.matches()) {
      return true;
    } else {
      return false;
    }
  }
}
