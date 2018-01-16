<%@ page language="java" contentType="text/plain; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%-- 输出struts2的错误信息 --%>
<s:property value="getErrorJSONObject()" escape="false" />