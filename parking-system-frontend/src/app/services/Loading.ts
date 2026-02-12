import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  public isLoading = new BehaviorSubject<boolean>(false);
  
  // Keep track of how many requests are currently running
  private activeRequests = 0;

  show() {
    setTimeout(() => {
        this.activeRequests++;
        this.isLoading.next(true);
    }, 0);
  }

  hide() {
    setTimeout(() => {
        this.activeRequests--;
        if (this.activeRequests <= 0) {
            this.activeRequests = 0; 
            this.isLoading.next(false);
        }
    }, 0);
  }
}