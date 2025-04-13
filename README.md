# Simple Banking System

This project implements a simple banking system using **Spring Boot**, following the **CQRS (Command Query Responsibility Segregation)** pattern. It supports account operations like deposit, withdrawal, and bill payment, while keeping query responsibilities isolated.

---

## ✅ Features
- CQRS architecture with clean separation of command and query logic
- Spring Data JPA with H2 in-memory database
- Liquibase integration for schema and test data
- Unit and integration tests for both command and query layers

---

## 🚀 Tech Stack
- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database (in-memory)
- Liquibase
- JUnit 5
- Mockito

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
    - DTOs: `AccountView`, `TransactionView`

### 🧾 Domain Model
- `BankAccount`: holds account number, owner, balance
- `Transaction` (abstract) with subtypes:
    - `DepositTransaction`
    - `WithdrawalTransaction`
    - `BillPaymentTransaction`
- `@Version` field added for optimistic locking

---

## 🔁 Example API Usage

### Deposit to account
```bash
POST /account/v1/credit/12345
{
  "amount": 1000.0
}
```

### Withdraw from account
```bash
POST /account/v1/debit/12345
{
  "amount": 50.0
}
```

### Pay bill
```bash
POST /account/v1/paybill/12345
{
  "payee": "Netflix",
  "amount": 96.5
}
```

### Get account data
```bash
GET /account/v1/12345
```

---

## 🧪 Tests
- `BankAccountCommandServiceTest`: unit test with mocked repo
- `BankAccountQueryServiceTest`: unit test with DTO verification
- `BankAccountCommandIntegrationTest`: integration test for HTTP endpoints
- `BankAccountQueryIntegrationTest`: integration test for GET endpoint

---

## 🗃️ Database
- H2 in-memory used for runtime
- Liquibase changelog auto-applies schema + seed data
    - Default account:
        - accountNumber: `12345`
        - owner: `Jim`
        - balance: `1000.0`

---

## 🛠️ Running the App
```bash
./gradlew bootRun
```

Visit H2 console: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL: `jdbc:h2:mem:simplebanking`  
Username: `sa`, Password: *(empty)*

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
├── model
├── repository
└── ...
```

---

## ✅ Possible Improvements
- Add REST exception handler (`@ControllerAdvice`)
- Add pagination to transaction query
- Add Swagger/OpenAPI docs
- Export account history as PDF/CSV

---

## 📜 License
MIT License