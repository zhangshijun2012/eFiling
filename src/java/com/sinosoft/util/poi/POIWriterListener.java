package com.sinosoft.util.poi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.sinosoft.util.DateHelper;
import com.sinosoft.util.Helper;
import com.sinosoft.util.NumberHelper;
import com.sinosoft.util.StringHelper;

public class POIWriterListener extends POIEventListener {

	/**
	 * 默认的百分比样式
	 */
	public static final String FORMAT_PERCENT = "0.00%";

	/**
	 * 默认的小数样式
	 */
	public static final String FORMAT_DECIMAL = "#,##0.00";

	/**
	 * 默认的整数格式
	 */
	public static final String FORMAT_INTEGER = "#,##0.##";

	/**
	 * 默认的日期格式
	 */
	public static final String FORMAT_DATE = DateHelper.DEFAULT_DATE_FORMAT;

	/** 是否使用自动列宽 */
	private boolean autoSizeColumn = false;

	public boolean isAutoSizeColumn() {
		return autoSizeColumn;
	}

	public void setAutoSizeColumn(boolean autoSizeColumn) {
		this.autoSizeColumn = autoSizeColumn;
	}

	/** 用于存放创建的样式缓存 */
	private static Map<Workbook, Map<Class<?>, CellStyle>> styleCaches = new HashMap<Workbook, Map<Class<?>, CellStyle>>();

	/**
	 * 创建单元格
	 * 
	 * @param workbook
	 * @param cell 已创建的cell对象
	 * @param value cell的值
	 * @return
	 */
	public Cell createCell(Workbook workbook, Cell cell, Object value) {
		if (value == null) return cell;
		CellStyle cellStyle = null;
		Map<Class<?>, CellStyle> styles = styleCaches.get(workbook);
		if (styles == null) {
			styles = new HashMap<Class<?>, CellStyle>();
			styleCaches.put(workbook, styles);
		}
		Class<?> key = String.class;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			String format;
			if (DateHelper.isDate(value)) {
				/* 日期类型 */
				format = FORMAT_DATE;
				key = Date.class;

				cellStyle = styles.get(key);
				if (cellStyle == null) {
					cellStyle = workbook.createCellStyle();
					cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
				}
				cell.setCellStyle(cellStyle);

				/* 日期类型 */
				cell.setCellValue((Date) value);
			} else if (NumberHelper.isNumber(value)) {
				/* 整数类型 */
				cell.setCellValue(NumberHelper.doubleValue(value));
			} else {
				cell.setCellValue(value.toString());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cell.setCellValue(StringHelper.isTrue(value));
			break;
		case Cell.CELL_TYPE_ERROR:
			cell.setCellErrorValue((byte) NumberHelper.intValue(value));
			break;
		case Cell.CELL_TYPE_FORMULA:
			cell.setCellFormula(value.toString());
			break;
		default:
			cellStyle = cell.getCellStyle();
			if (cellStyle == null) cellStyle = styles.get(key);
			if (cellStyle == null) cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(value.toString());
			break;
		}
		if (cellStyle != null) styles.put(key, cellStyle);
		return cell;

	}

	/**
	 * 建立样式
	 * 
	 * @param e
	 * @return
	 */
	public CellStyle createCellStyle(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIObject source = e.getSource();
		Workbook workbook = source.getWorkbook();
		Cell cell = workbook.getSheetAt(index).getRow(rownum).getCell(cellnum);
		Object value = getCellData(e);
		CellStyle cellStyle = null;
		Map<Class<?>, CellStyle> styles = styleCaches.get(workbook);
		if (styles == null) {
			styles = new HashMap<Class<?>, CellStyle>();
			styleCaches.put(workbook, styles);
		}
		Class<?> key = String.class;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			// cellStyle = cell.getCellStyle();
			String format;
			if (DateHelper.isDate(value)) {
				/* 日期类型 */
				format = FORMAT_DATE;
				key = Date.class;
				// cellStyle.setDataFormat(workbook.createDataFormat().getFormat(FORMAT_DATE));

				cellStyle = styles.get(key);
				if (cellStyle == null) {
					cellStyle = workbook.createCellStyle();
					cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
				}
				cell.setCellStyle(cellStyle);
				// }
				// else if (NumberHelper.isNumber(value) && NumberHelper.format(value, 5).endsWith(".00000")) {
				// /* 整数类型 */
				// format = FORMAT_INTEGER;
				// key = Integer.class;
				// // cellStyle.setDataFormat(workbook.createDataFormat().getFormat(FORMAT_INTEGER));
			}
			// else {
			// format = FORMAT_DECIMAL;
			// key = Double.class;
			// // cellStyle.setDataFormat(workbook.createDataFormat().getFormat(FORMAT_DECIMAL));
			//
			// cellStyle = styles.get(key);
			// if (cellStyle == null) {
			// cellStyle = workbook.createCellStyle();
			// cellStyle.setDataFormat((short) 2); // #,##.00
			// }
			// cell.setCellStyle(cellStyle);
			// }
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			// cell.setCellValue(StringHelper.isTrue(value));
			break;
		case Cell.CELL_TYPE_ERROR:
			// cell.setCellErrorValue((byte) NumberHelper.intValue(value));
			break;
		case Cell.CELL_TYPE_FORMULA:
			// cell.setCellFormula(value.toString());
			break;
		default:
			// cell.setCellValue(value.toString());
			cellStyle = cell.getCellStyle();
			if (cellStyle == null) cellStyle = styles.get(key);
			if (cellStyle == null) cellStyle = workbook.createCellStyle();
			cellStyle.setWrapText(true);
			cell.setCellStyle(cellStyle);
			break;
		}
		if (cellStyle != null) styles.put(key, cellStyle);
		return cellStyle;
	}

	/**
	 * 新建一个表格
	 * 
	 * @param e
	 * @return
	 */
	public Workbook create(POIEvent e) {
		POIObject source = e.getSource();
		if (source.getSheetNames() == null) {
			int l = source.getData() == null ? 0 : source.getData().length;
			if (l <= 0) return source.getWorkbook();
			String[] sheetNames = new String[l];

			for (int i = 0; i < l; i++) {
				sheetNames[i] = "Sheet " + i;
			}
			source.setSheetNames(sheetNames);
		}
		int sheets = source.getSheetNames().length;
		for (int i = 0; i < sheets; i++) {
			e.setInedx(i);
			createSheet(e);
		}

//		this.createAllIdentifiers(e);

		this.onSuccess(e);

		Workbook workbook = source.getWorkbook();
		Map<Class<?>, CellStyle> styles = styleCaches.remove(workbook);
		if (styles != null) styles.clear();
		styles = null;
		return workbook;
	}

	/**
	 * 新建一个sheet
	 * 
	 * @param e
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Sheet createSheet(POIEvent e) {
		int index = e.getInedx();
		POIWriter source = (POIWriter) e.getSource();
		Sheet sheet = source.getWorkbook().createSheet(source.getSheetNames()[index]);
		int[][] widths = source.getWidths();

		int min = 0;
		if (!autoSizeColumn && !Helper.isEmpty(widths) && widths.length > index && widths[index] != null) {
			min = widths[index].length;
			for (int i = 0; i < min; i++) {
				sheet.setColumnWidth(i, widths[index][i]);
			}

		}
		List data = getSheetData(e);
		// if (autoSizeColumn && !Helper.isEmpty(data)) {
		// e.setRownum(0);
		// Object[] values = getRowData(e);
		// if (!Helper.isEmpty(values)) {
		// for (int i = 0, l = values.length; i < l; i++) {
		// sheet.autoSizeColumn(i, true);
		// }
		// }
		// }
		int max = 0;
		Row row;
		for (int r = 0, rows = data == null ? 0 : data.size(); r < rows; r++) {
			e.setRownum(r);
			row = createRow(e);
			max = Math.max(max, row.getLastCellNum());
		}
		
		this.createSheetIdentifiers(e);
		
		for (; min < max; min++) {
			sheet.autoSizeColumn(min);
		}
		return sheet;
	}

	/**
	 * 新建一行
	 * 
	 * @param e
	 * @return
	 */
	public Row createRow(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		POIObject source = e.getSource();

		Row row = source.getWorkbook().getSheetAt(index).createRow(rownum);

		Object[] values = getRowData(e);
		for (int c = 0, cells = values == null ? 0 : values.length; c < cells; c++) {
			e.setCellnum(c);
			createCell(e);
		}

		return row;
	}

	/**
	 * 新建一个单元格
	 * 
	 * @param e
	 * @return
	 */
	public Cell createCell(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIObject source = e.getSource();
		Cell cell = source.getWorkbook().getSheetAt(index).getRow(rownum).createCell(cellnum);
		cell.setCellType(getCellType(e));
		createCellStyle(e);
		setCellValue(e);
		return cell;
	}

	/**
	 * 
	 * 获取当前事件指定的类型.即得到第index个工作簿第rownum行cellnum列的单元格的类型
	 * 
	 * @param e
	 *            ,指定的事件,其中:index 工作簿,rownum 行,cellnum 列
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public int getCellType(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIWriter source = (POIWriter) e.getSource();

		Integer cellType = source.getCellType(getKey(index, rownum, cellnum));
		if (cellType == null) cellType = source.getCellType(getKey(index, rownum));
		if (cellType == null) cellType = source.getCellType(getKey(index));

		List[] data = source.getData();
		if (cellType == null && data != null) {
			Object o = getCellData(e);
			cellType = source.getCellType(o);
			if (cellType == null && o != null) cellType = source.getCellType(o.getClass());
			if (cellType == null) cellType = POIObject.convertCellType(o);

		}
		return cellType == null ? Cell.CELL_TYPE_STRING : cellType.intValue();
	}

	/**
	 * 设置单元格的值
	 * 
	 * @param e
	 */
	public void setCellValue(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIObject source = e.getSource();

		Cell cell = source.getWorkbook().getSheetAt(index).getRow(rownum).getCell(cellnum);

		Object value = getCellData(e);
		if (value == null) return;

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			if (DateHelper.isDate(value)) {
				/* 日期类型 */
				cell.setCellValue((Date) value);
			} else if (NumberHelper.isNumber(value)) {
				/* 整数类型 */
				cell.setCellValue(NumberHelper.doubleValue(value));
			} else {
				cell.setCellValue(value.toString());
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			cell.setCellValue(StringHelper.isTrue(value.toString()));
			break;
		case Cell.CELL_TYPE_ERROR:
			cell.setCellErrorValue((byte) NumberHelper.intValue(value));
			break;
		case Cell.CELL_TYPE_FORMULA:
			cell.setCellFormula(value.toString());
			break;
		default:
			cell.setCellValue(value.toString());
			break;
		}
	}

	/**
	 * 创建sheet的表头.必须在创建完workbook之后再进行调用
	 * 
	 * @param e
	 * @return
	 */
	public Workbook createAllIdentifiers(POIEvent e) {
		POIObject source = e.getSource();
		Workbook workbook = source.getWorkbook();
		if (workbook == null) return null;
		if (Helper.isEmpty(e.getSource().getIdentifiers())) return workbook;
		for (int i = 0, sheets = workbook.getNumberOfSheets(); i < sheets; i++) {
			e.setInedx(i);
			this.createSheetIdentifiers(e);
		}
		return workbook;
	}

	/**
	 * 创建单个sheet的表头
	 * 
	 * @param e
	 * @return
	 */
	public Sheet createSheetIdentifiers(POIEvent e) {
		int index = e.getInedx();
		POIObject source = e.getSource();
		// Workbook workbook = source.getWorkbook();
		Sheet sheet = source.getWorkbook().getSheetAt(index);
		if (sheet == null) return null;
		Object[] identifiers = this.getSheetIdentifiers(e);
		if (Helper.isEmpty(identifiers)) return sheet;
		// 将rownum至lastRowNum的行下移identifiers.length行
		sheet.shiftRows(0, sheet.getLastRowNum(), identifiers.length, true, true);

		for (int r = 0, rows = identifiers.length; r < rows; r++) {
			e.setRownum(r);
			this.createRowIdentifiers(e);
		}

		return sheet;
	}

	/**
	 * 创建单个sheet的单行表头.必须在创建完数据之后再进行调用
	 * 
	 * @param e
	 * @return
	 */
	public Row createRowIdentifiers(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		POIObject source = e.getSource();
		Sheet sheet = source.getWorkbook().getSheetAt(index);
		if (sheet == null) return null;
		Row row = sheet.createRow(rownum);
		Object[] identifiers = this.getRowIdentifiers(e);
		if (identifiers == null) return null;
		for (int c = 0, cells = identifiers.length; c < cells; c++) {
			e.setCellnum(c);
			this.createCellIdentifier(e);
		}

		return row;
	}

	/**
	 * 创建单个表头.表头中默认均视为字符串
	 * 
	 * @param e
	 * @return
	 */
	public Cell createCellIdentifier(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIWriter source = (POIWriter) e.getSource();
		Workbook workbook = source.getWorkbook();
		Cell cell = workbook.getSheetAt(index).getRow(rownum).createCell(cellnum);

		Object identifier = this.getCellIdentifier(e);
		if (identifier != null) {
			Integer cellType = source.getCellType(identifier.getClass());
			if (cellType == null) cellType = POIObject.convertCellType(identifier);
			// cell.setCellValue(identifier.toString());
			// createCellStyle(e);
			// setCellValue(e);
			cell.setCellType(cellType);
			createCell(workbook, cell, identifier);
//			CellStyle cellStyle = cell.getCellStyle();
//			if (cellStyle != null) {
//				cellStyle.setWrapText(false);	// 不自动换行
//				cell.setCellStyle(cellStyle);
//			}
		}

		return cell;
	}

	/**
	 * 得到第e.index个sheet的表头
	 * 
	 * @return
	 */

	public Object[] getSheetIdentifiers(POIEvent e) {
		Object[] identifiers = e.getSource().getIdentifiers();
		int index = e.getInedx();
		Object value = identifiers == null || identifiers.length <= index ? null : identifiers[index];
		if (index == 0 && value != null && !value.getClass().isArray()) {
			// 如果identifiers是个一维数组,在认为是一个sheet,一行表头
			identifiers = new Object[] { new Object[] { identifiers } };
			e.getSource().setIdentifiers(identifiers);
			value = identifiers[index];
		}
		return value == null ? null : (value.getClass().isArray() ? (Object[]) value : new Object[] { value });
	}

	/**
	 * 得到第e.index个sheet的第e.rownum行的表头
	 * 
	 * @return
	 */

	public Object[] getRowIdentifiers(POIEvent e) {
		Object[] identifiers = this.getSheetIdentifiers(e);
		int index = e.getRownum();
		Object value = identifiers == null || identifiers.length <= index ? null : identifiers[index];

		if (index == 0 && value != null && !value.getClass().isArray()) {
			// 如果identifiers是个维数组,此sheet中只有一行表头
			identifiers = new Object[] { identifiers };// Arrays.copyOf(elementData, size, a.getClass());

			Object[] allIdentifiers = e.getSource().getIdentifiers();
			allIdentifiers[e.getInedx()] = identifiers;
			e.getSource().setIdentifiers(allIdentifiers);

			value = identifiers[index];
		}

		return value == null ? null : (value.getClass().isArray() ? (Object[]) value : new Object[] { value });
	}

	/**
	 * 得到单个表头
	 * 
	 * @param e
	 * @return
	 */
	public Object getCellIdentifier(POIEvent e) {
		Object[] identifiers = this.getRowIdentifiers(e);
		int cellnum = e.getCellnum();
		return identifiers == null || identifiers.length <= cellnum ? null : identifiers[cellnum];
	}
}
