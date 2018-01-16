<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--借阅页面 --%>
<s:form name="Filelending.lendView" id="Filelending.lendView" method="post">
<table class="form">
	<tbody>
		<tr>
			<td width="10%" class="required title">
				<s:text name="file.lending.lenter" />：
			</td>
			<td width="40%">
				 ${ entity.borrowerName }
			</td>
			<td width="10%" class="required title">
				<s:text name="file.lending.lent.department" />：
			</td>
			<td width="40%">
				${ entity.borrowerDeptName }
			</td>
		</tr>
		
		<tr>
			<td width="10%" class="required title">
				<s:text name="file.lending.lent.company" />：
			</td>
			<td width="40%">
				${ entity.borrowerCompanyName }
			</td>
			<td width="10%" class="required title">
				<s:text name="file.lending.lent.time" />：
			</td>
			<td width="40%">
				<s:property value="formatDate(entity.insertTime)" />
			</td>
		</tr>
		
		<tr>
			<td width="10%" class="required title">
				<s:text name="file.lending.lent.days" />：
			</td>
			<td width="40%">
				${ entity.days }
			</td>
			<td width="10%" class="title required">
				<s:text name="file.lending.loaner" />：
			</td>
			<td width="40%">
				${ entity.user.name }
			</td>
		</tr>
		
		<tr>
			<td width="10%" class="title required">
				<s:text name="file.lending.loan.department" />：
			</td>
			<td width="40%">
				${ entity.department.name }
			</td>
			<td width="10%" class="title required">
				<s:text name="file.lending.loan.company" />：
			</td>
			<td width="40%">
				${ entity.company.name }
			</td>
		</tr>
		<tr>
			<td class="title required">
				<s:text name="file.lending.lent.reason" />：
			</td>
			<td colspan="3">
				${ entity.reason }
			</td>
		</tr>
	</tbody>
</table>
</s:form>