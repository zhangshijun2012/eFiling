var BarcodePrinter = Class.create({
	/** 打码机控件的Object对象 */
	// printer : 'PSKPrt',
	/** 打码机在windows中的名字 */
	// printerName: '',
	/** 安装的打印机名称,在每台电脑上安装时需要命名为这个,更改USB口后名称会变 */
	DEFAULT_PRINTER_NAME: 'POSTEK Q8/200',
	maxCount: 999,	// 最大的打印数量
	/** 创建对象时的初始化 */
	initialize : function(options) {
	},
	/** 创建控件的调用对象 */
	create: function(options) {
		this.options = Object.extend({
			printer : 'PSKPrt',
			printerName : this.DEFAULT_PRINTER_NAME // 安装的打印机名称必须固定
		}, options || {});
		this.printer = this.options['printer'];
		if (Object.isString(this.printer)) {
			var printer = this.printer;
			this.printer = window[printer] || $(printer);
			if (!this.printer) {
				var div = document.createElement('div');
				Element.hide(div);
				$('footer').appendChild(div);
				div.innerHTML = '<object id="' + printer + '" classid="clsid:81C07687-3353-4ABA-B108-94BCE81E5CBA" '
						+ 'codebase="resources/PSKPrn.ocx#version=1,0,0,1" width="0" height="0"></object>';

				this.printer = window[printer] || $(printer);
			}
		}
		this.printerName = this.options['printerName'];
	},
	/**
	 * 打开打印机端口
	 * 
	 * @param printerName 打印机在windows中的名字
	 */
	open : function(printerName) {
		if (!this.printer) this.create();
		this.printerName = printerName || this.DEFAULT_PRINTER_NAME;
		this.printer.OpenPort(this.printerName);
	},
	close : function() {
		if (this.printer) this.printer.ClosePort();
	},
	/**
	 * 打印条码值
	 * 
	 * @param code 条码值
	 * @param count 打印的数量,可以省略,取options['count']
	 * @param options 参数,如果为String,表示打印的文字
	 * 
	 * <pre>
	 * 此方法调用控件的方法:
	 * PTKDrawBarcode (long  px, long  py, 
	 * 				long  pdirec, LPCSTR   pCode, 
	 * 				long  NarrowWidth,  long pHorizontal,long pVertical,  
	 * 				short ptext, LPCSTR pstr )
	 * px: 设置X坐标,以点(dots)为单位. 
	 * py: 设置Y坐标,以点(dots)为单位.
	 * pdirec:选择条码的打印方向. 0—不旋转;1—旋转90°; 2—旋转180°; 3—旋转270°.
	 * pCode: 选择要打印的条码类型.
	 * NarrowWidth: 设置条码中窄单元的宽度,以点(dots)为单位.
	 * pHorizontal: 设置条码中宽单元的宽度,以点(dots)为单位.
	 * pVertical: 设置条码高度,以点(dots)为单位.
	 * ptext: 选’N’ 对应ASCII值 78 则不打印条码下面的人可识别文字,
	 * 		  选’B’ 对应ASCII值 66 则打印条码下面的人可识别文字.
	 * pstr：一个长度为1-100的字符串。   
	 * 返回值：0 -&gt; OK；
	 * 		  其它返回值请参考章节：  this.printer.ocx  错误返回值解析。 
	 *  
	 * 范例: 
	 * PTKDrawBarcode (50,30,0,’1A’,1,1,10,78,”123456”); 
	 * 
	 * long PTKDrawText ( long    px, long   py,   
	 * 	long    pdirec, long    pFont,   
	 * 	long   pHorizontal, long   pVertical, 
	 * 	short ptext, LPCSTR pstr ); 
	 * 	
	 * 	参数: 
	 * 	  	px: 设置X坐标,以点(dots)为单位. 
	 * 	    py: 设置Y坐标,以点(dots)为单位. 
	 * 	    pdirec: 选择文字的打印方向. 0—不旋转;1—旋转90°; 2—旋转180°; 3—旋转270°. 
	 * 	    pFont: 选择内置字体或软字体. 1—5: 为打印机内置字体; A—Z: 为下载的软字体. 
	 * 	a为打印机内置24*24简体汉字. 
	 * 	取值  描述 
	 * 	1  西文字体1 
	 * 	2  西文字体2 
	 * 	3  西文字体3 
	 * 	4  西文字体4 
	 * 	5  西文字体5 
	 * 	a  24点阵中文宋体 
	 * 	A-Z  软字体 
	 * 
	 * 	    pHorizontal: 设置点阵水平放大系数. 可选择:1—24. 
	 * 	    pVertical: 设置点阵垂直放大系数. 可选择:1—24. 
	 * 	    ptext:	选’N’对应ASCII值 78 则打印正常文本(如黑字白底文本),  
	 * 				选’R’ 对应ASCII值 82则打印文本反色文本(如白字黑底文本). 
	 * 		pstr：一个长度为1-100的字符串。
	 * 
	 * </pre>
	 */
	print : function(code, count, options) {
		// alert(options + ',' + count);
		if (!options && !Object.isNumber(count)) {
			options = count;
			count = 0;
		}
		// alert(options + ',' + count);
		options = options || {};
		if (Object.isString(options)) options = {
			text : options
		};
		// alert(options['text'] + ',' + count);
		var text = options['text'] || '';
		// alert(text + ',' + count);
		options['count'] = count = count || options['count'] || 1; // 打印的数量
		if (count > this.maxCount) {
			alert('打印数量最大为999!');
			return false;
		}
		var only = options['only'];	// 是否进打印一次，如果此参数为false或未定义，则一次打印，会调用open和close，否则可能是打印多个数据，由外部调用open和close
		only = only || Object.isUndefined(only);
		if (only) this.open();

		// this.printer.PTKDrawBarcode(0, 0, 0, '1', 2, 1, 40, 66, code);
		// this.printer.OpenPort("POSTEK Q8/200");
		this.printer.PTKClearBuffer();
		this.printer.PTKSetPrintSpeed(4);
		this.printer.PTKSetDarkness(10);
		this.printer.PTKSetCoordinateOrigin(150, 105);
		this.printer.PTKSetLabelHeight(160, 24);
		this.printer.PTKSetLabelWidth(400);

		//if (text) this.printer.PTKDrawText(360, 135, 2, 2, 1, 1, 78, text);
		if (text) this.printer.PTKDrawText(355, 140, 2, 2, 1, 1, 78, text);
		this.printer.PTKDrawBarcode(350, 110, 2, '1', 3, 1, 80, 66, code);
		this.printer.PTKPrintLabel(count, 1);
		// this.printer.ClosePort();

		if (only) this.close();
	},
	/**
	 * 打印一个QR二维码
	 * @param code 条码值
	 * @param count 打印的数量
	 * @param options 参数,如果为String,表示打印的文字
	 * 
	 * <pre>
	 * 此方法调用控件的方法: 
	 * long PTKDrawBar2DQR(long x, long y, long w, long v, long o, long r, long m, long g, long s, LPCSTR pstr);
	 * 参数：
	 * int     x;         ●X 座标。
	 * int     y;         ●Y 座标。备注：1 dot = 0.125 mm。
	 * int     w;         ●最大列印宽度，单位 dots。
	 * int     v;         ●最大列印高度，单位 dots。
	 * int     o;         ●设置旋转方向, 范围：0～3。
	 * int     r;         ●设置放大倍数，以点(dots)为单位,范围值：(1 - 9)。
	 * int     m;         ●QR码编码模式选择,范围值(0 - 4)。
	 * int     g;         ●QR码纠错等级选择,范围值(0 - 
	 * int     s;         ●QR码掩模图形选择,范围值(0 - 8)。
	 * LPCTSTR pstr;      ●资料字串。
	 * </pre>
	 */
	print2DQR : function(code, count, options) {
		if (!options && !Object.isNumber(count)) {
			options = count;
			count = 0;
		}
		// alert(options + ',' + count);
		options = options || {};
		if (Object.isString(options)) options = {
			text : options
		};
		// alert(options['text'] + ',' + count);
		var text = options['text'] || code;
		// alert(text + ',' + count);
		options['count'] = count = count || options['count'] || 1; // 打印的数量
		if (count > this.maxCount) {
			alert('打印数量最大为999!');
			return false;
		}
		var only = options['only'];
		only = only || Object.isUndefined(only);
		if (only) this.open();

		// this.printer.PTKDrawBarcode(0, 0, 0, '1', 2, 1, 40, 66, code);
		// this.printer.OpenPort("POSTEK Q8/200");
		this.printer.PTKClearBuffer();
		this.printer.PTKSetPrintSpeed(4);
		this.printer.PTKSetDarkness(10);
		this.printer.PTKSetCoordinateOrigin(150, 105);
		this.printer.PTKSetLabelHeight(160, 24);
		this.printer.PTKSetLabelWidth(400);

		if (text) this.printer.PTKDrawText(355, 140, 2, 2, 1, 1, 78, text);
		// this.printer.PTKDrawBarcode(350, 105, 2, '1', 3, 1, 60, 66, code);
		this.printer.PTKDrawBar2DQR(220, 15, 100, 100, 2, 4, 2, 0, 0, code);
		this.printer.PTKPrintLabel(count, 1);
		// this.printer.ClosePort();

		if (only) this.close();
	}
});

BarcodePrinter.DEFAULT_PRINTER_NAME = 'POSTEK Q8/200';
BarcodePrinter.MAX_COUNT = 999;