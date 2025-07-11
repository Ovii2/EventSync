import {inject, Injectable} from '@angular/core';
import {environment} from '../../../enviroments/enviroment';
import {Observable} from 'rxjs';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Event} from '../models/event';
import {EventRequest} from '../models/event-request';


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

  createEvent(payload: EventRequest): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this._baseUrl}`, payload, {observe: 'response'})
  }
}
