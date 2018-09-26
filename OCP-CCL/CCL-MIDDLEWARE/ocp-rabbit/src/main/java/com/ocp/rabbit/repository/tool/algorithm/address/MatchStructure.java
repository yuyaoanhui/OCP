package com.ocp.rabbit.repository.tool.algorithm.address;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class MatchStructure {
  int start;
  int end;
  String name;
  String sent;
  boolean ifNameContainsKw;
  String code;
  boolean inferred;

  public String toString() {
    return name + ":" + start + "->" + end;
  }

  public MatchStructure(int start, int end) {
    this.start = start;
    this.end = end;
    inferred = false;
  }

  public MatchStructure(int start, int end, String name) {
    this.start = start;
    this.end = end;
    this.name = name;
    ifNameContainsKw = false;
    sent = "";
    inferred = false;
  }

  public MatchStructure(int start, int end, String name, String sent) {
    this.start = start;
    this.end = end;
    this.name = name;
    this.sent = sent;
    ifNameContainsKw = false;
    inferred = false;
  }

  public MatchStructure(int start, int end, String name, String sent, boolean ifNameContainsKw) {
    this.start = start;
    this.end = end;
    this.name = name;
    this.sent = sent;
    this.ifNameContainsKw = ifNameContainsKw;
    inferred = false;
  }

  public MatchStructure(int start, int end, String name, String sent, boolean ifNameContainsKw,
      String code) {
    this.start = start;
    this.end = end;
    this.name = name;
    this.sent = sent;
    this.ifNameContainsKw = ifNameContainsKw;
    this.code = code;
    inferred = false;
  }

  public static int compare(MatchStructure ms1, MatchStructure ms2) {
    int result = -1;
    if (!ms1.inferred && ms2.inferred)
      return result;
    else if (ms1.inferred && !ms2.inferred)
      return -result;
    // here ms1.inferred=ms2.inferred;
    if (ms1.ifNameContainsKw) {
      if (!ms2.ifNameContainsKw) {
        return result;
      } else {
        if (ms1.start < ms2.start) {
          return result;
        } else if (ms1.start > ms2.start) {
          return -result;
        } else if (ms1.start == ms2.start) {
          if (ms1.end > ms2.end)
            return result;
          else if (ms1.end < ms2.end)
            return -result;
          else
            return 0;
        }
      }

    } else {
      if (ms2.ifNameContainsKw) {
        return -result;
      } else {
        if (ms1.start < ms2.start) {
          return result;
        } else if (ms1.start > ms2.start) {
          return -result;
        } else if (ms1.start == ms2.start) {
          if (ms1.end > ms2.end)
            return result;
          else if (ms1.end < ms2.end)
            return -result;
          else
            return 0;
        }
      }
    }
    return 0;
  }
}
