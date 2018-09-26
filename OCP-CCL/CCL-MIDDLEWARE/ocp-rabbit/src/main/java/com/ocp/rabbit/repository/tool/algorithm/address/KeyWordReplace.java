package com.ocp.rabbit.repository.tool.algorithm.address;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class KeyWordReplace {
  public boolean ifFind;
  public String replacedAddr;
  public String nameWithKeyWord;

  public KeyWordReplace(boolean ifFind, String replacedAddr, String nameWithKeyWord) {
    this.ifFind = ifFind;
    this.replacedAddr = replacedAddr;
    this.nameWithKeyWord = nameWithKeyWord;
  }

  public KeyWordReplace() {}
}
