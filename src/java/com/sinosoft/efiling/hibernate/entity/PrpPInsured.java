package com.sinosoft.efiling.hibernate.entity;

import com.sinosoft.util.hibernate.entity.EntitySupport;

// Generated 2013-7-1 18:57:58 by Hibernate Tools 4.0.0


/**
 * PrpPInsured generated by hbm2java
 */
@SuppressWarnings("serial")
public class PrpPInsured extends EntitySupport<PrpPInsuredId> {

	private PrpPInsuredId id;
	private String policyno;
	private String riskCode;
	private String language;
	private String insuredType;
	private String insuredCode;
	private String insuredName;
	private String insuredAddress;
	private String insurednature;
	private String insuredFlag;
	private String insuredidentity;
	private Double relateserialno;
	private String identifyType;
	private String identifyNumber;
	private String creditLevel;
	private String possessnature;
	private String businesssource;
	private String businesssort;
	private String occupationCode;
	private String educationCode;
	private String bank;
	private String accountName;
	private String account;
	private String linkerName;
	private String postAddress;
	private String postCode;
	private String phoneNumber;
	private String mobile;
	private String email;
	private String benefitFlag;
	private Double benefitrate;
	private String flag;
	private String occupationgrade;
	private String waysofPayment;
	private String mainidentifyNumber;
	private String planno;
	private String planCode;
	private String planName;
	private String benefType;
	private String blackstate;
	private Double benefserialno;
	private String proposalshare;
	private String mainidentifyType;
	private String payeEName;
	private String payeeidentifyNumber;
	private String refundreason;
	private String address1;
	private String address2;
	private String address3;
	private String address4;
	private String paymentmethord;
	private String socialsecurityno;
	private String specificbusiness;
	private String specificbusinessCode;
	private String servicemark;
	private String occupationLevel;
	private String receiver;
	private String branchaccount;
	private String industry;

	public PrpPInsured() {
	}

	public PrpPInsured(PrpPInsuredId id, String policyno, String riskCode) {
		this.id = id;
		this.policyno = policyno;
		this.riskCode = riskCode;
	}

	public PrpPInsured(PrpPInsuredId id, String policyno, String riskCode, String language, String insuredType,
			String insuredCode, String insuredName, String insuredAddress, String insurednature, String insuredFlag,
			String insuredidentity, Double relateserialno, String identifyType, String identifyNumber,
			String creditLevel, String possessnature, String businesssource, String businesssort,
			String occupationCode, String educationCode, String bank, String accountName, String account,
			String linkerName, String postAddress, String postCode, String phoneNumber, String mobile, String email,
			String benefitFlag, Double benefitrate, String flag, String occupationgrade, String waysofPayment,
			String mainidentifyNumber, String planno, String planCode, String planName, String benefType,
			String blackstate, Double benefserialno, String proposalshare, String mainidentifyType, String payeEName,
			String payeeidentifyNumber, String refundreason, String address1, String address2, String address3,
			String address4, String paymentmethord, String socialsecurityno, String specificbusiness,
			String specificbusinessCode, String servicemark, String occupationLevel, String receiver,
			String branchaccount, String industry) {
		this.id = id;
		this.policyno = policyno;
		this.riskCode = riskCode;
		this.language = language;
		this.insuredType = insuredType;
		this.insuredCode = insuredCode;
		this.insuredName = insuredName;
		this.insuredAddress = insuredAddress;
		this.insurednature = insurednature;
		this.insuredFlag = insuredFlag;
		this.insuredidentity = insuredidentity;
		this.relateserialno = relateserialno;
		this.identifyType = identifyType;
		this.identifyNumber = identifyNumber;
		this.creditLevel = creditLevel;
		this.possessnature = possessnature;
		this.businesssource = businesssource;
		this.businesssort = businesssort;
		this.occupationCode = occupationCode;
		this.educationCode = educationCode;
		this.bank = bank;
		this.accountName = accountName;
		this.account = account;
		this.linkerName = linkerName;
		this.postAddress = postAddress;
		this.postCode = postCode;
		this.phoneNumber = phoneNumber;
		this.mobile = mobile;
		this.email = email;
		this.benefitFlag = benefitFlag;
		this.benefitrate = benefitrate;
		this.flag = flag;
		this.occupationgrade = occupationgrade;
		this.waysofPayment = waysofPayment;
		this.mainidentifyNumber = mainidentifyNumber;
		this.planno = planno;
		this.planCode = planCode;
		this.planName = planName;
		this.benefType = benefType;
		this.blackstate = blackstate;
		this.benefserialno = benefserialno;
		this.proposalshare = proposalshare;
		this.mainidentifyType = mainidentifyType;
		this.payeEName = payeEName;
		this.payeeidentifyNumber = payeeidentifyNumber;
		this.refundreason = refundreason;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.address4 = address4;
		this.paymentmethord = paymentmethord;
		this.socialsecurityno = socialsecurityno;
		this.specificbusiness = specificbusiness;
		this.specificbusinessCode = specificbusinessCode;
		this.servicemark = servicemark;
		this.occupationLevel = occupationLevel;
		this.receiver = receiver;
		this.branchaccount = branchaccount;
		this.industry = industry;
	}

	public PrpPInsuredId getId() {
		return this.id;
	}

	public void setId(PrpPInsuredId id) {
		this.id = id;
	}

	public String getPolicyno() {
		return this.policyno;
	}

	public void setPolicyno(String policyno) {
		this.policyno = policyno;
	}

	public String getRiskCode() {
		return this.riskCode;
	}

	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getInsuredType() {
		return this.insuredType;
	}

	public void setInsuredType(String insuredType) {
		this.insuredType = insuredType;
	}

	public String getInsuredCode() {
		return this.insuredCode;
	}

	public void setInsuredCode(String insuredCode) {
		this.insuredCode = insuredCode;
	}

	public String getInsuredName() {
		return this.insuredName;
	}

	public void setInsuredName(String insuredName) {
		this.insuredName = insuredName;
	}

	public String getInsuredAddress() {
		return this.insuredAddress;
	}

	public void setInsuredAddress(String insuredAddress) {
		this.insuredAddress = insuredAddress;
	}

	public String getInsurednature() {
		return this.insurednature;
	}

	public void setInsurednature(String insurednature) {
		this.insurednature = insurednature;
	}

	public String getInsuredFlag() {
		return this.insuredFlag;
	}

	public void setInsuredFlag(String insuredFlag) {
		this.insuredFlag = insuredFlag;
	}

	public String getInsuredidentity() {
		return this.insuredidentity;
	}

	public void setInsuredidentity(String insuredidentity) {
		this.insuredidentity = insuredidentity;
	}

	public Double getRelateserialno() {
		return this.relateserialno;
	}

	public void setRelateserialno(Double relateserialno) {
		this.relateserialno = relateserialno;
	}

	public String getIdentifyType() {
		return this.identifyType;
	}

	public void setIdentifyType(String identifyType) {
		this.identifyType = identifyType;
	}

	public String getIdentifyNumber() {
		return this.identifyNumber;
	}

	public void setIdentifyNumber(String identifyNumber) {
		this.identifyNumber = identifyNumber;
	}

	public String getCreditLevel() {
		return this.creditLevel;
	}

	public void setCreditLevel(String creditLevel) {
		this.creditLevel = creditLevel;
	}

	public String getPossessnature() {
		return this.possessnature;
	}

	public void setPossessnature(String possessnature) {
		this.possessnature = possessnature;
	}

	public String getBusinesssource() {
		return this.businesssource;
	}

	public void setBusinesssource(String businesssource) {
		this.businesssource = businesssource;
	}

	public String getBusinesssort() {
		return this.businesssort;
	}

	public void setBusinesssort(String businesssort) {
		this.businesssort = businesssort;
	}

	public String getOccupationCode() {
		return this.occupationCode;
	}

	public void setOccupationCode(String occupationCode) {
		this.occupationCode = occupationCode;
	}

	public String getEducationCode() {
		return this.educationCode;
	}

	public void setEducationCode(String educationCode) {
		this.educationCode = educationCode;
	}

	public String getBank() {
		return this.bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLinkerName() {
		return this.linkerName;
	}

	public void setLinkerName(String linkerName) {
		this.linkerName = linkerName;
	}

	public String getPostAddress() {
		return this.postAddress;
	}

	public void setPostAddress(String postAddress) {
		this.postAddress = postAddress;
	}

	public String getPostCode() {
		return this.postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getMobile() {
		return this.mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBenefitFlag() {
		return this.benefitFlag;
	}

	public void setBenefitFlag(String benefitFlag) {
		this.benefitFlag = benefitFlag;
	}

	public Double getBenefitrate() {
		return this.benefitrate;
	}

	public void setBenefitrate(Double benefitrate) {
		this.benefitrate = benefitrate;
	}

	public String getFlag() {
		return this.flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getOccupationgrade() {
		return this.occupationgrade;
	}

	public void setOccupationgrade(String occupationgrade) {
		this.occupationgrade = occupationgrade;
	}

	public String getWaysofPayment() {
		return this.waysofPayment;
	}

	public void setWaysofPayment(String waysofPayment) {
		this.waysofPayment = waysofPayment;
	}

	public String getMainidentifyNumber() {
		return this.mainidentifyNumber;
	}

	public void setMainidentifyNumber(String mainidentifyNumber) {
		this.mainidentifyNumber = mainidentifyNumber;
	}

	public String getPlanno() {
		return this.planno;
	}

	public void setPlanno(String planno) {
		this.planno = planno;
	}

	public String getPlanCode() {
		return this.planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getPlanName() {
		return this.planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getBenefType() {
		return this.benefType;
	}

	public void setBenefType(String benefType) {
		this.benefType = benefType;
	}

	public String getBlackstate() {
		return this.blackstate;
	}

	public void setBlackstate(String blackstate) {
		this.blackstate = blackstate;
	}

	public Double getBenefserialno() {
		return this.benefserialno;
	}

	public void setBenefserialno(Double benefserialno) {
		this.benefserialno = benefserialno;
	}

	public String getProposalshare() {
		return this.proposalshare;
	}

	public void setProposalshare(String proposalshare) {
		this.proposalshare = proposalshare;
	}

	public String getMainidentifyType() {
		return this.mainidentifyType;
	}

	public void setMainidentifyType(String mainidentifyType) {
		this.mainidentifyType = mainidentifyType;
	}

	public String getPayeEName() {
		return this.payeEName;
	}

	public void setPayeEName(String payeEName) {
		this.payeEName = payeEName;
	}

	public String getPayeeidentifyNumber() {
		return this.payeeidentifyNumber;
	}

	public void setPayeeidentifyNumber(String payeeidentifyNumber) {
		this.payeeidentifyNumber = payeeidentifyNumber;
	}

	public String getRefundreason() {
		return this.refundreason;
	}

	public void setRefundreason(String refundreason) {
		this.refundreason = refundreason;
	}

	public String getAddress1() {
		return this.address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return this.address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return this.address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getAddress4() {
		return this.address4;
	}

	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	public String getPaymentmethord() {
		return this.paymentmethord;
	}

	public void setPaymentmethord(String paymentmethord) {
		this.paymentmethord = paymentmethord;
	}

	public String getSocialsecurityno() {
		return this.socialsecurityno;
	}

	public void setSocialsecurityno(String socialsecurityno) {
		this.socialsecurityno = socialsecurityno;
	}

	public String getSpecificbusiness() {
		return this.specificbusiness;
	}

	public void setSpecificbusiness(String specificbusiness) {
		this.specificbusiness = specificbusiness;
	}

	public String getSpecificbusinessCode() {
		return this.specificbusinessCode;
	}

	public void setSpecificbusinessCode(String specificbusinessCode) {
		this.specificbusinessCode = specificbusinessCode;
	}

	public String getServicemark() {
		return this.servicemark;
	}

	public void setServicemark(String servicemark) {
		this.servicemark = servicemark;
	}

	public String getOccupationLevel() {
		return this.occupationLevel;
	}

	public void setOccupationLevel(String occupationLevel) {
		this.occupationLevel = occupationLevel;
	}

	public String getReceiver() {
		return this.receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getBranchaccount() {
		return this.branchaccount;
	}

	public void setBranchaccount(String branchaccount) {
		this.branchaccount = branchaccount;
	}

	public String getIndustry() {
		return this.industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

}