import {Component, inject, OnDestroy} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './core/layout/header/header';
import {Toast} from './shared/toast/toast';
import {Footer} from './core/layout/footer/footer';
import {WebsocketService} from './core/services/websocket-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Toast, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnDestroy {

  private websocketService: WebsocketService = inject(WebsocketService);

  ngOnDestroy(): void {
    this.websocketService.disconnect();
  }
}
