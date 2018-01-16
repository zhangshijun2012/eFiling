/** 档案查询 */
var FileManualhandle = Service.create({
	namespace : 'file/manualhandle/',
	selector : true,
	dataHeader: [
  		{ html: "file.document.no", attributes: { style: { width: "15%" } } },
  		{ html: "file.document.type.V", attributes: { style: { width: "5%" } } }, 
  		{ html: "file.document.lacks" }, 
  		{ html: "file.document.status", attributes: { style: { width: "5%" } } },
  		{ html: "global.policy.holder", attributes: { style: { } } }, 
  		{ html: "file.document.salesman", attributes: { style: { width: "10%" } } }, 
  		{ html: "file.document.business.department", attributes: { style: { width: "15%" } } }, 
  		{ html: "file.document.agent", attributes: { style: { width: "20%" } } },  
  		{ html: "file.document.business.user", attributes: { style: { width: "10%" } } },
  		{ html: "归档记录", attributes: { style: { width: "10%" } } }
	],
  	dataHandler: [null, null, null, function(cell, value, index, values, data) {
  		// 显示单证类型的文字描述
  		if (value) {
  			cell.innerHTML = this.getText('file.document.status.' + value);
  		}
  	}, null, null, null, null, null, function(cell, value, index, values, data) {
  		// 显示单证类型的文字描述
  		var businessNo = ""; //业务号
  		var visaNo = ""; 	//单证号
  		if (values) {
  			businessNo = values[0];
  			visaNo = values[1];
  		}
  		cell.innerHTML = '<a href="#" onClick="Main.service.viewlog(\'' + businessNo + '\',\'' + visaNo +'\')">归档日志</>';
  	}],
	initialize : function() {
  		this.urls['index'] = 'index.do';
	},
	/** 
	 * 查看明细信息
	 * @param id 数据主键
	 */
	viewlog: function(businessNo, visaNo) {
		var parameters = "";
		if (businessNo) parameters = "ids=" + businessNo;
		if (visaNo) {
			if (parameters != "") parameters += "&ids=" + visaNo;
			else parameters += "ids=" + visaNo;
		}
		var options = Object.extend({
			parameters: parameters,
			width: 800,
			buttons: {
				close: {
					text: this.getText('close'),
					handler: function() {
						this.hide();
					}
				}
			}
		},  { });
		var onSuccess = options['onSuccess'];
		options['onSuccess'] = (function() {
			this.form = this.forms['viewlog'] = $($(this.dialogs['viewlog'].body).select('form')[0]);
			if (this.viewHandler) this.viewHandler.apply(this, $A(arguments));
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		this.dialog = this.dialogs['viewlog'] = Dialog.open(this.getUrl('viewlog'), "归档记录", options);
	},
	/**                                                        
	 * 查看明细信息
	 * @param id 数据主键
	 */
	view: function(id, options) {
		if (!Object.isString(id)) options = id || options;
		options = Object.extend({
			parameters: 'id=' + id,
			//minWidth: 600,
			width: 800,
			buttons: {
				comfirm: {
					text: this.getText("file.type.document"),
					id: "collectDetailButton",
					attributes: {'className': 'button6'},
					disabled: true,
					handler: (function() {
						this.collectDetail();
					}).bind(this)
				},
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
			this.form = this.forms['view'] = $($(this.dialogs['view'].body).select('form')[0]);
			if (this.viewHandler) this.viewHandler.apply(this, $A(arguments));
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		this.dialog = this.dialogs['view'] = Dialog.open(this.getUrl('view'), this.getText('view'), options);
	},
	
	/**
	 * 选中某一个数据
	 * @param e 
	 * @param checked 可以省略,否则true/false.表示勾选/不选此选择框
	 */
	onCheck: function(e, checked) {
		this.changeActionDisabled();
	},
	/**
	 * 全部选中
	 * @param checked
	 */
	onCheckAll: function(checked) {
		this.changeActionDisabled();
	},
	
	/**
	 * 根据规则判断审核通过按钮是否显示或者隐藏 
	 */
	changeActionDisabled: function() {
		var form = $(this.forms['query']);
		var comfirmButton = form.elements['comfirmButton'];
		var disabled = this.selected.size() <= 0;
		comfirmButton.disabled = disabled;
	},
	/**
	 * 选中所有明细
	 * @param checked
	 */
	checkAllDetails: function(checked) {
		var disabled = false; 
		var form = $('viewForm');
		var checkBoxes = form.getInputs("checkbox", 'ids');
		var count = 0;
		var no = "";	 //档案编码
		var documentStatus = ""; //档案状态
		var collectFiles = [];
		var revokeFiles = [];
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
	checkDetail: function(checked) {
		var isChecked = false; 
		var form = $('viewForm');
		var checkBoxes = form.getInputs("checkbox", 'ids');
		for (var i = 0; i < checkBoxes.length; i++) {
			if (checkBoxes[i].checked) {
				isChecked = true;
				break;
			}
		}
		$('collectDetailButton').disabled = !isChecked;
	},
	/**
	 *归档操作明细
	 */
	collectDetail: function() {
		var form = $('viewForm');
		var parameters = "&" + form.serialize();
		var options = {
				parameters: parameters,
				onSuccess: (function(response) {
					var message = response.responseText.toJSON();
					if(message['success']) alert(message['message']);
					this.dialog.hide();
					this.query();
				}).bind(this)
			};
		this.request(this.getUrl('collectDetail'), options);
	},
	/**
	 * 归档操作
	 */
	collect: function() {
		var parameters = this.initializeParameters();
		var options = {
			parameters: parameters,
			onSuccess: (function(response) {
				var message = response.responseText.toJSON();
				alert(message['message']);
				this.query();
			}).bind(this)
		};
		this.request(this.getUrl('collect'), options);
	},
	/**
	 * 对审核通过和审核不通过时候,做初始化参数
	 */
	initializeParameters: function() {
		var selected = this.selected;
		if (selected.size() === 0) {
			alert("请选择业务号！");
			return;
		} 
		var form = $(this.forms['query']);
		var parameters = form.serialize();
		return parameters;
	},
	/**
	 * 查询
	 */
	query : function() {
		Service.query.apply(this, $A(arguments));
	}
});