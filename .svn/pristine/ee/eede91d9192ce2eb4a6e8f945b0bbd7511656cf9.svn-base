<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- 查询条件显示区域 -->
<table class="form" id="fileBoxVersion.query">
	<tbody>
		<tr>
			<td width="5%" class="title">
				<s:text name="global.company" />：
			</td>
			<td width="28%">
				<s:select name="companyId"
					list="#request.companies"
					value="#session.currentCompany.id"
					listKey="id" listValue="id + ' - ' + name"
					headerKey="" headerValue="%{getText('global.all')}" />
			</td>
			<td width="5%" class="title">
				<s:text name="global.date" />：
			</td>
			<td width="28%">
				<table>
					<tfoot>
						<tr>
							<td width="48%"><input name="insertTime" id="insertTimeStart" class="calendar" /></td>
							<td width="4%" class="txtCenter">-</td>
							<td width="48%"><input name="insertTime" id="insertTimeEnd" class="calendar" /></td>
						</tr>
					</tfoot>
				</table>
			</td>
			<td width="5%" class="title">
				<s:text name="global.status" />：
			</td>
			<td width="28%">
				<s:checkboxlist name="status" id="status" cssClass="checkbox"
					value="%{@com.sinosoft.efiling.util.SystemUtils@STATUS_VALID}"
					list="#request.status"></s:checkboxlist>
			</td>
		</tr>
		<tr>
			<td colspan="6" class="actionBar">
				<input name="queryButton" id="queryButton" type="button" 
					value="<s:text name="global.query" />" onclick="Main.service.query()" />
				<input name="appendButton" id="appendButton" type="button" 
					value="<s:text name="global.append" />" onclick="Main.service.append()" />
			</td>
		</tr>
	</tbody>
</table>