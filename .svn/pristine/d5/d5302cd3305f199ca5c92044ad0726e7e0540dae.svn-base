package com.sinosoft.efiling.service;

import java.util.List;

import com.sinosoft.efiling.hibernate.dao.FileTypeDao;
import com.sinosoft.efiling.hibernate.dao.PrpdCodeDao;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.hibernate.entity.PrpdCode;
import com.sinosoft.efiling.hibernate.entity.PrpdCodeId;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.service.ServiceSupport;

/**
 * 承保档案资料与条码值的对应关系
 * 
 * @author LuoGang
 * 
 */
public class FileTypeService extends ServiceSupport<FileType, FileTypeDao> {
	PrpdCodeDao prpdCodeDao;

	/**
	 * 查询承保系统中新的档案资料.即尚未设定条码值的档案类型.不可手工设置的资料类型不会出现
	 * 
	 * @see SystemUtils#FILE_TYPE_FINAL_CODES *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PrpdCode> queryUnsetFileTypes() {
		int length = SystemUtils.FILE_TYPE_FINAL_CODES.length;
		StringBuilder hql = new StringBuilder(200);
		hql.append("FROM ").append(PrpdCode.class.getName());
		hql.append(" WHERE id.codeType = ? AND validStatus = ? ");

		Object[] parameters = new Object[length + 2];
		parameters[0] = SystemUtils.FILE_TYPE_CODE_TYPE;
		parameters[1] = SystemUtils.STATUS_VALID;

		// hql.append("AND id.codeCode NOT IN (");
		// hql.append("	SELECT code FROM ").append(FileType.class.getName()).append(" WHERE status = ?");
		// hql.append(" )");
		if (length > 0) {
			hql.append("AND id.codeCode NOT IN (?").append(StringHelper.copy(", ?", length - 1)).append(") ");
			System.arraycopy(SystemUtils.FILE_TYPE_FINAL_CODES, 0, parameters, 2, length);
		}
		hql.append(" ORDER BY id.codeCode");
		return (List<PrpdCode>) prpdCodeDao.query(hql.toString(), parameters);
	}

	/**
	 * 查询承保资料类型
	 * 
	 * @param code
	 * @return
	 */
	public PrpdCode getPrpdCode(String code) {
		PrpdCodeId id = new PrpdCodeId(SystemUtils.FILE_TYPE_CODE_TYPE, code);
		return prpdCodeDao.get(id);
	}

	/**
	 * 查询承保资料类型code对应的有效的条码值
	 * 
	 * @param code
	 * @return
	 */
	public FileType get(PrpdCode prpdCode) {
		String code = prpdCode.getId().getCodeCode();
		// if (StringHelper.indexInArray(code, SystemUtils.FILE_TYPE_ID_CODES) > -1) return null;

		// List<FileType> list = dao.queryByProperty(new Object[][] { { "code", code },
		// { "status", SystemUtils.STATUS_VALID } });
		// if (list == null || list.isEmpty()) return null;
		// return list.get(0);
		return dao.getByCode(code);
	}

	public PrpdCodeDao getPrpdCodeDao() {
		return prpdCodeDao;
	}

	public void setPrpdCodeDao(PrpdCodeDao prpdCodeDao) {
		this.prpdCodeDao = prpdCodeDao;
	}
}