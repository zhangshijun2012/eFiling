<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!-- 查询条件显示区域 -->
<div id="policyInputContainer" style="width: 661px">
<s:hidden name="xml" value=""></s:hidden>
<s:hidden name="file.fileModel" value="%{@com.sinosoft.efiling.util.SystemUtils@FILE_MODEL_IMAGE}"></s:hidden>
<s:hidden name="file.document.id" value=""></s:hidden>
<s:hidden name="file.document.no" value=""></s:hidden>
<s:hidden name="file.type.id" value=""></s:hidden>
<table class="form" id="FileCore.save">
	<tr class="nowrap">
		<td width="5%" class="title" style="padding-left: 3px"><s:text name="file.document.no" /></td>
		<td width="45%"><input name="no" class="text" onblur="this.value = this.value.trim();Main.service.queryOnblur();" /></td>
		<td width="5%" class="title"><s:text name="file.type" /></td>
		<td width="45%"><select name="fileTypeId" id="fileTypeId"><option value="">...</option></select></td>
	</tr>
	<tr>
		<td colspan="4" class="txtCenter">
			<input type="button" class="button" value="<s:text name="global.confirm" />" onclick="Main.service.doQuery()" name="doQueryButton"  />
			<input type="button" class="button" value="继续上传" onclick="Main.service.reQuery()" name="requeryButton" disabled />
		</td>
	</tr>
</table>
</div>
<div style="width: 680px; padding: 2px 5px 0px 3px" id="uploaderContainer"></div>