package com.sinosoft.efiling.struts2.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.sinosoft.efiling.hibernate.dao.DocumentDao;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.service.FileApproveService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.ContentTypeHelper;
import com.sinosoft.util.Helper;
import com.sinosoft.util.POIHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class FileCoreApproveAction extends EntityActionSupport<Document, DocumentDao, FileApproveService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1403885798213284109L;

	protected String[] ids;

	/* 是否下载全部 */
	protected String all;

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String getAll() {
		return all;
	}

	public void setAll(String all) {
		this.all = all;
	}

	public void validateIndex() {
		// super.validateIndex();
		// 业务部门
		getRequest().setAttribute("departments", getCurrentInternalDepartments());
		Map<String, String> map = new ResourcesMap(SystemUtils.DOCUMENT_STATUS);
		getRequest().setAttribute("documentStatus", map);
		map = new ResourcesMap(SystemUtils.DOCUMENT_TYPES);
		map.remove(SystemUtils.DOCUMENT_TYPE_VISA);
		getRequest().setAttribute("documentTypes", map);
		types = new String[] { SystemUtils.DOCUMENT_TYPE_PROPOSAL, SystemUtils.DOCUMENT_TYPE_POLICY,
				SystemUtils.DOCUMENT_TYPE_ENDOR };
		// 产品线
		getRequest().setAttribute("products", SystemUtils.PRODUCTS);
		map = new ResourcesMap(SystemUtils.FILE_APPROVE_STATUS);
		getRequest().setAttribute("fileStatus", map);

	}

	@Override
	public void validateQuery() {
		queryString = new StringBuilder(100);
		queryParameters = new ArrayList<Object>();
		addQueryString(SystemHelper.getProperty("sql.file.core.audit"), new Object[] {
				SystemUtils.DOCUMENT_FILE_STATUS_FILE, SystemUtils.FILE_APPROVE_STATUS_UNAUDITED,
				SystemUtils.DOCUMENT_FILE_STATUS_FILE, SystemUtils.FILE_APPROVE_STATUS_UNAUDITED,
				SystemUtils.FILE_APPROVE_STATUS_AUDITED, SystemUtils.FILE_APPROVE_STATUS_NOPASSED });
		addCompanyIdQuery("T.BUSINESS_COMPANY_ID");
		this.addLikeQuery("T.PROPOSAL_NO", proposalNo);
		this.addLikeQuery("T.POLICY_NO", policyNo);
		this.addLikeQuery("T.ENDOR_NO", endorNo);
		this.addLikeQuery("PC.COMCODE", departmentId);
		this.addLikeQuery("T.AGENT_NAME", agentName);
		this.addLikeQuery("T.BUSINESS_NO", businessNo);
		this.addLikeQuery("T.APPLICANT", policyHolder);
		this.addQuery("T.PRODUCT_ID", productId);
		this.addQuery("T.TYPE", types);
		this.addQuery("T.FILE_APPROVE_STATUS", fileApprvoeStatus);

		addOrderString("ORDER BY T.FILE_APPROVE_STATUS");
		// addOrderString("ORDER BY T.SALES_TIME DESC, SUBSTR(T.POLICY_NO, 11, 9) DESC NULLS LAST, T.ENDOR_NO NULLS LAST, T.PROPOSAL_NO DESC, T.NO");
	}

	@Override
	public String query() {
		String queryString = this.queryString.toString();
		if (orderString != null) {
			queryString += (" " + orderString.toString());
		}
		Object[] parameters = this.queryParameters == null || this.queryParameters.isEmpty() ? null
				: this.queryParameters.toArray();
		pagingEntity = service.querySQL(queryString, parameters, pageIndex, maxResults);
		list = pagingEntity.list();
		return LIST;
	}

	public void validateDownload() {
		this.validateQuery();
		if (StringHelper.isTrue(all)) {
			// 全部下载
			pageIndex = 1;
			maxResults = Integer.MAX_VALUE;
		}
	}

	/**
	 * 下载EXCEL
	 * 
	 * @return
	 */
	public String download() {
		// String queryString = this.queryString.toString();// .toString().replaceFirst(" T.ID,", " ");
		String str = " T.ID";
		int start = queryString.indexOf(str);
		str = ",";
		int end = queryString.indexOf(str, start) + str.length();
		queryString.delete(start, end); // 不查询ID

		str = " T.FILE_APPROVE_STATUS";
		start = queryString.indexOf(str);
		end = start + str.length();
		StringBuilder caseWhen = new StringBuilder("(CASE ");
		for (Entry<String, String> entry : SystemUtils.FILE_APPROVE_STATUS.entrySet()) {
			caseWhen.append(" WHEN T.FILE_APPROVE_STATUS = '").append(entry.getKey()).append("' ");
			caseWhen.append(" THEN '").append(this.getText(entry.getValue())).append("' ");
		}
		caseWhen.append(" ELSE '' END )");
		queryString.replace(start, end, caseWhen.toString()); // 审核状态显示文字描述

		str = " AS USERNAME";
		start = queryString.indexOf(str) + str.length();
		str = " AS UNAUDITED_COUNT";
		end = queryString.indexOf(str, start) + str.length();

		String queryString = this.queryString.delete(start, end).toString(); // 不显示待审核数量

		if (orderString != null) {
			queryString += (" " + orderString.toString());
		}

		queryString = "SELECT ROWNUM, T.* FROM (" + queryString + ") T";

		Object[] parameters = this.queryParameters == null || this.queryParameters.isEmpty() ? null
				: this.queryParameters.toArray();

		if (StringHelper.isTrue(all)) {
			list = service.querySQL(queryString, parameters);
		} else {
			pagingEntity = service.querySQL(queryString, parameters, pageIndex, maxResults);
			list = pagingEntity.list();
		}

		try {
			String fileName = "查询列表" + POIHelper.VERSION_DEFAULT;
			try {
				// 防止中文乱码
				fileName = new String(fileName.getBytes("GBK"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getResponse().setHeader("content-disposition", "attachment; filename=" + fileName);
			getResponse().setContentType(ContentTypeHelper.get(POIHelper.VERSION_DEFAULT));

			Object[] headers = { "序号", "业务号", "审核状态", "投保人", "出单员", "业务部门", "代理人", "业务员", "档案管理员" };
			// if (this.headers != null) headers = this.headers;
			POIHelper.write(headers, new List[] { list }, getResponse().getOutputStream(), POIHelper.VERSION_DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ids表示的单证号主键数组 对整个单证的承保资料上传文件审核不通过
	 * 
	 * @return
	 */
	public String decline() {
		JSONObject json = new JSONObject();
		if (Helper.isEmpty(ids)) return dispatchException(new Exception("请选择业务号？"));

		list = service.decline(ids, this.getCurrentUserSession());

		if (Helper.isEmpty(list)) json.put("success", false);
		else json.put("success", true);

		return dispatchSuccess(json);
	}

	/**
	 * 单独的资料类型审核不通过
	 * 
	 * @return
	 */
	public String declineDetail() {
		JSONObject json = new JSONObject();
		if (Helper.isEmpty(ids)) return dispatchException(new Exception("请选择资料类型？"));
		list = service.declineDetail(ids, this.getCurrentUserSession());

		if (Helper.isEmpty(list)) json.put("success", false);
		else json.put("success", true);
		return dispatchSuccess(json);
	}

	/**
	 * 单独的资料类型审核通过
	 * 
	 * @return
	 */
	public String approveDetail() {
		JSONObject json = new JSONObject();
		if (Helper.isEmpty(ids)) return dispatchException(new Exception("请选择资料类型？"));
		list = service.approveDetail(ids, getCurrentUserSession());
		if (Helper.isEmpty(list)) json.put("success", false);
		else json.put("success", true);
		return dispatchSuccess(json);
	}

	/**
	 * 对整个document对象的资料类型进行审核通过
	 * 
	 * @return
	 */
	public String approve() {
		JSONObject json = new JSONObject();
		if (Helper.isEmpty(ids)) return dispatchException(new Exception("请选择业务号？"));
		list = service.approve(ids, getCurrentUserSession());
		if (Helper.isEmpty(list)) json.put("success", false);
		else json.put("success", true);

		return dispatchSuccess(json);
	}

	private String no; // 业务号

	private String proposalNo;// 投保单号
	private String policyNo;// 保单号
	private String endorNo;// 批单号
	private String departmentId;// 业务部门
	private String agentName;// 代理人
	private String businessNo;// 业务关系代码
	private String policyHolder;// 投保人
	// private String[] documentStatus;// 归档状态
	// private String[] salesTime;// 签单时间
	// private String[] insertTime;// 归档时间
	// private String loanStatus;// 是否借出
	private String productId;// 产品线
	/** 业务类型:9投保单,8保单,7批单 */
	protected String[] types;
	/** 审核状态 */
	protected String[] fileApprvoeStatus;

	// private String visaNo;// 单证号

	public String[] getFileApprvoeStatus() {
		return fileApprvoeStatus;
	}

	public void setFileApprvoeStatus(String[] fileApprvoeStatus) {
		this.fileApprvoeStatus = fileApprvoeStatus;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getEndorNo() {
		return endorNo;
	}

	public void setEndorNo(String endorNo) {
		this.endorNo = endorNo;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public String getPolicyHolder() {
		return policyHolder;
	}

	public void setPolicyHolder(String policyHolder) {
		this.policyHolder = policyHolder;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

}
