<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- 新增/编辑信息的页面. --%>
<s:form name="FileBoxVersion.view" id="FileBoxVersion.view" method="post">
<table class="form">
	<tbody>
		<tr>
			<td width="10%" class="title">
				<s:text name="global.company" />：
			</td>
			<td width="40%">${ entity.company.id } - ${ entity.company.name }</td>
			<td width="10%" nowrap class="title">
				<s:text name="file.box.version.capacity" />：
			</td>
			<td><s:property value="formatInteger(entity.capacity)" /></td>
		</tr>
		<tr>
			<td class="title">
				<s:text name="global.status"></s:text>：
			</td>
			<td><s:property value="getText('global.status.' + entity.status)" /></td>
		</tr>
		<tr>
			<td class="title">
				<s:text name="global.user"></s:text>：
			</td>
			<td>${ entity.user.name }</td>
			<td class="title">
				<s:text name="global.date"></s:text>：
			</td>
			<td><s:property value="formatDate(entity.insertTime)" /></td>
		</tr>
		<tr>
			<td class="title">
				<s:text name="global.remarks" />：
			</td>
			<td colspan="3">${ entity.remarks }</td>
		</tr>
	</tbody>
</table>
</s:form>