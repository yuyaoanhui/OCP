package com.ocp.rabbit.bootstrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.script.ScriptException;

import org.ansj.splitWord.analysis.NlpAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

import com.ocp.rabbit.bootstrap.context.RabbitContext;
import com.ocp.rabbit.fsm.AbstractFSMState;
import com.ocp.rabbit.middleware.datacache.InfoPointCache;
import com.ocp.rabbit.middleware.datacache.Initor;
import com.ocp.rabbit.middleware.datacache.LawInfoCache;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.script.RabbitJSEngine;
import com.ocp.rabbit.plugin.custom.AbstractRuleFunctionPlugin;
import com.ocp.rabbit.proxy.component.custom.ClassifyComp;
import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.proxy.process.custom.ExtractProcess;
import com.ocp.rabbit.repository.bean.PointVarBean;
import com.ocp.rabbit.repository.tool.HandleEntrance;
import com.ocp.rabbit.repository.tool.RabbitException;
import com.ocp.rabbit.repository.util.ClassUtil;
import com.ocp.rabbit.repository.util.FileOperate;
import com.ocp.rabbit.repository.util.PropertiesUtil;

/**
 * 系统启动核心引导程序
 * 
 * @author yu.yao 2018年6月28日
 *
 */
public final class Bootstrap {
  private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

  public static ApplicationContext springContext = null;
  public static volatile int startup = -1;

  public synchronized static void init() {
    if (startup == -1) {
      startup = 0;
      if (springContext == null) {// java 手动加载pring初始化
        logger.info("rabbit平台开始初始化......");
        springContext = new AnnotationConfigApplicationContext("com.futuredata.rabbit.bootstrap",
            "com.futuredata.rabbit.middleware");
      }
      initMiddleWare();
      initContext();
      registerPlugin();
      loadResource();
      initAllPointsBean();
      logger.info("rabbit平台初始化完成......");
      startup = 1;
    }
  }

  /**
   * 加载资源
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void loadResource() {
    NlpAnalysis.parse("");
    readAndCompileScript();
    setBasicVarBeans();
  }

  /**
   * 读取所有的js脚本文件内容并编译
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void readAndCompileScript() {
    String[] scriptPaths =
        PropertiesUtil.getProperty("application.properties", "script.path").split(",");
    try {
      List<String> basicPaths = new ArrayList<String>();
      String scriptText = "";
      for (String path : scriptPaths) {
        List<String> fileNames = FileOperate.listFilesInDic(path);
        String[] splits = path.split("/");
        if (splits[splits.length - 1].equals("basic")) {
          fileNames.remove("basic.js");
          fileNames.remove("structure.xml");
          basicPaths.add(path + "basic.js");
        }
        for (String fileName : fileNames) {
          String filePath = path + fileName;
          scriptText = FileOperate.readTxt(Bootstrap.class.getResourceAsStream(filePath), "utf-8");
          if (!StringUtils.isEmpty(scriptText)) {
            logger.info("编译脚本文件：" + filePath);
            RabbitJSEngine.compileAndRun(scriptText);
          }
        }
      }
      for (String filePath : basicPaths) {
        scriptText = FileOperate.readTxt(Bootstrap.class.getResourceAsStream(filePath), "utf-8");
        if (!StringUtils.isEmpty(scriptText)) {
          logger.info("编译脚本文件：" + filePath);
          RabbitJSEngine.compileAndRun(scriptText);
        }
      }
    } catch (ScriptException e) {
      logger.error("解析js脚本出错：", e);
      throw new RuntimeException("解析js脚本出错：");
    }
  }

  /**
   * 设置基础信息点的参数bean
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void setBasicVarBeans() {
    String[] scriptPaths =
        PropertiesUtil.getProperty("application.properties", "script.path").split(",");
    for (String path : scriptPaths) {
      List<String> fileNames = FileOperate.listFilesInDic(path);
      String type = "";
      String version = "";
      String org = "";
      String[] splits = path.split("/");
      if (splits[splits.length - 1].equals("basic")) {
        fileNames.remove("basic.js");
        fileNames.remove("structure.xml");
        type = splits[splits.length - 2];
        version = splits[splits.length - 3];
        org = splits[splits.length - 4];
      } else {
        type = splits[splits.length - 1];
        version = splits[splits.length - 2];
        org = splits[splits.length - 3];
      }
      String prefix = "NAMESPACE_" + org.toUpperCase() + "_" + type.toUpperCase() + "_";
      for (String fileName : fileNames) {
        logger.info("配置脚本:" + path + fileName + "的执行参数.");
        String ay = fileName.replace(".js", "").replaceAll("、", "X");
        RabbitJSEngine.setBasicPre(prefix, ay, type, version, org);
      }
    }
  }

  /**
   * 初始化中间件
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void initMiddleWare() {
    Initor infoPointInitor = springContext.getBean(InfoPointCache.class);
    Initor lawInfoCacheInitor = springContext.getBean(LawInfoCache.class);
    try {
      infoPointInitor.init();
      lawInfoCacheInitor.init();
    } catch (RabbitException e) {
      logger.error("信息点缓存出错:", e);
      throw new RuntimeException("信息点缓存出错");
    }
  }

  /**
   * 初始化上下文
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void initContext() {
    try {
      Class.forName(AbstractFSMState.class.getName());
      Class.forName(AbstractRuleFunctionPlugin.class.getName());
      Class.forName(ClassifyComp.class.getName());
      RabbitContext.init();
    } catch (ClassNotFoundException e) {
      logger.error("找不到类:", e);
      throw new RuntimeException("加载资源出错");
    }
  }

  /**
   * 预先配置所有信息点
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void initAllPointsBean() {
    Map<String, List<InfoPoint>> pointsCache = InfoPointCache.caches;
    for (DocumentType type : DocumentType.values()) {
      logger.info("配置" + type.name() + "类型信息点参数.");
      if (pointsCache.containsKey(type.name())) {
        List<InfoPoint> tmpList = pointsCache.get(type.name());
        if (tmpList != null && !tmpList.isEmpty()) {
          // C.执行脚本
          for (InfoPoint point : tmpList) {
            // 配置正则表达式
            if (StringUtils.isEmpty(point.getAy())) {
              continue;
            }
            String ay = point.getAy().replaceAll("、", "X");
            String org = point.getOrg().toUpperCase();
            String prefix = "NAMESPACE_" + org + "_" + type.name().toUpperCase() + "_" + ay;
            String pointPath = prefix + "." + point.getVariable();
            PointVarBean var = RabbitJSEngine.getVarBean(point.getVariable(), pointPath);
            if (var != null) {
              if (!ExtractProcess.allPoints.containsKey(point.getDoctype())) {
                ExtractProcess.allPoints.put(point.getDoctype(), new HashMap<>());
              }
              if (!ExtractProcess.allPoints.get(point.getDoctype())
                  .containsKey(point.getMajoray())) {
                ExtractProcess.allPoints.get(point.getDoctype()).put(point.getMajoray(),
                    new HashMap<>());
              }
              ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray()).put(point,
                  var);
            }
          }
        }
      }
    }
  }

  /**
   * 扫描自定义插件并注册
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void registerPlugin() {
    // 下面循环时newInstance()会新增加class，因此使用并发容器
    CopyOnWriteArrayList<Class<?>> list = new CopyOnWriteArrayList<Class<?>>();
    list.addAll(ClassUtil.getAllSubClasses(AbstractRuleFunctionPlugin.class));
    try {
      Context context = new Context("daemon");
      for (Class<?> clazz : list) {
        if (clazz.getSuperclass() != null) {
          if (AbstractRuleFunctionPlugin.class.equals(clazz.getSuperclass())) {
            Constructor<?> c0 = clazz.getDeclaredConstructor(Context.class);
            AbstractRuleFunctionPlugin obj = (AbstractRuleFunctionPlugin) c0.newInstance(context);
            obj.setCustomPlugin(obj);
            obj.register();
            weave(clazz);
          }
        }
      }
    } catch (IllegalArgumentException | InstantiationException | IllegalAccessException
        | NoSuchMethodException | SecurityException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }


  /**
   * 提取插件中定义的方法
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void weave(Class<?> clazz) {
    Map<Class<?>, List<String>> map = HandleEntrance.getExtendNameMap();
    if (!map.containsKey(clazz)) {
      List<String> methodNames = new ArrayList<String>();
      for (Method method : clazz.getMethods()) {
        methodNames.add(method.getName());
      }
      map.put(clazz, methodNames);
    }
  }
}
