package com.sinosoft.efiling.struts2.action;

import java.util.Date;
import java.util.List;

import com.sinosoft.efiling.hibernate.dao.FileTypeDao;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.hibernate.entity.PrpdCode;
import com.sinosoft.efiling.service.FileTypeService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.json.JSONArray;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class FileTypeAction extends EntityActionSupport<FileType, FileTypeDao, FileTypeService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4140030828791395716L;

	/** 可以进行配置的资料类型 */
	private List<PrpdCode> unsetFileTypes;
	private String[] status;
	private String[] fileModel;

	/** 条码值主键的长度 */
	public static final int ID_LENGTH = 3;

	@Override
	public void validateAppend() {
		unsetFileTypes = service.queryUnsetFileTypes();
		super.validateAppend();
		// 查询最大的条码值
		// String hql = "SELECT MAX(TO_NUMBER(F.id)) FROM T_F_FILE_TYPE F ";
		// hql += " WHERE F.id NOT LIKE 'IMG%' AND F.id <> 'MANUAL'";
		String hql = "SELECT MAX(CAST(f.id AS int)) FROM " + FileType.class.getName() + " f WHERE f.fileModel = ? ";
		int maxId = NumberHelper.intValue(service.getDao().uniqueResult(hql,
				new Object[] { SystemUtils.FILE_MODEL_FILE }));
		maxId++;
		id = NumberHelper.formatInteger(maxId, false);
		id = StringHelper.copy("0", ID_LENGTH - id.length()) + id;
		entity = new FileType();
		entity.setId(id);
	}

	@Override
	public void validateSave() {
		super.validateSave();
		if (StringHelper.isEmpty(entity) || StringHelper.isEmpty(entity.getId())) {
			addFieldError("id", "条码值必须输入!");
			return;
		}
		if (null != service.get(entity.getId())) {
			addFieldError("id", "条码值\"" + entity.getId() + "\"已经存在,不能重复!");
			return;
		}
		String code = entity.getCode();
		PrpdCode prpdCode = service.getPrpdCode(code);
		if (StringHelper.isEmpty(code) || prpdCode == null) {
			addFieldError("code", "指定的承保资料\"" + code + "\"不存在!");
			return;
		}
		FileType bean = service.get(prpdCode);
		if (bean != null) {
			addFieldError("code", "指定的承保资料\"" + prpdCode.getName() + "(" + code + ")\"已经配置了条码值\"" + bean.getId()
					+ "\",如果的确需要重新配置,请先将原条码值禁用!");
			return;
		}
		entity.setAlias(StringHelper.trim(prpdCode.getName(), prpdCode.getCodeEName()));
		if (StringHelper.isEmpty(entity.getName())) {
			entity.setName(entity.getAlias());
		}
		entity.setFileModel(SystemUtils.FILE_MODEL_FILE); // 承保资料
		entity.setUser(getCurrentUser());
		entity.setCompany(entity.getDepartment().getRoot());
		entity.setInsertTime(new Date());
		entity.setUpdateTime(new Date());
		entity.setStatus(SystemUtils.STATUS_VALID);
	}

	@Override
	public void validateEdit() {
		addActionError("条码值不允许修改。如要更改配置，请禁用此条码值后新增。");
	}

	@Override
	public void validateUpdate() {
		validateEdit();
	}

	@Override
	public void validateQuery() {
		super.validateQuery();

		this.addLikeQuery("t.id", id);
		this.addLikeQuery("t.name", name);
		this.addQuery("t.status", status);
		if (fileModel == null)
			this.addQuery("t.fileModel", SystemUtils.FILE_MODEL_FILE);
		else
			this.addQuery("t.fileModel", fileModel);

		if (this.queryString.length() > 0) {
			queryString.insert(0, "WHERE ");
		}
		queryString.insert(0, "FROM " + FileType.class.getName() + " t LEFT JOIN t.updateUser u ");
		queryString.insert(0, "SELECT t.id, t.id AS no, t.name, t.status, u.name AS updateUserName, t.updateTime ");

		addOrderString("ORDER BY t.id");
	}

	public void validateDisable() {
		this.entity = service.get(id);
		if (entity == null) {
			this.addActionError("数据\"" + id + "\"不存在!");
		}
		if (SystemUtils.STATUS_INVALID.equals(entity.getStatus())) {
			// 已经被禁用
			this.addActionError("\"" + entity.getName() + "\"已被\"" + entity.getUser().getName() + "\"在\""
					+ DateHelper.format(entity.getUpdateTime()) + "\"禁用!");
		}
		if (!SystemUtils.FILE_MODEL_FILE.equals(entity.getFileModel())
				|| StringHelper.indexInArray(entity.getCode(), SystemUtils.FILE_TYPE_FINAL_CODES) >= 0) {
			// 不允许手动配置的资料
			this.addActionError("\"" + entity.getName() + "\"不允许手动更改!");
		}
	}

	/**
	 * 禁用数据
	 * 
	 * @return
	 */
	public String disable() {
		entity.setStatus(SystemUtils.STATUS_INVALID);
		entity.setUpdateUser(getCurrentUser());
		entity.setUpdateTime(new Date());
		service.save(entity);
		return this.dispatchSaveSuccess();
	}

	/**
	 * 查询所有资料类型
	 * 
	 * @return
	 */
	public String queryAll() {
		list = service.query("FROM " + FileType.class.getName() + " ORDER BY id");
		return this.dispatchSuccess(new JSONArray(list));
	}

	public String[] getStatus() {
		return status;
	}

	public void setStatus(String[] status) {
		this.status = status;
	}

	public List<PrpdCode> getUnsetFileTypes() {
		return unsetFileTypes;
	}

	public void setUnsetFileTypes(List<PrpdCode> unsetFileTypes) {
		this.unsetFileTypes = unsetFileTypes;
	}

	public String[] getFileModel() {
		return fileModel;
	}

	public void setFileModel(String[] fileModel) {
		this.fileModel = fileModel;
	}

}
