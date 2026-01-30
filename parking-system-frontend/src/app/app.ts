import { Component, signal } from '@angular/core';
import { RouterOutlet, RouterLink  } from '@angular/router';

/**
 * Root Application Component
 * -------------------------------------------------------------------------
 * Acts as the shell for the application.
 * Contains the Global Navigation Bar and the Router Outlet.
 */

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('parking-system-frontend');
}
