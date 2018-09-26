package com.ocp.rabbit.proxy.extractor.custom.law;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ocp.rabbit.middleware.datacache.LawInfoCache;
import com.ocp.rabbit.proxy.process.AbstractProcess.Context;
import com.ocp.rabbit.repository.algorithm.NamedEntityRecognizer;
import com.ocp.rabbit.repository.entity.NamedEntity;
import com.ocp.rabbit.repository.tool.ResourceReader;
import com.ocp.rabbit.repository.tool.algorithm.law.LawContainer;
import com.ocp.rabbit.repository.tool.algorithm.law.LawInfo;
import com.ocp.rabbit.repository.tool.algorithm.law.UrlLabel;
import com.ocp.rabbit.repository.tool.algorithm.number.NumberHandler;
import com.ocp.rabbit.repository.util.DocumentUtils;
import com.ocp.rabbit.repository.util.TextUtils;

/**
 * 法律法规条款
 *
 * @author yu.yao 2018年8月13日
 */
public class LegalProvisionExtractor {
  private static String judgeDate = "";

  private static final String TIAOZHI = "条之"; // 如第十七条之一
  private static final String TIAO = "条";
  private static final String KUAN = "款";
  private static final String XIANG = "项";
  private static final Character DI = '第';

  private static final String[] UNITS_TIAOKUANXIANG = {TIAOZHI, TIAO, KUAN, XIANG};
  private static final Pattern PATTERN__PROVISION_NAME = Pattern.compile("《([^《》。;；,，]*)》");
  private static final Pattern[] PATTERN_CONSECUTIVE_KUANS =
      {Pattern.compile("第[一二三四五六七八九十\\d、]+款")};
  private static final Pattern PATTERN_XIANG_BRACKET =
      Pattern.compile("第[\\(（][一二三四五六七八九十\\d]+[\\)）]项");

  // 需要考虑书名号配对
  // 书名号之间不能有《》。；;,，可能存在、
  private static final Set<Character> CHARACTERS_PAUSE = new HashSet<Character>();

  private static Pattern PATTERN_SYNONYM = Pattern.compile("称");

  private static Pattern PATTERN_EXCLUDE = Pattern.compile("[。；;、\\d]+");

  static final Pattern[] patterns = {Pattern.compile("第.*?项"), Pattern.compile("第.*?款"),
      Pattern.compile("第.*?条之[一二三四五六七八九十]{1,2}"), Pattern.compile("第.*?条")};

  static {
    CHARACTERS_PAUSE.add(',');
    CHARACTERS_PAUSE.add('，');
    CHARACTERS_PAUSE.add(';');
    CHARACTERS_PAUSE.add('；');
    CHARACTERS_PAUSE.add('。');
    CHARACTERS_PAUSE.add('、');
  }

  private Context context;

  public LegalProvisionExtractor(Context context) {
    this.context = context;
  }

  @SuppressWarnings("unchecked")
  public void extract(String oldSectionKey, String signatureDateKey, String lawKey,
      String lawIdKey) {
    List<String> lawIdList;
    Map<String, Object> extractInfo = context.rabbitInfo.extractInfo;
    if (extractInfo.containsKey(signatureDateKey)) {
      judgeDate = (String) (extractInfo.get(signatureDateKey));
    }
    if (!extractInfo.containsKey(lawIdKey)) {
      lawIdList = new ArrayList<>();
    } else {
      lawIdList = (List<String>) (extractInfo.get(lawIdKey));
    }
    List<LawContainer> llc = new ArrayList<>();
    Map<String, Map<String, List<UrlLabel>>> urlLabels = context.rabbitInfo.getUrlLabels();
    List<UrlLabel> urlLabelList = new ArrayList<>();
    recognizeLegalProvisions((String) (context.rabbitInfo.extractInfo.get(oldSectionKey)), llc,
        lawIdList, urlLabelList);
    if (!urlLabelList.isEmpty()) {
      if (!urlLabels.containsKey(lawKey)) {
        urlLabels.put(lawKey, new HashMap<>());
      }
      urlLabels.get(lawKey).put(oldSectionKey, urlLabelList);
    }
    List<LawContainer> lawList;
    if (!extractInfo.containsKey(lawKey)) {
      if (!llc.isEmpty()) {
        lawList = new ArrayList<>();
        for (LawContainer lc : llc) {
          String lcStr = lc.toString();
          if (!TextUtils.isEmpty(lcStr)) {
            lawList.add(lc);
          }
        }
        extractInfo.put(lawKey, lawList);
      }
    } else {
      lawList = (List<LawContainer>) (extractInfo.get(lawKey));
      List<String> list = new ArrayList<>();
      for (LawContainer lc : lawList) {
        list.add(lc.toString());
      }
      if (!llc.isEmpty()) {
        for (LawContainer lc : llc) {
          String lcStr = lc.toString();
          if ((!TextUtils.isEmpty(lcStr)) && (!list.contains(lcStr))) {
            lawList.add(lc);
          }
        }
        extractInfo.put(lawKey, lawList);
      }
    }
    if (!lawIdList.isEmpty()) {
      extractInfo.put(lawIdKey, lawIdList);
    }
  }

  /**
   * 对本院认为的每一段分别处理
   */
  private String recognizeLegalProvisions(String paragrah_str, List<LawContainer> llc,
      List<String> lawIdList, List<UrlLabel> urlLabelList) {
    if (paragrah_str == null) {
      return "";
    }
    String[] paragraphs = DocumentUtils.splitParagraphs(paragrah_str);
    if (paragraphs.length >= 4) {
      Map<String, String> sameLawMapper = new HashMap<>();
      StringBuilder sb = new StringBuilder();
      AtomicInteger paragraphOffset = new AtomicInteger(0);
      CountDownLatch latch = new CountDownLatch(paragraphs.length);
      for (String paragraph : paragraphs) {
        new Runnable() {
          @Override
          public void run() {
            // 分段处理，记录法律法规相对于该段落的位置
            List<NamedEntity> lne = recognizeLegalProvisions(paragraph, sameLawMapper);
            // 分段处理，需要传入前面段落的总偏移量，因为最后是以section为一个整体，计算偏移量
            String paragraph_modified = writeLawInfo(paragraph, lne, llc, sameLawMapper, lawIdList,
                urlLabelList, paragraphOffset.get());
            // +1表示拼接的时候都加了一个"\n"
            paragraphOffset.set(paragraphOffset.get() + paragraph.length() + 1);
            // 拼接
            sb.append(paragraph_modified).append("\n");
            latch.countDown();
          }
        }.run();
      }
      try {
        latch.await();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return sb.toString();
    } else {
      Map<String, String> sameLawMapper = new HashMap<>();
      StringBuilder sb = new StringBuilder();
      AtomicInteger paragraphOffset = new AtomicInteger(0);
      for (String paragraph : paragraphs) {
        // 分段处理，记录法律法规相对于该段落的位置
        List<NamedEntity> lne = recognizeLegalProvisions(paragraph, sameLawMapper);
        // 分段处理，需要传入前面段落的总偏移量，因为最后是以section为一个整体，计算偏移量
        String paragraph_modified = writeLawInfo(paragraph, lne, llc, sameLawMapper, lawIdList,
            urlLabelList, paragraphOffset.get());
        // +1表示拼接的时候都加了一个"\n"
        paragraphOffset.set(paragraphOffset.get() + paragraph.length() + 1);
        // 拼接
        sb.append(paragraph_modified).append("\n");
      }
      return sb.toString();
    }
  }

  /**
   * 对某一段处理，识别出法律的范围，并将范围和标准化后的法律存储在List<NamedEntity>里面 增加对 第一、三款 这种情况的处理
   */
  private List<NamedEntity> recognizeLegalProvisions(String paragraph,
      Map<String, String> sameLawMapper) {
    NamedEntity[] nes_name =
        NamedEntityRecognizer.recognizeEntityByRegex(paragraph, PATTERN__PROVISION_NAME);
    if (nes_name.length == 0) {
      return null;
    }
    NamedEntity[] nes_numbers =
        NamedEntityRecognizer.recognizeNumber(paragraph, UNITS_TIAOKUANXIANG);
    // 增加对 第（三）项 这种带括号的情况提取
    NamedEntity[] nes_numbers_xiang =
        NamedEntityRecognizer.recognizeEntityByRegex(paragraph, PATTERN_XIANG_BRACKET);
    List<NamedEntity> lne_xiang = new ArrayList<>();
    for (NamedEntity ne : nes_numbers_xiang) {
      String numberCh = ne.getSource().substring(2, ne.getSource().length() - 2);
      double numberArabic = NumberHandler.convert2Double(numberCh);
      ne.setInfo(numberArabic);
      lne_xiang.add(ne);
    }
    NamedEntity[] nes_xiang = NamedEntityRecognizer.dropDuplicates(lne_xiang);
    // 增加对 第一、三款 这种情况的处理
    NamedEntity[] nes_numbers_kuan =
        NamedEntityRecognizer.recognizeEntityByRegex(paragraph, PATTERN_CONSECUTIVE_KUANS);
    // 将nes_numbers_kuan 按顿号、分割，并且给info赋值，注意这里提取的实体有的没有单位 比如 “第一”
    // 后面判断的时候默认为是 款
    List<NamedEntity> lne_kuan = new ArrayList<>();
    Pattern pattern = Pattern.compile("[、]+");
    for (NamedEntity ne : nes_numbers_kuan) {
      // 取其中数字部分
      LinkedList<NamedEntity> lne_tmp = new LinkedList<>();
      String numberCh = ne.getSource().substring(1, ne.getSource().length() - 1);
      // 必须含有顿号"、"，否则跳过（之前的规则已经涵盖了）
      if (!numberCh.contains("、")) {
        continue;
      }
      Matcher matcher = pattern.matcher(numberCh);
      int fromIndex = 0;
      while (matcher.find()) {
        if (matcher.start() <= fromIndex) {
          break;
        }
        String thisNumber = numberCh.substring(fromIndex, matcher.start());
        double numberArabic = NumberHandler.convert2Double(thisNumber);
        NamedEntity namedEntity =
            new NamedEntity(thisNumber, ne.getOffset() + 1 + fromIndex, numberArabic);
        fromIndex = matcher.end();
        lne_tmp.add(namedEntity);
      }
      // 加入最后一个
      if (fromIndex != 0 && fromIndex < numberCh.length()) {
        String thisNumber = numberCh.substring(fromIndex, numberCh.length());
        double numberArabic = NumberHandler.convert2Double(thisNumber);
        NamedEntity namedEntity =
            new NamedEntity(thisNumber, ne.getOffset() + 1 + fromIndex, numberArabic);
        lne_tmp.add(namedEntity);
      }
      // 对第一个和最后一个补全
      if (!lne_tmp.isEmpty()) {
        NamedEntity first = lne_tmp.getFirst();
        first.setSource("第" + first.getSource());
        first.setOffset(first.getOffset() - 1);
        NamedEntity last = lne_tmp.getLast();
        last.setSource(last.getSource() + "款");
        lne_kuan.addAll(lne_tmp);
      }
    }
    NamedEntity[] nes_kuan = NamedEntityRecognizer.dropDuplicates(lne_kuan);
    for (NamedEntity entity : nes_numbers) {
      String source = entity.getSource();
      // 处理第十七条之一等特殊法条
      if (source.endsWith(TIAOZHI)) {
        if (source.length() + entity.getOffset() + 1 <= paragraph.length()) {
          String nextchar = paragraph.substring(source.length() + entity.getOffset(),
              source.length() + entity.getOffset() + 1);
          if ("一二三四五六七八九十".contains(nextchar))
            entity.setSource(source + nextchar);
          else
            entity.setSource(source.substring(0, source.length() - 1));
        } else {
          entity.setSource(source.substring(0, source.length() - 1));
        }
      }
    }
    // 合并相邻的条款项
    NamedEntity[] nes_all =
        NamedEntityRecognizer.combineEntities(new String[] {"name", "number", "number", "number"},
            nes_name, nes_numbers, nes_kuan, nes_xiang);
    if (nes_all.length == 0)
      return null;
    // 得到法律的简写对应关系
    getLawSynonym(sameLawMapper, nes_name, paragraph);
    Integer[] periods = NamedEntityRecognizer.recognizePeriods(paragraph);
    NamedEntity ne_lastLawName = null;
    NamedEntity info = null;
    int startLaw = -1, endLaw = -1;
    LinkedList<NamedEntity> lne = new LinkedList<>();
    for (NamedEntity ne : nes_all) {
      String neSource = ne.getSource();
      if ("name".equals(ne.getType())) {
        // 新建一个法律
        // info 存储了最近找到的法律条款的位置和信息，包括了法律的名字、条款项
        LawContainer lc = new LawContainer(neSource);
        // 保存法律名字的位置
        lc.setPos(0, 1, ne.getOffset(), ne.getOffset() + neSource.length());
        info = new NamedEntity(neSource, ne.getOffset(), lc);
        lne.add(info);
        // 保存最近找到的法律名字,开始位置，结束位置
        ne_lastLawName = ne;
        startLaw = ne.getOffset();
        endLaw = ne.getOffset() + neSource.length();
      } else {
        // 条款项之间和最近的一个法律名字之间不能有"；;。"，否则认为非法的条款项
        if (null == ne_lastLawName
            || NamedEntityRecognizer.betweenTwoCommas(ne_lastLawName, ne, periods)
            || ne.getOffset() + neSource.length() <= startLaw)
          continue;
        // 当前信息，包括单位和数字
        // 保护机制
        if (neSource.length() < 1) {
          continue;
        }
        String unit = neSource.substring(neSource.length() - 1);
        if (neSource.contains(TIAOZHI)) {
          unit = TIAOZHI;
        }
        int number = (int) ((double) ne.getInfo());
        if (number <= 0) {
          continue;
        }
        if (unit.equals(TIAOZHI)) {
          String strZhiNumber = neSource.substring(neSource.length() - 1, neSource.length());
          int numberzhi = NumberHandler.convert2Int(strZhiNumber);
          number = number * 100 + numberzhi;
        } else if (unit.equals(TIAO)) {
          number = number * 100;
        }
        // 最近找到的法律
        if (info.getInfo() == null) {
          continue;
        }
        LawContainer lastLaw = (LawContainer) info.getInfo();
        // 注意条款项提取的时候没有考虑前缀"第"。
        int diff = ne.getOffset() - endLaw;
        // 需要判断是否超出边界
        if (endLaw < 0 || endLaw >= paragraph.length()) {
          continue;
        }
        Character character = paragraph.charAt(endLaw);
        // 判断是不是连续的，默认是不连续的
        // 如果开始字符和上一个结束字符之间最多相差一个字符，并且如果差一个字符，这个字符不能是标点符号
        // 规则：如果条款和上一个条款的距离是0或者1，并且当前单位还没有出现（比如之前出现了条，现在又出现了条）中间没有标点符号则认为是连续的
        boolean nextLaw = true;
        if (diff >= 0 && diff <= 1 && !CHARACTERS_PAUSE.contains(character)) {
          nextLaw = false;
        }
        switch (unit) {
          case TIAO:
          case TIAOZHI: {
            if (!nextLaw) {
              // 如果是连续的，则修改前面的法律
              lastLaw.setTiao(number);
              // 同时修改原文里面对应的字符串的位置
              startLaw = ne.getOffset();
              if (startLaw - 1 >= 0 && DI == paragraph.charAt(startLaw - 1)) {
                startLaw = startLaw - 1;
              }
              endLaw = ne.getOffset() + neSource.length();
              info.setSource(paragraph.substring(startLaw, endLaw));
              // 设定条的位置
              lastLaw.setPos(2, 3, startLaw, endLaw);
            } else {
              // 如果不是连续的，则新增一个法律
              // 新增的法律的名字和补全的条款项，来自列表最后一个元素info里面存储的lastLaw;
              startLaw = ne.getOffset();
              if (startLaw - 1 >= 0 && DI == paragraph.charAt(startLaw - 1)) {
                startLaw = startLaw - 1;
              }
              endLaw = ne.getOffset() + neSource.length();
              // 新建一个LawContainer并添加到列表，更新info
              LawContainer newLc = new LawContainer(lastLaw.getLawName(), number);
              NamedEntity newInfo =
                  new NamedEntity(paragraph.substring(startLaw, endLaw), startLaw, newLc);
              // 设定条的位置
              newLc.setPos(2, 3, startLaw, endLaw);
              lne.add(newInfo);
              info = newInfo;
              lastLaw = newLc;
            }
            break;
          }
          case XIANG: {
            if (!nextLaw) {
              // 如果是连续的，则修改前面的法律
              lastLaw.setXiang(number);
              // 同时修改原文里面对应的字符串的位置
              startLaw = ne.getOffset();
              if (startLaw - 1 >= 0 && DI == paragraph.charAt(startLaw - 1)) {
                startLaw = startLaw - 1;
              }
              endLaw = ne.getOffset() + neSource.length();
              info.setSource(paragraph.substring(startLaw, endLaw));
              // 设定 项 的位置
              lastLaw.setPos(6, 7, startLaw, endLaw);
            } else {
              // 如果不是连续的，则新增一个法律
              // 改变startLaw和endLaw
              // 新增的法律的名字和补全的条款项，来自列表最后一个元素info里面存储的lastLaw;
              startLaw = ne.getOffset();
              if (startLaw - 1 >= 0 && DI == paragraph.charAt(startLaw - 1)) {
                startLaw = startLaw - 1;
              }
              endLaw = ne.getOffset() + neSource.length();
              // 新建一个LawContainer并添加到列表，更新info
              LawContainer newLc = new LawContainer(lastLaw.getLawName(), lastLaw.getTiao(),
                  lastLaw.getKuan(), number);
              NamedEntity newInfo =
                  new NamedEntity(paragraph.substring(startLaw, endLaw), startLaw, newLc);
              // 设定 项 的位置
              newLc.setPos(6, 7, startLaw, endLaw);
              lne.add(newInfo);
              info = newInfo;
              lastLaw = newLc;
            }
            break;
          }
          case KUAN:
          default: {
            if (!nextLaw) {
              // 如果是连续的，则修改前面的法律
              lastLaw.setKuan(number);
              // 同时修改原文里面对应的字符串的位置
              startLaw = ne.getOffset();
              if (startLaw - 1 >= 0 && DI == paragraph.charAt(startLaw - 1)) {
                startLaw = startLaw - 1;
              }
              endLaw = ne.getOffset() + neSource.length();
              info.setSource(paragraph.substring(startLaw, endLaw));
              // 设定 款 的位置
              lastLaw.setPos(4, 5, startLaw, endLaw);
            } else {
              // 如果不是连续的，则新增一个法律
              // 新增的法律的名字和补全的条款项，来自列表最后一个元素info里面存储的lastLaw;
              startLaw = ne.getOffset();
              if (startLaw - 1 >= 0 && DI == paragraph.charAt(startLaw - 1)) {
                startLaw = startLaw - 1;
              }
              endLaw = ne.getOffset() + neSource.length();
              // 新建一个LawContainer并添加到列表，更新info
              LawContainer newLc =
                  new LawContainer(lastLaw.getLawName(), lastLaw.getTiao(), number);
              NamedEntity newInfo =
                  new NamedEntity(paragraph.substring(startLaw, endLaw), startLaw, newLc);
              // 设定 款 的位置
              newLc.setPos(4, 5, startLaw, endLaw);
              lne.add(newInfo);
              info = newInfo;
              lastLaw = newLc;
            }
            break;
          }
        }
      }
    }
    return lne;
  }

  private static Map<String, String> lawNameMap = ResourceReader.readLawNameMapper();

  /**
   * 将抽取得到的结果写入相应的类List<LawContainer>,并插入索引改变原文
   */
  private String writeLawInfo(String paragraph, List<NamedEntity> lne, List<LawContainer> llc,
      Map<String, String> sameLawMapper, List<String> lawIdList, List<UrlLabel> urlLabelList,
      int paragraphOffset) {
    // 对结果进行整理
    if (null == lne || lne.isEmpty()) {
      return paragraph;
    }
    StringBuffer sb = new StringBuffer(paragraph);
    int start, end, offset = 0;
    for (NamedEntity ne : lne) {
      start = ne.getOffset();
      end = ne.getOffset() + ne.getSource().length();
      if (start >= end || end > paragraph.length())
        continue;
      LawContainer lc = (LawContainer) ne.getInfo();
      String lawName = lc.getLawName();
      lawName = lawName.substring(1, lawName.length() - 1);
      // 转化不标准的<>
      lawName = lawName.replaceAll("[﹤＜〈]", "<");
      lawName = lawName.replaceAll("[﹥＞〉]", ">");
      if (lawNameMap.containsKey(lawName)) {
        lawName = lawNameMap.get(lawName);
      }
      lawName = sameLawMapper.getOrDefault(lawName, lawName);
      // 结构化信息里面还是存储了书名号。
      lc.setLawName("《" + lawName + "》");
      adjustLawContainer(lc);
      // 查询数据库得到 uuid (url)。这里法律名字没有书名号
      // 需要考虑UUID=null的情况怎么处理
      // 把单引号替换成双引号，和数据库保持一致
      String queryLawName = lawName.replaceAll("<", "《").replaceAll(">", "》");
      int i = offset;
      offset = insertLawItemLink(ne, sb, queryLawName, lc, offset, lawIdList, urlLabelList,
          paragraphOffset);
      // 认为位置改变，即是在数据库中找到了该法律条款，
      if (offset != i)
        llc.add(lc);
    }
    return sb.toString();
  }

  private int insertLawItemLink(NamedEntity ne, StringBuffer sb, String queryLawName,
      LawContainer lc, int offset, List<String> lawIdList, List<UrlLabel> urlLabelList,
      int paragraphOffset) {
    if (TextUtils.isEmpty(ne.getSource()))
      return offset;
    String legalText = lc.toString();
    String lawRowkey = getLawRowkey(queryLawName);
    if (TextUtils.isEmpty(lawRowkey))
      return offset;
    int[] pos = lc.getPos();
    if (pos[0] != -1 && pos[1] != -1) {
      LawContainer lcTemp = new LawContainer(lc.getLawName());
      String lawUUID = getLawUUID(lcTemp, lawRowkey);
      if (!TextUtils.isEmpty(lawUUID)) {
        String leftInserted = insertLeft(lawUUID, "law");
        String rightInserted = insertRight(lawUUID, "law");
        // 插入字符后，字符的位置改变，需要调整过来
        sb.insert(pos[0] + offset, leftInserted);
        offset = offset + leftInserted.length();
        sb.insert(pos[1] + offset, rightInserted);
        offset = offset + rightInserted.length();
        if (!lawIdList.contains(lawUUID)) {
          lawIdList.add(lawUUID);
        }
        UrlLabel urlLabel = new UrlLabel(UrlLabel.LABEL_TYPE_LAW_NAME, leftInserted, rightInserted,
            pos[0] + paragraphOffset, pos[1] + paragraphOffset);
        urlLabelList.add(urlLabel);
      }
    }
    legalText = legalText.substring(lc.getLawName().length(), legalText.length());
    for (int i = 0; i < patterns.length; i++) {
      Pattern pattern = patterns[i];
      Matcher matcher = pattern.matcher(legalText);
      if (matcher.find()) {
        String result = matcher.group();
        legalText = legalText.substring(result.length(), legalText.length());
        if (!TextUtils.isEmpty(result)) {
          LawContainer lcTemp = new LawContainer(lc.getLawName());
          int start = -1;
          int end = -1;
          String type = "";
          if (i >= 2) {
            lcTemp.setTiao(lc.getTiao());
            type = "tiao";
          } else if (i == 1) {
            lcTemp.setTiao(lc.getTiao());
            lcTemp.setKuan(lc.getKuan());
            type = "kuan";
          } else if (i == 0) {
            lcTemp.setTiao(lc.getTiao());
            lcTemp.setKuan(lc.getKuan());
            lcTemp.setXiang(lc.getXiang());
            type = "xiang";
          }
          for (int endIndex = pos.length - 1; endIndex > 1; endIndex--) {
            if (pos[endIndex] != -1) {
              end = pos[endIndex];
              break;
            }
          }
          for (int startIndex = 2; startIndex < pos.length; startIndex++) {
            if (pos[startIndex] != -1) {
              start = pos[startIndex];
              break;
            }
          }
          if (start != -1 && end != -1) {
            String lawUUID = getLawUUID(lcTemp, lawRowkey);
            if (!TextUtils.isEmpty(lawUUID)) {
              String leftInserted = insertLeft(lawUUID, type);
              String rightInserted = insertRight(lawUUID, type);
              // 插入字符后，字符的位置改变，需要调整过来
              sb.insert(start + offset, leftInserted);
              offset = offset + leftInserted.length();
              sb.insert(end + offset, rightInserted);
              offset = offset + rightInserted.length();
              if (!lawIdList.contains(lawUUID)) {
                lawIdList.add(lawUUID);
              }
              UrlLabel urlLabel = new UrlLabel(type, leftInserted, rightInserted,
                  start + paragraphOffset, end + paragraphOffset);
              urlLabelList.add(urlLabel);
            }
          }
          break;
        }
      }
    }
    return offset;
  }

  /**
   * 处理条款项某几个不存在的情况
   */
  private void adjustLawContainer(LawContainer lc) {
    if (lc.getTiao() < 0) {
      lc.setKuan(-1);
      lc.setXiang(-1);
    } else {
      if (lc.getKuan() < 0) {
        // 如果有条和项，没有款，则款设置为1。
        if (lc.getXiang() > 0) {
          lc.setKuan(1);
        }
      }
    }
  }

  // 根据法律名称和判决时间来找到最适合的一部法律
  private String getLawRowkey(String queryLawName) {
    java.sql.Date sqldate = null;
    if (!TextUtils.isEmpty(judgeDate)) {
      Date utildate;
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      try {
        utildate = sdf.parse(judgeDate);
        sqldate = new java.sql.Date(utildate.getTime());
      } catch (Exception e) {

      }
    }
    // 去除（废止）
    if (queryLawName.endsWith("废止）") || queryLawName.endsWith("废止")) {
      queryLawName = queryLawName.replace("[（(]废止[）)]", "");
    }
    // 去除法律名称中的（2000年修订）字样
    if (queryLawName.endsWith("）") || queryLawName.endsWith(")")) {
      queryLawName = queryLawName.replaceAll("[（(][0-9１２３４５６７８９０]{4}.*?[）)]", "");
    }
    String rowkey = "";
    for (LawInfo info : LawInfoCache.lawInfos) {
      if (info.strLawName.equals(queryLawName) || info.strLawName2.equals(queryLawName)) {
        if (info.exeDate != null && sqldate != null && info.exeDate.compareTo(sqldate) > 0)
          break;
        rowkey = info.strRowKey;
      }
    }
    return rowkey;
  }

  private String getLawUUID(LawContainer lc, String lawRowkey) {
    if (TextUtils.isEmpty(lawRowkey))
      return "";
    String lawItemId = "";
    if (lc.getTiao() > 0)
      lawItemId = lawItemId + String.format("%05d", lc.getTiao());
    if (lc.getKuan() > 0)
      lawItemId = lawItemId + String.format("%03d", lc.getKuan());
    if (lc.getXiang() > 0)
      lawItemId = lawItemId + String.format("%03d", lc.getXiang());
    if (!TextUtils.isEmpty(lawItemId)
        && !LawInfoCache.lawItemIndexs.contains(lawRowkey + lawItemId))
      return "";
    lc.setLawItemId(lawRowkey + lawItemId);
    return lawRowkey + lawItemId;
  }

  /**
   * 插入左边 <law value=lawUUID type=lawName>
   */
  private String insertLeft(String lawUUID, String lawName) {
    return "<law " + "value=" + lawUUID + " type=" + lawName + ">";
  }

  /**
   * 插入右边 </law value=lawUUID type=lawName>
   */
  private String insertRight(String lawUUID, String lawName) {
    return "</law>";
  }


  /**
   * 得到法律名字的简写
   *
   * @param sameLawMapper 返回结果
   */
  private void getLawSynonym(Map<String, String> sameLawMapper, NamedEntity[] nes_name,
      String paragraph) {
    NamedEntity prev = null;
    int lastMatch;
    for (NamedEntity ne : nes_name) {
      if (null != prev) {
        // 简称不能超过7个字符，包括《》不能超过9个字符
        if (ne.getSource().length() < 9) {
          int endOfLastLaw = prev.getOffset() + prev.getSource().length();
          int startOfCurrentLaw = ne.getOffset();
          // 中间最多7个字符
          if (endOfLastLaw < startOfCurrentLaw && startOfCurrentLaw - endOfLastLaw < 7) {
            // 中间不能有【。；;】
            String strBetween = paragraph.substring(endOfLastLaw, startOfCurrentLaw);
            if (!PATTERN_EXCLUDE.matcher(strBetween).find()) {
              java.util.regex.Matcher matcher = PATTERN_SYNONYM.matcher(strBetween);
              lastMatch = -1;
              while (matcher.find()) {
                lastMatch = matcher.start();
              }
              // 找到的“称” 距离右边的法律不超过2个字符
              if (-1 != lastMatch && strBetween.length() - lastMatch <= 2) {
                sameLawMapper.put(ne.getSource().substring(1, ne.getSource().length() - 1),
                    prev.getSource().substring(1, prev.getSource().length() - 1));
              }
            }
          }
        }
      }
      prev = ne;
    }
  }

}

