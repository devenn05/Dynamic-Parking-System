import { Inject, Injectable, NgZone, PLATFORM_ID } from "@angular/core";
import { Observable, EMPTY } from "rxjs";
import { isPlatformBrowser } from '@angular/common';
import { environment } from "../../environments/environment";

@Injectable({
    providedIn: 'root'
})

export class RealTimeService{

    private streamUrl = environment.streamUrl;

    constructor(
        private ngZone: NgZone,
        @Inject(PLATFORM_ID) private platformId: Object
    ){}

    getSlotUpdate(lotId: number): Observable<any>{
        if (!isPlatformBrowser(this.platformId)) return EMPTY;
        return new Observable(observer => {
      const eventSource = new EventSource(`${this.streamUrl}/stream/subscribe/${lotId}`);
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
        const eventSource = new EventSource(`${this.streamUrl}/stream/subscribe/${lotId}`);
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
        const eventSource = new EventSource(`${this.streamUrl}/stream/subscribe/0`);

        eventSource.addEventListener('lot-registry-update', (event: any) => {
            const data = JSON.parse(event.data);
            this.ngZone.run(() => observer.next(data));
        });

        eventSource.onerror = (error) => this.ngZone.run(() => observer.error(error));
        return () => eventSource.close();
        });
    }
}