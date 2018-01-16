<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--综合查询的view--%>
<s:form name="FileQuery.view" id="FileQuery.view" method="post">
	<s:hidden name="entity.id"></s:hidden>
	<s:hidden name="id" value="%{entity.id}" />
	<s:hidden name="riskType" value="%{entity.riskType}" />
	<s:hidden name="status" value="%{entity.status}" />
	<p><s:text name="file.document.no" />: ${ entity.no }
		&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		<s:text name="global.policy.holder" />: <span id="viewPolicyApplicant">${ entity.applicant }</span></p>
	<p><s:text name="file.list" />:</p>
	<s:set name="canViewDisabledFile" value="#session.viewDisabledFile == null ? false : #session.viewDisabledFile"></s:set>
	<table class="list nowrap" canViewDisabledFile="<s:property value="#canViewDisabledFile" />">
		<thead>
			<tr>
				<td><s:text name="file.type" /></td>
				<td width="15%"><s:text name="file.no" /></td>
				<td><s:text name="global.user" /></td>
				<td width="10%"><s:text name="file.time" /></td>
				<td width="5%"><s:text name="file.status" /></td>
				<td width="5%"><s:text name="file.loan.status" /></td>
				<td width="5%"><s:text name="global.attachment" /></td>
			</tr>
		</thead>
		<tbody>
		<s:set name="rowNum" value="1"></s:set>
		<s:iterator value="entity.documentFiles" var="documentFile" status="st">
			<s:if test="@com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_DISABLED == status && !#canViewDisabledFile">
				<%-- 不能查看禁用文件 --%>
			</s:if>
			<s:else>
			<tr <s:if test="#rowNum % 2 == 0">class="even"</s:if> id="documentFile${ id }">
				<td>${ fileType.name }</td>
				<td><s:if test="file.no == null">e-Document</s:if><s:else>${ file.no }</s:else></td>
				<td>${ file.user.name }</td>
				<td class="date"><s:property value="formatDate(fileTime)" /></td>
				<td><s:property value="getTextMap('DOCUMENT_FILE_STATUS').get(status)" /></td>
				<td><s:property value="getTextMap('YES_NO').get(file.lent)" /></td>
				<td><s:if test="file.fileId != null">
					<a href="#" onclick="FileIndexService.view('${ file.fileId }')">附件</a></s:if></td>
			</tr>
				<s:set name="rowNum" value="#rowNum + 1"></s:set>
			</s:else>
		</s:iterator>
		</tbody>
	</table>
	<p>&nbsp;</p>
	<%-- <p><s:text name="file.document.image"></s:text>: <a href="#" onclick="FileService.viewImages('${ entity.id }')">查 看</a></p> --%>
<s:if test="list != null && !list.isEmpty()">
	<p><s:text name="file.document.image.list" />:</p>
	<table class="list nowrap">
		<thead>
			<tr>
				<td><s:text name="file.type" /></td>
				<td><s:text name="global.user" /></td>
				<td><s:text name="file.time" /></td>
				<td width="10%"><s:text name="global.attachment" /></td>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="list" var="img" status="st">
			<tr <s:if test="#st.even">class="even"</s:if> id="image${ id }">
				<td>${ fileTitle }</td>
				<td>${ operator }</td>
				<td><s:property value="formatDate(operateTime)" /></td>
				<td><a href="#" onclick="FileIndexService.view('${ id }')">附件</a></td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
</s:if>
</s:form>