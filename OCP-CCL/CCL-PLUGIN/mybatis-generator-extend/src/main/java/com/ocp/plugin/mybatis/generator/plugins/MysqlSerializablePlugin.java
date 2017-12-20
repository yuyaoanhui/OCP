package com.ocp.plugin.mybatis.generator.plugins;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerClass;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 扩展一个插件，用于在生成的实体类以及*Example类序列化
 * 
 * @author yu.yao
 *
 */
public class MysqlSerializablePlugin extends PluginAdapter {

  private FullyQualifiedJavaType serializable;

  public MysqlSerializablePlugin() {
    super();
    serializable = new FullyQualifiedJavaType("java.io.Serializable");
  }

  /**
   * 添加给model类序列化的方法
   */
  @Override
  public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    StringBuilder sb = new StringBuilder();
    topLevelClass.addJavaDocLine("/** ");
    sb.append(" * 用于操作表:");
    sb.append(introspectedTable.getFullyQualifiedTable());
    topLevelClass.addJavaDocLine(sb.toString());
    topLevelClass.addJavaDocLine(" * 功能描述:" + introspectedTable.getRemarks());
    topLevelClass.addJavaDocLine(" */");
    makeSerializable(topLevelClass, introspectedTable);
    return true;
  }

  /**
   * 添加给Example类序列化的方法
   */
  @Override
  public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    StringBuilder sb = new StringBuilder();
    topLevelClass.addJavaDocLine("/** ");
    sb.append(" * 用于操作表:");
    sb.append(introspectedTable.getFullyQualifiedTable());
    topLevelClass.addJavaDocLine(sb.toString());
    topLevelClass.addJavaDocLine(" */");

    makeSerializable(topLevelClass, introspectedTable);

    for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
      if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
        innerClass.addSuperInterface(serializable);
        addField(innerClass, introspectedTable);
      }
      if ("Criteria".equals(innerClass.getType().getShortName())) {
        addField(innerClass, introspectedTable);
      }
      if ("Criterion".equals(innerClass.getType().getShortName())) {
        innerClass.addSuperInterface(serializable);
        addField(innerClass, introspectedTable);
      }
    }

    return true;
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;// 不验证propertes文件内容
  }

  private void makeSerializable(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
    topLevelClass.addImportedType(serializable);
    topLevelClass.addSuperInterface(serializable);

    addField(topLevelClass, introspectedTable);
  }

  private void addField(InnerClass innerClass, IntrospectedTable introspectedTable) {
    Field field = new Field();
    field.setFinal(true);
    field.setInitializationString("1L");
    field.setName("serialVersionUID");
    field.setStatic(true);
    field.setType(new FullyQualifiedJavaType("long"));
    field.setVisibility(JavaVisibility.PRIVATE);
    context.getCommentGenerator().addFieldComment(field, introspectedTable);

    innerClass.addField(field);
  }

}
