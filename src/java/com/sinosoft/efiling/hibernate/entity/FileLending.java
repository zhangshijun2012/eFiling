package com.sinosoft.efiling.hibernate.entity;

// Generated 2013-4-25 20:00:25 by Hibernate Tools 4.0.0

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.sinosoft.util.hibernate.entity.EntityOperatorSupport;

/**
 * 档案借阅信息
 * 
 * @author LuoGang
 * 
 */
public class FileLending extends EntityOperatorSupport<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3569094271936885065L;
	private FileLendingReturn fileLendingReturn;
	private User borrower;
	private Company borrowerDept;
	private Company borrowerCompany;
	private int days;
	private Date expectedReturnDate;
	private String reason;
	private Date returnDate;
	private String borrowerName; // 借阅人名称
	private String borrowerDeptName; // 借阅人所属部门名称
	private String borrowerCompanyName; // 借阅人所属分公司名称
	private String borrowerEmail; // 借阅人邮箱
	private Set<FileLendingDetail> fileLendingDetails = new HashSet<FileLendingDetail>(0);

	public String getBorrowerEmail() {
		return borrowerEmail;
	}

	public void setBorrowerEmail(String borrowerEmail) {
		this.borrowerEmail = borrowerEmail;
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public String getBorrowerDeptName() {
		return borrowerDeptName;
	}

	public void setBorrowerDeptName(String borrowerDeptName) {
		this.borrowerDeptName = borrowerDeptName;
	}

	public String getBorrowerCompanyName() {
		return borrowerCompanyName;
	}

	public void setBorrowerCompanyName(String borrowerCompanyName) {
		this.borrowerCompanyName = borrowerCompanyName;
	}

	public FileLending() {
	}

	public FileLendingReturn getFileLendingReturn() {
		return fileLendingReturn;
	}

	public void setFileLendingReturn(FileLendingReturn fileLendingReturn) {
		this.fileLendingReturn = fileLendingReturn;
	}

	public User getBorrower() {
		return borrower;
	}

	public void setBorrower(User borrower) {
		this.borrower = borrower;
	}

	public Company getBorrowerDept() {
		return borrowerDept;
	}

	public void setBorrowerDept(Company borrowerDept) {
		this.borrowerDept = borrowerDept;
	}

	public Company getBorrowerCompany() {
		return borrowerCompany;
	}

	public void setBorrowerCompany(Company borrowerCompany) {
		this.borrowerCompany = borrowerCompany;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Date getExpectedReturnDate() {
		return expectedReturnDate;
	}

	public void setExpectedReturnDate(Date expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}

	public Set<FileLendingDetail> getFileLendingDetails() {
		return fileLendingDetails;
	}

	public void setFileLendingDetails(Set<FileLendingDetail> fileLendingDetails) {
		this.fileLendingDetails = fileLendingDetails;
	}
}
