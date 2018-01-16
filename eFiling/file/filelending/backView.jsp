<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--借阅页面 --%>
<s:form name="Filelending.backView" id="Filelending.backView" method="post">
	<s:hidden name="returnedDate" value="%{formatDate(entity.fileLendingReturn.insertTime)}"></s:hidden>
	<s:hidden name="returner" value="%{entity.fileLendingReturn.borrowerName}"></s:hidden>
	<s:hidden name="returnedFiles" value="%{entity.fileLendingReturn.borrowerName}"></s:hidden>
	<s:hidden name="receiveUser" value="%{entity.fileLendingReturn.user.name}"></s:hidden>
<table class="form">
	<tbody>
		<tr>
			<td width="10%" class="required title">
				<s:text name="file.lending.returner" />：
			</td>
			<td width="40%">
				 ${ entity.fileLendingReturn.borrowerName }
			</td>
			<td width="10%" class="required title">
				<s:text name="file.lending.return.department" />：
			</td>
			<td width="40%">
				${ entity.fileLendingReturn.borrowerDeptName }
			</td>
		</tr>
		<tr>
			<td width="10%" class="required title">
				<s:text name="file.lending.return.time" />：
			</td>
			<td width="40%">
				<s:property value="formatDate(entity.fileLendingReturn.insertTime)" />
			</td>
			<td width="10%" class="title">
				<s:text name="操作人" />：
			</td>
			<td width="40%">
				${ entity.fileLendingReturn.user.name }
			</td>
			
		</tr>
		<tr>
			<td width="10%" class=" title">
				<s:text name="操作人部门" />：
			</td>
			<td width="40%">
				${ entity.fileLendingReturn.department.name }
			</td>
			<td width="10%" class="title">
				<s:text name="操作人分公司" />：
			</td>
			<td width="40%">
				${ entity.fileLendingReturn.company.name }
			</td>
			
		</tr>
		
		<tr>
			<td class="title">
				<s:text name="global.remarks" />：
			</td>
			<td colspan="3">
				${ entity.fileLendingReturn.remarks }
			</td>
		</tr>
	</tbody>
</table>
</s:form>