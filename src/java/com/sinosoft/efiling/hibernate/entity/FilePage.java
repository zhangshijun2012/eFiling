package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 20:00:25 by Hibernate Tools 4.0.0

import com.sinosoft.util.hibernate.entity.EntitySupport;

/**
 * 档案页数信息,注意,此表已未使用
 * 
 * @author LuoGang
 * 
 */
public class FilePage extends EntitySupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6203754813446479579L;
	private File file;
	private int pageIndex;
	private int fileSize;
	private String fileName;
	private String lent;

	public FilePage() {
	}

	public File getFile() {
		return this.file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLent() {
		return this.lent;
	}

	public void setLent(String lent) {
		this.lent = lent;
	}

	public int size() {
		return this.fileSize;
	}

}
