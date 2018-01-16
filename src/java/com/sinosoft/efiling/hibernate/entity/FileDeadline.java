package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 19:41:18 by Hibernate Tools 4.0.0

import com.sinosoft.util.hibernate.entity.EntityOperatorSupport;

/**
 * 归档期限设置信息
 * 
 * @author LuoGang
 * 
 */
public class FileDeadline extends EntityOperatorSupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6483083872576500865L;
	/** 控制的业务部门 */
	private Company fileDept;
	private Product product;
	/** 控制方式（0.是否归档：只有使用了批量扫描方式上传资料的才是已归档；1. 归档齐全：所有资料已上传或已手动点归档齐全才是归档齐全) */
	private String ctrlType;
	private int days;

	public FileDeadline() {
	}

	public Company getFileDept() {
		return fileDept;
	}

	public void setFileDept(Company archiveDept) {
		this.fileDept = archiveDept;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getCtrlType() {
		return ctrlType;
	}

	public void setCtrlType(String ctrlType) {
		this.ctrlType = ctrlType;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

}
