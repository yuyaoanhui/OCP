package com.ocp.rabbit.repository.algorithm.structure;

import java.util.HashMap;

import org.springframework.util.StringUtils;

import com.ocp.rabbit.proxy.component.custom.PatternComp;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;

/**
 * 检察院文书识别算法类
 * 
 * @author yu.yao 2018年9月6日
 *
 */
public class Procuratorate {
  private Context context;
  private PatternComp pattern;

  public Procuratorate(Context context) {
    this.context = context;
    pattern = new PatternComp(context);
  }

  public void doStructure() {
    for (int i = 0; i < context.getAllUnits().size(); i++) {
      String unit = context.getAllUnits().get(i);
      if (StringUtils.isEmpty(unit)) {
        continue;
      }
      for (ParaLabelEnum key : context.docInfo.getLabelPatterns().keySet()) {
        context.regex = context.docInfo.getLabelPatterns().get(key);
        pattern.handle();
        if (context.isPatterned) {
          if (context.docInfo.getParaLabels().getProBeanByEnum(key).getContent() == null) {// 第一个自然段
            context.docInfo.getParaLabels().getProBeanByEnum(key)
                .setContent(new HashMap<Integer, String>());
          }
          context.docInfo.getParaLabels().getProBeanByEnum(key).getContent().put(i + 1, unit);
        }
      }
    }

  }
}
