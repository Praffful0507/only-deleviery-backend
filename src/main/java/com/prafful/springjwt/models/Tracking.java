package com.prafful.springjwt.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Embeddable
@Data
public class Tracking {

	@Column(name = "TRACKING_ID")
	private String id;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "STATUSLOCATION")
	private String statusLocation;

	@Column(name = "STATUSDATETIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date statusDateTime;

	@Column(name = "INSTRUCTIONS")
	private String instructions;
}
