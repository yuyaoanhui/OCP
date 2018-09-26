package com.ocp.rabbit.repository.entity;

/**
 * 命名实体输出结果，source ： 原文表述 offset: 位置 info:标准化后的信息
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class NamedEntity {

  public String source;
  public int offset;
  public Object info;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  String type;

  public NamedEntity(String source, int offset, Object info) {
    this.source = source;
    this.offset = offset;
    this.info = info;
  }

  public NamedEntity(String source, int offset, Object info, String type) {
    this.source = source;
    this.offset = offset;
    this.info = info;
    this.type = type;
  }

  @Override
  public String toString() {
    return "NamedEntity{" + "source='" + source + '\'' + ", offset=" + offset + ", info=" + info
        + ", type='" + type + '\'' + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    NamedEntity that = (NamedEntity) o;

    if (offset != that.offset)
      return false;
    return source.equals(that.source);

  }

  @Override
  public int hashCode() {
    int result = source.hashCode();
    result = 31 * result + offset;
    return result;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }

  public Object getInfo() {
    return info;
  }

  public void setInfo(Object info) {
    this.info = info;
  }
}
