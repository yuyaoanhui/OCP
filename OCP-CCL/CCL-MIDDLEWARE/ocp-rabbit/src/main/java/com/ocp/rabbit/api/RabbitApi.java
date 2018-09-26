package com.ocp.rabbit.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.ocp.rabbit.bootstrap.Bootstrap;
import com.ocp.rabbit.middleware.controller.WebApiResult;
import com.ocp.rabbit.middleware.datacache.InfoPointCache;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.proxy.constance.MajorAy;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.proxy.process.custom.ExtractProcess;
import com.ocp.rabbit.repository.algorithm.AyIdentify;
import com.ocp.rabbit.repository.bean.ParaLabel;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.entity.RabbitInfo;

/**
 * 为jar包集成方式提供的接口类
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public class RabbitApi {
  private static final Logger logger = LoggerFactory.getLogger(RabbitApi.class);

  /**
   * 构造函数内启动rabbit平台,需要较长时间,建议放在系统启动时调用
   */
  public RabbitApi() {
    startup();
  }

  /**
   * 系统启动方法 </br>
   * 需要较长时间，建议放在系统启动时生成
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private synchronized static void startup() {
    if (Bootstrap.startup == -1) {
      Bootstrap.init();
      logger.info("rabbit平台启动成功，可以进行信息抽取！");
    }
  }

  /**
   * 自动抽取案由的全量信息点抽取接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @return
   */
  public WebApiResult<RabbitInfo> RabbitInterpret(String content) {
    if (Bootstrap.startup == -1) {
      throw new IllegalAccessError("rabbit平台未启动，请先实例化RabbitApi！");
    }
    WebApiResult<RabbitInfo> result = new WebApiResult<RabbitInfo>();
    if (Bootstrap.startup == 0) {
      logger.info("rabbit平台启动中，请耐心等待！");
      result.setResultCode(RabbitResultCode.RABBIT_WAITING);
      return result;
    }
    if (StringUtils.isEmpty(content)) {
      result.setResultCode(RabbitResultCode.RABBIT_INVALID_PARAM);
      return result;
    }
    // B.初始化流程
    ExtractProcess process = new ExtractProcess();
    process.context.document = content;// 保存文书内容
    process.context.docInfo.setParaLabels(new ParaLabel());
    List<InfoPoint> list = cutDownPoints(process.context);
    process.context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_ay[InfoPointKey.mode],
        process.context.docInfo.getAy());
    // C.执行脚本
    for (InfoPoint point : list) {
      if (!ExtractProcess.allPoints.containsKey(point.getDoctype())) {
        continue;
      }
      if (!ExtractProcess.allPoints.get(point.getDoctype()).containsKey(point.getMajoray())) {
        continue;
      }
      if (ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray())
          .containsKey(point)) {
        if (!process.context.points.containsKey(point.getDoctype())) {
          process.context.points.put(point.getDoctype(), new HashMap<>());
        }
        process.context.points.get(point.getDoctype()).put(point,
            ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray()).get(point));
      } else {
        // 配置正则表达式
        process.setPointRegex(point);
      }
    }
    // D.实际抽取
    process.handle();
    result.setResultCode(RabbitResultCode.RABBIT_SUCCESS);
    if (process.context.complexChain.code != null) {
      result.setResultCode(process.context.complexChain.code);
    }
    result.setData(process.context.rabbitInfo);
    return result;
  }

  /**
   * 指定案由的信息点抽取接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @param ay 案由名称
   * @return
   */
  public WebApiResult<RabbitInfo> RabbitInterpret(String content, String ay) {
    if (Bootstrap.startup == -1) {
      throw new IllegalAccessError("rabbit平台未启动，请先实例化RabbitApi！");
    }
    WebApiResult<RabbitInfo> result = new WebApiResult<RabbitInfo>();
    if (Bootstrap.startup == 0) {
      logger.info("rabbit平台启动中，请耐心等待！");
      result.setResultCode(RabbitResultCode.RABBIT_WAITING);
      return result;
    }
    if (StringUtils.isEmpty(content) || StringUtils.isEmpty(ay)) {
      result.setResultCode(RabbitResultCode.RABBIT_INVALID_PARAM);
      return result;
    }
    // B.初始化流程
    ExtractProcess process = new ExtractProcess();
    process.context.document = content;// 保存文书内容
    process.context.docInfo.setAy(ay);
    process.context.docInfo.setParaLabels(new ParaLabel());
    List<InfoPoint> list = cutDownPoints(process.context);
    process.context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_ay[InfoPointKey.mode],
        process.context.docInfo.getAy());
    // C.执行脚本
    for (InfoPoint point : list) {
      if (!ExtractProcess.allPoints.containsKey(point.getDoctype())) {
        continue;
      }
      if (!ExtractProcess.allPoints.get(point.getDoctype()).containsKey(point.getMajoray())) {
        continue;
      }
      if (ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray())
          .containsKey(point)) {
        if (!process.context.points.containsKey(point.getDoctype())) {
          process.context.points.put(point.getDoctype(), new HashMap<>());
        }
        process.context.points.get(point.getDoctype()).put(point,
            ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray()).get(point));
      } else {
        // 配置正则表达式
        process.setPointRegex(point);
      }
    }
    // D.实际抽取
    process.handle();
    result.setResultCode(RabbitResultCode.RABBIT_SUCCESS);
    if (process.context.complexChain.code != null) {
      result.setResultCode(process.context.complexChain.code);
    }
    result.setData(process.context.rabbitInfo);
    return result;
  }

  /**
   * 指定信息点列表的抽取接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @param infoPoints 要抽取的信息点列表
   * @return
   */
  public WebApiResult<RabbitInfo> RabbitInterpret(String content, List<InfoPoint> infoPoints) {
    // A.参数校验
    if (Bootstrap.startup == -1) {
      throw new IllegalAccessError("rabbit平台未启动，请先实例化RabbitApi！");
    }
    WebApiResult<RabbitInfo> result = new WebApiResult<RabbitInfo>();
    if (Bootstrap.startup == 0) {
      logger.info("rabbit平台启动中，请耐心等待！");
      result.setResultCode(RabbitResultCode.RABBIT_WAITING);
      return result;
    }
    if (StringUtils.isEmpty(content) || infoPoints == null || infoPoints.isEmpty()) {
      result.setResultCode(RabbitResultCode.RABBIT_INVALID_PARAM);
      return result;
    }
    // B.初始化流程
    ExtractProcess process = new ExtractProcess();
    process.context.document = content;// 保存文书内容
    setAyMajorAy(process.context);
    process.context.docInfo.setParaLabels(new ParaLabel());
    process.context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_ay[InfoPointKey.mode],
        process.context.docInfo.getAy());
    // C.执行脚本
    for (InfoPoint point : infoPoints) {
      if (!ExtractProcess.allPoints.containsKey(point.getDoctype())) {
        continue;
      }
      if (!ExtractProcess.allPoints.get(point.getDoctype()).containsKey(point.getMajoray())) {
        continue;
      }
      if (ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray())
          .containsKey(point)) {
        if (!process.context.points.containsKey(point.getDoctype())) {
          process.context.points.put(point.getDoctype(), new HashMap<>());
        }
        process.context.points.get(point.getDoctype()).put(point,
            ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray()).get(point));
      } else {
        // 配置正则表达式
        process.setPointRegex(point);
      }
    }
    // D.实际抽取
    process.handle();
    result.setResultCode(RabbitResultCode.RABBIT_SUCCESS);
    if (process.context.complexChain.code != null) {
      result.setResultCode(process.context.complexChain.code);
    }
    result.setData(process.context.rabbitInfo);
    return result;
  }

  /**
   * 指定信息点列表的抽取接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @param infoPoints 要抽取的信息点列表
   * @return
   */
  public WebApiResult<RabbitInfo> RabbitInterpret(String content, String ay,
      List<InfoPoint> infoPoints) {
    // A.参数校验
    if (Bootstrap.startup == -1) {
      throw new IllegalAccessError("rabbit平台未启动，请先实例化RabbitApi！");
    }
    WebApiResult<RabbitInfo> result = new WebApiResult<RabbitInfo>();
    if (Bootstrap.startup == 0) {
      logger.info("rabbit平台启动中，请耐心等待！");
      result.setResultCode(RabbitResultCode.RABBIT_WAITING);
      return result;
    }
    if (StringUtils.isEmpty(content) || StringUtils.isEmpty(ay) || infoPoints == null
        || infoPoints.isEmpty()) {
      result.setResultCode(RabbitResultCode.RABBIT_INVALID_PARAM);
      return result;
    }
    // B.初始化流程
    ExtractProcess process = new ExtractProcess();
    process.context.document = content;// 保存文书内容
    process.context.docInfo.setAy(ay);
    process.context.docInfo.setMajorAy(MajorAy.valueOf(InfoPointCache.major_ay.get(ay)));
    process.context.docInfo.setParaLabels(new ParaLabel());
    process.context.rabbitInfo.extractInfo.put(InfoPointKey.meta_case_ay[InfoPointKey.mode],
        process.context.docInfo.getAy());
    // C.执行脚本
    for (InfoPoint point : infoPoints) {
      if (!ExtractProcess.allPoints.containsKey(point.getDoctype())) {
        continue;
      }
      if (!ExtractProcess.allPoints.get(point.getDoctype()).containsKey(point.getMajoray())) {
        continue;
      }
      if (ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray())
          .containsKey(point)) {
        if (!process.context.points.containsKey(point.getDoctype())) {
          process.context.points.put(point.getDoctype(), new HashMap<>());
        }
        process.context.points.get(point.getDoctype()).put(point,
            ExtractProcess.allPoints.get(point.getDoctype()).get(point.getMajoray()).get(point));
      } else {
        // 配置正则表达式
        process.setPointRegex(point);
      }
    }
    // D.实际抽取
    process.handle();
    result.setResultCode(RabbitResultCode.RABBIT_SUCCESS);
    if (process.context.complexChain.code != null) {
      result.setResultCode(process.context.complexChain.code);
    }
    result.setData(process.context.rabbitInfo);
    return result;
  }

  /**
   * 缩小信息点范围
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private List<InfoPoint> cutDownPoints(Context context) {
    setAyMajorAy(context);
    String anyou = context.docInfo.getAy();
    String majorAy = context.docInfo.getMajorAy().name();
    List<InfoPoint> list = new ArrayList<InfoPoint>();
    for (String docType : InfoPointCache.caches.keySet()) {// 该案由下的所有信息点
      for (InfoPoint point : InfoPointCache.caches.get(docType)) {
        if (docType.equals(point.getDoctype())) {
          list.add(point);
        }
        if (!StringUtils.isEmpty(anyou) && !anyou.equals(point.getAy())) {
          list.remove(point);
        }
        if (!StringUtils.isEmpty(majorAy) && !majorAy.equals(point.getMajoray())) {
          list.remove(point);
        }
        if (StringUtils.isEmpty(majorAy)) {
          list.remove(point);
        }
      }
    }
    return list;
  }

  private void setAyMajorAy(Context context) {
    if (StringUtils.isEmpty(context.docInfo.getAy())) {// 如果不传案由则自动由文书内容识别<link ClassifyComp>
      List<String> list = AyIdentify.getAnyouByTextContent(context.document);
      if (list.size() == 2) {
        context.docInfo.setAy(list.get(1));
        context.docInfo.setMajorAy(MajorAy.valueOf(list.get(0)));
      } else if (list.size() == 1) {
        context.docInfo.setMajorAy(MajorAy.valueOf(list.get(0)));
      }
    }
    String anyou = context.docInfo.getAy();
    if (context.docInfo.getMajorAy() == null) {
      if (InfoPointCache.major_ay.get(anyou) != null) {
        context.docInfo.setMajorAy(MajorAy.valueOf(InfoPointCache.major_ay.get(anyou)));
      } else {
        context.docInfo.setMajorAy(MajorAy.valueOf("base"));
      }
    }
  }
}
