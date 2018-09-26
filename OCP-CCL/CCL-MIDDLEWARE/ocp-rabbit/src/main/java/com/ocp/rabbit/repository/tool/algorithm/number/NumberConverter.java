package com.ocp.rabbit.repository.tool.algorithm.number;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 数字格式转换
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class NumberConverter {

  private static Character[] nums = {'一', '二', '三', '四', '五', '六', '七', '八', '九', '１', '２', '３',
      '４', '５', '６', '７', '８', '９', '1', '2', '3', '4', '5', '6', '7', '8', '9', '壹', '贰', '叁', '肆',
      '伍', '陆', '柒', '捌', '玖', '两', '十', '百', '千', '万', '亿', '拾', '佰', '仟', '萬', '亿', '○', 'Ｏ', '零',
      '０', '〇', '○', 'Ｏ', '０', 'O', 'o', 'Ο', '0', 'О', '0',};

  // 数字初始值,与上面数字字符一一对应
  private static int[] intNumValues = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2,
      3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 2, 10, 100, 1000, 10000, 100000000, 10, 100,
      1000, 10000, 100000000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,};
  // 单位
  private static Character[] units = {'十', '百', '千', '万', '亿', '拾', '佰', '仟', '萬', '亿',};
  // 单位初始值,与上面单位字符一一对应
  private static int[] intUnitValues =
      {10, 100, 1000, 10000, 100000000, 10, 100, 1000, 10000, 100000000,};

  /**
   * <数字字符,初始化数字>映射表
   */
  public static Map<Character, Integer> num2Arabic = buildNumMap();
  /**
   * <单位字符,初始化数字>映射表
   */
  public static Map<Character, Integer> unit2Arabic = buildUnitMap();

  /**
   * 将字符串(可能包含中文数字字符、阿拉伯数字字符)转换为对应的数字
   * 
   * @param
   * @return
   */
  public static double parseNumber(String s) {
    char[] chars = s.toCharArray();
    LinkedList<NumbersUnit> leftChars = new LinkedList<NumbersUnit>();
    LinkedList<NumbersUnit> rightChars = new LinkedList<NumbersUnit>();
    LinkedList<NumbersUnit> currentSide;
    boolean leftFlag = true;
    int prevFlag = -1;
    LinkedList<Integer> rightUnits = new LinkedList<Integer>();
    // 只有在上一个是单位本次是数字的情况下，新建一个元素
    for (char current : chars) {
      currentSide = leftChars;
      if (current == '.' || current == '点') {
        leftFlag = false;
        currentSide = rightChars;
        prevFlag = -1;
        continue;
      }
      if (!leftFlag) {
        currentSide = rightChars;
      }
      if (unit2Arabic.containsKey(current)) {
        int unitValue = unit2Arabic.get(current);
        if (leftFlag && leftChars.isEmpty()) {
          leftChars.add(new NumbersUnit());
        } else if (!leftFlag && rightChars.isEmpty()) {
          rightChars.add(new NumbersUnit());
        }
        if (prevFlag == 0 && leftFlag) {
          currentSide.add(new NumbersUnit());
        }
        if (leftFlag) {
          currentSide.getLast().unit = unitValue;
        } else {
          rightUnits.add(unitValue);
        }
        prevFlag = 0;
      } else if (num2Arabic.containsKey(current)) {
        int numberValue = num2Arabic.get(current);
        // 如果当前在左边并且是空的
        if (leftFlag && leftChars.size() == 0) {
          leftChars.add(new NumbersUnit());
        }
        // 如果当前在右边并且是空的
        else if (!leftFlag && rightChars.size() == 0) {
          rightChars.add(new NumbersUnit());
        }
        // 如果上一个字符是单位unit并且不是空的，则新建一个元素
        // 如果currentSide.size() == 0 表明刚开始循环，前面已经初始化了，不能重复初始化
        if (prevFlag == 0) {
          currentSide.add(new NumbersUnit());
        }
        currentSide.getLast().numbers.add(numberValue);
        prevFlag = 1;
      }
    }
    // 最左边只有单位，没有数字，添加“1”,比如"十五"
    if (leftChars.size() != 0 && leftChars.get(0).numbers.size() == 0) {
      leftChars.get(0).numbers.add(1);
    }
    // 处理3.5千万这种情况，即前面是小数，后面是单位
    long totalUnitValue = 1;
    if (rightUnits.size() != 0) {
      for (int i : rightUnits)
        totalUnitValue *= i;
    }
    double left = (double) convertLeftNumber(leftChars);
    double right = convertRightNumber(rightChars);
    return (left + right) * totalUnitValue;
  }

  /**
   * 构建<数字字符,初始化数字>映射表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static Map<Character, Integer> buildNumMap() {
    Map<Character, Integer> num2Arabic = new HashMap<Character, Integer>();
    for (int i = 0; i < nums.length; i++) {
      num2Arabic.put(nums[i], intNumValues[i]);
    }
    return num2Arabic;
  }

  /**
   * 构建<单位字符,初始化数字>映射表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static Map<Character, Integer> buildUnitMap() {
    Map<Character, Integer> unit2Arabic = new HashMap<Character, Integer>();
    for (int i = 0; i < units.length; i++) {
      unit2Arabic.put(units[i], intUnitValues[i]);
    }
    return unit2Arabic;
  }

  private static class NumbersUnit {
    LinkedList<Integer> numbers;
    int unit;

    public NumbersUnit() {
      numbers = new LinkedList<Integer>();
      unit = 1;
    }
  }

  private static long convertLeftNumber(LinkedList<NumbersUnit> chars) {
    if (chars == null || chars.isEmpty()) {
      return 0;
    } else if (chars.size() == 1) {
      return (long) parseInt(chars.get(0).numbers) * chars.get(0).unit;
    }
    LinkedList<int[]> descArray = new LinkedList<int[]>();
    int maxNum = 0;
    for (int i = 0; i < chars.size(); i++) {
      maxNum = insertIntoDecreasingArray(descArray, maxNum, chars.get(i).unit, i);
    }
    int leftPos = 0, rightPos = chars.size() - 1;
    long totalSum = 0;
    for (int i = 0; i < maxNum; i++) {
      int unitValue = descArray.get(i)[0];
      rightPos = descArray.get(i)[1];
      LinkedList<NumbersUnit> llnus = new LinkedList<>();
      for (int j = leftPos; j < rightPos; j++)
        llnus.add(chars.get(j));
      leftPos = rightPos + 1;
      totalSum += (convertLeftNumber(llnus) + parseInt(chars.get(rightPos).numbers)) * unitValue;
    }
    return totalSum;
  }

  /**
   * 小数点后面不允许出现不是1的 单位
   * 
   * @param chars
   * @return 用 -1 标记错误
   */
  private static double convertRightNumber(LinkedList<NumbersUnit> chars) {
    int tmpSum = 0;
    if (chars.size() > 1)
      return 0;
    for (int i = 0; i < chars.size();) {
      if (chars.get(i).unit != 1)
        return 0;
      else {
        return parseIntRight(chars.get(i).numbers);
      }
    }
    return tmpSum;
  }

  private static int parseInt(LinkedList<Integer> numbers) {
    // TODO 如何处理 0 和 1 的问题
    if (numbers.size() == 0)
      return 0;
    int tmpSum = 0;
    for (int i = 0; i < numbers.size(); i++) {
      tmpSum = 10 * tmpSum;
      tmpSum += numbers.get(i);
    }
    return tmpSum;
  }

  private static double parseIntRight(LinkedList<Integer> numbers) {
    // TODO 如何处理 0 和 1 的问题
    if (numbers.size() == 0)
      return 0;
    double tmpSum = 0.;
    for (int i = numbers.size() - 1; i >= 0; i--) {
      tmpSum = tmpSum / 10;
      tmpSum += numbers.get(i);
    }
    tmpSum = tmpSum / 10;
    return tmpSum;
  }

  private static int insertIntoDecreasingArray(LinkedList<int[]> descArray, int maxNum,
      int unitValue, int unitPos) {
    boolean updated = false;
    int updatedMaxNum = maxNum;
    for (int i = 0; i < maxNum; i++) {
      if (unitValue >= descArray.get(i)[0]) {
        updatedMaxNum = i + 1;
        descArray.get(i)[0] = unitValue;
        descArray.get(i)[1] = unitPos;
        updated = true;
        break;
      }
    }
    if (!updated) {
      if (descArray.size() <= maxNum + 1) {
        for (int i = 1; i <= maxNum + 1 - descArray.size(); i++)
          descArray.add(new int[] {1, 1});
      }
      descArray.get(maxNum)[0] = unitValue;
      descArray.get(maxNum)[1] = unitPos;
      updatedMaxNum = maxNum + 1;
    }
    return updatedMaxNum;
  }
}
