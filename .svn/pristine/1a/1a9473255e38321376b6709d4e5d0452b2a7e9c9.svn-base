package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 19:41:18 by Hibernate Tools 4.0.0

import java.util.Date;
import java.util.Set;

import com.sinosoft.efiling.util.SystemUtils;
import com.sinosoft.util.hibernate.entity.EntityOperatorSupport;

/**
 * 单证信息,投保单、保单、批单等信息都会写入此数据表中
 * 
 * @author LuoGang
 * 
 */
public class Document extends EntityOperatorSupport<String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5709504894511284980L;
	private Product product;
	private User sales;
	private User businessUser;
	private Company businessDept;
	private Company businessCompany;
	/** 单证类型(7批单，8保单，9投保单,投保单只要生成了保单最终会变为保单) */
	private String type;
	/** 业务号，投保单为投保单号，保单为保单号，批单为批单号 */
	private String no;
	private String proposalNo;
	private String policyNo;
	private String endorNo;
	/** 另一单证（主要用于联合投保商业险、交强险时，指定另一个保单号） */
	private Document another;
	private String source;
	private Date fileTime;
	private String fileStatus;
	/** 手动归档原因 */
	private String manuallyReason;
	/** 手动归档时上传的附件 */
	private String manuallyFiles;

	private String lacks;
	/** 1.借出,0.未借出 */
	private String lent;
	private String riskType;
	private String riskClass;
	private String riskCode;
	private Integer year;
	private Date effectiveTime;
	private Date dueTime;
	private String applicant;
	private String applicantType;
	private String applicantPassportType;
	private String applicantPassportNo;
	private String agentNo;
	private String agentName;
	private Date salesTime;
	private String businessNo;
	private Date createTime;
	private String vin;
	private String engineNo;
	private String licenseNo;
	private String insured;
	/** 单证号 */
	private String visaNo;
	/** 单证状态 002：作废 003 遗失 */
	private String visaStatus;
	/** 自动单证审核的状态（0.自动单证审核，1.手工单证审核，2.自动单证审核按概率抽取的手工单证审核） */
	private String approveStatus;

	/** 最后一次借阅ID */
	private FileLending fileLending;
	private Set<File> files;
	/** 手工上传文件的审批状态:0.未审核,1.审核通过,2.不通过,null不需要审核 */
	private String fileApproveStatus;
	private Set<DocumentFile> documentFiles;

	public Document() {
		status = SystemUtils.STATUS_INVALID;
		createTime = new Date();
	}

	public String getFileApproveStatus() {
		return fileApproveStatus;
	}

	public void setFileApproveStatus(String fileApproveStatus) {
		this.fileApproveStatus = fileApproveStatus;
	}

	public String getApproveStatus() {
		return approveStatus;
	}

	public void setApproveStatus(String approveStatus) {
		this.approveStatus = approveStatus;
	}

	public String getVisaNo() {
		return visaNo;
	}

	public void setVisaNo(String visaNo) {
		this.visaNo = visaNo;
	}

	public String getVisaStatus() {
		return visaStatus;
	}

	public void setVisaStatus(String visaStatus) {
		this.visaStatus = visaStatus;
	}

	public FileLending getFileLending() {
		return fileLending;
	}

	public void setFileLending(FileLending filelending) {
		this.fileLending = filelending;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getInsured() {
		return insured;
	}

	public void setInsured(String insured) {
		this.insured = insured;
	}

	public User getSales() {
		return sales;
	}

	public void setSales(User sales) {
		this.sales = sales;
	}

	public User getBusinessUser() {
		return businessUser;
	}

	public void setBusinessUser(User businessUser) {
		this.businessUser = businessUser;
	}

	public Company getBusinessDept() {
		return businessDept;
	}

	public void setBusinessDept(Company businessDept) {
		this.businessDept = businessDept;
	}

	public Company getBusinessCompany() {
		return businessCompany;
	}

	public void setBusinessCompany(Company businessCompany) {
		this.businessCompany = businessCompany;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getProposalNo() {
		return proposalNo;
	}

	public void setProposalNo(String proposalNo) {
		this.proposalNo = proposalNo;
	}

	public String getPolicyNo() {
		return policyNo;
	}

	public void setPolicyNo(String policyNo) {
		this.policyNo = policyNo;
	}

	public String getEndorNo() {
		return endorNo;
	}

	public void setEndorNo(String endorNo) {
		this.endorNo = endorNo;
	}

	public Document getAnother() {
		return another;
	}

	public void setAnother(Document another) {
		this.another = another;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getFileTime() {
		return fileTime;
	}

	public void setFileTime(Date fileTime) {
		this.fileTime = fileTime;
	}

	public String getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getLacks() {
		return lacks;
	}

	public void setLacks(String lacks) {
		this.lacks = lacks;
	}

	public String getLent() {
		return lent;
	}

	public void setLent(String lent) {
		this.lent = lent;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public String getRiskClass() {
		return riskClass;
	}

	public void setRiskClass(String riskClass) {
		this.riskClass = riskClass;
	}

	public String getRiskCode() {
		return riskCode;
	}

	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public String getApplicantType() {
		return applicantType;
	}

	public void setApplicantType(String applicantType) {
		this.applicantType = applicantType;
	}

	public String getApplicantPassportType() {
		return applicantPassportType;
	}

	public void setApplicantPassportType(String applicantPassportType) {
		this.applicantPassportType = applicantPassportType;
	}

	public String getApplicantPassportNo() {
		return applicantPassportNo;
	}

	public void setApplicantPassportNo(String applicantPassportNo) {
		this.applicantPassportNo = applicantPassportNo;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public Date getSalesTime() {
		return salesTime;
	}

	public void setSalesTime(Date salesTime) {
		this.salesTime = salesTime;
	}

	public String getBusinessNo() {
		return businessNo;
	}

	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public String getLicenseNo() {
		return licenseNo;
	}

	public void setLicenseNo(String licenseNo) {
		this.licenseNo = licenseNo;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public Set<File> getFiles() {
		return files;
	}

	public void setFiles(Set<File> files) {
		this.files = files;
	}

	public Set<DocumentFile> getDocumentFiles() {
		return documentFiles;
	}

	public void setDocumentFiles(Set<DocumentFile> documentFiles) {
		this.documentFiles = documentFiles;
	}

	public String getManuallyReason() {
		return manuallyReason;
	}

	public void setManuallyReason(String manuallyReason) {
		this.manuallyReason = manuallyReason;
	}

	public String getManuallyFiles() {
		return manuallyFiles;
	}

	public void setManuallyFiles(String manuallyFiles) {
		this.manuallyFiles = manuallyFiles;
	}

}
