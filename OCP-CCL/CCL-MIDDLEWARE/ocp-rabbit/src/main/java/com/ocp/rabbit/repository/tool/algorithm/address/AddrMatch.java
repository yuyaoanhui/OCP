package com.ocp.rabbit.repository.tool.algorithm.address;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;

/**
 * 
 * @author yu.yao 2018年8月12日
 *
 */
public class AddrMatch {
  private Map<String, AreaStructure> areaBook; // code to as
  private Map<String, String> codeToName; // code to name
  private Map<String, List<String>> nameToCode; // name to codes because
  // multiple codes share one
  // name;

  private Set<String> provKeyWords;
  private Set<String> cityKeyWords;
  private Set<String> countyKeyWords;
  private Set<String> streetKeyWords;
  // The key means a found name, the value list means possible other matches
  // ex: 朗县->白朗县
  // default county->county
  private Map<String, Set<String>> conflictNames;
  private Map<String, Set<String>> conflictNamesCity;
  private Map<String, Set<String>> conflictNamesCountyCity;

  private Map<String, List<AreaStructure>> provToCities; // code to las
  private Map<String, List<AreaStructure>> cityToCounties; // code to las
  private Map<String, List<AreaStructure>> provToCounties; // code to las
  private Map<String, List<AreaStructure>> countyToStreets; // code to las
  private Map<String, List<AreaStructure>> cityToStreets; // code to las
  private Map<String, Set<AreaStructure>> provToStreets;
  private Map<String, String[]> courtAddrMap;
  // name to code
  private Map<String, String> provNames;
  // standard code to multiple names
  private Map<String, List<String>> provNamesInvert;

  // 一个名字简写可能对应多个标准名字。
  // 标准名字是唯一的。
  // name to codes
  private Map<String, List<String>> cityNames;
  // standard code to multiple names
  private Map<String, List<String>> cityNamesInvert;

  // name to codes
  private Map<String, List<String>> countyNames;
  // standard code to multiple names
  private Map<String, List<String>> countyNamesInvert;

  // name to codes
  // the value list could be very long because certain street names are used
  // in various areas domestically.
  // To have a better result, we use a Set<String> instead of List<String>
  private Map<String, Set<String>> streetNames;

  // standard code to multiple names
  // But here we only allow standard names, that is to say, no syn names for
  // one specific code
  private Map<String, Set<String>> streetNamesInvert;

  private String province, city, county, town;

  public String getProvince(String addr) {
    addrMatch.splitAddr(addr);
    return this.province;
  }

  public String getCity(String addr) {
    addrMatch.splitAddr(addr);
    return this.city;
  }

  public String getCounty(String addr) {
    addrMatch.splitAddr(addr);
    return this.county;
  }

  public String getTown(String addr) {
    addrMatch.splitAddr(addr);
    return this.town;
  }

  public String[] getEachLevel(String addr) {
    String[] result = addrMatch.splitAddr(addr);
    return result;
  }

  static {
    getInstance();
  }

  public static AddrMatch getInstance() {
    try {
      // 读取配置文件
      if (addrMatch == null) {
        synchronized (AddrMatch.class) {
          // 中国地域信息
          InputStream chinaAreaIs = FileUtils.loadProperties("china.area.data");
          // 安徽法院信息
          InputStream anhuiCourtIs = FileUtils.loadProperties("court.anhui.data");
          // 冲突地域信息
          InputStream conflictNamesIs = FileUtils.loadProperties("china.conflict.name.data");
          addrMatch = new AddrMatch(chinaAreaIs, anhuiCourtIs, conflictNamesIs);
        }
      }
      return addrMatch;
    } catch (Exception e) {
      addrMatch = null;
      return null;
    }
  }

  private String[] splitAddr(String inputAddr) {

    String[] groupedAddr = new String[4];
    String[] groupedAddrNames = new String[4];
    // if find prov; if find city; if find county; if find street; if
    // contradictory or ambiguous which should be determined by next level
    // match
    boolean[] groupedAddrFind = new boolean[] {false, false, false, false, false};
    String replaceAddr = inputAddr;
    SignalStructure signal = new SignalStructure(null, null, null, null);
    String[] addrCourt = new String[] {null, null, null, null};
    replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCounty(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseStreet(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);

    String[] codes = inferUpperLevelCode(groupedAddr);
    for (int i = 0; i < 4; i++) {
      if (codes[i] != null && codeToName.containsKey(codes[i])) {
        groupedAddrNames[i] = codeToName.get(codes[i]);
      }
    }
    this.province = groupedAddrNames[0];
    this.city = groupedAddrNames[1];
    this.county = groupedAddrNames[2];
    this.town = groupedAddrNames[3];
    return groupedAddrNames;
  }

  /**
   * 分类地址并得到匹配的具体位置
   */
  public String[] splitAddr(String inputAddr, int[] pos, String[] addrCourt) {
    String[] groupedAddr = new String[4];
    String[] groupedAddrNames = new String[4];
    boolean[] groupedAddrFind = new boolean[] {false, false, false, false, false};
    String replaceAddr = inputAddr;
    SignalStructure signal = new SignalStructure(null, null, null, null);
    replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCounty(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseStreet(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    String[] codes = inferUpperLevelCode(groupedAddr);
    for (int i = 0; i < 4; i++) {
      if (codes[i] != null && codeToName.containsKey(codes[i])) {
        groupedAddrNames[i] = codeToName.get(codes[i]);
      }
    }
    this.province = groupedAddrNames[0];
    this.city = groupedAddrNames[1];
    this.county = groupedAddrNames[2];
    this.town = groupedAddrNames[3];
    int[] _pos = getPos(signal);
    pos[0] = _pos[0];
    pos[1] = _pos[1];
    return groupedAddrNames;
  }

  /**
   * 得到匹配的具体位置
   */
  private static int[] getPos(SignalStructure signal) {

    List<MatchStructure> lms = new ArrayList<>();
    lms.addAll(signal.provMatches);
    lms.addAll(signal.cityMatches);
    lms.addAll(signal.countyMatches);
    lms.addAll(signal.streetMatches);
    lms.sort((o1, o2) -> o1.start - o2.start);
    int[] pos = {-1, -1};
    if (lms.size() > 0) {
      pos[0] = lms.get(0).start;
      pos[1] = lms.get(lms.size() - 1).end;
    }
    return pos;
  }

  /****** add by hcsang begin **************************************/
  private static AddrMatch addrMatch;

  private AddrMatch(InputStream filePath, InputStream courtAddrPath,
      InputStream conflictNamesPath) {

    loadAddrBook(filePath, courtAddrPath, conflictNamesPath);
    genMaps();
    genProvNames();
    genCityNames();
    genCountyNames();
    genStreetNames();
    if (conflictNamesPath == null)
      genConflictNames();

  }

  private void loadAddrBook(InputStream filePath, InputStream courtAddrPath,
      InputStream conflictNamesPath) {
    List<String> areas = FileOperate.readTxtToArrays(filePath, "UTF-8");
    areaBook = new HashMap<>();
    codeToName = new HashMap<>();
    nameToCode = new HashMap<>();
    for (int i = 0; i < areas.size(); i++) {
      String[] items = areas.get(i).split("\t");
      if (items.length != 5)
        continue;
      if (!areaBook.containsKey(items[0])) {
        areaBook.put(items[0], new AreaStructure(items[0], items[1], items[2], items[3], items[4]));
      }
      if (!codeToName.containsKey(items[0])) {
        codeToName.put(items[0], items[1]);
      }
      if (!nameToCode.containsKey(items[1])) {
        List<String> lnc = new ArrayList<>();
        lnc.add(items[0]);
        nameToCode.put(items[1], lnc);
      } else {
        nameToCode.get(items[1]).add(items[0]);
      }
    }
    courtAddrMap = new HashMap<String, String[]>();
    if (courtAddrPath != null) {
      List<String> lines = FileOperate.readTxtToArrays(courtAddrPath, "UTF-8");
      for (String line : lines) {
        String[] thisLine = line.split("\t");
        if (thisLine.length != 6)
          continue;
        if (!courtAddrMap.containsKey(thisLine[1])) {
          String[] addrComp = new String[4];
          for (int j = 0; j < 4; j++) {
            if (!thisLine[j].equals("null"))
              addrComp[j] = thisLine[j + 2];
          }
          courtAddrMap.put(thisLine[1], addrComp);
        }
      }
    }
    provKeyWords = new HashSet<String>();
    provKeyWords.add("省");
    provKeyWords.add("市");
    provKeyWords.add("州");
    provKeyWords.add("区");
    cityKeyWords = new HashSet<String>();
    cityKeyWords.add("市");
    cityKeyWords.add("盟");
    cityKeyWords.add("州");
    countyKeyWords = new HashSet<String>();
    countyKeyWords.add("县");
    countyKeyWords.add("市");
    countyKeyWords.add("旗");
    streetKeyWords = new HashSet<String>();
    streetKeyWords.add("乡");
    streetKeyWords.add("镇");
    streetKeyWords.add("街道");
    conflictNames = new HashMap<>();
    conflictNamesCity = new HashMap<>();
    conflictNamesCountyCity = new HashMap<>();
    if (conflictNamesPath == null) {
      return;
    }
    List<String> lines = FileOperate.readTxtToArrays(conflictNamesPath, "UTF-8");
    for (String line : lines) {
      String[] thisLine = line.split("\t");
      if (thisLine.length != 3) {
        continue;
      }
      Set<String> s_temp;
      switch (thisLine[2]) {
        case "1": {
          if (!conflictNamesCity.containsKey(thisLine[0])) {
            s_temp = new HashSet<>();
            s_temp.add(thisLine[1]);
            conflictNamesCity.put(thisLine[0], s_temp);
          } else
            s_temp = conflictNamesCity.get(thisLine[0]);
          s_temp.add(thisLine[1]);
          break;
        }
        case "2": {
          if (!conflictNames.containsKey(thisLine[0])) {
            s_temp = new HashSet<>();
            s_temp.add(thisLine[1]);
            conflictNames.put(thisLine[0], s_temp);
          } else
            s_temp = conflictNames.get(thisLine[0]);
          s_temp.add(thisLine[1]);
          break;
        }
        default: {
          {
            if (!conflictNamesCountyCity.containsKey(thisLine[0])) {
              s_temp = new HashSet<>();
              s_temp.add(thisLine[1]);
              conflictNamesCountyCity.put(thisLine[0], s_temp);
            } else
              s_temp = conflictNamesCountyCity.get(thisLine[0]);
            s_temp.add(thisLine[1]);
            break;
          }
        }
      }
    }
  }

  private void genMaps() {
    provToCities = new HashMap<>();
    cityToCounties = new HashMap<>();
    provToCounties = new HashMap<>();
    countyToStreets = new HashMap<>();
    cityToStreets = new HashMap<>();
    provToStreets = new HashMap<>();
    for (String s : areaBook.keySet()) {
      AreaStructure as = areaBook.get(s);
      switch (as.level) {
        case "1":
          if (!provToCities.containsKey(as.code)) {
            List<AreaStructure> temp = new ArrayList<>();
            provToCities.put(as.code, temp);
          }
          if (!provToCounties.containsKey(as.code)) {
            List<AreaStructure> temp = new ArrayList<>();
            provToCounties.put(as.code, temp);
          }
          break;
        case "2":
          String t_as = findProvlevel(as.code);
          if (!provToCities.containsKey(t_as)) {
            List<AreaStructure> temp = new ArrayList<>();
            temp.add(as);
            provToCities.put(t_as, temp);
          } else {
            provToCities.get(t_as).add(as);
          }
          if (!cityToCounties.containsKey(as.code)) {
            List<AreaStructure> temp = new ArrayList<>();
            cityToCounties.put(as.code, temp);
          }
          break;
        case "3": {
          String c_as = findCitylevel(as.code);
          String p_as = findProvlevel(as.code);
          if (!provToCounties.containsKey(p_as)) {
            List<AreaStructure> temp = new ArrayList<>();
            temp.add(as);
            provToCounties.put(p_as, temp);
          } else {
            provToCounties.get(p_as).add(as);
          }
          if (!cityToCounties.containsKey(c_as)) {
            List<AreaStructure> temp = new ArrayList<>();
            temp.add(as);
            cityToCounties.put(c_as, temp);
          } else {
            cityToCounties.get(c_as).add(as);
          }
          break;
        }
        default: {
          String c_as = findCitylevel(as.code); // 2
          String p_as = findProvlevel(as.code); // 1
          String county_as = findCountylevel(as.code); // 3
          if (!countyToStreets.containsKey(county_as)) {
            List<AreaStructure> temp = new ArrayList<>();
            temp.add(as);
            countyToStreets.put(county_as, temp);
          } else {
            countyToStreets.get(county_as).add(as);
          }
          if (!cityToStreets.containsKey(c_as)) {
            List<AreaStructure> temp = new ArrayList<>();
            temp.add(as);
            cityToStreets.put(c_as, temp);
          } else {
            cityToStreets.get(c_as).add(as);
          }

          if (!provToStreets.containsKey(p_as)) {
            Set<AreaStructure> temp = new HashSet<>();
            temp.add(as);
            provToStreets.put(p_as, temp);
          } else {
            provToStreets.get(p_as).add(as);
          }
          break;
        }
      }
    }
  }

  private void genProvNames() {
    provNames = new HashMap<>();
    provNamesInvert = new HashMap<>();
    for (String code : provToCities.keySet()) {
      String s = codeToName.get(code);
      if (s.contains("省")) {
        String tempName = s.substring(0, s.indexOf("省"));
        provNames.put(s, code);
        provNames.put(tempName, code);

        List<String> tempL = new ArrayList<>();
        tempL.add(s);
        tempL.add(tempName);
        provNamesInvert.put(code, tempL);
      } else if (s.contains("市")) {
        String tempName = s.substring(0, s.indexOf("市"));
        provNames.put(s, code);
        provNames.put(tempName, code);
        List<String> tempL = new ArrayList<>();
        tempL.add(s);
        tempL.add(tempName);
        provNamesInvert.put(code, tempL);
      } else {
        switch (s) {
          case "广西壮族自治区": {
            provNames.put(s, code);
            provNames.put("广西", code);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add("广西");
            provNamesInvert.put(code, tempL);
            break;
          }
          case "西藏自治区": {
            provNames.put(s, code);
            provNames.put("西藏", code);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add("西藏");
            provNamesInvert.put(code, tempL);
            break;
          }
          case "内蒙古自治区": {
            provNames.put(s, code);
            provNames.put("内蒙", code);
            provNames.put("内蒙古", code);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add("内蒙");
            tempL.add("内蒙古");
            provNamesInvert.put(code, tempL);
            break;
          }
          case "宁夏回族自治区": {
            provNames.put(s, code);
            provNames.put("宁夏", code);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add("宁夏");
            provNamesInvert.put(code, tempL);
            break;
          }
          case "新疆维吾尔自治区": {
            provNames.put(s, code);
            provNames.put("新疆", code);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add("新疆");
            provNamesInvert.put(code, tempL);
            break;
          }
          default: {
            provNames.put(s, code);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            provNamesInvert.put(code, tempL);
          }
        }
      }
    }
  }

  public void genCityNames() {
    cityNames = new HashMap<>();
    cityNamesInvert = new HashMap<>();
    for (String code : cityToCounties.keySet()) {
      String s = codeToName.get(code);
      switch (s) {
        case "市辖区": {
          break;
        }
        case "省直辖行政单位": {
          break;
        }
        case "县": {
          break;
        }
        case "海南州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "湘西土家族苗族自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("湘西", temp);
          cityNames.put("湘西州", temp);
          cityNames.put("湘西自治州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("湘西");
          tempL.add("湘西州");
          tempL.add("湘西自治州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "延边朝鲜族自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("延边", temp);
          cityNames.put("延边州", temp);
          cityNames.put("延边自治州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("延边");
          tempL.add("延边州");
          tempL.add("延边自治州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "博尔塔拉蒙古自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("博尔塔拉", temp);
          cityNames.put("博尔塔拉州", temp);
          cityNames.put("博尔塔拉自治州", temp);
          cityNames.put("博州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("博尔塔拉");
          tempL.add("博尔塔拉州");
          tempL.add("博尔塔拉自治州");
          tempL.add("博州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "巴音郭楞蒙古自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("巴音郭楞", temp);
          cityNames.put("巴音郭楞州", temp);
          cityNames.put("巴音州", temp);
          cityNames.put("巴音郭楞自治州", temp);
          cityNames.put("巴州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("巴音郭楞");
          tempL.add("巴音郭楞州");
          tempL.add("巴音州");
          tempL.add("巴州");
          tempL.add("巴音郭楞自治州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "黔南布依族苗族自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("黔南", temp);
          cityNames.put("黔南自治州", temp);
          cityNames.put("黔南州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("黔南");
          tempL.add("黔南自治州");
          tempL.add("黔南州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "黔东南苗族侗族自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("黔东南", temp);
          cityNames.put("黔东南自治州", temp);
          cityNames.put("黔东南州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("黔东南");
          tempL.add("黔东南自治州");
          tempL.add("黔东南州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "甘孜藏族自治州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put("甘孜", temp);
          cityNames.put("甘孜自治州", temp);
          cityNames.put("甘孜州", temp);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          tempL.add("甘孜");
          tempL.add("甘孜自治州");
          tempL.add("甘孜州");
          cityNamesInvert.put(code, tempL);
          break;
        }
        case "克州": {
          List<String> temp = new ArrayList<>();
          temp.add(code);
          cityNames.put(s, temp);
          List<String> tempL = new ArrayList<>();
          tempL.add(s);
          cityNamesInvert.put(code, tempL);
          break;
        }
        default: {
          if (s.length() >= 2 && (s.lastIndexOf("市") == s.length() - 1
              || s.lastIndexOf("盟") == s.length() - 1 || s.lastIndexOf("州") == s.length() - 1)) {
            String temp = s.substring(0, s.length() - 1);
            List<String> tempList;
            if (!cityNames.containsKey(s)) {
              tempList = new ArrayList<>();
            } else {
              tempList = cityNames.get(s);
            }
            tempList.add(code);
            cityNames.put(s, tempList);
            if (!cityNames.containsKey(temp)) {
              tempList = new ArrayList<>();
            } else {
              tempList = cityNames.get(temp);
            }
            tempList.add(code);
            cityNames.put(temp, tempList);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add(temp);
            cityNamesInvert.put(code, tempL);
          } else if (s.length() >= 3 && s.lastIndexOf("地区") == s.length() - 2) {
            String temp = s.substring(0, s.length() - 2);
            List<String> tempList;
            if (!cityNames.containsKey(s)) {
              tempList = new ArrayList<>();
            } else {
              tempList = cityNames.get(s);
            }
            tempList.add(code);
            cityNames.put(s, tempList);
            if (!cityNames.containsKey(temp)) {
              tempList = new ArrayList<>();
            } else {
              tempList = cityNames.get(temp);
            }
            tempList.add(code);
            cityNames.put(temp, tempList);
            temp = temp + "区";
            if (!cityNames.containsKey(temp)) {
              tempList = new ArrayList<>();
            } else {
              tempList = cityNames.get(temp);
            }
            tempList.add(code);
            cityNames.put(temp, tempList);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            tempL.add(temp);
            cityNamesInvert.put(code, tempL);
          } else {
            // normally the program does not reach here;
            List<String> tempList;
            if (!cityNames.containsKey(s)) {
              tempList = new ArrayList<>();
            } else {
              tempList = cityNames.get(s);
            }
            tempList.add(code);
            cityNames.put(s, tempList);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            cityNamesInvert.put(code, tempL);
          }
        }
      }
    }
  }

  private void genCountyNames() {
    countyNames = new HashMap<>();
    countyNamesInvert = new HashMap<>();
    // 这里简单的给了一种别名，即去掉后面的市区县旗关键字
    String reEx_lastCh = "(.*)[市区县旗]$";
    Matcher m_lastCh;
    Pattern p_lastCh = Pattern.compile(reEx_lastCh, Pattern.CASE_INSENSITIVE);
    for (String code : countyToStreets.keySet()) {
      String s = codeToName.get(code);
      switch (s) {
        case "南沙群岛": {
          break;
        }
        case "中沙群岛的岛礁及其海域": {
          break;
        }
        case "市辖镇": {
          break;
        }
        case "西沙群岛": {
          break;
        }
        default: {
          if (s.length() == 2) {
            List<String> tempList;
            if (!countyNames.containsKey(s)) {
              tempList = new ArrayList<>();
            } else {
              tempList = countyNames.get(s);
            }
            tempList.add(code);
            countyNames.put(s, tempList);
            List<String> tempL = new ArrayList<>();
            tempL.add(s);
            countyNamesInvert.put(code, tempL);
          } else {
            m_lastCh = p_lastCh.matcher(s);
            if (m_lastCh.find()) {
              List<String> tempList;
              if (!countyNames.containsKey(s)) {
                tempList = new ArrayList<>();
              } else {
                tempList = countyNames.get(s);
              }
              tempList.add(code);
              countyNames.put(s, tempList);
              String temp = m_lastCh.group(1);
              if (!countyNames.containsKey(temp)) {
                tempList = new ArrayList<>();
              } else {
                tempList = countyNames.get(temp);
              }
              tempList.add(code);
              countyNames.put(temp, tempList);
              List<String> tempL = new ArrayList<>();
              tempL.add(s);
              tempL.add(temp);
              countyNamesInvert.put(code, tempL);
            } else {
              // normally the program should not arrive here;
              List<String> tempList;
              if (!countyNames.containsKey(s)) {
                tempList = new ArrayList<>();
              } else {
                tempList = countyNames.get(s);
              }
              tempList.add(code);
              countyNames.put(s, tempList);
              List<String> tempL = new ArrayList<>();
              tempL.add(s);
              countyNamesInvert.put(code, tempL);
            }
          }
        }
      }
    }
  }

  private void addNewNames(String code, Set<String> names, Map<String, Set<String>> areaNames,
      Map<String, Set<String>> areaNamesInvert) {
    Set<String> tempSet, tempSetInvert;
    for (String name : names) {
      if (!areaNames.containsKey(name)) {
        tempSet = new HashSet<>();
      } else {
        tempSet = areaNames.get(name);
      }
      tempSet.add(code);
      areaNames.put(name, tempSet);
      //
      if (!areaNamesInvert.containsKey(code)) {
        tempSetInvert = new HashSet<>();
      } else {
        tempSetInvert = areaNamesInvert.get(code);
      }
      tempSetInvert.add(name);
      areaNamesInvert.put(code, tempSetInvert);
    }
  }

  private void genStreetNames() {
    streetNames = new HashMap<>();
    streetNamesInvert = new HashMap<>();
    // For further usage; here we don't use this reg exp.
    String reEx_lastCh = "(.{1,})街道";
    Matcher m_lastCh;
    Pattern p_lastCh = Pattern.compile(reEx_lastCh, Pattern.CASE_INSENSITIVE);
    // We don't have a street names set, so we parse the complete areabook
    // and filter addr that is of level 4
    // To do: XXX街道可以简写为 XX街
    for (String code : areaBook.keySet()) {
      if (!areaBook.get(code).level.equals("4"))
        continue;
      Set<String> names = new HashSet<>();
      String name = codeToName.get(code);
      names.add(name);
      m_lastCh = p_lastCh.matcher(name);
      if (m_lastCh.find()) {
        names.add(m_lastCh.group());
        names.add(m_lastCh.group(1) + "街");
        names.add(m_lastCh.group(1) + "街道办");
        names.add(m_lastCh.group(1) + "街道办事处");
      }
      addNewNames(code, names, streetNames, streetNamesInvert);
    }
  }

  private void genConflictNames() {
    Set<String> s_temp;
    for (String name : countyNames.keySet()) {
      String code = countyNames.get(name).get(0);
      for (String name2 : countyNames.keySet()) {
        String code2 = countyNames.get(name2).get(0);
        if (!code.equals(code2)) {
          if (name2.contains(name)) {
            if (!conflictNames.containsKey(name)) {
              s_temp = new HashSet<>();
              conflictNames.put(name, s_temp);
            } else {
              s_temp = conflictNames.get(name);
            }
            s_temp.add(name2);
          }
        }
      }
    }
    for (String name : cityNames.keySet()) {
      String code = cityNames.get(name).get(0);
      for (String name2 : cityNames.keySet()) {
        String code2 = cityNames.get(name2).get(0);
        if (!code.equals(code2)) {
          if (name2.contains(name)) {
            if (!conflictNamesCity.containsKey(name)) {
              s_temp = new HashSet<>();
              conflictNamesCity.put(name, s_temp);
            } else {
              s_temp = conflictNamesCity.get(name);
            }
            s_temp.add(name2);
          }
        }
      }
    }

    for (String name : cityNames.keySet()) {
      String code = cityNames.get(name).get(0);
      for (String name2 : countyNames.keySet()) {
        String code2 = countyNames.get(name2).get(0);
        if (!code.equals(code2)) {
          if (name2.contains(name)) {
            if (!conflictNamesCountyCity.containsKey(name)) {
              s_temp = new HashSet<>();
              conflictNamesCountyCity.put(name, s_temp);
            } else {
              s_temp = conflictNamesCountyCity.get(name);
            }
            s_temp.add(name2);
          }
        }
      }
    }

  }

  public String[] splitAddr(String inputAddr, String courtAddr) {
    String[] groupedAddr = new String[4];
    String[] groupedAddrNames = new String[4];
    // if find prov; if find city; if find county; if find street; if
    // contradictory or ambiguous which should be determined by next level
    // match
    boolean[] groupedAddrFind = new boolean[] {false, false, false, false, false};
    String replaceAddr = inputAddr;
    SignalStructure signal = new SignalStructure(null, null, null, null);
    String[] addrCourt = new String[] {null, null, null, null};
    if (courtAddrMap.containsKey(courtAddr)) {
      addrCourt = courtAddrMap.get(courtAddr);
    }
    replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCounty(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseStreet(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    String[] codes = inferUpperLevelCode(groupedAddr);
    for (int i = 0; i < 4; i++) {
      if (codes[i] != null && codeToName.containsKey(codes[i])) {
        groupedAddrNames[i] = codeToName.get(codes[i]);
      }
    }
    return groupedAddrNames;
  }

  public String getWordsFromSent(String inputAddr) {
    if (inputAddr == null) {
      return inputAddr;
    }
    String[] groupedAddr = new String[4];
    String[] groupedAddrNames = new String[4];
    // if find prov; if find city; if find county; if find street; if
    // contradictory or ambiguous which should be determined by next level
    // match
    boolean[] groupedAddrFind = new boolean[] {false, false, false, false, false};
    String replaceAddr = inputAddr;
    SignalStructure signal = new SignalStructure(null, null, null, null);
    String[] addrCourt = new String[] {null, null, null, null};
    replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseCounty(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    replaceAddr = parseStreet(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    if (signal.provMatches.size() > 0) {
      groupedAddrNames[0] = signal.provMatches.get(0).name;
    }
    if (signal.cityMatches.size() > 0) {
      groupedAddrNames[1] = signal.cityMatches.get(0).name;
    }
    if (signal.countyMatches.size() > 0) {
      groupedAddrNames[2] = signal.countyMatches.get(0).name;
    }
    if (signal.streetMatches.size() > 0) {
      groupedAddrNames[3] = signal.streetMatches.get(0).name;
    }
    String s = inputAddr;
    for (int i = 0; i < 4; i++) {
      if (groupedAddrNames[i] != null) {
        s = s.replaceAll(groupedAddrNames[i], "");
      }
    }
    return s;
  }

  public String[] splitAddr(String inputAddr, String courtAddr, int level) {
    String[] groupedAddr = new String[4];
    String[] groupedAddrNames = new String[4];
    // if find prov; if find city; if find county; if find street; if
    // contradictory or ambiguous which should be determined by next level
    // match
    boolean[] groupedAddrFind = new boolean[] {false, false, false, false, false};
    String replaceAddr = inputAddr;
    SignalStructure signal = new SignalStructure(null, null, null, null);
    String[] addrCourt = new String[] {null, null, null, null};
    if (courtAddrMap.containsKey(courtAddr))
      addrCourt = courtAddrMap.get(courtAddr);
    if (level == 1) {
      replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    } else if (level == 2) {
      replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
      replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    } else if (level == 3) {
      replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
      replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
      replaceAddr = parseCounty(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    } else {
      replaceAddr = parseProv(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
      replaceAddr = parseCity(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
      replaceAddr = parseCounty(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
      replaceAddr = parseStreet(replaceAddr, groupedAddr, groupedAddrFind, signal, addrCourt);
    }
    String[] codes = inferUpperLevelCode(groupedAddr);
    for (int i = 0; i < 4; i++) {
      if (codes[i] != null && codeToName.containsKey(codes[i])) {
        groupedAddrNames[i] = codeToName.get(codes[i]);
      }
    }
    return groupedAddrNames;
  }

  public String[] inferUpperLevelCode(String[] codes) {
    String[] newcodes = new String[4];
    if (codes.length != 4)
      return newcodes;
    if (codes[3] != null) {
      newcodes[3] = codes[3];
      newcodes[2] = findCountylevel(codes[3]);
      newcodes[1] = findCitylevel(codes[3]);
      newcodes[0] = findProvlevel(codes[3]);
    } else if (codes[2] != null) {
      newcodes[2] = codes[2];
      newcodes[1] = findCitylevel(codes[2]);
      newcodes[0] = findProvlevel(codes[2]);
    } else if (codes[1] != null) {
      newcodes[1] = codes[1];
      newcodes[0] = findProvlevel(codes[1]);
    } else if (codes[0] != null) {
      newcodes[0] = codes[0];
    }
    return newcodes;
  }

  // code to code
  public String findProvlevel(String as) {
    if (areaBook.get(as).level.equals("1"))
      return as;
    else
      return findProvlevel(areaBook.get(as).upperLevelCode);
  }

  // code to code
  public String findCitylevel(String as) {
    if (areaBook.get(as).level.equals("1"))
      return null;
    if (areaBook.get(as).level.equals("2"))
      return as;
    else
      return findCitylevel(areaBook.get(as).upperLevelCode);
  }

  // code to code
  public String findCountylevel(String as) {
    if (areaBook.get(as).level.equals("1") || as.equals("2"))
      return null;
    if (areaBook.get(as).level.equals("3"))
      return as;
    else
      return findCountylevel(areaBook.get(as).upperLevelCode);
  }

  // 判断某一code对应的所有可能的名字是否有关键字
  // 省一级没有歧义，不需要判断
  // 如果返回非NULL,可以很大的概率确定是正确的匹配，不会有歧义
  // 经过实验发现如果含有关键词，市县不会重合（市全名和县全名不会重合，但是简写会重合）
  public KeyWordReplace ifContainsProvKeyWord(String code, String addr, Set<String> provKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    if (addr.length() < 2 || !provNamesInvert.containsKey(code))
      return null;
    List<String> possibleNames = provNamesInvert.get(code);
    for (String name : possibleNames) {
      // 地址含有这个名字，并且这个名字含有关键字。
      if (name.length() > 1 && addr.contains(name)
          && provKeyWords.contains(name.substring(name.length() - 1))) {
        String replacedAddr = addr.replace(name, "");
        return new KeyWordReplace(true, replacedAddr, name);
      }
    }
    return null;
  }

  public KeyWordReplace ifContainsCityKeyWord(String code, String addr, Set<String> cityKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    if (addr.length() < 2 || !cityNamesInvert.containsKey(code))
      return null;
    List<String> possibleNames = cityNamesInvert.get(code);
    for (String name : possibleNames) {
      // 地址含有这个名字，并且这个名字含有关键字。
      if (name.length() > 1 && addr.contains(name)
          && cityKeyWords.contains(name.substring(name.length() - 1))) {
        String replacedAddr = addr.replace(name, "");
        return new KeyWordReplace(true, replacedAddr, name);
      }
    }
    return null;
  }

  public KeyWordReplace ifContainsCountyKeyWord(String code, String addr,
      Set<String> countyKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    if (addr.length() < 2 || !countyNamesInvert.containsKey(code))
      return null;
    List<String> possibleNames = countyNamesInvert.get(code);
    for (String name : possibleNames) {
      if (name.length() > 1 && addr.contains(name)
          && countyKeyWords.contains(name.substring(name.length() - 1))) {
        String replacedAddr = addr.replace(name, "");
        return new KeyWordReplace(true, replacedAddr, name);
      }
    }
    return null;
  }

  public KeyWordReplace ifContainsStreetKeyWord(String code, String addr,
      Set<String> streetKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    if (addr.length() < 2 || !streetNamesInvert.containsKey(code))
      return null;
    Set<String> possibleNames = streetNamesInvert.get(code);
    for (String name : possibleNames) {
      if (name.length() > 1 && addr.contains(name)
          && streetKeyWords.contains(name.substring(name.length() - 1))) {
        String replacedAddr = addr.replace(name, "");
        return new KeyWordReplace(true, replacedAddr, name);
      }
    }
    return null;
  }

  private void ifContainsProvKeyWord(MatchStructure ms, Set<String> provKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    // 如果本身含有关键字，返回本身
    ms.ifNameContainsKw = false;
    if (ms.name.length() < 2 && ms.end > ms.sent.length())
      return;
    // 如果本身含有关键字
    if (provKeyWords.contains(ms.name.substring(ms.name.length() - 1))) {
      ms.ifNameContainsKw = true;
    } else {
      for (String name : provNamesInvert.get(ms.code)) {
        if (name.length() < 2 || name.equals(ms.name))
          continue;
        Matcher matcher = Pattern.compile(name, Pattern.LITERAL).matcher(ms.sent);
        if (matcher.find(ms.start)) {
          if (matcher.start() == ms.start
              && provKeyWords.contains(name.substring(name.length() - 1))) {
            ms.end = matcher.end();
            ms.ifNameContainsKw = true;
            ms.name = name;
            break;
          }
        }
      }
    }
  }

  private void ifContainsCityKeyWord(MatchStructure ms, Set<String> cityKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    // 如果本身含有关键字，返回本身
    ms.ifNameContainsKw = false;
    if (ms.name.length() < 2 && ms.end > ms.sent.length())
      return;
    // 如果本身含有关键字
    if (cityKeyWords.contains(ms.name.substring(ms.name.length() - 1))) {
      // 不是市，表示罕见的市级区划，需要长度比较长。
      if (!ms.name.endsWith("市") && ms.name.length() <= 4)
        return;
      ms.ifNameContainsKw = true;
    } else {
      for (String name : cityNamesInvert.get(ms.code)) {
        if (name.length() < 2 || name.equals(ms.name))
          continue;
        Matcher matcher = Pattern.compile(name, Pattern.LITERAL).matcher(ms.sent);
        if (matcher.find(ms.start)) {
          if (matcher.start() == ms.start
              && cityKeyWords.contains(name.substring(name.length() - 1))) {
            ms.end = matcher.end();
            ms.ifNameContainsKw = true;
            ms.name = name;
            break;
          }
        }
      }
    }
  }

  private void ifContainsCountyKeyWord(MatchStructure ms, Set<String> countyKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    // 如果本身含有关键字，返回本身
    ms.ifNameContainsKw = false;
    if (ms.name.length() < 2 && ms.end > ms.sent.length())
      return;
    // 如果本身含有关键字
    if (countyKeyWords.contains(ms.name.substring(ms.name.length() - 1))) {
      ms.ifNameContainsKw = true;
    } else {
      for (String name : countyNamesInvert.get(ms.code)) {
        if (name.length() < 2 || name.equals(ms.name))
          continue;
        Matcher matcher = Pattern.compile(name, Pattern.LITERAL).matcher(ms.sent);
        if (matcher.find(ms.start)) {
          if (matcher.start() == ms.start
              && countyKeyWords.contains(name.substring(name.length() - 1))) {
            ms.end = matcher.end();
            ms.ifNameContainsKw = true;
            ms.name = name;
            break;
          }
        }
      }
    }
  }

  private void ifContainsStreetKeyWord(MatchStructure ms, Set<String> streetKeyWords) {
    // 默认code 是合法的city code,需要在外部进行规范
    // 如果本身含有关键字，返回本身
    ms.ifNameContainsKw = false;
    if (ms.name.length() < 2 && ms.end > ms.sent.length())
      return;
    // 如果本身含有关键字
    if (streetKeyWords.contains(ms.name.substring(ms.name.length() - 1))) {
      ms.ifNameContainsKw = true;
    } else {
      boolean find = false;
      for (String name : streetNamesInvert.get(ms.code)) {
        if (find)
          break;
        if (name.length() < 2 || name.equals(ms.name))
          continue;
        Matcher matcher = Pattern.compile(name, Pattern.LITERAL).matcher(ms.sent);
        if (matcher.find(ms.start)) {
          for (String s : streetKeyWords) {
            if (matcher.start() == ms.start && ms.sent.endsWith(s)) {
              ms.end = matcher.end();
              ms.ifNameContainsKw = true;
              ms.name = name;
              find = true;
              break;
            }
          }
        }
      }
    }
  }

  private void ifContainsOtherCounties(MatchStructure ms, SignalStructure signal) {
    if (conflictNames.containsKey(ms.name)) {
      for (String name : conflictNames.get(ms.name)) {
        Matcher matcher = Pattern.compile(name, Pattern.LITERAL).matcher(ms.sent);
        if (matcher.find() && nameToCode.containsKey(name)) {
          String code = nameToCode.get(name).get(0);
          MatchStructure ms_new =
              new MatchStructure(matcher.start(), matcher.end(), name, ms.sent, false, code);
          ifContainsCountyKeyWord(ms_new, countyKeyWords);
          signal.countyMatches.add(ms_new);
        }
      }
    }
  }

  private void ifContainsOtherCity(MatchStructure ms, SignalStructure signal) {
    if (conflictNamesCity.containsKey(ms.name)) {
      for (String name : conflictNamesCity.get(ms.name)) {
        Matcher matcher = Pattern.compile(name, Pattern.LITERAL).matcher(ms.sent);
        if (matcher.find() && nameToCode.containsKey(name)) {
          String code = nameToCode.get(name).get(0);
          MatchStructure ms_new =
              new MatchStructure(matcher.start(), matcher.end(), name, ms.sent, false, code);
          ifContainsCityKeyWord(ms_new, cityKeyWords);
          signal.cityMatches.add(ms_new);
        }
      }
    }
  }

  private boolean ifHasKeyWord(Collection<MatchStructure> cms) {
    for (MatchStructure ms : cms) {
      if (ms.ifNameContainsKw)
        return true;
    }
    return false;
  }

  // To do:考虑一个地址，可以同时匹配上市县或者县镇或者市镇或者市县镇，由于之前匹配顺序的原因，可能会导致错误的搜索范围
  // To do:可以在匹配上一级的时候，如果找到，可以判断是否在下一级出现，如果出现，根据设定的规则，有限选择上一级的匹配或者
  // TO do:下一级的匹配。省级名字不会出现重名，不用考虑。
  // To do:某一即如果可以匹配上，还应该用匹配上的名字找到同级所有可能其他名字，再匹配一遍，然后判断是否含有关键字
  // To do:比如“市县乡镇”等，通过这些关键字可以判断是否保留下一级得到的匹配。
  // Note：目前是假设找到的都是同一个类型的匹配，比如 四川河北路，会找到四川，河北；四川省河北路，只会找到四川
  private String parseProv(String replaceAddrInput, String[] groupedAddr, boolean[] groupedAddrFind,
      SignalStructure signal, String[] addrCourt) {
    // 如果找到含有关键字的省，则退出搜索。
    String replaceAddr = replaceAddrInput;
    if (replaceAddr == null || replaceAddr.equals(""))
      return replaceAddr;
    String temp_prov;
    String reEx_prov = "^([^省市]{1,8}?)(?:省|市|自治区)";
    Matcher m_prov;
    Pattern p_prov = Pattern.compile(reEx_prov, Pattern.CASE_INSENSITIVE);
    m_prov = p_prov.matcher(replaceAddr);
    // 先规则匹配，否则暴力寻找
    if (m_prov.find()) {
      // 这里名字已经包含了关键字
      temp_prov = m_prov.group();
      if (provNames.containsKey(temp_prov)) {
        String code = provNames.get(temp_prov);
        MatchStructure ms =
            new MatchStructure(m_prov.start(), m_prov.end(), temp_prov, replaceAddr, true, code);
        signal.provMatches.add(ms);
        groupedAddrFind[0] = true;
      }
    }
    // 没找到
    if (!groupedAddrFind[0]) {
      // 暴力寻找！
      int start;
      boolean endCondition = false;
      for (String code : provNamesInvert.keySet()) {
        // 为了结果的完整性，应该遍历所有可能的省份
        // 有可能先匹配到句子后面的
        start = 0;
        if (start >= replaceAddr.length() || endCondition)
          break;
        for (String s : provNamesInvert.get(code)) {
          Matcher m = Pattern.compile(s, Pattern.LITERAL).matcher(replaceAddr);
          if (m.find(start)) {
            MatchStructure ms = new MatchStructure(m.start(), m.end(), s, replaceAddr, false, code);
            ifContainsProvKeyWord(ms, provKeyWords);
            signal.provMatches.add(ms);
            start = ms.end;// 下一次匹配从end开始。
            // 结束条件
            if (ms.ifNameContainsKw)
              endCondition = true;
            groupedAddrFind[0] = true;
            break;
          }
        }
      }
    }
    // replaceAddr and cross validation
    // To modify
    if (signal.provMatches.size() != 0) {
      String code = SelectRules.selectOneProv(signal.provMatches);
      if (signal.provMatches.get(0).start == 0 || signal.provMatches.get(0).ifNameContainsKw)
        replaceAddr = replaceMatches(replaceAddr, signal.provMatches, "#", true);
      // should always be true; just in case of error
      if (provNamesInvert.containsKey(code)) {
        groupedAddr[0] = code;
      }
      if (signal.provMatches.size() >= 2) {
        signal.conflicts[0] = true;
        groupedAddrFind[4] = true;
      }
    }
    signal.conflictMatrix[0][1] = hasUniqueCode(signal.provMatches);
    String[] temp = SelectRules.selectFromProv(signal);
    groupedAddr[0] = temp[0];
    return replaceAddr;
  }

  private String parseCity(String replaceAddrInput, String[] groupedAddr, boolean[] groupedAddrFind,
      SignalStructure signal, String[] addrCourt) {
    if (replaceAddrInput == null || replaceAddrInput.equals(""))
      return replaceAddrInput;
    String temp_city;
    String reEx_city = "^(?:省|自治区|(?:#+))?(.{1,10}?(?:市|自治州|州|地区|盟))";
    Matcher m_city;
    Pattern p_city = Pattern.compile(reEx_city, Pattern.CASE_INSENSITIVE);
    // 看是不是有本市关键字：
    if (!groupedAddrFind[1] && replaceAddrInput.length() >= 2 && addrCourt[1] != null) {
      int pos = replaceAddrInput.substring(0, Math.min(4, replaceAddrInput.length())).indexOf("本市");
      if (pos >= 0 && cityNames.containsKey(addrCourt[1])) {
        String code = cityNames.get(addrCourt[1]).get(0);
        MatchStructure ms = new MatchStructure(pos, pos + 2, "本市", replaceAddrInput, true, code);
        ms.inferred = true;
        groupedAddrFind[1] = true;
        signal.cityMatches.add(ms);
      }
    }
    if (!groupedAddrFind[1] && signal.provMatches.size() > 0 && groupedAddr[0] != null) {
      m_city = p_city.matcher(replaceAddrInput);
      // 此处有可能找到的市和省份出现矛盾。
      if (m_city.find()) {
        temp_city = m_city.group(1);
        if (cityNames.containsKey(temp_city)) {
          String code = cityNames.get(temp_city).get(0);
          MatchStructure ms = new MatchStructure(m_city.start(1), m_city.end(1), temp_city,
              replaceAddrInput, true, code);
          signal.cityMatches.add(ms);
          groupedAddrFind[1] = true;
        }
      }
      if (!groupedAddrFind[1]) {
        // 暴力查找市区，范围在查找的省下面
        // 缩小范围
        int start;
        boolean endCondition = false;
        Collection<AreaStructure> searchRange = new HashSet<AreaStructure>();
        for (MatchStructure mstemp : signal.provMatches) {
          if (provToCities.containsKey(mstemp.code)) {
            searchRange.addAll(provToCities.get(mstemp.code));
          }
        }
        for (AreaStructure as : searchRange) {
          start = 0;
          if (start >= replaceAddrInput.length() || endCondition)
            break;
          String code = as.code;
          if (cityNamesInvert.containsKey(code)) {
            for (String syn : cityNamesInvert.get(code)) {
              Matcher m = Pattern.compile(syn, Pattern.LITERAL).matcher(replaceAddrInput);
              if (m.find(start)) {
                MatchStructure ms =
                    new MatchStructure(m.start(), m.end(), syn, replaceAddrInput, false, code);
                ifContainsCityKeyWord(ms, cityKeyWords);
                ifContainsOtherCity(ms, signal);
                if (ifHasKeyWord(signal.cityMatches))
                  endCondition = true;
                groupedAddrFind[1] = true;
                start = m.end();
                signal.cityMatches.add(ms);
                break;
              }
            }
          }
        }
      }
    }
    if (signal.provMatches.size() <= 0 || signal.cityMatches.size() <= 0) {
      // 没有找到省，暴力查找市区
      // 从所有可能的别名里直接查找,此处不能缩小范围
      int start;
      boolean endCondition = false;
      for (String code : cityNamesInvert.keySet()) {
        start = 0;
        if (start >= replaceAddrInput.length() || endCondition)
          break;
        for (String syn : cityNamesInvert.get(code)) {
          Matcher m = Pattern.compile(syn, Pattern.LITERAL).matcher(replaceAddrInput);
          if (m.find(start)) {
            MatchStructure ms =
                new MatchStructure(m.start(), m.end(), syn, replaceAddrInput, false, code);
            ifContainsCityKeyWord(ms, cityKeyWords);
            ifContainsOtherCity(ms, signal);
            if (ifHasKeyWord(signal.cityMatches))
              endCondition = true;
            groupedAddrFind[1] = true;
            start = m.end();
            signal.cityMatches.add(ms);
            break;
          }
        }
      }
    }
    // 判断找到的市能否被下一级区域匹配上。需要用没有替换过的字符串orig
    // 如果匹配到的市有关键字,则原句子被替换。
    if (signal.cityMatches.size() > 0) {
      boolean hasConflict = true;
      String firstCode = SelectRules.selectOneCity(signal.cityMatches);
      String provCode, cityCode;
      if (signal.provMatches.size() > 0) {
        signal.conflictMatrix[1][1] = true;
        for (MatchStructure ms_prov : signal.provMatches) {
          if (!hasConflict)
            break;
          provCode = ms_prov.code;
          for (MatchStructure ms_city : signal.cityMatches) {
            cityCode = ms_city.code;
            if (provCode.equals(findProvlevel(cityCode))) {
              hasConflict = false;
              signal.conflictMatrix[1][1] = false;
              signal.pair1 = new String[] {provCode, cityCode};
              signal.mspair1 = new MatchStructure[] {ms_prov, ms_city};
              signal.conflicts[1] = false;
              break;
            }
          }
        }
      } else {
        hasConflict = false;
        cityCode = firstCode;
        provCode = findProvlevel(firstCode);
      }
      if (!hasConflict) {
        signal.conflicts[1] = false;
        groupedAddrFind[4] = false;
      } else {
        groupedAddrFind[4] = true;
        signal.conflicts[1] = true;
      }
    }
    groupedAddrFind[4] = signal.conflicts[0] || signal.conflicts[1];
    signal.conflictMatrix[1][2] = hasUniqueCode(signal.cityMatches);
    String[] temp = SelectRules.selectFromCity(signal);
    groupedAddr[0] = temp[0];
    groupedAddr[1] = temp[1];
    return replaceAddrInput;
  }

  private String parseCounty(String replaceAddrInput, String[] groupedAddr,
      boolean[] groupedAddrFind, SignalStructure signal, String[] addrCourt) {
    if (replaceAddrInput == null || replaceAddrInput.equals(""))
      return replaceAddrInput;
    if (replaceAddrInput.length() > 2 && replaceAddrInput.substring(0, 2).equals("本县")
        && addrCourt[2] != null) {
      if (countyNames.containsKey(addrCourt[2])) {
        String code = countyNames.get(addrCourt[2]).get(0);
        MatchStructure ms = new MatchStructure(0, 2, "本县", replaceAddrInput, true, code);
        ms.inferred = true;
        groupedAddrFind[2] = true;
        signal.countyMatches.add(ms);
      }
    }
    if (!groupedAddrFind[2] && signal.cityMatches.size() <= 1) {
      String city_name;
      if (signal.cityMatches.size() == 0) {
        city_name = "";
      } else {
        city_name = signal.cityMatches.get(0).name;
      }
      String county_pattern = "^#*" + "(?:" + city_name + ")?" + "(.{1,5}[县市区])";
      Matcher m = Pattern.compile(county_pattern).matcher(replaceAddrInput);
      if (m.find()) {
        if (countyNames.containsKey(m.group(1))) {
          // TODO 根据countyName得到一个code，需要考虑多个的情况
          for (String code : countyNames.get(m.group(1))) {
            // 只记录县的位置
            MatchStructure ms =
                new MatchStructure(m.start(1), m.end(1), m.group(1), replaceAddrInput, true, code);
            groupedAddrFind[2] = true;
            signal.countyMatches.add(ms);
          }
        }
      }
    }
    // 不存在冲突
    if (!groupedAddrFind[2] && !groupedAddrFind[4]) {
      // 有市区，先找市区
      Collection<AreaStructure> searchRange = new ArrayList<>();
      if (groupedAddr[1] != null && cityToCounties.containsKey(groupedAddr[1])) {
        searchRange = cityToCounties.get(groupedAddr[1]);
      } else if (groupedAddr[0] != null && provToCounties.containsKey(groupedAddr[0])) {
        searchRange = provToCounties.get(groupedAddr[0]);
      }
      int loop_times = 0;
      while (true) {
        if (searchRange != null && searchRange.size() != 0) {
          int start;
          boolean endCondition = false;
          for (AreaStructure as : searchRange) {
            start = 0;
            if (start >= replaceAddrInput.length() || endCondition)
              break;
            String code = as.code;
            if (!countyNamesInvert.containsKey(code))
              continue;
            for (String syn : countyNamesInvert.get(code)) {
              Matcher m = Pattern.compile(syn, Pattern.LITERAL).matcher(replaceAddrInput);
              if (m.find(start)) {
                MatchStructure ms =
                    new MatchStructure(m.start(), m.end(), syn, replaceAddrInput, false, code);
                ifContainsCountyKeyWord(ms, countyKeyWords);
                ifContainsOtherCounties(ms, signal);
                if (ifHasKeyWord(signal.countyMatches))
                  endCondition = true;
                groupedAddrFind[2] = true;
                start = m.end();
                signal.countyMatches.add(ms);
                break;
              }
            }
          }
        }
        loop_times++;
        if (!groupedAddrFind[2]) {
          String code = "";
          if (loop_times == 1) {
            searchRange = new HashSet<>();
            if (provNames.containsKey(addrCourt[1]))
              code = provNames.get(addrCourt[1]);
            if (provToCounties.containsKey(code))
              searchRange.addAll(provToCounties.get(code));
          } else {
            break;
          }
        } else {
          break;
        }
      }
      if (signal.countyMatches.size() < 1) {// 都没有，暴力查找
        int start;
        boolean endCondition = false;
        for (String code : countyNamesInvert.keySet()) {
          start = 0;
          if (start >= replaceAddrInput.length() || endCondition)
            break;
          for (String syn : countyNamesInvert.get(code)) {
            Matcher m = Pattern.compile(syn, Pattern.LITERAL).matcher(replaceAddrInput);
            if (m.find(start)) {
              MatchStructure ms =
                  new MatchStructure(m.start(), m.end(), syn, replaceAddrInput, false, code);
              ifContainsCountyKeyWord(ms, countyKeyWords);
              ifContainsOtherCounties(ms, signal);
              if (ifHasKeyWord(signal.countyMatches))
                endCondition = true;
              groupedAddrFind[2] = true;
              start = m.end();
              signal.countyMatches.add(ms);
              break;
            }
          }
        }
      }
    }
    // 存在冲突
    else if (!groupedAddrFind[2] && groupedAddrFind[4]) {
      // 直接暴力查找，看匹配的是省还是市
      // 可以优化，即缩小范围。
      // 所有的县
      int start = 0;
      boolean endCondition = false;
      for (String code : countyNamesInvert.keySet()) {
        start = 0;
        if (start >= replaceAddrInput.length() || endCondition)
          break;
        for (String syn : countyNamesInvert.get(code)) {
          Matcher m = Pattern.compile(syn, Pattern.LITERAL).matcher(replaceAddrInput);
          if (m.find(start)) {
            MatchStructure ms =
                new MatchStructure(m.start(), m.end(), syn, replaceAddrInput, false, code);
            ifContainsCountyKeyWord(ms, countyKeyWords);
            ifContainsOtherCounties(ms, signal);
            if (ifHasKeyWord(signal.countyMatches))
              endCondition = true;
            groupedAddrFind[2] = true;
            start = m.end();
            signal.countyMatches.add(ms);
            break;
          }
        }
      }
    }
    // 如果找到了，都不匹配，认为地址错误，不再纠错。
    // 之前如果没有冲突，不会影响结果。
    if (signal.countyMatches.size() > 0) {
      boolean hasConflict = true, hasConflict_temp;
      String firstCode = SelectRules.selectOneCounty(signal.countyMatches);
      String provCode, cityCode, countyCode;
      if (signal.cityMatches.size() > 0) {
        signal.conflictMatrix[2][2] = true;
        for (MatchStructure ms_county : signal.countyMatches) {
          countyCode = ms_county.code;
          if (!hasConflict) {
            signal.conflictMatrix[2][2] = false;
            break;
          }
          for (MatchStructure ms_city : signal.cityMatches) {
            cityCode = ms_city.code;
            if (cityCode.equals(findCitylevel(countyCode))) {
              hasConflict = false;
              provCode = findProvlevel(cityCode);
              signal.conflictMatrix[2][2] = false;
              signal.pair4 = new String[] {cityCode, countyCode};
              signal.mspair4 = new MatchStructure[] {ms_city, ms_county};
              signal.conflicts[2] = false;
              break;
            }
          }
        }
      } else {
        hasConflict = false;
      }
      hasConflict_temp = hasConflict;
      hasConflict = true;
      if (signal.provMatches.size() > 0) {
        signal.conflictMatrix[2][1] = true;
        for (MatchStructure ms_county : signal.countyMatches) {
          countyCode = ms_county.code;
          if (!hasConflict) {
            signal.conflictMatrix[2][1] = false;
            break;
          }
          for (MatchStructure ms_prov : signal.provMatches) {
            provCode = ms_prov.code;
            if (provCode.equals(findProvlevel(countyCode))) {
              hasConflict = false;
              firstCode = countyCode;
              cityCode = findCitylevel(countyCode);
              signal.conflictMatrix[2][1] = false;
              signal.pair2 = new String[] {provCode, countyCode};
              signal.mspair2 = new MatchStructure[] {ms_prov, ms_county};
              signal.conflicts[2] = false;
              break;
            }
          }
        }
      } else {
        hasConflict = false;
      }

      hasConflict = hasConflict_temp && hasConflict;
      if (signal.provMatches.size() == 0 && signal.cityMatches.size() == 0) {
        hasConflict = false;
        countyCode = firstCode;
        cityCode = findCitylevel(countyCode);
        provCode = findProvlevel(cityCode);
      }
      if (!hasConflict) {
        groupedAddrFind[4] = false;
        signal.conflicts[2] = false;// 表示和前面的市或者省，至少有一个没有冲突。
      } else {
        groupedAddrFind[4] = true;
        signal.conflicts[2] = true;
      }
    }
    // 没有找到，进入下一个层级匹配

    groupedAddrFind[4] = signal.conflicts[0] || signal.conflicts[1] || signal.conflicts[2];
    signal.conflictMatrix[2][3] = hasUniqueCode(signal.countyMatches);

    String[] temp = SelectRules.selectFromCounty(signal);
    groupedAddr[0] = temp[0];
    groupedAddr[1] = temp[1];
    groupedAddr[2] = temp[2];
    return replaceAddrInput;
  }

  private String parseStreet(String replaceAddrInput, String[] groupedAddr,
      boolean[] groupedAddrFind, SignalStructure signal, String[] addrCourt) {
    if (replaceAddrInput == null || replaceAddrInput.equals(""))
      return replaceAddrInput;
    Collection<AreaStructure> searchRange = new HashSet<>();
    // 不存在冲突,或者县一级的冲突可以调和
    if (!groupedAddrFind[4]) {
      if (groupedAddr[2] != null && countyToStreets.containsKey(groupedAddr[2])) {
        searchRange.addAll(countyToStreets.get(groupedAddr[2]));
      } else if (groupedAddr[1] != null && cityToStreets.containsKey(groupedAddr[1])) {
        searchRange.addAll(cityToStreets.get(groupedAddr[1]));
      } else if (groupedAddr[0] != null && provToStreets.containsKey(groupedAddr[0])) {
        searchRange.addAll(provToStreets.get(groupedAddr[0]));
      }
    }
    // 存在冲突
    else {
      for (MatchStructure ms : signal.provMatches) {
        if (provToStreets.containsKey(ms.code))
          searchRange.addAll(provToStreets.get(ms.code));
      }
      for (MatchStructure ms : signal.cityMatches) {
        if (cityToStreets.containsKey(ms.code))
          searchRange.addAll(cityToStreets.get(ms.code));
      }
      for (MatchStructure ms : signal.countyMatches) {
        if (countyToStreets.containsKey(ms.code))
          searchRange.addAll(countyToStreets.get(ms.code));
      }
    }
    if (searchRange.size() <= 0) {
      if (addrCourt[1] != null) {
        if (cityNames.containsKey(addrCourt[1])) {
          String code = cityNames.get(addrCourt[1]).get(0);
          if (cityToStreets.containsKey(code)) {
            searchRange = cityToStreets.get(code);
          }
        }
      }
    }
    int loop_times = 0;
    while (true) {
      if (searchRange.size() > 0) {
        int start;
        boolean endCondition = false;
        for (AreaStructure as : searchRange) {
          start = 0;
          if (start >= replaceAddrInput.length() || endCondition)
            break;
          String code = as.code;
          for (String syn : streetNamesInvert.get(code)) {
            Matcher m = Pattern.compile(syn, Pattern.LITERAL).matcher(replaceAddrInput);
            if (m.find(start)) {
              MatchStructure ms =
                  new MatchStructure(m.start(), m.end(), syn, replaceAddrInput, false, code);
              ifContainsStreetKeyWord(ms, streetKeyWords);
              if (ms.ifNameContainsKw)
                endCondition = true;
              groupedAddrFind[3] = true;
              start = m.end();
              signal.streetMatches.add(ms);
              break;
            }
          }
        }
      }
      loop_times++;
      if (!groupedAddrFind[3]) {
        // 县
        String code = "";
        if (loop_times == 1) {
          searchRange = new HashSet<>();
          if (countyNames.containsKey(addrCourt[2]))
            code = countyNames.get(addrCourt[2]).get(0);
          if (countyToStreets.containsKey(code))
            searchRange.addAll(countyToStreets.get(code));
        }
        // 市
        else if (loop_times == 2) {
          searchRange = new HashSet<>();
          if (countyNames.containsKey(addrCourt[1]))
            code = countyNames.get(addrCourt[1]).get(0);
          if (cityToStreets.containsKey(code))
            searchRange.addAll(cityToStreets.get(code));
        }
        // 省
        else
          break;
      } else {
        break;
      }
    }
    if (signal.streetMatches.size() > 0) {
      boolean hasConflict = true, hasConflict_temp;
      String firstCode = SelectRules.selectOneStreet(signal.streetMatches);
      String provCode, cityCode, countyCode, streetCode;
      if (signal.countyMatches.size() > 0) {
        signal.conflictMatrix[3][3] = true;
        for (MatchStructure ms_street : signal.streetMatches) {
          streetCode = ms_street.code;
          if (!hasConflict)
            break;
          for (MatchStructure ms_county : signal.countyMatches) {
            countyCode = ms_county.code;
            if (countyCode.equals(findCountylevel(streetCode))) {
              hasConflict = false;
              signal.conflictMatrix[3][3] = true;
              signal.pair6 = new String[] {countyCode, streetCode};
              signal.mspair6 = new MatchStructure[] {ms_county, ms_street};
              signal.conflicts[3] = false;
              break;
            }
          }
        }
      } else {
        hasConflict = false;
      }
      hasConflict_temp = hasConflict;
      hasConflict = true;
      if (signal.cityMatches.size() > 0) {
        signal.conflictMatrix[3][2] = true;
        for (MatchStructure ms_street : signal.streetMatches) {
          streetCode = ms_street.code;
          if (!hasConflict)
            break;
          for (MatchStructure ms_city : signal.cityMatches) {
            cityCode = ms_city.code;
            if (cityCode.equals(findCitylevel(streetCode))) {
              hasConflict = false;
              signal.conflictMatrix[3][2] = true;
              signal.pair5 = new String[] {cityCode, streetCode};
              signal.mspair5 = new MatchStructure[] {ms_city, ms_street};
              signal.conflicts[3] = false;
              break;
            }
          }
        }
      } else {
        hasConflict = false;
      }
      hasConflict_temp = hasConflict && hasConflict_temp;
      hasConflict = true;
      if (signal.provMatches.size() > 0) {
        signal.conflictMatrix[3][1] = true;
        for (MatchStructure ms_street : signal.streetMatches) {
          streetCode = ms_street.code;
          if (!hasConflict)
            break;
          for (MatchStructure ms_prov : signal.provMatches) {
            provCode = ms_prov.code;
            if (provCode.equals(findProvlevel(streetCode))) {
              hasConflict = false;
              signal.conflictMatrix[3][1] = true;
              signal.pair3 = new String[] {provCode, streetCode};
              signal.mspair3 = new MatchStructure[] {ms_prov, ms_street};
              signal.conflicts[3] = false;
              break;
            }
          }
        }
      } else {
        hasConflict = false;
      }
      hasConflict_temp = hasConflict && hasConflict_temp;
      hasConflict = hasConflict_temp;
      if (signal.countyMatches.size() == 0 && signal.cityMatches.size() == 0
          && signal.provMatches.size() == 0) {
        hasConflict = false;
        countyCode = firstCode;
        cityCode = findCitylevel(countyCode);
        provCode = findProvlevel(cityCode);
      }
      if (!hasConflict) {
        // To modify
        groupedAddrFind[4] = false;
        signal.conflicts[3] = false;// 表示和前面的市或者省，至少有一个没有冲突。
      } else {
        groupedAddrFind[4] = true;
        signal.conflicts[3] = true;
      }
    }
    signal.conflictMatrix[3][4] = hasUniqueCode(signal.streetMatches);
    groupedAddrFind[4] =
        signal.conflicts[0] || signal.conflicts[1] || signal.conflicts[2] || signal.conflicts[3];
    String[] temp = SelectRules.selectFromStreet(signal);
    groupedAddr[0] = temp[0];
    groupedAddr[1] = temp[1];
    groupedAddr[2] = temp[2];
    groupedAddr[3] = temp[3];
    return replaceAddrInput;
  }

  public String replaceMatches(String s, List<MatchStructure> lm, String replacement) {
    // 保持长度不变
    char[] chs = s.toCharArray();
    StringBuilder sb = new StringBuilder();
    Set<Integer> except = new HashSet<>();
    for (MatchStructure aLm : lm) {
      for (int j = aLm.start; j < aLm.end; j++)
        except.add(j);
    }
    for (int i = 0; i < chs.length; i++) {
      if (!except.contains(i)) {
        sb.append(chs[i]);
      } else
        sb.append(replacement);
    }
    return sb.toString();
  }

  private String replaceMatches(String s, List<MatchStructure> lm, String replacement,
      boolean ifFirst) {
    if (lm.size() < 1)
      return s;
    // 保持长度不变
    char[] chs = s.toCharArray();
    StringBuilder sb = new StringBuilder();
    Set<Integer> except = new HashSet<>();
    int size;
    if (ifFirst)
      size = 1;
    else
      size = lm.size();
    for (int i = 0; i < size; i++) {
      for (int j = lm.get(i).start; j < lm.get(i).end; j++)
        except.add(j);
    }
    for (int i = 0; i < chs.length; i++) {
      if (!except.contains(i)) {
        sb.append(chs[i]);
      } else
        sb.append(replacement);
    }
    return sb.toString();
  }

  private boolean hasUniqueCode(List<MatchStructure> lm) {
    if (lm == null || lm.size() < 2)
      return true;
    Set<String> s = new HashSet<>();
    for (MatchStructure aLm : lm) {
      s.add(aLm.code);
    }
    if (s.size() <= 1)
      return true;
    return false;
  }

}
