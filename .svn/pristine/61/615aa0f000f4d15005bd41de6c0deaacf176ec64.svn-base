<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--新增归档期限 --%>
<s:form name="FileDeadline.view" id="FileDeadline.view" method="post">
	<s:hidden name="entity.id"></s:hidden>
	<s:hidden name="entity.status"></s:hidden>
	<table class="form">
		<tbody>
			<tr>
				<td width="10%" class="title">
					<s:text name="global.company"></s:text>：
				</td>
				<td width="40%">${ entity.company.name }</td>
				<td width="10%" class="title">
					<s:text name="global.department"></s:text>：
				</td>
				<td width="40%">
					<s:if test="%{entity.fileDept==null}"><s:text name="global.all"/></s:if>
					<s:else>${ entity.fileDept.name }</s:else>
				</td>
			</tr>
			<tr>
				<td class="title">
					<s:text name="global.product"></s:text>：
				</td>
				<td>
					<%-- <s:text name="entity.product"/> --%>
					<s:if test="%{entity.product==null}">
						<s:text name="global.all"/>
					</s:if>
					<s:else>
						 ${entity.product.name }
					</s:else>
				</td>
				<td class="title">
					<s:text name="file.deadline.ctrl.type"></s:text>：
				</td>
				<td><s:property value="getText('file.deadline.ctrl.type.' + entity.ctrlType)" /></td>
			</tr>
		 
			<tr>
				<td class="title">
					<s:text name="file.deadline.days"></s:text>：
				</td>
				<td>${ entity.days }</td>
				<td width="10%" class="title">
					<s:text name="global.status"></s:text>：
				</td>
				<td width="40%"><s:property value="getText('global.status.' + entity.status)" /></td>
			</tr>
		 
			<tr>
				<td class="title">
					<s:text name="global.user"></s:text>：
				</td>
				<td>${ entity.user.name }</td>
				<td class="title">
					<s:text name="global.date"></s:text>：
				</td>
				<td>
					<s:property value="formatDate(entity.insertTime)" />
				</td>
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