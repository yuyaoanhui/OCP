package com.ocp.plugin.mybatis.generator.utils;

import java.lang.reflect.Field;
import java.util.List;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.PluginConfiguration;
import org.mybatis.generator.internal.DefaultCommentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.plugin.mybatis.generator.plugins.CommentPlugin;
import com.ocp.plugin.mybatis.generator.utils.enhanced.TemplateCommentGenerator;

/**
 * 基础plugin,用于读取模板
 * 
 * @author: yu.yao
 */
public class BasePlugin extends PluginAdapter {
  protected static final Logger logger = LoggerFactory.getLogger(BasePlugin.class); // 日志
  protected CommentGenerator commentGenerator; // 注释工具
  protected List<String> warnings; // 提示

  /**
   * {@inheritDoc}
   */
  @Override
  public void setContext(Context context) {
    super.setContext(context);
    // 配置插件使用的模板引擎
    PluginConfiguration cfg = PluginTools.getPluginConfiguration(context, CommentPlugin.class);
    if (cfg == null || cfg.getProperty(CommentPlugin.PRO_TEMPLATE) == null) {
      if (context.getCommentGenerator() instanceof DefaultCommentGenerator) {
        // 使用默认模板引擎
        commentGenerator = new TemplateCommentGenerator("gencodetemplate/default-comment.ftl", true);
      } else {
        // 用户自定义
        commentGenerator = context.getCommentGenerator();
      }
    } else {
      TemplateCommentGenerator templateCommentGenerator =
          new TemplateCommentGenerator(cfg.getProperty(CommentPlugin.PRO_TEMPLATE), false);
      // mybatis 插件使用的注释生成器
      commentGenerator = templateCommentGenerator;
      // 修正系统插件
      try {
        // 先执行一次生成CommentGenerator操作，然后再替换
        context.getCommentGenerator();
        Field field = Context.class.getDeclaredField("commentGenerator");
        field.setAccessible(true);
        field.set(context, templateCommentGenerator);
      } catch (Exception e) {
        logger.error("反射异常", e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validate(List<String> warnings) {
    return true;
  }
}
