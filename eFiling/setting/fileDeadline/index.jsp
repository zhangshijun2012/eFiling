<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- 查询条件显示区域 -->
<table class="form" id="">
	<tbody>
		<tr>
			<!-- 分公司 -->
			<td width="5%" class="title">
				<s:text name="global.company" />：
			</td>
			<td width="20%">
				<s:select name="companyId"
					list="#request.companies"
					value="#session.user.currentCompany.id"
					listKey="id" listValue="id + ' - ' + name"
					onchange="Main.service.changeCompany(this.value)"
					headerKey="" headerValue="%{getText('global.all')}" />		 
			</td>
			<!-- 适用部门 -->
			<td width="5%" class="title">
				<s:text name="适用部门" />：
			</td>
			<td width="20%">
				<div id="departmentsData" class="hidden"><s:property value="#request.currentInternalDepartments" escape="false" /></div>
				<select name="deptId" id="deptId">
					<option value=""><s:text name="global.all"/></option>
				</select>
			</td>
			<!-- 产品线 -->
			<td width="5%" class="title">
				<s:text name="global.product" />：
			</td>
			<td width="20%"> 
				<s:select name="productId" id="productId" 
					list="#request.products"
					listKey="id" listValue="name"
					headerKey="" headerValue="%{getText('global.all')}" />
			</td>
		</tr>
		
		<tr>
			<!-- 归档时间 -->
			<td width="5%" class="title">
				<s:text name="file.time" />：
			</td>
			<td>
				<input name="days" id="days" class="text calendar" />
			</td>
			<td class="title">
				<s:text name="file.deadline.ctrl.type" />：
			</td>
			<td>
				<s:checkboxlist name="ctrlTypes" id="ctrlTypes" cssClass="checkbox"
					list="#request.ctrlTypes"></s:checkboxlist>
			</td>
			<td class="title">
				<s:text name="global.status" />：
			</td>
			<td>
				<s:checkboxlist name="status" id="status" cssClass="checkbox"
					value="%{@com.sinosoft.efiling.util.SystemUtils@STATUS_VALID}"
					list="#request.statuses"></s:checkboxlist>
			</td>
		</tr>
		
		<tr>
			<td colspan="6" class="actionBar">
				<input name="queryButton" id="queryButton" type="button" 
					value="<s:text name="global.query" />" onclick="Main.service.query()" />
			    <input name="addButton" id="addButton" type="button"
					value="<s:text name="global.append" />" onclick="Main.service.append()" />	
				
			</td>
		</tr>
	</tbody>
</table>