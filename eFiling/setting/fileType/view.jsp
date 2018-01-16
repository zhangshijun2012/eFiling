<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- 新增/编辑信息的页面. --%>
<s:form name="FileType.view" id="FileType.view" method="post">
	<s:hidden name="entity.id"></s:hidden>
	<s:hidden name="entity.status"></s:hidden>
	<s:hidden name="entity.name"></s:hidden>
	<table class="form">
		<tbody>
			<tr>
				<td width="10%" class="title">
					<s:text name="file.type.id"></s:text>：
				</td>
				<td width="40%">${ entity.id }</td>
				<td width="10%" class="title">
					<s:text name="file.type.name"></s:text>：
				</td>
				<td width="40%">${ entity.name }</td>
			</tr>
			<tr>
				<td class="title">
					<s:text name="file.type.alias"></s:text>：
				</td>
				<td>${ entity.alias }</td>
				<td class="title">
					<s:text name="global.status"></s:text>：
				</td>
				<td><s:property value="getText('global.status.' + entity.status)" /></td>
			</tr>
			<tr>
				<td class="title">
					是否原件资料：
				</td>
				<td><s:property value="getTextMap('YES_NO').get(entity.signed)" /></td>
				<td class="title">
					是否可共享的资料：
				</td>
				<td><s:property value="getTextMap('YES_NO').get(entity.shared)" /></td>
			</tr>
			<tr>
				<td class="title">
					<s:text name="global.user"></s:text>：
				</td>
				<td>${ entity.user.name }</td>
				<td class="title">
					<s:text name="global.date"></s:text>：
				</td>
				<td><s:property value="formatDate(entity.insertTime)"/></td>
			</tr>
			<tr>
				<td class="title">
					<s:text name="global.remarks"></s:text>：
				</td>
				<td>${ entity.remarks }</td>
			</tr>
		</tbody>
	</table>
</s:form>