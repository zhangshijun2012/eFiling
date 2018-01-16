/** 影像文件上传 */
var FileImage = Service.create({
	namespace : 'file/image/',
	/** 加载输入的业务号 */
	handlers : {
		/** query方法的回调 */
		'query' : function(json, response) {
			// alert(response.responseText);
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
			form.elements['file.document.id'].value = this.documentId = json['id'];
			form.elements['file.document.no'].value = this.documentNo = json['no'];
			form.elements['file.type.id'].value = this.fileTypeId = form.elements['fileTypeId'].value;

			form.elements['no'].disabled = true;
			form.elements['fileTypeId'].disabled = true;
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

		// property01字段用于保存DOCUMENT_FILE表的fileModle,1表示核保资料,0表示影像文件
		this.uploader = { };
		this.uploader['parameters'] = 'onSuccess=' + encodeURIComponent('parent.Main.service.save(json, xhq, success);') 
				+ '&show=true&systemCode=' + Main.systemCode + '&property01=0&operator=' + Main.user.id;
	},
	/** // 显示文件上传框 */
	showUploader : function() {
		var form = $(this.forms['query']);
		var f = form.elements['fileTypeId'].options[form.elements['fileTypeId'].selectedIndex];
		
//		var url = this.urls['upload'] + '&fileTitle=' + encodeURIComponent(f.text) + '&businessNo='
//				+ this.documentId + '&property00=' + this.fileTypeId;

		var e = $('uploaderContainer');
		var height = $('mainContainer').offsetHeight - $('policyInputContainer').offsetHeight;
		e.style.height = height + 'px';
		// e.innerHTML = '<iframe src="' + url + '"' + 'width="100%" height="100%" scrolling="auto" frameborder="0"></iframe>';		
		var parameters = this.uploader['parameters'] 
				+ '&fileTitle=' + encodeURIComponent(f.text) + '&businessNo='
				+ this.documentId + '&property00=' + this.fileTypeId;
		var url = Uploader.index(parameters);
		var html = '<iframe src="' + url + '"' + 'width="100%" height="100%" scrolling="auto" frameborder="0"></iframe>';
		e.innerHTML = html;
	},
	mainHandler : function() {
		var types = FileType.all;
		var form = $(this.forms['query']);
		var select = form.elements['fileTypeId'];
		for ( var id in types) {
			var type = types[id];
			if (FileType.FILE_MODEL_IMAGE != type['fileModel']) continue;
			select.add(new Option(type['name'], id));
		}
		this.documentId = '';
		this.documentNo = '';
		this.fileTypeId = '';

		if (PARAMETERS['businessNo']) {
			form.elements['no'].value = PARAMETERS['businessNo'];
			form.elements['no'].readOnly = true;
			form.elements['no'].style.color = 'red';
		} else {
			form.elements['no'].readOnly = false;
		}
		return false;
	},
	doQuery : function() {
		var form = $(this.forms['query']);
		var no = form.elements['no'].value.trim();
		if (!no) {
			alert('请输入要上传影像文件的业务号!');
			return false;
		}
		form.elements['no'].value = no;
		if (!form.elements['fileTypeId'].value.trim()) {
			alert('请选择影像文件类型!');
			return false;
		}

		form.elements['doQueryButton'].disabled = true;
		if (this.documentNo == no) {
			form.elements['file.type.id'].value = this.fileTypeId = form.elements['fileTypeId'].value;

			form.elements['no'].disabled = true;
			form.elements['fileTypeId'].disabled = true;
			form.elements['requeryButton'].disabled = false;
			// 显示文件上传框
			this.showUploader();
			return;
		}
		this.query.apply(this, arguments);
	},
	query : function() {
		var form = $(this.forms['query']);
		if (!form.elements['no'].value.trim()) return;
		Service.query.apply(this, arguments);
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

	/** 保存上传结果 */
	save : function(data, xhq, success) {
		// alert( xhq.responseText );
		if (!data) {
			// 失败
			data = {
				'message' : xhq.responseText
			};
		}
		if (data['message']) alert(data['message']);
		if (!data['success']) return;

		var form = $(this.forms['query']);
		var files = data['list'];
		var xml = '<?xml version="1.0" ?><root>' + '<document id="' + this.documentId + '">';
		for ( var i = 0, l = files.length; i < l; i++) {
			data = files[i];
			xml = xml + '	<file type="' + this.fileTypeId + '">' + '		<id>' + data['id'] + '</id>' + '		<name>'
					+ data['fileName'] + '</name>' + '		<count>' + (data['pageCount'] || 1) + '</count>' + '		<size>'
					+ data['fileSize'] + '</size>' + '	</file>';
		}
		xml = xml + '</document>' + '</root>';
		// alert(xml);
		var form = $(this.forms['query']);
		form.elements['xml'].value = xml;
		var parameters = Form.serialize(form);
		// alert(parameters);
		var onSuccess = (function(response) {
			this.handle('save', response);
		}).bind(this);
		this.send(this.getUrl('save'), {
			parameters : parameters,
			onSuccess : onSuccess
		});
	}

});