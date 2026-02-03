# Dynamic Pricing System for Parking Lots

![Project Status](https://img.shields.io/badge/status-complete-green)

This repository contains the full-stack source code for a **Dynamic Pricing System for Parking Lots**. The system manages parking facilities, vehicle entry/exit, and dynamically calculates charges based on parking duration and real-time lot occupancy.

The project is structured as a monorepo, containing:
-   A **Backend** service built with Java Spring Boot.
-   A **Frontend** single-page application built with Angular.

---

## Core Features

-   **Lot Management:** Create, view, and update parking facilities with defined capacity and pricing.
-   **Slot Management:** Automatically generate and track the status (`AVAILABLE`/`OCCUPIED`) of each slot.
-   **High-Concurrency Operations:**
    -   Safely handle simultaneous vehicle entries using **Pessimistic Locking** to prevent slot assignment race conditions.
    -   Safely handle simultaneous exit attempts for the same vehicle using **Optimistic Locking** to prevent data corruption.
-   **Dynamic Pricing Engine:** Calculates bills using a formula that incorporates a free initial period, hourly rates, and a surge multiplier based on current lot occupancy.
-   **Live Dashboard:** View currently parked vehicles and a complete history of all past parking sessions with powerful search and filtering capabilities.
-   **Administrative Tools:** Manually terminate "stuck" sessions to free up slots.
-   **Dark Mode:** A sleek, user-friendly dark theme that persists across sessions.
---

## Tech Stack

| Component | Technology                                             |
| :-------- | :----------------------------------------------------- |
| **Backend**  | Java 24, Spring Boot 3.5.9, Spring Data JPA, Maven  |
| **Frontend** | Angular 21.1.0, TypeScript, HTML/CSS                |
| **Database** | MySQL                                               |
| **API**      | RESTful API                                         |

---

## High-Level Architecture

The application follows a simple, robust client-server architecture.

```
+--------------------+      HTTP/S      +-------------------------+      JPA       +--------------+
|                    |                  |                         |                |              |
|  Angular Frontend  | <--------------->|    Spring Boot Backend  | <------------->|   Database   |
|  (Client Browser)  |     (REST API)   |       (Java Service)    |                |   (MySQL)    |
|                    |                  |                         |                |              |
+--------------------+                  +-------------------------+                +--------------+

```
-   The **Angular Frontend** provides the user interface for all operations.
-   The **Spring Boot Backend** exposes a REST API, contains all the business logic, and manages data persistence.
-   The **Database** stores the state of lots, slots, vehicles, and sessions.

---

## Repository Structure

The project is organized into two main sub-directories:

```
.
├── parking-system-backend/          # Contains the Java Spring Boot Application
│   └── README.md                    # Backend-specific documentation
├── parking-system-frontend/         # Contains the Angular Application
│   └── README.md                    # Frontend-specific documentation
└── README.md                        # This file (Project Overview)
```
---
## 6. Assumptions
# Backend

* **Unique Identity**: A Vehicle Number (License Plate) is the unique identifier for a vehicle. Two different physical vehicles cannot have the same number.
* **Single Active Session**: A specific vehicle can only have one **ACTIVE** parking session at a time. It cannot enter a second lot if it hasn't exited the first.
* **Slot Allocation Strategy**: Slots are assigned on a **First-Available-Basis** (e.g., Slot 1, then Slot 2). Drivers cannot choose specific slot numbers manually.
* **Uniform Slot Size**: For this version, we assume all slots in a specific Parking Lot are of uniform size and can accommodate the accepted vehicle types (**CAR**/**BIKE**).
* **Entry/Exit Atomicity**: A vehicle is considered "Entered" only when a Ticket is successfully generated. It is considered "Exited" only when a Bill is generated.
* **Base Price Fluctuation**: If the Base Price of a lot is changed by an admin while a car is parked, the new price is applied at the time of exit (**Dynamic Pricing**).
* **Occupancy Multiplier**: The "Surge Pricing" multiplier (**1.0x**, **1.25x**, **1.5x**) is determined based on the lot's occupancy at the exact moment of exit, not the average occupancy during the stay.
* **Free Tier**: The first 30 minutes are strictly free (₹0 bill). Even 30 minutes and 1 second triggers billing.
* **Rounding Logic**: After the free tier, billing is done per hour or part thereof (e.g., 61 minutes = 2 hours billed).
* **Slot Scaling**: When updating a Parking Lot, the total number of slots can only be increased, not decreased. This prevents the accidental deletion of slots that might currently be occupied.
* **Input Normalization**: The system assumes vehicle numbers may be entered with spaces or lowercase letters but processes them as **Uppercased** and **Stripped** of Special Characters (e.g., "mh 12" -> "MH12").
* **Vehicle Types**: Only **CAR** and **BIKE** are currently supported. Adding a truck or bus would require code changes.
* **Data Persistence**: History is never deleted. Even after a vehicle exits, the `ParkingSession` record remains in the database with status **COMPLETED** for audit purposes.
* **Pessimistic Locking (Entry)**: We assume high contention for slots. **Pessimistic Write Locks** are used during entry to ensure two vehicles never grab the same slot ID simultaneously.
* **Optimistic Locking (Exit)**: We assume valid exits are the norm. **Optimistic Locking** (`@Version`) is used to prevent "Double Billing" if two exit requests for the same car hit the server simultaneously.
* **System Time**: The application relies on the **Server's Local Time** for all calculations. It assumes the database and application server are time-synced.
* **Database Availability**: The system assumes a continuous connection to the **MySQL** database. No offline-mode or local caching logic is implemented.
* **Sequential IDs**: Slot numbers are generated sequentially (**1 to N**) based on the total capacity configured.
* **Update Integrity**: An Admin cannot change the ID or Location of a Parking Lot once created, only its Name, Price, and Capacity (Upwards).

# Frontend
* **Backend Dependency**: The frontend assumes the Spring Boot backend is always running and accessible at `http://localhost:8080`. There is no offline mode or caching mechanism for API failures.
* **Single-operator Interface**: The UI is designed as a single-operator dashboard. It assumes the same person handles both Entry and Exit operations, which is why both forms are displayed side-by-side on the main screen.
* **Client-Side Filtering**: The "Search" and "Date Filter" features on the **Sessions** page perform filtering in the browser memory. It assumes the dataset size (number of parking sessions) is manageable for a web browser (e.g., < 10,000 records) and does not require server-side pagination for search.
* **No Authentication**: The frontend assumes a trusted environment (e.g., an internal company network). There is no Login/Logout functionality; the application loads directly into the operational dashboard.
* **Input Sanitation**: The UI assumes users might input messy data (lowercase, spaces, special chars). It proactively sanitizes vehicle numbers (auto-uppercasing, removing non-alphanumeric chars) **before** sending them to the backend.
* **Timezone**: All timestamps received from the backend (UTC or Server Time) are displayed using the **Browser's Local Timezone**.
* **Visual Feedback**: The application assumes that users prefer ephemeral feedback. Success messages (Tickets/Bills) and Error messages automatically disappear after a few seconds to keep the interface clean for the next transaction.
* **Styling Philosophy**: The project assumes a "Pure CSS" approach is preferred over heavy UI libraries (like Angular Material or Bootstrap) to keep the bundle size small and demonstrate core CSS skills.
---

## Getting Started

Follow these instructions to get both the backend and frontend applications running on your local machine.

### Prerequisites

You must have the following software installed:
-   **Git:** For cloning the repository.
-   **Java Development Kit (JDK):** Version 17 or later.
-   **Apache Maven:** For building and running the backend.
-   **Node.js:** Latest LTS version is recommended.
-   **Angular CLI:** Install globally using `npm install -g @angular/cli`.

### Installation & Running

#### 1. Clone the Repository
Clone this repository to your local machine.

```sh
git clone  https://github.com/devenn05/Dynamic-Parking-System.git
cd dynamic-parking-system
```

#### 2. Run the Backend Server
First, start the Spring Boot backend, which will run on `http://localhost:8080`.

```sh
# Navigate to the backend directory
cd parking-system-backend

# Run the application using Maven
mvn spring-boot:run
```
Leave this terminal running. The backend is now ready to accept API requests.

#### 3. Run the Frontend Application
Open a **new terminal window** and start the Angular frontend, which will run on `http://localhost:4200`.

```sh
# Navigate to the frontend directory from the project root
cd parking-system-frontend

# Start the Angular development server
ng serve
```
Leave this terminal running as well.

#### 4. Access the Application
Open your web browser and navigate to:

**http://localhost:4200**

You should see the parking management system interface, fully connected and ready to use!

---

## Detailed Documentation

For more specific details about each part of the project, please refer to their individual README files:

-   **Backend Documentation:** [**./backend/README.md**](./backend/README.md)
    -   (Includes API endpoint list, pricing logic in detail, and backend setup.)
-   **Frontend Documentation:** [**./frontend/README.md**](./frontend/README.md)
    -   (Includes descriptions of screens, components, and frontend setup.)
