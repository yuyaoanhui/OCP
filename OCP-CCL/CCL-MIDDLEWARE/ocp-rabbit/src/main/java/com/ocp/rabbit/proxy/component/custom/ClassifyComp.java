package com.ocp.rabbit.proxy.component.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.ocp.rabbit.proxy.component.AbstractComp;
import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.structure.FormatUtil;
import com.ocp.rabbit.repository.util.XmlParser;

/**
 * 文书分类构件
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public class ClassifyComp extends AbstractComp {
  private final static Map<Pattern, DocumentType> docClassifyMap = new HashMap<>();
  static {
    docClassifyMap.put(Pattern.compile("(裁\\s*定\\s*书)\\s*\\n"), DocumentType.verdict);
    docClassifyMap.put(Pattern.compile("(判\\s*决\\s*书)\\s*\\n"), DocumentType.judgement);
    docClassifyMap.put(Pattern.compile("(审\\s*查\\s*逮\\s*捕\\s*意\\s*见\\s*书)\\s*\\n"),
        DocumentType.examiningCatchingOpinion);
    docClassifyMap.put(Pattern.compile("(起\\s*诉\\s*书)\\s*\\n"), DocumentType.indictment);
    docClassifyMap.put(Pattern.compile("(不\\s*起\\s*诉\\s*决\\s*定\\s*书)\\s*\\n"),
        DocumentType.nonProsecutionDecision);
    docClassifyMap.put(Pattern.compile("(复\\s*(核|议)\\s*案\\s*件\\s*审\\s*查\\s*意\\s*见\\s*书)\\s*\\n"),
        DocumentType.recheckExaminingOpinion);
    docClassifyMap.put(Pattern.compile("(提\\s*请\\s*批\\s*(捕|准)\\s*逮\\s*捕\\s*书)\\s*\\n"),
        DocumentType.appealArrest);
  }
  private Context context;

  public ClassifyComp(Context context) {
    this.context = context;
  }

  @Override
  public void handle() {
    String[] units = context.document.split("\n");// 自然段划分
    context.setAllUnits(new ArrayList<String>());
    for (int i = 0; i < units.length; i++) {
      if (!StringUtils.isEmpty(units[i])) {
        if (units[i].contains("PAGE") || units[i].contains("page") || units[i].equals("1")) {
          continue;
        }
        context.getAllUnits().add(FormatUtil.format(units[i]));
      }
    }
    Matcher matcher = null;
    // 识别文书类型
    for (Map.Entry<Pattern, DocumentType> entry : docClassifyMap.entrySet()) {
      matcher = entry.getKey().matcher(this.context.document);
      if (matcher.find()) {
        this.context.docInfo.setDocType(entry.getValue());
        break;
      }
    }
    // 从配置文件读取每个标签的匹配规则
    XmlParser.parser(context.docInfo.getLabelPatterns(), context.docInfo.getDocType());
  }

}
