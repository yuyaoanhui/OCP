package com.ocp.rabbit.proxy.extractor.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.algorithm.RelatedCase;
import com.ocp.rabbit.repository.util.DocumentUtils;

/**
 * 关联案件
 * 
 * @author yu.yao 2018年8月13日
 *
 */
public class PreCaseIdExtractor {

  private Context context;

  public PreCaseIdExtractor(Context context) {
    this.context = context;
  }

  public void extract(List<String> paragraphs) {
    Map<String, Object> info = context.rabbitInfo.getExtractInfo();
    NamedEntityRecognizer ner = new NamedEntityRecognizer(this.context);
    List<RelatedCase> lrc = parseRelatedCase(paragraphs, ner);
    if (lrc.size() != 0) {
      List<Map<String, String>> relatedCaseInfo = new ArrayList<>();
      List<String> precaseAnhao = new ArrayList<>();
      for (RelatedCase rc : lrc) {
        Map<String, String> map = new HashMap<>();
        if (null != rc.getStrPreCaseId()) {
          map.put(InfoPointKey.meta_case_id[InfoPointKey.mode], rc.getStrPreCaseId());
          precaseAnhao.add(rc.getStrPreCaseId());
        }
        if (null != rc.getStrPreCaseCourt())
          map.put(InfoPointKey.meta_court_name[InfoPointKey.mode], rc.getStrPreCaseCourt());
        if (null != rc.getStrCaseName())
          map.put(InfoPointKey.meta_case_name[InfoPointKey.mode], rc.getStrCaseName());
        if (null != rc.getStrPreCaseDate())
          map.put(InfoPointKey.meta_doc_date[InfoPointKey.mode], rc.getStrPreCaseDate());
        relatedCaseInfo.add(map);
      }
      info.put(InfoPointKey.meta_related_case[InfoPointKey.mode], relatedCaseInfo);
      if (precaseAnhao.size() != 0) {
        info.put(InfoPointKey.meta_related_caseid[InfoPointKey.mode], precaseAnhao);
      }
    }

    // 起诉书
    parseRelatedIndictment(paragraphs);
  }

  private static Pattern PATTERN_ANHAO =
      Pattern.compile("[\\(（]\\d+[\\)）][\u4e00-\u9fa5\\d]{4,15}号");

  /**
   * 得到关联案件
   */
  private List<RelatedCase> parseRelatedCase(List<String> paragraphs, NamedEntityRecognizer ner) {
    Set<RelatedCase> lrc = new HashSet<>();
    NamedEntity NES_LAST_COURT = null;
    for (String paragraph : paragraphs) {
      String[] sentences = DocumentUtils.splitOneParagraphByPeriod(paragraph);
      for (String sentence : sentences) {
        NamedEntity[] nes_anhao =
            NamedEntityRecognizer.recognizeEntityByRegex(sentence, PATTERN_ANHAO);
        if (nes_anhao.length == 0)
          continue;
        for (NamedEntity ne : nes_anhao) {
          String tmp = (String) ne.getSource();
          tmp = tmp.replaceAll("\\(", "（");
          tmp = tmp.replaceAll("\\)", "）");
          lrc.add(new RelatedCase(tmp));
        }
        String thisCourt = null;
        if (context.rabbitInfo.getExtractInfo()
            .get(InfoPointKey.meta_court_name[InfoPointKey.mode]) != null) {
          thisCourt = (String) (context.rabbitInfo.getExtractInfo()
              .get(InfoPointKey.meta_court_name[InfoPointKey.mode]));
        }
        NamedEntity[] nes_court =
            NamedEntityRecognizer.recognizeCourtName(sentence, false, thisCourt, NES_LAST_COURT);
        if (nes_court.length != 0)
          NES_LAST_COURT = nes_court[nes_court.length - 1];
        List<NamedEntity[]> lnes =
            NamedEntityRecognizer.entityMatch(sentence, nes_court, nes_anhao, true, false);
        for (NamedEntity[] nes : lnes) {
          String tmp = (String) nes[1].getSource();
          tmp = tmp.replaceAll("\\(", "（");
          tmp = tmp.replaceAll("\\)", "）");
          for (RelatedCase relatedCase : lrc) {
            if (tmp.equals(relatedCase.getStrPreCaseId())) {
              relatedCase.setStrPreCaseCourt((String) nes[0].getInfo());
              break;
            }
          }
        }
      }
    }
    return new ArrayList<RelatedCase>(lrc);
  }

  /**
   * 刑事案件起诉书
   */
  private void parseRelatedIndictment(List<String> paragraphs) {
    Pattern pattern_Indictment = Pattern.compile("检察院[^;；。\\.]*?以(.*?)(?:起诉)?书");

    for (String paragraph : paragraphs) {
      Matcher matcher = pattern_Indictment.matcher(paragraph);
      if (matcher.find()) {
        String result = matcher.group(1);
        result = result.replaceAll("[【﹝〔\\(\\[]", "（");
        result = result.replaceAll("[】〕﹞\\)\\]]", "）");
        context.rabbitInfo.getExtractInfo().put(InfoPointKey.meta_indictment[InfoPointKey.mode],
            result);
      }
    }
  }

}
