package com.ocp.rabbit.proxy.extractor.custom.divoce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.entity.RabbitInfo;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;

/**
 * 抚养信息
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class ChildCustodyExtractor {

  private Context context;
  private ReferLigitantRelatedInfoExtrator referExtractor;
  public ChildCustodyExtractor(Context context) {
    this.context = context;
    referExtractor =new ReferLigitantRelatedInfoExtrator(context);
  }

  private static final Pattern patternChild =
      Pattern.compile("((二三四五六七八两)个?)?(婚生子|婚生女|女儿|生子|生女|儿子|男孩|女孩|之子|之女|[一长次][子女])");
  private static final Pattern[] patternVerb = {Pattern.compile("(?<=由[\u4e00-\u9fa5×]{1,7})抚养"),
      Pattern.compile("(?<=随[\u4e00-\u9fa5×]{1,7})生活")};
  private static final Pattern[] patternsVerbMoney = {Pattern.compile("支付|给付|负担")};
  private static final Pattern patternAction = Pattern.compile("抚养|随[\u4e00-\u9fa5×]{1,7}生活");

  static class PaymentAndPayerFreq {
    double payment = 0;
    String freq = null;
    String payer = null;

    public PaymentAndPayerFreq(double payment, String freq, String payer) {
      this.payment = payment;
      this.freq = freq;
      this.payer = payer;
    }
  }

  public void childCustodyExtract(RabbitInfo rbInfo, List<String> keyNames) {
    List<String> labels = new ArrayList<String>();
    labels.add("judgement_content");
    List<Map<Integer, String>> paragraphList = context.docInfo.getParaLabels().getContentByLabels(labels);
    if (paragraphList.size() == 0) {
      return;
    }
    List<ChildCustody> lcc = this.parseChildCustody(rbInfo, paragraphList);
    if (lcc.size() != 0) {
      ChildCustodyExtractor.generateDerivedInfo(lcc, rbInfo, keyNames);
      for (ChildCustody cc : lcc) {
        List<Map<String, String>> mapCc =
            ChildCustodyExtractor.convertChildFormat(cc.getChildName());
        if (null != mapCc && mapCc.size() > 0) {
          cc.setChildren(mapCc);
        }
        cc.setChildName(null);
      }
      rbInfo.getExtractInfo().put(InfoPointKey.info_child_raise_info[InfoPointKey.mode], lcc);
    }
  }

  @SuppressWarnings("unchecked")
  public List<ChildCustody> parseChildCustody(RabbitInfo rbInfo,
      List<Map<Integer, String>> paragraphs) {
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    List<Map<String, String>> childInfo2 = (List<Map<String, String>>) rbInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.info_kid_info[InfoPointKey.mode], new ArrayList<>());
    List<String[]> childInfo = new ArrayList<>();
    for (Map<String, String> ci : childInfo2) {
      String[] child = {null, null, null};
      child[0] = ci.getOrDefault(ChildExtractor.childName, null);
      child[1] = ci.getOrDefault(ChildExtractor.childGender, null);
      child[2] = ci.getOrDefault(ChildExtractor.childDob, null);
      childInfo.add(child);
    }
    List<String> tmp = childInfo.stream().map(t -> t[0]).collect(Collectors.toList());
    String[] childNames = tmp.toArray(new String[tmp.size()]);

    List<ChildCustody> lcc = new ArrayList<>();
    String timeBase = null;
    List<String> mergedParas = new ArrayList<String>();
    for (Map<Integer, String> map : paragraphs) {
      for (int i : map.keySet()) {
        mergedParas.add(map.get(i));
      }
    }
    for (String parapgraph : mergedParas) {// 遍历该标签列表下的所有自然段
      if (!patternAction.matcher(parapgraph).find()) {
        continue;
      }
      Integer[] commas = NamedEntityRecognizer.recognizeComma(parapgraph);
      NamedEntity[] nes_litigant = lr.recognize(parapgraph);
      NamedEntity[] nes_children = findChildren(parapgraph, childNames, childInfo, patternChild);
      NamedEntity[] nes_money = NamedEntityRecognizer.recognizeMoney(parapgraph);
      NamedEntity[] nes_verb_money =
          NamedEntityRecognizer.recognizeEntityByRegex(parapgraph, patternsVerbMoney);
      NamedEntity[] nes_freq = findFrequency(parapgraph);
      // 时间位置，考虑了上下文的时间提取
      DateHandler dh = new DateHandler(parapgraph, timeBase);
      NamedEntity[] nes_date = NamedEntityRecognizer.recognizeTime(dh);
      if (nes_date.length > 0) {
        timeBase = dh.getTimeBase();
      }
      NamedEntity[] nes_verb =
          NamedEntityRecognizer.recognizeEntityByRegex(parapgraph, patternVerb);
      lcc = matchRaiser(parapgraph, nes_litigant, nes_children, nes_verb, commas);
      if (lcc.isEmpty()) {
        continue;
      }
      PaymentAndPayerFreq paymentAndPayerFreq = matchPaymentAndFrequencey(parapgraph, nes_litigant,
          nes_money, nes_verb_money, nes_freq, commas);
      String payer = paymentAndPayerFreq.payer;
      double payment = paymentAndPayerFreq.payment;
      String freq = paymentAndPayerFreq.freq;
      // 合并结果，得到最终输出
      for (ChildCustody cc : lcc) {
        if (payer != null) {
          cc.setPayer(payer);
        } else {
          cc.setPayer(LitigantUnit.reverseLabel(cc.getCustodian()));
        }
        if (payment != 0) {
          cc.setAlimoney(payment);
        }
        if (freq != null) {
          cc.setPaymentFrequency(freq);
        }
      }
      return lcc;
    }
    return lcc;
  }

  public static NamedEntity[] findChildren(String str, String[] childNames,
      List<String[]> childInfo, Pattern Pattern_Child) {
    NamedEntity[] nes2 = NamedEntityRecognizer.recognizeEntityByString(str, childNames);
    if (nes2.length != 0) {
      for (int i = 0; i < nes2.length; i++) {
        for (String[] s : childInfo) {
          if (nes2[i].getSource().equals(s[0])) {
            nes2[i].setInfo(s);
            break;
          }
        }
      }
      return nes2;
    } else {
      NamedEntity[] nes1 = NamedEntityRecognizer.recognizeEntityByRegex(str, Pattern_Child);
      for (int i = 0; i < nes1.length; i++) {
        String gender = "男";
        String source = nes1[i].getSource();
        if (source.contains("子女")) {
          gender = null;
        } else if (source.contains("女")) {
          gender = "女";
        }
        nes1[i].setInfo(new String[] {null, gender, null});
      }
      return nes1;
    }
  }

  private static final Pattern PATTERN_FREQ = Pattern.compile("半月|[按／/]月|半年|每个?月|[一每/／按]年|一次性|季度");

  private static NamedEntity[] findFrequency(String str) {
    NamedEntity[] nes = NamedEntityRecognizer.recognizeEntityByRegex(str, PATTERN_FREQ);
    for (int i = 0; i < nes.length; i++) {
      String source = nes[i].getSource();
      String freq = null;
      if (source.equals("半年")) {
        freq = "半年";
      } else if (source.equals("一次性")) {
        freq = "一次性";
      } else if (source.equals("季度")) {
        freq = "季度";
      } else if (source.equals("半月")) {
        freq = "半月";
      } else if (source.contains("月")) {
        freq = "每月";
      } else {
        freq = "每年";
      }
      nes[i].setInfo(freq);
    }
    return nes;
  }

  private static List<ChildCustody> matchRaiser(String paragraph, NamedEntity[] nes_litigant,
      NamedEntity[] nes_children, NamedEntity[] nes_verb, Integer[] commas) {
    List<NamedEntity[]> nes_litigant_verb = new ArrayList<>();
    List<NamedEntity[]> nes_litigant_vb =
        NamedEntityRecognizer.entityMatch(paragraph, commas, nes_litigant, nes_verb, true, true);
    for (NamedEntity[] nes : nes_litigant_vb) {
      String label = ((LitigantUnit) nes[0].getInfo()).getLabel();
      if (!"原被告".equals(label)) {
        nes_litigant_verb.add(nes);
      }
    }
    List<NamedEntity[]> nes_children_new = new ArrayList<>();
    for (NamedEntity ne : nes_children)
      nes_children_new.add(new NamedEntity[] {ne});
    List<NamedEntity[]> nes_children_litigant_verb = NamedEntityRecognizer
        .entityMatchAllLeft(paragraph, commas, nes_children_new, nes_litigant_verb, false, true);

    Map<String, ChildCustody> lcc = new HashMap<>();
    for (NamedEntity[] nes : nes_children_litigant_verb) {

      String custodian = ((LitigantUnit) nes[1].getInfo()).getLabel();
      ChildCustody cc = lcc.getOrDefault(custodian, new ChildCustody());
      // TODO 去掉重复的小孩信息
      cc.getChildName().add((String[]) nes[0].getInfo());
      cc.setCustodian(custodian);
      lcc.put(custodian, cc);
      // lcc.add(cc);
    }
    // 合并相同的抚养人
    List<ChildCustody> lcc_new = new ArrayList<>();
    for (Map.Entry<String, ChildCustody> entry : lcc.entrySet()) {
      entry.getValue().setCustodian(entry.getKey());
      lcc_new.add(entry.getValue());
    }
    return lcc_new;
  }

  private static PaymentAndPayerFreq matchPaymentAndFrequencey(String paragraph,
      NamedEntity[] nes_litigant, NamedEntity[] nes_money, NamedEntity[] nes_verb_money,
      NamedEntity[] nes_freq, Integer[] commas) {
    List<NamedEntity[]> lnes_litigant_money = NamedEntityRecognizer.entityMatchAllLeft(paragraph,
        commas, NamedEntityRecognizer.changeFormat(nes_litigant),
        NamedEntityRecognizer.changeFormat(nes_money), true, false);
    double payment = 0;
    String freq = null;
    String payer = null;
    if (lnes_litigant_money.size() != 0) {
      payment = (double) lnes_litigant_money.get(0)[1].getInfo();
      payer = ((LitigantUnit) lnes_litigant_money.get(0)[0].getInfo()).getLabel();
    }
    List<NamedEntity[]> lnes_freq_verb_money =
        NamedEntityRecognizer.entityMatch(paragraph, commas, nes_freq, nes_verb_money, true, true);
    if (lnes_freq_verb_money.size() != 0) {
      freq = (String) lnes_freq_verb_money.get(0)[0].getInfo();
    }
    if (freq == null && nes_freq.length != 0) {
      freq = (String) nes_freq[0].getInfo();
    }
    return new PaymentAndPayerFreq(payment, freq, payer);
  }

  public static List<Map<String, String>> convertChildFormat(List<String[]> source) {
    if (null == source)
      return null;
    List<Map<String, String>> dest = new ArrayList<>();
    for (String[] kid : source) {
      Map<String, String> kidAttrMap = new HashMap<>();
      if (null != kid[0])
        kidAttrMap.put(ChildExtractor.childName, kid[0]);
      if (null != kid[1])
        kidAttrMap.put(ChildExtractor.childGender, kid[1]);
      if (null != kid[2]) {
        DateTime birthData = DateHandler.makeDateTime(kid[2]);
        if (birthData != null) {
          kidAttrMap.put(ChildExtractor.childDob, DateHandler.convertDateTimeFormat(birthData));
        }
      }
      if (kidAttrMap.size() > 0)
        dest.add(kidAttrMap);
    }
    return dest;
  }

  public static void generateDerivedInfo(List<ChildCustody> lcc, RabbitInfo rbInfo,
      List<String> keyNames) {

    String gender = (String) rbInfo.getExtractInfo()
        .getOrDefault(InfoPointKey.info_plaintiff_gender[InfoPointKey.mode], null);
    // 抚养费支付方式
    String freq = lcc.get(0).getPaymentFrequency();
    if (freq != null)
      rbInfo.getExtractInfo().put(keyNames.get(0), freq);

    int boyRaiserD = 0, girlRaiserD = 0, unknownRaiserD = 0, totalRaiserD = 0;
    int boyRaiserP = 0, girlRaiserP = 0, unknownRaiserP = 0, totalRaiserP = 0;
    double moneyD = 0, moneyP = 0;
    for (ChildCustody cc : lcc) {
      String raiser = cc.getCustodian();
      String payer = cc.getPayer();
      if (payer != null) {
        if (payer.equals(LitigantUnit.LABEL_DEFENDANT)) {
          moneyD = cc.getAlimoney();
        } else if (payer.equals(LitigantUnit.LABEL_PLAINTIFF)) {
          moneyP = cc.getAlimoney();
        }
      }
      if (raiser.equals(LitigantUnit.LABEL_DEFENDANT)) {
        for (String[] child : cc.getChildName()) {
          if (child[1] == null) {
            unknownRaiserD++;
          } else if (child[1].equals("男")) {
            boyRaiserD++;
          } else {
            girlRaiserD++;
          }
        }
      } else if (raiser.equals(LitigantUnit.LABEL_PLAINTIFF)) {
        for (String[] child : cc.getChildName()) {
          if (child[1] == null) {
            unknownRaiserP++;
          } else if (child[1].equals("男")) {
            boyRaiserP++;
          } else {
            girlRaiserP++;
          }
        }
      } else {
        // 错误情况
        return;
      }
    }
    // 父亲母亲所负抚养费
    if (moneyD != 0) {
      if (gender != null && freq != null) {
        if (gender.equals("男")) {
          double moneyC = convertMoney(moneyD, freq);
          if (moneyC != 0)
            rbInfo.getExtractInfo().put(keyNames.get(2), moneyC);
        } else if (gender.equals("女")) {
          double moneyC = convertMoney(moneyD, freq);
          if (moneyC != 0)
            rbInfo.getExtractInfo().put(keyNames.get(1), moneyC);
        }
      }
    } else if (moneyP != 0) {
      if (gender != null && freq != null) {
        if (gender.equals("女")) {
          double moneyC = convertMoney(moneyD, freq);
          if (moneyC != 0)
            rbInfo.getExtractInfo().put(keyNames.get(2), moneyC);
        } else if (gender.equals("男")) {
          double moneyC = convertMoney(moneyD, freq);
          if (moneyC != 0)
            rbInfo.getExtractInfo().put(keyNames.get(1), moneyC);
        }
      }
    }
    // 所有小孩抚养人
    totalRaiserD = boyRaiserD + girlRaiserD + unknownRaiserD;
    totalRaiserP = boyRaiserP + girlRaiserP + unknownRaiserP;
    if (totalRaiserD == 0) {
      if (totalRaiserP != 0) {
        if (gender != null) {
          if (gender.equals("男")) {
            rbInfo.getExtractInfo().put(keyNames.get(5), "男方");
          } else {
            rbInfo.getExtractInfo().put(keyNames.get(5), "女方");
          }
        }
      }
    } else {
      if (totalRaiserP == 0) {
        if (gender != null) {
          if (gender.equals("女")) {
            rbInfo.getExtractInfo().put(keyNames.get(5), "男方");
          } else {
            rbInfo.getExtractInfo().put(keyNames.get(5), "女方");
          }
        }
      } else {
        rbInfo.getExtractInfo().put(keyNames.get(5), "双方");
      }
    }

    // 男孩抚养人
    if (boyRaiserD != 0 && boyRaiserP == 0) {
      if ("女".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(3), "男方");
      } else if ("男".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(3), "女方");

      }
    } else if (boyRaiserD == 0 && boyRaiserP != 0) {
      if ("女".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(3), "女方");
      } else if ("男".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(3), "男方");
      }
    } else if (boyRaiserD != 0 && boyRaiserP != 0) {
      rbInfo.getExtractInfo().put(keyNames.get(3), "双方");
    }

    // 女孩抚养人
    if (girlRaiserD != 0 && girlRaiserP == 0) {
      if ("女".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(4), "男方");
      } else if ("男".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(4), "女方");
      }
    } else if (girlRaiserD == 0 && girlRaiserP != 0) {
      if ("女".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(4), "女方");
      } else if ("男".equals(gender)) {
        rbInfo.getExtractInfo().put(keyNames.get(4), "男方");
      }
    } else if (girlRaiserD != 0 && girlRaiserP != 0) {
      rbInfo.getExtractInfo().put(keyNames.get(4), "双方");
    }
  }

  private static double convertMoney(double money, String freq) {
    if (freq.equals("半年")) {
      return money / 6;
    } else if (freq.equals("一次性")) {
      return 0;
    } else if (freq.equals("季度")) {
      return money / 3;
    } else if (freq.equals("半月")) {
      return money * 2;
    } else if (freq.contains("月")) {
      return money;
    } else {
      return money / 12;
    }
  }
}
