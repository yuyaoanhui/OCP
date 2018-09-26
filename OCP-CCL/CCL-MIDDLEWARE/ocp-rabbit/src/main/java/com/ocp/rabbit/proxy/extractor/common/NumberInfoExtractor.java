package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 根据分词算人数
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class NumberInfoExtractor {

  private static final Pattern regCharacter = Pattern.compile("[\u4e00-\u9fa5]+");
  private static NumberRecognizer pr = new NumberRecognizer(new String[] {"人"});

  // 抽取人数
  public static double parseNumber(List<String> paragraphList, Pattern pattern, String point) {
    double number = 0.0;
    for (String paragraph : paragraphList) {
      Matcher matcher = pattern.matcher(paragraph);
      while (matcher.find()) {
        NamedEntity[] nes_action = NamedEntityRecognizer.recognizeEntityByRegex(paragraph, pattern);
        String value = (String) nes_action[0].getSource();
        List<WrapNumberFormat> wnfs = pr.getNumbers(value, true);
        if (wnfs.size() > 0)
          number = wnfs.get(0).getArabicNumber();
      }
    }
    if ((point.equals("人")) && number == 0.0) {
      for (String paragraph : paragraphList) {
        String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
        for (String sentence : sentences) {
          number = parseStatistics(sentence, pattern);
          if (number > 0.0) {
            return number;
          }
        }
      }
    }
    return number;
  }

  private static final Pattern numberPattern = Pattern.compile("[0-9]|甲|乙|丙|丁|戊|己|庚|辛|壬|癸");

  // 针对没有直接描述人数，进行分词处理
  public static double parseStatistics(String sentence, Pattern pattern) {
    double number = 0.0;
    List<String> names = new ArrayList<String>();
    List<Integer> numbers = new ArrayList<Integer>();
    // String[] sents = sentence.split("[,，]");
    int temp = 0;
    // for (String sent : sentence) {
    Matcher m2 = pattern.matcher(sentence);
    if (m2.find()) {
      List<Term> lterm = NlpAnalysis.parse(sentence).getTerms();
      // 遍历分词，找到属性是nr的词认为是人名或者姓
      for (int i = 0; i < lterm.size(); i++) {
        Term term = lterm.get(i);
        if (term.getNatureStr().equals("nr") || term.getNatureStr().equals("nw")) {
          // 只有人的姓，没有名字则判断下一个分词是否可以连在一起组成人名
          if (term.getName().length() == 1 && i < lterm.size() - 1) {
            Matcher ma = regCharacter.matcher(term.getName());
            if (ma.find()) {
              ma = regCharacter.matcher(lterm.get(i + 1).getName());
              if (ma.find()) {
                if (lterm.get(i + 1).getName().length() <= 2) {
                  String nam = term.getName() + lterm.get(i + 1).getName();
                  lterm.remove(i + 1);
                  names.add(nam);
                  numbers.add(i);
                  temp += 1;
                }
              }
            }
            // 认为是人名的直接判断
          } else if (term.getName().length() <= 4) {
            temp += 1;
            int num = 0;
            Matcher num1 = numberPattern.matcher(lterm.get(i + 1).getName());
            if (num1.find()) {
              for (int n = 0; n < names.size(); n++) {
                if (term.getName().equals(names.get(n))) {
                  num += 1;
                  Matcher num2 = numberPattern.matcher(lterm.get(numbers.get(n) + 1).getName());
                  if (num2.find()) {
                    if (!lterm.get(numbers.get(n) + 1).getName()
                        .equals(lterm.get(i + 1).getName())) {
                      String nam = term.getName() + lterm.get(i + 1).getName();
                      names.add(nam);
                      numbers.add(i);
                    }
                  }
                }
              }
              if (num == 0) {
                String nam = term.getName() + lterm.get(i + 1).getName();
                names.add(nam);
                numbers.add(i);
              }
            } else {
              names.add(term.getName());
              numbers.add(i);
            }

          }
        }
        if ((term.getNatureStr().equals("r"))
            && ((term.getName().equals("某某")) || (term.getName().equals("某")))) {
          if ((i > 0) && (lterm.get(i - 1).getName().length() == 1)) {
            String nam = lterm.get(i - 1).getName() + term.getName();
            for (int n = 0; n < names.size(); n++) {
              if (nam.equals(names.get(n))) {
                names.add(nam);
                numbers.add(i - 1);
                lterm.remove(i);
              }
            }
          }
        }
      }
      // 短句中如果说明了，但是没有人名，则认为前面的句中说明了人名且只有一个
      if (temp == 0) {
        number += 1;
      }
    }
    // }
    number += names.size();
    number = number == 0 ? 1 : number;

    return number;
  }
}
