package com.sinosoft.efiling.struts2.action;

import java.util.List;

import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.service.DocumentAuditService;
import com.sinosoft.efiling.service.DocumentService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.Helper;
import com.sinosoft.util.json.JSONObject;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class DocumentAuditServiceAction extends EntityActionSupport<Document, DocumentDao, DocumentService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3373369754177726170L;
	protected DocumentAuditService documentAuditService;
	protected String businessNo;
	protected String type;
	private String[] businessNos; // 多个业务关系代码
	private String no; // 业务号

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String[] getBusinessNos() {
		return businessNos;
	}

	public void setBusinessNos(String[] businessNos) {
		this.businessNos = businessNos;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public DocumentAuditService getDocumentAuditService() {
		return documentAuditService;
	}

	public void setDocumentAuditService(DocumentAuditService documentAuditService) {
		this.documentAuditService = documentAuditService;
	}

	/**
	 * 保存业务数据
	 */
	public String save() {
		getLogger().info("DocumentAuditServiceAction.save(),生成保单数据接口调用:" + businessNo);
		try {
			documentAuditService.save(businessNo);
			getLogger().info("DocumentAuditServiceAction.save(),生成保单数据接口调用成功:" + businessNo);
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().error(
					"DocumentAuditServiceAction.save(),生成保单数据接口调用失败:" + businessNo + "," + e.getMessage(),
					e);
		}
		return null;
	}

	/**
	 * 承保系统验链接到eFiling系统,判断业务关系代码是否可以提交核保
	 * 
	 * @return
	 */
	public String checkCommit() {
		// String[] comcodes = businessNos;
		JSONObject json = new JSONObject();
		List<String> nos = null;
		for (String businessNo : businessNos) {
			getLogger().info("DocumentAuditServiceAction.chekCommit(),校验业务关系代码是否:" + businessNo);
			nos = documentAuditService.checkCommitUndwrt(businessNo);
			if (!Helper.isEmpty(nos)) json.put(businessNo, nos);
		}

		/*
		 * StringBuffer buffer = new StringBuffer();
		 * buffer.append("{'LB02339' : ['9103015000130000031000', '9103015000130000031001', '9103015000130000031002'],"
		 * + "'LB02340' : ['9103015000130000032000', '9103015000130000032001', '9103015000130000032002']," +
		 * "'LB02341' : ['9103015000130000033000', '9103015000130000033001']}");
		 */
		return dispatchSuccess(json);
	}

	/**
	 * 保存数据,共享相同资料,判断是否自动单证审核
	 * 
	 * @return
	 */
	public String handle() {
		getLogger().info("DocumentAuditServiceAction.handle(),共享核保资料接口调用:" + businessNo);
		boolean result = false;
		try {
			result = documentAuditService.handle(businessNo);
			getLogger().info("DocumentAuditServiceAction.handle(),共享核保资料接口调用成功:" + businessNo);
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().error("DocumentAuditServiceAction.handle(),共享核保资料接口调用失败:" + businessNo + "," + e.getMessage(),
					e);
		}
		write(String.valueOf(result));
		return null;
	}

	/**
	 * 承保系统单证审核时点击单证信息,访问eFiling系统的这个方法
	 * 
	 * @return 返回某个业务号下的档案类型的一个xml
	 */
	public String show() {
		List<DocumentFile> documentFiles = documentAuditService.queryDocumentFiles(no);
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>\n");
		DocumentFile documentFile = null;
		for (int i = 0; i < documentFiles.size(); i++) {
			documentFile = documentFiles.get(i);
			if (!SystemUtils.DOCUMENT_FILE_STATUS_FILE.equals(documentFile.getStatus())) continue;
			xml.append("<DocumentFile>");
			xml.append("   <code>").append(documentFile.getFileTypeCode()).append("</code>\n");
			// xml.append("   <check>").append(documentFile.getStatus().equals("0") ? "否" : "是").append("</check>\n");
			// 表示已经归档
			xml.append("    <file>").append(documentFile.getFile().getFileId()).append("</file>\n");
			xml.append("</DocumentFile>\n");
		}
		xml.append("</root>");
		return dispatchSuccess(xml.toString());
	}

}
