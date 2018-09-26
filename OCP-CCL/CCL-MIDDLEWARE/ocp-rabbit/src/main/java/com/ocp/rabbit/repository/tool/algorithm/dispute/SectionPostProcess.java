package com.ocp.rabbit.repository.tool.algorithm.dispute;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ocp.rabbit.repository.constant.ParaLabelEnum;

/**
 * 
 * @author yu.yao 2018年8月27日
 *
 */
public class SectionPostProcess {
  /*
   * 如果定义新的段落结构，比如是之前段落结构的某种组合，可以在这里实现； 目前实现了对人物相关段落的组合定义。
   */
  public static List<ParaLabelEnum> people = combinePeople();
  public static List<ParaLabelEnum> plaintiffArgsAndCourtOpinions = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> plaintiffArgsAndCourtOpinions2 = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> facts = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> plaintiffArgsAndFacts = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> plaintiffArgs = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> defendantArgsAndCourtOpinions = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> defendantArgs = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> plaintiffAndDefendantArgs = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> factsRelated = new ArrayList<ParaLabelEnum>();
  public static List<ParaLabelEnum> factsAndCourtOpinion = new ArrayList<ParaLabelEnum>();

  static {
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.PLAINTIFF_ARGS);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.PLAINTIFF_ARGS_FISRT);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.PLAINTIFF_ARGS_SECOND);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.PLAINTIFF_ARGS_REVIEW);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.OFFICE_OPINION);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.COURT_OPINION);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.COURT_BASE_OPINION);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.COURT_PRIMARY_OPINION);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.COURT_SECONDARY_OPINION);
    plaintiffArgsAndCourtOpinions.add(ParaLabelEnum.COURT_REVIEW_OPINION);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.PLAINTIFF_ARGS);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.PLAINTIFF_ARGS_FISRT);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.PLAINTIFF_ARGS_SECOND);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.COURT_OPINION);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.COURT_BASE_OPINION);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.COURT_PRIMARY_OPINION);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.COURT_SECONDARY_OPINION);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.FACTS_FOUND_SECONDARY);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.FACTS_FOUND_PRIMARY);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.FACTS_FOUND_BASE);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.FACTS_FOUND);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.FACTS_FOUND_CMPL);
    plaintiffArgsAndCourtOpinions2.add(ParaLabelEnum.FACTS_ABOVE);
    facts.add(ParaLabelEnum.FACTS_FOUND_REVIEW);
    facts.add(ParaLabelEnum.FACTS_FOUND_SECONDARY);
    facts.add(ParaLabelEnum.FACTS_FOUND_PRIMARY);
    facts.add(ParaLabelEnum.FACTS_FOUND_BASE);
    facts.add(ParaLabelEnum.FACTS_FOUND);
    facts.add(ParaLabelEnum.FACTS_FOUND_CMPL);
    facts.add(ParaLabelEnum.FACTS_ABOVE);
    facts.add(ParaLabelEnum.COURT_REVIEW_OPINION);
    facts.add(ParaLabelEnum.COURT_BASE_OPINION);
    facts.add(ParaLabelEnum.COURT_PRIMARY_OPINION);
    facts.add(ParaLabelEnum.COURT_SECONDARY_OPINION);
    facts.add(ParaLabelEnum.COURT_OPINION);
    plaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS);
    plaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL);
    plaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_FISRT);
    plaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_SECOND);
    plaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_REVIEW);
    plaintiffArgsAndFacts.add(ParaLabelEnum.OFFICE_OPINION);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_REVIEW);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_SECONDARY);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_PRIMARY);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_BASE);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_CMPL);
    plaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_ABOVE);
    plaintiffArgs.add(ParaLabelEnum.PLAINTIFF_ARGS);
    plaintiffArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL);
    plaintiffArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_FISRT);
    plaintiffArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_SECOND);
    plaintiffArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_REVIEW);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.DEFENDANT_ARGS);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.DEFENDANT_ARGS_ORIGINAL);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.DEFENDANT_ARGS_FIRST);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.DEFENDANT_ARGS_SECOND);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.DEFENDANT_ARGS_REVIEW);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.COURT_OPINION);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.COURT_BASE_OPINION);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.COURT_PRIMARY_OPINION);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.COURT_SECONDARY_OPINION);
    defendantArgsAndCourtOpinions.add(ParaLabelEnum.COURT_REVIEW_OPINION);
    defendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS);
    defendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_ORIGINAL);
    defendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_FIRST);
    defendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_SECOND);
    defendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_REVIEW);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.PLAINTIFF_ARGS);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_FISRT);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_SECOND);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.PLAINTIFF_ARGS_REVIEW);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_ORIGINAL);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_FIRST);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_SECOND);
    plaintiffAndDefendantArgs.add(ParaLabelEnum.DEFENDANT_ARGS_REVIEW);
    factsRelated.add(ParaLabelEnum.FACTS_FOUND_SECONDARY);
    factsRelated.add(ParaLabelEnum.FACTS_FOUND_PRIMARY);
    factsRelated.add(ParaLabelEnum.FACTS_FOUND_BASE);
    factsRelated.add(ParaLabelEnum.FACTS_FOUND);
    factsRelated.add(ParaLabelEnum.FACTS_FOUND_CMPL);
    factsRelated.add(ParaLabelEnum.FACTS_ABOVE);
    factsRelated.add(ParaLabelEnum.FACTS_FOUND_REVIEW);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_FOUND_SECONDARY);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_FOUND_PRIMARY);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_FOUND_BASE);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_FOUND);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_FOUND_CMPL);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_ABOVE);
    factsAndCourtOpinion.add(ParaLabelEnum.FACTS_FOUND_REVIEW);
    factsAndCourtOpinion.add(ParaLabelEnum.COURT_OPINION);
  };

  public static List<ParaLabelEnum> combinePeople() {
    List<ParaLabelEnum> pd_set = new ArrayList<ParaLabelEnum>();
    pd_set.add(ParaLabelEnum.REPRESENTATIVE);
    pd_set.add(ParaLabelEnum.ASSIGNED);
    pd_set.add(ParaLabelEnum.REPRESENTATIVE);
    pd_set.add(ParaLabelEnum.ATTORNEY);
    pd_set.add(ParaLabelEnum.DEFENDANT);
    pd_set.add(ParaLabelEnum.PLAINTIFF);
    pd_set.add(ParaLabelEnum.THIRD_PERSON);
    return pd_set;
  }

  public static Set<ParaLabelEnum> combineFactsAndCourtOpinion() {
    HashSet<ParaLabelEnum> pd_set = new HashSet<ParaLabelEnum>();
    pd_set.add(ParaLabelEnum.FACTS_FOUND);
    pd_set.add(ParaLabelEnum.COURT_OPINION);
    return pd_set;
  }
}
