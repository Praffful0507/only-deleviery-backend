package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ADDHAR_DETAILS")
public class AadharDetails {

    @Id
    @Column(name = "EMAIL_ID", length = 20, nullable = false)
    private String emailID;

    @Column(name = "AADHAR_NO", length = 20, nullable = false)
    private String aadharNo;

    @Column(name = "GUARDIAN_NAME", length = 100)
    private String guardianName;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "ADDRESS", length = 255)
    private String address;

    @Column(name = "STATE", length = 50)
    private String state;
}
