<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, java.util.ArrayList"%>
<!DOCTYPE html>
<html>
<head>
<%
	// 业务数据处理
	String message = "";	// 错误信息
	boolean info = Boolean.parseBoolean((String) request.getAttribute("info"));
	// String propsalNo;	// 投保单号
	// String policyNo;	// 保单号
	// String[] endorNo;	// 批单单号
	List<String> businessNos = null;	// 所有业务号
	if (info) {
		List<String> list = (List<String>) request.getAttribute("policyAndPheadInfo");
		if (list == null || list.isEmpty()) {
			message = "对不起，没有相应的保单和批单信息！"; 
		} else {
			businessNos = new ArrayList<String>();
			for (String str : list) {
				String[] nos = str.split(",");
				for (String no : nos) {
					no = no.trim();
					if (businessNos.contains(no)) continue;
					businessNos.add(no);
				}
			}
		}
	} else {
		message = "对不起，没有相应的用户名和密码！";
	}
%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>影像文件</title>
<style type="text/css">
	html,body {
		margin: 0;
		padding: 0;
		width: 100%;
		height: 100%;
		overflow: hidden;
	}
	
	iframe {
		width: 100%;
		height: 100%;
		border: 0px;
		overflow: auto;
	}
</style>
<script type="text/javascript">
	var businessNos = null;	// 全局变量,要查看的业务号
<% if (businessNos != null && !businessNos.isEmpty()) { %>
	businessNos = [];
	<% for (String businessNo : businessNos) { %>
	businessNos.push('<%=businessNo%>');
	<% } %>
<% } %>

	/**
	 * 显示businessNo对应的影像文件
	 */
	function showFile(businessNos) {
		if (!businessNos || businessNos.length < 0) return;
		// 影像文件查看地址,注意生产上需要进行更改
		var url = 'http://10.132.3.41:9002/uploader/show.do?systemCode=eFiling&password=1';
		var title = '';
		for (var i = 0; i < businessNos.length; i++) {
			url += '&businessNo=' + businessNos[i];	
			title += ',' + businessNos[i];
		}
		title = title.substring(0);
		title = document.title + ' ' + title;
		document.title = title;
		var html = '<iframe src="' + url + '" id="filesFrame" name="filesFrame" scrolling="auto" frameborder="0"></iframe>';
		document.body.innerHTML = html;
	}
</script>
</head>
<body onload="showFile(businessNos)"><%=message%></body>
</html>
