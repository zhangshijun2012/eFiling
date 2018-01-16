/** 档案差缺报表 */
var FileLackReport = Service.create({
	namespace : 'file/report/',
	//"险种（产品线）", "部门", "业务人员", "业务关系代码", "保/批单号", "投保人", "签单日期", "生效日期", "差缺文件清单",
	// "超期天数(签单日期至报表日期-设置的超期天数)"
	dataHeader: [
   		{ html: "保/批单号", attributes: { style: { width: "8%" } } }, 
  		{ html: "险种", attributes: { title: '产品线', style: { width: "5%" } } }, 
  		{ html: "车牌/发动机号", attributes: { style: { width: "5%" } } },
  		{ html: "部门", attributes: { style: { width: "8%" } } }, 
  		{ html: "业务人员", attributes: { style: { width: "5%" } } }, 
  		{ html: "业务关系代码", attributes: { style: { width: "5%" } } }, 
  		{ html: "投保人" }, 
  		{ html: "签单日期", attributes: { style: { width: "5%" } } }, 
  		{ html: "生效日期", attributes: { style: { width: "5%" } } }, 
  		{ html: "差缺文件清单", attributes: { style: { width: "8%" } } }, 
  		{ html: "超期天数", attributes: { title: '签单日期至报表日期-设置的超期天数', style: { width: "8%" } } }
	],
	// idHandler: false,	// 第一行显示超链接
	dataHandler: [null, null, null, null, null, null, null, null, null, null, 
		function(cell, value, index, values, data) {
			cell.innerHTML = value = NumberHelper.format(value, 0, 0);
			$(cell).addClassName('number');
		}],
	pagerMaxResults: 200,
	/*
	initialize : function() {
  		this.urls['index'] = 'index.do';
	},
	*/
	/** 覆盖view方法 */
	view: function(id) {
		FileService.view(id); 
	}
});