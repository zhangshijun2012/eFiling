/** 承保资料类型设置 */
var FileDeadline = Service.create({
	/** 访问此模块的路径.以/结尾,不要以/开头，因为在base.js中SERVER_ROOT是以/开头和结束的 */
	namespace: 'setting/fileDeadline/',
	//selector: true, //true表示显示checkbox
	dataHeader: [
  		{ value: 'global.company', attributes: { } }, 
  		{ value: 'global.department', attributes: { } }, 
  		{ value: 'global.product', attributes: { } }, 
  		{ value: 'file.deadline.days', attributes: { } }, 
  		{ value: 'file.deadline.ctrl.type', attributes: { } }, 
  		{ value: 'global.status', attributes: { } }, 
  		{ value: 'global.user', attributes: { } }, 
  		{ value: 'global.time', attributes: { } }
  	],
 
	dataHandler: [null, function(cell, value, index, values, data) {
		if (!value) {
	  		cell.innerHTML = this.getText('global.all');
		} else {
			cell.innerHTML = value;
		}
		}, function(cell, value, index, values, data) {
	  		// 控制产品的文字描述，如果value值为null，则是全部
			if (!value) {
		  		cell.innerHTML = this.getText('global.all');
			} else {
				cell.innerHTML = value;
			}
  		}, function(cell, value, index, values, data) {
  			cell.innerHTML = value = NumberHelper.format(value, 0, 0);
  			$(cell).addClassName('Number');
  		}, function(cell, value, index, values, data) {
	  		// 控制类型的文字描述
	  		cell.innerHTML = this.getText('file.deadline.ctrl.type.' + value);
  		}, function(cell, value, index, values, data) {
	  		// 状态的文字描述
	  		cell.innerHTML = this.getText('global.status.' + value);
	  	}
	],
	
	initialize : function() {
		
		this.urls['index'] = 'index.do';
		this.html['mainNav'] = '';
		this.html['mainFooter'] = true;
		/* 
		this.html[this.INDEX_CONTAINER] = '<table class="form"><tbody>'
			+ '<tr>'
			+ '<td width="5%" class="title">' + this.getText('file.type.id') + '</td>'
			+ '<td width="25%"><input class="text" name="id" /></td>'
			+ '<td width="5%" class="title">' + this.getText('file.type.name') + '</td>'
			+ '<td width="25%"><input class="text" name="name" /></td>'
			+ '<td class="actionBar">'
			+ '<input name="queryButton" id="queryButton" type="button" value="' + this.getText('global.query') 
			+ '" onclick="Main.service.query()" />'
			+ '<input name="queryButton" id="queryButton" type="button" value="' + this.getText('global.append') 
			+ '" onclick="Main.service.append()" />'
			+ '</td>'
			+ '</tr>'
			+ '</tbody></table>';
	 
		/*	
		/*
		this.addValidator('save', function() {
			var form = $(this.forms['save']);
			if (!form) return true;
			
			form.elements['entity.code'].setAttribute('required', true);
			return true;
		});
		*/
	},
	
	/** 进入此模块后执行的方法 */
	mainHandler: function() {
		this.departments = $('departmentsData').innerHTML.toJSON();
		var form = $(this.forms['query']);
		this.changeCompany(form.elements['companyId'].value);
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
			// var status = this.forms['view'].elements['entity.status'].value;
		}).bind(this),
		this['super']['view'](id, options);
	},
	
	appendHandler: function() {
		var form = this.form;
		var companyId = form.elements['entity.company.id'].value;
		var field = form.elements['entity.fileDept.id'];
		this.changeCompany(companyId, field, true);
	},
	
	departments: null,
	/**
	 * 改变分公司,则需要改变机构的选择框.使用机构代码的前4位作为公司代码进行匹配
	 * @param companyId
	 * @param filed 部门的选择框
	 * @param emptyFlag 公司代码为空的处理方法,true则不处理,false则全部列出来
	 */
	changeCompany: function(companyId, filed, emptyFlag) {
		// companyId = companyId ? companyId.substring(0, 4) : '';
		var options = (filed ? filed : $(this.forms['query']).elements['deptId']).options;
		options.length = 1;
		if (!companyId && emptyFlag) return;
		for (var i = 0, l = this.departments.length; i < l; i++) {
			option = this.departments[i];
			if (companyId && companyId != option['companyId']) continue;
			options.add(new Option(option['id'] + ' - ' + option['name'], option['id']));
		}
	}
});