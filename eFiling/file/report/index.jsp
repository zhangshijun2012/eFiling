<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- 查询条件显示区域 -->
<table class="form" id="FileManager.query">
	<tbody>
		<tr>
			<td width="8%" class="title"><s:text name="global.proposal.no" />：</td>
			<td width="25%"><input name="proposalNo" id="proposalNo" class="text" /></td>
			<td width="8%" class="title"><s:text name="global.policy.no" />：</td>
			<td width="25%"><input name="policyNo" id="policyNo" class="text" /></td>
			<td width="8%" class="title"><s:text name="global.endor.no" />：</td>
			<td width="25%"><input name="endorNo" id="endorNo" class="text" /></td>
		</tr>
		<tr>
			<td class="title"><s:text name="file.document.business.department" />：</td>
			<td>
		<%-- 		<s:select name="departmentId" id="departmentId" 
					list="#request.departments"
					listKey="id" listValue="id + ' - ' + name"
					headerKey="" headerValue="%{getText('global.all')}" /> --%>
				<input name="departmentId" id="departmentId" class="text" />
			</td>
			<td class="title"><s:text name="file.document.agent" />：</td>
			<td><input name="agentName" id="agentName" class="text" /></td>
			<td class="title"><s:text name="file.document.business.no" />：</td>
			<td><input name="businessNo" id="businessNo" class="text" /></td>
		</tr>
		<tr>
			<td class="title" ><s:text name="global.policy.holder" />：</td>
			<td><input name="applicant" id="applicant" class="text" /></td>
			<td class="title"><s:text name="global.product" />：</td>
			<td>
			<%-- 	<s:select name="productId" id="productId" 
					list="#request.products"
					listKey="id" listValue="name" 
					headerKey="" headerValue="%{getText('global.all')}" /> --%>
				<s:checkboxlist name="productId" id="productId" listKey="id" listValue="name" cssClass="checkbox" list="#request.products" />
					
			</td>
			<td class="title"><s:text name="file.document.business.user" />：</td>
			<td><input name="businessUser" id="businessUser" class="text" /></td>
		</tr>
		<tr>
			<td class="title"><s:text name="file.document.salesman.date" />：</td>
			<td class="internal">
				<table>
					<tr>
						<td width="49%"><input name="salesTime" id="salesTime0" class="text calendar"></td>
						<td width="2%">-</td>
						<td width="49%"><input name="salesTime" id="salesTime1" class="text calendar"></td>
					</tr>
				</table> 
			</td>
			<td class="title">生效日期：</td>
			<td class="internal">
				<table>
					<tr>
						<td width="49%"><input name="effectiveTime" id="salesTime0" class="text calendar"></td>
						<td width="2%">-</td>
						<td width="49%"><input name="effectiveTime" id="salesTime1" class="text calendar"></td>
					</tr>
				</table> 
			</td>
		</tr>
		<tr>
			<td colspan="6" class="actionBar">
				<input name="queryButton" id="queryButton" type="button" 
					value="<s:text name="global.query" />" onclick="Main.service.query()" />
				<input name="downloadButton" id="downloadButton" type="button" 
					value="<s:text name="global.export" />" onclick="Main.service.download()" />
				<input name="downloadAllButton" id="downloadAllButton" type="button" 
					value="<s:text name="global.export.all" />" onclick="Main.service.download('all')" />
			</td>
		</tr>
	</tbody>
</table>