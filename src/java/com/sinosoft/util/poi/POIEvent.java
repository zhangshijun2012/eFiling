package com.sinosoft.util.poi;

import java.util.EventObject;

public class POIEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8738959236555267703L;

	public POIEvent(POIObject source) {
		super(source);
	}

	public POIEvent(Object source, int inedx, int rownum, int cellnum) {
		super(source);
		this.inedx = inedx;
		this.rownum = rownum;
		this.cellnum = cellnum;
	}

	@Override
	public POIObject getSource() {
		// TODO 自动生成方法存根
		return (POIObject) super.getSource();
	}

	/**
	 * 第index个工作簿
	 */
	private int inedx;

	/**
	 * 第ronnum行
	 */
	private int rownum;

	/**
	 * 第cellnum个单元格
	 */
	private int cellnum;

	public int getCellnum() {
		return cellnum;
	}

	public void setCellnum(int cellnum) {
		this.cellnum = cellnum;
	}

	public int getInedx() {
		return inedx;
	}

	public void setInedx(int inedx) {
		this.inedx = inedx;
	}

	public int getRownum() {
		return rownum;
	}

	public void setRownum(int rownum) {
		this.rownum = rownum;
	}

}
