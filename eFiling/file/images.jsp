<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<title>${ entity.no } 影像文件</title>
	<link type="text/css" rel="stylesheet" href="../uploader/style/uploader.css">
	<script type="text/javascript" src="../resources/javascript/prototype-1.7.1.js"></script>
	<script type="text/javascript" src="../resources/javascript/prototype-helper.js"></script>
	
	<script type="text/javascript" src="../resources/javascript/base.js"></script>
		
	<script type="text/javascript" src="../resources/javascript/helper.js"></script>
	<script type="text/javascript" src="../resources/javascript/string.js"></script>
	<script type="text/javascript" src="../resources/javascript/number.js"></script>
	<script type="text/javascript" src="../resources/javascript/ajax.js"></script>
	
	<script type="text/javascript" src="../uploader/js/helper.js"></script>
	<script type="text/javascript" src="../uploader/js/FileIndexService.js"></script>
	
	<script type="text/javascript">
		var ids = [];
		<s:iterator value="entity.files">
			<s:if test="@com.sinosoft.efiling.util.SystemUtils@FILE_MODEL_IMAGE == fileModel">
				ids.push('${ fileId }');
			</s:if>
		</s:iterator>
		var show = function() {
			FileIndexService.show({
				'systemCode': '<s:property value="@com.sinosoft.efiling.util.SystemUtils@SYSTEM_CODE" />',
				// 'businessNo': '${ entity.id }',
				'ids': ids,
				// 'maxResults': ids.length,
				'property01': '<s:property value="@com.sinosoft.efiling.util.SystemUtils@FILE_MODEL_IMAGE" />'	// 0表示影像文件
			}, "images");
		};
	</script>
</head>
<body onload="show()">
	<div id="images" class="preview"></div>
	<div id="more" class="previewContainer" style="display: none">
		<a href="javascript: void(0)" onclick="FileIndexService.next()"><img src="icon/more.png" />更多/more...</a>
	</div>
</body>
</html>