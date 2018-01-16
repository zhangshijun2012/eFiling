<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--显示档案盒装盒明细view--%>
<s:form name="viewForm" method="post">
	<s:hidden name="id" value="%{entity.id}" />
	<s:hidden name="riskType" value="%{entity.riskType}" />
	<s:hidden name="status" value="%{entity.status}" />
	<s:hidden name="fileStatus" value="%{entity.fileStatus}" />
	<s:hidden name="no" value="%{entity.no}" />
	<s:hidden name="applicant" value="%{entity.applicant}" />
	<s:hidden name="insured" value="%{entity.insured}" />
	<s:hidden name="effectiveTime" value="%{formatDate(entity.effectiveTime)}" />
	<s:hidden name="dueTime" value="%{formatDate(entity.dueTime)}" />
	<%-- <p><s:text name="file.document.no" />: ${ entity.no }</p>
	<p><s:text name="global.policy.holder" />: <span id="viewPolicyApplicant">${ entity.applicant }</span></p> --%>
	<p><s:text name="file.document.no" />: ${ entity.no }
		&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		<s:text name="global.policy.holder" />: <span id="viewPolicyApplicant">${ entity.applicant }</span></p>
	<p><s:text name="file.list" />:</p>
	<table class="list nowrap">
		<thead>
			<tr>
				<td><s:text name="file.type" /></td>
				<td width="15%"><s:text name="file.no" /></td>
				<td><s:text name="global.user" /></td>
				<td width="10%"><s:text name="file.time" /></td>
				<td width="5%"><s:text name="file.status" /></td>
				<td width="5%"><s:text name="file.loan.status" /></td>
				<td width="5%"><s:text name="global.attachment" /></td>
				<%-- <td width="10%"><s:text name="global.operate" /></td> --%>
			</tr>
		</thead>
		<tbody id="viewDialogFilesTbody">
		<s:iterator value="entity.documentFiles" var="documentFile" status="st">
			<tr <s:if test="#st.even">class="even"</s:if> id="documentFile${ id }" 
				handleStatus="<s:property value="@com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_DISABLED != status" />"
				fileId="${ id }" fileTypeId="${ fileType.id }" fileStatus="${ file.fileApproveStatus}" no="${ file.no }" documentStatus="${ status }">
				<td>${ fileType.name }</td>
				<td><s:if test="file.no == null">e-Document</s:if><s:else>${ file.no }</s:else></td>
				<td>${ file.user.name }</td>
				<td class="date"><s:property value="formatDate(fileTime)" /></td>
				<td><s:property value="getTextMap('DOCUMENT_FILE_STATUS').get(status)" /></td>
				<td><s:property value="getTextMap('YES_NO').get(file.lent)" /></td>
				<td>
				<s:if test="file.fileId != null && file.fileApproveStatus != 3">
					<a href="#" onclick="FileIndexService.view('${ file.fileId }')">附件</a>
				</s:if>
				</td>
				<%-- <td class="txtCenter">
				<s:if test="@com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_DISABLED != status"><!-- 已禁用的文件不允许再做操作 -->
					<input type="button" value="扫描" onclick="Main.service.scan('${ id }', '${ fileType.id }')" class="button" />
					<input type="button" value="上传" onclick="Main.service.upload('${ id }', '${ fileType.id }')" class="button" />
				</s:if>
				</td> --%>
			</tr>
		</s:iterator>
		</tbody>
	</table>
</s:form>