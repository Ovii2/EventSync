import {Component, inject, Input, OnDestroy, OnInit} from '@angular/core';
import {EventService} from '../../../core/services/event-service';
import {FeedbackSummary} from '../../../core/models/feedback-summary';
import {WebsocketService} from '../../../core/services/websocket-service';
import {Subject, takeUntil} from 'rxjs';
import {Feedback} from '../../../core/models/feedback';

@Component({
  selector: 'app-event-feedback-summary',
  imports: [],
  templateUrl: './event-feedback-summary.html',
  styleUrl: './event-feedback-summary.scss'
})
export class EventFeedbackSummary implements OnInit, OnDestroy {

  @Input() eventId!: string;

  feedbackSummary!: FeedbackSummary;
  error: string | null = null;
  isLoading: boolean = true;

  private eventService: EventService = inject(EventService);
  private websocketService: WebsocketService = inject(WebsocketService);
  private destroy$: Subject<void> = new Subject<void>();


  ngOnInit(): void {
    this.fetchEventSummary();
    this.websocketService.connect();
    this.listenToFeedbackUpdates();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  fetchEventSummary(): void {
    this.isLoading = true;
    this.eventService.getEventFeedbackSummaryById(this.eventId).subscribe({
      next: (data: FeedbackSummary): void => {
        this.feedbackSummary = data;
        this.error = null;
      },
      error: (err): void => {
        this.isLoading = false;
      }
    });
  }

  refreshSummary(): void {
    this.fetchEventSummary();
  }

  private listenToFeedbackUpdates(): void {
    this.websocketService.feedbackUpdates$
      .pipe(takeUntil(this.destroy$))
      .subscribe((updatedFeedback: Feedback): void => {
        if (updatedFeedback.eventId === this.eventId) {
          this.refreshSummary();
        }
      });
  }
}
