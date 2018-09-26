package com.ocp.rabbit.repository.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件(夹)操作类: <br>
 * 1.读取文件内容<br>
 * 2.新建删除移动复制</br>
 * 
 * @author yu.yao 2018年8月1日
 *
 */
public class FileOperate {
  private String message;

  public FileOperate() {}

  /**
   * 读取文本文件内容
   * 
   * @author yu.yao
   * @param filePathAndName 带有完整绝对路径的文件名
   * @param encoding 文本文件打开的编码方式
   * @return 返回文本文件的内容
   */
  public static String readTxt(String filePathAndName, String encoding) {
    File file = new File(filePathAndName);
    if (!file.exists()) {
      return "";
    }
    encoding = encoding.trim();
    StringBuffer str = new StringBuffer("");
    String st;
    FileInputStream fs = null;
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      fs = new FileInputStream(filePathAndName);
      if (encoding.equals("")) {
        isr = new InputStreamReader(fs);
      } else {
        isr = new InputStreamReader(fs, encoding);
      }
      br = new BufferedReader(isr);
      String data;
      while ((data = br.readLine()) != null) {
        str.append(data);
        str.append("\n");
      }
      st = str.toString();
    } catch (Exception e1) {
      st = "";
      e1.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
        if (isr != null) {
          isr.close();
        }
        if (fs != null) {
          fs.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }

    return st;
  }

  public static String readTxt(InputStream fs, String encoding) {
    encoding = encoding.trim();
    StringBuffer str = new StringBuffer("");
    String st;
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      if (encoding.equals("")) {
        isr = new InputStreamReader(fs);
      } else {
        isr = new InputStreamReader(fs, encoding);
      }
      br = new BufferedReader(isr);
      String data;
      while ((data = br.readLine()) != null) {
        str.append(data);
        str.append("\n");
      }
      st = str.toString();
    } catch (Exception e1) {
      st = "";
      e1.printStackTrace();
    } finally {
      try {
        if (br != null) {
          br.close();
        }
        if (isr != null) {
          isr.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }

    return st;
  }

  public static List<String> readTxtFromJarToArr(String filePathAndName, String encoding) {
    encoding = encoding.trim();
    InputStream in = FileOperate.class.getClassLoader().getResourceAsStream(filePathAndName);
    List<String> list = FileOperate.readTxtToArrays(in, encoding);

    return list;
  }

  public static List<String> readTxtToArrays(String filePathAndName, String encoding) {
    encoding = encoding.trim();
    List<String> lst = new ArrayList<String>();
    try {
      FileInputStream fs = new FileInputStream(filePathAndName);
      InputStreamReader isr;
      if (encoding.equals("")) {
        isr = new InputStreamReader(fs);
      } else {
        isr = new InputStreamReader(fs, encoding);
      }
      BufferedReader br = new BufferedReader(isr);
      try {
        String data;
        while ((data = br.readLine()) != null) {
          if (data.indexOf("#") == 1) {
            continue;
          }
          lst.add(data);
        }
        br.close();
      } catch (Exception e) {
        return new ArrayList<String>();
      }
    } catch (IOException es) {
      return new ArrayList<String>();
    }
    return lst;
  }

  public static List<String> readTxtToArrays(InputStream fs, String encoding) {
    encoding = encoding.trim();
    List<String> lst = new ArrayList<String>();
    InputStreamReader isr = null;
    BufferedReader br = null;
    try {
      if (encoding.equals("")) {
        isr = new InputStreamReader(fs);
      } else {
        isr = new InputStreamReader(fs, encoding);
      }
      br = new BufferedReader(isr);
      String data;
      while ((data = br.readLine()) != null) {
        lst.add(data);
      }
    } catch (IOException es) {
      es.printStackTrace();
    } finally {
      try {
        if (isr != null) {
          isr.close();
        }
        if (br != null) {
          br.close();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return lst;
  }

  /**
   * 新建目录
   * 
   * @author yu.yao
   * @param folderPath 目录
   * @return 返回目录创建后的路径
   */
  private static String createFolder(String folderPath) {
    String txt = folderPath;
    try {
      File myFilePath = new File(txt);
      txt = folderPath;
      if (!myFilePath.exists()) {
        myFilePath.mkdir();
      }
    } catch (Exception e) {
      System.out.println("创建目录操作出错");
    }
    return txt;
  }

  /**
   * 多级目录创建
   * 
   * @author yu.yao
   * @param folderPath 准备要在本级目录下创建新目录的目录路径 例如 c:myf
   * @param paths 无限级目录参数，各级目录以单数线区分 例如 a|b|c
   * @return 返回创建文件后的路径 例如 c:myfa c
   */
  public String createFolders(String folderPath, String paths) {
    String txts = folderPath;
    try {
      String txt;
      txts = folderPath;
      StringTokenizer st = new StringTokenizer(paths, "|");
      while (st.hasMoreTokens()) {
        txt = st.nextToken().trim();
        if (txts.lastIndexOf("/") != -1) {
          txts = createFolder(txts + txt);
        } else {
          txts = createFolder(txts + txt + "/");
        }
      }
    } catch (Exception e) {
      message = "创建目录操作出错！";
    }
    return txts;
  }

  /**
   * 新建文件
   * 
   * @author yu.yao
   * @param filePathAndName 文本文件完整绝对路径及文件名
   * @param fileContent 文本文件内容
   */
  public void createFile(String filePathAndName, String fileContent) {

    try {
      String filePath = filePathAndName;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      if (!myFilePath.exists()) {
        myFilePath.createNewFile();
      }
      FileWriter resultFile = new FileWriter(myFilePath);
      PrintWriter myFile = new PrintWriter(resultFile);
      String strContent = fileContent;
      myFile.println(strContent);
      myFile.close();
      resultFile.close();
    } catch (Exception e) {
      message = "创建文件操作出错";
    }
  }

  /**
   * 有编码方式的文件创建
   * 
   * @author yu.yao
   * @param filePathAndName 文本文件完整绝对路径及文件名
   * @param fileContent 文本文件内容
   * @param encoding 编码方式 例如 GBK 或者 UTF-8
   */
  public static void createFile(String filePathAndName, String fileContent, String encoding) {

    try {
      String filePath = filePathAndName;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      if (!myFilePath.exists()) {
        myFilePath.createNewFile();
      }
      PrintWriter myFile = new PrintWriter(myFilePath, encoding);
      String strContent = fileContent;
      myFile.println(strContent);
      myFile.close();
    } catch (Exception e) {
      System.out.println("创建文件操作出错");
    }
  }

  public static void createFile(String fileName, String content, boolean isAppend) {
    FileWriter fstream = null;
    BufferedWriter out = null;
    try {
      fstream = new FileWriter(fileName, isAppend);
      out = new BufferedWriter(fstream);
      out.write(content);
      out.close();
    } catch (Exception e) {
      System.out.println("写文件失败，文件：");
    } finally {
      try {
        fstream.close();
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  public void createFile(String fileName, String content, boolean isAppend, FileWriter fstream,
      BufferedWriter out) {
    try {
      fstream = new FileWriter(fileName, isAppend);
      out = new BufferedWriter(fstream);
      out.write(content);
      out.close();
    } catch (Exception e) {
      message = "写文件失败，文件：";
    } finally {
      try {
        fstream.close();
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }



  /**
   * 删除文件
   * 
   * @author yu.yao
   * @param filePathAndName 文本文件完整绝对路径及文件名
   * @return Boolean 成功删除返回true遭遇异常返回false
   */
  public boolean delFile(String filePathAndName) {
    boolean bea = false;
    try {
      String filePath = filePathAndName;
      File myDelFile = new File(filePath);
      if (myDelFile.exists()) {
        myDelFile.delete();
        bea = true;
      } else {
        bea = false;
        message = (filePathAndName + "删除文件操作出错");
      }
    } catch (Exception e) {
      message = e.toString();
    }
    return bea;
  }

  /**
   * 删除文件夹
   * 
   * @author yu.yao
   * @param folderPath 文件夹完整绝对路径
   */
  private void delFolder(String folderPath) {
    try {
      delAllFile(folderPath); // 删除完里面所有内容
      String filePath = folderPath;
      filePath = filePath.toString();
      File myFilePath = new File(filePath);
      myFilePath.delete(); // 删除空文件夹
    } catch (Exception e) {
      message = ("删除文件夹操作出错");
    }
  }

  /**
   * 删除指定文件夹下所有文件
   * 
   * @author yu.yao
   * @param path 文件夹完整绝对路径
   */
  private boolean delAllFile(String path) {
    boolean bea = false;
    File file = new File(path);
    if (!file.exists()) {
      return bea;
    }
    if (!file.isDirectory()) {
      return bea;
    }
    String[] tempList = file.list();
    File temp;
    for (String aTempList : tempList) {
      if (path.endsWith(File.separator)) {
        temp = new File(path + aTempList);
      } else {
        temp = new File(path + File.separator + aTempList);
      }
      if (temp.isFile()) {
        temp.delete();
      }
      if (temp.isDirectory()) {
        delAllFile(path + "/" + aTempList);// 先删除文件夹里面的文件
        delFolder(path + "/" + aTempList);// 再删除空文件夹
        bea = true;
      }
    }
    return bea;
  }

  /**
   * 复制单个文件
   * 
   * @author yu.yao
   * @param oldPathFile 准备复制的文件源
   * @param newPathFile 拷贝到新绝对路径带文件名
   */
  private void copyFile(String oldPathFile, String newPathFile) {
    try {
      int bytesum = 0;
      int byteread;
      File oldfile = new File(oldPathFile);
      if (oldfile.exists()) { // 文件存在时
        InputStream inStream = new FileInputStream(oldPathFile); // 读入原文件
        FileOutputStream fs = new FileOutputStream(newPathFile);
        byte[] buffer = new byte[1444];
        while ((byteread = inStream.read(buffer)) != -1) {
          bytesum += byteread; // 字节数 文件大小
          System.out.println(bytesum);
          fs.write(buffer, 0, byteread);
        }
        inStream.close();
        fs.close();
      }
    } catch (Exception e) {
      message = ("复制单个文件操作出错");
    }
  }

  /**
   * 复制整个文件夹的内容
   * 
   * @author yu.yao
   * @param oldPath 准备拷贝的目录
   * @param newPath 指定绝对路径的新目录
   */
  private void copyFolder(String oldPath, String newPath) {
    try {
      new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
      File a = new File(oldPath);
      String[] file = a.list();
      File temp;
      for (int i = 0; i < file.length; i++) {
        if (oldPath.endsWith(File.separator)) {
          temp = new File(oldPath + file[i]);
        } else {
          temp = new File(oldPath + File.separator + file[i]);
        }
        if (temp.isFile()) {
          FileInputStream input = new FileInputStream(temp);
          FileOutputStream output =
              new FileOutputStream(newPath + "/" + (temp.getName()).toString());
          byte[] b = new byte[1024 * 5];
          int len;
          while ((len = input.read(b)) != -1) {
            output.write(b, 0, len);
          }
          output.flush();
          output.close();
          input.close();
        }
        if (temp.isDirectory()) {// 如果是子文件夹
          copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
        }
      }
    } catch (Exception e) {
      message = "复制整个文件夹内容操作出错";
    }
  }

  /**
   * 移动文件
   * 
   * @author yu.yao
   * @param oldPath 源路径
   * @param newPath 目标路径
   * @return
   */
  public void moveFile(String oldPath, String newPath) {
    copyFile(oldPath, newPath);
    delFile(oldPath);
  }

  /**
   * 移动目录
   * 
   * @author yu.yao
   * @param oldPath 源路径
   * @param newPath 目标路径
   * @return
   */
  public void moveFolder(String oldPath, String newPath) {
    copyFolder(oldPath, newPath);
    delFolder(oldPath);
  }

  /**
   * 遍历某个文件夹下所有文件，返回文件名列表。并按照文件名升序。 <br>
   * 先从外部找资源，若果找不到从jar包中找
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static List<String> listFilesInDic(String dicPath) {
    List<String> fileNameList = new ArrayList<String>();
    String path = System.getProperty("java.class.path");
    File dir = new File(FileOperate.class.getResource(dicPath).getPath());
    File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
    if (files != null && files.length > 0) {
      for (int i = 0; i < files.length; i++) {
        if (files[i].isFile()) {
          String strFileName = files[i].getName();
          fileNameList.add(strFileName);
        }
      }
    } else {
      try {
        path = URLDecoder.decode(path, "UTF-8");
        if (path.endsWith(".jar")) {
          @SuppressWarnings("resource")
          JarFile jarFile = new JarFile(path);
          Enumeration<?> jarFiles = jarFile.entries();
          while (jarFiles.hasMoreElements()) {
            JarEntry entry = (JarEntry) jarFiles.nextElement();
            String name = entry.getName();
            String dic = name;
            if (name.lastIndexOf("/") >= 0
                && (name.endsWith(".js") || name.endsWith("structure.xml"))) {
              dic = name.substring(0, name.lastIndexOf("/") + 1);
            }
            if (dic.equals(dicPath.substring(1))
                && (name.endsWith(".js") || name.endsWith("structure.xml"))) {
              String[] names = name.split("/");
              fileNameList.add(names[names.length - 1]);
            }
          }
        }
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
    Collections.sort(fileNameList);
    return fileNameList;
  }

  public String getMessage() {
    return this.message;
  }
}

