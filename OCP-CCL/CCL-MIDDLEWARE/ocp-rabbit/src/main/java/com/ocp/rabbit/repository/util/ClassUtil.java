package com.ocp.rabbit.repository.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类加载器操作工具类
 * 
 * @author yu.yao 2018年8月3日
 *
 */
public class ClassUtil {

  private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

  private static final Vector<Class<?>> loadedClasses = getLoaderClasses();

  public static Vector<Class<?>> getLoadedClasses() {
    return loadedClasses;
  }

  /**
   * 获取已加载的所有类
   * 
   * @author yu.yao
   * @param
   * @return
   */
  @SuppressWarnings("unchecked")
  private static Vector<Class<?>> getLoaderClasses() {
    Field field;
    Vector<Class<?>> result = null;
    try {
      field = ClassLoader.class.getDeclaredField("classes");
      field.setAccessible(true);
      result = (Vector<Class<?>>) field.get(ClassLoader.getSystemClassLoader());
    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e) {
      logger.error("getLoaderClasses failed!", e);
    }
    return result;
  }

  /**
   * 通过父类的所有子类
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static List<Class<?>> getAllSubClasses(Class<?> c) {
    List<Class<?>> returnClassList = new ArrayList<Class<?>>();
    String packageNames[] =
        PropertiesUtil.getProperty("application.properties", "plugin.base-package").split(",");
    for (String packageName : packageNames) {
      List<Class<?>> allClass = getClasses(packageName);
      if (allClass != null) {
        for (Class<?> cls : allClass) {
          // 判断是否是同一个接口或父类
          if (c.isAssignableFrom(cls)) {
            // 本身不加入进去
            if (!c.equals(cls)) {
              returnClassList.add(cls);
            }
          }
        }
      }
    }
    return returnClassList;
  }

  /**
   * 从包package中获取所有的Class
   * 
   * @author yu.yao
   * @param pack
   * @return
   */
  private static List<Class<?>> getClasses(String packageName) {
    List<Class<?>> classes = new ArrayList<Class<?>>();
    // 是否循环迭代
    boolean recursive = true;
    String packageDirName = packageName.replace('.', '/');
    // 定义一个枚举的集合 并进行循环来处理这个目录下的things
    Enumeration<URL> dirs;
    try {
      dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
      while (dirs.hasMoreElements()) {
        URL url = dirs.nextElement();
        String protocol = url.getProtocol();
        // 如果是以文件的形式保存在服务器上,扫描整个包下的文件 并添加到集合中
        if ("file".equals(protocol)) {
          String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
          findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
        } else if ("jar".equals(protocol)) {// 如果是jar包文件
          JarFile jar;
          try {
            jar = ((JarURLConnection) url.openConnection()).getJarFile();
            // 从此jar包 得到一个枚举类
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
              // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
              JarEntry entry = entries.nextElement();
              String name = entry.getName();
              if (name.charAt(0) == '/') {// 如果是以/开头的
                name = name.substring(1);// 获取后面的字符串
              }
              // 如果前半部分和定义的包名相同
              if (name.startsWith(packageDirName)) {
                int idx = name.lastIndexOf('/');
                // 如果以"/"结尾 是一个包
                if (idx != -1) {
                  // 获取包名 把"/"替换成"."
                  packageName = name.substring(0, idx).replace('/', '.');
                }
                // 如果可以迭代下去 并且是一个包
                if ((idx != -1) || recursive) {
                  // 如果是一个.class文件 而且不是目录
                  if (name.endsWith(".class") && !entry.isDirectory()) {
                    // 去掉后面的".class" 获取真正的类名
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    try {
                      // 添加到classes
                      classes.add(Class.forName(packageName + '.' + className));
                    } catch (ClassNotFoundException e) {
                      e.printStackTrace();
                    }
                  }
                }
              }
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return classes;
  }

  /**
   * 以文件的形式来获取包下的所有Class
   * 
   * @author yu.yao
   * @param packageName
   * @param packagePath
   * @param recursive
   * @param classes
   */
  @SuppressWarnings("rawtypes")
  private static void findAndAddClassesInPackageByFile(String packageName, String packagePath,
      final boolean recursive, List<Class<?>> classes) {
    // 获取此包的目录 建立一个File
    File dir = new File(packagePath);
    // 如果不存在或者 也不是目录就直接返回
    if (!dir.exists() || !dir.isDirectory()) {
      return;
    }
    // 如果存在 就获取包下的所有文件 包括目录
    File[] dirfiles = dir.listFiles(new FileFilter() {
      // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
      public boolean accept(File file) {
        return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
      }
    });
    // 循环所有文件
    for (File file : dirfiles) {
      // 如果是目录 则继续扫描
      if (file.isDirectory()) {
        findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(),
            recursive, classes);
      } else {
        // 如果是java类文件 去掉后面的.class 只留下类名
        String className = file.getName().substring(0, file.getName().length() - 6);
        try {
          // 添加到集合中去
          Class clazz = Class.forName(packageName + '.' + className);
          if (!classes.contains(clazz)) {
            classes.add(clazz);
          }
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static Object map2Obj(Map<String, Object> map, Class<?> clz) throws Exception {
    Object obj = clz.newInstance();
    Field[] declaredFields = obj.getClass().getDeclaredFields();
    for (Field field : declaredFields) {
      int mod = field.getModifiers();
      if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
        continue;
      }
      field.setAccessible(true);
      field.set(obj, map.get(field.getName()));
    }
    return obj;
  }
}
