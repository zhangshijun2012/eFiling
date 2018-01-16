<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- 查询条件显示区域 -->
<table class="form" id="FileManager.query">
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
				<s:select name="departmentId" id="departmentId" 
					list="#request.departments"
					listKey="id" listValue="name"
					headerKey="" headerValue="%{getText('global.all')}" />
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
				<s:text name="file.lending.lenter" />：
			</td>
			<td >
				 <input name="lender" id="lender" class="text" />
			</td>
			<td class="title">
				<s:text name="file.lending.lent.time" />：
			</td>
			<td class="internal">
				<table>
					<tr>
						<td width="49%"><input name="lendsTime" id="lendsTime" class="text calendar"></td>
						<td width="2%">-</td>
						<td width="49%"><input name="lendsTime" id="lendsTime" class="text calendar"></td>
					</tr>
				</table> 
			</td>
		</tr>
		<tr>
			<td class="title" >
				<s:text name="file.lending.isOverdue" />：
			</td>
			<td>
			 	<s:checkboxlist name="overStatus" id="overStatus" cssClass="checkbox" 
					list="#request.overStatus"></s:checkboxlist>
			</td>
			<td class="title" >
				<s:text name="file.loan.status" />：
			</td>
			<td>
				<!-- value="%{@com.sinosoft.efiling.util.SystemUtils@FILE_LENT_YES}" -->
				<s:checkboxlist name="loanStatus" id="loanStatus" cssClass="checkbox" list="#request.loanStatus"></s:checkboxlist>
			</td>
		</tr>
		<tr>
			<td colspan="6" class="actionBar">
				<input name="queryButton" id="queryButton" type="button" 
					value="<s:text name="global.query" />" onclick="Main.service.query()" />
				<input name="lendButton" id="lendButton" type="button" disabled="disabled"
					value="<s:text name="file.lending.lent" />" onclick="Main.service.operate(0)" />
				<input name="returnButton" id="returnButton" type="button" disabled="disabled" 
					value="<s:text name="file.lending.return" />" onclick="Main.service.operate(1)" />
				<input name="downloadButton" id="downloadButton" type="button" 
					value="<s:text name="global.export" />" onclick="Main.service.download()" />
				<input name="downloadAllButton" id="downloadAllButton" type="button" 
					value="<s:text name="global.export.all" />" onclick="Main.service.download('all')" />
			</td>
			
		</tr>
	</tbody>
</table>