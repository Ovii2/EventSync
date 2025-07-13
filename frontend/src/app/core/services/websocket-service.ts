import {inject, Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
import {Feedback} from '../models/feedback';
import {Client, IMessage} from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {ToastService} from './toast-service';
import {AuthService} from './auth-service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  private stompClient!: Client;
  private isConnected: boolean = false;

  private toastService: ToastService = inject(ToastService);
  private authService: AuthService = inject(AuthService);

  private feedbackUpdatesSubject: Subject<Feedback> = new Subject<Feedback>();
  public feedbackUpdates$: Observable<Feedback> = this.feedbackUpdatesSubject.asObservable();

  connect(): void {
    if (this.isConnected || !this.authService.isLoggedIn()) {
      return;
    }

    const token = this.authService.getToken();
    this.stompClient = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      reconnectDelay: 5000,
      connectHeaders: token ? {
        Authorization: `Bearer ${token}`
      } : {},
      onConnect: (): void => {
        this.isConnected = true;
        this.subscribeToFeedbackUpdates();
        this.subscribeToSessionExpiration();
      },
      onDisconnect: (): void => {
        this.isConnected = false;
      },
    });
    this.stompClient.activate();
  }

  private subscribeToFeedbackUpdates(): void {
    this.stompClient.subscribe('/topic/feedback-updates', (message: IMessage): void => {
      const feedback: Feedback = JSON.parse(message.body);
      this.feedbackUpdatesSubject.next(feedback);
    });
  }

  private subscribeToSessionExpiration(): void {
    this.stompClient.subscribe('/user/queue/session', (message: any) => {
      console.log('Received session expiration message:', message.body);
      const data = JSON.parse(message.body);
      if (data.type === 'SESSION_EXPIRED') {
        this.toastService.info("Your session has expired. Please log in again.");
      }
    });
  }

  disconnect(): void {
    if (this.stompClient && this.isConnected) {
      this.isConnected = false;
      void this.stompClient.deactivate();
    } else {
      this.isConnected = false;
    }
  }

  isWebSocketConnected(): boolean {
    return this.isConnected && this.stompClient != null;
  }
}
