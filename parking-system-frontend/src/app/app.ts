import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
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
export class App implements OnInit {
  isDarkMode = signal<boolean>(false);

  constructor(
    @Inject(DOCUMENT) private document: Document,
    @Inject(PLATFORM_ID) private platformId: Object // <--- Inject Platform ID
  ) {}

  ngOnInit() {
    // Check LocalStorage to remember preference
    if (isPlatformBrowser(this.platformId)) {
      const saved = localStorage.getItem('theme');
      if (saved === 'dark') {
        this.toggleTheme(true);
      }
    }
  }

  toggleTheme(forceDark?: boolean) {
    const newState = forceDark ?? !this.isDarkMode();
    
    this.isDarkMode.set(newState);
    
    if (newState) {
      this.document.body.classList.add('dark-theme');
      
      // Only write to localStorage if in Browser
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('theme', 'dark');
      }
    } else {
      this.document.body.classList.remove('dark-theme');
      
      // Only write to localStorage if in Browser
      if (isPlatformBrowser(this.platformId)) {
        localStorage.setItem('theme', 'light');
      }
    }
  }
}