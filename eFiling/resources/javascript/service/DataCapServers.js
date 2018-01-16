(function() {
	// 生产上datacap服务器地址
	//	 10.132.11.37   CBJ-EF-01
	//	 10.132.21.37   CCQ-EF-01
	//	 10.132.16.37   CZJ-EF-01
	//	 10.132.23.37   CGD-EF-01
	//	 10.132.33.37   CSD-EF-01
	var SERVERS = {	// 配置各分公司的服务端IP地址或域名，每个地址其后面的应用必须一致
		'00000000' : '10.132.21.37', 	// 总公司,总公司使用重庆事业部的地址
		'50000000' : '10.132.21.37', 	// 重庆事业部
		'11000000' : '10.132.11.37', 	// 北京分公司
		'33000000' : '10.132.16.37',	// 浙江
		'37000000' : '10.132.33.37',	// 山东
		'44000000' : '10.132.23.37',	// 广东
		'51000000' : '10.132.40.37'		// 四川
	};
	// 测试环境使用
	var SERVERS = {	// 配置各分公司的服务端IP地址或域名，每个地址其后面的应用必须一致
		'00000000' : '10.132.21.29', 	// 总公司,总公司使用重庆事业部的地址
		'33000000' : '10.132.21.29'	// 浙江
	};
	/** 
	 * 根据参数构造分公司的地址
	 * @param url 应用地址
	 * @param port 端口号
	 * @param protocol 协议，主要是http或https
	 */
	function createServers(url, port, protocol) {
		var servers = { };
		for (var companyId in SERVERS) {
			var html = (protocol ? protocol : (port == 443 ? 'https' : 'http'))  + '://';
			html += SERVERS[companyId]; // 测试地址统一使用10.132.21.29 //
			html += port ? ':' + port : '';
			html += '/' + url;
			servers[companyId] = html;
		}
		return servers;
	}

	var ROOT_SERVER = '00000000';
	/** 配置扫描时DataCapServer的地址 */
	window['DataCapServers'] = DataCapServers = {
		// dataCap的地址中需要传入userId
		servers: createServers('tmweb.net/default.aspx?why=true'),
		/** 根据分公司得到扫描地址 */
		get : function(options) {
			options = options || {};
			var parameters;
			if (Object.isString(options)) {
				parameters = options;
				options = {};
			} else parameters = options['parameters'];
			var key = options['companyId'] || options['company'] || Main.user.company['id'];
			var server = this.servers[key];
			if (!server) server = this.servers[key.substring(0, 2) + '000000'];
			if (!server) server = this.servers[ROOT_SERVER];
			server += (server.include('?') ? '&' : '?') + 'userId='
					+ (options['userNo'] || options['user'] || Main.user['no']);
			if (parameters) server += '&' + parameters;
			return server;
		}
	};

	/** 扫描图片预览地址 */
	window['DataCapPreviewServers'] = DataCapPreviewServers = {
		servers: createServers('readtif', 8080),
		/** 根据分公司得到图片预览地址 */
		get : function(options) {
			options = options || {};
			var key = options['companyId'] || options['company'] || Main.user.company['id'];
			var server = this.servers[key];
			if (!server) server = this.servers[key.substring(0, 2) + '000000'];
			if (!server) server = this.servers[ROOT_SERVER];
			server += '/' + options['xmlId'] + '/' + options['name'];
			return server;
		}
	};

	var namespace = SERVER_ROOT + 'file/scan/';
	/** 读取XML的webservice地址 */
	window['WebServiceServers'] = WebServiceServers = {
		servers: createServers('XMLOperation/services/XMLOperation', 8080),
		/** 根据分公司得到WebService地址 */
		get : function(companyId) {
			var key = companyId || Main.user.company['id'];
			var server = this.servers[key];
			if (!server) server = this.servers[key.substring(0, 2) + '000000'];
			if (!server) server = this.servers[ROOT_SERVER];
			return encodeURIComponent(server);
		},
		/** 得到读取xml的WebService地址 */
		getReadUrl : function(options) {
			// return SERVER_ROOT + 'ecm.xml';
			options = options || {};
			var parameters;
			if (Object.isString(options)) {
				parameters = options;
				options = {};
			} else parameters = options['parameters'];
			var url = namespace + 'read.do?url=' + this.get(options['companyId'] || options['company']);
			if (parameters) url += '&' + parameters;
			return url;
		},
		/** 得到更新xml的WebService地址 */
		getUpdateUrl : function(options) {
			options = options || {};
			var parameters;
			if (Object.isString(options)) {
				parameters = options;
				options = {};
			} else parameters = options['parameters'];
			var url = namespace + 'update.do?url=' + this.get(options['companyId'] || options['company']);
			if (parameters) url += '&' + parameters;
			return url;
		},
		/** 得到取消上传的WebService地址 */
		getCancelUrl : function(options) {
			options = options || {};
			var parameters;
			if (Object.isString(options)) {
				parameters = options;
				options = {};
			} else parameters = options['parameters'];
			var url = namespace + 'cancel.do?url=' + this.get(options['companyId'] || options['company']);
			if (parameters) url += '&' + parameters;
			return url;
		}
	};
})();