<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--综合查询的view--%>
<s:form name="FileCore.view" id="FileCore.view" method="post">
	<p><s:text name="file.document.no" />: ${ entity.no }
		&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
		<s:text name="global.policy.holder" />: <span id="viewPolicyApplicant">${ entity.applicant }</span></p>
	<p><s:text name="file.list" />:</p>
	<table class="list nowrap">
		<thead>
			<tr>
				<td style="width:20px"><input type="checkbox" onclick="Main.service.checkAllDetails(this.checked)" name="checkAllDetails" /></td>
				<td width="40%"><s:text name="file.type" /></td>
				<!-- <td width="15%"><s:text name="file.no" /></td> -->
				<td width="20%"><s:text name="global.user" /></td>
				<td width="20%"><s:text name="file.time.upload" /></td>
				<td width="15%"><s:text name="file.approve.status" /></td>
				<!-- <td width="5%"><s:text name="file.loan.status" /></td> -->
				<td width="5%"><s:text name="global.attachment" /></td>
			</tr>
		</thead>
		<tbody>
		<s:iterator value="entity.documentFiles" var="documentFile" status="st">
			<%--显示承保资料上传菜单未审核的承保资料类型并且文件没有被禁用 --%>
			 <s:if test="@com.sinosoft.efiling.util.SystemUtils@FILE_APPROVE_STATUS_AUDITED == file.fileApproveStatus
			 		|| @com.sinosoft.efiling.util.SystemUtils@FILE_APPROVE_STATUS_NOPASSED == file.fileApproveStatus
			 		|| (@com.sinosoft.efiling.util.SystemUtils@FILE_APPROVE_STATUS_UNAUDITED == file.fileApproveStatus 
			 			&& @com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_FILE == status)"> 
				<tr <s:if test="#st.even">class="even"</s:if> id="documentFile${ id }">
					<td>
					<s:if test="@com.sinosoft.efiling.util.SystemUtils@FILE_APPROVE_STATUS_UNAUDITED == file.fileApproveStatus">
						<%-- 待审核才显示勾选框 --%>
						<input type="checkbox" name="ids" approveStatus="${ file.fileApproveStatus }" value="${ id }" onclick="Main.service.checkDetail(this, this.checked)" />
					</s:if>
					</td>
					<td>${ fileType.name }</td>
					<td>${ file.user.name }</td>
					<td class="date"><s:property value="formatDate(fileTime)" /></td>
					<td><s:property value="getText('file.approve.status.' + file.fileApproveStatus)" /></td>
					<td><s:if test="file.fileId != null"><a href="#" onclick="FileIndexService.view('${ file.fileId }')">附件</a></s:if></td>
				</tr>
			</s:if>
		</s:iterator>
		</tbody>
	</table>
</s:form>