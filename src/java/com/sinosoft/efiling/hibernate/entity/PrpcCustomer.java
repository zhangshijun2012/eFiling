package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-7-31 18:53:08 by Hibernate Tools 4.0.0

import com.sinosoft.util.hibernate.entity.EntitySupport;

@SuppressWarnings("serial")
public class PrpcCustomer extends EntitySupport<PrpcCustomerId> {

	private PrpcCustomerId id;
	private PrpcMaincovernote prpcMaincovernote;
	private String riskcode;
	private String language;
	private String insuredtype;
	private String insuredcode;
	private String insuredname;
	private String insuredaddress;
	private String insurednature;
	private String insuredflag;
	private String insuredidentity;
	private Double relateserialno;
	private String identifytype;
	private String identifynumber;
	private String creditlevel;
	private String possessnature;
	private String businesssource;
	private String businesssort;
	private String occupationcode;
	private String educationcode;
	private String bank;
	private String accountname;
	private String account;
	private String linkername;
	private String postaddress;
	private String postcode;
	private String phonenumber;
	private String mobile;
	private String email;
	private String benefitflag;
	private Double benefitrate;
	private String flag;
	private String occupationgrade;
	private String waysofpayment;
	private String payeename;
	private String payeeidentifynumber;
	private String refundreason;
	private String receiver;
	private String branchaccount;
	private String industry;

	public PrpcCustomer() {
	}

	public PrpcCustomer(PrpcCustomerId id, PrpcMaincovernote prpcMaincovernote, String riskcode) {
		this.id = id;
		this.prpcMaincovernote = prpcMaincovernote;
		this.riskcode = riskcode;
	}

	public PrpcCustomer(PrpcCustomerId id, PrpcMaincovernote prpcMaincovernote, String riskcode, String language,
			String insuredtype, String insuredcode, String insuredname, String insuredaddress, String insurednature,
			String insuredflag, String insuredidentity, Double relateserialno, String identifytype,
			String identifynumber, String creditlevel, String possessnature, String businesssource,
			String businesssort, String occupationcode, String educationcode, String bank, String accountname,
			String account, String linkername, String postaddress, String postcode, String phonenumber, String mobile,
			String email, String benefitflag, Double benefitrate, String flag, String occupationgrade,
			String waysofpayment, String payeename, String payeeidentifynumber, String refundreason, String receiver,
			String branchaccount, String industry) {
		this.id = id;
		this.prpcMaincovernote = prpcMaincovernote;
		this.riskcode = riskcode;
		this.language = language;
		this.insuredtype = insuredtype;
		this.insuredcode = insuredcode;
		this.insuredname = insuredname;
		this.insuredaddress = insuredaddress;
		this.insurednature = insurednature;
		this.insuredflag = insuredflag;
		this.insuredidentity = insuredidentity;
		this.relateserialno = relateserialno;
		this.identifytype = identifytype;
		this.identifynumber = identifynumber;
		this.creditlevel = creditlevel;
		this.possessnature = possessnature;
		this.businesssource = businesssource;
		this.businesssort = businesssort;
		this.occupationcode = occupationcode;
		this.educationcode = educationcode;
		this.bank = bank;
		this.accountname = accountname;
		this.account = account;
		this.linkername = linkername;
		this.postaddress = postaddress;
		this.postcode = postcode;
		this.phonenumber = phonenumber;
		this.mobile = mobile;
		this.email = email;
		this.benefitflag = benefitflag;
		this.benefitrate = benefitrate;
		this.flag = flag;
		this.occupationgrade = occupationgrade;
		this.waysofpayment = waysofpayment;
		this.payeename = payeename;
		this.payeeidentifynumber = payeeidentifynumber;
		this.refundreason = refundreason;
		this.receiver = receiver;
		this.branchaccount = branchaccount;
		this.industry = industry;
	}

	public PrpcCustomerId getId() {
		return this.id;
	}

	public void setId(PrpcCustomerId id) {
		this.id = id;
	}

	public PrpcMaincovernote getPrpcMaincovernote() {
		return this.prpcMaincovernote;
	}

	public void setPrpcMaincovernote(PrpcMaincovernote prpcMaincovernote) {
		this.prpcMaincovernote = prpcMaincovernote;
	}

	public String getRiskcode() {
		return this.riskcode;
	}

	public void setRiskcode(String riskcode) {
		this.riskcode = riskcode;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getInsuredtype() {
		return this.insuredtype;
	}

	public void setInsuredtype(String insuredtype) {
		this.insuredtype = insuredtype;
	}

	public String getInsuredcode() {
		return this.insuredcode;
	}

	public void setInsuredcode(String insuredcode) {
		this.insuredcode = insuredcode;
	}

	public String getInsuredname() {
		return this.insuredname;
	}

	public void setInsuredname(String insuredname) {
		this.insuredname = insuredname;
	}

	public String getInsuredaddress() {
		return this.insuredaddress;
	}

	public void setInsuredaddress(String insuredaddress) {
		this.insuredaddress = insuredaddress;
	}

	public String getInsurednature() {
		return this.insurednature;
	}

	public void setInsurednature(String insurednature) {
		this.insurednature = insurednature;
	}

	public String getInsuredflag() {
		return this.insuredflag;
	}

	public void setInsuredflag(String insuredflag) {
		this.insuredflag = insuredflag;
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

	public String getIdentifytype() {
		return this.identifytype;
	}

	public void setIdentifytype(String identifytype) {
		this.identifytype = identifytype;
	}

	public String getIdentifynumber() {
		return this.identifynumber;
	}

	public void setIdentifynumber(String identifynumber) {
		this.identifynumber = identifynumber;
	}

	public String getCreditlevel() {
		return this.creditlevel;
	}

	public void setCreditlevel(String creditlevel) {
		this.creditlevel = creditlevel;
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

	public String getOccupationcode() {
		return this.occupationcode;
	}

	public void setOccupationcode(String occupationcode) {
		this.occupationcode = occupationcode;
	}

	public String getEducationcode() {
		return this.educationcode;
	}

	public void setEducationcode(String educationcode) {
		this.educationcode = educationcode;
	}

	public String getBank() {
		return this.bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getAccountname() {
		return this.accountname;
	}

	public void setAccountname(String accountname) {
		this.accountname = accountname;
	}

	public String getAccount() {
		return this.account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getLinkername() {
		return this.linkername;
	}

	public void setLinkername(String linkername) {
		this.linkername = linkername;
	}

	public String getPostaddress() {
		return this.postaddress;
	}

	public void setPostaddress(String postaddress) {
		this.postaddress = postaddress;
	}

	public String getPostcode() {
		return this.postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPhonenumber() {
		return this.phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
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

	public String getBenefitflag() {
		return this.benefitflag;
	}

	public void setBenefitflag(String benefitflag) {
		this.benefitflag = benefitflag;
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

	public String getWaysofpayment() {
		return this.waysofpayment;
	}

	public void setWaysofpayment(String waysofpayment) {
		this.waysofpayment = waysofpayment;
	}

	public String getPayeename() {
		return this.payeename;
	}

	public void setPayeename(String payeename) {
		this.payeename = payeename;
	}

	public String getPayeeidentifynumber() {
		return this.payeeidentifynumber;
	}

	public void setPayeeidentifynumber(String payeeidentifynumber) {
		this.payeeidentifynumber = payeeidentifynumber;
	}

	public String getRefundreason() {
		return this.refundreason;
	}

	public void setRefundreason(String refundreason) {
		this.refundreason = refundreason;
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
