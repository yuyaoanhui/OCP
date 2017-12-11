package com.futuredata.judicature.plugin.mybatis.generator.extend;

import java.util.List;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;

/**
 * 生成dao时添加baseMapper的泛型表达
 * 
 * @author yu.yao
 *
 */
public class MysqlGenericParadigmPlugin extends PluginAdapter {

  private final static String baseDAO = "com.futuredata.judicature.base.dao.BaseMapper";

  /**
   * 给dao层mapper类继承BaseMapper增加泛型表达
   */
  @Override
  public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
      IntrospectedTable introspectedTable) {
    StringBuilder sb = new StringBuilder();
    interfaze.addJavaDocLine("/** ");
    sb.append(" * 用于操作表:");
    sb.append(introspectedTable.getFullyQualifiedTable());
    interfaze.addJavaDocLine(sb.toString());
    interfaze.addJavaDocLine(" */");
    List<Method> methods = interfaze.getMethods();
    for (Method method : methods) {
      method.addAnnotation("@Override");
    }

    interfaze.addImportedType(new FullyQualifiedJavaType(baseDAO));
    String mapperName = interfaze.getType().getShortName();
    String modelName = mapperName.substring(0, mapperName.lastIndexOf("Mapper"));
    String exampleName = modelName.concat("Example");
    interfaze.addSuperInterface(new FullyQualifiedJavaType(
        baseDAO.concat("<").concat(modelName).concat(", ").concat(exampleName).concat(">")));
    return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
  }

  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }

}
