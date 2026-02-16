# 💻 Parking System - Angular Frontend

The user interface for the Dynamic Pricing Parking System. Built with **Angular 21**, it utilizes **Angular Material** for a responsive design and **Angular Signals** for granular, high-performance state management.

This Single Page Application (SPA) connects to two distinct backend services:
1.  **Main Backend** (REST) for transactional operations.
2.  **Notification Service** (SSE) for real-time updates.

---

## 🎨 Features & Screens

### 1. Operations Dashboard (`/lot/:id/operations`)
*   **Unified Interface:** A split-screen layout allowing operators to handle **Vehicle Entry** and **Exit** simultaneously.
*   **Visual Feedback:**
    *   **Tickets:** Generated instantly upon entry (Entry Time, Slot #).
    *   **Bills:** Calculated upon exit with a breakdown of charges (Base Price + Surge Multiplier).
*   **Real-time Availability:** The "Available Slots" counter updates live without refreshing the page.

### 2. Live Sessions (`/lot/:id/sessions`)
*   **Monitoring:** View a table of all currently parked vehicles.
*   **History:** Switch tabs to view past records and revenue generated.
*   **Emergency Termination:** Admins can force-terminate a session (e.g., lost ticket scenarios).

### 3. Lot Management (Admin)
*   **CRUD Operations:** Create new parking lots and configure capacity/pricing.
*   **Live Registry:** See the status of all parking lots in the system at a glance.

### 4. Technical Features
*   **Dark Mode:** A toggle-able theme (using Angular Material theming).
*   **Environment Config:** distinct configurations for **Local Development** vs **Production (Render)**.

---

## 🛠️ Technology Stack

| Technology | Usage |
| :--- | :--- |
| **Angular 21** | Core Framework |
| **TypeScript** | Strict typing for business logic |
| **Angular Material** | UI Components (Tables, Cards, Forms, Dialogs) |
| **Angular Signals** | State Management (Replaces older `BehaviorSubject` patterns) |
| **RxJS** | Handling SSE streams and HTTP Events |

---

## 📡 Real-Time Integration

The frontend uses a hybrid data fetching strategy to ensure data consistency:

1.  **Initial Load (REST):** When a component initializes, it fetches the current state (e.g., "50 available slots") via a standard HTTP GET request to the **Main Backend**.
2.  **Live Updates (SSE):** Simultaneously, the `RealTimeService` opens a persistent connection to the **Notification Service**.
3.  **Merge:** As updates arrive (`SlotUpdate`, `SessionEntry`), Angular Signals automatically update the view.


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
