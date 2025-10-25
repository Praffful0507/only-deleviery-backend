package com.prafful.springjwt.controllers;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.prafful.springjwt.models.BankDetails;
import com.prafful.springjwt.models.BillingInfo;
import com.prafful.springjwt.models.DocumentDetails;
import com.prafful.springjwt.models.Order;
import com.prafful.springjwt.models.User;
import com.prafful.springjwt.models.UserMultipleAdress;
import com.prafful.springjwt.service.Servicess;

@RestController
@RequestMapping("/api")
public class Controller {

	@Autowired
	Servicess service;

	@GetMapping("/staffRole/verify")
	public void staffRole() {

	}

	@GetMapping("/order/getUser")
	public User getDetails(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		return service.getUserDetils(authHeader);
	}

	@GetMapping("dashboard/getBusinessInsights")
	public void getBuisnessInsights() {

	}

	@GetMapping("/dashboard/dashboard")
	public void getDashboard() {

	}

	@GetMapping("getKyc/getBillingInfo")
	public BillingInfo getKyc(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		return service.getBillingInfo(authHeader);
	}

	@GetMapping("/getKyc/getAadhaar")
	public DocumentDetails getKycOfAadhaar(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		return service.getAadhaarDetails(authHeader);
	}

	@GetMapping("/getKyc/getPan")
	public DocumentDetails getKycOfPan(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		return service.getAadhaarDetails(authHeader);
	}

	@GetMapping("/getKyc/getBankAccount")
	public BankDetails getKycOfBankAccount(
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		return service.getKycOfBankAccount(authHeader);
	}

	@GetMapping("/getKyc/getGST")
	public void getKycOfGST() {

	}

	@PostMapping("/merchant/verfication/billing-info")
	public void billinginfo(@RequestBody Map<String, Object> billingInfo,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		service.SaveBillingInfo(billingInfo, authHeader);
	}

	@PostMapping("/merchant/verfication/bank-account")
	public Map<String, Object> verifyBankAccount(@RequestBody BankDetails bankDetails) {
		return service.verifyBankAccount(bankDetails);
	}

	@PostMapping("/merchant/verfication/generate-otp")
	public Map<String, Object> generateAddharotp(Map<String, Object> addharOtp) {
		return service.generateAddharotp(addharOtp);
	}

	@PostMapping("/merchant/verfication/verify-otp")
	public Map<String, Object> verifyAddharOtp(Map<String, Object> verifyAddharOtp) {
		return service.verifyAddharOtp(verifyAddharOtp);
	}

	@PostMapping("/merchant/verfication/pan")
	public Map<String, Object> verifyPanCard(Map<String, Object> verifyPanCard) {
		return service.verifyPanCard(verifyPanCard);
	}

	@PostMapping("/merchant/verfication/kyc")
	public Map<String, Object> vericationisDone(@RequestBody Map<String, Object> request,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		return service.vericationisDone(request, authHeader);
	}

	@GetMapping("/order/pincode/{pinCode}")
	public Map<String, Object> getPinCode(@PathVariable String pinCode) {
		return service.getPinCode(pinCode);
	}

	@PostMapping("/order/pickupAddress")
	public void addUpdateAdress(@RequestBody UserMultipleAdress multipleAdress,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		service.addUpdateAdress(multipleAdress, authHeader);
	}

	@GetMapping("/order/pickupAddress")
	public Map<String, Object> getPickUpAddress() {
		return service.getPickUpAddress();
	}

	@GetMapping("/dispreancy/allDispreancyById")
	public void getAllDiscrepancyById(@RequestParam(required = false) Long id,
			@RequestParam(required = false) String fromDate, @RequestParam(required = false) String toDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit,
			@RequestParam(required = false) String orderId, @RequestParam(required = false) String awbNumber,
			@RequestParam(required = false) String provider, @RequestParam(required = false) String status) {

	}

	@PatchMapping("/order/pickupAddress/setPrimary/{id}")
	public void updatepickUpId(@PathVariable Long id) {
		service.updatePrimary(id);
	}

	@PutMapping("/order/updatePickupAddress/{id}")
	public void updatepickUpAdress(@PathVariable Long id, @RequestBody UserMultipleAdress userMultipleAddress) {
		service.updatePickUpAddress(id, userMultipleAddress);
	}

	@DeleteMapping("/order/pickupAddress/{id}")
	public void deletePickUpAdress(@PathVariable Long id) {
		service.deletePickUpAdress(id);

	}

	@GetMapping("/users/getUsers")
	public Map<String, Object> isUserVerified() {
		return service.isUserVerified();
	}

	@PostMapping("/order/neworder")
	public void newOrder(@RequestBody Order order) {
		service.saveOrder(order);
	}

	@GetMapping("order/orders")
	public Map<String, Object> getOrders() {
		return service.getOrders();
	}

	@GetMapping("/order/ship/{id}")
	public Map<String, Object> getShipOrders(@PathVariable Long id)
			throws JsonMappingException, JsonProcessingException {
		return service.getShipOrders(id);
	}

	@PostMapping("/{safeProvider}/createShipment")
	public Map<String, Object> createShipment(@RequestBody Map<String, Object> order,
			@PathVariable String safeProvider) {
		return Map.of("success", true);
	}

	@GetMapping("/order/getOrderById/{orderID}")
	public Order getOrderId(@PathVariable Long orderID) {
		return service.getOrderById(orderID);
	}

	@CrossOrigin(origins = "*")
	@GetMapping("/printinvoice/download-invoice/{orderID}")
	public ResponseEntity<byte[]> generatePdf(@PathVariable Long orderID) {
		// 1. Prepare/Fetch your dynamic HTML content (ensure all variables and images
		// are resolved)
		String finalHtmlContent = """
								<!DOCTYPE html>
								<html lang="en">
								<head>
								  <meta charset="UTF-8" />
								  <title>Invoice</title>
								  <style>
								  body {
				  font-family: sans-serif;
				  font-size: 12px;
				  background: #f8f8f8;
				  margin: 0;
				  padding: 20px;
				}

				.invoice-container {
				  display: flex;
				  justify-content: center;
				}

				.invoice-box {
				  background-color: #fff;
				  padding: 20px;
				  border-radius: 8px;
				  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
				  width: 100%;
				  max-width: 600px;
				  border: 1px solid #000;
				}

				.section {
				  border-top: 1px solid #000;
				  padding-top: 10px;
				  margin-top: 10px;
				}

				.section:first-child {
				  border-top: none;
				  padding-top: 0;
				  margin-top: 0;
				}

				.section-row {
				  display: flex;
				  justify-content: space-between;
				}

				.section-col {
				  flex: 1;
				}

				.text-center {
				  text-align: center;
				}

				.company-name {
				  font-weight: bold;
				}

				.invoice-table {
				  width: 100%;
				  border-collapse: collapse;
				  margin-top: 10px;
				}

				.invoice-table th,
				.invoice-table td {
				  border: 1px solid #000;
				  padding: 4px;
				  text-align: left;
				  font-size: 12px;
				}

				.invoice-table thead {
				  background-color: #e2e8f0; /* light gray like Tailwind's gray-200 */
				}

				.border-top {
				  border-top: 1px solid #000;
				  margin-top: 10px;
				  padding-top: 10px;
				}

				  </style>
								</head>
								<body>
								  <div class="invoice-container">
								    <div class="invoice-box">
								      <!-- To Address -->
								      <div class="section">
								        <div class="section-row">
								          <div class="section-col">
								            <p><strong>To:</strong></p>
								            <p>narinder kaur</p>
								            <p>block c 14/2 new govind pura street no-8 near</p>
								            <p>gandhi park</p>
								            <p>East Delhi, DELHI, 110051</p>
								            <p>MOBILE NO:</p>
								          </div>
								        </div>
								      </div>

								      <!-- Order Info -->
								      <div class="section">
								        <div class="section-row">
								          <div class="section-col">
								            <p><strong>Order Date:</strong> Mar 7, 2025</p>
								            <p><strong>Invoice No:</strong> 843987</p>
								          </div>
								        </div>
								      </div>

								      <!-- Payment and Shipping Info -->
								      <div class="section">
								        <div class="section-row space-between">
								          <div class="section-col">
								            <p><strong>MODE:</strong> PREPAID</p>
								            <p><strong>AMOUNT:</strong> 800</p>
								            <p>WEIGHT: 0.4</p>
								            <p>Dimensions (cm): 10*10*10</p>
								          </div>
								          <div class="text-center">
								            <p class="company-name">SHIPEX INDIA</p>
								            <p>35973710008735</p>
								          </div>
								        </div>
								      </div>

								      <!-- Table -->
								      <table class="invoice-table">
								        <thead>
								          <tr>
								            <th>SKU</th>
								            <th>Item Name</th>
								            <th>Qty.</th>
								            <th>Unit Price</th>
								            <th>Total Amount</th>
								          </tr>
								        </thead>
								        <tbody>
								          <tr>
								            <td>1</td>
								            <td>honey</td>
								            <td>1</td>
								            <td>800</td>
								            <td>800</td>
								          </tr>
								        </tbody>
								      </table>

								      <!-- Pickup Address -->
								      <div class="section">
								        <p><strong>Pickup Address:</strong></p>
								        <p>Ajeet Kumar</p>
								        <p>Vaidic Panchgavyya, Near LIC Building, Laxmi Sweets, Sagwan Chowk</p>
								        <p>Sirsa, HARYANA, 125055</p>
								        <p>Mobile No: 9518156020</p>
								      </div>

								      <!-- Return Address -->
								      <div class="section">
								        <p><strong>Return Address:</strong></p>
								        <p>Ajeet Kumar</p>
								        <p>Vaidic Panchgavyya, Near LIC Building, Laxmi Sweets, Sagwan Chowk</p>
								        <p>Sirsa, HARYANA, 125055</p>
								        <p>Mobile No: 9518156020</p>
								      </div>

								      <!-- Footer -->
								      <div class="section border-top">
								        <p>This is a computer-generated document, hence does not require a signature.</p>
								        <p>
								          <strong>Note:</strong> All disputes are subject to Delhi jurisdiction. Goods once
								          sold will only be taken back or exchanged as per the store’s exchange/return policy.
								        </p>
								      </div>
								    </div>
								  </div>
								</body>
								</html>
								 """;

		// 2. Call the utility method to generate the PDF bytes

		String d = """
				<!DOCTYPE html>
				<html lang="en">
				<head>
				    <meta charset="UTF-8">
				    <meta name="viewport" content="width=device-width, initial-scale=1.0">
				    <title>Invoice</title>
				    <style>
				        body {
				            font-family: Arial, sans-serif;
				            margin: 20px;
				            font-size: 12px;
				        }
				        .invoice-container {
				            max-width: 800px;
				            margin: 0 auto;
				        }
				        .invoice-box {
				            border: 2px solid #000;
				            padding: 20px;
				        }
				        .section {
				            margin-bottom: 15px;
				        }
				        .section-row {
				            display: flex;
				        }
				        .section-col {
				            flex: 1;
				        }
				        .space-between {
				            justify-content: space-between;
				        }
				        .text-center {
				            text-align: center;
				        }
				        .company-name {
				            font-size: 18px;
				            font-weight: bold;
				            margin: 5px 0;
				        }
				        .invoice-table {
				            width: 100%;
				            border-collapse: collapse;
				            margin: 15px 0;
				        }
				        .invoice-table th,
				        .invoice-table td {
				            border: 1px solid #000;
				            padding: 8px;
				            text-align: left;
				        }
				        .invoice-table th {
				            background-color: #f0f0f0;
				            font-weight: bold;
				        }
				        .border-top {
				            border-top: 2px solid #000;
				            padding-top: 15px;
				            margin-top: 15px;
				        }
				        p {
				            margin: 3px 0;
				        }
				    </style>
				</head>
				<body>
				<div class="invoice-container">
				<div class="invoice-box">
				    <!-- To Address -->
				    <div class="section">
				        <div class="section-row">
				            <div class="section-col">
				                <p><strong>To:</strong></p>
				                <p>{{RECEIVER_NAME}}</p>
				                <p>{{RECEIVER_ADDRESS}}</p>
				                <p>{{RECEIVER_CITY}}, {{RECEIVER_STATE}}, {{RECEIVER_PIN_CODE}}</p>
				                <p>MOBILE NO: {{RECEIVER_PHONE}}</p>
				            </div>
				        </div>
				    </div>

				    <!-- Order Info -->
				    <div class="section">
				        <div class="section-row">
				            <div class="section-col">
				                <p><strong>Order Date:</strong> {{ORDER_DATE}}</p>
				                <p><strong>Invoice No:</strong> {{INVOICE_NO}}</p>
				            </div>
				        </div>
				    </div>

				    <!-- Payment and Shipping Info -->
				    <div class="section">
				        <div class="section-row space-between">
				            <div class="section-col">
				                <p><strong>MODE:</strong> {{PAYMENT_MODE}}</p>
				                <p><strong>AMOUNT:</strong> {{TOTAL_AMOUNT}}</p>
				                <p>WEIGHT: {{PACKAGE_WEIGHT}}</p>
				                <p>Dimensions (cm): {{PACKAGE_LENGTH}}*{{PACKAGE_WIDTH}}*{{PACKAGE_HEIGHT}}</p>
				            </div>
				            <div class="text-center">
				                <p class="company-name">{{COMPANY_NAME}}</p>
				                <p>{{AWB_NUMBER}}</p>
				            </div>
				        </div>
				    </div>

				    <!-- Table -->
				    <table class="invoice-table">
				        <thead>
				            <tr>
				                <th>SKU</th>
				                <th>Item Name</th>
				                <th>Qty.</th>
				                <th>Unit Price</th>
				                <th>Total Amount</th>
				            </tr>
				        </thead>
				        <tbody>
				            {{PRODUCT_ROWS}}
				        </tbody>
				    </table>

				    <!-- Pickup Address -->
				    <div class="section">
				        <p><strong>Pickup Address:</strong></p>
				        <p>{{PICKUP_NAME}}</p>
				        <p>{{PICKUP_ADDRESS}}</p>
				        <p>{{PICKUP_CITY}}, {{PICKUP_STATE}}, {{PICKUP_PIN_CODE}}</p>
				        <p>Mobile No: {{PICKUP_PHONE}}</p>
				    </div>

				    <!-- Return Address -->
				    <div class="section">
				        <p><strong>Return Address:</strong></p>
				        <p>{{RETURN_NAME}}</p>
				        <p>{{RETURN_ADDRESS}}</p>
				        <p>{{RETURN_CITY}}, {{RETURN_STATE}}, {{RETURN_PIN_CODE}}</p>
				        <p>Mobile No: {{RETURN_PHONE}}</p>
				    </div>

				    <!-- Footer -->
				    <div class="section border-top">
				        <p>This is a computer-generated document, hence does not require a signature.</p>
				        <p>
				            <strong>Note:</strong> All disputes are subject to Delhi jurisdiction. Goods once
				            sold will only be taken back or exchanged as per the store's exchange/return policy.
				        </p>
				    </div>
				</div>
				</div>
				</body>
				</html>
				""";

		byte[] pdfBytes = service.generatePdfBytesFromHtml(d, orderID);

		// --- Setting HTTP Headers for Download ---

		// a. Set the file type to 'application/pdf'
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);

		// b. Set the Content-Disposition header.
		// 'attachment' tells the browser to download the file.
		// 'filename' provides the suggested name for the downloaded file.
		String filename = "shipping_label_843987.pdf";
		headers.setContentDispositionFormData("attachment", filename);

		// c. Set the content size (optional but recommended)
		headers.setContentLength(pdfBytes.length);

		// 3. Return the response entity containing the PDF bytes and headers
		return ResponseEntity.ok().headers(headers).body(pdfBytes);
	}

	@GetMapping("/bulkOrderUpload/download-excel")
	@CrossOrigin(origins = "*")
	public ResponseEntity<byte[]> downloadExcel() throws IOException {
		byte[] pdfBytes = service.downloadBulkExcel();

		// --- Setting HTTP Headers for Download ---

		// a. Set the file type to 'application/pdf'
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);

		// b. Set the Content-Disposition header.
		// 'attachment' tells the browser to download the file.
		// 'filename' provides the suggested name for the downloaded file.
		String filename = "shipping_label_843987.pdf";
		headers.setContentDispositionFormData("attachment", filename);

		// c. Set the content size (optional but recommended)
		headers.setContentLength(pdfBytes.length);

		// 3. Return the response entity containing the PDF bytes and headers
		return ResponseEntity.ok().headers(headers).body(pdfBytes);

	}

	@PostMapping("/bulkOrderUpload/upload")
	public void uploadOrder(@RequestParam("file") MultipartFile file) throws Exception {
		service.uploadOrder(file);
	}
}
