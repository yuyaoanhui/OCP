package com.ocp.rabbit.repository.tool.algorithm.address;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class SignalStructure {

  public KeyWordReplace prov;
  public KeyWordReplace city;
  public KeyWordReplace county;
  public KeyWordReplace street;
  // 以上四种变量为临时变量

  public List<MatchStructure> provMatches;
  public List<MatchStructure> cityMatches;
  public List<MatchStructure> countyMatches;
  public List<MatchStructure> streetMatches;
  public boolean[] conflicts;// 4 level confilct;
  public boolean[][] conflictMatrix;
  public String[] pair1, pair2, pair3, pair4, pair5, pair6;
  public MatchStructure[] mspair1, mspair2, mspair3, mspair4, mspair5, mspair6;

  public SignalStructure(KeyWordReplace prov, KeyWordReplace city, KeyWordReplace county,
      KeyWordReplace street) {
    this.prov = prov;
    this.city = city;
    this.county = county;
    this.street = street;
    provMatches = new ArrayList<>();
    cityMatches = new ArrayList<>();
    countyMatches = new ArrayList<>();
    streetMatches = new ArrayList<>();
    conflicts = new boolean[] {false, false, false, false};
    conflictMatrix = new boolean[][] {
        // 全国，省，市，县，乡
        {false, false, false, false, false}, // 省
        {false, false, false, false, false}, // 市
        {false, false, false, false, false}, // 县
        {false, false, false, false, false},// 乡
    };
  }
}
