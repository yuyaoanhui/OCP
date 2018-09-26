package com.ocp.rabbit.repository.tool.algorithm.date;

import java.util.ArrayList;
import java.util.List;

import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;

/**
 * 偏移量调整
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class OffsetAdjustment {

  String str;
  List<WrapNumberFormat> lwnf;
  List<int[]> src;
  int[] targetStart;
  int[] targetEnd;
  String srcModified;

  public OffsetAdjustment(String str) {
    this.str = str;
    lwnf = new NumberRecognizer().getNumbersForTime(new String[] {str}, false);
    src = new ArrayList<>();
    targetStart = new int[lwnf.size()];
    targetEnd = new int[lwnf.size()];
    srcModified = getPositionMapper(str);
  }

  /**
   * 用于处理时间时，把不规范的数字转成规范数字时候，字符串的对应位置会有变化，因此会有调整 Note: 数字识别的时候会把 "点" 转变成 "."，因此要转化回来。比如
   * 15点20分，数字识别会识别成15.20
   * 
   * @param str
   * @return
   */
  private String getPositionMapper(String str) {
    int prevDiff = 0;
    StringBuffer sb = new StringBuffer(str);
    for (int i = 0; i < lwnf.size(); i++) {
      WrapNumberFormat wnf = lwnf.get(i);
      int offset = wnf.getPosition().getPos_of_sentenceByComma();
      int afterLength = wnf.replacedString().length();
      int beforeLength = wnf.getNumber().length();
      src.add(new int[] {offset, offset + beforeLength - 1});
      targetStart[i] = offset + prevDiff;
      targetEnd[i] = offset + prevDiff + afterLength - 1;
      sb.replace(prevDiff + offset, prevDiff + offset + beforeLength, wnf.replacedString());
      // 更新字符位移差值
      prevDiff = prevDiff + afterLength - beforeLength;
    }
    return sb.toString();
  }


  public String getOriginalPosition(int[] offset, int[] offsetAdjust) {

    if (targetStart.length <= 0) {
      offsetAdjust[0] = offset[0];
      offsetAdjust[1] = offset[1];
      return str.substring(offset[0], offset[1] + 1);
    }

    int pos1 = binarySearch(targetStart, offset[0]);
    int pos2 = binarySearch(targetStart, offset[1]);
    int posStart = 0, posEnd = 0;
    if (pos1 < 0) {
      posStart = offset[0];
    } else {
      if (offset[0] <= targetEnd[pos1]) {
        // 在区间内部，设定为原始左边界
        posStart = src.get(pos1)[0];
      } else {
        // 在区间和区间之间，设定为原始右边界的值加上位移
        posStart = src.get(pos1)[1] + offset[0] - targetEnd[pos1];
      }
    }

    if (pos2 < 0) {
      posEnd = offset[1];
    } else {
      if (offset[1] <= targetEnd[pos2]) {
        // 在区间内部，设定为右边界。
        posEnd = src.get(pos2)[1];
      } else {
        // 区间和区间之间，设定为原始右边界的值加上位移
        posEnd = src.get(pos2)[1] + offset[1] - targetEnd[pos2];
      }
    }

    offsetAdjust[0] = posStart;
    offsetAdjust[1] = posEnd;
    return str.substring(posStart, posEnd + 1);
  }

  /**
   *
   * @param a 排序好的数组
   * @param key 要查找的数
   * @return 数组中的位置，比如 [1,4,6,7],key = 5 则位置在2，(4和6中间)
   */
  private static int binarySearch(int[] a, int key) {

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

}
