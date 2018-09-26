package com.ocp.rabbit.repository.tool.algorithm.profession;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.FileUtils;

/**
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class InstitutionSynonym {

  private Map<String, String> STDCATEGORY;
  private Pattern PATTERN_SYNONYM;

  private InstitutionSynonym() {
    readSource();
  }

  private static InstitutionSynonym institutionSynonym = new InstitutionSynonym();

  private void readSource() {
    InputStream is = FileUtils.loadProperties("synonym.institution");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    Map<String, String> stdCategory = new HashMap<>();
    Pattern pattern;
    for (String line : lines) {
      if (line.length() <= 1 || Pattern.compile("##").matcher(line).find())
        continue;
      String[] tokens = line.split("[\\s\t]+");
      for (int i = 1; i < tokens.length; i++) {
        stdCategory.put(tokens[i], tokens[0]);
      }
    }
    String s = "";
    List<String> synonyms = new ArrayList<String>(stdCategory.keySet());
    synonyms.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });
    for (String word : synonyms) {
      s = s + "|" + word;
    }
    s = "(" + s.substring(1) + ")";
    pattern = Pattern.compile(s);

    STDCATEGORY = stdCategory;
    PATTERN_SYNONYM = pattern;
  }

  /**
   * 同义词替换
   */
  public static String replaceSynonym(String name) {
    java.util.regex.Matcher matcher = institutionSynonym.PATTERN_SYNONYM.matcher(name);
    String newToken = name;
    if (matcher.find()) {
      newToken = matcher.replaceAll(institutionSynonym.STDCATEGORY.get(matcher.group()));
    }
    return newToken;
  }

  public static void main(String[] args) {
    InputStream is = FileUtils.loadProperties("ranking.administrative");
    List<String> lines = FileOperate.readTxtToArrays(is, "utf-8");
    List<String> output = new ArrayList<>();
    for (String line : lines) {
      if (line.length() <= 1 || Pattern.compile("##").matcher(line).find()) {
        output.add(line);
        continue;
      } else {
        String[] tokens = line.split("[\\s\t]+");
        List<String> newTokens = new ArrayList<>();
        for (String token : tokens) {
          java.util.regex.Matcher matcher = institutionSynonym.PATTERN_SYNONYM.matcher(token);
          String newToken = token;
          if (matcher.find()) {
            newToken = matcher.replaceAll(institutionSynonym.STDCATEGORY.get(matcher.group()));
          }
          newTokens.add(newToken);
        }
        String s = "";
        for (String token : newTokens) {
          s = s + token + "\t";
        }
        output.add(s);
      }
    }
  }
}
