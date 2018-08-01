package com.ocp.base.service;

import java.util.List;

import org.aspectj.lang.annotation.Aspect;

import com.ocp.base.dao.BaseMapper;
import com.ocp.base.exception.BizException;
import com.ocp.base.model.BaseModel;
import com.ocp.base.result.BaseResultCode;
import com.ocp.sdk.utils.StringUtils;
import com.ocp.sdk.utils.UUIDUtil;

/**
 * 
 * @author yu.yao
 *
 */
@Aspect
public abstract class AbstractBaseService<T extends BaseModel, TExample>
    implements IBaseService<T, TExample> {

  private BaseMapper<T, TExample> baseDao;

  public void setBaseDao(BaseMapper<T, TExample> baseDao) {
    this.baseDao = baseDao;
  }

  /**
   * {@inheritDoc}
   * 
   * @throws BizException
   */
  @Override
  public String save(T t, boolean notNull) throws BizException {
    if (t == null) {
      throw new BizException("request id", BaseResultCode.SYS_PARAM_NULL, new Object[] {t});
    }
    String id = t.getId();
    if (StringUtils.isEmpty(id)) {
      id = UUIDUtil.generatorID();
      t.setId(id);
    }
    int i = 0;
    if (notNull) {
      i = baseDao.insertSelective(t);
    } else {
      i = baseDao.insert(t);
    }
    if (i == 0) {
      throw new BizException("request id", BaseResultCode.SYS_INSERT_FAIL, new Object[] {t});
    } else
      return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean modify(T t, boolean notNull) throws BizException {
    if (t == null) {
      throw new BizException("request id", BaseResultCode.SYS_PARAM_NULL, new Object[] {t});
    }
    String id = t.getId();
    if (StringUtils.isEmpty(id)) {
      throw new BizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {id});
    }
    int i = 0;
    if (notNull) {
      i = baseDao.updateByPrimaryKeySelective(t);
    } else {
      i = baseDao.updateByPrimaryKey(t);
    }
    if (i == 0) {
      throw new BizException("request id", BaseResultCode.SYS_UPDATE_FAIL, new Object[] {t});
    } else
      return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean deleteById(String id) throws BizException {
    if (StringUtils.isEmpty(id)) {
      throw new BizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {id});
    } else {
      int i = baseDao.deleteByPrimaryKey(id);
      if (i == 0) {
        throw new BizException("request id", BaseResultCode.SYS_DELETE_FAIL, new Object[] {id});
      } else
        return true;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean deleteByIds(String[] ids) throws BizException {
    if (ids == null || ids.length == 0) {
      throw new BizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {ids});
    }
    int i = baseDao.deleteByPrimaryKeys(ids);
    if (i == 0) {
      throw new BizException("request id", BaseResultCode.SYS_DELETE_FAIL, new Object[] {ids});
    } else
      return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public T queryOneById(String id) throws BizException {
    if (StringUtils.isEmpty(id)) {
      throw new BizException("request id", BaseResultCode.SYS_ID_NULL, new Object[] {id});
    } else {
      try {
        return baseDao.selectByPrimaryKey(id);
      } catch (Exception e) {
        throw new BizException("request id", BaseResultCode.SYS_QUERY_FAIL, new Object[] {id}, e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<T> queryList(TExample example) throws BizException {
    if (example == null) {
      throw new BizException("request id", BaseResultCode.SYS_PARAM_NULL, new Object[] {example});
    } else {
      try {
        return baseDao.selectByExample(example);
      } catch (Exception e) {
        throw new BizException("request id", BaseResultCode.SYS_QUERY_FAIL,
            new Object[] {example}, e);
      }
    }
  }
}
