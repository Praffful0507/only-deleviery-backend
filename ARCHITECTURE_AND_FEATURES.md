# Backend Architecture & Implemented Features Documentation

## 🏗️ Architecture Overview

Your backend follows a **Spring Boot MVC (Model-View-Controller)** architecture with the following layers:

```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (React)                      │
└──────────────────────┬──────────────────────────────────┘
                       │ HTTP/REST API
                       │ JWT Authentication
┌──────────────────────▼──────────────────────────────────┐
│              Controller Layer (REST APIs)                │
│  - AuthController, UserController, Controller           │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│              Service Layer (Business Logic)              │
│  - Servicess.java (Main service class)                  │
│  - CustomUserDetailsService (Security)                   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│           Repository Layer (Data Access)                │
│  - JPA Repositories (UserRepository, OrderRepository)   │
└──────────────────────┬──────────────────────────────────┘
                       │
┌──────────────────────▼──────────────────────────────────┐
│              Database (MySQL)                            │
│  - Tables: USER, ORDERS, BILLING_INFO, etc.            │
└─────────────────────────────────────────────────────────┘
```

## 🛠️ Technology Stack

### Core Framework
- **Spring Boot 3.1.0** - Main application framework
- **Java 21** - Programming language
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Database abstraction layer
- **Hibernate** - ORM (Object-Relational Mapping)

### Database
- **MySQL** - Relational database
- **JPA/Hibernate** - Database operations with `ddl-auto=update`

### Security
- **JWT (JSON Web Tokens)** - Token-based authentication
- **BCrypt** - Password encryption
- **Spring Security Filter Chain** - Request filtering

### Additional Libraries
- **Lombok** - Reduces boilerplate code
- **Jackson** - JSON processing
- **Aspose Cells** - Excel file processing
- **iText HTML2PDF** - PDF generation from HTML
- **OkHttp** - HTTP client for external API calls
- **Jsoup** - HTML parsing
- **Flexmark** - Markdown processing

### Custom Components
- **SPEL (Spring Expression Language)** - Custom expression parser (`PraffulExpressionExpressior`)
- Custom property accessors for dynamic data evaluation

## 📁 Project Structure

```
src/main/java/com/prafful/springjwt/
├── controllers/              # REST API Controllers
│   ├── AuthController.java   # Authentication endpoints
│   ├── UserController.java   # User management
│   ├── Controller.java       # Main controller (mixed endpoints)
│   └── TestController.java   # Test endpoints
│
├── models/                   # JPA Entity Models
│   ├── User.java
│   ├── Order.java
│   ├── BillingInfo.java
│   ├── BankDetails.java
│   ├── DocumentDetails.java
│   ├── PickUpAddress.java
│   ├── ReceiverAddress.java
│   ├── PackageDetails.java
│   ├── PaymentDetails.java
│   ├── ProductDetail.java
│   ├── Tracking.java
│   ├── UserMultipleAdress.java
│   └── VolumetricWeight.java
│
├── repository/               # JPA Repositories
│   ├── UserRepository.java
│   ├── OrderRepository.java
│   ├── BillinfoRepository.java
│   ├── BankDetailsRepository.java
│   ├── DocumentDetailsRepository.java
│   ├── AadharDetailsRepository.java
│   └── UserMultipleAdressRepository.java
│
├── service/                  # Business Logic Layer
│   ├── Servicess.java       # Main service (contains most business logic)
│   ├── SecurityUtils.java   # Thread-local security context
│   └── Appconfig.java
│
├── security/                 # Security Configuration
│   ├── WebSecurityConfig.java        # Security filter chain
│   ├── jwt/
│   │   ├── JwtUtils.java            # JWT token generation/validation
│   │   ├── AuthTokenFilter.java     # JWT filter for requests
│   │   └── AuthEntryPointJwt.java   # Authentication entry point
│   └── services/
│       └── CustomUserDetailsService.java
│
├── payload/                  # DTOs (Data Transfer Objects)
│   ├── request/
│   │   ├── LoginRequest.java
│   │   └── SignupRequest.java
│   └── response/
│
├── spel/                     # Custom SPEL Components
│   ├── PraffulAccessor.java
│   ├── PraffulHelper.java
│   └── PraffulData.java
│
├── email/                     # Email Services
│   ├── EmailController.java
│   └── EmailService.java
│
└── pdf2Markdown/             # PDF Processing
    ├── pdf2MarkDownController.java
    └── pdf2MarkDownService.java
```

## ✅ Implemented Features

### 1. Authentication & Authorization

#### ✅ User Authentication
- **POST `/api/external/login`** - User login with email/password
  - Returns JWT token
  - Uses Spring Security AuthenticationManager
  - Token includes user email, fullname, isEmployee, kycDone

- **POST `/api/external/register`** - User registration
  - Validates email uniqueness
  - Encrypts password with BCrypt
  - Returns success response with user data

- **GET `/api/external/verify`** - Token verification
  - Simple endpoint returning "true"

#### ✅ JWT Implementation
- **Token Generation**: Custom claims with user data
- **Token Validation**: Validates signature, expiration, format
- **Token Parsing**: Extracts email from token claims
- **Filter Chain**: `AuthTokenFilter` intercepts requests and validates JWT
- **Security Context**: Sets authenticated user in Spring Security context

#### ⚠️ Security Features
- CORS configured for multiple origins (localhost, Netlify deployments)
- CSRF disabled (for API usage)
- Public endpoints: `/api/external/**`, `/api/auth/**`, `/api/test/**`
- All other endpoints require JWT authentication

### 2. User Management

#### ✅ User Operations
- **GET `/api/user/getUserDetails`** - Get current user details
  - Extracts email from JWT token
  - Returns user object with wallet, KYC status, etc.

- **GET `/api/order/getUser`** - Get user in order context
  - Returns user with isAdmin, adminTab, kycDone flags

- **GET `/api/users/getUsers`** - Get all verified users
  - Returns list of verified sellers

#### ✅ User Model Features
- Email-based authentication
- KYC completion tracking (`kycDone`)
- Verification status (`isVerified`)
- Company information
- Monthly orders tracking
- Password encryption

### 3. KYC (Know Your Customer) Verification

#### ✅ Billing Information
- **GET `/api/getKyc/getBillingInfo`** - Retrieve saved billing info
- **POST `/api/merchant/verfication/billing-info`** - Save billing information
  - Stores address, city, state, postal code
  - Linked to user email

#### ✅ Document Verification
- **GET `/api/getKyc/getAadhaar`** - Get Aadhaar details
- **GET `/api/getKyc/getPan`** - Get PAN details
- **GET `/api/getKyc/getBankAccount`** - Get bank account details
- **POST `/api/merchant/verfication/generate-otp`** - Generate Aadhaar OTP
  - Currently returns mock data (ref_id: "12345")
- **POST `/api/merchant/verfication/verify-otp`** - Verify Aadhaar OTP
  - Currently returns mock data (name, sonOf, address, state)
- **POST `/api/merchant/verfication/pan`** - Verify PAN card
  - Currently returns mock data
- **POST `/api/merchant/verfication/bank-account`** - Verify bank account
  - Currently returns mock data (hardcoded bank details)

#### ✅ KYC Completion
- **POST `/api/merchant/verfication/kyc`** - Complete KYC process
  - Saves bank details and document details
  - Updates user: sets `kycDone=true`, `verified=true`, `checked=true`
  - Stores selected business type

**Note**: Most KYC verification endpoints return mock/hardcoded data and need integration with actual verification services.

### 4. Order Management

#### ✅ Order CRUD Operations
- **POST `/api/order/neworder`** - Create new order
  - Saves order with user ID from security context
  - Includes pickup address, receiver address, package details, products, payment

- **GET `/api/order/orders`** - Get all orders for current user
  - Returns orders with pagination support (currently basic)
  - Includes pickup locations
  - Returns courier services (currently empty list)

- **GET `/api/order/getOrderById/{orderID}`** - Get order by ID
  - Returns complete order with all embedded details

- **GET `/api/order/ship/{id}`** - Get order with courier rates
  - Returns order details
  - Returns mock courier rate estimates (hardcoded JSON)
  - Includes multiple courier options (DTDC, Amazon, Delhivery)
  - Each option has charges, GST, final charges, estimated delivery date

#### ✅ Order Model Structure
- **Order ID**: Auto-generated
- **User ID**: Email-based user identification
- **Status**: Order status tracking
- **Embedded Objects**:
  - `PickUpAddress` - Sender details
  - `ReceiverAddress` - Recipient details
  - `PackageDetails` - Weight and dimensions
  - `PaymentDetails` - Payment method and amount
  - `ProductDetail[]` - List of products
  - `Tracking[]` - Order tracking history
- **Courier Information**: AWB number, courier service name, provider, estimated delivery
- **Timestamps**: Created at, updated at, shipment created at

#### ⚠️ Missing Order Features
- Order update endpoint (PUT)
- Order cancellation
- Bulk package details update
- Order status filtering
- Advanced pagination
- Order search functionality

### 5. Pickup Address Management

#### ✅ Complete CRUD Operations
- **GET `/api/order/pickupAddress`** - Get all pickup addresses
  - Returns addresses with primary flag
  - Filters by current user email

- **POST `/api/order/pickupAddress`** - Add/update pickup address
  - Saves address linked to user email

- **PATCH `/api/order/pickupAddress/setPrimary/{id}`** - Set primary address
  - Toggles primary flag

- **DELETE `/api/order/pickupAddress/{id}`** - Delete address

#### ✅ Features
- Multiple addresses per user
- Primary address designation
- Full address details (name, phone, email, address, city, state, pincode)

### 6. Bulk Order Operations

#### ✅ Excel Upload
- **POST `/api/bulkOrderUpload/upload`** - Upload bulk orders via Excel
  - Uses Aspose Cells library to read Excel files
  - Parses "Sample Bulk Order" worksheet
  - Supports up to 3 products per order
  - Maps Excel columns to order fields:
    - Columns 0-6: Receiver address
    - Column 7: Package weight
    - Columns 8-10: Package dimensions
    - Columns 11-22: Product details (3 products)
    - Column 23: Payment method
  - Uses first pickup address as default
  - Saves all orders in batch

- **GET `/api/bulkOrderUpload/download-excel`** - Download Excel template
  - Returns sample Excel file from resources (`/pdf/Bulk_Order_Sample_Formate.xlsx`)

### 7. Invoice Generation

#### ✅ PDF Invoice Generation
- **GET `/api/printinvoice/download-invoice/{orderID}`** - Generate invoice PDF
  - Uses iText HTML2PDF library
  - HTML template with placeholders
  - Replaces placeholders with order data:
    - Receiver details
    - Order date, invoice number
    - Payment mode, amount, weight, dimensions
    - Company name, AWB number
    - Pickup address
    - Return address
    - Product table with SKU, name, quantity, price
  - Returns PDF as byte array with proper headers
  - Sets Content-Disposition for download

### 8. Pincode Lookup

#### ✅ Basic Implementation
- **GET `/api/order/pincode/{pinCode}`** - Get city/state from pincode
  - Currently returns hardcoded data: `{"city": "Gwalior", "state": "MP"}`
  - Needs integration with actual pincode API

### 9. Staff Role Verification

#### ✅ Basic Endpoint
- **GET `/api/staffRole/verify`** - Verify staff role
  - Currently empty implementation
  - Intended for employee authentication

### 10. Dashboard

#### ⚠️ Placeholder Endpoints
- **GET `/api/dashboard/dashboard`** - Dashboard data
  - Currently empty implementation

- **GET `/api/dashboard/getBusinessInsights`** - Business insights
  - Currently empty implementation

### 11. External API Integration

#### ✅ Token Retrieval
- **GET `/api/getToken`** - Get external API token
  - Makes HTTP call to external Shipex API
  - Uses OkHttp client
  - Hardcoded credentials (needs configuration)
  - Returns token from external service

### 12. Custom SPEL (Spring Expression Language)

#### ✅ Expression Parser
- **PraffulExpressionExpressior** - Custom SPEL parser
  - Caches parsed expressions
  - Supports custom functions (e.g., `sum()`)
  - Custom property accessor for dynamic data evaluation
  - Used in main application for expression evaluation

## 🔒 Security Implementation

### JWT Token Structure
```json
{
  "user": {
    "email": "user@example.com",
    "fullname": "User Name",
    "isEmployee": false,
    "kycDone": false
  },
  "sub": "user@example.com",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Security Flow
1. User logs in → `AuthController.login()`
2. Spring Security authenticates credentials
3. `JwtUtils.generateJwtToken()` creates JWT
4. Token returned to frontend
5. Frontend includes token in `Authorization: Bearer <token>` header
6. `AuthTokenFilter` intercepts requests
7. Token validated and user extracted
8. User set in SecurityContext
9. Email stored in ThreadLocal via `SecurityUtils`

### Thread-Local Security Context
- `SecurityUtils` uses `ThreadLocal<String>` to store current user email
- Allows service layer to access current user without passing parameters
- Cleared after request processing

## 📊 Database Models

### User Table
- Primary key: `id` (Long, auto-generated)
- Email (unique identifier)
- Password (BCrypt encrypted)
- Company, phone number, monthly orders
- KYC flags: `kycDone`, `isVerified`, `checked`
- Business type selection

### Order Table
- Primary key: `ORDER_ID` (Long, auto-generated)
- Foreign key: `USER_ID` (email-based)
- Status, channel, composite order ID
- Courier information (AWB, service name, provider)
- Timestamps
- Embedded: PaymentDetails, PackageDetails
- Collections: ProductDetail[], Tracking[]

### Related Tables
- `BILLING_INFO` - User billing addresses
- `BANK_DETAILS` - Bank account information
- `DOCUMENT_DETAILS` - Aadhaar/PAN details
- `USER_MULTIPLE_ADDRESS` - Multiple pickup addresses
- `ORDER_TRACKING` - Order tracking history
- `ORDER_PRODUCT_DETAILS` - Order products

## ⚠️ Current Limitations & Gaps

### 1. Mock/Hardcoded Data
- KYC verification returns mock data
- Pincode lookup returns hardcoded values
- Courier rates are hardcoded JSON
- External API token uses hardcoded credentials

### 2. Missing Endpoints
- Order update/cancellation
- Employee authentication
- Admin operations (search, filtering)
- Courier management
- Payment integration (Razorpay)
- Support tickets
- Rate card management
- NDR processing
- Label/manifest generation
- And 50+ more endpoints from frontend requirements

### 3. Service Layer Organization
- Single `Servicess.java` file contains all business logic
- Should be split into separate service classes:
  - `OrderService`
  - `KycService`
  - `UserService`
  - `CourierService`
  - etc.

### 4. Error Handling
- Limited exception handling
- No standardized error response format
- Missing validation for request bodies

### 5. API Documentation
- No Swagger/OpenAPI documentation
- Endpoint comments are minimal

### 6. Testing
- No visible test files
- No unit tests or integration tests

### 7. Configuration
- Hardcoded values in code
- External API credentials in service layer
- Should use `@ConfigurationProperties` or environment variables

## 🎯 Strengths

1. **Clean MVC Structure** - Well-organized layers
2. **JWT Security** - Proper token-based authentication
3. **Database Design** - Good use of embedded objects and collections
4. **PDF Generation** - Working invoice generation
5. **Excel Processing** - Functional bulk upload
6. **Thread-Local Context** - Clean way to access current user
7. **CORS Configuration** - Properly configured for frontend

## 📝 Recommendations

1. **Split Service Layer** - Create separate service classes
2. **Add Validation** - Use `@Valid` and validation annotations
3. **Error Handling** - Implement `@ControllerAdvice` for global exception handling
4. **API Documentation** - Add Swagger/OpenAPI
5. **Configuration** - Move hardcoded values to properties
6. **Testing** - Add unit and integration tests
7. **Complete Missing Endpoints** - Implement remaining 50+ endpoints
8. **Integration** - Connect to real verification services and APIs
9. **Logging** - Add comprehensive logging
10. **Code Organization** - Split large `Controller.java` into focused controllers

## 🔄 API Prefix Handling

**Current Issue**: Backend uses `/api` prefix, frontend doesn't.

**Solutions**:
1. Configure Spring Boot context path to remove `/api`
2. Update frontend to include `/api` prefix
3. Use reverse proxy/API gateway to handle routing

The security filter currently checks paths without `/api` prefix, suggesting option 1 or 3 is intended.
