-- ============================================================================
-- NORMALIZED MYSQL DATABASE SCHEMA
-- Based on API Contract and Frontend Requirements
-- Delivery Management System
-- ============================================================================

-- Drop existing tables (for fresh setup - use with caution in production)
-- SET FOREIGN_KEY_CHECKS = 0;
-- DROP TABLE IF EXISTS ... (tables listed in reverse dependency order)
-- SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================================
-- 1. CORE USER & AUTHENTICATION TABLES
-- ============================================================================

-- Users Table (Main user entity)
CREATE TABLE `users` (
    `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt hashed',
    `fullname` VARCHAR(255) NOT NULL,
    `phone_number` VARCHAR(20),
    `company` VARCHAR(255),
    `monthly_orders` INT UNSIGNED DEFAULT 0,
    `user_id_string` VARCHAR(50) UNIQUE COMMENT 'Display ID like USER001',
    `is_employee` BOOLEAN DEFAULT FALSE,
    `is_admin` BOOLEAN DEFAULT FALSE,
    `admin_tab` BOOLEAN DEFAULT FALSE COMMENT 'Has admin access',
    `kyc_done` BOOLEAN DEFAULT FALSE,
    `is_verified` BOOLEAN DEFAULT FALSE,
    `checked` BOOLEAN DEFAULT FALSE,
    `selected_type` VARCHAR(50) COMMENT 'individual/company',
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_users_email` (`email`),
    UNIQUE KEY `uk_users_user_id_string` (`user_id_string`),
    INDEX `idx_users_email` (`email`),
    INDEX `idx_users_is_employee` (`is_employee`),
    INDEX `idx_users_is_admin` (`is_admin`),
    INDEX `idx_users_kyc_done` (`kyc_done`),
    INDEX `idx_users_is_verified` (`is_verified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Roles Table (for RBAC)
CREATE TABLE `roles` (
    `role_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `role_name` VARCHAR(50) NOT NULL,
    `description` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`role_id`),
    UNIQUE KEY `uk_roles_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Roles (Many-to-Many: Users can have multiple roles)
CREATE TABLE `user_roles` (
    `user_role_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `role_id` BIGINT UNSIGNED NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_role_id`),
    UNIQUE KEY `uk_user_roles` (`user_id`, `role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`role_id`) REFERENCES `roles`(`role_id`) ON DELETE CASCADE,
    INDEX `idx_user_roles_user_id` (`user_id`),
    INDEX `idx_user_roles_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Staff Roles (Employee-specific roles with access rights)
CREATE TABLE `staff_roles` (
    `staff_role_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'Employee user_id',
    `role_name` VARCHAR(100) NOT NULL,
    `access_rights` JSON COMMENT 'Structured access permissions',
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`staff_role_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_staff_roles_user_id` (`user_id`),
    INDEX `idx_staff_roles_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 2. WALLET & FINANCIAL TABLES
-- ============================================================================

-- Wallets Table
CREATE TABLE `wallets` (
    `wallet_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `balance` DECIMAL(15, 2) DEFAULT 0.00,
    `hold_amount` DECIMAL(15, 2) DEFAULT 0.00 COMMENT 'Amount on hold for pending orders',
    `currency` VARCHAR(3) DEFAULT 'INR',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`wallet_id`),
    UNIQUE KEY `uk_wallets_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_wallets_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Wallet Transactions
CREATE TABLE `wallet_transactions` (
    `transaction_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `wallet_id` BIGINT UNSIGNED NOT NULL,
    `transaction_type` ENUM('CREDIT', 'DEBIT', 'HOLD', 'RELEASE') NOT NULL,
    `amount` DECIMAL(15, 2) NOT NULL,
    `balance_before` DECIMAL(15, 2) NOT NULL,
    `balance_after` DECIMAL(15, 2) NOT NULL,
    `description` VARCHAR(255),
    `reference_id` VARCHAR(100) COMMENT 'Order ID, Payment ID, etc.',
    `reference_type` VARCHAR(50) COMMENT 'ORDER', 'RECHARGE', 'REFUND', etc.',
    `status` ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`transaction_id`),
    FOREIGN KEY (`wallet_id`) REFERENCES `wallets`(`wallet_id`) ON DELETE CASCADE,
    INDEX `idx_wallet_transactions_wallet_id` (`wallet_id`),
    INDEX `idx_wallet_transactions_type` (`transaction_type`),
    INDEX `idx_wallet_transactions_status` (`status`),
    INDEX `idx_wallet_transactions_created_at` (`created_at`),
    INDEX `idx_wallet_transactions_reference` (`reference_id`, `reference_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payment Transactions (Razorpay, etc.)
CREATE TABLE `payment_transactions` (
    `payment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `wallet_id` BIGINT UNSIGNED,
    `payment_gateway` VARCHAR(50) DEFAULT 'RAZORPAY',
    `gateway_order_id` VARCHAR(255),
    `gateway_payment_id` VARCHAR(255),
    `amount` DECIMAL(15, 2) NOT NULL,
    `currency` VARCHAR(3) DEFAULT 'INR',
    `status` ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING',
    `payment_method` VARCHAR(50),
    `gateway_response` JSON COMMENT 'Full gateway response',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`payment_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`wallet_id`) REFERENCES `wallets`(`wallet_id`) ON DELETE SET NULL,
    INDEX `idx_payment_transactions_user_id` (`user_id`),
    INDEX `idx_payment_transactions_wallet_id` (`wallet_id`),
    INDEX `idx_payment_transactions_status` (`status`),
    INDEX `idx_payment_transactions_gateway_order_id` (`gateway_order_id`),
    INDEX `idx_payment_transactions_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 3. KYC & VERIFICATION TABLES
-- ============================================================================

-- Billing Information
CREATE TABLE `billing_info` (
    `billing_info_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `address` TEXT NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `postal_code` VARCHAR(10) NOT NULL,
    `is_verified` BOOLEAN DEFAULT FALSE,
    `verified_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`billing_info_id`),
    UNIQUE KEY `uk_billing_info_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_billing_info_user_id` (`user_id`),
    INDEX `idx_billing_info_postal_code` (`postal_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Bank Account Details
CREATE TABLE `bank_details` (
    `bank_detail_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `account_number` VARCHAR(50) NOT NULL,
    `ifsc_code` VARCHAR(20) NOT NULL,
    `bank_name` VARCHAR(255),
    `branch_name` VARCHAR(255),
    `branch_city` VARCHAR(100),
    `account_holder_name` VARCHAR(255),
    `is_verified` BOOLEAN DEFAULT FALSE,
    `verification_ref_id` VARCHAR(100),
    `verified_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`bank_detail_id`),
    UNIQUE KEY `uk_bank_details_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_bank_details_user_id` (`user_id`),
    INDEX `idx_bank_details_ifsc` (`ifsc_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Document Details (Aadhaar, PAN)
CREATE TABLE `document_details` (
    `document_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `document_type` ENUM('AADHAAR', 'PAN', 'GSTIN') NOT NULL,
    `document_number` VARCHAR(50) NOT NULL,
    `name` VARCHAR(255),
    `guardian_name` VARCHAR(255) COMMENT 'For Aadhaar',
    `address` TEXT,
    `state` VARCHAR(100),
    `city` VARCHAR(100),
    `pan_type` VARCHAR(50) COMMENT 'Individual/Company',
    `pan_name` VARCHAR(255) COMMENT 'Name on PAN',
    `gstin_address` TEXT COMMENT 'For GSTIN',
    `gstin_city` VARCHAR(100),
    `verification_ref_id` VARCHAR(100) COMMENT 'OTP ref_id for Aadhaar',
    `otp` VARCHAR(10) COMMENT 'Temporary OTP storage',
    `is_verified` BOOLEAN DEFAULT FALSE,
    `verified_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`document_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_document_details_user_id` (`user_id`),
    INDEX `idx_document_details_type` (`document_type`),
    INDEX `idx_document_details_number` (`document_number`),
    INDEX `idx_document_details_verified` (`is_verified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- OTP Storage (for registration and verification)
CREATE TABLE `otp_storage` (
    `otp_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `identifier` VARCHAR(255) NOT NULL COMMENT 'Email or phone number',
    `otp_type` ENUM('PHONE', 'EMAIL', 'AADHAAR') NOT NULL,
    `otp_code` VARCHAR(10) NOT NULL,
    `purpose` VARCHAR(50) COMMENT 'REGISTRATION', 'VERIFICATION', 'PASSWORD_RESET',
    `is_verified` BOOLEAN DEFAULT FALSE,
    `expires_at` TIMESTAMP NOT NULL,
    `verified_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`otp_id`),
    INDEX `idx_otp_storage_identifier` (`identifier`, `otp_type`),
    INDEX `idx_otp_storage_code` (`otp_code`),
    INDEX `idx_otp_storage_expires` (`expires_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 4. ADDRESS MANAGEMENT
-- ============================================================================

-- Pickup Addresses (User's multiple pickup addresses)
CREATE TABLE `pickup_addresses` (
    `pickup_address_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `contact_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255),
    `phone_number` VARCHAR(20) NOT NULL,
    `address` TEXT NOT NULL,
    `city` VARCHAR(100) NOT NULL,
    `state` VARCHAR(100) NOT NULL,
    `pin_code` VARCHAR(10) NOT NULL,
    `is_primary` BOOLEAN DEFAULT FALSE,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`pickup_address_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_pickup_addresses_user_id` (`user_id`),
    INDEX `idx_pickup_addresses_is_primary` (`user_id`, `is_primary`),
    INDEX `idx_pickup_addresses_pin_code` (`pin_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 5. CHANNELS & INTEGRATIONS
-- ============================================================================

-- Sales Channels
CREATE TABLE `channels` (
    `channel_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `channel_name` VARCHAR(100) NOT NULL,
    `channel_type` ENUM('WOOCOMMERCE', 'SHOPIFY', 'MANUAL', 'API', 'OTHER') NOT NULL,
    `channel_config` JSON COMMENT 'Channel-specific configuration',
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`channel_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_channels_user_id` (`user_id`),
    INDEX `idx_channels_type` (`channel_type`),
    INDEX `idx_channels_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 6. COURIER MANAGEMENT
-- ============================================================================

-- Courier Providers (Delhivery, DTDC, Shiprocket, etc.)
CREATE TABLE `courier_providers` (
    `provider_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `provider_name` VARCHAR(100) NOT NULL COMMENT 'Delhivery, DTDC, Shiprocket',
    `provider_code` VARCHAR(50) NOT NULL COMMENT 'DELHIVERY, DTDC, SHIPROCKET',
    `api_endpoint` VARCHAR(255),
    `auth_type` VARCHAR(50) COMMENT 'TOKEN', 'API_KEY', 'OAUTH',
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`provider_id`),
    UNIQUE KEY `uk_courier_providers_code` (`provider_code`),
    INDEX `idx_courier_providers_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Courier Provider Credentials (per user)
CREATE TABLE `courier_credentials` (
    `credential_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `provider_id` BIGINT UNSIGNED NOT NULL,
    `api_key` VARCHAR(255),
    `api_secret` VARCHAR(255),
    `token` TEXT COMMENT 'OAuth token or access token',
    `token_expires_at` TIMESTAMP NULL,
    `refresh_token` TEXT,
    `additional_config` JSON COMMENT 'Provider-specific config',
    `is_active` BOOLEAN DEFAULT TRUE,
    `is_verified` BOOLEAN DEFAULT FALSE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`credential_id`),
    UNIQUE KEY `uk_courier_credentials_user_provider` (`user_id`, `provider_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`provider_id`) REFERENCES `courier_providers`(`provider_id`) ON DELETE CASCADE,
    INDEX `idx_courier_credentials_user_id` (`user_id`),
    INDEX `idx_courier_credentials_provider_id` (`provider_id`),
    INDEX `idx_courier_credentials_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Courier Services (Services from providers like "DTDC Surface 0.5KG")
CREATE TABLE `courier_services` (
    `courier_service_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'User who configured this service',
    `provider_id` BIGINT UNSIGNED NOT NULL,
    `service_name` VARCHAR(255) NOT NULL COMMENT 'DTDC Surface 0.5KG',
    `service_code` VARCHAR(100),
    `courier_type` ENUM('SURFACE', 'AIR', 'EXPRESS') NOT NULL,
    `min_weight` DECIMAL(10, 3) DEFAULT 0,
    `max_weight` DECIMAL(10, 3),
    `is_recommended` BOOLEAN DEFAULT FALSE,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`courier_service_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`provider_id`) REFERENCES `courier_providers`(`provider_id`) ON DELETE CASCADE,
    INDEX `idx_courier_services_user_id` (`user_id`),
    INDEX `idx_courier_services_provider_id` (`provider_id`),
    INDEX `idx_courier_services_active` (`is_active`),
    INDEX `idx_courier_services_type` (`courier_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 7. RATE CARDS & PLANS
-- ============================================================================

-- Rate Card Plans
CREATE TABLE `rate_card_plans` (
    `plan_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `plan_name` VARCHAR(255) NOT NULL,
    `description` TEXT,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`plan_id`),
    UNIQUE KEY `uk_rate_card_plans_name` (`plan_name`),
    INDEX `idx_rate_card_plans_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rate Cards (Pricing rules)
CREATE TABLE `rate_cards` (
    `rate_card_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `plan_id` BIGINT UNSIGNED NOT NULL,
    `courier_service_id` BIGINT UNSIGNED NOT NULL,
    `from_pincode` VARCHAR(10),
    `to_pincode` VARCHAR(10),
    `from_zone` VARCHAR(50),
    `to_zone` VARCHAR(50),
    `weight_from` DECIMAL(10, 3),
    `weight_to` DECIMAL(10, 3),
    `base_charge` DECIMAL(10, 2) NOT NULL,
    `per_kg_charge` DECIMAL(10, 2) DEFAULT 0,
    `cod_charge_percent` DECIMAL(5, 2) DEFAULT 0,
    `cod_charge_fixed` DECIMAL(10, 2) DEFAULT 0,
    `fuel_surcharge_percent` DECIMAL(5, 2) DEFAULT 0,
    `gst_percent` DECIMAL(5, 2) DEFAULT 0,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`rate_card_id`),
    FOREIGN KEY (`plan_id`) REFERENCES `rate_card_plans`(`plan_id`) ON DELETE CASCADE,
    FOREIGN KEY (`courier_service_id`) REFERENCES `courier_services`(`courier_service_id`) ON DELETE CASCADE,
    INDEX `idx_rate_cards_plan_id` (`plan_id`),
    INDEX `idx_rate_cards_courier_service_id` (`courier_service_id`),
    INDEX `idx_rate_cards_pincodes` (`from_pincode`, `to_pincode`),
    INDEX `idx_rate_cards_zones` (`from_zone`, `to_zone`),
    INDEX `idx_rate_cards_weight` (`weight_from`, `weight_to`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User Rate Card Assignments
CREATE TABLE `user_rate_card_assignments` (
    `assignment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `plan_id` BIGINT UNSIGNED NOT NULL,
    `assigned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `assigned_by` BIGINT UNSIGNED COMMENT 'Admin user who assigned',
    PRIMARY KEY (`assignment_id`),
    UNIQUE KEY `uk_user_rate_card_assignments` (`user_id`, `plan_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`plan_id`) REFERENCES `rate_card_plans`(`plan_id`) ON DELETE CASCADE,
    FOREIGN KEY (`assigned_by`) REFERENCES `users`(`user_id`) ON DELETE SET NULL,
    INDEX `idx_user_rate_card_assignments_user_id` (`user_id`),
    INDEX `idx_user_rate_card_assignments_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 8. ORDERS & SHIPMENTS
-- ============================================================================

-- Orders Table
CREATE TABLE `orders` (
    `order_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `order_id_string` VARCHAR(50) UNIQUE COMMENT 'Display ID like ORD001',
    `channel_id` BIGINT UNSIGNED,
    `composite_order_id` VARCHAR(100) COMMENT 'For grouped orders',
    `status` ENUM('NEW', 'PENDING', 'READY_TO_SHIP', 'BOOKED', 'IN_TRANSIT', 
                  'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED', 'RTO', 
                  'RTO_DELIVERED', 'RTO_IN_TRANSIT', 'LOST', 'DAMAGED') DEFAULT 'NEW',
    `pickup_address_id` BIGINT UNSIGNED NOT NULL,
    `receiver_name` VARCHAR(255) NOT NULL,
    `receiver_email` VARCHAR(255),
    `receiver_phone` VARCHAR(20) NOT NULL,
    `receiver_address` TEXT NOT NULL,
    `receiver_city` VARCHAR(100) NOT NULL,
    `receiver_state` VARCHAR(100) NOT NULL,
    `receiver_pin_code` VARCHAR(10) NOT NULL,
    `payment_method` ENUM('COD', 'PREPAID') NOT NULL,
    `order_value` DECIMAL(15, 2) NOT NULL,
    `cod_amount` DECIMAL(15, 2) DEFAULT 0.00,
    `dead_weight` DECIMAL(10, 3) NOT NULL COMMENT 'Actual weight in kg',
    `volumetric_weight` DECIMAL(10, 3) COMMENT 'Calculated volumetric weight',
    `applicable_weight` DECIMAL(10, 3) NOT NULL COMMENT 'Charged weight',
    `package_length` INT UNSIGNED COMMENT 'in cm',
    `package_width` INT UNSIGNED COMMENT 'in cm',
    `package_height` INT UNSIGNED COMMENT 'in cm',
    `courier_service_id` BIGINT UNSIGNED,
    `provider_id` BIGINT UNSIGNED,
    `awb_number` VARCHAR(100),
    `shipment_id` VARCHAR(255),
    `estimated_delivery_date` DATE,
    `zone` VARCHAR(50),
    `total_freight_charges` DECIMAL(15, 2) DEFAULT 0.00,
    `shipment_created_at` TIMESTAMP NULL,
    `version` INT UNSIGNED DEFAULT 1,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_id`),
    UNIQUE KEY `uk_orders_order_id_string` (`order_id_string`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`channel_id`) REFERENCES `channels`(`channel_id`) ON DELETE SET NULL,
    FOREIGN KEY (`pickup_address_id`) REFERENCES `pickup_addresses`(`pickup_address_id`) ON DELETE RESTRICT,
    FOREIGN KEY (`courier_service_id`) REFERENCES `courier_services`(`courier_service_id`) ON DELETE SET NULL,
    FOREIGN KEY (`provider_id`) REFERENCES `courier_providers`(`provider_id`) ON DELETE SET NULL,
    INDEX `idx_orders_user_id` (`user_id`),
    INDEX `idx_orders_status` (`status`),
    INDEX `idx_orders_order_id_string` (`order_id_string`),
    INDEX `idx_orders_created_at` (`created_at`),
    INDEX `idx_orders_awb_number` (`awb_number`),
    INDEX `idx_orders_shipment_id` (`shipment_id`),
    INDEX `idx_orders_pickup_address_id` (`pickup_address_id`),
    INDEX `idx_orders_courier_service_id` (`courier_service_id`),
    INDEX `idx_orders_composite_order_id` (`composite_order_id`),
    INDEX `idx_orders_user_status` (`user_id`, `status`),
    INDEX `idx_orders_created_status` (`created_at`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Order Products
CREATE TABLE `order_products` (
    `order_product_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `product_name` VARCHAR(255) NOT NULL,
    `sku` VARCHAR(100),
    `quantity` INT UNSIGNED NOT NULL,
    `unit_price` DECIMAL(15, 2) NOT NULL,
    `total_price` DECIMAL(15, 2) NOT NULL COMMENT 'quantity * unit_price',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`order_product_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
    INDEX `idx_order_products_order_id` (`order_id`),
    INDEX `idx_order_products_sku` (`sku`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Order Tracking
CREATE TABLE `order_tracking` (
    `tracking_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `status` VARCHAR(100) NOT NULL,
    `status_location` VARCHAR(255),
    `status_date_time` TIMESTAMP NOT NULL,
    `instructions` TEXT,
    `source` VARCHAR(50) DEFAULT 'COURIER' COMMENT 'COURIER', 'MANUAL', 'SYSTEM',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`tracking_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
    INDEX `idx_order_tracking_order_id` (`order_id`),
    INDEX `idx_order_tracking_status_date` (`order_id`, `status_date_time`),
    INDEX `idx_order_tracking_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 9. NDR (Non-Delivery Report) MANAGEMENT
-- ============================================================================

-- NDR Records
CREATE TABLE `ndr_records` (
    `ndr_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `ndr_type` ENUM('ACTION_REQUIRED', 'ACTION_REQUESTED', 'DELIVERED', 'RTO_INITIATED') NOT NULL,
    `attempt_number` INT UNSIGNED DEFAULT 1,
    `reason` VARCHAR(255),
    `action_requested` VARCHAR(255) COMMENT 'What action user requested',
    `action_taken` VARCHAR(255) COMMENT 'What action was actually taken',
    `status` ENUM('PENDING', 'RESOLVED', 'RTO') DEFAULT 'PENDING',
    `resolved_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`ndr_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
    INDEX `idx_ndr_records_order_id` (`order_id`),
    INDEX `idx_ndr_records_type` (`ndr_type`),
    INDEX `idx_ndr_records_status` (`status`),
    INDEX `idx_ndr_records_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 10. BILLING & REMITTANCE
-- ============================================================================

-- Shipping Charges (Billing records)
CREATE TABLE `shipping_charges` (
    `shipping_charge_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `freight_charges` DECIMAL(15, 2) NOT NULL,
    `cod_charges` DECIMAL(15, 2) DEFAULT 0.00,
    `fuel_surcharge` DECIMAL(15, 2) DEFAULT 0.00,
    `gst_amount` DECIMAL(15, 2) DEFAULT 0.00,
    `total_charges` DECIMAL(15, 2) NOT NULL,
    `billing_period_start` DATE,
    `billing_period_end` DATE,
    `invoice_number` VARCHAR(100),
    `invoice_date` DATE,
    `payment_status` ENUM('PENDING', 'PAID', 'DUE') DEFAULT 'PENDING',
    `paid_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`shipping_charge_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_shipping_charges_order_id` (`order_id`),
    INDEX `idx_shipping_charges_user_id` (`user_id`),
    INDEX `idx_shipping_charges_payment_status` (`payment_status`),
    INDEX `idx_shipping_charges_billing_period` (`billing_period_start`, `billing_period_end`),
    INDEX `idx_shipping_charges_invoice` (`invoice_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- COD Remittances
CREATE TABLE `cod_remittances` (
    `remittance_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `remittance_amount` DECIMAL(15, 2) NOT NULL,
    `remittance_type` ENUM('SELLER', 'COURIER') NOT NULL,
    `remittance_status` ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED') DEFAULT 'PENDING',
    `remittance_date` DATE,
    `transaction_reference` VARCHAR(255),
    `courier_remittance_id` VARCHAR(255) COMMENT 'Remittance ID from courier',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`remittance_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
    INDEX `idx_cod_remittances_user_id` (`user_id`),
    INDEX `idx_cod_remittances_order_id` (`order_id`),
    INDEX `idx_cod_remittances_status` (`remittance_status`),
    INDEX `idx_cod_remittances_type` (`remittance_type`),
    INDEX `idx_cod_remittances_date` (`remittance_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Passbook Entries (Recharge history)
CREATE TABLE `passbook_entries` (
    `passbook_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `transaction_type` ENUM('RECHARGE', 'DEBIT', 'REFUND', 'ADJUSTMENT') NOT NULL,
    `amount` DECIMAL(15, 2) NOT NULL,
    `balance_before` DECIMAL(15, 2) NOT NULL,
    `balance_after` DECIMAL(15, 2) NOT NULL,
    `description` VARCHAR(255),
    `reference_id` VARCHAR(100),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`passbook_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_passbook_entries_user_id` (`user_id`),
    INDEX `idx_passbook_entries_type` (`transaction_type`),
    INDEX `idx_passbook_entries_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 11. SUPPORT & TICKETS
-- ============================================================================

-- Support Tickets
CREATE TABLE `support_tickets` (
    `ticket_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `ticket_number` VARCHAR(50) UNIQUE NOT NULL,
    `subject` VARCHAR(255) NOT NULL,
    `description` TEXT NOT NULL,
    `category` VARCHAR(100),
    `priority` ENUM('LOW', 'MEDIUM', 'HIGH', 'URGENT') DEFAULT 'MEDIUM',
    `status` ENUM('OPEN', 'IN_PROGRESS', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    `assigned_to` BIGINT UNSIGNED COMMENT 'Employee user_id',
    `resolved_at` TIMESTAMP NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`ticket_id`),
    UNIQUE KEY `uk_support_tickets_number` (`ticket_number`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`assigned_to`) REFERENCES `users`(`user_id`) ON DELETE SET NULL,
    INDEX `idx_support_tickets_user_id` (`user_id`),
    INDEX `idx_support_tickets_status` (`status`),
    INDEX `idx_support_tickets_priority` (`priority`),
    INDEX `idx_support_tickets_assigned_to` (`assigned_to`),
    INDEX `idx_support_tickets_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Support Ticket Attachments
CREATE TABLE `support_ticket_attachments` (
    `attachment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `ticket_id` BIGINT UNSIGNED NOT NULL,
    `file_name` VARCHAR(255) NOT NULL,
    `file_path` VARCHAR(500) NOT NULL,
    `file_size` BIGINT UNSIGNED,
    `file_type` VARCHAR(100),
    `uploaded_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`attachment_id`),
    FOREIGN KEY (`ticket_id`) REFERENCES `support_tickets`(`ticket_id`) ON DELETE CASCADE,
    INDEX `idx_support_ticket_attachments_ticket_id` (`ticket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Support Ticket Comments
CREATE TABLE `support_ticket_comments` (
    `comment_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `ticket_id` BIGINT UNSIGNED NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `comment_text` TEXT NOT NULL,
    `is_internal` BOOLEAN DEFAULT FALSE COMMENT 'Internal note not visible to user',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`comment_id`),
    FOREIGN KEY (`ticket_id`) REFERENCES `support_tickets`(`ticket_id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_support_ticket_comments_ticket_id` (`ticket_id`),
    INDEX `idx_support_ticket_comments_user_id` (`user_id`),
    INDEX `idx_support_ticket_comments_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 12. EDD (Estimated Delivery Date) MAPPING
-- ============================================================================

-- EDD Mappings
CREATE TABLE `edd_mappings` (
    `edd_mapping_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `courier_service_id` BIGINT UNSIGNED NOT NULL,
    `from_pincode` VARCHAR(10),
    `to_pincode` VARCHAR(10),
    `from_zone` VARCHAR(50),
    `to_zone` VARCHAR(50),
    `estimated_days` INT UNSIGNED NOT NULL COMMENT 'Days from shipment',
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`edd_mapping_id`),
    FOREIGN KEY (`courier_service_id`) REFERENCES `courier_services`(`courier_service_id`) ON DELETE CASCADE,
    INDEX `idx_edd_mappings_courier_service_id` (`courier_service_id`),
    INDEX `idx_edd_mappings_pincodes` (`from_pincode`, `to_pincode`),
    INDEX `idx_edd_mappings_zones` (`from_zone`, `to_zone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 13. LABEL CUSTOMIZATION
-- ============================================================================

-- Label Settings
CREATE TABLE `label_settings` (
    `label_setting_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `company_name` VARCHAR(255),
    `company_logo_url` VARCHAR(500),
    `company_address` TEXT,
    `company_phone` VARCHAR(20),
    `company_email` VARCHAR(255),
    `company_gstin` VARCHAR(20),
    `additional_fields` JSON COMMENT 'Custom fields for labels',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`label_setting_id`),
    UNIQUE KEY `uk_label_settings_user_id` (`user_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_label_settings_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 14. WEIGHT DISCREPANCY
-- ============================================================================

-- Weight Discrepancies
CREATE TABLE `weight_discrepancies` (
    `discrepancy_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `order_id` BIGINT UNSIGNED NOT NULL,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `declared_weight` DECIMAL(10, 3) NOT NULL,
    `actual_weight` DECIMAL(10, 3) NOT NULL,
    `weight_difference` DECIMAL(10, 3) NOT NULL,
    `additional_charges` DECIMAL(15, 2) DEFAULT 0.00,
    `status` ENUM('PENDING', 'DISPUTE_RAISED', 'RESOLVED', 'ACCEPTED') DEFAULT 'PENDING',
    `dispute_reason` TEXT,
    `resolved_at` TIMESTAMP NULL,
    `resolved_by` BIGINT UNSIGNED COMMENT 'Admin user',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`discrepancy_id`),
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`order_id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`resolved_by`) REFERENCES `users`(`user_id`) ON DELETE SET NULL,
    INDEX `idx_weight_discrepancies_order_id` (`order_id`),
    INDEX `idx_weight_discrepancies_user_id` (`user_id`),
    INDEX `idx_weight_discrepancies_status` (`status`),
    INDEX `idx_weight_discrepancies_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 15. ADMIN BILLING (Admin-only billing operations)
-- ============================================================================

-- Admin Billing History
CREATE TABLE `admin_billing_history` (
    `billing_history_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `transaction_type` VARCHAR(100) NOT NULL,
    `amount` DECIMAL(15, 2) NOT NULL,
    `description` TEXT,
    `created_by` BIGINT UNSIGNED NOT NULL COMMENT 'Admin user',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`billing_history_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`created_by`) REFERENCES `users`(`user_id`) ON DELETE RESTRICT,
    INDEX `idx_admin_billing_history_user_id` (`user_id`),
    INDEX `idx_admin_billing_history_created_by` (`created_by`),
    INDEX `idx_admin_billing_history_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 16. AUDIT & LOGGING TABLES
-- ============================================================================

-- API Request Logs (Optional - for debugging and analytics)
CREATE TABLE `api_request_logs` (
    `log_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED,
    `endpoint` VARCHAR(255) NOT NULL,
    `method` VARCHAR(10) NOT NULL,
    `request_body` JSON,
    `response_status` INT,
    `response_time_ms` INT,
    `ip_address` VARCHAR(45),
    `user_agent` VARCHAR(500),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`log_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL,
    INDEX `idx_api_request_logs_user_id` (`user_id`),
    INDEX `idx_api_request_logs_endpoint` (`endpoint`),
    INDEX `idx_api_request_logs_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 17. PACKAGE TYPES (Setup & Manage)
-- ============================================================================

-- Package Types
CREATE TABLE `package_types` (
    `package_type_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `type_name` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`package_type_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_package_types_user_id` (`user_id`),
    INDEX `idx_package_types_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- 18. CUSTOMERS (Setup & Manage)
-- ============================================================================

-- Customers
CREATE TABLE `customers` (
    `customer_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT UNSIGNED NOT NULL COMMENT 'Seller user_id',
    `customer_name` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255),
    `phone_number` VARCHAR(20),
    `address` TEXT,
    `city` VARCHAR(100),
    `state` VARCHAR(100),
    `pin_code` VARCHAR(10),
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`customer_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    INDEX `idx_customers_user_id` (`user_id`),
    INDEX `idx_customers_email` (`email`),
    INDEX `idx_customers_phone` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================================
-- INITIAL DATA / SEED DATA
-- ============================================================================

-- Insert default roles
INSERT INTO `roles` (`role_name`, `description`) VALUES
('ROLE_USER', 'Regular user'),
('ROLE_EMPLOYEE', 'Employee user'),
('ROLE_ADMIN', 'Administrator');

-- Insert default courier providers
INSERT INTO `courier_providers` (`provider_name`, `provider_code`, `is_active`) VALUES
('Delhivery', 'DELHIVERY', TRUE),
('DTDC', 'DTDC', TRUE),
('Shiprocket', 'SHIPROCKET', TRUE),
('Nimbuspost', 'NIMBUSPOST', TRUE),
('Xpressbees', 'XPRESSBEES', TRUE),
('Ecom Express', 'ECOMEXPRESS', TRUE),
('Ekart', 'EKART', TRUE),
('Shree Maruti', 'SHREEMARUTI', TRUE),
('SmartShip', 'SMARTSHIP', TRUE),
('Vamaship', 'VAMASHIP', TRUE),
('Amazon Shipping', 'AMAZON', TRUE);

-- ============================================================================
-- VIEWS (Optional - for common queries)
-- ============================================================================

-- View: User Dashboard Summary
CREATE OR REPLACE VIEW `v_user_dashboard` AS
SELECT 
    u.user_id,
    u.email,
    u.fullname,
    u.kyc_done,
    u.is_verified,
    w.balance,
    w.hold_amount,
    COUNT(DISTINCT o.order_id) as total_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'NEW' THEN o.order_id END) as new_orders,
    COUNT(DISTINCT CASE WHEN o.status = 'DELIVERED' THEN o.order_id END) as delivered_orders,
    SUM(CASE WHEN o.status = 'DELIVERED' THEN o.total_freight_charges ELSE 0 END) as total_revenue
FROM users u
LEFT JOIN wallets w ON u.user_id = w.user_id
LEFT JOIN orders o ON u.user_id = o.user_id
GROUP BY u.user_id, u.email, u.fullname, u.kyc_done, u.is_verified, w.balance, w.hold_amount;

-- View: Order Summary with Courier Info
CREATE OR REPLACE VIEW `v_order_summary` AS
SELECT 
    o.order_id,
    o.order_id_string,
    o.user_id,
    u.fullname as user_name,
    u.email as user_email,
    o.status,
    o.receiver_name,
    o.receiver_city,
    o.receiver_state,
    o.payment_method,
    o.order_value,
    o.total_freight_charges,
    cp.provider_name,
    cs.service_name,
    o.awb_number,
    o.estimated_delivery_date,
    o.created_at
FROM orders o
LEFT JOIN users u ON o.user_id = u.user_id
LEFT JOIN courier_providers cp ON o.provider_id = cp.provider_id
LEFT JOIN courier_services cs ON o.courier_service_id = cs.courier_service_id;

-- ============================================================================
-- STORED PROCEDURES (Optional - for complex operations)
-- ============================================================================

DELIMITER //

-- Procedure: Calculate Order Charges
CREATE PROCEDURE `sp_calculate_order_charges`(
    IN p_order_id BIGINT UNSIGNED
)
BEGIN
    DECLARE v_base_charge DECIMAL(15, 2);
    DECLARE v_per_kg_charge DECIMAL(10, 2);
    DECLARE v_weight DECIMAL(10, 3);
    DECLARE v_cod_amount DECIMAL(15, 2);
    DECLARE v_cod_charge_percent DECIMAL(5, 2);
    DECLARE v_cod_charge_fixed DECIMAL(10, 2);
    DECLARE v_fuel_surcharge_percent DECIMAL(5, 2);
    DECLARE v_gst_percent DECIMAL(5, 2);
    DECLARE v_total_charges DECIMAL(15, 2);
    
    -- Get order details
    SELECT 
        o.applicable_weight,
        o.cod_amount,
        o.courier_service_id,
        o.receiver_pin_code,
        o.pickup_address_id
    INTO v_weight, v_cod_amount, @v_courier_service_id, @v_to_pincode, @v_pickup_address_id
    FROM orders o
    WHERE o.order_id = p_order_id;
    
    -- Get pickup pincode
    SELECT pin_code INTO @v_from_pincode
    FROM pickup_addresses
    WHERE pickup_address_id = @v_pickup_address_id;
    
    -- Get rate card (simplified - you may need more complex logic)
    SELECT 
        base_charge,
        per_kg_charge,
        cod_charge_percent,
        cod_charge_fixed,
        fuel_surcharge_percent,
        gst_percent
    INTO v_base_charge, v_per_kg_charge, v_cod_charge_percent, v_cod_charge_fixed,
         v_fuel_surcharge_percent, v_gst_percent
    FROM rate_cards rc
    WHERE rc.courier_service_id = @v_courier_service_id
    LIMIT 1;
    
    -- Calculate charges
    SET v_total_charges = v_base_charge + (v_weight * v_per_kg_charge);
    
    IF v_cod_amount > 0 THEN
        SET v_total_charges = v_total_charges + 
            (v_cod_amount * v_cod_charge_percent / 100) + v_cod_charge_fixed;
    END IF;
    
    SET v_total_charges = v_total_charges + (v_total_charges * v_fuel_surcharge_percent / 100);
    SET v_total_charges = v_total_charges + (v_total_charges * v_gst_percent / 100);
    
    -- Update order
    UPDATE orders
    SET total_freight_charges = v_total_charges
    WHERE order_id = p_order_id;
    
    SELECT v_total_charges as calculated_charges;
END //

DELIMITER ;

-- ============================================================================
-- TRIGGERS (Optional - for automatic operations)
-- ============================================================================

DELIMITER //

-- Trigger: Auto-generate order_id_string
CREATE TRIGGER `trg_orders_before_insert`
BEFORE INSERT ON `orders`
FOR EACH ROW
BEGIN
    IF NEW.order_id_string IS NULL THEN
        SET NEW.order_id_string = CONCAT('ORD', LPAD(NEW.order_id, 8, '0'));
    END IF;
END //

-- Trigger: Auto-generate user_id_string
CREATE TRIGGER `trg_users_before_insert`
BEFORE INSERT ON `users`
FOR EACH ROW
BEGIN
    IF NEW.user_id_string IS NULL THEN
        SET NEW.user_id_string = CONCAT('USER', LPAD(NEW.user_id, 6, '0'));
    END IF;
END //

-- Trigger: Auto-generate support ticket number
CREATE TRIGGER `trg_support_tickets_before_insert`
BEFORE INSERT ON `support_tickets`
FOR EACH ROW
BEGIN
    IF NEW.ticket_number IS NULL THEN
        SET NEW.ticket_number = CONCAT('TKT', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(NEW.ticket_id, 6, '0'));
    END IF;
END //

-- Trigger: Update wallet balance on transaction
CREATE TRIGGER `trg_wallet_transactions_after_insert`
AFTER INSERT ON `wallet_transactions`
FOR EACH ROW
BEGIN
    IF NEW.status = 'COMPLETED' THEN
        UPDATE wallets
        SET balance = NEW.balance_after,
            updated_at = NOW()
        WHERE wallet_id = NEW.wallet_id;
    END IF;
END //

DELIMITER ;

-- ============================================================================
-- INDEXES SUMMARY
-- ============================================================================

-- All indexes are defined inline with table definitions above.
-- Key indexes for performance:
-- 1. Foreign key indexes on all FK columns
-- 2. Status/enum fields for filtering
-- 3. Date fields for time-based queries
-- 4. Unique identifiers (email, order_id_string, etc.)
-- 5. Composite indexes for common query patterns

-- ============================================================================
-- NOTES
-- ============================================================================

-- 1. All monetary values use DECIMAL(15, 2) for precision
-- 2. All timestamps use TIMESTAMP with automatic defaults
-- 3. Email is used as unique identifier in some legacy tables (billing_info, bank_details)
--    but normalized to use user_id as FK
-- 4. JSON columns used for flexible configuration storage
-- 5. Soft deletes can be added using is_active flags
-- 6. Consider adding created_by/updated_by audit fields if needed
-- 7. Consider partitioning large tables (orders, tracking) by date if needed
-- 8. Consider read replicas for reporting queries
