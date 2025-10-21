package com.prafful.springjwt.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.HtmlConverter;
import com.prafful.springjwt.models.BankDetails;
import com.prafful.springjwt.models.BillingInfo;
import com.prafful.springjwt.models.DocumentDetails;
import com.prafful.springjwt.models.Order;
import com.prafful.springjwt.models.User;
import com.prafful.springjwt.models.UserMultipleAdress;
import com.prafful.springjwt.repository.AadharDetailsRepository;
import com.prafful.springjwt.repository.BankDetailsRepository;
import com.prafful.springjwt.repository.BillinfoRepository;
import com.prafful.springjwt.repository.DocumentDetailsRepository;
import com.prafful.springjwt.repository.OrderRepository;
import com.prafful.springjwt.repository.UserMultipleAdressRepository;
import com.prafful.springjwt.repository.UserRepository;
import com.prafful.springjwt.security.jwt.JwtUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class Servicess {

	BillinfoRepository billinfoRepository;

	BankDetailsRepository bankDetailsRepository;

	UserRepository userRepository;

	AadharDetailsRepository aadharDetailsRepository;

	DocumentDetailsRepository documentDetailsRepository;

	JwtUtils jwtUtils;

	ObjectMapper mapper = new ObjectMapper();

	UserMultipleAdressRepository userMultipleAdressRepository;

	OrderRepository orderRepository;

	private ObjectMapper objectMapper;

	public void SaveBillingInfo(Map<String, Object> billingInfo, String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		BillingInfo billingInfo1 = mapper.convertValue(billingInfo.get("billingInfo"), BillingInfo.class);
		billingInfo1.setEmailID(emailId);
		billinfoRepository.save(billingInfo1);
	}

	public Map<String, Object> verifyBankAccount(BankDetails bankDetails) {
		Map<String, Object> response = new HashMap<>();
		bankDetails.setBank("FEDERAL BANK");
		bankDetails.setBranch("CP");
		bankDetails.setNameAtBank("Prafful Agarwal");
		bankDetails.setCity("DELHI");
		response.put("data", bankDetails);
		response.put("success", true);
		return response;
	}

	public Map<String, Object> generateAddharotp(Map<String, Object> addharOtp) {
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> data = new HashMap<>();
		response.put("message", "Sent OTP SuccesFully");
		data.put("ref_id", "12345");
		response.put("data", data);
		return response;
	}

	public Map<String, Object> verifyAddharOtp(Map<String, Object> verifyAddharOtp) {
		Map<String, Object> response = new HashMap<>();
		response.put("name", "Prafful");
		response.put("sonOf", "Hari Shyam Agarwal");
		response.put("address", "Nakkash No.1 nai Sadak");
		response.put("state", "MP");
		return Map.of("data", response, "success", true);
	}

	public Map<String, Object> verifyPanCard(Map<String, Object> verifyPanCard) {
		Map<String, Object> response = new HashMap<>();
		response.put("nameProvided", "Prafful");
		response.put("panType", "Indivisual");
		return Map.of("data", response, "success", true);
	}

	public Map<String, Object> vericationisDone(Map<String, Object> payload, String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		User user = userRepository.findByEmail(emailId).get();
		Map<String, Object> payloads = (Map<String, Object>) payload.get("payload");
		BankDetails bankDetails = mapper.convertValue(payloads.get("bankDetails"), BankDetails.class);
		DocumentDetails documentDetails = mapper.convertValue(payloads.get("documentDetails"), DocumentDetails.class);
		bankDetails.setEmailId(emailId);
		documentDetails.setEmailID(emailId);
		bankDetailsRepository.save(bankDetails);
		user.setSelectedType((String) payloads.get("selectedType"));
		user.setVerified((Boolean) payloads.get("isVerified"));
		user.setKycDone(true);
		user.setChecked(true);
		bankDetailsRepository.save(bankDetails);
		documentDetailsRepository.save(documentDetails);
		userRepository.save(user);
		return Map.of("success", true);
	}

	public DocumentDetails getAadhaarDetails(String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		DocumentDetails aadharDetails = documentDetailsRepository.findById(emailId).get();
		return aadharDetails;
	}

	public BankDetails getKycOfBankAccount(String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		return bankDetailsRepository.findById(emailId).get();
	}

	public User getUserDetils(String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		User user = userRepository.findByEmail(emailId).get();
		return user;
	}

	public BillingInfo getBillingInfo(String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		return billinfoRepository.findById(emailId).get();
	}

	public Map<String, Object> getPinCode(String pinCode) {
		return Map.of("city", "Gwalior", "state", "MP");
	}

	public void addUpdateAdress(UserMultipleAdress multipleAdress, String authHeader) {
		String emailId = jwtUtils.getEmailId(authHeader);
		multipleAdress.setEmail(emailId);
		userMultipleAdressRepository.save(multipleAdress);
	}

	public Map<String, Object> getPickUpAddress() {
		List<UserMultipleAdress> addresss = userMultipleAdressRepository.findAllByEmail(SecurityUtils.getEmailId());
		// Map<String, Object> trueResponse = new HashMap<>();
		List<Object> addResponse = new ArrayList<>();
		addresss.forEach((e) -> {
			Map<String, Object> response = Map.of("pickupAddress", e, "isPrimary", e.getIsPrimary(), "_id", e.getId());
			addResponse.add(response);
		});
		return Map.of("data", addResponse, "success", true);
	}

	public void updatePrimary(Long pickupId) {
		UserMultipleAdress userMultipleAdress = userMultipleAdressRepository.findById(pickupId).get();
		userMultipleAdress.setIsPrimary(!userMultipleAdress.getIsPrimary());
		userMultipleAdressRepository.save(userMultipleAdress);
	}

	public void updatePickUpAddress(Long id, UserMultipleAdress userMultipleAddress) {
		UserMultipleAdress userMultipleAdre = userMultipleAdressRepository.findById(id).get();
		userMultipleAddress.setIsPrimary(userMultipleAdre.getIsPrimary());
		userMultipleAddress.setId(id);
		userMultipleAdressRepository.save(userMultipleAddress);
	}

	public void deletePickUpAdress(Long id) {
		userMultipleAdressRepository.deleteById(id);
	}

	public Map<String, Object> isUserVerified() {
		User user = userRepository.findByEmail(SecurityUtils.getEmailId()).get();
		return Map.of("isSeller", user.isVerified());
	}

	@Transactional
	public void saveOrder(Order order) {
		order.setUserId(SecurityUtils.getEmailId());
		orderRepository.save(order);
	}

	public Map<String, Object> getOrders() {
		List<Order> orders = orderRepository.findAllByUserId(SecurityUtils.getEmailId());
		List<UserMultipleAdress> addresss = userMultipleAdressRepository.findAllByEmail(SecurityUtils.getEmailId());
		return Map.of("orders", orders, "pickupLocations", addresss, "courierServices", List.of(), "currentPage", 1,
				"totalCount", 1, "totalPages", 1);
	}

	@Transactional
	public Map<String, Object> getShipOrders(Long id) throws JsonMappingException, JsonProcessingException {
		String estimateOfCourier = """
				[
				  {
				    "courierServiceName": "Dtdc Surface 0.5KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1289.6,
				      "gst": "232.13",
				      "finalCharges": "1521.73"
				    },
				    "provider": "Dtdc",
				    "courierType": "Domestic (Surface)",
				    "courier": "B2C SMART EXPRESS",
				    "serviceName": "Dtdc Surface 0.5KG",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.377Z"
				  },
				  {
				    "courierServiceName": "Dtdc Surface 1KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1067.95,
				      "gst": "192.23",
				      "finalCharges": "1260.18"
				    },
				    "provider": "Dtdc",
				    "courierType": "Domestic (Surface)",
				    "courier": "B2C SMART EXPRESS",
				    "serviceName": "Dtdc Surface 1KG",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Amazon Surface 0.5KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1209,
				      "gst": "217.62",
				      "finalCharges": "1426.62"
				    },
				    "provider": "Amazon Shipping",
				    "courierType": "Domestic (Surface)",
				    "courier": "",
				    "serviceName": "Amazon Surface 0.5KG ",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Amazon Surface 1KG",
				    "cod": 0,
				    "forward": {
				      "charges": 826.1499999999999,
				      "gst": "148.71",
				      "finalCharges": "974.86"
				    },
				    "provider": "Amazon Shipping",
				    "courierType": "Domestic (Surface)",
				    "courier": "",
				    "serviceName": "Amazon Surface 1KG ",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Dtdc Air 0.5KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1692.5999999999997,
				      "gst": "304.67",
				      "finalCharges": "1997.27"
				    },
				    "provider": "Dtdc",
				    "courierType": "Domestic (Air)",
				    "courier": "B2C PRIORITY",
				    "serviceName": "Dtdc Air 0.5KG",
				    "estimatedDeliveryDate": "2025-10-14T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Dtdc Air 1KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1612,
				      "gst": "290.16",
				      "finalCharges": "1902.16"
				    },
				    "provider": "Dtdc",
				    "courierType": "Domestic (Air)",
				    "courier": "B2C PRIORITY",
				    "serviceName": "Dtdc Air 1KG",
				    "estimatedDeliveryDate": "2025-10-14T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Delhivery Air 0.25KG",
				    "cod": 0,
				    "forward": {
				      "charges": 3224,
				      "gst": "580.32",
				      "finalCharges": "3804.32"
				    },
				    "provider": "Delhivery",
				    "courierType": "Domestic (Air)",
				    "courier": "",
				    "serviceName": "Delhivery Air 0.25KG",
				    "estimatedDeliveryDate": "2025-10-15T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Delhivery Air 0.5KG",
				    "cod": 0,
				    "forward": {
				      "charges": 2095.6,
				      "gst": "377.21",
				      "finalCharges": "2472.81"
				    },
				    "provider": "Delhivery",
				    "courierType": "Domestic (Air)",
				    "courier": "",
				    "serviceName": "Delhivery Air 0.5KG",
				    "estimatedDeliveryDate": "2025-10-15T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Delhivery Air 1KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1773.2000000000003,
				      "gst": "319.18",
				      "finalCharges": "2092.38"
				    },
				    "provider": "Delhivery",
				    "courierType": "Domestic (Air)",
				    "courier": "",
				    "serviceName": "Delhivery Air 1KG",
				    "estimatedDeliveryDate": "2025-10-15T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Delhivery Surface 0.25KG",
				    "cod": 0,
				    "forward": {
				      "charges": 2498.6,
				      "gst": "449.75",
				      "finalCharges": "2948.35"
				    },
				    "provider": "Delhivery",
				    "courierType": "Domestic (Surface)",
				    "courier": "",
				    "serviceName": "Delhivery Surface 0.25KG",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Delhivery Surface 0.5KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1450.8,
				      "gst": "261.14",
				      "finalCharges": "1711.94"
				    },
				    "provider": "Delhivery",
				    "courierType": "Domestic (Surface)",
				    "courier": "",
				    "serviceName": "Delhivery Surface 0.5KG",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Delhivery Surface 1KG",
				    "cod": 0,
				    "forward": {
				      "charges": 1249.2999999999997,
				      "gst": "224.87",
				      "finalCharges": "1474.17"
				    },
				    "provider": "Delhivery",
				    "courierType": "Domestic (Surface)",
				    "courier": "",
				    "serviceName": "Delhivery Surface 1KG",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Dtdc Surface 3KG",
				    "cod": 0,
				    "forward": {
				      "charges": 720.75,
				      "gst": "129.73",
				      "finalCharges": "850.49"
				    },
				    "provider": "Dtdc",
				    "courierType": "Domestic (Surface)",
				    "courier": "B2C GROUND ECONOMY",
				    "serviceName": "Dtdc Surface 3KG ",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Dtdc Surface 5KG",
				    "cod": 0,
				    "forward": {
				      "charges": 657.2,
				      "gst": "118.30",
				      "finalCharges": "775.50"
				    },
				    "provider": "Dtdc",
				    "courierType": "Domestic (Surface)",
				    "courier": "B2C GROUND ECONOMY",
				    "serviceName": "Dtdc Surface 5KG ",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.378Z"
				  },
				  {
				    "courierServiceName": "Amazon Surface 2KG",
				    "cod": 0,
				    "forward": {
				      "charges": 779.65,
				      "gst": "140.34",
				      "finalCharges": "919.99"
				    },
				    "provider": "Amazon Shipping",
				    "courierType": "Domestic (Surface)",
				    "courier": "",
				    "serviceName": "Amazon Surface 2KG ",
				    "estimatedDeliveryDate": "2025-10-16T15:23:01.379Z"
				  }] """;

		List<Map<String, Object>> updatedRates = objectMapper.readValue(estimateOfCourier, new TypeReference<>() {
		});
		Order orders = orderRepository.findById(id).get();
		return Map.of("updatedRates", updatedRates, "success", "true", "order", orders);
	}

	public Order getOrderById(Long orderID) {
		Order orders = orderRepository.findById(orderID).get();
		return orders;
	}

	public byte[] generatePdfBytesFromHtml(String finalHtmlContent) {
		// A ByteArrayOutputStream holds the PDF data in memory temporarily.
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // HtmlConverter writes the generated PDF content into the output stream
        HtmlConverter.convertToPdf(finalHtmlContent, outputStream);
        
        // Convert the stream content to a byte array and return it
        return outputStream.toByteArray();
	}
}
