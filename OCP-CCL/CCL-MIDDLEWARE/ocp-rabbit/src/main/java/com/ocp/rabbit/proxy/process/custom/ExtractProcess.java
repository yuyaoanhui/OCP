package com.ocp.rabbit.proxy.process.custom;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.script.RabbitJSEngine;
import com.ocp.rabbit.proxy.IProcess;
import com.ocp.rabbit.proxy.component.custom.ClassifyComp;
import com.ocp.rabbit.proxy.extractor.internal.ComplexExtractor;
import com.ocp.rabbit.proxy.extractor.internal.StructureExtractor;
import com.ocp.rabbit.proxy.process.AbstractProcess;
import com.ocp.rabbit.repository.bean.PointVarBean;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 简单信息点信息抽取流程实现类
 * 
 * @author yu.yao 2018年8月3日
 *
 */
public class ExtractProcess extends AbstractProcess implements IProcess {

  private static Logger logger = LoggerFactory.getLogger(ExtractProcess.class);

  /**
   * 所有的信息点列表,根据文书类型和案由大类划分
   */
  public static Map<String, Map<String, Map<InfoPoint, PointVarBean>>> allPoints = new HashMap<>();// <docType,<majorAy,Map>>

  public ExtractProcess() {
    /**
     * init complex process,目前所有信息点均认为是复杂信息点
     */
    context.complexChain.addHandle(new ClassifyComp(context));
    context.complexChain.addHandle(new StructureExtractor(context));
    context.complexChain.addHandle(new ComplexExtractor(context));
  }

  @Override
  public void handle() {
    logger.info("开始抽取");
    context.complexChain.doHandle(context.complexChain);
    remove_unused_key("meta_people_name2obj");
    logger.info("抽取结束");
  }

  /**
   * 为上下文设置信息点的正则、反向正则、捕获模式等params信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public void setPointRegex(InfoPoint point) {
    String type = point.getDoctype().toUpperCase();
    String ay = point.getAy().replaceAll("、", "X");
    String org = point.getOrg().toUpperCase();
    String prefix = "NAMESPACE_" + org + "_" + type + "_" + ay;
    String pointPath = prefix + "." + point.getVariable();
    PointVarBean var = RabbitJSEngine.getVarBean(point.getVariable(), pointPath);
    if (var != null) {
      if (!context.points.containsKey(point.getDoctype())) {
        context.points.put(point.getDoctype(), new HashMap<>());
      }
      context.points.get(point.getDoctype()).put(point, var);
    }
  }

  /**
   * 删除不需要输出的key
   * 
   * @author yu.yao
   * @param 信息点名称，多个之间用#隔开
   * @return
   */
  private void remove_unused_key(String infoNameStr) {
    String[] infoNames = infoNameStr.split("#");
    String key;
    for (String infoName : infoNames) {
      key = TextUtils.getRightKeyByName(infoName);
      if (TextUtils.isEmpty(key) || (context.rabbitInfo.extractInfo.get(key) == null)) {
        continue;
      }
      context.rabbitInfo.extractInfo.remove(key);
    }
  }

}
