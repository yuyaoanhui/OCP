package es.data.export.tool;

/**
 * 旧数据处理类,如：将数据放在案由名称下面,统一归为一个type,以适应新版本ES-Schema的要求
 * 
 * @author yu.yao 2018年6月25日
 *
 */
public class DataHandleTool {
  // 定义 旧数据覆盖的所有案由-分别对应一个type
  public static final String[] caseTypes = {"经典案例", "敲诈勒索罪", "危险驾驶罪", "抢夺罪", "机动车交通事故责任纠纷",
      "民间借贷纠纷", "离婚纠纷", "其他", "交通肇事罪", "盗窃罪", "诈骗罪", "非法拘禁罪", "抢劫罪", "职务侵占罪", "妨害公务罪", "故意伤害罪",
      "强奸罪", "聚众斗殴罪", "信用卡诈骗罪", "合同诈骗罪", "非法吸收公众存款罪", "非法持有毒品罪", "容留他人吸毒罪", "引诱、容留、介绍卖淫罪", "集资诈骗罪"};

  /**
   * 判断对不同的索引数据进行何种处理函数分发的入口类
   * 
   * @author yu.yao
   * @param
   * @return
   */
  public static String handle(String index, String data, String caseType) {
    if ("fdlawcase".equals(index)) {
      return wrappLawcase(data, caseType);
    } else if ("fdsearchtype".equals(index)) {
      return data;
    } else if ("lxfdlawcase".equals(index)) {
      return wrappLxlawcase(data, caseType);
    } else if ("fdlaw".equals(index)) {
      return data;
    } else if ("fdppolawcase".equals(index)) {
      return wrappPPOLawcase(data, caseType);
    } else
      return data;
  }

  /**
   * 封装"{案由：***}" 数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static String wrappLawcase(String data, String caseType) {
    data = "{\"" + caseType + "\":" + data + "}";
    return data;
  }

  /**
   * 封装"{案由：***}" 数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static String wrappLxlawcase(String data, String caseType) {
    data = "{\"" + caseType + "\":" + data + "}";
    return data;
  }

  /**
   * 封装"{案由：***}" 数据
   * 
   * @author yu.yao
   * @param
   * @return
   */
  private static String wrappPPOLawcase(String data, String caseType) {
    data = "{\"" + caseType + "\":" + data + "}";
    return data;
  }
}
