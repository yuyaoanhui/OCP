package com.ocp.rabbit.repository.util;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectWriter;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON处理工具类，包括了jackjson一些转换方法
 * 
 * @author yu.yao 2018年7月28日
 *
 */
public class JSONUtil {

  private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

  public static final ObjectMapper mapper = new ObjectMapper();

  @SuppressWarnings("unchecked")
  public static <T> T toCollection(Class<?> collectionClass, Class<?> elementClasses, String json) {
    JavaType javaType = mapper.getTypeFactory().constructParametrizedType(collectionClass,
        collectionClass, elementClasses);
    try {
      return (T) mapper.readValue(json, javaType);
    } catch (JsonParseException e) {
      logger.error("JsonParseException: ", e);
    } catch (JsonMappingException e) {
      logger.error("JsonMappingException: ", e);
    } catch (IOException e) {
      logger.error("IOException: ", e);
    }
    return null;
  }

  /**
   * jackjson把json字符串转换为Java对象的实现方法
   * 
   * 
   * @param <T> 转换为的java对象
   * @param json json字符串
   * @param typeReference jackjson自定义的类型
   * @return 返回Java对象
   */
  public static <T> T toObject(String json, TypeReference<T> typeReference) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, typeReference);
    } catch (JsonParseException e) {
      e.printStackTrace();
      logger.error("JsonParseException: ", e);
    } catch (JsonMappingException e) {
      e.printStackTrace();
      logger.error("JsonMappingException: ", e);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error("IOException: ", e);
    }
    return null;
  }

  /**
   * json转换为java对象
   * 
   * @param <T> 要转换的对象
   * @param json 字符串
   * @param valueType 对象的class
   * @return 返回对象
   */
  public static <T> T toObject(String json, Class<T> valueType) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(json, valueType);
    } catch (JsonParseException e) {
      logger.error("JsonParseException: ", e);
    } catch (JsonMappingException e) {
      logger.error("JsonMappingException: ", e);
    } catch (IOException e) {
      logger.error("IOException: ", e);
    }
    return null;
  }

  /**
   * java对象转换为json字符串
   * 
   * @param object Java对象
   * @return 返回字符串
   */
  public static String toJson(Object object) {
    return toJson(object, false);
  }

  /**
   * Java对象转换为JSON字符串
   *
   * @param object Java对象
   * @param pretty 是否pretty print
   * @return JSON字符串
   */
  public static String toJson(Object object, boolean pretty) {
    ObjectWriter writer = pretty ? mapper.writerWithDefaultPrettyPrinter() : mapper.writer();
    String json = null;
    try {
      json = writer.writeValueAsString(object);
    } catch (JsonGenerationException e) {
      logger.error("JsonGenerationException: ", e);
    } catch (JsonMappingException e) {
      logger.error("JsonMappingException: ", e);
    } catch (IOException e) {
      logger.error("IOException: ", e);
    }
    return json;
  }

}
