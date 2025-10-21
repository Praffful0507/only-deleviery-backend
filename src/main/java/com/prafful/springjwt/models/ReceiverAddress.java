package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ReceiverAddress {

	@Column(name = "RECEIVER_CONTACT_NAME")
	private String contactName;

	@Column(name = "RECEIVER_EMAIL")
	private String email;

	@Column(name = "RECEIVER_PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "RECEIVER_ADDRESS")
	private String address;

	@Column(name = "RECEIVER_CITY")
	private String city;

	@Column(name = "RECEIVER_STATE")
	private String state;

	@Column(name = "RECEIVER_PIN_CODE")
	private String pinCode;


}
