package com.sinosoft.efiling.struts2.action;

import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.File;
import com.sinosoft.efiling.service.DocumentAuditService;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.hibernate.entity.EntitySupport;
import com.sinosoft.util.json.JSONObject;

/**
 * 影像文件上传的Action
 * 
 * @author LuoGang
 * 
 */
@SuppressWarnings("serial")
public class FileImageAction extends DocumentAction {
	/** 如果单证不存在时,用于保存数据的service */
	DocumentAuditService documentAuditService;

	@Override
	public Document createEntity() {
		return entity;
	}

	private File file;
	private String xml;

	public void validateSave() {
		if (StringHelper.isEmpty(xml)) {
			addActionError("XML cann't be empty!");
		}
		// else {
		// if (file == null) file = new File();
		// file.setUser(getCurrentUser());
		// file.setDepartment(getCurrentUserSession().getCurrentDepartment());
		// file.setCompany(getCurrentUserSession().getCurrentCompany());
		// }
	}

	public String save() {
		// 影响文件上传，在上传文件提交时已经保存FileIndex数据，此处主要用于共享上传的数据至联合投保的另外保单
		list = service.saveImages(xml, getCurrentUserSession());
		return this.dispatchSaveSuccess("文件上传成功!");
	}

	public String query() {
		entity = service.getDocument(no);
		if (entity == null) {
			// 如果此数据不存在,首先调用补数的业务
			entity = documentAuditService.save(no);
		}
		JSONObject json;
		if (entity != null) {
			json = entity.toJSONObject(EntitySupport.DEFAULT_VALUE_JSON_CONVERTER);
		} else {
			// 补登数据
			json = new JSONObject();
		}
		json.put("success", entity != null);
		return dispatchSuccess(json);
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	/**
	 * @return the documentAuditService
	 */
	public DocumentAuditService getDocumentAuditService() {
		return documentAuditService;
	}

	/**
	 * @param documentAuditService the documentAuditService to set
	 */
	public void setDocumentAuditService(DocumentAuditService documentAuditService) {
		this.documentAuditService = documentAuditService;
	}

}
