<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%--综合查询的view--%>
<s:form name="FileBackQuery.view" id="FileBackQuery.view" method="post">
<s:iterator value="list" var="entity">
	<table class="form">
		<tbody>
			 
			<tr>
				<td width="3%" class="title">
					<s:text name="file.document.no"></s:text>：
				</td>
				<td width="20%">${ no }</td>
				
				<td width="3%" class="title">
					<s:text name="global.policy.holder"></s:text>：
				</td>
				<td width="20%">${ applicant }</td>
			
			</tr>
			<tr>
				<td class="title">
					<s:text name="file.list"></s:text>：
				</td>
			</tr>
			<!-- 档案清单 -->
			<tr>
				<td colspan="4">
					<table class="list">
						<thead>
							<tr>
						 		<td style="width:20px"></td>
								<td><s:text name="file.type"></s:text></td>
								<td><s:text name="file.no"></s:text></td>
								<td><s:text name="global.user"></s:text></td>
								<td><s:text name="file.time"></s:text></td>
								<td><s:text name="file.status"></s:text></td>
								<td><s:text name="file.loan.status"></s:text></td>
								<td><s:text name="file.lending.lenter"></s:text></td>
								<td><s:text name="file.lending.lent.department"></s:text></td>
								<td><s:text name="global.attachment"></s:text></td>
							</tr>
						</thead>
						<tbody>
							<s:iterator value="documentFiles" var="documentFile">
								<%--没有档案编码的档案资料不显示出来,没有借阅出去的档案资料不需要归还, 禁用的文件不允许归还--%>
								<s:if test="file.no!=null && file.lent==1 && @com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_DISABLED!=status">
								<tr class="even">
									<s:if test="file != null">
									<td style="width:20px">
										<input type="checkbox" checked name="ids" value="${ documentFile.id }"/>
									</td>
									</s:if>
									<td><s:property value="fileType.name"/></td>
									<td><s:property value="file.no"/></td>
									<td>${ user.name }</td>
									<td class="date"><s:property value="formatDate(fileTime)"/></td>
									<td><s:property value="getText('file.status.' + status)"/></td>
									<td><s:property value="getText('file.loan.status.' + file.lent)"/></td>
									<td id='borrowerId'>
										<s:if test="file.fileLending!=null">
											${ documentFile.file.fileLending.borrowerName } 
										</s:if> 
									</td>
									<td id='borrowerDeptId'>
										<s:if test="file.fileLending!=null">
											${ documentFile.file.fileLending.borrowerDeptName } 
										</s:if>
									</td>
									<td>
										<s:if test="file != null">
											<a href="#" onclick="FileIndexService.view('${ file.fileId }')">附件</a>
										</s:if>
									</td>
								</tr>
								</s:if>
							</s:iterator>
						</tbody>
					</table> 
				</td>
			</tr>
		</tbody>
	</table>
</s:iterator>
</s:form>