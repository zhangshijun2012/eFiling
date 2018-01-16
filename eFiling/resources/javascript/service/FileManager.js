/** 单份档案管理 */
var FileManager = Service.create({
	namespace : 'file/manage/',
	/* 归档状态 */
	/** 00.未归档 */
	DOCUMENT_STATUS_UNFILE : "00",
	/** 01.归档不齐 */
	DOCUMENT_STATUS_LACK : "01",
	/** 11.归档齐全 */
	DOCUMENT_STATUS_FILE : "11",
	/** 10.特批归档齐全 */
	DOCUMENT_STATUS_FILE_MANUAL : "10",
	DOCUMENT_STATUS : {

	},
	selector : true,
	/*
	dataHeader : [ { html : "file.document.no", attributes : { style : { width : "10%" } } }, 
	               { html : "file.document.type", attributes : { style : { width : "5%" } } }, 
	               { html : "global.policy.holder", attributes : { style : { width : "8%" } } }, 
	               { html : "file.document.status",	attributes : { style : { width : "5%" } } }, 
	               { html : "file.document.lacks" }, 
	               { html : "file.document.salesman", attributes : { style : { width : "5%" } } },
	               { html : "file.document.business.no", attributes : {	style : { width : "5%" } } },
	               { html : "file.document.business.department", attributes : {	style : { width : "8%" } } }, 
	               { html : "file.document.business.user", attributes : { style : {	width : "8%" } } },
	               { html : "file.document.agent", attributes : { style : {	width : "8%" } } },
	               { html : "file.time", attributes : { style : { width : "5%" } } },
	               { html : "file.user", attributes : { style : { width : "5%" } } } ],
	dataHandler : [ null, function(cell, value, index, values, data) {
		// 显示单证类型的文字描述
		cell.innerHTML = this.getText('file.document.type.' + value);
	}, null, function(cell, value, index, values, data) {
		// 归档状态
		cell.innerHTML = this.getText('file.document.status.' + value);
	} ],*/
	
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
		this.urls['index'] = 'index.do';
		this.html['mainNav'] = '';
		this.html['mainFooter'] = true;

		this.uploader = { };
		// property01字段用于保存DOCUMENT_FILE表的fileModle,1表示核保资料,0表示影像文件
		this.uploader['upload'] = 'onSuccess='
				+ encodeURIComponent('parent.Main.service.uploadHandler(json, xhq, success);')
				+ '&handlerClass=eFiling&entity.systemCode=' + SYSTEM_CODE
				+ '&property01=1&operator=' + Main.user.id;
		
		// 特批归档附件
		this.uploader['uploadSave'] = 'onSuccess='
				+ encodeURIComponent('parent.Main.service.saveManualFile(json, xhq, success);')
				+ '&show=false&systemCode=' + SYSTEM_CODE + '&fileTitle=' + encodeURIComponent('特批归档附件')
				+ '&property00=MANUAL&property01=2&operator=' + Main.user.id;
		
	},
	onCheckAll : function(checked) {
		var form = $(this.forms['query']);
		form.elements['shareButton'].disabled = !checked;
	},
	onCheck : function(e, checked) {
		var form = $(this.forms['query']);
		form.elements['shareButton'].disabled = this.selected.length <= 0;
	},
	checkNumber : [],//选中共享的档案资料的份数
	//批量共享,单选择档案资料的时候,复选框所触发的事件
	shareCheck : function(e, checked) {
		if (checked) this.checkNumber.push(e.value);
		else this.checkNumber.pop(e.value);
		if (this.checkNumber.length > 0) $('shareComfirmButton').disabled = false;
		else $('shareComfirmButton').disabled = true;
		 
	},
	//批量共享,全选的时候,复选框所触发的事件
	shareCheckAll : function(checked) {
		//得到所有档案资料的id
		var shareIds = $('shareForm').getInputs('checkbox', 'shareIds');
		for (var i = 0; i < shareIds.length; i++) {
			if (checked) {
				shareIds[i].checked = true;
				this.checkNumber.push(shareIds[i].value);	
			} else {
				shareIds[i].checked = false;
				this.checkNumber.pop(shareIds[i].value);	
			}
		}
		if (this.checkNumber.length > 0) $('shareComfirmButton').disabled = false;
		else $('shareComfirmButton').disabled = true;
	},
	documentId : null, // 当前操作的业务号
	documentFileId : null, // 当前操作的文档
	fileTypeId : null, // 当前操作的资料类型
	receipts: [],	// 所有收到的资料
	privateView: function(id, options) {
		this.documentId = id;
		var $this = this;
		var _onSuccess = options ? options['onSuccess'] : null;
		options = Object.extend({
			width : 900,
			buttons : {
				scan : {
					text : this.getText('file.scan'),
					handler : function() {
						$this.viewScanDialog();
					}
				},
				upload : {
					text : this.getText('file.upload'),
					handler : function() {
						$this.viewUploadDialog();
					}
				},
				comfirm : {
					text : this.getText('file.type.document'),
					handler : function() {
						$this.viewPaperDialog();
					}
				},
				printReceipts : {
					text : this.getText('打印签收条'),
					attributes: { className: 'button6' },
					handler : function() {
						$this.showReceipts();
					}
				},
				manual : {
					text : this.getText('file.scan.manual'),
					handler : function() {
						$this.manual();
					}
				},
				close : {
					text : this.getText('close'),
					handler : function() {
						this.hide();
					}
				}
			}
		}, options || { });


		options['onSuccess'] = (function() {
			var status = this.form.elements['fileStatus'].value;
			// alert(status);
			if (![ this.DOCUMENT_STATUS_UNFILE, this.DOCUMENT_STATUS_LACK ].include(status)) {
				// 未归档、差缺的文档才允许特批归档
				this.dialog.buttons['manual']['element'].disabled = true;
			}
			
//			var tbody = $('viewDialogFilesTbody');
//			var count = 0;
//			$A(tbody.rows).each(function(row){
//				if (Boolean.parseBoolean(row.getAttribute('handleStatus'))) count++;
//			});
			
//			if (false && count < 1) {
//				// 没有任何有效文件，则不允许进行扫描上传操作。
//				this.dialog.buttons['scan']['element'].disabled = true;
//				this.dialog.buttons['upload']['element'].disabled = true;
//			}
			
			if (_onSuccess) _onSuccess.apply(this, arguments);
			
			if (!this.receipts || this.receipts.length < 1) {
				this.dialog.buttons['printReceipts']['element'].disabled = true;
			}
		}).bind(this);
		this['super']['view'].apply(this, [ id, options ]);
	},
	
	view : function(id) {
		this.privateView(id, {
			onSuccess : function() {
				this.openedScanDialog = null;
				this.openedUploadDialog = null;
				this.openedPaperDialog = null;				 
				this.receipts = [];
			}
		});
	},
	/**
	 * 打开扫描或上传的处理窗口
	 * @param scan {Boolean}是否为扫描窗口
	 * @param options
	 * @returns
	 */
	openHandleDialog: function(scan, options) {
		var dialog = new Dialog(this.namespace + (scan ? 'opendScan' : 'opendUpload') + 'Dialog');
		var form = this.forms['view'];
		// var tbodyId = 'tbody' + Math.uuid();
		var randId = Math.uuid();
		var html = '<form name="handleForm" method="post">'
				+ '<input type="hidden" name="id" value="' + form.elements['id'].value + '" />'
				+ '<input type="hidden" name="riskType" value="' + form.elements['riskType'].value + '" />'
				// + '<p>' + this.getText('file.document.no') + ': ' + form.elements['no'].value + '</p>'
				// + '<p>' + this.getText('global.policy.holder') + ': ' + form.elements['applicant'].value + '</p>'
				+ '<p>' + this.getText('file.list') 
				+ ' <a href="#" onclick="Main.service.appendFileType(' + scan + ')">增 加</a>'
				+ '</p>'
				+ '<table class="list nowrap">'
				+ '	<thead>'
				+ '		<tr>'
				+ '			<th>' + this.getText('file.type') + '</th>'
				+ '			<th width="35%">' + this.getText('file.no') + '</th>'
				+ '			<th width="15%">' + this.getText('global.operate') + '</th>'
				+ '		</tr>'
				+ '	</thead>'
				+ '	<tbody class="existsDocumentFilesTbody">';
		var tbody = $('viewDialogFilesTbody');
		if (scan) scan = '<input type="button" value="扫描" onclick="Main.service.scan(\'${ id }\', \'${ fileType.id }\')" class="button" />';
		else scan = '<input type="button" value="上传" onclick="Main.service.upload(\'${ id }\', \'${ fileType.id }\')" class="button" />';
		var rowCount = 0;
		var exists = { };
		var fileTypeId;
		$A(tbody.rows).each(function(row, index){
			if (!Boolean.parseBoolean(row.getAttribute('handleStatus'))) return;	// 禁用的文件不允许处理
			var cells = row.cells;
			var id = row.getAttribute('fileId');
			fileTypeId = row.getAttribute('fileTypeId');
			rowCount++;
			html += '<tr id="file_' + id + '" ' + (rowCount % 2 == 0 ? 'class="even"' : '') 
				+ ' documentFileId="' + id + '" fileTypeId="' + fileTypeId + '">'
				+ '<td>' + cells[0].innerHTML + '</td>'
				+ '<td>' + cells[1].innerHTML + '</td>'
				+ '<td>' + scan.replace('${ id }', id).replace('${ fileType.id }', fileTypeId) + '</td>'
				+ '</tr>';
			exists[fileTypeId] = true;
		});
		html += '</tbody><tbody class="appendDocumentFileTbody hidden">';
		html += '<tr id="file_append_' + randId  + '" ' + (rowCount % 2 == 0 ? 'class="even"' : '') + '>'
			+ '<td>'
			+ '<select name="appendFileType' + randId  + '" id="appendFileType' + randId  + '">'
			+ '	<option value="">请选择其他资料...</option>';
		FileType['fileTypes'].each(function(fileType) {
			if (fileType['fileModel'] && fileType['fileModel'] != '1') return;	// 仅显示核保资料
			var fileTypeId = fileType['id'];
			if (exists[fileTypeId]) return;
			html = html + '<option value="' + fileTypeId + '">' + fileType['name'] + '</option>';
		});
		html = html + '</select>'
			+ '</td>'
			+ '<td> </td>'
			+ '<td>' + scan.replace('${ id }', '').replace('\'${ fileType.id }\'', '$(\'appendFileType' + randId  + '\').value') + '</td>'
			+ '</tr>';
		html += '</tbody>';
		html += '<form>';
		options = options || { };
		this.receiptsCount = this.receipts.length;
		options = Object.extend({
			width : 800,
			onHide: (function() {
				if (this.receiptsCount < this.receipts.length) {
					// 如果有新上传的文件,则在关闭操作窗口后重新查看
					this.privateView(this.documentId);
				}
			}).bind(this),
			buttons : { 
				confirm: {
					text: this.getText('global.confirm'),
					handler: function() { this.hide(); }
				}
			}
		}, options);
		dialog.show(html, options['title'] || '', options);

		if (scan) this.appendFileTypeScan = false;
		else this.appendFileTypeUpload = false;
			
		return dialog;
	},
	openPaperHandleDialog: function(options) {
		//var	dialog = new Dialog(this.namespace + 'opendPaperDialog');
		var form = this.forms['view'];
		var scan = "";
		var randId = Math.uuid();
		randId = randId.toLowerCase().replaceAll("-", "");
		var html = '<form name="viewPaperForm" method="post">'
				+ '<input type="hidden" name="id" value="' + form.elements['id'].value + '" />'
				+ '<input type="hidden" name="riskType" value="' + form.elements['riskType'].value + '" />'
				+ '<p>' + this.getText('file.list') 
				+ ' <a href="#" onclick="Main.service.appendPaperFileType(' + scan + ')">增 加</a>'
				+ '</p>'
				+ '<table class="list nowrap">'
				+ '	<thead>'
				+ '		<tr>'
				+ '			<th><input type="checkbox" onclick="Main.service.checkPaperAllDetails(this.checked)" name="checkPaperAllDetails" /></th>'
				+ '			<th>' + this.getText('file.type') + '</th>'
				+ '			<th width="35%">' + this.getText('file.no') + '</th>'
				+ '		</tr>'
				+ '	</thead>'
				+ '	<tbody class="existsDocumentFilesTbody">';
		var tbody = $('viewDialogFilesTbody');
		var rowCount = 0;
		var exists = { };
		var fileTypeId;
		var no;
		var documentStatus;
		var fileStatus;
		$A(tbody.rows).each(function(row, index) {
			if (!Boolean.parseBoolean(row.getAttribute('handleStatus'))) return;	// 禁用的文件不允许处理
			var cells = row.cells;
			var id = row.getAttribute('fileId');
			fileTypeId = row.getAttribute('fileTypeId');
			no = row.getAttribute('no');
			documentStatus = row.getAttribute('documentStatus');
			fileStatus = row.getAttribute('fileStatus');
			rowCount++;
			html += '<tr id="file_' + id + '" ' + (rowCount % 2 == 0 ? 'class="even"' : '') 
				+ ' documentFileId="' + id + '" fileTypeId="' + fileTypeId + '">'
				+ '<td><input type="checkbox" no="'+ no + '" documentStatus="' + documentStatus + '" fileStatus = "' + fileStatus + '" onclick="Main.service.checkPaperDetail(this.checked)" id="ids" name="ids" value="' + id + '"/></td>'
				+ '<td>' + cells[0].innerHTML + '</td>'
				+ '<td>' + cells[1].innerHTML + '</td>'
				+ '</tr>';
			exists[fileTypeId] = true;
		});
		html += '</tbody><tbody class="appendDocumentFileTbody hidden">';
		html += '<tr id="file_append_' + randId  + '" ' + (rowCount % 2 == 0 ? 'class="even"' : '') + '>'
			 + '<td><input type="checkbox" onclick="Main.service.checkPaperDetail(this.checked)" id="ids" name="ids"/></td>'
			 + '<td>'
			 + '<select name="ids" id="ids">'
			 + '	<option value="">请选择其他资料...</option>';
		FileType['fileTypes'].each(function(fileType) {
			if (fileType['fileModel'] && fileType['fileModel'] != '1') return;	// 仅显示核保资料
			var fileTypeId = fileType['id'];
			if (exists[fileTypeId]) return;
			html = html + '<option value="' + fileTypeId + '">' + fileType['name'] + '</option>';
		});
		html = html + '</select>'
			+ '</td>'
			+ '<td> </td>'
			//+ '<td>' + scan.replace('${ id }', '').replace('\'${ fileType.id }\'', '$(\'appendFileType' + randId  + '\').value') + '</td>'
			+ '</tr>';
		html += '</tbody>';
		html += '</form>';
		options = options || { };
		options = Object.extend({
			width : 800,
			buttons : { 
				send: {
					text: this.getText('file.type.document'),
					disabled: true,
					id: 'collectDetailButton',
					handler: (function() {
						this.collectDetail();
					}).bind(this)
				},
				confirm: {
					text: this.getText('global.confirm'),
					handler: function() { 
						this.hide(); 
					}
				}
			}
		}, options);
		
		//TempDialog
		//dialog.show(html, options['title'] || '', options);
		//return dialog;
 		return TempDialog.show(html, this.getText('纸质档案手工处理'), options);
	},
	/**
	 *归档操作明细
	 */
	collectDetail: function() {
		var form = $('viewPaperForm');
		var parameters = "&" + form.serialize();
		if (parameters && parameters.endsWith("=")) {
			alert('请先处理已添加的资料类型!');
			return;
		}
		var options = {
			parameters: parameters,
			onSuccess: (function(response) {
				var message = response.responseText.toJSON();
				if(message['success']) alert(message['message']);
				this.openedPaperDialog.hide();
				this.dialog.hide();
				this.query();
				this.appendFileTypeDocument = false;
			}).bind(this)
		};
		this.request(this.getUrl('collectDetail'), options);
	},
	/**
	 * 选中所有明细
	 * @param checked
	 */
	checkPaperAllDetails: function(checked) {
		var disabled = false; 
		var form = $('viewPaperForm');
		var checkBoxes = form.getInputs("checkbox", 'ids');
		var count = 0;
		var no = "";	 //档案编码
		var documentStatus = ""; //档案状态
		var fileStatus = "";
		for (var i = 0; i < checkBoxes.length; i++) {
			no = checkBoxes[i].getAttribute("no");
			documentStatus = checkBoxes[i].getAttribute("documentStatus");
			fileStatus = checkBoxes[i].getAttribute("fileStatus");
			if (documentStatus == 'D') continue; 
			checkBoxes[i].checked = checked;
		}
		$('collectDetailButton').disabled = !checked;
	},
	/**
	 * 选中一个明细
	 * @param field
	 * @param checked
	 */
	checkPaperDetail: function(checked) {
		var isChecked = false; 
		var form = $('viewPaperForm');
		var checkBoxes = form.getInputs("checkbox", 'ids');
		for (var i = 0; i < checkBoxes.length; i++) {
			if (checkBoxes[i].checked) {
				isChecked = true;
				break;
			}
		}
		$('collectDetailButton').disabled = !isChecked;
	},
	appendFileTypeScan: false,
	appendFileTypeUpload: false,
	/** 扫描上传时增加一行资料类型 */
	appendFileType: function(scan) {
		if (scan ? this.appendFileTypeScan : this.appendFileTypeUpload) {
			alert('请先处理已添加的资料类型!');
			return;
		}
		var dialog = (scan ? this.openedScanDialog : this.openedUploadDialog);
		var container = dialog.body;
		var tbody = $$(container, 'tbody.appendDocumentFileTbody')[0];
		Element.show(tbody);
		if (scan) this.appendFileTypeScan = true;
		else this.appendFileTypeUpload = true;
		var select = $$(tbody, 'select')[0];
		select.selectedIndex = 0;	// 选中第一行
		dialog.repaint();
	},
	appendFileTypeDocument: false,
	/** 扫描上传时增加一行资料类型 */
	appendPaperFileType: function() {
		if (this.appendFileTypeDocument) {
			alert('请先处理已添加的资料类型!');
			return;
		}
		var dialog = this.openedPaperDialog;
		var container = dialog.body;
		var tbody = $$(container, 'tbody.appendDocumentFileTbody')[0];
		Element.show(tbody);
		this.appendFileTypeDocument = true;
		var select = $$(tbody, 'select')[0];
		select.selectedIndex = 0;	// 选中第一行
		dialog.repaint();
	},
	/** 打开扫描窗口 */
	viewScanDialog: function() {
		if (this.openedScanDialog) {
			this.openedScanDialog.show();
			this.openedScanDialog.repaint();
			this.receiptsCount = this.receipts.length;
			return;
		}

		this.openedScanDialog = this.openHandleDialog(true, { title: '单份文件扫描' });
	},
	/** 调用档案扫描功能 */
	validateScan : function() {
		var form = this.forms['scan'] = $($(this.openedScanDialog.body).select('form')[0]);
		var risk = form.elements["riskType"];
		this.riskType = risk.value;
		return true;
	},
	/** 启动扫描 */
	scan : function(id, fileTypeId, options) {
		if (!fileTypeId) return;

		if (!id) {
			// 可能是点击的新增资料
			var container = this.openedScanDialog.body;
			var tbody = $$(container, 'tbody.existsDocumentFilesTbody')[0];
			var row = $$(tbody, 'tr[fileTypeId=' + fileTypeId + ']');
			if (row && row[0]) id = row[0].getAttribute('documentFileId');
		}
		
		this.documentFileId = id;
		this.fileTypeId = fileTypeId;
		this.stopScan();
		var $this = this;
		this.options = options = Object.extend({
			afterComplete : function(data, html) {
				var options = {
					width : 800,
					// 'hide': (function() { this.view(this.documentId); }).bind(this),
					buttons : {
						cancel: {
							text: this.getText('global.close'),
							handler: function() { 
								this.hide(); 
							}
						}
					}
				};

				$this.saveFileHandler(true, data);
				
				// 显示扫描结果
				TempDialog.show(html, $this.getText('file.scan.list'), options);
				
				// 提醒是否打印签收条
				// $this.showReceipts();
				
				$this.addReceiptById();
			}
		}, options || {});
		this.batchId = options['batchId'];
		if (!this.batchId) this.batchId = Math.uuid();
		// CE6C8F36-BCA7-43E1-88C8-95CB625C654F
		// 1C8E4F40-3C86-490A-83BA-1F9A73480EF0
		// 7F7F7EEA-3062-4EEA-BBFD-8F72A89EB03E
		// this.batchId = Math.uuid();//'7F7F7EEA-3062-4EEA-BBFD-8F72A89EB03E';
		// this.batchId = '9CBA8588-27B7-485E-8643-AC7BE4BE172D';
		this.scanParameters = 'riskType=' + this.riskType; // 险种,如果是单份扫描,还需要业务号和资料类型

		// http://10.132.21.29/tmweb.net/default.aspx?userId=nasay&appName=LibertySingle&xmlId=3242423523532&serviceNum=11231&lbPageType=123123
		var url = DataCapServers.get('appName=LibertySingle&xmlId=' + this.batchId + '&serviceNum=' + this.documentId
				+ '&lbPageType=' + fileTypeId);
		// alert(url);
		var html = '<div class="txtCenter">本次扫描没有完成，请您重新扫描……</div>';
		this.scanningDialog = TempDialog.show(html, '', {
			width : 350,
			hideable : false,
			/* header: false, */
			buttons : {
				confirm: {
					text: this.getText('file.scan.cancel'), 
					handler: function() {
						$this.cancelScan();
					}
				}
			}
		});

		var top = NumberHelper.intValue(this.scanningDialog.dialog.style.top);
		var left = NumberHelper.intValue(this.scanningDialog.dialog.style.left);
		this.scanWindow = window.open(url, 'scanWindow', 'width=400, height=250, top=' + (top - 50) + ', left=' + (left - 30) + '');
		this.handler = new FileScanHandler(this.batchId, this.options, this);
		this.handler.read(); // 开始读取处理结果,true表示延迟默认的间隔时间后调用,也可以为数字
	},

	/** 停止扫描,取消定时器 */
	stopScan : function() {
		if (this.handler) this.handler.stop();
		if (this.scanWindow) this.scanWindow.close();
		this.scanWindow = null;
		if (this.scanningDialog) this.scanningDialog.hide();
		this.scanningDialog = null;
	},
	
	/** 取消扫描 */
	cancelScan: function() {
		this.stopScan();
	},

	/** 扫描完成 */
	completeScan : function() {
		if (this.scanningDialog) this.scanningDialog.hide();
		this.scanningDialog = null;
		if (this.scanWindow) this.scanWindow.close();
		this.scanWindow = null;

		// 为xml的file节点加上<another></another>
		var node = XMLHelper.selectNodes(this.handler.root, '//document/file')[0];
		var anotherNode = node.getElementsByTagName('another')[0];
		if (!anotherNode) {
			anotherNode = this.handler.xml.createElement("another");
			node.appendChild(anotherNode);
		}
		XMLHelper.setNodeText(anotherNode, this.documentFileId);
		anotherNode = null;
		this.handler.xmlString = XMLHelper.toString(this.handler.xml);
	},

	/** 打开归档窗口 */
	viewPaperDialog: function() {
	/*	if (this.openedPaperDialog) {
			this.openedPaperDialog.show();
			this.openedPaperDialog.repaint();
			return;
		}*/
		this.openedPaperDialog = this.openPaperHandleDialog({ title: '纸质档案手工处理' });
	},
	
	/** 打开扫描窗口 */
	viewUploadDialog: function() {
		if (this.openedUploadDialog) {
			this.openedUploadDialog.show();
			this.openedUploadDialog.repaint();
			this.receiptsCount = this.receipts.length;
			return;
		}

		this.openedUploadDialog = this.openHandleDialog(false, { title: '单份文件上传' });
	},

	
	/**
	 * 打开文件上传框
	 * 
	 * @param parameters
	 */
	openUploader: function(parameters) {
		this.uploaderDialog = Uploader.dialog(parameters);
	},
	
	/** 上传单份文件 */
	upload : function(id, fileTypeId, options) {
		if (!fileTypeId) return;
		if (!id) {
			// 可能是点击的新增资料
			var container = this.openedUploadDialog.body;
			var tbody = $$(container, 'tbody.existsDocumentFilesTbody')[0];
			var row = $$(tbody, 'tr[fileTypeId=' + fileTypeId + ']');
			if (row && row[0]) id = row[0].getAttribute('documentFileId');
		}
		this.documentFileId = id;
		this.fileTypeId = fileTypeId;
		this.options = options = options || {};
		this.batchId = options['batchId'];
		if (!this.batchId) this.batchId = Math.uuid();
		var f = FileType.all[fileTypeId];
		
		var parameters = this.uploader['upload'] + '&batch=' + this.batchId + '&entity.fileTitle='
				+ encodeURIComponent(f['name']) + '&entity.businessNo=' + this.documentId
				+ '&entity.property00=' + this.fileTypeId
				+ '&entity.property02=' + this.documentFileId;
		this.openUploader(parameters);
	},

	/** 文件上传完成 */
	uploadHandler : function(data, xhq, success) {
		// alert(data + ' ' + text);
		if (!data) {
			// 失败
			data = {
				'message' : xhq.responseText
			};
		}
		if (data['message']) alert(data['message']);
		if (!data['success']) return;

		data = data['list'][0];
		var xml = '<?xml version="1.0" ?><root>';
		xml = xml + '<document id="' + this.documentId + '">' + '	<file type="' + this.fileTypeId + '">'
				+ '		<another>' + this.documentFileId + '</another>' + '		<id>' + data['id'] + '</id>' + '		<name>'
				+ data['fileName'] + '</name>' + '		<count>' + (data['pageCount'] || 1) + '</count>' + '		<size>'
				+ data['fileSize'] + '</size>' + '	</file>' + '</document>' + '</root>';
		// alert(xml);
		var $this = this;
		// alert(this.getUrl('saveForUpload'));
		this.request(this.getUrl('saveForUpload'), {
			parameters : 'xml=' + encodeURIComponent(xml),
			onSuccess : function(response) {
				// alert(response.responseText);
				var json = response.responseText.toJSON();
				$this.saveFileHandler(false, json);
				// $this.view($this.documentId);
				$this.uploaderDialog.close();
				// var options = { 'hide':  (function() { this.view(this.documentId); }).bind($this) };
				
				// 提醒是否打印签收条
				// $this.showReceipts(options);
				$this.addReceiptById();
			}
		});
	},
	
	/**
	 * 保存完上传、扫描的文件后的回调函数
	 * @param scan 是否为扫描操作
	 * @param data 回调函数得到的ajax数据
	 */
	saveFileHandler: function(scan, data) {
		if (!data || !data.files) return;
		var dialog = (scan ? this.openedScanDialog : this.openedUploadDialog);
		var container = dialog.body;
		var tbody = $$(container, 'tbody.existsDocumentFilesTbody')[0];
		rowCount = tbody.rows.length;
		var inserted = false;	// 是否有新增行
		data.files.each(function(file, index) {
			var docFileId = file['id'];
			var fileTypeId = file['fileType']['id'];
			var row = $$(tbody, 'tr[fileTypeId=' + fileTypeId + ']');
			if (row) row = row[0];
			var cell;
			var fileNo = file['file']['no'] || '';
			if (row) {	// 已存在的行
				row.id = 'file_' + docFileId;
				row.setAttribute('documentFileId', docFileId);
				cell = row.cells[1];
				if (fileNo) cell.innerHTML = fileNo;
				cell = row.cells[2];
			} else {	// 新建一行
				rowCount++;
				var row = tbody.insertRow();
				row.id = 'file_' + docFileId;
				row.setAttribute('documentFileId', docFileId);
				row.setAttribute('fileTypeId', fileTypeId);
				if (rowCount % 2 == 0) Element.addClassName(row, 'even');
				cell = row.insertCell();
				cell.innerHTML = file['fileType']['name']; 
				cell = row.insertCell();
				cell.innerHTML = fileNo;
				cell = row.insertCell();
				inserted = true;
			}
			if (scan) {
				// 扫描操作
				cell.innerHTML = '<input type="button" value="扫描" onclick="Main.service.scan(\'' + docFileId
					+ '\', \'' + fileTypeId + '\')" class="button" />';
			} else {
				// 上传操作
				cell.innerHTML = '<input type="button" value="上传" onclick="Main.service.upload(\'' + docFileId
					+ '\', \'' + fileTypeId + '\')" class="button" />';
			}
		});
		if (inserted) {	// 有新增的行
			var tbody = $$(container, 'tbody.appendDocumentFileTbody')[0];
			$A(tbody.rows).each(function(row, index) {
				rowCount++;
				if (rowCount % 2 == 0) Element.addClassName(row, 'even');
				else  Element.removeClassName(row, 'even');
			});

			if (scan) this.appendFileTypeScan = false;
			else this.appendFileTypeUpload = false;
			Element.hide(tbody);
			
			dialog.repaint();
		}
	},
	/** 根据文件类型的id添加一个签收文件 */
	addReceiptById: function(fileTypeId) {
		var f = FileType.all[fileTypeId || this.fileTypeId];
		if (!f) return;
		var receipt = f['name'];
		// if (!this.receipts.include(receipt)) 
		this.receipts.push(receipt);
	},
	/** 显示签收条 */
	showReceipts: function(options) {
		// var f = FileType.all[this.fileTypeId];
		var form = this.forms['view'];
		var dueTime = form.elements['dueTime'].value.trim();
		var html = '<div><img src="' + Base.template + 'images/print_logo.png" /></div>';
		html += '<div style="padding: 20px 50px 0 50px;">';
		html += '<p>基本信息</p><hr>';
		html += '<div><table class="list">'
				+ '<tbody class="whitebg">'
				+ '<tr>'
				+ '<td width="15%">' + this.getText('file.document.no') + '</td>'
				+ '<td width="35%">' + this.documentId + '</td>'
				+ '<td width="15%">' + this.getText('global.policy.holder') + '</td>'
				+ '<td width="35%">' + form.elements['applicant'].value + '</td>'
				+ '<tr>'
				+ '<tr>'
				+ '<td>' + this.getText('保险期限') + '</td>'
				+ '<td>' + form.elements['effectiveTime'].value	+ (dueTime ? ' - ' : '') + dueTime + '</td>'
				+ '<td>' + this.getText('被保险人') + '</td>'
				+ '<td>' + form.elements['insured'].value + '</td>'
				+ '<tr>'
				+ '</tbody>'
				+ '</table></div>'
				+ '<p>&nbsp;</p>';
		html += '<p>提交的单证</p><hr>';
		html += '<div><table class="list">'
			+ '<tbody class="whitebg">'
			+ '<tr>'
			+ '<td width="40%">文档</td><td width="30%">	状态</td><td width="30%">接收日期</td>'
			+ '</tr>';
		var today = DateHelper.format(new Date());
		this.receipts.each(function(receipt) {
			html += '<tr>'
				+ '<td>' + receipt + '</td>'
				+ '<td>已收到</td>'
				+ '<td>' + today + '</td>'
				+ '<tr>';
		});
		html += '</tbody>';
		html += '</table></div>';
		html += '<div style="padding: 30px 30px 0 30px;">'
			+ '<div class="floatLeft">接收人 ' + Main.user['name'] + '</div>'
			+ '<div class="floatRight">日期 ' +today + '</div>'
			+ '<div>';
		html += '</div>';
		html = '<div id="printBody">' + html + '</div>';

		var _options = Object.extend({
			width : 800,
			minHeight : 220,
			buttons : {
				print : {
					text : this.getText('打 印'),
					handler : function() {
						Main.print(html);
						this.hide();
					}
				},
				cancel : {
					text : this.getText('global.cancel'),
					handler : function() {
						this.hide();
					}
				}
			}
		}, options || { });
		this.receiptDialog = TempDialogManager.show(html, this.getText('file.receipt.tip'), _options);
		// alert(this.receiptDialog.listeners['hide']==this.dialog.listeners['hide']);
	},

	manualFilesCount: 0,
	
	/** 显示特批文件上传框 */
	showUploader: function() {
		var parameters = this.uploader['uploadSave'] + '&businessNo=' + this.documentId;
		this.openUploader(parameters);
	},
	
	/** 保存特批归档的附件上传结果 */
	saveManualFile: function(data, xhq, success) {
		// alert( xhq.responseText );
		if (!data) {
			// 失败
			data = { 'message': xhq.responseText };
		}
		if (data['message']) alert(data['message']);
		if (!data['success']) return;
		var files = data['list'];		
		var xml = '<?xml version="1.0" ?><root>'
			+ '<document id="' + this.documentId + '">';
		for (var i = 0, l = files.length; i < l; i++) {
			data = files[i];
			xml = xml
				+ '	<file type="MANUAL">'
				+ '		<id>' + data['id'] + '</id>'
				+ '		<name>' + data['fileName'] + '</name>'
				+ '		<count>' + (data['pageCount'] || 1) + '</count>'
				+ '		<size>' + data['fileSize'] + '</size>'
				+ '	</file>';
		}
		xml = xml + '</document>' + '</root>';
		var parameters = "xml=" + encodeURIComponent(xml);
		var $this = this;
		var onSuccess = (function(response) {
			var json = response.responseText.toJSON();
			alert(json['message']);
			if (json['success']) {
				$this.uploaderDialog.close();
				$this.manualFilesCount = 1;
			}
			 $('manualReason').focus();
		}).bind(this);
		this.send(SERVER_ROOT + 'file/image/save.do', {
			parameters: parameters,
			onSuccess: onSuccess
		});
	},
	/** 特批归档 */
	manual : function() {
		this.manualFilesCount = 0;
		var parameters = Form.serialize($(this.forms['view']));
		var html = "<form name='manualForm' id='manualForm'>" + "<p>" + this.getText("file.scan.manual.tooltip") + ":"
				//+ "<input type='button' onclick='Main.service.showUploader()' value='" + this.getText('global.attachment.upload') + "' class='button'>"
				+ "</p>" + "<p>" + "<textarea name='manualReason' rows='6' style=\"width: 350px\"></textarea>" + "</p>" + 
				  "<p id='uploaderContainer'></p>" + 
				"</form>";
		this.manualTempDialog = TempDialogManager.show(html, this.getText('file.scan.manual.tooltip'), {
			width : 400,
			buttons : {
				upload : {
					text : this.getText('global.attachment.upload'),
					handler : (function() {
						//this.manualSave(parameters);
						Main.service.showUploader();
					}).bind(this)
				},
				confirm : {
					text : this.getText('global.confirm'),
					handler : (function() {
						this.manualSave(parameters);
					}).bind(this)
				},
				cancel : {
					text : this.getText('global.cancel'),
					handler : function() {
						this.hide();
					}
				}
			}
		});
	},
	/** 保存特批归档 */
	manualSave : function(ids) { 
		if (!this.manualFilesCount) {
			alert("请先上传特批归档附件!");
			return;
		}
		var manualReason = $('manualReason').value;
		if (!manualReason) {
			alert("请输入特批归档原因!");
			return;
		}
		var parameters = Form.serialize($('manualForm')) + "&" + ids;
		this.send(this.getUrl('manual'), {
			parameters : parameters,
			onSuccess : (function(response) {
				var json = response.responseText.toJSON();
				if (json) alert(json['message']);
				this.manualTempDialog.hide();
				this.dialog.hide();
				this.query();
			}).bind(this)

		});
	},
	
	/** 文件共享时，通过业务号查询单证所对应的档案文件 */
	shareView : function() {
		var no = $('shareForm').elements['no'].value.trim();
		if (!no) {
			alert('请输入业务号!');
			return;
		}
		this.requestUpdate($('shareDetailsContainer'), this.getUrl('shareView'), {
			parameters : 'no=' + no
		});
	},
	/** 文件共享 */
	share : function() {
		var ids = [];
	/*	this.selected.each(function(v) {
			ids.push(v[0]);
		});*/
		//var parameters = ids; // $A(arguments);
		var html = "<form name='shareForm' id='shareForm'>" + "<p><b>" + this.getText('file.share.document.no') + "</b>：";
		this.selected.each(function(v, index) {
			ids.push(v[0]);
			html = html + ((index > 0) && (index % 5 != 0) ? ',' : '<br>') + v[1] + '<input type="hidden" name="documentIds" value="' + v[0] + '"/>';
		});
		html = html + "</p>" + "<p><hr/></p>" + "<p>" + this.getText('file.share.list') + "：" + "</p>" + "<p>"
				+ this.getText('file.document.no') + "： <input size='25' name='no' />"
				+ "<input type='button' onclick='Main.service.shareView()' class='button' value='"
				+ this.getText('global.query') + "' />" + "</p>" + '<div id="shareDetailsContainer"></div>';
		this.dialog = this.dialogs['share'] = Dialog.show(html, this.getText('file.share'), {
			width : 800,
			height : 500,
			buttons : {
				confirm : {
					text : this.getText('global.confirm'),
					disabled : true,
					id : "shareComfirmButton",
					handler : (function() {
						this.shareComfirm();
					}).bind(this)
				},
				cancel : {
					text : this.getText('global.cancel'),
					handler : function() {
						this.hide();
					}
				}
			}
		});
	},
	/** 确定是所勾选的文件是否被共享 */
	shareComfirm : function() {
		if (!confirm("是否确认共享所选的档案资料?")) return;
		var parameters = Form.serialize($('shareForm'));
		this.send(this.getUrl('shareSave'), {
			parameters : parameters,
			onSuccess : (function(response) {
				var text = response.responseText;
				if (text) text = text.toJSON();
				if (text['success']) {
					// 此次业务号分享成功
					var amount = text['amount'];
					var message = "你共享了" + text[0];
					for ( var i = 1; i < amount; i++) {
						message += "," + text[i];
					}
					alert(message);
				}
				//销毁对话框
				this.dialog.hide();
				this.query();
			}).bind(this)

		});
	}
});