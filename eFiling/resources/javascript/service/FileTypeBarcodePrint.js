/** 条码打印 */
var FileTypeBarcodePrint = Service.create({
	/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
	namespace: 'setting/fileType/',
	dataHeader: [
  		// { value: 'file.type.id', attributes: { } }, 
  		{ value: 'file.type.name', attributes: { width: '35%' } }, 
  		// { value: 'global.user', attributes: { } }, 
  		// { value: 'global.time', attributes: { } }, 
  		{ value: '打印数量', attributes: {  } }
  	],
  	idHandler: false,
  	dataHandler: [function(cell, value, index, values, data, id) {
		cell.innerHTML = values[1];
	}, function(cell, value, index, values, data, id) {
  		var html = '<input name="printCount" id="printCount_' + id + '" class="input-shorter" barcode="{id: \'' 
  			+ id + '\', text: \'' + StringHelper.escape(values[1]) + '\'}" />';
  			// + '<a href="#" onclick="Main.service.printBarcode(\'' + value 
  			// + '\', NumberHelper.intValue($(\'printCount_' + value + '\').value), \'' + values[2] + '\')">打印</a>';

  		cell.innerHTML = html;
  		$(cell).addClassName('txtCenter');
  	}],
	pagerMaxResults: 200,
	FILE_MODEL_FILE: '1',	// 核保资料
	FILE_MODEL_IMAGE: '0',	// 影像文件
	initialize : function() {
		this.html[this.INDEX_CONTAINER] = '<table class="form"><tbody>'
			+ '<tr>'
			// + '<td width="5%" class="title">' + this.getText('file.type.id') + '</td>'
			// + '<td width="25%"><input class="text" name="id" /></td>'
			+ '<td width="5%" class="title">' + this.getText('file.type.name') + '</td>'
			+ '<td width="25%"><input class="text" name="name" /></td>'
			+ '<td class="actionBar">'
			+ '<input type="hidden" name="fileModel" value="1" />'
			+ '<input type="hidden" name="status" value="1" />'	// 默认仅查询有效数据
			+ '<input name="queryButton" id="queryButton" type="button" value="' + this.getText('global.query') 
			+ '" onclick="Main.service.query()" />'
			+ '<input name="queryButton" id="queryButton" type="button" value="' + this.getText('global.print') 
			+ '" onclick="Main.service.printAllBarcodes()" />'
			+ '</td>'
			+ '</tr>'
			+ '</tbody></table>';
	},
	QUERY_AUTO: true,	// 默认进行查询
	/** 条码打印机 */
	printer: new BarcodePrinter(),
	/** 打印某个条码 */
	printBarcode: function(id, count, options) {
		// BarcodePrinter printer = new BarcodePrinter();
		if (confirm('是否确认打印此条码?')) return false;
		this.printer.print(id, count, options);
	},

	/** 打印所有条码 */
	printAllBarcodes: function() {
		var form = $(this.forms['query']);
		var elements = form.elements['printCount'];
		if (!elements) return;
		if (!elements.length) elements = [ elements ];
		var barcodes = [];
		var message = '';
		for (var i = 0, l = elements.length; i < l; i++) {
			var field = elements[i];
			var count = field.value;
			
			if (count && (count = NumberHelper.intValue(count)) > 0) {
				var barcode = field.getAttribute('barcode').toJSON();
				if (count > this.printer.maxCount) {
					alert(barcode['text'] + '的数量最大为999!');
					return false;
				}
				barcode['count'] = count;
				barcodes.push(barcode);
				message += barcode['id'] + '.' + barcode['text'] + ': ' + count + '份;\n';
			}
		}
		if (barcodes.length < 1) return false;
		if (!confirm('是否确认打印以下条码:\n' + message)) return false;
		var printer = this.printer;
		printer.open();
		barcodes.each(function(barcode) {
			// 是否进打印一次，如果此参数为false或未定义，则一次打印，会调用open和close，否则可能是打印多个数据，由外部调用open和close
			barcode['only'] = false;
			printer.print(barcode['id'], barcode['count'], barcode);
		});
		printer.close();
	}
});