package com.ocp.rabbit.middleware.orm.mapper;

import com.ocp.rabbit.middleware.orm.model.GuideLawIndex;
import com.ocp.rabbit.middleware.orm.model.GuideLawIndexExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/** 
 * 用于操作表:tb_guide_law_index
 */
public interface GuideLawIndexMapper extends BaseMapper<GuideLawIndex, GuideLawIndexExample> {
    /**
     *
     * @mbg.generated
     */
    @Override
    long countByExample(GuideLawIndexExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int deleteByExample(GuideLawIndexExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insert(GuideLawIndex record);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insertSelective(GuideLawIndex record);

    /**
     *
     * @mbg.generated
     */
    @Override
    List<GuideLawIndex> selectByExample(GuideLawIndexExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int updateByExampleSelective(@Param("record") GuideLawIndex record, @Param("example") GuideLawIndexExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int updateByExample(@Param("record") GuideLawIndex record, @Param("example") GuideLawIndexExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insertBatch(@Param("list") List<GuideLawIndex> list);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insertSelectiveBatch(@Param("list") List<GuideLawIndex> list);
}