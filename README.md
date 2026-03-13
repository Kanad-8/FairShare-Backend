<div align="center">

# 💸 FairShare — Backend API

**A RESTful debt-simplification engine for shared expenses**

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6DB33F?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![JWT](https://img.shields.io/badge/Auth-JWT-000000?style=for-the-badge&logo=jsonwebtokens)](https://jwt.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

[Features](#-features) · [Architecture](#-architecture) · [API Reference](#-api-reference) · [Getting Started](#-getting-started) · [Database Schema](#-database-schema)

</div>

---

## 🧠 The Problem

When groups of people — roommates, travel buddies, or colleagues — share costs, tracking who owes whom becomes a logistical nightmare fast.

Say Person A pays for dinner, B pays for the hotel, and C pays for the rental car. A naive system creates a tangled web of overlapping micro-transactions. Even worse: **cyclical debts**.

> If A owes B ₹100 · B owes C ₹100 · C owes A ₹100 — three separate bank transfers are demanded for debts that perfectly cancel each other out.

**FairShare solves this.**

---

## ✨ Features

- 🔐 **Secure Auth** — JWT-based stateless authentication with encrypted passwords
- 👥 **Group Management** — Create expense-sharing groups and manage members
- 🧾 **Flexible Expense Splitting** — Split costs equally, by exact amounts, or by percentages
- 🤖 **Debt Simplification Engine** — Greedy algorithm that calculates the *minimum number of transactions* to settle all debts
- 🐳 **Fully Containerized** — Docker Compose spins up the entire stack in one command
- 📐 **Clean Architecture** — Strict Controller → Service → Repository layering

---

## 🏗️ Architecture

### System Design Principles

#### Strategy Pattern — Flexible Expense Splitting

The `SplitStrategyFactory` selects the correct splitting algorithm at runtime based on the `splitType` field (`EQUAL`, `EXACT`, `PERCENTAGE`). Each strategy is an isolated class implementing the `SplitStrategy` interface.

> **Why?** This follows the **Open/Closed Principle** (SOLID). Adding a new split type (e.g., "Split by Shares") means creating one new class — zero changes to existing business logic.

#### Greedy Algorithm — Debt Simplification

`DebtSimplificationService` solves the classic **Minimum Cash Flow** problem:

1. Computes each user's **net balance** across all expenses and settlements in the group
2. Iteratively matches the **largest debtor** with the **largest creditor**
3. Returns the absolute minimum list of transactions needed to settle all debts

> **Why?** A naive system requires one transfer per debt edge in the graph. The greedy approach collapses the entire web of IOUs into the fewest possible payments, eliminating cyclical debts entirely.

#### Layered Architecture — Separation of Concerns

```
HTTP Request → Controller (routing + parsing)
                    ↓
              Service (business logic + algorithms)
                    ↓
            Repository (database access via JPA)
```

#### Stateless Architecture — Horizontal Scalability

JWT tokens carry all authentication state client-side. The server holds no session memory, meaning you can run any number of Spring Boot container instances behind a load balancer without session affinity.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Database | PostgreSQL 16 |
| ORM | Spring Data JPA (JPQL) |
| Security | Spring Security + Custom JWT |
| Containerization | Docker & Docker Compose |
| Secret Management | `.env` environment variables |

---

## 📡 API Reference

**Base URL:** `http://localhost:8080`  
**Total Endpoints:** 20 across 5 controllers  
**Auth:** All endpoints (except `/api/auth/**`) require `Authorization: Bearer <token>`

---

### 🔑 Authentication — `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/signup` | Register a new user |
| `POST` | `/api/auth/signin` | Authenticate and receive a JWT |

<details>
<summary>Signup — Request Body</summary>

```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```
</details>

<details>
<summary>Signin — Response</summary>

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```
</details>

---

### 👤 User Management — `/api/user`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/user/me` | Get current user's profile |
| `PUT` | `/api/user/me` | Update current user's profile |
| `DELETE` | `/api/user/me` | Delete current user's account |
| `GET` | `/api/user` | Fetch all registered users |
| `GET` | `/api/user/group/{groupId}` | Get all members of a group |

---

### 👥 Group Management — `/api/group`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/group` | Create a new group |
| `GET` | `/api/group` | Fetch all groups |
| `GET` | `/api/group/{id}` | Get a specific group |
| `PUT` | `/api/group/{id}` | Update a group |
| `DELETE` | `/api/group/{id}` | Delete a group |
| `POST` | `/api/group/{groupId}/members` | Add members (array of user IDs) |

---

### 🧾 Expense Tracking — `/api/expense`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/expense` | Log a new expense with split config |
| `GET` | `/api/expense/group/{groupId}` | Get all expenses in a group |
| `GET` | `/api/expense/{id}` | Get a specific expense |
| `PUT` | `/api/expense/{id}` | Update an expense |
| `DELETE` | `/api/expense/{id}` | Delete an expense |

<details>
<summary>Create Expense — Request Body</summary>

```json
{
  "description": "Goa Hotel",
  "amount": 6000.00,
  "groupId": 1,
  "expenseDate": "2025-07-10",
  "splitType": "EQUAL",
  "splits": [
    { "userId": 1 },
    { "userId": 2 },
    { "userId": 3 }
  ]
}
```

> `splitType` accepts: `EQUAL` · `EXACT` · `PERCENTAGE`  
> For `EXACT` / `PERCENTAGE`, provide an `amount` / `percentage` field per split entry.
</details>

---

### 💰 Debt Settlement — `/api/settlements`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/settlements` | Record a manual payment |
| `GET` | `/api/settlements/group/{groupId}` | View all settlements in a group |
| `GET` | `/api/settlements/{id}` | Get a specific settlement |
| `PUT` | `/api/settlements/{id}` | Update a settlement |
| `DELETE` | `/api/settlements/{id}` | Delete a settlement |
| ⭐ `GET` | `/api/settlements/group/{groupId}/suggested` | **Get optimized settlement plan** |

#### ⭐ The Magic Endpoint

`GET /api/settlements/group/{groupId}/suggested`

Triggers the **Greedy Debt Simplification Algorithm**. Returns the minimum set of transactions to make everyone whole.

<details>
<summary>Sample Response</summary>

```json
[
  {
    "from": "Alice",
    "to": "Bob",
    "amount": 1500.00
  },
  {
    "from": "Charlie",
    "to": "Alice",
    "amount": 800.00
  }
]
```

A group of 6 people with 15 overlapping debts might settle everything with just 5 transactions.
</details>

---

## 🚀 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/get-started) & Docker Compose
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/Kanad-8/FairShare-Backend.git
cd FairShare-Backend
```

### 2. Configure Environment Variables

Create a `.env` file in the project root:

```env
# Database
POSTGRES_DB=fairshare
POSTGRES_USER=your_db_user
POSTGRES_PASSWORD=your_db_password

# Spring Datasource
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/fairshare
SPRING_DATASOURCE_USERNAME=your_db_user
SPRING_DATASOURCE_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_super_secret_key_here
JWT_EXPIRATION_MS=86400000
```

> ⚠️ Never commit your `.env` file. It is already listed in `.gitignore`.

### 3. Build and Run

```bash
docker compose up --build
```

The API will be live at `http://localhost:8080`.

### 4. Verify

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'
```

---

## 🗄️ Database Schema

```
┌─────────────┐        ┌───────────────┐        ┌──────────────┐
│    users    │◄──────►│ group_members │◄──────►│    groups    │
│─────────────│        └───────────────┘        │──────────────│
│ user_id  PK │                                  │ group_id  PK │
│ username    │                                  │ name         │
│ email       │                                  │ created_at   │
│ password_hash│                                 └──────┬───────┘
│ created_at  │                                         │
└──────┬──────┘                                         │
       │                                                │
       │         ┌──────────────────┐                   │
       └────────►│    expenses      │◄──────────────────┘
                 │──────────────────│
                 │ expense_id    PK │
                 │ description      │
                 │ amount           │
                 │ paid_user     FK │
                 │ group_id      FK │
                 │ expense_date     │
                 └────────┬─────────┘
                          │
                          ▼
                 ┌──────────────────┐       ┌───────────────────┐
                 │     splits       │       │    settlements    │
                 │──────────────────│       │───────────────────│
                 │ split_id      PK │       │ settlement_id  PK │
                 │ amount           │       │ amount            │
                 │ user_id       FK │       │ group_id       FK │
                 │ expense_id    FK │       │ paid_by_user_id FK│
                 └──────────────────┘       │ paid_to_user_id FK│
                                            │ created_at        │
                                            └───────────────────┘
```

<details>
<summary>Full SQL Schema</summary>

```sql
-- 1. Users
CREATE TABLE users (
    user_id       BIGSERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP    NOT NULL
);

-- 2. Groups
CREATE TABLE groups (
    group_id   BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    created_at TIMESTAMP
);

-- 3. Group Members (Many-to-Many)
CREATE TABLE group_members (
    group_id BIGINT NOT NULL,
    user_id  BIGINT NOT NULL,
    PRIMARY KEY (group_id, user_id),
    CONSTRAINT fk_group_members_group FOREIGN KEY (group_id) REFERENCES groups(group_id),
    CONSTRAINT fk_group_members_user  FOREIGN KEY (user_id)  REFERENCES users(user_id)
);

-- 4. Expenses
CREATE TABLE expenses (
    expense_id   BIGSERIAL PRIMARY KEY,
    description  VARCHAR(255),
    amount       NUMERIC(38, 2),
    paid_user    BIGINT,
    group_id     BIGINT,
    expense_date DATE      NOT NULL,
    created_at   TIMESTAMP NOT NULL,
    CONSTRAINT fk_expense_paid_user FOREIGN KEY (paid_user) REFERENCES users(user_id),
    CONSTRAINT fk_expense_group     FOREIGN KEY (group_id)  REFERENCES groups(group_id)
);

-- 5. Splits
CREATE TABLE splits (
    split_id   BIGSERIAL PRIMARY KEY,
    amount     NUMERIC(38, 2) NOT NULL,
    user_id    BIGINT         NOT NULL,
    expense_id BIGINT         NOT NULL,
    CONSTRAINT fk_split_user    FOREIGN KEY (user_id)    REFERENCES users(user_id),
    CONSTRAINT fk_split_expense FOREIGN KEY (expense_id) REFERENCES expenses(expense_id)
);

-- 6. Settlements
CREATE TABLE settlements (
    settlement_id    BIGSERIAL PRIMARY KEY,
    amount           NUMERIC(38, 2) NOT NULL,
    group_id         BIGINT         NOT NULL,
    paid_by_user_id  BIGINT         NOT NULL,
    paid_to_user_id  BIGINT         NOT NULL,
    created_at       TIMESTAMP      NOT NULL,
    CONSTRAINT fk_settlement_group    FOREIGN KEY (group_id)        REFERENCES groups(group_id),
    CONSTRAINT fk_settlement_paid_by  FOREIGN KEY (paid_by_user_id) REFERENCES users(user_id),
    CONSTRAINT fk_settlement_paid_to  FOREIGN KEY (paid_to_user_id) REFERENCES users(user_id)
);
```
</details>

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

<div align="center">

Built with ☕ Java and a love for clean architecture.

**[⬆ Back to top](#-fairshare--backend-api)**

</div>
