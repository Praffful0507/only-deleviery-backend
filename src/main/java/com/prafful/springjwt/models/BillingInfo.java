package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "BILLING_INFO")
@Data
public class BillingInfo {

	@Id
	@Column(name = "EMAIL_ID")
	private String emailID;

	@Column(name = "ADDRESS_LINE")
	private String address;

	@Column(name = "CITY")
	private String city;

	@Column(name = "POSTAL_CODE")
	private String postalCode;

	@Column(name = "STATE")
	private String state;
}
