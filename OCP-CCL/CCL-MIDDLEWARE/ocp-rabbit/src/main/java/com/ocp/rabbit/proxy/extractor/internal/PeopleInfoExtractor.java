package com.ocp.rabbit.proxy.extractor.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.splitWord.analysis.NlpAnalysis;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;

import com.ocp.rabbit.proxy.extractor.AbstractExtractor;
import com.ocp.rabbit.proxy.extractor.common.CriminalRecordExtractor;
import com.ocp.rabbit.proxy.extractor.common.CriminalRecordExtractor2;
import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.extractor.common.SequenceTimeInfoExtractor;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.InstitutionClassifier;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.algorithm.LitigantRoleRecognizer;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.algorithm.NumberRecognizer;
import com.ocp.rabbit.repository.constant.CaseType;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.date.DateHandler;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.tool.algorithm.number.WrapNumberFormat;
import com.ocp.rabbit.repository.tool.algorithm.personage.IDCardJudge;
import com.ocp.rabbit.repository.tool.algorithm.personage.NameHandler;
import com.ocp.rabbit.repository.tool.algorithm.personage.NameWrapper;
import com.ocp.rabbit.repository.tool.algorithm.personage.People;
import com.ocp.rabbit.repository.tool.algorithm.personage.PeopleType;
import com.ocp.rabbit.repository.tool.algorithm.personage.PretrialDetention;
import com.ocp.rabbit.repository.tool.algorithm.personage.Relation;
import com.ocp.rabbit.repository.tool.algorithm.personage.RelationType;
import com.ocp.rabbit.repository.tool.algorithm.profession.ProfessionContainer;
import com.ocp.rabbit.repository.tool.algorithm.profession.ProfessionLevel;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.Position;
import com.ocp.rabbit.repository.util.TextUtils;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

/**
 * 人物信息抽取
 *
 * @author yu.yao 2018年8月13日
 */
public class PeopleInfoExtractor {

  private Context context;
  private ReferLigitantRelatedInfoExtrator referExtractor;
  // 标准化
  private final static Map<String, String> ethnicGroupMapper =
      ResourceReader.loadEthnicGroupMapper();
  private final static Pattern patternEducation = ResourceReader.makeEducationPattern();
  private final static Map<String, String> educationMapper = ResourceReader.loadEducationMapper();

  private class Controller {
    String[] sourceSents;
    List<Integer> notVisited;

    private Controller(String[] sourceSents) {
      this.sourceSents = sourceSents;
      notVisited = new ArrayList<>();
      for (int i = 0; i < sourceSents.length; i++) {
        notVisited.add(i);
      }
    }
  }

  private class ParaContext {
    People current;
    People recentLitigant;

    public ParaContext() {}
  }

  private String courtName;
  private String type;// 组织类型：法院、检察院等

  private final static Pattern PATTERN_CRIMINAL_SUSPECT =
      Pattern.compile("^([－\\\\-a-zA-Z\\u4e00-\\u9fa5·\\.\\*]{2,}?)[,，、.。\\(（]");
  private static final Pattern[] PATTERN_LAW_FIRM =
      new Pattern[] {Pattern.compile("[,，]系?是?([^,，]*(?:事务所|服务所|援助中心|律所))"),
          Pattern.compile("[,，]系?是?([^,，]*)(?:(?:实习|见习)?律师|法律工作者)"),};
  private static final Pattern PATTERN_LAW_JOB = Pattern.compile("((实习|见习)?律师|法律工作者)");
  private static final Pattern PATTERN_CHIEF_JUDGE = Pattern.compile("审判长[,，:：]?(.+$)");
  private static final Pattern PATTERN_JUDGE = Pattern.compile("审判员[,，:：]?(.+$)");
  private static final Pattern PATTERN_CLERK = Pattern.compile("书记员[,，:：]?(.+$)");
  private static final Pattern PATTERN_JUDGE_ASSESSOR = Pattern.compile("(?:人民)?陪审员[,，:：]?(.+$)");
  private static final DateTime DTMIN =
      new DateTime(1992, 1, 1, 0, 0, 0, ISOChronology.getInstanceUTC());
  private static final DateTime DTMAX =
      new DateTime(2017, 6, 1, 0, 0, 0, ISOChronology.getInstanceUTC());
  private static final DateTime DTMAX1 =
      new DateTime(2000, 6, 1, 0, 0, 0, ISOChronology.getInstanceUTC());
  private static final NumberRecognizer numberRecognizer =
      new NumberRecognizer(new String[] {"个", "位", "被告", "原告", "上诉人", "名", "附带"});
  // 不能出现的词语
  private static final Pattern[] PATTERN_POS_BOUNDARY_WORDS = {Pattern
      .compile("(^因)|涉嫌|罪|(被[\u4e00-\u9fa5]*(公安局|派出所|检察院|法院))|判处|拘留|收容|教养|管制|曾因|因犯|有期|羁押|取保候审"),};
  private static final Pattern[] PATTERNS_BEGIN =
      {Pattern.compile("涉嫌|因本案"), Pattern.compile("被[^,，;；。\\.、]+(公安|分)局"),
          Pattern.compile("拘留|羁押|逮捕"), Pattern.compile("检察院批准")};

  public PeopleInfoExtractor(Context context, String litigantRoleFile, String type,
      String courtName) {
    this.context = context;
    this.courtName = courtName;
    this.type = type;
    referExtractor = new ReferLigitantRelatedInfoExtrator(context);
  }

  /**
   * 人物信息抽取入口
   *
   * @param
   * @return
   * @author yu.yao
   */
  public void extract() {
    List<People> l_p = new ArrayList<People>();// 当事人姓名列表：原被告、委托代理人、律师、代理人等
    if (ParaLabelEnum.PROCURATORATE.toString().equals(type)) {
      List<String> l_names_supect = new ArrayList<String>();// 犯罪嫌疑人姓名列表
      extract1(l_p, l_names_supect);
    } else if (ParaLabelEnum.COURT.toString().equals(type)) {
      List<String> l_names_def = new ArrayList<String>();// 被告姓名列表
      List<String> l_names_plt = new ArrayList<String>();// 原告姓名列表
      Map<String, People> peopleMap = new HashMap<String, People>();// <人名,该人名下的所有信息点>
      List<Relation> lr = new ArrayList<Relation>();// 当事人关系
      extract0(l_p, l_names_def, l_names_plt, peopleMap, lr);
    }
  }

  /**
   * 法院人物信息抽取
   *
   * @param
   * @return
   * @author yu.yao
   */
  private void extract0(List<People> l_p, List<String> l_names_def, List<String> l_names_plt,
      Map<String, People> peopleMap, List<Relation> lr) {
    // 抽取法院当事人姓名、角色、关系
    for (int i = 0; i < context.getAllUnits().size(); i++) {
      String tag = context.docInfo.getParaLabels().getParagraphLabel(i + 1).label;
      String paragraph = context.getAllUnits().get(i);
      if (!AbstractExtractor.Litigant.contains(tag)) {// 判断是否属于人物标签
        continue;
      }
      String peopType = tag.toUpperCase();
      PeopleType pt = PeopleType.getPeopleType(peopType);// 标签和人物类型存在对应关系，可根据标签获得人物类型
      Position position = LitigantRoleRecognizer.recognizeNameRoles(paragraph, i, pt);
      if (position == null) {
        continue;
      }
      NameWrapper nc = NameHandler.getNameType(position.getValue());
      String nameCleaned = nc.getNameCleaned();
      List<String> names = nc.getNamesSplit();
      if (nameCleaned == null && (names == null || names.isEmpty())) {// 没有抽取到任何人名
        continue;
      }
      int nameType = nc.getNameType();
      People people = null;
      if (nameCleaned == null) {// 如果是多个人名
        for (String name : names) {
          nameCleaned = name;
          if (position.getValue().contains("检察院")) {
            setProcuratorateInfo(l_p, nameCleaned, position);
          } else if ((ParaLabelEnum.DEFENDANT.getLabel().equals(tag))
              || (ParaLabelEnum.PLAINTIFF.getLabel().equals(tag))
              || (ParaLabelEnum.THIRD_PERSON.getLabel().equals(tag))) {
            people = new People(nameCleaned, pt, nameType, position);
            setRoles(people);
            peopleMap.put(nameCleaned, people);
            if (ParaLabelEnum.DEFENDANT.getLabel().equals(tag))
              l_names_def.add(nameCleaned);
            if (ParaLabelEnum.PLAINTIFF.getLabel().equals(tag))
              l_names_plt.add(nameCleaned);
            l_p.add(people);
          } else {
            RelationType rt = RelationType.getRelationType(peopType);
            if (peopleMap.containsKey(nameCleaned)) {
              people = peopleMap.get(nameCleaned);
              people.getPtypes().add(pt);
            } else {
              people = new People(nameCleaned, pt, nameType, position);
            }
            // 添加律师信息，将委托代理人和辩护人设为律师
            if ((ParaLabelEnum.ATTORNEY.getLabel().equals(tag))
                || (ParaLabelEnum.ENTRUSTED.getLabel().equals(tag))) {
              setLawFirm(people, paragraph);
            }
            if (!peopleMap.containsKey(nameCleaned))
              l_p.add(people);
            // 当事人关系
            parsePrivity(peopleMap, paragraph, people, lr, rt, i, l_p);
          }
        }
      } else {// 如果只有单个人名
        if (position.getValue().contains("检察院")) {
          setProcuratorateInfo(l_p, nameCleaned, position);
        } else if ((ParaLabelEnum.DEFENDANT.getLabel().equals(tag))
            || (ParaLabelEnum.PLAINTIFF.getLabel().equals(tag))
            || (ParaLabelEnum.THIRD_PERSON.getLabel().equals(tag))) {
          people = new People(nameCleaned, pt, nameType, position);
          setRoles(people);
          peopleMap.put(nameCleaned, people);
          if (ParaLabelEnum.DEFENDANT.getLabel().equals(tag))
            l_names_def.add(nameCleaned);
          if (ParaLabelEnum.PLAINTIFF.getLabel().equals(tag))
            l_names_plt.add(nameCleaned);
          l_p.add(people);
        } else {
          RelationType rt = RelationType.getRelationType(peopType);
          // 处理一段中有两个人物
          // 如：委托代理人郭XX、陈XX，
          if (peopleMap.containsKey(nameCleaned)) {
            people = peopleMap.get(nameCleaned);
            people.getPtypes().add(pt);
          } else {
            people = new People(nameCleaned, pt, nameType, position);
          }
          // 添加律师信息，将委托代理人和辩护人设为律师
          if ((ParaLabelEnum.ATTORNEY.getLabel().equals(tag))
              || (ParaLabelEnum.ENTRUSTED.getLabel().equals(tag))) {
            setLawFirm(people, paragraph);
          }
          if (!peopleMap.containsKey(nameCleaned))
            l_p.add(people);
          // 当事人关系
          parsePrivity(peopleMap, paragraph, people, lr, rt, i, l_p);
        }
      }
    }
    Map<String, Object> extractInfo = context.rabbitInfo.getExtractInfo();
    // 对人物进行去重
    for (int i = 0; i < l_p.size() - 1; i++) {
      People pl = l_p.get(i);
      if (pl.getPname() == null) {
        l_p.remove(i);
        i--;
      } else {
        for (int j = i + 1; j < l_p.size(); j++) {
          People pep = l_p.get(j);
          if (pep.getPname() == null) {
            l_p.remove(j);
            j--;
          }
          if (pl.getPname().equals(pep.getPname()) && pl.getPtype().equals(pep.getPtype())
              && (j - i == 1)) {
            l_p.remove(j);
            j--;
          }
        }
      }
    }
    if (!l_names_def.isEmpty()) {
      extractInfo.put(InfoPointKey.meta_defendant_names[InfoPointKey.mode],
          TextUtils.deduplicate(l_names_def));
    }
    if (!l_names_plt.isEmpty()) {
      extractInfo.put(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode],
          TextUtils.deduplicate(l_names_plt));
    }
    if (!l_p.isEmpty()) {
      extractInfo.put(InfoPointKey.meta_people_attr[InfoPointKey.mode], l_p);
    }
    if (!lr.isEmpty()) {
      extractInfo.put(InfoPointKey.meta_people_relation[InfoPointKey.mode], lr);
    }
    // 加入名字到实体的映射
    Map<String, People> name2People = new HashMap<>();
    for (People people : l_p) {
      name2People.put(people.getPname(), people);
    }
    if (name2People.size() > 0) {
      extractInfo.put(InfoPointKey.meta_people_name2obj[InfoPointKey.mode], name2People);
    }
    // 人物基本信息
    setPeopleAttrInfoCourt(l_p);
    // 加入法庭相关工作人员
    setCourtPersonnelInfo();
    // 加入律所信息
    setLawFirmInfo();
    if (extractInfo.containsKey("meta_案件类型")
        && ("刑事".equals(extractInfo.get("meta_案件类型").toString())
            || "刑事附带民事".equals(extractInfo.get("meta_案件类型").toString()))) {
      // 抽取羁押拘留时间
      parse_pretrial_detention();
      // 抽取第一次犯罪信息
      parse_first_crime_info();
    }
    // 抽取前科信息
    CriminalRecordExtractor cre = new CriminalRecordExtractor(this.context);
    cre.extract();
  }

  /**
   * 检察院人物信息抽取
   *
   * @param
   * @return
   * @author yu.yao
   */
  private void extract1(List<People> l_p, List<String> l_names_supect) {
    // 抽取检察院犯罪嫌疑人姓名、角色
    for (int i = 0; i < context.getAllUnits().size(); i++) {
      String tag = context.docInfo.getParaLabels().getParagraphLabel(i + 1).label;
      String paragraph = context.getAllUnits().get(i);
      // 判断段落标签，若不在指定标签集合则不作人物解析
      if (!ParaLabelEnum.SUSPECT_BASE_INFO.getLabel().equals(tag)) {
        continue;
      }
      // 获取前两句内容，后续抽取人物属性信息时使用
      String[] sents = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      if (sents[0].contains("影响羁押")) {
        continue;
      }
      Controller controller = makeControllerProtor(paragraph);
      if (controller.sourceSents.length == 0) {
        continue;
      }
      // 根据每个段落的第1个短句，得到People
      Matcher matcher =
          Pattern.compile("犯罪嫌疑人(及[\\u4e00-\\u9fa5]*)?(的)?基本情况").matcher(controller.sourceSents[0]);
      if (matcher.find()) {
        continue;
      }
      People people = getPeople(controller.sourceSents[0], i);
      if (people != null) {
        if (!l_names_supect.contains(people.getPname())) {
          l_names_supect.add(people.getPname());
          l_p.add(people);
        } else {
          for (People p : l_p) {
            if (p.getPname().equals(people.getPname())) {
              people = p;
              break;
            }
          }
        }
        CriminalRecordExtractor2.extractCriminalRecord(paragraph, courtName, people);
        parse_pretrial_detention(paragraph, people);
      } else {
        if (l_p.size() > 0) {
          people = l_p.get(l_p.size() - 1);
          CriminalRecordExtractor2.extractCriminalRecord(paragraph, courtName, people);
          parse_pretrial_detention(paragraph, people);
        }
      }

    }
    if (!l_names_supect.isEmpty()) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_suspect[InfoPointKey.mode],
          TextUtils.deduplicate(l_names_supect));
    }
    if (!l_p.isEmpty()) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_people_attr[InfoPointKey.mode],
          l_p);
    }
    // 加入名字到实体的映射
    Map<String, People> name2People = new HashMap<>();
    for (People people : l_p) {
      name2People.put(people.getPname(), people);
    }
    if (!name2People.isEmpty()) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_people_name2obj[InfoPointKey.mode],
          name2People);
    }
    // 人物基本信息
    setPeopleAttrInfoCourt(l_p);
  }

  // 当事人关系
  // 为了处理直接说明人物姓名的当事人
  // 如：被告蒋有智、蒋仕良、李凯标、李嘉美、李小友、曾华安的委托代理人刘胜
  // 获取上方所有人物的姓名，在当前句中寻找，若找到，认为有关系
  private void parsePrivity(Map<String, People> peopleMap, String paragraph, People people,
      List<Relation> lr, RelationType rt, int i, List<People> l_p) {
    int k = 0;
    for (String name : peopleMap.keySet()) {
      if ((paragraph.contains(name)) && (!name.equals(people.getPname()))) {
        lr.add(new Relation(name, people.getPname(), rt));
        k++;
      }
    }
    if (k == 0) {
      // 抽取当前句子中数字，向上寻找
      int number = parseNumberRelated(i);
      // 数字为1时，向上找原告、被告、第三人
      if (number == 1) {
        for (int j = l_p.size() - 1; j >= 0; j--) {
          PeopleType peopleType = l_p.get(j).getPtype();
          if (PeopleType.DEFENDANT.equals(peopleType) || (PeopleType.PLAINTIFF.equals(peopleType))
              || (PeopleType.THIRD_PERSON.equals(peopleType))) {
            lr.add(new Relation(l_p.get(j).getPname(), people.getPname(), rt));
            break;
          }
        }
      } else {
        // 数字大于1，直接向上寻找人物
        for (int n = l_p.size() - 1; n >= 0 && n >= l_p.size() - number; n--) {
          lr.add(new Relation(l_p.get(n).getPname(), people.getPname(), rt));
        }
      }
    }
  }

  // 抽取当前句子中数字
  private int parseNumberRelated(int paragraphNum) {
    // 得到段落的第一个句子。
    String thisLine = context.getAllUnits().get(paragraphNum);
    String[] temp = thisLine.split("[。；;]");
    if (temp.length > 0) {
      thisLine = temp[0];
    } else {
      thisLine = "";
    }
    List<WrapNumberFormat> lwnf = numberRecognizer.getNumbers(thisLine, true);
    if (lwnf.size() > 0) {
      return (int) lwnf.get(0).getArabicNumber();
    }
    return 1;
  }

  // 当事人地位
  private static void setRoles(People people) {
    @SuppressWarnings("unchecked")
    List<String> roles = (List<String>) people.getPosition().getInfo();
    if (!roles.isEmpty()) {
      people.getPeopleAttrMap().put(InfoPointKey.info_litigant_position[InfoPointKey.mode],
          roles.get(0));
      people.getPeopleAttrMap().put(InfoPointKey.info_all_litigant_positions[InfoPointKey.mode],
          roles);
    }
  }

  // 检察院角色
  private void setProcuratorateInfo(List<People> l_p, String nameCleaned, Position position) {
    String meta_case_type = (String) (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_case_type[InfoPointKey.mode]));
    if (CaseType.CRIMINAL_CASE.equals(meta_case_type)
        || CaseType.CRIMINAL_CIVIL_CASE.equals(meta_case_type)) {
      if (position.getValue().contains("检察院")) {
        People people = new People(nameCleaned, PeopleType.PROCURATORATE, 2, position);
        setRoles(people);
        l_p.add(people);
      }
    }
  }

  /**
   * 获取检察院人物信息
   *
   * @param shortSent 段落文本
   */
  private People getPeople(String shortSent, int para_num) {
    Position position = recognizeName(shortSent, para_num);
    if (position == null)
      return null;
    NameWrapper nc = NameHandler.getNameType(position.getValue());
    int nameType = nc.getNameType();
    String nameCleaned = nc.getNameCleaned();
    People people = new People(nameCleaned, PeopleType.SUSPECT, nameType, position);
    return people;
  }

  // 识别人名
  private Position recognizeName(String shortSent, int para_num) {
    Position p = null;
    int lastPos = 0;
    shortSent = shortSent + ",";
    if (shortSent.contains("被告人") || shortSent.contains("嫌疑人")) {
      Pattern pattern = Pattern.compile("被告人|(犯罪)?嫌疑人");
      NamedEntity[] nes = NamedEntityRecognizer.recognizeEntityByRegex(shortSent, pattern);
      if (nes.length == 0)
        return null;
      lastPos = nes[0].getOffset() + nes[0].getSource().length();
    }
    String sent = shortSent.substring(lastPos).replaceAll("^[:：]", "");
    Matcher matcher = PATTERN_CRIMINAL_SUSPECT.matcher(sent);
    if (matcher.find()) {
      String name = matcher.group(1);
      if (name.contains("·")) {
        p = new Position(name, PeopleType.SUSPECT.toString(), para_num, matcher.start());
      } else if (name.length() <= 4 && name.length() > 1) {
        if (shortSent.contains("被告人") || shortSent.contains("嫌疑人")) {
          p = new Position(name, PeopleType.SUSPECT.toString(), para_num, matcher.start());
        } else {
          List<org.ansj.domain.Term> terms = NlpAnalysis.parse(name).getTerms();
          if ((terms.size() == 1) && (!terms.get(0).getNatureStr().equals("nr"))
              && (!terms.get(0).getNatureStr().equals("nw"))) {
            return p;
          } else {
            p = new Position(name, PeopleType.SUSPECT.toString(), para_num, matcher.start());
          }
        }
      } else {
        List<org.ansj.domain.Term> terms = NlpAnalysis.parse(name).getTerms();
        if (terms.get(0).getNatureStr().equals("nr")) {
          if (terms.get(0).getName().length() <= 4 && terms.get(0).getName().length() > 1) {
            p = new Position(terms.get(0).getName(), PeopleType.SUSPECT.toString(), para_num,
                matcher.start());
          }
        }
        if (p == null) {
          for (int i = 0; i < terms.size(); i++) {
            if ((terms.get(i).getNatureStr().equals("r"))
                && (terms.get(i).getName().equals("某某"))) {
              if ((i > 0) && (terms.get(i - 1).getName().length() == 1)) {
                p = new Position(terms.get(i - 1).getName() + terms.get(i).getName(),
                    PeopleType.SUSPECT.toString(), para_num, matcher.start());
              }
            }
          }
        }
      }
    }
    return p;
  }

  /**
   * 在特定的段落里寻找律所信息 律师信息 用分词算法的话，会出现分词不准的情况。 将委托代理人和辩护人设为律师
   */
  private void setLawFirm(People people, String paragraph) {
    Matcher matcher = PATTERN_LAW_FIRM[0].matcher(paragraph);
    String sentence = null;
    Pattern pattern = Pattern.compile("[,，。;；：:]");
    Matcher match;
    if (matcher.find()) {
      String company = matcher.group(1);
      match = pattern.matcher(company);
      if (!match.find() && company.length() < 20) {
        people.peopleAttrMap.put(InfoPointKey.info_company[InfoPointKey.mode], company);
        sentence = paragraph;
      }
    } else {
      matcher = PATTERN_LAW_FIRM[1].matcher(paragraph);
      if (matcher.find()) {
        String company = matcher.group(1);
        match = pattern.matcher(company);
        if (!match.find() && company.length() < 20) {
          people.peopleAttrMap.put(InfoPointKey.info_company[InfoPointKey.mode], company);
          sentence = paragraph;
        }
      }
    }
    if (!TextUtils.isEmpty(sentence)) {
      matcher = PATTERN_LAW_JOB.matcher(sentence);
      if (matcher.find()) {
        people.peopleAttrMap.put(InfoPointKey.info_occupation[InfoPointKey.mode], matcher.group());
      }
    }
    if ((matcher.find()) && (people.getPtype().equals(PeopleType.ENTRUSTED))) {
      people.setPtype(PeopleType.ATTORNEY);
      people.getPtypes().add(PeopleType.ATTORNEY);
    }
  }

  // 加入法庭相关工作人员
  @SuppressWarnings("unchecked")
  private void setCourtPersonnelInfo() {
    for (String tag : AbstractExtractor.judgePeopleList) {
      tag = tag.toLowerCase();
      List<String> labels = new ArrayList<String>();
      labels.add(tag);
      List<Map<Integer, String>> mapList =
          context.docInfo.getParaLabels().getContentByLabels(labels);
      for (Map<Integer, String> map : mapList) {
        for (int i : map.keySet()) {
          String paragraph = map.get(i);
          // 删掉括号；
          paragraph = Pattern.compile("(?:[\\(（](.*?)[\\)）])").matcher(paragraph).replaceAll("");
          // 删掉助理，代理；
          paragraph = paragraph.replaceAll("助理|代理", "");
          // 删掉非中文字符
          paragraph = paragraph.replaceAll("[^\u4e00-\u9fa5]+", "");
          Matcher matcher = null;
          switch (tag) {
            case "chief_judge":
              matcher = PATTERN_CHIEF_JUDGE.matcher(paragraph);
              break;
            case "judges":
              matcher = PATTERN_JUDGE.matcher(paragraph);
              break;
            case "clerk":
              matcher = PATTERN_CLERK.matcher(paragraph);
              break;
            case "judge_assessor":
              matcher = PATTERN_JUDGE_ASSESSOR.matcher(paragraph);
              break;
          }
          if ((matcher != null) && (matcher.find())) {
            String name = matcher.group(1);
            if ((name.contains("审判")) || (name.contains("陪审员")) || (name.length() > 4)) {
              List<Term> termList = StandardTokenizer.segment(paragraph);
              for (Term term : termList) {
                if (term.nature.equals(Nature.nr)) {
                  name = term.word;
                  break;
                }
              }
            }
            switch (tag) {
              case "chief_judge":
                context.rabbitInfo.getExtractInfo()
                    .put(InfoPointKey.meta_chief_judge[InfoPointKey.mode], name);
                break;
              case "judges":
                if (context.rabbitInfo.getExtractInfo()
                    .get(InfoPointKey.meta_judges[InfoPointKey.mode]) == null) {
                  context.rabbitInfo.getExtractInfo()
                      .put(InfoPointKey.meta_judges[InfoPointKey.mode], new ArrayList<String>());
                }
                ((List<String>) context.rabbitInfo.getExtractInfo()
                    .get(InfoPointKey.meta_judges[InfoPointKey.mode])).add(name);
                break;
              case "clerk":
                context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_clerk[InfoPointKey.mode],
                    name);
                break;
            }
          }

        }
      }
    }
  }

  // 加入律所信息
  @SuppressWarnings("unchecked")
  private void setLawFirmInfo() {
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]) == null) {
      return;
    }
    List<People> peoples = (List<People>) context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]);
    // 律师 律所
    Set<String> lawNameSet = new HashSet<String>();
    Set<String> lawFirmSet = new HashSet<>();
    for (People people : peoples) {
      if (people.getPtype() == PeopleType.ATTORNEY) {
        if (!TextUtils.isEmpty(people.getPname()) && !TextUtils.isEmpty(
            (String) people.getPeopleAttrMap().get(InfoPointKey.info_company[InfoPointKey.mode]))) {
          lawNameSet.add(people.getPname());
          String firm = people.getPeopleAttrMap().get(InfoPointKey.info_company[InfoPointKey.mode])
              .toString();
          if (firm.endsWith("律师事务所") || firm.endsWith("律所"))
            lawFirmSet.add(firm);
        }
      }
    }
    if (lawNameSet.size() > 0) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_law_name[InfoPointKey.mode],
          lawNameSet);
    }
    if (lawFirmSet.size() > 0) {
      context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_law_firm[InfoPointKey.mode],
          lawFirmSet);
    }
  }

  // 检查院的
  private Controller makeControllerProtor(String paragraphStr) {
    // 对前两句按照最小的标点符号分割
    String[] shortSents = DocumentUtils.splitSentenceByCommaSemicolon(paragraphStr);
    PeopleInfoExtractor.Controller controller = new PeopleInfoExtractor.Controller(shortSents);

    return controller;
  }

  // 法院的
  private Controller makeCourtController(String paragraphStr) {
    String[] sentence = DocumentUtils.splitOneParagraphByPeriod(paragraphStr);
    // 如果超过2句，则只取前两句
    String s = "";
    if (sentence.length == 1)
      s = sentence[0];
    else if (sentence.length > 1) {
      s = sentence[0];
      if (!checkPattern(sentence[1], PATTERN_POS_BOUNDARY_WORDS)) {
        s += "。" + sentence[1];
      }
    }
    // 对前两句按照最小的标点符号分割
    String[] shortSents = DocumentUtils.splitSentenceByCommaSemicolon(s);
    return new Controller(shortSents);
  }

  // 抽取人物基本信息
  private void setPeopleAttrInfoCourt(List<People> l_p) {
    if (l_p.size() >= 4) {
      Context context = this.context;
      CountDownLatch latch = new CountDownLatch(l_p.size());
      for (People people : l_p) {
        new Runnable() {
          @Override
          public void run() {
            NamedEntityRecognizer ner = new NamedEntityRecognizer(context);
            LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
            ParaContext paraContext = new ParaContext();
            Controller controller;
            if (PeopleType.SUSPECT.equals(people.getPtype())) {
              controller =
                  makeControllerProtor(context.getAllUnits().get(people.getPosition().getPara()));
            } else if (PeopleType.DEFENDANT.equals(people.getPtype())
                || PeopleType.PLAINTIFF.equals(people.getPtype())
                || PeopleType.THIRD_PERSON.equals(people.getPtype())) {
              controller =
                  makeCourtController(context.getAllUnits().get(people.getPosition().getPara()));
            } else {
              latch.countDown();
              return;
            }
            Map<String, Object> peopleAttrMap = people.getPeopleAttrMap();
            if (2 == people.getPnameType()) {
              parseOrgID(controller, peopleAttrMap);
              if (type.equals("court")) {
                paraContext.current = people;
                parseAddrResidence(ner, lr, paraContext, controller, peopleAttrMap);
              } else {
                parseAddrResidence(controller, peopleAttrMap);
              }
            } else {
              parseIDCard(controller, peopleAttrMap);
              parseGender(controller, peopleAttrMap);
              parseDoBAndAddroB(ner, controller, peopleAttrMap);
              parseAddrRegister(ner, controller, peopleAttrMap);
              parseAddrFrom(ner, controller, peopleAttrMap);
              if (type.equals("court")) {
                paraContext.current = people;
                parseAddrResidence(ner, lr, paraContext, controller, peopleAttrMap);
              } else {
                parseAddrResidence(controller, peopleAttrMap);
              }
              parseAge(controller, peopleAttrMap);
              parseEthnicity(controller, peopleAttrMap);
              parseEducationLevel(controller, peopleAttrMap);
              parsePartisanship(controller, peopleAttrMap);
              parseOccupationAndCompany(controller, peopleAttrMap);
              parseMentalDisorder(controller, peopleAttrMap);
              parseBlind(controller, peopleAttrMap);
              parseDeafMute(controller, peopleAttrMap);
            }
            latch.countDown();
          }
        }.run();
      }
      try {
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } else {
      for (People people : l_p) {
        NamedEntityRecognizer ner = new NamedEntityRecognizer(context);
        LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
        ParaContext paraContext = new ParaContext();
        Controller controller;
        if (PeopleType.SUSPECT.equals(people.getPtype())) {
          controller =
              makeControllerProtor(context.getAllUnits().get(people.getPosition().getPara()));
        } else if (PeopleType.DEFENDANT.equals(people.getPtype())
            || PeopleType.PLAINTIFF.equals(people.getPtype())
            || PeopleType.THIRD_PERSON.equals(people.getPtype())) {
          controller =
              makeCourtController(context.getAllUnits().get(people.getPosition().getPara()));
        } else {
          continue;
        }
        Map<String, Object> peopleAttrMap = people.getPeopleAttrMap();
        if (2 == people.getPnameType()) {
          parseOrgID(controller, peopleAttrMap);
          if (type.equals("court")) {
            paraContext.current = people;
            parseAddrResidence(ner, lr, paraContext, controller, peopleAttrMap);
          } else {
            parseAddrResidence(controller, peopleAttrMap);
          }
        } else {
          parseIDCard(controller, peopleAttrMap);
          parseGender(controller, peopleAttrMap);
          parseDoBAndAddroB(ner, controller, peopleAttrMap);
          parseAddrRegister(ner, controller, peopleAttrMap);
          parseAddrFrom(ner, controller, peopleAttrMap);
          if (type.equals("court")) {
            paraContext.current = people;
            parseAddrResidence(ner, lr, paraContext, controller, peopleAttrMap);
          } else {
            parseAddrResidence(controller, peopleAttrMap);
          }
          parseAge(controller, peopleAttrMap);
          parseEthnicity(controller, peopleAttrMap);
          parseEducationLevel(controller, peopleAttrMap);
          parsePartisanship(controller, peopleAttrMap);
          parseOccupationAndCompany(controller, peopleAttrMap);
          parseMentalDisorder(controller, peopleAttrMap);
          parseBlind(controller, peopleAttrMap);
          parseDeafMute(controller, peopleAttrMap);
        }
      }
    }
  }

  private boolean checkPattern(String s, Pattern[] PATTERNS) {
    for (Pattern p : PATTERNS) {
      if (p.matcher(s).find())
        return true;
    }
    return false;
  }

  /**
   * 出生日期和出生地点，出生日期可以根据身份证推测
   */
  private void parseDoBAndAddroB(NamedEntityRecognizer ner, Controller controller,
      Map<String, Object> peopleAttrMap) {
    boolean flagDOBFound = false, flagPlaceFound = false;
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      java.util.regex.Matcher matcher = PATTERNS_POS_DOB[0].matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        boolean flagDOB = false, flagPlace = false;
        NamedEntity[] nes = NamedEntityRecognizer.recognizeTime(controller.sourceSents[i]);
        if (nes.length != 0) {
          String DoB = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
          if (null != DoB) {
            peopleAttrMap.put(InfoPointKey.info_birthday[InfoPointKey.mode], DoB);
            flagDOB = true;
            flagDOBFound = true;
          }
        }
        // 出生地址可能和出生年月在同一个地方
        NamedEntity ne = ner.recognizeAddress(controller.sourceSents[i]);
        if (null != ne) {
          if (ne.getOffset() > matcher.start()) {
            peopleAttrMap.put(InfoPointKey.info_dob_addr[InfoPointKey.mode],
                controller.sourceSents[i].substring(ne.getOffset()));
            peopleAttrMap.put(InfoPointKey.info_dob_addr_category[InfoPointKey.mode],
                (String[]) (ne.getInfo())); // 值需要转成String[]吗?
            flagPlace = true;
            flagPlaceFound = true;
          }
        }
        if (flagDOB || flagPlace)
          it.remove();
        if (flagDOBFound && flagPlaceFound)
          return;
      }
    }
    // 利用身份证日期补全
    if (peopleAttrMap.containsKey(InfoPointKey.info_id_card[InfoPointKey.mode])) {
      String DoB = IDCardJudge
          .getBirthday((String) peopleAttrMap.get(InfoPointKey.info_id_card[InfoPointKey.mode]));
      if (null != DoB) {
        peopleAttrMap.put(InfoPointKey.info_birthday[InfoPointKey.mode], DoB);
      }
    }
  }

  /**
   * 户籍地址：什么地方的人：安徽淮南人
   */
  private void parseAddrFrom(NamedEntityRecognizer ner, Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      java.util.regex.Matcher matcher2 = PATTERNS_POS_DOB[1].matcher(controller.sourceSents[i]);
      if (matcher2.find()) {
        String addr =
            controller.sourceSents[i].substring(0, controller.sourceSents[i].length() - 1);
        NamedEntity ne = ner.recognizeAddress(addr);
        if (null != ne && ne.getOffset() <= 2) {
          if (!peopleAttrMap.containsKey(InfoPointKey.info_cr_addr[InfoPointKey.mode])) {
            peopleAttrMap.put(InfoPointKey.info_cr_addr[InfoPointKey.mode],
                addr.substring(ne.getOffset()));
            peopleAttrMap.put(InfoPointKey.info_cr_addr_category[InfoPointKey.mode],
                (String[]) (ne.getInfo()));
          }
          it.remove();
          return;
        }
      }
    }
  }

  /**
   * 民族
   */
  private void parseEthnicity(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Matcher matcher = PATTERNS_POS_EGROUP[0].matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        String ethnicGroup = matcher.group();
        if (ethnicGroupMapper.containsKey(ethnicGroup)) {
          ethnicGroup = ethnicGroupMapper.get(ethnicGroup);
          peopleAttrMap.put(InfoPointKey.info_ethnicity[InfoPointKey.mode], ethnicGroup);
          it.remove();
          return;
        }
        break;
      }
    }
  }

  private void parseGender(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Matcher matcher = PATTERNS_POS_GENDER[0].matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        String gender = matcher.group(1);
        peopleAttrMap.put(InfoPointKey.info_gender[InfoPointKey.mode], gender);
        it.remove();
        return;
      }
    }
    // 利用身份证日期补全
    if (peopleAttrMap.containsKey(InfoPointKey.info_id_card[InfoPointKey.mode])) {
      String gender = IDCardJudge
          .getSex((String) peopleAttrMap.get(InfoPointKey.info_id_card[InfoPointKey.mode]));
      if ("".equals(gender)) {
        peopleAttrMap.get(InfoPointKey.info_gender[InfoPointKey.mode]);
      }
    }
  }

  /**
   * 法院居住地址
   */
  @SuppressWarnings("unchecked")
  private void parseAddrResidence(NamedEntityRecognizer ner, LitigantRecognizer lr,
      ParaContext paraContext, Controller controller, Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      java.util.regex.Matcher matcher = PATTERN_POS_ADDR[0].matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        NamedEntity ne = ner.recognizeAddress(controller.sourceSents[i]);
        if (null != ne) {
          peopleAttrMap.put(InfoPointKey.info_residence_addr[InfoPointKey.mode],
              controller.sourceSents[i].substring(ne.getOffset()));
          peopleAttrMap.put(InfoPointKey.info_residence_addr_category[InfoPointKey.mode],
              (String[]) ne.getInfo());
          it.remove();
          paraContext.recentLitigant = paraContext.current;
          return;
        }
      }
      if (!peopleAttrMap.containsKey(InfoPointKey.info_residence_addr[InfoPointKey.mode])) {
        java.util.regex.Matcher matcher2 = PATTERN_POS_ADDR[1].matcher(controller.sourceSents[i]);
        if (matcher2.find()) {
          // 根据paraContext里面的信息赋值
          People people = getReferenceSituation(lr, matcher2.group(1), paraContext,
              (Map<String, People>) context.rabbitInfo.getExtractInfo()
                  .get(InfoPointKey.meta_people_name2obj[InfoPointKey.mode]));
          if (null != people && people.getPeopleAttrMap()
              .containsKey(InfoPointKey.info_residence_addr[InfoPointKey.mode])) {
            peopleAttrMap.put(InfoPointKey.info_residence_addr[InfoPointKey.mode],
                people.getPeopleAttrMap().get(InfoPointKey.info_residence_addr[InfoPointKey.mode]));
            if (people.getPeopleAttrMap()
                .containsKey(InfoPointKey.info_residence_addr_category[InfoPointKey.mode])) {
              peopleAttrMap.put(InfoPointKey.info_residence_addr_category[InfoPointKey.mode],
                  people.getPeopleAttrMap()
                      .get(InfoPointKey.info_residence_addr_category[InfoPointKey.mode]));
            }
          }
          it.remove();
          return;
        } else if (!peopleAttrMap.containsKey(InfoPointKey.info_residence_addr[InfoPointKey.mode])
            && PATTERN_POS_ADDR[2].matcher(controller.sourceSents[i]).find()) {
          peopleAttrMap.put(InfoPointKey.info_residence_addr[InfoPointKey.mode], NO_FIXED_ADDR);
          it.remove();
          return;
        }
      }
    }
  }

  private People getReferenceSituation(LitigantRecognizer lr, String str, ParaContext paraContext,
      Map<String, People> name2Obj) {
    if (null == name2Obj || str.length() == 0 || null == paraContext.recentLitigant)
      return null;
    if (str.charAt(0) == '上') {
      return paraContext.recentLitigant;
    } else {
      // 临时调试才注释掉，需要看明白并改好
      NamedEntity[] nes = lr.recognize(str);
      if (nes.length == 0)
        return null;
      LitigantUnit lu = (LitigantUnit) nes[0].getInfo();
      if (LitigantUnit.LABEL_LITIGANT.equals(lu.getLabel())) {
        return paraContext.recentLitigant;
      } else if (LitigantUnit.LABEL_DEFENDANT.equals(lu.getLabel())
          || LitigantUnit.LABEL_PLAINTIFF.equals(lu.getLabel())) {
        String[] names = lu.getNames();
        for (String name : names) {
          if (name2Obj.get(name).getPeopleAttrMap()
              .containsKey(InfoPointKey.info_residence_addr[InfoPointKey.mode])) {
            return name2Obj.get(name);
          }
        }
        return null;
      }
      return null;
    }
  }

  /**
   * 居住地址
   */
  private void parseAddrResidence(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Matcher matcher = PATTERNS_POS_ADDR[0].matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        String matchValue = matcher.group(1);
        if (TextUtils.isEmpty(matchValue)) {
          continue;
        }
        if (matchValue.length() < 3) {
          matcher = PATTERNS_POS_ADDR[1].matcher(controller.sourceSents[i]);
          if (matcher.find()) {
            peopleAttrMap.put(InfoPointKey.info_residence_addr[InfoPointKey.mode], NO_FIXED_ADDR);
            it.remove();
            return;
          }
        } else {
          peopleAttrMap.put(InfoPointKey.info_residence_addr[InfoPointKey.mode], matchValue);
          it.remove();
          return;
        }
      }

      if (!peopleAttrMap.containsKey(InfoPointKey.info_residence_addr[InfoPointKey.mode])) {
        Matcher matcher2 = PATTERNS_POS_ADDR[1].matcher(controller.sourceSents[i]);
        if (matcher2.find()) {
          peopleAttrMap.put(InfoPointKey.info_residence_addr[InfoPointKey.mode], NO_FIXED_ADDR);
          it.remove();
          return;
        }
      }
    }
  }

  // 户籍地址
  private void parseAddrRegister(NamedEntityRecognizer ner, Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      java.util.regex.Matcher matcher = PATTERNS_POS_REGISTER[0].matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        String sent = controller.sourceSents[i].substring(matcher.start()).replaceAll("[(（）)]", "");
        NamedEntity ne = ner.recognizeAddress(sent);
        if (null != ne) {
          peopleAttrMap.put(InfoPointKey.info_cr_addr[InfoPointKey.mode],
              sent.substring(ne.getOffset()));
          peopleAttrMap.put(InfoPointKey.info_cr_addr_category[InfoPointKey.mode],
              (String[]) (ne.getInfo()));
          it.remove();
          return;
        }
      }
    }
  }

  /**
   * 教育水平，已经标准化
   */
  private void parseEducationLevel(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      if (controller.sourceSents[i].length() >= 7)
        continue;
      Matcher matcher = patternEducation.matcher(controller.sourceSents[i]);
      if (matcher.find() && controller.sourceSents[i].length() - matcher.end() <= 4) {
        String education = matcher.group();
        if (educationMapper.containsKey(education)) {
          education = educationMapper.get(education);
          peopleAttrMap.put(InfoPointKey.info_educational_status[InfoPointKey.mode], education);
          it.remove();
          return;
        }
        break;
      }
    }
  }

  /**
   * 政治党派
   */
  private void parsePartisanship(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      for (Pattern p : PATTERN_PARTISAN) {
        Matcher matcher = p.matcher(controller.sourceSents[i]);
        if (matcher.find()) {
          peopleAttrMap.put(InfoPointKey.info_political_party[InfoPointKey.mode], "党员");
          it.remove();
          return;
        }
      }
    }
  }


  /**
   * 根据剩余的句子，先判断是不是职业相关，然后调用职业分类模块对职业分类，同时转换成通达海的职业分类
   */
  private void parseOccupationAndCompany(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    List<Integer> lint = new ArrayList<>();
    for (int i : controller.notVisited) {
      if (i <= 2)
        continue;
      if (checkPattern(controller.sourceSents[i], PATTERNS_POS_BOUNDARY_WORDS)) {
        break;
      }
      lint.add(i);
    }
    if (lint.size() != 0) {
      boolean occupationSet = false;
      List<String> posssibleCandidates = new ArrayList<>();
      for (int i : lint) {
        if (checkIfProfessionRelated(controller.sourceSents[i])) {
          ProfessionLevel pl = ProfessionContainer.getProfessionLevel(controller.sourceSents[i]);
          if (!occupationSet && !ProfessionLevel.otherLevel.name.equals(pl.name)) {
            // 找到一个正确的分类
            peopleAttrMap.put(InfoPointKey.info_occupation_category[InfoPointKey.mode], pl.name);
            peopleAttrMap.put(InfoPointKey.info_occupation[InfoPointKey.mode],
                controller.sourceSents[i]);
            //
            ProfessionLevel pl_tdh = ProfessionContainer.convertClassification(pl);
            peopleAttrMap.put(InfoPointKey.info_occupation_category_tdh[InfoPointKey.mode],
                pl_tdh.name);
            occupationSet = true;
          }
          posssibleCandidates.add(controller.sourceSents[i]);
          // break;
        }
      }
      if (lint.size() == 1
          && !peopleAttrMap.containsKey(InfoPointKey.info_occupation[InfoPointKey.mode])) {
        peopleAttrMap.put(InfoPointKey.info_occupation[InfoPointKey.mode],
            controller.sourceSents[lint.get(0)]);
      }

      // 从筛选过的句子里，筛选一个最有可能表示职业的句子
      if (posssibleCandidates.size() > 0) {
        String selectedSent = getMostRelatedSentOfOccupationRelated(posssibleCandidates);

        // 对句子进行分类
        String institution = InstitutionClassifier.classifyInstitution(selectedSent);
        if (null != institution) {
          peopleAttrMap.put(InfoPointKey.info_gov_ins_category[InfoPointKey.mode], institution);
          if (!peopleAttrMap.containsKey(InfoPointKey.info_occupation[InfoPointKey.mode])) {
            peopleAttrMap.put(InfoPointKey.info_occupation[InfoPointKey.mode], selectedSent);
          }
          String adminstrativeRanking =
              InstitutionClassifier.classifyAdministrativeRanking(selectedSent);
          if (null != adminstrativeRanking) {
            peopleAttrMap.put(InfoPointKey.info_administrative_level[InfoPointKey.mode],
                adminstrativeRanking);
          }
        }
      }
    }
  }

  /**
   * 身份证，不合法的身份证丢掉
   */
  private void parseIDCard(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      for (int k = 0; k < 2; k++) {
        Pattern p = PATTERN_ID[k];
        Matcher matcher = p.matcher(controller.sourceSents[i]);
        if (matcher.find()) {
          if (IDCardJudge.isIDCard(matcher.group())) {
            peopleAttrMap.put(InfoPointKey.info_id_card[InfoPointKey.mode], matcher.group());
          }
          it.remove();
          return;
        }
      }
    }
  }

  /**
   * 组织机构代码
   */
  private void parseOrgID(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Pattern p = PATTERN_ID[2];
      Matcher matcher = p.matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        if (matcher.group(1).length() >= 5 && matcher.group(1).matches("\\d")) {
          peopleAttrMap.put(InfoPointKey.info_organization_code[InfoPointKey.mode],
              matcher.group(1));
        }
        it.remove();
        return;
      }
    }
  }

  /**
   * 年龄，可以根据出生年月推测
   */
  private void parseAge(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      for (Pattern p : PATTERNS_POS_AGE) {
        Matcher matcher = p.matcher(controller.sourceSents[i]);
        if (matcher.find()) {
          List<WrapNumberFormat> lwnf = new NumberRecognizer(new String[] {"岁"})
              .getNumbers(new String[] {controller.sourceSents[i]}, true);
          for (WrapNumberFormat wnf : lwnf) {
            if (wnf.getArabicNumber() != 0) {
              int age = (int) wnf.getArabicNumber();
              peopleAttrMap.put(InfoPointKey.info_age[InfoPointKey.mode], age);
              if ((age >= 14) && (age < 18)) {
                peopleAttrMap.put(InfoPointKey.info_yonger[InfoPointKey.mode], true);
                if (age < 16) {
                  peopleAttrMap.put(InfoPointKey.info_14_16[InfoPointKey.mode], true);
                } else {
                  peopleAttrMap.put(InfoPointKey.info_16_18[InfoPointKey.mode], true);
                }
              }
              if ((age >= 65) && (age < 75)) {
                peopleAttrMap.put(InfoPointKey.info_exceed_65[InfoPointKey.mode], true);
              }
              if (age >= 75) {
                peopleAttrMap.put(InfoPointKey.info_older[InfoPointKey.mode], true);
              }
              it.remove();
              return;
            }
          }
        }
      }
    }
    // 利用出生日期补全
    if (peopleAttrMap.containsKey(InfoPointKey.info_birthday[InfoPointKey.mode])
        && context.rabbitInfo.getExtractInfo()
            .containsKey(InfoPointKey.meta_doc_date[InfoPointKey.mode])) {
      DateTime dt1 = DateHandler.makeDateTime((String) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_doc_date[InfoPointKey.mode]));
      DateTime dt2 = DateHandler
          .makeDateTime((String) peopleAttrMap.get(InfoPointKey.info_birthday[InfoPointKey.mode]));
      if (null != dt1 && null != dt2) {
        try {
          int diff = DateHandler.getYearDiff(dt2, dt1);
          if (diff > 0) {
            peopleAttrMap.put(InfoPointKey.info_age[InfoPointKey.mode], diff);
            if ((diff >= 14) && (diff < 18)) {
              peopleAttrMap.put(InfoPointKey.info_yonger[InfoPointKey.mode], true);
              if (diff < 16) {
                peopleAttrMap.put(InfoPointKey.info_14_16[InfoPointKey.mode], true);
              } else {
                peopleAttrMap.put(InfoPointKey.info_16_18[InfoPointKey.mode], true);
              }
            }
            if ((diff >= 65) && (diff < 75)) {
              peopleAttrMap.put(InfoPointKey.info_exceed_65[InfoPointKey.mode], true);
            }
            if (diff >= 75) {
              peopleAttrMap.put(InfoPointKey.info_older[InfoPointKey.mode], true);
            }
          }
        } catch (Exception e) {
        }
      }
    }
  }

  private void parseMentalDisorder(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Matcher matcher = PATTERN_MENTAL_DISORDER.matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        peopleAttrMap.put(InfoPointKey.info_mental_disorder[InfoPointKey.mode], true);
        it.remove();
        return;
      }
    }
  }

  private void parseBlind(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Matcher matcher = PATTERN_BLIND.matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        peopleAttrMap.put(InfoPointKey.info_blind[InfoPointKey.mode], true);
        it.remove();
        return;
      }
    }
  }

  private void parseDeafMute(PeopleInfoExtractor.Controller controller,
      Map<String, Object> peopleAttrMap) {
    Iterator<Integer> it = controller.notVisited.iterator();
    while (it.hasNext()) {
      Integer i = it.next();
      Matcher matcher = PATTERN_DEFF_MUTE.matcher(controller.sourceSents[i]);
      if (matcher.find()) {
        peopleAttrMap.put(InfoPointKey.info_deaf_mute[InfoPointKey.mode], true);
        it.remove();
        return;
      }
    }
  }


  /**
   * 判断句子是否和职业相关
   */
  private boolean checkIfProfessionRelated(String s) {
    if (checkPattern(s, PATTERN_NOT_PROFESSION_RELATED)) {
      return false;
    }
    return true;
  }

  /**
   * 得到和职业最相关的句子
   */
  private String getMostRelatedSentOfOccupationRelated(List<String> candidates) {
    Iterator<String> it = candidates.iterator();
    while (it.hasNext()) {
      String s = it.next();
      if (s.length() >= 5) {
        for (Pattern p : PATTERNS_OCCUPATION_RELATED) {
          if (p.matcher(s).find())
            return s;
        }
      }
    }
    candidates.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o2.length() - o1.length();
      }
    });

    return candidates.get(0);
  }

  /**
   * 先统一定位各个分句的属性（可以有多个属性），然后根据属性匹配
   */
  public static final String NO_FIXED_ADDR = "无固定住所";

  // 出生地址
  public static final Pattern[] PATTERNS_POS_DOB = {
      // 生日，出生地址
      Pattern.compile("生(.*)"), Pattern.compile("([^盲聋哑]*)人$")};
  // 户籍地址
  private static final Pattern[] PATTERNS_POS_REGISTER = {Pattern.compile("户[籍口]")};

  // 居住地址
  private static final Pattern[] PATTERNS_POS_ADDR = {
      // 住所地
      Pattern.compile("^(?:[\u4e00-\u9fa5:：]{0,4})(?:住(?:所地|在|址)?|地址)(.*)"),
      Pattern.compile("(无|没有).*固定.*([住居])"),};
  // 定位
  private static final Pattern[] PATTERN_POS_ADDR = {
      // 住所地
      Pattern.compile("(^[\u4e00-\u9fa5:：]{0,4})(住|地址)"), Pattern.compile("(?:[住地])址?同(.*)"),
      Pattern.compile("(无|没有).*固定.*([住居])"),};
  // 定位
  private static final Pattern[] PATTERNS_POS_EGROUP = {
      // 民族
      Pattern.compile("[\u4e00-\u9fa5]{1,4}族$"),};
  // 定位
  private static final Pattern[] PATTERNS_POS_GENDER = {
      // 性别
      Pattern.compile("([男女])$"),};
  // 定位
  private static final Pattern[] PATTERNS_POS_AGE = {
      // 年龄
      Pattern.compile("岁$"),};

  // 不能出现的词语
  private static final Pattern[] PATTERNS_POS_BOUNDARY_WORDS = {Pattern
      .compile("(^因)|涉嫌|罪|(被[\u4e00-\u9fa5]*(公安局|派出所|检察院|法院))|判处|拘留|收容|教养|管制|曾因|因犯|有期|羁押|取保候审"),};

  // 身份证或者企业代码
  private static final Pattern[] PATTERN_ID = {Pattern.compile("[\\dxX]{18}"),
      Pattern.compile("[\\dxX]{15}"), Pattern.compile("(?:组织|机构|企业)代码[:：]?(.*)")};

  // 政治面貌
  private static final Pattern[] PATTERN_PARTISAN = {Pattern.compile("(中共|共产党)党员")};

  private static final Pattern PATTERN_MENTAL_DISORDER = Pattern.compile("精神病");

  private static final Pattern PATTERN_BLIND = Pattern.compile("盲人");

  private static final Pattern PATTERN_DEFF_MUTE = Pattern.compile("聋哑|又聋又哑");

  // 不是职业相关的句子
  private static final Pattern[] PATTERN_NOT_PROFESSION_RELATED = {Pattern.compile("\\d+年\\+月"),
      Pattern.compile("(^[\u4e00-\u9fa5:：]{0,4})(住|地址)"), Pattern.compile("\\d{14}"),};

  private static final Pattern[] PATTERNS_OCCUPATION_RELATED =
      {Pattern.compile("^[\u4e00-\u9fa5]{0,2}任"), Pattern.compile("主任|(长$)|(代表$)|(负责人$)|(委员$)")};

  private static final Pattern[] SEQUENCE_TIME_INFO = {Pattern.compile("抓获"),
      Pattern.compile("(决定|批准)[^。，,;；]*刑事拘留"), Pattern.compile("(执行|公安局|公安机关|派出所|被)[^。，,;；]*刑事拘留"),
      Pattern.compile("(决定|批准)[^。，,;；]*逮捕"), Pattern.compile("(执行|公安局|公安机关|派出所|[转被])[^。，,;；]*逮捕"),
      Pattern.compile("(检察院|该院)[^。，,;；]*取保候审"), Pattern.compile("(法院|本院)[^。，,;；]*取保候审"),
      Pattern.compile("(公安局|公安机关|派出所)[^。，,;；]*取保候审"), Pattern.compile("监视居住"),
      Pattern.compile("拘传")};

  private static String INFOS =
      "info_capture_date#info_detention_opinion_date#info_detention_action_date#info_arrest_opinion_date"
          + "#info_arrest_action_date#info_bail_procuratorate_date#info_bail_court_date#info_bail_police_date"
          + "#info_residence_monitor_date#info_residence_call_date";

  // 法院
  private PretrialDetention parsePretrialDetentionCourt(String paragraph,
      NamedEntityRecognizer ner) {
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
    int start = -1;
    for (int i = 0; i < sentences.length; i++) {
      String sentence = sentences[i];
      NamedEntity[] nes_begin =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_BEGIN[0]);
      if (nes_begin.length > 0) {
        start = i;
        break;
      }
    }
    if (start != -1) {
      String sent = "";
      for (int i = start; i < sentences.length; i++) {
        sent = sent + sentences[i] + "。";
      }
      Map<String, Object> rsltMap =
          SequenceTimeInfoExtractor.extractTimes(sent, SEQUENCE_TIME_INFO, INFOS);
      if (rsltMap.isEmpty()) {
        return null;
      }
      PretrialDetention pd = new PretrialDetention();
      String[] dates = new String[rsltMap.size()];
      Iterator<Map.Entry<String, Object>> iter = rsltMap.entrySet().iterator();
      int idx = 0;
      while (iter.hasNext()) {
        Map.Entry<String, Object> entry = iter.next();
        dates[idx++] = entry.getValue().toString();
        switch (entry.getKey()) {
          case "info_capture_date":
            pd.setDateCapture(entry.getValue().toString());
            break;
          case "info_detention_opinion_date":
            pd.setDateDetentionOpinion(entry.getValue().toString());
            break;
          case "info_detention_action_date":
            pd.setDateDetentionEnforcement(entry.getValue().toString());
            break;
          case "info_arrest_opinion_date":
            pd.setDateArrestOpinion(entry.getValue().toString());
            break;
          case "info_arrest_action_date":
            pd.setDateArrestEnforcement(entry.getValue().toString());
            break;
          case "info_bail_procuratorate_date":
            pd.setDateBailProcuratorate(entry.getValue().toString());
            break;
          case "info_bail_court_date":
            pd.setDateBailCourt(entry.getValue().toString());
            break;
          case "info_bail_police_date":
            pd.setDateBailPolice(entry.getValue().toString());
            break;
          case "info_residence_monitor_date":
            pd.setDateResidenceMonitor(entry.getValue().toString());
            break;
          case "info_residence_call_date":
            pd.setDateResidenceRecall(entry.getValue().toString());
            break;
        }
      }
      // 取最小的时间
      String minDate = DateHandler.convertDateTimeFormat(DateHandler.min(dates));
      if (null != minDate) {
        pd.setDateFirstCustody(minDate);
      }
      return pd;
    }
    return null;
  }

  // 检察院
  private PretrialDetention parsePretrialDetention(String paragraph, NamedEntityRecognizer ner) {
    String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
    int start = -1;
    for (int i = 0; i < sentences.length; i++) {
      String sentence = sentences[i];
      NamedEntity[] nes_begin =
          NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_BEGIN2);
      if (nes_begin.length > 0) {
        start = i;
        break;
      }
    }
    if (start != -1) { // 此处跟法院不同
      String sent = "";
      String[] shortSents = DocumentUtils.splitSentenceByCommaSemicolon(sentences[start]);
      for (String sentence : shortSents) {
        NamedEntity[] nes_begin =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERNS_BEGIN2);
        if (nes_begin.length > 0) {
          sent += "," + sentence;
        }
      }
      NamedEntity[] dates = NamedEntityRecognizer.recognizeTime(sent);
      if (dates.length < 1) {
        return null;
      }
      PretrialDetention pd = new PretrialDetention();
      // 取最小的时间
      String minDate = DateHandler
          .convertDateTimeFormat(DateHandler.min(DateHandler.convertNamedEntity2String(dates)));
      if (null != minDate) {
        pd.setDateFirstCustody(minDate);
      }
      NamedEntity[] ner_capture =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_CAPTURE);
      NamedEntity[] ner_detention_opinion =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_DETENTION_OPINION);
      NamedEntity[] ner_detention_action =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_DETENTION_ACTION);
      NamedEntity[] ner_arrest_opinion =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_ARREST_OPINION);
      NamedEntity[] ner_arrest_action =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_ARREST_ACTION);
      NamedEntity[] ner_bail_procuratorate =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_BAIL_PROCURATORATE);
      NamedEntity[] ner_bail_court =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_BAIL_COURT);
      NamedEntity[] ner_bail_police =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_BAIL_POLICE);
      NamedEntity[] ner_residence_monitor =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_RESIDENCE_MONITOR);
      NamedEntity[] ner_residence_recall =
          NamedEntityRecognizer.recognizeEntityByRegex(sent, PATTERNS_RESIDENCE_RECALL);
      Integer[] commas = NamedEntityRecognizer.recognizePeriods(sent);
      NamedEntity[] nes_allEntities = NamedEntityRecognizer.combineEntities(
          new String[] {"抓获", "决定刑事拘留", "执行刑事拘留", "决定逮捕", "执行逮捕", "检察院取保", "法院取保", "公安局取保", "监视居住",
              "拘传"},
          ner_capture, ner_detention_opinion, ner_detention_action, ner_arrest_opinion,
          ner_arrest_action, ner_bail_procuratorate, ner_bail_court, ner_bail_police,
          ner_residence_monitor, ner_residence_recall);
      if (nes_allEntities.length < 1)
        return null;
      List<NamedEntity[]> lnes =
          NamedEntityRecognizer.entityMatch(sent, commas, dates, nes_allEntities, true, false);
      boolean setAtleastOneDate = false;
      for (NamedEntity[] nes : lnes) {
        String date = DateHandler.convertDateTimeFormat((String) nes[0].getInfo());
        if (null == date)
          continue;
        setAtleastOneDate = true;
        switch (nes[1].getType()) {
          case "抓获": {
            pd.setDateCapture(date);
            break;
          }
          case "决定刑事拘留": {
            pd.setDateDetentionOpinion(date);
            break;
          }
          case "执行刑事拘留": {
            pd.setDateDetentionEnforcement(date);
            break;
          }
          case "决定逮捕": {
            pd.setDateArrestOpinion(date);
            break;
          }
          case "执行逮捕": {
            pd.setDateArrestEnforcement(date);
            break;
          }
          case "检察院取保": {
            pd.setDateBailProcuratorate(date);
            break;
          }
          case "法院取保": {
            pd.setDateBailCourt(date);
            break;
          }
          case "公安局取保": {
            pd.setDateBailPolice(date);
            break;
          }
          case "监视居住": {
            pd.setDateResidenceMonitor(date);
            break;
          }
          case "拘传": {
            pd.setDateResidenceRecall(date);
            break;
          }
          default: {
            // should never reach here;
          }
        }
      }
      // 决定机关和执行机关
      Matcher matcher_depart = PATTERNS_DEPARTMENT[0].matcher(sent);
      if (matcher_depart.find()) {
        pd.setDepartmentOpinion(matcher_depart.group(1));
        pd.setDepartmentEnforcement(matcher_depart.group(1));
      }
      if (setAtleastOneDate) {
        return pd;
      }
    }
    return null;
  }

  // 法院抽取羁押拘留时间
  private void parse_pretrial_detention() {
    NamedEntityRecognizer ner = new NamedEntityRecognizer(this.context);
    if (!context.rabbitInfo.getExtractInfo()
        .containsKey(InfoPointKey.meta_people_attr[InfoPointKey.mode])) {
      return;
    }
    @SuppressWarnings("unchecked")
    List<People> lp = (List<People>) context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]);
    lp.sort(new Comparator<People>() {
      @Override
      public int compare(People o1, People o2) {
        return o1.getPosition().getPara() - o2.getPosition().getPara();
      }
    });
    for (People people : lp) {
      if (people.getPtype().equals(PeopleType.DEFENDANT)) {
        int paraPos = people.getPosition().getPara();
        String paragraph = context.getAllUnits().get(paraPos);
        PretrialDetention pd = parsePretrialDetentionCourt(paragraph, ner);
        List<String> custodyDate = new ArrayList<>();
        if (null != pd) {
          if (pd.getDateCapture() != null) {
            people.getPeopleAttrMap().put(InfoPointKey.info_capture_date[InfoPointKey.mode],
                pd.getDateCapture());
          }
          if (pd.getDateDetentionOpinion() != null) {
            people.getPeopleAttrMap().put(
                InfoPointKey.info_detention_opinion_date[InfoPointKey.mode],
                pd.getDateDetentionOpinion());
            custodyDate.add(pd.getDateDetentionOpinion());
          }
          if (pd.getDateDetentionEnforcement() != null) {
            people.getPeopleAttrMap().put(
                InfoPointKey.info_detention_action_date[InfoPointKey.mode],
                pd.getDateDetentionEnforcement());
            custodyDate.add(pd.getDateDetentionEnforcement());
          }
          if (pd.getDateArrestOpinion() != null) {
            people.getPeopleAttrMap().put(InfoPointKey.info_arrest_opinion_date[InfoPointKey.mode],
                pd.getDateArrestOpinion());
            custodyDate.add(pd.getDateArrestOpinion());
          }
          if (pd.getDateArrestEnforcement() != null) {
            people.getPeopleAttrMap().put(InfoPointKey.info_arrest_action_date[InfoPointKey.mode],
                pd.getDateArrestEnforcement());
            custodyDate.add(pd.getDateArrestEnforcement());
          }
          if (pd.getDateBailProcuratorate() != null) {
            people.getPeopleAttrMap().put(
                InfoPointKey.info_bail_procuratorate_date[InfoPointKey.mode],
                pd.getDateBailProcuratorate());
          }
          if (pd.getDateBailCourt() != null) {
            people.getPeopleAttrMap().put(InfoPointKey.info_bail_court_date[InfoPointKey.mode],
                pd.getDateBailCourt());
          }
          if (pd.getDateBailPolice() != null) {
            people.getPeopleAttrMap().put(InfoPointKey.info_bail_police_date[InfoPointKey.mode],
                pd.getDateBailPolice());
          }
          if (pd.getDateResidenceMonitor() != null) {
            people.getPeopleAttrMap().put(
                InfoPointKey.info_residence_monitor_date[InfoPointKey.mode],
                pd.getDateResidenceMonitor());
          }
          if (pd.getDateResidenceRecall() != null) {
            people.getPeopleAttrMap().put(InfoPointKey.info_residence_call_date[InfoPointKey.mode],
                pd.getDateResidenceRecall());
          }
          if (!custodyDate.isEmpty()) {
            List<DateTime> list = new ArrayList<>();
            // 遍历所有时间,转为DateTime
            for (String dateStr : custodyDate) {
              list.add(DateHandler.makeDateTime(dateStr));
            }
            // 排序
            Collections.sort(list);
            // 将最小时间设置为逮捕或拘留时间信息点
            people.getPeopleAttrMap().put(InfoPointKey.info_custody_date[InfoPointKey.mode],
                DateHandler.convertDateTimeFormat(list.get(0)));
          }
        }
      }
    }
  }

  // 检察院抽取羁押拘留时间
  private void parse_pretrial_detention(String paragraph, People people) {
    NamedEntityRecognizer ner = new NamedEntityRecognizer(this.context);
    PretrialDetention pd = parsePretrialDetention(paragraph, ner);
    List<String> custodyDate = new ArrayList<>();
    if (null != pd) {
      if (pd.getDateCapture() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_capture_date[InfoPointKey.mode],
            pd.getDateCapture());
      }
      if (pd.getDateDetentionOpinion() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_detention_opinion_date[InfoPointKey.mode],
            pd.getDateDetentionOpinion());
        custodyDate.add(pd.getDateDetentionOpinion());
      }
      if (pd.getDateDetentionEnforcement() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_detention_action_date[InfoPointKey.mode],
            pd.getDateDetentionEnforcement());
        custodyDate.add(pd.getDateDetentionEnforcement());
      }
      if (pd.getDateArrestOpinion() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_arrest_opinion_date[InfoPointKey.mode],
            pd.getDateArrestOpinion());
        custodyDate.add(pd.getDateArrestOpinion());
      }
      if (pd.getDateArrestEnforcement() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_arrest_action_date[InfoPointKey.mode],
            pd.getDateArrestEnforcement());
        custodyDate.add(pd.getDateArrestEnforcement());
      }
      if (pd.getDateBailProcuratorate() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_bail_procuratorate_date[InfoPointKey.mode],
            pd.getDateBailProcuratorate());
      }
      if (pd.getDateBailCourt() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_bail_court_date[InfoPointKey.mode],
            pd.getDateBailCourt());
      }
      if (pd.getDateBailPolice() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_bail_police_date[InfoPointKey.mode],
            pd.getDateBailPolice());
      }
      if (pd.getDateResidenceMonitor() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_residence_monitor_date[InfoPointKey.mode],
            pd.getDateResidenceMonitor());
      }
      if (pd.getDateResidenceRecall() != null) {
        people.getPeopleAttrMap().put(InfoPointKey.info_residence_call_date[InfoPointKey.mode],
            pd.getDateResidenceRecall());
      }
      if (custodyDate.size() > 0) {
        List<DateTime> list = new ArrayList<>();
        // 遍历所有时间,转为DateTime
        for (String dateStr : custodyDate) {
          list.add(DateHandler.makeDateTime(dateStr));
        }
        // 排序
        Collections.sort(list);
        // 将最小时间设置为逮捕或拘留时间信息点
        people.getPeopleAttrMap().put(InfoPointKey.info_custody_date[InfoPointKey.mode],
            DateHandler.convertDateTimeFormat(list.get(0)));
      }
    }
  }

  // 抽取第一次犯罪信息
  private void parse_first_crime_info() {
    if (context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]) == null) {
      return;
    }
    @SuppressWarnings("unchecked")
    List<People> lp = (List<People>) context.rabbitInfo.getExtractInfo()
        .get(InfoPointKey.meta_people_attr[InfoPointKey.mode]);
    lp.sort(new Comparator<People>() {
      @Override
      public int compare(People o1, People o2) {
        return o1.getPosition().getPara() - o2.getPosition().getPara();
      }
    });

    String s = "";
    // 匹配前科的段落的正则
    String regex =
        "曾[^,，\\.。；;]*?((\\d{4}|\\d{2})([\\-\\/\\.])\\d{1,2}\\3\\d{1,2}|\\d{4}年\\d{1,2}月\\d{1,2}日)[^,，\\.。；;]*?(判处)";
    List<Map<Integer, String>> paragraphs =
        context.docInfo.getParaLabels().getContentByLabels(AbstractExtractor.PlaintiffArgsAndFacts);
    for (Map<Integer, String> map : paragraphs) {
      for (int i : map.keySet()) {
        String paragraph = map.get(i);
        // 如果前科正则能够匹配到,就跳过这段
        Matcher matcher = Pattern.compile(regex).matcher(paragraph);
        if (matcher.find()) {
          continue;
        }
        s = s + paragraph + "。";
      }
    }
    DateTime minDate, maxDate = null;
    if (context.rabbitInfo.getExtractInfo()
        .containsKey(InfoPointKey.meta_doc_date[InfoPointKey.mode])) {
      DateTime recordDate = DateHandler.makeDateTime((String) context.rabbitInfo.getExtractInfo()
          .get(InfoPointKey.meta_doc_date[InfoPointKey.mode]));
      if (null != recordDate && recordDate.isAfter(DTMAX1) && recordDate.isBefore(DTMAX)) {
        maxDate = recordDate;
      }
    }
    if (null == maxDate)
      maxDate = DTMAX;
    minDate = maxDate.minusYears(22);
    if (!minDate.isAfter(DTMIN)) {
      minDate = DTMIN;
    }
    // 过滤被害者生日日期
    s = DateHandler.birthdayFilter(s);
    NamedEntity[] nes = NamedEntityRecognizer.recognizeTime(s);
    DateTime[] dts = DateHandler.convertNamedEntity2DateTime(nes, minDate, maxDate);
    DateTime firstCrimeDate = DateHandler.min(dts);
    if (null != firstCrimeDate) {
      String date = DateHandler.convertDateTimeFormat(firstCrimeDate);
      if (null != date) {
        for (People people : lp) {
          if (!people.getPtype().equals(PeopleType.DEFENDANT)) {
            continue;
          }
          people.getPeopleAttrMap().put(InfoPointKey.info_first_crime_date[InfoPointKey.mode],
              date);
          if (people.getPeopleAttrMap()
              .get(InfoPointKey.info_custody_date[InfoPointKey.mode]) != null) {
            String custodyDate = (String) people.getPeopleAttrMap()
                .get(InfoPointKey.info_custody_date[InfoPointKey.mode]);
            DateTime custodyDt = DateHandler.makeDateTime(custodyDate);
            if (null != custodyDt && custodyDt.isAfter(firstCrimeDate)) {
              try {
                int months = DateHandler.getMonthDiff(firstCrimeDate, custodyDt);
                people.getPeopleAttrMap().put(InfoPointKey.info_crime_duration[InfoPointKey.mode],
                    months);
              } catch (Exception e) {
                // do nothing here
              }
            }
          } else if (maxDate.isAfter(firstCrimeDate)) {
            try {
              int months = DateHandler.getMonthDiff(firstCrimeDate, maxDate);
              people.getPeopleAttrMap().put(InfoPointKey.info_crime_duration[InfoPointKey.mode],
                  months);
            } catch (Exception e) {
              // do nothing here
            }
          }
        }
      }
    }
  }

  private static final Pattern[] PATTERNS_BEGIN2 = {Pattern.compile("涉嫌|因本案"),
      Pattern.compile("被[^,，;；。\\.、]+(公安|分)局"), Pattern.compile("拘留|羁押|逮捕")};
  private static final Pattern[] PATTERNS_CAPTURE = {Pattern.compile("抓获")};
  private static final Pattern[] PATTERNS_DETENTION_OPINION =
      {Pattern.compile("(决定|批准)[^。，,;；]*刑事拘留")};
  private static final Pattern[] PATTERNS_ARREST_OPINION = {Pattern.compile("(决定|批准)[^。，,;；]*逮捕")};
  private static final Pattern[] PATTERNS_DETENTION_ACTION =
      {Pattern.compile("(执行|公安局|公安机关|派出所|被)[^。，,;；]*刑事拘留")};
  private static final Pattern[] PATTERNS_ARREST_ACTION =
      {Pattern.compile("(执行|公安局|公安机关|派出所|被)[^。，,;；]*逮捕")};
  private static final Pattern[] PATTERNS_BAIL_PROCURATORATE =
      {Pattern.compile("(检察院|该院)[^。，,;；]*取保候审")};
  private static final Pattern[] PATTERNS_BAIL_COURT = {Pattern.compile("(法院|本院)[^。，,;；]*取保候审")};
  private static final Pattern[] PATTERNS_BAIL_POLICE =
      {Pattern.compile("(公安局|公安机关|派出所)[^。，,;；]*取保候审")};

  private static final Pattern[] PATTERNS_RESIDENCE_MONITOR = {Pattern.compile("监视居住")};

  private static final Pattern[] PATTERNS_RESIDENCE_RECALL = {Pattern.compile("拘传")};

  private static final Pattern[] PATTERNS_DEPARTMENT =
      {Pattern.compile("被([^。，,;；]*(?:公安局|分局|派出所))(?:逮捕|拘留|刑事拘留|行政拘留)")};
}
