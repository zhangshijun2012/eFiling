<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE HTML>
<html>
<head>
<title>eFiling Uploader</title>
<script type="text/javascript" src="../resources/javascript/uploader.js"></script>
<%--此页面的作用是建/eFiling/uploader的访问跳转至/uploader--%>
<script type="text/javascript">
	var actionName = '<s:property value="#request.actionName" escape="false" />';
	var parameters = <s:property value="#request.queryParameters" escape="false" />;
	function init() {
		var form = document.forms['uploader'];
		form.action = Uploader.namespace + actionName;
		var key;
		for (key in parameters) {
			var input = document.createElement("input");
			input.type = "hidden";
			input.name = key;
			input.value = parameters[key];
			form.appendChild(input);
		}
	}
</script>
</head>
<body onload="init()">
	<form action="/uploader" method="post" name="uploader"></form>
</body>
</html>