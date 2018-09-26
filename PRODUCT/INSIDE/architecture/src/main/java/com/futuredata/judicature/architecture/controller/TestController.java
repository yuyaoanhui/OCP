package com.ocp.judicature.architecture.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp.api.PaladinApi;
import com.ocp.base.controller.AbstractBaseController;
import com.ocp.base.result.BaseResultCode;
import com.ocp.common.result.WebApiResult;

@RestController
@RequestMapping("/api/v1.1")
@EnableAutoConfiguration
public class TestController extends AbstractBaseController {
  @RequestMapping("/hi")
  public WebApiResult<Map<String, Object>> queryAll() {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("yu.yao", "28");
    return new WebApiResult<Map<String, Object>>(BaseResultCode.SYS_SUCCESS, data);
  }

  @RequestMapping("/extraction")
  public WebApiResult<String> extractAPI() {
    PaladinApi pa = new PaladinApi("paladin.properties");
    String filePath = "/home/public/cpws/离婚纠纷/安徽省/泰和县人民法院/f0a2ede3-e53b-4e55-b403-a77500212025.txt";
    String result = pa.paladinInterpret("{\"file_path\":\"" + filePath + "\","
        + "\"res_dir\":\"resource-court/judgement\"," + "\"an_you\":\"离婚纠纷\","
        + "\"input_encode\":\"utf8\"," + "\"output_encode\":\"utf8\"}");
    System.out.println("ok");
    return new WebApiResult<String>(BaseResultCode.SYS_SUCCESS, result);
  }

}
