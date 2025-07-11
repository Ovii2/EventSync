import {Component, inject, Input, OnInit} from '@angular/core';
import {EventDetails} from '../../features/events/event-details/event-details';
import {ActivatedRoute} from '@angular/router';
import {Feedback} from '../../core/models/feedback';
import {FeedbackService} from '../../core/services/feedback-service';

@Component({
  selector: 'app-event-details-page',
  standalone: true,
  imports: [
    EventDetails
  ],
  templateUrl: './event-details-page.html',
  styleUrl: './event-details-page.scss'
})
export class EventDetailsPage implements OnInit {

  eventId: string | null = '';

  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    this.eventId = this.activatedRoute.snapshot.paramMap.get('id');
  }
}
