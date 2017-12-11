package com.futuredata.judicature.base.service;

import java.util.List;

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
public abstract class AbstractBaseServiceImpl<T extends BaseModel, TExample>
		implements IBaseService<T, TExample> {

	private static final Logger logger = LoggerFactory
			.getLogger(AbstractBaseServiceImpl.class);

	private BaseMapper<T, TExample> baseDao;

	public void setBaseDao(BaseMapper<T, TExample> baseDao) {
		this.baseDao = baseDao;
	}

	@Override
	public String save(T t) throws FdBizException {
		try {
			if (t == null) {
				throw new IllegalArgumentException("参数不能为空!");
			}
			String id = t.getId();
			if (StringUtils.isEmpty(id)) {
				id = UUIDUtil.generatorID();
				t.setId(id);
			}
			baseDao.insert(t);
			return id;
		} catch (Exception e) {
			logger.error("新增数据发生异常", e);
			throw new FdBizException("request id", BaseResultCode.SYS_ERROR,
					new Object[] { t }, e);
		}
	}

	@Override
	public boolean update(T t) throws FdBizException {
		try {
			if (t == null) {
				throw new IllegalArgumentException("参数不能为空!");
			}
			String id = t.getId();
			if (StringUtils.isEmpty(id)) {
				throw new IllegalArgumentException("对象id不能为空!");
			}
			int i = baseDao.updateByPrimaryKey(t);
			if (i > 0) {
				return true;
			}
			return false;
		} catch (Exception e) {
			logger.error("修改数据发生异常", e);
			throw new FdBizException("request id", BaseResultCode.SYS_ERROR,
					new Object[] { t }, e);
		}
	}

	@Override
	public boolean delete(String id) throws FdBizException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteBatch(String[] ids) throws FdBizException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T findOne(String id) throws FdBizException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<T> findList(TExample example) throws FdBizException {
		// TODO Auto-generated method stub
		return null;
	}
}
