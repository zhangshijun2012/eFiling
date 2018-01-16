<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title><s:text name="global.title" /></title>
	<script type="text/javascript" src="resources/javascript/prototype-1.7.1.js"></script>
	<script type="text/javascript" src="resources/javascript/prototype-helper.js"></script>
	<script type="text/javascript" src="resources/javascript/Math.uuid.js"></script>
	
	<script type="text/javascript" src="resources/javascript/base.js"></script>
	
	<script type="text/javascript" src="resources/javascript/interceptable.js"></script>
	
	<script type="text/javascript" src="resources/javascript/helper.js"></script>
	<script type="text/javascript" src="resources/javascript/string.js"></script>
	<script type="text/javascript" src="resources/javascript/number.js"></script>
	<script type="text/javascript" src="resources/javascript/ajax.js"></script>
	
	<script type="text/javascript" src="resources/javascript/file.js"></script>
	<script type="text/javascript" src="resources/javascript/grid.js"></script>
	<script type="text/javascript" src="resources/javascript/checkbox.js"></script>
	<%-- <script type="text/javascript" src="resources/javascript/message.js"></script> --%>
	<script type="text/javascript" src="resources/javascript/xtree.js"></script>
			
	<script type="text/javascript" src="resources/javascript/date.js"></script>
	<script type="text/javascript" src="resources/javascript/calendar.js"></script>

	<script type="text/javascript" src="resources/javascript/lang.js"></script>
	<script type="text/javascript" src="resources/javascript/dialog.js"></script>
	
	<script type="text/javascript" src="resources/javascript/layout.js"></script>
	
	<script type="text/javascript" src="resources/javascript/main.js"></script>
	<script type="text/javascript" src="resources/javascript/paging.js"></script>
	
	<script type="text/javascript" src="resources/javascript/template.js"></script>
	<script type="text/javascript" src="resources/javascript/loadingDialog.js"></script>
	<script type="text/javascript" src="resources/javascript/module.js"></script>
	<script type="text/javascript" src="resources/javascript/menu.js"></script>
	
	<script type="text/javascript" src="resources/javascript/uploader.js"></script>

	<script type="text/javascript" src="resources/javascript/service/BarcodePrinter.js"></script>
	<script type="text/javascript" src="resources/javascript/service/Service.js"></script>
	<script type="text/javascript" src="resources/javascript/service/FileType.js"></script>
	<script type="text/javascript" src="resources/javascript/service/DataCapServers.js"></script>
	<script type="text/javascript" src="resources/javascript/service/FileScanHandler.js"></script>
		
	<script type="text/javascript">
		LOCALE = Base.LOCALE = '<s:property value="getLocale()" />';
		Main.FIRST_LOGIN = !Boolean.parseBoolean('${ sessionScope.logined }') || 'first' == '${ param.firstLogin }';
		Main.FIRST_LOGIN = StringHelper.isEmpty('${ param.currentDepartmentId }');	// 是否第一次登陆，第一次则需要选择登陆机构

		Main.user = <s:property value="#session.user.toJSONString()" escape="false" />;
		
		SYSTEM_CODE = Main.systemCode = '<s:property value="@com.sinosoft.efiling.util.SystemUtils@SYSTEM_CODE" escape="false" />';

		Event.observe(window, "resize", function() { Layout.resize(); });

		Main.module = '${ param.module == null ? '' : param.module }';
		if (Main.module && !Module.modules[Main.module]) {
			Main.module = 'eFiling.file.' + Main.module;
			if (!Module.modules[Main.module]) Main.module = '';
		}
		Main.dept = '${ param.dept == null ? '' : param.dept }';
		
		var PARAMETERS = { };	// REQUEST的请求参数
		<s:iterator value="#parameters" var="param">
		PARAMETERS['<s:property value="#param.key" />'] = ['<s:property value="@com.sinosoft.util.StringHelper@join(#param.value, '\&apos;,\&apos;')"/>'];
		</s:iterator>
		
		Main.maximum = !!Main.module && '${ param.maximum == null ? 'true' : param.maximum }'.parseBoolean();	// 是否最大化窗口,在module不为空时有效
		Event.observe(window, "load", function() {
			Layout.resize();
			Main.ready();
			var parent = window.parent;
			if (parent && parent[SYSTEM_CODE] === true) {
				window.parent.document.title = document.title;
			}
		});
		
		// 防止session失效
		var IP;
		var intervalRefreshCount = 1;
		var intervalRefresh = (function(IntervalRefreshHelper) {
			return function() {
				IntervalRefreshHelper.request("ip.jsp", {
					showLoading: false,
					onSuccess: function(request) {
						IP = request.responseText;
						$('showIP').innerHTML = intervalRefreshCount + "." + IP;
						intervalRefreshCount++;
					}
				});
			};
		})(new Ajax.Support());
		intervalRefresh();
		// 每5分钟访问一下后台,防止session失效.如果单点一个账号只允许一个人登陆，则如果同一账号有多人登陆，则可能依然会session失效
		var intervalRefreshTimer = window.setInterval(intervalRefresh, 4 * 60 * 1000);
	</script>
</head>

<body>
<form name="mainForm" id="mainForm" method="post">
	<!-- 头部 -->
	<div id="header">
		<div class="logo">&nbsp;</div>
		<div class="title"><s:text name="global.title" /></div>
		<div class="content">
			<span id="userContainer"><s:property value="#session.user.name" /></span> | 
			<s:select name="currentDepartmentId" id="currentDepartmentId" 
				onchange="Main.changeDepartment(this.value)"
				value="#session.user.currentDepartment.id"
				list="#session.user.currentDepartments" 
				listKey="id" listValue="id + ' - ' + name" /> | 
			<span id="dateContainer"><s:property value="formatDate(new java.util.Date())" /></span>
		</div>
	</div>
	<div id="nav">
		<a href="/platform/portal.do" target="_parent" xonclick="Main.logout()"><s:text name="global.main.nav.return" /></a> | 
		<a href="#" onclick="Menu.toggleContainer()"><s:text name="global.main.nav.close.menu" /></a> | 
		<s:text name="global.main.nav" />: <span id="currentNav"><s:text name="global.main.nav.home" /></span>
	</div>
	
	<!-- 中部区域 -->
	<div id="bodyContainer">
		<!-- 左侧菜单栏 -->
		<div id="menu"><s:text name="global.loading" /></div>

		<!-- 中间的分隔符 -->
		<div id="separator"></div>

		<!-- 主要文本显示区分-->
		<div id="mainContainer">
			<div id="mainHeader" class="hidden"><!-- 可用于显示查询条件等操作 --></div>
			<div id="mainNav" class="hidden"><!-- 可以用于显示操作菜单 --></div>
			<div id="mainBody">
				<div id="mainContext"><!-- IE7用于显示滚动条的BUG -->
					<div id="container"><!-- 显示列表 -->
						<table id="dataTable" class="list">
							<thead id="dataHeader"></thead>
							
							<tbody id="dataBody" class="nowrap"></tbody>
						</table>
					</div>
				</div>
			</div>
			<div id="mainStatusBar" class="hidden"><!-- 可显示操作信息等 --></div>
			<div id="mainFooter" class="hidden"><!-- 显示分页信息 -->
				<div id="mainFooterLefter">
					<input type="checkbox"><a><s:text name="global.checkbox.all" /></a>/<a><s:text name="global.checkbox.none" /></a> 
					<a><s:text name="global.checkbox.inverse" /></a> 
				</div>
				<div id="mainFooterCenter"><!-- 可显示一些提示信息 --></div>
				<div id="mainFooterRighter">
					<div id="pagingEntity">
						<div class="first"><a href="#" onclick="Paging.first();" id="pagingEntity.first" title="<s:text name="global.paging.first" />"></a></div>
						<div class="previous"><a href="#" onclick="Paging.previous();" id="pagingEntity.previous" title="<s:text name="global.paging.previous" />"></a></div>
						<div class="next"><a href="#" onclick="Paging.next();" id="pagingEntity.next" class="next" title="<s:text name="global.paging.next" />"></a></div>
						<div class="last"><a href="#" onclick="Paging.last();" id="pagingEntity.last" class="last" title="<s:text name="global.paging.last" />"></a></div>
						<div>
							<s:text name="global.paging.total" />:<span id="pagingEntity.total" class="b"></span>
							<s:text name="global.paging.pageCount.prefix" />:<span id="pagingEntity.pageCount" class="b"></span><s:text name="global.paging.pageCount" />
							<s:text name="global.paging.size" />:<span id="pagingEntity.size" class="b"></span>
							<s:text name="global.paging.maxResults" />:<input name="maxResults" id="pagingEntity.maxResults" value="0">
							<s:text name="global.paging.goto" /><input name="pageIndex" id="pagingEntity.pageIndex" value="1"><s:text name="global.paging.pageCount" />
							<input type="submit" value="<s:text name="global.paging.go" />" class="go" onclick="Paging['go'](); return false;" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
				
	<!-- 底部 -->
	<div id="footer">
		<!-- <object id="PSKPrt" classid="clsid:81C07687-3353-4ABA-B108-94BCE81E5CBA" codebase="resources/PSKPrn.ocx#version=1,0,0,1" width="0" height="0"></object> -->
	</div>
</form>
	<!-- 创建一个隐藏框,用于表单提交.主要是用于文件上传使用.在IE中,form的target不能是用js创建的iframe对象 -->
	<iframe id="hiddenIframe" name="hiddenIframe" width="0" height="0" style="display: none"></iframe>
	<div id="showIP" style="display: none"></div>
</body>
</html>
