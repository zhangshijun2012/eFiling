/** 档案查询 */
var FileCoreApprove = Service.create({
	namespace : 'file/approve/',
	FILE_STATUS_UNAUDITED: '0', //未审核
	FILE_STATUS_PASSED: '9',    //审核通过
	FILE_STATUS_NOPASSED: '2',  //审核不通过
	selector: function(cell, value, values) {
		if (!value && !values) return Paging.$skip;	// 此时为表头,返回$break使用默认的处理
		var count = values[values.length - 1];	// 最后一列保存的是待审核数量
		if (count <= 0) return false;
		cell.innerHTML = '<input type="checkbox" onclick="Main.service[\'check\'](this)" name="ids" value="' + value + '"  />';
	},
	dataHeader: [
  		{ html: "file.document.no", attributes: { style: { width: "15%" } } },
  		{ html: "file.approve.status", attributes: { style: { width: "5%" } } }, 
  		{ html: "global.policy.holder", attributes: { style: { } } }, 
  		{ html: "file.document.salesman", attributes: { style: { width: "10%" } } }, 
  		{ html: "file.document.business.department", attributes: { style: { width: "15%" } } }, 
  		{ html: "file.document.agent", attributes: { style: { width: "20%" } } },  
  		{ html: "file.document.business.user", attributes: { style: { width: "10%" } } },
  		{ html: "file.user", attributes: { style: { width: "10%" } } }
  		// 最后还有一列待审核的单证数量,不显示,大于0则表示可以勾选进行审核
	],
  	dataHandler: [null, function(cell, value, index, values, data) {
  		// 显示单证类型的文字描述
  		cell.innerHTML = this.getText('file.approve.status.' + value);
  	}, null, null, null, null, null, null],
	initialize : function() {
  		this.urls['index'] = 'index.do';
  		//this.urls['decline'] = "decline.do";
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
				approve: {
					text: this.getText("file.approve.status.1"),
					id: "passedDetailButton",
					attributes: {'className': 'button6'},
					disabled: true,
					handler: (function() {
						this.approveDetail();
					}).bind(this)
				},
				decline: {
					text: this.getText("file.approve.status.2"),
					id: "nopassedDetailButton",
					attributes: {'className': 'button6'},
					disabled: true,
					handler: (function() {
						this.declineDetail();
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
		var passedButton = form.elements['passedButton'];
		var nopassedButton = form.elements['nopassedButton'];
		var disabled = this.selected.size() <= 0;
		passedButton.disabled = disabled;
		nopassedButton.disabled = disabled;
	},
	
	/**
	 * 选中所有明细
	 * @param checked
	 */
	checkAllDetails: function(checked) {
		var form = $('FileCore.view');
		var checkBoxes = form.getInputs("checkbox", 'ids');
		var count = 0;
		for (var i = 0; i < checkBoxes.length; i++) {
			checkBoxes[i].checked = checked;
			count++;
		}
		var passedDetailButton = $('passedDetailButton');  //审核通过按钮
		var nopassedDetailButton = $('nopassedDetailButton');  //审核不通过按钮
		disabled = !checked || count <= 0;
		passedDetailButton.disabled = disabled;
		nopassedDetailButton.disabled = disabled;
	},
	/**
	 * 选中一个明细
	 * @param field
	 * @param checked
	 */
	checkDetail: function(field, checked) {
		var disabled = false;
		if (!checked) {
			var form = $('FileCore.view');
			var checkBoxes = form.getInputs("checkbox", "ids");
			disabled = true;
			for (var i = 0; i < checkBoxes.length; i++) {
				if (checkBoxes[i].checked) {
					disabled = false;
					break;
				}
			}
		}
		
		var passedDetailButton = $('passedDetailButton');  //审核通过按钮
		var nopassedDetailButton = $('nopassedDetailButton');  //审核不通过按钮
		passedDetailButton.disabled = disabled;
		nopassedDetailButton.disabled = disabled;
	},
	
	/**
	 * 原来上传后的承保资料默认是归档,审核不通过把原来的状态改为未归档
	 * id 传入档案资料的
	 */
	decline: function() {
		var parameters = this.initializeParameters();
		var options = {
				parameters: parameters,
				onSuccess: (function(response) {
					var message = response.responseText.toJSON();
					if(message['success']) alert("审核不通过操作成功！");
					this.query();
				}).bind(this)
			};
		this.request(this.getUrl('decline'), options);
	},
	/**
	 * 点击业务号后进入每一单数据的详细页面,在页面中对单证进行审核操作
	 */
	declineDetail: function() {
		var form = $('FileCore.view');
		var parameters = "&" + form.serialize();
		var options = {
				parameters: parameters,
				onSuccess: (function(response) {
					var message = response.responseText.toJSON();
					if(message['success']) alert("审核不通过操作成功！");
					this.dialog.hide();
					FileCoreApprove.query();
				}).bind(this)
			};
		this.request(this.getUrl('declineDetail'), options);
	},
	/**
	 * 在业务号的详细页面点击审核通过
	 */
	approveDetail: function() {
		var form = $('FileCore.view');
		var parameters = "&" + form.serialize();
		var options = {
				parameters: parameters,
				onSuccess: (function(response) {
					var message = response.responseText.toJSON();
					if(message['success']) alert("审核通过操作成功！");
					this.dialog.hide();
					FileCoreApprove.query();
				}).bind(this)
			};
		this.request(this.getUrl('approveDetail'), options);
	},
	/**
	 * 在查询列表点击审核通过
	 */
	approve: function() {
		var parameters = this.initializeParameters();
		var options = {
			parameters: parameters,
			onSuccess: (function(response) {
				var message = response.responseText.toJSON();
				if(message['success']) alert("审核通过操作成功！");
				this.query();
			}).bind(this)
		};
		this.request(this.getUrl('approve'), options);
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