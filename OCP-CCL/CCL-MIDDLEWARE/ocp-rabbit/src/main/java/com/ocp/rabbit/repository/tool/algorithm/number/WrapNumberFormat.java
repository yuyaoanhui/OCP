package com.ocp.rabbit.repository.tool.algorithm.number;

import com.ocp.rabbit.repository.util.Position;

/**
 * 日期格式包装类
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class WrapNumberFormat {
  private boolean order;
  private String number;
  private double arabicNumber;
  private int peopNumber;
  private String unit;
  private Position position;

  public WrapNumberFormat(boolean order, String number, String unit, Position position) {
    this.order = order;
    this.number = number;
    this.unit = unit;
    this.position = position;
  }



  public WrapNumberFormat(boolean order, double arabicNumber, String unit) {
    super();
    this.order = order;
    this.arabicNumber = arabicNumber;
    this.unit = unit;
  }

  public WrapNumberFormat(boolean order, int peopNumber, String unit) {
    super();
    this.order = order;
    this.peopNumber = peopNumber;
    this.unit = unit;
  }


  public double getArabicNumber() {
    return arabicNumber;
  }

  public int getPeopNumber() {
    return peopNumber;
  }

  public void setPeopNumber(int peopNumber) {
    this.peopNumber = peopNumber;
  }

  public void setArabicNumber(double arabicNumber) {
    this.arabicNumber = arabicNumber;
  }

  public boolean isOrder() {
    return order;
  }

  public void setOrder(boolean order) {
    this.order = order;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  @Override
  public String toString() {
    return number + unit;
  }

  public String replacedString() {
    if (arabicNumber - (int) arabicNumber == 0)
      return String.valueOf((int) arabicNumber);
    else
      return String.valueOf(arabicNumber);
  }
}
