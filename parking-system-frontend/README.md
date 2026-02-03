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

#### 3. Sessions Dashboard (`/sessions`)
A powerful table view for tracking vehicles.
*   **Tabs:** Switch between 'Current Active' and 'History'.
*   **Filtering:**
    *   **By Lot:** Dropdown to specific facility.
    *   **Search:** Real-time search by Vehicle Number.
    *   **Date:** Filter history by specific date.
*   **Emergency Action:** The **"Terminate"** button allows admins to forcibly clear a vehicle from the system (frees up the slot) if manual override is required.

#### 4. Global Features
*   **Dark Mode:** Toggle switch in the navigation bar persists user preference in LocalStorage.

---

## 4. Assumptions Made

1.  **Backend Availability:** The application expects the Spring Boot backend API to be running and accessible at `http://localhost:8080`.
2.  **State Management:** The application uses component-level state and Angular Signals. No complex global state management library (like NgRx or Akita) is needed due to the simple nature of the UI.
3.  **Styling:** All styling is done with basic, clean CSS as specified. No component libraries like Angular Material or Bootstrap are used.
4. **Input Formatting:** The Entry/Exit fields automatically uppercase input and remove invalid special characters.
5. **Client-Side Filtering:** The vehicle search and date filter on the Sessions page are processed client-side for instant feedback.
6.  **Editing Lots:** The UI restricts updating a lot's capacity to a lower number than currently exists, based on backend validation rules.

---

## 5. How to Run the Application

#### Prerequisites

-   Node.js (latest LTS version recommended).
-   Angular CLI installed globally: `npm install -g @angular/cli`.

#### Steps

1.  **Run the development server:**
    ```sh
    ng serve
    ```

2.  Open your browser and navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.
