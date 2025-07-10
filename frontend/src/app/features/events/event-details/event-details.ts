import {Component, inject, Input, OnInit} from '@angular/core';
import {Event} from '../../../core/models/event';
import {EventService} from '../../../core/services/event-service';
import {ActivatedRoute} from '@angular/router';
import {FeedbackList} from '../../feedback/feedback-list/feedback-list';

@Component({
  selector: 'app-event-details',
  imports: [
    FeedbackList
  ],
  templateUrl: './event-details.html',
  styleUrl: './event-details.scss'
})
export class EventDetails implements OnInit {

  @Input() event?: Event;

  loadingEvent: boolean = true;
  id: string | null = '';

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    this.fetchEventDetails();
  }

  private eventService: EventService = inject(EventService);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  fetchEventDetails(): void {
    this.loadingEvent = true;
    this.eventService.getEventById(this.id).subscribe({
      next: (data: Event): void => {
        this.event = data;
      },
      error: (): void => {
        this.loadingEvent = false;
      }
    })
  }
}
