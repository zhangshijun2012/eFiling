<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <s:iterator id="entity" value="list"> --%>
<s:form name="viewlogForm" id="viewlogForm" method="post">
	<table class="list nowrap">
		<thead>
			<tr>
				<td><s:text name="业务号" /></td>
				<td width="15%"><s:text name="资料类型代码" /></td>
				<td><s:text name="资料类型名称" /></td>
				<td width="10%"><s:text name="归档操作" /></td>
				<td width="5%"><s:text name="归档人员" /></td>
				<td width="5%"><s:text name="归档时间" /></td>
			</tr>
		</thead>
		<tbody id="viewDialogFilesTbody">
		<s:iterator value="list" var="paperLogDocument" status="st">
			<tr>
 				<td>${ no }</td>
				<td>${ fileTypeCode }</td>
				<td>${ fileType.name }</td>
				<td>
				<s:if test="status==1">
					纸质归档	
				</s:if>
				<s:else>
					纸质归档撤销
				</s:else>
				</td>
				<td>${ user.name }</td>
				<td class="date"><s:property value="formatDate(dueTime)" /></td>
			</tr>
		</s:iterator>
		</tbody>
	</table>
</s:form>
<%-- </s:iterator>
 --%>