package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Data;

@Embeddable
@Data
public class PackageDetails {

    @Column(name = "DEADWEIGHT")
    private double deadWeight;
    
    @Column(name = "APPLICABLEWEIGHT")
    private double applicableWeight;
    
    @Embedded
    private VolumetricWeight volumetricWeight;
}
