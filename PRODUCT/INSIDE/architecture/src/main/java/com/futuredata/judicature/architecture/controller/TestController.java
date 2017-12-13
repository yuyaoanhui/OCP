package com.futuredata.judicature.architecture.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.futuredata.judicature.base.controller.AbstractBaseController;
import com.futuredata.judicature.base.result.BaseResultCode;
import com.futuredata.judicature.common.result.WebApiResult;

@RestController
@RequestMapping("/api/v1.1")
@EnableAutoConfiguration
public class TestController extends AbstractBaseController {
  @RequestMapping("/hi")
  public WebApiResult<Map<String, Object>> queryAll() {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("yu.yao", "27");
    data.put("haichao.sang", "33");
    return new WebApiResult<Map<String, Object>>(BaseResultCode.SYS_SUCCESS, data);
  }

}
