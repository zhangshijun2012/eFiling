package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-7-9 18:46:45 by Hibernate Tools 4.0.0

import java.io.Serializable;

/**
 * PrpPItemcarId generated by hbm2java
 */
@SuppressWarnings("serial")
public class PrpPItemcarId implements Serializable {

	private String endorseno;
	private double itemno;

	public PrpPItemcarId() {
	}

	public PrpPItemcarId(String endorseno, double itemno) {
		this.endorseno = endorseno;
		this.itemno = itemno;
	}

	public String getEndorseno() {
		return this.endorseno;
	}

	public void setEndorseno(String endorseno) {
		this.endorseno = endorseno;
	}

	public double getItemno() {
		return this.itemno;
	}

	public void setItemno(double itemno) {
		this.itemno = itemno;
	}

	public boolean equals(Object other) {
		if ((this == other)) return true;
		if ((other == null)) return false;
		if (!(other instanceof PrpPItemcarId)) return false;
		PrpPItemcarId castOther = (PrpPItemcarId) other;

		return ((this.getEndorseno() == castOther.getEndorseno()) || (this.getEndorseno() != null
				&& castOther.getEndorseno() != null && this.getEndorseno().equals(castOther.getEndorseno())))
				&& (this.getItemno() == castOther.getItemno());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getEndorseno() == null ? 0 : this.getEndorseno().hashCode());
		result = 37 * result + (int) this.getItemno();
		return result;
	}

}