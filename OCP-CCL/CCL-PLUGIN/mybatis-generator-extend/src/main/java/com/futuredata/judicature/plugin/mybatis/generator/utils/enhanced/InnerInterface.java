package com.futuredata.judicature.plugin.mybatis.generator.utils.enhanced;

import static org.mybatis.generator.api.dom.OutputUtilities.calculateImports;
import static org.mybatis.generator.api.dom.OutputUtilities.javaIndent;
import static org.mybatis.generator.api.dom.OutputUtilities.newLine;
import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

import java.util.Iterator;
import java.util.Set;

import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaDomUtils;
import org.mybatis.generator.api.dom.java.Method;

/**
 * 内部接口
 * 
 * @author: yu.yao
 */
public class InnerInterface extends Interface {
  public InnerInterface(FullyQualifiedJavaType type) {
    super(type);
  }

  public InnerInterface(String type) {
    super(type);
  }

  /**
   * 格式化后的内容，内部接口需要增加缩进
   *
   * @param indentLevel the indent level
   * @param compilationUnit the compilation unit
   * @return the formatted content
   */
  public String getFormattedContent(int indentLevel, CompilationUnit compilationUnit) {
    StringBuilder sb = new StringBuilder();

    for (String commentLine : getFileCommentLines()) {
      sb.append(commentLine);
      newLine(sb);
    }

    if (stringHasValue(getType().getPackageName())) {
      sb.append("package "); //$NON-NLS-1$
      sb.append(getType().getPackageName());
      sb.append(';');
      newLine(sb);
      newLine(sb);
    }

    for (String staticImport : getStaticImports()) {
      sb.append("import static "); //$NON-NLS-1$
      sb.append(staticImport);
      sb.append(';');
      newLine(sb);
    }

    if (getStaticImports().size() > 0) {
      newLine(sb);
    }

    Set<String> importStrings = calculateImports(getImportedTypes());
    for (String importString : importStrings) {
      sb.append(importString);
      newLine(sb);
    }

    if (importStrings.size() > 0) {
      newLine(sb);
    }

    addFormattedJavadoc(sb, indentLevel);
    addFormattedAnnotations(sb, indentLevel);

    OutputUtilities.javaIndent(sb, indentLevel);

    sb.append(getVisibility().getValue());

    if (isFinal()) {
      sb.append("final "); //$NON-NLS-1$
    }

    sb.append("interface "); //$NON-NLS-1$
    sb.append(getType().getShortName());

    if (getSuperInterfaceTypes().size() > 0) {
      sb.append(" extends "); //$NON-NLS-1$

      boolean comma = false;
      for (FullyQualifiedJavaType fqjt : getSuperInterfaceTypes()) {
        if (comma) {
          sb.append(", "); //$NON-NLS-1$
        } else {
          comma = true;
        }

        sb.append(JavaDomUtils.calculateTypeName(this, fqjt));
      }
    }

    sb.append(" {"); //$NON-NLS-1$
    indentLevel++;

    Iterator<Method> mtdIter = getMethods().iterator();
    while (mtdIter.hasNext()) {
      newLine(sb);
      Method method = mtdIter.next();
      sb.append(method.getFormattedContent(indentLevel, true, this));
      if (mtdIter.hasNext()) {
        newLine(sb);
      }
    }

    indentLevel--;
    newLine(sb);
    javaIndent(sb, indentLevel);
    sb.append('}');

    return sb.toString();
  }
}
