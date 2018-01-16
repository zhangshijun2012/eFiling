<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- 新增/编辑信息的页面. --%>
<s:form name="FileType.save" id="FileType.save" method="post">
	<table class="form">
		<tbody>
			<tr>
				<td width="10%" class="title required">
					<s:text name="file.type.alias"></s:text>：
				</td>
				<td width="40%" class="requiredValue">
					<s:select name="entity.code" title="%{getText('file.type.alias')}" 
						onchange="Main.service.changeCode(this)"
						list="unsetFileTypes"
						listKey="id.codeCode" listValue="(name == null ? (codeEName == null ? '其他' : codeEName): name)"
						headerKey="" headerValue="" />
				</td>
			</tr>
			<tr>
				<td width="10%" class="title required">
					<s:text name="file.type.id"></s:text>：
				</td>
				<td width="40%" class="requiredValue">
					<input name="entity.id" value="${ entity.id }" objectClass="int" class="text" title="<s:text name="file.type.id"></s:text>" readonly />
				</td>
				<td width="10%" class="title required">
					<s:text name="file.type.name"></s:text>：
				</td>
				<td width="40%" class="requiredValue">
					<input name="entity.name" value="${ entity.name }" maxLength="100" class="text" title="<s:text name="file.type.name"></s:text>" />	
				</td>
			</tr>
			<tr>
				<td width="10%" class="title required">
					是否原件资料：
				</td>
				<td width="40%" class="requiredValue">
					<s:radio name="entity.signed" id="entity.signed" value="%{entity.signed}" cssClass="radio" list="getTextMap('YES_NO')"></s:radio>
				</td>
				<td width="10%" class="title required">
					是否可共享资料：
				</td>
				<td width="40%" class="requiredValue">
					<s:radio name="entity.shared" id="entity.shared" value="%{entity.shared}" cssClass="radio" list="getTextMap('YES_NO')"></s:radio>
				</td>
			</tr>
			<tr>
				<td class="title">
					<s:text name="global.remarks"></s:text>：
				</td>
				<td>
					<s:textarea name="entity.remarks" />
				</td>
			</tr>
		</tbody>
	</table>
</s:form>