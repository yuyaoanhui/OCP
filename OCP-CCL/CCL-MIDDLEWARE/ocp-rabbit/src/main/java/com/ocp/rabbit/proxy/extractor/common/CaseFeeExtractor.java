package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.bean.ParaLabelBean;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.algorithm.FeeCase;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.tool.algorithm.personage.PeopleType;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 案件审理费用
 *
 * @author yu.yao 2018年8月13日
 */
public class CaseFeeExtractor {
  private Context context;

  public CaseFeeExtractor(Context context) {
    this.context = context;
  }

  public void extract() {
    List<FeeCase> feeList = parseFeeCase();
    double iFee = 0;
    if (feeList != null && feeList.size() > 0) {
      iFee = feeList.get(0).getFeeCourt();
    }
    if (iFee > 0 && iFee < 10000000) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_fee_case[InfoPointKey.mode],
          feeList);
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_fee_case_total[InfoPointKey.mode],
          iFee);
    }
  }

  @SuppressWarnings("unchecked")
  private List<FeeCase> parseFeeCase() {
    String caseHierarchy = "";
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode]) != null) {
      caseHierarchy = (String) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_case_hierarchy[InfoPointKey.mode]);
    }
    // 判决主文
    List<String> content = new ArrayList<>();
    ParaLabelBean label = context.docInfo.getParaLabels().getByLabel("judgement_content");
    for (int i : label.getContent().keySet()) {
      content.add(label.getContent().get(i));
    }

    // 原告姓名
    List<String> plaintiffs = new ArrayList<>();
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode]) != null) {
      plaintiffs = (ArrayList<String>) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode]);
    }
    // 被告姓名
    List<String> defendants = new ArrayList<>();
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_defendant_names[InfoPointKey.mode]) != null) {
      defendants = (ArrayList<String>) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_defendant_names[InfoPointKey.mode]);
    }
    List<FeeCase> feeList = new ArrayList<FeeCase>();
    List<String> paras = new ArrayList<String>();
    for (String s : content) {
      Matcher m = Pattern.compile("费.*?[\\d\\.]*元").matcher(s);
      if (m.find()) {
        // 去掉括号
        Pattern dropBrackets = Pattern.compile(DocumentUtils.regex_dropBrackets);
        Matcher ma1 = dropBrackets.matcher(s);
        s = ma1.replaceAll("");

        // 将数字中的逗号或者空格去除
        s = formatNumber(s);

        // 根据标点符号分割句子
        String[] temp = s.split("[。，；：！？,;:!?]");
        Collections.addAll(paras, temp);
      }
    }
    List<String> listParas = new ArrayList<>();
    // 根据受理费出现的次数，将段落集合分成若干句
    List<String> feeParas = new ArrayList<>();
    for (String para : paras) {
      Matcher m = Pattern.compile("费.*?[\\d\\.]*元").matcher(para);
      if (m.find()) {
        if (feeParas.size() != 0) {
          listParas.add(buildSentence(feeParas));
        }
        feeParas = new ArrayList<>();
        feeParas.add(para);
      } else {
        feeParas.add(para);
      }
    }
    listParas.add(buildSentence(feeParas));

    // 在多个受理费组中筛选需要的受理费
    boolean flag = true;
    if (listParas.size() == 1) {
      String sentence = listParas.get(0);
      feeList = parseCase(sentence, plaintiffs, defendants);
      flag = false;
    } else {
      if (!TextUtils.isEmpty(caseHierarchy)) {
        int count = 0;
        for (String singleParas : listParas) {
          String sentence = singleParas;
          if (sentence.contains(caseHierarchy)) {
            count++;
          }
        }
        if (count == 1) {
          for (String singleParas : listParas) {
            String sentence = singleParas;
            if (sentence.contains(caseHierarchy)) {
              feeList = parseCase(sentence, plaintiffs, defendants);
              flag = false;
              break;
            }
          }
        } else {
          for (String singleParas : listParas) {
            String sentence = singleParas;
            Matcher ms = Pattern.compile("由.*?(承担|负担)").matcher(sentence);
            if (ms.find()) {
              feeList = parseCase(sentence, plaintiffs, defendants);
              flag = false;
              break;
            }
          }
        }
      }
    }

    if (flag) {// 有多个受理费组，且没有审判次数的，按第一个取
      String sentence = listParas.get(0);
      feeList = parseCase(sentence, plaintiffs, defendants);
    }
    return feeList;
  }

  // 格式化句子中的数字
  private String formatNumber(String sentence) {
    Matcher mComma = Pattern.compile("\\d{1,3}([,，]\\d{3})*([.,，]\\d+)?").matcher(sentence);
    while (mComma.find()) {
      int index = 0;
      int f = sentence.lastIndexOf(".");
      int f2 = sentence.lastIndexOf("，");
      int f3 = sentence.lastIndexOf(",");
      int[] ar = {f, f2, f3};
      for (int i : ar) {
        if (i > index) {
          index = i;
        }
      }
      String l;
      if (f == -1) {
        continue;
      } else {
        l = sentence.substring(f, f + 1);
      }

      sentence = sentence.replace(l, ".");
      String in = sentence.substring(0, index);
      String fl = sentence.substring(index);
      String sp = "[,，\\s]";
      Matcher m = Pattern.compile("\\d" + sp + "\\d").matcher(in);
      while (m.find()) {
        Matcher msp = Pattern.compile(sp).matcher(m.group());
        in = in.replace(m.group(), msp.replaceAll(""));
      }
      sentence = in + fl;
    }
    return sentence;
  }

  // 拼接成一个完整的受理费句子
  private String buildSentence(List<String> sentences) {
    String sentence = "";
    for (int i = 0; i < sentences.size(); i++) {
      sentence += sentences.get(i);
      if (i != sentences.size() - 1) {
        sentence += ",";
      }
    }
    return sentence;
  }

  private List<FeeCase> parseCase(String sentence, List<String> plaintiffs,
      List<String> defendants) {
    Matcher mrmb = Pattern.compile("[。，；：！？、,;:!?]元").matcher(sentence);
    while (mrmb.find()) {
      sentence = mrmb.replaceAll("元");
      mrmb = Pattern.compile("[。，；：！？、,;:!?]元").matcher(sentence);
    }

    Matcher m = Pattern.compile("[0-9]\\.[0-9]*元").matcher(sentence);
    if (m.find()) {
      sentence = sentence.replaceAll("元", "元，");
    }

    Matcher mhalf = Pattern.compile("[。，；：！？、,;:!?]减半").matcher(sentence);
    while (mhalf.find()) {
      sentence = mhalf.replaceAll("减半");
      mhalf = Pattern.compile("[。，；：！？、,;:!?]减半").matcher(sentence);
    }

    Matcher mboth = Pattern.compile("(原告|原).?被告").matcher(sentence);
    if (mboth.find()) {
      sentence = mboth.replaceAll("原告和被告");
    }

    // 将长句子分成小短句
    String[] shortSents = sentence.split("[\\,，]+");

    // 识别审理费用
    double temp_max = 0;

    // 如果句中含有合计，则以合计为审理总费用
    Matcher mtotal = Pattern.compile("([合共总]计).*?[\\d\\.]*元").matcher(sentence);
    Matcher ma = Pattern.compile("费.*?[\\d\\.]*元").matcher(sentence);
    if (mtotal.find()) {
      List<WrapNumberFormat> lwnf =
          new NumberRecognizer(new String[] {"元"}).getNumbers(mtotal.group(0), true);
      for (WrapNumberFormat wnf : lwnf) {
        if (wnf.getArabicNumber() > temp_max) {
          temp_max = wnf.getArabicNumber();
        }
      }
    } else {
      double total = 0;
      while (ma.find()) {
        List<WrapNumberFormat> lwnf =
            new NumberRecognizer(new String[] {"元"}).getNumbers(ma.group(0), true);
        for (WrapNumberFormat wnf : lwnf) {
          if (sentence.contains("受理费")) {
            // 如果句中有减半等词语
            if (sentence.contains("减半") && !(sentence.contains("已减半") || sentence.contains("已经减半"))
                && sentence.indexOf("减半") > sentence.indexOf("元")) {
              total += wnf.getArabicNumber() / 2;
            } else {
              total += wnf.getArabicNumber();
            }
          } else {
            total += wnf.getArabicNumber();
          }
        }
      }
      temp_max = total;
    }

    // 找出各费用的对应关系
    List<FeeCase> feeList = new ArrayList<>();
    for (String shortSent : shortSents) {
      // 识别该短句是否阐述受理费承担方
      if (shortSent.contains("费")
          && !(shortSent.contains("承担") || shortSent.contains("负担") || shortSent.contains("担负"))) {
        continue;// 认为该短句不是阐述受理费承担方
      }
      double total = 0;
      for (FeeCase fee : feeList) {
        total += fee.getFeePart();
      }
      if (total == temp_max) {
        continue;// 受理费已经分配完成，后面是对分配细节的阐述
      }


      // 找句子中的原告姓名数量
      int pltNameCount = 0;
      List<String> pltNames = new ArrayList<>();
      for (String pltName : plaintiffs) {
        if (shortSent.contains(pltName)) {
          if (shortSent.contains("支付给")) {
            int a = shortSent.indexOf(pltName);
            int b = shortSent.indexOf("支付给");
            if (a - b == 3) {
              continue;
            }
          }
          pltNames.add(pltName);
          pltNameCount++;
        }
      }
      // 找句子中的"原告"数量
      int pltCount = 0;
      Matcher mplt = patternPlt.matcher(shortSent);
      while (mplt.find()) {
        pltCount++;
      }
      // 找句子中的被告姓名数量
      int defNameCount = 0;
      List<String> defNames = new ArrayList<>();
      for (String defName : defendants) {
        if (shortSent.contains(defName)) {
          if (shortSent.contains("支付给")) {
            int a = shortSent.indexOf(defName);
            int b = shortSent.indexOf("支付给");
            if (b - a == 3) {
              continue;
            }
          }
          defNames.add(defName);
          defNameCount++;
        }
      }
      // 找句子中的"被告"数量
      int defCount = 0;
      Matcher mdef = patternDef.matcher(shortSent);
      while (mdef.find()) {
        defCount++;
      }
      // 找到该句中提到的费用
      List<WrapNumberFormat> listMoney =
          new NumberRecognizer(new String[] {"元"}).getNumbers(shortSent, true);

      // 找到该句中提到的百分比
      List<WrapNumberFormat> listPercent =
          new NumberRecognizer(new String[] {"%", "％"}).getPercentage(shortSent);
      for (WrapNumberFormat percent : listPercent) {
        percent.setArabicNumber(temp_max * percent.getArabicNumber());// 出现百分比则认为是受理费的百分比
      }

      listMoney.addAll(listPercent);// 认为句中只会出现全是百分比或者全是金额的情况
      double money = 0.0;
      if (pltCount != 0 || pltNameCount != 0 || defNameCount != 0 || defCount != 0) {
        if (shortSent.contains("承担") || shortSent.contains("负担") || shortSent.contains("担负")) {
          if (listMoney.size() == 0) {// 如果没提到费用则认为该当事人承担所有费用
            if (shortSent.contains("各半")) {
              money = temp_max / 2;
            } else {
              money = temp_max;
            }
          } else if (listMoney.size() == 1) {
            money = listMoney.get(0).getArabicNumber();
          }
        } else {
          continue;// 认为不是阐述费用分配方
        }
      } else {
        continue;// 认为不是阐述费用分配方
      }

      /*
       * 句中有原告没有被告
       */
      if ((pltCount != 0 || pltNameCount != 0) && (defNameCount == 0 && defCount == 0)) {
        // 没有声明是否原告，只有一个或多个原告的姓名 认为是这些原告承担费用
        if (pltCount == 0) {
          // 如果该句中只有一个金额（数字或百分比）
          if (listMoney.size() <= 1) {
            money = money * pltNames.size();
            double share = money / temp_max;
            // 如果句中出现关键词则认为是各自承担
            if (shortSent.contains("各承担") || shortSent.contains("各负担")) {
              for (String pltName : pltNames) {
                List<String> lk = new ArrayList<>();
                lk.add(pltName);
                addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
              }
              // 否则认为是共同承担
            } else {
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                  pltNames);
            }
          }
          // 如果该句中出现多个金额则认为多个人各自承担各自的金额
          else if (listMoney.size() > 1 && listMoney.size() >= pltNames.size()) {
            for (int k = 0; k < pltNames.size(); k++) {
              double cash = listMoney.get(k).getArabicNumber();
              double share = cash / temp_max;
              List<String> lk = new ArrayList<>();
              lk.add(pltNames.get(k));
              addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.PLAINTIFF, lk);
            }
          }

        }
        // 有声明是否原告
        else {
          // 没有提及原告的姓名 则认为所有原告共同承担句中费用
          if (pltNameCount == 0) {
            double share = money / temp_max;
            if (shortSent.contains("各承担") || shortSent.contains("各负担")) {
              for (String plaintiff : plaintiffs) {
                List<String> lk = new ArrayList<>();
                lk.add(plaintiff);
                addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
              }
            }
            // 否则认为是共同承担
            else {
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                  plaintiffs);
            }
          }
          // 提到一个或多个原告 认为是这些原告承担句中费用
          else {
            // 如果该句中只有一个金额（数字或百分比）
            if (listMoney.size() <= 1) {
              // 如果句中出现关键词则认为是各自承担
              if (shortSent.contains("各承担") || shortSent.contains("各负担")) {
                money = money * pltNames.size();
                double share = money / temp_max;
                for (String pltName : pltNames) {
                  List<String> lk = new ArrayList<>();
                  lk.add(pltName);
                  addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
                }
              }
              // 否则认为是共同承担
              else {
                double share = money / temp_max;
                addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                    pltNames);
              }

            }
            // 如果该句中出现多个金额则认为多个人各自承担各自的金额
            else if (listMoney.size() > 1 && listMoney.size() >= pltNames.size()) {
              for (int k = 0; k < pltNames.size(); k++) {
                double cash = listMoney.get(k).getArabicNumber();
                double share = cash / temp_max;
                List<String> lk = new ArrayList<>();
                lk.add(pltNames.get(k));
                addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.PLAINTIFF, lk);
              }
            }

          }
        }

      }
      /*
       * 句中有被告没有原告
       */
      else if ((defCount != 0 || defNameCount != 0) && (pltCount == 0 && pltNameCount == 0)) {
        // 没有声明是否被告，只有一个或多个被告的姓名 认为是这些被告承担费用
        if (defCount == 0) {
          // 如果该句中只有一个金额（数字或百分比）
          if (listMoney.size() <= 1) {
            money = money * defNames.size();
            double share = money / temp_max;
            // 如果句中出现关键词则认为是各自承担
            if (shortSent.contains("各承担") || shortSent.contains("各负担")) {
              for (String defName : defNames) {
                List<String> lk = new ArrayList<>();
                lk.add(defName);
                addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
              }
              // 否则认为是共同承担
            } else {
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                  defNames);
            }
          }
          // 如果该句中出现多个金额则认为多个人各自承担各自的金额
          else if (listMoney.size() > 1 && listMoney.size() >= defNames.size()) {
            for (int k = 0; k < defNames.size(); k++) {
              double cash = listMoney.get(k).getArabicNumber();
              double share = cash / temp_max;
              List<String> lk = new ArrayList<>();
              lk.add(defNames.get(k));
              addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.DEFENDANT, lk);
            }
          }

        }
        // 有声明是否被告
        else {
          // 没有提及被告的姓名 则认为所有被告共同承担句中费用
          if (defNameCount == 0) {
            double share = money / temp_max;
            if (shortSent.contains("各承担") || shortSent.contains("各负担")) {
              for (String defendant : defendants) {
                List<String> lk = new ArrayList<>();
                lk.add(defendant);
                addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
              }
            }
            // 否则认为是共同承担
            else {
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                  defendants);
            }
          }
          // 提到一个或多个被告 认为是这些被告承担句中费用
          else {
            // 如果该句中只有一个金额（数字或百分比）
            if (listMoney.size() <= 1) {
              // 如果句中出现关键词则认为是各自承担
              if (shortSent.contains("各承担") || shortSent.contains("各负担")) {
                money = money * defNames.size();
                double share = money / temp_max;
                for (String defName : defNames) {
                  List<String> lk = new ArrayList<>();
                  lk.add(defName);
                  addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
                }
              }
              // 否则认为是共同承担
              else {
                double share = money / temp_max;
                addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                    defNames);
              }

            }
            // 如果该句中出现多个金额则认为多个人各自承担各自的金额
            else if (listMoney.size() > 1 && listMoney.size() >= defNames.size()) {
              for (int k = 0; k < defNames.size(); k++) {
                double cash = listMoney.get(k).getArabicNumber();
                double share = cash / temp_max;
                List<String> lk = new ArrayList<>();
                lk.add(defNames.get(k));
                addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.DEFENDANT, lk);
              }
            }

          }
        }

      }
      /*
       * 句中既有被告也有原告
       */
      else if ((defCount != 0 || defNameCount != 0) && (pltCount != 0 || pltNameCount != 0)) {
        // 如果句中没有金额
        if (listMoney.size() == 0) {
          // 只声明"原告"和"被告",没有姓名
          if (pltNameCount == 0 && defNameCount == 0) {
            double share = money / (plaintiffs.size() + defendants.size()) / temp_max;
            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                plaintiffs);
            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                defendants);
          }
          // 声明了"原告"和被告的姓名
          else if (pltNameCount == 0 && defNameCount != 0) {
            double share = money / (plaintiffs.size() + defNames.size()) / temp_max;
            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                plaintiffs);

            for (String defName : defNames) {
              List<String> lk = new ArrayList<>();
              lk.add(defName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
            }
          }
          // 声明了"被告"和原告的姓名
          else if (pltNameCount != 0 && defNameCount == 0) {
            double share = money / (pltNames.size() + defendants.size()) / temp_max;
            for (String pltName : pltNames) {
              List<String> lk = new ArrayList<>();
              lk.add(pltName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
            }

            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                defendants);
          }
          // 声明了被告的姓名和原告的姓名
          else if (pltNameCount != 0 && defNameCount != 0) {
            double share = money / (pltNames.size() + defNames.size()) / temp_max;
            for (String pltName : pltNames) {
              List<String> lk = new ArrayList<>();
              lk.add(pltName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
            }
            for (String defName : defNames) {
              List<String> lk = new ArrayList<>();
              lk.add(defName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
            }
          }
        }
        // 如果该句中只有一个金额（数字或百分比）
        else if (listMoney.size() == 1) {
          // 只声明"原告"和"被告",没有姓名
          if (pltNameCount == 0 && defNameCount == 0) {
            double share = money / temp_max;
            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                plaintiffs);
            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                defendants);
          }
          // 声明了"原告"和被告的姓名
          else if (pltNameCount == 0 && defNameCount != 0) {
            double share = money / temp_max;
            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF,
                plaintiffs);

            for (String defName : defNames) {
              List<String> lk = new ArrayList<>();
              lk.add(defName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
            }
          }
          // 声明了"被告"和原告的姓名
          else if (pltNameCount != 0 && defNameCount == 0) {
            double share = money / temp_max;
            for (String pltName : pltNames) {
              List<String> lk = new ArrayList<>();
              lk.add(pltName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
            }

            addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT,
                defendants);
          }
          // 声明了被告的姓名和原告的姓名
          else if (pltNameCount != 0 && defNameCount != 0) {
            double share = money / temp_max;
            for (String pltName : pltNames) {
              List<String> lk = new ArrayList<>();
              lk.add(pltName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.PLAINTIFF, lk);
            }
            for (String defName : defNames) {
              List<String> lk = new ArrayList<>();
              lk.add(defName);
              addCaseFee(listMoney, feeList, temp_max, money, share, PeopleType.DEFENDANT, lk);
            }
          }
        }
        // 如果该句中出现多个金额则认为多个人各自承担各自的金额
        else {
          // 只声明"原告"和"被告",没有姓名
          if (pltNameCount == 0 && defNameCount == 0) {
            List<String> totalTypes = new ArrayList<>();
            List<String> allTypes = new ArrayList<>();
            totalTypes.addAll(defKeyWords);
            totalTypes.addAll(pltKeyWords);
            List<Term> lterm = StandardTokenizer.segment(shortSent);
            for (Term t : lterm) {
              for (String str : totalTypes) {
                if (str.equals(t.word)) {
                  allTypes.add(t.word);
                }
              }
            }
            if (allTypes.size() <= listMoney.size()) {
              for (int k = 0; k < allTypes.size(); k++) {
                double cash = listMoney.get(k).getArabicNumber();
                double share = cash / temp_max;
                for (String name : pltKeyWords) {
                  if (name.equals(allTypes.get(k))) {
                    addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.PLAINTIFF,
                        plaintiffs);
                    break;
                  }
                }
                for (String name : defKeyWords) {
                  if (name.equals(allTypes.get(k))) {
                    addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.DEFENDANT,
                        defendants);
                    break;
                  }
                }
              }
            }
          }
          // 声明了被告的姓名和原告的姓名
          else if (pltNameCount != 0 && defNameCount != 0) {
            List<String> totalNames = new ArrayList<>();
            List<String> allNames = new ArrayList<>();
            totalNames.addAll(plaintiffs);
            totalNames.addAll(defendants);
            List<Term> lterm = StandardTokenizer.segment(shortSent);
            for (Term t : lterm) {
              for (String str : totalNames) {
                if (str.equals(t.word)) {
                  allNames.add(t.word);
                }
              }
            }
            if (allNames.size() <= listMoney.size()) {
              for (int k = 0; k < allNames.size(); k++) {
                double cash = listMoney.get(k).getArabicNumber();
                double share = cash / temp_max;
                List<String> lk = new ArrayList<>();
                lk.add(allNames.get(k));
                for (String name : plaintiffs) {
                  if (name.equals(allNames.get(k))) {
                    addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.PLAINTIFF, lk);
                    break;
                  }
                }
                for (String name : defendants) {
                  if (name.equals(allNames.get(k))) {
                    addCaseFee(listMoney, feeList, temp_max, cash, share, PeopleType.DEFENDANT, lk);
                    break;
                  }
                }
              }
            }
          }
        }
      }
    }
    return feeList;
  }

  // 加入到返回结果中
  private void addCaseFee(List<WrapNumberFormat> listMoney, List<FeeCase> feeList, double feeCourt,
      double feepart, double share, PeopleType pt, List<String> names) {
    FeeCase fee = new FeeCase(feeCourt, feepart, share, pt, names);
    boolean flag = false;
    for (FeeCase single : feeList) {
      // 如果该姓名在之前的集合中已经出现，且句中没有涉及到金额，视为对之前的姓名的阐述，不计入结果中
      if (single.getNames().toString().equals(names.toString()) && listMoney.size() == 0) {
        flag = true;
        break;
      }
    }
    if (!flag) {
      feeList.add(fee);
    }
  }

  private static Pattern patternDef = Pattern.compile("被告|被上诉人|被申诉人|被申请人|原公诉机关");
  private static Pattern patternPlt =
      Pattern.compile("原告|[^被]上诉人|[^被]申诉人|[^被]申请人|[^被]申请再审人|公诉机关|抗诉机关");

  private static Set<String> defKeyWords = new HashSet<String>();
  private static Set<String> pltKeyWords = new HashSet<String>();

  static {
    defKeyWords.add("被告");
    defKeyWords.add("被告人");
    defKeyWords.add("被上诉人");
    defKeyWords.add("被申诉人");
    defKeyWords.add("被申请人");
    pltKeyWords.add("原告");
    pltKeyWords.add("原告人");
    pltKeyWords.add("上诉人");
    pltKeyWords.add("申诉人");
    pltKeyWords.add("申请人");
    pltKeyWords.add("申请再审人");
  }
}
