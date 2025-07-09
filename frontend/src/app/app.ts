import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './core/layout/header/header';
import {Toast} from './shared/toast/toast';
import {Footer} from './core/layout/footer/footer';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Toast, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
}
