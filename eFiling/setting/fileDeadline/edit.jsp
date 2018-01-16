<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--新增归档期限--%>
<s:form name="FileDeadline.save" id="FileDeadline.save" method="post">
	<table class="form">
		<tbody>
			<tr>
				<td width="10%" class="title required">
					<s:text name="global.company"></s:text>：
				</td>
				<td width="40%" class="requiredValue">
					<s:select name="entity.company.id" title="%{getText('global.company')}" 
						onchange="Main.service.changeCompany(this.value, Main.service.form.elements['entity.fileDept.id'], true)"
						list="#session.user.currentCompanies"
						value="entity.company.id"
						listKey="id" listValue="id + ' - ' + name"
						headerKey="" headerValue="" />
				</td>
				<td width="10%" class="title">
					<s:text name="global.department"></s:text>：
				</td>
				<td width="40%">
					<%-- <s:select name="entity.fileDept.id" title="%{getText('global.department')}" 
						onchange=""
						list="#request.departments"
						listKey="id" listValue="name"
						headerKey="" headerValue="" /> --%>
				<select name="entity.fileDept.id" id="entity.fileDept.id">
					<option value=""><s:text name="global.all" /></option>
				</select>
				</td>
			</tr>
			<tr>
				<td width="10%" class="title">
					<s:text name="global.product"></s:text>：
				</td>
				<td width="40%">
					<s:select name="entity.product.id" id="entity.product.id" title="%{getText('global.product')}" 
					list="#request.products"
					listKey="id" listValue="name"
					headerKey="" headerValue="%{getText('global.all')}" />	
				</td>
				<td class="title required">
					<s:text name="file.deadline.ctrl.type"></s:text>：
				</td>
				<td>
					<%-- <s:checkboxlist name="entity.ctrlType" id="entity.ctrlType" cssClass="checkbox" 
					list="#request.documentStatus"></s:checkboxlist> --%>
					<s:radio name="entity.ctrlType" id="entity.ctrlType" cssClass="radio" list="#request.ctrlTypes"></s:radio>
				</td>
			</tr>
			<tr>
				<td class="title required">
					<s:text name="file.deadline.days"></s:text>：
				</td>
				<td class="requiredValue">
					<input name="entity.days" value="${entity.days} " class="text" title="<s:text name="file.deadline.days"></s:text>" />
				</td>
			</tr>		
			<tr>
				<td class="title ">
					<s:text name="global.remarks"></s:text>：
				</td>
				<td>
					<s:textarea name="entity.remarks" />
				</td>
			</tr>
		</tbody>
	</table>
</s:form>