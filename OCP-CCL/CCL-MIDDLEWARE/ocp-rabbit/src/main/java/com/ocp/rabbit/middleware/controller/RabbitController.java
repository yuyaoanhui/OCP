package com.ocp.rabbit.middleware.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.rabbit.api.RabbitApi;
import com.ocp.rabbit.middleware.orm.QueryDTO;
import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.service.InfoPointService;
import com.ocp.rabbit.repository.constant.RabbitResultCode;
import com.ocp.rabbit.repository.entity.RabbitInfo;
import com.ocp.rabbit.repository.tool.RabbitException;
import com.ocp.rabbit.repository.util.ClassUtil;
import com.ocp.rabbit.repository.util.FileOperate;

/**
 * rabbit提供的http接口类
 * 
 * @author yu.yao 2018年8月1日
 *
 */
@RestController
@RequestMapping("/api")
@EnableAutoConfiguration
public class RabbitController {
  @Resource
  private InfoPointService infoPointService;

  /**
   * 获取所有信息点
   * 
   * @author yu.yao
   * @param content 文书内容
   * @return
   */
  @RequestMapping("/infopoints/all")
  public WebApiResult<Map<String, Object>> RabbitQueryPoints(
      @RequestParam("variable") String variable, @RequestParam("name") String name,
      @RequestParam("version") String version, @RequestParam("w") String w,
      @RequestParam("org") String org, @RequestParam("ay") String ay,
      @RequestParam("majoray") String majorAy, @RequestParam("doctype") String docType,
      @RequestParam("page") Integer pageNum, @RequestParam("rows") Integer pageSize) {
    if (pageNum == null || pageNum <= 0) {
      pageNum = 1;
    }
    if (pageSize == null || pageSize <= 0) {
      pageSize = 50;
    }
    QueryDTO dto = new QueryDTO();
    dto.setVariable(variable);
    dto.setName(name);
    dto.setVersion(version);
    dto.setW(w);
    dto.setDoctype(docType);
    dto.setMajoray(majorAy);
    dto.setAy(ay);
    dto.setOrg(org);
    List<InfoPoint> result = new ArrayList<InfoPoint>();
    long records = 0;
    long total = 0;
    try {
      records = infoPointService.queryCount(dto);
      total = records / pageSize + 1;
      dto.setPageNum(pageNum);
      dto.setPageSize(pageSize);
      result = infoPointService.queryByCondition(dto);
    } catch (RabbitException e) {
      e.printStackTrace();
    }
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("list", result);
    data.put("total", total);
    data.put("records", records);
    data.put("page", pageNum);
    return new WebApiResult<Map<String, Object>>(RabbitResultCode.RABBIT_SUCCESS, data);
  }

  /**
   * 抽取某个案由的全量信息点接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @return
   */
  @RequestMapping("/extraction/{ay}")
  public WebApiResult<RabbitInfo> RabbitInterpretAy(@RequestParam("docpath") String docpath,
      @PathVariable String ay) {
    RabbitApi api = new RabbitApi();
    String document = FileOperate.readTxt(docpath, "utf-8");
    WebApiResult<RabbitInfo> result = api.RabbitInterpret(document, ay);
    return result;
  }

  /**
   * 抽取全量信息点接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @return
   */
  @RequestMapping("/extraction/all")
  public WebApiResult<RabbitInfo> RabbitInterpretAll(@RequestParam("docpath") String docpath) {
    RabbitApi api = new RabbitApi();
    String document = FileOperate.readTxt(docpath, "utf-8");
    WebApiResult<RabbitInfo> result = api.RabbitInterpret(document);
    return result;
  }

  /**
   * 抽取指定信息点接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @param List<Long> 信息点id列表
   * @return
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  @ResponseBody
  @RequestMapping(value = "/extraction/portion", method = RequestMethod.POST)
  public WebApiResult<RabbitInfo> RabbitInterpretPortion(@RequestBody Map<String, Object> map) {
    RabbitApi api = new RabbitApi();
    String ay = (String) map.get("ay");
    String document = FileOperate.readTxt((String) map.get("docpath"), "utf-8");
    List<InfoPoint> list = new ArrayList<InfoPoint>();
    try {
      for (Map ele : (List<Map>) map.get("rows")) {
        InfoPoint p = (InfoPoint) ClassUtil.map2Obj(ele, InfoPoint.class);
        list.add(p);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    WebApiResult<RabbitInfo> result = api.RabbitInterpret(document, ay, list);
    return result;
  }

  /**
   * 抽取指定信息点接口
   * 
   * @author yu.yao
   * @param content 文书内容
   * @param List<Long> 信息点id列表
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/extraction/document", method = RequestMethod.POST)
  public WebApiResult<RabbitInfo> RabbitInterpretContent(@RequestBody Map<String, Object> map) {
    RabbitApi api = new RabbitApi();
    WebApiResult<RabbitInfo> result = null;
    String document = (String) map.get("document");
    if (map.containsKey("ay") && map.containsKey("rows")) {
      String ay = (String) map.get("ay");
      result = api.RabbitInterpret(document, ay, getPointList(map));
    } else if (!map.containsKey("ay") && !map.containsKey("rows")) {
      result = api.RabbitInterpret(document);
    } else if (!map.containsKey("ay") && map.containsKey("rows")) {
      result = api.RabbitInterpret(document, getPointList(map));
    } else if (map.containsKey("ay") && !map.containsKey("rows")) {
      String ay = (String) map.get("ay");
      result = api.RabbitInterpret(document, ay);
    }
    return result;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private List<InfoPoint> getPointList(Map<String, Object> map) {
    List<InfoPoint> list = new ArrayList<InfoPoint>();
    try {
      for (Map ele : (List<Map>) map.get("rows")) {
        InfoPoint p = (InfoPoint) ClassUtil.map2Obj(ele, InfoPoint.class);
        list.add(p);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }
}
