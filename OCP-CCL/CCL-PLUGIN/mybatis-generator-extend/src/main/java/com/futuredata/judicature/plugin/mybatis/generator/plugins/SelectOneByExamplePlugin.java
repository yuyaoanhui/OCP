package com.futuredata.judicature.plugin.mybatis.generator.plugins;

import com.futuredata.judicature.plugin.mybatis.generator.utils.BasePlugin;
import com.futuredata.judicature.plugin.mybatis.generator.utils.FormatTools;
import com.futuredata.judicature.plugin.mybatis.generator.utils.JavaElementGeneratorTools;
import com.futuredata.judicature.plugin.mybatis.generator.utils.XmlElementGeneratorTools;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * 增加查询一条数据方法
 * 
 * @author: yu.yao
 */
public class SelectOneByExamplePlugin extends BasePlugin {
  public static final String METHOD_SELECT_ONE_BY_EXAMPLE = "selectOneByExample"; // 方法名
  public static final String METHOD_SELECT_ONE_BY_EXAMPLE_WITH_BLOBS =
      "selectOneByExampleWithBLOBs"; // 方法名

  /**
   * Java Client Methods 生成 具体执行顺序 http://www.mybatis.org/generator/reference/pluggingIn.html
   * 
   * @param interfaze
   * @param topLevelClass
   * @param introspectedTable
   * @return
   */
  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    // 方法生成 selectOneByExample
    Method method = JavaElementGeneratorTools.generateMethod(METHOD_SELECT_ONE_BY_EXAMPLE,
        JavaVisibility.DEFAULT,
        JavaElementGeneratorTools.getModelTypeWithoutBLOBs(introspectedTable),
        new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
    commentGenerator.addGeneralMethodComment(method, introspectedTable);

    // interface 增加方法
    FormatTools.addMethodWithBestPosition(interfaze, method);
    logger
        .debug("mybatis(查询单条数据插件):" + interfaze.getType().getShortName() + "增加selectOneByExample方法。");

    // 方法生成 selectOneByExampleWithBLOBs !!! 注意这里的行为不以有没有生成Model 的 WithBLOBs类为基准
    if (introspectedTable.hasBLOBColumns()) {
      // 方法生成 selectOneByExample
      Method method1 = JavaElementGeneratorTools.generateMethod(
          METHOD_SELECT_ONE_BY_EXAMPLE_WITH_BLOBS, JavaVisibility.DEFAULT,
          JavaElementGeneratorTools.getModelTypeWithBLOBs(introspectedTable),
          new Parameter(new FullyQualifiedJavaType(introspectedTable.getExampleType()), "example"));
      commentGenerator.addGeneralMethodComment(method1, introspectedTable);

      // interface 增加方法
      FormatTools.addMethodWithBestPosition(interfaze, method1);
      logger.debug("mybatis(查询单条数据插件):" + interfaze.getType().getShortName()
          + "增加selectOneByExampleWithBLOBs方法。");
    }

    return true;
  }

  /**
   * SQL Map Methods 生成 具体执行顺序 http://www.mybatis.org/generator/reference/pluggingIn.html
   * 
   * @param document
   * @param introspectedTable
   * @return
   */
  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    // ------------------------------------ selectOneByExample ----------------------------------
    // 生成查询语句
    XmlElement selectOneElement = new XmlElement("select");
    // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
    // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
    commentGenerator.addComment(selectOneElement);

    // 添加ID
    selectOneElement.addAttribute(new Attribute("id", METHOD_SELECT_ONE_BY_EXAMPLE));
    // 添加返回类型
    selectOneElement
        .addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));
    // 添加参数类型
    selectOneElement
        .addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
    selectOneElement.addElement(new TextElement("select"));

    StringBuilder sb = new StringBuilder();
    if (stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
      sb.append('\'');
      sb.append(introspectedTable.getSelectByExampleQueryId());
      sb.append("' as QUERYID,");
      selectOneElement.addElement(new TextElement(sb.toString()));
    }
    selectOneElement
        .addElement(XmlElementGeneratorTools.getBaseColumnListElement(introspectedTable));

    sb.setLength(0);
    sb.append("from ");
    sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
    selectOneElement.addElement(new TextElement(sb.toString()));
    selectOneElement
        .addElement(XmlElementGeneratorTools.getExampleIncludeElement(introspectedTable));

    XmlElement ifElement = new XmlElement("if");
    ifElement.addAttribute(new Attribute("test", "orderByClause != null")); //$NON-NLS-2$
    ifElement.addElement(new TextElement("order by ${orderByClause}"));
    selectOneElement.addElement(ifElement);

    // 只查询一条
    selectOneElement.addElement(new TextElement("limit 1"));
    // 添加到根节点
    FormatTools.addElementWithBestPosition(document.getRootElement(), selectOneElement);
    logger.debug("mybatis(查询单条数据插件):" + introspectedTable.getMyBatis3XmlMapperFileName()
        + "增加selectOneByExample方法。");

    // ------------------------------------ selectOneByExampleWithBLOBs
    // ----------------------------------
    // !!! 注意这里的行为不以有没有生成Model 的 WithBLOBs类为基准
    if (introspectedTable.hasBLOBColumns()) {
      // 生成查询语句
      XmlElement selectOneWithBLOBsElement = new XmlElement("select");
      // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
      // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
      commentGenerator.addComment(selectOneWithBLOBsElement);

      // 添加ID
      selectOneWithBLOBsElement
          .addAttribute(new Attribute("id", METHOD_SELECT_ONE_BY_EXAMPLE_WITH_BLOBS));
      // 添加返回类型
      selectOneWithBLOBsElement
          .addAttribute(new Attribute("resultMap", introspectedTable.getResultMapWithBLOBsId()));
      // 添加参数类型
      selectOneWithBLOBsElement
          .addAttribute(new Attribute("parameterType", introspectedTable.getExampleType()));
      // 添加查询SQL
      selectOneWithBLOBsElement.addElement(new TextElement("select"));

      sb.setLength(0);
      if (stringHasValue(introspectedTable.getSelectByExampleQueryId())) {
        sb.append('\'');
        sb.append(introspectedTable.getSelectByExampleQueryId());
        sb.append("' as QUERYID,");
        selectOneWithBLOBsElement.addElement(new TextElement(sb.toString()));
      }

      selectOneWithBLOBsElement
          .addElement(XmlElementGeneratorTools.getBaseColumnListElement(introspectedTable));
      selectOneWithBLOBsElement.addElement(new TextElement(","));
      selectOneWithBLOBsElement
          .addElement(XmlElementGeneratorTools.getBlobColumnListElement(introspectedTable));

      sb.setLength(0);
      sb.append("from ");
      sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
      selectOneWithBLOBsElement.addElement(new TextElement(sb.toString()));
      selectOneWithBLOBsElement
          .addElement(XmlElementGeneratorTools.getExampleIncludeElement(introspectedTable));

      XmlElement ifElement1 = new XmlElement("if");
      ifElement1.addAttribute(new Attribute("test", "orderByClause != null")); //$NON-NLS-2$
      ifElement1.addElement(new TextElement("order by ${orderByClause}"));
      selectOneWithBLOBsElement.addElement(ifElement1);

      // 只查询一条
      selectOneWithBLOBsElement.addElement(new TextElement("limit 1"));

      // 添加到根节点
      FormatTools.addElementWithBestPosition(document.getRootElement(), selectOneWithBLOBsElement);
      logger.debug("mybatis(查询单条数据插件):" + introspectedTable.getMyBatis3XmlMapperFileName()
          + "增加selectOneByExampleWithBLOBs方法。");
    }


    return true;
  }
}
