package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 20:00:25 by Hibernate Tools 4.0.0

import java.io.Serializable;

/**
 * 档案借阅明细FileLendingDetail的主键
 * 
 * @author LuoGang
 * 
 */
public class FileLendingDetailId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 342566916064244022L;
	/** 借阅id */
	private String lendingId;
	/** 借阅的单证文档id */
	private String fileId;

	public FileLendingDetailId() {
	}

	public FileLendingDetailId(String lendingId, String fileId) {
		this.lendingId = lendingId;
		this.fileId = fileId;
	}

	public String getLendingId() {
		return this.lendingId;
	}

	public void setLendingId(String lendingId) {
		this.lendingId = lendingId;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public boolean equals(Object other) {
		if ((this == other)) return true;
		if ((other == null)) return false;
		if (!(other instanceof FileLendingDetailId)) return false;
		FileLendingDetailId castOther = (FileLendingDetailId) other;

		return ((this.getLendingId() == castOther.getLendingId()) || (this.getLendingId() != null
				&& castOther.getLendingId() != null && this.getLendingId().equals(castOther.getLendingId())))
				&& ((this.getFileId() == castOther.getFileId()) || (this.getFileId() != null
						&& castOther.getFileId() != null && this.getFileId().equals(castOther.getFileId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getLendingId() == null ? 0 : this.getLendingId().hashCode());
		result = 37 * result + (getFileId() == null ? 0 : this.getFileId().hashCode());
		return result;
	}

}
