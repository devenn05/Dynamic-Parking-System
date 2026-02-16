# 🅿️ Parking System - Main Backend Service

This is the **Command & Control** center of the application. It hosts the REST API that handles all business logic, data persistence, and transaction management. It serves as the "Producer" in our Event-Driven Architecture.

---

## 🏗️ Architecture & Features

This service follows a layered architecture (**Controller → Service → Repository**) and enforces strict data consistency rules.

### Core Capabilities
*   **Lot Management:** Create and configure parking facilities (Capacity, Base Price).
*   **Concurrency Control:**
    *   **Entry:** Uses **Pessimistic Locking** (`SELECT ... FOR UPDATE`) on slots to ensure no two vehicles are assigned the same spot simultaneously.
    *   **Exit:** Uses **Optimistic Locking** (`@Version`) to prevent double-billing on simultaneous exit requests.
*   **Dynamic Billing Engine:** Calculates fees based on complex rules involving duration and live occupancy.
*   **Event Production:** Publishes state changes (`LOT_UPDATED`, `SESSION_CREATED`, `SESSION_ENDED`) to **Apache Kafka**.

---

## 💰 Pricing Logic (The Algorithm)

The system calculates parking fees using the following formula:

1.  **Grace Period:** The first **30 minutes** are free ($0.00).
2.  **Hourly Rate:** Time is rounded **UP** to the nearest hour (e.g., 61 mins = 2 hours).
3.  **Surge Multiplier:** The rate increases based on the lot's occupancy *at the time of exit*:

| Occupancy % | Multiplier |
| :--- | :--- |
| **0% - 50%** | **1.0x** (Standard) |
| **51% - 80%** | **1.25x** (Medium Demand) |
| **81% - 100%** | **1.5x** (High Demand) |

**Formula:** `Final Bill = (Hours * Base Price) * Multiplier`

---

## 🔌 API Endpoints

### 1. Parking Lots
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/parking-lots` | Retrieve all lots and current status. |
| `POST` | `/api/parking-lots` | Create a new parking facility. |
| `PUT` | `/api/parking-lots/{id}` | Update details (Capacity can only increase). |
| `DELETE` | `/api/parking-lots/{id}` | Delete a lot (Only if empty). |

### 2. Operations (Entry/Exit)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/parking/entry` | Validates vehicle, locks slot, returns Ticket. |
| `POST` | `/api/parking/exit` | Ends session, calculates bill, releases slot. |

### 3. Sessions & History
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/sessions/active` | List vehicles currently parked. |
| `GET` | `/api/sessions/history` | Full audit log of all parking events. |
| `POST` | `/api/sessions/{id}/terminate` | **Admin:** Force-close a stuck session ($0 bill). |

---

## ⚙️ Configuration (`application.properties`)

To run this locally, you must configure your database and Kafka broker in `src/main/resources/application.properties`.

```properties
# Server Port
server.port=8080

# MySQL Configuration (Local or Aiven Cloud)
spring.datasource.url=jdbc:mysql://<YOUR_DB_HOST>:<PORT>/parking_db
spring.datasource.username=<YOUR_USER>
spring.datasource.password=<YOUR_PASSWORD>

# Kafka Producer Configuration (Local or Confluent Cloud)
spring.kafka.bootstrap-servers=<YOUR_KAFKA_BROKER_URL>
spring.kafka.properties.security.protocol=SASL_SSL
spring.kafka.properties.sasl.mechanism=PLAIN
spring.kafka.properties.sasl.jaas.config=...

# Hibernate Settings
spring.jpa.hibernate.ddl-auto=update

# CORS Configuration (Reflects your Frontend URL)
app.frontend.url=http://localhost:4200

## 6. How to Run

1.  Clone repository.
2.  Ensure MySQL is running and configured in `application.properties`.
3.  Running instance of MySQL and Kafka.
4.  Run: `mvn spring-boot:run`