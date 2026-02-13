import { Inject, Injectable, NgZone, PLATFORM_ID } from "@angular/core";
import { Observable, EMPTY } from "rxjs";
import { isPlatformBrowser } from '@angular/common';

@Injectable({
    providedIn: 'root'
})

export class RealTimeService{
    constructor(
        private ngZone: NgZone,
        @Inject(PLATFORM_ID) private platformId: Object
    ){}

    getSlotUpdate(lotId: number): Observable<any>{
        if (!isPlatformBrowser(this.platformId)) return EMPTY;
        return new Observable(observer => {
      const eventSource = new EventSource(`https://parking-notification-service.onrender.com/api/stream/subscribe/${lotId}`);
      eventSource.addEventListener('parking-update', (event: any) => {
        const data = JSON.parse(event.data);
        this.ngZone.run(() => observer.next(data));
      });

      eventSource.onerror = (error) => this.ngZone.run(() => observer.error(error));
      return () => eventSource.close();
    });
    }

    getSessionUpdate(lotId: number): Observable<any>{
        if (!isPlatformBrowser(this.platformId)) return EMPTY;
        return new Observable(observer => {
        const eventSource = new EventSource(`http://localhost:8081/api/stream/subscribe/${lotId}`);
        eventSource.addEventListener('session-update', (event: any) => {
        const data = JSON.parse(event.data);
        this.ngZone.run(() => observer.next(data));
        });

        eventSource.onerror = (error) => this.ngZone.run(() => observer.error(error));
        return () => eventSource.close();
    });
    }
    getGlobalLotUpdates(): Observable<any> {
    if (!isPlatformBrowser(this.platformId)) return EMPTY;

    return new Observable(observer => {
        // We subscribe to Lot 0 for global changes
        const eventSource = new EventSource(`http://localhost:8081/api/stream/subscribe/0`);

        eventSource.addEventListener('lot-registry-update', (event: any) => {
            const data = JSON.parse(event.data);
            this.ngZone.run(() => observer.next(data));
        });

        eventSource.onerror = (error) => this.ngZone.run(() => observer.error(error));
        return () => eventSource.close();
        });
    }
}