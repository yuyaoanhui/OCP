package com.ocp.rabbit.repository.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;

/**
 * XML文件内容解析器——dom4j解析
 * 
 * @author yu.yao 2018年8月9日
 *
 */
public class XmlParser {
  private static final Logger logger = LoggerFactory.getLogger(XmlParser.class);

  private static Map<String, Document> tmpXML = new HashMap<String, Document>();// <filePath,
                                                                                // Document>

  public static void parser(Map<ParaLabelEnum, List<Pattern>> map, DocumentType type) {
    SAXReader reader = new SAXReader();
    try {
      String[] paths =
          PropertiesUtil.getProperty("application.properties", "script.path").split(",");
      String prefix = "";
      for (String path : paths) {
        if (path.contains(type.name()) && path.contains("basic")) {
          prefix = path;
          break;
        }
      }
      String filePath = prefix + "structure.xml";
      Document document = null;
      if (!tmpXML.containsKey(filePath)) {
        document = reader.read(XmlParser.class.getResourceAsStream(filePath));
        tmpXML.put(filePath, document);
      }
      document = tmpXML.get(filePath);
      Element paraLabel = document.getRootElement();
      Iterator<?> it = paraLabel.elementIterator();
      while (it.hasNext()) {
        Element label = (Element) it.next();
        List<Pattern> list = new ArrayList<Pattern>();
        for (String regex : label.getText().trim().split("#")) {
          list.add(Pattern.compile(regex));
        }
        map.put(ParaLabelEnum.valueOf(label.getName()), list);
      }
    } catch (DocumentException e) {
      logger.error("XML文件解析失败", e);
      throw new RuntimeException("XML文件解析失败", e);
    }
  }

}
