package com.sinosoft.efiling.struts2.action;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sinosoft.efiling.hibernate.dao.FileDeadlineDao;
import com.sinosoft.efiling.hibernate.entity.FileDeadline;
import com.sinosoft.efiling.service.FileDeadlineService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.efiling.util.UserSessionEntity;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class FileDeadlineAction extends EntityActionSupport<FileDeadline, FileDeadlineDao, FileDeadlineService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3373369754177726170L;

	/**
	 * 
	 */
	public static final Map<String, String> CTRL_TYPES = new LinkedHashMap<String, String>();

	static {
		CTRL_TYPES.put(SystemUtils.DOCUMENT_STATUS_UNFILE,
				SystemUtils.getDocumentStatusDescription(SystemUtils.DOCUMENT_STATUS_UNFILE));
		CTRL_TYPES.put(SystemUtils.DOCUMENT_STATUS_FILE,
				SystemUtils.getDocumentStatusDescription(SystemUtils.DOCUMENT_STATUS_FILE));
	}

	private String productId;
	private String companyId;
	private String deptId;
	private String days;
	private String[] ctrlTypes;
	private String[] status;

	public String[] getCtrlTypes() {
		return ctrlTypes;
	}

	public void setCtrlTypes(String[] ctrlTypes) {
		this.ctrlTypes = ctrlTypes;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDays() {
		return days;
	}

	public void setDays(String days) {
		this.days = days;
	}
	@Override
	public void validateIndex() {
		// TODO Auto-generated method stub
		super.validateIndex();
		UserSessionEntity user = getCurrentUserSession();
		getRequest().setAttribute("companies", user.getCurrentCompanies());

		// List<Company> departments = (List<Company>) getSession().getAttribute("allInternalDepartments");
		// String companyId = user.getCurrentCompany().getId();
		// String sessionName = "CurrentInternalDepartmentsJSON" + companyId;
		// if (getSession().getAttribute(sessionName) == null) {
		// List<Company> departments = getCurrentInternalDepartments(); // companyService.getInternal();
		// JSONArray json = new JSONArray();
		// for (Company dept : departments) {
		// JSONObject o = dept.toJSONObject();
		// dept = CompanyService.getCompany(dept); // 所在分公司代码
		// o.put("companyId", dept == null ? "" : dept.getId());
		// json.put(o);
		// }
		// getSession().setAttribute("allInternalDepartments", json.toString());
		// }

		getRequest().setAttribute("currentInternalDepartments", getCurrentInternalDepartmentsJSON());
		Map<String, String> map = new ResourcesMap(CTRL_TYPES);
		getRequest().setAttribute("ctrlTypes", map);
		getRequest().setAttribute("products", SystemUtils.PRODUCTS);
		getRequest().setAttribute("statuses", new ResourcesMap(SystemUtils.STATUSES));
	}

	@Override
	public void validateAppend() {
		// TODO Auto-generated method stub
		super.validateAppend();
		validateIndex();
		getRequest().setAttribute("departments", getCurrentInternalDepartments());
		entity = new FileDeadline();
		// 默认为当前机构
		entity.setCompany(getCurrentUserSession().getCurrentCompany());
		// 默认份数
		entity.setDays(SystemUtils.FILE_DEADLINE_DEFAULT);
	}

	@Override
	public void validateSave() {
		// TODO Auto-generated method stub
		super.validateSave();
		if (StringHelper.isEmpty(entity) || StringHelper.isEmpty(entity.getCompany())) {

			addFieldError("company", "必须输入分公司！");
			return;
		}
		/*
		 * String productId = entity.getProduct().getId();
		 * String departmentId = entity.getFileDept().getId();
		 * //得到当前已经存在的分公司部门产品线的归档期限
		 * FileDeadline fileDeadline = service.getCurrent(departmentId, productId);
		 * if (!StringHelper.isEmpty(fileDeadline)) {
		 * 
		 * addFieldError("days", entity.getCompany().getId() + "的部门: " +
		 * entity.getFileDept().getId() + "的产品线: " + entity.getProduct().getId() + " 已经存在了归档期限!");
		 * return;
		 * }
		 */
		// 设置分公司属性
		entity.setCompany(companyService.get(entity.getCompany().getId()));
		// 设置部门属性
		if (entity.getFileDept() != null && !StringHelper.isEmpty(entity.getFileDept().getId())) {
			entity.setFileDept(companyService.get(entity.getFileDept().getId()));
		} else {
			// 适用于所有部门
			entity.setFileDept(null);
		}
		// 设置产品线
		if (entity.getProduct() != null && !StringHelper.isEmpty(entity.getProduct().getId())) {
			entity.setProduct(service.getProductDao().get(entity.getProduct().getId()));
		} else {
			entity.setProduct(null);
		}
		// entity.setCtrlType("0");
		entity.setInsertTime(new Date());
		entity.setStatus(SystemUtils.STATUS_VALID);
		entity.setUser(getCurrentUser());

	}

	@Override
	public void validateQuery() {
		// TODO Auto-generated method stub
		super.validateQuery();

		queryString.append("SELECT d.id, d.company.name AS companyName, f.name AS fileDeptName, ");
		queryString.append("p.name AS productName, d.days, d.ctrlType, d.status," + " d.user.name, d.insertTime ");
		queryString.append("FROM ").append(FileDeadline.class.getName()).append(" d ");
		queryString.append("LEFT JOIN d.fileDept f LEFT JOIN d.product p ");
		queryString.append("WHERE 1 = 1 ");

		this.addCompanyQuery("d.company");

		this.addQuery("d.company.id", companyId);
		this.addQuery("d.product.id", productId);
		this.addQuery("d.fileDept.id", deptId);
		this.addQuery("d.days", days);
		this.addQuery("d.ctrlType", ctrlTypes);
		this.addQuery("d.status", status);
		// if (this.queryString.length() > 0) {
		// queryString.insert(0, "WHERE ");
		// }
		// queryString.insert(0, "FROM " + FileDeadline.class.getName()
		// + " d LEFT JOIN d.fileDept f LEFT JOIN d.product p ");
		// queryString
		// .insert(0,
		// "SELECT d.id, d.company.name AS companyName, f.name AS fileDeptName, p.name AS productName, d.days, d.ctrlType, d.status,"
		// + " d.user.name, d.insertTime ");

		addOrderString("ORDER BY d.company.id, f.id, p.id, d.insertTime DESC");
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

}
