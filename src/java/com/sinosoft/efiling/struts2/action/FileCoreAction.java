package com.sinosoft.efiling.struts2.action;

import java.util.Set;

import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.FileType;
import com.sinosoft.efiling.service.DocumentAuditService;
import com.sinosoft.efiling.util.SystemUtils;
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
public class FileCoreAction extends FileCoreApproveAction {
	/** 如果单证不存在时,用于保存数据的service */
	DocumentAuditService documentAuditService;

	@Override
	public Document createEntity() {
		return entity;
	}

	private String xml;

	public void validateSave() {
		if (StringHelper.isEmpty(xml)) {
			addActionError("XML cann't be empty!");
		}
	}

	public String save() {
		// list = service.saveImages(xml, getCurrentUserSession());
		// 保存承保资料上传的附件
		JSONObject json = service.save(xml, getCurrentUserSession());
		return this.dispatchSuccess(json);
		// return this.dispatchSaveSuccess("文件上传成功!");
	}

	public String query() {
		entity = service.getDocument(getNo());
		if (entity == null) {
			// 如果此数据不存在,首先调用补数的业务
			entity = documentAuditService.save(getNo());
		}
		JSONObject json;
		if (entity != null) {
			json = entity.toJSONObject(EntitySupport.DEFAULT_VALUE_JSON_CONVERTER);
			
			Set<DocumentFile> documentFiles = entity.getDocumentFiles();
			JSONObject fileTypes = new JSONObject();
			String required = ""; // 1.核保勾选的资料 0.上传的资料
			FileType fileType = null;
			for (DocumentFile documentFile : documentFiles) {
				required = documentFile.getRequired();
				if (SystemUtils.YES.equals(required)) {
					// 核保勾选的资料
					fileType = documentFile.getFileType();
					fileTypes.put(fileType.getId(), fileType);
				}
			}
			// 加载核保勾选的资料类型
			json.put("fileTypes", fileTypes);
		} else {
			json = new JSONObject();
		}
		json.put("success", entity != null);
		return dispatchSuccess(json);
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public DocumentAuditService getDocumentAuditService() {
		return documentAuditService;
	}

	public void setDocumentAuditService(DocumentAuditService documentAuditService) {
		this.documentAuditService = documentAuditService;
	}

}
