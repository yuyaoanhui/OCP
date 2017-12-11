package com.futuredata.judicature.sdk.utils;

import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 收到请求的时间+随机数+本地ip 生成requestId
 * 
 * @author yu.yao
 *
 */
public class UniqRequestIdGen {
  /**
   * 自增id，用于requestId的生成过程
   */
  private static AtomicLong lastId = new AtomicLong();
  /**
   * 启动加载时的时间戳(纳秒)，用于requestId的生成过程
   */
  private static final long startTimeStamp = System.nanoTime();

  /**
   * 本地ip地址
   */
  private static final String ip = getIp();

  private static String getIp() {
    String genIp = "";
    Set<String> addrs = LocalIpAddressUtil.resolveLocalIps();
    if (addrs != null && !addrs.isEmpty()) {
      for (String addr : addrs) {
        genIp = genIp.concat(addr).concat(".");
      }
    } else {
      throw new NullPointerException("无法获取本地ip.");
    }
    return genIp;
  }

  /**
   * <p>
   * 1.将ip转换为定长8个字符的16进制表示形式：255.255.255.255 -> FFFFFFFF
   * </p>
   * <p>
   * 2.将该字符串的哈希码转为十六进制
   * </p>
   * 
   * @param ip
   * @return
   */
  private static String hexIp(String ips) {
    StringBuilder sb = new StringBuilder();
    for (String seg : ips.split("\\.")) {
      String h = Long.toString(Long.parseLong(seg), Character.MAX_RADIX);
      if (h.length() == 1)
        sb.append("0");
      sb.append(h);
    }
    return Integer.toHexString(sb.toString().hashCode());
  }

  /**
   * 规则：hexIp(ip)-base36(timestamp)-AtomicLong
   * 
   * @return
   */
  public static String generatorRequestId() {
    return hexIp(ip) + "-" + Long.toString(startTimeStamp, Character.MAX_RADIX) + "-"
        + lastId.incrementAndGet();
  }
}
