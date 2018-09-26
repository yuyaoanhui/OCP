package com.ocp.rabbit.repository.tool.algorithm.law;

/**
 * 法律法规标签类
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class UrlLabel {

  /**
   * 法律法规名称标签
   */
  public static final String LABEL_TYPE_LAW_NAME = "labelTypeLaw";
  /**
   * 法律法规条目标签 条
   */
  public static final String LABEL_TYPE_LAW_ITEM_TIAO = "tiao";
  /**
   * 法律法规条目标签 款
   */
  public static final String LABEL_TYPE_LAW_ITEM_KUAN = "kuan";
  /**
   * 法律法规条目标签 项
   */
  public static final String LABEL_TYPE_LAW_ITEM_XIANG = "xiang";
  /**
   * 标红标签
   */
  public static final String LABEL_TYPE_EM = "labelTypeEm";
  /**
   * 信息点位置标签
   */
  public static final String LABEL_TYPE_INFO = "labelTypeInfo";


  /**
   * 标签类别
   */
  private String labelType;
  /**
   * 左边标签内容
   */
  private String labelContentLeft;

  /**
   * 右边标签内容
   */
  private String labelContentRight;

  /**
   * 左边标签开始位置
   */
  private int labelStart;
  /**
   * 右边标签开始位置
   */
  private int labelEnd;

  public String getLabelType() {
    return labelType;
  }

  public void setLabelType(String labelType) {
    this.labelType = labelType;
  }

  public String getLabelContentLeft() {
    return labelContentLeft;
  }

  public void setLabelContentLeft(String labelContentLeft) {
    this.labelContentLeft = labelContentLeft;
  }

  public String getLabelContentRight() {
    return labelContentRight;
  }

  public void setLabelContentRight(String labelContentRight) {
    this.labelContentRight = labelContentRight;
  }

  public int getLabelStart() {
    return labelStart;
  }

  public void setLabelStart(int labelStart) {
    this.labelStart = labelStart;
  }

  public int getLabelEnd() {
    return labelEnd;
  }

  public void setLabelEnd(int labelEnd) {
    this.labelEnd = labelEnd;
  }


  public UrlLabel() {}

  public UrlLabel(String labelType, String labelContentLeft, String labelContentRight,
      int labelStart, int labelEnd) {
    this.labelType = labelType;
    this.labelContentLeft = labelContentLeft;
    this.labelContentRight = labelContentRight;
    this.labelStart = labelStart;
    this.labelEnd = labelEnd;
  }

  public UrlLabel(UrlLabel urlLabel) {
    this.labelType = urlLabel.getLabelType();
    this.labelContentLeft = urlLabel.getLabelContentLeft();
    this.labelContentRight = urlLabel.getLabelContentRight();
    this.labelStart = urlLabel.getLabelStart();
    this.labelEnd = urlLabel.getLabelEnd();
  }


  @Override
  public String toString() {
    return "UrlLabel{" + "labelType='" + labelType + '\'' + ", labelContentLeft='"
        + labelContentLeft + '\'' + ", labelContentRight='" + labelContentRight + '\''
        + ", labelStart=" + labelStart + ", labelEnd=" + labelEnd + '}';
  }
}
