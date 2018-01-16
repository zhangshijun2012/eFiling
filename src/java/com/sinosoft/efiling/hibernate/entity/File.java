package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 20:28:16 by Hibernate Tools 4.0.0

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sinosoft.util.hibernate.entity.EntityOperatorSupport;

/**
 * 档案资料信息
 * 
 * @author LuoGang
 * 
 */
public class File extends EntityOperatorSupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3609944308507378507L;
	private FileBox fileBox;
	private FileType fileType;
	private File another;
	/**
	 * 档案归属的单证，但是可以归属于多个单证，此字段仅保存其第一单证id. 单证所包含的档案请件DocumentFile类
	 */
	private Document document;

	private String fileModel;
	private String paperType;
	private String paperCode;
	private String no;
	/** 文件ID，如果有多页则保存第1页的文件ID */
	private String fileId;
	private int fileSize;
	private String fileName;
	private String lent;
	private Date effectiveTime;
	private Date dueTime;
	private String property01;
	private String property02;
	private String property03;
	private String property04;
	private String property05;
	private int pageCount;
	private int pageIndex;
	private int batchPageIndex;
	private String batchNo;
	// private Set<FilePage> pages = new HashSet<FilePage>(0);
	private FileLending fileLending;

	/** 手工上传文件的审批状态:0.未审核,1.审核通过,2.不通过,null非手工上传文件,不需要审核 */
	private String fileApproveStatus;

	private Set<DocumentFile> documentFiles = new HashSet<DocumentFile>(0);

	public File() {
	}

	public int getBatchPageIndex() {
		return batchPageIndex;
	}

	public void setBatchPageIndex(int batchPageIndex) {
		this.batchPageIndex = batchPageIndex;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public FileBox getFileBox() {
		return fileBox;
	}

	public void setFileBox(FileBox fileBox) {
		this.fileBox = fileBox;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public File getAnother() {
		return another;
	}

	public void setAnother(File another) {
		this.another = another;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public String getPaperType() {
		return paperType;
	}

	public void setPaperType(String paperType) {
		this.paperType = paperType;
	}

	public String getPaperCode() {
		return paperCode;
	}

	public void setPaperCode(String paperCode) {
		this.paperCode = paperCode;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getLent() {
		return lent;
	}

	public void setLent(String lent) {
		this.lent = lent;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public String getFileApproveStatus() {
		return fileApproveStatus;
	}

	public void setFileApproveStatus(String fileApproveStatus) {
		this.fileApproveStatus = fileApproveStatus;
	}

	public String getProperty01() {
		return property01;
	}

	public void setProperty01(String property01) {
		this.property01 = property01;
	}

	public String getProperty02() {
		return property02;
	}

	public void setProperty02(String property02) {
		this.property02 = property02;
	}

	public String getProperty03() {
		return property03;
	}

	public void setProperty03(String property03) {
		this.property03 = property03;
	}

	public String getProperty04() {
		return property04;
	}

	public void setProperty04(String property04) {
		this.property04 = property04;
	}

	public String getProperty05() {
		return property05;
	}

	public void setProperty05(String property05) {
		this.property05 = property05;
	}

	/*
	 * public Set<FilePage> getPages() {
	 * return pages;
	 * }
	 * 
	 * public void setPages(Set<FilePage> filePages) {
	 * this.pages = filePages;
	 * }
	 */

	public FileLending getFileLending() {
		return fileLending;
	}

	public void setFileLending(FileLending fileLending) {
		this.fileLending = fileLending;
	}

	public String getFileModel() {
		return fileModel;
	}

	public void setFileModel(String fileModel) {
		this.fileModel = fileModel;
	}

	public int size() {
		return this.fileSize;
	}

	public Set<DocumentFile> getDocumentFiles() {
		return documentFiles;
	}

	public void setDocumentFiles(Set<DocumentFile> documentFiles) {
		this.documentFiles = documentFiles;
	}

}
