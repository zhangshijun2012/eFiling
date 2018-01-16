package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 20:00:25 by Hibernate Tools 4.0.0

import com.sinosoft.util.hibernate.entity.EntitySupport;

/**
 * 档案借阅明细信息
 * 
 * @author LuoGang
 * 
 */
public class FileLendingDetail extends EntitySupport<FileLendingDetailId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4706424221334570262L;

	private FileLending fileLending;
	private FileLendingReturn fileLendingReturn;
	private DocumentFile documentFile;
	private File file;
	private String status;

	public FileLendingDetail() {
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	public FileLendingDetail(FileLending fileLending, DocumentFile documentFile) {
		this(fileLending.getId(), documentFile.getId());
		this.fileLending = fileLending;
		this.documentFile = documentFile;
	}

	public FileLendingDetail(String lendingId, String documentFileId) {
		this.id = new FileLendingDetailId(lendingId, documentFileId);
	}

	public FileLending getFileLending() {
		return fileLending;
	}

	public void setFileLending(FileLending fileLending) {
		this.fileLending = fileLending;
	}

	public FileLendingReturn getFileLendingReturn() {
		return fileLendingReturn;
	}

	public void setFileLendingReturn(FileLendingReturn fileLendingReturn) {
		this.fileLendingReturn = fileLendingReturn;
	}

	public DocumentFile getDocumentFile() {
		return documentFile;
	}

	public void setDocumentFile(DocumentFile documentFile) {
		this.documentFile = documentFile;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
