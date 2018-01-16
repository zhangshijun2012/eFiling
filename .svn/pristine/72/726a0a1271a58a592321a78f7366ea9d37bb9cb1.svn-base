package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-7-31 18:53:08 by Hibernate Tools 4.0.0

import java.io.Serializable;

@SuppressWarnings("serial")
public class PrppCustomerId implements Serializable {

	private String endorseno;
	private double serialno;

	public PrppCustomerId() {
	}

	public PrppCustomerId(String endorseno, double serialno) {
		this.endorseno = endorseno;
		this.serialno = serialno;
	}

	public String getEndorseno() {
		return this.endorseno;
	}

	public void setEndorseno(String endorseno) {
		this.endorseno = endorseno;
	}

	public double getSerialno() {
		return this.serialno;
	}

	public void setSerialno(double serialno) {
		this.serialno = serialno;
	}

	public boolean equals(Object other) {
		if ((this == other)) return true;
		if ((other == null)) return false;
		if (!(other instanceof PrppCustomerId)) return false;
		PrppCustomerId castOther = (PrppCustomerId) other;

		return ((this.getEndorseno() == castOther.getEndorseno()) || (this.getEndorseno() != null
				&& castOther.getEndorseno() != null && this.getEndorseno().equals(castOther.getEndorseno())))
				&& (this.getSerialno() == castOther.getSerialno());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getEndorseno() == null ? 0 : this.getEndorseno().hashCode());
		result = 37 * result + (int) this.getSerialno();
		return result;
	}

}
