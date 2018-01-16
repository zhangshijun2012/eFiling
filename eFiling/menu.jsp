<%@ page language="java" contentType="text/plain; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- <s:iterator value="list" var="menu" status="status">
	<div id="menu_${ id }" class="collapsed menu" title="${ name }" 
		action="${ task.id }" 
		empty="<s:property value="@com.sinosoft.efiling.service.UserService@isActionMenu(#menu)" />">
		<div class="menuTitle" onclick="Menu.click('${ id }', 'menu_${ id }', 'menuItems_${ id }')"><a href="#">${ name }</a></div>
	<s:if test="@com.sinosoft.efiling.service.UserService@isActionMenu(#menu)">
		<!-- 无子节点的菜单 -->
	</s:if>
	<s:else>
		<!-- 可加载子菜单的节点 -->
		<div class="menuItems" id="menuItems_${ id }">
			<!-- 用于加载子节点 -->
			正在加载子菜单...
		</div>
	</s:else>
	</div>
</s:iterator>
<s:fielderror></s:fielderror>
<s:actionerror></s:actionerror> --%>
{
<s:iterator value="list" var="menu" status="status">
	"${ id }": <s:property value="#menu.toJSONString()" escape="false" /><s:if test="!#status.last">,</s:if>
</s:iterator>
}