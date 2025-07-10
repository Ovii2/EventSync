import {Component, inject, OnInit} from '@angular/core';
import {EventsList} from '../../features/events/events-list/events-list';
import {EventService} from '../../core/services/event-service';
import {Event} from '../../core/models/event';
import {ToastService} from '../../core/services/toast-service';

@Component({
  selector: 'app-events-page',
  imports: [
    EventsList
  ],
  templateUrl: './events-page.html',
  styleUrl: './events-page.scss'
})
export class EventsPage implements OnInit {
  events: Event[] = [];

  private eventService: EventService = inject(EventService);
  private toastService: ToastService = inject(ToastService);

  ngOnInit(): void {
    this.eventService.getAllEvents().subscribe({
      next: (data: Event[]): void => {
        this.events = data;
      },
      error: () => {
        this.toastService.error("Failed to load events");
      }
    })
  }
}
