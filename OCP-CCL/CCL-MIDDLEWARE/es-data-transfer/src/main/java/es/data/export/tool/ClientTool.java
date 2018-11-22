package es.data.export.tool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import es.data.export.util.PropertiesUtil;

/**
 * ES单例客户端工具
 * 
 * @author yu.yao 2018年6月23日
 *
 */

public class ClientTool {

  private volatile static TransportClient client;

  private ClientTool() {}

  public static TransportClient getInstance() {
    if (client == null) {
      synchronized (ClientTool.class) {
        if (client == null) {
          try {
            Settings settings = Settings.settingsBuilder().put("client.transport.sniff", true)
                .put("cluster.name", PropertiesUtil.getProperty("cluster.old.name")).build();
            String ipAndPort = PropertiesUtil.getProperty("cluster.old.ip.port");
            List<InetSocketTransportAddress> transportAddress = getAllAddress(ipAndPort);
            client = TransportClient.builder().settings(settings).build().addTransportAddresses(
                transportAddress.toArray(new InetSocketTransportAddress[transportAddress.size()]));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return client;
  }

  private static List<InetSocketTransportAddress> getAllAddress(String ipAndPort) {

    HashMap<String, Integer> ips = new HashMap<String, Integer>();

    if (ipAndPort != null) {
      String[] ipArray = ipAndPort.split(";");
      if (ipArray.length > 0) {
        for (String str : ipArray) {
          if (str != null) {
            String[] portArray = str.split(":");
            ips.put(portArray[0], Integer.parseInt(portArray[1]));
          }
        }
      }
    }

    List<InetSocketTransportAddress> addressList = new ArrayList<InetSocketTransportAddress>();
    for (String ip : ips.keySet()) {
      try {
        addressList.add(new InetSocketTransportAddress(InetAddress.getByName(ip), ips.get(ip)));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
    return addressList;
  }
}
