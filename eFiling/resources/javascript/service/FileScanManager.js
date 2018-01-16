/** 批量档案管理 */
var FileScanManager = Service.create({
	/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
	namespace : 'file/scan/',
	batchId : null, // ECM产生的唯一码,扫描批次号
	handler : null, // FileScanHandler.js
	RISK_TYPE_MOTOR : '0', // 车险
	RISK_TYPE_NON_MOTOR : '1', // 非车险
	initialize : function() {
		// this.html['mainHeader'] = ' ';
		this.html['mainHeader'] = '<div class="txtCenter marginTop">' + this.getText('file.scan.risk') + '：'
				+ '<input type="button" class="button" value="' + this.getText('file.scan.risk.0')
				+ '" onclick="Main.service.scan(\'' + this.RISK_TYPE_MOTOR + '\')" />'
				+ '<input type="button" class="button" value="' + this.getText('file.scan.risk.1')
				+ '" onclick="Main.service.scan(\'' + this.RISK_TYPE_NON_MOTOR + '\')" />'
				// + '<select style="width: 200px" name="riskType" id="scanRiskType" onchange="Main.service.scan()">'
				// + '<option value="">...</option>' + '<option value="0">' + this.getText('file.scan.risk.0')
				// + '</option>' + '<option value="1">' + this.getText('file.scan.risk.1') + '</option>'
				// + '</select>'
				+ '</div><div>&nbsp; </div>';
		this.html['mainNav'] = ' ';
		this.html['mainFooter'] = false;
		this.scanWindow = null;
		this.batchId = null;
		this.forms['scan'] = this.forms['query'];
		this.stopScan();
	},

	exit : function() {
		this.stopScan();
	},
	query : function() {
		// this.openScanWindow();
	},
	single : false, // 是否为单份扫描

	// validateScan : function() {
	// var form = $(this.forms['scan']);
	// // 批量扫描,只有选择了产品线才开始扫描
	// var risk = form.elements["riskType"];
	// // 定义一个全局的险种类型
	// this.riskType = risk.value;
	// return true;
	// },
	/** 启动扫描 */
	scan : function(options) {
		this.stopScan();
		if (Object.isString(options)) options = {
			'riskType' : options
		};
		this.options = options = options || {};
		this.riskType = options['riskType'];
		this.batchId = options['batchId'];
		if (!this.batchId) this.batchId = Math.uuid();
		// this.batchId = Math.uuid(); // 'B06BB1BA-A052-4F6A-BD3E-864AF142BD4C'; //F3167250-B4E0-4B22-8025-4BB3EF36A805
		// this.batchId = "19F4F956-E4B7-4067-A666-DD6E875E41B1";
		// var html = '';
		// alert(this.riskType);
		this.scanParameters = 'riskType=' + this.riskType; // 险种,如果是单份扫描,还需要业务号和资料类型
		if (this.riskType) {
			// http://10.132.21.29/tmweb.net/default.aspx?userId=nasay&appName=LibertySingle&xmlId=3242423523532&serviceNum=11231&lbPageType=123123
			var url = DataCapServers.get('appName='
					+ (this.RISK_TYPE_NON_MOTOR == this.riskType ? 'LibertySuc' : 'LibertyCar') 
					+ '&xmlId='	+ this.batchId);
			// 用于显示扫描的页面
			var $this = this;
			// var html = '<img src="' + Base.template + 'images/waiting.png" class="middle" />'
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
			this.scanWindow = window.open(url, 'scanWindow', 'width=400, height=250, top=' + (top - 10) + ', left=' + (left - 30) + '');
			// , alwaysRaised, dependent');
			this.handler = new FileScanHandler(this.batchId, this.options, this);
			this.handler.read(); // 开始读取处理结果,true表示延迟默认的间隔时间后调用,也可以为数字
		}
		// alert(html);
		// $(this.container).innerHTML = html;
	},

	/** 停止扫描,取消定时器 */
	stopScan : function() {
		if (this.handler) this.handler.stop();
		if (this.scanWindow) {
			this.scanWindow.close();
		}
		this.scanWindow = null;
		if (this.scanningDialog) this.scanningDialog.hide();
		this.scanningDialog = null;
	},

	/** 取消扫描 */
	cancelScan : function() {
		this.stopScan();
		// var form = $(this.forms['scan']);
		// form.elements["riskType"].value = '';
	},

	/** 扫描完成 */
	completeScan : function() {
		if (this.scanningDialog) this.scanningDialog.hide();
		this.scanningDialog = null;
		if (this.scanWindow) this.scanWindow.close();
		this.scanWindow = null;
	},

	/** 保存扫描的数据 */
	save : function() {
		if (!confirm(this.getText('是否提交这次扫描结果'))) return;
		var onSuccess = (function(response) {
			this.handle('query', response).bind(this);
		}).bind(this);
		// 得到最后经过处理后的档案文件数量
		var fileCount = this.xmlDocumnet.getElementsByTagName('file').length;
		var url = this.getUrl('save') + "?xmlString=" + this.xmlDocument.xml;
		this.send(url, {
			onSuccess : onSuccess
		});

	},
	/** 显示档案盒装盒信息 id: 档案盒号 */

	/*
	 * view : function(id) { },
	 */
	/** 列出扫描清单 */
	list : function() {
		var html = '<table class="list">' + '<thead><tr>' + '<td width="10%">' + this.getText('file.box.no') + '</td>'
				+ '<td width="5%">' + this.getText('file.scan.result.box') + '</td>' + '<td width="5%">'
				+ this.getText('file.count.page') + '</td>' + '</tr></thead>' + '<tbody>' + '<tr>'
				+ '<td><a href="javascript: FileScanManager.showBoxDetail(\'5000113032201\')">' + 5000113032201
				+ '</a></td>' + '<td>' + this.getText('file.scan.result.box.tip', [ 1, 10 ]) + '</td>'
				+ '<td class="number">10</td>' + '</tr>' + '<tr>'
				+ '<td><a href="javascript: FileScanManager.showBoxDetail(\'5000113032201\')">' + 5000113032202
				+ '</a></td>' + '<td>' + this.getText('file.scan.result.box.tip', [ 11, 13 ]) + '</td>'
				+ '<td class="number">3</td>' + '</tr>' + '</tbody>' + '</table>';
		// alert(this.html[this.container]);
		/*
		 * var html2 = '<table class="list">' + '<thead><tr>' + '<th>' + this.getText('file.document.no') + '</th>' + '<th>' +
		 * this.getText('file.document.type') + '</th>' + '<th>' + this.getText('global.policy.holder') + '</th>' + '<th>' +
		 * this.getText('file.document.status') + '</th>' + '<th>' + this.getText('file.document.lacks') + '</th>' + '<th>' +
		 * this.getText('file.time') + '</th>' + '<th>' + this.getText('file.user') + '</th>' + '</tr></thead>' + '<tbody><tr>' + '<td><a
		 * href="javascript: FileScanManager.view(\'\')">' + this.getText('8131015001201001134000') + '</a></td>' + '<td>' +
		 * this.getText('file.document.type.8') + '</td>' + '<td>' + this.getText('张三') + '</td>' + '<td>' +
		 * this.getText('file.document.status.11') + '</td>' + '<td></td>' + '<td>2013-03-25</td>' + '<td>nasay</td>' + '</tr>' + '<tr>' + '<td><a
		 * href="javascript: FileScanManager.view(\'\')">' + this.getText('8131015001201001135000') + '</a></td>' + '<td>' +
		 * this.getText('file.document.type.8') + '</td>' + '<td>' + this.getText('李四') + '</td>' + '<td>' +
		 * this.getText('file.document.status.01') + '</td>' + '<td>被保人身份证</td>' + '<td>2013-03-25</td>' + '<td>nasay</td>' + '</tr>' + '<tr>' + '<td><a
		 * href="javascript: FileScanManager.view(\'\')">' + this.getText('8131015001201001136000') + '</a></td>' + '<td>' +
		 * this.getText('file.document.type.8') + '</td>' + '<td>' + this.getText('王五') + '</td>' + '<td>' +
		 * this.getText('file.document.status.01') + '</td>' + '<td>其他1</td>' + '<td>2013-03-25</td>' + '<td>nasay</td>' + '</tr></tbody>' ;
		 */
		TempDialogManager.show(html, this.getText('file.scan.list'));

	},
	/** 打印上传清单 */
	print : function() {
		if (!confirm(this.getText('file.scan.print.tip'))) return;
	},

	/**
	 * 得到档案盒号
	 * 
	 * @param newBox 是否新建档案盒
	 */
	getFileBoxNo : function(newBox) {
		// 调用Ajax后台处理

		// 弹出提示框
		Dialog.show(this.getText('file.scan.tip.boxNo', '<span class="red b">5000113032201</span>'), '', {
			buttons : {
				confirm : {
					text : this.getText('file.scan.tip.boxNo.yes'),
					handler : (function() {
						this.startScanner();
					}).bind(this)
				},

				no : {
					text : this.getText('file.scan.tip.boxNo.no'),
					className : 'button6'
				},

				cancel : {
					text : this.getText('global.return'),
					handler : function() {
						this.hide();
					}
				}
			}
		});
	}
});