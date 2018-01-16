<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- 新增/编辑信息的页面. --%>
<s:form name="FileBoxVersion.save" id="FileBoxVersion.save" method="post">
<table class="form">
	<tbody>
		<tr>
			<td width="10%" class="required title">
				<s:text name="global.company" />：
			</td>
			<td width="40%" class="requiredValue">
				<s:select name="entity.company.id" title="%{getText('global.company')}" 
					list="#session.user.currentCompanies"
					value="#session.user.currentCompany.id"
					listKey="id" listValue="name"
					headerKey="" headerValue="" />
			</td>
			<td width="10%" nowrap class="required title">
				<s:text name="file.box.version.capacity" />：
			</td>
			<td class="requiredValue">
				<input name="entity.capacity" required objectClass="number" min=">0" class="text" title="<s:text name="file.box.version.capacity" />" value="${ entity.capacity }" />
			</td>
		</tr>
		<tr>
			<td class="title">
				<s:text name="global.remarks" />：
			</td>
			<td colspan="3">
				<s:textarea name="entity.remarks" />
			</td>
		</tr>
	</tbody>
</table>
</s:form>