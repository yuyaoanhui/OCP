package com.ocp.rabbit.proxy.extractor.custom.divoce;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 子女信息
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class ChildExtractor {

  private Context context;

  public ChildExtractor(Context context) {
    this.context = context;
  }

  static final String childName = "info_子女姓名";
  static final String childGender = "info_子女性别";
  static final String childDob = "info_子女出生日期";

  private static final Pattern patternChild =
      Pattern.compile("((二三四五六七八两)个?)?(婚生子|养子|婚生女|女儿|子女|生子|生女|儿子|男孩|女孩|之子|之女|[一长次][子女])");
  private static final Pattern patternBirth =
      Pattern.compile("生育|于[\u4e00-\u9fa5\\d]+生|育有|养育|抚养|[有生][\u4e00-\u9fa5]*?[子女]|婚生|取名|出生|降生");
  private static final Pattern patternBirthNumber = Pattern.compile("生育?|育?有|养育|抚养");
  private static final Pattern noKid = Pattern.compile("((未|没有)生育(子女|孩子))|((无|没有)(子女|孩子))");
  private static String[] kidUnits =
      new String[] {"子女", "子", "女儿", "个女", "儿子", "个儿", "男", "小孩", "女"};

  @SuppressWarnings("unchecked")
  public void childExtract(List<String> paragraphList) {
    List<String> defNames = (List<String>) context.rabbitInfo.extractInfo
        .getOrDefault(InfoPointKey.meta_defendant_names[InfoPointKey.mode], new ArrayList<>());
    List<String> pltNames = (List<String>) context.rabbitInfo.extractInfo
        .getOrDefault(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode], new ArrayList<>());
    List<NamedEntity[]> lnes = new ArrayList<>();
    List<String[]> kidInfo = new ArrayList<>();
    List<Map<String, String>> lmap = new ArrayList<>();
    boolean no_kid = ChildExtractor.parseKidInfo(paragraphList, defNames, pltNames, lnes, kidInfo);
    int[] childNumber = ChildExtractor.parseChildNumber(lnes, kidInfo, no_kid);
    if (kidInfo.size() != 0) {
      // 转换输出格式
      for (String[] kid : kidInfo) {
        Map<String, String> kidMap = new HashMap<>();
        if (null != kid[0])
          kidMap.put(ChildExtractor.childName, kid[0]);
        if (null != kid[1])
          kidMap.put(ChildExtractor.childGender, kid[1]);
        if (null != kid[2]) {
          DateTime birthData = DateHandler.makeDateTime(kid[2]);
          if (birthData != null) {
            kidMap.put(ChildExtractor.childDob, DateHandler.convertDateTimeFormat(birthData));
          }
        }
        if (kidMap.size() != 0)
          lmap.add(kidMap);
      }
    }
    List<Integer> ageRange = null;
    if (childNumber != null) {
      context.rabbitInfo.extractInfo.put(InfoPointKey.info_divorce_number_kids[InfoPointKey.mode],
          childNumber[0]);
      context.rabbitInfo.extractInfo.put(InfoPointKey.info_divorce_number_boy[InfoPointKey.mode],
          childNumber[1]);
      context.rabbitInfo.extractInfo.put(InfoPointKey.info_divorce_number_girl[InfoPointKey.mode],
          childNumber[2]);
      ageRange = this.generateDerivedInfo(kidInfo);
    }
    if (ageRange != null && ageRange.size() > 0) {
      context.rabbitInfo.extractInfo
          .put(InfoPointKey.info_divorce_kids_age_range[InfoPointKey.mode], ageRange);
    }
    if (lmap.size() > 0) {
      context.rabbitInfo.extractInfo.put(InfoPointKey.info_kid_info[InfoPointKey.mode], lmap);
    }
  }

  public static boolean parseKidInfo(List<String> paragraphs, List<String> defNames,
      List<String> pltNames, List<NamedEntity[]> lnes, List<String[]> kidInfo) {

    String[] parentNames, defNplt = new String[2];
    defNplt[0] = defNames.size() > 0 ? defNames.get(0) : null;
    defNplt[1] = pltNames.size() > 0 ? pltNames.get(0) : null;
    List<String> _parentNames = new ArrayList<>();
    String tmp;
    tmp = defNames.size() > 0 ? defNames.get(0).substring(0, 1) : null;
    if (tmp != null)
      _parentNames.add(tmp);
    tmp = pltNames.size() > 0 ? pltNames.get(0).substring(0, 1) : null;
    if (tmp != null)
      _parentNames.add(tmp);
    parentNames = _parentNames.toArray(new String[_parentNames.size()]);
    String[][] sentences = DocumentUtils.splitSentencesByPeriod(paragraphs);
    boolean no_kid = false;
    String timeBase = null;
    for (String[] paragraph : sentences) {
      for (String sentence : paragraph) {
        // handling here
        sentence = sentence + "。";

        // 判断小孩个数
        if (!no_kid && noKid.matcher(sentence).find()) {
          no_kid = true;
          break;
        }
        // 具有生育等动作的句子
        NamedEntity[] nes_action =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternBirth);
        // 时间位置，考虑了上下文的时间提取
        DateHandler dh = new DateHandler(sentence, timeBase);
        NamedEntity[] nes_date = NamedEntityRecognizer.recognizeTime(dh);
        if (nes_date.length > 0) {
          timeBase = dh.getTimeBase();
        }

        if (nes_action.length == 0) {
          continue;
        }
        List<NamedEntity> allEntities = new ArrayList<>();
        allEntities.addAll(Arrays.asList(nes_action));
        // 标点符号位置
        Integer[] commas = NamedEntityRecognizer.recognizeComma(sentence);
        // 提及小孩等关键词位置
        NamedEntity[] nes_kids =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternChild);
        allEntities.addAll(Arrays.asList(nes_kids));
        allEntities.addAll(Arrays.asList(nes_date));
        // 小孩名字位置
        NamedEntity[] nes_kids_names =
            recognizeChildName(sentence, parentNames, defNplt, commas, allEntities);
        kidInfo
            .addAll(entityMatch(sentence, commas, nes_action, nes_kids, nes_date, nes_kids_names));
        if (!no_kid) {
          NamedEntity[] kids_number = NamedEntityRecognizer.recognizeNumber(sentence, kidUnits);
          if (kids_number.length <= 0) {
            continue;
          }
          NamedEntity[] kids_number_action =
              NamedEntityRecognizer.recognizeEntityByRegex(sentence, patternBirthNumber);
          lnes.addAll(NamedEntityRecognizer.entityMatch(sentence, kids_number_action, kids_number,
              true, false));
        }
      }
    }
    // 结合小孩信息综合判断小孩个数
    return no_kid;
  }

  private static List<String[]> entityMatch(String s, Integer[] nesCommas, NamedEntity[] nes_action,
      NamedEntity[] nes_kids, NamedEntity[] nes_date, NamedEntity[] nes_kids_names) {
    /**
     * 规则： 1. 根据小孩名字和小孩关键词位置，配对 2. 在配对里面，根据最小的位置，和时间配对
     */
    List<NamedEntity[]> lneKidName =
        NamedEntityRecognizer.entityMatch(s, nesCommas, nes_kids, nes_kids_names, false, false);
    List<NamedEntity[]> nes_date_new = new ArrayList<>();
    for (NamedEntity ne : nes_date)
      nes_date_new.add(new NamedEntity[] {ne});
    List<NamedEntity[]> lneDateKidName =
        NamedEntityRecognizer.entityMatch(nesCommas, nes_date_new, lneKidName, false, true);
    List<String[]> kidInfo = new ArrayList<>();

    for (NamedEntity[] nes : lneDateKidName) {
      String kid = nes[1].getSource();
      String gender = null;
      if (kid.contains("子女")) {
        gender = null;
      } else if (kid.contains("女")) {
        gender = "女";
      } else {
        gender = "男";
      }
      String DoB = (String) nes[0].getInfo();
      String name = nes[2].getSource();
      kidInfo.add(new String[] {name, gender, DoB});
    }
    return kidInfo;
  }

  private static List<NamedEntity> filter(List<NamedEntity> lne, String[] defNplt) {
    List<NamedEntity> lne_new = new ArrayList<>();
    for (int i = 0; i < lne.size(); i++) {
      NamedEntity ne = lne.get(i);
      boolean flag = true;
      for (int j = 0; j < lne.size(); j++) {
        if (j == i)
          continue;
        NamedEntity ne2 = lne.get(j);
        if (ne2.getOffset() > ne.getOffset())
          break;
        // ne2.getOffset() <= ne.getOffset();
        if ((ne.getOffset() + ne.getSource().length() <= ne2.getOffset()
            + ne2.getSource().length())) {
          flag = false;
          break;
        }
      }
      if (flag) {
        boolean flag2 = false;
        for (String s : defNplt) {
          if (s != null && ne.getSource().equals(s)) {
            flag2 = true;
            break;
          }
        }
        if (!flag2)
          lne_new.add(ne);
      }
    }
    return lne_new;
  }

  public static int[] parseChildNumber(List<NamedEntity[]> lnes, List<String[]> kidInfo,
      boolean no_kid) {
    /**
     * int[0] 总数 int[1] 男孩个数 int[2] 女孩个数 int[3] 未知性别 如果男女未知，则取值为-1
     */

    int[] childNumber = new int[] {0, 0, 0, 0};
    if (no_kid)
      return childNumber;
    if (lnes.size() == 0 && kidInfo.size() == 0)
      return null;
    int male = 0, female = 0, unknown = 0, total = 0;
    for (NamedEntity[] nes : lnes) {
      String word = nes[1].getSource();
      if ((word.contains("子女") || word.contains("小孩"))) {
        // if (unknown == 0)
        unknown++;
      } else if (word.contains("女")) {
        // if (female == 0)
        female++;
      } else {
        // if (male == 0)
        male++;
      }
    }
    total = male + female + unknown;
    int male_kid = 0, female_kid = 0, unknown_kid = 0, total_kid = kidInfo.size();
    for (String[] kid : kidInfo) {
      if (kid[1] == null)
        unknown_kid++;
      else {
        switch (kid[1]) {
          case "男":
            male_kid++;
            break;
          case "女":
            female_kid++;
            break;
          default:
            unknown_kid++;
            break;
        }
      }
    }
    // 以下假设，只要有一个出现了男或者女，则不存在未知性别的小孩
    if (total_kid == 0) {
      // 没有找到出生日期，根据小孩性别确定总数
      if (male != 0 || female != 0)
        total = male + female;
      else {
        total = unknown;
      }
    } else if (male != 0 || female != 0) {
      // 如果日期里面的小孩性别吻合，则用总的个数相减
      if (male == male_kid) {
        female = total_kid - male;
        total = total_kid;
      } else if (female == female_kid) {
        male = total_kid - female;
        total = total_kid;
      } else if (unknown_kid == 0) {
        male = male_kid;
        female = female_kid;
        total = male + female;
      } else if (total == total_kid) {
        total = total_kid;
        // 未知性别为男
        male = male + unknown;
      } else {
        // 未知性别为男
        male = male + unknown;
        total = male + female;
      }
    }
    // (male ==0 && female ==0)
    else {
      if (total == total_kid) {
        if (unknown_kid == total_kid) {
          //
        } else {
          male = male_kid + unknown_kid;
          female = female_kid;
        }
      } else if (total < total_kid) {
        if (unknown_kid == total_kid) {
          total = total_kid;
        } else {
          male = male_kid + unknown_kid;
          female = female_kid;
          total = male + female;
        }
      } else {
        if (unknown == total) {
          //
        } else {
          if (unknown_kid == total_kid) {
            total = total_kid;
          } else {
            male = male_kid + unknown_kid;
            female = female_kid;
            total = male + female;
          }
        }
      }
    }
    childNumber[0] = total;
    childNumber[1] = male;
    childNumber[2] = female;
    childNumber[3] = unknown;
    return childNumber;
  }

  private static NamedEntity[] recognizeChildName(String s, String[] parentNames, String[] defNplt,
      Integer[] commas, List<NamedEntity> stop) {

    NamedEntity[] nes = NamedEntityRecognizer.recognizeEntityByString(s, parentNames);
    List<NamedEntity> lne = new ArrayList<>();

    Integer[] a = new Integer[commas.length + stop.size()];
    for (int i = 0; i < commas.length; i++)
      a[i] = commas[i];
    for (int i = commas.length; i < a.length; i++)
      a[i] = stop.get(i - commas.length).getOffset();
    Arrays.sort(a);
    for (NamedEntity ne : nes) {
      ;
      String newWord = NamedEntityRecognizer.subStringUntilComma(s, ne, commas);
      newWord = NamedEntityRecognizer.cutoffWords(s, a, newWord, ne.getOffset());
      if (newWord.length() >= 2 && newWord.length() <= 4 && !lne.contains(ne)) {
        lne.add(new NamedEntity(newWord, ne.getOffset(), null));
      }
    }
    List<NamedEntity> lne_new = filter(lne, defNplt);
    NamedEntity[] nes_result = lne_new.toArray(new NamedEntity[lne_new.size()]);
    return nes_result;
  }

  public List<Integer> generateDerivedInfo(List<String[]> kidInfo) {
    String recordDate =
        (String) context.rabbitInfo.extractInfo.get(InfoPointKey.meta_doc_date[InfoPointKey.mode]);
    DateTime recordDateTime = DateHandler.makeDateTime(recordDate);
    List<Integer> ageRange = new ArrayList<>();
    if (recordDateTime != null) {
      // 下面处理年龄
      for (String[] kid : kidInfo) {
        DateTime DoB = DateHandler.makeDateTime(kid[2]);
        if (DoB != null) {
          try {
            int yearDiff = DateHandler.getYearDiff(DoB, recordDateTime);
            if (yearDiff >= 0 && yearDiff <= 80) {
              ageRange.add(yearDiff);
            }
          } catch (Exception e) {
          }
        }
      }
    }
    return ageRange;
  }
}
