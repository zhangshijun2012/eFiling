/**
 * 基础功能.
 */
var Service = {
	/** 国际化查询的前缀 */
	'i18n.prefix': '',
     /** 查询国际化语言的方法 */
    getText: function(key, args) {
		if (!key) return key;
    	if (!Object.isString(key)) return (key);
    	args = Object.isUndefined(args) || args === null ? null : (Object.isArray(args) ? args : Array.prototype.slice.call(arguments, 1));
    	if (key.startsWith('global.')) return Language.get(key, args);
    	var value = Language.find(key, args);
    	if (value !== null) return value;
    	if (!this['i18n.prefix'] && this.namespace) {
    		this['i18n.prefix'] = this.namespace.replaceAll('\\\\|\/', '.');
    		if (this['i18n.prefix'].lastIndexOf('.') == this['i18n.prefix'].length - 1) {
    			this['i18n.prefix'] = this['i18n.prefix'].substring(0, this['i18n.prefix'].length - 1);
    		}
    	}
    	var k = this['i18n.prefix'] ? this['i18n.prefix'] + '.' + key : key;
    	var value = Language.find(k, args);	// find方法如果不存在则会返回null
    	if (value === null && !k.startsWith('global.')) value = Language.find('global.' + key, args);
    	if (value === null) return Language.get(key, args);
    	return value;
    },
	/** 在执行observe时默认要绑定事件的方法 */
	includeMethods: '*', // ['query', 'append', 'save', 'edit', 'update', 'delete'],
	/** before,after,initialize等方法不被拦截 */
	excludeMethods : [ 'create', 'before', 'after', 'initialize', 'getText', 'url', 'form', '*Listener', '*Validator',
			'applyMethod', 'validate*', 'validator', 'on*', 'before*', 'after*', 'handle*', 'handler', '*Handler',
			'DEFAULT_MAIN_LISTENER', 'DEFAULT_RESULT_HANDLER', 'DEFAULT_VALIDATOR', 'DEFAULT_*' ],
	/** 使用ajax处理方法的 */          
	handleMethods: ['save', 'update', 'delete'],
	services: [],
	/** ajax交互对象 */
	transport: null,
	initialize: function() {
		// 默认仅创建request对象
		this.transport = new Ajax.Support(true);
	},
	/** 执行ajax请求 */
	request: function(url, options) {
		this.transport.request(url, options);
	},
	/** 执行ajax请求，同this.request */
	send: function(url, options) {
		this.request(url, options);
	},
	/** 执行更新容器的请求 */
	requestUpdate: function(container, url, options) {
		if (!this.transport.updater) this.transport.updater = new Ajax.Support.Updater();
		this.transport.update(container, url, options);
	},
	/** 终止ajax交互
	 * @see this#abort()
	 */
	stop: function() {
		this.abort();
	},
	/** 终止ajax交互 */
	abort: function() {
		this.transport.abort();
	},
	create: function() {
		var ServiceSupport = Class.create(Helper.clone(this));
		var service = new ServiceSupport();	// 创建Ajax对象
		service.initialize = undefined;		// 删除initialize方法
		this.services.push(service);
		var properties = $A(arguments);
		var initialize;
	    if (Object.isFunction(properties[0])) initialize = properties.shift();

	    for (var i = 0; i < properties.length; i++) {
	    	Object.extend(service, properties[i]);
	    }
		if (initialize) service['initialize'] = initialize;
	    
	    service['super'] = { };	// 指定父类,仅使用父类的方法
		for (var method in this) {
		    if (Object.isFunction(this[method])) {
		    	// 调用父类的方法时会直接将this指针指向service对象
		    	service['super'][method] = this[method].bind(service);
		    } else {
		    	service['super'][method] = Helper.clone(this[method]);
		    }
		}
		service['includeMethods'] = service['includeMethods'] || this.includeMethods || Service['includeMethods'];
		service['excludeMethods'] = service['excludeMethods'] || this.excludeMethods || Service['excludeMethods'];

		Interceptable.intercept(service, service['includeMethods'], service['excludeMethods']);
		
		// service必须集成ajax.support
		var addDefaultMainListener = true;
	    if (Object.isFunction(service['initialize'])) {
	    	// 创建时就执行initialize方法,返回false则不添加main方法的监听事件
	    	addDefaultMainListener = (false !== service['initialize']());
	    } 
	    if (addDefaultMainListener) {
	    	// 添加main方法的默认监听
	    	service.addListener('main', service.DEFAULT_MAIN_LISTENER);
	    }
		return service;
	},
	containers: Layout.containers,	// 一些容器id
	container: Layout.containers['container'],	// 主要内容显示区域
	/** 可为不同的dom指定innerHTML */
	html: {
		// 'mainHeader': ' ',
		'mainNav': '',
		'mainStatusBar': '',
		'mainFooter': true,
		'mainFooterLefter': false,
		'container': '<table id="dataTable" class="list"><thead id="dataHeader"></thead><tbody id="dataBody" class="nowrap"></tbody></table>'
	},
	/** 加载首页内容的DOM节点 */
	INDEX_CONTAINER: Layout.containers['body.container.header'],
	QUERY_AUTO: false,	// 是否默认进行查询
	/**
	 * main方法执行的默认事件
	 * @returns {Boolean}
	 */
	DEFAULT_MAIN_LISTENER: function() {
		Paging.previousService = Paging.service = null;	// 以便重新划表头
		// alert(this.pagerMaxResults);
		$('pagingEntity.maxResults').value = this.pagerMaxResults;
		$('pagingEntity.pageIndex').value = 1;
		
		if (this['html']) {
			// 指定了添加一些静态的数据
			for (var container in this['html']) {
				var html = this['html'][container];
				container = $(container);
				if (Object.isString(html) && html) {
					// html = html.trim() || '&nbsp;';
					container.innerHTML = (html.trim() || '&nbsp;');
					if (html.trim()) {
						CalendarHelper.observe(container);
					}
				}
				Element[html ? 'show': 'hide'](container);
			}
		}


		// 如果指定了INDEX_CONTAINER,则需要读取通过ajax读取后台数据
		var INDEX_CONTAINER = this.INDEX_CONTAINER;
		var onComplete = (function() {
			Layout.resizeBody();
			if (this.mainHandler) {
				var v = this.mainHandler();
				if (!Object.isUndefined(v) && v === false) return;
			}
			this.query('first');	// 默认不进行查询
		}).bind(this);
		
		if (!Object.isUndefined(this['html'][INDEX_CONTAINER]) || !INDEX_CONTAINER || !(INDEX_CONTAINER = $(INDEX_CONTAINER))) {
			// 加载后默认调用this.query方法
			onComplete();
		} else {
			Element.show(INDEX_CONTAINER);
			$(INDEX_CONTAINER).innerHTML = '';
			this.requestUpdate(INDEX_CONTAINER, this.getUrl('index'), {
				onComplete: onComplete
			});
		}
		return false;	// 由于有ajax,则不执行后面的静听事件
	},
	pagerMaxResults: 20,	// 默认的查询每页数量
	/** main方法,加载后执行的方法 */
	main: function() {
		// 空方法,在各个子类中具体实现
	},
	/** 退出service时调用 */
	exit: function() { },
	/** 重新执行此service */
	refresh: function() {
		this.exit();
		this.main();
	},
	/** 不同操作使用的form对象 */
	forms: {
		'query': 'mainForm',
		'append': 'appendForm',
		'edit': 'editForm'
	},
	
	/** namespace,当前模块使用的路径.最好以/结尾 */
	namespace: '',	
	urls: {
		index: 'index.do',
		'query': 'query.do'
	},
	
	getUrl: function(key) {
		var url = (this.namespace || '') + (Object.isUndefined(this.urls[key]) ? (key + '.do') : this.urls[key]);
		return url;
	},
	
	/** 默认对是使用ajax调用后的处理类.主要是query,save,update,delte方法 */
	handlers: { },
	/**
	 * 对于需要AJAX处理的方法,可调用此方法对ajax返回结果进行处理
	 * @param method 调用方法
	 * @param response
	 */
	handle: function(method, response) {
		var text = response.responseText.trim();
		var result = text.toJSON();
		if (!result) result = { 'message': text };
		var handler = result['handler'];	// 只能是function或String
		if (handler && Object.isString(handler)) handler = this[handler];
		if (!handler && method) handler = this.handlers[method];
		if (handler) return handler.apply(this, [ result, response ]);
		this.DEFAULT_RESULT_HANDLER(method, result);
	},
	
	/**
	 * 对返回结果的默认处理方法.主要处理了query,save,update,delete四种方法
	 * @param method
	 * @param result
	 */
	DEFAULT_RESULT_HANDLER: function(method, result) {
		if (!result) return;
		if (result['message']) alert(result['message']);
		if (!result['success']) return;
		switch (method) {
		case 'query':
			Paging.list(result);
			break;
		case 'save':
			/*
			this.view(result['id'], {
				onSuccess: (function() {
					this.forms['view'] = $($(this.dialogs['view'].body).select('form')[0]);
					var dialog = this.dialogs['append'];
					if (dialog && dialog.visible && dialog != this.dialogs['view']) {
						dialog.hide();	// 将新增框隐藏
					}
				}).bind(this),
			});
			this.query();
			break;
			*/
		case 'update':
			this.view(result['id'], {
				onSuccess: (function(dialog) {
					if (dialog && dialog.visible && dialog != this.dialogs['view']) {
						dialog.hide();	// 将新增/编辑框隐藏
					}
				}).bind(this, this.dialog)
			});
			this.query();
			break;
		case 'delete':
			if (this.dialog && this.dialog['view'].visible) this.dialog.hide();	// 将详细信息框隐藏
			this.query();
			break;
		}
	},

	/* 查询后显示时使用的相关参数 */
	dataHeader: [],	// 表头
	dataHandler: [{	
		// 第1列默认定义的事件为查看数据.事件中的this指针均为当前的service对象
		handler: function(id) {	this.view(id);	}
	}],
	/** 数据处理函数,参数为要处理的那一行的数据数组,如果定义了此函数,则必须返回一个数组 */
	dataFilter: null,
	selector: false,	// 是否显示多选框
	lineNumber: false,	// 是否显示行号
	selectorData: { },	// 用于选择的数据,使用id作为主键
	selectorCount: 0,	// 用于选择的数据的数量
	selected: [],	// 被选中的数据
	onCheckAll: function(checked) { },	// 全选触发的事件
	checkAll: function(checked) {	// 全部选中
		var form = $(this.forms['query']);
		var eles = form.elements['ids'];
		if (!eles) return;
		if (!eles.length) {	// 只有一个勾选框
			eles = [ eles ];
		}
		this.selected = [];
		for (var i = 0, l = eles.length; i < l; i++) {
			eles[i].checked = checked;
			if (checked) this.selected.push(this.selectorData[eles[i].value]);
		}
		
		//this.onCheckAll(checked);
	},
	/**
	 * 选择某个多选框的事件.在子类中实现时,如果需要实现全选事件的触发,通过判断this.selected.length == this.selectorCount 进行判断是否已经全选
	 * @param e 选择框DOM
	 * @param checked 是否选中
	 */
	onCheck: function(e, checked) { },
	/**
	 * 选中某一个数据
	 * @param e 
	 * @param checked 可以省略,否则true/false.表示勾选/不选此选择框
	 */
	check: function(e, checked) {
		var id = e.value;
		if (Object.isUndefined(checked)) checked = e.checked;
		else e.checked = !!checked;
		if (checked) this.selected.push(this.selectorData[id]);
		else this.selected.remove(this.selectorData[id]);
		var f = $('checkAllSelector');
		if (f) f.checked = checked && (this.selected.length == this.selectorCount);
		//this.onCheck(e, checked);
	},
	
	/** 导出的最大数量 */
	MAX_EXPORT_RESULTS: 30000,
	/**
	 * 下载数据
	 * @param all 是否全部下载
	 */
	download: function(all) {
		all = !!all;	// 是否全部导出
		var count = (all ? this.data['total'] : this.data['size']) || 0;
		if (count > this.MAX_EXPORT_RESULTS) {
			alert('要导出的数据超出30000条,请增加查询条件后重新导出!');
			return false;
		}
		var url = this.getUrl('download');
		url += (url.include('?') ? '&' : '?') + 'all=' + !!all;
		if (Object.isArray(this.dataHeader)) {
			var headers = '';
			var $this = this;
			this.dataHeader.each(function(header) {
				if (!Object.isString(header)) {
					header = header['value'] || header['html'] || header['innerHTML'];
				}
				headers += '&headers=' + encodeURIComponent($this.getText(header));
			});
			url += headers;
		}
		var form = $(this.forms['query']);
		Form.setValue(form, this.queryJSONParameters);	// 还原查询的参数
		form.method = 'post';
		form.action = url;
		form.target = '_blank';
		form.submit();
	},

	/** 导出按钮 */
	exportButtons: ['downloadButton', 'downloadAllButton'],
	data: { },	// 查询后的数据
	/** 查询方法 */
	query: function(options) {
		if ('first' == options) {	// 加载模块时的第一次调用
			if (!this.QUERY_AUTO) {
				// return Paging.list({});
				this.exportButtons.each(function(name, index) {
					var e = $(name);
					if (e) {
						e.setAttribute('outerDisabled', 'true'); 
						e.disabled = true;	// 默认不允许导出
					}
				});
				Paging.service = this;
				return;
			}
			options = null;
		}
		// var url = this.namespace + this.urls['query'];
		// options = options || { };
		if (!options || !options['paging']) {
			// 不是使用分页查询,则默认查询第一页的数据
			// alert(Paging);
			this.queryParameters = null;
			this.queryJSONParameters = null;
			Paging.service = this;
			Paging.go(1);
			return;
		}
		var form = $(this.forms['query']);
		if (!this.queryJSONParameters) {
			// 初始化JSON数据的查询参数
			this.queryJSONParameters = Form.serialize(form, { hash: true });
			// if (!Object.isUndefined(this.queryJSONParameters['pageIndex'])) delete this.queryJSONParameters['pageIndex'];
		} else {
			// 还原查询的参数
			Form.setValue(form, this.queryJSONParameters);
		}
		var parameters = this.queryParameters || Form.serialize(form);
		var onSuccess = (function(response) {
			// alert(response.responseText.length + ":" + response.responseText);
			this.handle('query', response);
			this.onCheckAll(false);	// 全不选
			
			var disabled = !this.data || !this.data['total'];
			
			this.exportButtons.each(function(name, index) {
				var e = $(name);
				if (e) {
					e.setAttribute('outerDisabled', 'true'); 
					e.disabled = disabled;	// 默认不允许导出
				}
			});
		}).bind(this);
		options = Object.extend({
			parameters: parameters,
			onSuccess: onSuccess
		}, options || { });
		this.send(this.getUrl('query'), options);
	},

	/**
	 * 查询后执行的方法
	 */
	onQuery: function(response) {
		// this.handle(response, 'query');
	},
	
	dialogs: { },
	/** 
	 * 查看明细信息
	 * @param id 数据主键
	 */
	view: function(id, options) {
		if (!Object.isString(id)) options = id || options;
		options = Object.extend({
			parameters: 'id=' + id,
			minWidth: 800,
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
			this.form = this.forms['view'] = $($(this.dialogs['view'].body).select('form')[0]);
			if (this.viewHandler) this.viewHandler.apply(this, $A(arguments));
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		this.dialog = this.dialogs['view'] = Dialog.open(this.getUrl('view'), this.getText('view'), options);
	},
	
	/** 新增 */
	append: function(options) {
		var dialog = this.dialog = this['dialogs']['append'];
		if (dialog) {
			this.form = this.forms['save']; // = $($(this['dialogs']['append'].body).select('form')[0]);
			dialog.show();
			dialog.repaint();
			return;
		}
		dialog = this.dialog = this['dialogs']['append'] = new Dialog(this.namespace + "appendDialog");

		options = Object.extend({
			width: 900,
			buttons: {
				confirm: {
					text: this.getText('confirm'),
					handler: (function() {
						this.save();
					}).bind(this)
				},
				/*
				reset: {
					text: this.getText('reset'),
					handler: (function() {
						this.forms['save'].reset();
					}).bind(this)
				},
				*/
				cancel: {
					text: this.getText('cancel'),
					handler: function() {
						this.hide();
					}
				}
			}
		}, options || { });
		var onSuccess = options['onSuccess'];
		options['onSuccess'] = (function() {
			this.form = this.forms['save'] = $($(this['dialogs']['append'].body).select('form')[0]);
			if (this.appendHandler) this.appendHandler.apply(this, $A(arguments));
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		
		dialog.open(this.getUrl('append'), this.getText('append'), options);
	},
	/** 保存 */
	save: function() {
		if (!confirm(this.getText('global.save.confirm'))) return;
		var form = $(this.forms['save']);
		if (!form) return;
		var parameters = Form.serialize(form);
		var onSuccess = (function(response) {
			// this.after('save', $A(arguments));
			if (this.saveHandler) this.saveHandler.apply(this, $A(arguments));
			this.handle('save', response);
		}).bind(this);
		this.send(this.getUrl('save'), {
			parameters: parameters,
			onSuccess: onSuccess
		});
		// throw $break;
	},
	validateSave: function() {
		return this.DEFAULT_VALIDATOR('save');
	},
	onSave: function(response) {
		// this.handle(response, 'save');
	},
	
	/**
	 * 编辑
	 * @param id 要编辑的数据
	 * @returns
	 */
	edit: function(id, options) {
		if (!Object.isString(id)) options = id || options;
		options = Object.extend({
			width: 900,
			buttons: {
				confirm: {
					text: this.getText('confirm'),
					handler: (function() {
						this.update();
					}).bind(this)
				},
				/*
				reset: {
					text: this.getText('reset'),
					handler: (function() {
						this.forms['update'].reset();
					}).bind(this)
				},
				*/
				
				cancel: {
					text: this.getText('cancel'),
					handler: (function(id) {
						this.view(id);
					}).bind(this, id)
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
			this.form = this.forms['update'] = $($(this['dialogs']['edit'].body).select('form')[0]);
			if (this.editHandler) this.editHandler.apply(this, $A(arguments));
			if (onSuccess) onSuccess.apply(this, $A(arguments));
		}).bind(this),
		
		this.dialog = this['dialogs']['edit'] = Dialog.open(this.getUrl('edit'), this.getText('edit'), options);
	},

	/** 保存 */
	update: function() {
		if (!confirm(this.getText('global.update.confirm'))) return;
		var form = $(this.forms['update']);
		if (!form) return;
		var parameters = Form.serialize(form);
		var onSuccess = (function(response) {
			// this.after('update', $A(arguments));
			if (this.updateHandler) this.updateHandler.apply(this, $A(arguments));
			this.handle('update', response);
		}).bind(this);

		this.send(this.getUrl('update'), {
			parameters: parameters,
			onSuccess: onSuccess
		});
		// throw $break;
	},
	validateUpdate: function() {
		return this.DEFAULT_VALIDATOR('update');
	},
	onUpdate: function(response) {
		// this.handle(response, 'update');
	},

	/**
	 * 删除一条数据
	 * @param id
	 */
	'delete': function(id) {
		if (!confirm(this.getText('global.delete.confirm'))) return;
		var onSuccess = (function() {
			this.after('delete', $A(arguments));
		}).bind(this);
		this.send(this.getUrl('delete'), {
			parameters: 'id=' + id,
			onSuccess: onSuccess
		});
		throw $break;
	},
	onDelete: function(response) {
		this.handle(response, 'delete');
	},
	

	/**
	 * 默认的校验方法.
	 * method: 执行的方法
	 * target: 操作的对象
	 */
	DEFAULT_VALIDATOR: function(method) {
		var form = $(this.forms[method]);
		if (!form) return true;
		var fields = form.elements;
		var field;
		var value;
		var title;
		var input = false;
		for (var i = 0, l = fields.length; i < l; i++) {
			field = $(fields[i]);
			if (field.type.toLowerCase() == "checkbox" || field.type.toLowerCase() == "radio") {
				// 多选或单选框
				// value = Form.serialize(form, null, field.type, field.name);
				value = Form.serializeElements($A(form.elements(field.name)));
				input = false;
				// continue;
			} else {
				input = ((field.tagName.toLowerCase() == "input" && field.type != "hidden") || field.tagName.toLowerCase() == "textarea") && !field.readOnly  && !field.disabled;
				value = String.trim(Form.Element.getValue(field));
			}
			// alert(field.name + "=" + value);
			title = String.trim(field.getAttribute("alt") || field.getAttribute("title"));
			/* 检验必填 */
			var required = field.getAttribute("requiredValue");
			required = !required && field.hasAttribute('requiredValue') || required && Boolean.parseBoolean(required);
			if (!required && field.type != "hidden") {
				// 根据其父节点或其父节点的前一节点是否含有required样式进行判断
				var node = $(field.parentNode);
				required = node.hasClassName('required');
				if (required && !title) title = Element.getTextContent(node); //.innerHTML;
				if (!required) {
					node = $(node.previousSibling);
					required = node ? node.hasClassName('required') : false;
					if (required && !title) title = Element.getTextContent(node);
				}
			}
			
			if (required && !value) {
				// 该项必填
				alert(title ? ("请" + (input ? "输入" : "选择") + " " + title) : "请完成必须填写的内容!");
				Form.Element.focus(field);
				return false;
			} else if (!value) continue;
			
			var minLength = field.getAttribute("minLength") || field.getAttribute("minlength") || field.getAttribute("min-length");
			var maxLength = field.getAttribute("maxLength") || field.getAttribute("maxlength") || field.getAttribute("max-length");
			var length = value.length;
			if (minLength && length < minLength) {
				alert((title ? title + '的' : '') + '长度最少为:' + minLength + ',现有:' + length);
				Form.Element.focus(field);
				return false;
			}
			if (maxLength && length > maxLength) {
				alert((title ? title + '的' : '') + '长度最大为:' + maxLength + ',现有:' + length);
				Form.Element.focus(field);
				return false;
			}
			
			/* 校验该项必须为数字 */
			var objectClass = field.getAttribute("objectClass") || field.getAttribute("objectclass") || field.getAttribute("object-class") 
						|| field.getAttribute("dataType") || field.getAttribute("datatype") || field.getAttribute("data-type");
			if (!objectClass) {
				if (field.getBoolean('number') || field.getBoolean('Number')) objectClass = 'number';
				else if (field.getBoolean('int') || field.getBoolean('integer') || field.getBoolean('Integer')) objectClass = 'int';
				else if (field.getBoolean('long') || field.getBoolean('Long')) objectClass = 'long';
				else if (field.getBoolean('double') || field.getBoolean('Double')) objectClass = 'double';
				else if (field.getBoolean('float') || field.getBoolean('Float')) objectClass = 'float';
				else if (field.getBoolean('date') || field.getBoolean('Date')) objectClass = 'date';
			}
			objectClass = objectClass ? objectClass.toLowerCase() : objectClass;
			if (value && ['number', 'int', 'integer', 'long', 'double', 'float'].include(objectClass)) {
				if (!NumberHelper.isNumber(value)) {
					// 该项必填
					alert(title ? (title + " 必须为数字!") : "该项数据必须为数字!");
					Form.Element.focus(field);
					return false;
				}
				value = value.replaceAll(",", "");
				var precision = NumberHelper.intValue(field.getAttribute("precision"));	//数字的精度(整数+小数的总位数)
				var scale = field.getAttribute("scale");	// 小数的最大位数,有此值则整数位数为precision-scale
				var pos = value.indexOf(".");
				var minimumFractionDigits = 0;
				if (['int', 'integer', 'long'].include(objectClass)) {
					if (pos > 0) {
						alert(title ? (title + " 必须为整数!") : "该项数据必须为整数!");
						return false;
					}
					scale = 0;	// 整数
				}
				if (precision > 0 && (scale || scale === 0)) { // 指定了精度
					scale = Math.abs(NumberHelper.intValue(scale));	// 小数位数
					pos = (pos > 0 ? pos : value.length) - (value.startsWith("+") || value.startsWith("-") ? 1 : 0);
					minimumFractionDigits = Math.min(value.length - pos, scale);
					// var s = i > 0 ? value.substring(p + 1) : "";
					var p = precision - scale;
					if (pos > p) {
						alert(title ? (title + " 的整数部分最多允许" + p + "位数字!") : "该项数据的整数部分最多允许" + p + "位数字!");
						Form.Element.focus(field);
						return false;
					}
				} else {
					scale = scale ? NumberHelper.intValue(scale) : (pos > 0 ? value.length - pos - 1 : 0);
					minimumFractionDigits = scale;
				}
					
				// 格式化数字
				value = NumberHelper.doubleValue(value);
				// p = NumberHelper.format(value, scale, 0);
				field.value = NumberHelper.format(value, scale, minimumFractionDigits, "");

				// value = NumberHelper.doubleValue(value);
				var min = field.getAttribute("min");
				var equal = true;
				if (min && min.startsWith(">")) {
					min = min.substring(1);
					equal = false;
				}
				if (NumberHelper.isNumber(min) && (value < (min = NumberHelper.doubleValue(min)) || (!equal && value == min))) {
					alert(title ? (title + " 必须大于" + (equal ? "等于" : "") + min + "!") : "该项数据必须大于" + (equal ? "等于" : "") + min + "!");
					Form.Element.focus(field);
					return false;
				}
				
				equal = true;
				var max = field.getAttribute("max");
				if (max && max.startsWith("<")) {
					max = max.substring(1);
					equal = false;
				}
				if (NumberHelper.isNumber(max) && (value > (max = NumberHelper.doubleValue(max)) || (!equal && value == max))) {
					alert(title ? (title + " 必须小于" + (equal ? "等于" : "") + max + "!") : "该项数据必须小于" + (equal ? "等于" : "") +  max + "!");
					Form.Element.focus(field);
					return false;
				}
				
			} else if (value && objectClass == "date") {
				/* 校验日期 */
				if (!DateHelper.isDate(value)) {
					// 该项必填
					alert(title ? (title + " 必须为日期!") : "该项数据必须为日期!");
					Form.Element.focus(field);
					return false;
				}
				var date = DateHelper.parse(value);
				var min = field.getAttribute("min");
				var equal = true;
				if (min && min.startsWith(">")) {
					min = min.substring(1);
					equal = false;
				}
				if ('now' == min) min = new Date();
				else if ('today' == min) {
					min = new Date();
					min.setHours(0);
					min.setMinutes(0);
					min.setSeconds(0);
					min.setMilliseconds(0);
				} else min = DateHelper.parse(min);
				if (min && (equals ? min > date : min >= date)) { // 小于最小值
					alert((title ? title + ' ' : '') + '必须大于' + (equals ? '等于' : '') + ':' + DateHelper.format(min));
					Form.Element.focus(field);
					return false;
				}
				
				equal = true;
				var max = field.getAttribute("max");
				if (max && max.startsWith("<")) {
					max = max.substring(1);
					equal = false;
					if ('now' == max) max = new Date();
					else if ('today' == max) {
						max = new Date();
						max.setHours(0);
						max.setMinutes(0);
						max.setSeconds(0);
						max.setMilliseconds(0);
					} else max = DateHelper.parse(max);
				}
				if (max && (equals ? max < date : max <= date)) { // 小于最小值
					alert((title ? title + ' ' : '') + '必须小于' + (equals ? '等于' : '') + ':' + DateHelper.format(max));
					Form.Element.focus(field);
					return false;
				}
			}
		}
		
		return true;
	}
//	
//	/** 执行before方法用的事件 */
//	beforeListeners: { },
//	/**
//	 * 执行方法前的调用
//	 * @param method 正在执行的方法
//	 * @param args 调用method的参数，已转换为数组形式
//	 */
//	before: function(method, args) {
//		var listeners;
//		var value;
//		if (listeners = this.beforeListeners[method]) {
//			if (Object.isFunction(listeners)) {
//				value = listeners.apply(this, args);
//			} else {
//				for (var i = 0, l = listeners.length; i < l; i++) {
//					value = listeners[i].apply(this, args);
//					if (value === false) break;
//				}
//			}
//		}
//
//		if (value === false) return value;	// 一旦返回false则不继续执行
//		if (listeners = this['before' + method.substring(0, 1).toUpperCase() + method.substring(1)]) {
//			if (Object.isFunction(listeners)) {
//				value = listeners.apply(this, args);
//			}
//		}
//		return value;
//	},
//
//	/** 所有监听事件 */
//	listeners: { },
//	/**
//	 * 执行方法后的调用
//	 * @param method 正在执行的方法
//	 * @param args 调用method的参数，已转换为数组形式
//	 */
//	after: function(method, args) {
//		var listeners;
//		var value;
//		if (listeners = this.listeners[method]) {
//			if (Object.isFunction(listeners)) {
//				value = listeners.apply(this, args);
//			} else {
//				for (var i = 0, l = listeners.length; i < l; i++) {
//					value = listeners[i].apply(this, args);
//					if (value === false) break;
//				}
//			}
//		}
//
//		if (value === false) return value;	// 一旦返回false则不继续执行
//
//		if (listeners = this['after' + method.substring(0, 1).toUpperCase() + method.substring(1)]) {
//			if (Object.isFunction(listeners)) {
//				value = listeners.apply(this, args);
//			}
//		}
//		
//		if (value === false) return value;	// 一旦返回false则不继续执行
//		
//		if (listeners = this['on' + method.substring(0, 1).toUpperCase() + method.substring(1)]) {
//			if (Object.isFunction(listeners)) {
//				value = listeners.apply(this, args);
//			}
//		}
//		return value;
//	},
//	/**
//	 * 增加一个监听事件
//	 * @param method
//	 * @param listener
//	 * @param before
//	 */
//	addListener: function(method, listener, before) {
//		var listeners = this[before === 'validate' ? 'validators' : (before ? 'beforeListeners' : 'listeners')][method];
//		if (!listener) return listeners;
//		if (!listeners) {
//			listeners = this[before === 'validate' ? 'validators' : (before ? 'beforeListeners' : 'listeners')][method] = [];
//		}
//		listeners.push(listener);
//		return listeners;
//	},
//	
//	/**
//	 * 删除method的监听事件
//	 * @param method
//	 * @param listener
//	 */
//	removeListener: function(method, listener, before) {
//		var listeners = this[before === 'validate' ? 'validators' : (before ? 'beforeListeners' : 'listeners')][method];
//		
//		if (Object.isUndefined(listener) || listener === null) {
//			var v = listeners[method];
//			listeners[method] = null;
//			return v;
//		}
//		var listeners = listeners[method];
//		if (listeners) {
//			var index = listeners.indexOf(listener);
//			if (index >= 0) return listeners.splice(index, 1)[0];
//		}
//	},
//
//	/**
//	 * 为method方法添加一个校验器
//	 * @param method
//	 * @param listener
//	 */
//	addValidator: function(method, validator) {
//		var validators = this.validators[method];
//		if (!validator) return validators;
//		if (!validators) {
//			validators = this.validators[method] = [];
//		}
//		validators.push(validator);
//	},
//	
//	/**
//	 * 删除method的校验器
//	 * @param method
//	 * @param validator
//	 */
//	removeValidator: function(method, validator) {
//		if (Object.isUndefined(validator) || validator === null) {
//			var v = this.validators[method];
//			this.validators[method] = null;
//			return v;
//		}
//		var validators = this.validators[method];
//		if (validators) {
//			var index = validators.indexOf(validator);
//			if (index >= 0) return validators.splice(index, 1)[0];
//		}
//	},
//	validators: { },
//	/**
//	 * 检验method方法是否可执行
//	 * @param method
//	 * @returns {Boolean}
//	 */
//	validate: function(method) {
//		var validators;
//		var value = true;
//		var args = $A(arguments);
//		args.shift();
//		if (validators = this.validators[method]) {
//			if (Object.isFunction(validators)) {
//				value = validators.apply(this, args);
//			} else {
//				for (var i = 0, l = validators.length; i < l; i++) {
//					value = validators[i].apply(this, args);
//					if (value === false) break;
//				}
//			}
//		}
//
//		if (value === false) return value;	// 一旦返回false则不继续执行
//		
//		if (validators = this['validate' + method.substring(0, 1).toUpperCase() + method.substring(1)]) {
//			if (Object.isFunction(validators)) {
//				value = validators.apply(this, args);
//			}
//		}
//		return value || Object.isUndefined(value);	//undfiend则视为true
//	}
};

