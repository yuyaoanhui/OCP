package com.ocp.rabbit.proxy.extractor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.script.RabbitJSEngine;
import com.ocp.rabbit.plugin.AbstractPlugin;
import com.ocp.rabbit.plugin.custom.AbstractRuleFunctionPlugin;
import com.ocp.rabbit.proxy.CommonUnit;
import com.ocp.rabbit.proxy.chain.HandleChain;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.bean.ParamsBean;
import com.ocp.rabbit.repository.bean.PointVarBean;
import com.ocp.rabbit.repository.constant.ParaLabelEnum;

/**
 * 抽取器抽象模板
 * 
 * @author yu.yao 2018年8月2日
 *
 */
public abstract class AbstractExtractor extends CommonUnit<AbstractExtractor> {

  // 诉讼当事人列表
  public static Set<String> Litigant = new HashSet<String>();
  // 审判人员列表
  public static List<String> judgePeopleList = new ArrayList<String>();
  // 原告诉称和事实
  public static List<String> PlaintiffArgsAndFacts = new ArrayList<String>();

  static {
    Litigant.add(ParaLabelEnum.REPRESENTATIVE.toString());
    Litigant.add(ParaLabelEnum.ASSIGNED.toString());
    Litigant.add(ParaLabelEnum.ATTORNEY.toString());
    Litigant.add(ParaLabelEnum.ENTRUSTED.toString());
    Litigant.add(ParaLabelEnum.DEFENDANT.toString());
    Litigant.add(ParaLabelEnum.PLAINTIFF.toString());
    Litigant.add(ParaLabelEnum.THIRD_PERSON.toString());
    Litigant.add(ParaLabelEnum.SUSPECT_BASE_INFO.toString());
    judgePeopleList.add(ParaLabelEnum.CHIEF_JUDGE.toString());
    judgePeopleList.add(ParaLabelEnum.JUDGES.toString());
    judgePeopleList.add(ParaLabelEnum.JUDGE_ASSESSOR.toString());
    judgePeopleList.add(ParaLabelEnum.CLERK.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.OFFICE_OPINION.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_ORIGINAL.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_FISRT.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.PLAINTIFF_ARGS_SECOND.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_SECONDARY.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_PRIMARY.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_BASE.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_FOUND_CMPL.toString());
    PlaintiffArgsAndFacts.add(ParaLabelEnum.FACTS_ABOVE.toString());
  }
  public static final HandleChain chain = new HandleChain();

  /**
   * 向责任链中添加处理实体
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static void addChain(AbstractExtractor obj) {
    chain.addHandle(obj);
  }

  /**
   * 抽取基本信息点,并根据文书案由大类抽取指定的前置信息点
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void automatic(Context context) {
    try {
      String docTypeName = context.docInfo.getDocType().name();
      Map<InfoPoint, PointVarBean> map = context.prePoints.get(docTypeName).get("base");
      for (InfoPoint point : map.keySet()) {// 先跑base前置信息点
        run(map.get(point), context.plugins);
      }
      String majorAy = context.docInfo.getMajorAy().name();
      map = context.prePoints.get(docTypeName).get(majorAy);
      for (InfoPoint point : map.keySet()) {// 再跑该文书案由大类的前置信息点
        run(map.get(point), context.plugins);
      }
      String org = context.docInfo.getDocType().getOrgnization().toUpperCase();
      String type = docTypeName.toUpperCase();
      String ay = context.docInfo.getAy().replaceAll("、", "X");
      String prefix = "NAMESPACE_" + org + "_" + type + "_";
      RabbitJSEngine.controversyFocus(prefix, ay, context);
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * 核心处理逻辑：匹配指定的所有信息点
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void doParams(Context context) {
    try {
      String docTypeName = context.docInfo.getDocType().name();
      Map<InfoPoint, PointVarBean> map = context.points.get(docTypeName);
      for (InfoPoint point : map.keySet()) {
        run(map.get(point), context.plugins);
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  /**
   * 执行指定信息点的抽取
   * 
   * @author yu.yao
   * @param
   * @return
   */
  @SuppressWarnings("rawtypes")
  private static void run(PointVarBean varBean, List<AbstractRuleFunctionPlugin> plugins)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (varBean == null) {
      return;
    }
    Object obj = null;
    Method varMethod = varBean.getMethod();
    if (varMethod != null) {
      for (AbstractPlugin plugin : plugins) {
        if (varMethod.getDeclaringClass().equals(plugin.getClass())) {
          obj = plugin;
          break;
        }
      }
      if (obj != null) {
        List<ParamsBean> list = varBean.getParamsList();
        for (ParamsBean bean : list) {
          varMethod.invoke(obj, bean);
        }
      }
      if (varBean.getAdjust() != null) {
        for (PointVarBean adjust : varBean.getAdjust()) {
          Object adjustObj = null;
          Method adjustMethod = adjust.getMethod();
          for (AbstractPlugin plugin : plugins) {
            if (adjustMethod.getDeclaringClass().equals(plugin.getClass())) {
              adjustObj = plugin;
              break;
            }
          }
          if (adjustObj != null) {
            List<ParamsBean> adjustParams = adjust.getParamsList();
            for (ParamsBean bean : adjustParams) {
              if (adjustMethod != null) {
                adjustMethod.invoke(adjustObj, bean);
              }
            }
          }
        }
      }
    }
  }
}
