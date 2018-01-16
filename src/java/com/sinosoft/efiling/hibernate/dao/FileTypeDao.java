package com.sinosoft.efiling.hibernate.dao;

import java.util.List;

import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.hibernate.dao.EntityDaoSupport;

public class FileTypeDao extends EntityDaoSupport<FileType> {

	/**
	 * 得到code对应的有效条码,如果code对应多个条码对象，则优先返回id与code一致的对象
	 * 
	 * @param code
	 * @return
	 */
	public FileType getByCode(String code) {
		List<FileType> list = queryByProperty(new Object[][] { { "code", code }, { "status", SystemUtils.STATUS_VALID } });
		if (list == null || list.isEmpty()) return null;
		for (FileType fileType : list) {
			if (fileType.getId().equals(code)) return fileType;
		}
		return list.get(0);
	}

}
