package com.futuredata.judicature.plugin.mybatis.generator.utils.enhanced;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.internal.util.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 模板注释生成工具
 * 
 * @author: yu.yao
 */
public class TemplateCommentGenerator implements CommentGenerator {

  private static final Logger logger = LoggerFactory.getLogger(TemplateCommentGenerator.class);// 日志

  private Map<EnumNode, Template> templates = new HashMap<>();// 模板

  /**
   * 构造函数
   * 
   * @param templatePath 模板路径
   * @param useForDefault 未使用Comment插件，用作默认注释生成器
   */
  public TemplateCommentGenerator(String templatePath, boolean useForDefault) {
    try {
      Document doc = null;
      if (useForDefault) {
        InputStream inputStream =
            this.getClass().getClassLoader().getResourceAsStream(templatePath);
        doc = new SAXReader().read(inputStream);
        inputStream.close();
      } else {
        File file = new File(templatePath);
        if (file.exists()) {
          doc = new SAXReader().read(file);
        } else {
          logger.error("没有找到对应注释模板:" + templatePath);
        }
      }
      // 遍历comment 节点
      if (doc != null) {
        for (EnumNode node : EnumNode.values()) {
          Element element = doc.getRootElement().elementByID(node.value());
          if (element != null) {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
            // 字符串清理
            Template template = new Template(node.value(), element.getText(), cfg);
            templates.put(node, template);
          }
        }
      }
    } catch (Exception e) {
      logger.error("注释模板XML解析失败！", e);
    }
  }

  /**
   * 获取评论
   * 
   * @param map 模板参数
   * @param node 节点ID
   * @return
   */
  private String[] getComments(Map<String, Object> map, EnumNode node) {
    // 1. 模板引擎解析
    try {
      StringWriter stringWriter = new StringWriter();
      Template template = templates.get(node);
      if (template != null) {
        template.process(map, stringWriter);

        String comment = stringWriter.toString();
        stringWriter.close();
        // 需要先清理字符串
        return comment.replaceFirst("^[\\s\\t\\r\\n]*", "").replaceFirst("[\\s\\t\\r\\n]*$", "")
            .split("\n");
      }
    } catch (Exception e) {
      logger.error("freemarker 解析失败！", e);
    }
    return null;
  }

  /**
   * 添加评论
   *
   * @param javaElement
   * @param map
   * @param node
   */
  private void addJavaElementComment(JavaElement javaElement, Map<String, Object> map,
      EnumNode node) {
    // 获取评论
    String[] comments = getComments(map, node);
    if (comments != null) {
      // 去除空评论
      if (comments.length == 1 && !StringUtility.stringHasValue(comments[0])) {
        return;
      }
      // 添加评论
      for (String comment : comments) {
        javaElement.addJavaDocLine(comment);
      }
    }
  }

  /**
   * 添加评论
   *
   * @param compilationUnit
   * @param map
   * @param node
   */
  private void addCompilationUnitComment(CompilationUnit compilationUnit, Map<String, Object> map,
      EnumNode node) {
    // 获取评论
    String[] comments = getComments(map, node);
    if (comments != null) {
      // 去除空评论
      if (comments.length == 1 && !StringUtility.stringHasValue(comments[0])) {
        return;
      }
      // 添加评论
      for (String comment : comments) {
        compilationUnit.addFileCommentLine(comment);
      }
    }
  }

  /**
   * 添加XML注释
   *
   * @param xmlElement
   * @param map
   * @param node
   */
  private void addXmlElementComment(XmlElement xmlElement, Map<String, Object> map, EnumNode node) {
    // 获取评论
    String[] comments = getComments(map, node);
    if (comments != null) {
      // 去除空评论
      if (comments.length == 1 && !StringUtility.stringHasValue(comments[0])) {
        return;
      }
      // 添加评论
      for (String comment : comments) {
        xmlElement.addElement(new TextElement(comment));
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addFieldComment(Field field, IntrospectedTable introspectedTable,
      IntrospectedColumn introspectedColumn) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("field", field);
    map.put("introspectedTable", introspectedTable);
    map.put("introspectedColumn", introspectedColumn);

    // 添加评论
    addJavaElementComment(field, map, EnumNode.ADD_FIELD_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("field", field);
    map.put("introspectedTable", introspectedTable);

    // 添加评论
    addJavaElementComment(field, map, EnumNode.ADD_FIELD_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addModelClassComment(TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("topLevelClass", topLevelClass);
    map.put("introspectedTable", introspectedTable);
    // 添加java元素注释
    addJavaElementComment(topLevelClass, map, EnumNode.ADD_MODEL_CLASS_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
    if (innerClass instanceof InnerInterfaceWrapperToInnerClass) {
      InnerInterface innerInterface =
          ((InnerInterfaceWrapperToInnerClass) innerClass).getInnerInterface();

      Map<String, Object> map = new HashMap<>();
      map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
      map.put("innerInterface", innerInterface);
      map.put("introspectedTable", introspectedTable);

      // 添加评论
      addJavaElementComment(innerInterface, map, EnumNode.ADD_INTERFACE_COMMENT);
    } else {
      Map<String, Object> map = new HashMap<>();
      map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
      map.put("innerClass", innerClass);
      map.put("introspectedTable", introspectedTable);

      // 添加评论
      addJavaElementComment(innerClass, map, EnumNode.ADD_CLASS_COMMENT);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable,
      boolean markAsDoNotDelete) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("innerClass", innerClass);
    map.put("introspectedTable", introspectedTable);
    map.put("markAsDoNotDelete", markAsDoNotDelete);
    // 添加java元素注释
    addJavaElementComment(innerClass, map, EnumNode.ADD_CLASS_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("innerEnum", innerEnum);
    map.put("introspectedTable", introspectedTable);
    // 添加java元素注释
    addJavaElementComment(innerEnum, map, EnumNode.ADD_ENUM_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addGetterComment(Method method, IntrospectedTable introspectedTable,
      IntrospectedColumn introspectedColumn) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("method", method);
    map.put("introspectedTable", introspectedTable);
    map.put("introspectedColumn", introspectedColumn);
    // 添加评论
    addJavaElementComment(method, map, EnumNode.ADD_GETTER_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addSetterComment(Method method, IntrospectedTable introspectedTable,
      IntrospectedColumn introspectedColumn) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("method", method);
    map.put("introspectedTable", introspectedTable);
    map.put("introspectedColumn", introspectedColumn);
    // 添加评论
    addJavaElementComment(method, map, EnumNode.ADD_SETTER_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("method", method);
    map.put("introspectedTable", introspectedTable);
    // 添加评论
    addJavaElementComment(method, map, EnumNode.ADD_GENERAL_METHOD_COMMENT);
  }

  /**
   * /** {@inheritDoc}
   */
  @Override
  public void addJavaFileComment(CompilationUnit compilationUnit) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("compilationUnit", compilationUnit);
    // 添加评论
    addCompilationUnitComment(compilationUnit, map, EnumNode.ADD_JAVA_FILE_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addComment(XmlElement xmlElement) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("xmlElement", xmlElement);
    // 添加评论
    addXmlElementComment(xmlElement, map, EnumNode.ADD_COMMENT);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addRootComment(XmlElement rootElement) {
    Map<String, Object> map = new HashMap<>();
    map.put("mgb", MergeConstants.NEW_ELEMENT_TAG);
    map.put("rootElement", rootElement);
    // 添加评论
    addXmlElementComment(rootElement, map, EnumNode.ADD_ROOT_COMMENT);
  }

  /**
   * 评论模板节点ID
   */
  public static enum EnumNode {
    ADD_COMMENT("addComment"), // Xml 节点注释
    ADD_ROOT_COMMENT("addRootComment"), // xml root 节点注释
    ADD_JAVA_FILE_COMMENT("addJavaFileComment"), // java 文件注释
    ADD_GENERAL_METHOD_COMMENT("addGeneralMethodComment"), // java 方法注释
    ADD_SETTER_COMMENT("addSetterComment"), // setter 方法注释
    ADD_GETTER_COMMENT("addGetterComment"), // getter 方式注释
    ADD_ENUM_COMMENT("addEnumComment"), // 枚举 注释
    ADD_CLASS_COMMENT("addClassComment"), // 类 注释
    ADD_INTERFACE_COMMENT("addInterfaceComment"), // 接口 注释
    ADD_MODEL_CLASS_COMMENT("addModelClassComment"), // model 类注释
    ADD_FIELD_COMMENT("addFieldComment"); // 字段 注释

    private final String value; // 值

    /**
     * 构造方法
     * 
     * @param value
     */
    EnumNode(String value) {
      this.value = value;
    }

    /**
     * 值
     *
     * @return
     */
    public String value() {
      return value;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addConfigurationProperties(Properties properties) {

  }
}
