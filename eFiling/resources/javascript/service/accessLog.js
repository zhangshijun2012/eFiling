/**
 * 角色管理相关js.
 */
var AccessLog = Service.clone();
AccessLog.showResultsOptions.events = {};

AccessLog.events["clear"] = "Clear";
AccessLog.urls["clear"] = "clear.do";
AccessLog.forms["clear"] = "mainForm";
AccessLog.operations["clear"] = "clearButton";

Object.extend(AccessLog, {
	module: "access_log",					// 模块编号
	navigation: "系统管理 >> 日志管理",
	path: "system/accessLog/",
	
	
	showResultsHead: [
		//{ value: "标题" },
		{ attributes: { innerHTML: "姓名",	style: { width: "15%" } } }, 
		{ attributes: { innerHTML: "部门",	style: { width: "20%" } } }, 
		{ attributes: { innerHTML: "时间",	style: { width: "15%" } } }, 
		{ attributes: { innerHTML: "IP",	style: { width: "10%" } } }, 
		{ attributes: { innerHTML: "类型",	style: { width: "8%" } } }, 
		{ attributes: { innerHTML: "访问地址" } }
	],
	
	_validate: function(method, target, options) {
		if (method == "clear") {
			return confirm("确实要清空所有日志数据吗?");
		}
	},
	
	clear: function(target, options, successOptions) {
		this.execute("clear", target, options, successOptions);
	},
	
	onClear: function(response, target, options, headerJSON) {
		this.onRemove(response, target, options, headerJSON);
	}
});