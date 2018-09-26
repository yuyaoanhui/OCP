package com.ocp.rabbit.proxy.extractor.custom.dispute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.algorithm.dispute.DocumentMapper;
import com.ocp.rabbit.repository.tool.algorithm.dispute.RegexBasedFilter;
import com.ocp.rabbit.repository.tool.algorithm.dispute.RegexModelGenerator3;
import com.ocp.rabbit.repository.tool.algorithm.law.UrlLabel;

/**
 * 
 * @author yu.yao 2018年8月20日
 *
 */
public class TestRegexBasedModelExtractor3 {
  private Context context;

  public TestRegexBasedModelExtractor3(Context context) {
    this.context = context;
  }

  public void extract() {
    DocumentMapper documentMapper = new DocumentMapper(context);
    Map<String, RegexBasedFilter> rbf_map =
        RegexModelGenerator3.getInstance().getRegexBasedFilterMap();
    for (String key : rbf_map.keySet()) {
      RegexBasedFilter rbf = rbf_map.get(key);
      String ay = "";
      if (context.rabbitInfo.extractInfo
          .containsKey(InfoPointKey.meta_case_ay[InfoPointKey.mode])) {// 如果案由不匹配则不抽取
        ay = context.rabbitInfo.extractInfo.get(InfoPointKey.meta_case_ay[InfoPointKey.mode])
            .toString();
      }
      String filter = rbf.getAy();
      if ((filter.length() >= 1) && (!ay.equals(filter))) {
        continue;
      }

      double prob = rbf.getScore(documentMapper);
      if (prob >= rbf.getCondition()) {
        context.rabbitInfo.extractInfo.put(key, true);
        Map<String, List<int[]>> tag_pos = GetRegexMatchPos(documentMapper, rbf);
        putUrlLabelTag_pos(key, tag_pos);
        Map<String, Object> info = context.rabbitInfo.extractInfo;
        String caseNo = (String) info.get(InfoPointKey.meta_case_id[InfoPointKey.mode]);
        if (rbf.getCases().contains(caseNo)) {
          context.rabbitInfo.extractInfo.put("cases_" + key, true);
        }

      }
    }
  }

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
              // if (mt.groupCount() > 2){
              // System.out.println("here");
              // }
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
              if (LabelToPos.containsKey(entry.getKey())) {
                posSet.addAll(LabelToPos.get(entry.getKey()));
              }
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
            if (LabelToPos.containsKey(entry.getKey())) {
              posSet.addAll(LabelToPos.get(entry.getKey()));
            }
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
