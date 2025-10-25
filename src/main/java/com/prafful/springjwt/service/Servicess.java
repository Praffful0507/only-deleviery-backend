package com.prafful.springjwt.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.aspose.cells.Cell;
import com.aspose.cells.CellValueType;
import com.aspose.cells.Cells;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.HtmlConverter;
import com.prafful.springjwt.models.BankDetails;
import com.prafful.springjwt.models.BillingInfo;
import com.prafful.springjwt.models.DocumentDetails;
import com.prafful.springjwt.models.Order;
import com.prafful.springjwt.models.PackageDetails;
import com.prafful.springjwt.models.PaymentDetails;
import com.prafful.springjwt.models.PickUpAddress;
import com.prafful.springjwt.models.ProductDetail;
import com.prafful.springjwt.models.ReceiverAddress;
import com.prafful.springjwt.models.User;
import com.prafful.springjwt.models.UserMultipleAdress;
import com.prafful.springjwt.models.VolumetricWeight;
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

	public byte[] generatePdfBytesFromHtml(String finalHtmlContent, Long orderID) {
		// A ByteArrayOutputStream holds the PDF data in memory temporarily.
		Order order = orderRepository.findById(orderID).get();
		try {
			finalHtmlContent = finalHtmlContent.replace("{{RECEIVER_NAME}}",
					order.getReceiverAddress().getContactName());
			finalHtmlContent = finalHtmlContent.replace("{{RECEIVER_ADDRESS}}",
					order.getReceiverAddress().getAddress());
			finalHtmlContent = finalHtmlContent.replace("{{RECEIVER_CITY}}", order.getReceiverAddress().getCity());
			finalHtmlContent = finalHtmlContent.replace("{{RECEIVER_STATE}}", order.getReceiverAddress().getState());
			finalHtmlContent = finalHtmlContent.replace("{{RECEIVER_PIN_CODE}}",
					order.getReceiverAddress().getPinCode());
			finalHtmlContent = finalHtmlContent.replace("{{RECEIVER_PHONE}}",
					order.getReceiverAddress().getPhoneNumber());

			finalHtmlContent = finalHtmlContent.replace("{{ORDER_DATE}}", String.valueOf(order.getCreatedAt()));
			finalHtmlContent = finalHtmlContent.replace("{{INVOICE_NO}}", String.valueOf(order.getOrderId()));

			// Replace payment and shipping info
			finalHtmlContent = finalHtmlContent.replace("{{PAYMENT_MODE}}", order.getPaymentDetails().getName());
			finalHtmlContent = finalHtmlContent.replace("{{TOTAL_AMOUNT}}",
					String.valueOf(order.getTotalFreightCharges()));
			finalHtmlContent = finalHtmlContent.replace("{{PACKAGE_WEIGHT}}",
					String.valueOf(order.getPackageDetails().getDeadWeight()));
			finalHtmlContent = finalHtmlContent = finalHtmlContent.replace("{{PACKAGE_LENGTH}}",
					String.valueOf(Optional.ofNullable(order.getPackageDetails()).map(pd -> pd.getVolumetricWeight())
							.map(vw -> vw.getLength())));

			finalHtmlContent = finalHtmlContent = finalHtmlContent.replace("{{PACKAGE_WIDTH}}",
					String.valueOf(Optional.ofNullable(order.getPackageDetails()).map(pd -> pd.getVolumetricWeight())
							.map(vw -> vw.getWidth())));

			finalHtmlContent = finalHtmlContent = finalHtmlContent.replace("{{PACKAGE_HEIGHT}}",
					String.valueOf(Optional.ofNullable(order.getPackageDetails()).map(pd -> pd.getVolumetricWeight())
							.map(vw -> vw.getHeight())));

			finalHtmlContent = finalHtmlContent.replace("{{COMPANY_NAME}}", "Delevery GLOBAL INDIA");
			finalHtmlContent = finalHtmlContent.replace("{{AWB_NUMBER}}",
					order.getAwbNumber() != null ? order.getAwbNumber() : "");

			finalHtmlContent = finalHtmlContent.replace("{{PICKUP_NAME}}", order.getPickupAddress().getContactName());
			finalHtmlContent = finalHtmlContent.replace("{{PICKUP_ADDRESS}}", order.getPickupAddress().getAddress());
			finalHtmlContent = finalHtmlContent.replace("{{PICKUP_CITY}}", order.getPickupAddress().getCity());
			finalHtmlContent = finalHtmlContent.replace("{{PICKUP_STATE}}", order.getPickupAddress().getState());
			finalHtmlContent = finalHtmlContent.replace("{{PICKUP_PIN_CODE}}", order.getPickupAddress().getPinCode());
			finalHtmlContent = finalHtmlContent.replace("{{PICKUP_PHONE}}", order.getPickupAddress().getPhoneNumber());

			// Replace return address (using pickup address as return address)
			finalHtmlContent = finalHtmlContent.replace("{{RETURN_NAME}}", order.getPickupAddress().getContactName());
			finalHtmlContent = finalHtmlContent.replace("{{RETURN_ADDRESS}}", order.getPickupAddress().getAddress());
			finalHtmlContent = finalHtmlContent.replace("{{RETURN_CITY}}", order.getPickupAddress().getCity());
			finalHtmlContent = finalHtmlContent.replace("{{RETURN_STATE}}", order.getPickupAddress().getState());
			finalHtmlContent = finalHtmlContent.replace("{{RETURN_PIN_CODE}}", order.getPickupAddress().getPinCode());
			finalHtmlContent = finalHtmlContent.replace("{{RETURN_PHONE}}", order.getPickupAddress().getPhoneNumber());

			String productRows = generateProductRows(order.getProductDetails());
			finalHtmlContent = finalHtmlContent.replace("{{PRODUCT_ROWS}}", productRows);
		} catch (Exception e) {
			System.out.println("Its ok to be bugy code " + e.getMessage());
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		// HtmlConverter writes the generated PDF content into the output stream
		HtmlConverter.convertToPdf(finalHtmlContent, outputStream);

		// Convert the stream content to a byte array and return it
		return outputStream.toByteArray();
	}

	private String generateProductRows(List<ProductDetail> products) {
		StringBuilder rows = new StringBuilder();
		for (ProductDetail product : products) {
			double totalAmount = product.getQuantity() * Double.parseDouble(product.getUnitPrice());
			rows.append("<tr>").append("<td>").append(product.getSku()).append("</td>").append("<td>")
					.append(product.getName()).append("</td>").append("<td>").append(product.getQuantity())
					.append("</td>").append("<td>").append(product.getUnitPrice()).append("</td>").append("<td>")
					.append(String.format("%.2f", totalAmount)).append("</td>").append("</tr>");
		}
		return rows.toString();
	}

	public byte[] downloadBulkExcel() throws IOException {
		// URL url = getClass().getClassLoader().getResource("/pdf/djdj.pdf");
		Resource resource = new ClassPathResource("/pdf/Bulk_Order_Sample_Formate.xlsx");
		// Convert the stream content to a byte array and return it
		return resource.getContentAsByteArray();
	}

	public void uploadOrder(MultipartFile file) throws Exception {
		List<Order> orders = new ArrayList<>();
		try (InputStream inputStream = file.getInputStream()) {
			Workbook workbook = new Workbook(inputStream);
			Worksheet worksheet = workbook.getWorksheets().get("Sample Bulk Order");
			Cells cells = worksheet.getCells();

			List<UserMultipleAdress> addresss = userMultipleAdressRepository.findAllByEmail(SecurityUtils.getEmailId());

			int maxRow = cells.getMaxDataRow();
			for (int row = 1; row <= maxRow; row++) {
				Order order = new Order();

				// Set basic order info
				order.setUserId(SecurityUtils.getEmailId());
				order.setCreatedAt(new Date());
				order.setUpdatedAt(new Date());
				order.setStatus("PENDING");
				PickUpAddress pickupAddress = new PickUpAddress(addresss.get(0));
				order.setPickupAddress(pickupAddress);
				// Receiver Address (columns 0-7)
				ReceiverAddress receiverAddress = new ReceiverAddress();
				receiverAddress.setContactName(getCellValue(cells, row, 0));
				receiverAddress.setEmail(getCellValue(cells, row, 1));
				receiverAddress.setPhoneNumber(getCellValue(cells, row, 2));
				receiverAddress.setAddress(getCellValue(cells, row, 3));
				receiverAddress.setPinCode(getCellValue(cells, row, 4));
				receiverAddress.setCity(getCellValue(cells, row, 5));
				receiverAddress.setState(getCellValue(cells, row, 6));
				order.setReceiverAddress(receiverAddress);

				PackageDetails packageDetails = new PackageDetails();
				packageDetails.setDeadWeight(parseDouble(getCellValue(cells, row, 7)));
				VolumetricWeight weight = new VolumetricWeight();
				weight.setLength(parseInt(getCellValue(cells, row, 8)));
				weight.setWidth(parseInt(getCellValue(cells, row, 9)));
				weight.setHeight(parseInt(getCellValue(cells, row, 10)));
				order.setPackageDetails(packageDetails);

				// Product Details
				List<ProductDetail> productDetails = new ArrayList<>();

				// Product 1 (columns 11-14) - Required
				ProductDetail product1 = new ProductDetail();
				product1.setName(getCellValue(cells, row, 11));
				product1.setSku(getCellValue(cells, row, 12));
				product1.setQuantity(parseInt(getCellValue(cells, row, 13)));
				product1.setUnitPrice(getCellValue(cells, row, 14));
				productDetails.add(product1);

				String product2Name = getCellValue(cells, row, 15);
				if (product2Name != null && !product2Name.trim().isEmpty()) {
					ProductDetail product2 = new ProductDetail();
					product2.setName(product2Name);
					product2.setSku(getCellValue(cells, row, 16));
					product2.setQuantity(parseInt(getCellValue(cells, row, 17)));
					product2.setUnitPrice(getCellValue(cells, row, 18));
					productDetails.add(product2);
				}

				String product3Name = getCellValue(cells, row, 19);
				if (product3Name != null && !product3Name.trim().isEmpty()) {
					ProductDetail product3 = new ProductDetail();
					product3.setName(product3Name);
					product3.setSku(getCellValue(cells, row, 20));
					product3.setQuantity(parseInt(getCellValue(cells, row, 21)));
					product3.setUnitPrice(getCellValue(cells, row, 22));
					productDetails.add(product3);
				}

				order.setProductDetails(productDetails);

				// Payment Details (Method - column 23)
				PaymentDetails paymentDetails = new PaymentDetails();
				String method = getCellValue(cells, row, 23);
				paymentDetails.setName(method); // COD or Prepaid
				order.setPaymentDetails(paymentDetails);

				orders.add(order);
			}
			orderRepository.saveAll(orders);
		}
	}

	private String getCellValue(Cells cells, int row, int column) {
		try {
			Cell cell = cells.get(row, column);
			if (cell == null || cell.getType() == CellValueType.IS_NULL) {
				return null;
			}
			return cell.getStringValue().trim();
		} catch (Exception e) {
			return null;
		}
	}

	private int parseInt(String value) {
		try {
			if (value == null || value.trim().isEmpty()) {
				return 0;
			}
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private double parseDouble(String value) {
		try {
			if (value == null || value.trim().isEmpty()) {
				return 0.0;
			}
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}
}
