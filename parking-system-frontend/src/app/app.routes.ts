import { Routes } from '@angular/router';
import { ParkingLotList } from './components/parking-lot-list/parking-lot-list';
import { Component } from '@angular/compiler';
import { ParkingOperations } from './components/parking-operations/parking-operations';
import { ParkingSessions } from './components/parking-sessions/parking-sessions';

/**
 * Route Configuration
 * Defines the navigation paths for the application.
 */

export const routes: Routes = [
    // Redirect empty path to the Operations dashboard (default view)
    { path: '', redirectTo: 'operations', pathMatch: 'full' },

    // 'Manage Lots' Screen - Create & List Parking Lots
    {path: 'lots', component: ParkingLotList},

    // 'Entry/Exit' Screen - Handle vehicle processing
    {path: 'operations', component: ParkingOperations},

    // 'Sessions' Screen - View History and Active vehicles
    {path: 'sessions', component: ParkingSessions}
];
