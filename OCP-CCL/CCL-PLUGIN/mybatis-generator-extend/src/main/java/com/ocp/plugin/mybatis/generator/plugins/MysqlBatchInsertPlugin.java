package com.ocp.plugin.mybatis.generator.plugins;

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
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import com.ocp.plugin.mybatis.generator.utils.BasePlugin;
import com.ocp.plugin.mybatis.generator.utils.JavaElementGeneratorTools;
import com.ocp.plugin.mybatis.generator.utils.XmlElementGeneratorTools;

public class MysqlBatchInsertPlugin extends BasePlugin {

  public static final String METHOD_BATCH_INSERT = "insertBatch"; // 方法名
  public static final String METHOD_BATCH_INSERT_SELECTIVE = "insertSelectiveBatch"; // 方法名

  /**
   * Java Client Methods 生成
   */
  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    // insertBatch
    FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
    listType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());
    Method mBatchInsert = JavaElementGeneratorTools.generateMethod(METHOD_BATCH_INSERT,
        JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(),
        new Parameter(listType, "list", "@Param(\"list\")"));
    mBatchInsert.addJavaDocLine("/**");
    mBatchInsert.addJavaDocLine(" *");
    mBatchInsert.addJavaDocLine(" * @mbg.generated");
    mBatchInsert.addJavaDocLine(" */");
    // interface 增加方法
    interfaze.addMethod(mBatchInsert);
    // insertSelectiveBatch
    Method mBatchInsertSelective =
        JavaElementGeneratorTools.generateMethod(METHOD_BATCH_INSERT_SELECTIVE,
            JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(),
            new Parameter(listType, "list", "@Param(\"list\")"));
    mBatchInsertSelective.addJavaDocLine("/**");
    mBatchInsertSelective.addJavaDocLine(" *");
    mBatchInsertSelective.addJavaDocLine(" * @mbg.generated");
    mBatchInsertSelective.addJavaDocLine(" */");
    // interface 增加方法
    interfaze.addMethod(mBatchInsertSelective);
    return true;
  }

  @Override
  public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
    // 1. batchInsert
    XmlElement batchInsertEle = new XmlElement("insert");
    batchInsertEle.addAttribute(new Attribute("id", METHOD_BATCH_INSERT));
    // 参数类型
    batchInsertEle.addAttribute(new Attribute("parameterType", "map"));
    // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
    // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
    commentGenerator.addComment(batchInsertEle);
    // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
    XmlElementGeneratorTools.useGeneratedKeys(batchInsertEle, introspectedTable);
    batchInsertEle.addElement(
        new TextElement("insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime()));
    for (Element element : XmlElementGeneratorTools.generateKeys(
        ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()))) {
      batchInsertEle.addElement(element);
    }
    // 添加foreach节点
    XmlElement foreachElement = new XmlElement("foreach");
    foreachElement.addAttribute(new Attribute("collection", "list"));
    foreachElement.addAttribute(new Attribute("item", "item"));
    foreachElement.addAttribute(new Attribute("separator", ","));
    for (Element element : XmlElementGeneratorTools.generateValues(
        ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns()),
        "item.")) {
      foreachElement.addElement(element);
    }
    // values 构建
    batchInsertEle.addElement(new TextElement("values"));
    batchInsertEle.addElement(foreachElement);
    if (context.getPlugins().sqlMapInsertElementGenerated(batchInsertEle, introspectedTable)) {
      document.getRootElement().addElement(batchInsertEle);
    }
    // 2. batchInsertSelective
    XmlElement element = new XmlElement("insert");
    element.addAttribute(new Attribute("id", METHOD_BATCH_INSERT_SELECTIVE));
    // 参数类型
    element.addAttribute(new Attribute("parameterType", "map"));
    // 添加注释(!!!必须添加注释，overwrite覆盖生成时，@see
    // XmlFileMergerJaxp.isGeneratedNode会去判断注释中是否存在OLD_ELEMENT_TAGS中的一点，例子：@mbg.generated)
    commentGenerator.addComment(element);
    // 使用JDBC的getGenereatedKeys方法获取主键并赋值到keyProperty设置的领域模型属性中。所以只支持MYSQL和SQLServer
    XmlElementGeneratorTools.useGeneratedKeys(element, introspectedTable);
    element.addElement(new TextElement(
        "insert into " + introspectedTable.getFullyQualifiedTableNameAtRuntime() + " ("));
    XmlElement foreachInsertColumns = new XmlElement("foreach");
    foreachInsertColumns.addAttribute(new Attribute("collection", "selective"));
    foreachInsertColumns.addAttribute(new Attribute("item", "column"));
    foreachInsertColumns.addAttribute(new Attribute("separator", ","));
    foreachInsertColumns.addElement(new TextElement("${column.value}"));
    element.addElement(foreachInsertColumns);
    element.addElement(new TextElement(")"));
    // values
    element.addElement(new TextElement("values"));
    // foreach values
    XmlElement foreachValues = new XmlElement("foreach");
    foreachValues.addAttribute(new Attribute("collection", "list"));
    foreachValues.addAttribute(new Attribute("item", "item"));
    foreachValues.addAttribute(new Attribute("separator", ","));
    foreachValues.addElement(new TextElement("("));
    // foreach 所有插入的列，比较是否存在
    XmlElement foreachInsertColumnsCheck = new XmlElement("foreach");
    foreachInsertColumnsCheck.addAttribute(new Attribute("collection", "selective"));
    foreachInsertColumnsCheck.addAttribute(new Attribute("item", "column"));
    foreachInsertColumnsCheck.addAttribute(new Attribute("separator", ","));
    // 所有表字段
    List<IntrospectedColumn> columns =
        ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
    List<IntrospectedColumn> columns1 =
        ListUtilities.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
    for (int i = 0; i < columns1.size(); i++) {
      IntrospectedColumn introspectedColumn = columns.get(i);
      XmlElement check = new XmlElement("if");
      check.addAttribute(new Attribute("test",
          "'" + introspectedColumn.getActualColumnName() + "' == column.value"));
      check.addElement(new TextElement(
          MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, "item.")));
      foreachInsertColumnsCheck.addElement(check);
    }
    foreachValues.addElement(foreachInsertColumnsCheck);
    foreachValues.addElement(new TextElement(")"));
    element.addElement(foreachValues);
    if (context.getPlugins().sqlMapInsertElementGenerated(element, introspectedTable)) {
      document.getRootElement().addElement(element);
    }
    return true;
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

}
