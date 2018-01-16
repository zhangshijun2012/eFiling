/**
 * 文件上传下载对象的地址
 */
var Uploader = {
	namespace: '/uploader/',
	actions: {	// 可能的action,
		'open': '',
		'index': '',
		'show': 'show.do',
		'query': 'query.do',
		'list': 'list.do',
		'view': 'view.do',
		'read': 'read.do',
		'download': 'download.do',
		'preview': 'preview.do'
	},
	
	/**
	 * 执行action,如果options中指定了onSuccess,这执行此onSuccess方法,否则返回action对应的URL
	 * @param action
	 * @param options
	 * @returns
	 */
	execute: function(action, options) {
		var url = this.namespace + this.actions[action];
		if (!options) {
			// TODO
			return url;
		}
		var parameters;
		if (Object.isString(options)) {
			parameters = options;
			options = {};
		} else {
			parameters = options['parameters'] || '';
			if (parameters && !Object.isString(parameters)) {
				parameters = Object.toQueryString(parameters);
			}
		}
		if (parameters.charAt('0') != '?') url += '?';
		url += parameters;
		url += '&downloadDisabled=true';	// 不允许下载
		
		var onSuccess = options['onSuccess'];
		if (onSuccess) {
			// 执行onSuccess函数
			return onSuccess(url, options);
		}
		
		return url;
	},
	/**
	 * 显示文件上传框
	 * @param parameters 访问uploader地址的参数
	 * @param options TempDialog.show的options参数
	 * @return Dialog 打开的文件上传窗口
	 */
	dialog: function(parameters, options) {
		if (!options && !Object.isString(parameters)) {
			options = parameters;
			parameters = null;
		}
		parameters = parameters || options['parameters'];
		options = Object.extend({
				title: '文件上传',
				width : 670,
				height : 300,
				buttons : {}
		}, options || { });
		var onSuccess = function(url) {
			var html = options['html'];
			if (!html) html = '<iframe src="' + url + '"' + 'width="650" height="255" scrolling="no" frameborder="0"></iframe>';
			else if (Object.isFunction(html)) html = html(url);
			else if (Object.isString(html)) html = html.replace(new RegExp('\\${\\s*url\\s*}', "gi"), url);
			return TempDialogManager.show(html, options);
		};
		
		return Uploader.open(parameters, onSuccess);
	}
};
(function(Uploader) {
	for (var action in Uploader.actions) {
		Uploader[action] = (function(action) {
			return function(parameters, options) {
				if (!options) {
					options = parameters;
					parameters = null;
				}
				if (Object.isFunction(options)) {
					var onSuccess = options;
					options = { 'onSuccess' : onSuccess };
				}
				if (options && parameters && !Object.isString(options)) {
					options['parameters'] = parameters;
				}
				return this.execute(action, options); 
			};
		})(action);	
	}
})(Uploader);


var FileIndexService = {
	/**
	 * 打开一个新窗口
	 * 
	 * @param url
	 * @param options
	 */
	open : function(url, options) {
		options = options || {};
		if (options == window || options['window'] == window) {
			// 正在当前窗口打开url
			window.location.href = url;
			return;
		}
		options = Object.extend({
			'name': '',
			'arguments': '' // 'width=800, height=600, top=100, left=100'
		}, options || {});
		return window.open(url, options['name'], options['arguments']);
	},
	/**
	 * 查看单个文件
	 * 
	 * @param id
	 * @param options
	 */
	view : function(id, options) {
		var $this = this;
		Uploader.view({
			parameters: { id: id, message: '<p>您要查看的文件不存在，可能的原因：</p>'
					+ '<ol style="line-height: 1.8">'
					+ '<li>如果是<b style="color:red">当天扫描的文件</b>，则您扫描的影像文件系统正在处理，请于明日查看！</li>'
					+ '<li>如果是<b style="color:red">昨天或更早扫描的文件</b>，请重新进行扫描上传！</li><ol>' },
			onSuccess: function(url) {
				return $this.open(url, options);
			}
		});
	},
	
	/**
	 * 显示文件
	 * @param parameters 文件查询参数
	 * @param options
	 */
	show : function(parameters, options) {
		var $this = this;
		Uploader.show({
			parameters: parameters,
			onSuccess: function(url) {
				return $this.open(url, options);
			}
		});
	}
};