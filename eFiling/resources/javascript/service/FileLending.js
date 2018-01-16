/** 档案查询 */
var FileLending = Service.create({
	namespace : 'file/filelending/',
	selector: true,
	dataHeader: [
  		{ html: "file.document.no", attributes: { style: { width: "20%" } } }, 
  		{ html: "file.document.type", attributes: { style: { width: "8%" } } }, 
  		//{ html: "global.policy.holder", attributes: { style: { width: "8%" } } }, 
  		//{ html: "file.document.status", attributes: { style: { width: "8%" } } }, 
  		{ html: "借阅人", attributes: { style: { width: "8%" } } }, 
  		//{ html: "file.document.salesman", attributes: { style: { width: "5%" } } }, 
  		{ html: "借阅日期", attributes: { style: { width: "10%" } } }, 
  		{ html: "预计归还日期", attributes: { style: { width: "10%" } } }, 
  		{ html: "是否归档", attributes: { style: { width: "10%" } } },
  		{ html: "是否超期", attributes: { style: { width: "8%" } } }, 
  		//{ html: "file.document.agent", attributes: { style: { width: "8%" } } },  
  		{ html: "超期天数", attributes: { style: { width: "8%" } } }, 
  		{ html: "file.user"/*, attributes: { style: { width: "8%" } }*/ },
  		//{ html: "file.document.b2c", attributes: { style: { width: "5%" } } },
  		{ html: "file.loan.status", attributes: { style: { width: "8%" } } }
	],
  	dataHandler: [null, function(cell, value, index, values, data) {
  		// 显示单证类型的文字描述
  		cell.innerHTML = this.getText('file.document.type.' + value);
  	}, null, null, null, function(cell, value, index, values, data) {
  		// 显示档案归档状态
  		cell.innerHTML = this.getText('file.document.status.' + value);
  	},  function(cell, value, index, values, data) {
  		//是否超期
  		if (value != null) cell.innerHTML = this.getText(value >= '1' ? 'global.yes' : 'global.no');
  	}, function(cell, value, index, values, data) {
  		//如果value是负数,表示没有超期,界面应该显示0,不显示负数
  		if (value < 0) value = 0; 
		cell.innerHTML = value = NumberHelper.format(value, 0, 0);
		$(cell).addClassName('number');
	}, null, function(cell, value, index, values, data) {
  		// 是否借出
  		if (value) cell.innerHTML = this.getText(value === '1' ? 'global.yes' : 'global.no');
  	}],
	initialize : function() {
  		this.urls['index'] = 'index.do';
	},
	
	lentCount: 0,	// 表示被勾选的已借出的行数
	unLentCount: 0,  	// 表示被勾选的未借出的行数
	/**
	 * @param {Object} e 被选中的checkBox
	 * @param {Object} checked  多选框是否被选中 true:被选中  false:未被选中
	 * @memberOf {TypeName} 
	 * @return {TypeName} 
	 */
	lentButtonStatus: true, //表示借阅按钮的状态，true表示被禁用
	returnButtonStatus: true, //表示归还按钮的状态，true表示被禁用
	//定一个两个form对象，一个表示查询时候的form对象，一个表示view明细时候的form对象
	// @param checked 可以省略,否则true/false.表示勾选/不选此选择框
	onCheck : function(e, checked) {
		if (Object.isUndefined(checked)) checked = e.checked;
		else e.checked = !!checked;
		var selectDatas = this.selectorData[e.value];
		if (!selectDatas) return;
		var mainForm = $(this.forms['query']);
		var lentButton = mainForm.lendButton; //借阅按钮
		var returnButton = mainForm.returnButton; //归还按钮
		var lent = selectDatas[10];//取得是否借出的状态   0：表示没有被借出, 1：表示已经借出
		if (checked) {
			if (lent == '1') this.lentCount++;
			else this.unLentCount++;
		} else {
			if (lent == '1') this.lentCount--;
			else this.unLentCount--;
		}
		if (this.lentCount == this.unLentCount || (this.lentCount > 0 && this.unLentCount > 0)){
			// 全禁用
			returnButton.disabled = true;
			lentButton.disabled = true;
		} else if (this.unLentCount > 0) {
			// 可以借出
			returnButton.disabled = true;
			this.lentButtonStatus = false;//可以借阅该档案资料
			lentButton.disabled = false;
		} else if (this.lentCount > 0) {
			// 可以归还
			returnButton.disabled = false;
			this.returnButtonStatus = false; //可以归还该档案资料
			lentButton.disabled = true;
		}
	},
	onCheckAll : function(checked) {
		var mainForm = $(this.forms['query']);
		var lentButton = mainForm.lendButton; //借阅按钮
		var returnButton = mainForm.returnButton; //归还按钮
		var $selected = this.selected;
		if (checked) {
			var $lent = '';
			for (var i = 0; i < $selected.length; i++) {
				$lent = $selected[i][10];
				if (checked) {
					if ($lent == '1') this.lentCount++;
					else this.unLentCount++;
				} else {
					if ($lent == '1') this.lentCount--;
					else this.unLentCount--;
				}
			}
		} else {
			this.lentCount = 0;   
			this.unLentCount = 0;
		}
		if (this.lentCount == this.unLentCount || (this.lentCount > 0 && this.unLentCount > 0)) {
			// 全禁用
			returnButton.disabled = true;
			lentButton.disabled = true;
		} else if (this.unLentCount > 0) {
			// 可以借出
			returnButton.disabled = true;
			//this.lentButtonStatu = true;//可以借阅该档案资料
			lentButton.disabled = false;
		} else if (this.lentCount > 0) {
			// 可以归还
			returnButton.disabled = false;
			//this.returnButtonStatu = true; //可以归还该档案资料
			lentButton.disabled = true;
		}
	},
	view : function(id, options) {
		if (!Object.isString(id)) options = id || options;
		options = Object.extend({
			parameters: 'ids=' + id,
			width: 800,
			buttons: {
				lend: {
					id: "lentDetaiButton",
					text: this.getText('file.lending.lent'),
					disabled: true,
					handler: (function() {
						this.lent();
					}).bind(this)  
				},
				back: {
					text: this.getText('file.lending.return'),
					disabled: true,
					id: "backDetaiButton",
					handler: (function() {
						this.back();
					}).bind(this)  
				},
				close: {
					text: this.getText('close'),
//					handler: (function() {
//						this.dialog.hide();
//						//当关闭窗口的时候,把档案清单所选择的所有档案资料个数设置为0
//						this.lentDetailCount = 0;
//						this.unlentDetailCount = 0;
//					}).bind(this)
					handler: function() {
						this.hide();
						//当关闭窗口的时候,把档案清单所选择的所有档案资料个数设置为0
						FileLending.lentDetailCount = 0;
						FileLending.unlentDetailCount = 0;
					}
				}
			}
		}, options || { });
		var onSuccess = options['onSuccess'];
		options['onSuccess'] = (function(response) {
			this.form = this.forms['view'] = $($(this.dialogs['view'].body).select('form')[0]);
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		this.dialog = this.dialogs['view'] = Dialog.open(this.getUrl('view'), this.getText('view'), options);
	},
	/**
	 * @param {Object} status 0:未借出 1：借出
	 * @memberOf {TypeName} 
	 */
	operate : function(status) {
		var form = $(this.forms['query']);
		var $status = status;
		var parameters = Form.serialize(form);
		var options = Object.extend({
			parameters: parameters,
			width: 800,
			buttons: {
				lend: {
					text: this.getText('file.lending.lent'),
					disabled: $status == 1 ? true : false,
					handler: (function() {
						this.lent();
					}).bind(this)  
				},
				back: {
					text: this.getText('file.lending.return'),
					disabled: $status == 0 ? true : false,
					handler: (function() {
						this.back();
					}).bind(this)  
				},
				cancel: {
					text: this.getText('global.cancel'),
					handler: function() {
						this.hide();
					}
				}
			}
		},{});
		var onSuccess = options['onSuccess'];
		//根据状态需要访问的页面
		var viewPage = $status == 1 ? "viewBack" : "viewLent";
		options['onSuccess'] = (function() {
			this.form = this.forms[viewPage] = $($(this.dialogs[viewPage].body).select('form')[0]);
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		this.dialog = this.dialogs[viewPage] = Dialog.open(this.getUrl(viewPage), this.getText('view'), options);
		
	},
	
	/*借阅*/
	lent : function() {
		var parameters = Form.serialize(this.form);
        //如果没有勾选档案明细不让借阅
        if (!parameters) {
        	alert("请选择借阅资料类型");
	        return false;    
        }
        var today = DateHelper.format(new Date()); 
	    var html = "<form name='Filelending.lend' id='Filelending.lend' method='post'>" + 
	    		   "	<table class='form'>" +
	    		   "		<tr>" +
	    		   "		   <td width='10%' class='title required requeird'>" + 
	    		  					this.getText('file.lending.lenter') +
	    		   "           </td>" +
	    		   "           <td width='40%' class='requiredValue'>" +
	    		   "				<input type='text' name='entity.borrowerName' class='text' title required='" + this.getText('file.lending.lenter') + "'/>" + 
	    		   "           </td>" + 
	    		   "		   <td width='10%' class='title required'>" + 
	    		  					this.getText('file.lending.lent.department') + 
	    		   "           </td>" +
	    		   "           <td width='40%' class='requiredValue'>" +
	    		   "				<input type='text' name='entity.borrowerDeptName' class='text' title required='" + this.getText('file.lending.lent.department') + "'/>" +
	    		   "           </td>" + 
	    		   "		</tr>" +
	    		   "		<tr>" +
	    		   "		   <td width='10%' class='title required'>" + 
	    		  					this.getText('file.lending.lent.company') +
	    		   "           </td>" +
	    		   "           <td width='40%' class='requiredValue'>" +
	    		   "				<input type='text' name='entity.borrowerCompanyName' class='text' title required='" + this.getText('file.lending.lent.company') + "'/>" + 
	    		   "           </td>" + 
	    		   "		   <td width='10%' class='title required'>" + 
	    		 					this.getText('file.lending.lent.time') + 
	    		   "           </td>" +
	    		   "           <td width='40%' class='requiredValue'>" + today +
	    		   "				<input type=\"hidden\" name='entity.insertTime' value='" + today + "' />" +
	    		   "           </td>" + 
	    		   "		</tr>" +
	    		   "		<tr>" +
	    		   "		   <td width='10%' class='title required'>" + 
	    		   					this.getText('file.lending.lent.days') + 
	    		   "           </td>" +
	    		   "           <td width='40%' class='requiredValue'>" +
	    		   "				<input type='text' name='entity.days' value='30' class='text' title required='" + this.getText('file.lending.lent.days') + "'/>" +
	    		   "           </td>" + 
	    		   "		   <td width='10%' class='title required'>" + 
	    		 					this.getText('借阅人邮箱') + 
	    		   "           </td>" +
	    		   "           <td width='40%' class='requiredValue'>" +
	    		   "				<input name='entity.borrowerEmail' value='' class='text' title required='" + this.getText('借阅人邮箱') + "'/>" +
	    		   "           </td>" + 
	    		   "		</tr>" +
	    		   "		<tr>" +
	    		   "		   <td class='title required'>" + 
	    		   					this.getText('file.lending.lent.reason') + 
	    		   "           </td>" +
	    		   "           <td colspan='3' class='requiredValue'>" +
	    		   "				<textarea name='entity.reason'></textarea> " +
	    		   "           </td>" + 
	    		   "		</tr>" +
	    		   "	 </table>" +
	    		   "</form>" ;
		var options = Object.extend({
			width: 800,
			buttons: {
				confirm: {
					text: this.getText('confirm'),
					handler: (function() {
						this.save('Lent', parameters);
					}).bind(this)
				},
				
				reset: {
					text: this.getText('reset'),
					handler: (function() {
						this.forms['save'].reset();
					}).bind(this)
				},
				
				cancel: {
					text: this.getText('cancel'),
					handler: function() {
						//this.hide();
						this.destroy();
					}
				}
			}
		
		}, { });
		dialog = this.dialog = this['dialogs']['lend'] = TempDialogManager.show(html, this.getText('file.lending.lent'), options);
		/*this.form = */this.forms['save'] = $('Filelending.lend'); 
	},
	/*归还*/
	back : function() {
		var parameters = Form.serialize(this.form);
		//没有勾选档案资料不准归还
		if (!parameters) {
			alert("请选择归还的档案资料");
			return false;
		}
		var checkboxes = this.form.getInputs('checkbox');
		if(!checkboxes.length) checkboxes = [checkboxes];
		var tr = "";
		var borrName = "";   //归还默认带出借阅人是归还人
		var borrDept = "";   //归还默认带出借阅部门是归还部门
		for (var i = 0; i < checkboxes.length; i++) {
			tr = checkboxes[i].parentNode.parentNode;
			borrName = tr.cells[7].innerText;
			borrDept = tr.cells[8].innerText;
			if (borrName && borrDept) break; 
		}
		var today = DateHelper.format(new Date(), 'YYYY-MM-DD');
		var html = "<form name='Filelending.back' id='Filelending.back' method='post'>" + 
        		   "	<table class='form'>" +
        		   "		<tr>" +
        		   "		   <td width='10%' class='title required'>" + 
        		   					this.getText('file.lending.returner') +
        		   "           </td>" +
        		   "           <td width='40%' class='requiredValue'>" +
        		   "				<input type='text' name='entity.fileLendingReturn.borrowerName' value='" + borrName + "' class='text' title required='" + this.getText('file.lending.returner') + "'/>" + 
        		   "           </td>" + 
        		   "		   <td width='10%' class='title required'>" + 
        		  					this.getText('file.lending.return.department') + 
        		   "           </td>" +
        		   "           <td width='40%' class='requiredValue'>" +
        		   "				<input type='text' name='entity.fileLendingReturn.borrowerDeptName' value='" + borrDept + "' class='text' title required='" + this.getText('file.lending.return.department') + "'/>" +
        		   "           </td>" + 
        		   "		</tr>" +
        		   "		<tr>" +
        		   "		   <td width='10%' class='title required'>" + 
        		  					this.getText('file.lending.return.time') + 
        		   "           </td>" +
        		   "           <td width='40%' class='requiredValue'>" + today +
        		   "				<input name='entity.fileLendingReturn.insertTime' type=\"hidden\" value='"+ today +"' />" +
        		   "           </td>" + 
        		   "		</tr>" +
        		   "		<tr>" + 
        		   "		   <td width='10%' class='title'>" + 
        		  					this.getText('global.remarks') + 
        		   "           </td>" +
        		   "           <td colspan='3'>" +
        		   "				<textarea name='entity.fileLendingReturn.remarks'></textarea> " +
        		   "           </td>" + 
        		   "		</tr>" +
        		   "	 </table>" +
        		   "</form>" ;
		var options = Object.extend({
			width: 800,
			buttons: {
				confirm: {
					text: this.getText('confirm'),
					handler: (function() {
						this.save('Back', parameters);
					}).bind(this)
				},
				
				reset: {
					text: this.getText('reset'),
					handler: (function() {
						this.forms['save'].reset();
					}).bind(this)
				},
				
				cancel: {
					text: this.getText('cancel'),
					handler: function() {
						//this.hide();
						this.destroy();
					}
				}
			}
//			on: function() {
//				// 对话框打开之后执行的函数
//			}
		}, { });
		dialog = this.dialog = this['dialogs']['back'] = TempDialogManager.show(html, this.getText('file.lending.return'), options);
		/*this.form = */this.forms['save'] = $('Filelending.back');
	},
	/**
	 * @param {Object} parameters 传入选中的文件ids
	 * @memberOf {TypeName} 
	 * @return {TypeName} 
	 */
	save : function(method, parameters) {
		if (!confirm(this.getText('global.save.confirm'))) return;
		var form = $(this.forms['save']);
		if (!form) return;
		var parameters = Form.serialize(form) + (!Object.isUndefined(parameters) ? '&' + parameters : "");
		var onSuccess = (function(response) {
			// this.after('save', $A(arguments));
			if (method == 'Lent') {
				this.lentView(response);
			} else if (method == 'Back') {
				this.backView(response);
			}
		}).bind(this);
		this.send(this.getUrl('save' + method), {
			parameters: parameters,
			onSuccess: onSuccess
		});
	},
	/*
	 * 显示借阅后的显示
	 */
	lentView : function(response) {
		var text = response.responseText.trim();
		var result = text.toJSON();
		if (!result) result = { 'message': text };
		if (!result) return;
		if (result['message']) alert(result['message']);
		if (!result['success']) return;
		var options = Object.extend({
			parameters: 'id=' + result['id'],
			width: 800,
			buttons: {
				close: {
					text: this.getText('close'),
					handler: function() {
						this.hide();
					}
				}
			}
		}, {});
		var onSuccess = options['onSuccess'];
		options['onSuccess'] = (function(dialog) {
			if (dialog && dialog.visible && dialog != this.dialogs['lentView']) {
				dialog.hide();	 //将新增/编辑框隐藏
			}
		}).bind(this, this.dialog);
		this.dialog = this.dialogs['lentView'] = Dialog.open(this.getUrl('lentView'), this.getText('file.lending.lent'), options);
		this.query();
	},
	/*
	 * 显示归还后的显示
	 */
	backView : function(response) {
		var text = response.responseText.trim();
		var result = "";
		if (text) result = text.toJSON();
		var options = Object.extend({
			parameters: 'id=' + result['id'],
			width: 800,
			buttons: {
				close: {
					text: this.getText('close'),
					handler: function() {
						this.hide();
					}
				}
			}
		}, {});
		var onSuccess = options['onSuccess'];
		options['onSuccess'] = (function(object) {
			$this = object;
			if (dialog && dialog.visible && dialog != this.dialogs['backView']) {
				dialog.hide();	 //将新增/编辑框隐藏
			}
			if (!confirm(this.getText('file.lending.isprint.receipt'))) return;
			//打印归还的签收条
			this.printReceipt(result);
		}).bind(this);
		this.dialog = this.dialogs['backView'] = Dialog.open(this.getUrl('backView'), this.getText('file.lending.return'), options);
		this.query();
	},
	//打印归还的签收条
	printReceipt : function (result) {
		//归还后打印签收条
		//var form = document.forms['Filelending.backView'];
		var html = "";
		var list = result['list'];
	    list.each(function(document) {
	       html += "<div class='pageAfter'><div><img src='" + Base.template + "images/print_logo.png'/></div>";
		   html += "<div style='padding: 20px 50px;'>";
		   html += "<p>" + "基本信息" + "</p><hr>";
		   html += "<table class='form'>" +
			   "		<tr>" +
			   "		   <td>" + "业务号码" + "</td>" + 
	 		   "		   <td>" + document['no'] + "</td>" +  
	 		   "           <td>" + "投保人" + "</td>" + 
	 		   "		   <td>" + document['applicant'] + "</td>" + 				 
	 		   "		</tr>" +
	 		   "		<tr>" +
			   "		   <td>" + "保险期限" + "</td>" + 
	 		   "		   <td>" + (DateHelper.format(document['effectiveTime']) + (document['dueTime'] ? (' - ' + DateHelper.format(document['dueTime'])) : "")) + "</td>" +  
	 		   "           <td>" + "被保险人" + "</td>" + 
	 		   "		   <td>" + (document['insured'] || '') + "</td>" + 				 
	 		   "		</tr>" +
	 		   "		<tr>" +
			   "		   <td>" + "借出日期" + "</td>" + 
	 		   "		   <td>" + (document['lentTime'] || '') + "</td>" +  
	 		   "           <td></td>" + 
	 		   "		   <td></td>" + 				 
	 		   "		</tr>" +
	 		   "	 </table><p>&nbsp;</p>";
		   html += "<p>" + "本次归还的单证" + "</p><hr>";
		   html += "<table class='form'>" +
			   "		<tr>" +
			   "		   <td>" + "文档" + "</td>" + 
			   "		   <td>" + "状态" + "</td>" +  
			   "           <td>" + "归还日期" + "</td>" + 
			   "		   <td>" + "归还人姓名" + "</td>" + 				 
			   "		</tr>";
		   var documentFiles = document['documentFiles'];
		   documentFiles.each(function(documentFile) {
			   html += "		<tr>" +
					   "		   <td>" + documentFile + "</td>" + 
					   "		   <td>" + "已收到" + "</td>" +  
					   "           <td>" + result['returnTime'] + "</td>" + 
					   "		   <td>" + result['returnName'] + "</td>" + 				 
					   "		</tr>";
		   });
		   html +="</table></div></div>";
	    });

		html = '<div id="printBody">' + html + '</div>';
		Main.print(html);
	},
	//在档案清单中全选方法
	lentDetailCount : 0,
	unlentDetailCount : 0,
	checkDetail : function(e) {
		var $checked = e.checked;
		var lent = e.getAttribute('statu');
		//禁用的资料不让借阅了
		var lentCount = 0;	// 表示被勾选的已借出的行数
		var unLentCount = 0;  	// 表示被勾选的未借出的行数
		var form = $('FileLending.view');
		var lentDetaiButton = $('lentDetaiButton');//档案清单中的借阅按钮this.dialog.buttons['lend'].element;
		var backDetaiButton = $('backDetaiButton');//档案清单中的归还按钮
		if ($checked) {
			if (lent == '1') this.lentDetailCount++;
			else this.unlentDetailCount++;
		} else {
			if (lent == '1') this.lentDetailCount--;
			else this.unlentDetailCount--;
		}
		if (this.lentDetailCount == this.unlentDetailCount || (this.lentDetailCount > 0 && this.unlentDetailCount > 0)) {
			// 全禁用
			backDetaiButton.disabled = true;
			lentDetaiButton.disabled = true;
		} else if (this.unlentDetailCount > 0) {
			// 可以借出
			backDetaiButton.disabled = true;
			lentDetaiButton.disabled = false;
		} else if (this.lentDetailCount > 0) {
			// 可以归还
			backDetaiButton.disabled = false;
			lentDetaiButton.disabled = true;
		}
	},
	//档案清单中单独选择一个档案资料
	checkAllDetail : function(e) {
		var form = $('FileLending.view');
		var lentDetaiButton = $('lentDetaiButton');//档案清单中的借阅按钮
		var backDetaiButton = $('backDetaiButton');//档案清单中的归还按钮
		var $checked = e.checked;
		var checkboxes = form.getInputs("checkbox");
		var checkbox;
		var status;
		for (var i = 0; i < checkboxes.length; i++) {
			checkbox = checkboxes[i];
			checkbox.checked = $checked;
			status = checkbox.getAttribute('statu');
		}
		
		if (this.lentDetailCount == this.unlentDetailCount || (this.lentDetailCount > 0 && this.unlentDetailCount > 0)) {
			// 全禁用
			backDetaiButton.disabled = true;
			lentDetaiButton.disabled = true;
		} else if (this.unlentDetailCount > 0) {
			// 可以借出
			backDetaiButton.disabled = true;
			lentDetaiButton.disabled = false;
		} else if (this.lentDetailCount > 0) {
			// 可以归还
			backDetaiButton.disabled = false;
			lentDetaiButton.disabled = true;
		}
		
	}
});