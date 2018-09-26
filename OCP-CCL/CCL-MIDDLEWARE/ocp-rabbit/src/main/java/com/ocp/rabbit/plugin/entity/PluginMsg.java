package com.ocp.rabbit.plugin.entity;

import java.util.List;
import java.util.UUID;

/**
 * 插件之间传递的消息封装类
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public class PluginMsg {
  private String id = UUID.randomUUID().toString();// 唯一ID
  private String sender;// 发送者,pulgin的全限定名
  private List<String> recievers;// 接收者们,pulgin的全限定名
  private PluginCmd command;// 指令

  public String getId() {
    return id;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public List<String> getRecievers() {
    return recievers;
  }

  public void setRecievers(List<String> recievers) {
    this.recievers = recievers;
  }

  public PluginCmd getCommand() {
    return command;
  }

  public void setCommand(PluginCmd command) {
    this.command = command;
  }

}
