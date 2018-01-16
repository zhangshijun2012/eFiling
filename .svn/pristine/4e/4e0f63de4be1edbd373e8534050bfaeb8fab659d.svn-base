(function() {
/** XML中的异常代码 */
var EXCEPTION_CODES = {
	'NOT_FOUND' : 'not_found', // 可能是因为尚未生成文档
	'FIRST_PAGE_NOT_FOUND' : 'first_page_not_find', // 第一页上没法二维码

	// 错误代码: E0(业务号错误)/1(资料类型错误),0(扫描错误)/1(业务错误),
	'DOCUMENT_NOT_IDENTIFIED' : 'E000', // 单证号未能识别
	'DOCUMENT_NOT_EXISTS' : 'E010', // 业务号不存在
	'FILE_TYPE_NOT_IDENTIFIED' : 'E100', // 资料类型未能识别
	'FILE_TYPE_NOT_NEED' : 'E110', // 识别出的资料类型不在核保所需的资料中
	'FILE_TYPE_NOT_EXISTS' : 'E111' // 识别出的资料类型不存在
};
var ERROR_CODE = EXCEPTION_CODES;
var ERROR_HANDLE_METHODS = { // 错误处理方式
	'DELETE' : '0', // 删除资料
	'CHANGE_FILE_TYPE' : '1', // 更改资料类型
	'INPUT_DOCUMENT_NO' : '2', // 补录保单号
	'NOT_HANDLE' : '3', // 不进行处理,保留此档案类型.此处理方式仅适用于FILE_TYPE_NOT_NEED识别出的文档类型不在所需的核保资料中
	'VOIDED_DOCUMENT': '4',	// 处理为作废单证
	'INPUT_VOIDED_DOCUMENT': '40'	// 补录作废单证，适用于业务号没有识别的情况
};
var DEFAULT_PERIOD = 500; // 在读取xml时默认的时间间隔
/** 文件扫描的响应程序 */
var FileScanHandler = window['FileScanHandler'] = Class.create({
	batchId : null, // 批次号
	data : null, // 读取XML后的JSON对象
	xml : null, // xml对象,在read方法后才生成
	xmlString : null, // XML字符串
	root : null, // XML的根节点
	options : null,
	stopped : false,	// 当前扫描是否已停止
	period : 0,	// 扫描结果读取定时器的读取间隔
	/**
	 * 初始化,必须传入batchId
	 * 
	 * @param batchId
	 * @param options
	 * @param parent 调用此对象的FileScanManager对象
	 */
	initialize : function(batchId, options, parent) {
		this.stopped = false;
		this.batchId = batchId || this.batchId;
		this.options = options || this.options || {};
		this.parent = parent || this.parent;
		this.timer = null;
		this.period = NumberHelper.intValue(this.options['period']) || this.period || FileScanHandler.DEFAULT_PERIOD;
		this.data = null;
		this.xml = null;
		this.root = null;
		this.errors = null;
	},
	/** 停止扫描,取消定时器 */
	stop : function() {
		this.stopped = true;
		if (!this.timer) return;
		// 停止定时器
		clearTimeout(this.timer);
		this.timer = null;
		// this.period = 0; // NumberHelper.intValue(this.options['period']) || FileScanHandler.DEFAULT_PERIOD;
		this.xml = null;
		this.root = null;
		this.errors = null;
	},

	/** 获得国际化文本 */
	getText : function() {
		return this.parent.getText.apply(this.parent, arguments);
	},
	/** 列出扫描结果 */
	list : function(response) {
		// alert('list' + this.options['list']);
		if (this.options['list']) { return this.options['list'].apply(this, arguments); }
		var errors = this.data['errors'] = this.data['errors'] || [];
		// var errors = this.data['errors']; // response.responseText.trim();
		// if (errors) errors = errors.toJSON();
		var documentCount = this.data['count']; // XMLHelper.selectNodes(this.root, '//document').length;
		// var fileCountNodes = XMLHelper.selectNodes(this.root, '//document/file/count');
		var pageCount = this.data['pages'];
		// for ( var i = 0, l = fileCountNodes.length; i < l; i++) {
		// var node = fileCountNodes[i];
		// var count = getNodeText(node);
		// count = NumberHelper.intValue(count);
		// pageCount += count;
		// }

//		var html = '<from name="completeForm" id="completeForm">' + '<p>' + this.getText('file.count.document')
//				+ ':' + documentCount + '</p>' + '<p>' + this.getText('file.count.page') + ':' + pageCount + '</p>'
//				+ '<p>' + this.getText('file.time.upload') + ':' + DateHelper.format(new Date(), 'YYYY-MM-DD')
//				+ '</p>' + '<p>' + this.getText('global.user') + ':' + Main.user['name'] + '</p>';
		var html = '<from name="completeForm" id="completeForm">';
		html += this.getText('file.scan.result.tip', [ documentCount, pageCount ]); // "本次扫描保/批单共？份，档案页数？页"
		var handledErrors = [];	// 自动处理默认结果
		if (errors && errors.length > 0) {
			// 列出错误信息
			html = html + '<p>' + this.getText('file.scan.error') + ':' + '</p>';
			this.errors = errors;
			// 处理错误信息
			html = html + '<table class="list">' 
				+ '	<thead><tr>' 
				+ '		<td width="10%">' + this.getText('file.document.no') + '</td>' 
				+ '		<td width="15%">' + this.getText('file.type') + '</td>' 
				+ '		<td width="5%">' + this.getText('file.page') + '</td>' 
				+ '		<td>' + this.getText('file.scan.error.reason') + '</td>' 
				+ '		<td width="5%">' + this.getText('global.attachment') + '</td>' 
				+ '		<td width="45%">' + this.getText('global.operate') + '</td>' 
				+ '		<td width="10%">' + this.getText('file.scan.error.handle.status') + '</td>' 
				+ '</tr></thead>' 
				+ '<tbody class="nowrap">';
			errors.each((function(err, index) {
				var fileType = err['fileType'] || '';
				fileType = FileType.all[fileType];
				if (fileType) fileType = fileType['name']; // fileType['id'] + '.' + fileType['name'];
				if (!fileType) fileType = err['fileType'] || '';
				var li = 1;
				html = html 
					+ '<tr exceptionIndex="' + index + '">' 
					+ '	<td>' + (err['documentId'] || '') + '</td>'
					+ '	<td>' + fileType + '</td>'
					+ '	<td class="number">' + (err['pageIndex'] || '') + '</td>'
					+ '	<td>' + (err['message'] ? this.getText(err['message'], [(err['documentId'] || ''), fileType, (err['pageIndex'] || '')]) : '') + '</td>'
					+ '<td><a href="' + DataCapPreviewServers.get({ 'xmlId': this.batchId, 'name': err['fileName'] })
					+ '" target="_blank">' + this.getText('global.attachment') + '</a>'
					+ '</td>'
					+ '	<td><div style="width: 445px; height: 23px; overflow: hidden;"><select name="errorHandle" id="errorHandle' + index 
					+ '" xpath="' + err['xpath'] 
					+ '" exceptionCode="' + err['code'] 
					+ '" fileType="' + (err['fileType'] || '')
					+ '" visaNo="' + (err['visaNo'] || '') 
					+ '" documentId="' + err['documentId']
					+ '" documentIndex="' + err['documentIndex'] + '" style="width: 180px"'
					+ ' onchange="Main.service.handler.handleError(' + index + ', this.value)">' 
					+ '			<option value="">' + this.getText('file.scan.error.handle.tip')+ '</option>'
					+ '			<option value="' + ERROR_HANDLE_METHODS['DELETE'] + '">'
					+ (li++) + '.' + this.getText('file.scan.error.handle.0') + '</option>'; // 删除记录
				switch (err['code']) {
				case EXCEPTION_CODES['FILE_TYPE_NOT_NEED']:
					handledErrors.push({ "index" : index, "value" : ERROR_HANDLE_METHODS['NOT_HANDLE'] });
					html = html + '			<option selected value="' + ERROR_HANDLE_METHODS['NOT_HANDLE'] + '">'
						+ (li++) + '.' + this.getText('file.scan.error.handle.3') + '</option>'; // 不进行处理,保留此资料类型
				case EXCEPTION_CODES['FILE_TYPE_NOT_IDENTIFIED']:
				case EXCEPTION_CODES['FILE_TYPE_NOT_EXISTS']:	// 资料类型不存在
					/* 可更改资料类型 */
					html = html + '			<option value="' + ERROR_HANDLE_METHODS['CHANGE_FILE_TYPE'] + '">'
						+ (li++) + '.'+ this.getText('file.scan.error.handle.1') + '</option>' + '</select> '
						+ '<select name="errorHandleResult" id="errorHandleResult' + index
						+ '" class="hidden" style="width: 260px"'
						+ ' onchange="Main.service.handler.handleErrorResult(' + index + ', this.value)">'
						+ '	<option value="">请选择...</option>';
				
					(err['fileTypes'] || FileType['fileTypes']).each(function(fileType) {
						if (fileType['fileModel'] && fileType['fileModel'] != '1') return;	// 仅显示核保资料
						html = html + '<option value="' + fileType['id'] + '">' + fileType['id'] + '.' + fileType['name'] + '</option>';
					});
					html = html + '</select>';
					break;
				case EXCEPTION_CODES['DOCUMENT_NOT_IDENTIFIED']:
				case EXCEPTION_CODES['DOCUMENT_NOT_EXISTS']:
					html += '		<option value="' + ERROR_HANDLE_METHODS['INPUT_DOCUMENT_NO'] + '">'
							+ (li++) + '.'+ this.getText('file.scan.error.handle.2') + '</option>'; // 补录业务号
					if (!err['documentId']) {
						// 补录作废单证号
						html += '		<option value="' + ERROR_HANDLE_METHODS['INPUT_VOIDED_DOCUMENT'] + '">'
								+ (li++) + '.'+ this.getText('file.scan.error.handle.40') + '</option>';
					} else if (err['documentId'].length != 22) {
						// 处理为作废单证
						html += '		<option value="' + ERROR_HANDLE_METHODS['VOIDED_DOCUMENT'] + '">'
								+ (li++) + '.'+ this.getText('file.scan.error.handle.4') + '</option>';
					}
					html += '</select> ' + '<input name="errorHandleResult" id="errorHandleResult' + index
							+ '" class="hidden" style="width: 260px" onblur="Main.service.handler.handleErrorResult(' + index + ', this.value)"/>';
					break;
				}
				html = html + '</div></td>' + '	<td id="errorHandleStatus' + index + '" class="red" value="0">未处理</td>' + '</tr>';
				// alert(index + ',' + err['code'] +',' + (err['code'] == EXCEPTION_CODES['DOCUMENT_NOT_EXISTS']) + html);
			}).bind(this));
			html = html + '</tbody></table>';
		} else errors = null;
		//alert(html);
		var $this = this;
		this.listDialog = TempDialog.show(html, '扫描结果', {
			maxWidth : errors ? 1200 : 300,
			minHeight : 200,
			hideable: false,	// 隐藏关闭按钮
			buttons : {
				confirm : {
					text : this.getText('global.submit'),
					handler : function() {
						// 确定保存扫描结果,需要上传XML文档
						var dialog = this;
						$this.update({
							'afterUpdate': function(json) {
								if (json['success']) dialog.hide();
							}
						});
					}
				},
				cancel : {
					text : this.getText('global.cancel'),
					handler : function() {
						var dialog = this;
						$this.cancel({
							'afterCancel': function(json) {
								if (json['success']) dialog.hide();
							}
						});
					}
				}
			}
		});
		
		if (handledErrors.length > 0) {	// 选择不处理的进行处理
			handledErrors.each((function(error, index) {
				this.handleError(error.index, error.value);
			}).bind(this));
		}
	},
	/**
	 * 读取XML后的回调函数
	 * 
	 * @param response
	 * @returns
	 */
	readHandler : function(response) {
		var json = this.data = response.responseText.toJSON();
		// alert(json + ' = ' + response.responseText);
		this.xmlString = json.xml;
		// alert(this.xmlString);
		var xml = this.xml = XMLHelper.load(this.xmlString);
		var root = this.root = xml.documentElement;
		// alert('xml=' + this.xmlString);

		var handler = this.options['read'] || this.options['onRead'] || this.options['readHandler'];
		if (handler) return handler.apply(this, arguments);

		var exceptionNode = this.data['exception'];
		// alert(exceptionNode);
		if (exceptionNode) {
			var onException = this.options['readException'];
			if (onException) {
				// 定义了异常处理函数
				return onException.apply(this, [ exceptionNode, response ]);
			}

			var exceptionCode = exceptionNode['code'];
			if (EXCEPTION_CODES['NOT_FOUND'] == exceptionCode) {
				// 没有发现处理结果,可能是尚未处理完成,因此每隔5秒重新读取
				return this.read(this.period);
			}
			var exceptionMsg;
			if (EXCEPTION_CODES['FIRST_PAGE_NOT_FOUND'] == exceptionCode) {
				exceptionMsg = '扫描的文档第一页有误,可能的情况有:\n'
					+ '1.非车险扫描时,首页没有二维码或者二维码无法识别;\n'
					+ '2.车险扫描时,第一页非有价单证,或者有价单证号无法识别!';
			} else {
				exceptionMsg = exceptionNode['message']; // 异常信息
			}
			alert(exceptionMsg || exceptionCode);
			this.parent.cancelScan();
			return;
		}

		// Dialog.show(response.responseText.replaceAll('<', '&lt;'));
		// alert('xml=' + root + this.xmlString);

		var onSuccess = this.options['readSuccess'];
		if (onSuccess) return onSuccess.apply(this, arguments);

		this.list();
		this.parent.completeScan();
	},
	/**
	 * 读取XML文档
	 * 
	 * @param delay
	 * @returns
	 */
	read : function(delay) {
		if (this.stopped) return;	// 已停止
		if (delay) {
			delay = NumberHelper.intValue(delay, FileScanHandler.DEFAULT_PERIOD);
			return this.timer = window.setTimeout((function() {
				this.read();
			}).bind(this), delay);
		}

		FileScanXML.read(this.batchId, {
			parameters: this.parent.scanParameters || '',
			onSuccess : (function() {
				this.readHandler.apply(this, arguments);
			}).bind(this)
		});
	},
	/**
	 * 上传XML的回调函数
	 * 
	 * @param response
	 * @returns
	 */
	updateHandler : function(response) {
		// alert(response.responseText);
		// 通过外部指定的回调函数
		var handler = this.options['update'] || this.options['onUpdate'] || this.options['updateHandler'];
		if (handler) return handler.apply(this, arguments);

		
		var json = response.responseText.toJSON();
		if (!json) json = { message : response.responseText };
		if (json['message']) {
			// 如果有message,则进行提示
			alert(json['message']);
		}
		var success = json && json['success'];
		if (success) {
			// 保存成功后的回调函数
			var onSuccess = this.options['updateSuccess'] || this.complete;
			if (onSuccess) onSuccess.apply(this, [ json, response ]);
		} else {
			var onException = this.options['updateException'];
			if (onException) onException.apply(this, [ json, response ]);
		}
		
		// 最后统一调用afterUpdate
		var afterUpdate = this.options['afterUpdate'];
		if (afterUpdate) afterUpdate.apply(this, [ json, response ]);
	},
	/** 上传更新xml文档 */
	update : function(options) {
		for (var i = 0, l = this.data.errors.length; i < l; i++) {
			// var e = $('errorHandle' + i);
			// if (e.disabled) continue;	// 已经禁用,则不需要判断
			var e = $('errorHandleStatus' + i);
			if (e.getAttribute('value') != '1') {
				alert('请先处理第' + (i + 1) + '行的错误!');
				return false;
			}
		}
		// if (!confirm('确定保存扫描结果?')) return false;
		this.options['afterUpdate'] = options ? options['afterUpdate'] : null;
		// alert(this.xmlString + ',\n' + XMLHelper.toString(this.xml));
		FileScanXML.update(this.batchId, XMLHelper.toString(this.xml), {
			parameters: this.parent.scanParameters || '',
			onSuccess : (function() {
				this.updateHandler.apply(this, arguments);
			}).bind(this)
		});
	},

	/**
	 * 取消上传操作的回调函数
	 * 
	 * @param response
	 * @returns
	 */
	cancelHandler : function(response) {
		var json = response.responseText.toJSON();
		// alert(response.responseText + ' ' + json);
		if (!json) json = {
			message : response.responseText
		};
		if (json['message']) {
			// 如果有message,则进行提示
			alert(json['message']);
		}
		var success = json && json['success'];
		if (success) {
			var onSuccess = this.options['cancelSuccess'];
			if (onSuccess) onSuccess.apply(this, arguments);
		} else {
			var onException = this.options['cancelException'];
			if (onException) onException.apply(this, [ json, response ]);
		}
				
		// 最后统一调用afterCancel
		var afterCancel = this.options['afterCancel'];
		if (afterCancel) afterCancel.apply(this, [ json, response ]);
		this.parent.cancelScan();	// 取消扫描
	},
	/** 取消上传 */
	cancel : function(options) {
		if (!confirm('是否确定要取消本次扫描?')) return false;
		this.options['afterCancel'] = options ? options['afterCancel'] : null;
		FileScanXML.cancel(this.batchId, {
			parameters: this.parent.scanParameters || '',
			onSuccess : (function() {
				this.cancelHandler.apply(this, arguments);
			}).bind(this)
		});
	},
	
	/**
	 * 得到装盒的数据,将返回的数据封装为按档案盒归类的JSON数据
	 */
	getBoxesData: function(details) {
		var boxes = this.boxes = { };
		this.boxesCount = 0;
		// var details = this.data.details;
		if (!details) return;
		// fileBox.getId(), batchPageIndex, count, file.getNo(), file.getFileType().getName(), entity.getNo(), entity.getApplicant(), pageIndex
		// this.boxesCount = details.length;
		var boxesCount = 0;
		details.each(function(detail) {
			var boxId = detail[0];
			var box = boxes[boxId];
			if (!box) {
				boxesCount++;
				box = boxes[boxId] = {
					id: boxId, 
					pagesCount: 0, // 页数
					filesCount: 0,	// 文件数量
					files: [ ], 
					add: function(file) { 
						this.files.push(file); 
						this.pagesCount += file['pagesCount'];
						this.filesCount++;
					} 
				};
			}
			var file = {
				businessNo: detail[5],
				applicant: detail[6],
				id: detail[3],
				pageIndex: detail[7],	// 在档案盒中的页码,从0开始
				batchPageIndex: detail[1],	// 本批次的页码,从0开始
				pagesCount: detail[2],	// 页数
				typeName: detail[4]
			};
			box.add(file);
		});
		
		this.boxesCount = boxesCount;
	},
	
	printMaxResults: 49,	// 打印清单时每页的最大行数,默认情况下,每页可打印49行
	
	/** 
	 * 打印上传清单,格式为:
	 * 档案盒号	500013092301		装盒页数	23页
	 * 页数	业务号	资料类型	投保人	档案编码
	 * 
	 * @param boxId 要打印的档案盒号,可以为数组或者逗号分隔的数组字符串
	 */
	print: function(boxId) {
		if (!this.boxes) return;
		var html = '';
		var printBoxId = boxId;
		if (printBoxId && Object.isString(printBoxId)) printBoxId = printBoxId.trim().split(/\s*,\s*/);
		else if (printBoxId && !Object.isArray(printBoxId)) printBoxId = [ printBoxId ];
		var boxesCount = printBoxId ? printBoxId.length : this.boxesCount;
		for (var boxId in this.boxes) {
			if (printBoxId && !printBoxId.include(boxId)) continue;	// 不是要打印的档案盒
			boxesCount--;
			var box = this.boxes[boxId];
			// var filesCount = box['filesCount'];
			// var pages = Math.ceil(box['filesCount'] / this.printMaxResults);	// 需要打印的页数
			var files = box['files'];
			var index = 0;
			var filesCount = files.length;
			while (index < filesCount) {
				html += '<p class="b">档案盒号 ' + boxId + '</p>';
				html += '<div';
				// 注意最后一行不需要pagingAfter
				html += ((boxesCount == 0 && filesCount - index <= this.printMaxResults) ? '' : ' class="pagingAfter paddingBottom"');
				html += '">';
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
				var count = 0;
				while(index < filesCount && (count++ < this.printMaxResults)) {
					var file = files[index++];
					html += '<tr>'
						+ '<td class="number">' + (file['pageIndex'] + 1) + '</td>'	// 在档案盒中的页码
						+ '<td>' + file['businessNo'] + '</td>'
						+ '<td>' + file['applicant'] + '</td>'
						+ '<td>' + file['typeName'] + '</td>'
						+ '<td>' + file['id'] + '</td>'
						+ '</tr>';
				}
				
				html += '</tbody>';
				html += '</table>';
				html += '</div>';
			}
		}

		TempDialog.show(html, this.getText('打印上传清单'), 
				{ width: 1000, buttons: { confirm: { text: this.getText('global.print'), handler: function() { Main.print(html); this.hide(); } } } });
	},


	/** 完成,扫描并提交保存成功后的处理 */
	complete: function(json) {
		this.data = json;
		var details = json.details;
		this.getBoxesData(details);
		// alert(details);
		var html = '<table class="list">' 
				+ '<thead><tr>' 
				+ '<td width="33%">' + this.getText('file.box.no') + '</td>'
				+ '<td width="33%">' + this.getText('file.scan.result.box') + '</td>' 
				+ '<td width="33%">' + this.getText('file.count.page') + '</td>' 
				+ '</tr></thead>' 
				+ '<tbody>';
		var preRow, row;
		var length = details.length - 1;
		var $this = this;
		var rowIndex = 0;
		details.each(function(detail, index){
			// fileBox.getId(), pageIndex(0开始), file.getPageCount(), file.getNo(), file.getFileType().getName(), entity.getNo(), entity.getApplicant()
			row = [ detail[0], detail[1], detail[2] ];
			if (!preRow) preRow = row;
			else if (preRow[0] == row[0]) {
				preRow[2] += row[2];
			}
			if (preRow[0] != row[0]) {
				html = html + '<tr class="' + (rowIndex % 2 ? 'even' : 'odd') + '">'
					+ '<td><a href="#" onclick="Main.service.handler.showBox(\'' + preRow[0] + '\')">' + preRow[0] + '</a></td>'
					+ '<td>' 
					// + $this.getText('file.scan.result.box.tip', [ preRow[1] + 1, preRow[1] + preRow[2] ])
					+ $this.getText(preRow[2] == 1 ? 'file.scan.result.box.tip.one' : 'file.scan.result.box.tip', [ preRow[1] + 1, preRow[1] + preRow[2] ])
					+ '</td>' 
					+ '<td class="number">' + preRow[2] + '</td>' 
					+ '</tr>'; 
				preRow = row;
				rowIndex++;
			}
			if (length == index) {	// 最后一行
				html = html + '<tr class="' + (rowIndex % 2 ? 'even' : 'odd') + '">'
				+ '<td><a href="#" onclick="Main.service.handler.showBox(\'' + preRow[0] + '\')">' + preRow[0] + '</a></td>'
				+ '<td>' 
				// + $this.getText('file.scan.result.box.tip', [ preRow[1] + 1, preRow[1] + preRow[2] ]) 
				+ $this.getText(preRow[2] == 1 ? 'file.scan.result.box.tip.one' : 'file.scan.result.box.tip', [ preRow[1] + 1, preRow[1] + preRow[2] ])
				+ '</td>' 
				// /*+ ',' + $this.getText('file.scan.result.box.total.page', [ preRow[2] ])*/ + '</td>' 
				+ '<td class="number">' + preRow[2] + '</td>' 
				+ '</tr>';
				// rowIndex++;
			} 
		});

		html = html + '</tbody></table>';
		if (this.options['afterComplete']) this.options['afterComplete'].apply(this, [json, html]);
		else {
			html = '<div class="margin floatLeft nowrap" style="width: 500px">' 
//				+ '<div class="marginTop marginBottom txtCenter b red">' + this.getText('file.scan.success') 
//				+ '&nbsp; &nbsp; &nbsp; <a href="#" onclick="Main.service.refresh();">继续扫描</a></div>'
//				+ '<p class="b">' + this.getText('file.scan.list') + ':'
//				+ ' &nbsp; &nbsp; <input type="button" class="button6" value="打印上传清单" onclick="Main.service.handler.print()" /></p>'
//				+ html 
				+ '<p>' + this.getText('file.scan.list') + '</p>'
				+ html
				+ '<div class="txtCenter margin">'
				+ '<input type="button" class="button6" value="打印上传清单" onclick="Main.service.handler.print()" /> '
				+ '<input type="button" class="button6" value="继续扫描" onclick="Main.service.refresh()" /> '
				+ '</div>'
				+ '</div>';
			$('mainHeader').innerHTML = html;
		}
	},
	
	/**
	 * 显示装盒信息.这个是按照业务号来进行显示的.每个业务号一条记录
	 * @param boxId 档案盒号
	 */
	showBox: function(boxId) {
		var details = this.data.details;
		var html = '<table class="list nowrap">'
			+ '<thead><tr>' 
			+ '<td width="5%">' + this.getText('file.scan.result.box.detail.page') + '</td>' 
			+ '<td width="5%">' + this.getText('file.count.page') + '</td>' 
			+ '<td width="15%">' + this.getText('file.no') + '</td>' 
			+ '<td width="30%">' + this.getText('file.type') + '</td>' 
			+ '<td width="15%">' + this.getText('file.document.no') + '</td>' 
			+ '<td>' + this.getText('global.policy.holder') + '</td>' 
			+ '</tr></thead>'
			+ '<tbody>';
		var preRow, row;
		var length = details.length - 1;
		var rowIndex = 0;
		var $this = this;
		details.each(function(detail, index){
			// fileBox.getId(), pageIndex(0开始), file.getPageCount(), file.getNo(), file.getFileType().getName(), entity.getNo(), entity.getApplicant()
			if (boxId != detail[0]) return;
			// if (documentId && documentId == detail[5]) continue;
			row = [ detail[5], detail[1], detail[2], detail[3], detail[4], detail[6] ];
			if (!preRow) preRow = row;
			else if (preRow[0] == row[0]) {
				preRow[2] += row[2];
			}

			if (preRow[0] != row[0]) {	// 新的业务号
				html = html + '<tr class="' + (rowIndex % 2 ? 'even' : 'odd') + '">'
					+ '<td class="number">' 
					+ $this.getText(preRow[2] == 1 ? 'file.scan.result.box.tip.one' : 'file.scan.result.box.tip', [ preRow[1] + 1, preRow[1] + preRow[2] ]) 
					+ '</td>'
					+ '<td class="number">' + preRow[2] + '</td>'
					+ '<td>' + preRow[3] + '</td>'
					+ '<td>' + preRow[4] + '</td>'
					+ '<td><a href="#" onclick="Main.service.handler.showDetail(\'' + preRow[0] + '\')">' + preRow[0] + '</a></td>'
					+ '<td>' + preRow[5] + '</td>'
					+ '</tr>';
				preRow = row;
				rowIndex++;
			}
		});
		// 最后一行
		html = html + '<tr class="' + (rowIndex % 2 ? 'even' : 'odd') + '">'
			+ '<td class="number">' 
			+ $this.getText(preRow[2] == 1 ? 'file.scan.result.box.tip.one' : 'file.scan.result.box.tip', [ preRow[1] + 1, preRow[1] + preRow[2] ])
			+ '</td>'
			+ '<td class="number">' + preRow[2] + '</td>'
			+ '<td>' + preRow[3] + '</td>'
			+ '<td>' + preRow[4] + '</td>'
			+ '<td><a href="#" onclick="Main.service.handler.showDetail(\'' + preRow[0] + '\')">' + preRow[0] + '</a></td>'
			+ '<td>' + preRow[5] + '</td>'
			+ '</tr>';

		html = html + '</tbody></table>';
		var $this = this;
		TempDialog.show(html, boxId + ' ' + this.getText('file.scan.result.box.detail'), {
					width: 1000,
					buttons: {
						print: {
							text: this.getText("global.print"),
							handler: function() {
								$this.print(boxId); 
								this.hide(); 
							}
						}, 
						confirm: { 
							text: this.getText('global.close'), 
							handler: function() { this.hide(); } 
						}
					}
				}
		);
		
	},
	
	/**
	 * 显示详细的业务号信息
	 * @param documentId
	 */
	showDetail: function(documentId) {
		var details = this.data.details;
		var html = '<table class="list nowrap">'
			+ '<thead><tr>' 
			+ '<td width="5%">' + this.getText('file.scan.result.box.detail.page') + '</td>' 
			+ '<td width="5%">' + this.getText('file.count.page') + '</td>' 
			+ '<td width="15%">' + this.getText('file.no') + '</td>' 
			+ '<td width="30%">' + this.getText('file.type') + '</td>' 
			+ '<td width="15%">' + this.getText('file.document.no') + '</td>' 
			+ '<td>' + this.getText('global.policy.holder') + '</td>' 
			+ '</tr></thead>'
			+ '<tbody>';
		//var preRow, row;
		//var length = details.length - 1;
		var rowIndex = 0;
		var $this = this;
		details.each(function(detail, index){
			// fileBox.getId(), pageIndex(0开始), file.getPageCount(), file.getNo(), file.getFileType().getName(), entity.getNo(), entity.getApplicant()
			if (documentId != detail[5]) return;
			html = html + '<tr class="' + (rowIndex % 2 ? 'even' : 'odd') + '">'
				+ '<td class="number">' // + (detail[1] + 1)
				+ $this.getText(detail[2] == 1 ? 'file.scan.result.box.tip.one' : 'file.scan.result.box.tip', [ detail[1] + 1, detail[1] + detail[2] ])
				+ '</td>'
				+ '<td class="number">' + detail[2] + '</td>'
				+ '<td>' + detail[3] + '</td>'
				+ '<td>' + detail[4] + '</td>'
				+ '<td>' + detail[5] + '</td>'
				+ '<td>' + detail[6] + '</td>'
				+ '</tr>'; 
			rowIndex++;
		});

		html = html + '</tbody></table>';
		TempDialog.show(html, documentId + ' ' + this.getText('file.scan.result.box.detail'), 
				{ width: 1000, buttons: { confirm: { text: this.getText('global.close'), handler: function() { this.hide(); } } } });
	},
	/**
	 * 处理错误信息
	 * @param index 第index行
	 * @param method 处理方式
	 */
	handleError: function(index, method) {
		// var e = $('errorHandleStatus' + index);
		/*
		if (ERROR_HANDLE_METHODS['DELETE'] == method) {
			// 删除记录
			var result = true;
			this.handleErrorResult(index, result);
			return;
		}
		*/
		
		var result = [ ERROR_HANDLE_METHODS['NOT_HANDLE'], ERROR_HANDLE_METHODS['DELETE'], ERROR_HANDLE_METHODS['VOIDED_DOCUMENT'] ].include(method);
		this.handleErrorResult(index, result);
	},
	/**
	 * 处理错误信息的结果
	 * @param index 第index行
	 * @param result 处理结果
	 */
	handleErrorResult: function(index, result) {
		var e = $('errorHandle' + index);
		var method = e.value;
		var exceptionCode = e.getAttribute('exceptionCode');
		var documentId = e.getAttribute('documentId');	// 原始的单证号
		var visaNo = e.getAttribute('visaNo');	// 原始的单证号
		var documentIndex = e.getAttribute('documentIndex');
		var fileType = e.getAttribute('fileType');	// 原始的资料类型
		
		e = $('errorHandleStatus' + index);
		var errorHandleResult = $('errorHandleResult' + index);
		if (!result) {
			// 未处理
			e.addClassName('red');
			// e.addClassName('b');
			e.innerHTML = this.getText('file.scan.error.handle.status.0');
			e.setAttribute('value', '0');
			
			if (!method) {
				errorHandleResult.addClassName('hidden');
			} else {
				errorHandleResult.value = '';
				errorHandleResult.removeClassName('hidden');
			}
			// return;
		} else {
			var xpath = $('errorHandle' + index);
			xpath = xpath.getAttribute('xpath');
			var node = XMLHelper.selectNodes(this.xml, xpath)[0];
			var removeNode = node.getElementsByTagName('remove')[0];
			node.setAttribute('visa', 'false');	// 非作废单证
			node.setAttribute('visaNo', visaNo || '');
			switch (method) {
			case ERROR_HANDLE_METHODS['DELETE']: // 删除资料
				errorHandleResult.addClassName('hidden');
				if (!removeNode) {	// 添加一个<remove>true<remove>节点
					removeNode = this.xml.createElement("remove");
					XMLHelper.setNodeText(removeNode, 'true');
					node.appendChild(removeNode);
				}
				removeNode = null;
				break;
			case ERROR_HANDLE_METHODS['INPUT_DOCUMENT_NO']: // 补录保单号
				node.setAttribute('id', result);
				break;
			case ERROR_HANDLE_METHODS['VOIDED_DOCUMENT']:	// 置为作废单证
				node.setAttribute('id', result = documentId);
				errorHandleResult.addClassName('hidden');
			case ERROR_HANDLE_METHODS['INPUT_VOIDED_DOCUMENT']:	// 补录作废单证号
				node.setAttribute('id', result);
				node.setAttribute('visa', '002');	// 作废单证
				node.setAttribute('visaNo', result);	// 单证号
				break;
			case ERROR_HANDLE_METHODS['CHANGE_FILE_TYPE']: // 更改资料类型
				node.setAttribute('type', result);
				break;
			case ERROR_HANDLE_METHODS['NOT_HANDLE']:	// 不需要的资料类型，不进行处理,则保存识别出的资料
				node.setAttribute('type', fileType);	// 还原资料类型
				errorHandleResult.value = '';
				errorHandleResult.addClassName('hidden');
				break;
			}
			
			if (removeNode) removeNode.parentNode.removeChild(removeNode);
			e.removeClassName('red');
			// e.removeClassName('b');
			e.innerHTML = this.getText('file.scan.error.handle.status.1');
			e.setAttribute('value', '1');
			this.xmlString = XMLHelper.toString(this.xml);
		}
		if (EXCEPTION_CODES['DOCUMENT_NOT_IDENTIFIED'] == exceptionCode
				|| EXCEPTION_CODES['DOCUMENT_NOT_EXISTS'] == exceptionCode) {
			// 删除业务号,则在业务号下的其下的其他错误不需要处理
			// var e;
			var disabled = ERROR_HANDLE_METHODS['DELETE'] == method;
			for (var rowIndex = index + 1, l = this.data.errors.length; rowIndex < l; rowIndex++) {
				e = $('errorHandle' + rowIndex);
				if (!e) break;	// 已经到了最后一条记录
				if (e.getAttribute('documentIndex') != documentIndex) break;	// 不是同一个保单
				e.disabled = disabled;
				e = $('errorHandleResult' + rowIndex);
				e.disabled = disabled;
				

				e = $('errorHandleStatus' + rowIndex);
				if (disabled) {
					// 如果禁用此错误处理方式,则视为已处理错误
					if (!e.getAttribute('lastValue')) {
						e.setAttribute('lastValue', e.getAttribute('value'));
					}
					e.setAttribute('value', '1');
				} else {
					if (!e.getAttribute('lastValue')) continue;
					e.setAttribute('value', e.getAttribute('lastValue'));
					e.removeAttribute('lastValue');
				}
				
				if (e.getAttribute('value') == '1') {
					// 此行的错误信息已处理
					e.removeClassName('red');
					// e.removeClassName('b');
					e.innerHTML = this.getText('file.scan.error.handle.status.1');
				} else {
					// 此行的错误信息尚未处理
					e.addClassName('red');
					// e.addClassName('b');
					e.innerHTML = this.getText('file.scan.error.handle.status.0');
				}
			}
		}
	}
});

FileScanHandler.EXCEPTION_CODES = EXCEPTION_CODES;
FileScanHandler.DEFAULT_PERIOD = DEFAULT_PERIOD;
/** 扫描结果XML处理 */
var FileScanXML = {
	/**
	 * 
	 * 读取batchId的扫描结果
	 * 
	 * @param batchId 批次号
	 * @param options 可选参数
	 */
	read : function(batchId, options) {
		options = options || {};
		options['showLoading'] = false;	// 不显示Loading框
		var parameters = 'batchId=' + encodeURIComponent(batchId);
		if (!options['parameters']) options['parameters'] = parameters;
		else options['parameters'] = options['parameters'] + '&' + parameters;
		var url = WebServiceServers.getReadUrl();
		// alert(url);
		AjaxHelper.request(url, options);
	},

	/**
	 * 更新batchId的XML文本
	 * 
	 * @param batchId
	 * @param xml
	 * @param options
	 */
	update : function(batchId, xml, options) {
		options = options || {};
		var parameters = 'batchId=' + encodeURIComponent(batchId) + (xml ? '&xml=' + encodeURIComponent(xml) : '');
		if (!options['parameters']) options['parameters'] = parameters;
		else options['parameters'] = options['parameters'] + '&' + parameters;
		var url = WebServiceServers.getUpdateUrl();
		AjaxHelper.request(url, options);
	},

	/**
	 * 确认保存扫描结果,此方法与update一致,但是不需要回传xml对象
	 * 
	 * @param batchId
	 * @param options
	 * @returns
	 * @see this#update
	 */
	submit : function(batchId, options) {
		return this.update.apply(this, [ batchId, null, options ]);
	},

	/**
	 * 取消batchId这批的扫描结果,删除扫描文件
	 * 
	 * @param batchId
	 * @param options
	 */
	cancel : function(batchId, options) {
		options = options || {};
		var parameters = 'batchId=' + encodeURIComponent(batchId);
		if (!options['parameters']) options['parameters'] = parameters;
		else options['parameters'] = options['parameters'] + '&' + parameters;
		var url = WebServiceServers.getCancelUrl();
		// alert(url);
		AjaxHelper.request(url, options);
	}
};
})();
