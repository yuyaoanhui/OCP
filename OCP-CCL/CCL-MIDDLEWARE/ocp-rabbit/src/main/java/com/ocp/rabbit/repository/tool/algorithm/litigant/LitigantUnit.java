package com.ocp.rabbit.repository.tool.algorithm.litigant;

import java.util.Arrays;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class LitigantUnit {
  /**
   * 记录句子中当事人信息<br>
   * String litigantExpression;<br>
   * 句子中找到的描述当事人的substring<br>
   * int offset : 描述当事人substring的起始位置<br>
   * String[] names : 描述当事人这个substring对应的名字<br>
   * String label : 描述当事人substring对应的当事人类型 {"被告","原告","原被告"}<br>
   * example: String s = "本案案件受理费减半收取3200元，由被告负担2240元，被告李仲文在继承";<br>
   * 假设被告集合为 List<String> defendants = Arrays.asList(new String[]{"李仲文","仁寿民主汽车运输有限公司"});<br>
   * 里面找到两个LitigantUnit LitigantUnit{litigantExpression='被告', offset=18, names=[李仲文, 仁寿民主汽车运输有限公司]
   * label='被告'}<br>
   * LitigantUnit{litigantExpression='被告李仲文', offset=28, names=[李仲文], label='被告'}<br>
   */
  String expression;
  int offset;
  String[] names;
  String label;

  public static final String LABEL_DEFENDANT = "被告";
  public static final String LABEL_PLAINTIFF = "原告";
  public static final String LABEL_LITIGANT = "原被告";
  public static final String LABEL_SUSPECTS = "嫌疑人";

  public LitigantUnit(String expression, int offset, String[] names, String label) {
    this.expression = expression;
    this.offset = offset;
    this.names = names;
    this.label = label;
  }

  @Override
  public String toString() {
    return "LitigantUnit{" + "litigantExpression='" + expression + '\'' + ", offset=" + offset
        + ", names=" + Arrays.toString(names) + ", label='" + label + '\'' + '}';
  }

  public String getExpression() {
    return expression;
  }

  public int getOffset() {
    return offset;
  }

  public String[] getNames() {
    return names;
  }

  public String getLabel() {
    return label;
  }

  /**
   * 反转人物标签(角色)
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static String reverseLabel(String label) {
    if (label.equals(LABEL_DEFENDANT))
      return LABEL_PLAINTIFF;
    else if (label.equals(LABEL_PLAINTIFF)) {
      return LABEL_PLAINTIFF;
    } else if (label.equals(LABEL_LITIGANT)) {
      return LABEL_LITIGANT;
    } else if (label.equals(LABEL_SUSPECTS)) {
      return LABEL_SUSPECTS;
    } else
      return null;
  }
}
