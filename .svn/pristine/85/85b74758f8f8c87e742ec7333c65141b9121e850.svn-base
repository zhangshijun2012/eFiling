package com.sinosoft.util.poi;

import java.util.EventListener;
import java.util.List;

import com.sinosoft.util.StringHelper;

public class POIEventListener implements EventListener {
	/**
	 * 读取/写入完成后进行的操作
	 * 
	 */
	public void onSuccess(POIEvent e) {

	}

	/**
	 * 组合关键字
	 * 
	 * @return
	 */
	public String getKey(Integer... index) {
		return StringHelper.join(index, "*");
	}

	/**
	 * 得到某个sheet的数据
	 * 
	 * @param e
	 * @return
	 */
	public List getSheetData(POIEvent e) {
		List[] data = e.getSource().getData();
		int index = e.getInedx();
		return data == null || data.length <= index ? null : data[index];
	}

	/**
	 * 得到行的数据
	 * 
	 * @param e
	 * @return
	 */
	public Object[] getRowData(POIEvent e) {
		List list = getSheetData(e);
		int rownum = e.getRownum();
		if (list == null || list.size() <= rownum) return null;
		Object value = list.get(rownum);
		return value == null ? null : (value.getClass().isArray() ? (Object[]) value : new Object[] { value });
	}

	/**
	 * 获取当前事件指定的数据.即得到需要写入第index个工作簿第rownum行cellnum列的单元格的数据.<br>
	 * 
	 * @param e,指定的事件,其中:index 工作簿,rownum 行,cellnum 列
	 * @return
	 */
	public Object getCellData(POIEvent e) {
		Object[] data = getRowData(e);
		int cellnum = e.getCellnum();
		return data == null || data.length <= cellnum ? null : data[cellnum];
	}
}
