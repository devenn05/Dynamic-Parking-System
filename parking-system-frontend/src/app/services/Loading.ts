import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  public isLoading = new BehaviorSubject<boolean>(false);
  
  // Keep track of how many requests are currently running
  private activeRequests = 0;

  show() {
    this.activeRequests++;
    // Only emit true if we have at least one active request
    this.isLoading.next(true);
  }

  hide() {
    this.activeRequests--;
    // Only hide the spinner if ALL requests have finished
    if (this.activeRequests <= 0) {
      this.activeRequests = 0; // Prevent negative numbers
      this.isLoading.next(false);
    }
  }
}