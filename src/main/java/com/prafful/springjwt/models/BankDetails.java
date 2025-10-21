package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "BANK_DETAILS")
public class BankDetails {

	@Id
	@Column(name = "EMAIL_ID")
	private String EmailId;

	@Column(name = "ACCOUNT_NUMBER")
	private String accountNumber;

	@Column(name = "IFSC")
	private String ifsc;

	@Column(name = "BANK_NAME")
	private String bank;

	@Column(name = "BRANCH_NAME")
	private String branch;

	@Column(name = "CITY")
	private String city;

	@Column(name = "NAME_AT_BANK")
	private String nameAtBank;

	@Transient
	private String accountNo;
	
	@Transient
	private String refId;
	
	@Transient
	private String branchName;
	
	@Transient
	private String bankName;

	public void setAccountNo(String accounNumber) {
		this.accountNo = accounNumber;
		this.accountNumber = accounNumber;
	}
	
	public void setBranchName(String branchName) {
		this.branchName = branchName;
		this.branch = branchName;
	}
	
	public void setBankName(String bankName) {
		this.bankName = bankName;
		this.bank = bankName;
	}
}
