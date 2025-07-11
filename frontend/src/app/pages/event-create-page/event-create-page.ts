import { Component } from '@angular/core';
import {EventCreateForm} from '../../features/events/event-create-form/event-create-form';

@Component({
  selector: 'app-event-create-page',
  imports: [
    EventCreateForm
  ],
  templateUrl: './event-create-page.html',
  styleUrl: './event-create-page.scss'
})
export class EventCreatePage {

}
