/** 条码打印 */
var BarcodePrint = Service.create({
	/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
	namespace: 'setting/fileType/',
	
	initialize : function() {
		this.html['mainNav'] = ' ';
		this.html['mainFooter'] = false;
		this.html[this.INDEX_CONTAINER] = '<table class="form"><tbody>'
			+ '<tr>'
			+ '<td width="5%" class="title">' + this.getText('file.type.id') + '</td>'
			+ '<td width="20%" class="requiredValue"><input class="text" name="id" /></td>'
			+ '<td></td>'
			+ '</tr>'
			+ '<tr>'
			+ '<td width="5%" class="title">' + this.getText('file.type.name') + '</td>'
			+ '<td width="20%"><input class="text" name="name" /></td>'
			+ '<td></td>'
			+ '</tr>'
			+ '<tr>'
			+ '<td width="5%" class="title">' + this.getText('数量') + '</td>'
			+ '<td width="20%" class="requiredValue"><input class="text" name="count" value="" /></td>'
			+ '<td></td>'
			+ '</tr>'
			+ '<tr>'
			+ '<td class="actionBar txtCenter" colspan="2">'
			+ '<input name="printButton" id="printButton" type="button" value="打印条形码" class="button6" onclick="Main.service.printBarcode()" />'
			+ '<input name="print2DButton" id="print2DButton" type="button" value="打印二维码" class="button6" onclick="Main.service.printQRCode()" />'
			+ '</td>'
			+ '</tr>'
			+ '</tbody></table>';
	},
	/** 得到打印参数 */
	getPrintOptions: function() {
		var form = $(this.forms['query']);
		var id = form.elements['id'].value.trim();
		if (!id) {
			alert('请输入要打印的条码值!');
			return false;
		}
		var text = form.elements['name'].value.trim();
		var count = NumberHelper.intValue(form.elements['count'].value);
		if (count < 1) {
			alert('请输入大于0的数量!');
			return false;
		}

		return {
			id: id,
			text: text,
			count: count
		};
	},
	/** 条码打印机 */
	printer: new BarcodePrinter(),
	
	/** 打印条码 */
	printBarcode: function() {
		var options = this.getPrintOptions();
		if (!options) return;
		this.printer.print(options['id'], options);
	},

	/** 打印二维码 */
	printQRCode: function() {
		var options = this.getPrintOptions();
		if (!options) return;
		this.printer.print2DQR(options['id'], options);
	},
	
	query: function() { }
});