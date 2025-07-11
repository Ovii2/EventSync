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
  private isConnected = false;

  private feedbackUpdatesSubject: Subject<Feedback> = new Subject<Feedback>();
  public feedbackUpdates$: Observable<Feedback> = this.feedbackUpdatesSubject.asObservable();

  connect(): void {
    if (this.isConnected) {
      return;
    }

    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      onConnect: (): void => {
        this.isConnected = true;

        this.stompClient.subscribe('/topic/feedback-updates', (message: IMessage): void => {
          const feedback: Feedback = JSON.parse(message.body);
          this.feedbackUpdatesSubject.next(feedback);
        });
      },
      onDisconnect: (): void => {
        this.isConnected = false;
      }
    });

    this.stompClient.activate();
  }

  disconnect(): void {
    this.isConnected = false;
    void this.stompClient.deactivate();
  }
}
