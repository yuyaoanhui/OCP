package com.ocp.rabbit.repository.tool.algorithm.dispute;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;

/**
 * 
 * @author yu.yao 2018年9月26日
 *
 */
public class RegexModelGenerator {

  private Map<String, RegexBasedFilter> regexBasedFilterMap;
  private Set<String> factsLabelsSet;


  public Map<String, RegexBasedFilter> getRegexBasedFilterMap() {
    return regexBasedFilterMap;
  }

  public Set<String> getFactsLabelsSet() {
    return factsLabelsSet;
  }

  private static RegexModelGenerator rmg;

  private RegexModelGenerator() {
    loadRegexModelFile();
  }

  public static Map<String, RegexBasedFilter> load_from_string(String json) {
    Map<String, RegexBasedFilter> rbfmap = new HashMap<>();
    JSONObject source = new JSONObject(json);
    Iterator<?> iterator = source.keys();
    Set<String> factsLabels = new HashSet<>();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      JSONObject jsonObject = source.getJSONObject(key);
      RegexBasedFilter rbf = parseJsonConfig(jsonObject, factsLabels);
      rbfmap.put(key, rbf);
    }
    return rbfmap;
  }

  public static RegexModelGenerator getInstance() {
    if (null == rmg) {
      rmg = new RegexModelGenerator();
      return rmg;
    } else {
      return rmg;
    }
  }

  private synchronized void loadRegexModelFile() {

    Map<String, RegexBasedFilter> rbfmap = new HashMap<>();
    Map<String, Set<String>> flmapper = new HashMap<>();
    InputStream is = FileUtils.loadProperties("regex.based.json.file");
    String json = FileOperate.readTxt(is, "utf-8");
    JSONObject source = new JSONObject(json);
    Iterator<?> iterator = source.keys();
    Set<String> factsLabels = new HashSet<>();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      JSONObject jsonObject = source.getJSONObject(key);
      RegexBasedFilter rbf = parseJsonConfig(jsonObject, factsLabels);
      rbfmap.put(key, rbf);
      flmapper.put(key, factsLabels);
    }
    regexBasedFilterMap = rbfmap;
    factsLabelsSet = factsLabels;
  }


  public static RegexBasedFilter parseJsonConfig(JSONObject jsonObject, Set<String> factsLabels) {
    Iterator<?> iterator = jsonObject.keys();
    String logiclabel = "AND";
    Pattern pattern = null;
    Double weight = 1.0;
    String targetSentencesLabel = null;
    String targetSentenceLabel = null;
    double score = 0.;
    double condition = RegexBasedFilter.MINIMUM_CONDITION;
    String name = null;
    List<RegexBasedFilter> children = null;
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      switch (key) {
        case "LogicLabel": {
          logiclabel = (String) jsonObject.get(key);
          break;
        }
        case "Pattern": {
          pattern = Pattern.compile((String) jsonObject.get(key));
          break;
        }
        case "Weight": {
          weight = Double.parseDouble(jsonObject.get(key).toString());
          break;
        }
        case "Paragraph": {
          targetSentencesLabel = (String) jsonObject.get(key);
          factsLabels.add(targetSentencesLabel);
          break;
        }
        case "Score": {
          score = Double.parseDouble(jsonObject.get(key).toString());
          break;
        }
        case "Condition": {
          condition = Double.parseDouble(jsonObject.get(key).toString());
          break;
        }
        case "Name": {
          name = (String) jsonObject.get(key);
          break;
        }
        case "Children": {
          JSONArray jsonArray = jsonObject.getJSONArray(key);
          List<RegexBasedFilter> lrbf = new ArrayList<>();
          for (int i = 0; i < jsonArray.length(); i++) {
            lrbf.add(parseJsonConfig(jsonArray.getJSONObject(i), factsLabels));
          }
          children = lrbf;
        }
      }
    }

    RegexBasedFilter rbf = new RegexBasedFilter(pattern, weight, targetSentencesLabel,
        targetSentenceLabel, logiclabel, score, children, condition);
    rbf.setName(name);
    return rbf;
  }
}
