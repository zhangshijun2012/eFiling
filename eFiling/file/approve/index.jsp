<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- 查询条件显示区域 -->
<table class="form" id="FileCoreQuery.query">
	<tbody>
		<tr>
			<td width="8%" class="title">
				<s:text name="global.proposal.no" />：
			</td>
			<td width="25%">
				<input name="proposalNo" id="proposalNo" class="text" />
			</td>
			<td width="8%" class="title">
				<s:text name="global.policy.no" />：
			</td>
			<td width="25%">
				<input name="policyNo" id="policyNo" class="text" />
			</td>
			<td width="8%" class="title">
				<s:text name="global.endor.no" />：
			</td>
			<td width="25%">
				<input name="endorNo" id="endorNo" class="text" />
			</td>
		</tr>
	
		<tr>
			<td class="title">
				<s:text name="file.document.business.department" />：
			</td>
			<td>
			<%-- 	<s:select name="departmentId" id="departmentId" 
					list="#request.departments"
					listKey="id" listValue="id + ' - ' + name"
					headerKey="" headerValue="%{getText('global.all')}" /> --%>
				<input name="departmentId" id="departmentId" class="text" />
			</td>
			<td class="title">
				<s:text name="file.document.agent" />：
			</td>
			<td>
				<input name="agentName" id="agentName" class="text" />
			</td>
			<td class="title">
				<s:text name="file.document.business.no" />：
			</td>
			<td>
				<input name="businessNo" id="businessNo" class="text" />
			</td>
		</tr>
		
		<tr>
			<td class="title">
				<s:text name="global.policy.holder" />：
			</td>
			<td>
				<input name="policyHolder" id="policyHolder" class="text" />
			</td>
			<td class="title">
				<s:text name="global.product" />：
			</td>
			<td>
				<s:select name="productId" id="productId" 
					list="#request.products"
					listKey="id" listValue="name" 
					headerKey="" headerValue="%{getText('global.all')}" />
			</td>
			<td class="title">
				<s:text name="file.document.type" />：
			</td>
			<td>
				<s:checkboxlist name="types" id="types" value="%{types}" cssClass="checkbox" tabindex="0"
				list="#request.documentTypes"></s:checkboxlist>
			</td>
			 
		</tr>
		 
		<tr>
			<td colspan="6" class="actionBar">
				<input name="queryButton" id="queryButton" type="button" 
					value="<s:text name="global.query" />" onclick="Main.service.query()" />
				<input name="passedButton" id="passedButton" type="button" 
					value="<s:text name="file.approve.status.1" />" class="button6" disabled="disabled" onclick="Main.service.approve()" />
				<input name="nopassedButton" id="nopassedButton" type="button" 
					value="<s:text name="file.approve.status.2" />" class="button6" disabled="disabled" onclick="Main.service.decline()" />
				<input name="downloadButton" id="downloadButton" type="button" 
					value="<s:text name="global.export" />" onclick="Main.service.download()" />
				<input name="downloadAllButton" id="downloadAllButton" type="button" 
					value="<s:text name="global.export.all" />" onclick="Main.service.download('all')" />
			</td>
		</tr>
	</tbody>
</table>