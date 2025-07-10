import {inject, Injectable} from '@angular/core';
import {environment} from '../../../enviroments/enviroment';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Event} from '../models/event';


@Injectable({
  providedIn: 'root'
})
export class EventService {

  private readonly _baseUrl: string = `${environment.apiBaseUrl}/events`;

  constructor() {
  }

  private http: HttpClient = inject(HttpClient);

  getAllEvents(): Observable<Event[]> {
    return this.http.get<Event[]>(`${this._baseUrl}`);
  }

  getEventById(eventId: string | null): Observable<Event> {
    return this.http.get<Event>(`${this._baseUrl}/${eventId}`);
  }
}
