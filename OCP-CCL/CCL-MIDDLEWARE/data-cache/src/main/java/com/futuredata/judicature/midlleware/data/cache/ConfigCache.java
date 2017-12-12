package com.futuredata.judicature.midlleware.data.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 缓存配置信息在内存中的管理类
 * 
 * @author yu.yao
 *
 */
public class ConfigCache implements Initor {

  private static final Map<String, Object> caches = new HashMap<String, Object>();

  /**
   * {@inheritDoc}
   */
  @Override
  public void init() {
    // TODO Auto-generated method stub
    caches.put(null, null);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void refresh(String type) {
    // TODO Auto-generated method stub

  }

}
