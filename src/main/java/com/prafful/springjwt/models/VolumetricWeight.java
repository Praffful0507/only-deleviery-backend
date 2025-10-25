package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data	
public class VolumetricWeight {
    @Column(name = "length")
    private int length;
    
    @Column(name = "width")
    private int width;
    
    @Column(name = "height")
    private int height;

}
