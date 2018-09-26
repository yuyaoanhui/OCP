package com.ocp.rabbit.middleware.orm.mapper;

import com.ocp.rabbit.middleware.orm.model.InfoPoint;
import com.ocp.rabbit.middleware.orm.model.InfoPointExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/** 
 * 用于操作表:infopoint
 */
public interface InfoPointMapper extends BaseMapper<InfoPoint, InfoPointExample> {
    /**
     *
     * @mbg.generated
     */
    @Override
    long countByExample(InfoPointExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int deleteByExample(InfoPointExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int deleteByPrimaryKey(Long id);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insert(InfoPoint record);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insertSelective(InfoPoint record);

    /**
     *
     * @mbg.generated
     */
    @Override
    List<InfoPoint> selectByExample(InfoPointExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    InfoPoint selectByPrimaryKey(Long id);

    /**
     *
     * @mbg.generated
     */
    @Override
    int updateByExampleSelective(@Param("record") InfoPoint record, @Param("example") InfoPointExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int updateByExample(@Param("record") InfoPoint record, @Param("example") InfoPointExample example);

    /**
     *
     * @mbg.generated
     */
    @Override
    int updateByPrimaryKeySelective(InfoPoint record);

    /**
     *
     * @mbg.generated
     */
    @Override
    int updateByPrimaryKey(InfoPoint record);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insertBatch(@Param("list") List<InfoPoint> list);

    /**
     *
     * @mbg.generated
     */
    @Override
    int insertSelectiveBatch(@Param("list") List<InfoPoint> list);
}