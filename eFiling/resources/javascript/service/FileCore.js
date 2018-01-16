/** 影像文件上传 */
var FileCore = Service.create({
	namespace : 'file/core/',
	/** 加载输入的业务号 */
	handlers : {
		/** query方法的回调 */
		'query' : function(json, response) {
			//alert(response.responseText);
			//return;
			if (!json) json = {
				message : response.responseText
			};
			if (json['messag']) alert(json['message']);
			var form = $(this.forms['query']);
			if (!json['success']) {
				if (!json['messag']) alert('业务号不存在,请重新输入!\n如果输入的投保单号尚未核保通过,请待核保通过之后再上传影像文件!');
				form.elements['doQueryButton'].disabled = false;
				return;
			}
			var types = json['fileTypes'] || { };
			var select = form.elements['fileTypeId'];
			
			/*
			var optgroup = document.getElementById('fileModelRequiredOptgroup');
			var opt;
			while (opt = optgroup.firstChild) {
				// 删除原来的必选资料
				optgroup.removeChild(opt);
			}
			*/
			// 重新设置必选资料
			var insertBeforeIndex = 1;	// 0为空选项
			var inserted = { };
			for (var id in types) {
				if (inserted[id]) continue;
				var type = types[id];
				if (FileType.FILE_SINED == type['signed']) continue;	// 签章资料不能上传
				inserted[id] = type;
				for (var i = 0, options = select.options, l = options.length; i < l; i++) {
					if (options[i].value == id) {
						// 删除此已有的资料
						if (!options[i].selected) {	// 当前被选中则不删除
							select.remove(i);
						}
						break;
					}
				}
				select = form.elements['fileTypeId'];
				var option = ElementHelper.createElement('option', { 'value': id, 'innerHTML': type['name'], 'fileModel': type['fileModel'] });
				select.insertBefore(option, select.options[insertBeforeIndex++]);
			}
			form.elements['file.document.id'].value = this.documentId = json['id'];
			form.elements['file.document.no'].value = json['no'];	//  this.documentNo =


			if (this.queryOnblurEvent) {
				this.queryOnblurEvent = false;
				return false;
			}

			var select = form.elements['fileTypeId'];
			this.fileTypeId = select.value;
			this.fileModel = select.options[select.selectedIndex].getAttribute('fileModel') || '0';
			select.disabled = true;
			
			form.elements['file.type.id'].value = this.fileTypeId; // = form.elements['fileTypeId'].value;

			form.elements['no'].disabled = true;
			// form.elements['fileTypeId'].disabled = true;
			form.elements['requeryButton'].disabled = false;
			
			this.showUploader();
		},

		save : function(json) {
			alert(json['message']);
			if (json['success']) {
				// 成功
				// $('uploaderContainer').innerHTML = '';
			}
		}
	},
	
	main : function() {
		this.html['mainFooter'] = false;
		this.uploader = { };
		// property01字段用于保存DOCUMENT_FILE表的fileModle,1表示核保资料,0表示影像文件
		// property02用于保存DOCUMENT_FILE的id,此处留空
		// property03=upload用于表示上传的资料
		this.uploader['parameters'] = 'onSuccess='
				+ encodeURIComponent('parent.Main.service.save(json, xhq, success);') + '&show=true&systemCode='
				+ Main.systemCode + '&property03=upload&operator=' + Main.user.id;
	},
	/** 显示文件上传框 */
	showUploader : function() {
		var fileTitle = DateHelper.format(new Date());
		//var url = this.urls['upload'] + '&entity.fileTitle=' + fileTitle + '&businessNo='
		//		+ this.documentId + '&property00=' + this.fileTypeId + '&property01=' + this.fileModel;
		//if ('1' == this.fileModel) {
			// 承保资料上传需要将多个文件合并为一个文件,添加下面的参数
		//	url = url + '&handlerClass=eFiling';
		//}
		var e = $('uploaderContainer');
		var height = $('mainContainer').offsetHeight - $('policyInputContainer').offsetHeight;
		e.style.height = height + 'px';
		// e.innerHTML = '<iframe src="' + url + '"' + 'width="100%" height="100%" scrolling="auto" frameborder="0"></iframe>';
		
		var parameters = this.uploader['parameters'] 
			+ '&entity.fileTitle=' + fileTitle + '&businessNo='
			+ this.documentId + '&property00=' + this.fileTypeId + '&property01=' + this.fileModel;
		if ('1' == this.fileModel) {
			// 承保资料上传需要将多个文件合并为一个文件,添加下面的参数
			this.batchId = Math.uuid();
			parameters += '&batch=' + this.batchId; 
			parameters += '&handlerClass=eFiling';
		}
		var url = Uploader.index(parameters);
		var html = '<iframe src="' + url + '"' + 'width="100%" height="100%" scrolling="auto" frameborder="0"></iframe>';
		e.innerHTML = html;
	},
	
	//加载资料类型
	mainHandler : function() {
		var types = FileType.fileTypes;
		var form = $(this.forms['query']);
		var select = form.elements['fileTypeId'];
		//var optgroup = ElementHelper.createElement('optgroup', { 'label': '核保资料', 'id': 'fileModelRequiredOptgroup' }, select);
		//ElementHelper.createElement('option', { 'value': 1, 'innerHTML': '身份证' }, optgroup);
		//ElementHelper.createElement('option', { 'value': 2, 'innerHTML': '行驶证' }, optgroup);

		//optgroup = ElementHelper.createElement('optgroup', { 'label': '可选资料', 'id': 'fileModelFileOptgroup' }, select);
		types.each(function(type){
			var id = type['id'];
			//核保要求资料。并且不是签章资料
			if ((FileType.FILE_MODEL_FILE != type['fileModel']) || 
				(FileType.FILE_SINED == type['signed'])) return;
			ElementHelper.createElement('option', { 'value': id, 'innerHTML': type['name'], 'fileModel': type['fileModel'] }, select);			
		});
		/*
		for ( var id in types) {
			var type = types[id];
			//核保要求资料。并且不是签章资料
			if ((FileType.FILE_MODEL_FILE != type['fileModel']) || 
				(FileType.FILE_SINED == type['signed'])) continue;
			ElementHelper.createElement('option', { 'value': id, 'innerHTML': type['name'], 'fileModel': type['fileModel'] }, select);
		}
		*/

		// var optgroup = ElementHelper.createElement('optgroup', { 'label': '影像文件', 'id': 'fileModelImageOptgroup' }, select);
		types.each(function(type){
			var id = type['id'];
			//影像资料
			if ((FileType.FILE_MODEL_IMAGE != type['fileModel'])) return;
			// optgroup.add(new Option(type['name'], id));
			ElementHelper.createElement('option', { 'value': id, 'innerHTML': type['name'], 'fileModel': type['fileModel'] }, select);		
		});
		/*
		for ( var id in types) {
			var type = types[id];
			//影像资料
			if ((FileType.FILE_MODEL_IMAGE != type['fileModel'])) continue;
			// optgroup.add(new Option(type['name'], id));
			ElementHelper.createElement('option', { 'value': id, 'innerHTML': type['name'], 'fileModel': type['fileModel'] }, select);
		}
		*/
		
		this.documentId = '';
		this.documentNo = '';
		this.fileTypeId = '';
		this.fileModel = '1';

		if (PARAMETERS['businessNo']) {
			form.elements['no'].value = PARAMETERS['businessNo'];
			form.elements['no'].readOnly = true;
			form.elements['no'].style.color = 'red';
			
			this.queryOnblur();
		} else {
			form.elements['no'].readOnly = false;
		}
		return false;
	},
	
	//上传承保资料文件
	doQuery : function() {
		var form = $(this.forms['query']);
		var no = form.elements['no'].value.trim();
		if (!no) {
			alert('请输入要上传资料类型的业务号!');
			return false;
		}
		form.elements['no'].value = no;
		if (!form.elements['fileTypeId'].value.trim()) {
			alert('请选择资料类型!');
			return false;
		}
		form.elements['doQueryButton'].disabled = true;
		// alert(this.documentNo + ',' + no);
		if (this.documentNo == no) {
			var select = form.elements['fileTypeId'];
			this.fileTypeId = select.value;
			this.fileModel = select.options[select.selectedIndex].getAttribute('fileModel') || '0';
			select.disabled = true;
			form.elements['file.type.id'].value = this.fileTypeId;
			form.elements['no'].disabled = true;
			form.elements['requeryButton'].disabled = false;
			
			
			// 显示文件上传框
			this.showUploader();
			return;
		}
		this.documentNo == no;	// 输入的业务号
		this.query.apply(this, arguments);
	},

	query : function() {
		var form = $(this.forms['query']);
		if (!form.elements['no'].value.trim()) return;
		Service.query.apply(this, arguments);
	},
	
	/**
	 * 鼠标离开业务号时触发的事件
	 */
	queryOnblur: function() {
		var form = $(this.forms['query']);
		// var select = form.elements['fileTypeId'];

		var no = form.elements['no'].value.trim();
		if (!no || this.documentNo == no) return;
		
		this.queryOnblurEvent = true;
		/*
		var optgroup = document.getElementById('fileModelRequiredOptgroup');
		var opt;
		while (opt = optgroup.firstChild) {
			// 删除原来的必选资料
			optgroup.removeChild(opt);
		}*/
		this.query.apply(this, arguments);
	},

	/** 重新上传的操作 */
	reQuery : function() {
		var form = $(this.forms['query']);
		form.elements['file.document.id'].value = '';
		form.elements['file.type.id'].value = '';
		form.elements['xml'].value = '';

		// form.elements['no'].value = '';
		form.elements['no'].disabled = false;
		form.elements['fileTypeId'].value = '';
		form.elements['fileTypeId'].disabled = false;

		form.elements['requeryButton'].disabled = true;
		form.elements['doQueryButton'].disabled = false;

		$('uploaderContainer').innerHTML = '';
	},

	/** 文件上传完成 */
	save : function(data, xhq, success) {
		if (!data) {
			// 失败
			data = {
				'message' : xhq.responseText
			};
		}
		if (data['message']) alert(data['message']);
		if (!data['success']) return;

		//var form = $(this.forms['query']);
		var files = data['list'];
		var xml = '<?xml version="1.0" ?><root>' + '<document id="' + this.documentId + '">';
		for ( var i = 0, l = files.length; i < l; i++) {
			data = files[i];
			xml = xml 
				+ '	<file type="' + this.fileTypeId + '">' 
				+ '		<id>' + data['id'] + '</id>' 
				+ '		<name>' + data['fileName'] + '</name>' 
				+ '		<count>' + (data['pageCount'] || 1) + '</count>' 
				+ '		<size>' + data['fileSize'] + '</size>' 
				+ '	</file>';
		}
		xml = xml + '</document>' + '</root>';
		var form = $(this.forms['query']);
		form.elements['xml'].value = xml;
		var parameters = Form.serialize(form);
		var onSuccess = (function(response) {
			var data = response.responseText;
			if(data) alert("上传成功！");
		}).bind(this);
		this.send(this.getUrl('save'), {
			parameters : parameters,
			onSuccess : onSuccess
		});
	}
});