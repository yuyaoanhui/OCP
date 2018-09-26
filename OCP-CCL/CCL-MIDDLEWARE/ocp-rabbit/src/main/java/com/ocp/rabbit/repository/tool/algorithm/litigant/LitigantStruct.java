package com.ocp.rabbit.repository.tool.algorithm.litigant;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 诉讼人物结构
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class LitigantStruct {
  private String label;// 诉讼人物标签,比如被告
  private boolean referFlg;// 是否是人物指代
  private Pattern patt;// 人物指代的匹配模式
  private List<String> names;// 该类型人物名字列表

  public LitigantStruct(String label, boolean referFlg, Pattern patt) {
    this.label = label;
    this.referFlg = referFlg;
    this.patt = patt;
  }

  public LitigantStruct(String label, boolean referFlg, List<String> names) {
    this.label = label;
    this.referFlg = referFlg;
    this.names = names;
  }

  /**
   * 获取诉讼人物类型，比如被告
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public String getLabel() {
    return label;
  }

  /**
   * 是否是人物指代
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public boolean getReferFlg() {
    return referFlg;
  }

  /**
   * 人物指代的匹配模式
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public Pattern getPattern() {
    return patt;
  }

  /**
   * 人物名字
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public List<String> getNames() {
    return names;
  }
}
