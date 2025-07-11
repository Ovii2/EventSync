import {inject, Injectable} from '@angular/core';
import {environment} from '../../../enviroments/enviroment';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams, HttpResponse} from '@angular/common/http';
import {Event} from '../models/event';
import {EventRequest} from '../models/event-request';
import {PageResponse} from '../models/page-response';
import {FeedbackSummary} from '../models/feedback-summary';


@Injectable({
  providedIn: 'root'
})
export class EventService {

  private readonly _baseUrl: string = `${environment.apiBaseUrl}/events`;

  constructor() {
  }

  private http: HttpClient = inject(HttpClient);

  getAllEventsPaginated(page: number, size: number, sort: string = 'createdAt,desc'): Observable<PageResponse<Event>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<PageResponse<Event>>(`${this._baseUrl}`, {params});
  }

  getEventById(eventId: string | null): Observable<Event> {
    return this.http.get<Event>(`${this._baseUrl}/${eventId}`);
  }

  createEvent(payload: EventRequest): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this._baseUrl}`, payload, {observe: 'response'})
  }

  getEventFeedbackSummaryById(eventId: string | null): Observable<FeedbackSummary> {
    return this.http.get<FeedbackSummary>(`${this._baseUrl}/${eventId}/summary`);
  }
}
