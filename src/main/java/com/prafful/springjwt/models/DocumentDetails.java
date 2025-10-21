package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "DOCUMENT_DETAILS")
public class DocumentDetails {

	@Id
	@Column(name = "EMAIL_ID")
	private String emailID;

	@Column(name = "AADHAR_NO")
	private String aadharNo;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "GUARDIAN_NAME")
	private String guardianName;

	@Column(name = "NAME")
	private String name;

	@Column(name = "OTP")
	private String otp;

	@Column(name = "PAN")
	private String pan;

	@Column(name = "PAN_NAME")
	private String panName;

	@Column(name = "PAN_TYPE")
	private String panType;

	@Column(name = "REF_ID")
	private String refId;

	@Column(name = "STATE")
	private String state;
	
	@Transient
	private String aadhaarNumber;
	
	@Transient
	private String aadhaarNo;
	
	@Transient
	private String city;
	
	public String getAadhaarNumber() {
		return this.aadharNo;
	}
}
