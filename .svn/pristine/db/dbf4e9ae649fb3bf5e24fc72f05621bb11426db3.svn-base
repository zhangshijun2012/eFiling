package com.sinosoft.efiling.struts2.action;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.service.DocumentService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class DocumentAction extends EntityActionSupport<Document, DocumentDao, DocumentService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7786843880667726532L;

	/**
	 * 用于单份处理时查询的状态
	 */
	public static final Map<String, String> DOCUMENT_STATUS_QUERY = new LinkedHashMap<String, String>();
	static {
		DOCUMENT_STATUS_QUERY.put(SystemUtils.DOCUMENT_STATUS_UNFILE,
				SystemUtils.getDocumentStatusDescription(SystemUtils.DOCUMENT_STATUS_UNFILE));
		DOCUMENT_STATUS_QUERY.put(SystemUtils.DOCUMENT_STATUS_LACK,
				SystemUtils.getDocumentStatusDescription(SystemUtils.DOCUMENT_STATUS_LACK));
		DOCUMENT_STATUS_QUERY.put(SystemUtils.DOCUMENT_STATUS_FILE,
				SystemUtils.getDocumentStatusDescription(SystemUtils.DOCUMENT_STATUS_FILE));
		DOCUMENT_STATUS_QUERY.put(SystemUtils.DOCUMENT_STATUS_FILE_MANUAL,
				SystemUtils.getDocumentStatusDescription(SystemUtils.DOCUMENT_STATUS_FILE_MANUAL));
	}

	/** 归档状态 */
	public static final Map<String, String> DOCUMENT_FILE_STATUS = SystemUtils.DOCUMENT_FILE_STATUS;

	@Override
	public Class<Document> getEntityClass() {
		return Document.class;
	}

	protected String no;// 业务号

	@Override
	public String view() {
		String result = super.view();
		// list = service.queryImages(id);
		list = service.queryImages(entity);
		return result;
	}

	/**
	 * 从其他系统open window后查看文件,返回open.jsp,此JSP需要引入所有需要的css文件
	 * 
	 * @return
	 */
	public String viewOpen() {
		businessNo = "";
		this.entity = service.getDocument(no);
		if (entity != null) list = service.queryImages(entity);
		else {
			String documentType = DocumentService.getDocumentType(no);
			if (SystemUtils.DOCUMENT_TYPE_POLICY.equals(documentType)) {
				// 根据NO查找对应的投保单号
				String sql = "SELECT proposalNo FROM PrpCMain WHERE policyNo = ?";
				this.businessNo = StringHelper.trim(service.getDao().uniqueResultSQL(sql, new String[] { no }));
			} else if (SystemUtils.DOCUMENT_TYPE_PROPOSAL.equals(documentType)) {
				// NO是投保单号
				String sql = "SELECT policyNo FROM PrpCMain WHERE proposalNo = ?";
				this.businessNo = StringHelper.trim(service.getDao().uniqueResultSQL(sql, new String[] { no }));
			}
		}

		return SUCCESS;
	}

	/**
	 * 查看文件的所有影像文件
	 * 
	 * @return
	 */
	public String viewImages() {
		entity = service.get(id);
		return SUCCESS;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	protected String businessNo;

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
}
