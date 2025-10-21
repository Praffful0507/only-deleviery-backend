package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "USER_PICK_UP_ADDRESS")
public class UserMultipleAdress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_PICK_UP_ADDRESS_ID")
	private Long id;

	@Column(name = "CONTACT_NAME")
	private String contactName;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "ADDRESS")
	private String address;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "PIN_CODE")
	private String pinCode;

	@Column(name = "EMAIL_ID")
	private String emailId;

	@Column(name = "IS_PRIMARY")
	private Boolean isPrimary = false;

}
