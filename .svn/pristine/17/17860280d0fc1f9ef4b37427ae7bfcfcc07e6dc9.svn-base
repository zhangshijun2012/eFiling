package com.sinosoft.efiling.service;

import java.util.ArrayList;
import java.util.List;

import com.sinosoft.efiling.hibernate.dao.FileDeadlineDao;
import com.sinosoft.efiling.hibernate.dao.ProductDao;
import com.sinosoft.efiling.hibernate.entity.FileDeadline;
import com.sinosoft.efiling.hibernate.entity.ProductRisk;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.service.ServiceSupport;

public class FileDeadlineService extends ServiceSupport<FileDeadline, FileDeadlineDao> {
	ProductDao productDao;

	/**
	 * 得到departmentId当前对riskCode险种的期限控制
	 * 
	 * @param departmentId
	 *            部门id
	 * @param riskCode
	 *            险种代码
	 * @return
	 */
	public FileDeadline getCurrentByRiskCode(String departmentId, String riskCode) {
		String hql = "FROM " + dao.getEntityClassName()
				+ " WHERE department.id = ? AND product IN (SELECT product FROM " + ProductRisk.class.getName()
				+ " WHERE id = ?) AND status = ?";
		FileDeadline entity = (FileDeadline) dao.uniqueResult(hql, new Object[] { departmentId, riskCode,
				SystemUtils.STATUS_VALID });

		return entity;
	}

	/**
	 * 
	 * 得到departmentId当前对产品线productId的期限控制
	 * 
	 * @param departmentId
	 *            部门id
	 * @param productId
	 *            产品线
	 * @return
	 */
	public FileDeadline getCurrent(String departmentId, String productId) {
		String hql = "FROM " + dao.getEntityClassName() + " WHERE department.id = ? AND product.id = ? AND status = ?";
		FileDeadline entity = (FileDeadline) dao.uniqueResult(hql, new Object[] { departmentId, productId,
				SystemUtils.STATUS_VALID });

		return entity;
	}

	/**
	 * 停用数据，如果未能停用任何数据，则返回false
	 * 
	 * @param entity
	 *            用此参考对象中的department和product为禁用条件
	 * @return
	 */
	public boolean invalid(FileDeadline entity) {
		String hql = "UPDATE " + dao.getEntityClassName() + " SET status = ? WHERE status = ? ";
		// department = ? AND product = ? AND
		List<Object> parameters = new ArrayList<Object>();
		parameters.add(SystemUtils.STATUS_INVALID);
		parameters.add(SystemUtils.STATUS_VALID);
		hql += " AND company = ?";
		parameters.add(entity.getCompany());

		if (StringHelper.isEmpty(entity.getFileDept())) {
			// 新增部门设置的是全部,新增的归档日期,会把分公司的以前各个部门设置的归档日期设置为无效
			hql += " AND fileDept IS NULL";
			// parameters.add(entity.getDepartment());
		} else {
			hql += " AND fileDept = ?";
			parameters.add(entity.getFileDept());
		}
		if (StringHelper.isEmpty(entity.getProduct())) {
			// 如果新增归档日期，产品线设置的是全部，会把这个部门下的所有产品线设置为无效
			hql += " AND product IS NULL";
			// hql += " AND department = ?";
			// parameters.add(entity.getDepartment());
		} else {
			hql += " AND product = ?";
			parameters.add(entity.getProduct());
		}
		return 1 == dao.execute(hql, parameters.toArray());
	}

	/**
	 * 增加一个期限控制
	 */
	@Override
	public FileDeadline save(FileDeadline entity) {
		// 将当前正在使用的版本置为无效,保证在同一部门，统一产品线下只有一个期限
		invalid(entity);
		entity.setStatus(SystemUtils.STATUS_VALID); // 新增必然为有效状态
		return super.save(entity);
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}
}
