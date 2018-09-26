package com.ocp.rabbit.repository.tool.algorithm.dispute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;
import com.ocp.rabbit.repository.entity.RabbitInfo;

/**
 * 文书标签内容映射类
 * 
 * @author yu.yao 2018年8月20日
 *
 */
public class DocumentMapper {
  private Context context;


  // 添加注意类型和写法
  private static Map<String, List<ParaLabelEnum>> str2csl =
      new HashMap<String, List<ParaLabelEnum>>() {
        private static final long serialVersionUID = 1L;
        {
          put("facts", SectionPostProcess.facts);
          put("caseSummary", new ArrayList<ParaLabelEnum>() {
            private static final long serialVersionUID = 1L;
            {
              add(ParaLabelEnum.CASE_SUMMARY);
            }
          });
          put("people", SectionPostProcess.people);
          put("plaintiffArgsAndCourtOpinions", SectionPostProcess.plaintiffArgsAndCourtOpinions);
          put("defendantArgsAndCourtOpinions", SectionPostProcess.defendantArgsAndCourtOpinions);
          put("plaintiffArgsAndFacts", SectionPostProcess.plaintiffArgsAndFacts);
          put("plaintiffArgs", SectionPostProcess.plaintiffArgs);
          put("defendantArgs", SectionPostProcess.defendantArgs);
          put("factsRelated", SectionPostProcess.factsRelated);
          put("factsAndCourtOpinion", SectionPostProcess.factsAndCourtOpinion);
          put("courtOpinion", new ArrayList<ParaLabelEnum>() {
            private static final long serialVersionUID = 1L;
            {
              add(ParaLabelEnum.COURT_OPINION);
            }
          });
          put("plaintiffAndDefendantArgs", SectionPostProcess.plaintiffAndDefendantArgs);
          put("judgement_content", new ArrayList<ParaLabelEnum>() {
            private static final long serialVersionUID = 1L;
            {
              add(ParaLabelEnum.JUDGEMENT_CONTENT);
            }
          });
        }

      };

  private RabbitInfo rbInfo;
  private Set<String> paragraphLabels;
  private Map<String, List<String>> paragraphMapper;

  public DocumentMapper(Context context) {
    this.context = context;
    this.rbInfo = context.rabbitInfo;
    paragraphLabels = RegexModelGenerator.getInstance().getFactsLabelsSet();
    generateParagraphMapper(rbInfo, paragraphLabels);
  }

  public RabbitInfo getDocument() {
    return rbInfo;
  }

  public synchronized void generateParagraphMapper(RabbitInfo rbInfo, Set<String> labels) {
    Map<String, List<String>> paraMapper = new HashMap<>();
    for (String label : str2csl.keySet()) {
      List<String> paragraphList = new ArrayList<>();
      List<ParaLabelEnum> eles = str2csl.get(label);
      for (ParaLabelEnum parle : eles) {
        Map<Integer, String> map =
            context.docInfo.getParaLabels().getByLabel(parle.getLabel()).getContent();
        if (map == null)
          continue;
        for (int i : map.keySet()) {
          paragraphList.add(map.get(i));
        }
        paraMapper.put(label, paragraphList);
      }
    }
    String label = "all";
    List<String> paragraphs = new ArrayList<String>();
    List<String> sentences = context.getAllUnits();
    for (String sent : sentences) {
      paragraphs.add(sent);
    }
    paraMapper.put(label, paragraphs);
    paragraphMapper = paraMapper;
  }

  public Set<String> getParagraphLabels() {
    return paragraphLabels;
  }

  public void setParagraphLabels(Set<String> paragraphLabels) {
    this.paragraphLabels = paragraphLabels;
  }

  public List<String> getParagraphs(String label) {
    return paragraphMapper.getOrDefault(label, new ArrayList<>());
  }

  public String getParagraph(String label) {
    String s = "";
    for (String str : paragraphMapper.getOrDefault(label, new ArrayList<>())) {
      s += str + "\n";
    }
    return s;
  }
}
