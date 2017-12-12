package com.futuredata.judicature.plugin.mybatis.generator.extend;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import com.futuredata.judicature.plugin.mybatis.generator.utils.JavaElementGeneratorTools;

public class MysqlBatchInsertPlugin extends PluginAdapter {

  public static final String METHOD_BATCH_INSERT = "batchInsert"; // 方法名
  public static final String METHOD_BATCH_INSERT_SELECTIVE = "batchInsertSelective"; // 方法名

  /**
   * Java Client Methods 生成
   */
  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    // 1. batchInsert
    FullyQualifiedJavaType listType = FullyQualifiedJavaType.getNewListInstance();
    listType.addTypeArgument(introspectedTable.getRules().calculateAllFieldsClass());
    Method mBatchInsert = JavaElementGeneratorTools.generateMethod(METHOD_BATCH_INSERT,
        JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(),
        new Parameter(listType, "list", "@Param(\"list\")")

    );
    // interface 增加方法
    interfaze.addMethod(mBatchInsert);
    // 2. batchInsertSelective
    FullyQualifiedJavaType selectiveType = new FullyQualifiedJavaType(
        introspectedTable.getRules().calculateAllFieldsClass().getShortName() + "."
    /* + ModelColumnPlugin.ENUM_NAME */);// FIXME
    Method mBatchInsertSelective =
        JavaElementGeneratorTools.generateMethod(METHOD_BATCH_INSERT_SELECTIVE,
            JavaVisibility.DEFAULT, FullyQualifiedJavaType.getIntInstance(),
            new Parameter(listType, "list", "@Param(\"list\")"),
            new Parameter(selectiveType, "selective", "@Param(\"selective\")", true));
    // interface 增加方法
    interfaze.addMethod(mBatchInsertSelective);
    return true;
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

}
