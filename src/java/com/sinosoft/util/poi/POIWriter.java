package com.sinosoft.util.poi;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sinosoft.util.POIHelper;

/**
 * 用于写入excel
 * 
 * @author LuoGang
 * 
 */
public class POIWriter extends POIObject {
	public POIWriter() {
		super();
		// TODO 自动生成构造函数存根
	}

	public POIWriter(String version, POIEventListener listener) {
		super(version, listener);
		// TODO 自动生成构造函数存根
	}

	public POIWriter(String version) {
		super(version);
		// TODO 自动生成构造函数存根
	}

	public POIWriter(List[] data, String version) {
		this(version);
		this.data = data;
	}

	public POIWriter(Object[] identifiers, List[] data, String version) {
		this(data, version);
		this.identifiers = identifiers;
	}

	/**
	 * 所有的单元格的类型.如需要取第sheet个工作簿的第i行j列的类型,则使用cellTypes.get(sheet + "*" + i + "*" + j).<br>
	 * 其次根据其数据内容取得,最后根据数据的class类型取得,则使用cellTypes.get(class)
	 */
	Map<Object, Integer> types;

	/**
	 * 所有的单元格的数据样式.
	 */
	Map<Object, String> formats;

	/**
	 * 列宽.每256个单位表示一个字符
	 */
	protected int[][] widths;

	public int[][] getWidths() {
		return widths;
	}

	public void setWidths(int[][] widths) {
		this.widths = widths;
	}

	/**
	 * 得到key对应的cellType
	 * 
	 * @param key
	 * @return
	 */
	public Integer getCellType(Object key) {
		return types == null ? null : types.get(key);
	}

	/**
	 * 设定key所对应的单元格类型
	 * 
	 * @param key
	 * @param cellType
	 */
	public void setCellType(Object key, int cellType) {
		if (types == null)
			types = new HashMap<Object, Integer>();
		types.put(key, cellType);
	}

	/**
	 * 设定第sheet个工作簿第row行col列的单元格类型
	 * 
	 * @param index
	 * @param rownum
	 * @param cellnum
	 * @param cellType
	 */
	public void setCellType(int cellType, Integer... index) {
		setCellType(getListener().getKey(index), cellType);
	}

	/**
	 * 得到key对应的cellType
	 * 
	 * @param key
	 * @return
	 */
	public String getCellFormat(Object key) {
		return formats == null ? null : formats.get(key);
	}

	/**
	 * 设定key所对应的单元格类型
	 * 
	 * @param key
	 * @param cellType
	 */
	public void setCellFormat(Object key, String format) {
		if (formats == null)
			formats = new HashMap<Object, String>();
		formats.put(key, format);
	}

	/**
	 * 设定第sheet个工作簿第row行col列的单元格类型
	 * 
	 * @param index
	 * @param rownum
	 * @param cellnum
	 * @param cellType
	 */
	public void setCellFormat(String format, Integer... index) {
		setCellFormat(getListener().getKey(index), format);
	}

	/**
	 * 创建workbook
	 * 
	 */
	public void create() {
		workbook = POIHelper.getWorkbookInstance(version);
		if (getListener() == null)
			setListener(new POIWriterListener());
		getListener().create(new POIEvent(this));
	}

	/**
	 * 写入输出流中out
	 * 
	 * @param out
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		if (workbook == null)
			create();
		workbook.write(out);
	}

	@Override
	public POIWriterListener getListener() {
		return (POIWriterListener) super.getListener();
	}

	public Map<Object, Integer> getTypes() {
		return types;
	}

	public void setTypes(Map<Object, Integer> cellTypes) {
		types = cellTypes;
	}

	public Map<Object, String> getFormats() {
		return formats;
	}

	public void setFormats(Map<Object, String> formats) {
		this.formats = formats;
	}

}
