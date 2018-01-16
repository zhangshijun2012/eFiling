/** 档案查询 */
var FileQuery = Service.create({
	namespace : 'file/query/',
	dataHeader: [
  		{ html: "file.document.no", attributes: { style: { width: "10%" } } }, 
  		{ html: "file.document.type", attributes: { style: { width: "5%" } } }, 
  		{ html: "global.policy.holder", attributes: { style: { width: "8%" } } }, 
  		{ html: "file.document.status", attributes: { style: { width: "5%" } } }, 
  		{ html: "file.document.lacks" }, 
  		{ html: "file.document.salesman", attributes: { style: { width: "5%" } } }, 
  		{ html: "file.document.business.no", attributes: { style: { width: "5%" } } }, 
  		{ html: "file.document.business.department", attributes: { style: { width: "8%" } } }, 
  		{ html: "file.document.agent", attributes: { style: { width: "8%" } } },  
  		{ html: "file.document.business.user", attributes: { style: { width: "8%" } } }, 
  		{ html: "file.document.b2c", attributes: { style: { width: "5%" } } },
  		{ html: "file.time", attributes: { style: { width: "5%" } } }, 
  		{ html: "file.loan.status", attributes: { style: { width: "5%" } } },
  		{ html: "file.user", attributes: { style: { width: "5%" } } }
	],
  	dataHandler: [null, function(cell, value, index, values, data) {
  		// 显示单证类型的文字描述
  		cell.innerHTML = this.getText('file.document.type.' + value);
  	}, null, function(cell, value, index, values, data) {
  		// 归档状态
  		cell.innerHTML = this.getText('file.document.status.' + value);
  	}, null, null, null, null, null, null, function(cell, value, index, values, data) {
  		// 是否电子商务
  		if (value) cell.innerHTML = this.getText(value === '1' ? 'global.yes' : 'global.no');
  	}, null, function(cell, value, index, values, data) {
  		// 是否借出
  		if (value) cell.innerHTML = this.getText(value === '1' ? 'global.yes' : 'global.no');
  	}],
	initialize : function() {
		this.exportButtons.push("printButton");
  		this.urls['index'] = 'index.do';
	},
	
	view: function() {
		FileService.view.apply(FileService, arguments);
	},
	print: function() {
		var boxId = $('boxId');
		if (boxId && !boxId.value) {
			alert("请输入档案盒查询后,在打印档案盒清单！");
			return;
		} 
		var html = '';
		var printBoxId = boxId;
		if (printBoxId && Object.isString(printBoxId)) printBoxId = printBoxId.trim().split(/\s*,\s*/);
		else if (printBoxId && !Object.isArray(printBoxId)) printBoxId = [ printBoxId ];
		var parameters = "boxId=" + boxId.value;
		var options = {
				parameters: parameters,
				onSuccess: (function(response) {
					var message = response.responseText.toJSON();
					if(!message['list']) {
						alert("档案盒号:" + boxId.value + "没有资料类型！");
						return;
					} 
					var list = message['list'];
					var index = 0;
					var html = '<p class="b">档案盒号 ' + boxId.value + '</p>';
					html += '<div class="pagingAfter paddingBottom">';
					html += '<table class="list minHeight">';
					html += '<thead>'
						+ '<tr>'
						+ '<td width="2%">页码</td>'
						+ '<td width="10%">业务号</td>'
						+ '<td>投保人</td>'
						+ '<td>资料类型</td>'
						+ '<td width="8%">档案编码</td>'
						+ '</tr>';
					+ '</thead>';
					html += '<tbody>';
					list.each(function(row, index) {
						html += '<tr>'
							+ '<td class="number">' + row.pageIndex + '</td>'	// 在档案盒中的页码
							+ '<td>' + row['document'].no + '</td>'
							+ '<td>' + row['document'].applicant + '</td>';
							if (row['fileType']) {
								html += '<td>' + row['fileType'].name + '</td>';
							} else {
								html += '<td>' + '</td>';
							}
						html += '<td>' + row.no + '</td>'
							 + '</tr>';
					});
					html += '</tbody>';
					html += '</table>';
					html += '</div>';
			 		TempDialog.show(html, this.getText('打印上传清单'), { width: 1000, buttons: { confirm: { text: this.getText('global.print'), handler: function() { Main.print(html); this.hide(); } } } });
				}).bind(this)
			};
		this.request(this.getUrl('print'), options);
	}
});