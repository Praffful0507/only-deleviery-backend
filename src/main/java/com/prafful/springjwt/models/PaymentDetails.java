package com.prafful.springjwt.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class PaymentDetails {
    @Column(name = "PAYMENT_DETAILS_ID")
    private int id;
    
    @Column(name = "QUANTITY")
    private int quantity;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "SKU")
    private String sku;
    
    @Column(name = "UNITPRICE")
    private String unitPrice; // Kept as String to match JSON
    
    @Column(name = "DETAILS_ID")
    private String detailId;
}
