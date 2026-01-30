# Dynamic Pricing System for Parking Lots (Backend)

## 1. Project Overview

This project is the backend service for a **Dynamic Pricing System for Parking Lots**, built using **Java Spring Boot**. It provides a robust, rule-based REST API to manage the entire lifecycle of a parking facility.

The core focus is on clean architecture and correct enforcement of business logic. Key features include:
-   Creating and managing multi-slot parking lots.
-   Handling vehicle entry and exit operations.
-   Tracking real-time parking occupancy.
-   Dynamically calculating parking charges based on **parking duration** and **current occupancy**.
-   Proper data validation and robust error handling.

This service is designed to be consumed by the accompanying Angular frontend.

---

## 2. Tech Stack

-   **Java 17+**
-   **Spring Boot 3.5.9**
-   **Spring Data JPA:** For database interaction.
-   **MySQL Database.**
-   **Maven:** For dependency management and build.
-   **Lombok:** To reduce boilerplate code.

---

## 3. High-Level Architecture

The backend follows a classic layered architecture, ensuring separation of concerns:

`Controller (API Layer) -> Service (Business Logic) -> Repository (Data Access)`

This structure is stateless and exposes a set of RESTful endpoints. The flow is as follows:

1.  An **Angular UI** sends an HTTP request.
2.  A **`@RestController`** receives the request, validates the input DTO, and calls the appropriate service method.
3.  A **`@Service`** class contains the core business logic, orchestrating calls to repositories and other services.
4.  A **`@Repository`** (JPA Repository interface) executes database queries to persist or retrieve entities.
5.  The **Database** stores the application state.

---

## 4. Pricing Logic Explanation

The dynamic pricing model is based on two factors: parking duration and lot occupancy.

#### Base Rules

1.  **First 30 minutes are free:** Any vehicle exiting within 30 minutes of entry is charged ₹0.
2.  **Minimum Billing Unit:** After the initial 30 minutes, charges are calculated per **hour**. Any fraction of an hour is rounded up to the next full hour (e.g., 61 minutes of billable time is charged as 2 hours).

#### Occupancy-Based Multiplier

A price multiplier is applied based on how full the parking lot is at the **time of vehicle exit**. This encourages parking during off-peak hours.

| Occupancy Percentage | Price Multiplier |
| -------------------- | ---------------- |
| ≤ 50%                | 1.0x             |
| 51% - 80%            | 1.25x            |
| > 80%                | 1.5x             |

#### Pricing Formula

`finalAmount = (billableHours) * (basePricePerHour) * (occupancyMultiplier)`

---

## 5. API List (Endpoints)

#### Parking Lot APIs

| Method | Endpoint                    | Description                                  |
| ------ | --------------------------- | -------------------------------------------- |
| `POST` | `/api/parking-lots`         | Creates a new parking lot and its slots.     |
| `GET`  | `/api/parking-lots`         | Retrieves all parking lots with live stats.  |
| `GET`  | `/api/parking-lots/{id}`    | Retrieves a single parking lot by its ID.    |
| `GET`  | `/api/parking-lots/{id}/slots` | Retrieves all slots for a specific lot. |

#### Vehicle Entry & Exit APIs

| Method | Endpoint                | Description                                        |
| ------ | ----------------------- | -------------------------------------------------- |
| `POST` | `/api/parking/entry`    | Registers a vehicle's entry and issues a ticket.   |
| `POST` | `/api/parking/exit`     | Registers a vehicle's exit and generates a bill.   |

#### Parking Session APIs

| Method | Endpoint                | Description                                                              |
| ------ | ----------------------- | ------------------------------------------------------------------------ |
| `GET`  | `/api/sessions/active`  | Retrieves all vehicles currently parked (active sessions). Can be filtered by `lotId`. |
| `GET`  | `/api/sessions/history` | Retrieves a complete log of all sessions (active + completed). Can be filtered by `lotId`. |

---

## 6.1 Assumptions Made in Project

1.  **Concurrency:** Pessimistic locking (`LockModeType.PESSIMISTIC_WRITE`) is used at the database level on critical read operations (like finding an available slot). This prevents race conditions where two vehicles might be assigned the same slot simultaneously.
2.  **Time Zone:** The application operates under a single, server-defined time zone. Timestamps are stored and processed accordingly.
3. **Project Assumption** : 1) Occupancy is calculated before freeing the slot at exit 2) Exit is blocked if attempted within the same minute of entry 3) Billing amount is immutable after exit
---
## 6.1 Assumptions Made in Documentation

- Parking charges are calculated at the time of vehicle exit, as specified in the assignment.
- The first 30 minutes of parking are free; billing starts after that period.
- After the free window, parking is billed per hour or part thereof.
- A vehicle can have only one active parking session at a time.
- Parking slots are assigned on a first-available basis.
- Vehicle number uniquely identifies a vehicle.
- Entry and exit timestamps are recorded using system time.
- Payment processing is out of scope; only bill calculation is handled.

---
    ```

4.  The application will start, and the REST API will be available at `http://localhost:8080`.
