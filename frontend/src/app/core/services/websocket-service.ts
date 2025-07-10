import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {Feedback} from '../models/feedback';
import {Client, IMessage} from '@stomp/stompjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stompClient!: Client;
  public feedbackUpdates$: Subject<Feedback> = new Subject<Feedback>();

  public feedbackUpdates: Observable<Feedback> = this.feedbackUpdates$.asObservable();

  connect(): void {
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000
    });

    this.stompClient.onConnect = (): void => {
      this.stompClient.subscribe('/topic/feedback-updates', (message: IMessage): void => {
        const feedback: Feedback = JSON.parse(message.body);
        this.feedbackUpdates$.next(feedback);
      });
    };

    this.stompClient.activate();
  }

  disconnect(): void {
    void this.stompClient.deactivate();
  }
}
