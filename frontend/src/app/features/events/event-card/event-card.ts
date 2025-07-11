import {Component, Input} from '@angular/core';
import {Event} from '../../../core/models/event';
import {DatePipe} from '@angular/common';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-event-card',
  imports: [
    DatePipe,
    RouterLink
  ],
  templateUrl: './event-card.html',
  styleUrl: './event-card.scss'
})
export class EventCard {

  @Input() event!: Event;
}
