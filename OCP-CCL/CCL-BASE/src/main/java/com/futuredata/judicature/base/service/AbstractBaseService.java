package com.futuredata.judicature.base.service;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.futuredata.judicature.base.dao.BaseMapper;
import com.futuredata.judicature.base.exception.FdBizException;
import com.futuredata.judicature.base.model.BaseModel;
import com.futuredata.judicature.base.result.BaseResultCode;
import com.futuredata.judicature.sdk.utils.StringUtils;
import com.futuredata.judicature.sdk.utils.UUIDUtil;

/**
 * 
 * @author yu.yao
 *
 */
@Aspect
public abstract class AbstractBaseService<T extends BaseModel, TExample>
    implements IBaseService<T, TExample> {

  private static final Logger logger = LoggerFactory.getLogger(AbstractBaseService.class);

  private BaseMapper<T, TExample> baseDao;

  public void setBaseDao(BaseMapper<T, TExample> baseDao) {
    this.baseDao = baseDao;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws FdBizException
   */
  @Override
  public String save(T t, boolean notNull) throws FdBizException {
    try {
      if (t == null) {
        throw new FdBizException("request id", BaseResultCode.SYS_PARAM_NULL, new Object[] {t},
            new IllegalAccessException());
      }
      String id = t.getId();
      if (StringUtils.isEmpty(id)) {
        id = UUIDUtil.generatorID();
        t.setId(id);
      }
      if (notNull) {
        baseDao.insertSelective(t);
      } else {
        baseDao.insert(t);
      }
      return id;
    } catch (Exception e) {
      logger.error("新增数据发生异常", e);
      throw new FdBizException("request id", BaseResultCode.SYS_ERROR, new Object[] {t}, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean modify(T t, boolean notNull) throws FdBizException {
    try {
      if (t == null) {
        throw new FdBizException("request id", BaseResultCode.SYS_PARAM_NULL, new Object[] {t},
            new IllegalAccessException());
      }
      String id = t.getId();
      if (StringUtils.isEmpty(id)) {
        throw new FdBizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {id},
            new IllegalAccessException());
      }
      int i = 0;
      if (notNull) {
        i = baseDao.updateByPrimaryKeySelective(t);
      } else {
        i = baseDao.updateByPrimaryKey(t);
      }
      if (i > 0) {
        return true;
      }
      return false;
    } catch (Exception e) {
      logger.error("修改数据发生异常", e);
      throw new FdBizException("request id", BaseResultCode.SYS_ERROR, new Object[] {t}, e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean deleteById(String id) throws FdBizException {
    if (StringUtils.isEmpty(id)) {
      throw new FdBizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {id},
          new IllegalAccessException());
    } else {
      int i = baseDao.deleteByPrimaryKey(id);
      if (i == 1) {
        return true;
      } else
        return false;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean deleteByIds(String[] ids) throws FdBizException {
    if (ids == null || ids.length == 0) {
      throw new FdBizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {ids},
          new IllegalAccessException());
    }
    int i = baseDao.deleteByPrimaryKeys(ids);
    if (i == 1) {
      return true;
    } else
      return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T queryOneById(String id) throws FdBizException {
    if (StringUtils.isEmpty(id)) {
      throw new FdBizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {id},
          new IllegalAccessException());
    } else {
      return baseDao.selectByPrimaryKey(id);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<T> queryList(TExample example) throws FdBizException {
    if (example == null) {
      throw new FdBizException("request id", BaseResultCode.SYS_PARAM_NULL, new Object[] {example},
          new IllegalAccessException());
    }
    return baseDao.selectByExample(example);
  }
}
