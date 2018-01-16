package com.sinosoft.util.poi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.sinosoft.util.NumberHelper;

public class POIReaderListener extends POIEventListener {

	/**
	 * 
	 * 获取当前事件指定的数据类型.即得到第index个工作簿第rownum行cellnum列的单元格的class
	 * 
	 * @param e
	 *            ,指定的事件,其中:index 工作簿,rownum 行,cellnum 列
	 * @return
	 */
	public Class getCellClass(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIReader source = (POIReader) e.getSource();

		Class cellClass = source.getCellClass(getKey(index, rownum, cellnum));
		if (cellClass == null) cellClass = source.getCellClass(getKey(index, rownum));
		if (cellClass == null) cellClass = source.getCellClass(getKey(index));

		return cellClass == null ? Object.class : cellClass;
	}

	/**
	 * 读取数据
	 * 
	 * @param e
	 * @return
	 */
	public List[] read(POIEvent e) {
		POIObject source = e.getSource();
		int sheets = source.getWorkbook().getNumberOfSheets();
		List[] data = new List[sheets];
		source.setSheetNames(new String[sheets]);
		for (int i = 0; i < sheets; i++) {
			e.setInedx(i);
			data[i] = readSheetData(e);
		}
		this.onSuccess(e);
		return data;
	}

	/**
	 * 读取第index个表格的数据
	 * 
	 * @param e
	 * @param index
	 * @return
	 */
	public List[] read(POIEvent e, int index) {
		if (index < 0) return this.read(e);

		// POIObject source = e.getSource();
		// int sheets = source.getWorkbook().getNumberOfSheets();
		List[] data = new List[1];
		e.getSource().setSheetNames(new String[1]);
		// for (int i = 0; i < sheets; i++) {
		e.setInedx(index);
		data[0] = readSheetData(e);
		// }
		return data;
	}

	public List readSheetData(POIEvent e) {
		int index = e.getInedx();
		POIObject source = e.getSource();
		Sheet sheet = source.getWorkbook().getSheetAt(index);
		if (sheet == null) return null;

		String[] sheetNames = source.getSheetNames();
		if (sheetNames != null && sheetNames.length > index) {
			sheetNames[index] = sheet.getSheetName();
		}

		int rows = sheet.getLastRowNum();
		if (rows < 0) return null;
		List<Object[]> data = new ArrayList<Object[]>();
		for (int r = 0; r <= rows; r++) {
			e.setRownum(r);
			data.add(readRowData(e));
		}
		return data;
	}

	public Object[] readRowData(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		POIObject source = e.getSource();
		Row row = source.getWorkbook().getSheetAt(index).getRow(rownum);
		if (row == null) return null;
		int cells = row.getLastCellNum(); // 最后一列有数据的列号
		if (cells < 0) return null;
		Object[] values = new Object[cells];
		for (int c = row.getFirstCellNum(); c < cells; c++) {
			e.setCellnum(c);
			values[c] = readCellData(e);
		}
		return values;
	}

	/**
	 * 获取单元格的值
	 * 
	 * @param e
	 */
	@SuppressWarnings("unchecked")
	public Object readCellData(POIEvent e) {
		int index = e.getInedx();
		int rownum = e.getRownum();
		int cellnum = e.getCellnum();
		POIObject source = e.getSource();

		Cell cell = source.getWorkbook().getSheetAt(index).getRow(rownum).getCell(cellnum);
		if (cell == null) return null;
		Object value = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
			value = cell.getCellFormula();
			break;

		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				// 日期对象
				value = cell.getDateCellValue();
			} else {
				// 整数还是小数由得到数据后的程序自己判断
				value = cell.getNumericCellValue();
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = cell.getBooleanCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		default:
			value = cell.getStringCellValue();
		}
		Class clazz = getCellClass(e);
		// 与需要的类型一致
		if (value == null || clazz.isAssignableFrom(value.getClass())) return value;

		if (Date.class.isAssignableFrom(clazz)) {
			// 日期
			return (Date) value;
		}

		if (Integer.class.isAssignableFrom(clazz)) {
			// int
			return NumberHelper.intValue(value);
		}
		if (Long.class.isAssignableFrom(clazz)) {
			// long
			return NumberHelper.longValue(value);
		}
		if (Number.class.isAssignableFrom(clazz)) {
			// double
			return NumberHelper.doubleValue(value);
		}

		return value;
	}
}
