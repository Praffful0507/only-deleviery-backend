package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PickUpAddress {

	public PickUpAddress(UserMultipleAdress userMultipleAdress) {
		this.contactName = userMultipleAdress.getContactName();
		this.email = userMultipleAdress.getEmail();
		this.phoneNumber = userMultipleAdress.getPhoneNumber();
		this.address = userMultipleAdress.getAddress();
		this.city = userMultipleAdress.getCity();
		this.state = userMultipleAdress.getState();
		this.pinCode = userMultipleAdress.getPinCode();
	}

	@Column(name = "PICKUP_CONTACT_NAME")
	private String contactName;

	@Column(name = "PICKUP_EMAIL")
	private String email;

	@Column(name = "PICKUP_PHONE_NUMBER")
	private String phoneNumber;

	@Column(name = "PICKUP_ADDRESS")
	private String address;

	@Column(name = "PICKUP_CITY")
	private String city;

	@Column(name = "PICKUP_STATE")
	private String state;

	@Column(name = "PICKUP_PIN_CODE")
	private String pinCode;
}
