import {Component, inject, Input, OnInit, ViewChild} from '@angular/core';
import {Event} from '../../../core/models/event';
import {EventService} from '../../../core/services/event-service';
import {ActivatedRoute} from '@angular/router';
import {FeedbackList} from '../../feedback/feedback-list/feedback-list';
import {FeedbackForm} from '../../feedback/feedback-form/feedback-form';
import {EventFeedbackSummary} from '../event-feedback-summary/event-feedback-summary';

@Component({
  selector: 'app-event-details',
  imports: [
    FeedbackList,
    FeedbackForm,
    EventFeedbackSummary
  ],
  templateUrl: './event-details.html',
  styleUrl: './event-details.scss'
})
export class EventDetails implements OnInit {

  @Input() event?: Event;
  @ViewChild('feedbackList') feedbackListComponent!: FeedbackList;
  @ViewChild('feedbackSummary') feedbackSummaryComponent!: EventFeedbackSummary;

  loadingEvent: boolean = true;
  id: string | null = '';

  private eventService: EventService = inject(EventService);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    if (this.id) {
      this.fetchEventDetails();
    }
  }

  onFeedbackAdded(): void {
    if (this.event?.id) {
      this.feedbackListComponent.refreshFeedback();
      this.feedbackSummaryComponent.refreshSummary();
    }
  }

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
