<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--显示档案盒装盒明细view--%>
<%--<p><s:text name="file.document.no" />： ${ entity.no }</p> --%>
<p><s:text name="global.policy.holder" />： ${ entity.applicant }</p>
<p><s:text name="file.list" />：<%--  ${ entity.applicant } --%></p>
<table class="list nowrap">
	<thead>
		<tr>
			<th width="2%"><input type="checkbox" onclick="Main.service.shareCheckAll(this.checked)" /></th>
			<th><s:text name="file.type" /></th>
			<th width="13%"><s:text name="file.no" /></th>
			<th><s:text name="global.user" /></th>
			<th width="10%"><s:text name="file.time" /></th>
			<th width="8%"><s:text name="file.status" /></th>
			<th width="8%"><s:text name="file.loan.status" /></th>
			<th width="8%"><s:text name="global.attachment" /></th>
		</tr>
	</thead>
	<tbody>
	<s:set var="rowIndex" value="0"></s:set>
	<s:iterator value="entity.documentFiles" var="documentFile" status="st">
		<!-- 只有归档了的档案资料才能够共享 -->
		<s:if test="@com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_FILE==status">
			<!-- 条形码和投保单不显示到批量共享的档案资料中 -->
			<s:if test="fileType.shared==@com.sinosoft.efiling.util.SystemUtils@YES">
			<tr <s:if test="rowIndex % 2 == 1">class="even"</s:if> id="documentFile${ id }">
				<td align="center">
				<input name="shareIds" onclick="Main.service.shareCheck(this, this.checked)" value="${ id }" type="checkbox" />
				</td>
				<td>${ fileType.name }</td>
				<td>${ file.no }</td>
				<td>${ file.user.name }</td>
				<td class="date"><s:property value="formatDate(fileTime)" /></td>
				<td><s:property value="getTextMap('DOCUMENT_FILE_STATUS').get(status)" /></td>
				<td><s:property value="getTextMap('YES_NO').get(file.lent)" /></td>
				<td><s:if test="file.fileId != null">
					<a href="#" onclick="FileIndexService.view('${ file.fileId }')">附件</a></s:if></td>
			</tr>
			<s:set var="rowIndex" value="%{rowIndex + 1}"></s:set>
			</s:if>
		</s:if> 
	</s:iterator>
	</tbody>
</table>