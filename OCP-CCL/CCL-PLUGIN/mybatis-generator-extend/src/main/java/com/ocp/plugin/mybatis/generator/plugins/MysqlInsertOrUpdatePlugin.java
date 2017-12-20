package com.ocp.plugin.mybatis.generator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.Element;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;

import com.ocp.plugin.mybatis.generator.utils.BasePlugin;
import com.ocp.plugin.mybatis.generator.utils.IncrementsPluginTools;
import com.ocp.plugin.mybatis.generator.utils.IntrospectedTableTools;
import com.ocp.plugin.mybatis.generator.utils.JavaElementGeneratorTools;
import com.ocp.plugin.mybatis.generator.utils.XmlElementGeneratorTools;

/**
 * 存在即更新插件
 * 
 * @author: yu.yao
 */
public class MysqlInsertOrUpdatePlugin extends BasePlugin {
  public static final String METHOD_UPSERT = "upsert"; // 方法名
  public static final String METHOD_UPSERT_WITH_BLOBS = "upsertWithBLOBs"; // 方法名
  public static final String METHOD_UPSERT_SELECTIVE = "upsertSelective"; // 方法名

  public static final String METHOD_UPSERT_BY_EXAMPLE = "upsertByExample"; // 方法名
  public static final String METHOD_UPSERT_BY_EXAMPLE_WITH_BLOBS = "upsertByExampleWithBLOBs"; // 方法名
  public static final String METHOD_UPSERT_BY_EXAMPLE_SELECTIVE = "upsertByExampleSelective"; // 方法名

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

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
    // ====================================== upsert ======================================
    Method mUpsert = JavaElementGeneratorTools.generateMethod(METHOD_UPSERT, JavaVisibility.DEFAULT,
        FullyQualifiedJavaType.getIntInstance(), new Parameter(
            JavaElementGeneratorTools.getModelTypeWithoutBLOBs(introspectedTable), "record"));
    commentGenerator.addGeneralMethodComment(mUpsert, introspectedTable);
    // interface 增加方法
    interfaze.addMethod(mUpsert);
    // ===================== upsertWithBLOBs ============================
    // !!! 注意这里的行为不以有没有生成Model 的 WithBLOBs类为基准
    if (introspectedTable.hasBLOBColumns()) {
      Method mUpsertWithBLOBs = JavaElementGeneratorTools.generateMethod(METHOD_UPSERT_WITH_BLOBS,
          JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(), new Parameter(
              JavaElementGeneratorTools.getModelTypeWithBLOBs(introspectedTable), "record"));
      commentGenerator.addGeneralMethodComment(mUpsertWithBLOBs, introspectedTable);
      // interface 增加方法
      interfaze.addMethod(mUpsertWithBLOBs);
    }
    // ============================= upsertSelective ===============================
    Method mUpsertSelective = JavaElementGeneratorTools.generateMethod(METHOD_UPSERT_SELECTIVE,
        JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(),
        new Parameter(introspectedTable.getRules().calculateAllFieldsClass(), "record"));
    commentGenerator.addGeneralMethodComment(mUpsertSelective, introspectedTable);
    // interface 增加方法
    interfaze.addMethod(mUpsertSelective);
    return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
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
    this.generateXmlElementWithoutBLOBs(document, introspectedTable);
    this.generateXmlElementWithSelective(document, introspectedTable);
    this.generateXmlElementWithBLOBs(document, introspectedTable);
    return super.sqlMapDocumentGenerated(document, introspectedTable);
  }

  /**
   * 当Selective情况
   * 
   * @param document
   * @param introspectedTable
   */
  private void generateXmlElementWithSelective(Document document,
      IntrospectedTable introspectedTable) {
    List<IntrospectedColumn> columns =
        ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getAllColumns());
    // ========================== upsertSelective ===========================
    XmlElement eleUpsertSelective = new XmlElement("insert");
    eleUpsertSelective.addAttribute(new Attribute("id", METHOD_UPSERT_SELECTIVE));
    // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
    // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
    commentGenerator.addComment(eleUpsertSelective);
    // 参数类型
    eleUpsertSelective.addAttribute(new Attribute("parameterType",
        introspectedTable.getRules().calculateAllFieldsClass().getFullyQualifiedName()));
    // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
    XmlElementGeneratorTools.useGeneratedKeys(eleUpsertSelective, introspectedTable);
    // insert
    eleUpsertSelective.addElement(
        new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
    eleUpsertSelective.addElement(XmlElementGeneratorTools.generateKeysSelective(columns));
    eleUpsertSelective.addElement(new TextElement("values"));
    eleUpsertSelective.addElement(XmlElementGeneratorTools.generateValuesSelective(columns));
    eleUpsertSelective.addElement(new TextElement("on duplicate key update "));
    // set 操作增加增量插件支持
    this.incrementsSelectiveSupport(eleUpsertSelective,
        XmlElementGeneratorTools.generateSetsSelective(columns, null, false), introspectedTable,
        false);
    document.getRootElement().addElement(eleUpsertSelective);
  }

  /**
   * 当Model有生成WithBLOBs类时的情况
   * 
   * @param document
   * @param introspectedTable
   */
  private void generateXmlElementWithBLOBs(Document document, IntrospectedTable introspectedTable) {
    if (introspectedTable.hasBLOBColumns()) {
      List<IntrospectedColumn> columns =
          ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getAllColumns());
      // =========================== upsertWithBLOBs =======================
      XmlElement eleUpsertWithBLOBs = new XmlElement("insert");
      eleUpsertWithBLOBs.addAttribute(new Attribute("id", METHOD_UPSERT_WITH_BLOBS));
      // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
      // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
      commentGenerator.addComment(eleUpsertWithBLOBs);
      // 参数类型
      eleUpsertWithBLOBs.addAttribute(new Attribute("parameterType", JavaElementGeneratorTools
          .getModelTypeWithBLOBs(introspectedTable).getFullyQualifiedName()));
      // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
      XmlElementGeneratorTools.useGeneratedKeys(eleUpsertWithBLOBs, introspectedTable);
      // insert
      eleUpsertWithBLOBs.addElement(new TextElement(
          "insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
      for (Element element : XmlElementGeneratorTools.generateKeys(columns)) {
        eleUpsertWithBLOBs.addElement(element);
      }
      eleUpsertWithBLOBs.addElement(new TextElement("values"));
      for (Element element : XmlElementGeneratorTools.generateValues(columns)) {
        eleUpsertWithBLOBs.addElement(element);
      }
      eleUpsertWithBLOBs.addElement(new TextElement("on duplicate key update "));
      // set 操作增加增量插件支持
      this.incrementsSupport(eleUpsertWithBLOBs, XmlElementGeneratorTools.generateSets(columns),
          introspectedTable, false);
      document.getRootElement().addElement(eleUpsertWithBLOBs);
    }
  }

  /**
   * 当Model没有生成WithBLOBs类时的情况
   * 
   * @param document
   * @param introspectedTable
   */
  private void generateXmlElementWithoutBLOBs(Document document,
      IntrospectedTable introspectedTable) {
    List<IntrospectedColumn> columns =
        ListUtilities.removeGeneratedAlwaysColumns(introspectedTable.getNonBLOBColumns());
    // ====================================== upsert ======================================
    XmlElement eleUpsert = new XmlElement("insert");
    eleUpsert.addAttribute(new Attribute("id", METHOD_UPSERT));
    // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
    // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
    commentGenerator.addComment(eleUpsert);
    // 参数类型
    eleUpsert.addAttribute(new Attribute("parameterType", JavaElementGeneratorTools
        .getModelTypeWithoutBLOBs(introspectedTable).getFullyQualifiedName()));
    // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
    XmlElementGeneratorTools.useGeneratedKeys(eleUpsert, introspectedTable);
    // insert
    eleUpsert.addElement(
        new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
    for (Element element : XmlElementGeneratorTools.generateKeys(columns)) {
      eleUpsert.addElement(element);
    }
    eleUpsert.addElement(new TextElement("values"));
    for (Element element : XmlElementGeneratorTools.generateValues(columns)) {
      eleUpsert.addElement(element);
    }
    eleUpsert.addElement(new TextElement("on duplicate key update "));
    // set 操作增加增量插件支持
    this.incrementsSupport(eleUpsert, XmlElementGeneratorTools.generateSets(columns),
        introspectedTable, false);
    document.getRootElement().addElement(eleUpsert);
  }

  /**
   * 增量操作支持
   * 
   * @param xmlElement
   * @param trimXmlElement
   * @param introspectedTable
   * @param hasPrefix
   */
  private void incrementsSelectiveSupport(XmlElement xmlElement, XmlElement trimXmlElement,
      IntrospectedTable introspectedTable, boolean hasPrefix) {
    IncrementsPluginTools incTools =
        IncrementsPluginTools.getTools(context, introspectedTable, warnings);
    if (incTools.support()) {
      List<Element> ifs = new ArrayList<>();
      // 获取if节点
      for (Element element : trimXmlElement.getElements()) {
        String text = ((TextElement) (((XmlElement) element).getElements().get(0))).getContent();
        String columnName = text.split("=")[0];
        IntrospectedColumn introspectedColumn =
            IntrospectedTableTools.safeGetColumn(introspectedTable, columnName);
        if (incTools.supportColumn(introspectedColumn)) {
          // if 节点数据替换
          ((XmlElement) element).getElements().clear();
          ((XmlElement) element).getElements()
              .addAll(incTools.generatedIncrementsElement(introspectedColumn, hasPrefix, true));
          continue;
        }
        ifs.add(element);
      }
    }
    xmlElement.addElement(trimXmlElement);
  }

  /**
   * 增量操作支持
   * 
   * @param xmlElement
   * @param elements
   * @param introspectedTable
   * @param hasPrefix
   */
  private void incrementsSupport(XmlElement xmlElement, List<TextElement> elements,
      IntrospectedTable introspectedTable, boolean hasPrefix) {
    IncrementsPluginTools incTools =
        IncrementsPluginTools.getTools(context, introspectedTable, warnings);
    for (TextElement element : elements) {
      if (incTools.support()) {
        // 获取column
        String text = element.getContent().trim();
        String columnName = text.split("=")[0];
        IntrospectedColumn introspectedColumn =
            IntrospectedTableTools.safeGetColumn(introspectedTable, columnName);
        if (incTools.supportColumn(introspectedColumn)) {
          xmlElement.getElements().addAll(incTools.generatedIncrementsElement(introspectedColumn,
              hasPrefix, text.endsWith(",")));
          continue;
        }
      }
      xmlElement.addElement(element);
    }
  }
}
