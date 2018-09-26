package com.ocp.rabbit.repository.tool.algorithm.law;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.tool.algorithm.number.Number2Ch;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class LawContainer {
  /**
   * 法律条文的标题，条目数 后续加入时间，法律内容，然后还可以通过这个类管理法律之间的层级关系 有四个构造函数 （1）只有法律名字 （2）法律名字，第XX条 （3）法律名字，第XX条第XX项
   * （4）法律名字，第XX条第XX款第XX项
   */
  private String lawName;
  /**
   * 第XX条
   */
  private int tiao;
  /**
   * 第XX项
   */
  private int xiang;
  /**
   * 第XX款
   */
  private int kuan;

  // 法律名字和条框项对应的id,前32位对应法律名，5位对应条，3位对应款，3位对应项
  private String lawItemId;

  /**
   * 记录 法律名字，条／条之，款，项的具体位置，8维数组，采用绝对位置记录，即第一个字符位置为相对于全文的偏移量 记录开始位置和结束位置，惯例采用substring的start，end惯例
   * 分别表示： 法律名字start,法律名字end; 条／条之start,条／条之end; 款start,款end; 项start,项end 对应的位置分别是：0,1, 2,3, 4,5,
   * 6,7
   *
   */
  private int[] pos = {-1, -1, -1, -1, -1, -1, -1, -1};

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    LawContainer that = (LawContainer) o;

    if (tiao != that.tiao)
      return false;
    if (xiang != that.xiang)
      return false;
    if (kuan != that.kuan)
      return false;
    return lawName.equals(that.lawName);

  }

  @Override
  public int hashCode() {
    int result = lawName.hashCode();
    result = 31 * result + tiao;
    result = 31 * result + xiang;
    result = 31 * result + kuan;
    return result;
  }

  public LawContainer(String name) {
    this.lawName = name;
    this.tiao = -1;
    this.kuan = -1;
    this.xiang = -1;

  }

  public LawContainer(String name, int tiao) {
    this.lawName = name;
    this.tiao = tiao;
    this.kuan = -1;
    this.xiang = -1;
  }

  public LawContainer(String name, int tiao, int kuan) {
    this.lawName = name;
    this.tiao = tiao;
    this.kuan = kuan;
    this.xiang = -1;
  }

  public LawContainer(String name, int tiao, int kuan, int xiang) {
    this.lawName = name;
    this.tiao = tiao;
    this.kuan = kuan;
    this.xiang = xiang;
  }

  @Override

  public String toString() {

    StringBuffer sb = new StringBuffer(lawName);
    if (tiao != -1) {
      sb.append("第").append(Number2Ch.number2Chinese(tiao / 100)).append("条");
      if (tiao % 100 != 0)
        sb.append("之").append(Number2Ch.number2Chinese(tiao % 100));
    }
    if (kuan != -1) {
      sb.append("第").append(Number2Ch.number2Chinese(kuan)).append("款");
    }
    if (xiang != -1) {
      sb.append("第").append(Number2Ch.number2Chinese(xiang)).append("项");
    }
    return sb.toString();
  }

  public String getLawName() {
    return lawName;
  }

  public void setLawName(String name) {
    this.lawName = name;
  }

  public int getTiao() {
    return tiao;
  }

  public void setTiao(int tiao) {
    this.tiao = tiao;
  }

  public int getXiang() {
    return xiang;
  }

  public void setXiang(int xiang) {
    this.xiang = xiang;
  }

  public int getKuan() {
    return kuan;
  }

  public void setKuan(int kuan) {
    this.kuan = kuan;
  }

  public String getLawItemId() {
    return lawItemId;
  }

  public void setLawItemId(String lawItemId) {
    this.lawItemId = lawItemId;
  }

  public int[] getPos() {
    return pos;
  }

  /**
   * 给定位置，得到该位置的取值。如果不合法的位置，返回-1。
   * 
   * @param index
   * @return
   */
  public int getPos(int index) {
    if (index >= 0 && index <= 7)
      return pos[index];
    return -1;
  }

  public void setPos(int[] pos) {
    this.pos = pos;
  }

  /**
   * 通过设定指定位置的值来改变取值
   * 
   * @param index
   * @param posValue
   */
  public void setPos(int index, int posValue) {
    if (index >= 0 && index <= 7 && posValue >= 0) {
      this.pos[index] = posValue;
    }
  }

  /**
   * 通过子字符串的左右边界，设定位置。end表示最后一个字符的下一位，类似substring
   * 
   * @param fromIndex
   * @param endIndex
   * @param fromIndexPosValue
   * @param endIndexPosValue
   */
  public void setPos(int fromIndex, int endIndex, int fromIndexPosValue, int endIndexPosValue) {
    if (fromIndex >= 0 && fromIndex <= 7 && endIndex >= 0 && endIndex <= 7 && fromIndexPosValue >= 0
        && endIndexPosValue >= 0) {
      this.pos[fromIndex] = fromIndexPosValue;
      this.pos[endIndex] = endIndexPosValue;
    }
  }


  private static final Pattern PATTERN_LAWNAME = Pattern.compile("(《.*》)(.*)$");

  public static String convertFormat(String law) {
    java.util.regex.Matcher matcher = PATTERN_LAWNAME.matcher(law);

    if (matcher.find()) {
      List<Integer> parts = new ArrayList<>();
      java.util.regex.Matcher matcherNumber = Pattern.compile("\\d+").matcher(matcher.group(2));
      while (matcherNumber.find()) {
        parts.add(1);
      }
    }
    return law;
  }
}
