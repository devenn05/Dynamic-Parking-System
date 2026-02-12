import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { Component, Inject, OnInit, PLATFORM_ID, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive  } from '@angular/router';
import { LoadingService } from './services/Loading';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
/**
 * Root Application Component
 * -------------------------------------------------------------------------
 * Acts as the shell for the application.
 * Contains the Global Navigation Bar and the Router Outlet.
 */

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, CommonModule, MatIconModule, RouterLinkActive ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  isDarkMode = signal<boolean>(false);

  constructor(
    @Inject(DOCUMENT) private document: Document,
    public loadingService: LoadingService,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit() {

  }
}