package com.ocp.rabbit.repository.tool.algorithm.dispute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class RegexBasedFilter {

  public static final String NOT_LOGIC = "NOT";
  public static final String AND_LOGIC = "AND";
  public static final String OR_LOGIC = "OR";
  public static final int MINIMUM_CONDITION = -1000;
  private static final double MINIMUM_ERROR = 1e-6;



  /**
   * null if not leaf node
   */
  private Pattern pattern;

  /**
   * has value for all nodes
   */
  private double weight;

  /**
   * null for non-leaf node
   */
  private String targetSentencesLabel; // for all children nodes

  /**
   * null for non-leaf node
   */
  private String targetSentenceLabel; // for all children nodes

  /**
   * to combine all children nodes default to AND_LOGIC for leaf node
   */
  private String logicLabel;

  /**
   * has value for all nodes to combine the result of all children nodes
   */
  private double score;

  /**
   * threshold value this node should satisfy; otherwise return final result
   */
  private double condition;

  private String name;

  private Collection<String> cases = new ArrayList<>();
  private String ay = "";
  /**
   * null indicates leaf node
   */
  private List<RegexBasedFilter> childRegexBasedFilters;


  public RegexBasedFilter() {}

  public RegexBasedFilter(Pattern pattern, double weight, String targetSentencesLabel,
      String targetSentenceLabel, String logicLabel, double score,
      List<RegexBasedFilter> childRegexBasedFilters, double condition) {

    this.pattern = pattern;
    this.weight = weight;
    this.targetSentencesLabel = targetSentencesLabel;
    this.targetSentenceLabel = targetSentenceLabel;
    this.logicLabel = logicLabel;
    this.score = score;
    this.childRegexBasedFilters = childRegexBasedFilters;
    this.condition = condition;
  }

  public RegexBasedFilter(Pattern pattern, double weight, String targetSentencesLabel,
      String targetSentenceLabel, String logicLabel, double score,
      List<RegexBasedFilter> childRegexBasedFilters) {
    this.pattern = pattern;
    this.weight = weight;
    this.targetSentencesLabel = targetSentencesLabel;
    this.targetSentenceLabel = targetSentenceLabel;
    this.logicLabel = logicLabel;
    this.score = score;
    this.childRegexBasedFilters = childRegexBasedFilters;
    this.condition = MINIMUM_CONDITION;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCases(Collection<String> cases) {
    this.cases = cases;
  }

  public Collection<String> getCases() {
    return cases;
  }

  public double getScore() {
    return score;
  }

  public double getScore(DocumentMapper documentMapper) {
    return calculateScore(documentMapper);
  }

  public void setScore(double score) {
    this.score = score;
  }

  public double getCondition() {
    return condition;
  }

  public double calculateScore(DocumentMapper documentMapper) {
    /**
     * handle leaf node
     */
    if (null == childRegexBasedFilters) {
      score = 0;
      for (String sentence : documentMapper.getParagraphs(targetSentencesLabel)) {
        if (pattern.matcher(sentence).find()) {
          score = weight;
          break;
        }
      }
      /**
       * 对叶子节点的条件判断，看是否必须满足
       */
      if (condition - score >= MINIMUM_ERROR) {
        score = 0;
      }

    } else {
      /**
       * handle none-leaf nodes
       */
      double tmpScore = 0.;
      boolean CONDITION_FLAG = false;
      for (RegexBasedFilter crbf : childRegexBasedFilters) {
        if (CONDITION_FLAG)
          break;
        switch (logicLabel) {
          case AND_LOGIC: {
            double childScore = crbf.calculateScore(documentMapper);
            crbf.setScore(childScore);
            tmpScore += weight * childScore;
            if (crbf.getCondition() - childScore >= MINIMUM_ERROR) {
              CONDITION_FLAG = true;
              tmpScore = 0;
            }
            break;
          }
          case OR_LOGIC: {
            /**
             * stop if at least one node satisfy
             */
            if (0. == tmpScore) {
              double childScore = crbf.calculateScore(documentMapper);
              crbf.setScore(childScore);
              tmpScore += weight * childScore;
              if (crbf.getCondition() - childScore >= MINIMUM_ERROR) {
                CONDITION_FLAG = true;
                tmpScore = 0;
              }
            }
            break;
          }
          case NOT_LOGIC: {
            double childScore = crbf.calculateScore(documentMapper);
            childScore = crbf.weight - childScore;
            crbf.setScore(childScore);
            tmpScore = childScore;
            if (crbf.getCondition() - childScore >= MINIMUM_ERROR) {
              CONDITION_FLAG = true;
              tmpScore = 0;
            }
            break;
          }
          default: {
            break;
          }
        }
      }
      /**
       * 判断累加的tmpScore是否满足条件
       */
      if (condition - tmpScore >= MINIMUM_ERROR) {
        CONDITION_FLAG = true;
      }

      if (CONDITION_FLAG)
        score = 0;
      else
        score = tmpScore;
    }
    return score;
  }

  public List<RegexBasedFilter> getChildRegexBasedFilters() {
    return childRegexBasedFilters;
  }

  public String getTargetSentencesLabel() {
    return targetSentencesLabel;
  }

  public String getTargetSentenceLabel() {
    return targetSentenceLabel;
  }

  public Pattern getPattern() {
    return pattern;
  }

  public int matchCount(DocumentMapper documentMapper) {
    java.util.regex.Matcher matcher =
        pattern.matcher(documentMapper.getParagraph(targetSentenceLabel));
    int count = 0;
    while (matcher.find()) {
      count++;
    }
    return count;
  }


  public String getAy() {
    return ay;
  }

  public void setAy(String ay) {
    this.ay = ay;
  }
}
