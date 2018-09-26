package com.ocp.rabbit.repository.tool.algorithm.address;

import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class SelectRules {
  // 决定从一堆匹配的结果里选择一个省份
  public static String selectOneProv(List<MatchStructure> lm) {
    Comparator<MatchStructure> compareProv = new CompareArea();
    if (lm.size() != 0) {
      MatchStructure mst = lm.get(0);
      for (MatchStructure ele : lm) {
        int result = compareProv.compare(mst, ele);
        if (result > 0) {// mst>ele
          mst = ele;
        }
      }
      return mst.code;
    }
    return null;
  }

  // The same as for prov. to modify
  public static String selectOneCity(List<MatchStructure> lm) {
    Comparator<MatchStructure> compareCity = new CompareArea();
    if (lm.size() != 0) {
      MatchStructure mst = lm.get(0);
      for (MatchStructure ele : lm) {
        int result = compareCity.compare(mst, ele);
        if (result > 0) {// mst>ele
          mst = ele;
        }
      }
      return mst.code;
    }
    return null;
  }

  public static String selectOneCounty(List<MatchStructure> lm) {
    Comparator<MatchStructure> compareCounty = new CompareArea();
    if (lm.size() != 0) {
      MatchStructure mst = lm.get(0);
      for (MatchStructure ele : lm) {
        int result = compareCounty.compare(mst, ele);
        if (result > 0) {// mst>ele
          mst = ele;
        }
      }
      return mst.code;
    }
    return null;
  }

  public static String selectOneCounty(List<MatchStructure> lm, MatchStructure city) {
    Comparator<MatchStructure> compareCounty = new CompareArea();
    if (lm.size() != 0) {
      MatchStructure mst = lm.get(0);
      for (MatchStructure ele : lm) {
        int result = compareCounty.compare(mst, ele);
        if (result > 0) {// mst>ele
          mst = ele;
        }
      }
      return mst.code;
    }
    return null;
  }

  public static String selectOneStreet(List<MatchStructure> lm) {
    Comparator<MatchStructure> compareStreet = new CompareArea();
    if (lm.size() != 0) {
      MatchStructure mst = lm.get(0);
      for (MatchStructure ele : lm) {
        int result = compareStreet.compare(mst, ele);
        if (result > 0) {// mst>ele
          mst = ele;
        }
      }
      return mst.code;
    }
    return null;
  }

  public static String[] selectFromProv(SignalStructure signal) {
    String[] codes = new String[4];
    if (signal.provMatches.size() > 0) {
      codes[0] = selectOneProv(signal.provMatches);
    }
    return codes;
  }

  public static String[] selectFromCity(SignalStructure signal) {
    String[] codes = new String[4];
    if (signal.cityMatches.size() < 1)
      return selectFromProv(signal);
    if (signal.pair1 != null) {
      codes[0] = signal.pair1[0];
      codes[1] = signal.pair1[1];
    } else {
      String prov = selectOneProv(signal.provMatches);
      // 如果有矛盾，但是prov含有关键字。
      if (NumOfMatchLevelWithKw(signal, 1) > 0) {
        codes[0] = prov;
        return codes;
      } else {
        codes[1] = selectOneCity(signal.cityMatches);
        return codes;
      }
    }
    return codes;
  }

  public static String[] selectFromCounty(SignalStructure signal) {
    String[] codes = new String[4];
    if (signal.countyMatches.size() < 1)
      return selectFromCity(signal);
    // if(findSameCodeWithKw(signal.countyMatches,signal.pair4[1]))
    if (signal.pair4 != null && signal.pair2 != null) {
      if (signal.pair4[1].equals(signal.pair2[1])) {
        // 如果县的匹配位置包含在市里面，而且都和省份不冲突，则丢弃这种情况。
        if (signal.mspair4[1].start >= signal.mspair4[0].start
            && signal.mspair4[1].end <= signal.mspair4[0].end) {
          return selectFromCity(signal);
        } else {
          codes[1] = signal.pair4[0];
          codes[2] = signal.pair4[1];
        }
      } else {
        // 如果市有关键词或市在省前面，忽略市
        if (MatchStructure.compare(signal.mspair4[0], signal.mspair2[0]) < 0) {
          codes[1] = signal.pair4[0];
          codes[2] = signal.pair4[1];
          codes[0] = null;
        } else {
          codes[0] = signal.pair2[0];
          codes[2] = signal.pair2[1];
          codes[1] = null;
        }
      }
    }
    // 市优先
    else if (signal.pair4 != null) {
      // if(findSameCodeWithKw(signal.countyMatches,signal.pair4[1]))
      // 市和区，市和省
      // 如果区在市前面，忽略区
      if (signal.mspair1 != null && signal.mspair4 != null) {
        if (MatchStructure.compare(signal.mspair4[1], signal.mspair1[1]) < 0)
          return selectFromCity(signal);
      }
      if (signal.mspair4[1].start >= signal.mspair4[0].start
          && signal.mspair4[1].end <= signal.mspair4[0].end) {
        return selectFromCity(signal);
      } else {
        codes[1] = signal.pair4[0];
        codes[2] = signal.pair4[1];
        codes[0] = null;
      }
    }
    // 省
    else if (signal.pair2 != null) {
      // 区和省，市和省
      // 如果市在区前面，并且市和省搭配，忽略区
      if (signal.mspair1 != null && signal.mspair2 != null) {
        if (MatchStructure.compare(signal.mspair1[1], signal.mspair2[1]) < 0)
          return selectFromCity(signal);
      }
      codes[0] = signal.pair2[0];
      codes[2] = signal.pair2[1];
      codes[1] = null;
    } else {
      String code = selectOneCounty(signal.countyMatches);
      codes = selectFromCity(signal);
      if (codes[0] == null && codes[1] == null) {
        codes[2] = code;
        return codes;
      }
      // 有关键词
      if (NumOfMatchWithKw(signal.cityMatches) > 0) {
        return codes;
      }
      if (signal.countyMatches.get(0).ifNameContainsKw) {
        if (signal.provMatches.size() == 0) {
          codes[2] = code;
          return codes;
        }
        // 都没有关键字
        codes[2] = code;
        return codes;
      }
      // 和前面不搭配，并且没有关键词,city,prov不都非空
      else {
        return codes;
      }
    }
    return codes;
  }

  public static String[] selectFromStreet(SignalStructure signal) {
    String[] codes = new String[4];
    if (signal.streetMatches.size() < 1)
      return selectFromCounty(signal);
    // 县优先
    if (signal.pair6 != null) {
      codes[3] = signal.pair6[1];
      codes[2] = signal.pair6[0];
    }
    // 市
    else if (signal.pair5 != null) {
      codes[3] = signal.pair5[1];
      codes[1] = signal.pair5[0];
    }
    // 省
    else if (signal.pair3 != null) {
      codes[3] = signal.pair3[1];
      codes[0] = signal.pair3[0];
    } else {
      codes = selectFromCounty(signal);
      int num = NumOfMatchLevelWithKw(signal, 3);
      if (num >= 1)
        return codes;
      else {
        codes[3] = selectOneStreet(signal.streetMatches);
      }
      return codes;
    }
    return codes;
  }

  public static int NumOfMatchLevelWithKw(SignalStructure signal, int level) {
    int tempSum = 0, prov = 0;
    if (level >= 1) {
      prov = NumOfMatchWithKw(signal.provMatches);
      if (prov == 1)
        tempSum += prov;
    }
    if (level >= 2) {
      prov = NumOfMatchWithKw(signal.cityMatches);
      if (prov == 1)
        tempSum += prov;
    }
    if (level >= 3) {
      prov = NumOfMatchWithKw(signal.countyMatches);
      if (prov == 1)
        tempSum += prov;
    }
    if (level >= 4) {
      prov = NumOfMatchWithKw(signal.streetMatches);
      if (prov == 1)
        tempSum += prov;
    }
    return tempSum;
  }

  public static int NumOfMatchWithKw(List<MatchStructure> lm) {
    if (lm.size() == 0)
      return -1;
    for (MatchStructure aLm : lm) {
      if (aLm.ifNameContainsKw)
        return 1;
    }
    return 0;
  }

  public static boolean findSameCodeWithKw(List<MatchStructure> lm, String code) {
    for (MatchStructure ms : lm) {
      if (ms.code.equals(code) && ms.ifNameContainsKw)
        return true;
    }
    return false;
  }

  public static MatchStructure findMatchStructure(List<MatchStructure> lm, String code) {
    for (MatchStructure ms : lm) {
      if (ms.code.equals(code))
        return ms;
    }
    return null;
  }

  private static class CompareArea implements Comparator<MatchStructure> {
    @Override
    public int compare(MatchStructure ms1, MatchStructure ms2) {
      int result = -1;
      if (!ms1.inferred && ms2.inferred)
        return result;
      else if (ms1.inferred && !ms2.inferred)
        return -result;
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
}
