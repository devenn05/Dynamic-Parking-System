import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  // A boolean stream to track if we are loading or not
  public isLoading = new BehaviorSubject<boolean>(false);

  show() { this.isLoading.next(true); }
  hide() { this.isLoading.next(false); }
}