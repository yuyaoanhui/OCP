package com.ocp.rabbit.middleware.orm.mapper;

import com.ocp.rabbit.middleware.orm.model.GuideLaw;
import com.ocp.rabbit.middleware.orm.model.GuideLawExample;
import com.ocp.rabbit.middleware.orm.model.GuideLawWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 用于操作表:tb_guide_law
 */
public interface GuideLawMapper extends BaseMapper<GuideLaw, GuideLawExample> {
  /**
   *
   * @mbg.generated
   */
  @Override
  long countByExample(GuideLawExample example);

  /**
   *
   * @mbg.generated
   */
  @Override
  int deleteByExample(GuideLawExample example);

  /**
   *
   * @mbg.generated
   */
  int insert(GuideLawWithBLOBs record);

  /**
   *
   * @mbg.generated
   */
  int insertSelective(GuideLawWithBLOBs record);

  /**
   *
   * @mbg.generated
   */
  List<GuideLawWithBLOBs> selectByExampleWithBLOBs(GuideLawExample example);

  /**
   *
   * @mbg.generated
   */
  @Override
  List<GuideLaw> selectByExample(GuideLawExample example);

  /**
   *
   * @mbg.generated
   */
  int updateByExampleSelective(@Param("record") GuideLawWithBLOBs record,
      @Param("example") GuideLawExample example);

  /**
   *
   * @mbg.generated
   */
  int updateByExampleWithBLOBs(@Param("record") GuideLawWithBLOBs record,
      @Param("example") GuideLawExample example);

  /**
   *
   * @mbg.generated
   */
  @Override
  int updateByExample(@Param("record") GuideLaw record, @Param("example") GuideLawExample example);
}
