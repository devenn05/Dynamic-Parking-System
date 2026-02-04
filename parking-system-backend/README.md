# Dynamic Pricing System for Parking Lots (Backend)

## 1. Project Overview

This project is the backend service for a **Dynamic Pricing System for Parking Lots**, built using **Java Spring Boot**. It provides a robust, rule-based REST API to manage the entire lifecycle of a parking facility.

The core focus is on clean architecture and correct enforcement of business logic. Key features include:
-   Creating, Updating, and managing multi-slot parking lots.
-   Handling vehicleEntity entry and exit operations.
-   Tracking real-time parking occupancy.
-   Dynamically calculating parking charges based on duration and occupancy.
-   Administrative tools to Force-Terminate stuck sessions.

This service is designed to be consumed by the accompanying Angular frontend.

## 2. Tech Stack

-   **Java 17+**
-   **Spring Boot 3.5.9** 
-   **MySQL Database**

## 3. High-Level Architecture

The backend follows a stateless Controller-Service-Repository pattern:
`Controller (REST) -> Service (Business Rules) -> Repository (DB Access)`

## 4. Pricing Logic Explanation

#### Base Rules
1.  **First 30 minutes are free.**
2.  **Hourly Billing:** After the free window, charges are calculated per hour (rounded up).
3.  **Surge Pricing:** Multiplier applied based on lot occupancy at the moment of exit.
    *  > Less than 50%: 1.0x
    *  > Between 51 - 80%: 1.25x
    *  > Above 80%: 1.5x

`finalAmount = (billableHours) * (basePricePerHour) * (occupancyMultiplier)`

## 5. API List (Endpoints)

#### Parking Lot APIs

| Method | Endpoint                       | Description                                                   |
| ------ | ------------------------------ |---------------------------------------------------------------|
| `POST` | `/api/parking-lots`            | Create a new parking lot.                                     |
| `GET`  | `/api/parking-lots`            | Retrieve all lots with live availability.                     |
| `PUT`  | `/api/parking-lots/{id}`       | Update lot details. (Supports increasing slot capacity only). |
| `GET`  | `/api/parking-lots/{id}/slots` | View physical slot status (Grid).                             |

#### Operations

| Method | Endpoint             | Description                                      |
| ------ | -------------------- | ------------------------------------------------ |
| `POST` | `/api/parking/entry` | Vehicle Entry (Issue Ticket).                    |
| `POST` | `/api/parking/exit`  | Vehicle Exit (Generate Bill).                    |

#### Session Management

| Method | Endpoint                        | Description                               |
| ------ | ------------------------------- | ----------------------------------------- |
| `GET`  | `/api/sessions/active`          | Active vehicles (can filter by `lotId`).  |
| `GET`  | `/api/sessions/history`         | All vehicleEntity logs (can filter by `lotId`). |
| `POST` | `/api/sessions/{id}/terminate`  | Force-close a session (Price set to 0.0). |


---

## 6. How to Run

1.  Clone repository.
2.  Ensure MySQL is running and configured in `application.properties`.
3.  Run: `mvn spring-boot:run`
