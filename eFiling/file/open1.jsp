<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width = device-width, initial-scale = 1.0, minimum-scale = 1.0, maximum-scale = 1.0, user-scalable = no"/>
	<title>${ no } <s:text name="file.document.image.list" /></title>
	<link rel="stylesheet" href="../bootstrap/css/bootstrap.min.css"></link>	
	<link rel="stylesheet" href="../bootstrap/css/Undwrt-bootstrap.css" type="text/css"></link>
	<!--[if lt IE 9]>
	<script src="../bootstrap/js/html5shiv.js"></script>
	<script src="../bootstrap/js/respond.js"></script>
	<![endif]-->
    <link href="../bootstrap/css/footable.core.css" rel="stylesheet" type="text/css"/>
    <link href="../bootstrap/css/arbitrary_toggle_markup.css" rel="stylesheet" type="text/css"/>

	<script type="text/javascript" src="../resources/javascript/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="../resources/javascript/base.js"></script>
	<script type="text/javascript" src="../bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="../bootstrap/js/footable.js"></script>
    <script type="text/javascript">
    <s:if test="entity == null">
    	(function viewHistoryFiles() {
    		var url = '/uploader/show.do?systemCode=' + SYSTEM_CODE + '&' 
    					+ 'businessNo=${ no }&businessNo=${ businessNo }&'
    					+ 'property01=<s:property value="@com.sinosoft.efiling.util.SystemUtils@FILE_MODEL_IMAGE" />&'
    					+ 'message=' + encodeURIComponent('没有找到业务号${ no }的任何影像资料!');
    		window.location.href	= url;
    	})();
    </s:if>
    <s:else>
    	/**
    	 * 查看附件
    	 * @param id 附件id
    	 */
    	function view(id) {
    		var url = '/uploader/view.do?id=' + id + '&'
    				+ 'message=' + encodeURIComponent('您扫描的影像文件系统正在处理，请于明日查看!');
			window.open(url);
    	}
		$(document).ready(function () {
	        $('.footable').footable({
	        	toggleHTMLElement: '<span><img src="../bootstrap/images/plus.png" class="footable-toggle footable-expand" border="0" alt="Expand"><img src="../bootstrap/images/minus.png" class="footable-toggle footable-contract" border="0" alt="Contract"></span>'
	        });
	    });
    </s:else>
    </script>
</head>
<body>
<s:if test="entity == null">
</s:if>
<s:else>
	<div class="container">
		<div class="sinosoft-vspace"></div>
		<div class="row">
			<div class="col-sm-12 col-md-12 div-grid">
				<div class="row">
					<div class="col-xs-12 col-sm-5 col-md-2 sinosoft-lable"><s:text name="file.document.no" />：</div>
					<div class="col-xs-12 col-sm-7 col-md-4 sinosoft-lable-control">${ entity.no }</div>
					<div class="col-xs-12 col-sm-5 col-md-2 sinosoft-lable"><s:text name="global.policy.holder" />：</div>
					<div class="col-xs-12 col-sm-7 col-md-4 sinosoft-lable-control">${ entity.applicant }</div>
				</div>
			</div>
		</div>
		<div class="sinosoft-vspace"></div>
		<div class="row"><h3><s:text name="file.list" /></h3></div>
		<div class="row">
			<div class="col">
				<table class="footable table table-striped table-bordered">
					<thead>
						<tr class="sinosoft-table-head">
							<th nowrap data-toggle="true"><s:text name="file.type" /></th>
							<th nowrap data-hide="phone"><s:text name="file.no" /></th>
							<th nowrap data-hide="phone,tablet"><s:text name="global.user" /></th>
							<th nowrap data-hide="phone,tablet"><s:text name="file.time" /></th>
							<th nowrap data-hide="phone,tablet"><s:text name="file.status" /></th>
							<th nowrap data-hide="phone,tablet"><s:text name="file.loan.status" /></th>
							<th nowrap><s:text name="global.attachment" /></th>
						</tr>
					</thead>
					<tbody>
						<s:set name="canViewDisabledFile" value="#session.viewDisabledFile == null ? false : #session.viewDisabledFile"></s:set>
						<s:set name="rowNum" value="1"></s:set>
					<s:iterator value="entity.documentFiles" var="documentFile" status="st">
						<s:if test="@com.sinosoft.efiling.util.SystemUtils@DOCUMENT_FILE_STATUS_DISABLED == status && !#canViewDisabledFile">
							<%-- 不能查看禁用文件 --%>
						</s:if>
						<s:else>
							<tr class="<s:if test="#rowNum % 2 == 0">even</s:if><s:else>odd</s:else>" id="documentFile${ id }">
								<td>${ fileType.name }</td>
								<td><s:if test="file.no == null">e-Document</s:if><s:else>${ file.no }</s:else></td>
								<td>${ file.user.name }</td>
								<td class="date"><s:property value="formatDate(fileTime)" /></td>
								<td><s:property value="getTextMap('DOCUMENT_FILE_STATUS').get(status)" /></td>
								<td><s:property value="getTextMap('YES_NO').get(lent)" /></td>
								<td><s:if test="file.fileId != null"><a href="#" onclick="view('${ file.fileId }')"><s:text name="global.attachment" /></a></s:if></td>
							</tr>
							<s:set name="rowNum" value="#rowNum + 1"></s:set>
						</s:else>
					</s:iterator>
					</tbody>
				</table>
			</div>
		</div>
	<s:if test="list != null && !list.isEmpty()">
		<div class="row"><h3><s:text name="file.document.image.list" /></h3></div>
		<div class="row">
			<div class="col">
				<table class="footable table table-striped table-bordered">
					<thead>
						<tr class="sinosoft-table-head">
							<th nowrap><s:text name="file.type" /></th>
							<th nowrap data-hide="phone,tablet"><s:text name="global.user" /></th>
							<th nowrap data-hide="phone"><s:text name="file.time" /></th>
							<th nowrap><s:text name="global.attachment" /></th>
						</tr>
					</thead>
					<tbody>
					<s:iterator value="list" var="img" status="st">
						<tr class="<s:if test="#st.even">even</s:if><s:else>odd</s:else>" id="image${ id }">
							<td>${ fileTitle }</td>
							<td>${ operator }</td>
							<td class="date"><s:property value="formatDate(operateTime)" /></td>
							<td><a href="#" onclick="view('${ id }')"><s:text name="global.attachment" /></a></td>
						</tr>
					</s:iterator>
					</tbody>
				</table>
			</div>
		</div>
	</s:if>
	

		<div class="sinosoft-vspace">&nbsp;</div>
		<div class="row sinosoft-center">
			<input type="button" class="btn btn-primary" onclick="window.close()" value="    关     闭     " />
		</div>
		
	</div>
</s:else>
</body>
</html>