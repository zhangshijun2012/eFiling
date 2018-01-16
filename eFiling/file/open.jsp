<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
	<title>${ no } <s:text name="file.document.image.list" /></title>
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/default.css">
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/layout.css">
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/menu.css">
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/table.css">
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/dialog.css">
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/shortcutBar.css">
	<link type="text/css" rel="stylesheet" href="../resources/template/default/css/calendar.css">
	
	<script type="text/javascript" src="../resources/javascript/prototype-1.7.1.js"></script>
	<script type="text/javascript" src="../resources/javascript/prototype-helper.js"></script>
	<script type="text/javascript" src="../resources/javascript/base.js"></script>
	<script type="text/javascript" src="../resources/javascript/uploader.js"></script>
	<script type="text/javascript">
		var FileService = {
			/**
			 * 直接打开uploader查看影像文件
			 * 
			 * @param id
			 */
			viewFiles: function(id, options) {
				FileIndexService.show({
					'systemCode': SYSTEM_CODE,
					'businessNo': id,
					'property01': '<s:property value="@com.sinosoft.efiling.util.SystemUtils@FILE_MODEL_IMAGE" />',	// 0表示影像文件
					'message': '没有找到业务号${ no }的任何影像资料!'
				}, options);
			},

			/**
			 * 查看影像文件
			 * 
			 * @param id
			 */
			viewImages: function(id, options) {
				this.viewFiles(id, options);
			}
		};

		<s:if test="entity == null">
			// 历史数据,直接查看历史影像文件
			FileService.viewImages(['${ no }', '${ businessNo }'], window);
		</s:if>
	</script>
</head>
<body>
<s:if test="entity == null">
	<div>没有找到业务号${ no }的相关数据,可能此业务号是历史数据,请<a href="#" onclick="FileService.viewFiles(['${ no }', '${ businessNo }'])">点击此处查看历史影像文件</a>.</div>
</s:if>
<s:else>
	<s:include value="view.jsp"></s:include>
</s:else>
	<hr>
	<div class="txtCenter"><input type="button" class="button" onclick="window.close()" value="关 闭"/></div>
</body>
</html>