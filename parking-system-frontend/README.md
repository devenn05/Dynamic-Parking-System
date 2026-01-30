# Dynamic Pricing System for Parking Lots (Frontend)

## 1. Project Overview

This is a functional, minimal-UI Angular application that serves as the frontend for the **Dynamic Pricing System for Parking Lots**. It interacts with the backend via REST APIs to provide a simple and effective user interface for managing parking operations.

As per the requirements, the focus is on functionality and clarity over complex styling. The application is built as a Single Page Application (SPA).

---

## 2. Tech Stack

-   **Angular 21.1.2**
-   **TypeScript**
-   **HTML**
-   **CSS** (No heavy UI frameworks)
-   **Angular CLI**

---

## 3. Screens & Features

The UI is divided into three main screens, accessible via the top navigation bar.

#### 1. Entry/Exit (`/operations`)
This is the default and primary screen for daily operations.
-   **Vehicle Entry:** A form to input a vehicle's number and type, and select a parking lot. On successful entry, a **Ticket** is displayed showing the assigned slot number.
-   **Vehicle Exit:** A form to input a vehicle's number. On successful exit, a **Bill** is displayed with the total duration and calculated fee.
-   Error messages from the backend (e.g., "Lot is full", "Vehicle not found") are displayed clearly.

#### 2. Manage Lots (`/lots`)
This screen serves as the administrative panel for managing parking facilities.
-   **Create Lot:** A form to define a new parking lot with its name, location, total capacity, and base hourly price.
-   **Lot List:** A table that displays all existing parking lots, including their real-time occupancy (`Available Slots / Total Slots`).

#### 3. Sessions (`/sessions`)
This screen is a dashboard for viewing vehicle data.
-   **Tabs:** Allows toggling between `Current Active Vehicles` and `History (All)`.
-   **Filter:** A dropdown menu to filter the data for a specific parking lot or show all lots.
-   **Active Table:** Shows details of vehicles currently inside a lot.
-   **History Table:** Provides a complete log of all sessions (both active and completed), showing entry/exit times and final bill amounts.

---

## 4. Assumptions Made

1.  **Backend Availability:** The application expects the Spring Boot backend API to be running and accessible at `http://localhost:8080`.
2.  **State Management:** The application uses component-level state and Angular Signals. No complex global state management library (like NgRx or Akita) is needed due to the simple nature of the UI.
3.  **Styling:** All styling is done with basic, clean CSS as specified. No component libraries like Angular Material or Bootstrap are used.

---

## 5. How to Run the Application

#### Prerequisites

-   Node.js (latest LTS version recommended).
-   Angular CLI installed globally: `npm install -g @angular/cli`.

#### Steps

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    ```

2.  **Navigate to the frontend project directory:**
    ```sh
    cd path/to/frontend/parking-system-frontend
    ```

3.  **Install project dependencies:**
    ```sh
    npm install
    ```

4.  **Run the development server:**
    ```sh
    ng serve
    ```

5.  Open your browser and navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.