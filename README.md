# Simple Banking System

This project implements a simple banking system using **Spring Boot**, following the **CQRS (Command Query Responsibility Segregation)** pattern. It supports account operations like deposit, withdrawal, and bill payment, while keeping query responsibilities isolated.

---

## ✅ Features
- CQRS architecture with clean separation of command and query logic
- Spring Data JPA with H2 in-memory database
- Kafka integration with Outbox Pattern for reliable event publishing
- Liquibase support for schema and test data
- Unit and integration tests for both command and query layers

---

## 🚀 Tech Stack
- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Kafka
- H2 Database (in-memory)
- Liquibase
- JUnit 5
- Mockito
- Docker Compose (for Kafka setup)

---

## 🧱 Architecture Overview

### 📤 Command Side
- Responsible for modifying system state
- Includes deposit, withdraw, and bill payment operations
- Contains:
    - `BankAccountCommandController`
    - `BankAccountCommandService`
    - DTOs: `CreditRequest`, `DebitRequest`, `BillPaymentRequest`

### 📥 Query Side
- Responsible for reading account data without side effects
- Returns detailed views of account and transactions
- Contains:
    - `BankAccountQueryController`
    - `BankAccountQueryService`
    - DTOs: `AccountView`, `TransactionView`, `PagedTransactionView`

### 🔁 Outbox Pattern with Kafka

In a typical **database-per-microservice architecture**, each service has its own local database. To ensure **eventual consistency** across services (or duplicated instances), we use Kafka to propagate changes reliably.

However, writing to both the database and Kafka in the same operation introduces a risk: if Kafka is down after the database write, the event could be lost.

To solve this, we use the **Outbox Pattern**:
- Events are first written to an `outbox_event` table **in the same transaction** as the database update
- A scheduled job (relay) then reads unpublished events and safely sends them to Kafka
- Kafka consumers receive these events and **re-apply the changes** to their own local databases in an **idempotent** way

This ensures:
- High reliability
- Eventual consistency across replicated services
- Safe, decoupled event processing
- Account events (e.g. deposits, withdrawals) are written to an `outbox_event` table
- A scheduled job reads unpublished events and publishes them to Kafka
- Kafka consumer listens and applies changes idempotently

### 🧾 Domain Model
- `BankAccount`: holds account number, owner, balance
- `Transaction` (abstract) with subtypes:
    - `DepositTransaction`
    - `WithdrawalTransaction`
    - `BillPaymentTransaction`
- `@Version` field added for optimistic locking

---

## 🔁 Example API Usage (from task)

### Deposit to account
```bash
POST /account/v1/credit/669-7788
{
  "amount": 1000.0
}
```
Response:
```json
{
  "status": "OK",
  "approvalCode": "<uuid>"
}
```

### Withdraw from account
```bash
POST /account/v1/debit/669-7788
{
  "amount": 50.0
}
```
Response:
```json
{
  "status": "OK",
  "approvalCode": "<uuid>"
}
```

### Get account data
```bash
GET /account/v1/669-7788
```
Response:
```json
{
  "accountNumber": "669-7788",
  "owner": "Kerem Karaca",
  "balance": 1000.0,
  "createDate": "2020-03-26T06:15:50.550+0000",
  "transactions": []
}
```

---

## 🧪 Tests
- `BankAccountCommandServiceTest`: unit tests with mocked repository
- `BankAccountQueryServiceTest`: unit test verifying account and paginated transactions
- `BankAccountCommandIntegrationTest`: full REST test of `/credit`, `/debit`, `/paybill`
- `BankAccountQueryIntegrationTest`: test for account retrieval and paginated query

---

## 🗃️ Database & Kafka
- H2 in-memory used for runtime
- Kafka for async event-driven replication
- Liquibase changelog auto-applies schema + test data:
    - Default account:
        - accountNumber: `669-7788`
        - owner: `Kerem Karaca`
        - balance: `1000.0`

---

## 🛠️ Running the App
```bash
./gradlew bootRun
```

Visit H2 console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL: `jdbc:h2:mem:simplebanking`  
Username: `sa`, Password: *(empty)*

To run Kafka:
```bash
docker-compose up -d
```

> This will spin up Kafka and Zookeeper using the provided `docker-compose.yml` file.

---

## 📂 Package Structure
```
com.eteration.simplebanking
├── command
│   ├── controller
│   ├── service
│   └── dto
├── query
│   ├── controller
│   ├── service
│   └── dto
├── event
├── kafka
├── outbox
├── model
├── repository
└── ...
```

---

## ✅ Possible Improvements
- Retry/delay policy for outbox failure handling
- Dead letter queue for stuck events
- OpenAPI/Swagger documentation
- Profile-based config for switching between H2 and Postgres

---

## 📜 License
MIT License

