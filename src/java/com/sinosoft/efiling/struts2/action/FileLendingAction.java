package com.sinosoft.efiling.struts2.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONObject;

import com.sinosoft.efiling.hibernate.dao.FileLendingDao;
import com.sinosoft.efiling.hibernate.entity.Company;
import com.sinosoft.efiling.hibernate.entity.Document;
import com.sinosoft.efiling.hibernate.entity.DocumentFile;
import com.sinosoft.efiling.hibernate.entity.FileLending;
import com.sinosoft.efiling.hibernate.entity.FileLendingReturn;
import com.sinosoft.efiling.service.CompanyService;
import com.sinosoft.efiling.service.FileLendingService;
import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.ContentTypeHelper;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.POIHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;
import com.sinosoft.util.struts2.action.EntityActionSupport;

public class FileLendingAction extends EntityActionSupport<FileLending, FileLendingDao, FileLendingService, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1403885798213284109L;
	public static final String LENT = "lent";
	public static final String BACK = "back";
	public static final String LENTVIEW = "lentView";
	public static final String BACKVIEW = "backView";
	public static final String VIEWLENT = "viewLent";
	public static final String VIEWBACK = "viewBack";
	// protected
	private String proposalNo;
	private String policyNo;
	private String endorNo;
	private String departmentId;
	private String agentName;
	private String businessNo;
	private String policyHolder;
	private String documentStatus;
	private String lender;
	private String[] lendsTime;
	private String[] loanStatus;
	private String[] overStatus;
	/*
	 * private String status; //表示借阅状态 0：未借出 1：借出
	 * 
	 * public String getStatus() {
	 * return status;
	 * }
	 * 
	 * public void setStatus(String status) {
	 * this.status = status;
	 * }
	 */
	/* 是否下载全部 */
	protected String all;

	public String getAll() {
		return all;
	}

	public void setAll(String all) {
		this.all = all;
	}

	/*
	 * private String [] lendIds;
	 * 
	 * public String[] getLendIds() {
	 * return lendIds;
	 * }
	 * 
	 * public void setLendIds(String[] lendIds) {
	 * this.lendIds = lendIds;
	 * }
	 */

	public String[] getLoanStatus() {
		return loanStatus;
	}

	public void setLoanStatus(String[] loanStatus) {
		this.loanStatus = loanStatus;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public String[] getOverStatus() {
		return overStatus;
	}

	public void setOverStatus(String[] overStatus) {
		this.overStatus = overStatus;
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

	public String getDocumentStatus() {
		return documentStatus;
	}

	public void setDocumentStatus(String documentStatus) {
		this.documentStatus = documentStatus;
	}

	public String getLender() {
		return lender;
	}

	public void setLender(String lender) {
		this.lender = lender;
	}

	public String[] getLendsTime() {
		return lendsTime;
	}

	public void setLendsTime(String[] lendsTime) {
		this.lendsTime = lendsTime;
	}

	@Override
	public void validateIndex() {
		// 业务部门
		getRequest().setAttribute("departments", getCurrentInternalDepartments());
		/*
		 * // 档案状态
		 * Map<String, String> map = new ResourcesMap(SystemUtils.DOCUMENT_STATUS);
		 * getRequest().setAttribute("documentStatus", map);
		 */
		// 产品线
		getRequest().setAttribute("products", SystemUtils.PRODUCTS);
		// 是否 借出
		Map<String, String> loanMap = new ResourcesMap();
		loanMap.put(SystemUtils.FILE_LENT_YES, "global.yes");
		loanMap.put(SystemUtils.FILE_LENT_NO, "global.no");
		getRequest().setAttribute("loanStatus", loanMap);

		// 是否超期
		Map<String, String> onverMap = new ResourcesMap();
		onverMap.put(SystemUtils.FILE_LENT_YES, "global.yes");
		onverMap.put(SystemUtils.FILE_LENT_NO, "global.no");

		getRequest().setAttribute("overStatus", onverMap);
	}

	@Override
	public void validateQuery() {
		queryString = new StringBuilder(100);
		queryParameters = new ArrayList<Object>();
		addQueryString(SystemHelper.getProperty("sql.fileLending.query"));
		addQueryString("WHERE 1 = 1 ");
		addCompanyIdQuery("T.BUSINESS_COMPANY_ID");
		// 增加查询条件
		this.addLikeQuery("T.PROPOSAL_NO", proposalNo.trim());
		this.addLikeQuery("T.POLICY_NO", policyNo.trim());
		this.addLikeQuery("T.ENDOR_NO", endorNo.trim());
		this.addLikeQuery("T.BUSINESS_DEPT_ID", departmentId.trim());
		this.addLikeQuery("T.AGENT_NAME", agentName.trim());
		this.addLikeQuery("T.BUSINESS_NO", businessNo.trim());
		this.addLikeQuery("T.APPLICANT", policyHolder.trim());
		this.addQuery("AND", "T.FILE_STATUS", " <> ", SystemUtils.DOCUMENT_STATUS_UNFILE); // 未归档的不查询
		this.addQuery("T.LENT", loanStatus);
		if (!StringHelper.isEmpty(overStatus) && overStatus.length == 1) {
			// this.addQueryString(" AND TT.STATUS <> ?", SystemUtils.FILE_LENDING_STATUS_RETURNED);
			if (SystemUtils.YES.equals(overStatus[0])) {
				this.addQueryString(" AND FD.EXPECTED_RETURN_DATE < ?", DateHelper.clear(new Date()));
			} else {
				this.addQueryString(" AND FD.EXPECTED_RETURN_DATE >= ?", DateHelper.clear(new Date()));
			}

		}
		// this.addQuery("d.lent", overStatus);
		this.addLikeQuery("FD.BORROWER_NAME", lender.trim());
		// this.addQuery("d.fileStatus", documentStatus);
		this.addBetweenQuery("FD.INSERT_TIME", lendsTime, Date.class);
		// this.addQuery("d.filelending.borrower.name", lender);
		// 根据什么排序
		// addOrderString("ORDER BY T.POLICY_NO T.INSERT_TIME DESC,  ");
		addOrderString("ORDER BY RETURN_DATE2 DESC, SUBSTR(T.POLICY_NO, 11, 9) DESC NULLS LAST, T.ENDOR_NO NULLS LAST, SUBSTR(T.PROPOSAL_NO, 11, 9) DESC, T.INSERT_TIME DESC");
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

	/**
	 * 借阅
	 */
	public void validateLent() {
		this.validateIndex(); // 初始化借阅部门
	}

	public String lent() {

		return LENT;
	}

	public void validateBack() {
		this.validateIndex(); // 初始化归还部门
	}

	public String back() {
		return BACK;
	}

	@Override
	public void validateView() {
		// TODO Auto-generated method stub
		// this.validateIndex();

	}

	public String view() {
		String hql = "FROM " + Document.class.getName() + " WHERE id IN (?" + StringHelper.copy(", ?", ids.length - 1)
				+ ")";
		list = service.query(hql, ids);
		return VIEW;
	}

	public void validateLentView() {

		this.entity = service.get(id);
	}

	public String lentView() {
		return LENTVIEW;
	}

	public void validateBackView() {

		FileLendingReturn fileLendingReturn = service.getFileLendingReturnDao().get(id);
		if (this.entity == null) this.entity = new FileLending();
		this.entity.setFileLendingReturn(fileLendingReturn);
	}

	public String backView() {

		return BACKVIEW;
	}

	// 点击借阅按钮,调用action的方法
	public String viewLent() {
		String hql = "FROM " + Document.class.getName() + " WHERE id IN (?" + StringHelper.copy(", ?", ids.length - 1)
				+ ")";
		list = service.query(hql, ids);
		return VIEWLENT;
	}

	// 点击归还按钮,调用action的方法
	public String viewBack() {
		String hql = "FROM " + Document.class.getName() + " d  WHERE d.id IN (?"
				+ StringHelper.copy(", ?", ids.length - 1) + ")";
		list = service.query(hql, ids);
		return VIEWBACK;
	}

	public void validateSaveLent() {
		// TODO Auto-generated method stub
		entity.setExpectedReturnDate(DateHelper.addDays(entity.getInsertTime(), entity.getDays()));
		entity.setStatus(SystemUtils.FILE_LENDING_STATUS_LENDING);
		entity.setId(StringHelper.randomUUID());
		entity.setUser(getCurrentUser());
		entity.setDepartment(getCurrentUser().getDepartment());
		Company department = getCurrentUser().getDepartment();
		entity.setCompany(CompanyService.getCompany(department));
	}

	public String saveLent() {
		// service.save(entity);
		service.save(entity, ids);
		return dispatchSaveSuccess();
	}

	public void validateSaveBack() {

	}

	public String saveBack() {
		FileLendingReturn fileLendingReturn = entity.getFileLendingReturn(); // 需要通过前端输入归还信息
		fileLendingReturn.setId(StringHelper.randomUUID());
		fileLendingReturn.setUser(getCurrentUser());
		fileLendingReturn.setDepartment(getCurrentUser().getDepartment());
		Company department = getCurrentUser().getDepartment();
		fileLendingReturn.setCompany(CompanyService.getCompany(department));
		service.save(fileLendingReturn, ids);
		return dispatchSaveBackSuccess();
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
		String queryString = this.queryString.toString().replaceFirst("T.ID,", "")
				.replaceFirst(", FD.ID AS LENDING_ID", "");

		String statusDecodeString = "(CASE ";
		for (String key : SystemUtils.DOCUMENT_STATUS.keySet()) {
			statusDecodeString += "WHEN T.FILE_STATUS = '" + key + "' THEN '"
					+ StringHelper.escapeSQLComponent(getText(SystemUtils.DOCUMENT_STATUS.get(key))) + "' ";
		}
		statusDecodeString += "ELSE '' END)";
		queryString = queryString.replaceFirst("T.FILE_STATUS", statusDecodeString);

		String typeString = "(CASE ";
		for (String key : SystemUtils.DOCUMENT_TYPES.keySet()) {
			typeString += "WHEN T.TYPE = '" + key + "' THEN '"
					+ StringHelper.escapeSQLComponent(getText(SystemUtils.DOCUMENT_TYPES.get(key))) + "' ";
		}
		typeString += "ELSE '' END)";
		queryString = queryString.replaceFirst("T.TYPE", typeString);

		queryString = queryString.replaceFirst("T.SOURCE", "(CASE WHEN T.SOURCE = '1' THEN '是' ELSE '否' END)");
		queryString = queryString.replaceFirst("T.LENT", "(CASE WHEN T.LENT = '1' THEN '是' ELSE '否' END)");
		if (orderString != null) {
			queryString += (" " + orderString.toString());
		}
		Object[] parameters = this.queryParameters == null || this.queryParameters.isEmpty() ? null
				: this.queryParameters.toArray();

		if (StringHelper.isTrue(all)) {
			list = service.querySQL(queryString, parameters);
		} else {
			pagingEntity = service.querySQL(queryString, parameters, pageIndex, maxResults);
			list = pagingEntity.list();
		}

		try {
			String fileName = "档案借阅列表" + POIHelper.VERSION_DEFAULT;
			try {
				// 防止中文乱码
				fileName = new String(fileName.getBytes("GBK"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getResponse().setHeader("content-disposition", "attachment; filename=" + fileName);
			getResponse().setContentType(ContentTypeHelper.get(POIHelper.VERSION_DEFAULT));

			// 业务号
			// 类型
			// 借阅人
			// 借阅日期
			// 归还日期
			// 是否超期
			// 超期天数
			// 档案管理员
			// 是否借出
			POIHelper.write(new Object[] { new Object[] { new Object[] { "业务号", "业务号类型", "借阅人", "借阅日期", "预计归还日期",
					"是否归档", "是否超期", "超期天数", "档案管理员", "是否借出" } } }, new List[] { list },
					getResponse().getOutputStream(), POIHelper.VERSION_DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 主要是返回打印签收条组装的一些数据
	@SuppressWarnings("unchecked")
	public String dispatchSaveBackSuccess() {
		JSONObject json = new JSONObject();
		json.put("success", true);
		json.put("id", entity.getFileLendingReturn().getId() == null ? id : entity.getFileLendingReturn().getId());
		json.put("returnTime", DateHelper.format(entity.getFileLendingReturn().getInsertTime()));
		json.put("returnName", entity.getFileLendingReturn().getBorrowerName());
		String returnId = entity.getFileLendingReturn().getId();
		System.out.println(returnId);
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ").append(FileLending.class.getName()).append(" f ");
		hql.append(" WHERE f.fileLendingReturn.id = ?");
		// service.get(returnId)
		List<FileLending> fileLendings = (List<FileLending>) service.query(hql.toString(), new Object[] { returnId });
		String retrunTime = "";
		for (FileLending fileLending : fileLendings) {
			retrunTime += DateHelper.format(fileLending.getInsertTime()) + " ";
		}
		DocumentFile documentFile = null;
		Document document = null;
		String typeName = "";
		Set<Document> set = new HashSet<Document>(); // set去掉相同的Documnet对象
		Map<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		List<JSONObject> list = new ArrayList<JSONObject>();
		Vector<String> files = null;
		for (int i = 0; i < ids.length; i++) {
			documentFile = service.getDocumentFileDao().get(ids[i]);
			document = documentFile.getDocument();
			set.add(document);
			typeName = documentFile.getFileType().getName();
			if (map.containsKey(document.getNo())) {
				files = map.get(document.getNo());
				files.add(typeName);
				map.put(document.getNo(), files);
			} else {
				files = new Vector<String>();
				files.add(typeName);
				map.put(document.getNo(), files);
			}
		}
		Iterator<Document> it = set.iterator();
		while (it.hasNext()) {
			document = (Document) it.next();
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("no", document.getNo());
			jsonObject.put("applicant", document.getApplicant());
			jsonObject.put("effectiveTime", document.getEffectiveTime());
			jsonObject.put("dueTime", document.getDueTime());
			jsonObject.put("insured", document.getInsured());
			jsonObject.put("lentTime", retrunTime);
			jsonObject.put("documentFiles", map.get(document.getNo()));
			list.add(jsonObject);
		}
		json.put("list", list);
		if (getOutputMessage() != null) json.put("message", getOutputMessage());
		return dispatchSuccess(json.toString());
	}
}
