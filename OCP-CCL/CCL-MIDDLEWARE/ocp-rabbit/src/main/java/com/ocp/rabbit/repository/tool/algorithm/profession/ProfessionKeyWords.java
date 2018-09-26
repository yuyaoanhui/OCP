package com.ocp.rabbit.repository.tool.algorithm.profession;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class ProfessionKeyWords {
  public String lastWord;
  public String sectorWord;

  public ProfessionKeyWords(String lastWord, String sectorWord) {
    this.lastWord = lastWord;
    this.sectorWord = sectorWord;
  }

  public ProfessionKeyWords(String lastWord) {
    this.lastWord = lastWord;
  }


  @Override
  public int hashCode() {
    return (lastWord + "，" + sectorWord).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this.hashCode() == obj.hashCode())
      return true;
    return false;
  }

  public String toString() {
    return sectorWord + "，" + lastWord;
  }
}
