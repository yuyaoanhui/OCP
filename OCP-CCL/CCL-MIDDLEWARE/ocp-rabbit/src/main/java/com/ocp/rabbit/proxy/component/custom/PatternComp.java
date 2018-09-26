package com.ocp.rabbit.proxy.component.custom;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.proxy.component.AbstractComp;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;

/**
 * 用于模式匹配的构件
 * 
 * @author yu.yao 2018年6月28日
 *
 */
public class PatternComp extends AbstractComp {
  private Context context;

  public PatternComp(Context context) {
    this.context = context;
  }

  @Override
  public void handle() {
    String content = context.getContent();
    Matcher matcher = null;
    List<Pattern> regexes = context.regex;
    if (regexes == null || regexes.isEmpty()) {
      throw new NullPointerException("正向正则为空！");
    }
    // 正向匹配
    for (Pattern regex : regexes) {
      matcher = regex.matcher(content);
      if (matcher.find()) {
        context.isPatterned = true;
        break;
      } else {
        context.isPatterned = false;
      }
    }
    Pattern reverseRegex = context.reverseRegex;
    if (reverseRegex != null) {// 反向匹配
      matcher = reverseRegex.matcher(content);
      if (matcher.find()) {
        context.isPatterned = false;
      }
    }
  }
}
