package es.data.migration.tool;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import es.data.migration.util.PropertiesUtil;

/**
 * ES单例客户端工具
 * 
 * @author yu.yao 2018年6月23日
 *
 */

public class ClientTool {

  private volatile static TransportClient client;

  private ClientTool() {}

  @SuppressWarnings("resource")
  public static TransportClient getInstance() {
    if (client == null) {
      synchronized (ClientTool.class) {
        if (client == null) {
          Settings settings = Settings.builder().put("client.transport.sniff", true)
              .put("cluster.name", PropertiesUtil.getProperty("cluster.new.name")).build();
          TransportAddress transportAddress =
              getAllAddress(PropertiesUtil.getProperty("cluster.new.ip.port")).get(0);
          try {
            client = new PreBuiltTransportClient(settings).addTransportAddress(
                new TransportAddress(InetAddress.getByName(transportAddress.getAddress()), 9300));
          } catch (UnknownHostException e) {
            e.printStackTrace();
          }
        }
      }
    }
    return client;
  }

  private static List<TransportAddress> getAllAddress(String ipAndPort) {

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

    List<TransportAddress> addressList = new ArrayList<TransportAddress>();
    for (String ip : ips.keySet()) {
      try {
        addressList.add(new TransportAddress(InetAddress.getByName(ip), ips.get(ip)));
      } catch (UnknownHostException e) {
        e.printStackTrace();
      }
    }
    return addressList;
  }
}
