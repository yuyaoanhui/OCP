package com.ocp.ai.engine.vision.ocr;

public class OCRJNI {
  static {
    System.load("E:/path/tesseract40.dll");
    System.load("E:/path/libdll.dll");
  }

  /**
   * 从一张图片中识别文字
   * 
   * @author Alex
   * @param path 图片绝对路径
   * @param type 图片文件类型
   * @param simpleName 图片文件简称(不包含后缀)
   * @return
   */
  public native String convert(String path, String type, String simpleName);

  public static void main(String[] args) {
    OCRJNI jni_ins = new OCRJNI();
    jni_ins.convert("/src/resource/6.tif", "tif", "6");
  }
}
