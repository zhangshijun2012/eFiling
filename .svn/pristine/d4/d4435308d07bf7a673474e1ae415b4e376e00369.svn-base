package com.sinosoft.efiling.struts2.action;

import java.util.Date;

import com.sinosoft.efiling.hibernate.dao.FileBoxVersionDao;
import com.sinosoft.efiling.hibernate.entity.FileBoxVersion;
import com.sinosoft.efiling.service.FileBoxVersionService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class FileBoxVersionAction extends
		EntityActionSupport<FileBoxVersion, FileBoxVersionDao, FileBoxVersionService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6358463381332955636L;
	/** 仅有日期的启用日期的格式 */
	public static final String ENABLE_TIME_FORMAT_DATE_ONLY = "yyyy-MM-dd";
	/** yyyy-MM-dd HH:mm:ss启用日期的格式 */
	public static final String ENABLE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private String companyId;
	private String[] insertTime;
	private String[] status;

	@Override
	public void validateIndex() {
		getRequest().setAttribute("companies", getCurrentUserSession().getCurrentCompanies());
		getRequest().setAttribute("status", new ResourcesMap(SystemUtils.STATUSES));
	}

	@Override
	public void validateQuery() {
		super.validateQuery();

		queryString.append("SELECT id, company.name, capacity, status, user.name, insertTime ");
		queryString.append("FROM ").append(FileBoxVersion.class.getName()).append(" ");
		queryString.append("WHERE 1 = 1 ");

		this.addCompanyQuery("company");

		this.addQuery("company.id", companyId);
		this.addQuery("status", status);
		this.addBetweenQuery("insertTime", insertTime, Date.class);

		// if (this.queryString.length() > 0) {
		// queryString.insert(0, "WHERE ");
		// }
		// queryString.insert(0, " insertTime, user.name FROM " + FileBoxVersion.class.getName() + " ");
		// queryString.insert(0, "SELECT id, company.id || ' - ' || company.name, capacity, status, ");
		addOrderString("ORDER BY company ASC, insertTime DESC, id");
	}

	@Override
	public void validateAppend() {
		validateIndex();
		entity = new FileBoxVersion();

		// 默认为当前机构
		entity.setCompany(getCurrentUserSession().getCurrentCompany());

		// 默认份数
		entity.setCapacity(SystemUtils.DEFAULT_FILE_BOX_CAPACITY);

		super.validateAppend();
	}

	@Override
	public void validateSave() {
		super.validateSave();
		if (entity.getCompany() != null && !StringHelper.isEmpty(entity.getCompany().getId())) entity
				.setCompany(companyService.get(entity.getCompany().getId()));
		if (entity.getCompany() == null || StringHelper.isEmpty(entity.getCompany().getId())) {
			addFieldError("entity.company", "请选择所属分公司!");
			return;
		}
		if (entity.getCapacity() <= 0) {
			addFieldError("entity.capacity", "请输入大于0的份数!");
			return;
		}
		// entity.setUser(getCurrentUser());
		entity.setUser(getCurrentUser());
		entity.setInsertTime(new Date());
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String[] getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String[] insertTime) {
		this.insertTime = insertTime;
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

}
