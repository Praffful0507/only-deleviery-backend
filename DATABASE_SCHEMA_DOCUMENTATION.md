# Database Schema Documentation

## Overview

This document describes the normalized MySQL database schema designed based on the API contract and frontend requirements. The schema follows **Third Normal Form (3NF)** principles to eliminate data redundancy and ensure data integrity.

## Normalization Approach

### Normalization Levels Applied

1. **First Normal Form (1NF)**: All tables have atomic values, no repeating groups
2. **Second Normal Form (2NF)**: All non-key attributes fully depend on primary key
3. **Third Normal Form (3NF)**: No transitive dependencies

### Key Normalization Decisions

1. **Separated Embedded Objects**: 
   - `PaymentDetails`, `PackageDetails`, `PickUpAddress`, `ReceiverAddress` were embedded in `Order` entity
   - Normalized into separate columns in `orders` table or related tables
   - `ProductDetail` and `Tracking` moved to separate tables (`order_products`, `order_tracking`)

2. **User Identification**:
   - Changed from email-based foreign keys to `user_id` (BIGINT) foreign keys
   - Email remains unique but not used as FK (better for user management)

3. **Address Management**:
   - Pickup addresses normalized into `pickup_addresses` table
   - Receiver address stored directly in `orders` (one-time use)
   - Supports multiple pickup addresses per user

4. **Courier Management**:
   - Separated providers, credentials, and services
   - Supports multiple courier integrations per user
   - Credentials stored separately for security

5. **Financial Transactions**:
   - Separate tables for wallets, transactions, payments, remittances
   - Audit trail for all financial operations

## Database Structure

### Total Tables: 35

#### Core Tables (5)
1. `users` - Main user entity
2. `roles` - Role definitions
3. `user_roles` - User-role assignments
4. `staff_roles` - Employee-specific roles
5. `wallets` - User wallets

#### Financial Tables (4)
6. `wallet_transactions` - Wallet transaction history
7. `payment_transactions` - Payment gateway transactions
8. `shipping_charges` - Shipping billing records
9. `cod_remittances` - COD remittance records
10. `passbook_entries` - Recharge/payment history

#### KYC & Verification Tables (4)
11. `billing_info` - Billing addresses
12. `bank_details` - Bank account information
13. `document_details` - Aadhaar, PAN, GSTIN documents
14. `otp_storage` - OTP management

#### Address & Location Tables (1)
15. `pickup_addresses` - User pickup addresses

#### Order Management Tables (3)
16. `orders` - Main orders table
17. `order_products` - Order line items
18. `order_tracking` - Order tracking history

#### Courier Management Tables (3)
19. `courier_providers` - Courier provider master
20. `courier_credentials` - User courier credentials
21. `courier_services` - Courier services configuration

#### Rate Card Tables (3)
22. `rate_card_plans` - Rate card plan definitions
23. `rate_cards` - Rate card pricing rules
24. `user_rate_card_assignments` - User-plan assignments

#### NDR & Support Tables (4)
25. `ndr_records` - Non-delivery reports
26. `support_tickets` - Support tickets
27. `support_ticket_attachments` - Ticket attachments
28. `support_ticket_comments` - Ticket comments

#### Configuration Tables (4)
29. `channels` - Sales channels
30. `edd_mappings` - Estimated delivery date mappings
31. `label_settings` - Label customization
32. `package_types` - Package type definitions
33. `customers` - Customer master

#### Admin & Audit Tables (2)
34. `admin_billing_history` - Admin billing operations
35. `weight_discrepancies` - Weight discrepancy records
36. `api_request_logs` - API request logging (optional)

## Entity Relationship Diagram (Text Representation)

```
┌─────────────┐
│   users     │
└──────┬──────┘
       │
       ├───┬─────────────────────────────────────┐
       │   │                                     │
       │   ├──► wallets                         │
       │   │    └──► wallet_transactions        │
       │   │
       │   ├──► user_roles                      │
       │   │    └──► roles                      │
       │   │
       │   ├──► staff_roles                     │
       │   │
       │   ├──► billing_info                    │
       │   ├──► bank_details                    │
       │   ├──► document_details                │
       │   ├──► pickup_addresses                │
       │   ├──► channels                        │
       │   ├──► courier_credentials             │
       │   ├──► courier_services                │
       │   ├──► user_rate_card_assignments      │
       │   ├──► orders                          │
       │   │    ├──► order_products            │
       │   │    ├──► order_tracking             │
       │   │    ├──► ndr_records                │
       │   │    ├──► shipping_charges          │
       │   │    ├──► cod_remittances            │
       │   │    └──► weight_discrepancies       │
       │   ├──► support_tickets                │
       │   │    ├──► support_ticket_attachments │
       │   │    └──► support_ticket_comments    │
       │   ├──► payment_transactions            │
       │   ├──► passbook_entries                │
       │   ├──► label_settings                  │
       │   ├──► package_types                   │
       │   ├──► customers                       │
       │   └──► admin_billing_history           │
       │
       └──► api_request_logs

┌──────────────────┐
│courier_providers │
└────────┬─────────┘
         │
         ├──► courier_credentials
         ├──► courier_services
         │    ├──► rate_cards
         │    └──► edd_mappings
         └──► orders

┌──────────────────┐
│rate_card_plans   │
└────────┬─────────┘
         │
         ├──► rate_cards
         └──► user_rate_card_assignments

┌──────────────────┐
│pickup_addresses  │
└────────┬─────────┘
         │
         └──► orders
```

## Key Relationships

### One-to-Many Relationships

1. **User → Orders**: One user can have many orders
2. **User → Pickup Addresses**: One user can have multiple pickup addresses
3. **User → Support Tickets**: One user can have multiple tickets
4. **Order → Order Products**: One order can have multiple products
5. **Order → Order Tracking**: One order can have multiple tracking entries
6. **Order → NDR Records**: One order can have multiple NDR records
7. **Courier Provider → Courier Services**: One provider can have multiple services
8. **Courier Service → Rate Cards**: One service can have multiple rate cards
9. **Support Ticket → Comments**: One ticket can have multiple comments
10. **Support Ticket → Attachments**: One ticket can have multiple attachments

### Many-to-Many Relationships

1. **Users ↔ Roles**: Users can have multiple roles, roles can be assigned to multiple users
   - Junction table: `user_roles`

2. **Users ↔ Rate Card Plans**: Users can be assigned multiple plans
   - Junction table: `user_rate_card_assignments`

### One-to-One Relationships

1. **User → Wallet**: One user has one wallet
2. **User → Billing Info**: One user has one billing info
3. **User → Bank Details**: One user has one bank account
4. **User → Document Details**: One user has one document record (but can have multiple document types)
5. **User → Label Settings**: One user has one label configuration

## Index Strategy

### Primary Indexes
- All tables have `BIGINT UNSIGNED AUTO_INCREMENT` primary keys
- Composite primary keys avoided for simplicity

### Unique Indexes
- `users.email` - Email must be unique
- `users.user_id_string` - Display ID must be unique
- `orders.order_id_string` - Order display ID must be unique
- `support_tickets.ticket_number` - Ticket number must be unique
- `wallets.user_id` - One wallet per user
- `billing_info.user_id` - One billing info per user
- `bank_details.user_id` - One bank detail per user
- `label_settings.user_id` - One label setting per user

### Foreign Key Indexes
- All foreign key columns are indexed for join performance
- Examples: `orders.user_id`, `orders.pickup_address_id`, `order_products.order_id`

### Query Optimization Indexes

#### Status/State Indexes
- `orders.status` - Filter orders by status
- `support_tickets.status` - Filter tickets by status
- `ndr_records.status` - Filter NDR by status
- `wallet_transactions.status` - Filter transactions by status

#### Date/Time Indexes
- `orders.created_at` - Time-based queries
- `order_tracking.status_date_time` - Tracking timeline queries
- `wallet_transactions.created_at` - Transaction history

#### Composite Indexes
- `orders(user_id, status)` - User's orders by status
- `orders(created_at, status)` - Orders by date and status
- `order_tracking(order_id, status_date_time)` - Tracking timeline
- `pickup_addresses(user_id, is_primary)` - Primary address lookup
- `rate_cards(from_pincode, to_pincode)` - Rate lookup
- `edd_mappings(from_pincode, to_pincode)` - EDD lookup

#### Search Indexes
- `users.email` - User search
- `orders.order_id_string` - Order search
- `orders.awb_number` - AWB tracking
- `support_tickets.ticket_number` - Ticket lookup

## Data Types & Constraints

### Common Patterns

#### IDs
- Primary Keys: `BIGINT UNSIGNED AUTO_INCREMENT`
- Foreign Keys: `BIGINT UNSIGNED`
- Display IDs: `VARCHAR(50)` with UNIQUE constraint

#### Monetary Values
- All amounts: `DECIMAL(15, 2)` for precision
- Examples: `balance`, `amount`, `charges`, `order_value`

#### Weights & Dimensions
- Weight: `DECIMAL(10, 3)` - Supports up to 9999999.999 kg
- Dimensions: `INT UNSIGNED` - Centimeters

#### Text Fields
- Names: `VARCHAR(255)`
- Addresses: `TEXT`
- Descriptions: `TEXT`
- Short codes: `VARCHAR(50)` or `VARCHAR(100)`

#### Enums
- Status fields: `ENUM` for controlled values
- Examples: Order status, Payment status, Ticket status

#### JSON Fields
- Configuration: `JSON` for flexible schema
- Examples: `access_rights`, `channel_config`, `additional_config`

#### Timestamps
- All tables: `created_at`, `updated_at`
- Format: `TIMESTAMP DEFAULT CURRENT_TIMESTAMP`
- Auto-update: `ON UPDATE CURRENT_TIMESTAMP`

## Key Design Decisions

### 1. User ID vs Email
- **Decision**: Use `user_id` (BIGINT) as primary key and foreign key
- **Reason**: Better performance, allows email changes, standard practice
- **Legacy**: Some tables may still reference email (can be migrated)

### 2. Receiver Address Storage
- **Decision**: Store directly in `orders` table
- **Reason**: One-time use, no need for separate table
- **Alternative**: Could normalize to `receiver_addresses` if reuse needed

### 3. Courier Credentials
- **Decision**: Separate table per user-provider combination
- **Reason**: Security, supports multiple providers per user
- **Encryption**: Should encrypt sensitive fields (api_key, api_secret, token)

### 4. Rate Cards
- **Decision**: Separate tables for plans and rate rules
- **Reason**: Flexible pricing, supports multiple plans
- **Complexity**: Rate calculation may need stored procedures

### 5. Order Status
- **Decision**: Single ENUM field with all statuses
- **Reason**: Simpler queries, single source of truth
- **Alternative**: Could use state machine table for complex workflows

### 6. Tracking History
- **Decision**: Separate table with one row per tracking event
- **Reason**: Complete audit trail, supports multiple sources
- **Performance**: Indexed for timeline queries

### 7. Financial Transactions
- **Decision**: Separate tables for different transaction types
- **Reason**: Clear separation, different business rules
- **Audit**: All transactions have before/after balances

## Performance Considerations

### Indexing Strategy
1. **Foreign Keys**: All FK columns indexed
2. **Status Fields**: Indexed for filtering
3. **Date Fields**: Indexed for time-based queries
4. **Composite Indexes**: For common query patterns
5. **Unique Constraints**: On business keys (email, order_id_string)

### Partitioning Recommendations
For high-volume tables, consider partitioning by date:
- `orders` - Partition by `created_at` (monthly)
- `order_tracking` - Partition by `status_date_time` (monthly)
- `wallet_transactions` - Partition by `created_at` (monthly)
- `api_request_logs` - Partition by `created_at` (daily/weekly)

### Query Optimization
1. Use views for common joins (`v_user_dashboard`, `v_order_summary`)
2. Stored procedures for complex calculations
3. Consider materialized views for reporting
4. Read replicas for analytics queries

## Security Considerations

### Sensitive Data
1. **Passwords**: Stored as BCrypt hash (already in `users.password`)
2. **API Keys/Tokens**: Should be encrypted at application level
3. **Bank Details**: Consider encryption for account numbers
4. **OTP**: Auto-expire, one-time use

### Access Control
1. Row-level security via `user_id` foreign keys
2. Admin operations tracked via `created_by` fields
3. Audit trail in transaction tables

## Migration Notes

### From Current Schema
1. **Email-based FKs**: Migrate to `user_id` foreign keys
2. **Embedded Objects**: Extract to separate columns/tables
3. **Element Collections**: Move to separate tables (`order_products`, `order_tracking`)
4. **User ID String**: Generate for existing users

### Data Migration Steps
1. Create new schema
2. Migrate users first (generate `user_id_string`)
3. Migrate addresses
4. Migrate orders (extract embedded data)
5. Migrate products and tracking
6. Migrate KYC data
7. Create wallets for existing users
8. Verify data integrity

## Maintenance

### Regular Tasks
1. **Archive old data**: Move old orders/tracking to archive tables
2. **Cleanup OTP**: Delete expired OTPs
3. **Index maintenance**: Rebuild indexes periodically
4. **Statistics update**: Update table statistics for query optimizer

### Monitoring
1. Monitor slow queries
2. Check index usage
3. Monitor table sizes
4. Track foreign key constraint violations

## Future Enhancements

### Potential Additions
1. **Audit Tables**: Track all changes to critical tables
2. **Soft Deletes**: Add `deleted_at` timestamp
3. **Versioning**: Track order/rate card versions
4. **Caching Tables**: Materialized views for dashboards
5. **Notification Queue**: For async notifications
6. **Webhook Logs**: Track external API calls
7. **Rate Limiting**: Track API usage per user

### Scalability
1. **Sharding**: By user_id for multi-tenant scaling
2. **Read Replicas**: For reporting and analytics
3. **Caching Layer**: Redis for frequently accessed data
4. **Search Engine**: Elasticsearch for full-text search

## Table Size Estimates

### Expected Volumes (per year)
- **users**: 10,000 - 100,000
- **orders**: 1,000,000 - 10,000,000
- **order_products**: 2,000,000 - 20,000,000 (avg 2 products/order)
- **order_tracking**: 5,000,000 - 50,000,000 (avg 5 events/order)
- **wallet_transactions**: 500,000 - 5,000,000
- **support_tickets**: 50,000 - 500,000

### Storage Estimates
- **orders**: ~500 bytes/row = 500MB - 5GB
- **order_tracking**: ~200 bytes/row = 1GB - 10GB
- **wallet_transactions**: ~300 bytes/row = 150MB - 1.5GB

## Backup Strategy

### Recommended
1. **Full Backup**: Daily
2. **Incremental Backup**: Every 6 hours
3. **Transaction Log**: Continuous
4. **Point-in-Time Recovery**: Enabled
5. **Retention**: 30 days full, 7 days incremental

### Critical Tables Priority
1. **users**, **wallets**, **wallet_transactions** - Highest priority
2. **orders**, **order_products** - High priority
3. **courier_credentials** - High priority (sensitive)
4. Others - Standard priority
