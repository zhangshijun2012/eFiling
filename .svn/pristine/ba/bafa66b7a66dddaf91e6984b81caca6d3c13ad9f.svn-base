package com.sinosoft.util.poi;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;

import com.sinosoft.util.StringHelper;

public class POIObject {
	/**
	 * excel2003以下的文件类型
	 */
	public static final String VERSION_2003 = ".xls";

	/**
	 * excel2007的文件类型
	 */
	public static final String VERSION_2007 = ".xlsx";

	/**
	 * 默认使用2003
	 */
	public static final String VERSION_DEFAULT = VERSION_2003;

	/**
	 * excel的版本类型.如果指定的版本与文件的版本有错误可能会读取失败
	 */
	protected String version;

	/**
	 * 根据指定类型生成的workbook实例
	 */
	protected Workbook workbook;

	/**
	 * sheet的名称
	 */
	protected String[] sheetNames;

	/**
	 * 每个sheet中每一列的标题. 此数据应该为一个3维数组.第1维表示sheet的index,第2维为行号,第3维为单元格号. 如果只有2维,则表示只有一行标题
	 * [第一个sheet{第一行标题{第一列的标题,第二列标题},...},..]
	 */
	protected Object[] identifiers;

	/**
	 * 数据.每个sheet的数据分别为一个list.data中的每一个数据是一个数组.如果不是数组,则说明只有一列
	 */
	protected List[] data;

	protected POIEventListener listener;

	public POIObject() {
		super();
		// version = VERSION_DEFAULT;
	}

	public POIObject(String version) {
		super();
		this.version = version;
	}

	public POIObject(String version, POIEventListener listener) {
		this(version);
		this.listener = listener;
	}

	/**
	 * 将o转换为默认的cellType
	 * 
	 * @param o
	 * @return
	 */
	public static int convertCellType(Object o) {
		if (o == null)
			return Cell.CELL_TYPE_BLANK;
		return convertCellType(o.getClass());
	}

	/**
	 * 将cls转换为默认的cellType
	 * 
	 * @param cls
	 * @return
	 */
	public static int convertCellType(Class cls) {
		if (Number.class.isAssignableFrom(cls) || Date.class.isAssignableFrom(cls)) {
			return Cell.CELL_TYPE_NUMERIC;
		}
		if (Boolean.class.isAssignableFrom(cls)) {
			return Cell.CELL_TYPE_BOOLEAN;
		}

		return Cell.CELL_TYPE_STRING;
	}

	// /**
	// * 得到第index个工作簿第rownum行cellnum列的单元格的数据
	// *
	// * @param index 工作簿
	// * @param rownum 行
	// * @param cellnum 列
	// * @return
	// */
	// public Object getData(int index, int rownum, int cellnum) {
	// if (ObjectHelper.isEmpty(data)) return null;
	// try {
	// Object value = data[index].get(rownum);
	// Object[] values = value.getClass().isArray() ? (Object[]) value : new Object[] { value };
	// value = values[cellnum];
	// return value;
	// } catch (Exception e) {
	// // TODO 可能会出现数据下表越界的异常
	// }
	// return null;
	// }

	public List[] getData() {
		return data;
	}

	/**
	 * 得到第index个sheet的数据
	 * 
	 * @param index
	 * @return
	 */
	public List getData(int index) {
		return getListener().getSheetData(new POIEvent(this, index, 0, 0));
	}

	/**
	 * 得到第index个sheet第rownum行的数据
	 * 
	 * @param index
	 * @param rownum
	 * @return
	 */
	public Object[] getData(int index, int rownum) {
		return getListener().getRowData(new POIEvent(this, index, rownum, 0));
	}

	/**
	 * 得到指定单元格的数据
	 * 
	 * @param index
	 * @param rownum
	 * @param cellnum
	 * @return
	 */
	public Object getData(int index, int rownum, int cellnum) {
		return getListener().getRowData(new POIEvent(this, index, rownum, cellnum));
	}

	public void setData(List[] data) {
		this.data = data;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = StringHelper.isEmpty(version) ? VERSION_DEFAULT : version;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public POIEventListener getListener() {
		return listener;
	}

	public void setListener(POIEventListener listener) {
		this.listener = listener;
	}

	public String[] getSheetNames() {
		return sheetNames;
	}

	public void setSheetNames(String[] sheetNames) {
		this.sheetNames = sheetNames;
	}

	/**
	 * 增加一个sheet
	 * 
	 * @param sheetNames
	 *            可以为数组或sheet名
	 */
	public void addSheetNames(Object sheetNames) {
		int m = 0;
		if (sheetNames != null && sheetNames.getClass().isArray()) {
			m = Array.getLength(sheetNames);
			if (m <= 0)
				return;
		}

		int l = this.sheetNames == null ? 0 : this.sheetNames.length;
		String[] old = this.sheetNames;
		this.sheetNames = new String[l + Math.max(1, m)];
		if (l > 0) {
			System.arraycopy(old, 0, this.sheetNames, 0, l);
		}

		if (m > 0) {
			System.arraycopy(sheetNames, 0, this.sheetNames, l, m);
		} else {
			this.sheetNames[l] = sheetNames == null ? "" : sheetNames.toString();
		}
	}

	public Object[] getIdentifiers() {
		return identifiers;
	}

	public void setIdentifiers(Object[] identifiers) {
		this.identifiers = identifiers;
	}
}
