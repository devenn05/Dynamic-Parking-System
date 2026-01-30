# Dynamic Pricing System for Parking Lots

![Project Status](https://img.shields.io/badge/status-complete-green)

This repository contains the full-stack source code for a **Dynamic Pricing System for Parking Lots**. The system manages parking facilities, vehicle entry/exit, and dynamically calculates charges based on parking duration and real-time lot occupancy.

The project is structured as a monorepo, containing:
-   A **Backend** service built with Java Spring Boot.
-   A **Frontend** single-page application built with Angular.

---

## Core Features

-   **Manage Parking Lots:** Create and view parking facilities with defined capacity and pricing.
-   **Manage Parking Slots:** Automatically generate and track the status (`AVAILABLE`/`OCCUPIED`) of each slot.
-   **Vehicle Entry & Exit:** Handle the core operational flow of vehicles entering and leaving the lots.
-   **Track Occupancy:** Provides a real-time view of how many slots are available in each lot.
-   **Dynamic Pricing:** Calculates the final bill using a formula that incorporates both parking duration and a surge multiplier based on current lot occupancy.
-   **Session History:** View currently parked vehicles and a complete history of all past parking sessions.

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

# Install dependencies
npm install

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
