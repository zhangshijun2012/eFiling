package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-3-8 17:57:18 by Hibernate Tools 4.0.0

import java.util.HashSet;
import java.util.Set;

import com.sinosoft.util.hibernate.entity.EntitySupport;

/**
 * 产品线配置信息
 * 
 * @author LuoGang
 */
public class Product extends EntitySupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6871020191640582553L;
	private String name;
	private String remarks;
	private Set<FileBoxVersion> fileBoxVersions = new HashSet<FileBoxVersion>(0);
	private Set<ProductRisk> productRisks = new HashSet<ProductRisk>(0);

	public Product() {
	}

	public Product(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return this.remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Set<ProductRisk> getProductRisks() {
		return this.productRisks;
	}

	public void setProductRisks(Set<ProductRisk> productRisks) {
		this.productRisks = productRisks;
	}

	public Set<FileBoxVersion> getFileBoxVersions() {
		return fileBoxVersions;
	}

	public void setFileBoxVersions(Set<FileBoxVersion> fileBoxVersions) {
		this.fileBoxVersions = fileBoxVersions;
	}

}
