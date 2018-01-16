package com.sinosoft.efiling.hibernate.entity;

import java.io.Serializable;

// Generated 2013-7-31 18:53:08 by Hibernate Tools 4.0.0


@SuppressWarnings("serial")
public class PrpcCustomerId implements Serializable {

	private String policyno;
	private double serialno;

	public PrpcCustomerId() {
	}

	public PrpcCustomerId(String policyno, double serialno) {
		this.policyno = policyno;
		this.serialno = serialno;
	}

	public String getPolicyno() {
		return this.policyno;
	}

	public void setPolicyno(String policyno) {
		this.policyno = policyno;
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
		if (!(other instanceof PrpcCustomerId)) return false;
		PrpcCustomerId castOther = (PrpcCustomerId) other;

		return ((this.getPolicyno() == castOther.getPolicyno()) || (this.getPolicyno() != null
				&& castOther.getPolicyno() != null && this.getPolicyno().equals(castOther.getPolicyno())))
				&& (this.getSerialno() == castOther.getSerialno());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (getPolicyno() == null ? 0 : this.getPolicyno().hashCode());
		result = 37 * result + (int) this.getSerialno();
		return result;
	}

}
