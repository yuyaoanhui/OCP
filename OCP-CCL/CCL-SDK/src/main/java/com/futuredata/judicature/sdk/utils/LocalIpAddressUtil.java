package com.futuredata.judicature.sdk.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * 获取本地ip的工具
 * 
 * @author yu.yao
 *
 */
public class LocalIpAddressUtil {

  /**
   * 获取本地ip地址，有可能会有多个地址, 若有多个网卡则会搜集多个网卡的ip地址
   * 
   * @return
   */
  public static Set<String> resolveLocalIps() {
    Set<InetAddress> addrs = resolveLocalAddresses();
    Set<String> ret = new HashSet<String>();
    for (InetAddress addr : addrs)
      ret.add(addr.getHostAddress());
    return ret;
  }

  public static Set<InetAddress> resolveLocalAddresses() {
    Set<InetAddress> addrs = new HashSet<InetAddress>();
    Enumeration<NetworkInterface> ns = null;
    try {
      ns = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      e.printStackTrace();
    }
    while (ns != null && ns.hasMoreElements()) {
      NetworkInterface n = ns.nextElement();
      Enumeration<InetAddress> is = n.getInetAddresses();
      while (is.hasMoreElements()) {
        InetAddress i = is.nextElement();
        if (!i.isLoopbackAddress() && !i.isLinkLocalAddress() && !i.isMulticastAddress()
            && !isSpecialIp(i.getHostAddress()))
          addrs.add(i);
      }
    }
    return addrs;
  }

  private static boolean isSpecialIp(String ip) {
    if (ip.contains(":"))
      return true;
    if (ip.startsWith("127."))
      return true;
    if (ip.startsWith("169.254."))
      return true;
    if (ip.equals("255.255.255.255"))
      return true;
    return false;
  }
}
