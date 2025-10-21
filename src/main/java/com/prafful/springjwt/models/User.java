package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Entity
@Table(name = "USER")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "FULL_NAME")
	private String fullname;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "COMPANY")
	private String company;

	@Column(name = "MONTHLY_ORDERS")
	private Integer monthlyOrders;

	@Column(name = "PASSWORD")
	private String password;

	@Transient
	private String confirmedPassword;

	@Column(name = "CHECKED")
	private boolean checked;
	
	//what it mean ?
	@Column(name = "KYC")
	private boolean kycDone = false;

	//what is this ?
	@Column(name = "IS_VERIFIED")
	private boolean isVerified = false;
	
	@Column(name = "SELECTED_TYPE")
	private String selectedType;
		
	@Transient
	private String userName = this.email;
	
//	public Boolean getIsVerified() {
//		return this.isVerified;
//	}
	
}
