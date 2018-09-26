package com.ocp.rabbit.proxy.extractor.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.proxy.extractor.AbstractExtractor;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.LitigantRoleRecognizer;
import com.ocp.rabbit.repository.algorithm.structure.Judgement;
import com.ocp.rabbit.repository.algorithm.structure.Procuratorate;
import com.ocp.rabbit.repository.algorithm.structure.Verdict;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.util.PropertiesUtil;

/**
 * 文书结构抽取
 *
 * @author yu.yao 2018年8月1日
 */
public class StructureExtractor extends AbstractExtractor {
  private Context context;

  public StructureExtractor(Context context) {
    this.context = context;
    LitigantRoleRecognizer.readLitigantRole("litigant.role");
  }

  /**
   * {@inheritDoc}: 根据文书类型分段和打标签
   */
  @Override
  public void handle() {
    DocumentType type = context.docInfo.getDocType();
    if (type == null) {
      context.complexChain.stopNow(context.complexChain,
          RabbitResultCode.RABBIT_UNSUPPORTED_DOC_TYPE);
    }
    if ("on".equals(getValue(type))) {
      List<Pattern> list1 = new ArrayList<Pattern>();
      List<Pattern> list2 = new ArrayList<Pattern>();
      list1.add(Pattern.compile(
          LitigantRoleRecognizer.getRoleDefendantPattern().toString() + "(?!(诉称|辩称|主张|指控|认为))"));
      list2.add(Pattern.compile(
          LitigantRoleRecognizer.getRolePlaintiffPattern().toString() + "(?!(诉称|辩称|主张|指控|认为))"));
      context.docInfo.getLabelPatterns().put(ParaLabelEnum.DEFENDANT, list1);
      context.docInfo.getLabelPatterns().put(ParaLabelEnum.PLAINTIFF, list2);
      if (type.equals(DocumentType.judgement)) {
        new Judgement(context).doStructure();
      } else if (type.equals(DocumentType.verdict)) {
        new Verdict(context).doStructure();
      } else if (DocumentType.getProcuratorate().contains(type)) {
        new Procuratorate(context).doStructure();
      }
    } else {
      context.complexChain.stopNow(context.complexChain,
          RabbitResultCode.RABBIT_UNSUPPORTED_DOC_TYPE);
    }
  }

  private String getValue(DocumentType type) {
    String key = "switch." + type.name();
    String value = PropertiesUtil.getProperty("application.properties", key);
    return value;
  }
}
