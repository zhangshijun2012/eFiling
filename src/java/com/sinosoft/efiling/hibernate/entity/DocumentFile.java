package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 19:41:18 by Hibernate Tools 4.0.0

import java.util.Date;

import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.hibernate.entity.EntitySupport;

/**
 * 单证的资料文件信息
 * 
 * @author LuoGang
 * 
 */
public class DocumentFile extends EntitySupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2857038146663334810L;
	private Document document;
	/** 承保资料编码，在初始化生成资料时。如果为影像文件，则此字段为空 */
	private String fileTypeCode;
	private FileType fileType;
	private File file;
	/** 如果是被另一份文件覆盖，则此字段保存新的文件id,反之亦然 */
	private DocumentFile another;
	/** 0.影像文件,1.承保资料文件 */
	private String fileModel;
	private String paperType;
	private String paperCode;
	/** 0.差缺；1.已归档；D：禁用,被其他文件覆盖 */
	private String status;
	/** 1.此文件被其他文件替换,0.此文件尚未被替换 */
	private String replaced;
	private Date fileTime;
	private Date effectiveTime;
	/** 真实的有效期 */
	private Date dueTime;
	// /** 身份证、组织机构代码证填写的有效期，行驶证计算出的有效期 */
	// private Date secondDueTime;

	private int ordinal;

	/** 是否为核保提交的必须资料 */
	private String required;
	/** 是否为自动共享的资料 */
	private String shared;
	/** 如果是共享来的资料,是由那个资料共享来的 */
	private DocumentFile sharedFrom;

	// /** 手工上传文件的审批状态:0.未审核,1.审核通过,2.不通过,null非手工上传文件,不需要审核 */
	// private String fileApproveStatus;

	// private Set<FileLendingDetail> fileLendingDetails = new HashSet<FileLendingDetail>(0);

	// public String getFileApproveStatus() {
	// return this.file == null ? null : this.file.getFileApproveStatus();
	// }
	//
	// public void setFileApproveStatus(String fileApproveStatus) {
	// this.file.setFileApproveStatus(fileApproveStatus);
	// }

	public DocumentFile() {
		required = SystemUtils.YES;
		shared = SystemUtils.NO;
		replaced = SystemUtils.NO;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public FileType getFileType() {
		return fileType;
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public DocumentFile getAnother() {
		return another;
	}

	public void setAnother(DocumentFile another) {
		this.another = another;
	}

	public String getFileModel() {
		return fileModel;
	}

	public void setFileModel(String fileModel) {
		this.fileModel = fileModel;
	}

	public String getFileTypeCode() {
		return fileTypeCode;
	}

	public void setFileTypeCode(String fileTypeCode) {
		this.fileTypeCode = fileTypeCode;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getFileTime() {
		return fileTime;
	}

	public void setFileTime(Date fileTime) {
		this.fileTime = fileTime;
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

	public int getOrdinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String getRequired() {
		return required;
	}

	public void setRequired(String required) {
		this.required = required;
	}

	public String getShared() {
		return shared;
	}

	public void setShared(String shared) {
		this.shared = shared;
	}

	public DocumentFile getSharedFrom() {
		return sharedFrom;
	}

	public void setSharedFrom(DocumentFile sharedFrom) {
		this.sharedFrom = sharedFrom;
	}

	public String getReplaced() {
		return replaced;
	}

	public void setReplaced(String replaced) {
		this.replaced = replaced;
	}
}
