package com.sinosoft.efiling.service;

import com.sinosoft.efiling.hibernate.dao.FileBoxVersionDao;
import com.sinosoft.efiling.hibernate.entity.FileBoxVersion;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.CustomException;
import com.sinosoft.util.service.ServiceSupport;

public class FileBoxVersionService extends ServiceSupport<FileBoxVersion, FileBoxVersionDao> {

	/**
	 * 得到当前的版本
	 * 
	 * @param companyId
	 * @return
	 */
	public FileBoxVersion getCurrentVersion(String companyId) {
		String hql = "FROM FileBoxVersion WHERE company.id = ? AND status = ?";
		FileBoxVersion fileBoxVersion = (FileBoxVersion) dao.uniqueResult(hql, new Object[] { companyId,
				SystemUtils.STATUS_VALID });

		if (fileBoxVersion == null) {
			throw new CustomException("\"" + companyId + "\"分公司尚未设置档案盒可装页数,\n请先进入\"参数设置->档案盒页数设置\"中设置档案盒的可装页数");
		}

		return fileBoxVersion;
	}

	/**
	 * 停用数据，如果未能停用任何数据，则返回false
	 * 
	 * @param entity 用此参考对象中的company为禁用条件
	 * @return
	 */
	public boolean invalid(FileBoxVersion entity) {
		String hql = "UPDATE " + dao.getEntityClassName() + " SET status = ? WHERE company.id = ? AND status = ?";
		return 1 == dao.execute(hql, new Object[] { SystemUtils.STATUS_INVALID, entity.getCompany().getId(),
				SystemUtils.STATUS_VALID });
	}

	@Override
	public FileBoxVersion save(FileBoxVersion entity) {
		invalid(entity);
		entity.setStatus(SystemUtils.STATUS_VALID); // 新增必然为有效状态
		return super.save(entity);
	}
}
