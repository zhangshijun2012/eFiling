/** 承保资料类型设置 */
var TimeLimit = Service.clone();
// FileBoxVersion['urls']['index'] = 'index.do';
TimeLimit.showResultsOptions['selector'] = false;
Object.extend(TimeLimit, {
	/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
	namespace: 'basic/fileType/',
	showResultsHead: [
  		//{ value: "标题" },
  		{ attributes: { innerHTML: "部门",	style: { width: "15%" } } }, 
  		{ attributes: { innerHTML: "产品" } }, 
  		{ attributes: { innerHTML: "状态",	style: { width: "10%" } } }, 
  		{ attributes: { innerHTML: "期限",	style: { width: "10%" } } }, 
  		{ attributes: { innerHTML: "设置时间",	style: { width: "15%" } } }, 
  		{ attributes: { innerHTML: "设置人",	style: { width: "15%" } } }
  	],
	
	
	/** 查询方法 */
	/*
	query: function(options) {
		
	}
	*/
	
	appendX: function() {
		var url = this.getUrl('append');
		Message.open(url, {}, this.titles[method], { 
			
		});
	}
});