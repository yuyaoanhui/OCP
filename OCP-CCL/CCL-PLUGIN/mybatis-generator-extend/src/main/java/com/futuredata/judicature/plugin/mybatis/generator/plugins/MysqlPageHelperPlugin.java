package com.futuredata.judicature.plugin.mybatis.generator.plugins;

import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * 
 * @author yu.yao
 *
 */
public class MysqlPageHelperPlugin extends PluginAdapter {

  @Override
  public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    /** 添加分页参数 start */
    addLimit(topLevelClass, introspectedTable, "startNum", "-1");
    addLimit(topLevelClass, introspectedTable, "endNum", "10");
    /** 添加分页参数 end */
    return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
  }

  @Override
  public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
      IntrospectedTable introspectedTable) {
    XmlElement isNotNullElement = new XmlElement("if");
    isNotNullElement.addAttribute(new Attribute("test", "startNum >= 0"));
    isNotNullElement.addElement(new TextElement("limit #{startNum} , #{endNum}"));
    element.addElement(isNotNullElement);
    return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;// 不验证propertes文件内容
  }

  /**
   * 添加分页属性及其get/set方法
   * 
   * @author yu.yao
   */
  private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable,
      String name, String defultVlaue) {
    CommentGenerator commentGenerator = context.getCommentGenerator();
    Field field = new Field();
    field.setVisibility(JavaVisibility.PRIVATE);
    field.setType(FullyQualifiedJavaType.getIntInstance());
    field.setName(name);
    field.setInitializationString(defultVlaue);
    commentGenerator.addFieldComment(field, introspectedTable);
    topLevelClass.addField(field);
    String getterSetter = Character.toUpperCase(name.charAt(0)) + name.substring(1);
    Method method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setName("set" + getterSetter);
    method.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), name));
    method.addBodyLine("this." + name + "=" + name + ";");
    commentGenerator.addGeneralMethodComment(method, introspectedTable);
    topLevelClass.addMethod(method);
    method = new Method();
    method.setVisibility(JavaVisibility.PUBLIC);
    method.setReturnType(FullyQualifiedJavaType.getIntInstance());
    method.setName("get" + getterSetter);
    if ("endNum".equals(name)) {
      method.addBodyLine("return " + name + " - startNum;");
    }
    if ("startNum".equals(name)) {
      method.addBodyLine("return " + name + ";");
    }
    commentGenerator.addGeneralMethodComment(method, introspectedTable);
    topLevelClass.addMethod(method);
  }

}
