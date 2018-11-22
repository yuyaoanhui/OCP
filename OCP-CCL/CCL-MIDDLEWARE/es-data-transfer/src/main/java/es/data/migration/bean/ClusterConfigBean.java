package es.data.migration.bean;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import es.data.migration.util.PropertiesUtil;

/**
 * ES配置bean
 * 
 * @author yu.yao 2018年6月23日
 *
 */
public class ClusterConfigBean {
  private String newClusterName; // 新集群名称
  private String newClusterAddress; // 新集群地址

  private String fdlawcase;// 文书索引名称
  private String fdsearchtype;// 智能联想索引名称
  private String lxfdlawcase;// 量刑辅助索引名称
  private String fdlaw;// 法律法规索引名称
  private String ppofdlawcase;// 检察院审查逮捕文书索引名称

  private Map<String, String> typesMap = new HashMap<>();
  private Map<String, String> indecesMap = new HashMap<>();

  public ClusterConfigBean() {
    this.newClusterName = PropertiesUtil.getProperty("cluster.new.name");
    this.newClusterAddress = PropertiesUtil.getProperty("cluster.new.ip.port").split(";")[0];
    this.fdlawcase = PropertiesUtil.getProperty("index.name.fdlawcase");
    this.fdsearchtype = PropertiesUtil.getProperty("index.name.fdsearchtype");
    this.lxfdlawcase = PropertiesUtil.getProperty("index.name.lxfdlawcase");
    this.fdlaw = PropertiesUtil.getProperty("index.name.fdlaw");
    this.ppofdlawcase = PropertiesUtil.getProperty("index.name.ppofdlawcase");
    if (!StringUtils.isEmpty(fdlawcase)) {
      indecesMap.put("fdlawcase", fdlawcase);
      typesMap.put("fdlawcase", PropertiesUtil.getProperty("index.type.fdlawcase"));
    }
    if (!StringUtils.isEmpty(fdsearchtype)) {
      indecesMap.put("fdsearchtype", fdsearchtype);
      typesMap.put("fdsearchtype", PropertiesUtil.getProperty("index.type.fdsearchtype"));
    }
    if (!StringUtils.isEmpty(lxfdlawcase)) {
      indecesMap.put("lxfdlawcase", lxfdlawcase);
      typesMap.put("lxfdlawcase", PropertiesUtil.getProperty("index.type.lxfdlawcase"));
    }
    if (!StringUtils.isEmpty(fdlaw)) {
      indecesMap.put("fdlaw", fdlaw);
      typesMap.put("fdlaw", PropertiesUtil.getProperty("index.type.fdlaw"));
    }
    if (!StringUtils.isEmpty(ppofdlawcase)) {
      indecesMap.put("ppofdlawcase", ppofdlawcase);
      typesMap.put("ppofdlawcase", PropertiesUtil.getProperty("index.type.ppofdlawcase"));
    }
  }

  public String getNewClusterName() {
    return newClusterName;
  }

  public void setNewClusterName(String newClusterName) {
    this.newClusterName = newClusterName;
  }

  public String getNewClusterAddress() {
    return newClusterAddress;
  }

  public void setNewClusterAddress(String newClusterAddress) {
    this.newClusterAddress = newClusterAddress;
  }

  public String getFdlawcase() {
    return fdlawcase;
  }

  public void setFdlawcase(String fdlawcase) {
    this.fdlawcase = fdlawcase;
  }

  public String getFdsearchtype() {
    return fdsearchtype;
  }

  public void setFdsearchtype(String fdsearchtype) {
    this.fdsearchtype = fdsearchtype;
  }

  public String getLxfdlawcase() {
    return lxfdlawcase;
  }

  public void setLxfdlawcase(String lxfdlawcase) {
    this.lxfdlawcase = lxfdlawcase;
  }

  public String getFdlaw() {
    return fdlaw;
  }

  public void setFdlaw(String fdlaw) {
    this.fdlaw = fdlaw;
  }

  public Map<String, String> getIndecesMap() {
    return indecesMap;
  }

  public void setIndecesMap(Map<String, String> indecesMap) {
    this.indecesMap = indecesMap;
  }

  public Map<String, String> getTypesMap() {
    return typesMap;
  }

  public void setTypesMap(Map<String, String> typesMap) {
    this.typesMap = typesMap;
  }

  public String getPpofdlawcase() {
    return ppofdlawcase;
  }

  public void setPpofdlawcase(String ppofdlawcase) {
    this.ppofdlawcase = ppofdlawcase;
  }

}
