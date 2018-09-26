package com.ocp.rabbit.repository.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class FileUtils {

  private static Logger logger = Logger.getLogger(FileUtils.class);


  private static final int BUFFER_SIZE = 4096;

  /*** 从zip文件中解压 开始 **/
  /***
   * 从Zip文件中解压文件，并删除原文件。
   *
   * @param zipfile Input .zip file
   * @param dest Output directory
   */
  public static List<String> unZip(String zipfile, String dest) {
    return unZip(new File(zipfile), dest);
  }

  public static List<String> unZip(File zipfile, String dest) {

    List<String> files = new ArrayList<String>();

    try {
      File outdir = new File(dest);

      ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
      ZipEntry entry;
      String name, dir;
      while ((entry = zin.getNextEntry()) != null) {
        name = entry.getName();
        if (entry.isDirectory()) {
          mkdirs(outdir, name);
          continue;
        }
        dir = dirpart(name);
        if (dir != null) {
          mkdirs(outdir, dir);
        }

        extractFile(zin, outdir, name);
        files.add(dest + name);
      }
      zin.close();
    } catch (IOException e) {
      logger.error("文件解压失败，文件：" + zipfile.getPath());
    } finally {
      zipfile.delete();
    }
    return files;
  }

  private static void extractFile(ZipInputStream in, File outdir, String name) throws IOException {

    byte[] buffer = new byte[BUFFER_SIZE];
    BufferedOutputStream out =
        new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
    int count = -1;
    while ((count = in.read(buffer)) != -1)
      out.write(buffer, 0, count);
    out.close();
  }

  private static void mkdirs(File outdir, String path) {
    File d = new File(outdir, path);
    if (!d.exists())
      d.mkdirs();
  }

  private static String dirpart(String name) {
    int s = name.lastIndexOf(File.separatorChar);
    return s == -1 ? null : name.substring(0, s);
  }



  /****** 根据文件流的头部，判断文件类型。结束 ****/


  /**
   * 移动文件
   */
  public static void moveFile(String src, String dest) {

    moveFile(new File(src), dest);
  }

  public static void moveFile(File src, String dest) {

    int index = dest.lastIndexOf(".");
    if (index == -1) {
      dest = dest + src.getName();
    }

    moveFile(src, new File(dest));
  }

  public static void moveFile(File src, File dest) {
    copyFile(src, dest);
    src.delete();
  }

  /**
   * 复制文件
   */
  public static void copyFile(String src, String dest) {

    copyFile(new File(src), dest);
  }

  public static void copyFile(File src, String dest) {

    int index = dest.lastIndexOf(".");
    if (index == -1) {
      dest = dest + src.getName();
    }

    copyFile(src, dest);
  }

  public static void copyFile(File src, File dest) {
    FileInputStream fi = null;
    FileOutputStream fo = null;
    FileChannel in = null;
    FileChannel out = null;
    try {

      fi = new FileInputStream(src);
      fo = new FileOutputStream(dest);
      in = fi.getChannel();// 得到对应的文件通道
      out = fo.getChannel();// 得到对应的文件通道
      in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fi.close();
        in.close();
        fo.close();
        out.close();
      } catch (Exception e) {
        logger.error("复制文件失败，源文件：" + src.getPath() + ",目标地址:" + dest.getPath(), e);
      }
    }
  }

  /**
   * 文件重命名，改为案件ID，并添加后缀
   *
   * @param fileName 文件名
   * @return
   */

  public static void writeFile(String fileName, String content) {
    writeFile(fileName, content, false);
  }

  public static void writeFile(String fileName, String content, boolean isAppend) {
    FileWriter fstream = null;
    BufferedWriter out = null;
    try {
      fstream = new FileWriter(fileName, isAppend);
      out = new BufferedWriter(fstream);
      out.write(content);
      out.close();
    } catch (Exception e) {
      logger.error("写文件失败，文件：" + fileName, e);
    } finally {
      try {
        fstream.close();
        out.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static String readToString(String file) {
    return readToString(new File(file));
  }

  public static String readToString(InputStream in) {
    StringBuffer str = new StringBuffer("");
    InputStreamReader isr = null;
    try {
      isr = new InputStreamReader(in, "UTF-8");
    } catch (UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    BufferedReader br = new BufferedReader(isr);
    try {
      String data = "";
      while ((data = br.readLine()) != null) {
        str.append(data);
      }
      in.close();
      isr.close();
      br.close();
    } catch (Exception e) {
      return "";
    }
    return str.toString();
  }

  public static String readToString(File file) {

    Long filelength = file.length(); // 获取文件长度
    FileInputStream in = null;
    byte[] filecontent = new byte[filelength.intValue()];
    try {
      in = new FileInputStream(file);
      in.read(filecontent);
      in.close();
    } catch (Exception e) {
      logger.error("读取文件失败，文件：" + file.getName(), e);
    } finally {
      if (in != null) {
        try {
          in.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return new String(filecontent);// 返回文件内容,默认编码
  }

  public static String getFileName(String file) {
    File f = new File(file);
    return f.getName();
  }

  public static void writeParserModel(String file, byte[] content) throws IOException {

    BufferedOutputStream out = null;
    try {
      out = new BufferedOutputStream(new FileOutputStream(new File(file)));
      out.write(content);

    } finally {
      try {
        if (out != null)
          out.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  public static byte[] readParserModel(String file) throws IOException {

    FileInputStream in = null;
    byte[] buffer = new byte[0];

    try {
      in = new FileInputStream(file);
      buffer = new byte[in.available()];
      in.read(buffer);
    } finally {
      try {
        if (in != null)
          in.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return buffer;

  }

  public static InputStream loadProperties(String path) {
    return FileUtils.class.getClassLoader()
        .getResourceAsStream(PropertiesUtil.getProperty("rabbit.properties", path));
  }

  /**
   * 判断文件的编码格式
   * 
   * @param fileName :file
   * @return 文件编码格式
   * @throws Exception
   */
  public static String codeString(String fileName) throws Exception {
    BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName));
    int p = (bin.read() << 8) + bin.read();
    String code = null;
    // 其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
    switch (p) {
      case 0xefbb:
        code = "UTF-8";
        break;
      case 0xfffe:
        code = "Unicode";
        break;
      case 0xfeff:
        code = "UTF-16BE";
        break;
      case 0x5c75:
        code = "ANSI|ASCII";
        break;
      default:
        code = "GBK";
    }
    bin.close();
    return code;
  }
}
