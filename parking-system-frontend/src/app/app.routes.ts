import { Routes } from '@angular/router';
import { ParkingLotList } from './components/parking-lot-list/parking-lot-list';
import { Component } from '@angular/compiler';
import { ParkingOperations } from './components/parking-operations/parking-operations';
import { ParkingSessions } from './components/parking-sessions/parking-sessions';
import { HomeComponent } from './components/home/home';
import { LotLayoutComponent } from './components/lot-layout/lot-layout';

/**
 * Route Configuration
 * Defines the navigation paths for the application.
 */

export const routes: Routes = [
    // 1. HOME: Dropdown to select a lot
    { path: '', component: HomeComponent },

    // 2. ADMIN: Manage lots (Create/Edit/Delete)
    { path: 'admin', component: ParkingLotList },

    // 3. LOT DASHBOARD: Nested routes for specific lot operations
    { 
        path: 'lot/:id', 
        component: LotLayoutComponent, 
        children: [
            { path: '', redirectTo: 'operations', pathMatch: 'full' },
            { path: 'operations', component: ParkingOperations },
            { path: 'sessions', component: ParkingSessions }
        ]
    }
];
