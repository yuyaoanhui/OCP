package com.futuredata.judicature.plugin.mybatis.generator.utils;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;

/**
 * 引用静态方法,帅的一比
 */
import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * Java 属性和方法等 生成工具
 * 
 * @author yu.yao
 *
 */
public class JavaElementGeneratorTools {
  /**
   * 生成静态常量
   * 
   * @param fieldName 常量名称
   * @param javaType 类型
   * @param initString 初始化字段
   * @return
   */
  public static Field generateStaticFinalField(String fieldName, FullyQualifiedJavaType javaType,
      String initString) {
    Field field = new Field(fieldName, javaType);
    field.setVisibility(JavaVisibility.PUBLIC);
    field.setStatic(true);
    field.setFinal(true);
    if (initString != null) {
      field.setInitializationString(initString);
    }
    return field;
  }

  /**
   * 生成属性
   * 
   * @param fieldName 常量名称
   * @param visibility 可见性
   * @param javaType 类型
   * @param initString 初始化字段
   * @return
   */
  public static Field generateField(String fieldName, JavaVisibility visibility,
      FullyQualifiedJavaType javaType, String initString) {
    Field field = new Field(fieldName, javaType);
    field.setVisibility(visibility);
    if (initString != null) {
      field.setInitializationString(initString);
    }
    return field;
  }

  /**
   * 生成方法
   * 
   * @param methodName 方法名
   * @param visibility 可见性
   * @param returnType 返回值类型
   * @param parameters 参数列表
   * @return
   */
  public static Method generateMethod(String methodName, JavaVisibility visibility,
      FullyQualifiedJavaType returnType, Parameter... parameters) {
    Method method = new Method(methodName);
    method.setVisibility(visibility);
    method.setReturnType(returnType);
    if (parameters != null) {
      for (Parameter parameter : parameters) {
        method.addParameter(parameter);
      }
    }

    return method;
  }

  /**
   * 生成方法实现体
   * 
   * @param method 方法
   * @param bodyLines 方法实现行
   * @return
   */
  public static Method generateMethodBody(Method method, String... bodyLines) {
    if (bodyLines != null) {
      for (String bodyLine : bodyLines) {
        method.addBodyLine(bodyLine);
      }
    }
    return method;
  }

  /**
   * 生成Filed的Set方法
   * 
   * @param field field
   * @return
   */
  public static Method generateSetterMethod(Field field) {
    Method method = generateMethod(
        "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
        JavaVisibility.PUBLIC, null, new Parameter(field.getType(), field.getName()));
    return generateMethodBody(method, "this." + field.getName() + " = " + field.getName() + ";");
  }

  /**
   * 生成Filed的Get方法
   * 
   * @param field field
   * @return
   */
  public static Method generateGetterMethod(Field field) {
    Method method = generateMethod(
        "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
        JavaVisibility.PUBLIC, field.getType());
    return generateMethodBody(method, "return this." + field.getName() + ";");
  }

  /**
   * 获取Model没有BLOBs类时的类型
   * 
   * @param introspectedTable
   * @return
   */
  public static FullyQualifiedJavaType getModelTypeWithoutBLOBs(
      IntrospectedTable introspectedTable) {
    FullyQualifiedJavaType type;
    if (introspectedTable.getRules().generateBaseRecordClass()) {
      type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    } else if (introspectedTable.getRules().generatePrimaryKeyClass()) {
      type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
    } else {
      throw new RuntimeException(getString("RuntimeError.12"));
    }
    return type;
  }

  /**
   * 获取Model有BLOBs类时的类型
   * 
   * @param introspectedTable
   * @return
   */
  public static FullyQualifiedJavaType getModelTypeWithBLOBs(IntrospectedTable introspectedTable) {
    FullyQualifiedJavaType type;
    if (introspectedTable.getRules().generateRecordWithBLOBsClass()) {
      type = new FullyQualifiedJavaType(introspectedTable.getRecordWithBLOBsType());
    } else {
      // the blob fields must be rolled up into the base class
      type = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
    }
    return type;
  }

  /**
   * 克隆方法
   * 
   * @param method
   */
  public static Method cloneMethod(Method method) {
    Method result = new Method();
    result.setConstructor(method.isConstructor());
    result.setFinal(method.isFinal());
    result.setName(method.getName());
    result.setNative(method.isNative());
    result.setReturnType(method.getReturnType());
    result.setSynchronized(method.isSynchronized());
    result.setStatic(method.isStatic());
    result.setVisibility(method.getVisibility());
    for (Parameter parameter : method.getParameters()) {
      result.addParameter(parameter);
    }
    for (String docLine : method.getJavaDocLines()) {
      result.addJavaDocLine(docLine);
    }
    result.addBodyLines(method.getBodyLines());
    return result;
  }
}
