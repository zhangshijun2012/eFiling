package com.sinosoft.efiling.struts2.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.ContentTypeHelper;
import com.sinosoft.util.DateHelper;
import com.sinosoft.util.POIHelper;
import com.sinosoft.util.StringHelper;
import com.sinosoft.util.SystemHelper;

/**
 * 差缺报表.查询截止到当前的差缺信息.条件中不包括差缺时间
 * 
 * @author LuoGang
 * 
 */
public class FileLackReportAction extends FileQueryAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3991261430629253732L;

	// @Override
	// public void validateIndex() {
	// // 业务部门
	// getRequest().setAttribute("departments", this.companyService.getInternal());
	// // 产品线
	// getRequest().setAttribute("products", SystemUtils.PRODUCTS);
	// }

	// 险种（产品线） 部门 业务人员 业务关系代码 保/批单号 投保人 签单日期 生效日期 差缺文件清单 超期天数(签单日期至报表日期-设置的超期天数)
	@Override
	public void validateQuery() {
		queryString = new StringBuilder(100);
		queryParameters = new ArrayList<Object>();
		addQueryString(SystemHelper.getProperty("sql.report.lack"), new Object[] { SystemUtils.FILE_DEADLINE_DEFAULT,
				SystemUtils.DOCUMENT_STATUS_UNFILE, SystemUtils.DOCUMENT_STATUS_LACK, SystemUtils.STATUS_VALID });
		addQueryString("WHERE 1 = 1 ");
		addCompanyIdQuery("T.BUSINESS_COMPANY_ID");
		addQuery("T.PRODUCT_ID", productId);
		addQuery("T.BUSINESS_DEPT_ID", departmentId);
		addLikeQuery("T.BUSINESS_NO", businessNo);
		addLikeQuery("T.USER_NAME", businessUser);
		addLikeQuery("T.NO", no);
		addLikeQuery("T.POLICY_NO", policyNo);
		addLikeQuery("T.PROPOSAL_NO", proposalNo);
		addLikeQuery("T.ENDOR_NO", endorNo);
		addLikeQuery("T.APPLICANT", applicant);
		addBetweenQuery("TO_CHAR(T.SALES_TIME, 'YYYY-MM-DD')", salesTime);
		addBetweenQuery("TO_CHAR(T.EFFECTIVE_TIME, 'YYYY-MM-DD')", effectiveTime);
		// addOrderString("T.DAYS DESC, T.SALES_TIME DESC");

		addOrderString("ORDER BY T.DAYS DESC, T.NO DESC, T.SALES_TIME DESC");

		// addOrderString("T.USER_NAME, T.BUSINESS_NO, T.PROPOSAL_NO DESC, T.POLICY_NO, T.ENDOR_NO, T.NO, T.NO");
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
		int start = this.queryString.indexOf("SELECT ") + 7;
		int end = this.queryString.indexOf(",") + 1;
		this.queryString.delete(start, end);
		String queryString = this.queryString.toString();
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
			String fileName = "差缺报表" + DateHelper.nowDate() + POIHelper.VERSION_DEFAULT;
			try {
				// 防止中文乱码
				fileName = new String(fileName.getBytes("GBK"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			getResponse().setHeader("content-disposition", "attachment; filename=" + fileName);
			getResponse().setContentType(ContentTypeHelper.get(POIHelper.VERSION_DEFAULT));
			Object[] headers = { "序号", "保/批单号", "险种（产品线）", "车牌/发动机号", "部门", "业务人员", "业务关系代码", "投保人", "签单日期", "生效日期",
					"差缺文件清单", "超期天数(签单日期至报表日期-设置的超期天数)" };
			// if (this.headers != null) headers = this.headers;
			headers = new Object[] { new Object[] { new Object[] { "报表日期", new Date() }, headers } };
			POIHelper.write(headers, new List[] { list }, getResponse().getOutputStream(), POIHelper.VERSION_DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/** 是否下载全部 */
	// protected String all;

	// protected String productId;
	// protected String departmentId;
	// protected String companyId;
	// protected String businessNo;
	// protected String businessUser;
	// protected String no;
	// protected String policyNo;
	// protected String endorNo;
	// protected String proposalNo;
	// protected String applicant;
	// protected String[] salesTime;
	/** 生效日期 */
	protected String[] effectiveTime;

	public String[] getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String[] effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

}
