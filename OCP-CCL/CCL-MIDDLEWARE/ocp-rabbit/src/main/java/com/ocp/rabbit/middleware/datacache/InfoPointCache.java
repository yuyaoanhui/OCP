package com.ocp.rabbit.middleware.datacache;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.service.InfoPointService;
import com.ocp.rabbit.proxy.constance.DocumentType;
import com.ocp.rabbit.proxy.constance.MajorAy;
import com.ocp.rabbit.repository.bean.PointVarBean;
import com.ocp.rabbit.repository.entity.InfoPointKey;
import com.ocp.rabbit.repository.tool.RabbitException;
import com.ocp.rabbit.repository.util.FileOperate;

/**
 * 信息点缓存管理类
 * 
 * @author yu.yao 2018年8月1日
 *
 */
@Component
public class InfoPointCache implements Initor {
  private static final Logger logger = LoggerFactory.getLogger(InfoPointCache.class);

  @Autowired
  InfoPointService service;

  public static Map<String, List<InfoPoint>> caches = new HashMap<String, List<InfoPoint>>();// 缓存数据库中所有的信息点,key:文书类型
  public static Map<String, String> major_ay = new HashMap<String, String>();// <ay, major>
  public static Map<String, Map<String, LinkedHashMap<InfoPoint, PointVarBean>>> prePoints =
      new HashMap<>();// <docType,<majorAy,Map>>

  @Override
  public void init() {
    try {
      readMajorAy();
      for (DocumentType type : DocumentType.values()) {
        List<InfoPoint> tmpList = service.queryByType(type.name());
        if (tmpList != null && !tmpList.isEmpty()) {
          caches.put(type.name(), tmpList);
        }
      }
    } catch (RabbitException e) {
      logger.error("查询数据库失败", e);;
    }
  }

  @Override
  public synchronized void refresh() throws RabbitException {
    init();
  }

  /**
   * 根据条件查询
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static InfoPoint query(String ay, String docType, String variable, String version,
      String org) {
    boolean isMajor = false;
    for (MajorAy ma : MajorAy.values()) {
      if (ma.name().equals(ay)) {
        isMajor = true;
      }
    }
    for (InfoPoint point : caches.get(docType)) {
      if (isMajor) {
        if (ay.equals(point.getMajoray()) && org.equals(point.getOrg())
            && variable.equals(point.getVariable()) && version.equals(point.getVersion())) {
          return point;
        }
      } else if (ay.equals(point.getAy()) && org.equals(point.getOrg())
          && variable.equals(point.getVariable()) && version.equals(point.getVersion())) {
        return point;
      }
    }
    return null;
  }

  /**
   * 获取需要指定抽取的直接信息点
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static List<InfoPoint> getWPoints() {
    List<InfoPoint> rslt = new ArrayList<InfoPoint>();
    for (String docType : caches.keySet()) {
      for (InfoPoint point : caches.get(docType)) {
        if (point.getW().equals("yes")) {
          rslt.add(point);
        }
      }
    }
    return rslt;
  }

  /**
   * 获取案由分类信息
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static void readMajorAy() {
    List<String> fileContent = null;
    InputStream aystream = InfoPointKey.class.getResourceAsStream("/ay.txt");
    fileContent = FileOperate.readTxtToArrays(aystream, "utf-8");
    for (String tmp : fileContent) {
      if (tmp.trim().indexOf("#") == 1) {
        continue;
      }
      String[] tmpArr = tmp.split(" ");
      major_ay.put(tmpArr[0].trim(), tmpArr[1].trim());
    }
  }
}
