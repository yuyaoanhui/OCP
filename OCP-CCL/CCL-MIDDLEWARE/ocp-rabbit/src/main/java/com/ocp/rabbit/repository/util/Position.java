package com.ocp.rabbit.repository.util;

/**
 * 辅助类用来记录正则表达式匹配的位置
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class Position {

  public String label;// 诉讼人物类型，比如原告、被告
  public String value;// 待匹配的字符串:包含人名
  public int para;// 段落编号
  public int sentence;// 句子在段落中的位置
  public int sentenceByComma;// 标点位置
  public int pos_of_sentenceByComma;// 从该位置开始有一个value能匹配上
  public int pos_of_sentence;
  private Object info;// 是否做人物指代

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Position position = (Position) o;
    if (!label.equals(position.label))
      return false;
    return value.equals(position.value);
  }

  @Override
  public int hashCode() {
    int result = label.hashCode();
    result = 31 * result + value.hashCode();
    return result;
  }

  public Position(String label, int para, int sentenceByComma, int pos_of_sentenceByComma) {
    this.label = label;
    this.para = para;
    this.sentenceByComma = sentenceByComma;
    this.pos_of_sentenceByComma = pos_of_sentenceByComma;
  }

  public Position(String label, int para, int sentenceByComma) {
    this.label = label;
    this.para = para;
    this.sentenceByComma = sentenceByComma;
  }

  public Position(String value, String label, int para, int sentenceByComma,
      int pos_of_sentenceByComma) {
    this.value = value;
    this.label = label;
    this.para = para;
    this.sentenceByComma = sentenceByComma;
    this.pos_of_sentenceByComma = pos_of_sentenceByComma;
  }

  public Position(String value, String label, int pos_of_sentenceByComma, boolean flag) {
    this.value = value;
    this.label = label;
    this.pos_of_sentenceByComma = pos_of_sentenceByComma;
    this.info = flag;
  }

  public Position(String value, String label, int para, int sentenceByComma) {
    this.value = value;
    this.label = label;
    this.para = para;
    this.sentenceByComma = sentenceByComma;
  }


  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public int getPos_of_sentenceByComma() {
    return pos_of_sentenceByComma;
  }

  public void setPos_of_sentenceByComma(int pos_of_sentenceByComma) {
    this.pos_of_sentenceByComma = pos_of_sentenceByComma;
  }

  public int getPos_of_sentence() {
    return pos_of_sentence;
  }

  public void setPos_of_sentence(int pos_of_sentence) {
    this.pos_of_sentence = pos_of_sentence;
  }

  public int getSentence() {
    return sentence;
  }

  public void setSentence(int sentence) {
    this.sentence = sentence;
  }

  public int getSentenceByComma() {
    return sentenceByComma;
  }

  public void setSentenceByComma(int sentenceByComma) {
    this.sentenceByComma = sentenceByComma;
  }

  public int getPara() {
    return para;
  }

  public void setPara(int para) {
    this.para = para;
  }

  public Object getInfo() {
    return info;
  }

  public void setInfo(Object info) {
    this.info = info;
  }
}
