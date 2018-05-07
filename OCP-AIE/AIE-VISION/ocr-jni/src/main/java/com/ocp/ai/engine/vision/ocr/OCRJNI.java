package com.ocp.ai.engine.vision.ocr;

public class OCRJNI {

  static {
    System.load("/usr/local/lib/libtesseract.so");
  }

  /**
   * 从一张图片中识别文字
   * 
   * @author Alex
   * @param input 输入
   * @param output 输出
   * @return
   */
  public native String convert(String input, String output);
}
