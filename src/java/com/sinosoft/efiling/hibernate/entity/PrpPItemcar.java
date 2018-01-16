package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-7-9 18:46:45 by Hibernate Tools 4.0.0

import java.util.Date;

import com.sinosoft.util.hibernate.entity.EntitySupport;


/**
 * PrpPItemcar generated by hbm2java
 */
@SuppressWarnings("serial")
public class PrpPItemcar extends EntitySupport<PrpPItemcarId> {

	private PrpPItemcarId id;
	private String policyno;
	private String riskCode;
	private String insuredTypeCode;
	private String carinsuredrelation;
	private String carowner;
	private String clauseType;
	private String agreedriverFlag;
	private String newdeviceFlag;
	private String carpolicyno;
	private String licenseno;
	private String licensecolorCode;
	private String carkindCode;
	private String hkFlag;
	private String hklicenseno;
	private String engineno;
	private String vinno;
	private String frameno;
	private String runAreaCode;
	private String runAreaName;
	private Double runmiles;
	private Date enrollDate;
	private Double useyears;
	private String modelCode;
	private String brandName;
	private String countrynature;
	private String countryCode;
	private String usenatureCode;
	private String businessclassCode;
	private Double seatcount;
	private Double toncount;
	private Double exhaustscale;
	private String colorCode;
	private String safedevice;
	private String parksite;
	private String ownerAddress;
	private String othernature;
	private String rateCode;
	private Date makeDate;
	private String carusage;
	private String currency;
	private Double purchaseprice;
	private Double actualValue;
	private String invoiceno;
	private String carloanFlag;
	private String insurerCode;
	private String lastinsurer;
	private String carcheckStatus;
	private String carchecker;
	private String carchecktime;
	private Double specialtreat;
	private String relievingAreaCode;
	private Double addoncount;
	private String cardealerCode;
	private String cardealerName;
	private String remark;
	private String flag;
	private String carcheckreason;
	private Double lviolatedtimes;
	private Double sviolatedtimes;
	private String licensekindCode;
	private String registmodelCode;
	private String secondhandcarFlag;
	private Double secondhandcarprice;
	private String runAreadesc;
	private String licenseno1;
	private String visaCode;
	private Double usemonths;
	private String chgusenatureCode;
	private Double modellosration;
	private String fuelType;
	private String certificateType;
	private Date certificateDate;
	private String certificateno;
	private Double priceadjustrate;
	private String dangerouscar;

	public PrpPItemcar() {
	}

	public PrpPItemcar(PrpPItemcarId id, String policyno, String riskCode) {
		this.id = id;
		this.policyno = policyno;
		this.riskCode = riskCode;
	}

	public PrpPItemcarId getId() {
		return id;
	}

	public void setId(PrpPItemcarId id) {
		this.id = id;
	}

	public String getPolicyno() {
		return policyno;
	}

	public void setPolicyno(String policyno) {
		this.policyno = policyno;
	}

	public String getRiskCode() {
		return riskCode;
	}

	public void setRiskCode(String riskCode) {
		this.riskCode = riskCode;
	}

	public String getInsuredTypeCode() {
		return insuredTypeCode;
	}

	public void setInsuredTypeCode(String insuredTypeCode) {
		this.insuredTypeCode = insuredTypeCode;
	}

	public String getCarinsuredrelation() {
		return carinsuredrelation;
	}

	public void setCarinsuredrelation(String carinsuredrelation) {
		this.carinsuredrelation = carinsuredrelation;
	}

	public String getCarowner() {
		return carowner;
	}

	public void setCarowner(String carowner) {
		this.carowner = carowner;
	}

	public String getClauseType() {
		return clauseType;
	}

	public void setClauseType(String clauseType) {
		this.clauseType = clauseType;
	}

	public String getAgreedriverFlag() {
		return agreedriverFlag;
	}

	public void setAgreedriverFlag(String agreedriverFlag) {
		this.agreedriverFlag = agreedriverFlag;
	}

	public String getNewdeviceFlag() {
		return newdeviceFlag;
	}

	public void setNewdeviceFlag(String newdeviceFlag) {
		this.newdeviceFlag = newdeviceFlag;
	}

	public String getCarpolicyno() {
		return carpolicyno;
	}

	public void setCarpolicyno(String carpolicyno) {
		this.carpolicyno = carpolicyno;
	}

	public String getLicenseno() {
		return licenseno;
	}

	public void setLicenseno(String licenseno) {
		this.licenseno = licenseno;
	}

	public String getLicensecolorCode() {
		return licensecolorCode;
	}

	public void setLicensecolorCode(String licensecolorCode) {
		this.licensecolorCode = licensecolorCode;
	}

	public String getCarkindCode() {
		return carkindCode;
	}

	public void setCarkindCode(String carkindCode) {
		this.carkindCode = carkindCode;
	}

	public String getHkFlag() {
		return hkFlag;
	}

	public void setHkFlag(String hkFlag) {
		this.hkFlag = hkFlag;
	}

	public String getHklicenseno() {
		return hklicenseno;
	}

	public void setHklicenseno(String hklicenseno) {
		this.hklicenseno = hklicenseno;
	}

	public String getEngineno() {
		return engineno;
	}

	public void setEngineno(String engineno) {
		this.engineno = engineno;
	}

	public String getVinno() {
		return vinno;
	}

	public void setVinno(String vinno) {
		this.vinno = vinno;
	}

	public String getFrameno() {
		return frameno;
	}

	public void setFrameno(String frameno) {
		this.frameno = frameno;
	}

	public String getRunAreaCode() {
		return runAreaCode;
	}

	public void setRunAreaCode(String runAreaCode) {
		this.runAreaCode = runAreaCode;
	}

	public String getRunAreaName() {
		return runAreaName;
	}

	public void setRunAreaName(String runAreaName) {
		this.runAreaName = runAreaName;
	}

	public Double getRunmiles() {
		return runmiles;
	}

	public void setRunmiles(Double runmiles) {
		this.runmiles = runmiles;
	}

	public Date getEnrollDate() {
		return enrollDate;
	}

	public void setEnrollDate(Date enrollDate) {
		this.enrollDate = enrollDate;
	}

	public Double getUseyears() {
		return useyears;
	}

	public void setUseyears(Double useyears) {
		this.useyears = useyears;
	}

	public String getModelCode() {
		return modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getCountrynature() {
		return countrynature;
	}

	public void setCountrynature(String countrynature) {
		this.countrynature = countrynature;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getUsenatureCode() {
		return usenatureCode;
	}

	public void setUsenatureCode(String usenatureCode) {
		this.usenatureCode = usenatureCode;
	}

	public String getBusinessclassCode() {
		return businessclassCode;
	}

	public void setBusinessclassCode(String businessclassCode) {
		this.businessclassCode = businessclassCode;
	}

	public Double getSeatcount() {
		return seatcount;
	}

	public void setSeatcount(Double seatcount) {
		this.seatcount = seatcount;
	}

	public Double getToncount() {
		return toncount;
	}

	public void setToncount(Double toncount) {
		this.toncount = toncount;
	}

	public Double getExhaustscale() {
		return exhaustscale;
	}

	public void setExhaustscale(Double exhaustscale) {
		this.exhaustscale = exhaustscale;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public String getSafedevice() {
		return safedevice;
	}

	public void setSafedevice(String safedevice) {
		this.safedevice = safedevice;
	}

	public String getParksite() {
		return parksite;
	}

	public void setParksite(String parksite) {
		this.parksite = parksite;
	}

	public String getOwnerAddress() {
		return ownerAddress;
	}

	public void setOwnerAddress(String ownerAddress) {
		this.ownerAddress = ownerAddress;
	}

	public String getOthernature() {
		return othernature;
	}

	public void setOthernature(String othernature) {
		this.othernature = othernature;
	}

	public String getRateCode() {
		return rateCode;
	}

	public void setRateCode(String rateCode) {
		this.rateCode = rateCode;
	}

	public Date getMakeDate() {
		return makeDate;
	}

	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}

	public String getCarusage() {
		return carusage;
	}

	public void setCarusage(String carusage) {
		this.carusage = carusage;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getPurchaseprice() {
		return purchaseprice;
	}

	public void setPurchaseprice(Double purchaseprice) {
		this.purchaseprice = purchaseprice;
	}

	public Double getActualValue() {
		return actualValue;
	}

	public void setActualValue(Double actualValue) {
		this.actualValue = actualValue;
	}

	public String getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}

	public String getCarloanFlag() {
		return carloanFlag;
	}

	public void setCarloanFlag(String carloanFlag) {
		this.carloanFlag = carloanFlag;
	}

	public String getInsurerCode() {
		return insurerCode;
	}

	public void setInsurerCode(String insurerCode) {
		this.insurerCode = insurerCode;
	}

	public String getLastinsurer() {
		return lastinsurer;
	}

	public void setLastinsurer(String lastinsurer) {
		this.lastinsurer = lastinsurer;
	}

	public String getCarcheckStatus() {
		return carcheckStatus;
	}

	public void setCarcheckStatus(String carcheckStatus) {
		this.carcheckStatus = carcheckStatus;
	}

	public String getCarchecker() {
		return carchecker;
	}

	public void setCarchecker(String carchecker) {
		this.carchecker = carchecker;
	}

	public String getCarchecktime() {
		return carchecktime;
	}

	public void setCarchecktime(String carchecktime) {
		this.carchecktime = carchecktime;
	}

	public Double getSpecialtreat() {
		return specialtreat;
	}

	public void setSpecialtreat(Double specialtreat) {
		this.specialtreat = specialtreat;
	}

	public String getRelievingAreaCode() {
		return relievingAreaCode;
	}

	public void setRelievingAreaCode(String relievingAreaCode) {
		this.relievingAreaCode = relievingAreaCode;
	}

	public Double getAddoncount() {
		return addoncount;
	}

	public void setAddoncount(Double addoncount) {
		this.addoncount = addoncount;
	}

	public String getCardealerCode() {
		return cardealerCode;
	}

	public void setCardealerCode(String cardealerCode) {
		this.cardealerCode = cardealerCode;
	}

	public String getCardealerName() {
		return cardealerName;
	}

	public void setCardealerName(String cardealerName) {
		this.cardealerName = cardealerName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getCarcheckreason() {
		return carcheckreason;
	}

	public void setCarcheckreason(String carcheckreason) {
		this.carcheckreason = carcheckreason;
	}

	public Double getLviolatedtimes() {
		return lviolatedtimes;
	}

	public void setLviolatedtimes(Double lviolatedtimes) {
		this.lviolatedtimes = lviolatedtimes;
	}

	public Double getSviolatedtimes() {
		return sviolatedtimes;
	}

	public void setSviolatedtimes(Double sviolatedtimes) {
		this.sviolatedtimes = sviolatedtimes;
	}

	public String getLicensekindCode() {
		return licensekindCode;
	}

	public void setLicensekindCode(String licensekindCode) {
		this.licensekindCode = licensekindCode;
	}

	public String getRegistmodelCode() {
		return registmodelCode;
	}

	public void setRegistmodelCode(String registmodelCode) {
		this.registmodelCode = registmodelCode;
	}

	public String getSecondhandcarFlag() {
		return secondhandcarFlag;
	}

	public void setSecondhandcarFlag(String secondhandcarFlag) {
		this.secondhandcarFlag = secondhandcarFlag;
	}

	public Double getSecondhandcarprice() {
		return secondhandcarprice;
	}

	public void setSecondhandcarprice(Double secondhandcarprice) {
		this.secondhandcarprice = secondhandcarprice;
	}

	public String getRunAreadesc() {
		return runAreadesc;
	}

	public void setRunAreadesc(String runAreadesc) {
		this.runAreadesc = runAreadesc;
	}

	public String getLicenseno1() {
		return licenseno1;
	}

	public void setLicenseno1(String licenseno1) {
		this.licenseno1 = licenseno1;
	}

	public String getVisaCode() {
		return visaCode;
	}

	public void setVisaCode(String visaCode) {
		this.visaCode = visaCode;
	}

	public Double getUsemonths() {
		return usemonths;
	}

	public void setUsemonths(Double usemonths) {
		this.usemonths = usemonths;
	}

	public String getChgusenatureCode() {
		return chgusenatureCode;
	}

	public void setChgusenatureCode(String chgusenatureCode) {
		this.chgusenatureCode = chgusenatureCode;
	}

	public Double getModellosration() {
		return modellosration;
	}

	public void setModellosration(Double modellosration) {
		this.modellosration = modellosration;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getCertificateType() {
		return certificateType;
	}

	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}

	public Date getCertificateDate() {
		return certificateDate;
	}

	public void setCertificateDate(Date certificateDate) {
		this.certificateDate = certificateDate;
	}

	public String getCertificateno() {
		return certificateno;
	}

	public void setCertificateno(String certificateno) {
		this.certificateno = certificateno;
	}

	public Double getPriceadjustrate() {
		return priceadjustrate;
	}

	public void setPriceadjustrate(Double priceadjustrate) {
		this.priceadjustrate = priceadjustrate;
	}

	public String getDangerouscar() {
		return dangerouscar;
	}

	public void setDangerouscar(String dangerouscar) {
		this.dangerouscar = dangerouscar;
	}
	
	
}
