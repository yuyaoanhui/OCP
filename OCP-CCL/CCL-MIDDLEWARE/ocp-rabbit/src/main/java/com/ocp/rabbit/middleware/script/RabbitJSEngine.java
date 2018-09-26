package com.ocp.rabbit.middleware.script;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.ocp.rabbit.middleware.datacache.InfoPointCache;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.plugin.AbstractPluginHub;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.bean.PointVarBean;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;
import com.ocp.rabbit.repository.tool.HandleEntrance;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * js脚本执行引擎
 * 
 * @author yu.yao 2018年8月10日
 *
 */
@SuppressWarnings("restriction")
public class RabbitJSEngine {
  private static final Logger logger = LoggerFactory.getLogger(RabbitJSEngine.class);

  public static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

  /**
   * 编译js脚本
   * 
   * @author yu.yao
   * @param scriptText 脚本内容
   * @return
   */
  public static void compileAndRun(String scriptText) throws ScriptException {
    if (engine instanceof Compilable) {
      CompiledScript script = ((Compilable) engine).compile(scriptText);
      script.eval();// 表示将js代码转换成java字节码，而非执行js内部方法
    }
  }

  /**
   * 封装需要提前执行的信息点参数bean列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void setBasicPre(String prefix, String ay, String type, String version,
      String org) {
    try {
      ScriptObjectMirror basicPoints =
          (ScriptObjectMirror) engine.eval(prefix + "basic.basicPrePoints");
      String[] allKeys = basicPoints.getOwnKeys(true);
      for (String key : allKeys) {
        ScriptObjectMirror array = (ScriptObjectMirror) basicPoints.get(key);
        for (int i = 0; i < array.size(); i++) {
          String variable = (String) array.getSlot(i);
          String pointPath = prefix + ay + "." + variable;
          PointVarBean var = getVarBean(variable, pointPath);
          if (var == null) {
            continue;
          }
          InfoPoint point = InfoPointCache.query(ay, type, variable, version, org);
          if (point == null) {
            logger.info("手动封装自定义信息点:" + pointPath);
            point = new InfoPoint();
            point.setVariable(variable);
            point.setVersion(version);
            point.setOrg(org);
            point.setDoctype(type);
            point.setAy(ay);
            point.setMajoray(InfoPointCache.major_ay.get(ay));
          }
          if (!InfoPointCache.prePoints.containsKey(point.getDoctype())) {
            InfoPointCache.prePoints.put(point.getDoctype(), new HashMap<>());
          }
          if (!InfoPointCache.prePoints.get(point.getDoctype()).containsKey(point.getMajoray())) {
            InfoPointCache.prePoints.get(point.getDoctype()).put(point.getMajoray(),
                new LinkedHashMap<>());
          }
          InfoPointCache.prePoints.get(point.getDoctype()).get(point.getMajoray()).put(point, var);
        }
      }
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

  /**
   * 抽取争议焦点
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void controversyFocus(String prefix, String ay, Context context) {
    try {
      if (engine.eval(prefix + "basic.controversy_focus") == null) {
        return;
      }
      ScriptObjectMirror focus =
          (ScriptObjectMirror) engine.eval(prefix + "basic.controversy_focus");
      ScriptObjectMirror focus_ays = (ScriptObjectMirror) focus.get("ay");
      List<String> list = new ArrayList<String>();
      if (!focus_ays.isEmpty()) {
        for (int i = 0; i < focus_ays.size(); i++) {
          list.add((String) focus_ays.getSlot(i));
        }
      }
      String methodName = (String) focus.get("method");
      if (list.contains(ay)) {
        Class<?> clazz = HandleEntrance.entrance(methodName);
        Object obj = AbstractPluginHub.getPluginByType(clazz, context);
        if (obj != null) {
          clazz.getMethod(methodName, ParamsBean.class).invoke(obj, new ParamsBean());
        } else {
          throw new RuntimeException("找不到" + clazz.getName() + "类型的插件！");
        }
      }
    } catch (ScriptException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * 封装信息点配置包装类
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static PointVarBean getVarBean(String pointName, String pointPath) {
    PointVarBean var = new PointVarBean();
    try {
      if (engine.eval(pointPath) == null || engine.eval(pointPath + ".params") == null) {
        return null;
      }
      ScriptObjectMirror params = (ScriptObjectMirror) engine.eval(pointPath + ".params");
      ScriptObjectMirror varPoint = (ScriptObjectMirror) engine.eval(pointPath);
      String methodName = (String) varPoint.get("method");
      if (params.isArray() && !params.isEmpty()) {
        var.setParamsList(paramBeanList(pointName, pointPath, params));
        Class<?> clazz = HandleEntrance.entrance(methodName);
        if (clazz != null) {
          var.setMethod(clazz.getMethod(methodName, ParamsBean.class));
        }
        /************************** adjust start *********************************/
        if (engine.eval(pointPath + ".adjust") != null) {
          ScriptObjectMirror adjusts = (ScriptObjectMirror) engine.eval(pointPath + ".adjust");
          if (adjusts.isArray() && !adjusts.isEmpty()) {
            List<PointVarBean> adjustVars = new ArrayList<PointVarBean>();
            for (int i = 0; i < adjusts.size(); i++) {
              ScriptObjectMirror adjustParams =
                  (ScriptObjectMirror) engine.eval(pointPath + ".adjust[" + i + "].params");
              ScriptObjectMirror adjust = (ScriptObjectMirror) adjusts.getSlot(i);
              String adjustMethodName = (String) adjust.get("method");
              if (adjustParams.isArray() && !adjustParams.isEmpty()) {
                PointVarBean adjustVar = new PointVarBean();
                adjustVar.setParamsList(paramBeanList(pointName, pointPath, adjustParams));
                Class<?> adjustClazz = HandleEntrance.entrance(adjustMethodName);
                if (adjustClazz != null) {
                  adjustVar.setMethod(adjustClazz.getMethod(adjustMethodName, ParamsBean.class));
                }
                adjustVars.add(adjustVar);
              }
            }
            var.setAdjust(adjustVars);
          }
        }
        /************************** adjust end *********************************/
      }
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (ScriptException e) {
      e.printStackTrace();
    }
    return var;
  }

  /**
   * 为指定信息点生成参数bean列表
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static List<ParamsBean> paramBeanList(String pointName, String pointPath,
      ScriptObjectMirror params) {
    List<ParamsBean> list = new ArrayList<ParamsBean>();
    try {
      Map<String, Method> fields = getAllFields(ParamsBean.class);
      for (int i = 0; i < params.size(); i++) {// js中配置的params数组
        ParamsBean bean = new ParamsBean();
        bean.setInfoPointName(pointName);
        ScriptObjectMirror param = (ScriptObjectMirror) params.getSlot(i);
        for (String field : fields.keySet()) {
          if (param.get(field) != null) {
            String str = (String) param.get(field);
            fields.get(field).invoke(bean, str);
          } else {
            fields.get(field).invoke(bean, "");
          }
        }
        /******************************************************
         * 判断标签是否存在，不存在则根据是否剩余有效标签决定是否跳过该配置 start
         *********************************************************/
        if (!StringUtils.isEmpty(bean.getTagList())) {
          List<String> tags = new ArrayList<String>();
          for (String tag : bean.getTagList().split("#")) {
            if (ParaLabelEnum.getByLabel(tag) != null) {
              tags.add(tag);
            }
          }
          String newTags = "";
          for (String tag : tags) {
            newTags = newTags.concat(tag).concat("#");
          }
          if (!StringUtils.isEmpty(newTags)) {
            newTags = newTags.substring(0, newTags.lastIndexOf("#"));
          }
          bean.setTagList(newTags);
        }
        /******************************************************
         * 判断标签是否存在，不存在则根据是否剩余有效标签决定是否跳过该配置 end
         *********************************************************/
        list.add(bean);
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return list;
  }

  /**
   * 获取所有属性名和setter方法
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static Map<String, Method> getAllFields(Class<?> clazz) {
    Map<String, Method> map = new HashMap<String, Method>();
    try {
      for (Field field : clazz.getDeclaredFields()) {
        String name = field.getName();
        if (name.equals("infoPointName")) {
          continue;
        }
        String setter = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        map.put(name, clazz.getMethod(setter, String.class));
      }
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    }
    return map;
  }
}
