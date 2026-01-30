import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import {provideHttpClient} from '@angular/common/http';
import { routes } from './app.routes';
import { provideClientHydration, withEventReplay } from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),

    // Registers the router with the defined routes.
    provideRouter(routes), 
    
    provideClientHydration(withEventReplay()),

    // IMPORTANT: Enables HttpClient injection across the app.
    provideHttpClient()
  ]
};
