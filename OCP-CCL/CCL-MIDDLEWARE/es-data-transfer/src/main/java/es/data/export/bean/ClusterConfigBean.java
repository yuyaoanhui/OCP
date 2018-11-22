package es.data.export.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import es.data.export.tool.DataHandleTool;
import es.data.export.util.PropertiesUtil;

/**
 * ES配置bean
 * 
 * @author yu.yao 2018年6月23日
 *
 */
public class ClusterConfigBean {
  private String oldClusterName; // 旧集群名称
  private String oldClusterAddress; // 旧集群地址

  private String fdlawcase;// 文书索引名称
  private String fdsearchtype;// 智能联想索引名称
  private String lxfdlawcase;// 量刑辅助索引名称
  private String fdlaw;// 法律法规索引名称

  private String ppofdlawcase;// 检察院审查逮捕文书索引名称

  private List<String> indeces = new ArrayList<>(); // 索引列表

  private Map<String, String> indecesMap = new HashMap<String, String>();

  // 每个index各自包含多少type
  private Map<String, List<String>> indexTypes = new HashMap<String, List<String>>();

  public ClusterConfigBean() {
    this.oldClusterName = PropertiesUtil.getProperty("cluster.old.name");
    this.oldClusterAddress = PropertiesUtil.getProperty("cluster.old.ip.port").split(";")[0];
    this.fdlawcase = PropertiesUtil.getProperty("index.name.fdlawcase");
    this.fdsearchtype = PropertiesUtil.getProperty("index.name.fdsearchtype");
    this.lxfdlawcase = PropertiesUtil.getProperty("index.name.lxfdlawcase");
    this.fdlaw = PropertiesUtil.getProperty("index.name.fdlaw");
    this.ppofdlawcase = PropertiesUtil.getProperty("index.name.ppofdlawcase");
    if (!StringUtils.isEmpty(fdlawcase)) {
      indeces.add("fdlawcase");
      indecesMap.put("fdlawcase", fdlawcase);
      indexTypes.put("fdlawcase", Arrays.asList(DataHandleTool.caseTypes));
    }
    if (!StringUtils.isEmpty(fdsearchtype)) {
      indeces.add("fdsearchtype");
      indecesMap.put("fdsearchtype", fdsearchtype);
      indexTypes.put("fdsearchtype", Arrays.asList(new String[] {"linksearch"}));
    }
    if (!StringUtils.isEmpty(lxfdlawcase)) {
      indeces.add("lxfdlawcase");
      indecesMap.put("lxfdlawcase", lxfdlawcase);
      indexTypes.put("lxfdlawcase", Arrays.asList(DataHandleTool.caseTypes));
    }
    if (!StringUtils.isEmpty(fdlaw)) {
      indeces.add("fdlaw");
      indecesMap.put("fdlaw", fdlaw);
      indexTypes.put("fdlaw", Arrays.asList(new String[] {"law"}));
    }
    if (!StringUtils.isEmpty(ppofdlawcase)) {
      indeces.add("ppofdlawcase");
      indecesMap.put("ppofdlawcase", ppofdlawcase);
      indexTypes.put("ppofdlawcase", Arrays.asList(DataHandleTool.caseTypes));
    }
  }

  public String getOldClusterName() {
    return oldClusterName;
  }

  public void setOldClusterName(String oldClusterName) {
    this.oldClusterName = oldClusterName;
  }

  public String getOldClusterAddress() {
    return oldClusterAddress;
  }

  public void setOldClusterAddress(String oldClusterAddress) {
    this.oldClusterAddress = oldClusterAddress;
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

  public List<String> getIndeces() {
    return indeces;
  }

  public void setIndeces(List<String> indeces) {
    this.indeces = indeces;
  }

  public Map<String, String> getIndecesMap() {
    return indecesMap;
  }

  public void setIndecesMap(Map<String, String> indecesMap) {
    this.indecesMap = indecesMap;
  }

  public Map<String, List<String>> getIndexTypes() {
    return indexTypes;
  }

  public void setIndexTypes(Map<String, List<String>> indexTypes) {
    this.indexTypes = indexTypes;
  }

  public String getPpofdlawcase() {
    return ppofdlawcase;
  }

  public void setPpofdlawcase(String ppofdlawcase) {
    this.ppofdlawcase = ppofdlawcase;
  }

}
