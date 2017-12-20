package com.ocp.plugin.mybatis.generator.extend;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.internal.DefaultCommentGenerator;

/**
 * 
 * @author yu.yao
 *
 */
public class MybaitsCommentGeneratorExtend extends DefaultCommentGenerator {

  @Override
  public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
    method.addJavaDocLine("/**");
    addJavadocTag(method, false);
    method.addJavaDocLine(" */");
  }

  @Override
  public void addFieldComment(Field field, IntrospectedTable introspectedTable,
      IntrospectedColumn introspectedColumn) {
    if (introspectedColumn.getRemarks() != null && !introspectedColumn.getRemarks().equals("")) {
      field.addJavaDocLine("/**");
      field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
      addJavadocTag(field, false);
      field.addJavaDocLine(" */");
    }
  }
}
