package com.sinosoft.efiling.hibernate.entity;

import com.sinosoft.util.hibernate.entity.EntityOperatorSupport;

public class FileBoxVersion extends EntityOperatorSupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4519296679906998108L;

	private int capacity;

	public FileBoxVersion() {
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}
