package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-3-8 17:57:18 by Hibernate Tools 4.0.0

import com.sinosoft.util.hibernate.entity.EntitySupport;

/**
 * 产品线与险种代码的对应关系.一个险种代码只能对应一个产品线
 * 
 * @author LuoGang
 * 
 */
public class ProductRisk extends EntitySupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2462206143185141216L;
	// private String riskCode;
	private Product product;
	private String remarks;

	public ProductRisk() {
	}

	public ProductRisk(String riskCode, Product product) {
		this.id = riskCode;
		this.product = product;
	}

	public ProductRisk(String riskCode, Product product, String remarks) {
		this.id = riskCode;
		this.product = product;
		this.remarks = remarks;
	}

	public String getRiskCode() {
		return this.id;
	}

	public void setRiskCode(String riskCode) {
		this.id = riskCode;
	}

	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
