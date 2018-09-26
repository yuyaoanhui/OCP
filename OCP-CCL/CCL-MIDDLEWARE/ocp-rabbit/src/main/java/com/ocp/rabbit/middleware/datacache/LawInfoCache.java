package com.ocp.rabbit.middleware.datacache;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ocp.rabbit.bootstrap.Bootstrap;
import com.ocp.rabbit.middleware.orm.model.GuideLawIndex;
import com.ocp.rabbit.middleware.orm.model.GuideLawWithBLOBs;
import com.ocp.rabbit.middleware.service.GuideLawAndIndexService;
import com.ocp.rabbit.middleware.service.GuideLawAndIndexServiceImpl;
import com.ocp.rabbit.repository.tool.RabbitException;
import com.ocp.rabbit.repository.tool.algorithm.law.LawInfo;

@Component
public class LawInfoCache implements Initor {

  public static List<LawInfo> lawInfos = new ArrayList<LawInfo>();
  public static List<String> lawItemIndexs = new ArrayList<String>();

  @Override
  public void init() throws RabbitException {

    GuideLawAndIndexService service =
        Bootstrap.springContext.getBean(GuideLawAndIndexServiceImpl.class);
    try {
      List<GuideLawWithBLOBs> guideLawWithBLOBs = service.queryLaw();
      for (GuideLawWithBLOBs guideLaw : guideLawWithBLOBs) {
        String lawName = guideLaw.getLawName();
        // 去除（废止）
        if (lawName.endsWith("废止）") || lawName.endsWith("废止)")) {
          lawName = lawName.replace("[（(]废止[）)]", "");
        }
        // 去除法律名称中的（2000年修订）字样
        if (lawName.endsWith("）") || lawName.endsWith(")")) {
          lawName = lawName.replaceAll("[（(][0-9１２３４５６７８９０]{4}.*?[）)]", "");
        }
        String law_name2 = lawName;
        if (lawName.contains("、")) {
          law_name2 = law_name2.replace("、", "");
        }
        String law_rowkey = guideLaw.getHbaseRowkey();
        Date exeDate = guideLaw.getExecuteDate();
        if (!lawName.isEmpty()) {
          lawInfos.add(new LawInfo(lawName, law_name2, law_rowkey, exeDate));
        }
      }
    } catch (RabbitException e) {
      e.printStackTrace();
    }
    try {
      List<GuideLawIndex> GuideLawIndexs = service.queryLawIndex();
      for (GuideLawIndex guideLawIndex : GuideLawIndexs) {
        String hbase_rowkey = guideLawIndex.getHbaseRowkey();
        String item_index = guideLawIndex.getItemIndex();
        if (!StringUtils.isEmpty(hbase_rowkey) && !StringUtils.isEmpty(item_index)) {
          lawItemIndexs.add(hbase_rowkey + item_index);
        }
      }
    } catch (RabbitException e) {
      e.printStackTrace();
    }
    // 排序
    Collections.sort(lawInfos, new Comparator<LawInfo>() {
      @Override
      public int compare(LawInfo o1, LawInfo o2) {
        if (o1.exeDate == null)
          return -1;
        else if (o2.exeDate == null)
          return 1;
        return o1.exeDate.compareTo(o2.exeDate);
      }
    });

    Collections.sort(lawItemIndexs);
  }

  @Override
  public void refresh() throws RabbitException {

  }
}
