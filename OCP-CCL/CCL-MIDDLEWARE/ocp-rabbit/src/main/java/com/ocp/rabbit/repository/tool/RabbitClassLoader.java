package com.ocp.rabbit.repository.tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.ocp.rabbit.plugin.AbstractPlugin.Context;

/**
 * Rabbit项目自定义的ClassLoader,用于实现资源隔离
 * 
 * @author yu.yao 2018年8月5日
 *
 */
public class RabbitClassLoader extends ClassLoader {

  private static volatile int count;
  private static volatile RabbitClassLoader instance;

  private RabbitClassLoader() {
    synchronized (Context.class) {// 防止反射破坏单例
      if (count > 0) {
        throw new RuntimeException("禁止创建两个实例");
      }
      count = 1;
    }
  }

  public static RabbitClassLoader getInstance() {
    return instance;
  }

  private synchronized static RabbitClassLoader getClassLoader() {// double check
    if (instance == null) {
      synchronized (RabbitClassLoader.class) {
        if (instance == null) {
          instance = new RabbitClassLoader();
        }
      }
    }
    return instance;
  }

  static {
    getClassLoader();
  }
  public static final String fileType = ".class";

  private static final String[] sysPackageHead = new String[] {"oracle", "com.sun", "com.oracle",
      "javax", "java", "jdk", "sun", "org.ietf", "org.jcp", "org.omg", "org.w3c.dom", "org.xml.sax",
      "sun.reflect", "java.util", "sun.reflect.annotation", "sun.net", "javafx", "netscape"};

  /**
   * 重写findClass指明如何获取类的字节码流
   * 
   * @throws ClassNotFoundException
   */
  @Override
  public synchronized Class<?> findClass(String name) {
    byte[] data = null;
    try {
      data = loadClassData(name);
    } catch (Exception e) {
      e.printStackTrace();
    }
    for (String sysName : sysPackageHead) {
      if (name.startsWith(sysName)) {
        return null;
      }
    }
    return defineClass(name, data, 0, data.length);// 将一个 byte数组转换为 Class类的实例
  }

  /**
   * 
   * 重写loadClass抛弃双亲委派机制
   * 
   * @return
   * 
   * @throws ClassNotFoundException
   */
  @Override
  public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
    // 1.检查该类是否已被加载
    Class<?> c = findLoadedClass(name);
    // 2.若没有被加载,手动加载
    if (c == null) {
      for (String sysName : sysPackageHead) {
        if (name.startsWith(sysName)) {// 如果式JRE定义类
          c = super.loadClass(name, true);
          return c;
        }
      }
      c = findClass(name);// 应用类
    }
    return c;
  }

  private byte[] loadClassData(String name) throws Exception {
    InputStream fis = null;
    byte[] data = null;
    try {
      fis = RabbitClassLoader.class.getResourceAsStream("/" + name.replace(".", "/") + fileType);
      if (fis == null) {
        String jarPath =
            RabbitClassLoader.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        URL url = new URL("jar:file" + jarPath + "!/" + name.replace(".", "/") + fileType);
        fis = url.openStream();
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      int ch = 0;
      while ((ch = fis.read()) != -1) {
        baos.write(ch);
      }
      data = baos.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
      throw new ClassNotFoundException();
    } finally {
      fis.close();
    }
    return data;
  }

}
