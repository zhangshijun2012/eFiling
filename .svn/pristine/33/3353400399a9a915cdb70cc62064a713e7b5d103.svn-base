/** 单份档案管理 */
var FileService = Service.create({
	namespace : 'file/',
	/* 归档状态 */
	/** 00.未归档 */
	DOCUMENT_STATUS_UNFILE: "00",
	/** 01.归档不齐 */
	DOCUMENT_STATUS_LACK: "01",
	/** 11.归档齐全 */
	DOCUMENT_STATUS_FILE: "11",
	/** 10.手动归档齐全 */
	DOCUMENT_STATUS_FILE_MANUAL: "10",
	DOCUMENT_STATUS: {
		
	},
	
	view: function(id, options) {
		options = Object.extend({
			width: 800
		}, options || { });
		Service.view.apply(this, [ id, options ]);
	},
	
	/**
	 * 查看影像文件
	 * 
	 * @param id
	 */
	viewImages: function(id) {
		FileIndexService.show({
			'systemCode': SYSTEM_CODE,
			'businessNo': id,
			'property01': '0'	// 0表示影像文件
		});
		/*
		var url = this.getUrl('viewImages');
		url += ((url.include('?') ? '&' : '?') + 'id=' + id);
		window.open(url, '');
		*/
	}
});