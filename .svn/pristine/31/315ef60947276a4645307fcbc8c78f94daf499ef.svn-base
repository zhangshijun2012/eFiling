/** 承保资料类型设置 */
Language.ready(function() {
	window['FileType'] = Service.create({
		/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
		namespace: 'setting/fileType/',
		dataHeader: [
	  		{ value: 'file.type.id', attributes: { } }, 
	  		{ value: 'file.type.name', attributes: { } }, 
	  		// { value: 'file.type.alias', attributes: { } }, 
	  		{ value: 'global.status', attributes: { } }, 
	  		{ value: 'global.user', attributes: { } }, 
	  		{ value: 'global.time', attributes: { } }
	  	],
		dataHandler: [null, null, function(cell, value, index, values, data) {
		  		// 状态的文字描述
		  		cell.innerHTML = this.getText('global.status.' + value);
			}, null, function(cell, value, index, values, data) {
		  		cell.innerHTML = value || '';
		  		$(cell).addClassName('Date');
			}, function(cell, value, index, values, data) {
		  		var html = '<input id="printCount_' + value + '" value="1" class="inputCount" /> '
		  			+ '<a href="#" onclick="Main.service.printBarcode(\'' + value 
		  			+ '\', NumberHelper.intValue($(\'printCount_' + value + '\').value), \'' + values[2] + '\')">打印</a>';

		  		cell.innerHTML = html;
		  		$(cell).addClassName('txtCenter');
		  	}
		],
		FILE_MODEL_FILE: '1',	// 核保资料
		FILE_MODEL_IMAGE: '0',	// 影像文件
		FILE_SINED: '1', // 签章资料
		initialize : function() {
			this.html[this.INDEX_CONTAINER] = '<table class="form"><tbody>'
				+ '<tr>'
				+ '<td width="5%" class="title">' + this.getText('file.type.id') + '</td>'
				+ '<td width="45%"><input class="text" name="id" /></td>'
				+ '<td width="5%" class="title">' + this.getText('file.type.name') + '</td>'
				+ '<td width="45%"><input class="text" name="name" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="4" class="actionBar">'
				+ '<input name="queryButton" id="queryButton" type="button" value="' + this.getText('global.query') 
				+ '" onclick="Main.service.query()" />'
				+ '<input name="appendButton" id="appendButton" type="button" value="' + this.getText('global.append') 
				+ '" onclick="Main.service.append()" />'
				+ '</td>'
				+ '</tr>'
				+ '</tbody></table>';
			
			this.queryAll();
		},
		/** 条码打印机 */
		printer: new BarcodePrinter(),
		/** 打印条码 */
		printBarcode: function(id, count, options) {
			// BarcodePrinter printer = new BarcodePrinter();
			if (count > 10) return confirm('是否确认打印此条码?');
			this.printer.print(id, count, options);
		},
		/** 
		 * 查看明细信息
		 * @param id 数据主键
		 */
		view: function(id, options) {
			if (!Object.isString(id)) options = id || options;
			options = Object.extend({
				parameters: 'id=' + id,
				width: 800,
				buttons: {
					disable: {
						text: this.getText('disable'),
						handler: (function(id) {
							this.disable(id);
						}).bind(this, id)
					},
					/*
					print: {
						text: '打印条码',
						handler: (function(id) {
							var options = {
								'text': this.form.elements['entity.name'].value
							};
							this.printBarcode(id, options);
						}).bind(this, id)
					},*/
					close: {
						text: this.getText('close'),
						handler: function() {
							this.hide();
						}
					}
				}
			}, options || { });
	
			var onSuccess = options['onSuccess'];
			options['onSuccess'] = (function() {
				if (onSuccess) onSuccess.apply(this, $A(arguments));
				// var form = this.forms['view'] = $($(this.dialogs['view'].body).select('form')[0]); 父类中最先会执行此条语句
				var status = this.forms['view'].elements['entity.status'].value;
				if (status == '0') {
					// 数据已经被禁用,则不需要禁用按钮
					this.dialogs['view'].removeButtons(['disable']);
				}
			}).bind(this),
			this['super']['view'](id, options);
		},
		/** 
		 * 禁用某条数据
		 * @param id 数据主键
		 */
		disable: function(id) {
			if (!confirm(this.getText('file.type.disable.tip'))) return;
			this.request(this.getUrl('disable'), {
				parameters: 'id=' + id,
				onSuccess: (function(id, response) {
					var text = response.responseText;
					var json = text.toJSON();
					if (json && json['success']) {
						// 操作成功
						alert(this.getText('file.type.disable.success'));
						this.view(id);
					} else {
						alert(json ? json['message'] || this.getText('file.type.disable.failure') : text);
					}
				}).bind(this, id)
			});
		},
		
		/**
		 * 改变承保资料
		 * @param field
		 */
		changeCode: function(field) {
			var form = this.form;
			if (!field) field = form.elements['entity.code'];
			var option = field.options[field.selectedIndex];
			var name = option ? option.text : '';
			 field = form.elements['entity.name'];
			 var alias = field.getAttribute('alias');
			 var value = field.value.trim();
			if (!value || value == String.trim(alias)) {
				field.value = name;
				field.setAttribute('alias', name);
			}
			
		},
		
		all: null,	// 所有数据,JSON格式
		fileTypes: null,	// 所有数据,数组格式
		queryAll: function() {
			var all = this.all = { };
			var $this = this;
			this.request(this.getUrl('queryAll'), {
				onSuccess: function(response) {
					var json = response.responseText.toJSON();
					if (!json) return;
					$this.fileTypes = json;
					json.each(function(value){
						all[value['id']] = value;
					});
				}
			});
		}
	});
});
// (FileType.queryAll.bind(FileType));