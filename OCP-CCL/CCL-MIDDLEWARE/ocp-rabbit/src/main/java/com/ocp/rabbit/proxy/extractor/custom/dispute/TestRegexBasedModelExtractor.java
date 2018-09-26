package com.ocp.rabbit.proxy.extractor.custom.dispute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.extractor.common.ReferLigitantRelatedInfoExtrator;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.LitigantRecognizer;
import com.ocp.rabbit.repository.bean.ParaLabelBean;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.dispute.DocumentMapper;
import com.ocp.rabbit.repository.tool.algorithm.dispute.RegexBasedFilter;
import com.ocp.rabbit.repository.tool.algorithm.dispute.RegexModelGenerator;
import com.ocp.rabbit.repository.tool.algorithm.dispute.SectionPostProcess;
import com.ocp.rabbit.repository.tool.algorithm.dispute.TestInfoKey;
import com.ocp.rabbit.repository.tool.algorithm.law.UrlLabel;
import com.ocp.rabbit.repository.tool.algorithm.litigant.LitigantUnit;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 基于模型的抽取
 * 
 * @author yu.yao 2018年8月20日
 *
 */
public class TestRegexBasedModelExtractor {

  private Context context;

  private ReferLigitantRelatedInfoExtrator referExtractor ;

  public TestRegexBasedModelExtractor(Context context) {
    this.context = context;
    referExtractor =
            new ReferLigitantRelatedInfoExtrator(context);
  }


  static Pattern pattern_couple = Pattern.compile("夫妻关系");
  static Pattern pattern_relative = Pattern.compile(
      "(((原(.){0,1}被告)(是|系))|((原告|被告|上诉人|被上诉人)(.){0,3}(与|是|系)(原告|被告|上诉人|被上诉人)(.){0,3}(的)?)|((.){0,3}与(.){0,4}))(直系亲属|母子|父子|儿子|女儿|孙子|孙女|之子|之女|之父|母亲|父亲|爷爷|奶奶|外婆|祖父|祖母|母女)(关系|[;；，,。]|$)");
  static Pattern pattern_affair = Pattern.compile("婚外情|包养协议|包养关系|情妇|不正当关系");

  @SuppressWarnings("unchecked")
  public void extract() {
    LitigantRecognizer lr = referExtractor.buildLitigantRecognizer();
    Map<String, Object> info = context.rabbitInfo.extractInfo;
    DocumentMapper documentMapper = new DocumentMapper(context);
    RegexBasedFilter rbf;
    double prob;
    List<String> defendants = (List<String>) info
        .getOrDefault(InfoPointKey.meta_defendant_names[InfoPointKey.mode], new ArrayList<>());
    List<String> plaintiffs = (List<String>) info
        .getOrDefault(InfoPointKey.meta_plaintiff_names[InfoPointKey.mode], new ArrayList<>());
    /**
     * 被告原告是直系亲属关系
     */
    boolean relative = parseRelation(defendants, plaintiffs, lr, pattern_relative,
        context.docInfo.getParaLabels().getBeanByEnum(SectionPostProcess.facts));
    if (relative) {
      info.put(TestInfoKey.INFO_RELATIVE_DIRECT, true);
      rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
          .get(TestInfoKey.INFO_RELATIVE_DIRECT);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_RELATIVE_DIRECT, tag_pos);
    }
    /**
     * 款项交付无凭证
     */
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_DEBT_DELIVER_WITHOUT_RECORD);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_DEBT_DELIVER_WITHOUT_RECORD, true);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_DEBT_DELIVER_WITHOUT_RECORD, tag_pos);
    }
    /**
     * 当事人是夫妻关系
     *
     */
    boolean couple = parseRelationBetweenLitigant(lr, pattern_couple,
        context.docInfo.getParaLabels().getBeanByEnum(SectionPostProcess.facts));
    if (couple) {
      info.put(TestInfoKey.INFO_COUPLE, true);
      rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap().get(TestInfoKey.INFO_COUPLE);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_COUPLE, tag_pos);
    }
    /**
     * 离婚后出具借条
     */
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_IOU_AFTER_DIVORCE);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_IOU_AFTER_DIVORCE, true);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_IOU_AFTER_DIVORCE, tag_pos);
    }
    /**
     * 债务在夫妻关系存续期间
     */
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_DEBT_MARRIAGE_PERIOD);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_DEBT_MARRIAGE_PERIOD, true);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_DEBT_MARRIAGE_PERIOD, tag_pos);
    }
    /**
     * info_夫妻关系存续期间
     */
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_MARRIAGE_PERIOD);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_MARRIAGE_PERIOD, true);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_MARRIAGE_PERIOD, tag_pos);
    }
    /**
     * 个人名义
     */
    if (info.containsKey(TestInfoKey.INFO_MARRIAGE_PERIOD)) {
      rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
          .get(TestInfoKey.INFO_ONE_PART_BORROWER);
      prob = rbf.getScore(documentMapper);
      if (prob >= 0.85) {
        context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_ONE_PART_BORROWER, true);
        Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
        putUrlLabelTag_pos(TestInfoKey.INFO_ONE_PART_BORROWER, tag_pos);
      }
    } else {
      if (info.containsKey(TestInfoKey.INFO_COUPLE)
          && info.containsKey(TestInfoKey.INFO_DEBT_MARRIAGE_PERIOD)
          && info.containsKey(TestInfoKey.INFO_IOU_AFTER_DIVORCE)) {
        info.put(TestInfoKey.INFO_ONE_PART_BORROWER, true);
      }
    }
    /**
     * 被告原告是婚外情关系
     */
    boolean affair = parseRelation(defendants, plaintiffs, lr, pattern_affair,
        context.docInfo.getParaLabels().getBeanByEnum(SectionPostProcess.facts));
    if (affair) {
      info.put(TestInfoKey.INFO_AFFAIR, true);
      rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap().get(TestInfoKey.INFO_AFFAIR);
      Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
      putUrlLabelTag_pos(TestInfoKey.INFO_AFFAIR, tag_pos);
    }
    /**
     * 用来维持婚外情
     */
    if (info.containsKey(TestInfoKey.INFO_AFFAIR)) {
      rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
          .get(TestInfoKey.INFO_DEBT_FOR_AFFAIR);
      prob = rbf.getScore(documentMapper);
      if (prob >= 0.85) {
        context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_DEBT_FOR_AFFAIR, true);
        Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
        putUrlLabelTag_pos(TestInfoKey.INFO_DEBT_FOR_AFFAIR, tag_pos);
      }
    } else {
      rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap().get("info_款项用来维持不正当关系2");
      prob = rbf.getScore(documentMapper);
      if (prob >= 0.85) {
        context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_DEBT_FOR_AFFAIR, true);
        context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_AFFAIR, true);
        Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
        putUrlLabelTag_pos(TestInfoKey.INFO_DEBT_FOR_AFFAIR, tag_pos);
      }
    }
    // info_夫妻一方与其亲密亲属虚构债务
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_FAKE_DEBT_ONE_PART_WITH_RELATIVE);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_FAKE_DEBT_ONE_PART_WITH_RELATIVE, true);
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_RELATIVE_RELATED, true);
    }

    // info_夫妻一方为任职公司无偿提供借款担保
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_GUARANT_ONEPART_COMPANY);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_GUARANT_ONEPART_COMPANY, true);
    }
    // info_一方虚构债务
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_FAKE_DEBT_ONE_PART);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_FAKE_DEBT_ONE_PART, true);
    }
    // info_一方冒用另一方名义共同出具借条
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_GUARANT_ONEPART_ASSUME);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_GUARANT_ONEPART_ASSUME, true);
    }
    // info_夫妻关系存续期间
    rbf = RegexModelGenerator.getInstance().getRegexBasedFilterMap()
        .get(TestInfoKey.INFO_MARRIAGE_PERIOD);
    prob = rbf.getScore(documentMapper);
    if (prob >= 0.85) {
      context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_MARRIAGE_PERIOD, true);
    } else {
      if (context.rabbitInfo.extractInfo.containsKey(TestInfoKey.INFO_GUARANT_ONEPART_COMPANY)
          || context.rabbitInfo.extractInfo
              .containsKey(TestInfoKey.INFO_FAKE_DEBT_ONE_PART_WITH_RELATIVE)
          || context.rabbitInfo.extractInfo.containsKey(TestInfoKey.INFO_FAKE_DEBT_ONE_PART)
          || context.rabbitInfo.extractInfo.containsKey(TestInfoKey.INFO_GUARANT_ONEPART_ASSUME)) {
        context.rabbitInfo.extractInfo.put(TestInfoKey.INFO_MARRIAGE_PERIOD, true);
      }
    }
  }

  /**
   * 当事人关系
   * 
   * @param plInfo
   */
  private boolean parseRelationBetweenLitigant(LitigantRecognizer lr, Pattern pattern,
      List<ParaLabelBean> para) {
    List<String> paragraphList = new ArrayList<String>();
    for (ParaLabelBean pl : para) {
        if(pl.getContent() != null) {
            for (int i : pl.getContent().keySet()) {
                paragraphList.add(pl.getContent().get(i));
            }
        }
    }
    for (String paragraph : paragraphList) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        if (pattern.matcher(sentence).find()) {
          NamedEntity[] defs = lr.recognize(sentence);
          if (defs.length != 0) {
            Set<String> foundNames = new HashSet<>();
            for (NamedEntity ne : defs) {
              String[] names = ((LitigantUnit) ne.getInfo()).getNames();
              foundNames.addAll(Arrays.asList(names));
            }
            if (foundNames.size() >= 2) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * 夫妻关系
   * 
   * @param plInfo
   * @param defendants
   * @param plaintiffs
   * @param lr
   * @param pattern
   * @param para
   * @return
   */
  private boolean parseRelation(List<String> defendants, List<String> plaintiffs,
      LitigantRecognizer lr, Pattern pattern, List<ParaLabelBean> para) {
    if (defendants.size() != 1 || plaintiffs.size() != 1)
      return false;
    List<String> paragraphList = new ArrayList<>();
    for (ParaLabelBean pl : para) {
        if(pl.getContent() != null) {
            for (int i : pl.getContent().keySet()) {
                paragraphList.add(pl.getContent().get(i));
            }
        }
    }
    String[][] paragraph = DocumentUtils.splitSentences(paragraphList);
    for (String[] sentences : paragraph) {
      for (String sentence : sentences) {
        if (sentence.length() <= 20 && pattern.matcher(sentence).find()) {
          NamedEntity[] litigants = lr.recognize(sentence);
          int defs = 0, plts = 0;
          for (NamedEntity ne : litigants) {
            if (LitigantUnit.LABEL_LITIGANT.equals(((LitigantUnit) ne.getInfo()).getLabel())) {
              return true;
            } else if (LitigantUnit.LABEL_DEFENDANT
                .equals(((LitigantUnit) ne.getInfo()).getLabel())) {
              defs++;
            } else {
              plts++;
            }
          }
          if (1 == defs && 1 == plts) {
            return true;
          }
        }
      }
    }
    return false;
  }

  static Pattern pattern_lover = Pattern.compile("情人");
  static Pattern pattern_lover2 = Pattern.compile("夫妻|妻子|结婚");

  /**
   *
   * @param documentMapper
   * @param rbf
   * @return
   */
  public Map<String, List<int[]>> GetRegexMatchPos(DocumentMapper documentMapper,
      RegexBasedFilter rbf) {
    Map<String, List<int[]>> LabelToPos = new HashMap<>();
    if (rbf.getChildRegexBasedFilters() != null) {
      for (RegexBasedFilter rf : rbf.getChildRegexBasedFilters()) {
        Pattern pattern = rf.getPattern();
        if (pattern == null) {
          continue;
        }
        // assemble para by label
        Map<String, Object> info = documentMapper.getDocument().getExtractInfo();
        for (Map.Entry<String, Object> entry : info.entrySet()) {
          if (entry.getKey().contains("section")) {
            List<int[]> posSet = new ArrayList<>();
            String match_str = entry.getValue().toString();
            java.util.regex.Matcher mt = pattern.matcher(match_str);
            while (mt.find()) {
              if (mt.groupCount() > 2) {
              }
              int start = mt.start();
              for (int i = start; i > 0; i--) {
                String tmp_str = match_str.substring(i - 1, i);
                if (tmp_str.equals("，") || tmp_str.equals("。") || tmp_str.equals("；")
                    || tmp_str.equals("：")) {
                  start = i;
                  break;
                }
              }
              int end = mt.end();
              for (int i = end; i < match_str.length(); i++) {
                String tmp_str = match_str.substring(i, i + 1);
                if (tmp_str.equals("，") || tmp_str.equals("。") || tmp_str.equals("；")
                    || tmp_str.equals("：")) {
                  end = i;
                  break;
                }
              }
              int[] tmp = {start, end};
              posSet.add(tmp);
            }

            if (posSet.size() != 0) {
              LabelToPos.put(entry.getKey(), posSet);
            }
          } else {
            continue;
          }
        }
      }
    } else {
      // assemble para by label
      Pattern pattern = rbf.getPattern();
      Map<String, Object> info = documentMapper.getDocument().getExtractInfo();
      for (Map.Entry<String, Object> entry : info.entrySet()) {
        if (entry.getKey().contains("section")) {
          List<int[]> posSet = new ArrayList<>();
          // add by cywei by 2017.5.24
          String match_str = entry.getValue().toString();
          java.util.regex.Matcher mt = pattern.matcher(match_str);
          if (mt.find()) {
            // MatchResult mr = mt.toMatchResult();
            int start = mt.start();
            for (int i = start; i > 0; i--) {
              String tmp_str = match_str.substring(i - 1, i);
              if (tmp_str.equals("，") || tmp_str.equals("。") || tmp_str.equals("；")
                  || tmp_str.equals("：")) {
                start = i;
                break;
              }
            }
            int end = mt.end();
            for (int i = end; i < match_str.length(); i++) {
              String tmp_str = match_str.substring(i, i + 1);
              if (tmp_str.equals("，") || tmp_str.equals("。") || tmp_str.equals("；")
                  || tmp_str.equals("：")) {
                end = i;
                break;
              }
            }
            int[] tmp = {start, end};
            posSet.add(tmp);
          }

          if (posSet.size() != 0) {
            LabelToPos.put(entry.getKey(), posSet);
          }

        } else {
          continue;
        }
      }
    }
    return LabelToPos;
  }

  /**
   * 将抽取器提取的em位置保存
   */
  public void putUrlLabelTag_pos(String info, Map<String, List<int[]>> tag_pos) {
    Map<String, Map<String, List<UrlLabel>>> urlLabels = new HashMap<>();;
    if (context.rabbitInfo.getUrlLabels() == null) {
      context.rabbitInfo.setUrlLabels(urlLabels);
    }
    Map<String, List<UrlLabel>> posLabels = new HashMap<>();// Map<Section,>
    for (Map.Entry<String, List<int[]>> entry : tag_pos.entrySet()) {
      String Section = entry.getKey();
      List<int[]> posList = entry.getValue();
      List<UrlLabel> lawLabels = new ArrayList<>();
      for (int[] e : posList) {
        UrlLabel urlLabel = new UrlLabel(UrlLabel.LABEL_TYPE_EM, "<em>", "</em>", e[0], e[1]);
        if (posLabels.containsKey(Section)) {
          lawLabels = posLabels.get(Section);
        } else
          lawLabels = new ArrayList<>();

        lawLabels.add(urlLabel);
        posLabels.put(Section, lawLabels);
      }
    }
    if (context.rabbitInfo.getUrlLabels().containsKey(info)) {
      posLabels.putAll(context.rabbitInfo.getUrlLabels().get(info));
    }
    context.rabbitInfo.getUrlLabels().put(info, posLabels);
  }
}
