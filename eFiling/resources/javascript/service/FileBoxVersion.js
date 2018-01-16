/** 档案盒版本设置 */
var FileBoxVersion = Service.create({
	/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
	namespace: 'setting/fileBoxVersion/',
	dataHeader: [
  		{ value: "global.company" }, 
  		{ value: "file.box.version.capacity", attributes: { style: { width: "20%" } } }, 
  		{ value: "global.status", attributes: { style: { width: "20%" } } }, 
  		{ value: "global.user.insert", attributes: { style: { width: "20%" } } },
  		{ value: "global.date.insert", attributes: { style: { width: "20%" } } }
  	],
  	idHandler: false,
  	dataHandler: [null, function(cell, value, index, values, data) {
  		// 状态的文字描述
  		cell.innerHTML = NumberHelper.format(value, 0);
  		$(cell).addClassName('Number');
  	}, function(cell, value, index, values, data) {
  		// 状态的文字描述
  		cell.innerHTML = this.getText('global.status.' + value);
  	}, { attributes: { className: 'date' } }],
  	initialize: function() {
  		// this.urls['index'] = 'index.do';
  	}
});