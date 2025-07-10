import {inject, Injectable} from '@angular/core';
import {environment} from '../../../enviroments/enviroment';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Feedback} from '../models/feedback';
import {FeedbackRequest} from '../models/feedback-request';

@Injectable({
  providedIn: 'root'
})
export class FeedbackService {

  private readonly _baseUrl: string = `${environment.apiBaseUrl}/events`;

  constructor() {
  }

  private http: HttpClient = inject(HttpClient);

  createFeedback(eventId: string, feedback: FeedbackRequest): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this._baseUrl}/${eventId}/feedback`, feedback, {observe: 'response'}
    );
  }

  getFeedbackByEventId(eventId: string | null): Observable<Feedback[]> {
    return this.http.get<Feedback[]>(`${this._baseUrl}/${eventId}/feedback`)
  }
}
