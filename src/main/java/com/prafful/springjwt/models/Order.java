package com.prafful.springjwt.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "ORDERS")
@Data
public class Order {
	
	@Id
	@Column(name = "ORDER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "ORDER_DETAILS_ID")
	private Integer orderId;

	@Column(name = "CHANNEL")
	private String channel;

	@Column(name = "COMPOSITEORDERID")
	private String compositeOrderId;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATEDAT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name = "UPDATEDAT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedAt;

	@Column(name = "VIRSION")
	private Integer version;

	@Column(name = "AWB_NUMBER")
	private String awbNumber;

	@Column(name = "COURIERSERVICENAME")
	private String courierServiceName;

	@Column(name = "ESTIMATEDDELIVERYDATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date estimatedDeliveryDate;

	@Column(name = "PROVIDER")
	private String provider;

	@Column(name = "SHIPMENTCREATEDAT")
	@Temporal(TemporalType.TIMESTAMP)
	private Date shipmentCreatedAt;

	@Column(name = "SHIPMENT_ID")
	private String shipmentId;

	@Column(name = "TOTALFREIGHTCHARGES")
	private Integer totalFreightCharges;

	@Column(name = "ZONE")
	private String zone;
	
	@ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ORDER_TRACKING", joinColumns = @JoinColumn(name = "ORDER_TRACKING_ID"))
    private List<Tracking> tracking = new ArrayList<>();
    
    @Embedded
    private PaymentDetails paymentDetails;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ORDER_PRODUCT_DETAILS", joinColumns = @JoinColumn(name = "ORDER_PRODUCT_DETAILS_ID"))
    private List<ProductDetail> productDetails = new ArrayList<>();
    
    @Embedded
    private PickUpAddress pickupAddress;
    
    @Embedded
    private ReceiverAddress receiverAddress;
    
    @Embedded
    private PackageDetails packageDetails; 
}
