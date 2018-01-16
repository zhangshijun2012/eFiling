package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-3-8 17:57:18 by Hibernate Tools 4.0.0

import com.sinosoft.util.hibernate.entity.EntitySupport;

/**
 * UserGradePower generated by hbm2java
 */
public class UserGradePower extends EntitySupport<UserGradePowerId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4023058178166076378L;
	private UserGrade userGrade;
	private String permitComCode;
	private String exceptComCode;
	private String permitUserCode;
	private String permitRiskCode;
	private String codePermitComCode;
	private String codeExceptComCode;
	private String customerExceptComCode;
	private String customerPermitComCode;
	private String exceptUserCode;

	public UserGradePower() {
	}

	public UserGradePower(UserGradePowerId id, UserGrade userGrade, String permitComCode) {
		this.id = id;
		this.userGrade = userGrade;
		this.permitComCode = permitComCode;
	}

	public UserGradePower(UserGradePowerId id, UserGrade userGrade, String permitComCode, String exceptComCode,
			String permitUserCode, String permitRiskCode, String codePermitComCode, String codeExceptComCode,
			String customerExceptComCode, String customerPermitComCode, String exceptUserCode) {
		this.id = id;
		this.userGrade = userGrade;
		this.permitComCode = permitComCode;
		this.exceptComCode = exceptComCode;
		this.permitUserCode = permitUserCode;
		this.permitRiskCode = permitRiskCode;
		this.codePermitComCode = codePermitComCode;
		this.codeExceptComCode = codeExceptComCode;
		this.customerExceptComCode = customerExceptComCode;
		this.customerPermitComCode = customerPermitComCode;
		this.exceptUserCode = exceptUserCode;
	}

	public UserGrade getUserGrade() {
		return this.userGrade;
	}

	public void setUserGrade(UserGrade userGrade) {
		this.userGrade = userGrade;
	}

	public String getPermitComCode() {
		return this.permitComCode;
	}

	public void setPermitComCode(String permitComCode) {
		this.permitComCode = permitComCode;
	}

	public String getExceptComCode() {
		return this.exceptComCode;
	}

	public void setExceptComCode(String exceptComCode) {
		this.exceptComCode = exceptComCode;
	}

	public String getPermitUserCode() {
		return this.permitUserCode;
	}

	public void setPermitUserCode(String permitUserCode) {
		this.permitUserCode = permitUserCode;
	}

	public String getPermitRiskCode() {
		return this.permitRiskCode;
	}

	public void setPermitRiskCode(String permitRiskCode) {
		this.permitRiskCode = permitRiskCode;
	}

	public String getCodePermitComCode() {
		return this.codePermitComCode;
	}

	public void setCodePermitComCode(String codePermitComCode) {
		this.codePermitComCode = codePermitComCode;
	}

	public String getCodeExceptComCode() {
		return this.codeExceptComCode;
	}

	public void setCodeExceptComCode(String codeExceptComCode) {
		this.codeExceptComCode = codeExceptComCode;
	}

	public String getCustomerExceptComCode() {
		return this.customerExceptComCode;
	}

	public void setCustomerExceptComCode(String customerExceptComCode) {
		this.customerExceptComCode = customerExceptComCode;
	}

	public String getCustomerPermitComCode() {
		return this.customerPermitComCode;
	}

	public void setCustomerPermitComCode(String customerPermitComCode) {
		this.customerPermitComCode = customerPermitComCode;
	}

	public String getExceptUserCode() {
		return this.exceptUserCode;
	}

	public void setExceptUserCode(String exceptUserCode) {
		this.exceptUserCode = exceptUserCode;
	}

}
